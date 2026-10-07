package br.org.bandasantabarbara.controller;

import br.org.bandasantabarbara.application.dtos.DtoInspector;
import br.org.bandasantabarbara.application.dtos.MessageResponse;
import br.org.bandasantabarbara.application.dtos.exception.BadRequestExceptionDTO;
import br.org.bandasantabarbara.application.dtos.exception.EnumBadRequestExceptionDTO;
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

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
        log.warn("BadRequest capturado: {}", ex.getMessage());
        if (ex.getErrors() != null && ex.getErrors().size() > 1) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BadRequestExceptionDTO(
                    ex.getMessage(),
                    ex.getErrors()
            ));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(EnumBadRequestException.class)
    public ResponseEntity<Object> handleEnumBadRequest(EnumBadRequestException ex) {
        log.warn("EnumBadRequest capturado: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new EnumBadRequestExceptionDTO(
                ex.getMessage(),
                ex.getValidValues()
        ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MessageResponse> handleNotFound(NotFoundException ex) {
        log.warn("NotFound capturado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<MessageResponse> handleConflict(ConflictException ex) {
        log.warn("Conflict capturado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(ExpiredException.class)
    public ResponseEntity<MessageResponse> handleExpired(ExpiredException ex) {
        log.warn("Exceção de expiração capturada: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.GONE).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<MessageResponse> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unauthorized capturado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<MessageResponse> handleForbidden(ForbiddenException ex) {
        log.warn("Forbidden capturado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosErroValidacao>> handleValidation(MethodArgumentNotValidException ex) {
        List<DadosErroValidacao> erros = ex.getFieldErrors().stream()
                .map(DadosErroValidacao::new)
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erros);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("Erro de leitura do JSON: {}", ex.getMessage());

        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof EnumBadRequestException enumEx) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new EnumBadRequestExceptionDTO(
                    enumEx.getMessage(),
                    enumEx.getValidValues()
            ));
        }

        if (cause instanceof BadRequestException badRequestEx) {
            if (badRequestEx.getErrors() != null && !badRequestEx.getErrors().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(badRequestEx.getErrors());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(badRequestEx.getMessage()));
        }

        List<DtoInspector.CampoContrato> contrato = DtoInspector.extrairContrato(br.org.bandasantabarbara.application.dtos.setup.RegistrarAdminRequest.class);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(contrato);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponse> handleGeneric(Exception ex) {
        log.error("Erro interno não tratado:", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Ocorreu um erro interno no servidor."));
    }

    public record DadosErroValidacao(String campo, String mensagem) {
        public DadosErroValidacao(FieldError erro) {
            this(erro.getField(), erro.getDefaultMessage());
        }
    }
}