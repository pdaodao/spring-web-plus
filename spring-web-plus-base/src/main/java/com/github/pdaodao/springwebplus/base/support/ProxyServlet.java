package com.github.pdaodao.springwebplus.base.support;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.github.pdaodao.springwebplus.tool.util.SSLUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * http 代理 servlet
 */
@Component
public class ProxyServlet extends HttpServlet {
    private final ConcurrentHashMap<String, String> urlMapping = new ConcurrentHashMap<>();
    private transient HttpClient client;

    public ProxyServlet addMapping(final String key, final String target) {
        if (StrUtil.isBlank(key) || StrUtil.isBlank(target)) {
            return this;
        }
        urlMapping.put("/" + key.toLowerCase(), target);
        return this;
    }

    public String[] urlMapping() {
        if (urlMapping.isEmpty()) {
            return null;
        }
        return urlMapping.keySet().stream().map(t -> t + "/*")
                .collect(Collectors.toList())
                .toArray(new String[0]);
    }

    @Override
    public void init() throws ServletException {
        client = createHttpClient();
    }

    @Override
    protected void service(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        final String method = req.getMethod();
        //1. 根据路径映射 获取目标路径
        final String target = rewriteUrlFromRequest(req);
        //2. 构建请求
        HttpRequest proxyRequest;
        if (req.getHeader(HttpHeaders.CONTENT_LENGTH) != null || req.getHeader(HttpHeaders.TRANSFER_ENCODING) != null) {
            proxyRequest = new BasicHttpEntityEnclosingRequest(method, target);
            ((BasicHttpEntityEnclosingRequest) proxyRequest).setEntity(
                    new InputStreamEntity(req.getInputStream(), getContentLength(req)));
        } else {
            proxyRequest = new BasicHttpRequest(method, target);
        }
        //3. 拷贝请求头
        copyRequestHeaders(req, target, proxyRequest);
        //4. x
        setXForwardedForHeader(req, proxyRequest);

        HttpResponse proxyResponse = null;
        try {
            proxyResponse = client.execute(new HttpHost("127.0.0.1", 80), proxyRequest);
            int statusCode = proxyResponse.getStatusLine().getStatusCode();
            resp.setStatus(statusCode);
            // todo
            copyResponseHeaders(target, proxyResponse, req, resp);

            if (statusCode == HttpServletResponse.SC_NOT_MODIFIED) {
                resp.setIntHeader(HttpHeaders.CONTENT_LENGTH, 0);
            } else {
                // Send the content to the client
                copyResponseEntity(proxyResponse, resp, proxyRequest, req);
            }

        } catch (Exception e) {
            // todo
            // handleRequestException(proxyRequest, proxyResponse, e);
        } finally {
            // make sure the entire entity was consumed, so the connection is released
            if (proxyResponse != null) {
                EntityUtils.consumeQuietly(proxyResponse.getEntity());
            }
        }
    }

    /**
     * Copy response body data (the entity) from the proxy to the servlet client.
     */
    protected void copyResponseEntity(final HttpResponse proxyResponse, final HttpServletResponse servletResponse,
                                      final HttpRequest proxyRequest, final HttpServletRequest servletRequest) throws IOException {
        final HttpEntity entity = proxyResponse.getEntity();
        if (entity == null) {
            return;
        }
        if (entity.isChunked()) {
            // Flush intermediate results before blocking on input -- needed for SSE
            InputStream is = entity.getContent();
            OutputStream os = servletResponse.getOutputStream();
            byte[] buffer = new byte[10 * 1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
                if (is.available() == 0 /* next is.read will block */) {
                    os.flush();
                }
            }
            // Entity closing/cleanup is done in the caller (#service)
            return;
        }
        final OutputStream servletOutputStream = servletResponse.getOutputStream();
        entity.writeTo(servletOutputStream);
    }

    protected void copyResponseHeaders(final String targetUri, HttpResponse proxyResponse, HttpServletRequest servletRequest,
                                       HttpServletResponse servletResponse) {
        for (Header header : proxyResponse.getAllHeaders()) {
            copyResponseHeader(targetUri, servletRequest, servletResponse, header);
        }
    }

