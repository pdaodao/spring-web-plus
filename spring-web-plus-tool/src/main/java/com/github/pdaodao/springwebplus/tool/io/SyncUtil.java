package com.github.pdaodao.springwebplus.tool.io;

import com.github.pdaodao.springwebplus.tool.data.StreamRow;

public class SyncUtil {

    public static long tableSync(final Reader reader, final Writer writer) throws Exception {
        try {
            writer.open();
            try {
                reader.open();
                final Long readTotal = reader.total();
                while (true) {
                    final StreamRow row = reader.read();
                    if (row == null || row.isEnd()) {
                        break;
                    }
                    writer.write(row);
                }
            } finally {
                reader.close();
            }
        } finally {
            writer.close();
        }
        return writer.total();
    }
}
