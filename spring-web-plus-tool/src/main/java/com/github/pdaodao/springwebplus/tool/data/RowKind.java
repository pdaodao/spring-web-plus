package com.github.pdaodao.springwebplus.tool.data;

/**
 * 行数据类型
 */
public enum RowKind {
    INSERT("+I", (byte) 0),
    UPDATE_BEFORE("-U", (byte) 1),
    UPDATE_AFTER("+U", (byte) 2),
    DELETE("-D", (byte) 3),
    // 表结构信息
    SCHEMA("M", (byte) 4),
    // 统计信息 如表数据行数
    STAT("T", (byte) 5),
    // 水印
    WATERMARK("W", (byte) 6),

    END("End", (byte) 11);

    private final String shortString;
    private final byte value;

    RowKind(String shortString, byte value) {
        this.shortString = shortString;
        this.value = value;
    }

    public static RowKind fromByteValue(byte value) {
        switch (value) {
            case 0:
                return INSERT;
            case 1:
                return UPDATE_BEFORE;
            case 2:
                return UPDATE_AFTER;
            case 3:
                return DELETE;
            case 4:
                return SCHEMA;
            case 5:
                return STAT;
            case 6:
                return WATERMARK;

            case 11:
                return END;
            default:
                throw new UnsupportedOperationException(
                        "Unsupported byte value '" + value + "' for row kind.");
        }
    }

    public String shortString() {
        return shortString;
    }

    public byte toByteValue() {
        return value;
    }
}