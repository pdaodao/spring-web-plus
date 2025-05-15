package com.github.pdaodao.springwebplus.util;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.service.FileStorageService;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.base.util.UploadCheckUtil;
import com.github.pdaodao.springwebplus.dao.SysFileDao;
import com.github.pdaodao.springwebplus.entity.SysFile;
import com.github.pdaodao.springwebplus.tool.data.PageResult;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import com.github.pdaodao.springwebplus.tool.fs.local.LocalConfig;
import com.github.pdaodao.springwebplus.tool.fs.local.LocalFileStorage;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传工具类
 */
public class FileUploadUtil {
    private static LocalFileStorage tempFileStorage;
    /**
     * 临时文件路径
     * @return
     */
    public static String tempFileDir() {
        return FilePathUtil.pathJoin(System.getProperty("user.dir"), "tmp");
    }

    /**
     * 临时文件系统
     * @return
     */
    public static LocalFileStorage tempFileStorage(){
        if(tempFileStorage != null){
            return tempFileStorage;
        }
        synchronized (FileUploadUtil.class){
            if(tempFileStorage != null){
                return tempFileStorage;
            }
            final LocalConfig cf = new LocalConfig();
            cf.setRootPath(tempFileDir());
            final LocalFileStorage fs = new LocalFileStorage(cf);
            fs.init();
            tempFileStorage = fs;
            return fs;
        }
    }

    /**
     * 文件信息列表
     * @param namespace
     * @param pageRequestParam
     * @return
     * @throws Exception
     */
    public static PageResult<SysFile> sysFileList(String namespace, final PageRequestParam pageRequestParam) throws Exception {
        final SysFileDao sysFileDao = SpringUtil.getBean(SysFileDao.class);
        try(final PageHelper ph = PageHelper.startPage(pageRequestParam)){
            final List<SysFile> list = sysFileDao.byNamespace(namespace, null);
            for(final SysFile f: list){
                f.setPath(buildFileHttpPath(f.getPath()));
            }
            return ph.toPageResult(list);
        }
    }

    /**
     * 上传文件并保存文件信息
     * @param file
     * @param namespace
     * @param maxSizeInM
     * @return
     * @throws Exception
     */
    public static FileInfo uploadSaveInfo(final MultipartFile file, String namespace, final Integer maxSizeInM) throws Exception {
        if(maxSizeInM != null){
            UploadCheckUtil.checkSize(file, maxSizeInM);
        }
        namespace = cn.hutool.core.io.FileUtil.cleanInvalid(namespace).trim();
        final FileStorageService fileStorageService = SpringUtil.getBean(FileStorageService.class);
        final SysFileDao sysFileDao = SpringUtil.getBean(SysFileDao.class);
        final FileInfo fileInfo = fileStorageService.upload(namespace, file);
        fileInfo.setNamespace(namespace);
        sysFileDao.saveInfo(fileInfo);
        return fileInfo;
    }

    /**
     * 构建前端文件请求路径
     *
     * @param path
     * @return
     */
    public static String buildFileHttpPath(final String path) {
        if(path == null){
            return null;
        }
        String type = "image";
        if(StrUtil.equals(FileNameUtil.getSuffix(path), "mp4")){
            type = "video";
        }
        return FilePathUtil.join("/", SpringUtil.getContextPath(), "/sys/file/"+type, path);
    }

    public static String parseStorePath(final String urlPath){
        if(StrUtil.isBlank(urlPath)){
            return null;
        }
        int index = urlPath.indexOf("/render");
        if(index < 1){
            return urlPath;
        }
        return urlPath.substring(index + 8);
    }
}
