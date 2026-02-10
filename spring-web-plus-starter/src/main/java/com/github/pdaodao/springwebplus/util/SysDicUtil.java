package com.github.pdaodao.springwebplus.util;

import cn.hutool.core.collection.CollUtil;
import com.github.pdaodao.springwebplus.base.util.SpringUtil;
import com.github.pdaodao.springwebplus.dao.SysDicDao;
import com.github.pdaodao.springwebplus.entity.SysDic;
import com.github.pdaodao.springwebplus.entity.SysDicValue;
import com.github.pdaodao.springwebplus.tool.data.LinkedCaseInsensitiveMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SysDicUtil {

    /**
     * 字典项列表
     * @return
     */
    public static List<SysDic> dicList(){
        final SysDicDao dao = SpringUtil.getBean(SysDicDao.class);
        return dao.infoList();
    }

    /**
     * 字典项 map   key 为 id
     * @return
     */
    public static Map<String, SysDic> dicIdMap(){
        final Map<String, SysDic> map = new LinkedHashMap<>();
        final List<SysDic> list = dicList();
        for(final SysDic d: list){
            map.put(d.getId(), d);
        }
        return map;
    }

    /**
     * 字典项 map   key 为 name
     * @return
     */
    public static Map<String, SysDic> dicNameMap(){
        final Map<String, SysDic> map = new LinkedHashMap<>();
        final List<SysDic> list = dicList();
        for(final SysDic d: list){
            map.put(d.getName(), d);
        }
        return map;
    }


    /**
     * 字典下的字典值列表
     * @param dicId
     * @return
     */
    public static List<SysDicValue> valueList(final String dicId){
        final SysDicDao dao = SpringUtil.getBean(SysDicDao.class);
        final List<SysDicValue> list = dao.values(dicId);
        return list;
    }


    /**
     * 字典值 map 结构
     * @param dicId
     * @return
     */
    public static Map<String, SysDicValue> valueMap(final String dicId){
        final Map<String, SysDicValue> map = new LinkedCaseInsensitiveMap<>();
        final List<SysDicValue> list = valueList(dicId);
        if(CollUtil.isNotEmpty(list)){
            for(final SysDicValue v: list){
                map.put(v.getName(), v);
            }
        }
        return map;
    }

    /**
     * 字典值 name -> title map结构
     * @param dicId
     * @return
     */
    public static Map<String, String> valueNameTitleMap(final String dicId){
        final Map<String, String> map = new LinkedCaseInsensitiveMap<>();
        final List<SysDicValue> list = valueList(dicId);
        if(CollUtil.isNotEmpty(list)){
            for(final SysDicValue v: list){
                map.put(v.getName(), v.getTitle());
            }
        }
        return map;
    }

    /**
     * 字典值 title -> name map结构
     * @param dicId
     * @return
     */
    public static Map<String, String> valueTitleNameMap(final String dicId){
        final Map<String, String> map = new LinkedCaseInsensitiveMap<>();
        final List<SysDicValue> list = valueList(dicId);
        if(CollUtil.isNotEmpty(list)){
            for(final SysDicValue v: list){
                map.put(v.getTitle(), v.getName());
            }
        }
        return map;
    }

}
