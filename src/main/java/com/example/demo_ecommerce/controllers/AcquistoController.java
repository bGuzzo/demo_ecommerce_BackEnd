package com.example.demo_ecommerce.controllers;

import com.example.demo_ecommerce.entities.Cliente;
import com.example.demo_ecommerce.repositories.ClienteRepository;
import com.example.demo_ecommerce.repositories.ProdottoRepository;
import com.example.demo_ecommerce.services.AcquistoService;
import com.example.demo_ecommerce.support.AcquistoJson;
import com.example.demo_ecommerce.support.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/acquisti")
public class AcquistoController {

    @Autowired
    private AcquistoService acquistoService;
    @Autowired
    private ProdottoRepository prodottoRepository;
    @Autowired
    private ClienteRepository clienteRepository;


    @PostMapping
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity acquista(@RequestParam(value = "email") String emailUser, @RequestParam(value = "id") String idProdotto) {
        try {
            acquistoService.aggiungiAcquisto(emailUser, idProdotto);
            return new ResponseEntity<>("Prododotto/i " + prodottoRepository.findById(Integer.parseInt(idProdotto)).getNome() + " acquistato con successo", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Prodotto/i " + prodottoRepository.findById(Integer.parseInt(idProdotto)).getNome() + " non acquistato/i", HttpStatus.OK);
        }
    }


    @GetMapping("/dettagli")
    public String getDettagliOrdine(@RequestParam(value = "id", defaultValue = "") String idOrdine) {
        try {
            return acquistoService.getDettagliOrdine(Integer.parseInt(idOrdine));
        } catch (Exception e) {
            System.out.println(e);
            return "ERROR";
        }
    }

    @GetMapping("/ordini")
    public ResponseEntity getAcquistiJSONperCliente(@RequestParam(value = "cliente", defaultValue = "") String userName) {
        Cliente c = clienteRepository.findByEmail(userName);
        if (c == null) c = clienteRepository.findByUsername(userName);
        List<AcquistoJson> listaOrdini = acquistoService.getAcquistiJsonPerCliente(c);
        if (listaOrdini.size() <= 0) {
            return new ResponseEntity<>(new ResponseMessage("Nessun risultato!"), HttpStatus.OK);
        }
        return new ResponseEntity<>(listaOrdini, HttpStatus.OK);
    }

}

