package ir.mojir.keycloak_initializer.dtos;

import java.util.ArrayList;
import java.util.List;

public class InitializationResult {

	public static class ClientCreationResult {
		private String clientId;
		private String clientUuid;
		private String clientSecret;
		
		public String getClientId() {
			return clientId;
		}
		public void setClientId(String clientId) {
			this.clientId = clientId;
		}
		public String getClientUuid() {
			return clientUuid;
		}
		public void setClientUuid(String clientUuid) {
			this.clientUuid = clientUuid;
		}
		public String getClientSecret() {
			return clientSecret;
		}
		public void setClientSecret(String clientSecret) {
			this.clientSecret = clientSecret;
		}
		
		
	}
	
	private List<ClientCreationResult> createdClients = new ArrayList<>();

	public List<ClientCreationResult> getCreatedClients() {
		return createdClients;
	}

	public void setCreatedClients(List<ClientCreationResult> createdClients) {
		this.createdClients = createdClients;
	}
	
	
	
}
