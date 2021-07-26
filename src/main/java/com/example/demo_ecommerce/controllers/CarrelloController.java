package com.example.demo_ecommerce.controllers;

import com.example.demo_ecommerce.entities.CarrelloProdotto;
import com.example.demo_ecommerce.entities.Cliente;
import com.example.demo_ecommerce.entities.Prodotto;
import com.example.demo_ecommerce.repositories.ClienteRepository;
import com.example.demo_ecommerce.services.CarrelloService;
import com.example.demo_ecommerce.services.ProdottoService;
import com.example.demo_ecommerce.support.ResponseMessage;
import com.example.demo_ecommerce.support.exceptions.AggiornamentoFallitoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;


@RestController
@RequestMapping("/carrello")
public class CarrelloController {

    @Autowired
    private CarrelloService carrelloService;
    @Autowired
    private ProdottoService prodottoService;
    @Autowired
    private ClienteRepository clienteRepository;


    @GetMapping("/prodotti")
    public ResponseEntity getProdottiNelCarrelloPerCliente(@RequestParam(value = "username", defaultValue = "") String username) {
        Cliente c = clienteRepository.findByUsername(username);
        if (c == null) {
            c = clienteRepository.findByEmail(username);
        }
        //System.out.println("cliente " + c);
        List<CarrelloProdotto> list = carrelloService.getProdotti(c);
        List<Prodotto> prodotti = new LinkedList<>();
        for (CarrelloProdotto cp : list) {
            prodotti.add(cp.getProdotto());
        }
        if (prodotti.size() == 0) {
            return new ResponseEntity<>(prodotti, HttpStatus.NO_CONTENT);
        }
        //System.out.println(prodotti);
        return new ResponseEntity<>(prodotti, HttpStatus.OK);
    }

    @PostMapping("/aggiorna")
    public ResponseEntity aggiornaPrdodottoNelCarrello(@RequestParam(value = "email", defaultValue = "") String emailCliente, @RequestParam(value = "id", defaultValue = "") String id, @RequestParam(value = "quantita", defaultValue = "0") String quantita) {
        try {
            Prodotto prodotto = prodottoService.mostraProdottoPerId(Integer.parseInt(id));
            Cliente cliente = clienteRepository.findByEmail(emailCliente);
            if (cliente == null) cliente = clienteRepository.findByUsername(emailCliente);
            carrelloService.aggiorna(cliente, prodotto, Integer.parseInt(quantita));
        } catch (AggiornamentoFallitoException e) {
            return new ResponseEntity<>(new ResponseMessage("Aggiornamento fallito"), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseMessage("Aggiornamento avvenuto con successo"), HttpStatus.OK);
    }

    @GetMapping("/quantita")
    public ResponseEntity getQuantitaProdotto(@RequestParam(value = "username", defaultValue = "") String username, @RequestParam(value = "id", defaultValue = "") String id) {
        Cliente c = clienteRepository.findByUsername(username);
        if (c == null) {
            c = clienteRepository.findByEmail(username);
        }
        List<CarrelloProdotto> list = carrelloService.getProdotti(c);
        int quantita = 0;
        for (CarrelloProdotto cp : list) {
            if (cp.getProdotto().getId() == Integer.parseInt(id)) {
                quantita = cp.getQuantita();
                break;
            }
        }
        if (quantita == 0) {
            return new ResponseEntity<>(new ResponseMessage("Il prodotto non è presente nel carrello"), HttpStatus.OK);
        }
        return new ResponseEntity<>(String.valueOf(quantita), HttpStatus.OK);
    }
}
