// src/main/java/com/edu/dto/LoginForm.java
package com.edu.dto;

public class LoginForm {
    private String userId;
    private String password;

    // getter/setter 꼭 만들어 주세요!
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
