package com.example.demo_ecommerce.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "prodotto", schema = "ecommerce")
public class Prodotto {
    public static final int LUNGHEZZA_NOME = 50, LUNGHEZZA_DESCRIZIONE = 500;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "nome")
    private String nome;

    @Basic
    @Column(name = "bar_code", unique = true)
    private String barCode;

    @Basic
    @Column(name = "descrizione")
    private String descrizione;

    @Basic
    @Column(name = "prezzo")
    private float prezzo;

    @Basic
    @Column(name = "quantita")
    private int quantita;

    @Version
    @Column(name = "version", nullable = false)
    @JsonIgnore
    private long version;

    @Basic
    @Column(name = "categoria")
    private String categoria;


}
