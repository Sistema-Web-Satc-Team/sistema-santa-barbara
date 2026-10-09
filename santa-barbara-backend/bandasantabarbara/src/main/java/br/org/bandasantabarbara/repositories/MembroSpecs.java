package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroVinculo;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class MembroSpecs {

    public static Specification<Membro> comVinculos(Boolean temVinculo) {
        return (root, query, criteriaBuilder) -> {
            if (temVinculo == null) return null;

            query.distinct(true);
            Join<Membro, MembroVinculo> vinculos = root.join("vinculos", JoinType.LEFT);

            if (temVinculo) {
                return criteriaBuilder.isNotNull(vinculos.get("id"));
            } else {
                return criteriaBuilder.isNull(vinculos.get("id"));
            }
        };
    }

    public static Specification<Membro> comFiltroVinculos(Boolean temVinculo, Boolean ativo) {
        return (root, query, criteriaBuilder) -> {
            if (temVinculo == null && ativo == null) return null;

            query.distinct(true);
            Join<Membro, MembroVinculo> vinculos = root.join("vinculos", JoinType.LEFT);

            jakarta.persistence.criteria.Predicate predicate = criteriaBuilder.conjunction();

            if (temVinculo != null) {
                if (temVinculo) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNotNull(vinculos.get("id")));
                } else {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.isNull(vinculos.get("id")));
                }
            }

            if (ativo != null && (temVinculo == null || temVinculo)) {
                LocalDate hoje = LocalDate.now();
                if (ativo) {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.or(
                            criteriaBuilder.isNull(vinculos.get("dataTermino")),
                            criteriaBuilder.greaterThanOrEqualTo(vinculos.get("dataTermino"), hoje)
                    ));
                } else {
                    predicate = criteriaBuilder.and(predicate, criteriaBuilder.and(
                            criteriaBuilder.isNotNull(vinculos.get("dataTermino")),
                            criteriaBuilder.lessThan(vinculos.get("dataTermino"), hoje)
                    ));
                }
            }

            return predicate;
        };
    }


    public static Specification<Membro> porNomeOuEmail(String termo) {
        return (root, query, criteriaBuilder) -> {
            if (termo == null || termo.isBlank()) return null;
            String pattern = "%" + termo.toLowerCase() + "%";
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("nome")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), pattern)
            );
        };
    }
}