package io.renren.modules.etf.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import io.renren.modules.etf.StockModel;
import io.renren.modules.etf.entity.EtfFundWorthEntity;
import io.renren.modules.etf.entity.EtfGridEntity;
import io.renren.modules.etf.entity.EtfInvestmentPlanEntity;
import io.renren.modules.etf.service.EtfGridService;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: etf-grid
 * @description:
 * @author: 许金泉
 * @create: 2020-09-28 10:36
 **/
@Service
public class AliPayService {

    @Autowired
    private EtfGridServiceImpl etfGridService;

    @Autowired
    private EtfInvestmentPlanServiceImpl planService;

    @Autowired
    private EtfFundWorthServiceImpl worthService;

    private List<String> zYue = Arrays.asList("一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月");
    private List<String> eYue = Arrays.asList("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");

    public String importAliFund(InputStream fis) throws Exception {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");//注意月份是MM
        SimpleDateFormat aliSimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

        Map<String, List<EtfFundWorthEntity>> worthMap = new HashMap<>();

        HashSet<String> hashSet = new HashSet<>();

        //解析excel
        POIFSFileSystem pSystem = new POIFSFileSystem(fis);
        //获取整个excel
        HSSFWorkbook hb = new HSSFWorkbook(pSystem);
        //获取第一个表单sheet
        HSSFSheet sheet = hb.getSheetAt(0);
        //获取第一行
        int firstrow = sheet.getFirstRowNum();
        //获取最后一行
        int lastrow = sheet.getLastRowNum();
        //循环行数依次获取列数
        for (int i = firstrow; i < lastrow + 1; i++) {
            //获取哪一行i
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            //获取这一行的第一列
            int firstcell = row.getFirstCellNum();

            //获取这一行的最后一列
            int lastcell = row.getLastCellNum();
            //创建一个集合，用处将每一行的每一列数据都存入集合中
            List<String> list = new ArrayList<>();
            for (int j = firstcell; j < lastcell; j++) {
                //获取第j列
                Cell cell = row.getCell(j);

                if (cell != null) {
                    list.add(cell.toString());
                }
            }

            if (list.size() <= 0) {
                continue;
            }
            String orderNo = list.get(0).replace("\t", "");
            if (!NumberUtil.isNumber(orderNo)) {
                continue;
            }
            String tradeString = list.get(8);
            String fundName = tradeString;
            if (!tradeString.contains("蚂蚁财富") || !tradeString.contains("买入")) {
                continue;
            }
            fundName = fundName.replace("蚂蚁财富-", "");
            fundName = fundName.replace("-买入", "");

            if (hashSet.contains(fundName)) {
                continue;
            }

            EtfInvestmentPlanEntity planEntity = planService.queryByFundName(fundName.trim());
            if (planEntity == null) {
                System.out.println("未查询到基金计划：" + fundName);
                hashSet.add(fundName);
                continue;
            }

            if (!worthMap.containsKey(planEntity.getFundNo())) {
                worthMap.put(planEntity.getFundNo(), worthService.importWorth(planEntity.getFundNo()));
            }

            List<EtfFundWorthEntity> etfFundWorthEntities = worthMap.get(planEntity.getFundNo());

            EtfGridEntity stockModel = new EtfGridEntity();
            stockModel.setOutId(orderNo);
            stockModel.setPlanId(planEntity.getId());
            stockModel.setFundNo(planEntity.getFundNo());
            stockModel.setName(planEntity.getFundName());
            String s = list.get(2);
            for (int j = zYue.size() - 1; j > -1; j--) {
                String s1 = zYue.get(j);
                if (s.contains(s1)) {
                    s = s.replace(s1, eYue.get(j));
                }
            }
            Date date = aliSimpleDateFormat.parse(s);
            stockModel.setBuyTime(date);
            Optional<EtfFundWorthEntity> first = etfFundWorthEntities.stream().filter(u -> u.getFundDate().equals(date)).findFirst();
            if (!first.isPresent()) {
                System.out.println("未查询到买入当天净值：" + fundName);
                continue;
            }
            stockModel.setBuyPrice(first.get().getWorth());
            stockModel.setBuyAmount(new BigDecimal(list.get(9)));
            etfGridService.buy(stockModel);
        }
        fis.close();
        return "成功";
    }

}
