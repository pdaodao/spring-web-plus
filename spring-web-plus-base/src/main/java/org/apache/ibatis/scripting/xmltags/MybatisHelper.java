package org.apache.ibatis.scripting.xmltags;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MybatisHelper {
    // 语句前后标识 %   %
    private static Pattern QueryPattern = Pattern.compile("%\\s+(?s).*?\\s+%");
    private static final String TableSelect = "select * from zdzq2 where ";

    /**
     * 处理被 %   % 标识的过滤部分
     * @param sql
     * @param context
     * @return
     */
    public static String processSql(final String sql, final DynamicContext context){
        // 1. 提取语句块
        final List<String> scriptList = parse(sql);
        if(CollUtil.isEmpty(scriptList)){
            return sql;
        }
        return sql;
//        try{
//            // 2. 提取mybatis传递的参数 并打平
//            final Map<String, Object> params = parseParams(context.getBindings());
//            String newSql = sql;
//
//            // 3. 处理每一个语句块
//            for(final String script: scriptList){
//                final String filterSql = script.replace("%", "").replace("%","").trim();
//
//                final Map<String, Object> scriptParams = new HashMap<>();
//                final List<String> vs = ZtSqlUtil.parseVariableName(filterSql);
//                for(final String v: vs){
//                    Preconditions.checkArgument(params.containsKey(v), "sql where param {} not provided", v);
//                    scriptParams.put(v, params.get(v));
//                }
//
//
//                final SqlMapArgWrap sqlWrap = WhereUtils.buildSqlByFilterMap(TableSelect + filterSql, scriptParams, "");
//                final String retSql = sqlWrap.getSql().replace(TableSelect, "").trim();
//                newSql = newSql.replace(script, retSql);
//                if(sqlWrap.getArgs() != null){
//                    for(Map.Entry<String, Object> entry: sqlWrap.getArgs().entrySet()){
//                        context.bind(entry.getKey(), entry.getValue());
//                    }
//                }
//            }
//            return replaceNamed(newSql);
//        }catch (Exception e){
//            throw new RuntimeException(e);
//        }
    }


    /**
     * 提取 mybatis 参数
     * @param param
     * @return
     */
    private static Map<String, Object> parseParams(final Map<String, Object> param){
        final Map<String, Object> paramMap = new HashMap<>();
        final Object pp = param.get("_parameter");
        if(ObjectUtil.isNull(pp)){
            return paramMap;
        }
        if(!(pp instanceof Map)){
            addParamToMap("_p", pp, paramMap);
        }else{
            final HashMap<String, Object> list = (HashMap<String, java.lang.Object>) pp;
            for(final Map.Entry<String, Object> entry : list.entrySet()) {
                addParamToMap(entry.getKey(), entry.getValue(), paramMap);
            }
        }
        return paramMap;
    }

    private static void addParamToMap(final String key, final Object val, final Map<String, Object> paramMap){
        if (ObjectUtil.isNull(val)) {
            paramMap.put(key, val);
            return;
        }
        if(ClassUtil.isSimpleTypeOrArray(val.getClass())
                || val.getClass().isEnum()
                || val instanceof Collection){
            paramMap.put(key, val);
            return;
        }
        BeanUtil.copyProperties(val, paramMap);
    }

    /**
     * 提取标识的语句块
     * @param sql
     * @return
     */
    private static List<String> parse(final String sql){
        final Matcher matcher = QueryPattern.matcher(sql);
        final List<String> ret = new ArrayList<>();
        while (matcher.find()) {
            ret.add(matcher.group(0));
        }
        return ret;
    }

    public static Pattern NamedPattern = Pattern.compile("(:[a-zA-Z0-9]+)");

    private String replaceNamed(String sql){
        if(StrUtil.isBlank(sql)){
            return sql;
        }
        final Matcher matcher = NamedPattern.matcher(sql);
        while(matcher.find()){
            sql = sql.replace(matcher.group(), "#{"+matcher.group().replace(":","")+"}");
        }
        return sql;
    }

    public static void main(String[] args) throws Exception{
        final Map<String, Object> params = new HashMap<>();
        params.put("a", "a");
        params.put("b", 1);
        String sql = "% a = #a \n and id = #id and c &lt;= #c % haaa % b = #b %";
        final List<String> script = parse(sql);
        System.out.println(script);
    }
}
