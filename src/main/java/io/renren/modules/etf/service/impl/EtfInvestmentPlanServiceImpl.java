package io.renren.modules.etf.service.impl;

import io.renren.modules.etf.dao.EtfInvestmentPlanDao;
import io.renren.modules.etf.entity.EtfInvestmentPlanEntity;
import io.renren.modules.etf.service.EtfInvestmentPlanService;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;


@Service("etfInvestmentPlanService")
public class EtfInvestmentPlanServiceImpl extends ServiceImpl<EtfInvestmentPlanDao, EtfInvestmentPlanEntity> implements EtfInvestmentPlanService  {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EtfInvestmentPlanEntity> page = this.page(
                new Query<EtfInvestmentPlanEntity>().getPage(params),
                new QueryWrapper<EtfInvestmentPlanEntity>()
        );

        return new PageUtils(page);
    }

}