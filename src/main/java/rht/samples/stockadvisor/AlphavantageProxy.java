package rht.samples.stockadvisor;

import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AlphavantageProxy {

  private static Logger logger = Logger.getLogger(AlphavantageProxy.class.getName());

  @Value("${alphavantage.baseurl}")
  private String baseUrl;

  @Value("${alphavantage.apikey}")
  private String apiKey;

  private String outputDataSize = "compact";

  public String getDailyData(String ticker) {
    
    return getDailyData(ticker, "json");
  }
  
  public String getDailyData(String ticker, String datatype) {

    RestTemplate restTemplate = new RestTemplate();

    String url = baseUrl + "/query" 
            + "?function=TIME_SERIES_DAILY_ADJUSTED" 
            + "&symbol=" + ticker 
            + "&apikey=" + apiKey
            + "&datatype=" + datatype 
            + "&outputsize=" + outputDataSize;

    logger.info("alphavantage query: " + url);

    ResponseEntity<String> entity = restTemplate.getForEntity(url, String.class);
    if (!HttpStatus.OK.equals(entity.getStatusCode())) {
      throw new RestClientException(entity.getStatusCode().toString());
    }
    
    return entity.getBody();
  }
  
  public void getDataHistoryForTicker(String ticker) {
    
  }
}
