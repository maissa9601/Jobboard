package com.example.admin.Dto;

import org.springframework.web.multipart.MultipartFile;

public class AdminUpdateRequest {
    private MultipartFile profilePhoto;

    public MultipartFile getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(MultipartFile profilePhoto) {
        this.profilePhoto = profilePhoto;
    }
}
