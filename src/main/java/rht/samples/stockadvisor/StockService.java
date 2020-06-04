package rht.samples.stockadvisor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cloudant.client.api.query.EmptyExpression;
import com.cloudant.client.api.query.QueryBuilder;
import com.cloudant.client.api.query.QueryResult;
import com.cloudant.client.org.lightcouch.NoDocumentException;

import rht.samples.stockadvisor.models.CompanyStockDatum;
import rht.samples.stockadvisor.services.AlphavantageProxy;
import rht.samples.stockadvisor.services.CloudantProxy;
import rht.samples.stockadvisor.services.DiscoveryProxy;

@RestController("apiController")
@RequestMapping(value = "/api")
public class StockService {

  private static final Logger logger = Logger.getLogger(StockService.class.getName());
  
  @Autowired
  AlphavantageProxy alphavantageProxy;

  @Autowired
  CloudantProxy cloudantProxy;

  @Autowired
  DiscoveryProxy discoveryProxy;
  
  @Autowired
  StockUpdate stockUpdate;

  @Autowired 
  CompanyIndex companyIndex;
  

  /**
   * Retrieve the list of all companies for which stock data has been retrieved.
   */
  @GetMapping("/companies")
  @ResponseBody
  List<CompanyStockDatum> getCompanyIndex() {
    
    String query = new QueryBuilder(EmptyExpression.empty())
            .fields("ticker", "name")
            .build();
    
    QueryResult<CompanyStockDatum> result = cloudantProxy.getDatabase()
            .query(query, CompanyStockDatum.class);
    
    return result.getDocs();
  }

  /**
   * Add a company
   */
  @PostMapping("/companies/add")
  void addCompany(@RequestParam String companyName) {
    
    List<String>companies = new ArrayList<String>();
    companies.add(companyName);
    
    stockUpdate.runUpdate(companies);
    
    logger.info(String.format("Added [%s] to stock data", companyName));
  }

  /**
   * Delete a company by name
   */
  @PostMapping("/companies/delete")
  void deleteCompany(@RequestParam String companyName) {
    
    if(cloudantProxy.getDatabase().contains(companyName)) {
      CompanyStockDatum old = cloudantProxy.getDatabase().find(CompanyStockDatum.class, companyName);
      cloudantProxy.getDatabase().remove(old);
      
      logger.info(String.format("Removed [%s] from stock data", companyName));
    }
  }


  /**
   * Retrieve the stock data
   * 
   * TODO: should this be implemented? 
   * 
  @GetMapping("/stocks")
  @ResponseBody
  CompanyStockDatum getStockDatum(@PathVariable(value = "companyName") String companyName) {
    return cloudantProxy.getDatabase().find(CompanyStockDatum.class, companyName);
  }
   */
  
  /**
   * Retrieve the stock data for company by name
   */
  @GetMapping("/stocks")
  @ResponseBody
  CompanyStockDatum getStockDatum(@RequestParam String companyName) {
      return cloudantProxy.getDatabase().find(CompanyStockDatum.class, companyName);
  }
  
  @ExceptionHandler({ NoDocumentException.class })
  @ResponseStatus(value=HttpStatus.NOT_FOUND, reason="No stock information available. Use \"/api/companies/add\"")
  public void handleException() {
  }

}
