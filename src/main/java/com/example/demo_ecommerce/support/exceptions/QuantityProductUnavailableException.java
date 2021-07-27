package com.example.demo_ecommerce.support.exceptions;


import com.example.demo_ecommerce.entities.Prodotto;

public class QuantityProductUnavailableException extends Exception {
    private Prodotto prodotto;

    public QuantityProductUnavailableException(Prodotto prodotto) {
        this.prodotto = prodotto;
    }

    public Prodotto getProdotto() {
        return prodotto;
    }
}
