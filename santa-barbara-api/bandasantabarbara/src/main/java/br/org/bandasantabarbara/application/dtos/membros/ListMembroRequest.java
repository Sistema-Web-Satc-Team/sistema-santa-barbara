package br.org.bandasantabarbara.application.dtos.membros;

import br.org.bandasantabarbara.application.dtos.OffsetPaginationRequest;
import br.org.bandasantabarbara.application.filters.MembroFilter;

public record ListMembroRequest(
        OffsetPaginationRequest pagination,
        MembroFilter filter
) {}
