package com.github.pdaodao.flow.dao;

import com.github.pdaodao.flow.entity.WorkflowDefine;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.flow.entity.WorkflowDefineModel;
import com.github.pdaodao.flow.mapper.WorkflowDefineModelMapper;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import org.springframework.stereotype.Component;

@Component
public class WorkflowDefineModelDao extends BaseDao<WorkflowDefineModelMapper, WorkflowDefineModel> {

    public WorkflowDefineModel saveInfo(final WorkflowDefine define){
        if(define.getVersion() == null){
            define.setVersion(1);
        }
        WorkflowDefineModel old = byFlowVersion(define.getId(), define.getVersion());
        if(old == null){
            old = new WorkflowDefineModel();
            old.setFlowId(define.getId());
            old.setVersion(define.getVersion());
        }
        old.setFlow(define.getFlow());
        old.setName(define.getName());
        old.setTitle(define.getTitle());
        old.setPagePath(define.getPagePath());
        save(old);
        return old;
    }

    public WorkflowDefineModel byFlowVersion(final String flowId, final Integer version){
        final WorkflowDefineModel model = getOne(QueryBuilder.lambda(WorkflowDefineModel.class)
                .eq(WorkflowDefineModel::getFlowId, flowId)
                .eq(WorkflowDefineModel::getVersion, version).build());
        return model;
    }
}
