package rht.samples.stockadvisor;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;

@SpringBootApplication
public class Main {

  private static Logger logger = Logger.getLogger(Main.class.getName());

  @Value("${spring.application.name}")
  private String applicationName;


	public static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}


  @EventListener
  public void handleApplicationEvent(ApplicationReadyEvent evt) {
  
    logger.info(String.format("[ApplicationReady] \"%s\" is ready for e-business!", applicationName));
  }

  @Bean
  public CompanyIndex loadCompanyIndex() {
    return new CompanyIndex();
  }
}
