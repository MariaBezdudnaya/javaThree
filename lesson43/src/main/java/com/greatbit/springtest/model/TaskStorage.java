package com.greatbit.springtest.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class TaskStorage {
  private static Set<Task> task = new HashSet<>();

  static {
    task.add(new Task("To sleep", true, LocalDateTime.now()));
    task.add(new Task("Go for a walk", false, LocalDateTime.now()));
  }

  public static Set<Task> getTasks() {
    return task;
  }
}