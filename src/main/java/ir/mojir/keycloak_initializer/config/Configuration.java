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

		private String[] serviceAccountRoles;

		private String[] clientRoles;

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

		public String[] getServiceAccountRoles() {
			return serviceAccountRoles;
		}

		public void setServiceAccountRoles(String[] serviceAccountRoles) {
			this.serviceAccountRoles = serviceAccountRoles;
		}

		public String[] getClientRoles() {
			return clientRoles;
		}

		public void setClientRoles(String[] clientRoles) {
			this.clientRoles = clientRoles;
		}
	}

	public static class UserConfig {

		public static class ClientRole {
			private String roleName;
			private String clientId;

			public String getRoleName() {
				return roleName;
			}

			public void setRoleName(String roleName) {
				this.roleName = roleName;
			}

			public String getClientId() {
				return clientId;
			}

			public void setClientId(String clientId) {
				this.clientId = clientId;
			}
		}
		private String username;
		private String password;

		private ClientRole[] clientRoles;

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public ClientRole[] getClientRoles() {
			return clientRoles;
		}

		public void setClientRoles(ClientRole[] clientRoles) {
			this.clientRoles = clientRoles;
		}
	}
	private String realmName;
	
	private String authServerUrl;
	
	private String tmpAdminUsername;
	    
	private String tmpAdminPassword;

	private String masterAdminUsername;

	private String masterAdminPassword;
	
	private List<ClientConfig> clients;

	private List<UserConfig> users;


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

	public List<UserConfig> getUsers() {
		return users;
	}

	public void setUsers(List<UserConfig> users) {
		this.users = users;
	}

	public String getMasterAdminUsername() {
		return masterAdminUsername;
	}

	public void setMasterAdminUsername(String masterAdminUsername) {
		this.masterAdminUsername = masterAdminUsername;
	}

	public String getMasterAdminPassword() {
		return masterAdminPassword;
	}

	public void setMasterAdminPassword(String masterAdminPassword) {
		this.masterAdminPassword = masterAdminPassword;
	}
}
