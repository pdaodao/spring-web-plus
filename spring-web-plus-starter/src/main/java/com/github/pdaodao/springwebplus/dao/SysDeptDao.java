package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysDept;
import com.github.pdaodao.springwebplus.mapper.SysDeptMapper;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;

@Component
public class SysDeptDao extends BaseDao<SysDeptMapper, SysDept> {
    public SysDept byTitle(final String title, final String teamId, final String pid){
        return getOne(QueryBuilder.lambda(SysDept.class)
                .eq(SysDept::getTitle, StrUtil.trim(title))
                .eq(SysDept::getTeamId, teamId)
                .eq(SysDept::getPid, pid)
                .build());
    }

    @Override
    protected void saveCheck(SysDept entity, boolean isInsert) {
        final SysDept old = byTitle(entity.getTitle(), entity.getTeamId(), entity.getPid());
        if (old != null) {
            Preconditions.checkArgument(ObjectUtil.equals(entity.getId(), old.getId()), "名称已存在");
        }
    }
}