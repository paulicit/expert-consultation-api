package com.code4ro.legalconsultation.repository;

import com.code4ro.legalconsultation.model.persistence.DocumentConsolidated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentConsolidatedRepository extends JpaRepository<DocumentConsolidated, UUID> {
    Optional<DocumentConsolidated> findByDocumentMetadataId(UUID id);
}
