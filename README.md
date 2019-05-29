Allows to register, cancel and summarize the silver-bars orders via a REST API.
It uses an in-memory DB which is empty on start-up.

## REST API

The service exposes a restful API on HTTP port 26034

- To register an order, send a POST request with in the BODY a QuoteDto in JSON format to: http://localhost:26034/order
  
  See the acceptance-test: [RegisterOrderRestAdapterAcceptanceTest](https://github.com/alex-arica/silverbars/blob/master/src/test/java/acceptance/RegisterOrderRestAdapterAcceptanceTest.java)
  
- To cancel an order, send a DELETE request with an orderId in parameter to: http://localhost:26034/order?orderId={value}
  
  See the acceptance-test: [CancelOrderRestAdapterAcceptanceTest](https://github.com/alex-arica/silverbars/blob/master/src/test/java/acceptance/CancelOrderRestAdapterAcceptanceTest.java)
  
- To summarize orders, send a GET request to: http://localhost:26034/orders/summary
  
  See the acceptance-test: [GetOrdersSummaryRestAdapterAcceptanceTest](https://github.com/alex-arica/silverbars/blob/master/src/test/java/acceptance/GetOrdersSummaryRestAdapterAcceptanceTest.java)

  
## RUN service locally in command-line

1) mvn clean package
2) cd target
3) java -jar silverbars-1.0-SNAPSHOT.jar


## Run service locally from IDE

- Run the file: Main.java
