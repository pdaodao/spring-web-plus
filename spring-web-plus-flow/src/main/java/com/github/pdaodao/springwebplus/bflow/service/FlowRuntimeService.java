package com.github.pdaodao.springwebplus.bflow.service;

import com.github.pdaodao.springwebplus.bflow.entity.BfFlowEntity;
import com.github.pdaodao.springwebplus.bflow.entity.BfInstanceEntity;
import org.springframework.stereotype.Service;

@Service
public class FlowRuntimeService {

    /**
     * 创建流程实例
     * @param flow
     * @return
     */
    public BfInstanceEntity createInstance(final BfFlowEntity flow){
        final BfInstanceEntity ii = new BfInstanceEntity();
        ii.setFlowId(flow.getId());
        // todo
        return ii;
    }
}
