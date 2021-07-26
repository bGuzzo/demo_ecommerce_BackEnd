package com.example.demo_ecommerce.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "prodotto_acquisto", schema = "ecommerce")
public class ProdottoAcquisto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "quantita")
    private int quantita;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "prodotto")
    private Prodotto prodotto;

    @ManyToOne
    @JoinColumn(name = "acquisto")
    private Acquisto acquisto;
}
