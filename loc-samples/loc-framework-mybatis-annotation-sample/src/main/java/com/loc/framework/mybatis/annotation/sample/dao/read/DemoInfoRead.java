package com.loc.framework.mybatis.annotation.sample.dao.read;

import com.loc.framework.mybatis.annotation.sample.domain.DemoInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DemoInfoRead {

  @Select("select `id`, `name`, `age`, `score` from demo_table where name = #{name}")
  DemoInfo getInfoByName(@Param("name") String name);

  @Select("select `id`, `name`, `age`, `score` from demo_table")
  List<DemoInfo> getAllDemoInfo();
}
