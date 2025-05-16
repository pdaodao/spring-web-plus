package com.github.pdaodao.springwebplus.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.service.FileStorageService;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.dao.SysFileDao;
import com.github.pdaodao.springwebplus.entity.SysFile;
import com.github.pdaodao.springwebplus.tool.data.PageResult;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import com.github.pdaodao.springwebplus.tool.fs.local.LocalConfig;
import com.github.pdaodao.springwebplus.tool.fs.local.LocalFileStorage;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

/**
 * 文件上传工具类
 */
public class FileUploadUtil {
    public static final String[] ImageExtensions = {"jpg", "jpeg", "png", "gif", "bmp", "svg", "webp"};
    public static final String[] DocExtensions = {"doc", "docx", "txt", "pdf", "csv", "ppt", "pptx"};
    public static final String[] ExcelExtensions = {"xlsx", "xls"};

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
            checkSize(file, maxSizeInM);
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
        String type = "down";
        if(StrUtil.equals(FileNameUtil.getSuffix(path), "mp4")){
            type = "video";
        }else if(isImage(path)){
            type = "image";
        }
        return FilePathUtil.join("/", SpringUtil.getContextPath(), "/sys/file/"+type, path);
    }

    /**
     * 从返回给前端的http路径中解析出后端存储路径
     * @param urlPath
     * @return
     */
    public static String parseStorePath(final String urlPath){
        if(StrUtil.isBlank(urlPath)){
            return null;
        }
        if(urlPath.contains("/sys/file/image/")){
            return StrUtil.subAfter(urlPath, "/sys/file/image/", false);
        }else if(urlPath.contains("/sys/file/video/")){
            return StrUtil.subAfter(urlPath, "/sys/file/video/", false);
        }else if(urlPath.contains("/sys/file/down/")){
            return StrUtil.subAfter(urlPath, "/sys/file/down/", false);
        }
        return urlPath;
    }

    /**
     * 校验文件大小
     *
     * @param file
     * @param sizeInM
     */
    public static void checkSize(final MultipartFile file, Integer sizeInM) {
        if (file == null || sizeInM == null) {
            return;
        }
        final Long size = sizeInM * 1024 * 1024l;
        Preconditions.assertTrue(file.getSize() > size, "文件大小不能超过{}M", sizeInM);
    }

    /**
     * 校验文件是否是图片
     *
     * @param fileName
     */
    public static void checkIsImage(final String fileName) {
        final boolean is = StrUtil.containsAnyIgnoreCase(FileNameUtil.extName(fileName), ImageExtensions);
        Preconditions.checkArgument(is, "非法的文件，只允许上传图片");
    }

    /**
     * 是否是图片
     * @param path
     * @return
     */
    public static boolean isImage(final String path){
        if(StrUtil.isBlank(path)){
            return false;
        }
        final String name = FileNameUtil.getName(path);
        return StrUtil.containsAnyIgnoreCase(FileNameUtil.extName(name), ImageExtensions);
    }

    public static void checkIsImage(final MultipartFile file) {
        Preconditions.checkNotNull(file, "文件不存在.");
        checkIsImage(file.getOriginalFilename());
    }

    /**
     * 是否是Excel文件
     * @param file
     */
    public static void checkIsExcel(final MultipartFile file) {
        if (file == null) {
            return;
        }
        checkIsExcel(file.getOriginalFilename());
    }

    /**
     * 是否是Excel文件
     * @param fileName
     */
    public static void checkIsExcel(final String fileName) {
        final boolean is = StrUtil.containsAnyIgnoreCase(FileNameUtil.extName(fileName), ExcelExtensions);
        Preconditions.checkArgument(is, "非法的文件，只允许上传excel");
    }


    /**
     * 是否是文档文件
     * @param file
     */
    public static void checkIsDoc(final MultipartFile file) {
        if (file == null) {
            return;
        }
        checkIsDoc(file.getOriginalFilename());
    }

    /**
     * 是否是文档文件
     * @param fileName
     */
    public static void checkIsDoc(final String fileName) {
        final boolean is = StrUtil.containsAnyIgnoreCase(FileNameUtil.extName(fileName), DocExtensions);
        Preconditions.checkArgument(is, "非法的文件，只允许上传文档");
    }
}
