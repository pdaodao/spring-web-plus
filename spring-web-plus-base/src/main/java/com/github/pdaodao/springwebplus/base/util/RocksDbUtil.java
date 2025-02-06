package com.github.pdaodao.springwebplus.base.util;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public class RocksDbUtil {
    static {
        RocksDB.loadLibrary();
    }

    public static RocksDB open(final String path) throws RocksDBException {
        final Options options = new Options();
        options.setCreateIfMissing(true);

        final RocksDB open = RocksDB.open(options, path);
        return open;
    }
}
