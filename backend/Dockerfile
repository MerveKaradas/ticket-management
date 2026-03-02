# Java runtime imajı
FROM eclipse-temurin:17-jdk-alpine  
# Çalışma dizini
WORKDIR /app                
# Build edilmiş jar dosyasını konteynere kopyala
COPY target/ticket-management-0.0.1-SNAPSHOT.jar app.jar  
# Uygulamayı başlat
ENTRYPOINT ["java","-jar","app.jar"]  
