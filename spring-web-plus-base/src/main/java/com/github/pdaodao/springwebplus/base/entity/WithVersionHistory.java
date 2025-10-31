package com.github.pdaodao.springwebplus.base.entity;

/**
 * 带版本的信息历史表-详情表
 */
public interface WithVersionHistory extends WithVersion{
    String getPublishNote();

    void setPublishNote(String publishNote);

    String getObjId();

    void setObjId(String objId);
}