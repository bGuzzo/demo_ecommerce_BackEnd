package com.example.demo_ecommerce.repositories;

import com.example.demo_ecommerce.entities.Acquisto;
import com.example.demo_ecommerce.entities.ProdottoAcquisto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdottoAcquistoRepository extends JpaRepository<ProdottoAcquisto, Integer> {
    List<ProdottoAcquisto> findByAcquisto(Acquisto acquisto);
}
