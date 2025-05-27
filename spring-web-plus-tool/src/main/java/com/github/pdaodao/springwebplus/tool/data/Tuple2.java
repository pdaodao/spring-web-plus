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

    public static <T0, T1> Tuple2<T0, T1> of(T0 t0, T1 t1){
        final Tuple2<T0, T1> t = new Tuple2<T0, T1>();
        t.setF0(t0);
        t.setF1(t1);
        return t;
    }
}