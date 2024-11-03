package entity;

import java.util.HashSet;
import java.util.Set;

public class TaskStorage {

  private static Set<Task> task = new HashSet<>();

  static {
    task.add(new Task("To sleep", true, "2024.10.31"));
    task.add(new Task("Go for a walk", false, 2024.01.02));
  }

  public static Set<Task> getTasks() {
    return task;
  }
}