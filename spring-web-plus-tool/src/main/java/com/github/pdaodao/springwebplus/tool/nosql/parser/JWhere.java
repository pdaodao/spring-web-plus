package com.github.pdaodao.springwebplus.tool.nosql.parser;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.sql.core.LogicOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * sql语句中的 过滤部分
 */
public class JWhere extends JMethodField {
    private LogicOperator logic;

    private final JOperator operator;   // 操作符

    private List<JWhere> wheres = new ArrayList<>();

    /**
     * 函数类型的比较
     *
     * @param method
     * @param args
     */
    public JWhere(String method, List<Object> args, JOperator operator) {
        super(null, null, method, args);
        this.operator = operator;
    }

    public void setLogic(LogicOperator logic) {
        this.logic = logic;
    }

    /**
     * a = b 类型的简单比较符 非函数类型的比较
     *
     * @param operator 比较符
     * @param name     字段名称
     * @param args     比较值
     */
    public JWhere(JOperator operator, String name, List<Object> args) {
        super(name, null, null, args);
        this.operator = operator;
    }


    /**
     * 逻辑链接符 and / or
     *
     * @param operator
     */
    public JWhere(JOperator operator) {
        super(null, null, null, null);
        this.operator = operator;
    }

    public void addWhere(JWhere where) {
        if (where != null) {
            this.wheres.add(where);
        }
    }

    public LogicOperator getLogic() {
        return logic;
    }

    public JOperator getOperator() {
        return operator;
    }

    public List<JWhere> getWheres() {
        return wheres;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (operator.isLogical()) {
            if (wheres == null) return StrUtil.EMPTY;
            if (wheres.size() > 1)
                sb.append("(");
            sb.append(StrUtil.join(operator.name, wheres));
            if (wheres.size() > 1)
                sb.append(")");
        } else {
            sb.append(StrUtil.SPACE).append(name).append(StrUtil.SPACE)
                    .append(operator.name).append(StrUtil.SPACE);
            if (args.size() == 1) {
                sb.append(args.get(0));
            } else {
                sb.append("(").append(StrUtil.join(",", args)).append(")");
            }
            sb.append(StrUtil.SPACE);
        }
        return sb.toString();
    }

    // 提取 like equal
    public void likeOrEqual(Set<String> fields, Set<String> values) {
        if (operator.isLogical()) {
            if(CollectionUtil.isEmpty(wheres)) return;
            for(JWhere w: wheres) {
                w.likeOrEqual(fields, values);
            }
        } else {
            if(CollectionUtil.isEmpty(args)) return;

            if(this.operator == JOperator.Equality || this.operator == JOperator.Like){
                String v = Objects.toString(args.get(0));
                if(StrUtil.isNotEmpty(v)){
                    if(this.operator == JOperator.Like)
                        v = v.replaceAll("%", StrUtil.EMPTY);
                    fields.add(getName());
                    values.add(v);
                }
            }
        }
    }
}