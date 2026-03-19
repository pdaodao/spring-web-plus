package com.github.pdaodao.springwebplus.tool.sql.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.Data;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 过滤条件项
 */
@Data
public class FilterItem {
    /**
     * 字段
     */
    protected String name;

    /**
     * 字段中文名称
     */
    protected String title;

    /**
     * 标准字段类型
     */
    protected DataType dataType;

    /**
     * 比较符号
     */
    protected WhereOperator op;

    /**
     * 参数值
     */
    protected RichValue param;

    /**
     * 字典id
     */
    protected String dicId;


    public static FilterItem of(final String name, final WhereOperator op, Object... params) {
        final FilterItem f = new FilterItem();
        f.setName(name);
        f.setOp(op);
        f.setParamValue(RichValue.ofLiteral(params));
        return f;
    }

    /**
     * 多个过滤条件项目 转为 字段：参数 map
     *
     * @param filterItems
     * @return
     */
    public static Map<String, Object> toParamValueMap(final List<FilterItem> filterItems) {
        final Map<String, Object> params = new LinkedHashMap<>();
        if (CollUtil.isEmpty(filterItems)) {
            return params;
        }
        for (final FilterItem f : filterItems) {
            if(f.getParam() == null || f.getParam().empty()){
                params.put(f.getName(), null);
            }else if(f.getParam().size() == 1){
                params.put(f.getName(), f.paramValue());
            }else{
                final List<Object> list = new ArrayList<>();
                for(final RichValue v: f.getParam().getValues()){
                    list.add(v.getValue());
                }
                params.put(f.getName(), list);
            }
        }
        return params;
    }

    @JsonIgnore
    public Object paramValue() {
        if(param == null || param.empty()){
            return null;
        }
        return param.getValue();
    }

    @JsonIgnore
    public void setParamValue(final Object pValue) {
        if (ObjectUtil.isNull(pValue)) {
            return;
        }
        if (param == null) {
            param = new RichValue();
            param.setType(RichValue.ValueType.lr);
        }
        if(param.getValues() == null){
            param.setValue(pValue);
        }else{
            param.addToValues(RichValue.ValueType.lr, param.getValue());
            param.addToValues(RichValue.ValueType.lr, pValue);
            param.setValue(null);
        }
    }

    /**
     * 参数个数
     *
     * @return
     */
    public int paramValueSize() {
        if(param == null){
            return 0;
        }
        if(CollUtil.isNotEmpty(param.getValues())){
            return CollUtil.size(param.getValue());
        }
        if(ObjectUtil.isNotNull(param.getValue())){
            return 1;
        }
        return 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ");
        if (op != null) {
            sb.append(op.sql);
        } else {
            sb.append("?");
        }
        sb.append(" ");
        if (ObjectUtil.isNotEmpty(param)) {
            sb.append(param.toString());
        }
        return sb.toString();
    }

    public String toSql(final DbDialect dialect){
        final StringBuilder sb = new StringBuilder();
//        if(StrUtil.isNotBlank(fn)){
//            if("count_distinct".equalsIgnoreCase(fn)){
//                sb.append("COUNT").append("(DISTINCT ");
//            }else{
//                sb.append(fn).append("(");
//            }
//        }
        if(name.contains(" ") || name.contains("(") || dialect == null){
            sb.append(name);
        }else{
            sb.append(dialect.quoteIdentifier(name));
        }
//        if(StrUtil.isNotBlank(fn)){
//            sb.append(")");
//        }
        return sb.toString();
    }

    protected String valueInSql(){
        Preconditions.checkNotNull(op, getName()+"("+getTitle()+")比较符为空");
        if(op.sql.startsWith("IS ")){
            return null;
        }
        Preconditions.assertTrue(param == null || param.empty(), getName()+"("+getTitle()+")比较值为空");
        final String vv = StrUtil.toString(paramValue());
        // like in between 比较特殊
        if(op.sql.equalsIgnoreCase("like")){
            if(WhereOperator.sw == op){
                return vv +"%'";
            }
            if(WhereOperator.ew == op){
                return "'%"+ vv +"'";
            }
            return  "'%"+ vv +"%'";
        }
        if(WhereOperator.bt == op){
            Preconditions.checkArgument(param.size() == 2, "between的值为两个例如 a,b");
            final String prefix = isValueNeedQuote(param.get(0)) ? "'" : "";
            final String left = prefix + param.get(0) + prefix;
            final String right = prefix + param.get(1) + prefix;
            return left + " AND " + right;
        }
        if(WhereOperator.in == op){
            final List<String> list = new ArrayList<>();
            for(final Object v: param.getValues()){
                final String prefix = isValueNeedQuote(param.get(0)) ? "'" : "";
                list.add(prefix + v +prefix);
            }
            return "("+StrUtil.join(",", list)+")";
        }
        final String prefix = isValueNeedQuote(param.get(0)) ? "'" :"";
        return  prefix + vv + prefix;
    }

    protected boolean isValueNeedQuote(final Object vv){
        if(vv == null || !(vv instanceof String)){
            return false;
        }
        String v = StrUtil.toString(vv);
        if(v.contains("(") && v.contains(")") && !(v.toLowerCase().contains(" and ") || v.toLowerCase().contains(" or "))){
            return false;
        }
        if(dataType != null && dataType.isStringFamily()){
            return true;
        }
        if(NumberUtil.isNumber(v)){
            return false;
        }
        return true;
    }
}
