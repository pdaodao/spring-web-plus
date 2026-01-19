package com.github.pdaodao.springwebplus.ai;

import com.github.pdaodao.springwebplus.tool.data.IdTitle;

import java.util.List;

public interface AiChatTextInfoProvider {
    String type();

    List<IdTitle> list(final String teamId, final String pid,
                       final String q, final List<String> ids);
}