package com.github.pdaodao.springwebplus.controller;

import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.pojo.PageRequestParam;
import com.github.pdaodao.springwebplus.base.service.FileStorageService;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.ResponseUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.base.util.UploadCheckUtil;
import com.github.pdaodao.springwebplus.dao.SysFileDao;
import com.github.pdaodao.springwebplus.entity.SysFile;
import com.github.pdaodao.springwebplus.tool.data.PageResult;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.Constant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "系统文件管理")
@RequestMapping(Constant.ApiPrefix + "/file")
@AllArgsConstructor
public class SysFileController {
    private final SysFileDao sysFileDao;
    private final FileStorageService fileStorageService;

    @PostMapping("upload")
    @Operation(summary = "文件上传")
    @Parameters({
            @Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true,
                    schema = @Schema(name = "file", format = "binary")),
            @Parameter(name = "namespace", description = "用途编码"),
            @Parameter(name = "objId", description = "objId", required = false)
    })
    public FileInfo upload(@RequestParam("file") final MultipartFile file,
                           @RequestParam("namespace") String namespace,
                           @RequestParam(required = false) String objId) throws Exception {
        UploadCheckUtil.checkSize(file, 500);
        namespace = FileNameUtil.cleanInvalid(namespace);
        final FileInfo fileInfo = fileStorageService.upload(namespace, file);
        fileInfo.setNamespace(namespace);
        fileInfo.setObjId(objId);
        sysFileDao.saveInfo(fileInfo);
        fileInfo.setPath(buildFileHttpPath(fileInfo.getPath()));
        return fileInfo;
    }

    @PostMapping("img")
    @Operation(summary = "图片上传")
    @Parameters({
            @Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true,
                    schema = @Schema(name = "file", format = "binary")),
            @Parameter(name = "namespace", description = "用途编码", required = false),
            @Parameter(name = "objId", description = "objId")
    })
    public FileInfo uploadImg(@RequestParam("file") final MultipartFile file,
                              @RequestParam("namespace") String namespace,
                              @RequestParam(required = false) String objId) throws Exception {
        UploadCheckUtil.checkIsImage(file);
        UploadCheckUtil.checkSize(file, 30);
        namespace = FileNameUtil.cleanInvalid(namespace);
        final FileInfo fileInfo = fileStorageService.upload(namespace, file);
        fileInfo.setNamespace(namespace);
        fileInfo.setObjId(objId);
        sysFileDao.saveInfo(fileInfo);
        fileInfo.setPath(buildFileHttpPath(fileInfo.getPath()));
        return fileInfo;
    }

    @GetMapping("list")
    @Operation(summary = "文件列表")
    public PageResult<SysFile> list(@RequestParam("namespace") String namespace, final PageRequestParam pageRequestParam) throws Exception {
        try(final PageHelper ph = PageHelper.startPage(pageRequestParam)){
            final List<SysFile> list = sysFileDao.byNamespace(namespace, null);
            for(final SysFile f: list){
                f.setPath(buildFileHttpPath(f.getPath()));
            }
            return ph.toPageResult(list);
        }
    }

    @GetMapping("download")
    @Operation(summary = "附件下载")
    public void download(@Parameter(name = "id", description = "文件id") final String id,
                         HttpServletResponse response) throws Exception {
        final SysFile sysFile = sysFileDao.getById(id);
        Preconditions.checkNotNull(sysFile, "文件信息不存在");
        try (final InputStreamWrap wrap = fileStorageService.download(sysFile.getPath())) {
            ResponseUtil.writeFile(sysFile.getName(), wrap.inputStream, response, true, 500);
        }
    }

    @GetMapping("image/{id}")
    @Operation(summary = "文件显示")
    public void fileRender(@PathVariable(name = "id") final String id,
                           HttpServletResponse response) throws Exception {
        final SysFile sysFile = sysFileDao.getById(id);
        Preconditions.checkNotNull(sysFile, "文件信息不存在");
        try (final InputStreamWrap wrap = fileStorageService.download(sysFile.getPath())) {
            ResponseUtil.writeFile(sysFile.getName(), wrap.inputStream, response, false, 500);
        }
    }

    /**
     * 构建前端文件请求路径
     *
     * @param path
     * @return
     */
    private static String buildFileHttpPath(final String path) {
        if(path == null){
            return null;
        }
        String type = "image";
        if(StrUtil.equals(FileNameUtil.getSuffix(path), "mp4")){
            type = "video";
        }
        return FilePathUtil.join("/", SpringUtil.getContextPath(), "/sys/file/"+type, path);
    }
}
