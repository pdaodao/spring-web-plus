package com.github.pdaodao.springwebplus.tool.db.sql.frame;

import java.io.IOException;

public class SafeAppendable {
    public static final String AND = ") \nAND (";
    public static final String OR = ") \nOR (";

    private final Appendable appendable;
    private boolean empty = true;

    public SafeAppendable(Appendable a) {
        this.appendable = a;
    }

    public SafeAppendable() {
        this.appendable = new StringBuilder();
    }

    public SafeAppendable append(CharSequence s) {
        try {
            if (empty && s.length() > 0) {
                empty = false;
            }
            appendable.append(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public boolean isEmpty() {
        return empty;
    }

    @Override
    public String toString() {
        return appendable.toString();
    }
}
