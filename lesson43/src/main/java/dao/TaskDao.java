package dao;

import entity.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

public class TaskDao {
  private final DataSource dataSource;

  public TaskDao(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public Task save(Task task) {
    String sql = "INSERT INTO task (title, finished, created_date) VALUES (?, ?, ?)";
    try (
          Connection connection = dataSource.getConnection(); // устанавливаем соединение
          PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
    ) { // создаём statement
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
    return task;
  }

  public List<Task> findAll() {
    List<Task> tasks = new ArrayList<>();
    String sql = "SELECT task_id, title, finished, created_date FROM task ORDER BY task_id";
    try (Connection connection = dataSource.getConnection(); // устанавливаем соединение
         Statement statement = connection.createStatement(); // создаём statement
         ResultSet resultSet = statement.executeQuery(sql) // присваиваем результату найденные значения
    ) {
      while (resultSet.next()) { // пока ещё есть значения
        final Task task = new Task( // создаём новое задание с параметрами
                resultSet.getString(2),
                resultSet.getBoolean(3),
                resultSet.getTimestamp(4).toLocalDateTime()
        );
        task.setId(resultSet.getInt(1)); // присваиваем id
        tasks.add(task); // добавляем его
      }
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
    return tasks;
  }

  public int deleteAll() {
    String sql = "DELETE FROM tasks";
    try (Connection connection = dataSource.getConnection(); // устанавливаем соединение
          Statement statement = connection.createStatement()) { // создаём statement
      return statement.executeUpdate(sql);
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
  }

  public Task getById(Integer id) {
    String sql = "SELECT * FROM tasks WHERE id = ?";
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
        task.setId(resultSet.getInt("id"));
        return task;
      }
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
    return null;
  }

  public List<Task> findAllNotFinished() {
    List<Task> tasks = new ArrayList<>();
    String sql = "SELECT * FROM tasks WHERE finished = false";
    try (Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        Task task = new Task(
                resultSet.getString("title"),
                resultSet.getBoolean("finished"),
                resultSet.getTimestamp("created_date").toLocalDateTime()
        );
        task.setId(resultSet.getInt("id"));
        tasks.add(task);
      }
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
    return tasks;
  }

  public List<Task> findNewestTasks(int limit) {
    List<Task> tasks = new ArrayList<>();
    String sql = "SELECT * FROM tasks ORDER BY created_date DESC LIMIT ?";
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
        task.setId(resultSet.getInt("id"));
        tasks.add(task);
      }
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
    return tasks;
  }

  public Task finishTask(Task task) {
    String sql = "UPDATE tasks SET finished = true WHERE id = ?";
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
    String sql = "DELETE FROM tasks WHERE id = ?";
    try (Connection connection = dataSource.getConnection();
          PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.setInt(1, id);
          statement.executeUpdate();
    } catch (SQLException throwables) {
      throw new RuntimeException(throwables);
    }
  }
}
