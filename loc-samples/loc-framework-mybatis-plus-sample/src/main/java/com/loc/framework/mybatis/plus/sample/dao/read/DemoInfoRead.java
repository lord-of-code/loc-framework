package com.loc.framework.mybatis.plus.sample.dao.read;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.loc.framework.mybatis.plus.sample.domain.DemoInfo;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DemoInfoRead extends BaseMapper<DemoInfo> {

  IPage<DemoInfo> getAllDemoInfo(Page page);

  IPage<DemoInfo> getInfoByScore(Page page, @Param("score") Integer score);

  IPage<DemoInfo> getInfoByBeanPage(Page page, @Param(value="info") DemoInfo demoInfo);
}
