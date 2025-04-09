//package com.github.pdaodao.springwebplus.tool.util;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.util.ArrayUtil;
//import cn.hutool.core.util.StrUtil;
//import com.github.pdaodao.springwebplus.tool.data.TableDataRow;
//import org.apache.commons.csv.CSVFormat;
//import org.apache.commons.csv.CSVParser;
//import org.apache.commons.csv.CSVPrinter;
//import org.apache.commons.csv.CSVRecord;
//
//import java.io.IOException;
//import java.io.StringReader;
//import java.io.StringWriter;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//public class CsvUtil {
//
//    public static List<TableDataRow> parseCsvString(final String csvString, final boolean isFirstHead){
//        if(StrUtil.isBlank(csvString)){
//            return null;
//        }
//        List<String[]> data = new ArrayList<>();
//
//        try (final StringReader stringReader = new StringReader(csvString); final CSVParser csvParser = new CSVParser(stringReader, CSVFormat.DEFAULT())) {
//            // 遍历 CSV 记录
//            for (CSVRecord record : csvParser) {
//                // 将每一行转换为字符串数组
//                String[] row = new String[record.size()];
//                for (int i = 0; i < record.size(); i++) {
//                    row[i] = record.get(i);
//                }
//                data.add(row);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//    public static String toCsv(final List<TableDataRow> rows, final String... fields) throws Exception{
//        if(CollUtil.isEmpty(rows)){
//            return null;
//        }
//        final String[] headers = ArrayUtil.isNotEmpty(fields) ? fields: rows.get(0).keys().toArray(new String[0]);
//        // 使用 StringWriter 写入 CSV 数据到字符串
//        try (final StringWriter stringWriter = new StringWriter(); CSVPrinter csvPrinter = new CSVPrinter(stringWriter, CSVFormat.DEFAULT.withHeader(headers))) {
//            for(final TableDataRow row: rows){
//                csvPrinter.printRecord(row.stringList(fields));
//            }
//            return stringWriter.toString();
//        }
//    }
//
//    public static void main(String[] args) throws Exception{
//
//        cn.hutool.core.text.csv.CsvUtil.getWriter()
//
//        final List<TableDataRow> list = new ArrayList<>();
//        final TableDataRow row1 = new TableDataRow();
//        row1.put("a", "1");
//        row1.put("b", new Date());
//        row1.put("c", "你好,北京\n欢迎您");
//        row1.put("d", true);
//        list.add(row1);
//
//        final TableDataRow row2 = new TableDataRow();
//        row2.put("a", "2");
//        row2.put("b", new Date());
//        row2.put("c", null);
//        row2.put("d", false);
//        list.add(row2);
//        final String msg = toCsv(list, "a", "b", "c", "d");
//        System.out.println(msg);
//    }
//}
