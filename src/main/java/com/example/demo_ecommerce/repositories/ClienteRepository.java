package com.example.demo_ecommerce.repositories;

import com.example.demo_ecommerce.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    Cliente findByEmail(String email);

    Cliente findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
