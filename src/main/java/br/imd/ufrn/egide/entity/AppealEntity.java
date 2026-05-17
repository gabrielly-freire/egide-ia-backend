package br.imd.ufrn.egide.entity;

import br.imd.ufrn.egide.enums.AppealStatus;
import br.imd.ufrn.egide.enums.AppellantRole;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;


// Recurso submetido por uma das partes após a validação da OG (Fase 5).
// A restrição UNIQUE (report_id, appellant_role) é enforçada em nível de aplicação no AppealServiceImpl
// para garantir que cada parte (DENUNCIANTE / DENUNCIADO) abra no máximo 1 recurso por caso.
// Regra de merge: quando ambas as partes recorrem, o mesmo novo ouvidor analisa tudo —
// o campo newOuvidor é compartilhado: se um recurso já existe para o caso, o novo recurso
// herda o mesmo ouvidor sorteado no primeiro, e o AppealServiceImpl propaga o vínculo.
// Anti-viés: o novo ouvidor não tem acesso às conclusões anteriores (parecer, defesa, relatório final).
@Data
@Entity
@Table(name = "appeal")
@SQLRestriction(value = "active = true")
public class AppealEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    private ReportEntity report;

    @ManyToOne
    @JoinColumn(name = "appellant_user_id", nullable = false)
    private UserInfoEntity appellant;

    @Enumerated(EnumType.STRING)
    @Column(name = "appellant_role", nullable = false, length = 32)
    private AppellantRole appellantRole;

    @Column(name = "grounds", columnDefinition = "TEXT")
    private String grounds;

    @ManyToOne
    @JoinColumn(name = "new_ouvidor_id")
    private UserInfoEntity newOuvidor;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AppealStatus status;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;
}
