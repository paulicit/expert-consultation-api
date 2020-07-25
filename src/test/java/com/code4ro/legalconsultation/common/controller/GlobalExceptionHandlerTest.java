package com.code4ro.legalconsultation.common.controller;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityNotFoundException;
import java.lang.reflect.Executable;
import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class GlobalExceptionHandlerTest {

    @Mock
    private TestController testController;
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(testController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void legalExceptionHandled() throws Exception {
        final LegalValidationException legalValidationException = LegalValidationException.builder()
                .i18nKey("i18nKey")
                .i8nArguments(Collections.singletonList("i18nArg"))
                .httpStatus(HttpStatus.BAD_REQUEST)
                .build();
        when(testController.testException()).thenThrow(legalValidationException);

        mockMvc.perform(get("/testException"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.i18nErrors[0].i18nErrorKey").value("i18nKey"))
                .andExpect(jsonPath("$.i18nErrors[0].i18nErrorArguments[0]").value("i18nArg"));
    }

    @Test
    public void entityNotFoundHandled() throws Exception {
        final EntityNotFoundException entityNotFoundException = new EntityNotFoundException();
        when(testController.testException()).thenThrow(entityNotFoundException);

        mockMvc.perform(get("/testException"))
                .andDo(print()).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.i18nErrors[0].i18nErrorKey").value("validation.Resource.not.found"))
                .andExpect(jsonPath("$.i18nErrors[0].i18nErrorArguments").isEmpty());
    }

    @Test
    public void invalidArgumentHandled() throws Exception {
        final BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(Collections.singletonList(
                new FieldError("maria", "fieldName", "i18nKey")));
        final MethodParameter methodParameter = mock(MethodParameter.class);
        when(methodParameter.getExecutable()).thenReturn(mock(Executable.class));
        final MethodArgumentNotValidException methodArgumentNotValidException
                = new MethodArgumentNotValidException(methodParameter, bindingResult);
        when(testController.testException()).thenThrow(methodArgumentNotValidException);

        mockMvc.perform(get("/testException"))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.i18nFieldErrors['fieldName'].i18nErrorKey").value("i18nKey"))
                .andExpect(jsonPath("$.i18nFieldErrors['fieldName'].i18nErrorArguments").isEmpty());
    }

    @Test
    public void unknownExceptionHandled() throws Exception {
        when(testController.testException()).thenThrow(new RuntimeException("Unexpected Exception"));

        mockMvc.perform(get("/testException"))
                .andDo(print()).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.additionalInfo").value("Unexpected Exception"));
    }

    @RestController
    public class TestController {

        @GetMapping("/testException")
        ResponseEntity<String> testException() throws MethodArgumentNotValidException {
            return null;
        }
    }

}
