package com.code4ro.legalconsultation.model.persistence;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "votes",
        uniqueConstraints = {@UniqueConstraint(
                columnNames = {"comment_id", "owner_id"},
                name = "uq_comment_id_owner_id")})
@Getter
@Setter
@NoArgsConstructor
public class Vote extends BaseEntity {

    @Column(name = "vote")
    @Enumerated(EnumType.STRING)
    private VoteType vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private ApplicationUser owner;

    @Column(name = "last_edit_date", nullable = false)
    @Temporal(TemporalType.DATE)
    @LastModifiedDate
    private Date lastEditDateTime;

    public Vote(Comment comment, ApplicationUser currentVoter) {
        this.comment = comment;
        this.owner = currentVoter;
    }
}
