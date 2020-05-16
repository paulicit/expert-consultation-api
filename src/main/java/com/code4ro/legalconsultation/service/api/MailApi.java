package com.code4ro.legalconsultation.service.api;
import com.code4ro.legalconsultation.model.persistence.DocumentMetadata;
import com.code4ro.legalconsultation.model.persistence.User;
import java.util.List;

public interface MailApi {

    void sendRegisterMail(final List<User> users);
    void sendDocumentAssignedEmail(final DocumentMetadata documentMetadata, final List<User> users);
}
