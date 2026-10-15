package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoRequest;
import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoResponse;
import br.org.bandasantabarbara.application.dtos.membros.FuncaoResponse;
import br.org.bandasantabarbara.model.Funcao;
import br.org.bandasantabarbara.model.Instrumento;
import org.springframework.stereotype.Component;

@Component
public class InstrumentoMapper {

    public InstrumentoResponse toResponse(Instrumento instrumento) {

        return new InstrumentoResponse(
          instrumento.getId(),
          instrumento.getNome(),
          instrumento.getDescricao(),
          "",
          instrumento.getTipo()
        );
    }

    public Instrumento toEntity(InstrumentoRequest request) {
        Instrumento instrumento = new Instrumento(request.nome(), request.tipo());

        if (request.descricao() != null) {
            instrumento.setDescricao(request.descricao());
        }

        return instrumento;
    }

    public void toUpdate(InstrumentoRequest request, Instrumento instrumento) {

        if (request.nome() != null) {
            instrumento.setNome(request.nome());
        }

        if (request.tipo() != null) {
            instrumento.setTipo(request.tipo());
        }

        if (request.descricao() != null) {
            instrumento.setDescricao(request.descricao());
        }


    }
}
