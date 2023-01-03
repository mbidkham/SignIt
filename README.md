# Getting Started
* Docker:
  * docker build -t signit
  * docker run -dp 3000:3000 getting-started
* Install Maven, Jdk11:
  * cd into the project's root directory
  * ./mvnw clean package
  * java -jar target/signature-0.0.1-SNAPSHOT com.signit.signature.SignItApplication

You can see Swagger :
* http://localhost:8080/swagger-ui/
