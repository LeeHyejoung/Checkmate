package com.security.CheckMate.Security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.security.CheckMate.DTO.ExamCreateDto;
import com.security.CheckMate.Domain.User;

import javax.crypto.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Cryptogram {
    public void encrypt(String plainFname, String hashFname, User user, SecretKey secretKey, String json) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        System.out.print("test");

        byte[] plainData;
        /*
        try (FileInputStream fis = new FileInputStream(plainFname)) {
            plainData = fis.readAllBytes();
        }*/
        plainData = json.getBytes();
        String plainBase = Base64.getEncoder().encodeToString(plainData);

        //해시
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashData = digest.digest(plainData);
        String hashBase = Base64.getEncoder().encodeToString(hashData);

        //비밀키 가져오기
        AsymmetricKeyManager keyMan = new AsymmetricKeyManager();
        PrivateKey privateKey = keyMan.loadPrivateKey("private" + user.getUserName() + ".txt");
        PublicKey publicKey = keyMan.loadPublicKey("public" + user.getUserName() + ".txt");

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] encryptedHashData = cipher.doFinal(hashData);
        String encryptedHashBase = Base64.getEncoder().encodeToString(encryptedHashData);

        String publicKeyBase = Base64.getEncoder().encodeToString(keyMan.getPublicKeyBytes());
        Map<String, String> jsonData = new HashMap<>();
        jsonData.put("plain", plainBase);
        jsonData.put("hash", hashBase);
        jsonData.put("encrypted_hash", encryptedHashBase);
        jsonData.put("public_key", publicKeyBase);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonString = gson.toJson(jsonData);

        Cipher symCipher = Cipher.getInstance("AES");
        symCipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedJson = symCipher.doFinal(jsonString.getBytes("UTF-8"));

        Files.write(Paths.get("encrypted_" + user.getUserName() + ".txt"), encryptedJson);
        System.out.println(jsonString);

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
