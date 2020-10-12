package io.renren.modules.etf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.etf.entity.EtfFundWorthEntity;

import java.util.List;
import java.util.Map;

/**
 * 
 *
 * @date 2020-09-23 15:10:56
 */
public interface EtfFundWorthService extends IService<EtfFundWorthEntity> {

    PageUtils queryPage(Map<String, Object> params);

    boolean isExit(String fundNo);

    List<EtfFundWorthEntity> importWorth(String fundNo);

    List<EtfFundWorthEntity> getListByFundNo(String fundNo);
}

