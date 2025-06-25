package com.github.pdaodao.aicompare.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DocTextBlock {
    private String title;

    private List<String> items;

    public void addItem(final String text){
        if(items == null){
            items = new ArrayList<>();
        }
        items.add(text);
    }

    /**
     * 是否是目录
     * @return
     */
    public boolean isCategory(){
        if(CollUtil.isNotEmpty(items)){
            return false;
        }
        return DocxUtil.isCategory(title);

    }


    public List<String> split(final int maxCharSize){
        if(StrUtil.isBlank(title) || items == null){
            return null;
        }
        final List<String> ret = new ArrayList<>();
        final StringBuilder sb = new StringBuilder();
        if(StrUtil.isNotBlank(title)){
            sb.append(title);
        }
        for(final String st: items){
            if(StrUtil.isBlank(st)){
                continue;
            }
            int allLength = sb.length() + StrUtil.length(st);
            if(allLength > maxCharSize){
                if(!sb.isEmpty()){
                    ret.add(sb.toString());
                    sb.delete(0, sb.length());
                }
            }
            if(!sb.isEmpty()){
                sb.append("\n");
            }
            sb.append(st);
        }
        if(!sb.isEmpty()){
            ret.add(sb.toString());
        }
        return ret;
    }

    public void lastAppend(final String line){
        if(items == null){
            items = new ArrayList<>();
            items.add(line);
            return;
        }
        final String text = items.get(items.size()-1) + line;
        items.set(items.size() -1, text);
    }

    @Override
    public String toString() {
        return title + "\n    "+StrUtil.join("\n     ", items);
    }
}
