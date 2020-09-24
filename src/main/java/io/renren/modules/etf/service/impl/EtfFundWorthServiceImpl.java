package io.renren.modules.etf.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.renren.modules.etf.dao.EtfFundWorthDao;
import io.renren.modules.etf.entity.EtfFundWorthEntity;
import io.renren.modules.etf.service.EtfFundWorthService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;


@Service("etfFundWorthService")
public class EtfFundWorthServiceImpl extends ServiceImpl<EtfFundWorthDao, EtfFundWorthEntity> implements EtfFundWorthService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EtfFundWorthEntity> page = this.page(new Query<EtfFundWorthEntity>().getPage(params), new QueryWrapper<EtfFundWorthEntity>());

        return new PageUtils(page);
    }

    @Override
    public boolean isExit(String fundNo) {
        LambdaQueryWrapper<EtfFundWorthEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EtfFundWorthEntity::getFundNo, fundNo);
        return getOne(queryWrapper) != null;

    }
    @Override
    public List<EtfFundWorthEntity> getListByFundNo(String fundNo) {
        LambdaQueryWrapper<EtfFundWorthEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EtfFundWorthEntity::getFundNo, fundNo);
        return list(queryWrapper);

    }

}