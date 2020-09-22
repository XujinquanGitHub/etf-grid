package io.renren.modules.etf.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import io.renren.modules.etf.FundModel;
import io.renren.modules.etf.danjuan.fund.DanJuanFundInfo;
import io.renren.modules.etf.danjuan.fund.Data;
import io.renren.modules.etf.danjuan.fund.FundDerived;
import io.renren.modules.etf.dao.EtfInvestmentPlanDao;
import io.renren.modules.etf.entity.EtfInvestmentPlanEntity;
import io.renren.modules.etf.service.EtfInvestmentPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.renren.common.utils.PageUtils;
import io.renren.common.utils.Query;


@Service("etfInvestmentPlanService")
public class EtfInvestmentPlanServiceImpl extends ServiceImpl<EtfInvestmentPlanDao, EtfInvestmentPlanEntity> implements EtfInvestmentPlanService {

    @Autowired
    private DanJuanService danJuanService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<EtfInvestmentPlanEntity> page = this.page(new Query<EtfInvestmentPlanEntity>().getPage(params), new QueryWrapper<EtfInvestmentPlanEntity>());

        return new PageUtils(page);
    }

    @Override
    public FundModel getFundInfo(String fundNo) {
        String url = "http://fundgz.1234567.com.cn/js/" + fundNo + ".js?rt=634543645643";
        Map<String, String> head = new HashMap<>();
        head.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        head.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
        String response = HttpRequest.get(url).addHeaders(head).execute().body();
        response = response.replace("jsonpgz({", "{");
        response = response.replace("});", "}");
        System.out.println(response);
        try {
            return JSON.parseObject(response, FundModel.class);
        } catch (Exception ex) {
            System.out.println(ex.toString());
            try {
                DanJuanFundInfo fundInfo = danJuanService.getFundInfo(fundNo, "");
                Data data = fundInfo.getData();
                FundDerived fundDerived = data.getFundDerived();
                return new FundModel().setFundcode(fundNo).setName(data.getFdName()).setGsz(fundDerived.getUnit_nav());
            } catch (Exception exs) {
                System.out.println(exs.toString());
                return new FundModel().setFundcode(fundNo).setGsz(new BigDecimal(1));
            }
        }
    }

    @Override
    public List<EtfInvestmentPlanEntity> queryList(Map<String, Object> params) {
        return listByMap(params);
    }
}