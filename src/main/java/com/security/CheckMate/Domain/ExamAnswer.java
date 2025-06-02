package com.security.CheckMate.Domain;

import java.time.LocalDateTime;

public class ExamAnswer {
    private String studentId;
    private String answerText;
    private LocalDateTime submittedAt;

    public ExamAnswer(String studentId, String answerText) {
        this.studentId = studentId;
        this.answerText = answerText;
        this.submittedAt = LocalDateTime.now();
    }


}


