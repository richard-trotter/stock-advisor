package rht.samples.stockadvisor.models;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  private static String parseCategory(Map<String, Object> category) {
    
    String[] parts = ((String)category.get("label")).split("/");
    return parts[parts.length-1];
  }

  public static ArticleReference fromQueryResultProperties(Map<String, Object> properties) {
  
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

}
