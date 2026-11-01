package br.org.bandasantabarbara.model.turma;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

import br.org.bandasantabarbara.exception.BadRequestException;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_horario", nullable = false)
    @Setter 
    private Horario horario;

    protected HorarioIntervalo() {}

    public HorarioIntervalo(DayOfWeek diaSemana, LocalTime inicio, LocalTime fim) {
        if (diaSemana == null || inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new BadRequestException("O horário deve ter dia, início e fim válidos.");
        }
        this.diaSemana = diaSemana;
        this.inicio = inicio;
        this.fim = fim;
    }

}
