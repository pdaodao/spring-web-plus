package com.github.pdaodao.springwebplus.dao;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysFile;
import com.github.pdaodao.springwebplus.mapper.SysFileMapper;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SysFileDao extends BaseDao<SysFileMapper, SysFile> {
    public List<SysFile> byNamespace(final String namespace, final String objId) {
        return list(QueryBuilder.lambda(SysFile.class)
                .eq(SysFile::getNamespace, namespace)
                .eq(SysFile::getObjId, objId).build());
    }

    /**
     * 把文件和主体内容进行绑定 以为新建内容时的上传还没有主体的id
     *
     * @param namespace
     * @param objId
     * @param list
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateObjId(final String namespace, final String objId, final List<SysFile> list) {
        Preconditions.checkNotBlank(namespace, "文件信息命名空间不能为空");
        Preconditions.checkNotBlank(objId, "文件信息主体不能为空");
        update(Wrappers.lambdaUpdate(SysFile.class)
                .set(SysFile::getObjId, null)
                .eq(SysFile::getNamespace, namespace)
                .eq(SysFile::getObjId, objId));
        if (CollUtil.isEmpty(list)) {
            return;
        }
        update(Wrappers.lambdaUpdate(SysFile.class)
                .set(SysFile::getObjId, objId)
                .set(SysFile::getNamespace, namespace)
                .in(SysFile::getId, list.stream().map(t -> t.getId()).collect(Collectors.toList())));
    }

    public SysFile saveInfo(final FileInfo fileInfo) {
        Preconditions.checkNotNull(fileInfo, "文件信息为空");
        final SysFile sysFile = new SysFile();
        BeanUtils.copyPropertiesIgnoreTransient(fileInfo, sysFile);
        save(sysFile);
        fileInfo.setId(sysFile.getId());
        fileInfo.setCreateTime(sysFile.getCreateTime());
        return sysFile;
    }
}
