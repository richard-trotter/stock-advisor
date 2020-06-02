package rht.samples.stockadvisor.models;

import java.util.List;

import com.google.gson.JsonObject;

public class CompanyStockDatum {

  String _id;
  String _rev;
  
  String name;
  String ticker;
  JsonObject priceHistory;
  List<ArticleReference> articles;

  public CompanyStockDatum(String name) {
    this._id = name;
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTicker() {
    return ticker;
  }

  public void setTicker(String ticker) {
    this.ticker = ticker;
  }

  public JsonObject getPriceHistory() {
    return priceHistory;
  }

  public void setPriceHistory(JsonObject priceHistory) {
    this.priceHistory = priceHistory;
  }

  public List<ArticleReference> getArticles() {
    return articles;
  }

  public void setArticles(List<ArticleReference> articles) {
    this.articles = articles;
  }

}
