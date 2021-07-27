package com.example.demo_ecommerce.services;

import com.example.demo_ecommerce.entities.*;
import com.example.demo_ecommerce.repositories.*;
import com.example.demo_ecommerce.support.AcquistoJson;
import com.example.demo_ecommerce.support.exceptions.QuantityProductUnavailableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class AcquistoService {
    @Autowired
    private AcquistoRepository acquistoRepository;
    @Autowired
    private ProdottoAcquistoRepository prodottoAcquistoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private CarrelloRepository carrelloRepository;
    @Autowired
    private ProdottoRepository prodottoRepository;
    @Autowired
    private CarrelloProdottoRepository carrelloProdottoRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Lock(LockModeType.OPTIMISTIC)
    /* Il lock ottimistico funziona mediante il campo indicato da @Version nell'entità critica.
     * Ogni transazione che legge i dati prende il valore del campo version (che è un intero) e prima che questa
     * transazione faccia un'aggiornamento sull'entità controlla la proprietà version nuovamente.
     * Se il valore è cambiato signifca che un'altra transazione ha fatto commit nell'intervallo di tempo e ha
     * modifcato il valore di version, si genera dunque un OptimisticLockException.
     * Altrimenti, la transazione è finalizzata (commit) e il valore della proprietà version viene incrementato.
     *
     * NB: in contrapposizione al lock ottimistico troviamo il lock pessimistico che implica il blocco
     * delle entità a livello di database. Ogni transazione può ottenere il lock sui dati. Finchè la transazione
     * tiene il lock nessun'altra transazione può leggere, cancellare o fare alcun cambiamento sui dati bloccati (con il lock).
     * Possiamo assumere che il lock pessimistico possa generare deadlock. Tuttavia garantisce maggiore integrità
     * dei dati rispetto al lock ottimistico.
     */
    //TODO: correggere commenti
    public Acquisto aggiungiAcquisto(String emailUser, String idProdotto) throws Exception { /* Per correttezza ritorniamo
    l'acquisto anche se praticamente non lo usiamo perchè il controller degli acquisti ritorna una stringa*/
        Cliente cliente = clienteRepository.findByEmail(emailUser);
        if (cliente == null) {
            cliente = clienteRepository.findByUsername(emailUser);
        }
        Carrello carrello = carrelloRepository.findByCliente(cliente);
        Prodotto prodotto = prodottoRepository.findById(Integer.parseInt(idProdotto));
        CarrelloProdotto carrelloProdotto = carrelloProdottoRepository.findByCarrelloAndProdotto(carrello, prodotto);

        int qta = carrelloProdotto.getQuantita();
        if (carrelloProdotto.getQuantita() > prodotto.getQuantita())
            throw new QuantityProductUnavailableException(prodotto); //se la quantità acquistabile non è disponibile nel DB

        /*  noi possiamo inserire una quantità non disponibile di oggetti
            nel carrello nel caso volessimo fare un acquisto nel futuro */

        //Creo un Acquisto, imposto cliente, data e lo salvo
        Acquisto nuovoAcquisto = new Acquisto();
        nuovoAcquisto.setCliente(cliente);
        nuovoAcquisto.setData(new Date());
        acquistoRepository.saveAndFlush(nuovoAcquisto);

        //Creo il prodottoAcquisto per contenere l'acquisto effettuato
        ProdottoAcquisto prodottoAcquisto = new ProdottoAcquisto();
        prodottoAcquisto.setAcquisto(nuovoAcquisto); //imposto l'acquisto
        prodottoAcquisto.setProdotto(prodotto); //imposto il prodotto
        prodottoAcquisto.setQuantita(carrelloProdotto.getQuantita()); //imposto la quantità  acquistata
        if (nuovoAcquisto.getProdottoAcquistoList() == null) nuovoAcquisto.setProdottoAcquistoList(new LinkedList<>());
        nuovoAcquisto.getProdottoAcquistoList().add(prodottoAcquisto); /*   aggiungo il prodotto alla lista di prodottoAcquisto
        contenuta nell'acquisto */
        prodottoAcquistoRepository.saveAndFlush(prodottoAcquisto);

        //eliminazione dal carrello
        List<CarrelloProdotto> listaCarrelloProdotto = carrello.getProdotti();
        listaCarrelloProdotto.remove(prodottoAcquisto); //elimino il prodotto acquistato dalla lista di CarrelloProdotto
        carrelloRepository.save(carrello);
        carrelloProdottoRepository.delete(carrelloProdotto); //elimino il carrelloProdotto dal carrello

        //aggiorno la quantità
        prodotto.setQuantita(prodotto.getQuantita() - qta);
        prodottoRepository.save(prodotto);

        entityManager.refresh(nuovoAcquisto);
        entityManager.refresh(prodottoAcquisto);
        return nuovoAcquisto;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<AcquistoJson> getAcquistiJsonPerCliente(Cliente c) {
        /*  Usiamo una classe di supporto AcquistoJson per inviare l'acquisto mediante il servizio rest */
        List<AcquistoJson> toRet = new LinkedList<>();
        for (Acquisto a : acquistoRepository.findByCliente(c)) {
            toRet.add(new AcquistoJson(a.getId(), a.getData().toString()));
        }
        return toRet;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public String getDettagliOrdine(int idOrdine) {
        /*
        Creiamo una stringa contenete il nome e la quantità del prodotto acquistato nell'ordine idOrdine.
        Tale stringa la visualizzaziamo nella textbox inerente ai dettagli dell'ordine nella OrderCard.
         */
        try {
            Acquisto a = acquistoRepository.findById(idOrdine);
            List<ProdottoAcquisto> prodottoAcquistoList = prodottoAcquistoRepository.findByAcquisto(a);
            String s = "";
            for (ProdottoAcquisto pa : prodottoAcquistoList) {
                //System.out.println(pa.toString());
                s += pa.getProdotto().getNome() + ", qt: " + pa.getQuantita() + ", " + pa.getProdotto().getPrezzo() * pa.getQuantita() + " €" + "\n";
            }
            return s;
        } catch (Exception e) {
            System.out.println("errore AcquistoService in getDettagliOrdine" + e);
        }
        return "";
    }

}
