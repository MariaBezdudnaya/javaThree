package com.greatbit.springtest.dao;

import com.greatbit.springtest.model.Book;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BookDaoImpl implements BookDao {
  private final DataSource dataSource;

  @Autowired
  public BookDaoImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public List<Book> findAll() {
    final String selectSql = "SELECT book_id, pages, name, author FROM book";
    List<Book> books = new ArrayList<>();

    try(
      Connection connection = dataSource.getConnection();
      Statement statement = connection.createStatement();
      ResultSet rs = statement.executeQuery(selectSql)
      ) {
        while (rs.next()) {
          Book book = new Book(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getString(4));
          books.add(book);
        }
      } catch (SQLException throwables) {
        throw new RuntimeException(throwables);
      }

    return books;
  }

  @Override
  public Book save(Book book) {
    return null;
  }

  @Override
  public Book getById(Integer bookId) {
    return null;
  }

  @Override
  public Book update(Book book) {
    return null;
  }

  @Override
  public void delete(Book book) {
  }

  @Override
  public void deleteAll() {
    String deleteSql = "TRUNCATE TABLE book";
    try(Connection connection = dataSource.getConnection();
    Statement statement = connection.createStatement()) {
      statement.executeUpdate(deleteSql);
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
  }
}
