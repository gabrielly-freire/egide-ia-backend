package br.imd.ufrn.egide.dto;

public record FileDTO(
    Long id,

    String name,

    String contentType,

    Long size
) {
}
