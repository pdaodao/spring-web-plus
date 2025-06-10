package com.github.pdaodao.springwebplus.base.util;

import cn.hutool.http.HttpUtil;
import com.github.pdaodao.springwebplus.tool.util.DateTimeUtil;
import com.github.pdaodao.springwebplus.tool.util.JsonUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 节假日工具类
 */
@Slf4j
public class HolidayUtil {

    @Data
    public static class HolidayVo{
        private String data;//日期

        private String status;//状态：0工作日/1周末/2法定节假日/3节假日调休补班

        private String msg;//描述
    }

    /**
     * 调用免费API查询全年工作日、周末、法定节假日、节假日调休补班数据
     * 1、调用 https://api.apihubs.cn/holiday/get?size=500&year=2021 查询全年日历（含周末）
     * 2、调用 https://timor.tech/api/holiday/year/2021 查询全年节假日、调休
     */
    public static List<HolidayVo> getAllHolidayByYear(String year) throws Exception{
        //查询全年日历包含周末
        final String allDayJson = HttpUtil.get("https://api.apihubs.cn/holiday/get?size=500&year="+year);
        final Map allDayMap = JsonUtil.strAsMap(allDayJson);
        final Map allDayData = (Map)allDayMap.get("data");
        List allDayDataList = (List)allDayData.get("list");
        //初始化大小
        final List<HolidayVo> holidayVoList = new ArrayList<>(allDayDataList.size());
        final  Map<String, HolidayVo> hashMap = new HashMap<>(allDayDataList.size());
        allDayDataList.forEach((value) -> {
            HolidayVo holidayVo = new HolidayVo();

            Map value1 = (Map) value;
            String year_ = value1.get("year").toString();
            String month = value1.get("month").toString().replace(year_,"");
            String day = value1.get("date").toString().replace(year_+month,"");

            holidayVo.setData(year_ + "-" + month + "-" + day);
            String status = "0";
            String msg = "工作日";
            if("1".equals(value1.get("weekend").toString())){
                status = "1";
                msg = "周末";
            }
            holidayVo.setStatus(status);
            holidayVo.setMsg(msg);

            hashMap.put(holidayVo.getData(),holidayVo);
        });

        //查询全年节假日、调休
        final String holidayJson = HttpUtil.get("https://timor.tech/api/holiday/year/"+year + "/");
        final Map holidayMap = JsonUtil.strAsMap(holidayJson);
        final LinkedHashMap holidayList = (LinkedHashMap)holidayMap.get("holiday");
        holidayList.forEach((key,value) -> {
            HolidayVo holidayVo = new HolidayVo();

            Map value1 = (Map) value;
            String dateTime = value1.get("date").toString();

            holidayVo.setData(dateTime);
            String status = "2";
            String msg = "法定节假日("+value1.get("name").toString()+")";
            if(value.toString().contains("调休")){
                status = "3";
                msg = "节假日调休补班("+value1.get("target").toString()+")";
            }
            holidayVo.setStatus(status);
            holidayVo.setMsg(msg);

            hashMap.replace(holidayVo.getData(),holidayVo);
        });

        for (String key : hashMap.keySet()) {
            holidayVoList.add(hashMap.get(key));
        }

        //排序
        holidayVoList.sort((a,b)->{
            try {
                return DateTimeUtil.tryParse(a.getData()).compareTo(DateTimeUtil.tryParse(b.getData()));
            } catch (Exception e) {
                //输出到日志文件中
                log.error(e.getMessage(), e);
            }
            return 1;
        });

        return holidayVoList;
    }

    public static void main(String[] args) throws Exception{
        List<HolidayVo> HolidayVoList = HolidayUtil.getAllHolidayByYear("2025");
        System.err.println("全年完整数据：");
        for (HolidayVo HolidayVo : HolidayVoList) {
            System.err.println(HolidayVo);
        }
    }
}