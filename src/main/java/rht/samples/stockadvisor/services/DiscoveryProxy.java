package rht.samples.stockadvisor.services;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.watson.discovery.v1.Discovery;
import com.ibm.watson.discovery.v1.model.QueryOptions;
import com.ibm.watson.discovery.v1.model.QueryResponse;
import com.ibm.watson.discovery.v1.model.QueryResult;

@Configuration
public class DiscoveryProxy {

  private static Logger logger = Logger.getLogger(DiscoveryProxy.class.getName());

  @Autowired
  Discovery discoveryService;

  @Value("${watson.discovery.environment_id}")
  String environmentID;

  @Value("${watson.discovery.collection_id}")
  String collectionID;

  @Value("${watson.discovery.result_count}")
  String resultCount;

  /*
   * Query Result Properties:
   * 
   *   [main_image_url, country, extracted_metadata, author, 
   *    crawl_date, source_type, language, forum_title, title, url, 
   *    enriched_text, host, publication_date, text, enriched_title]
   */

  public List<Map<String, Object>> query(String ticker) {

    QueryOptions queryOptions = new QueryOptions.Builder()
            .environmentId(environmentID)
            .collectionId(collectionID)
            .query(ticker)
            .count(Long.parseLong(resultCount))
            .build();

    ServiceCall<QueryResponse> serviceCall = discoveryService.query(queryOptions);
    Response<QueryResponse> response = serviceCall.execute();
    List<QueryResult> queryResults = response.getResult().getResults();

    logger.info("query result count: " + queryResults.size());

    return queryResults.stream().map(q -> q.getProperties()).collect(Collectors.toList());
  }

}