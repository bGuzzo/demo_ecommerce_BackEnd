package com.example.demo_ecommerce.repositories;

import com.example.demo_ecommerce.entities.Prodotto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdottoRepository extends JpaRepository<Prodotto, Integer> {
    Page<Prodotto> findByNomeContainingOrDescrizioneContaining(String ricerca1, String ricerca2, Pageable paging);

    Prodotto findById(int id);
}
