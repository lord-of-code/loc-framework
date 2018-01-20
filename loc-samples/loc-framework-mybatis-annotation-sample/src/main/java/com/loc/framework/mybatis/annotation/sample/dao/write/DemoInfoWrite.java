package com.loc.framework.mybatis.annotation.sample.dao.write;


import com.loc.framework.mybatis.annotation.sample.domain.DemoInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DemoInfoWrite {

  @Insert("INSERT INTO demo_table(name, age, score) VALUES (#{name}, #{age}, #{score})")
  int addDemoInfo(DemoInfo demoInfo);

}
