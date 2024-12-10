package com.github.pdaodao.springwebplus.base.util;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传校验
 */
public class UploadCheckUtil {
    /**
     * 校验文件大小
     * @param file
     * @param sizeInM
     */
    public static void checkSize(final MultipartFile file, Integer sizeInM) {
        if(file == null || sizeInM == null){
            return;
        }
        final Long size = sizeInM * 1024 * 1024l;
        Preconditions.assertTrue(file.getSize() > size, "文件大小不能超过{}M", sizeInM);
    }

    /**
     * 校验文件是否是图片
     * @param file
     */
    public static void checkIsImage(final MultipartFile file) {
        if(file == null){
            return;
        }
        final String contentType = file.getContentType();
        if(StrUtil.isBlank(contentType)){
            return;
        }
        Preconditions.checkArgument(contentType.contains("image"), "非法的文件，只允许上传图片");
    }

    public static void checkIsExcel(final MultipartFile file) {
        if(file == null){
            return;
        }
        final String contentType = file.getContentType();
        if(StrUtil.isBlank(contentType)){
            return;
        }
        if(StrUtil.isNotBlank(file.getOriginalFilename())){
            if(file.getOriginalFilename().endsWith(".xlsx") || file.getOriginalFilename().endsWith(".xls")){
                return;
            }
        }
        if(contentType.endsWith(".sheet")){
            return;
        }
        Preconditions.checkArgument(contentType.contains("excel")
                || contentType.contains("sheet")
                || contentType.contains("xlsx"), "非法的文件，只允许上传excel");
    }

    /**
     * todo
     * @param file
     */
    public static void checkIsDoc(final MultipartFile file) {
        if(file == null){
            return;
        }
        final String contentType = file.getContentType();
        if(StrUtil.isBlank(contentType)){
            return;
        }
        // todo
//        Preconditions.checkArgument(contentType.startsWith("excel"), "非法的文件，只允许上传excel");
    }
}
