package com.leetcodeapi.services.impl;

import com.leetcodeapi.services.MailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Value("${env.frontend.url}")
    private String FRONTEND_URL;

    private final JavaMailSender javaMailSender;

    public MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendForgetPasswordMail(String email, String username, String resetToken) {
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mail, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("Password Reset Request");
            helper.setText(forgetPasswordMailBody(username,resetToken),true);
            javaMailSender.send(mail);
            log.info("HTML Mail sent successfully");
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while sending HTML mail", e);
        }
    }

    @Override
    public void sendVerificationMail(String to) {
        // send verification mail
    }

    @Override
    public void sendWelcomeMail(String to) {
        // send welcome mail
    }

    @Override
    public void sendMail(String to, String subject, String body) {
        // send mail
    }

    private String welcomeMailBody(String username) {
        String html = "" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Welcome to Leetcode List!</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "            background-color: #f0f9ff;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .container {\n" +
                "            max-width: 650px;\n" +
                "            margin: 50px auto;\n" +
                "            padding: 30px;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 12px;\n" +
                "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\n" +
                "            text-align: center;\n" +
                "            border-top: 5px solid #007bff;\n" +
                "        }\n" +
                "        h2 {\n" +
                "            font-size: 30px;\n" +
                "            color: #007bff;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            font-size: 18px;\n" +
                "            color: #555555;\n" +
                "            line-height: 1.6;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 14px 28px;\n" +
                "            font-size: 18px;\n" +
                "            color: #ffffff;\n" +
                "            background-color: #007bff;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 6px;\n" +
                "            margin-top: 20px;\n" +
                "            transition: background-color 0.3s ease, transform 0.3s ease;\n" +
                "        }\n" +
                "        .button:hover {\n" +
                "            background-color: #0056b3;\n" +
                "            transform: translateY(-2px);\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            font-size: 14px;\n" +
                "            color: #888888;\n" +
                "            margin-top: 30px;\n" +
                "        }\n" +
                "        .footer a {\n" +
                "            color: #007bff;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "        .footer a:hover {\n" +
                "            text-decoration: underline;\n" +
                "        }\n" +
                "        .support-info {\n" +
                "            font-size: 16px;\n" +
                "            color: #333;\n" +
                "            margin-top: 20px;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h2>Welcome to Leetcode List!</h2>\n" +
                "        <p class=\"content\">Hi "+username+",</p>\n" +
                "        <p class=\"content\">We’re absolutely thrilled to have you join our community! At Leetcode List, we’re committed to supporting you as you tackle coding challenges and achieve your career goals.</p>\n" +
                "        <p class=\"content\">To get started, visit your dashboard and dive into the resources we have prepared for you:</p>\n" +
                "        <a href=\""+FRONTEND_URL+"/dashboard\" class=\"button\">Go to Dashboard</a>\n" +
                "        <p class=\"content\">If you have any questions or need assistance, don’t hesitate to reach out. We’re here to help!</p>\n" +
                    "        <p class=\"support-info\">Contact our support team at: <a href=\"mailto:support@leetcode-list.com\">support@leetcode-list.com</a></p>\n" +
                "    </div>\n" +
                "    <div class=\"footer\">\n" +
                "        <p>&copy; 2024 Leetcode List. All rights reserved.</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
        return html;
    }

    private String forgetPasswordMailBody(String username, String resetToken){
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Reset Your Password</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "            background-color: #f0f9ff;\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .container {\n" +
                "            max-width: 650px;\n" +
                "            margin: 50px auto;\n" +
                "            padding: 30px;\n" +
                "            background-color: #ffffff;\n" +
                "            border-radius: 12px;\n" +
                "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);\n" +
                "            text-align: center;\n" +
                "            border-top: 5px solid #007bff;\n" +
                "        }\n" +
                "        h2 {\n" +
                "            font-size: 30px;\n" +
                "            color: #007bff;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .content {\n" +
                "            font-size: 18px;\n" +
                "            color: #555555;\n" +
                "            line-height: 1.6;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "        .button {\n" +
                "            display: inline-block;\n" +
                "            padding: 14px 28px;\n" +
                "            font-size: 18px;\n" +
                "            color: #ffffff;\n" +
                "            background-color: #007bff;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 6px;\n" +
                "            margin-top: 20px;\n" +
                "            transition: background-color 0.3s ease, transform 0.3s ease;\n" +
                "        }\n" +
                "        .button:hover {\n" +
                "            background-color: #0056b3;\n" +
                "            transform: translateY(-2px);\n" +
                "        }\n" +
                "        .footer {\n" +
                "            text-align: center;\n" +
                "            font-size: 14px;\n" +
                "            color: #888888;\n" +
                "            margin-top: 30px;\n" +
                "        }\n" +
                "        .footer a {\n" +
                "            color: #007bff;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "        .footer a:hover {\n" +
                "            text-decoration: underline;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h2>Reset Your Password</h2>\n" +
                "        <p class=\"content\">Hi "+ username +",</p>\n" +
                "        <p class=\"content\">We received a request to reset your password. If you made this request, please click the button below to reset your password. If you didn't request this change, you can safely ignore this email.</p>\n" +
                "        <p class=\"content\">To proceed with resetting your password, click the link below:</p>\n" +
                "        <a href=\""+FRONTEND_URL+"/reset-password?token="+resetToken+"\" class=\"button\">Reset Password</a>\n" +
                "        <p class=\"content\">For security reasons, this link will expire in 1 hours. If you need further assistance, feel free to reach out to our support team.</p>\n" +
                "        <p class=\"content\">Thank you!</p>\n" +
                "    </div>\n" +
                "    <div class=\"footer\">\n" +
                "        <p>&copy; 2024 Leetcode List. All rights reserved.</p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>\n";
        return html;
    }

}
