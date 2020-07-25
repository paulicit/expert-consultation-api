package com.code4ro.legalconsultation.service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.code4ro.legalconsultation.common.exceptions.LegalValidationException;
import com.code4ro.legalconsultation.service.api.StorageApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

@Service
@Profile("production")
@Slf4j
public class AmazonS3StorageService implements StorageApi {

    @Value("${storage.aws.access.key.id}")
    private String awsKeyId;

    @Value("${storage.aws.access.key.secret}")
    private String secretKey;

    @Value("${storage.aws.region}")
    private String region;

    @Value("${storage.aws.s3.documentBucket}")
    private String documentBucket;

    private AmazonS3 amazonS3;

    @PostConstruct
    private void initializeS3() {
        try {
            final BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsKeyId, secretKey);
            amazonS3 = AmazonS3ClientBuilder
                    .standard()
                    .withRegion(Regions.fromName(region))
                    .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                    .build();
            if (amazonS3.doesBucketExist(documentBucket)) {
                log.info("Bucket already created");
                return;
            }
            log.info("Creating aws s3 bucket {}.", documentBucket);
            amazonS3.createBucket(documentBucket);
        } catch (Exception e) {
            log.error("Could not access aws s3", e);
        }
    }

    @Override
    public String storeFile(final MultipartFile document) {
        final ObjectMetadata data = new ObjectMetadata();
        data.setContentLength(document.getSize());
        final String uniqueDocumentName = StorageApi.resolveUniqueName(document);
        log.info("Storing document with name {}.", uniqueDocumentName);
        try {
            final PutObjectRequest putObjectRequest =
                new PutObjectRequest(documentBucket, uniqueDocumentName, document.getInputStream(), data)
                .withCannedAcl(CannedAccessControlList.PublicRead);
            amazonS3.putObject(putObjectRequest);
        } catch (Exception e) {
            log.error("Storing of document with name: {} failed.", uniqueDocumentName, e);
            throw LegalValidationException.builder()
                    .i18nKey("storage.upload.failed")
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
        return uniqueDocumentName;
    }

    @Override
    public byte[] loadFile(String documentURI) {
        log.info("Loading document with uri {}.", documentURI);
        try {
            return amazonS3.getObject(documentBucket, documentURI)
                    .getObjectContent()
                    .readAllBytes();
        } catch (Exception e) {
            log.error("Loading of document uri: {} failed.", documentURI, e);
            throw LegalValidationException.builder()
                    .i18nKey("storage.load.failed")
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @Override
    public void deleteFile(String documentURI) {
        try {
            amazonS3.deleteObject(documentBucket, documentURI);
        } catch (Exception e) {
            log.error("Load File fail", e);
            throw LegalValidationException.builder()
                    .i18nKey("storage.delete.failed")
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
