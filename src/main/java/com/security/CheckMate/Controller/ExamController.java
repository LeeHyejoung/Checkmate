package com.security.CheckMate.Controller;

import com.security.CheckMate.DTO.ExamCommand;
import com.security.CheckMate.Domain.User;
import com.security.CheckMate.Security.AsymmetricKeyManager;
import com.security.CheckMate.Service.ExamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

@Controller
public class ExamController {

    @Autowired
    ExamService examSvc;

    @PostMapping("/exam/submit")
    public String submitAnswer(@ModelAttribute ExamCommand examCommand, HttpSession session) throws NoSuchPaddingException, IOException, NoSuchAlgorithmException, InvalidKeyException, ClassNotFoundException, SignatureException, IllegalBlockSizeException, BadPaddingException {
        String json = examSvc.toJson(examCommand);
        //examSvc.makeExamAnswer(examCommand, json, session);
        User user = (User) session.getAttribute("user");
        examSvc.encryptAnswer(json, user);
        return "submit";
    }

    @GetMapping("/exam/submit")
    public String showSubmitPage(@ModelAttribute ExamCommand examCommand, HttpSession session) throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, SignatureException, BadPaddingException, InvalidKeyException, ClassNotFoundException {
        String json = examSvc.toJson(examCommand);
        //examSvc.makeExamAnswer(examCommand, json, session);
        User user = (User) session.getAttribute("user");
        examSvc.encryptAnswer(json, user);
        return "submit";
    }

    @RequestMapping("/exam/verify")
    public String verifyExamAnswer(HttpSession session, Model model) {
        boolean valid = examSvc.verifyExamAnswer(session);
        model.addAttribute("verification", valid ? "검증 성공" : "검증 실패");
        return "verification_result";
    }

    @GetMapping("/exam")
    public String showExam(HttpSession session, Model model) throws NoSuchAlgorithmException {
        User user = new User("20221234", "student", new String[]{"웹코드보안", "소프트웨어시스템개발"});

        AsymmetricKeyManager keyMan = new AsymmetricKeyManager();
        keyMan.generate();
        keyMan.savePrivateKey("private" + user.getUserName() + ".txt");
        keyMan.savePublicKey("public" + user.getUserName() + ".txt");

        session.setAttribute("user", user);
        model.addAttribute("user", user);

        return "exam";
    }

    @RequestMapping("/score")
    public String showScore(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        int score = examSvc.getScoreForUser(user);
        model.addAttribute("user", user);
        model.addAttribute("score", score);

        return "score";
    }

    @GetMapping("/professor_exam")
    public String professorExamPage() {
        return "professor_exam";
    }

    @PostMapping("/professor_exam")
    public String processStudentId(@RequestParam("studentId") String studentId, Model model) {
        if ("20221234".equals(studentId)) {
            User user = new User("20221234", "student", new String[]{"웹코드보안"});
            ExamCommand examCommand = new ExamCommand();
            examCommand.setQ1Answer("Spring Security는 인증과 권한을 처리해주는 프레임워크입니다.");
            examCommand.setQ2Answer("3");

            model.addAttribute("message", "유효한 학번입니다.");
            model.addAttribute("user", user);
            model.addAttribute("examCommand", examCommand);
        } else {
            model.addAttribute("message", "잘못된 학번입니다.");
        }
        return "professor_exam";
    }

}
