package com.github.pdaodao.springwebplus.ai;

import com.github.pdaodao.springwebplus.ai.doc.AiDoc;

import java.io.InputStream;

public interface AiDocParser {

    AiDoc read(final InputStream inputStream);
}