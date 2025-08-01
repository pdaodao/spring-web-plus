package com.github.pdaodao.flow.service;

import com.github.pdaodao.flow.dao.WorkflowDefineDao;
import com.github.pdaodao.flow.entity.WorkflowDefine;
import com.github.pdaodao.flow.query.WorkflowQuery;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
public class WorkflowInfoService {
    private final WorkflowDefineDao defineDao;

    public List<WorkflowDefine> list(final WorkflowQuery query){
        return defineDao.list(query);
    }

    public WorkflowDefine byTitle(final String title, final String teamId){
        return defineDao.byTitle(title, teamId);
    }

    public WorkflowDefine info(final String id, final Integer version){
        return defineDao.info(id, version);
    }

    public WorkflowDefine infoPublished(final String id){
        return defineDao.infoPublished(id);
    }

    public WorkflowDefine saveDraft(final WorkflowDefine define){
         defineDao.saveDraft(define);
         return define;
    }

    @Transactional(rollbackFor = Exception.class)
    public WorkflowDefine savePublish(final WorkflowDefine entity) {
        defineDao.savePublish(entity);
        return entity;
    }
}