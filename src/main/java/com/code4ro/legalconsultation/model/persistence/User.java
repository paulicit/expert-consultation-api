package com.code4ro.legalconsultation.model.persistence;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Size(max = 40)
    private String firstName;

    @Size(max = 40)
    private String lastName;

    @NaturalId
    @NotBlank
    @Size(max = 40)
    @Email
    @Column(unique = true)
    private String email;

    @Size(max = 40)
    private String phoneNumber;

    @Size(max = 40)
    private String district;

    @Size(max = 100)
    private String organisation;

    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole role;

    public User() {
    }

    public User(final String email, final UserRole role) {
        this.email = email;
        this.role = role;
    }
}
