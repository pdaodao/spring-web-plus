package com.github.pdaodao.springwebplus.tool.io.lang;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.tool.data.RowKind;
import com.github.pdaodao.springwebplus.tool.data.StreamRow;
import com.github.pdaodao.springwebplus.tool.db.core.TableColumn;
import com.github.pdaodao.springwebplus.tool.util.DataValueUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class CdcBatchData {
    private transient List<StreamRow> inserts = new ArrayList<>();
    private transient List<StreamRow> updates = new ArrayList<>();
    private transient List<StreamRow> deletes = new ArrayList<>();

    public void add(final StreamRow row){
        if(row == null){
            return;
        }
        if(row.getKind() == null || row.getKind() == RowKind.INSERT){
            inserts.add(row);
            return;
        }
        if(RowKind.UPDATE_AFTER == row.getKind() || RowKind.UPDATE_AFTER == row.getKind()){
            updates.add(row);
            return;
        }
        if(RowKind.DELETE == row.getKind()){
            deletes.add(row);
        }
    }


//     for (final TableColumn t : fields) {
//        Object value = row.getFieldAs(t.getFrom());
//        value = DataValueUtil.toAs(value, t.getDataType());
//        ps.setObject(i++, value);
//    }
//        ps.addBatch();
}
