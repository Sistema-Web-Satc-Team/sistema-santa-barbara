package br.org.bandasantabarbara.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Getter
@Entity
@Table(name = "horario_intervalo")
public class HorarioIntervalo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false)
    private DayOfWeek diaSemana;

    @Column(nullable = false)
    private LocalTime inicio;

    @Column(nullable = false)
    private LocalTime fim;

    protected HorarioIntervalo() {}

    public HorarioIntervalo(DayOfWeek diaSemana, LocalTime inicio, LocalTime fim) {
        this.diaSemana = diaSemana;
        this.inicio = inicio;
        this.fim = fim;
    }

}
