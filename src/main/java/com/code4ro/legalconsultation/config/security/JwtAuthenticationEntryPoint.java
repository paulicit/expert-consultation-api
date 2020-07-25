package com.code4ro.legalconsultation.config.security;

import com.code4ro.legalconsultation.common.exceptions.ExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest httpServletRequest,
                         HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        // invoked when user tries to access a secured REST resource without supplying any credentials
        log.error("Responding with unauthorized error. Message - {}", e.getMessage());
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final ExceptionResponse exceptionResponse = new ExceptionResponse();
        exceptionResponse.setAdditionalInfo(e.getMessage());
        httpServletResponse.getOutputStream().println(MAPPER.writeValueAsString(exceptionResponse));
    }
}
