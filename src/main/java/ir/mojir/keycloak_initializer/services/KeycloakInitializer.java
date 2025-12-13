package ir.mojir.keycloak_initializer.services;

import java.io.File;
import java.util.Optional;

import ir.mojir.keycloak_initializer.config.Parameters;
import ir.mojir.my_kc_auth_client.dtos.*;
import ir.mojir.spring_boot_commons.exceptions.InternalErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ir.mojir.keycloak_initializer.config.Configuration;
import ir.mojir.keycloak_initializer.config.Configuration.ClientConfig;

import ir.mojir.my_kc_auth_client.config.KeycloakConfiguration;
import ir.mojir.my_kc_auth_client.external.KeycloakClient;
import jakarta.annotation.PostConstruct;

@Component
public class KeycloakInitializer {

	private static final Logger logger = LoggerFactory.getLogger(KeycloakInitializer.class);
	
//	private static final String CONFIG_FILE_NAME = "config.json";
	
//	private static final String RESULT_FILE_NAME = "result.json";
	
	private String adminAccessToken = null;
	
	private Configuration config;
	
	private InitializationResult result = new InitializationResult();
	
	@Autowired
	private KeycloakConfiguration kcConfig;
	
	@Autowired
	private KeycloakClient keycloakClient;

	@Autowired
	private Parameters params;

	@PostConstruct
	public void initialize() {
		loadConfiguration();
		getAdminAccessToken();
		createRealm();
		createClients();
		createUsers();
		createMasterAdminUser();
		saveResult();
	}

	private void createUsers() {
		if(config.getUsers() == null)
			return;
		for(Configuration.UserConfig userConfig: config.getUsers()) {
			createUser(userConfig);
		}
	}

	private void createUser(Configuration.UserConfig userConfig) {
		if(isUserExists(userConfig.getUsername())) {
			logger.info("username {}  already exists. So no need for creation...", userConfig.getUsername());
			return;
		}
		KcCreateUserReq req = new KcCreateUserReq();
		req.setUsername(userConfig.getUsername());
		req.setEnabled(true);
		String userId = keycloakClient.createUser(kcConfig.getKcRealm(), req, adminAccessToken);
		logger.info("User with username {} was created on keycloak.", userConfig.getUsername());

		KcResetPasswordReq resetReq = new KcResetPasswordReq();
		resetReq.setTemporary(false);
		resetReq.setType("password");
		resetReq.setValue(userConfig.getPassword());
		keycloakClient.resetAdminPassword(kcConfig.getKcRealm(), userId, resetReq, adminAccessToken);
		logger.info("Password was successfully set on user {}.", userConfig.getUsername());

		if(userConfig.getClientRoles() != null) {
			for (Configuration.UserConfig.ClientRole clientRole : userConfig.getClientRoles()) {
				assignClientRoleToUser(userId, clientRole.getClientId(), clientRole.getRoleName());
			}
		}
//		assignClientRoleToUser(userId, UserRole.ADMIN);
//		assignClientRoleToUser(userId, UserRole.HELP_DESK);
	}

	private boolean isUserExists(String username) {
		return isUserExists(config.getRealmName(), username);
	}

	private boolean isUserExists(String realmName, String username) {
		KcSearchUserRespRow[] result = keycloakClient.searchUsers(username, realmName, adminAccessToken);
		if(result == null || result.length == 0)
			return false;
		return true;
	}

	private void assignClientRoleToUser(String userId, String clientId, String role) {
		logger.info("Trying to assign role {} from client {} to user with id {}", role, clientId, userId);
		KcGetAvailableClientRolesForUserRespRow[] availableRoles = keycloakClient.getAvailableClientRolesForUser(
				userId, adminAccessToken, role
		);
		if(availableRoles == null || availableRoles.length == 0)
			throw new InternalErrorException("Failed to find <"+role+"> client role in order to assign to user", null);
		KcAssignRoleToUserReqRow assignRoleReqRow = null;
		for(KcGetAvailableClientRolesForUserRespRow row: availableRoles) {
			if(row.getRole().equals(role) && row.getClient().equals(clientId)) {
				assignRoleReqRow = new KcAssignRoleToUserReqRow();
				assignRoleReqRow.setId(row.getId());
				assignRoleReqRow.setName(row.getRole());
				break;
			}
		}
		if(assignRoleReqRow == null)
			throw new InternalErrorException(String.format("Failed to find role %s in client %s from AvailableClientRolesForUser", role, clientId), null);
		KcAssignRoleToUserReqRow[] assignRoleReq = { assignRoleReqRow };
		keycloakClient.assignClientRoleToUser(userId, findClientUuid(clientId), assignRoleReq, adminAccessToken);
		logger.info("Role {} from client {} was successfully assigned to user {}", role, clientId, userId);
	}

	private String findClientUuid(String clientId) {
		Optional<InitializationResult.ClientCreationResult> clientInfo =
				result.getCreatedClients().stream().filter(item -> item.getClientId().equals(clientId)).findFirst();
		if(clientInfo.isEmpty())
			throw new InternalErrorException("Faile to find client id " + clientId + " from created clients list", null);
		return clientInfo.get().getClientUuid();
	}

