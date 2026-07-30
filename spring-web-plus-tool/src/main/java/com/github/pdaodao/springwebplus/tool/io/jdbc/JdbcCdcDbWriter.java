package com.github.pdaodao.springwebplus.tool.io.jdbc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import com.github.pdaodao.springwebplus.tool.data.TableRow;
import com.github.pdaodao.springwebplus.tool.db.DBDdLUtil;
import com.github.pdaodao.springwebplus.tool.db.DbUtil;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbDialect;
import com.github.pdaodao.springwebplus.tool.db.dialect.DbFactory;
import com.github.pdaodao.springwebplus.tool.io.Writer;
import com.github.pdaodao.springwebplus.tool.io.pojo.DoNothingWriter;
import com.github.pdaodao.springwebplus.tool.io.pojo.WriterInfo;
import com.github.pdaodao.springwebplus.tool.table.DbInfo;
import com.github.pdaodao.springwebplus.tool.table.TableField;
import com.github.pdaodao.springwebplus.tool.table.TableInfo;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JdbcCdcDbWriter implements Writer, Runnable{
    private final DbInfo fromDbInfo;
    private final DbInfo dbInfo;
    private final String targetTablePrefix;
    private final Map<String, Writer> writerMap = new ConcurrentHashMap<>();
    private long total = 0;

    private boolean autoCreateTable = false;

    private DbDialect fromDialect = null;
    private DbDialect targetDialect = null;
    private transient ScheduledExecutorService scheduler;

    public JdbcCdcDbWriter(WriterInfo info, String targetTablePrefix, DbInfo fromDb) {
        this.dbInfo = info.getDbInfo();
        this.fromDbInfo = fromDb;
        this.targetTablePrefix = targetTablePrefix;
    }

    @Override
    public void write(TableRow row) throws Exception {
        if(StrUtil.isBlank(row.getTag()) || row.getData() == null){
            return;
        }
        total += 1;
//        System.out.println(JSONUtil.toJsonStr(row));
        final Writer writer = getWriter(row.getTag());
        writer.write(row);
    }

    private Writer getWriter(final String tag) throws Exception{
        Writer writer = writerMap.get(tag);
        if(writer == null){
            synchronized (this){
                writer = writerMap.get(tag);
                if(writer != null){
                    return writer;
                }
                try{
                    writer = createWriter(tag);
                }catch (Exception e){
                    log.error(e.getMessage(), e);
                    throw e;
                }
                writerMap.put(tag, writer);
            }
        }
        return writer;
    }

    private Writer createWriter(final String tag) throws Exception{
        // 获取来源表结构
        if(fromDialect == null){
            fromDialect = DbFactory.of(fromDbInfo.getUrl());
        }
        if(targetDialect == null){
            targetDialect = DbFactory.of(dbInfo.getUrl());
        }
        final TableInfo fromTableInfo = fromDialect.metaLoader(fromDbInfo).tableInfo(tag, fromDbInfo.getDbSchema());
        Preconditions.checkNotNull(fromTableInfo, "fromTableInfo {} is null", tag);
        final String targetTable = StrUtil.isBlank(targetTablePrefix) ? tag : targetTablePrefix + tag;
        TableInfo targetTableInfo = targetDialect.metaLoader(dbInfo).tableInfo(targetTable, dbInfo.getDbSchema());
        if(targetTableInfo == null || CollUtil.isEmpty(targetTableInfo.getFields())){
            if(!autoCreateTable){
                return new DoNothingWriter();
            }
            targetTableInfo = fromTableInfo.clone();
            targetTableInfo.setName(targetTable);
            targetTableInfo.setDbSchema(dbInfo.getDbSchema());
            final String sqls = DBDdLUtil.tableCheck(targetDialect, false, DbUtil.getDatasource(dbInfo), targetTableInfo, null);
            if(StrUtil.isNotEmpty(sqls)){
                log.info(sqls);
            }
        }
        final Map<String, String> fromFieldMap = new LinkedCaseInsensitiveMap<>();
        for(final TableField f: fromTableInfo.getFields()){
            fromFieldMap.put(f.getName(), f.getName());
        }
        final List<TableField> fs = new ArrayList<>();
        for(final TableField f: targetTableInfo.getFields()){
            if(fromFieldMap.containsKey(f.getName())){
                f.setFrom(fromFieldMap.get(f.getName()));
                fs.add(f);
            }
        }
        final WriterInfo tableWriterInfo = new WriterInfo();
        tableWriterInfo.setDbInfo(dbInfo);
        tableWriterInfo.setTableName(targetTable);
        tableWriterInfo.setFields(fs);
        final JdbcTableWriter w = new JdbcTableWriter(tableWriterInfo);
        w.open();
        return w;
    }


    @Override
    public List<TableField> fields() {
        return null;
    }

    @Override
    public void open() throws Exception {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this, 3, 3, TimeUnit.SECONDS);
    }

    @Override
    public Long total() {
        return total;
    }

    @Override
    public void close() throws Exception {
        for(final Writer writer : writerMap.values()){
            writer.close();
        }
        if(scheduler != null){
            scheduler.shutdown();
        }
    }

    @Override
    public void flush() throws Exception {
        final Collection<Writer> values = writerMap.values();
        values.parallelStream().forEach(t ->{
            try{
                t.flush();
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        });
    }

    @Override
    public void run() {
        try{
            flush();
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }
}
