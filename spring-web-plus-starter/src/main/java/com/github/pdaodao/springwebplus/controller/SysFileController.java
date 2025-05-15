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
import com.github.pdaodao.springwebplus.tool.fs.FileStorage;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.fs.local.LocalFileStorage;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.FilePathUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.Constant;
import com.github.pdaodao.springwebplus.util.FileUploadUtil;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Parameters({@Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true,
                    schema = @Schema(name = "file", format = "binary"))})
    public FileInfo upload(@RequestParam("file") final MultipartFile file,
                           @Parameter(name = "namespace", description = "用途编码") @RequestParam("namespace") String namespace,
                           @Parameter(name = "objId", description = "objId", required = false) @RequestParam(required = false) String objId) throws Exception {
        UploadCheckUtil.checkSize(file, 500);
        final FileInfo fileInfo = FileUploadUtil.uploadSaveInfo(file, namespace, null);
        fileInfo.setPath(FileUploadUtil.buildFileHttpPath(fileInfo.getPath()));
        return fileInfo;
    }

    @Operation(summary = "分片上传")
    @PostMapping(value = "upload-sharding")
    @Parameters({@Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true,
            schema = @Schema(name = "file", format = "binary"))})
    public FileInfo uploadSharding(final MultipartFile file,
                                   @Parameter(description = "总分片数") final Integer totalChunks,
                                   @Parameter(description = "当前分片序号从1开始") final Integer chunkNumber,
                                   @Parameter(description = "文件md5") final String fileSumMd5,
                                   @Parameter(description = "namespace") String namespace) throws Exception {
        final String fileName = FileNameUtil.cleanInvalid(file.getOriginalFilename());
        final String basePath = DateTimeUtil.formatDate(DateTimeUtil.now())+"/"+fileSumMd5;
        FileStorage.setOverwrite();
        final LocalFileStorage tempFs = FileUploadUtil.tempFileStorage();
        try(final InputStream inputStream = file.getInputStream()){
            final String path = tempFs.upload(basePath, fileName + "."+chunkNumber, inputStream);
            if(chunkNumber < totalChunks){
                final FileInfo fileInfo = new FileInfo();
                fileInfo.setName(namespace);
                fileInfo.setPath(path);
                fileInfo.setSize(file.getSize());
                return fileInfo;
            }
        }
        // 合并
        if(chunkNumber == totalChunks){
            final String resultFilePath = FilePathUtil.pathJoin(basePath, fileName);
            final String resultFileFullPath = tempFs.fullPath(resultFilePath);
            final File outputFile = new File(resultFileFullPath);
            final long fileSize = outputFile.length();
            try (final OutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {
                for (int i = 1; i <= totalChunks; i++) {
                    final Path tempFile = Paths.get(tempFs.fullPath(FilePathUtil.pathJoin(basePath, fileName+"."+i)));
                    Files.copy(tempFile, out);
                }
            }
            FileStorage.clearHolder();
            // 保存到目标
            namespace = cn.hutool.core.io.FileUtil.cleanInvalid(namespace).trim();
            try(final InputStreamWrap wrap = tempFs.download(resultFilePath)){
                final String path = fileStorageService.upload(namespace, fileName, wrap.inputStream);
                final FileInfo fileInfo = new FileInfo();
                fileInfo.setName(namespace);
                fileInfo.setPath(FileUploadUtil.buildFileHttpPath(path));
                fileInfo.setSize(fileSize);
                tempFs.delete(basePath);
                return fileInfo;
            }
        }
        return new FileInfo();
    }

    @PostMapping("img")
    @Operation(summary = "图片上传")
    @Parameters({@Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true,
                    schema = @Schema(name = "file", format = "binary"))})
    public FileInfo uploadImg(@RequestParam("file") final MultipartFile file,
                              @Parameter(name = "namespace", description = "用途编码") @RequestParam(value = "namespace") String namespace,
                              @Parameter(name = "objId", description = "objId") @RequestParam(required = false) String objId) throws Exception {
        UploadCheckUtil.checkIsImage(file);
        final FileInfo fileInfo = FileUploadUtil.uploadSaveInfo(file, namespace, 50);
        fileInfo.setPath(FileUploadUtil.buildFileHttpPath(fileInfo.getPath()));
        return fileInfo;
    }

    @GetMapping("list")
    @Operation(summary = "文件列表")
    public PageResult<SysFile> list(@RequestParam("namespace") String namespace, final PageRequestParam pageRequestParam) throws Exception {
        return FileUploadUtil.sysFileList(namespace, pageRequestParam);
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
}