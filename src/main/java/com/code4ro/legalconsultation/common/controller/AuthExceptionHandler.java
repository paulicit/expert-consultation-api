package com.code4ro.legalconsultation.common.controller;

import com.code4ro.legalconsultation.common.exceptions.ExceptionResponse;
import com.code4ro.legalconsultation.common.exceptions.I18nError;
import com.code4ro.legalconsultation.controller.AuthController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(assignableTypes = AuthController.class)
public class AuthExceptionHandler extends GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Object> handleBadCredentials(final BadCredentialsException ex) {
        final ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setI18nErrors(Collections.singletonList(
                new I18nError("login.Bad.credentials")));
        return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
    }
}
