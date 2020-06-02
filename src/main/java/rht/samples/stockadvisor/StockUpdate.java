package rht.samples.stockadvisor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import rht.samples.stockadvisor.models.ArticleReference;
import rht.samples.stockadvisor.models.CompanyStockDatum;

@Component
public class StockUpdate {

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
            .map(r -> parseArticle(r))
            .collect(Collectors.toList());
    
    stockDatum.setArticles(articles);
  }

  private ArticleReference parseArticle(Map<String, Object> properties) {

//    Map<String,Object> article = new HashMap<>();
//    
//    article.put("url", properties.get("url").toString());
//    article.put("date", properties.get("crawl_date").toString());
//    article.put("title", properties.get("title").toString());
//    article.put("source", properties.get("forum_title").toString());
//    
//    Map<String, Object> enrichedText = (Map<String, Object>)properties.get("enriched_text");
//    Map<String, Object> sentiment = (Map<String, Object>)enrichedText.get("sentiment");
//    Map<String, Object> document = (Map<String, Object>)sentiment.get("document");
//    
//    article.put("sentiment", document.get("label").toString());
//
//    List<Map<String, Object>> cats = (List<Map<String, Object>>)enrichedText.get("categories");
//    List<String> shortLabels = cats.stream()
//      .map(x -> parseCategory(x))
//      .collect(Collectors.toList());
//    article.put("categories", shortLabels); 
//
//    return article;

    ArticleReference articleReference = new ArticleReference();

    articleReference.setUrl(properties.get("url").toString());
    articleReference.setDate(properties.get("crawl_date").toString());
    articleReference.setTitle(properties.get("title").toString());
    articleReference.setSource(properties.get("forum_title").toString());

    Map<String, Object> enrichedText = (Map<String, Object>) properties.get("enriched_text");
    Map<String, Object> sentiment = (Map<String, Object>) enrichedText.get("sentiment");
    Map<String, Object> document = (Map<String, Object>) sentiment.get("document");

    articleReference.setSentiment(document.get("label").toString());

    List<Map<String, Object>> cats = (List<Map<String, Object>>) enrichedText.get("categories");
    List<String> shortLabels = cats.stream()
            .map(x -> parseCategory(x)).collect(Collectors.toList());
    articleReference.setCategories(shortLabels);

    return articleReference;

  }
  
  private String parseCategory(Map<String, Object> category) {
    
    String[] parts = ((String)category.get("label")).split("/");
    return parts[parts.length-1];
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
  }
  
  public void sortArticles() {}
  public void articleContains() {}
  public void getImageForArticle() {}
  public void getImages() {}
  public void insertNewData() {}
  public void parseResults() {}
  public void findTickerForCompanyWithName() {}




  /**
   * Parse a single discovery result for the relevant data
   * @param {discoveryResult} result
   * @returns {object} - a simplified JSON object with relevant data
  private void parseArticle() {
    function parseArticle(result) {
      
      var parseCategories = function(cats) {

        var categories = [];
        if (!cats || cats.length == 0) {
          return categories;
        }    
        
        for (var i=0; i<cats.length; i++) {
          var rawLabel = cats[i].label;
          var lastSlashIndex = rawLabel.lastIndexOf('/');
          var category = lastSlashIndex == -1 ? rawLabel : rawLabel.substring(lastSlashIndex + 1);
          categories.push(category);
        }

        return categories;
      };
      
      return {
        url: result.url,
        sentiment: result.enriched_text.sentiment.document.label,
        categories: parseCategories(result.enriched_text.categories),
        date: result.crawl_date,
        title: result.title,
        source: result.forum_title
      };
    }
  }
   */
  
}
