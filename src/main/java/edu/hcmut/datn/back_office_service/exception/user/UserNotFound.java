package edu.hcmut.datn.back_office_service.exception.user;

public class UserNotFound extends RuntimeException {

    public UserNotFound(String message) {
        super(message);
    }
}
