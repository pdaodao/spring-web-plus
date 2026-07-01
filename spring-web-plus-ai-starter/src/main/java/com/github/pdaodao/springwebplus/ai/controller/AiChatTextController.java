package com.github.pdaodao.springwebplus.ai.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.ai.dao.AiChatTopicDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatText;
import com.github.pdaodao.springwebplus.ai.entity.AiChatTopic;
import com.github.pdaodao.springwebplus.ai.query.AiChatTextQuery;
import com.github.pdaodao.springwebplus.ai.service.AiChatTextService;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.util.ExcelUtil;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import com.github.pdaodao.springwebplus.tool.fs.local.LocalFileStorage;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import com.github.pdaodao.springwebplus.util.FileUploadUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "知识文本管理")
@RestController
@RequestMapping(Constant.ChatApiPrefix + "/doc-text")
@AllArgsConstructor
public class AiChatTextController {
    private final AiChatTopicDao topicDao;
    private final AiChatTextService termTextService;

    @GetMapping("check-title")
    @Operation(summary = "名称是否可用")
    public Boolean checkTitleExist(@Schema(description = "id") @RequestParam(required = false) final String id,
                                   @Schema(description = "名称") @RequestParam final String title,
                                   @Schema(description = "主题id") @RequestParam String topicId) {
        Preconditions.checkNotBlank(title, "请指定名称");
        if(StrUtil.isNotBlank(id) && StrUtil.isBlank(topicId)){
            final AiChatText old = termTextService.info(id);
            if(old != null){
                topicId = old.getTopicId();
            }
        }
        Preconditions.checkNotBlank(topicId, "分类主题id不能为空.");
        return termTextService.checkDistinctTitle(RequestUtil.getTeamOrDefault(), id, title, topicId);
    }

    @GetMapping("/list")
    @Operation(summary = "分页")
    public List<AiChatText> page(final AiChatTextQuery query) {
        PageHelper.startPage(query);
        final List<AiChatText> list = termTextService.infoList(query);
        return list;
    }

    @PostMapping("/save")
    @Operation(summary = "保存")
    public AiChatText save(@Validated @RequestBody final AiChatText text) throws Exception{
        text.setTeamId(RequestUtil.getTeamOrDefault());
        termTextService.save(text);
        return text;
    }

    @PostMapping("/excel")
    @Operation(summary = "上传excel")
    @Parameters({
            @Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true, schema = @Schema(name = "file", format = "binary")),
            @Parameter(name = "namespace", description = "命名空间"),
            @Parameter(name = "topicId", description = "主题")
    })
    public Boolean uploadExcel(@RequestParam("file") final MultipartFile file,
                               @RequestParam(required = false, defaultValue = "qa")  final AiChatNamespace namespace,
                               @RequestParam(required = false)  final String topicId) throws Exception{
        FileUploadUtil.checkIsExcel(file);
        final String teamId = RequestUtil.getTeamOrDefault();
        final LocalFileStorage tempFs = FileUploadUtil.tempFileStorage();
        final FileInfo fileInfo = FileUploadUtil.upload("excel", file, tempFs);
        final String filePath = tempFs.fullPath(fileInfo.getPath());
        try{
            final List<String> sheets = ExcelUtil.getSheetNames(filePath);
            for(final String sh: sheets){
                final List<Map<String, Object>> listMap = com.github.pdaodao.springwebplus.base.util.ExcelUtil.readMapByFile(filePath, sh, ListUtil.of("question", "answer", "title", "content"));
                if(CollUtil.isEmpty(listMap)){
                    continue;
                }
                String sheetTopicId = topicId;
                if(StrUtil.isBlank(topicId)){
                    final AiChatTopic topic = topicDao.saveByTitle(namespace, teamId, StrUtil.trim(sh));
                    sheetTopicId = topic.getId();
                }
                final List<AiChatText> textList = new ArrayList<>();
                for(final Map<String, Object> map: listMap){
                    final AiChatText text = new AiChatText();
                    text.setNamespace(namespace);
                    text.setTeamId(teamId);
                    text.setTopicId(sheetTopicId);
                    String title = StrUtil.toStringOrNull(map.get("title"));
                    if(StrUtil.isBlank(title)){
                        title = StrUtil.toStringOrNull(map.get("question"));
                    }
                    text.setTitle(title);
                    String content = StrUtil.toStringOrNull(map.get("content"));
                    if(StrUtil.isBlank(content)){
                        content = StrUtil.toStringOrNull(map.get("answer"));
                    }
                    text.setContent(content);
                    if(StrUtil.isBlank(text.toEmbeddingText())){
                        continue;
                    }
                    textList.add(text);
                }
                termTextService.saveBatch(sheetTopicId, teamId, textList);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }finally{
            tempFs.delete(fileInfo.getPath());
        }
        return true;
    }


    @GetMapping("info")
    @Operation(summary = "详情")
    public AiChatText info(final String id) throws Exception{
        return termTextService.info(id);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody IdWrap<String> idWrap) throws Exception{
        return termTextService.deleteById(idWrap.getId());
    }
}