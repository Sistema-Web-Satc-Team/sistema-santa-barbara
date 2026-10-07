package br.org.bandasantabarbara.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class DtoInspector {

    public record CampoContrato(String campo, String mensagem, boolean obrigatorio) {}

    public static List<CampoContrato> extrairContrato(Class<?> dtoClass) {
        List<CampoContrato> contrato = new ArrayList<>();
        Field[] fields = dtoClass.getDeclaredFields();

        for (Field field : fields) {
            String nomeCampo = field.getName();
            boolean obrigatorio = false;
            String mensagem = "Campo obrigatório.";

            if (field.isAnnotationPresent(NotNull.class)) {
                obrigatorio = true;
                mensagem = field.getAnnotation(NotNull.class).message();
            } else if (field.isAnnotationPresent(NotBlank.class)) {
                obrigatorio = true;
                mensagem = field.getAnnotation(NotBlank.class).message();
            } else if (field.isAnnotationPresent(NotEmpty.class)) {
                obrigatorio = true;
                mensagem = field.getAnnotation(NotEmpty.class).message();
            }

            contrato.add(new CampoContrato(nomeCampo, mensagem, obrigatorio));
        }

        return contrato;
    }
}