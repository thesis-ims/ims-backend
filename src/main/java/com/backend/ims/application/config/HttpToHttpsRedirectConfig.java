package com.backend.ims.application.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HttpToHttpsRedirectConfig implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

  @Override
  public void customize(ConfigurableWebServerFactory factory) {
    // Configures HTTP port to redirect to HTTPS
    if (factory instanceof TomcatServletWebServerFactory tomcat) {
      tomcat.addAdditionalTomcatConnectors(createHttpToHttpsRedirectConnector());
    }
  }

  private Connector createHttpToHttpsRedirectConnector() {
    // Redirect HTTP (port 8080) to HTTPS (port 443)
    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setPort(8080);
    connector.setSecure(false);
    connector.setRedirectPort(443);  // Redirect to HTTPS
    return connector;
  }
}
