package com.security.CheckMate.Service;

import com.security.CheckMate.DTO.ExamCreateDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class ExamService {

    public void makeExamAnswer(ExamCreateDto examCreateDto, HttpSession session) {
        session.setAttribute("publicKeyBytes", examCreateDto.getPublicKeyBytes()); // 예시
    }

    public void verifyExamAnswer(HttpSession session) {
        byte[] publicKeyBytes = (byte[]) session.getAttribute("publicKeyBytes");
    }
}
