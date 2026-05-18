package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.entity.FileEntity;
import br.imd.ufrn.egide.entity.DefenseEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// Interface de serviço de gerenciamento de arquivos de evidência das manifestações.
public interface FileService {

    // Valida e persiste a lista de arquivos no disco e no banco vinculados à manifestação informada.
    void upload(List<MultipartFile> files, ReportEntity report);

    // Valida e persiste a lista de arquivos no disco e no banco vinculados à defesa.
    void uploadForDefense(List<MultipartFile> files, DefenseEntity defense);

    // Retorna a entidade de arquivo pelo id; lança ResourceNotFoundException se não encontrado.
    FileEntity findById(Long id);

    // Retorna o arquivo como Resource para download/pré-visualização; lança exceção se não existir no disco.
    Resource findResourceById(Long id);

    // Retorna todos os arquivos ativos vinculados à manifestação informada.
    List<FileEntity> findAllByReportId(Long reportId);
}
