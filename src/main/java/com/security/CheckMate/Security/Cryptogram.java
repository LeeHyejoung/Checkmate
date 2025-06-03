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
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Cryptogram {
    public void encrypt(/*String plainFname, String hashFname,*/User user, SecretKey secretKey, String json) throws IOException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
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

    public void decrypt(User user, SecretKey secretKey) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException {
        byte[] encrypted = Files.readAllBytes(Paths.get("encrypted_" + user.getUserName() + ".txt"));

        Cipher symCipher = Cipher.getInstance("AES");
        symCipher.init(Cipher.DECRYPT_MODE, secretKey);

        //여기가 문제
        byte[] decryptedJsonBytes = symCipher.doFinal(encrypted);



        String decryptedJson = new String(decryptedJsonBytes, "UTF-8");

        System.out.println(decryptedJson);

        Gson gson = new Gson();
        Map<String, String> jsonData = gson.fromJson(decryptedJson, Map.class);

        String plainBase = jsonData.get("plain");
        String hashBase = jsonData.get("hash");
        String encryptedHashBase = jsonData.get("encrypted_hash");
        String publicKeyBase = jsonData.get("public_key");

        byte[] plainData = Base64.getDecoder().decode(plainBase);
        byte[] encryptedHashData = Base64.getDecoder().decode(encryptedHashBase);
        byte[] originalHash = Base64.getDecoder().decode(hashBase);
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] decryptedHash = cipher.doFinal(encryptedHashData);

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(plainData);

        boolean hashCheck = Arrays.equals(decryptedHash, hash);

        System.out.println("[integrity check result]");
        if (hashCheck) {
            System.out.println("integrity checked.");
        } else {
            System.out.println("integrity errer! corrupted data");
        }

        // (선택) 복호화된 평문 출력
        String plainText = new String(plainData, "UTF-8");
        System.out.println("[decrypted plain text]");
        System.out.println(plainText);
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
        bos.close();
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
