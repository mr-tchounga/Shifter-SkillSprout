package com.shifter.shifter_back.services.auth;

import com.shifter.shifter_back.payloads.requests.EmailBase;
import org.thymeleaf.context.Context;

public interface EmailService {
    public void sendEmailWithHtmlTemplate(EmailBase email, String templateName, Context context);
}
