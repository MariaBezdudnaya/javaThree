package dao;

import entity.Task;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class TaskDao {
  private final DataSource dataSource;

  public TaskDao(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Task save(Task task) {
    String sql = "INSERT INTO task (title, finished, created_date) VALUES (?, ?, ?) RETURNING task_id";
    try (
            Connection connection = dataSource.getConnection(); // устанавливаем соединение
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
    ) {
      statement.setString(1, task.getTitle()); // устанавливаем параметры
      statement.setBoolean(2, task.getFinished());
      statement.setTimestamp(3, java.sql.Timestamp.valueOf(task.getCreatedDate()));

      statement.executeUpdate(); // сохраняем данные

      try (ResultSet resultSet = statement.getGeneratedKeys()) { // получаем ключи
        if (resultSet.next()) {
          task.setId(resultSet.getInt(1)); // присваиваем ключи
        }
      }
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
    return task; // Возвращаем объект с установленным id
  }

  public List<Task> findAll() {
    List<Task> tasks = new ArrayList<>();
    String sql = "SELECT task_id, title, finished, created_date FROM task ORDER BY task_id";
    try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        Task task = new Task(
                resultSet.getString("title"),
                resultSet.getBoolean("finished"),
                resultSet.getTimestamp("created_date").toLocalDateTime()
        );
        task.setId(resultSet.getInt("task_id")); // Используем long для идентификатора
        tasks.add(task);
      }
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
    return tasks;
  }

  public int deleteAll() {
    String sql = "DELETE FROM task";
    try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      return statement.executeUpdate();
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
  }

  public Task getById(Integer id) {
    String sql = "SELECT * FROM task WHERE task_id = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, id);
      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        Task task = new Task(
                resultSet.getString("title"),
                resultSet.getBoolean("finished"),
                resultSet.getTimestamp("created_date").toLocalDateTime()
        );
        task.setId(resultSet.getInt("task_id"));
        return task;
      }
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
    return null;
  }

  public List<Task> findAllNotFinished() {
    List<Task> tasks = new ArrayList<>();
    String sql = "SELECT task_id, title, finished, created_date FROM task WHERE finished = false ORDER BY task_id";
    try (
            Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        Task task = new Task(
                resultSet.getString("title"),
                resultSet.getBoolean("finished"),
                resultSet.getTimestamp("created_date").toLocalDateTime()
        );
        task.setId(resultSet.getInt("task_id"));
        tasks.add(task);
      }
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
    return tasks;
  }

  public List<Task> findNewestTasks(int limit) {
    List<Task> tasks = new ArrayList<>();
    String sql = "SELECT * FROM task ORDER BY created_date DESC LIMIT ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, limit);
      ResultSet resultSet = statement.executeQuery();
      while (resultSet.next()) {
        Task task = new Task(
                resultSet.getString("title"),
                resultSet.getBoolean("finished"),
                resultSet.getTimestamp("created_date").toLocalDateTime()
        );
        task.setId(resultSet.getInt("task_id"));
        tasks.add(task);
      }
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
    return tasks;
  }

  public Task finishTask(Task task) {
    String sql = "UPDATE task SET finished = true WHERE task_id = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, task.getId());
      statement.executeUpdate();
      task.setFinished(true);
      return task;
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
  }

  public void deleteById(Integer id) {
    String sql = "DELETE FROM task WHERE task_id = ?";
    try (Connection connection = dataSource.getConnection();
         PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setInt(1, id);
      statement.executeUpdate();
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
  }
}