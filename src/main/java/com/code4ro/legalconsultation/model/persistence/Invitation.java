package com.code4ro.legalconsultation.model.persistence;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "invitations")
@Getter
@Setter
public class Invitation extends BaseEntity {
    @NotNull
    @OneToOne(fetch = FetchType.EAGER)
    private User user;
    @NotNull
    private String code;
    @NotNull
    @Enumerated(EnumType.STRING)
    private InvitationStatus status = InvitationStatus.PENDING;
}
