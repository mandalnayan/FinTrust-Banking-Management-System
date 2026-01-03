package com.fintrust.model;

import java.time.LocalDateTime;

import org.zkoss.util.media.Media;

public class UserDocument {

    private Long docId;
    private Long userId;
    private String docType;               // AADHAAR, PAN, ADDRESS_PROOF, PHOTO
    private String originalFileName;      // user uploaded name
    private String storedFileName;        // UUID filename
    private String mimeType;
    private Long fileSize;
    private String storagePath;           // absolute path on disk
    private LocalDateTime uploadedAt;
    
    private Media media;

    // ---- getters & setters ----

    public Long getDocId() {
        return docId;
    }

    public void setDocId(Long docId) {
        this.docId = docId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
//  Updating media document
    public Media getMedia() {
        return this.media;
    }

    public void setMedia(Media media) {
    	this.media = media;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
