package com.example.demo_ecommerce.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "acquisto", schema = "ecommerce")
public class Acquisto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data")
    private Date data;

    @ManyToOne
    @JoinColumn(name = "cliente")
    @JsonIgnore
    private Cliente cliente;

    @OneToMany(mappedBy = "acquisto", cascade = CascadeType.MERGE)
    @JsonIgnore
    @ToString.Exclude
    private List<ProdottoAcquisto> prodottoAcquistoList;
    /* Acquisto ha una relazione uno-a-molti con la tabella prodotto_acquisto per memorizzare tutti i
     * prodotti relativi ad un acquisto
     */

}
