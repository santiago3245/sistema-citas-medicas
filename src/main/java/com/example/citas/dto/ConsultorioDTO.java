package com.example.citas.dto;

public class ConsultorioDTO {
    private Long id;
    private String numero;
    private Integer piso;

    public ConsultorioDTO(Long id, String numero, Integer piso) {
        this.id = id;
        this.numero = numero;
        this.piso = piso;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }
}
