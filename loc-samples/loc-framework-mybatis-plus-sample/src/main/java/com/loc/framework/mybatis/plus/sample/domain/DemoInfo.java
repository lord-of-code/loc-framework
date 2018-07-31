package com.loc.framework.mybatis.plus.sample.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("demo_table")
public class DemoInfo {

  private long id;
  private String name;
  private int age;
  private int score;
}
