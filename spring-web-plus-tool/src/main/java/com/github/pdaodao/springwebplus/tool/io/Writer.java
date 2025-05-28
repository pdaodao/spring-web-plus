package com.github.pdaodao.springwebplus.tool.io;

import com.github.pdaodao.springwebplus.tool.data.TableRow;
import com.github.pdaodao.springwebplus.tool.io.lang.StreamIO;

public interface Writer extends StreamIO {

    void write(TableRow row) throws Exception;

    void flush() throws Exception;
    // void checkpoint();
}
