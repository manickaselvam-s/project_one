package com.example.demo.controller;

import com.example.demo.model.FileMetrics;
import com.example.demo.service.AnalyzerService;
import com.example.demo.service.UserService;
import com.example.demo.service.EmailService;
import com.example.demo.service.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private AnalyzerService analyzerService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OTPService otpService;

    // FRONT PAGE
    @GetMapping("/")
    public String frontPage() {
        return "front";
    }

    // LOGIN PAGE
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // REGISTER PAGE
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // SEND OTP FOR REGISTRATION
    @PostMapping("/register/send-otp")
    @ResponseBody
    public String sendOTP(@RequestParam String email, @RequestParam String password) {
        // Validate email format
        if (!userService.isEmailValid(email)) {
            return "INVALID_EMAIL";
        }
        
        // Check if email already exists
        if (userService.exists(email)) {
            return "EMAIL_EXISTS";
        }
        
        // Generate and send OTP
        String otp = otpService.generateOTP(email);
        emailService.sendOTP(email, otp);
        
        return "OTP_SENT";
    }

    // VERIFY OTP AND REGISTER
    @PostMapping("/register/verify-otp")
    @ResponseBody
    public String verifyOTP(@RequestParam String email, 
                            @RequestParam String password,
                            @RequestParam String otp) {
        
        if (!otpService.verifyOTP(email, otp)) {
            return "INVALID_OTP";
        }
        
        // Register user
        userService.register(email, password);
        return "SUCCESS";
    }

    // LOGIN PROCESSING
    @PostMapping("/login")
    @ResponseBody
    public String doLogin(@RequestParam String username,
                          @RequestParam String password) {
        
        if (!userService.exists(username)) {
            return "INVALID_EMAIL";
        }
        
        if (!userService.login(username, password)) {
            return "INVALID_PASSWORD";
        }
        
        return "SUCCESS";
    }

    // FILE UPLOAD
    @PostMapping("/upload")
    public String uploadFiles(@RequestParam("files") MultipartFile[] files, Model model) throws IOException {

        if (files == null || files.length == 0) {
            model.addAttribute("message", "Please upload at least one file");
            return "upload";
        }

        List<FileMetrics> results = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String content = new String(file.getBytes());
                FileMetrics result = analyzerService.analyze(file.getOriginalFilename(), content);
                results.add(result);
            }
        }

        model.addAttribute("results", results);

        return "result";
    }

    @GetMapping("/upload")
public String showUploadPage() {
    return "upload"; // Thymeleaf template name: upload.html
}
}
