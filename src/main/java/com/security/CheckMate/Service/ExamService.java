package com.security.CheckMate.Service;

import com.security.CheckMate.DTO.ExamCommand;
import com.security.CheckMate.Domain.ExamAnswer;
import com.security.CheckMate.Domain.User;
import com.security.CheckMate.Security.Cryptogram;
import com.security.CheckMate.Security.Envelope;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SignatureException;

@Service
public class ExamService {

    public void makeExamAnswer(ExamCommand examCommand, HttpSession session) {
        User user = new User(examCommand.getStudentId(), "student", null);
        ExamAnswer Q1answer = new ExamAnswer(user.getUserName(), examCommand.getQ1Answer());
        ExamAnswer Q2answer = new ExamAnswer(user.getUserName(), examCommand.getQ2Answer());

        // 나중에 DB 저장 또는 파일 저장 로직
        // 지금은 암호화로만 넘겨도 OK
        try {
            encryptAnswer(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void encryptAnswer(User user) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, SignatureException {
        Envelope envelope = new Envelope();

        String publicKeyFname = "public" + user.getUserName() + ".txt";
        FileInputStream fis = new FileInputStream(publicKeyFname);
        ObjectInputStream os = new ObjectInputStream(fis);
        PublicKey publicKey = (PublicKey) os.readObject();

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey secretKey = keyGen.generateKey();

        envelope.encrypt(publicKey, secretKey, user);
        Cryptogram cryptogram = new Cryptogram();
        cryptogram.encrypt("envelope"  + user.getUserName() + ".txt", user);
    }

    public boolean verifyExamAnswer(HttpSession session) {
        try {
            byte[] publicKeyBytes = (byte[]) session.getAttribute("publicKeyBytes");
            // 검증 로직 ...
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public int getScoreForUser(User user) {
        return 100;
    }

}
