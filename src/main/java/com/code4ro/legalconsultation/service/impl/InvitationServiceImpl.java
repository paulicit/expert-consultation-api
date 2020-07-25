package com.code4ro.legalconsultation.service.impl;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.model.dto.SignUpRequest;
import com.code4ro.legalconsultation.model.persistence.Invitation;
import com.code4ro.legalconsultation.model.persistence.InvitationStatus;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.repository.InvitationRepository;
import com.code4ro.legalconsultation.service.api.InvitationService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional
public class InvitationServiceImpl implements InvitationService {
    private final InvitationRepository invitationRepository;

    @Autowired
    public InvitationServiceImpl(final InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    @Override
    public Invitation getInvitation(final String code) {
        final Invitation invitation = invitationRepository.findByCode(code)
                .orElseThrow(EntityNotFoundException::new);

        if (!InvitationStatus.PENDING.equals(invitation.getStatus())) {
            throw LegalValidationException.builder()
                    .i18nKey("user.invitation.invalid")
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .build();
        }

        return invitation;
    }

    @Override
    public Invitation create(final User user) {
        Invitation invitation = new Invitation();
        invitation.setStatus(InvitationStatus.PENDING);
        invitation.setUser(user);
        invitation.setCode(RandomStringUtils.randomAlphanumeric(10));

        return invitationRepository.save(invitation);
    }

    @Override
    public boolean isValid(final SignUpRequest signUpRequest) {
        final Invitation invitation = invitationRepository.findByCode(signUpRequest.getInvitationCode())
                .orElseThrow(EntityNotFoundException::new);

        return InvitationStatus.PENDING.equals(invitation.getStatus())
                && signUpRequest.getEmail().equals(invitation.getUser().getEmail());
    }

    @Override
    public void markAsUsed(final String code) {
        final Invitation invitation = invitationRepository.findByCode(code)
                .orElseThrow(EntityNotFoundException::new);

        invitation.setStatus(InvitationStatus.USED);
        invitationRepository.save(invitation);
    }
}
