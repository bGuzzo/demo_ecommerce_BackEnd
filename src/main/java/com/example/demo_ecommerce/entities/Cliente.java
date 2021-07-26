package com.example.demo_ecommerce.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "cliente", schema = "ecommerce")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Basic
    @Column(name = "nome")
    private String nome;

    @Basic
    @Column(name = "cognome")
    private String cognome;

    @Basic
    @Column(name = "telefono")
    private String telefono;

    @Basic
    @Column(name = "email")
    private String email;

    @Basic
    @Column(name = "indirizzo")
    private String indirizzo;

    @Basic
    @Column(name = "username")
    private String username;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.MERGE) /* Mediante questa relazione uno-a-molti prendiamo
    la lista degli acquisti effettuati dal cliente mediante il campo cliente in acquisto */
    @JsonIgnore
    @ToString.Exclude
    private List<Acquisto> acquisti;

}
