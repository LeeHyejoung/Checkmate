package com.security.CheckMate.Security;

import com.security.CheckMate.Domain.User;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.util.Scanner;

public class Envelope {
    //전자 봉투 암호화, 복호화 (받는 사람의 공개키로 비밀키를 암호화)
    public void encrypt(PublicKey publicKey, SecretKey secretKey, User user) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        String fname = "envelope" + user.getUserName() + ".txt";

        FileOutputStream fos = new FileOutputStream(fname);
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);

        //data
        byte[] data= secretKey.getEncoded();
        cos.write(data);
        cos.flush();
        cos.close();
    }

    public SecretKey decrypt(PrivateKey privateKey, User sender) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        String fname = "envelope" + sender.getUserName() + ".txt";

        try (FileInputStream fis = new FileInputStream(fname);
             CipherInputStream cis = new CipherInputStream(fis, cipher)) {
            // 바이트 배열로 읽기
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int read;
            while ((read = cis.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            byte[] decodedKey = baos.toByteArray();

            // SecretKey 복원
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");  // 또는 실제 알고리즘명
            return originalKey;
        }
    }

}
