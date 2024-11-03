package com.greatbit.springtest.model;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BookStorage {

    private static Set<Book> books = new HashSet<>();

    static {
        books.add(new Book("B1", "A1"));
        books.add(new Book("B2", "A2"));
    }

    public static Set<Book> getBooks() {
        return books;
    }

  @SpringBootApplication
  public static class Application {
      @Bean
      public DataSource h2DtaSource() {
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

                      String insertSql = "INSERT INTO book (pages, name, author) VALUES (?, ?, ?)";
                      try(PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
                          preparedStatement.setInt(1, 123);
                          preparedStatement.setString(2, "java book");
                          preparedStatement.setString(3, "product star");
                          preparedStatement.executeUpdate();
                      }

                      System.out.println ("Printing books from db....");
                      ResultSet rs = statement.executeQuery("SELECT book_id, pages, name, author FROM book");
                      while(rs.next()) {
                          Book book = new Book(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4));
                          System.out.println(book);
                      }
                  }
              }
          };
      }

      public static void main(String[] args) {
          SpringApplication.run(Application.class, args);
      }
  }

  @Controller
  public static class BooksController {

      @GetMapping("/")
      public String books(Model model) {
          model.addAttribute("books", getBooks());
          return "books-list";
      }

      @GetMapping("/create-form")
      public String createForm() {
          return "create-form";
      }

      @PostMapping("/create")
      public String create(Book book) {
          book.setId(UUID.randomUUID().toString());
          getBooks().add(book);
          return "redirect:/";
      }

      @GetMapping("/edit-form/{id}")
      public String createForm(@PathVariable("id") String id, Model model) {
          Book bookToEdit = getBooks().stream().
                  filter(book -> book.getId().equals(id)).
                  findFirst().
                  orElseThrow(RuntimeException::new);
          model.addAttribute("book", bookToEdit);
          return "edit-form";
      }


      @PostMapping("/update")
      public String update(Book book) {
          delete(book.getId());
          getBooks().add(book);
          return "redirect:/";
      }


      @GetMapping("/delete/{id}")
      public String delete(@PathVariable("id") String id) {
          Book bookToDelete = getBooks().stream().
                  filter(book -> book.getId().equals(id)).
                  findFirst().
                  orElseThrow(RuntimeException::new);
          getBooks().remove(bookToDelete);
          return "redirect:/";
      }

  }
}
