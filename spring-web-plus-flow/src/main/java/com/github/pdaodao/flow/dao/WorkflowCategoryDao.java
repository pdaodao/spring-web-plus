package com.github.pdaodao.flow.dao;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pdaodao.flow.entity.WorkflowCategory;
import com.github.pdaodao.flow.mapper.WorkflowCategoryMapper;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkflowCategoryDao extends BaseDao<WorkflowCategoryMapper, WorkflowCategory> {
    public WorkflowCategory byTitle(final String title, final String teamId){
        return getOne(QueryBuilder.lambda(WorkflowCategory.class)
                .eq(WorkflowCategory::getTitle, StrUtil.trim(title))
                .eq(WorkflowCategory::getTeamId, teamId)
                .build());
    }

    public List<WorkflowCategory> list(final String teamId, final String q){
        return list(QueryBuilder.lambda(WorkflowCategory.class)
                .eq(WorkflowCategory::getTeamId, teamId)
                .like(q, WorkflowCategory::getTitle).build());
    }

    @Override
    protected void saveCheck(WorkflowCategory entity, boolean isInsert) {
        final WorkflowCategory old = byTitle(entity.getTitle(), entity.getTeamId());
        if (old != null) {
            Preconditions.checkArgument(ObjectUtil.equals(entity.getId(), old.getId()), "名称已存在");
        }
    }
}
