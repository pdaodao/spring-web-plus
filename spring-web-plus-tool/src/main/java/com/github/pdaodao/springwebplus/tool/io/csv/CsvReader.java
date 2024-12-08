package com.github.pdaodao.springwebplus.tool.io.csv;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import com.github.pdaodao.springwebplus.tool.data.DataType;
import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.fs.InputStreamWrap;
import com.github.pdaodao.springwebplus.tool.io.Reader;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CsvReader implements Reader {
    private final InputStreamWrap wrap;
    // 首行是否为字段名称
    private boolean firstHead = true;
    private transient List<TableColumn> fields;
    private transient Map<Integer, String> nameMap;

    private transient java.io.Reader reader;
    private transient CSVParser csvParser;
    private transient Iterator<CSVRecord> iterator;


    private long total = 0;

    public CsvReader(InputStreamWrap wrap) {
        this.wrap = wrap;
    }

    @Override
    public void open() throws Exception {
        reader = new InputStreamReader(new BufferedInputStream(wrap.inputStream), StandardCharsets.UTF_8);
        csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setSkipHeaderRecord(firstHead).build());
        csvParser.iterator();
        if (firstHead) {
            int size = 0;
            for (final String name : csvParser.getHeaderNames()) {
                final TableColumn ff = new TableColumn();
                ff.setName(name.trim());
                ff.setDataType(DataType.STRING);
                fields.add(ff);
                nameMap.put(size++, ff.getName());
            }
        }
        if (MapUtil.isEmpty(nameMap)) {
            for (int i = 0; i < 500; i++) {
                nameMap.put(i, "c" + (i + 1));
            }
        }
    }

    @Override
    public Long total() {
        return total;
    }

    @Override
    public List<TableColumn> fields() {
        return fields;
    }

    public List<StreamRow> topN(final int topN) throws Exception {
        final List<StreamRow> list = new ArrayList<>();
        if (topN < 1) {
            return list;
        }
        for (int i = 0; i < Integer.MAX_VALUE; i++) {
            final StreamRow row = read();
            if (row == null) {
                return list;
            }
            list.add(row);
            if (total >= topN) {
                return list;
            }
        }
        return list;
    }

    @Override
    public StreamRow read() throws Exception {
        if (!iterator.hasNext()) {
            return null;
        }
        total++;
        return toRow(iterator.next());
    }

    private StreamRow toRow(final CSVRecord csvRow) {
        if (csvRow == null) {
            return null;
        }
        final StreamRow row = StreamRow.of(nameMap.size());
        int index = 0;
        for (final String t : csvRow.values()) {
            final String name = nameMap.get(index++);
            Preconditions.checkNotBlank(name, "第" + total + "行数据,字段个数不一致.");
            row.setField(name, t);
        }
        return row;
    }

    @Override
    public void close() throws Exception {
        IoUtil.close(csvParser);
        IoUtil.close(reader);
        IoUtil.close(wrap);
    }
}
