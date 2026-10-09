package br.org.bandasantabarbara.application.services.instrumentos;

import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoRequest;
import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoResponse;
import br.org.bandasantabarbara.application.mapper.InstrumentoMapper;
import br.org.bandasantabarbara.exception.NotFoundException;
import br.org.bandasantabarbara.repositories.InstrumentoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstrumentoService {

    private final InstrumentoRepository instrumentoRepository;
    private final InstrumentoMapper instrumentoMapper;


    public InstrumentoService(InstrumentoRepository instrumentoRepository, InstrumentoMapper instrumentoMapper) {
        this.instrumentoRepository = instrumentoRepository;
        this.instrumentoMapper = instrumentoMapper;
    }


    public List<InstrumentoResponse> listarInstrumentos() {
        return this.instrumentoRepository.findAll().stream().map(this.instrumentoMapper::toResponse).toList();
    }

    public InstrumentoResponse cadastrarInstrumento(InstrumentoRequest request) {
        return this.instrumentoMapper.toResponse(this.instrumentoRepository.save(
                this.instrumentoMapper.toEntity(request)
        ));
    }

    public InstrumentoResponse atualizarInstrumento(int id, InstrumentoRequest request) {
        var instrumento = this.instrumentoRepository.findById(id).orElseThrow(() -> new NotFoundException("Instrumento não foi encontrado."));

        this.instrumentoMapper.toUpdate(request, instrumento);

        return this.instrumentoMapper.toResponse(instrumento);
    }

    public void removerInstrumento(int id) {
        this.instrumentoRepository.deleteById(id);
    }

}
