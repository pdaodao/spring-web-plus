package com.github.pdaodao.springwebplus.ai.pojo;

public class AiChatContextHolder {
    public static final ThreadLocal<AiChatContext> holder = new ThreadLocal<>();

    public static void set(final AiChatContext context){
        holder.set(context);
    }

    public static AiChatContext get(){
        return holder.get();
    }

    public static void clear(){
        holder.remove();
    }
}
