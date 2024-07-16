package org.apache.ibatis.scripting.xmltags;

import org.apache.ibatis.builder.SqlSourceBuilder;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.session.Configuration;

/**
 * 拷贝自mybatis 修改一行 处理过滤条件 当不存在时自动消失
 */
public class DynamicSqlSource implements SqlSource {
    private final Configuration configuration;
    private final SqlNode rootSqlNode;

    public DynamicSqlSource(Configuration configuration, SqlNode rootSqlNode) {
        this.configuration = configuration;
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public BoundSql getBoundSql(Object parameterObject) {
        final DynamicContext context = new DynamicContext(configuration, parameterObject);
        rootSqlNode.apply(context);
        final SqlSourceBuilder sqlSourceParser = new SqlSourceBuilder(configuration);
        final Class<?> parameterType = parameterObject == null ? Object.class : parameterObject.getClass();
        // pdaodao 加入这一行
        final String sql = MybatisHelper.processSql(context.getSql(), context);

        SqlSource sqlSource = sqlSourceParser.parse(sql, parameterType, context.getBindings());
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);
        context.getBindings().forEach(boundSql::setAdditionalParameter);
        return boundSql;
    }
}
