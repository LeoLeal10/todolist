package br.com.cefsa.todolist.task;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cefsa.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody TaskModel model, HttpServletRequest request) {
        model.setIdUser((UUID)request.getAttribute("idUser"));
        System.out.println("Save task IdUser: " + request.getAttribute("idUser"));

        if (model.getEndAt().isBefore(model.getStartAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("end date must be greater than creation date");
        }

        taskRepository.save(model);
        return ResponseEntity.status(HttpStatus.OK).body(model);
    }

    @GetMapping("/listTasks")
    public List<TaskModel> list(HttpServletRequest request) {
        var list = taskRepository.findByIdUser((UUID)request.getAttribute("idUser"));
        return list;
    }

    @PutMapping("/update/{id}")
    public ResponseEntity update(@RequestBody TaskModel model, @PathVariable UUID id, HttpServletRequest request) {
        var task = taskRepository.findById(id).orElse(null);

        if (task.equals(null))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("task not found");

        var idUser = (UUID)request.getAttribute("idUser");

        if (!task.getIdUser().equals(idUser))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user does not have permission");

        Utils.copyNonNullProperties(model, task);

        var taskUpdated = taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body(taskUpdated);
    }    
}
