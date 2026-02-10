package com.github.pdaodao.springwebplus.tool.sql.util;

import cn.hutool.core.collection.ListUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.sql.util.visitor.FieldAlignVisitor;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.table.TableInfo;


public class ZtSqlVisitorTest {
    public static void main(String[] args) throws Exception{
        final TableInfo tableInfo = new TableInfo();
        tableInfo.setName("t1");
        tableInfo.addColumn(TableField.of("a", DataType.STRING));
        tableInfo.addColumn(TableField.of("ab", DataType.STRING));

        // SELECT EquipName, SUM(CAST(UsedData AS DECIMAL)) AS TotalUsedData, SUM(CAST(Fee AS DECIMAL)) AS TotalFee FROM pse_energyusedstatistics WHERE EquipName = '普通表' AND YEAR(PointInTime) = 2026 GROUP BY EquipName
        final String sql = "select a, sum(a_b) as cc from t1 where a = 1 group by a";
        final FieldAlignVisitor fieldCheckVisitor = new FieldAlignVisitor(ListUtil.of(tableInfo));
        final String ret = VisitorUtil.sqlVisit(sql, "1", fieldCheckVisitor, null);
        System.out.println("hello");
    }
}
