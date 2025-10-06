package com.example.ejerciciofinal.dtos;

import com.example.ejerciciofinal.dtos.CreateUserDTO.PersonDTO;
import com.example.ejerciciofinal.model.Role;

public class ResponseUserDTO {

    private String userName;
    private Role role;
    private PersonDTO person;

    public ResponseUserDTO(){}

    public ResponseUserDTO(String userName, Role role, PersonDTO person){
        this.userName = userName;
        this.role = role;
        this.person = person;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public Role getRole() {
        return role;
    }
    public PersonDTO getPersonDTO() {
        return person;
    }
}