    protected void copyResponseHeader(final String targetUri, HttpServletRequest servletRequest,
                                      HttpServletResponse servletResponse, Header header) {
        final String headerName = header.getName();
        final String headerValue = header.getValue();
        if (headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE) ||
                headerName.equalsIgnoreCase(org.apache.http.cookie.SM.SET_COOKIE2)) {
            // copyProxyCookie(servletRequest, servletResponse, headerValue);
        } else if (headerName.equalsIgnoreCase(HttpHeaders.LOCATION)) {
            // LOCATION Header may have to be rewritten.
            servletResponse.addHeader(headerName, rewriteUrlFromResponse(targetUri, servletRequest, headerValue));
        } else {
            servletResponse.addHeader(headerName, headerValue);
        }
    }

    protected String rewriteUrlFromResponse(final String targetUri, HttpServletRequest servletRequest, String theUrl) {
        //TODO document example paths
        if (theUrl.startsWith(targetUri)) {
            /*-
             * The URL points back to the back-end server.
             * Instead of returning it verbatim we replace the target path with our
             * source path in a way that should instruct the original client to
             * request the URL pointed through this Proxy.
             * We do this by taking the current request and rewriting the path part
             * using this servlet's absolute path and the path from the returned URL
             * after the base target URL.
             */
            StringBuffer curUrl = servletRequest.getRequestURL();//no query
            int pos;
            // Skip the protocol part
            if ((pos = curUrl.indexOf("://")) >= 0) {
                // Skip the authority part
                // + 3 to skip the separator between protocol and authority
                if ((pos = curUrl.indexOf("/", pos + 3)) >= 0) {
                    // Trim everything after the authority part.
                    curUrl.setLength(pos);
                }
            }
            // Context path starts with a / if it is not blank
            curUrl.append(servletRequest.getContextPath());
            // Servlet path starts with a / if it is not blank
            curUrl.append(servletRequest.getServletPath());
            curUrl.append(theUrl, targetUri.length(), theUrl.length());
            return curUrl.toString();
        }
        return theUrl;
    }

    private long getContentLength(HttpServletRequest request) {
        String contentLengthHeader = request.getHeader("Content-Length");
        if (contentLengthHeader != null) {
            return Long.parseLong(contentLengthHeader);
        }
        return -1L;
    }

    /**
     * 重写请求路径
     *
     * @param req
     * @return
     */
    protected String rewriteUrlFromRequest(final HttpServletRequest req) {
        final String root = req.getServletPath();
        final String path = req.getPathInfo();
        final String queryString = req.getQueryString();
        String target = urlMapping.get(root.toLowerCase());
        return target + path + (StrUtil.isNotBlank(queryString) ? "?" + queryString : "");
    }

    /**
     * 拷贝请求头
     *
     * @param servletRequest
     * @param proxyRequest
     */
    protected void copyRequestHeaders(HttpServletRequest servletRequest, final String target, HttpRequest proxyRequest) {
        final Enumeration<String> enumerationOfHeaderNames = servletRequest.getHeaderNames();
        while (enumerationOfHeaderNames.hasMoreElements()) {
            String headerName = enumerationOfHeaderNames.nextElement();
            copyRequestHeader(servletRequest, target, proxyRequest, headerName);
        }
    }


    protected void copyRequestHeader(final HttpServletRequest req, final String target, final HttpRequest proxyRequest, final String headerName) {
        if (headerName.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
            return;
        }
        final Enumeration<String> headers = req.getHeaders(headerName);
        if (CollUtil.isEmpty(headers)) {
            return;
        }
        final URL targetUrl = URLUtil.url(target);
        while (headers.hasMoreElements()) {//sometimes more than one value
            String headerValue = headers.nextElement();
            if (headerName.equalsIgnoreCase(HttpHeaders.HOST)) {
                headerValue = targetUrl.getHost() + ":" + targetUrl.getPort();
            }
            if ("Cookie".equalsIgnoreCase(headerName)) {
                // todo
            }
            proxyRequest.addHeader(headerName, headerValue);
        }
    }

    private void setXForwardedForHeader(final HttpServletRequest req, final HttpRequest proxyRequest) {
        final String forHeaderName = "X-Forwarded-For";
        String forHeader = req.getRemoteAddr();
        final String existingForHeader = req.getHeader(forHeaderName);
        if (existingForHeader != null) {
            forHeader = existingForHeader + ", " + forHeader;
        }
        proxyRequest.setHeader(forHeaderName, forHeader);

        final String protoHeaderName = "X-Forwarded-Proto";
        final String protoHeader = req.getScheme();
        proxyRequest.setHeader(protoHeaderName, protoHeader);
    }


    @Override
    public void destroy() {
        if (client == null) {
            return;
        }
        if (client instanceof Closeable) {
            IoUtil.closeIfPosible(client);
            return;
        }
        client.getConnectionManager().shutdown();
    }

    protected HttpClient createHttpClient() {
        final HttpClientBuilder builder = HttpClientBuilder.create()
                .setSSLContext(SSLUtil.getSc())
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setDefaultRequestConfig(buildRequestConfig())
                .setDefaultSocketConfig(buildSocketConfig())
                .setMaxConnTotal(5000)
                .setMaxConnPerRoute(1000);
        builder.useSystemProperties();
        return builder.build();
    }

    protected RequestConfig buildRequestConfig() {
        return RequestConfig.custom()
                .setRedirectsEnabled(true)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES) // we handle them in the servlet instead
                .setConnectTimeout(3000)
                .setSocketTimeout(3000)
                .setConnectionRequestTimeout(3000)
                .build();
    }

    protected SocketConfig buildSocketConfig() {
        return SocketConfig.custom()
                .setSoTimeout(3000)
                .build();
    }
}
