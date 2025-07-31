package com.github.pdaodao.springwebplus.tool.flow.processor.join;

import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.flow.base.NodeFieldDef;
import com.github.pdaodao.springwebplus.tool.sql.core.JoinType;
import com.github.pdaodao.springwebplus.tool.sql.core.WhereOperator;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据关联join
 */
@Data
public class JoinInfo {

    /**
     * 类型
     */
    private JoinType joinType;

    /**
     * 条件
     */
    private List<JoinCondition> joinOperators;


    private List<String> ons = new ArrayList<>();

    /**
     * 左字段
     */
    private List<NodeFieldDef> leftFields;

    /**
     * 右字段
     */
    private List<NodeFieldDef> rightFields;

    public void addJoinItem(final String leftField,
                            final String leftAlias,
                            final WhereOperator op,
                            final  String rightField, final  String rightAlias){
        if(joinOperators == null){
            joinOperators = new ArrayList<>();
        }
        final JoinCondition j = new JoinCondition();
        j.setLeftField(leftField);
        j.setLeftAlias(leftAlias);
        j.setOp(op);
        j.setRightField(rightField);
        j.setRightAlias(rightAlias);
        joinOperators.add(j);
    }

    public void addJoinItem(final JoinCondition j){
        if(j == null){
            return;
        }
        if(joinOperators == null){
            joinOperators = new ArrayList<>();
        }
        joinOperators.add(j);
    }

    /**
     * 联合条件
     */
    @Data
    public static class JoinCondition {
        private String leftField;
        private String leftAlias;
        private WhereOperator op;
        private String rightField;
        private String rightAlias;

        public String getLeftAlias() {
            return StrUtil.isNotBlank(leftAlias) ? leftAlias : leftField;
        }

        public String getRightAlias() {
            return  StrUtil.isNotBlank(rightAlias) ? rightAlias : rightField;
        }
    }
}
