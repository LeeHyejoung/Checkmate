package com.security.CheckMate.Security;

import com.security.CheckMate.Domain.User;

import javax.crypto.*;
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

    public SecretKey decrypt(PrivateKey privateKey, User sender) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException, ClassNotFoundException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        String fname = "envelope" + sender.getUserName() + ".txt";

        //보안분석 - 자원동반 try catch문으로 수정
        FileInputStream fis = new FileInputStream(fname);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        ObjectInputStream os = new ObjectInputStream(cis);
        SecretKey secretKey = (SecretKey) os.readObject();

        return secretKey;
    }
}
