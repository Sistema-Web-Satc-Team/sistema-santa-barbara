package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoRequest;
import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoResponse;
import br.org.bandasantabarbara.application.services.instrumentos.InstrumentoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/instrumentos")
public class InstrumentoController {

    private final InstrumentoService instrumentoService;

    public InstrumentoController(InstrumentoService instrumentoService) {
        this.instrumentoService = instrumentoService;
    }



    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public InstrumentoResponse criarInstrumento(
            @RequestBody InstrumentoRequest body
    ) {
        return this.instrumentoService.cadastrarInstrumento(body);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO', 'ALUNO', 'PROFESSOR', 'SECRETARIO', 'MUSICO_BANDA', 'RESPONSAVEL')")
    public List<InstrumentoResponse> listarInstrumentos(

    ) {
        return this.instrumentoService.listarInstrumentos();
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO', 'ALUNO', 'PROFESSOR', 'SECRETARIO', 'MUSICO_BANDA', 'RESPONSAVEL')")
    public InstrumentoResponse pegarInstrumentoPorId(
        @PathVariable int id
    ) {
        return this.instrumentoService.pegarPorId(id);
    }



    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public InstrumentoResponse atualizarInstrumentos(
            @PathVariable int id,
            @RequestBody InstrumentoRequest body
    ) {
        return this.instrumentoService.atualizarInstrumento(id, body);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public void removerInstrumentos(
            @PathVariable int id
    ) {
        this.instrumentoService.removerInstrumento(id);
    }







}
