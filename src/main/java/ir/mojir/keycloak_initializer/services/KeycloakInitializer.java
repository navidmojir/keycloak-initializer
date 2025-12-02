package ir.mojir.keycloak_initializer.services;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ir.mojir.keycloak_initializer.config.Configuration;
import ir.mojir.keycloak_initializer.config.Configuration.ClientConfig;
import ir.mojir.keycloak_initializer.dtos.InitializationResult;
import ir.mojir.keycloak_initializer.dtos.InitializationResult.ClientCreationResult;
import ir.mojir.my_kc_auth_client.config.KeycloakConfiguration;
import ir.mojir.my_kc_auth_client.dtos.KcCreateClientReq;
import ir.mojir.my_kc_auth_client.dtos.KcCreateRealmReq;
import ir.mojir.my_kc_auth_client.dtos.KcGetClientResp;
import ir.mojir.my_kc_auth_client.external.KeycloakClient;
import jakarta.annotation.PostConstruct;

@Component
public class KeycloakInitializer {

	private static final Logger logger = LoggerFactory.getLogger(KeycloakInitializer.class);
	
	private static final String CONFIG_FILE_NAME = "config.json";
	
	private static final String RESULT_FILE_NAME = "result.json";
	
	private String adminAccessToken = null;
	
	private Configuration config;
	
	private InitializationResult result = new InitializationResult();
	
	@Autowired
	private KeycloakConfiguration kcConfig;
	
	@Autowired
	private KeycloakClient keycloakClient;

	@PostConstruct
	public void initialize() {
		loadConfiguration();
		getAdminAccessToken();
		createRealm();
		createClients();
		
		saveResult();
	}
	
	private void saveResult() {
		try {
			new ObjectMapper().writeValue(new File(RESULT_FILE_NAME), result);
		} catch(Exception e) {
			throw new RuntimeException("Failed to write result to file " + RESULT_FILE_NAME, e);
		}
		
	}

	private void createClients() {
		for(ClientConfig clientConf: config.getClients()) {
			KcCreateClientReq req = new KcCreateClientReq();
	        req.setClientId(clientConf.getClientId());
	        req.setEnabled(true);
	        req.setPublicClient(clientConf.isPublicClient());
	        req.setProtocol("openid-connect");
	        req.setServiceAccountsEnabled(clientConf.isServiceAccountsEnabled());
	        req.setDirectAccessGrantsEnabled(clientConf.isDirectAccessGrantsEnabled());
	        req.setStandardFlowEnabled(clientConf.isStandardFlowEnabled());
	        req.setAuthorizationServicesEnabled(clientConf.isAuthorizationServicesEnabled());
	        req.setRootUrl(clientConf.getRootUrl());
	        req.setRedirectUris(clientConf.getRedirectUris());
	        req.setWebOrigins(clientConf.getWebOrigins());
	        createClient(req);

	        KcGetClientResp client = keycloakClient.getClient(clientConf.getClientId(), adminAccessToken);
	        String clientSecret = keycloakClient.fetchClientSecret(client.getId(), adminAccessToken);
	        ClientCreationResult clientCreationResult = new ClientCreationResult();
	        clientCreationResult.setClientId(clientConf.getClientId());
	        clientCreationResult.setClientUuid(client.getId());
	        clientCreationResult.setClientSecret(clientSecret);
	        result.getCreatedClients().add(clientCreationResult);
		}
		
		
	}
	
	private void createClient(KcCreateClientReq req) {

        if(isClientExists(req.getClientId())) {
            logger.trace("Client with clientId {} exists. So no need for creation...", req.getClientId());
            return;
        }
        logger.trace("Trying to create client with clientId {}", req.getClientId());
        keycloakClient.createClient(req, adminAccessToken);
        logger.info("Client with clientId {} was created successfully", req.getClientId());
    }
	
	private boolean isClientExists(String clientId) {
        logger.trace("Checking if client with clientId {} exists", clientId);
        return keycloakClient.isClientExists(clientId, adminAccessToken);
    }

	private void loadConfiguration() {
		try {
			config = new ObjectMapper().readValue(new File(CONFIG_FILE_NAME), Configuration.class);
			kcConfig.setKcRealm(config.getRealmName());
			kcConfig.setAuthServerUrl(config.getAuthServerUrl());
			
		} catch(Exception e) {
			throw new RuntimeException("Failed to load configuration from config.json", e);
		}
		
	}
	
	private void getAdminAccessToken() {
		adminAccessToken = keycloakClient.getAdminAccessToken(
                    config.getTmpAdminUsername(), config.getTmpAdminPassword());
    }
	
	private void createRealm() {
        if(isRealmExists()) {
            logger.trace("Realm exists. So no need for creation...");
            return;
        }
        logger.trace("Trying to create realm {}", config.getRealmName());

        KcCreateRealmReq req = new KcCreateRealmReq();
        req.setRealm(config.getRealmName());
        req.setEnabled(true);
        keycloakClient.createRealm(req, adminAccessToken);

        logger.info("Realm {} created successfully", config.getRealmName());
    }
	
	private boolean isRealmExists() {
        logger.trace("Checking if realm {} exists", config.getRealmName());
        return keycloakClient.isRealmExists(config.getRealmName(), adminAccessToken);
    }
	
	
}
