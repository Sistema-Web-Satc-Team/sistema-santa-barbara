package br.org.bandasantabarbara.application.dtos.papel;

import java.util.Set;

public record AtribuirPermissaoRequest(String papel, Set<String> permissoes)  {
}
