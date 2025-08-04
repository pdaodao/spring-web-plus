package com.github.pdaodao.flow.dao;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.flow.entity.WorkflowDefineModel;
import com.github.pdaodao.flow.query.WorkflowQuery;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.flow.entity.WorkflowDefine;
import com.github.pdaodao.flow.mapper.WorkflowDefineMapper;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class WorkflowDefineDao extends BaseDao<WorkflowDefineMapper, WorkflowDefine> {
    @Autowired
    private WorkflowDefineModelDao modelDao;

    public List<WorkflowDefine> list(final WorkflowQuery query){
        return list(QueryBuilder.lambda(WorkflowDefine.class)
                .eq(WorkflowDefine::getTeamId, query.getTeamId())
                .eq(WorkflowDefine::getPublished, query.getPublished())
                .eq(WorkflowDefine::getCategoryId, query.getCategoryId())
                .like(query.getQ(), WorkflowDefine::getTitle, WorkflowDefine::getName)
                .build());
    }

    public WorkflowDefine byTitle(final String title, final String teamId){
        return getOne(QueryBuilder.lambda(WorkflowDefine.class)
                .eq(WorkflowDefine::getTitle, StrUtil.trim(title))
                .eq(WorkflowDefine::getTeamId, teamId)
                .build());
    }

    public WorkflowDefine info(final String id, final Integer version){
        final WorkflowDefine d = getById(id);
        Preconditions.checkNotNull(d, "信息不存在.");
        if(version == null || version == 0){
            return d;
        }
        final WorkflowDefineModel model = modelDao.byFlowVersion(id, version);
        d.setVersion(version);
        d.setFlow(model.getFlow());
        return d;
    }

    public WorkflowDefine infoPublished(final String id){
        final WorkflowDefine d = getById(id);
        Preconditions.checkNotNull(d, "信息不存在.");
        final WorkflowDefineModel model = modelDao.byFlowVersion(id, d.getVersion());
        if(model == null){
            return d;
        }
        d.setFlow(model.getFlow());
        return d;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean saveDraft(final WorkflowDefine entity) {
        if(entity.getVersion() == null){
            entity.setVersion(0);
        }
        entity.setPublished(false);
        save(entity);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean savePublish(final WorkflowDefine entity) {
        if(entity.getVersion() == null){
            entity.setVersion(1);
        }else{
            entity.setVersion(entity.getVersion() + 1);
        }
        entity.setPublished(true);
        save(entity);
        modelDao.saveInfo(entity);
        return true;
    }

    @Override
    protected void saveCheck(WorkflowDefine entity, boolean isInsert) {
        final WorkflowDefine old = byTitle(entity.getTitle(), entity.getTeamId());
        if (old != null) {
            Preconditions.checkArgument(ObjectUtil.equals(entity.getId(), old.getId()), "名称已存在");
        }
    }
}