package com.leetcodeapi.services;

public interface MailService {
    void sendForgetPasswordMail(String email,String username, String resetToken);
    void sendVerificationMail(String to);
    void sendWelcomeMail(String to);
    void sendMail(String to, String subject, String body);
}
