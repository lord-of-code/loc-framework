package com.loc.framework.mybatis.plus.sample.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.loc.framework.mybatis.plus.sample.dao.read.DemoInfoRead;
import com.loc.framework.mybatis.plus.sample.dao.write.DemoInfoWrite;
import com.loc.framework.mybatis.plus.sample.domain.DemoInfo;
import java.sql.SQLException;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 2018/1/18.
 */
@Slf4j
@RestController
public class MybatisPlusController {

  @Autowired
  private DemoInfoRead demoInfoRead;

  @Autowired
  private DemoInfoWrite demoInfoWrite;

  @RequestMapping(value = "/mybatisPlusOp1", method = RequestMethod.GET)
  public String mybatisOp1() throws SQLException {
    String name = UUID.randomUUID().toString();
    log.info("demo info is {}, uuid is {}",
        demoInfoRead.selectList(new QueryWrapper<DemoInfo>().eq("name", name)), name);
    demoInfoWrite.insert(DemoInfo.builder().name(name).age(1200).score(10).build());
    return "ok";
  }

  @RequestMapping(value = "/mybatisPlusPage", method = RequestMethod.GET)
  public String mybatisPage() throws SQLException {
    IPage<DemoInfo> demoInfoList = demoInfoRead
        .selectPage(new Page<>(1, 10), null);
    log.info("size is {}, demo info list are {}", demoInfoList.getSize(), demoInfoList.getRecords().stream().map(DemoInfo::toString)
        .collect(Collectors.joining(",")));
    return "OK";
  }


  @RequestMapping(value = "/mybatisPlusPageNO", method = RequestMethod.GET)
  public IPage<DemoInfo> mybatisPageNO(int pageNo, int pageSize) throws SQLException {
    IPage<DemoInfo> demoInfoList = demoInfoRead
        .selectPage(new Page<>(pageNo, pageSize), null);
    log.info("size is {}, demo info list are {}", demoInfoList.getSize(), demoInfoList.getRecords().stream().map(DemoInfo::toString)
        .collect(Collectors.joining(",")));
    return demoInfoList;
  }
}
