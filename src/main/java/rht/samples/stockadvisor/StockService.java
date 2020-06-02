package rht.samples.stockadvisor;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import rht.samples.stockadvisor.models.ArticleReference;
import rht.samples.stockadvisor.models.CompanyStockDatum;

@RestController("advisorController")
@RequestMapping(value = "/advisor")
public class StockService {

  @Autowired
  AlphavantageProxy alphavantageProxy;

  @Autowired
  CloudantProxy cloudantProxy;

  @Autowired
  DiscoveryProxy discoveryProxy;
  
  @Autowired
  StockUpdate stockUpdate;


  @GetMapping("/daily/{ticker}")
  @ResponseBody
  String getDaily(@PathVariable(value = "ticker") String ticker, @RequestParam(defaultValue = "json") String datatype) {
    return alphavantageProxy.getDailyData(ticker, datatype);
  }

  @GetMapping("/cloudant/db")
  @ResponseBody
  List<String> getCloudantDbs() {
    return cloudantProxy.getDbList();
  }

  @PostMapping("/company")
  void addCompany(@RequestParam String companyName) {
    
    List<String>companies = new ArrayList<String>();
    companies.add(companyName);
    
    stockUpdate.runUpdate(companies);
  }

  @GetMapping("/stockInfo")
  @ResponseBody
  CompanyStockDatum getStockDatum(@RequestParam String companyName) {
    return cloudantProxy.getDatabase().find(CompanyStockDatum.class, companyName);
  }


//  @GetMapping("/discovery/{ticker}")
//  @ResponseBody
//  List<Map<String, Object>> getArticleDataForCompany(@PathVariable(value = "ticker") String ticker) {        
//    List<Map<String, Object>> queryResults = discoveryProxy.query(ticker);
//    
//    return queryResults;
//  }

  @GetMapping("/articles")
  @ResponseBody
  List<ArticleReference> getArticleDataForCompany(@RequestParam String company) {        

    CompanyStockDatum stockDatum = new CompanyStockDatum(company);
    stockUpdate.getArticleDataForCompany(stockDatum);
    return stockDatum.getArticles();
  }

  /**
   * Add a company
   * 
   *   POST /api/companies/add
   *     stockService.addCompany(companyName)
   */

  /**
   * Retrieve the stock data for company by name
   * 
   *   GET /api/stocks
   *     servicesStockService.getStockByCompanyName(companyName)
   */


}
