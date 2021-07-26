package com.example.demo_ecommerce.services;

import com.example.demo_ecommerce.entities.Carrello;
import com.example.demo_ecommerce.entities.CarrelloProdotto;
import com.example.demo_ecommerce.entities.Cliente;
import com.example.demo_ecommerce.entities.Prodotto;
import com.example.demo_ecommerce.repositories.CarrelloProdottoRepository;
import com.example.demo_ecommerce.repositories.CarrelloRepository;
import com.example.demo_ecommerce.support.exceptions.AggiornamentoFallitoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class CarrelloService {
    @Autowired
    private CarrelloRepository carrelloRepository;
    @Autowired
    private CarrelloProdottoRepository carrelloProdottoRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CarrelloProdotto> getProdotti(Cliente cliente) {
        Carrello car = carrelloRepository.findByCliente(cliente); //carrello associato al cliente
        return car.getProdotti();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void aggiorna(Cliente c, Prodotto p, int q) throws AggiornamentoFallitoException {
        /*  Usiamo questo metodo per aggiungere un prodotto al carrello, modificarne la quantità
         *  oppure eliminarlo (se la quantità q è 0).
         */
        try {
            Carrello car = carrelloRepository.findByCliente(c); //  Predno il carrello del cliente c
            CarrelloProdotto carProd = carrelloProdottoRepository.findByCarrelloAndProdotto(car, p); /*
            prendo il carrelloProdotto associato al cliente c e al prodotto p qual'ora questo ci sia. Se non c'è significa
            che il prodotto non è presente nel carrello e dobbiamo aggiungerlo
            */
            if (carProd == null) { //   non è presente nel carrello, lo aggiungiamo da zero con lq quantità specificata
                CarrelloProdotto cp = new CarrelloProdotto(); //    creo il carrelloProdotto e lo popolo.
                cp.setCarrello(car);
                cp.setQuantita(q);
                cp.setProdotto(p);
                car.getProdotti().add(cp); //   aggiungo il carrello prodotto al carrello del cliente
                CarrelloProdotto ok = carrelloProdottoRepository.save(cp); //   rendo cp persistente
                entityManager.refresh(ok);
            } else { /* se carProd non è nullo significa che lo vogliamo eliminare
                        oppure che ne vogliamo modificare la quantità    */
                if (q == 0) { //    lo eliminiamo
                    car.getProdotti().remove(carProd); //   lo rimuovo dall lista di CarrelloPrdodotto del carrello
                    carrelloProdottoRepository.delete(carProd); // lo rimuovo dalla tabella carrello_prodotto

                } else { //lo aggiorniamo
                    /*
                    la quantità viene incrementata di quanto specificato quando si aggiunge al carrello
                     */
                    carProd.setQuantita(q);
                    CarrelloProdotto ok = carrelloProdottoRepository.saveAndFlush(carProd);
                    entityManager.refresh(ok);
                }
            }
            entityManager.refresh(car);
        } catch (Exception e) {
            throw new AggiornamentoFallitoException();
        }
    }
}
