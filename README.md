# Watson Stock Advisor backend server

## Overview 

This is a Spring Boot implementation of the subject "backend server". 

See: 

- IBM Code Pattern: [Create an app to get stock information, prices, and sentiment](https://developer.ibm.com/patterns/create-a-web-app-to-get-stock-information-prices-and-sentiment/?cm_sp=Developer-_-code-_-stock_information)  
- Node.js source: [Watson Stock Advisor](https://github.com/IBM/watson-stock-advisor/blob/master/README.md)

The backend server mediates interactions with 3 external services: [Alphavantage](https://www.alphavantage.co), 
[Cloudant](https://cloud.ibm.com/docs/Cloudant?topic=Cloudant-about), and 
[Watson Discovery Service](https://www.ibm.com/cloud/watson-discovery/resources). 
Service URLs and credentials are specified in `application.properties`. 

To facilitate deployment to Kubernetes, the backend server is implemented as an [Appsody](https://appsody.dev) application. 

## API

There is a single principal REST endpoint - `/api/stocks` - to retrieve the stock price history and news article references for a company by company name. 

Example invocation:

``` bash
curl -s 'http://localhost:8080/api/stockInfo?companyName=International+Business+Machines'
```
Example JSON response payload:

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
            }
        }
    }
}
```

Before this data is available from the API, the `/companies/add` endpoint must be invoked to retrieve the data 
from Alphavantage and Watson Discovery, and add the data to the Cloudant database. 

Example invocation:

``` bash
curl -w "\nSTATUS: %{http_code}\n" -X POST 'http://localhost:8080/api/companies/add?companyName=International+Business+Machines'
```

A listing of known company names, suitable for use with `/companies/add` is available via the `/util/index` endpoint.

## Kubernetes 

### Prerequsites

- [Docker Desktop Kubernetes](https://docs.docker.com/docker-for-mac/kubernetes/)
- [Kubernetes CLI](https://kubernetes.io/docs/tasks/tools/install-kubectl/)
- [Appsody CLI](https://appsody.dev/docs/installing/installing-appsody/)

### Appsody Spring Boot Stack

The backend server (BE) can be deployed to Kubernetes using the Appsody CLI. 
The Appsody CLI was indeed used to kickstart this BE Spring Boot implementation.

``` bash
appsody init incubator/java-spring-boot2
```

This `init` produced a Maven POM specifying the parent POM as the following.

``` bash
  <parent>
    <groupId>dev.appsody</groupId>
    <artifactId>spring-boot2-stack</artifactId>
    <version>[0.3, 0.4)</version>
  </parent>
```

That parent POM specifies a Spring Boot version, and includes the dependency `org.springframework.boot:spring-boot-starter-actuator`.

The `init` also produced some source code for a functional, though minimal, Spring Boot application. In particular, there exists an 
`application.properties` configuration file with the following
[Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-features.html) configuration.

``` bash
management.endpoints.web.exposure.include=health,metrics,prometheus,liveness
```

The `appsody deploy` command generates a Kubernetes resource of "kind" `AppsodyApplication`, and includes specifications for a `readinessProbe` and `livenessProbe`. 
These specifications indicate to Kubernetes a set of REST endpoints supported by the AppsodyApplication that Kubernetes will use to determine "readiness" (ready to receive client requests) and "liveness" (still able to respond to client requests). The generated configuration specifies that the Spring Boot Actuator `/health` and `/liveness` endpoints be used as Kubernetes "readiness" and "liveness" probes.


### Service integration credentials

Integration with external services typically involves the use of authorization credentials. These are usefully managed in a Kubernetes cluster as Kubernetes Secrets. In turn, Spring Cloud Kubernetes (SCK) can be used to access these Secrets in the runtime environment as Spring environment properties. At the same time, however, it's likely useful to have these credentials available for local testing via some other Spring property source. This BE server implementation will by default use a Kubernetes Secrets property source, but uses a Spring 'test' profile to select an `application-test.properties` configuration file as a properties source. 

The actual credentials must not be stored in a shared code repository. This implementation provides for generation of two required config files, and uses `.gitignore` to ensure that these are not included in the project Git repository. These two files are:

```
 secrets.yml 
 src/main/resources/application-test.properties 
```
Generation of these files is performed using the provided script: `map_credentials.py`. To use this script, the user needs to provide all the actual credentials as input to the script - via a file named `service_config.json`. The implementation provides a template for that file: `service_config-template.json`.

With `service_config.json` established, the two files above are generated as shown below. 

``` bash
% python3 map_credentials.py                                
Wrote "application-test.properties"
Wrote "secrets.yml"
```
 
The `secrets.yml` file defines 3 Kubernetes Secrets. These are created in the cluster with:

``` bash
% kubectl apply -f secrets.yml
secret/alphavantage-access created
secret/cloudant-access created
secret/discovery-access created
```

This implementation expects the Secrets data items to be exposed to the application via volume mounts in the runtime environment. This requires an addition to the default AppsodyApplication specification. To get a copy of the default specification, run:

``` bash
% appsody deploy --generate-only
Created deployment manifest: app-deploy.yaml
```

This implementation includes an `app-deploy.yaml` that has already had the needed `volumes` and `volumeMounts` configurations added. 

Excerpt:

``` yaml
  volumeMounts:
  - mountPath: /etc/secrets/watson
    name: watson-secrets
    readOnly: true
  volumes:
  - name: watson-secrets
    secret:
      secretName: discovery-access
```

To deploy to Docker Desktop Kubernetes, ensure that the Kubernetes CLI is configured as shown below.

``` bash
kubectl config use-context docker-desktop
```

Then simply run `appsody deploy`. This will run a Maven `package`, build a Docker image, push the image to the Docker registry, and `apply` the AppsodyApplication Kubernetes resource. The deployed BE server will then be accessible via a Kubernetes "NodePort service".

Example: 

```bash
% kubectl get svc stock-advisor                
NAME            TYPE       CLUSTER-IP     EXTERNAL-IP   PORT(S)          AGE
stock-advisor   NodePort   10.103.15.95   <none>        8080:32055/TCP   4m24s
```
Note the port mapping, and verify access. 

``` bash
% curl http://localhost:32055/actuator/health
{"status":{"code":"UP","description":""},"details":{}}%                          
```

## References

- [Develop a Spring microservice for Kubernetes](https://developer.ibm.com/tutorials/spring-boot-to-cloud-native-with-appsody-and-spring-cloud-kubernetes/)
- [appsody4springsvc2k8s](https://github.com/richard-trotter/appsody4springsvc2k8s)

