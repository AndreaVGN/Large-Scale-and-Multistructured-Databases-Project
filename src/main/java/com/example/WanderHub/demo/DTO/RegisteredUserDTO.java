package com.example.WanderHub.demo.DTO;

public class RegisteredUserDTO {

    private String username;
    private String password;

    // Costruttore per il DTO
    public RegisteredUserDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter e Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
