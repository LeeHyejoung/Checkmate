package com.security.CheckMate.DTO;

public class ExamCommand {
    private String studentId;
    private String q1Answer;
    private String q2Answer;

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getQ1Answer() {
        return q1Answer;
    }

    public void setQ1Answer(String q1Answer) {
        this.q1Answer = q1Answer;
    }

    public String getQ2Answer() {
        return q2Answer;
    }

    public void setQ2Answer(String q2Answer) {
        this.q2Answer = q2Answer;
    }
}
