package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoRequest;
import br.org.bandasantabarbara.application.dtos.instrumentos.InstrumentoResponse;
import br.org.bandasantabarbara.application.services.instrumentos.InstrumentoService;
import jakarta.websocket.server.PathParam;
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
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public InstrumentoResponse criarInstrumento(
            @RequestBody InstrumentoRequest body
    ) {
        return this.instrumentoService.cadastrarInstrumento(body);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public List<InstrumentoResponse> listarInstrumentos(

    ) {
        return this.instrumentoService.listarInstrumentos();
    }


    /*@GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public InstrumentoResponse listarInstrumentos(
        @ParamPath int id
    ) {
        return this.instrumentoService.pegarPorId();
    }
    */


    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public InstrumentoResponse atualizarInstrumentos(
            @PathParam("id") int id,
            @RequestBody InstrumentoRequest body
    ) {
        return this.instrumentoService.atualizarInstrumento(id, body);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    public void removerInstrumentos(
            @PathParam("id") int id
    ) {
        this.instrumentoService.removerInstrumento(id);
    }







}

