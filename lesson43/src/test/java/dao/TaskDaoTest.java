package dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import static org.assertj.core.api.Assertions.assertThat;

import com.greatbit.springtest.dao.TaskDao;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.postgresql.ds.PGSimpleDataSource;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskDaoTest {
  private TaskDao taskDao;

  @BeforeAll
  public void setUp()  {
    PGSimpleDataSource dataSource = new PGSimpleDataSource();
    dataSource.setDatabaseName("productStar");
    dataSource.setUser("user");
    dataSource.setPassword("password");

    taskDao = new TaskDao(dataSource);
    initializeDb(dataSource);
  }

  @BeforeEach
  public void beforeEach() {
    taskDao.deleteAll();
  }

  private void initializeDb(DataSource dataSource) {
    try(InputStream inputStream = this.getClass().getResource("/initial.sql").openStream()) { // считать содержимое файла
      String sql = new String(inputStream.readAllBytes());
      try( // выполнить команду в базе
           Connection connection = dataSource.getConnection(); // получаем соединение
           Statement statement = connection.createStatement() // создаём из него statement
      ) {
        statement.executeUpdate(sql); // вызываем в statement executeUpdate(sql), который изменяет данные
      }

    } catch (IOException | SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testSaveSetsId() {
    Task task = new Task("test task", false, LocalDateTime.now());
    taskDao.save(task);

    assertThat(task.getId()).isNotNull();
  }

  @Test
  public void testFindAllReturnsAllTasks() {
    Task firstTask = new Task("first task", false, LocalDateTime.now());
    taskDao.save(firstTask);

    Task secondTask = new Task("second task", false, LocalDateTime.now());
    taskDao.save(secondTask);

    assertThat(taskDao.findAll())
      .hasSize(2)
      .extracting("id")
      .contains(firstTask.getId(), secondTask.getId());
  }

  @Test
  public void testDeleteAllDeletesAllRowsInTasks() {
    Task firstTask = new Task("any task", false, LocalDateTime.now());
    taskDao.save(firstTask);

    int rowsDeleted = taskDao.deleteAll();
    assertThat(rowsDeleted).isEqualTo(1);

    assertThat(taskDao.findAll()).isEmpty();
  }

  @Test
  public void testGetByIdReturnsCorrectTask() {
    Task task = new Task("test task", false, LocalDateTime.of(2023, 10, 1, 12, 0));
    taskDao.save(task);

    assertThat(taskDao.getById(task.getId()))
            .isNotNull()
            .extracting("id", "title", "finished", "createdDate")
            .containsExactly(task.getId(), task.getTitle(), task.getFinished(), task.getCreatedDate());
  }

  @Test
  public void testFindNotFinishedReturnsCorrectTasks() {
    Task unfinishedTask = new Task("unfinished task", false, LocalDateTime.of(2023, 10, 1, 12, 0));
    taskDao.save(unfinishedTask);

    Task finishedTask = new Task("finished task", true, LocalDateTime.of(2023, 10, 1, 12, 0));
    taskDao.save(finishedTask);

    assertThat(taskDao.findAllNotFinished())
            .hasSize(1) // Убедитесь, что возвращается только одна незавершенная задача
            .singleElement()
            .extracting("id", "title", "finished", "createdDate")
            .containsExactly(unfinishedTask.getId(), unfinishedTask.getTitle(), unfinishedTask.getFinished(), unfinishedTask.getCreatedDate());
  }

  @Test
  public void testFindNewestTasksReturnsCorrectTasks() {
    Task firstTask = new Task("first task", false, LocalDateTime.now());
    taskDao.save(firstTask);

    Task secondTask = new Task("second task", false, LocalDateTime.now());
    taskDao.save(secondTask);

    Task thirdTask = new Task("third task", false, LocalDateTime.now());
    taskDao.save(thirdTask);

    assertThat(taskDao.findNewestTasks(2))
      .hasSize(2)
      .extracting("id")
      .containsExactlyInAnyOrder(secondTask.getId(), thirdTask.getId());
  }

  @Test
  public void testFinishSetsCorrectFlagInDb() {
    Task task = new Task("test task", false, LocalDateTime.now());
    taskDao.save(task);

    assertThat(taskDao.finishTask(task).getFinished()).isTrue();
    assertThat(taskDao.getById(task.getId()).getFinished()).isTrue();
  }

  @Test
  public void deleteByIdDeletesOnlyNecessaryData() {
    Task taskToDelete = new Task("first task", false, LocalDateTime.now());
    taskDao.save(taskToDelete);

    Task taskToPreserve = new Task("second task", false, LocalDateTime.now());
    taskDao.save(taskToPreserve);

    taskDao.deleteById(taskToDelete.getId());
    assertThat(taskDao.getById(taskToDelete.getId())).isNull();
    assertThat(taskDao.getById(taskToPreserve.getId())).isNotNull();
  }
}
