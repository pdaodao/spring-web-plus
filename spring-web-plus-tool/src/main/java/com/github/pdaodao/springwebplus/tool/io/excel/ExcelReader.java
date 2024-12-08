package com.github.pdaodao.springwebplus.tool.io.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.io.Reader;
import com.github.pdaodao.springwebplus.tool.util.DataValueTypeUtil;
import com.github.pdaodao.springwebplus.tool.util.StrUtils;

import java.util.ArrayList;
import java.util.List;

public class ExcelReader implements Reader {
    protected final transient List<List<Object>> rows;
    private final InputStreamWrap inputStreamWrap;
    protected transient long total = 0;
    protected transient List<TableColumn> fields;
    protected transient int currentIndex = 0;

    public ExcelReader(InputStreamWrap inputStreamWrap) {
        this.inputStreamWrap = inputStreamWrap;
        this.rows = new ArrayList<>();
    }

    @Override
    public List<TableColumn> fields() {
        return fields;
    }

    @Override
    public StreamRow read() throws Exception {
        if (currentIndex > total || fields == null) {
            return null;
        }
        final List<Object> current = rows.get(currentIndex++);
        final StreamRow row = StreamRow.of(current.size());
        int index = 0;
        for (final Object v : current) {
            row.setField(fields.get(index++).getName(), v);
        }
        return row;
    }

    @Override
    public void open() throws Exception {
        ExcelUtil.readBySax(inputStreamWrap.inputStream, 0, new RowHandler() {
            @Override
            public void handle(int sheetIndex, long rowIndex, List<Object> rowCells) {
                rows.add(rowCells);
            }
        });
        if (CollUtil.isNotEmpty(rows)) {
            total = rows.size() - 1;
            this.fields = new ArrayList<>();
            final List<Object> head = rows.get(currentIndex++);
            if (head != null) {
                int index = 1;
                for (final Object v : head) {
                    final TableColumn f = new TableColumn();
                    f.setFrom("c" + (index++));
                    f.setName(f.getFrom());
                    f.setTitle(StrUtils.clean(ObjectUtil.toString(v)));
                    this.fields.add(f);
                }
            }
            if (total > 0) {
                final List<TableColumn> types = DataValueTypeUtil.guessFieldType(rows.subList(1, rows.size()));
                int index = 0;
                for (final TableColumn f : fields) {
                    final TableColumn dataType = types.get(index++);
                    f.setDataType(dataType.getDataType());
                    f.setLength(dataType.getLength());
                    if (f.getDataType() != null) {
                        f.setTypeName(f.getDataType().name());
                    }
                }
            }
        }
    }

    @Override
    public Long total() {
        return total;
    }

    @Override
    public void close() throws Exception {
        rows.clear();
        if (inputStreamWrap != null) {
            inputStreamWrap.close();
        }
    }
}
