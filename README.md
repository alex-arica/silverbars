Allows to register, cancel and summarize the silver-bars orders via a REST API.
It uses an in-memory DB which is empty on start-up.

## REST API

The service exposes a restful API on HTTP port 26034

- To register an order, send a POST request with a QuoteDto in JSON format in the body to: http://localhost:26034/order
  
  See the acceptance-test: RegisterOrderRestAdapterAcceptanceTest
  
- To cancel an order, send a DELETE request with an orderId in parameter to: http://localhost:26034/order?orderId={value}
  
  See the acceptance-test: CancelOrderRestAdapterAcceptanceTest
  
- To summarize orders, send a GET request to: http://localhost:26034/orders/summary
  
  See the acceptance-test: GetOrdersSummaryRestAdapterAcceptanceTest

  
## RUN locally Service in command-line

1) mvn clean package
2) cd target
3) java -jar silverbars-1.0-SNAPSHOT.jar


## Run locally Service from IDE

- Run the file: Main.java
