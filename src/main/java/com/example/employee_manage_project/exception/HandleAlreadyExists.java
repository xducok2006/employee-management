package com.example.employee_manage_project.exception;

public class HandleAlreadyExists extends RuntimeException {
    public HandleAlreadyExists(String message) {
        super(message);
    }
}
