package br.imd.ufrn.egide.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "report_response")
@SQLRestriction(value = "active = true")
public class ReportResponseEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String responseText;

    private String aiSuggestion;

    private Boolean usedAiSuggestion;

    private LocalDateTime respondedAt;

    @OneToOne
    @JoinColumn(name = "report_id", unique = true)
    private ReportEntity report;
}
