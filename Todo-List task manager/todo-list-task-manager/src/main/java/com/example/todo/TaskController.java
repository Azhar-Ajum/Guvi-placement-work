package com.example.todo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    
    @GetMapping("/viewAll")
    public String viewAllTasks(Model model) {
        List<Task> tasks = taskService.getAllTasks(); // Fetch all tasks
        if (tasks == null || tasks.isEmpty()) {
            model.addAttribute("errorMessage", "No tasks available to display.");
        }
        model.addAttribute("tasks", tasks);
        return "viewTasks"; // Render the viewTasks.html page
    }
    
   
    @GetMapping
    public String showIndex() {
        return "index"; // Render the updated index.html page
    }


    @GetMapping("/add")
    public String showAddTaskForm(Model model) {
        model.addAttribute("task", new Task());
        return "addTask";
    }

    
    @PostMapping
    public String addTask(@ModelAttribute Task task, Model model) {
        // Check if the ID already exists
        if (taskService.getTaskById(task.getId()).isPresent()) {
            model.addAttribute("errorMessage", "Task with this ID already exists. Please use a unique ID.");
            model.addAttribute("task", task); // Keep the entered values
            return "addTask"; // Return to the form with the error message
        }
        
        // Ensure createdAt is set explicitly (optional)
        if (task.getCreatedAt() == null) {
            task.setCreatedAt(LocalDateTime.now());
        }


        // Save the task if ID is unique
        taskService.saveTask(task);
        return "redirect:/tasks";
    }


    @GetMapping("/edit/{id}")
    public String showEditTaskForm(@PathVariable Long id, Model model) {
        Optional<Task> task = taskService.getTaskById(id);
        if (task.isPresent()) {
            model.addAttribute("task", task.get());
            return "editTask";
        }
        return "redirect:/tasks";
    }

    
    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task updatedTask) {
        Optional<Task> existingTask = taskService.getTaskById(id);
        if (existingTask.isPresent()) {
            Task task = existingTask.get();
            task.setTitle(updatedTask.getTitle());
            task.setDescription(updatedTask.getDescription());
            task.setStatus(updatedTask.getStatus());
            taskService.saveTask(task);
        }
        return "redirect:/tasks";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return "redirect:/tasks";
    }
    

    
    @GetMapping("/search")
    public String searchTaskById(@RequestParam Long id, Model model) {
        Optional<Task> task = taskService.getTaskById(id);
        if (task.isPresent()) {
            System.out.println("Task Found: " + task.get());
            model.addAttribute("task", task.get());
        } else {
            System.out.println("No Task Found with ID: " + id);
            model.addAttribute("errorMessage", "No task found with ID: " + id);
        }
        return "search";
    }
    
    
    

    @GetMapping("/searchPage")
    public String loadSearchPage() {
        return "search"; // Load the search.html page
    }

   


}
