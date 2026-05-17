package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.entity.FileEntity;
import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.repository.FileRepository;
import br.imd.ufrn.egide.utils.exception.BusinessException;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final String UPLOAD_DIR = "uploads/";

    private final FileRepository fileRepository;

    @Override
    public void upload(List<MultipartFile> files, ReportEntity report) {
        for (MultipartFile file : files) {
            validateFile(file);

            try {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

                Path path = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(path.getParent());
                Files.write(path, file.getBytes());

                FileEntity entity = new FileEntity();
                entity.setName(fileName);
                entity.setPath(path.toString());
                entity.setContentType(file.getContentType());
                entity.setSize(file.getSize());
                entity.setReport(report);

                fileRepository.save(entity);

            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar arquivo");
            }
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Arquivo vazio", HttpStatus.BAD_REQUEST);
        }

        if (file.getSize() > 20_000_000) {
            throw new BusinessException("Arquivo muito grande", HttpStatus.BAD_REQUEST);
        }

        String contentType = file.getContentType();

        if (!List.of("image/png", "image/jpeg", "application/pdf")
                .contains(contentType)) {
            throw new BusinessException("Tipo de arquivo inválido", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
    }

    @Override
    public FileEntity findById(Long id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado"));
    }

    @Override
    public List<FileEntity> findAllByReportId(Long reportId) {
        return fileRepository.findAllByReportId(reportId);
    }

    @Override
    public Resource findResourceById(Long id) {
        FileEntity file = findById(id);

        try {
            Path path = Paths.get(file.getPath());
            UrlResource resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new ResourceNotFoundException("Arquivo não encontrado no disco");
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao carregar arquivo");
        }
    }

}