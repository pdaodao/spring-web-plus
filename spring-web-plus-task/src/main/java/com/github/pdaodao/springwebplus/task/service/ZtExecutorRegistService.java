package com.github.pdaodao.springwebplus.task.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.task.config.ZtTaskExecutorConfig;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskCronDao;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskLogDao;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskExecutorDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskExecutorEntity;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 节点注册服务
 */
@Slf4j
@Service
public class ZtExecutorRegistService implements InitializingBean, Runnable {
    private final ZtTaskExecutorDao nodeDao;
    private final ZtTaskCronDao cronDao;
    private final ZtTaskLogDao logDao;
    private final ZtTaskExecutorConfig taskNodeConfig;
    private final ZtExecutorAdminService adminService;
    private transient ScheduledExecutorService scheduler;

    private List<ZtTaskExecutorEntity> nodeList = new ArrayList<>();
    private boolean processed = false;

    public ZtExecutorRegistService(ZtTaskExecutorDao nodeDao, ZtTaskCronDao cronDao, ZtTaskLogDao logDao,
                                   ZtTaskExecutorConfig nodeConfig, ZtExecutorAdminService adminScheduler) {
        this.nodeDao = nodeDao;
        this.cronDao = cronDao;
        this.logDao = logDao;
        this.taskNodeConfig = nodeConfig;
        this.adminService = adminScheduler;
    }

    public ZtTaskExecutorEntity executorNode(final String taskId) {
        final List<ZtTaskExecutorEntity> ns = nodeList.stream()
                .filter(t -> BooleanUtil.isTrue(t.getIsExecutor()))
                .filter(t -> BooleanUtil.isTrue(t.getEnabled()))
                .collect(Collectors.toList());
        Preconditions.checkArgument(CollUtil.size(ns) > 0, "执行节点不存在");
        return ns.get(RandomUtil.randomInt(ns.size()));
    }

    @Override
    public void run() {
        if(processed == false){
            cronDao.setExecutorRestartError(taskNodeConfig.getNodeId());
            logDao.setRunningErrorByNodeId(taskNodeConfig.getNodeId());
        }
        processed = true;
        try{
            final ZtTaskExecutorEntity nodeEntity = new ZtTaskExecutorEntity();
            nodeEntity.setUrl(taskNodeConfig.getHost());
            nodeEntity.setId(taskNodeConfig.getNodeId());
            nodeEntity.setIsAdmin(taskNodeConfig.getIsAdmin());
            nodeEntity.setIsExecutor(taskNodeConfig.getIsExecutor());
            nodeEntity.setAccess(taskNodeConfig.getAccess());
            final ZtTaskExecutorEntity old = nodeDao.getById(taskNodeConfig.getNodeId());
            if(old != null){
                nodeEntity.setIsAdmin(null);
                nodeEntity.setIsExecutor(null);
                nodeEntity.setAccess(null);
            }
            nodeDao.save(nodeEntity);
            // 清除2分钟前刷新的节点信息
            nodeDao.clear(DateTimeUtil.offsetMinute(DateTimeUtil.now(), -2));
            loadNodes();
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    public ZtTaskExecutorEntity getById(final String id){
        return nodeList.stream().filter(t -> StrUtil.equals(id, t.getId())).findFirst().get();
    }

    private void loadNodes(){
        final List<ZtTaskExecutorEntity> oldList = nodeList;
        nodeList = nodeDao.listEnabled();
        if(CollUtil.isEmpty(nodeList)){
            return;
        }

        final Optional<ZtTaskExecutorEntity> firstOption = nodeList.stream().filter(t -> t.getIsAdmin()).findFirst();
        if(!firstOption.isPresent()){
            adminService.setIsAdmin(false);
        }
        if(ObjectUtil.equals(taskNodeConfig.getNodeId() + 100, firstOption.get().getId())){
            adminService.setIsAdmin(true);
        }
        final Set<String> ids = nodeDao.list().stream().map(t -> t.getId()).collect(Collectors.toSet());
        for(final ZtTaskExecutorEntity old: oldList){
            if(!ids.contains(old.getId())){
                logDao.setRunningErrorByNodeId(old.getId());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this, 6, 90, TimeUnit.SECONDS);
        taskNodeConfig.setAccess(RandomUtil.randomString(8));
    }
}
