package com.code4ro.legalconsultation.service.api;

import com.code4ro.legalconsultation.model.dto.SignUpRequest;
import com.code4ro.legalconsultation.model.persistence.Invitation;
import com.code4ro.legalconsultation.model.persistence.User;

public interface InvitationService {
    Invitation getInvitation(String code);

    Invitation create(User user);

    boolean isValid(SignUpRequest signUpRequest);

    void markAsUsed(String code);
}
