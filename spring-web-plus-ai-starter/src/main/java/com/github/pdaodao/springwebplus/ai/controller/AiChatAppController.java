package com.github.pdaodao.springwebplus.ai.controller;

import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.base.ChatModelType;
import com.github.pdaodao.springwebplus.ai.dao.AiChatAppDao;
import com.github.pdaodao.springwebplus.ai.dao.AiChatModelDao;
import com.github.pdaodao.springwebplus.ai.entity.AiChatApp;
import com.github.pdaodao.springwebplus.ai.entity.AiChatModel;
import com.github.pdaodao.springwebplus.ai.service.AiChatTextInfoProviderFactory;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import com.github.pdaodao.springwebplus.base.pojo.IdWrap;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.data.IdTitle;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "问答场景管理")
@RestController
@AllArgsConstructor
@RequestMapping(Constant.ChatApiPrefix + "/app")
public class AiChatAppController {
    private final AiChatAppDao appDao;
    private final AiChatModelDao modelDao;
    private final AiChatTextInfoProviderFactory providerFactory;

    @GetMapping("list")
    @Operation(summary = "列表")
    public List<AiChatApp> list() {
        final List<AiChatModel>  models = modelDao.list(null, RequestUtil.getTeamOrDefault(), null);
        final Map<String, String> nameMap = new HashMap<>();
        for(final AiChatModel m: models){
            nameMap.put(m.getId(), m.getTitle());
        }
        final List<AiChatApp> list = appDao.list(QueryBuilder.lambda(AiChatApp.class)
                .eq(AiChatApp::getTeamId, RequestUtil.getTeamOrDefault())
                .build().orderByAsc(AiChatApp::getSeq));
        for(final AiChatApp app: list){
            if(StrUtil.isNotBlank(app.getModelId())){
                app.setModelTitle(nameMap.get(app.getModelId()));
            }
        }
        return list;
    }

    @GetMapping("info")
    @Operation(summary = "详情")
    public AiChatApp info(final String id) {
        return appDao.info(id);
    }

    @PostMapping("save")
    @Operation(summary = "保存")
    public AiChatApp info(@RequestBody final AiChatApp app) {
        appDao.save(app);
        return app;
    }


    @GetMapping("modelList")
    @Operation(summary = "模型列表")
    public List<IdTitle> modelList() {
        final List<AiChatModel> list = modelDao.list(ChatModelType.LLM, RequestUtil.getTeamId(), true);
        final List<IdTitle> ret = new ArrayList<>();
        for(final AiChatModel m: list){
            ret.add(IdTitle.of(m.getId(), m.getTitle()));
        }
        return ret;
    }

    @GetMapping("knowledgeList")
    @Operation(summary = "知识库列表")
    public List<IdTitle> knowledgeList() {
        return new ArrayList<>();
    }

    @GetMapping("datasourceList")
    @Operation(summary = "数据源列表")
    public List<IdTitle> datasourceList(@RequestParam(required = false) final String q) {
        return providerFactory.byType("datasource").list(RequestUtil.getTeamOrDefault(), null, q, null);
    }

    @GetMapping("tableList")
    @Operation(summary = "数据表列表")
    public List<IdTitle> tableList(@RequestParam(required = false) final String q,
                                   @Parameter(description = "数据源id") final String dbId) {
        return providerFactory.byType("table").list(RequestUtil.getTeamOrDefault(), dbId, q, null);
    }

    @PostMapping("delete")
    @Operation(summary = "删除")
    public Boolean delete(@Validated @RequestBody IdWrap<String> idWrap) {
        return appDao.removeById(idWrap.getId());
    }
}
