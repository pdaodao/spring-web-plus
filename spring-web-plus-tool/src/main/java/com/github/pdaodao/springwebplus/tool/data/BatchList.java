package com.github.pdaodao.springwebplus.tool.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchList<T>{
    protected List<T> upserts;
    protected List<T> deletes;
}
