//package com.blog;
//
//import org.jasypt.encryption.StringEncryptor;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.test.context.SpringBootTest;
//
//
//@SpringBootTest
//public class TestJayspt {
//
//    @Qualifier("jasyptStringEncryptor")
//    @Autowired
//    private StringEncryptor stringEncryptor;
//
//    @Test
//    public void testJayspt() {
//        String password = "timmy@870728";
//        String username = "root";
//        String email = "examyou076@gmail.com";
//        String encryptPassword = stringEncryptor.encrypt(password);
//        String encryptUsername = stringEncryptor.encrypt(username);
//
//        System.out.println("encryptPassword: " + encryptPassword);
//        System.out.println("encryptUsername: " + encryptUsername);
//        System.out.println("encryptEmail: " + stringEncryptor.encrypt(email));
//    }
//}
