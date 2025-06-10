package com.github.pdaodao.springwebplus.ai.annotation;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pdaodao.springwebplus.ai.tool.FuncParam;
import com.github.pdaodao.springwebplus.ai.tool.Tool;
import com.github.pdaodao.springwebplus.tool.util.Preconditions;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

public class ChatToolFuncUtil {
    public static List<ToolWrap> funcs;

    public static List<ToolWrap> of(final String namespace, final String basePkg){
        final List<ToolWrap> list = of(basePkg);
        if(StrUtil.isBlank(namespace)){
            return list;
        }
        return list.stream().filter(t -> StrUtil.equals(namespace, t.getNamespace())).collect(Collectors.toList());
    }

    public static List<ToolWrap> of(final String basePkg){
        if(funcs != null){
            return funcs;
        }
        synchronized (ChatToolFuncUtil.class){
            if(funcs != null){
                return funcs;
            }
            final Set<Class<?>> apps = ClassScanner.scanPackage(basePkg);
            final List<ToolWrap> ret = new ArrayList<>();
            for(final Class<?> clazz: apps){
                try{
                    final List<ToolWrap> funcs = ofClass(clazz);
                    if(funcs != null){
                        ret.addAll(funcs);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            funcs = ret;
        }
        return funcs;
    }


    /**
     * 扫描类得到其中的函数
     * @param classWithTools
     * @return
     */
    public static List<ToolWrap> ofClass(final Class<?> classWithTools) {
        if(classWithTools == null){
            return null;
        }
        final List<ToolWrap> ret = new ArrayList<>();
        for(final Method method : classWithTools.getDeclaredMethods()){
            if(!method.isAnnotationPresent(ChatToolFunc.class)){
                continue;
            }
            final ToolWrap toolWrap = new ToolWrap(classWithTools.getName(), method);
            final ChatToolFunc annotation = method.getAnnotation(ChatToolFunc.class);
            toolWrap.setNamespace(annotation.namespace());
            final String funcName = StrUtil.isBlank(annotation.name()) ? method.getName() : annotation.name();
            final String description = String.join("\n", annotation.value());
            toolWrap.setFuncName(funcName);
            final Tool tool = Tool.ofFunction(funcName, description);
            toolWrap.setTool(tool);
            for(final Parameter pm: method.getParameters()){
                final ChatToolFuncParam pa = pm.getAnnotation(ChatToolFuncParam.class);
                // 参数描述
                final FuncParam pp = toFuncParam(pm.getType(), pm.getParameterizedType());
                boolean required = true;
                if(pa != null){
                    required = pa.required();
                    if(StrUtil.isBlank(pp.getDescription())){
                        pp.setDescription(pa.value());
                    }
                }
                tool.addInputParam(pm.getName(), pp, required);
            }
            ret.add(toolWrap);
        }
        return ret;
    }

    private static FuncParam toFuncParam(final Class typeClass, final Type parameterizedType){
        if(typeClass == String.class){
            return FuncParam.of("string", null);
        }
        if(typeClass == Integer.class || typeClass == Long.class){
            return FuncParam.of("integer", null);
        }
        if(typeClass == Float.class || typeClass == Double.class){
            return FuncParam.of("number", null);
        }
        if(typeClass == Boolean.class){
            return FuncParam.of("boolean", null);
        }
        if(typeClass.isArray()){
            final Class subClass = typeClass.getComponentType();
            final FuncParam ret = FuncParam.of("array", null);
            // todo
            final FuncParam sub = toFuncParam(subClass, null);
            return ret;
        }
        if (Collection.class.isAssignableFrom(typeClass)) {
            final Class subClass = getActualType(parameterizedType);
            final FuncParam ret = FuncParam.of("array", null);
            // todo
            final FuncParam sub = toFuncParam(subClass, null);
            return ret;
        }
        if(BeanUtil.isBean(typeClass)){
            final FuncParam ret = FuncParam.of("object", null);
            for(final Map.Entry<String, Field> fieldEntry: ReflectUtil.getFieldMap(typeClass).entrySet()){
                final String name = fieldEntry.getKey();
                final Field ff = fieldEntry.getValue();
                // todo 考虑死循环问题
                final FuncParam sub = toFuncParam(ff.getType(), null);
                final ChatToolFuncParam subA = ff.getAnnotation(ChatToolFuncParam.class);
                boolean subRequired = false;
                if(subA != null){
                    sub.setDescription(subA.value());
                    subRequired = subA.required();
                }
                if(StrUtil.isBlank(sub.getDescription())){
                    final Schema schema = ff.getAnnotation(Schema.class);
                    if(schema != null){
                        sub.setDescription(schema.description());
                    }
                }
                ret.addInputParam(name, sub, subRequired);
            }
            return ret;
        }
        Preconditions.assertTrue(true, "unsupport tool func param type:"+typeClass.getName());
        return null;
    }

    private static Class<?> getActualType(Type type) {
        if (type instanceof final ParameterizedType parameterizedType) {
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            if (actualTypeArguments.length == 1) {
                return (Class<?>) actualTypeArguments[0];
            }
        }
        return null;
    }

}
