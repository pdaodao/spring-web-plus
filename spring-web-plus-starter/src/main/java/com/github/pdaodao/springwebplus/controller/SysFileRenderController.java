package com.github.pdaodao.springwebplus.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.github.pdaodao.springwebplus.base.service.FileStorageService;
import com.github.pdaodao.springwebplus.base.util.ResponseUtil;
import com.github.pdaodao.springwebplus.entity.SysFile;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Slf4j
@RestController
@Tag(name = "图片显示下载")
@RequestMapping("/sys/file")
@AllArgsConstructor
public class SysFileRenderController {
    private final FileStorageService fileStorageService;

    @GetMapping("/image/**")
    @Operation(summary = "图片文件显示")
    public void imgRender(@Parameter(name = "id", description = "文件id") final String id,
                          final HttpServletRequest httpServletRequest,
                          final HttpServletResponse response) throws Exception {
        final String reqPath = httpServletRequest.getServletPath();
        final String path = reqPath.replaceFirst("/sys/file/image", "");
        final String fileName = FileNameUtil.getName(path);
        try (final InputStreamWrap wrap = fileStorageService.download(path)) {
            ResponseUtil.writeFile(fileName, wrap.inputStream, response, false, 500);
        }
    }

    @GetMapping("/down/**")
    @Operation(summary = "文件下载")
    public void imgDown(@Parameter(name = "id", description = "文件id") final String id,
                          final HttpServletRequest httpServletRequest,
                          final HttpServletResponse response) throws Exception {
        final String reqPath = httpServletRequest.getServletPath();
        final String path = reqPath.replaceFirst("/sys/file/down", "");
        final String fileName = FileNameUtil.getName(path);
        try (final InputStreamWrap wrap = fileStorageService.download(path)) {
            ResponseUtil.writeFile(fileName, wrap.inputStream, response, true, 500);
        }
    }

    @GetMapping("/video/**")
    public ResponseEntity<StreamingResponseBody> streamVideo(final HttpServletRequest httpServletRequest,
            @RequestHeader(value = "Range", required = false) String rangeHeader) throws Exception{
        final String reqPath = httpServletRequest.getServletPath();
        final String path = reqPath.replaceFirst("/sys/file/video", "");
        final String fileName = FileNameUtil.getName(path);
        final InputStreamWrap wrap = fileStorageService.download(path);
        try{
            // 文件总大小
            long fileSize = wrap.inputStream.available();
            long start = 0;
            long end = fileSize - 1;
            if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
                String[] ranges = rangeHeader.substring(6).split("-");
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            }
            final long finalStart = start;
            final long finalEnd = end;
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("video/"+FileNameUtil.getSuffix(fileName)));
            headers.set("Accept-Ranges", "bytes");
            headers.set("Content-Range", "bytes " + finalStart + "-" + finalEnd + "/" + fileSize);
            headers.setContentLength(finalEnd - finalStart + 1);
            final StreamingResponseBody responseBody = new WrapStreamingResponseBody(wrap, finalStart, finalEnd);
            return new ResponseEntity<>(responseBody, headers, HttpStatus.PARTIAL_CONTENT);
        }catch (Exception e){
            log.error(e.getMessage(), e);
            IoUtil.close(wrap);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @AllArgsConstructor
    public static class WrapStreamingResponseBody implements StreamingResponseBody{
        private final InputStreamWrap wrap;
        private final long finalStart;
        private final long finalEnd;

        @Override
        public void writeTo(final OutputStream outputStream) throws IOException {
            try (final InputStreamWrap inputWrap = wrap) {
                final InputStream input = inputWrap.inputStream;
                input.skip(finalStart); // 跳过起始字节
                byte[] buffer = new byte[2048];
                long remaining = finalEnd - finalStart + 1;
                while (remaining > 0) {
                    int readSize = (int) Math.min(buffer.length, remaining);
                    int bytesRead = input.read(buffer, 0, readSize);
                    if (bytesRead == -1) break;
                    outputStream.write(buffer, 0, bytesRead);
                    remaining -= bytesRead;
                }
            }
        }
    }
}
