package com.example.demo_ecommerce.repositories;

import com.example.demo_ecommerce.entities.Acquisto;
import com.example.demo_ecommerce.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcquistoRepository extends JpaRepository<Acquisto, Integer> {
    List<Acquisto> findByCliente(Cliente cliente);

    Acquisto findById(int id);

}
