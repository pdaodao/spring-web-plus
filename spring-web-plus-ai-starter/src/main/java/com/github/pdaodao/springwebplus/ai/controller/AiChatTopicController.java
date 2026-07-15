package com.github.pdaodao.springwebplus.ai.controller;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.AiChatNamespace;
import com.github.pdaodao.springwebplus.ai.dao.AiChatTopicDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatTopic;
import com.github.pdaodao.springwebplus.ai.query.AiChatTextQuery;
import com.github.pdaodao.springwebplus.ai.query.AiChatTopicQuery;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.util.PageHelper;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
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
import java.util.List;

@Slf4j
@Tag(name = "知识库分类主题管理")
@RestController
@AllArgsConstructor
@RequestMapping(Constant.ChatApiPrefix + "/topic")
public class AiChatTopicController {
    private final AiChatTopicDao dao;

    @GetMapping("list")
    @Operation(summary = "列表")
    public List<AiChatTopic> list(final AiChatTopicQuery query) {
        if(StrUtil.isBlank(query.getPid())){
            query.setPid("0");
        }
        query.setTeamId(RequestUtil.getTeamId());
        return dao.infoList(query);
    }

    @GetMapping("files")
    @Operation(summary = "文件列表")
    public List<AiChatTopic> files(final AiChatTextQuery query) {
        PageHelper.startPage(query);
        query.setTeamId(RequestUtil.getTeamId());
        return dao.fileList(query);
    }


    @GetMapping("info")
    @Operation(summary = "详情")
    public AiChatTopic info(final String id) {
        final AiChatTopic doc = dao.getById(id);
        return doc;
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public AiChatTopic saveInfo(@RequestBody AiChatTopic aiChatDoc) throws Exception {
        Preconditions.checkNotBlank(aiChatDoc.getTitle(), "标题不能为空");
        dao.save(aiChatDoc);
        return aiChatDoc;
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody IdWrap<String> wrap) throws Exception {
        return dao.delete(wrap.getId());
    }

    @PostMapping("/upload-file")
    @Operation(summary = "上传知识库文档")
    @Parameters({
            @Parameter(name = "file", description = "文件", in = ParameterIn.DEFAULT, required = true, schema = @Schema(name = "file", format = "binary")),
            @Parameter(name = "topicId", description = "主题")
    })
    public AiChatTopic uploadFile(@RequestParam("file") final MultipartFile file,
                                  @RequestParam final String topicId) throws Exception{
        FileUploadUtil.checkSize(file, 50);
        final String teamId = RequestUtil.getTeamOrDefault();
        final AiChatTopic topic = new AiChatTopic();
        topic.setTitle(file.getOriginalFilename());
        topic.setPid(topicId);
        topic.setNamespace(AiChatNamespace.file);
        topic.setTeamId(teamId);
        dao.save(topic);
        return topic;
    }
}