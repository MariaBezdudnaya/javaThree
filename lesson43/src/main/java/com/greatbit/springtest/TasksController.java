package com.greatbit.springtest;

import com.greatbit.springtest.model.Task;
import com.greatbit.springtest.model.TaskStorage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.UUID;

@Controller
public class TasksController {

  @GetMapping("/")
  public String tasks(Model model) {
    model.addAttribute("tasks", TaskStorage.getTasks());
    return "tasks-list";
  }

  @GetMapping("/create-form")
  public String createForm() {
    return "create-form";
  }

  @PostMapping("/create")
  public String create(Task task) {
    task.setId();
    TaskStorage.getTasks().add(task);
    return "redirect:/";
  }

  @GetMapping("/edit-form/{id}")
  public String createForm(@PathVariable("id") String id, Model model) {
    Task taskToEdit = TaskStorage.getTasks().stream().
            filter(task -> task.getId().equals(id)).
            findFirst().
            orElseThrow(RuntimeException::new);
    model.addAttribute("task", taskToEdit);
    return "edit-form";
  }


  @PostMapping("/update")
  public String update(Task task) {
    delete(task.getId());
    TaskStorage.getTasks().add(task);
    return "redirect:/";
  }


  @GetMapping("/delete/{id}")
  public String delete(@PathVariable("id") String id) {
    Task taskToDelete = TaskStorage.getTasks().stream().
            filter(task -> task.getId().equals(id)).
            findFirst().
            orElseThrow(RuntimeException::new);
    TaskStorage.getTasks().remove(taskToDelete);
    return "redirect:/";
  }

}
