package br.org.bandasantabarbara.application.services.convite;

import br.org.bandasantabarbara.application.dtos.convite.EnviarEmailConviteRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EnviarEmailDeConvite {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Value("${frontend.invitation.url}")
    private String frontEndUrl;

    public EnviarEmailDeConvite(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public boolean handle(EnviarEmailConviteRequest request) {
        String destinatarioEmail = request.membro().getEmail();
        String urlConvite = request.URL();

        try {
            Context context = new Context();
            context.setVariable("urlConvite", urlConvite);

            String htmlContent = templateEngine.process("email-convite", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(destinatarioEmail);
            helper.setSubject("Convite Oficial - Banda Santa Bárbara 🎶");
            helper.setText(htmlContent, true);

            ClassPathResource imageResource = new ClassPathResource("static/images/Brand.jpg");
            helper.addInline("logoBanda", imageResource);


            this.mailSender.send(mimeMessage);
            return true;
        } catch (MessagingException | RuntimeException ex) {
            System.err.println("Erro ao enviar e-mail HTML para " + destinatarioEmail + ": " + ex.getMessage());
            return false;
        }
    }
}