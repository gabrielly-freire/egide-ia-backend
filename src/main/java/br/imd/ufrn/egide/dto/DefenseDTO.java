package br.imd.ufrn.egide.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DefenseDTO(
        Long reportId,
        String defenseText,
        LocalDateTime submittedAt,
        Long submittedByUserId,
        List<FileDTO> files
) {
}
