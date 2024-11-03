package com.greatbit.springtest;

import com.greatbit.springtest.model.Task;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@SpringBootApplication
public class Application {
  @Bean
  public JdbcDataSource h2DtaSource() {
    JdbcDataSource JdbcDataSource = new JdbcDataSource();
    JdbcDataSource.setURL("jdbc:h2:./db");
    JdbcDataSource.setUser("user");
    JdbcDataSource.setPassword("password");
    return JdbcDataSource;
  }

  @Bean
  public CommandLineRunner cmd(DataSource dataSource) {
    return args -> {
      try(InputStream inputStream = this.getClass().getResourceAsStream("/initial.sql")) {
        String sql = new String(inputStream.readAllBytes());
        try(
                Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
        ) {
          statement.executeUpdate(sql);

          String insertSql = "INSERT INTO task (title, finished, created_date) VALUES (?, ?, ?)";
          try(PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            preparedStatement.setString(1, "title");
            preparedStatement.setBoolean(2, true);
            preparedStatement.setTimeStamp(3, "created_date");
            preparedStatement.executeUpdate();
          }

          System.out.println ("Printing tasks from db....");
          ResultSet rs = statement.executeQuery("SELECT task_id, title, finished, created_date FROM task");
          while(rs.next()) {
            Task task = new Task(rs.getInt(1), rs.getString(2), rs.getBoolean(3), rs.getTimeStamp(4));
            System.out.println(task);
          }
        }
      }
    };
  }

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
