package com.github.pdaodao.springwebplus.tool.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tuple3<T0, T1,T2> implements Serializable {
    /**
     * Field 0 of the tuple.
     */
    public T0 f0;
    /**
     * Field 1 of the tuple.
     */
    public T1 f1;

    public T2 f2;

    public static <T0, T1, T2> Tuple3<T0, T1,T2> of(T0 t0, T1 t1, T2 t2){
        final Tuple3<T0, T1, T2> t = new Tuple3<T0, T1, T2>();
        t.setF0(t0);
        t.setF1(t1);
        t.setF2(t2);
        return t;
    }
}