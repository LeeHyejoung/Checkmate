package com.security.CheckMate.Controller;

import com.security.CheckMate.DTO.ExamCommand;
import com.security.CheckMate.DTO.ExamCreateDto;
import com.security.CheckMate.Domain.User;
import com.security.CheckMate.Security.AsymmetricKeyManager;
import com.security.CheckMate.Service.ExamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;


@Controller
public class ExamController {
    @Autowired
    ExamService examSvc;

    @RequestMapping("/exam/submit")
    public String submitAnswer(@ModelAttribute ExamCommand examCommand, HttpSession session) throws NoSuchPaddingException, IOException, NoSuchAlgorithmException, InvalidKeyException, ClassNotFoundException, SignatureException {
        examSvc.makeExamAnswer(examCommand, session);

        User user = (User) session.getAttribute("user");

        examSvc.encryptAnswer(user);

        return "submit";
    }

    @RequestMapping("/exam/verify")
    public String verifyExamAnswer(HttpSession session) {
        examSvc.verifyExamAnswer(session);
        return "someView";
    }

    @RequestMapping("/exam")
    public String showExam(HttpSession session, Model model) throws NoSuchAlgorithmException {
        User user = new User("김동덕", "student", new String[]{"웹코드보안", "소프트웨어시스템개발"});

        AsymmetricKeyManager keyMan = new AsymmetricKeyManager();
        keyMan.generate();
        keyMan.savePrivateKey("private" + user.getUserName() + ".txt");
        keyMan.savePublicKey("public" + user.getUserName() + ".txt");

        session.setAttribute("user", user);
        model.addAttribute("user", user);
        //examSvc.verifyExamAnswer(session);
        return "exam";
    }
}
