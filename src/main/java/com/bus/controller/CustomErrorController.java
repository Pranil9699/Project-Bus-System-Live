package com.bus.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
public class CustomErrorController {

    @ExceptionHandler(value = { AccessDeniedException.class })
    public String handleAccessDeniedException(Exception ex, Model model) {
        model.addAttribute("error", "403 Forbidden");
        model.addAttribute("message", "You are not allowed to access this resource.");
        return "error/403";
    }

    @ExceptionHandler(value = { Exception.class })
    public String handleException(Exception ex, Model model) {
        model.addAttribute("error", "500 Internal Server Error");
        model.addAttribute("message", "An unexpected error occurred.");
        return "error/500";
    }
}

