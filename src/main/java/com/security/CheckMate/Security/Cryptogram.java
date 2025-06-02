package com.security.CheckMate.Security;

import com.security.CheckMate.DTO.ExamCreateDto;
import com.security.CheckMate.Domain.User;

import java.io.*;
import java.security.*;

public class Cryptogram {
    /*
    비밀키로 다음과 같은 파일 암호화
    학생의 파일, 파일의 해시값을 학생의 사설키로 암호화한 값, 학생의 공개키
    */
    public void encrypt(String fname, User user) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        byte[] data;
        try (FileInputStream fis = new FileInputStream(fname)) {
            data = fis.readAllBytes();
        }
        AsymmetricKeyManager keyMan = new AsymmetricKeyManager();

        PrivateKey privateKey = keyMan.loadPrivateKey("private" + user.getUserName() + ".txt");

        sign(data, privateKey, "sign" + user.getUserName() + ".txt");
        keyMan.loadPublicKey("public" + user.getUserName() + ".txt");
        PublicKey publicKey = keyMan.getPublicKey();

    }
    public void sign(byte[] data, PrivateKey privateKey, String signName) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        //System.out.print("데이터 파일 이름 : ");
        //String plainName = sc.next();
        //System.out.print("개인키 파일 이름 : ");

        //FileInputStream fis = new FileInputStream(plainName);
        //byte[] data = fis.readAllBytes();
        /*
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(createDto);
        oos.flush();

        byte[] data = bos.toByteArray();

        oos.close();
        bos.close();;
*/
        //fis.close();

        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initSign(privateKey);
        sig.update(data);
        byte[] signature = sig.sign();

        for (byte bytes : signature) {
            System.out.print(String.format("%02x", bytes) + " ");
        }

        //파일에 저장;
        FileOutputStream fos = new FileOutputStream(signName);
        fos.write(signature);

        fos.close();
    }

    public void verify(ExamCreateDto examCreateDto, PublicKey publicKey, String signName) throws IOException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        //System.out.print("데이터 파일 이름 : ");
        //String plainName = sc.next();
        //FileInputStream fis1 = new FileInputStream(plainName);
        //평문
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(examCreateDto);
        oos.flush();

        byte[] data = bos.toByteArray();

        oos.close();
        bos.close();;
        //fis1.close();

        //System.out.print("공개키 파일 이름 : ");
        //String pubKeyName = sc.next();

        //KeyManager keyMan = new KeyManager();
        //keyMan.loadPublicKey(pubKeyName);

        //System.out.print("전자서명 파일 이름 : ");
        //String signName = sc.next();
        FileInputStream fis = new FileInputStream(signName);
        byte[] signature = fis.readAllBytes();
        for (byte bytes : signature) {
            System.out.print(String.format("%02x", bytes) + " ");
        }

        Signature sig = Signature.getInstance("SHA256withRSA");
        //sig.initVerify(keyMan.getPublicKey());
        sig.initVerify(publicKey);
        sig.update(data);
        boolean rslt = sig.verify(signature);

        System.out.println("\n서명 검증 결과:" + rslt);

        fis.close();
        //sc.close();
    }
}
