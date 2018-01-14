package com.loc.framework.autoconfigure.test.mybatis;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created on 2018/1/14.
 */
@Mapper
public interface CityMapper {

  @Select("select * from city where id = #{id}")
  City selectCityById(@Param("id") long id);
}
