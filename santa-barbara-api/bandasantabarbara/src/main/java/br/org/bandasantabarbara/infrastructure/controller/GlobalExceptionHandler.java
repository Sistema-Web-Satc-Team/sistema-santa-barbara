package br.org.bandasantabarbara.infrastructure.controller;

import br.org.bandasantabarbara.application.dtos.DefaultMessageResponse;
import br.org.bandasantabarbara.application.dtos.DomainExceptionResponse;
import br.org.bandasantabarbara.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
    public ResponseEntity<DefaultMessageResponse> handleNotFoundResource(org.springframework.web.servlet.NoHandlerFoundException ex) {
        log.warn("Rota não encontrada: {}", ex.getRequestURL());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new DefaultMessageResponse("Recurso inexistente.")
        );
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<DomainExceptionResponse> handleDomainException(DomainException ex) {
        log.warn("Exceção de domínio capturada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new DomainExceptionResponse(ex.getMessage(), ex.getErrors())
        );
    }

    @ExceptionHandler(NaoEncontradoException.class)
    public ResponseEntity<DefaultMessageResponse> handleNaoEncontradoException(NaoEncontradoException ex) {
        log.warn("Não encontrado capturado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new DefaultMessageResponse(ex.getMessage())
        );
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<DefaultMessageResponse> handleConflitoException(ConflitoException ex) {
        log.warn("Exceção de conflito capturada: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new DefaultMessageResponse(ex.getMessage())
        );
    }

    @ExceptionHandler(ExpiradoException.class)
    public ResponseEntity<DefaultMessageResponse> handleExpiradoException(ExpiradoException ex) {
        log.warn("Exceção de expiração capturada: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.GONE).body(
                new DefaultMessageResponse(ex.getMessage())
        );
    }


    @ExceptionHandler(InvalidoException.class)
    public ResponseEntity<DefaultMessageResponse> handleInvalidoException(InvalidoException ex) {
        log.warn("Exceção inválida capturada: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new DefaultMessageResponse(ex.getMessage())
        );
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<DefaultMessageResponse> handleApplicationException(ApplicationException ex) {
        log.warn("Erro de aplicação capturado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new DefaultMessageResponse("Ocorreu um erro inesperado.")
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DefaultMessageResponse> handleGenericException(Exception ex) {
        log.error("Ocorreu um erro não esperado no sistema:", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new DefaultMessageResponse("Ocorreu um erro interno no servidor.")
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleEmptyBody(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(Map.of("erro", "O corpo da requisição (JSON) é obrigatório e não pode estar vazio."));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> handleDTOException(MethodArgumentNotValidException ex) {
        List<FieldError> erros = ex.getFieldErrors();

        List<DadosErroValidacao> resposta = erros.stream()
                .map(DadosErroValidacao::new)
                .toList();

        return ResponseEntity.badRequest().body(resposta);
    }

    public record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }

}