package com.github.pdaodao.springwebplus.ai.service;

import com.github.pdaodao.springwebplus.ai.dao.AiQuestionAnswerDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AiQuestionAnswerService {
    private final AiQuestionAnswerDao dao;
}