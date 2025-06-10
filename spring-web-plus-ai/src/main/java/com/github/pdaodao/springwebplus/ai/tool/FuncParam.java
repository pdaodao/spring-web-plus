package com.github.pdaodao.springwebplus.ai.tool;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
public class FuncParam {
    private String type = "object";
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, FuncParam> properties;

    private List<String> required = new ArrayList<>();

    public static FuncParam of(){
        return new FuncParam();
    }

    public static FuncParam of(final String description){
        final FuncParam p = new FuncParam();
        p.setDescription(description);
        return p;
    }

    public static FuncParam of(final String type, final String description){
        final FuncParam p = new FuncParam();
        p.setType(type);
        p.setDescription(description);
        return p;
    }

    public FuncParam addInputParam(final String name, final String description, final boolean isRequired){
        if(properties == null){
            properties = new LinkedHashMap<>();
        }
        properties.put(name, FuncParam.of(description));
        if(true == isRequired){
            required.add(name);
        }
        return this;
    }

    public FuncParam addInputParam(final String name, final FuncParam param, final boolean isRequired){
        if(param == null){
            return this;
        }
        if(properties == null){
            properties = new LinkedHashMap<>();
        }
        properties.put(name, param);
        if(true == isRequired){
            required.add(name);
        }
        return this;
    }
}
