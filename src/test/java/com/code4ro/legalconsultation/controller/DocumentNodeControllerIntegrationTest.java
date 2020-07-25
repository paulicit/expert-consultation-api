package com.code4ro.legalconsultation.controller;

import com.code4ro.legalconsultation.common.controller.AbstractControllerIntegrationTest;
import com.code4ro.legalconsultation.converters.DocumentNodeMapper;
import com.code4ro.legalconsultation.model.persistence.DocumentNode;
import com.code4ro.legalconsultation.factory.DocumentNodeFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class DocumentNodeControllerIntegrationTest extends AbstractControllerIntegrationTest {

    @Autowired
    private DocumentNodeFactory documentNodeFactory;

    @Autowired
    private DocumentNodeMapper nodeMapper;

    @Before
    public void before() {persistMockedUser();}

    @Test
    @WithMockUser
    @Transactional
    public void modifyNode() throws Exception {
        DocumentNode documentNode = documentNodeFactory.save();
        documentNode.setTitle("Modified test title");

        mvc.perform(put(endpoint("/api/document-nodes/", documentNode.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nodeMapper.map(documentNode)))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get(endpoint("/api/document-nodes/", documentNode.getId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Modified test title"))
                .andExpect(jsonPath("$.children.size()").value(1));
    }

    @Test
    @WithMockUser
    @Transactional
    public void deleteChildNode() throws Exception {
        DocumentNode documentNode = documentNodeFactory.save();
        DocumentNode firstChild = documentNode.getChildren().get(0);

        int childCount = documentNode.getChildren().size();

        MvcResult result = mvc.perform(delete(endpoint("/api/document-nodes/", firstChild.getId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getContentAsString())
                .isEqualTo(objectMapper.writeValueAsString(documentNode.getId()));

        mvc.perform(get(endpoint("/api/document-nodes", documentNode.getId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.children.size()").value(childCount - 1))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    @Transactional
    public void deleteRootNode() throws Exception {
        DocumentNode documentNode = documentNodeFactory.save();
        mvc.perform(delete(endpoint("/api/document-nodes/", documentNode.getId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mvc.perform(get(endpoint("/api/document-nodes", documentNode.getId()))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.NOT_FOUND.value()));
    }
}
