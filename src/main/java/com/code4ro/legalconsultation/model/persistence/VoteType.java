package com.code4ro.legalconsultation.model.persistence;

public enum VoteType {
    UP(1),
    DOWN(-1),
    ABSTAIN(0);

    private int score;

    VoteType(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
