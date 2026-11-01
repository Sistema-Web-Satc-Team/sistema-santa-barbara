package br.org.bandasantabarbara.model.turma;

import br.org.bandasantabarbara.exception.BadRequestException;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.stream.Collectors;

@Entity
@Table(name = "horario")
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(
        mappedBy = "horario",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @Getter
    private final List<HorarioIntervalo> intervalos = new ArrayList<>();

    public Horario() {}

    public Long getId() { return id; }

    public void setSegunda(LocalTime inicio, LocalTime fim) { adicionarHorario(DayOfWeek.MONDAY, inicio, fim); }
    public List<HorarioIntervalo> getSegunda() { return filtrarPorDia(DayOfWeek.MONDAY); }

    public void setTerca(LocalTime inicio, LocalTime fim) { adicionarHorario(DayOfWeek.TUESDAY, inicio, fim); }
    public List<HorarioIntervalo> getTerca() { return filtrarPorDia(DayOfWeek.TUESDAY); }

    public void setQuarta(LocalTime inicio, LocalTime fim) { adicionarHorario(DayOfWeek.WEDNESDAY, inicio, fim); }
    public List<HorarioIntervalo> getQuarta() { return filtrarPorDia(DayOfWeek.WEDNESDAY); }

    public void setQuinta(LocalTime inicio, LocalTime fim) { adicionarHorario(DayOfWeek.THURSDAY, inicio, fim); }
    public List<HorarioIntervalo> getQuinta() { return filtrarPorDia(DayOfWeek.THURSDAY); }

    public void setSexta(LocalTime inicio, LocalTime fim) { adicionarHorario(DayOfWeek.FRIDAY, inicio, fim); }
    public List<HorarioIntervalo> getSexta() { return filtrarPorDia(DayOfWeek.FRIDAY); }

    public void setSabado(LocalTime inicio, LocalTime fim) { adicionarHorario(DayOfWeek.SATURDAY, inicio, fim); }
    public List<HorarioIntervalo> getSabado() { return filtrarPorDia(DayOfWeek.SATURDAY); }

    public void setDomingo(LocalTime inicio, LocalTime fim) { adicionarHorario(DayOfWeek.SUNDAY, inicio, fim); }
    public List<HorarioIntervalo> getDomingo() { return filtrarPorDia(DayOfWeek.SUNDAY); }

    private List<HorarioIntervalo> filtrarPorDia(DayOfWeek dia) {
        return intervalos.stream()
                .filter(i -> i.getDiaSemana() == dia)
                .collect(Collectors.toList());
    }

    private void adicionarHorario(DayOfWeek dia, LocalTime inicio, LocalTime fim) {
        if (dia == null || inicio == null || fim == null || !inicio.isBefore(fim)) {
            throw new BadRequestException("O horário deve ter dia, início e fim válidos.");
        }

        List<HorarioIntervalo> horariosDoDia = filtrarPorDia(dia);

        for (HorarioIntervalo existente : horariosDoDia) {
            boolean colide = inicio.isBefore(existente.getFim()) && fim.isAfter(existente.getInicio());

            if (colide) {
                throw new BadRequestException("O horário informado colide com um horário já existente neste dia.");
            }
        }

        HorarioIntervalo intervalo = new HorarioIntervalo(dia, inicio, fim);
        intervalo.setHorario(this);

        this.intervalos.add(intervalo);
    }

    public void adicionar(DayOfWeek dia, LocalTime inicio, LocalTime fim) {
        this.adicionarHorario(dia, inicio, fim);
    }

}
