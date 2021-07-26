package com.example.demo_ecommerce.entities;

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
@Table(name = "carrello", schema = "ecommerce")

public class Carrello {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @OneToMany(mappedBy = "carrello", cascade = CascadeType.MERGE)
    private List<CarrelloProdotto> prodotti;
    /* Carrello ha una relazione uno-a-molti con carrello_prodotto per memorizzare tutti i prodotti che
     * sono nel carrello. Il carrello nella tabella carrello_prodotto è identificato dal campo carrello che ne
     * contiene l'ID.
     * */

    //@ManyToOne
    @OneToOne //relazione uno-a-uno con cliente
    /* Nel DB è presente il vincolo di chiave esterna carrello(cliente)->cliente(id). */
    @JoinColumn(name = "cliente", unique = true) /* memorizzo in "cliente" il cliente
                                                    identificato dal campo cliente.*/
    private Cliente cliente;

}
