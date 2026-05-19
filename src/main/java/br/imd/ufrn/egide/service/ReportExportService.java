package br.imd.ufrn.egide.service;

import br.imd.ufrn.egide.entity.ReportEntity;
import br.imd.ufrn.egide.repository.ReportRepository;
import br.imd.ufrn.egide.utils.exception.ResourceNotFoundException;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportExportService {

    private final ReportRepository reportRepository;
    private final ReportService reportService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public byte[] generateReportPdf(Long id) {
        ReportEntity report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manifestação não encontrada"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            Paragraph title = new Paragraph("RELATÓRIO DE MANIFESTAÇÃO - EGIDE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("PROTOCOLO: " + report.getProtocolNumber(), boldFont));
            document.add(new Paragraph("STATUS ATUAL: " + report.getStatus().name(), normalFont));
            document.add(new Paragraph("TÍTULO: " + report.getTitle(), normalFont));
            document.add(new Paragraph("DESCRIÇÃO: " + report.getDescription(), normalFont));

            if (report.getFinalReport() != null) {
                document.add(new Paragraph(" "));
                document.add(new Paragraph("--- DECISÃO FINAL ---", boldFont));
                document.add(new Paragraph("PENALIDADE: " + report.getFinalReport().getPenaltyType(), normalFont));
                document.add(new Paragraph("PARECER: " + report.getFinalReport().getPenaltyDescription(), normalFont));
            }

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }

        return baos.toByteArray();
    }

    public byte[] generateGovernancePdf() {
        Map<String, Object> stats = reportService.getDashboardStatus();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);
            Font mutedFont = new Font(Font.HELVETICA, 10, Font.ITALIC);

            Paragraph title = new Paragraph("RELATÓRIO DE GOVERNANÇA — EGIDE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph gerado = new Paragraph("Gerado em: " + LocalDateTime.now().format(FORMATTER), mutedFont);
            gerado.setAlignment(Element.ALIGN_CENTER);
            document.add(gerado);
            document.add(new Paragraph(" "));

            document.add(new Paragraph("1. VOLUME DE MANIFESTAÇÕES", sectionFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total registrado:  " + stats.get("total"), boldFont));
            document.add(new Paragraph("Pendentes:         " + stats.get("pendentes"), normalFont));
            document.add(new Paragraph("Analisados:        " + stats.get("analisados"), normalFont));
            document.add(new Paragraph("Rejeitados:        " + stats.get("rejeitados"), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("2. INDICADORES DE SATISFAÇÃO", sectionFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Média de agilidade:  " + stats.get("mediaAgilidade"), normalFont));
            document.add(new Paragraph("Média de resolução:  " + stats.get("mediaResolucao"), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Documento gerado automaticamente pelo sistema EGIDE.", mutedFont));

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF de governança", e);
        }

        return baos.toByteArray();
    }
}