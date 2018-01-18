package com.loc.framework.mybatis.xml.sample.dao.read;

import com.loc.framework.mybatis.xml.sample.domain.DemoInfo;
import java.util.List;

public interface DemoInfoRead {

  DemoInfo getInfoByName(String name);

  List<DemoInfo> getAllDemoInfo();
}
