package com.github.pdaodao.springwebplus.tool.io.csv;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.data.RowKind;
import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.data.converter.StreamRowSetter;
import com.github.pdaodao.springwebplus.tool.data.converter.ValueConverterRowSetter;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.io.Reader;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.util.*;
import java.util.stream.Collectors;

public class CsvReader implements Reader {
    private final InputStreamWrap inputStreamWrap;
    private final CsvFormat csvFormat;
    private transient List<TableColumn> fields;
    private transient CSVParser csvParser;
    private transient Iterator<CSVRecord> it;
    private transient StreamRowSetter[] streamRowSetters;

    public CsvReader(InputStreamWrap inputStreamWrap, CsvFormat csvFormat, List<TableColumn> fields) {
        this.inputStreamWrap = inputStreamWrap;
        this.csvFormat = csvFormat;
        this.fields = fields;
    }

    @Override
    public void open() throws Exception {
        csvParser = CSVParser.builder()
                .setInputStream(inputStreamWrap.inputStream)
                .setFormat(csvFormat.toFormat()).get();
        it = csvParser.iterator();
        if(BooleanUtil.isTrue(csvFormat.isSkipHeaderRecord())){
            final Map<String, TableColumn> fsMap = new LinkedHashMap<>();
            for(final TableColumn f: fields){
                fsMap.put(f.getName(), f);
            }
            List<String> names = csvParser.getHeaderNames();
            if(CollUtil.isEmpty(names) && it.hasNext()){
                names = Arrays.stream(it.next().values()).collect(Collectors.toList());
            }
            final List<TableColumn> fsFiltered = new ArrayList<>();
            for(final String f: names){
                final TableColumn old = fsMap.get(f);
                if(old != null){
                    fsFiltered.add(old);
                    continue;
                }
                if(StrUtil.equals(csvFormat.getOpName(),f)){
                    fsFiltered.add(TableColumn.of(f, DataType.INT));
                    continue;
                }
                Preconditions.assertTrue(true, "unknown field:{}", f);
            }
            fields = fsFiltered;
        }
        streamRowSetters = new StreamRowSetter[fields.size()];
        int i = 0;
        for(final TableColumn f: fields){
            if(StrUtil.equals(csvFormat.getOpName(), f.getName())){
                streamRowSetters[i++] = new KingStreamRowSetter();
                continue;
            }
            streamRowSetters[i++] = new ValueConverterRowSetter<>(f);
        }
    }

    @Override
    public StreamRow read() throws Exception {
        if(it.hasNext()){
            return toRow(it.next());
        }
        return null;
    }

    private StreamRow toRow(final CSVRecord record){
        final StreamRow row = new StreamRow();
        int i = 0;
        for(final String v: record.values()){
            streamRowSetters[i++].set(row, v);
        }
        return row;
    }

    @Override
    public List<TableColumn> fields() {
        return fields;
    }

    @Override
    public Long total() {
        return -1l;
    }

    @Override
    public void close() throws Exception {
        if(csvParser != null){
            csvParser.close();
        }
        inputStreamWrap.close();
    }

    public static class KingStreamRowSetter implements StreamRowSetter{
        @Override
        public void set(StreamRow row, Object object) {
            final Integer kind = DataValueUtil.toInt(object);
            if(ObjectUtil.equals(1, kind)){
                row.setKind(RowKind.DELETE);
            }else{
                row.setKind(RowKind.INSERT);
            }
        }
    }
}
