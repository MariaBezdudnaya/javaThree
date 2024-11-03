package com.greatbit.springtest.dao;

import com.greatbit.springtest.model.Book;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collection;
import java.util.List;

@SpringBootTest(
  properties = {"jdbcUrl=jdbc:h2:mem:db"}
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookDaoImplTest {
  @Autowired
  private BookDao bookDao;

  @Test
  public void contextCreated() { }

  @Test
  void deleteAllDeletesAllData() {
    bookDao.save(new Book(1, "book name", "book author"));
    assertThat(bookDao.findAll()).isNotEmpty();
    bookDao.deleteAll();
    assertThat(bookDao.findAll()).isEmpty();
  }
}