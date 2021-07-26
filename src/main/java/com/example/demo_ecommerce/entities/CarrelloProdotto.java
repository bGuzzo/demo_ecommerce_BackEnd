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
@Table(name = "carrello_prodotto", schema = "ecommerce")

public class CarrelloProdotto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Basic
    @Column(name = "quantita")
    private int quantita;

    @ManyToOne /* CarrelloProdotto possiede una relazione molti-a-uno con prodotto poichè molteplici istanze di CarrelloProdotto
    possono fare riferimento ad una singola istanza di prodotto.
    Nel DB è presenta la chiave esterna carrello_prodotto(prodotto)->prodotto(id) */
    @JoinColumn(name = "prodotto") /* tramite il campo prodotto della tabella carrello_prodotto memeorizziamo
    il prodotto associato nella variabile privata prodotto  */
    private Prodotto prodotto;

    @ManyToOne /* relazione molti-a-uno con carrello: più entità di CarrelloProdotto possono fare riferimento
    ad una singola istanza di Carrello. Nel DB è presente la chiave esterna carrello_prodotto(carrello)->carrello(id) */
    @JoinColumn(name = "carrello") //Mappiamo la varibile Carrello sul campo carrello di carrello_prodotto
    @JsonIgnore
    @ToString.Exclude
    private Carrello carrello;
}
