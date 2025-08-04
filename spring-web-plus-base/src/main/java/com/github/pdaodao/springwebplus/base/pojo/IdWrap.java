package com.github.pdaodao.springwebplus.base.pojo;

import lombok.Data;

@Data
public class IdWrap<T>{
    private T id;
}
