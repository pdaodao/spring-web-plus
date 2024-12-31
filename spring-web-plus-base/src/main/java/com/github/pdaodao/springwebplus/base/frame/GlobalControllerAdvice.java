package com.github.pdaodao.springwebplus.base.frame;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * 使用 @InitBinder 统一处理所有控制器中的字符串参数，将空字符串 ("") 转换为 null
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // 将空字符串自动转换为 null
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

}
