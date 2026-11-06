package br.org.bandasantabarbara.application.services.aula;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AulaLifecycleScheduler {

    private final AulaService aulaService;

    public AulaLifecycleScheduler(AulaService aulaService) {
        this.aulaService = aulaService;
    }

    @Scheduled(fixedDelay = 60_000)
    public void encerrarAulasVencidas() {
        aulaService.encerrarAulasVencidas();
    }
}
