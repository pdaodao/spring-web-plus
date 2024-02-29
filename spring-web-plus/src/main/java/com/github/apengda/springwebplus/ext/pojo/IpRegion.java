package com.github.apengda.springwebplus.ext.pojo;

import lombok.Data;

@Data
public class IpRegion {

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区域描述
     */
    private String areaDesc;

    /**
     * 运营商
     */
    private String isp;

}
