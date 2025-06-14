package com.github.pdaodao.springwebplus.task.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ZtNodeAdminService {
    private final ZtNodeAdminScheduler adminScheduler;
    public void setIsAdmin(final boolean is){
        adminScheduler.setIsAdmin(is);
    }

}
