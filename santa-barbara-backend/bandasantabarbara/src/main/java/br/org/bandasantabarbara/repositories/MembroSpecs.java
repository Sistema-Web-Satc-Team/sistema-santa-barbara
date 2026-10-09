package br.org.bandasantabarbara.repositories;

import br.org.bandasantabarbara.model.Membro;
import br.org.bandasantabarbara.model.MembroVinculo;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;



public class MembroSpecs {

    public static Specification<Membro> comVinculoAtivo(Boolean ativo) {
        return (root, query, cb) -> {
            if (ativo == null) return null;

            Subquery<Long> sub = query.subquery(Long.class);
            Root<MembroVinculo> vinculo = sub.from(MembroVinculo.class);

            sub.select(cb.literal(1L)).where(
                    cb.equal(vinculo.get("membro"), root),
                    cb.or(
                            cb.isNull(vinculo.get("dataTermino")),
                            cb.greaterThanOrEqualTo(vinculo.<LocalDate>get("dataTermino"), LocalDate.now())
                    )
            );

            return ativo ? cb.exists(sub) : cb.not(cb.exists(sub));
        };
    }

    public static Specification<Membro> porNomeOuEmail(String termo) {
        return (root, query, cb) -> {
            if (termo == null || termo.isBlank()) return null;

            String pattern = "%" + termo.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("nome")), pattern),
                    cb.like(cb.lower(root.get("email")), pattern)
            );
        };
    }
}