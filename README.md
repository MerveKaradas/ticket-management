# 🎫 TicketMaster: Ticket Management System

**TicketMaster**, kurumsal bilet yönetim ihtiyaçları için geliştirilmiş; yüksek güvenlik, merkezi izlenebilirlik ve performans odaklı bir **Spring Boot** tabanlı operasyonel destek uygulamasıdır. Proje; **JWT tabanlı cookie güvenliği**, **Full AOP Audit Logging**, **Refresh Token döngüsü** ve **Docker tabanlı container mimarisi** gibi kurumsal özellikler barındırır.

## 🚀 Kullanılan Teknolojiler

* **Java 17**
* **Spring Boot 3.2+**
* **Spring Data JPA (Hibernate)**
* **Spring Data JPA Auditing:** AuditorAware ile tam otomatize veri izlenebilirliği.
* **PostgreSQL** (Docker Compose üzerinden)
* **Spring Security + JWT** (HttpOnly Cookie tabanlı)
* **MapStruct** (Performanslı DTO-Entity Mapping)
* **Swagger (OpenAPI 3.0)** → API dokümantasyonu
* **Maven** → Dependency & Build management
* **Docker & Docker Compose**
* **JUnit 5 & Mockito** → Birim testler

## 📂 Mimari Yapı

Proje **katmanlı mimari** ve modern tasarım desenleri üzerine kuruludur:

* **Controller:** REST API uçları (`TicketController`, `UserController`, vb.).
* **Config:**  `AuditConfig` & `SecurityAuditorAware`: Otomatik `createdBy` ve `lastModifiedBy` takibi.
* **Service:** İş mantığı ve SOLID prensipleri (`TicketService`, `AuthService`, vb.).
* **Security:** `JWT` & `HttpOnly Cookie` tabanlı güvenlik katmanı; JwtAuthFilter ile `stateless kimlik doğrulama`, JwtUtil ile token yönetimi ve SecurityConfig ile `rol tabanlı yetkilendirme (RBAC)`.
* **Repository:** Spring Data JPA ile veritabanı erişimi.
* **Model (Entity):** `Ticket`, `User`, `AuditLog` gibi domain sınıfları.
* **DTO:** Request & Response veri transfer objeleri (Modern Java Records).
* **AOP (Aspect):** `AuditLogAspect` ile sistemdeki tüm kritik işlemlerin iş mantığından bağımsız otomatik loglanması.
* **Exception Handling:** `GlobalExceptionHandler` ve özel exception sınıfları.
* **Mapper:** `MapStruct` arayüzleri (`TicketMapper`, `UserMapper`, `CommentMapper`).

## ⚙️ Kurulum ve Çalıştırma

### 1. Gereklilikler

* **Java 17+**
* **Maven 3.8+**
* **Docker & Docker Compose**

### 2. Ortam Değişkenleri (.env)

Projenin kök dizininde bir `.env` dosyası oluşturulmalı ve aşağıdaki parametreler tanımlanmalıdır:

```bash

JWT_SECRET=bu_cok_gizli_ve_uzun_bir_key_olmali_123456789
JWT_EXPIRATION=86400000 # 1 gün (ms cinsinden)
REFRESH_TOKEN_EXPIRATION=604800000

```

### 3. Servisleri Başlatma

Uygulama, Maven build süreciyle entegre bir Docker akışına sahiptir:

```bash
# 1. Maven build süreci (pom.xml dosyasının bulunduğu backend dizininde çalıştırılmalıdır)
mvn clean install -DskipTests

# 2. Docker imajlarını oluştur ve servisleri başlat
docker-compose up --build -d

```

### 4. API Dokümantasyonu

👉 **Swagger UI:** `http://localhost:8080/swagger-ui/index.html` (Uygulama çalışırken)

## 🔑 Öne Çıkan Özellikler

* **JWT Cookie Authentication:** Token yönetimi `HttpOnly Cookie` üzerinden yapılarak XSS saldırılarına karşı tam koruma sağlanmıştır.
* **Refresh Token Desteği:** Kesintisiz kullanıcı deneyimi için otomatik token yenileme mekanizması.
* **AOP Tabanlı Audit Logging:** Merkezi bir aspect üzerinden tüm bilet hareketleri ve güvenlik denemeleri anlık kayıt altına alınır.
* **Akıllı İş Kuralları:**  Çözümlenmiş `DONE` statüsündeki biletler üzerinde herhangi bir içerik veya detay değişikliği yapılmak istendiğinde, sistem veri tutarlılığını sağlamak adına bilet statüsünü otomatik olarak `REOPENED` konumuna çeker; böylece bitmiş işlerin kontrolsüzce değiştirilmesinin önüne geçilir.
* **Merkezi Denetim (JPA Auditing & AuditorAware):** Sistem genelinde AuditingEntityListener entegrasyonu ile tüm entity'lerin (Biletler, Yorumlar vb.) yaşam döngüsü otomatiğe bağlanmıştır; verinin ne zaman oluşturulduğu, ne zaman güncellendiği ve bu işlemleri hangi kullanıcının yaptığı manuel müdahale gerektirmeden veritabanı seviyesinde %100 doğrulukla takip edilmektedir.


## 🧪 Test ve Kalite
* **Unit Testing:** TicketService ve CommentService katmanları JUnit ve Mockito kullanılarak test edilmiştir.

## 🛡️ Güvenlik

* **JWT Authentication:** Her endpoint güvenlik kontrolünden geçer.
* **Role-Based Access Control (RBAC):** `USER` ve `ADMIN` rolleri için kısıtlanmış yetki yönetimi.

## 📊 İzleme & Loglama

* **Merkezi Denetim (Audit Logging):** Kritik kullanıcı aksiyonları, sistem hataları ve güvenlik hareketleri, AOP tabanlı merkezi bir log mekanizmasıyla izlenebilirlik (traceability) standartlarına uygun olarak kayıt altına alınmaktadır.

* **Operasyonel Dashboard & KPI Takibi:** Sistem sağlığını izlemek adına; toplam kullanıcı hacmi, ortalama çözüm süresi ve hata/işlem oranları gibi kritik performans göstergeleri (KPI) anlık olarak raporlanmaktadır.

* **Gelişmiş Veri Görselleştirme:** Ham veriler, Recharts entegrasyonu ile anlamlı metriklere dönüştürülerek; kullanıcı bazlı iş yükü dağılımı, bilet yaşam döngüsü grafikleri ve günlük aktivite yoğunluğu üzerinden trend analizi yapılabilmesine olanak sağlar.

* **Canlı Güvenlik & Tehdit İzleme:** Admin Dashboard üzerinden gerçekleşen başarılı/başarısız giriş denemeleri ve yetkisiz erişim teşebbüsleri eş zamanlı olarak takip edilerek sistem güvenliği proaktif olarak yönetilmektedir.

