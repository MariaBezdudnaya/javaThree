package com.greatbit.springtest.dao;

import com.greatbit.springtest.model.Book;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
  properties = {"jdbcUrl=jdbc:h2:mem:db; DB_CLOSE_DELAY=-1"}
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