package com.Project_INTELLCAP.Infinitum_Art.user.DTO;

import org.springframework.web.multipart.MultipartFile;

public class UserProfileUpdateRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private MultipartFile image;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}