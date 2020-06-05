package rht.samples.stockadvisor;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Main {

  private static Logger logger = Logger.getLogger(Main.class.getName());


	public static void main(String[] args) {
	  
	  Map<String,String> env = System.getenv();
	  Set<String> keys = new TreeSet<>(env.keySet());
	  for( String k : keys )
	    System.out.println("[env] " + k + " = " + env.get(k));
	  
		SpringApplication.run(Main.class, args);
	}


  @EventListener
  public void handleApplicationEvent(ApplicationReadyEvent evt) {
  
    String pfx = "[ApplicationReady] ";
    
    Environment env = evt.getApplicationContext().getEnvironment();
    
    String[] plist = {
        "alphavantage.baseurl",
        "cloudant.url",
        "watson.discovery.url",
    };

    for( String pname : plist ) 
      logger.info(pfx+pname+": "+env.getProperty(pname));


    logger.info(pfx + "\"Stock-Advisor\" is ready for e-business!");
  }

  @Bean
  public CompanyIndex loadCompanyIndex() {
    return new CompanyIndex();
  }
}
