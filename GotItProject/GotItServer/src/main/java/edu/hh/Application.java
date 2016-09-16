package edu.hh;


import java.io.File;

import javax.servlet.MultipartConfigElement;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.MultiPartConfigFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import edu.hh.auth.OAuth2SecurityConfiguration;
import edu.hh.repositories.UsersRepository;

//This annotation tells Spring to auto-wire your application
	@EnableAutoConfiguration
	// This annotation tells Spring to look for controllers, etc.
	// starting in the current package
	@ComponentScan
	//Tell Spring to automatically create a JPA implementation of our
	//repositories for everything
	@EnableJpaRepositories(basePackageClasses = UsersRepository.class)
	//Tell Spring to turn on WebMVC (e.g., it should enable the DispatcherServlet
	//so that requests can be routed to our Controllers)
	@EnableWebMvc
	//We use the @Import annotation to include our OAuth2SecurityConfiguration
	//as part of this configuration so that we can have security and oauth
	//setup by Spring
	@Import(OAuth2SecurityConfiguration.class)
	//This annotation tells Spring that this class contains configuration
	//information
	//for the application.

@Configuration
public class Application{
	private static final int MAX_REQUEST_SIZE = 1024 * 1024;



	// Spring application launcher
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	// This configuration element adds the ability to accept multipart
		// requests to the web container.
		@Bean
	    public MultipartConfigElement multipartConfigElement() {
			// Setup the application container to be accept multipart requests
			final MultiPartConfigFactory factory = new MultiPartConfigFactory();
			// Place upper bounds on the size of the requests to ensure that
			// clients don't abuse the web container by sending huge requests
			factory.setMaxFileSize(MAX_REQUEST_SIZE);
			factory.setMaxRequestSize(MAX_REQUEST_SIZE);

			// Return the configuration to setup multipart in the container
			return factory.createMultipartConfig();
		}
	
	
		
	// This version uses the Tomcat web container and configures it to
	// support HTTPS. The code below performs the configuration of Tomcat
	// for HTTPS. Each web container has a different API for configuring
	// HTTPS.
	//
	// The app now requires that you pass the location of the keystore and
	// the password for your private key that you would like to setup HTTPS
	// with. In Eclipse, you can set these options by going to:
	// 1. Run->Run Configurations
	// 2. Under Java Applications, select your run configuration for this app
	// 3. Open the Arguments tab
	// 4. In VM Arguments, provide the following information to use the
	// default keystore provided with the sample code:
	//
	// -Dkeystore.file=src/main/resources/private/keystore
	// -Dkeystore.pass=changeit
	//
	// 5. Note, this keystore is highly insecure! If you want more security, you
	// should obtain a real SSL certificate:
	//
	// http://tomcat.apache.org/tomcat-7.0-doc/ssl-howto.html
	//
	@Bean
	EmbeddedServletContainerCustomizer containerCustomizer(
			@Value("${keystore.file}") String keystoreFile,
			@Value("${keystore.pass}") final String keystorePass)
			throws Exception {

		
		// This is boiler plate code to setup https on embedded Tomcat
		// with Spring Boot:
		
		final String absoluteKeystoreFile = new File(keystoreFile)
				.getAbsolutePath();

		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
				tomcat.addConnectorCustomizers(new TomcatConnectorCustomizer() {

					@Override
					public void customize(Connector connector) {
						connector.setPort(8443);
						connector.setSecure(true);
						connector.setScheme("https");

						Http11NioProtocol proto = (Http11NioProtocol) connector
								.getProtocolHandler();
						proto.setSSLEnabled(true);
						
						// If you update the keystore, you need to change
						// these parameters to match the keystore that you generate
						proto.setKeystoreFile(absoluteKeystoreFile);
						proto.setKeystorePass(keystorePass);
						proto.setKeystoreType("JKS");
						proto.setKeyAlias("tomcat");

					}
				});
			}

		};
	}
}
