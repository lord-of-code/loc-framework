package com.loc.framework.autoconfigure.test.springmvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.loc.framework.autoconfigure.springmvc.LocAccessLogFilter;
import com.loc.framework.autoconfigure.springmvc.LocSpringMvcLogProperties;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created on 2017/12/1.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class LocAccessLogTest {

  @Autowired
  private MappingJackson2HttpMessageConverter jackson2HttpMessageConverter;

  @Autowired
  private StreamMessageConverter streamMessageConverter;

  private MockMvc requestMockMvc;
  private MockMvc bothMockMvc;

  @Before
  public void setUp() throws Exception {
    LocSpringMvcLogProperties requestProperties = new LocSpringMvcLogProperties();
    requestProperties.setRequestBodyLength(1024);
    requestProperties.setResponseBodyLength(1024);

    this.requestMockMvc = MockMvcBuilders
        .standaloneSetup(new GetController(), new PostController(), new StreamController())
        .setMessageConverters(jackson2HttpMessageConverter, streamMessageConverter)
        .addFilters(new LocAccessLogFilter(requestProperties))
        .build();

    LocSpringMvcLogProperties bothProperties = new LocSpringMvcLogProperties();
    bothProperties.setResponseBodyLength(1024);
    bothProperties.setIncludeResponse(true);
    bothMockMvc = MockMvcBuilders
        .standaloneSetup(new GetController(), new PostController(), new StreamController())
        .setMessageConverters(jackson2HttpMessageConverter, streamMessageConverter)
        .addFilters(new LocAccessLogFilter(bothProperties)).build();
  }

  @Test
  public void getTest1() throws Exception {
    this.requestMockMvc
        .perform(get("/get/test1").header("header-key", "header-value").accept("application/json"))
        .andExpect(status().isOk()).andReturn();

    this.bothMockMvc
        .perform(get("/get/test1").header("header-key", "header-value").accept("application/json"))
        .andExpect(status().isOk()).andReturn();

    this.bothMockMvc
        .perform(
            get("/actuator/info").header("header-key", "header-value").accept("application/json"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  public void postTest1() throws Exception {
    this.requestMockMvc
        .perform(
            post("/post/test1").header("header-key", "header-value").accept("application/json"))
        .andExpect(status().isOk()).andReturn();

    this.bothMockMvc
        .perform(
            post("/post/test1").header("header-key", "header-value").accept("application/json"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  public void getSleep() throws Exception {
    this.requestMockMvc.perform(get("/get/sleep?time=1000").accept("application/json"))
        .andExpect(status().isOk()).andReturn();

    this.bothMockMvc.perform(get("/get/sleep?time=1000").accept("application/json"))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  public void getStream() throws Exception {
    this.requestMockMvc
        .perform(get("/get/octetStream").accept(MediaType.APPLICATION_OCTET_STREAM_VALUE))
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
        .andExpect(status().isOk())
        .andReturn();

    this.bothMockMvc
        .perform(get("/get/octetStream").accept(MediaType.APPLICATION_OCTET_STREAM_VALUE))
        .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM_VALUE))
        .andExpect(status().isOk()).andReturn();
  }

  @Test
  public void postFormData() throws Exception {
    this.requestMockMvc.perform(
        post("/post/form-demo")
            .param("name", "thomas")
            .param("age", "29")
            .param("address", "a1", "a2")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andExpect(jsonPath("$.address").value(Lists.newArrayList("a1", "a2")))
        .andReturn();

    this.bothMockMvc.perform(
        post("/post/form-demo")
            .param("name", "thomas")
            .param("age", "29")
            .param("address", "a1", "a2")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andExpect(jsonPath("$.address").value(Lists.newArrayList("a1", "a2")))
        .andReturn();
  }

  @Test
  public void postJsonData() throws Exception {
    Demo demo = new Demo();
    demo.setName("thomas");
    demo.setAge(29);
    demo.setAddress(Lists.newArrayList("a1", "a2"));

    ObjectMapper objectMapper = new ObjectMapper();
    String content = objectMapper.writeValueAsString(demo);
    this.requestMockMvc.perform(
        post("/post/json-demo")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andExpect(jsonPath("$.address").value(Lists.newArrayList("a1", "a2")))
        .andReturn();

    this.bothMockMvc.perform(
        post("/post/json-demo")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andExpect(jsonPath("$.address").value(Lists.newArrayList("a1", "a2")))
        .andReturn();
  }

  @Test
  public void postMaxJsonData() throws Exception {
    Demo demo = new Demo();
    demo.setName("thomas");
    demo.setAge(29);
    List<String> address = Lists.newArrayList();
    for (int i = 0; i < 200; i++) {
      address.add("a" + i);
    }
    demo.setAddress(address);

    ObjectMapper objectMapper = new ObjectMapper();
    String content = objectMapper.writeValueAsString(demo);
    this.requestMockMvc.perform(
        post("/post/json-demo")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andReturn();

    this.bothMockMvc.perform(
        post("/post/json-demo")
            .content(content)
            .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andReturn();

    this.requestMockMvc.perform(
        post("/post/maxPlayload")
            .param("times", "200")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andReturn();

    this.bothMockMvc.perform(
        post("/post/maxPlayload")
            .param("times", "200")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andReturn();
  }

  @Test
  public void getDemo() throws Exception {
    this.requestMockMvc.perform(
        get("/get/demo")
            .param("name", "thomas")
            .param("age", "29")
            .param("address", "a1", "a2")
            .accept("application/json"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andExpect(jsonPath("$.address").value(Lists.newArrayList("a1", "a2")))
        .andReturn();

    this.bothMockMvc.perform(
        get("/get/demo")
            .param("name", "thomas")
            .param("age", "29")
            .param("address", "a1", "a2")
            .accept("application/json"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andExpect(jsonPath("$.address").value(Lists.newArrayList("a1", "a2")))
        .andReturn();
  }

  @Test
  public void getMaxPlayload() throws Exception {
    this.requestMockMvc.perform(
        get("/get/maxPlayload")
            .param("times", "200")
            .accept("application/json"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andReturn();

    this.bothMockMvc.perform(
        get("/get/maxPlayload")
            .param("times", "200")
            .accept("application/json"))
        .andExpect(status().isOk())
        .andExpect(content().contentType("application/json;charset=UTF-8"))
        .andExpect(jsonPath("$.name").value("thomas"))
        .andExpect(jsonPath("$.age").value("29"))
        .andReturn();
  }

  @Test
  public void testMultiPart() throws Exception {
    MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain",
        "some xml".getBytes());
    MockMultipartFile secondFile = new MockMultipartFile("data", "other-file-name.data",
        "text/plain", "some other type".getBytes());
    MockMultipartFile jsonFile = new MockMultipartFile("json", "", "application/json",
        "{\"json\": \"someValue\"}".getBytes());

    this.requestMockMvc.perform(MockMvcRequestBuilders.fileUpload("/post/multiPart")
        .file(firstFile)
        .file(secondFile).file(jsonFile)
        .param("some-random", "4"))
        .andExpect(status().is(200))
        .andReturn();

    this.bothMockMvc.perform(MockMvcRequestBuilders.fileUpload("/post/multiPart")
        .file(firstFile)
        .file(secondFile).file(jsonFile)
        .param("some-random", "4"))
        .andExpect(status().is(200))
        .andReturn();
  }


  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Demo {

    private String name;
    private int age;
    private List<String> address;
  }

  @MinimalWebConfiguration
  @Controller
  public static class StreamController {

    @GetMapping(value = "/get/octetStream", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStream> responseOctetStream() throws IOException {
      File tmpFile = new File("/tmp/" + UUID.randomUUID().toString() + ".log");
      tmpFile.createNewFile();
      Path path = Paths.get(tmpFile.getAbsolutePath());
      ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
      return ResponseEntity.ok()
          .contentLength(tmpFile.length())
          .contentType(MediaType.parseMediaType("application/octet-stream"))
          .body(resource.getInputStream());
    }

    @RequestMapping(value = "/post/multiPart", method = RequestMethod.POST)
    @ResponseBody
    public String saveAuto(
        @RequestPart(value = "json") JsonPojo pojo,
        @RequestParam(value = "some-random") String random,
        @RequestParam(value = "data", required = false) List<MultipartFile> files) {
      System.out.println(random);
      System.out.println(pojo.getJson());
      for (MultipartFile file : files) {
        System.out.println(file.getOriginalFilename());
      }
      return "success";
    }

    @Data
    static class JsonPojo {

      private String json;
    }
  }

  @MinimalWebConfiguration
  @RestController
  public static class PostController {

    @PostMapping(value = "/post/test1")
    public String responsePlainTest() {
      return "OK";
    }

    @PostMapping(value = "/post/form-demo")
    public Demo responseFormDemo(
        @RequestParam(value = "name")
            String name,
        @RequestParam(value = "age")
            int age,
        @RequestParam(value = "address")
            List<String> address) {
      Demo demo = new Demo();
      demo.setName(name);
      demo.setAge(age);
      demo.setAddress(address);
      return demo;
    }

    @PostMapping(value = "/post/json-demo")
    public Demo responseJsonDemo(
        @RequestBody Demo reqDemo) {
      Demo demo = new Demo();
      demo.setName(reqDemo.getName());
      demo.setAge(reqDemo.getAge());
      demo.setAddress(reqDemo.getAddress());
      return demo;
    }

    @PostMapping(value = "/post/maxPlayload")
    public Demo responseDemo(
        @RequestParam(value = "times")
            long times) {
      Demo demo = new Demo();
      demo.setName("thomas");
      demo.setAge(29);
      List<String> address = Lists.newArrayList();
      for (int i = 0; i < times; i++) {
        address.add("a" + i);
      }
      demo.setAddress(address);
      return demo;
    }

  }

  @MinimalWebConfiguration
  @RestController
  public static class GetController {

    @GetMapping(value = "/get/test1")
    public String responsePlainTest() {
      return "OK";
    }

    @GetMapping(value = "/actuator/info")
    public String info() {
      return "OK";
    }

    @GetMapping(value = "/get/sleep")
    public String responseSleep(
        @RequestParam(value = "time")
            long time) {
      try {
        Thread.sleep(time);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return String.valueOf(time);
    }

    @GetMapping(value = "/get/demo")
    public Demo responseDemo(
        @RequestParam(value = "name")
            String name,
        @RequestParam(value = "age")
            int age,
        @RequestParam(value = "address")
            List<String> address) {
      Demo demo = new Demo();
      demo.setName(name);
      demo.setAge(age);
      demo.setAddress(address);
      return demo;
    }

    @GetMapping(value = "/get/maxPlayload")
    public Demo responseDemo(
        @RequestParam(value = "times")
            long times) {
      Demo demo = new Demo();
      demo.setName("thomas");
      demo.setAge(29);
      List<String> address = Lists.newArrayList();
      for (int i = 0; i < times; i++) {
        address.add("a" + i);
      }
      demo.setAddress(address);
      return demo;
    }
  }


  static class StreamMessageConverter extends AbstractHttpMessageConverter<InputStream> {

    public StreamMessageConverter(MediaType supportedMediaType) {
      super(supportedMediaType);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
      return clazz.isAssignableFrom(ByteArrayInputStream.class);
    }

    @Override
    protected InputStream readInternal(Class<? extends InputStream> clazz,
        HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
      return inputMessage.getBody();
    }

    @Override
    protected void writeInternal(InputStream inputStream, HttpOutputMessage outputMessage)
        throws IOException, HttpMessageNotWritableException {
      IOUtils.copy(inputStream, outputMessage.getBody());
    }
  }

  @Configuration
  public static class WebConfig {

    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
      MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
      Jackson2ObjectMapperBuilder builder = this.jacksonBuilder();
      converter.setObjectMapper(builder.build());
      return converter;
    }

    @Bean
    public StreamMessageConverter streamMessageConverter() {
      return new StreamMessageConverter(MediaType.APPLICATION_OCTET_STREAM);
    }


    public Jackson2ObjectMapperBuilder jacksonBuilder() {
      return new Jackson2ObjectMapperBuilder();
    }
  }

  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @Documented
  @Configuration
  @Import({
      ServletWebServerFactoryAutoConfiguration.class,
      JacksonAutoConfiguration.class
  })
  protected @interface MinimalWebConfiguration {

  }

}
