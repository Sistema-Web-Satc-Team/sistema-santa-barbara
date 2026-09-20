package br.org.bandasantabarbara.application.filters;

import br.org.bandasantabarbara.model.Papel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class MembroFilter{
    @Getter
    private List<String> papeis;

    @Getter
    @Setter
    private String nome;

    public MembroFilter() {}


    public void setPapeis(List<String> papeis) {
        this.papeis = papeis
                .stream()
                .map(String::toUpperCase)
                .toList();
    }
}
