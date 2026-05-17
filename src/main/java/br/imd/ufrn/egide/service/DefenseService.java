package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.dto.DefenseDTO;
import br.imd.ufrn.egide.dto.DefenseRequestDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DefenseService {
    DefenseDTO submitDefense(Long reportId, DefenseRequestDTO request, List<MultipartFile> files);
    DefenseDTO getDefense(Long reportId);
}
