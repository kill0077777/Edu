package com.edu.security;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class EncodeTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "1234";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println(encodedPassword);

       // String hash = "$2a$10$uM6HzSC2rK2tsFQgnFdM7uq3p9RFS3A6cdqZ4WkZqByfd6mcHRm2i";
      // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
       // System.out.println("matches? " + encoder.matches("1234", hash));

    }
}

