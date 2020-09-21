package io.renren.modules.etf.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.renren.common.utils.PageUtils;
import io.renren.modules.etf.FundModel;
import io.renren.modules.etf.entity.EtfInvestmentPlanEntity;

import java.util.Map;

/**
 * 
 *
 *
 * @date 2020-09-18 17:06:22
 */
public interface EtfInvestmentPlanService extends IService<EtfInvestmentPlanEntity> {

    PageUtils queryPage(Map<String, Object> params);


    FundModel getFundInfo(String fundNo);
}

