package com.github.pdaodao.springwebplus.tool.io.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.data.TableRow;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.io.Reader;
import com.github.pdaodao.springwebplus.tool.util.DataValueTypeUtil;

import java.util.ArrayList;
import java.util.List;

public class ExcelReader implements Reader {
    protected final transient List<List<Object>> rows;
    private final InputStreamWrap inputStreamWrap;
    protected transient long total = 0;
    protected transient int sheetId = 0;
    protected transient List<TableField> fields;
    protected transient int currentIndex = 0;

    public ExcelReader(InputStreamWrap inputStreamWrap) {
        this.inputStreamWrap = inputStreamWrap;
        this.rows = new ArrayList<>();
    }

    public void setSheetId(int sheetId) {
        this.sheetId = sheetId;
    }

    @Override
    public List<TableField> fields() {
        return fields;
    }

    @Override
    public TableRow read() throws Exception {
        if (currentIndex > total || fields == null) {
            return null;
        }
        final List<Object> current = rows.get(currentIndex++);
        final TableRow row = TableRow.of(current.size());
        int index = 0;
        for (final Object v : current) {
            row.setField(fields.get(index++).getName(), v);
        }
        return row;
    }

    @Override
    public void open() throws Exception {
        ExcelUtil.readBySax(inputStreamWrap.inputStream, sheetId,  new ExcelRowCollectHandler(rows));
        if (CollUtil.isNotEmpty(rows)) {
            total = rows.size() - 1;
            this.fields = new ArrayList<>();
            final List<Object> head = rows.get(currentIndex++);
            if (head != null) {
                int index = 1;
                for (final Object v : head) {
                    final TableField f = new TableField();
                    f.setSeq(index);
                    f.setFrom("c" + (index++));
                    f.setName(f.getFrom());
                    f.setTitle(ObjectUtil.toString(v));
                    this.fields.add(f);
                }
            }
            if (total > 0) {
                final List<TableField> types = DataValueTypeUtil.guessFieldType(rows.subList(1, rows.size()));
                int index = 0;
                for (final TableField f : fields) {
                    final TableField dataType = types.get(index++);
                    if(dataType.getDataType() != null && dataType.getDataType().isIntFamily()
                            && ( StrUtil.contains(f.getTitle(), "码") || StrUtil.contains(f.getTitle(), "级") || StrUtil.contains(f.getTitle(), "标识"))){
                        dataType.setDataType(DataType.STRING);
                        dataType.setTypeName("varchar");
                        if(dataType.getLength() < 2){
                            dataType.setLength(64);
                        }
                    }
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
