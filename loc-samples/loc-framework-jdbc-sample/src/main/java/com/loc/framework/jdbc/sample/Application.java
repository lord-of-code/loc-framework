package com.loc.framework.jdbc.sample;

import com.google.common.collect.Lists;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zalando.problem.Problem;

/**
 * Created on 2017/12/29.
 */
@SpringBootApplication(exclude = {
    DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class
})
public class Application {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Slf4j
  @RestController
  public static class GetController {

    @Resource(name = "firstJdbcTemplate")
    private JdbcTemplate firstJdbcTemplate;

    @GetMapping(value = "/person")
    public List<Person> getPerson(@RequestParam("name") String name) throws SQLException {
      List<Person> list = Lists.newArrayList();
      list.add(firstJdbcTemplate(name));
      return list;
    }

    @PostMapping(value = "/addPerson")
    public Problem addPerson(@RequestParam("name") String name, @RequestParam("age") int age)
        throws SQLException {
      String sql = "INSERT INTO person_table(name,age,address) VALUES (?, ?, ?)";
      firstJdbcTemplate.update(sql, name, age, UUID.randomUUID().toString());
      return Problem.builder().build();
    }

    private Person firstJdbcTemplate(String name) throws SQLException {
      String sql = "select * from person_table where name=? limit 1;";
      Person person = firstJdbcTemplate
          .queryForObject(sql, new BeanPropertyRowMapper<>(Person.class), name);
      log.info("person is {}", person);
      return person;
    }
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Person {

    private String name;
    private int age;
    private String address;
  }
}
