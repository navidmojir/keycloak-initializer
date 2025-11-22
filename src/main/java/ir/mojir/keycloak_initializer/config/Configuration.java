package ir.mojir.keycloak_initializer.config;

import java.util.List;

public class Configuration {
	
	public static class ClientConfig {
		private String clientId;
		private boolean publicClient;
		private boolean serviceAccountsEnabled;
		private boolean directAccessGrantsEnabled;
		private boolean standardFlowEnabled;
		private boolean authorizationServicesEnabled;
		private String rootUrl;
		private String[] redirectUris;
		private String [] webOrigins;
		public String getClientId() {
			return clientId;
		}
		public void setClientId(String clientId) {
			this.clientId = clientId;
		}
		public boolean isPublicClient() {
			return publicClient;
		}
		public void setPublicClient(boolean publicClient) {
			this.publicClient = publicClient;
		}
		public boolean isServiceAccountsEnabled() {
			return serviceAccountsEnabled;
		}
		public void setServiceAccountsEnabled(boolean serviceAccountsEnabled) {
			this.serviceAccountsEnabled = serviceAccountsEnabled;
		}
		public boolean isDirectAccessGrantsEnabled() {
			return directAccessGrantsEnabled;
		}
		public void setDirectAccessGrantsEnabled(boolean directAccessGrantsEnabled) {
			this.directAccessGrantsEnabled = directAccessGrantsEnabled;
		}
		public boolean isStandardFlowEnabled() {
			return standardFlowEnabled;
		}
		public void setStandardFlowEnabled(boolean standardFlowEnabled) {
			this.standardFlowEnabled = standardFlowEnabled;
		}
		public boolean isAuthorizationServicesEnabled() {
			return authorizationServicesEnabled;
		}
		public void setAuthorizationServicesEnabled(boolean authorizationServicesEnabled) {
			this.authorizationServicesEnabled = authorizationServicesEnabled;
		}
		public String getRootUrl() {
			return rootUrl;
		}
		public void setRootUrl(String rootUrl) {
			this.rootUrl = rootUrl;
		}
		public String[] getRedirectUris() {
			return redirectUris;
		}
		public void setRedirectUris(String[] redirectUris) {
			this.redirectUris = redirectUris;
		}
		public String[] getWebOrigins() {
			return webOrigins;
		}
		public void setWebOrigins(String[] webOrigins) {
			this.webOrigins = webOrigins;
		}
		
		
	}
	
	private String realmName;
	
	private String authServerUrl;
	
	private String tmpAdminUsername;
	    
	private String tmpAdminPassword;
	
	private List<ClientConfig> clients;


	public String getRealmName() {
		return realmName;
	}


	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}


	public String getTmpAdminUsername() {
		return tmpAdminUsername;
	}


	public void setTmpAdminUsername(String tmpAdminUsername) {
		this.tmpAdminUsername = tmpAdminUsername;
	}


	public String getTmpAdminPassword() {
		return tmpAdminPassword;
	}


	public void setTmpAdminPassword(String tmpAdminPassword) {
		this.tmpAdminPassword = tmpAdminPassword;
	}


	public String getAuthServerUrl() {
		return authServerUrl;
	}


	public void setAuthServerUrl(String authServerUrl) {
		this.authServerUrl = authServerUrl;
	}


	public List<ClientConfig> getClients() {
		return clients;
	}


	public void setClients(List<ClientConfig> clients) {
		this.clients = clients;
	}
	
	
	
	
}
