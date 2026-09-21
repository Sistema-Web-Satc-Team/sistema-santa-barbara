package br.org.bandasantabarbara.application.usecase;

import br.org.bandasantabarbara.application.events.ConvidarMembroEvento;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EnviarEmailDeConvite {

    private final JavaMailSender mailSender;

    public EnviarEmailDeConvite(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @EventListener
    public void handle(ConvidarMembroEvento evento) {
        String destinatarioEmail = evento.membro().getEmail();
        String urlConvite = evento.URL();

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(destinatarioEmail);
        msg.setSubject("Convite - Banda Santa Bárbara");
        msg.setText(
                "Olá! Você foi convidado para participar da Banda Santa Bárbara. Clique no link abaixo para aceitar o convite:\n\n" + urlConvite
        );

        try {
            this.mailSender.send(msg);
        } catch (MailException ex) {
            System.err.println("Erro ao enviar e-mail: " + ex.getMessage());
            throw new RuntimeException("Ocorreu um erro interno no servidor ao tentar enviar o e-mail.");
        }
    }
}