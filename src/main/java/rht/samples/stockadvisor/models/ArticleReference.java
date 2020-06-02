package rht.samples.stockadvisor.models;

import java.util.List;

public class ArticleReference {

  String url;
  String date;
  String title;
  String source;
  String sentiment;
  List<String> categories;

  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public String getDate() {
    return date;
  }
  public void setDate(String date) {
    this.date = date;
  }
  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getSource() {
    return source;
  }
  public void setSource(String source) {
    this.source = source;
  }
  public String getSentiment() {
    return sentiment;
  }
  public void setSentiment(String sentiment) {
    this.sentiment = sentiment;
  }
  public List<String> getCategories() {
    return categories;
  }
  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

}
