package io.renren.modules.etf.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import io.renren.modules.etf.danjuan.worth.DanJuanWorthInfo;
import io.renren.modules.etf.danjuan.worth.Item;
import io.renren.modules.etf.dao.EtfFundWorthDao;
import io.renren.modules.etf.entity.EtfFundWorthEntity;
import io.renren.modules.etf.service.EtfFundWorthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;
import org.springframework.util.CollectionUtils;


@Service("etfFundWorthService")
public class EtfFundWorthServiceImpl extends ServiceImpl<EtfFundWorthDao, EtfFundWorthEntity> implements EtfFundWorthService {

    @Autowired
    private DanJuanService danJuanService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EtfFundWorthEntity> page = this.page(new Query<EtfFundWorthEntity>().getPage(params), new QueryWrapper<EtfFundWorthEntity>());

        return new PageUtils(page);
    }

    @Override
    public boolean isExit(String fundNo) {
        LambdaQueryWrapper<EtfFundWorthEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EtfFundWorthEntity::getFundNo, fundNo);
        return count(queryWrapper) > 0;

    }

    public EtfFundWorthEntity queryLastWorth(String fundNo) {
        Map<String, Object> params = new HashMap<>();
        params.put("fund_no", fundNo);
        IPage<EtfFundWorthEntity> etfFundWorthEntityIPage = new Query<EtfFundWorthEntity>().getPage(params).setPages(1).setSize(1);
        etfFundWorthEntityIPage.orders().add(new OrderItem().setAsc(false).setColumn("fund_date"));
        IPage<EtfFundWorthEntity> page = this.page(etfFundWorthEntityIPage, new QueryWrapper<EtfFundWorthEntity>());
        return CollectionUtils.isEmpty(page.getRecords()) ? null : page.getRecords().get(0);
    }

    @Override
    public List<EtfFundWorthEntity> getWorthByDanJuan(String fundNo, String cookies, long size){
        DanJuanWorthInfo fundWorth = danJuanService.getFundWorth(fundNo, null, size);
        List<Item> items = fundWorth.getData().getItems();
        return items.stream().map(u -> new EtfFundWorthEntity().setFundDate(u.getDate()).setFundNo(fundNo).setPercentage(u.getPercentage()).setWorth(u.getValue())).collect(Collectors.toList());
    }

    @Override
    public List<EtfFundWorthEntity> importWorth(String fundNo) {
        EtfFundWorthEntity etfFundWorthEntity = queryLastWorth(fundNo);

        long daySum = 10000;
        if (etfFundWorthEntity != null) {
                daySum = DateUtil.between(etfFundWorthEntity.getFundDate(), new Date(), DateUnit.DAY);
        }
        DanJuanWorthInfo fundWorth = danJuanService.getFundWorth(fundNo, null, daySum);
        List<Item> items = fundWorth.getData().getItems();
        if (!CollectionUtils.isEmpty(items)) {
            List<EtfFundWorthEntity> collect = items.stream().map(u -> new EtfFundWorthEntity().setFundDate(u.getDate()).setFundNo(fundNo).setPercentage(u.getPercentage()).setWorth(u.getValue())).collect(Collectors.toList());
            saveBatch(collect);
        }

        LambdaQueryWrapper<EtfFundWorthEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EtfFundWorthEntity::getFundNo, fundNo);
        return list(queryWrapper);
    }


    @Override
    public List<EtfFundWorthEntity> getListByFundNo(String fundNo) {
        LambdaQueryWrapper<EtfFundWorthEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EtfFundWorthEntity::getFundNo, fundNo);
        return list(queryWrapper);

    }

}