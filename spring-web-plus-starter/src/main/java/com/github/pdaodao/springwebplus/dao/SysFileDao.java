package com.github.pdaodao.springwebplus.dao;

import com.github.pdaodao.springwebplus.base.dao.BaseDao;
import com.github.pdaodao.springwebplus.base.query.QueryBuilder;
import com.github.pdaodao.springwebplus.entity.SysFile;
import com.github.pdaodao.springwebplus.mapper.SysFileMapper;
import com.github.pdaodao.springwebplus.tool.fs.FileInfo;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SysFileDao extends BaseDao<SysFileMapper, SysFile> {
    public List<SysFile> byNamespace(final String namespace, final String pid){
        return list(QueryBuilder.lambda(SysFile.class)
                .eq(SysFile::getNamespace, namespace)
                .eq(SysFile::getPid, pid).build());
    }

    public SysFile saveInfo(final FileInfo fileInfo){
        Preconditions.checkNotNull(fileInfo, "文件信息为空");
        final SysFile sysFile = new SysFile();
        BeanUtils.copyPropertiesIgnoreTransient(fileInfo, sysFile);
        save(sysFile);
        fileInfo.setId(sysFile.getId());
        fileInfo.setCreateTime(sysFile.getCreateTime());
        return sysFile;
    }
}
