package com.example.demo_ecommerce.controllers;

import com.example.demo_ecommerce.entities.Cliente;
import com.example.demo_ecommerce.services.ClienteService;
import com.example.demo_ecommerce.support.ResponseMessage;
import com.example.demo_ecommerce.support.exceptions.MailUserAlreadyExistsException;
import com.example.demo_ecommerce.support.exceptions.UsernameAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/clienti")
public class AccountingController {

    @Autowired
    private ClienteService clienteService;

    @PostMapping()
    public ResponseEntity aggiungiCliente(@RequestBody @Valid Cliente cliente, @RequestParam(value = "password") String password) {
        try {
            Cliente aggiunto = clienteService.registraCliente(cliente, cliente.getUsername(), password);
            return new ResponseEntity(aggiunto, HttpStatus.OK);
        } catch (MailUserAlreadyExistsException e) {
            return new ResponseEntity<>(new ResponseMessage("ERROR_MAIL_USER_ALREADY_EXISTS"), HttpStatus.BAD_REQUEST);
        } catch (UsernameAlreadyExistsException e) {
            return new ResponseEntity<>(new ResponseMessage("ERROR_USERNAME_ALREADY_EXISTS"), HttpStatus.BAD_REQUEST);
        }

    }

}
