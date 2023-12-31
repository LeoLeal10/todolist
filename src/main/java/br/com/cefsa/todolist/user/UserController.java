package br.com.cefsa.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/create")
    public ResponseEntity create(@RequestBody UserModel model) {

        if (userRepository.findByUserName(model.getUserName()) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
        }
        
        var passwordHash = BCrypt.withDefaults().hashToString(12, model.getPassword().toCharArray());

        model.setPassword(passwordHash);
        System.out.println("Save user");
        return ResponseEntity.status(HttpStatus.CREATED).body(userRepository.save(model));
    }    
}
