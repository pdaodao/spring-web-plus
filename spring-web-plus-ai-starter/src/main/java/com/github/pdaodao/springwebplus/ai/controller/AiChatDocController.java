package com.github.pdaodao.springwebplus.ai.controller;

import com.github.pdaodao.springwebplus.ai.dao.AiChatDocDao;
import com.github.pdaodao.springwebplus.ai.util.Constant;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "知识文档管理")
@RestController
@AllArgsConstructor
@RequestMapping(Constant.ChatApiPrefix + "/doc")
public class AiChatDocController {
    private final AiChatDocDao docDao;

}
