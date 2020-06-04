package rht.samples.stockadvisor;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rht.samples.stockadvisor.models.ArticleReference;
import rht.samples.stockadvisor.models.CompanyStockDatum;
import rht.samples.stockadvisor.services.AlphavantageProxy;
import rht.samples.stockadvisor.services.CloudantProxy;
import rht.samples.stockadvisor.services.DiscoveryProxy;

@Component
public class StockUpdate {

  // TODO: implement async "update all"
  
  private static final Logger logger = Logger.getLogger(StockUpdate.class.getName());
  
  @Autowired
  DiscoveryProxy discoveryProxy;

  @Autowired
  AlphavantageProxy alphavantageProxy;

  @Autowired
  CloudantProxy cloudantProxy;

  @Autowired 
  CompanyIndex companyIndex;


  public void runUpdate(List<String> companies) {
    
    companies.stream()
      .forEach(name -> runUpdate(new CompanyStockDatum(name)));;
  }
  
  public void runUpdate(CompanyStockDatum stockDatum) {
    
    updateStocksData(stockDatum);
  }

  public void getArticleDataForCompany(CompanyStockDatum stockDatum) {
    
    List<Map<String,Object>> queryResults = discoveryProxy.query(stockDatum.getName());
    
    List<ArticleReference> articles = queryResults.stream()
            .map(r -> ArticleReference.fromQueryResultProperties(r))
            .collect(Collectors.toList());
    
    stockDatum.setArticles(articles);
  }

  public JsonObject getLatestStockPrices(String ticker) {
    
    String jsonString = alphavantageProxy.getDailyData(ticker);
    return new Gson().fromJson(jsonString, JsonObject.class);
  }


  public void updateStocksData(List<CompanyStockDatum> stockData) {

    for( CompanyStockDatum stockDatum : stockData ) {

       updateStocksData(stockDatum);
    }
  }
  
  public void updateStocksData(CompanyStockDatum stockDatum) {

      String ticker = companyIndex.getTicker(stockDatum.getName());
      stockDatum.setTicker(ticker);

      JsonObject newPriceHistory = getLatestStockPrices(stockDatum.getTicker());
      stockDatum.setPriceHistory(newPriceHistory);
      
      getArticleDataForCompany(stockDatum);
      
      cloudantProxy.save(stockDatum);
      
      logger.info(String.format("Updated stock info for [%s]", stockDatum.getName()));
  }
  
  public void sortArticles() {}
  public void articleContains() {}
  public void getImageForArticle() {}
  public void getImages() {}
  public void insertNewData() {}
  public void parseResults() {}
  public void findTickerForCompanyWithName() {}
  
}
