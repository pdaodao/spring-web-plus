package com.github.pdaodao.springwebplus.tool.db.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
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
    protected List<Object> params;

    /**
     * 字典id
     */
    protected String dicId;


    public static FilterItem of(final String name, final WhereOperator op, Object... params) {
        final FilterItem f = new FilterItem();
        f.setName(name);
        f.setOp(op);
        f.setParams(Convert.toList(Object.class, params));
        return f;
    }

    /**
     * 多个过滤条件项目 转为 字段：参数 map
     *
     * @param filterItems
     * @return
     */
    public static Map<String, Object> toParamMap(final List<FilterItem> filterItems) {
        final Map<String, Object> params = new LinkedHashMap<>();
        if (CollUtil.isEmpty(filterItems)) {
            return params;
        }
        for (final FilterItem f : filterItems) {
            if (f.paramSize() < 1) {
                params.put(f.getName(), null);
            } else if (f.paramSize() > 1) {
                params.put(f.getName(), f.getParams());
            } else {
                params.put(f.getName(), f.getParam());
            }
        }
        return params;
    }

    @JsonIgnore
    public Object getParam() {
        if (CollUtil.isEmpty(params)) {
            return null;
        }
        return params.get(0);
    }

    @JsonIgnore
    public void setParam(final Object p) {
        if (ObjectUtil.isNull(p)) {
            return;
        }
        if (params == null) {
            params = new ArrayList<>();
        }
        params.add(p);
    }

    /**
     * 参数个数
     *
     * @return
     */
    public int paramSize() {
        if (CollUtil.isEmpty(params)) {
            return 0;
        }
        return params.size();
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
        if (CollUtil.isNotEmpty(params)) {
            sb.append(StrUtil.join(",", params));
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
        Preconditions.assertTrue(CollUtil.isEmpty(params), getName()+"("+getTitle()+")比较值为空");
        final String vv = StrUtil.toString(getParam());
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
            Preconditions.checkArgument(params.size() == 2, "between的值为两个例如 a,b");
            final String prefix = isValueNeedQuote(params.get(0)) ? "'" : "";
            final String left = prefix + params.get(0) + prefix;
            final String right = prefix + params.get(1) + prefix;
            return left + " AND " + right;
        }
        if(WhereOperator.in == op){
            final List<String> list = new ArrayList<>();
            for(final Object v: params){
                final String prefix = isValueNeedQuote(params.get(0)) ? "'" : "";
                list.add(prefix + v +prefix);
            }
            return "("+StrUtil.join(",", list)+")";
        }
        final String prefix = isValueNeedQuote(params.get(0)) ? "'" :"";
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
