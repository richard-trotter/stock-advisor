Watson Stock Advisor backend server

see: https://github.com/IBM/watson-stock-advisor/blob/master/README.md

This is a Spring Boot implementation of the subject "backend server". 

To retrieve company stock price and news data and add to the Cloudant database:

``` bash
curl -w "\nSTATUS: %{http_code}\n" -X POST 'http://localhost:8080/advisor/company?companyName=International+Business+Machines'
```
To retrieve company data from the Cloudant database:

``` bash
curl -s 'http://localhost:8080/advisor/stockInfo?companyName=International+Business+Machines'
```
Example JSON payload:

``` bash
{
    "name": "International Business Machines",
    "ticker": "IBM"
    "articles": [
        {
            "categories": [
                "day trading",
                "stocks",
                "computer"
            ],
            "date": "2020-04-23T00:12:32Z",
            "sentiment": "positive",
            "source": "(no title)",
            "title": "IBM Stock News - Technical Buy The Dip Bullish Momentum Trade and Trigger With Options",
            "url": "https://www.cmlviz.com/research.php?number=16315&cml_article_id=20191104_ibm-stock-news--technical-buy-the-dip-bullish-momentum-trade-and-trigger-with-options"
        },
        {
            "categories": [
                "investing",
                "beginning investing",
                "computer reviews"
            ],
            "date": "2020-06-01T15:55:01Z",
            "sentiment": "positive",
            "source": "Banking",
            "title": "IBM (NYSE:IBM) Earns Daily News Impact Rating of 0.22",
            "url": "https://www.tickerreport.com/banking-finance/5679355/ibm-nyseibm-earns-daily-news-impact-rating-of-0-22.html"
        }
    ],
    "priceHistory": {
        "Meta Data": {
            "1. Information": "Daily Time Series with Splits and Dividend Events",
            "2. Symbol": "IBM",
            "3. Last Refreshed": "2020-06-01",
            "4. Output Size": "Compact",
            "5. Time Zone": "US/Eastern"
        },
        "Time Series (Daily)": {
            "2020-01-08": {
                "1. open": "134.5100",
                "2. high": "135.8600",
                "3. low": "133.9200",
                "4. close": "135.3100",
                "5. adjusted close": "132.1196",
                "6. volume": "4345952",
                "7. dividend amount": "0.0000",
                "8. split coefficient": "1.0000"
            },
            "2020-01-09": {
                "1. open": "135.7400",
                "2. high": "136.7900",
                "3. low": "135.3100",
                "4. close": "136.7400",
                "5. adjusted close": "133.5159",
                "6. volume": "3730549",
                "7. dividend amount": "0.0000",
                "8. split coefficient": "1.0000"
            }
        }
    }
}
```