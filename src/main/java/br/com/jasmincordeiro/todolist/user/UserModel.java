package br.com.jasmincordeiro.todolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;


import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

//getters and setters 


@Data
@Entity(name = "tb_users")
public class UserModel {
    
    @PostMapping("/")
    public ResponseEntity create(UserController userController ){
        var user = userController.userRepository.findByUsername(getUsername());
        if(user != null){
            System.out.println("Usuário ja existe");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe");
        }
        var passwordHashred = BCrypt.withDefaults().hashToString(12, getPassword().toCharArray());
        setPassword((passwordHashred));
        var userCreated = userController.userRepository.save(this);
        return ResponseEntity.status(HttpStatus.OK).body(userCreated); 
    }

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(unique = true) //atributo unico
    private String name;
    private String username;
    private String password;

    @CreationTimestamp
    private LocalDateTime createdAt;




}
