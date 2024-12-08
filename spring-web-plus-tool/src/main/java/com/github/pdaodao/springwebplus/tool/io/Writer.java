package com.github.pdaodao.springwebplus.tool.io;

import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.io.lang.StreamIO;

public interface Writer extends StreamIO {

    void write(StreamRow row) throws Exception;

    // void checkpoint();
}
