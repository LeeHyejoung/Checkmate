package com.security.CheckMate.Security;

import com.security.CheckMate.Domain.User;
import com.security.CheckMate.Exception.DecryptionException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;

public class Envelope {
    //전자 봉투 암호화, 복호화 (받는 사람의 공개키로 비밀키를 암호화)
    public void encrypt(PublicKey publicKey, byte[] secretKey, User user) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IOException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        String fname = "envelope" + user.getUserName() + ".txt";

        try (FileOutputStream fos = new FileOutputStream(fname);) {
            try (CipherOutputStream cos = new CipherOutputStream(fos, cipher);) {
                byte[] data= secretKey;//.getEncoded();
                cos.write(data);
                cos.flush();
            }
        }

        //cos.close();
    }

    public SecretKey decrypt(PrivateKey privateKey, User sender) throws DecryptionException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new DecryptionException("암호화 알고리즘 설정 실패", e);
        } catch (InvalidKeyException e) {
            throw new DecryptionException("복호화용 개인키가 유효하지 않습니다", e);
        }

        String fname = "envelope" + sender.getUserName() + ".txt";

        try (FileInputStream fis = new FileInputStream(fname);
             CipherInputStream cis = new CipherInputStream(fis, cipher);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[1024];
            int read;
            while ((read = cis.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            byte[] decodedKey = baos.toByteArray();

            if (decodedKey.length == 0) {
                throw new DecryptionException("복호화된 키 데이터가 비어 있습니다.");
            }

            return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

        } catch (FileNotFoundException e) {
            throw new DecryptionException("암호화된 파일을 찾을 수 없습니다: " + fname, e);
        } catch (IOException e) {
            throw new DecryptionException("복호화 중 입출력 오류가 발생했습니다", e);
        } catch (IllegalArgumentException e) {
            throw new DecryptionException("SecretKey 생성 중 오류 발생: 잘못된 키 형식일 수 있습니다", e);
        }
    }

}
