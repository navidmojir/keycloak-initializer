package ir.mojir.keycloak_initializer;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ir.mojir.keycloak_initializer, ir.mojir.my_kc_auth_client.config, ir.mojir.my_kc_auth_client.external")
public class KeycloakInitializerApplication {

	public static void main(String[] args) {
		new SpringApplicationBuilder(KeycloakInitializerApplication.class)
	        .web(WebApplicationType.NONE)   // <-- disables Tomcat
	        .run(args);
		
	}
	
	

}
