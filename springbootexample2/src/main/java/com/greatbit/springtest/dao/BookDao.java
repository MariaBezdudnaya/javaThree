package com.greatbit.springtest.dao;

import com.greatbit.springtest.model.Book;
import java.util.List;

public interface BookDao {
  List<Book> findAll();
  Book save(Book book);
  Book getById(Integer bookId);
  Book update(Book book);
  void delete(Book book);
  void deleteAll();
}
