package com.example.demo_ecommerce.controllers;

import com.example.demo_ecommerce.entities.Prodotto;
import com.example.demo_ecommerce.services.ProdottoService;
import com.example.demo_ecommerce.support.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prodotti")
public class ProdottoController {
    @Autowired
    private ProdottoService prodottoService;

    @GetMapping("/cerca")
    public ResponseEntity getProdottiByNomeAndDescrizione(@RequestParam(value = "ricerca", defaultValue = "") String ricerca, @RequestParam(value = "numeroPagina", defaultValue = "0") int numeroPagina, @RequestParam(value = "prodottiPerPagina", defaultValue = "9999") int prodottiPerPagina, @RequestParam(value = "ordinaPer", defaultValue = "id") String sortBy) {
        List<Prodotto> ris = prodottoService.mostraProdottiNomeDescrizione(ricerca, numeroPagina, prodottiPerPagina, sortBy);
        if (ris.size() <= 0) {
            return new ResponseEntity<>(new ResponseMessage("Nessun risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(ris, HttpStatus.OK);
    }//possibile uso del metodo in maniera paginata nel caso vi fossero molti prodotti nel db. In questo caso trascuriamo tale funzione.

}
