package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.common.controller.AbstractControllerIntegrationTest;
import com.code4ro.legalconsultation.factory.PdfFileFactory;
import org.junit.AfterClass;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileSystemUtils;

import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FileControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @AfterClass
    public static void afterAll() {
        FileSystemUtils.deleteRecursively(new File(System.getProperty("user.home"), "test_uploads"));
    }

    @Test
    @WithMockUser
    @Transactional
    public void saveFile() throws Exception {
        final MockMultipartFile randomFile = PdfFileFactory.getAsMultipart(getClass().getClassLoader());

        final MvcResult mvcResult = mvc.perform(multipart("/api/file")
                .file(randomFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        final String filePath = mvcResult.getResponse().getContentAsString();
        assertThat(filePath, containsString("pdf"));
    }

    @Test
    @WithMockUser
    @Transactional
    public void deleteFile() throws Exception {
        final MockMultipartFile randomFile = PdfFileFactory.getAsMultipart(getClass().getClassLoader());

        final MvcResult mvcResult = mvc.perform(multipart("/api/file")
                .file(randomFile))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        final String filePath = mvcResult.getResponse().getContentAsString();
        assertThat(filePath, containsString("pdf"));

        mvc.perform(delete("/api/file")
                .contentType(MediaType.APPLICATION_JSON)
                .content(filePath)
                .accept(MediaType.APPLICATION_JSON));
    }
}
