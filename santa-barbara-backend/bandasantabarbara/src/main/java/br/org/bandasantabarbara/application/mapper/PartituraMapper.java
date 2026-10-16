package br.org.bandasantabarbara.application.mapper;

import br.org.bandasantabarbara.application.dtos.partituras.PartituraRequest;

import br.org.bandasantabarbara.application.dtos.partituras.PartituraResponse;
import br.org.bandasantabarbara.model.Musica;
import br.org.bandasantabarbara.model.Partitura;
import org.springframework.stereotype.Component;



@Component
public class PartituraMapper {

    private InstrumentoMapper instrumentoMapper;

    public PartituraMapper(InstrumentoMapper instrumentoMapper) {
        this.instrumentoMapper = instrumentoMapper;
    }


    public Partitura toEntity(PartituraRequest request, Musica musica) {
        var partitura = new Partitura(request.titulo(), musica);

        if (request.arranjador() != null) {
            partitura.setArranjador(request.arranjador());
        }

        return partitura;
    }

    public PartituraResponse toResponse(Partitura partitura, String partituraUrl) {
        return new PartituraResponse(
                partitura.getId(),
                partitura.getTitulo(),
                partitura.getArranjador(),
                partituraUrl,
                partitura.getInstrumentos().stream().map(this.instrumentoMapper::toResponse).toList()
        );
    }


}
