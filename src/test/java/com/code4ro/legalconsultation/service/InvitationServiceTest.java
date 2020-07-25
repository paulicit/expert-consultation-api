package com.code4ro.legalconsultation.service;

import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.factory.RandomObjectFiller;
import com.code4ro.legalconsultation.model.dto.SignUpRequest;
import com.code4ro.legalconsultation.model.persistence.Invitation;
import com.code4ro.legalconsultation.model.persistence.InvitationStatus;
import com.code4ro.legalconsultation.model.persistence.User;
import com.code4ro.legalconsultation.repository.InvitationRepository;
import com.code4ro.legalconsultation.service.impl.InvitationServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InvitationServiceTest {
    @Mock
    private InvitationRepository invitationRepository;
    @InjectMocks
    private InvitationServiceImpl invitationService;

    @Captor
    private ArgumentCaptor<Invitation> invitationArgumentCaptor;

    @Test
    public void testGetNonExistentInvitation() {
        when(invitationRepository.findByCode(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> invitationService.getInvitation(UUID.randomUUID().toString()));
    }

    @Test
    public void testGetValidInvitation() {
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setStatus(InvitationStatus.PENDING);

        when(invitationRepository.findByCode(any())).thenReturn(Optional.of(invitation));

        final Invitation foundInvitation = invitationService.getInvitation(invitation.getCode());

        assertThat(foundInvitation).isEqualTo(invitation);
    }

    @Test
    public void testGetInvitationWhichIsNotPending() {
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setStatus(InvitationStatus.USED);

        when(invitationRepository.findByCode(any())).thenReturn(Optional.of(invitation));

        assertThatExceptionOfType(LegalValidationException.class)
                .isThrownBy(() -> invitationService.getInvitation(invitation.getCode()));
    }

    @Test
    public void testCreateInvitation() {
        final User user = RandomObjectFiller.createAndFill(User.class);

        invitationService.create(user);

        verify(invitationRepository).save(invitationArgumentCaptor.capture());
        final Invitation invitation = invitationArgumentCaptor.getValue();
        assertThat(invitation.getStatus()).isEqualTo(InvitationStatus.PENDING);
        assertThat(invitation.getCode()).isNotEmpty();
        assertThat(invitation.getUser()).isEqualTo(user);
    }

    @Test
    public void testMarkInvitationAsUsed() {
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setStatus(InvitationStatus.PENDING);

        when(invitationRepository.findByCode(any())).thenReturn(Optional.of(invitation));

        invitationService.markAsUsed(invitation.getCode());

        verify(invitationRepository).save(invitationArgumentCaptor.capture());
        final Invitation savedInvitation = invitationArgumentCaptor.getValue();
        assertThat(savedInvitation.getStatus()).isEqualTo(InvitationStatus.USED);
    }

    @Test
    public void testMarkNonExistentInvitationAsUsed() {
        when(invitationRepository.findByCode(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> invitationService.markAsUsed(UUID.randomUUID().toString()));

    }

    @Test
    public void testIsValidNonExistentInvitation() {
        when(invitationRepository.findByCode(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> invitationService.isValid(new SignUpRequest()));
    }

    @Test
    public void testCheckUsedInvitation() {
        final SignUpRequest signUpRequest = RandomObjectFiller.createAndFill(SignUpRequest.class);
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setStatus(InvitationStatus.USED);

        when(invitationRepository.findByCode(any())).thenReturn(Optional.of(invitation));

        final boolean isValid = invitationService.isValid(signUpRequest);

        assertThat(isValid).isFalse();
    }

    @Test
    public void testCheckExpiredInvitation() {
        final SignUpRequest signUpRequest = RandomObjectFiller.createAndFill(SignUpRequest.class);
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setStatus(InvitationStatus.EXPIRED);

        when(invitationRepository.findByCode(any())).thenReturn(Optional.of(invitation));

        final boolean isValid = invitationService.isValid(signUpRequest);

        assertThat(isValid).isFalse();
    }

    @Test
    public void testCheckEmailMissmatch() {
        final SignUpRequest signUpRequest = RandomObjectFiller.createAndFill(SignUpRequest.class);
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setStatus(InvitationStatus.PENDING);

        when(invitationRepository.findByCode(any())).thenReturn(Optional.of(invitation));

        final boolean isValid = invitationService.isValid(signUpRequest);

        assertThat(isValid).isFalse();
    }

    @Test
    public void testCheckValidInvitation() {
        final Invitation invitation = RandomObjectFiller.createAndFill(Invitation.class);
        invitation.setStatus(InvitationStatus.PENDING);
        final SignUpRequest signUpRequest = RandomObjectFiller.createAndFill(SignUpRequest.class);
        signUpRequest.setEmail(invitation.getUser().getEmail());

        when(invitationRepository.findByCode(any())).thenReturn(Optional.of(invitation));

        final boolean isValid = invitationService.isValid(signUpRequest);

        assertThat(isValid).isTrue();
    }
}
