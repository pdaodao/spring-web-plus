package com.github.pdaodao.springwebplus.tool.io.csv;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.*;
import com.github.pdaodao.springwebplus.tool.data.converter.StreamRowStringGetter;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.io.Writer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import java.util.List;
import java.util.stream.Collectors;

public class CsvWriter implements Writer {
    private final String filePath;
    private final CsvFormat csvFormat;
    private final List<TableColumn> fields;

    private transient CSVPrinter printer;
    private transient long total = 0;

    private StreamRowStringGetter[] valueGetters;

    public CsvWriter(String filePath, CsvFormat csvFormat, List<TableColumn> fields) {
        this.filePath = filePath;
        this.csvFormat = csvFormat;
        this.fields = fields;
    }

    @Override
    public List<TableColumn> fields() {
        return fields;
    }

    @Override
    public void open() throws Exception {
        final CSVFormat format = csvFormat.toFormat();
        printer = format.print(FileUtil.newFile(filePath), CharsetUtil.CHARSET_UTF_8);
        valueGetters = StrUtil.isBlank(csvFormat.getOpName()) ? new StreamRowStringGetter[fields.size()] : new StreamRowStringGetter[fields.size() + 1];
        int i = 0;
        for(final TableColumn f: fields){
            valueGetters[i++] = new StreamRowStringGetter(StrUtil.isBlank(f.getFrom()) ? f.getName(): f.getFrom(), f.getDataType());
        }
        if(StrUtil.isNotBlank(csvFormat.getOpName())){
            valueGetters[i] = new KindStringGetter();
        }
        if(BooleanUtil.isTrue(csvFormat.isSkipHeaderRecord())){
            final List<String> fs = fields.stream().map(t -> t.getName()).collect(Collectors.toList());
            if(StrUtil.isNotBlank(csvFormat.getOpName())){
                fs.add(csvFormat.getOpName());
            }
            printer.printRecord(fs);
        }
    }

    @Override
    public Long total() {
        return total;
    }

    @Override
    public void write(StreamRow row) throws Exception{
        if(row == null || row.getData() == null || row.isEnd()){
            return;
        }
        final String[] list = new String[valueGetters.length];
        int i = 0;
        for(final StreamRowStringGetter g: valueGetters){
            list[i++] = g.get(row);
        }
        printer.printRecord(list);
    }

    @Override
    public void flush() throws Exception {
        printer.flush();
    }

    @Override
    public void close() throws Exception {
        printer.close();
    }

    public static class KindStringGetter extends StreamRowStringGetter{
        public KindStringGetter() {
            super(null, null);
        }

        @Override
        public String get(StreamRow row) {
            return RowKind.DELETE == row.getKind() ? "1" : "0";
        }
    }
}
