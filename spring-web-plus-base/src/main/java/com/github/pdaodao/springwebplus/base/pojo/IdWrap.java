package com.github.pdaodao.springwebplus.base.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IdWrap<T>{
    @NotNull(message = "id不能为空")
    private T id;
}
