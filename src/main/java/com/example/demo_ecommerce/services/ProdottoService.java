package com.example.demo_ecommerce.services;

import com.example.demo_ecommerce.entities.Prodotto;
import com.example.demo_ecommerce.repositories.ProdottoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class ProdottoService {

    @Autowired
    ProdottoRepository prodottoRepository;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Prodotto> mostraProdottiNomeDescrizione(String ricerca, int numeroPagina, int dimensionePagina, String sortBy) {
        Pageable paging = PageRequest.of(numeroPagina, dimensionePagina, Sort.by(sortBy));
        Page<Prodotto> pagedResult = prodottoRepository.findByNomeContainingOrDescrizioneContaining(ricerca, ricerca, paging);
        if (pagedResult.hasContent()) {
            return pagedResult.getContent();
        } else {
            return new ArrayList<>(); /*    Se non trovo nulla ritorno una lista vuota in modo che non venga visualizzato nulla
                                            nella schermata di ricerca*/
        }
    }

    @Transactional(readOnly = true)
    public Prodotto mostraProdottoPerId(int id) {
        if (!prodottoRepository.existsById(id)) {
            return null;
        }
        return prodottoRepository.findById(id);
    }
}
