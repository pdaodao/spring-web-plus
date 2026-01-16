package com.github.pdaodao.springwebplus.task.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.github.pdaodao.springwebplus.task.config.ZtTaskNodeConfig;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskLogDao;
import com.github.pdaodao.springwebplus.task.dao.ZtTaskNodeDao;
import com.github.pdaodao.springwebplus.task.entity.ZtTaskNodeEntity;
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
public class ZtTaskNodeRegistService implements InitializingBean, Runnable {
    private final ZtTaskNodeDao nodeDao;
    private final ZtTaskLogDao logDao;
    private final ZtTaskNodeConfig taskNodeConfig;
    private final ZtNodeAdminService adminService;
    private transient ScheduledExecutorService scheduler;

    private List<ZtTaskNodeEntity> nodeList = new ArrayList<>();

    public ZtTaskNodeRegistService(ZtTaskNodeDao nodeDao, ZtTaskLogDao logDao, ZtTaskNodeConfig nodeConfig, ZtNodeAdminService adminScheduler) {
        this.nodeDao = nodeDao;
        this.logDao = logDao;
        this.taskNodeConfig = nodeConfig;
        this.adminService = adminScheduler;
    }

    public ZtTaskNodeEntity executorNode(final String taskId) {
        final List<ZtTaskNodeEntity> ns = nodeList.stream().filter(t -> !t.getIsAdmin()).collect(Collectors.toList());
        Preconditions.checkArgument(CollUtil.size(ns) > 0, "执行节点不存在");
        return ns.get(RandomUtil.randomInt(ns.size() - 1));
    }

    public ZtTaskNodeEntity adminNode(final Long taskId) {
        final List<ZtTaskNodeEntity> ns = nodeList.stream().filter(t -> !t.getIsAdmin()).collect(Collectors.toList());
        Preconditions.checkArgument(CollUtil.size(ns) > 0, "执行节点不存在");
        return ns.get((int)(taskId % CollUtil.size(ns)));
    }

    @Override
    public void run() {
        try{
            final ZtTaskNodeEntity nodeEntity = new ZtTaskNodeEntity();
            nodeEntity.setUrl(taskNodeConfig.getHost());
            nodeEntity.setId(taskNodeConfig.getNodeId());
            nodeEntity.setIsAdmin(false);
            if(BooleanUtil.isTrue(taskNodeConfig.getIsExecutor())){
                nodeDao.save(nodeEntity);
            }
            if(BooleanUtil.isTrue(taskNodeConfig.getIsAdmin())){
                nodeEntity.setId(taskNodeConfig.getNodeId()+100);
                nodeEntity.setIsAdmin(true);
                nodeDao.save(nodeEntity);
            }
            // 清除2分钟前刷新的节点信息
            nodeDao.clear(DateTimeUtil.offsetMinute(DateTimeUtil.now(), -2));
            loadNodes();
            logDao.setRunningErrorByNodeId(taskNodeConfig.getNodeId());
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    private void loadNodes(){
        final List<ZtTaskNodeEntity> oldList = nodeList;
        nodeList = nodeDao.listEnabled();
        if(CollUtil.isEmpty(nodeList)){
            return;
        }

        final Optional<ZtTaskNodeEntity> firstOption = nodeList.stream().filter(t -> t.getIsAdmin()).findFirst();
        if(!firstOption.isPresent()){
            adminService.setIsAdmin(false);
        }
        if(ObjectUtil.equals(taskNodeConfig.getNodeId() + 100, firstOption.get().getId())){
            adminService.setIsAdmin(true);
        }
        final Set<String> ids = nodeDao.list().stream().map(t -> t.getId()).collect(Collectors.toSet());
        for(final ZtTaskNodeEntity old: oldList){
            if(!ids.contains(old.getId())){
                logDao.setRunningErrorByNodeId(old.getId());
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this, 3, 90, TimeUnit.SECONDS);
    }
}
