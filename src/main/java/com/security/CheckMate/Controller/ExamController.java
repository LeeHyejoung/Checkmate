package com.security.CheckMate.Controller;

import com.security.CheckMate.DTO.ExamCreateDto;
import com.security.CheckMate.Service.ExamService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ExamController {
    @Autowired
    ExamService examSvc;

    @RequestMapping("/exam/submit")
    public String makeExamAnswer(@ModelAttribute ExamCreateDto examCreateDto, HttpSession session) {
        examSvc.makeExamAnswer(examCreateDto, session);
        return "someView";
    }

    @RequestMapping("/exam/verify")
    public String verifyExamAnswer(HttpSession session) {
        examSvc.verifyExamAnswer(session);
        return "someView";
    }

    @RequestMapping("/exam")
    public String showExam(HttpSession session) {
        //examSvc.verifyExamAnswer(session);
        return "exam";
    }
}
