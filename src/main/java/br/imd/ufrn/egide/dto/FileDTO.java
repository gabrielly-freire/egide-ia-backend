package br.imd.ufrn.egide.dto;

// DTO de saída com metadados de arquivo vinculado a uma manifestação.
// Não expõe o caminho físico (path) por razões de segurança; o download é feito via endpoint dedicado.
public record FileDTO(
    Long id,

    String name,

    String contentType,

    Long size
) {
}
