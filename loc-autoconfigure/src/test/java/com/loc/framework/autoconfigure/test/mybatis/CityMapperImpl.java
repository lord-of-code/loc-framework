package com.loc.framework.autoconfigure.test.mybatis;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created on 2018/1/14.
 */
public class CityMapperImpl {

  @Autowired
  private SqlSessionTemplate sqlSessionTemplate;

  public City findById(long id) {
    return this.sqlSessionTemplate.selectOne("selectCityById", id);
  }


}