	private void saveResult() {
		try {
			new ObjectMapper().writeValue(new File(params.getResultFilePath()), result);
		} catch(Exception e) {
			throw new RuntimeException("Failed to write result to file " + params.getResultFilePath(), e);
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
	        InitializationResult.ClientCreationResult clientCreationResult = new InitializationResult.ClientCreationResult();
			clientCreationResult.setClientId(clientConf.getClientId());
	        clientCreationResult.setClientUuid(client.getId());
	        clientCreationResult.setClientSecret(clientSecret);
	        result.getCreatedClients().add(clientCreationResult);

			assignServiceAccountRolesToClient(client.getId(), clientConf.getServiceAccountRoles());
			createClientRoles(client.getId(), clientConf.getClientRoles());
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
			config = new ObjectMapper().readValue(new File(params.getConfigurationFilePath()), Configuration.class);
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

	private void assignServiceAccountRolesToClient(String clientUuid, String[] roles) {
		if(roles == null)
			return;
		for(String role: roles) {
			assignServiceAccountRoleToClient(clientUuid, role);
		}
	}

	private void assignServiceAccountRoleToClient(String clientUuid, String roleName) {
		logger.trace("trying to assign role {} to client {}", roleName, clientUuid);
		String clientServiceAccountUserId = getClientServiceAccountUserId(clientUuid);
		KcGetAvailableClientRolesForUserRespRow[] viewUsersRoleInfo = keycloakClient.getAvailableClientRolesForUser(
				clientServiceAccountUserId, adminAccessToken, roleName);
		if(viewUsersRoleInfo != null && viewUsersRoleInfo.length == 0)
		{
			logger.trace(roleName + " role is already assigned to client");
			return;
		}
		if(viewUsersRoleInfo == null || viewUsersRoleInfo.length != 1)
			throw new InternalErrorException("Failed to get information of "+roleName+" role from keycloak", null);
		KcAssignRoleToUserReqRow row = new KcAssignRoleToUserReqRow();
		row.setId(viewUsersRoleInfo[0].getId());
		row.setName(viewUsersRoleInfo[0].getRole());
		KcAssignRoleToUserReqRow[] req = { row };

		keycloakClient.assignClientRoleToUser(clientServiceAccountUserId, viewUsersRoleInfo[0].getClientId(), req, adminAccessToken);
		logger.info("role {} was successfully assigned to client", roleName);
	}

	private String getClientServiceAccountUserId(String clientUuid) {
		KcGetServiceAccountUserIdResp resp = keycloakClient.getServiceAccountUserId(clientUuid, adminAccessToken);
		return resp.getId();
	}

	private void createClientRoles(String clientUuid, String[] roleNames) {
		if(roleNames == null)
			return;
		logger.trace("Trying to create client roles for client with id {} if not exists", clientUuid);
		KcSearchClientRoleRespRow[] clientRoles = keycloakClient.getAllClientRoles(clientUuid, adminAccessToken);
		for(String role: roleNames) {
			if(!roleExists(role, clientRoles)) {
				KcCreateClientRoleReq req = new KcCreateClientRoleReq();
				req.setName(role);
				keycloakClient.createClientRole(clientUuid, req, adminAccessToken);
				logger.info("Role with name {} created for client {}", role, clientUuid);
			}
		}
	}

	private boolean roleExists(String role, KcSearchClientRoleRespRow[] clientRoles) {
		for(KcSearchClientRoleRespRow kcRole: clientRoles) {
			if(kcRole.getName().equals(role))
				return true;
		}
		return false;
	}

	private void createMasterAdminUser() {
		if(isUserExists("master", config.getMasterAdminUsername())) {
			logger.info("Master admin user already exists. So continue...");
			return;
		}
		KcCreateUserReq req = new KcCreateUserReq();
		req.setUsername(config.getMasterAdminUsername());
		req.setEnabled(true);
		String userId = keycloakClient.createUser("master", req, adminAccessToken);
		logger.info("Permanent master admin user with username {} was created on keycloak.", config.getMasterAdminUsername());

		KcResetPasswordReq resetReq = new KcResetPasswordReq();
		resetReq.setTemporary(false);
		resetReq.setType("password");
		resetReq.setValue(config.getMasterAdminPassword());
		keycloakClient.resetAdminPassword("master", userId, resetReq, adminAccessToken);
		logger.info("Password was successfully set on permanent master admin user.");

		KcGetAvailableRealmRolesForUserRespRow[] availableRoles = keycloakClient.getAvailableRealmRolesForUser(
				"master", userId, adminAccessToken, "admin"
		);
		if(availableRoles == null || availableRoles.length == 0)
			throw new InternalErrorException("Failed to find admin realm role in order to assign it to master admin", null);
		KcAssignRoleToUserReqRow assignRoleReqRow = new KcAssignRoleToUserReqRow();
		for(KcGetAvailableRealmRolesForUserRespRow row: availableRoles) {
			if(row.getName().equals("admin")) {
				assignRoleReqRow.setId(row.getId());
				assignRoleReqRow.setName(row.getName());
			}
		}
		KcAssignRoleToUserReqRow[] assignRoleReq = { assignRoleReqRow };
		keycloakClient.assignRealmRoleToUser("master", userId, assignRoleReq, adminAccessToken);
		logger.info("Role admin was successfully assigned to permanent master admin user");
	}
	
}
