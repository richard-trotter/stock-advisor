package rht.samples.stockadvisor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

// TODO: enable case insensitive lookup
public class CompanyIndex {
  
  private static Logger logger = Logger.getLogger(CompanyIndex.class.getName());

  private Map<String,String> tickerIndex = new HashMap<String,String>();
  private Map<String,String> nameIndex = new HashMap<String,String>();
  
  public CompanyIndex() {
    
    String[] dataFiles = new String[] {"/NASDAQ_mapping.txt", "/NYSE_mapping.txt"};
    
    for( String filename : dataFiles ) {
      try (InputStream inputStream = CompanyIndex.class.getResourceAsStream(filename)) {
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        reader.lines()
          .forEach(s -> addToIndices(s));
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return;
      }
    }
    
    logger.info(String.format("Loaded %d index entries from data files", tickerIndex.size()));    
  }
  
  public String getName(String ticker) {
    return tickerIndex.get(ticker);
  }
  
  public String getTicker(String name) {
    return nameIndex.get(name);
  }
  
  private void addToIndices(String s) {
    
    String[] parts = s.split("\\t");
    if( parts.length >= 2 ) {
      tickerIndex.put(parts[0], parts[1]);
      nameIndex.put(parts[1], parts[0]);
    }
  }
}
