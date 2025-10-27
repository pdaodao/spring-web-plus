package com.github.pdaodao.springwebplus.ai.base;

public enum LLMMsgType {
    text,
    sql,
    sqlData,
    reasoning,

    thinking,

    web_search_call,

    web_search_result,

    image,
    audio,
    video,
    // pdf, text, world
    file,

    tool_call,
    tool_call_chunk,
    invalid_tool_call,

    server_tool_call,
    server_tool_call_chunk,
    server_tool_result,

    non_standard
}
