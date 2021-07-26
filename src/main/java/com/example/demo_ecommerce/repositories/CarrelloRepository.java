package com.example.demo_ecommerce.repositories;

import com.example.demo_ecommerce.entities.Carrello;
import com.example.demo_ecommerce.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrelloRepository extends JpaRepository<Carrello, Integer> {
    Carrello findByCliente(Cliente cliente);
}
