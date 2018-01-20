package com.loc.framework.mybatis.annotation.sample.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DemoInfo {

  private long id;
  private String name;
  private int age;
  private int score;
}
