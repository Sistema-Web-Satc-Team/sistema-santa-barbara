package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoRequest;
import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoResponse;
import br.org.bandasantabarbara.application.services.musica.InstrumentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/instrumentos")
@Tag(name = "Instrumentos", description = "Cadastro e consulta de instrumentos musicais.")
public class InstrumentoController {

    private final InstrumentoService instrumentoService;

    public InstrumentoController(InstrumentoService instrumentoService) {
        this.instrumentoService = instrumentoService;
    }



    @PostMapping
    @Operation(summary = "Cadastrar instrumento", description = "Cadastra um instrumento musical.")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public InstrumentoResponse criarInstrumento(
            @RequestBody InstrumentoRequest body
    ) {
        return this.instrumentoService.cadastrarInstrumento(body);
    }

    @GetMapping
    @Operation(summary = "Listar instrumentos", description = "Lista os instrumentos cadastrados.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO', 'ALUNO', 'PROFESSOR', 'SECRETARIO', 'MUSICO_BANDA', 'RESPONSAVEL')")
    public List<InstrumentoResponse> listarInstrumentos(

    ) {
        return this.instrumentoService.listarInstrumentos();
    }


    @GetMapping("/{id}")
    @Operation(summary = "Consultar instrumento", description = "Consulta um instrumento pelo identificador.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO', 'ALUNO', 'PROFESSOR', 'SECRETARIO', 'MUSICO_BANDA', 'RESPONSAVEL')")
    public InstrumentoResponse pegarInstrumentoPorId(
        @PathVariable int id
    ) {
        return this.instrumentoService.pegarPorId(id);
    }



    @PatchMapping("/{id}")
    @Operation(summary = "Atualizar instrumento", description = "Atualiza os dados de um instrumento.")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public InstrumentoResponse atualizarInstrumentos(
            @PathVariable int id,
            @RequestBody InstrumentoRequest body
    ) {
        return this.instrumentoService.atualizarInstrumento(id, body);
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "Remover instrumento", description = "Remove um instrumento pelo identificador.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'MAESTRO')")
    public void removerInstrumentos(
            @PathVariable int id
    ) {
        this.instrumentoService.removerInstrumento(id);
    }







}
