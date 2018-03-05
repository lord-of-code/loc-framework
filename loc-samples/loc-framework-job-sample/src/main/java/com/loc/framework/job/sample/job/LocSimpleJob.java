package com.loc.framework.job.sample.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.loc.framework.autoconfigure.elasticjob.LocElasticJob;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@LocElasticJob(cron = "${jobs.cron}", elasticJobListeners = LocSimpleJobListener.class)
@Data
@Slf4j
public class LocSimpleJob implements SimpleJob {


  @Override
  public void execute(ShardingContext shardingContext) {
    log.info("this is simple job");

  }
}
