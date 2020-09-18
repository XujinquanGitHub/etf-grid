package io.renren.modules.etf.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.date.DateTime;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import io.renren.modules.etf.FundModel;
import io.renren.modules.etf.entity.EtfInvestmentPlanEntity;
import io.renren.modules.etf.service.EtfInvestmentPlanService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.renren.common.utils.PageUtils;
import io.renren.common.utils.R;


/**
 * @date 2020-09-18 17:06:22
 */
@RestController
@RequestMapping("generator/etfinvestmentplan")
public class EtfInvestmentPlanController {
    @Autowired
    private EtfInvestmentPlanService etfInvestmentPlanService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = etfInvestmentPlanService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Integer id) {
        EtfInvestmentPlanEntity etfInvestmentPlan = etfInvestmentPlanService.getById(id);

        return R.ok().put("etfInvestmentPlan", etfInvestmentPlan);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody EtfInvestmentPlanEntity etfInvestmentPlan) {
        String url = "http://fundgz.1234567.com.cn/js/" + etfInvestmentPlan.getFundNo() + ".js?rt=634543645643";
        Map<String, String> head = new HashMap<>();
        head.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        head.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.102 Safari/537.36");
        String response = HttpRequest.get(url).addHeaders(head).execute().body();
        response = response.replace("jsonpgz({", "{");
        response = response.replace("});", "}");
        System.out.println(response);
        FundModel fundModel = JSON.parseObject(response, FundModel.class);
        etfInvestmentPlan.setFundName(fundModel.getName());
        etfInvestmentPlan.setInitPrice(fundModel.getGsz());
        etfInvestmentPlan.setCurrentPrice(fundModel.getGsz());
        etfInvestmentPlan.setCreateDate(DateTime.now());

        etfInvestmentPlanService.save(etfInvestmentPlan);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("generator:etfinvestmentplan:update")
    public R update(@RequestBody EtfInvestmentPlanEntity etfInvestmentPlan) {
        etfInvestmentPlanService.updateById(etfInvestmentPlan);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("generator:etfinvestmentplan:delete")
    public R delete(@RequestBody Integer[] ids) {
        etfInvestmentPlanService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
