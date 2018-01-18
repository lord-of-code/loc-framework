package com.loc.framework.mybatis.xml.sample.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.loc.framework.mybatis.xml.sample.dao.read.DemoInfoRead;
import com.loc.framework.mybatis.xml.sample.dao.write.DemoInfoWrite;
import com.loc.framework.mybatis.xml.sample.domain.DemoInfo;
import java.sql.SQLException;
import java.util.List;
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
public class MybatisXmlController {

  @Autowired
  private DemoInfoRead demoInfoRead;

  @Autowired
  private DemoInfoWrite demoInfoWrite;

  @RequestMapping(value = "/mybatisOp1", method = RequestMethod.GET)
  public String mybatisOp1() throws SQLException {
    String name = UUID.randomUUID().toString();
    log.info("demo info is {}, uuid is {}", demoInfoRead.getInfoByName(name), name);
    demoInfoWrite.addDemoInfo(DemoInfo.builder().name(name).age(1200).score(10).build());
    return "ok";
  }

  @RequestMapping(value = "/mybatisPage", method = RequestMethod.GET)
  public String mybatisPage() throws SQLException {
    PageHelper.startPage(1, 10);
    List<DemoInfo> demoInfoList = demoInfoRead.getAllDemoInfo();
    log.info("demo info list are {}", demoInfoList.stream().map(DemoInfo::toString)
        .collect(Collectors.joining(",")));
    PageInfo<DemoInfo> pageValue = new PageInfo<>(demoInfoList);
    log.info("page value is {}", pageValue);
    return "OK";
  }


  @RequestMapping(value = "/mybatisPageNO", method = RequestMethod.GET)
  public PageInfo mybatisPageNO(int pageNo) throws SQLException {
    PageInfo page = new PageInfo();
    PageHelper.startPage(pageNo, page.getPageSize());
    List<DemoInfo> demoInfoList = demoInfoRead.getAllDemoInfo();
    log.info("demo info list are {}", demoInfoList.stream().map(DemoInfo::toString)
        .collect(Collectors.joining(",")));
    PageInfo<DemoInfo> pageValue = new PageInfo<>(demoInfoList);
    log.info("page value is {}", pageValue);
    return pageValue;
  }
}
