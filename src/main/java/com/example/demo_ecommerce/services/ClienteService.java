package com.example.demo_ecommerce.services;

import com.example.demo_ecommerce.entities.Carrello;
import com.example.demo_ecommerce.entities.Cliente;
import com.example.demo_ecommerce.repositories.CarrelloRepository;
import com.example.demo_ecommerce.repositories.ClienteRepository;
import com.example.demo_ecommerce.support.exceptions.MailUserAlreadyExistsException;
import com.example.demo_ecommerce.support.exceptions.UsernameAlreadyExistsException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

@Service
public class ClienteService {
    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CarrelloRepository carrelloRepository;

    @Autowired
    private EntityManager entityManager;

    @Value("${username_admin}")
    String username_admin;
    @Value("${password_admin}")
    String password_admin;
    @Value("${nome_client}")
    String nome_client;
    @Value("${ruolo}")
    String ruolo;
    @Value("${serverUrl}")
    String serverUrl;
    @Value("${realm}")
    String realm;
    @Value("${clientId}")
    String clientId;


    @Transactional(readOnly = false, propagation = Propagation.REQUIRED) //required è già di default
    public Cliente registraCliente(Cliente cliente, String username, String password) throws MailUserAlreadyExistsException, UsernameAlreadyExistsException {
        if (clienteRepository.existsByEmail(cliente.getEmail())) {
            throw new MailUserAlreadyExistsException();
        }
        if (clienteRepository.existsByUsername(cliente.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }
        registraKeycloak(cliente, username, password);
        Cliente aggiunto = clienteRepository.save(cliente);
        Carrello carrello = new Carrello();
        carrello.setCliente(aggiunto);
        carrello.setProdotti(new ArrayList<>());
        carrelloRepository.saveAndFlush(carrello);
        entityManager.refresh(carrello);
        return aggiunto;
    }

    private Cliente registraKeycloak(Cliente cliente, String username, String password) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .username(username_admin)
                .password(password_admin)
                .build();
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(cliente.getUsername());
        user.setFirstName(cliente.getNome());
        user.setLastName(cliente.getCognome());
        user.setEmail(cliente.getEmail());
        user.setAttributes(Collections.singletonMap("origin", Arrays.asList("demo")));
        RealmResource realmResource = keycloak.realm(realm);
        UsersResource usersResource = realmResource.users();
        Response response = usersResource.create(user);
        //System.out.printf("Repsonse: %s %s%n", response.getStatus(), response.getStatusInfo());
        //System.out.println(response.getLocation());
        String userId = CreatedResponseUtil.getCreatedId(response);
        //System.out.printf("User created with userId: %s%n", userId);
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        UserResource userResource = usersResource.get(userId);
        userResource.resetPassword(passwordCred);
        ClientRepresentation app1Client = realmResource.clients().findByClientId(clientId).get(0);
        //System.out.println(app1Client.getRegistrationAccessToken());
        RoleRepresentation userClientRole = realmResource.clients().get(app1Client.getId()).roles().get(ruolo).toRepresentation();
        userResource.roles().clientLevel(app1Client.getId()).add(Arrays.asList(userClientRole));
        TokenManager tokenManager = keycloak.tokenManager();
        //System.out.println("token ="+tokenManager.getAccessTokenString());

        return cliente;
    }

}
