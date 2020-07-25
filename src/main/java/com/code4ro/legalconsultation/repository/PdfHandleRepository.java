package com.code4ro.legalconsultation.repository;

import com.code4ro.legalconsultation.model.persistence.PdfHandle;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PdfHandleRepository extends JpaRepository<PdfHandle, UUID> {
    PdfHandle findByHash(Integer hash);
    PdfHandle findByHashAndState(Integer hash, String state);
    Boolean existsByHash(@NonNull Integer hash);
    Boolean existsByHashAndState(@NonNull Integer hash, @NonNull String state);
}
