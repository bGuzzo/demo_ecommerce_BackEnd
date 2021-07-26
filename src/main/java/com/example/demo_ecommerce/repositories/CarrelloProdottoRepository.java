package com.example.demo_ecommerce.repositories;

import com.example.demo_ecommerce.entities.Carrello;
import com.example.demo_ecommerce.entities.CarrelloProdotto;
import com.example.demo_ecommerce.entities.Prodotto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarrelloProdottoRepository extends JpaRepository<CarrelloProdotto, Integer> {
    CarrelloProdotto findByCarrelloAndProdotto(Carrello carrello, Prodotto prodotto);
}
