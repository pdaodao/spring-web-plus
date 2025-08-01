package com.github.pdaodao.flow.service;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.flow.entity.WorkflowDefine;
import com.github.pdaodao.flow.entity.WorkflowInstance;
import com.github.pdaodao.flow.pojo.FlowFormData;
import com.github.pdaodao.springwebplus.tool.flow.Flow;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WorkflowRuntimeService {
    private final WorkflowInfoService flowInfoService;

    public WorkflowInstance start(final FlowFormData flowFormData){
        //1. 获取流程定义
        final WorkflowDefine workflowDefine = flowInfoService.infoPublished(flowFormData.getFlowId());
        Preconditions.checkNotNull(workflowDefine, "流程{}不存在.", flowFormData.getFlowId());
        final Flow flow = workflowDefine.getFlow();
        Preconditions.checkNotNull(flow, "流程[{}]内容为空.", workflowDefine.getTitle());
        Preconditions.assertTrue(CollUtil.isEmpty(flow.rootNodes()), "流程[{}]无节点", workflowDefine.getTitle());
        Preconditions.assertTrue("0".equals(flow.rootNodes().get(0).getId()), "流程[{}]无开始节点", workflowDefine.getTitle());

        //2. 创建流程实例
        final WorkflowInstance instance = new WorkflowInstance();
        instance.setFlowId(workflowDefine.getId());
        instance.setFlowVersion(workflowDefine.getVersion());
        //3. 执行流程



        return null;
    }

    /**
     * 执行流程
     * @param flow
     * @param flowFormData
     */
    private void executeFlow(final Flow flow,  final FlowFormData flowFormData){

    }
}
