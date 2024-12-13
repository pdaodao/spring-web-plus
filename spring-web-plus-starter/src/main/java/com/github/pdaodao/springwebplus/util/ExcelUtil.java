package com.github.pdaodao.springwebplus.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelWriter;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.github.pdaodao.springwebplus.base.util.RequestUtil;
import com.github.pdaodao.springwebplus.tool.db.core.TableInfo;
import com.github.pdaodao.springwebplus.tool.util.BeanUtils;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.UriUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ExcelUtil {


    /**
     * 写实体数据到 excel文件中
     *
     * @param fileName   文件名称
     * @param entityList 实体列表
     * @param head       首行
     * @param fields     字段内容获取
     */
    public static <T> void write(final String fileName, final List<T> entityList, final List<String> head, final Function<T, ?>... fields) {
        Preconditions.checkArgument(CollUtil.size(head) == CollUtil.size(fields), "标题长度和内容长度不同");
        final List<List<?>> datas = toListRow(entityList, fields);
        write(fileName, datas, head, RequestUtil.getResponse());
    }

    public static <T> void write(final String fileName, final List<T> entityList, final TableInfo tableInfo) {
        final List<Map<String, ?>> mapList = BeanUtils.toMapList(entityList);
        tableInfo.toCamelCase();
        writeMapData(fileName, mapList, tableInfo);
    }


    /**
     * 写map数据到excel
     *
     * @param fileName
     * @param mapList
     * @param head
     * @param fields
     */
    public static void writeMapData(final String fileName, final List<Map<String, ?>> mapList, final List<String> head, final String... fields) {
        Preconditions.checkArgument(CollUtil.size(head) == CollUtil.size(fields), "标题长度和内容长度不同");
        final List<List<?>> datas = toListRowWithMap(mapList, fields);
        write(fileName, datas, head, RequestUtil.getResponse());
    }

    public static void writeMapData(final String fileName, final List<Map<String, ?>> mapList, final TableInfo tableInfo) {
        Preconditions.checkNotNull(tableInfo, "下载字段信息为空");
        Preconditions.assertTrue(CollUtil.isEmpty(tableInfo.getColumns()), "无下载字段");
        final List<String> fs = tableInfo.getColumns().stream().map(t -> t.getName()).collect(Collectors.toList());
        final List<List<?>> datas = toListRowWithMap(mapList, fs.toArray(new String[0]));
        final List<String> head = tableInfo.getColumns().stream().map(t -> t.getTitle()).collect(Collectors.toList());
        write(fileName, datas, head, RequestUtil.getResponse());
    }


    /**
     * 数据写到excel文件中发送到前端
     *
     * @param fileName
     * @param datas
     * @param head
     * @param response
     */
    private static void write(String fileName, final List<List<?>> datas, final List<String> head, final HttpServletResponse response) {
        if (StrUtil.isBlank(fileName)) {
            fileName = "下载";
        }
        fileName = fileName + ".xlsx";
        try (final ExcelWriter excelWriter = cn.hutool.poi.excel.ExcelUtil.getBigWriter()) {
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + UriUtils.encode(fileName, "UTF-8"));
            final ServletOutputStream outputStream = response.getOutputStream();
            if (CollUtil.isNotEmpty(head)) {
                excelWriter.writeHeadRow(head);
            }
            for (final List<?> row : datas) {
                excelWriter.writeRow(row);
            }
            excelWriter.flush(outputStream, true);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * sheet列表
     *
     * @param inputStream
     * @return
     */
    public static List<String> getSheetNames(final InputStream inputStream) {
        return cn.hutool.poi.excel.ExcelUtil.getReader(inputStream).getSheetNames();
    }

    /**
     * 读取数据为行
     *
     * @param inputStream
     * @return
     */
    public static List<List<Object>> read(final InputStream inputStream) {
        final List<List<Object>> result = new ArrayList<>();
        cn.hutool.poi.excel.ExcelUtil.readBySax(inputStream, 0, new RowHandler() {
            @Override
            public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
                result.add(rowCells);
            }
        });
        return result;
    }

    /**
     * 读取 excel 数据转换为 bean
     *
     * @param inputStream
     * @param clazz
     * @param propertyName
     * @param <T>
     * @return
     */
    public static <T> List<T> readBean(final InputStream inputStream, final List<String> propertyName, final Class<T> clazz) {
        final List<T> result = new ArrayList<>();
        final ExcelBeanHandler handler = new ExcelBeanHandler(result, propertyName, clazz);
        cn.hutool.poi.excel.ExcelUtil.readBySax(inputStream, 0, handler);
        return result;
    }


    /**
     * 读取为 map 结构
     *
     * @param inputStream
     * @param propertyName
     * @return
     */
    public static List<Map<String, Object>> readMap(final InputStream inputStream, final List<String> propertyName) {
        final List<Map<String, Object>> result = new ArrayList<>();
        final ExcelMapHandler handler = new ExcelMapHandler(result, propertyName);
        cn.hutool.poi.excel.ExcelUtil.readBySax(inputStream, 0, handler);
        return result;
    }

    private static <T> List<List<?>> toListRow(final List<T> entityList, final Function<T, ?>... fields) {
        Preconditions.checkNotNull(fields, "fields is null");
        final List<List<?>> result = new ArrayList<>();
        if (CollUtil.isEmpty(entityList)) {
            return result;
        }
        for (final T entity : entityList) {
            final List<Object> row = new ArrayList<>();
            for (final Function<T, ?> fn : fields) {
                final Object v = fn.apply(entity);
                //  final String vv = DataValueUtil.convertToString(v);
                row.add(v);
            }
            result.add(row);
        }
        return result;
    }

    private static List<List<?>> toListRowWithMap(final List<Map<String, ?>> mapList, final String... fields) {
        Preconditions.checkNotNull(fields, "fields is null");
        final List<List<?>> result = new ArrayList<>();
        if (CollUtil.isEmpty(mapList)) {
            return result;
        }
        for (final Map<String, ?> map : mapList) {
            final List<Object> row = new ArrayList<>();
            for (final String ff : fields) {
                final Object v = map.get(ff);
                // final String vv = DataValueUtil.convertToString(v);
                row.add(v);
            }
            result.add(row);
        }
        return result;
    }

    public static class ExcelBeanHandler<T> implements RowHandler {
        protected final List<T> result;
        protected final Func1<List<Object>, T> convertFunc;

        public ExcelBeanHandler(final List<T> datas, final List<String> fields, Class<T> clazz) {
            this.result = datas;
            this.convertFunc = (rowList) -> BeanUtil.toBean(IterUtil.toMap(fields, rowList), clazz);
        }

        @Override
        public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
            if (rowIndex == 0) {
                return;
            }
            final T entity = convertFunc.callWithRuntimeException(rowCells);
            result.add(entity);
        }
    }

    public static class ExcelMapHandler<T> implements RowHandler {
        protected final List<Map<String, Object>> result;
        protected final Func1<List<Object>, Map<String, Object>> convertFunc;

        public ExcelMapHandler(final List<Map<String, Object>> datas, final List<String> fields) {
            this.result = datas;
            this.convertFunc = (rowList) -> IterUtil.toMap(fields, rowList, true);
        }

        @Override
        public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
            if (rowIndex == 0) {
                return;
            }
            final Map<String, Object> entity = convertFunc.callWithRuntimeException(rowCells);
            result.add(entity);
        }
    }
}
