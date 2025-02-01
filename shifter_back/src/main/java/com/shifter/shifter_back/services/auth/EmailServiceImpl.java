package com.shifter.shifter_back.services.auth;

import com.shifter.shifter_back.exceptions.TokenRefreshException;
import com.shifter.shifter_back.models.User;
import com.shifter.shifter_back.models.auth.RefreshToken;
import com.shifter.shifter_back.payloads.requests.EmailBase;
import com.shifter.shifter_back.payloads.responses.UserMachineDetails;
import com.shifter.shifter_back.repositories.auth.RefreshTokenRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private JavaMailSender mailSender;
    private TemplateEngine templateEngine;

    @Override
    public void sendEmailWithHtmlTemplate(EmailBase email, String templateName, Context context) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

        String mail =  System.getProperty("EMAIL");
        try {
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setFrom("\"" + mail + "\"");
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            //
        }
    }
}
