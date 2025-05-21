package com.security.CheckMate.Controller;

import com.security.CheckMate.DTO.ExamCreateDto;
import com.security.CheckMate.Service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ExamController {
    @Autowired
    ExamService examSvc;

    @RequestMapping("/exam/submit")
    public String makeExamAnswer(@ModelAttribute ExamCreateDto examCreateDto) {
        examSvc.makeExamAnswer(examCreateDto);
        return "";
    }

    @RequestMapping("/exam/verify")
    public String verifyExamAnswer() {
        examSvc.verifyExamAnswer();
        return "";
    }
}
