package com.loc.framework.autoconfigure.okhttp;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2018/9/5.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OkHttpClientBuilder {

  private String url;
  private Map<String, String> headers;
  private Map<String, String> params;
  private TypeReference typeReference;
  private Object tag;
  private String body;

}
