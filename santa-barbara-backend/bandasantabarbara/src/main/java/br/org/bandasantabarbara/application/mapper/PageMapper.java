package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;


@Component
public class PageMapper {

    public <T> PageResponse<T> toResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

}
