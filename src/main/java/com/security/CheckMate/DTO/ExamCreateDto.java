package com.security.CheckMate.DTO;

public class ExamCreateDto {
    private byte[] publicKeyBytes;

    public byte[] getPublicKeyBytes() {
        return publicKeyBytes;
    }

    public void setPublicKeyBytes(byte[] publicKeyBytes) {
        this.publicKeyBytes = publicKeyBytes;
    }
}

