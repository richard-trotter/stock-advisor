package rht.samples.stockadvisor;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import rht.samples.stockadvisor.services.AlphavantageProxy;
import rht.samples.stockadvisor.services.CloudantProxy;
import rht.samples.stockadvisor.services.DiscoveryProxy;

@RestController("utilitiesController")
@RequestMapping(value = "/util")
public class UtilityService {

  @Autowired
  AlphavantageProxy alphavantageProxy;

  @Autowired
  CloudantProxy cloudantProxy;

  @Autowired
  DiscoveryProxy discoveryProxy;
  
  @Autowired 
  CompanyIndex companyIndex;
  

  @GetMapping("/alphav/daily/{ticker}")
  @ResponseBody
  String getDaily(@PathVariable(value = "ticker") String ticker, @RequestParam(defaultValue = "json") String datatype) {
    return alphavantageProxy.getDailyData(ticker, datatype);
  }

  @GetMapping("/discovery/{companyName}")
  @ResponseBody
  List<Map<String,Object>> getArticleDataForCompany(@PathVariable(value = "companyName") String companyName) {        

    return discoveryProxy.query(companyName);
  }

  @GetMapping("/db")
  @ResponseBody
  List<String> getCloudantDbs() {
    return cloudantProxy.getDbList();
  }

  @PostMapping("/db/delete")
  void dropDatabase() {
    
      cloudantProxy.dropDatabase();
  }

  @GetMapping("/index")
  @ResponseBody
  Map<String,String> getCompanyIndex() {
    return companyIndex.getTickerMap();

  }

}
