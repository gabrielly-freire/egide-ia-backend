package br.imd.ufrn.egide.domain;

import lombok.Data;

@Data
public class File {
    private Long id;
    private String name;
    private String path;
    private String contentType;
    private Long size;
}
