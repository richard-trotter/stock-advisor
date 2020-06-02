package rht.samples.stockadvisor;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;

import rht.samples.stockadvisor.models.CompanyStockDatum;

@Configuration
public class CloudantProxy {

  @Value("${cloudant.url}")
  private String cloudantUrl;
  
  @Value("${cloudant.apikey}")
  private String cloudantApiKey;
  
  @Value("${cloudant.db_name}")
  private String dbName;
  
  private CloudantClient cloudantClient = null;
  
  private Database db = null;
  
  private CloudantClient getClient() {
    
    if( cloudantClient != null )
            return cloudantClient;    
    
    /*
     * see: https://javadoc.io/static/com.cloudant/cloudant-client/2.19.0/overview-summary.html#IAM%20authentication
     */
    try {
      cloudantClient = ClientBuilder.url(new URL(cloudantUrl))
              .iamApiKey(cloudantApiKey)
              .build();
      return cloudantClient;
    }
    catch (MalformedURLException e) {
      throw new RuntimeException(e); 
    }
  }
  
  public List<String>getDbList() {
    return getClient().getAllDbs();
  }
  
  public Database getDatabase() {
    
    if( db != null )
      return db;
    
    // TODO: better db init
    getClient().deleteDB(dbName);
    
    db = getClient().database(dbName, true /*create*/);
    return db;
  }

  public void save(CompanyStockDatum stockDatum) {

    if(getDatabase().contains(stockDatum.getName())) {
      CompanyStockDatum old = getDatabase().find(CompanyStockDatum.class, stockDatum.getName());
      getDatabase().remove(old);
    }

    getDatabase().save(stockDatum);
  }

}
