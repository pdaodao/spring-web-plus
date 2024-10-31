package com.github.pdaodao.springwebplus.tool.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tuple2<T0, T1> implements Serializable {
    /**
     * Field 0 of the tuple.
     */
    public T0 f0;
    /**
     * Field 1 of the tuple.
     */
    public T1 f1;
}