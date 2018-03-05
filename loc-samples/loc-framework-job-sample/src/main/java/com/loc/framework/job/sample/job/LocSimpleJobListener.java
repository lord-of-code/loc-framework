package com.loc.framework.job.sample.job;


import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LocSimpleJobListener implements ElasticJobListener {

  @Override
  public void beforeJobExecuted(ShardingContexts shardingContexts) {
    log.info("jobName: {}********** simple beforeJobExecuted", shardingContexts.getJobName());
  }

  @Override
  public void afterJobExecuted(ShardingContexts shardingContexts) {
    log.info("jobName: {}********** simple afterJobExecuted", shardingContexts.getJobName());
  }
}
