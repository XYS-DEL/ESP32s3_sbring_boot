package com.iot.esp32.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iot.esp32.model.entity.IotDevice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 设备数据访问层
 * 继承 BaseMapper 后，自带了几十个现成的 CRUD 数据库操作方法
 */
@Mapper
public interface IotDeviceMapper extends BaseMapper<IotDevice> {
    // 留空
}