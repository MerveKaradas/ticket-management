# 🎫 TicketMaster: Ticket Management System

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB)![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)![Flyway](https://img.shields.io/badge/flyway-CC0202?style=for-the-badge&logo=flyway&logoColor=white)

**TicketMaster**, kurumsal bilet yönetim ihtiyaçları için geliştirilmiş; yüksek güvenlik, merkezi izlenebilirlik ve performans odaklı bir **Spring Boot** tabanlı operasyonel destek uygulamasıdır. Proje; **JWT tabanlı cookie güvenliği**, **Full AOP Audit Logging**, **Refresh Token döngüsü** ve **Docker tabanlı container mimarisi** gibi kurumsal özellikler barındırır.





## 🚀 Kullanılan Teknolojiler

* **Java 17**
* **Spring Boot 3.2+**
* **Spring Data JPA (Hibernate)**
* **Spring Data JPA Auditing:** AuditorAware ile tam otomatize veri izlenebilirliği.
* **PostgreSQL** (Docker Compose üzerinden)
* **Spring Security + JWT** (HttpOnly Cookie tabanlı)
* **MapStruct** (Performanslı DTO-Entity Mapping)
* **Spring Application Events:** Servisler arası gevşek bağlılık (loose coupling) için asenkron/senkron olay yönetimi.
* **Apache POI:** Dinamik Excel raporları oluşturmak için kullanılan kurumsal kütüphane.
* **Swagger (OpenAPI 3.0):**  API dokümantasyonu
* **Maven:**  Dependency & Build management
* **Flyway:** Veritabanı şeması versiyon kontrolü ve otomatik migration yönetimi.
* **Redis & Spring Data Redis:** Uygulama performansını optimize etmek için kullanılan distributed caching mekanizması.
* **Docker & Docker Compose**
* **JUnit 5 & Mockito:** Birim testler
* **React 18:** Modern UI bileşenleri ve performanslı rendering.
* **Tailwind CSS:** Responsive ve kurumsal tasarım dili.
* **Recharts:** Sistem metriklerinin görselleştirilmesi için kullanılan grafik kütüphanesi.
* **Axios:** Backend entegrasyonu ve interceptor yönetimi.
* **Design Patterns:** Strategy (Dinamik raporlama algoritmaları için) ve Factory (Raporlayıcı nesne üretimi için) desenleri ile genişletilebilir mimari.

## 📂 Mimari Yapı

Proje **katmanlı mimari** ve modern tasarım desenleri üzerine kuruludur:

* **Controller:** REST API uçları (`TicketController`, `UserController`, vb.).
* **Config:**  `AuditConfig` & `SecurityAuditorAware`: Otomatik `createdBy` ve `lastModifiedBy` takibi.
* **Service:** İş mantığı ve SOLID prensipleri (`TicketService`, `AuthService`, vb.).
* **Security:** `JWT` & `HttpOnly Cookie` tabanlı güvenlik katmanı; JwtAuthFilter ile `stateless kimlik doğrulama`, JwtUtil ile token yönetimi ve SecurityConfig ile `rol tabanlı yetkilendirme (RBAC)`.
* **Repository:** Spring Data JPA ile veritabanı erişimi.
* **Model (Entity):** `Ticket`, `User`, `AuditLog` gibi domain sınıfları.
* **DTO:** Request & Response veri transfer objeleri `(Modern Java Records)`.
* **AOP (Aspect):** `AuditLogAspect` ile sistemdeki tüm kritik işlemlerin iş mantığından bağımsız otomatik loglanması.
* **Exception Handling:** `GlobalExceptionHandler` ve özel exception sınıfları.
* **Mapper:** `MapStruct` arayüzleri (`TicketMapper`, `UserMapper`, `CommentMapper`).
* **Event & Listener:** UserDeletedEvent ve TicketListener ile servisler arası mesajlaşma ve otomatize iş akışları.
* **Export Engine (Strategy & Factory):** Raporlama mantığını Controller'dan ayıran, farklı formatları (Excel, PDF, CSV) tek bir arayüzden yöneten esnek yapı.

**Frontend UI:** React 18 tabanlı; Tailwind CSS ile responsive tasarlanmış ve Recharts ile veri analitiği sunan modern kullanıcı arayüzü.


## ⚙️ Kurulum ve Çalıştırma

### 1. Gereklilikler

* **Java 17+**
* **Maven 3.8+**
* **Node.js (v18+) & npm/yarn** (Frontend için)
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

###  4. Uygulama Erişim Bilgileri

Servisler başarıyla ayağa kalktığında aşağıdaki adresler üzerinden sisteme erişebilirsiniz:

* *Frontend UI:* `http://localhost:3000` (React Arayüzü)

* *Backend API:* `http://localhost:8080`

* *API Dokümantasyonu (Swagger):* `http://localhost:8080/swagger-ui/index.html#/`

![Swagger Doc](docs/gifs/swagger-api-docs.gif)

## 🔑 Öne Çıkan Özellikler

* **JWT Cookie Authentication:** Token yönetimi `HttpOnly Cookie` üzerinden yapılarak XSS saldırılarına karşı tam koruma sağlanmıştır.
* **Refresh Token Desteği:** Kesintisiz kullanıcı deneyimi için otomatik token yenileme mekanizması.
* **AOP Tabanlı Audit Logging:** Merkezi bir aspect üzerinden tüm bilet hareketleri ve güvenlik denemeleri anlık kayıt altına alınır.
* **Akıllı İş Kuralları:**  Çözümlenmiş `DONE` statüsündeki biletler üzerinde herhangi bir içerik veya detay değişikliği yapılmak istendiğinde, sistem veri tutarlılığını sağlamak adına bilet statüsünü otomatik olarak `REOPENED` konumuna çeker; böylece bitmiş işlerin kontrolsüzce değiştirilmesinin önüne geçilir.
* **Merkezi Denetim (JPA Auditing & AuditorAware):** Sistem genelinde `AuditingEntityListener` entegrasyonu ile tüm entity'lerin (Biletler, Yorumlar vb.) yaşam döngüsü otomatiğe bağlanmıştır; verinin ne zaman oluşturulduğu, ne zaman güncellendiği ve bu işlemleri hangi kullanıcının yaptığı manuel müdahale gerektirmeden veritabanı seviyesinde %100 doğrulukla takip edilmektedir.
* **Genişletilebilir Raporlama Altyapısı (Strategy Pattern):** Raporlama sistemi Strategy Pattern kullanılarak yeniden tasarlandı. Bu sayede sisteme yeni bir rapor formatı (örn. PDF) eklemek, mevcut kodları değiştirmeden sadece yeni bir strateji sınıfı ekleyerek mümkün hale getirildi. Nesne yaratım süreçleri Factory Pattern ile soyutlanarak bağımlılıklar (coupling) minimize edildi.
* **Type-Safe Querying:** JPA Metamodel ile hatasız ve performanslı dinamik arama altyapısı.



## 🛡️ Gelişmiş Veri Yönetimi & İş Akışları
* **1. Soft Delete & Güvenlik Döngüsü**

   ***Yazılımsal Silme (Soft Delete):*** Kullanıcılar veritabanından fiziksel olarak silinmez; active flag'i ile pasifize edilir. Bu sayede sistemdeki tarihsel bilet verileri ve audit loglarındaki "ataması yapılmış kullanıcı" bilgisi korunmuş olur.

    ***Security Check:*** Pasif duruma getirilen kullanıcıların oturum açma yetkileri, Spring Security UserDetails (isEnabled) seviyesinde anlık olarak kısıtlanmıştır.

* **2. Olay Güdümlü Bilet Ataması (Event-Driven Reassignment)**

    ***Loose Coupling:*** UserService ve TicketService arasındaki doğrudan bağımlılığı ortadan kaldırmak için Spring ApplicationEventPublisher mimarisi uygulanmıştır.

    ***UserDeletedEvent:*** Bir kullanıcı pasifize edildiği anda sistem bir domain event fırlatır. TicketListener bu olayı yakalayarak ilgili kullanıcıya ait tüm açık biletleri (Done olmayanları) otomatik olarak yönetir.

* **3. Akıllı Sahipsiz Bilet Yönetimi (System Pool)**

    ***Unassigned Pool:*** Sistemde "sahipsiz iş" kalmaması adına ROLE_SYSTEM yetkisine sahip sanal bir "System Pool" kullanıcısı kurgulanmıştır.

    ***Otomatik Aktarım:*** Silinen kullanıcı üzerindeki biletler, veritabanı NOT NULL kısıtlamalarını bozmadan otomatik olarak bu havuz kullanıcısına atanır ve bilet statüsü yeniden planlama için BACKLOG konumuna çekilir.

    ***Self-Assign (Bileti Üstlenme):*** Aktif kullanıcılar, havuzdaki (Unassigned) biletleri "Claim" mekanizması ile kendi üzerlerine alarak iş akışına dahil edebilirler.

* **4. Veritabanı Sürüm Kontrolü (Database Migration)**

    ***Flyway Integration:*** Uygulama şeması Hibernate'in otomatik üretimine bırakılmamış; Flyway ile versiyonlanmıştır. Bu sayede farklı ortamlarda (Dev/Test/Prod) tutarlı bir veritabanı yapısı garanti altına alınmıştır.

* **5. Cache Abstraction & Consistency**

     Bir ticket statüsü değiştiğinde veya yeni bir kullanıcı eklendiğinde, ilgili tüm analiz cache'leri (analytics, users) otomatik olarak geçersiz kılınır. Bu sayede Dashboard üzerindeki grafikler her zaman veritabanı ile tutarlı kalır.
## 🧪 Test ve Kalite
* **Unit Testing:** TicketService ve CommentService katmanları JUnit ve Mockito kullanılarak test edilmiştir.

Projenin sürdürülebilirliği ve iş mantığının doğruluğu için JUnit 5, Mockito ve AssertJ kütüphaneleri kullanılarak kapsamlı bir test altyapısı kurgulanmıştır.

* **Birim Testler:** TicketService ve CommentService katmanları, iş kurallarını +%90 kapsayacak şekilde test edilmiştir.

* **Davranış Odaklı Doğrulama (BDD):** Test yazımında given-when-then (BDDMockito) yapısı benimsenerek okunabilirlik artırılmıştır.

Gelişmiş Mocking Teknikleri:

* **ArgumentCaptor:** Metotlara gönderilen nesnelerin içeriği yakalanarak derinlemesine doğrulanmıştır.

* **Parameterized Tests:** Bilet statü geçişleri (TicketStatus) gibi çoklu senaryolar, @ParameterizedTest kullanılarak tek bir metotla optimize edilmiştir.

* **Esnek Test Verisi Yönetimi:** Test nesnelerinin merkezi yönetimi ve kod tekrarının önlenmesi için Test Data Factory tasarım desenleri uygulanmıştır.

* **Akıcı Doğrulamalar:** Standart JUnit assert metodları yerine, daha okunaklı ve detaylı hata mesajları sunan AssertJ tercih edilmiştir.

## 🛡️ Güvenlik

* **JWT Authentication:** Her endpoint güvenlik kontrolünden geçer.
* **Role-Based Access Control (RBAC):** `USER` ve `ADMIN` rolleri için kısıtlanmış yetki yönetimi.

## ⚡ Performans ve Ölçeklenebilirlik (Database Optimization)
Proje, yüksek veri hacminde bile milisaniye seviyesinde yanıt verebilmesi için veritabanı seviyesinde optimize edilmiştir:

* **Akıllı İndeksleme Stratejisi:** Yoğun veri içeren tablolarda sorgu performansını artırmak için özel indeksler tanımlanmıştır.


* **Lazy Loading & N+1 Önleme:** Tüm @ManyToOne ilişkileri FetchType.LAZY olarak konfigüre edilerek gereksiz veri transferi engellenmiş; performans ihtiyacına göre EntityGraph yapıları tercih edilmiştir.

* **Pagination (Sayfalama):** Tüm liste sorguları Spring Data Pageable arayüzü ile sarmalanarak, milyonlarca satırlık verinin belleği yormadan parça parça işlenmesi sağlanmıştır.

* **Memory-Efficient Export (Streaming):** Milyonlarca satırlık Audit Log verisinin dışa aktarımı sırasında bellek kullanımını stabilize etmek için SXSSF (Streaming XML Spreadsheets) ve JPA Stream entegrasyonu sağlandı. Veriler veritabanından uygulama belleğine dolmadan, parça parça işlenerek doğrudan çıktı akışına iletilir.


* **Tip Güvenli Dinamik Sorgular (JPA Metamodel):** Karmaşık bilet filtreleme operasyonlarında String bazlı sorgu hatalarını  tamamen ortadan kaldırmak için JPA Static Metamodel kullanılmıştır. Sorgular Ticket_ ve User_ sınıfları üzerinden tip güvenli şekilde inşa edilerek, refactoring süreçlerinde tam derleme zamanı güvenliği sağlanmıştır.

* **Distributed Caching & Ortalama Yanıt Süresi Optimizasyonu (Redis)**

    Sistemdeki hesaplama yükü yüksek analiz sorguları ve sık erişilen veriler Redis tabanlı bir önbellek katmanı ile optimize edilmiştir:

    * Multi-Layer Caching: @Cacheable ve @CacheEvict anotasyonları ile servis katmanında deklaratif önbellekleme.

    * Analytics Cache: calculateAverageResolveTime ve totalStatusCount gibi tüm tabloyu tarayan analiz metotları Redis üzerinde tutularak veritabanı üzerindeki I/O yükü %90 oranında azaltılmıştır.

    * Transaction-Aware Caching: Cache işlemleri veritabanı transaction'ları ile senkronize edilmiştir. Bir işlem rollback olursa, Redis üzerindeki kirli veri otomatik olarak engellenir.

    * TTL (Time-To-Live) Stratejisi: Verilerin bayatlamasını önlemek için her cache grubu (users, analytics, ticketStats) için farklı yaşam süreleri ve stratejik temizleme döngüleri kurgulanmıştır.

    * Spring Security Context Caching: Kimlik doğrulama performansını artırmak adına UserDetailsService seviyesinde Redis caching uygulanmıştır. Her istekte gerçekleşen mükerrer veritabanı sorguları (where email=?) engellenerek, sistemin yanıt verme süresi minimize edilmiştir.

## 📊 İzleme & Loglama

* **Merkezi Denetim (Audit Logging):** Kritik kullanıcı aksiyonları, sistem hataları ve güvenlik hareketleri, AOP tabanlı merkezi bir log mekanizmasıyla izlenebilirlik  standartlarına uygun olarak kayıt altına alınmaktadır.

* **Operasyonel Dashboard & KPI Takibi:** Sistem sağlığını izlemek adına; toplam kullanıcı hacmi, ortalama çözüm süresi ve hata/işlem oranları gibi kritik performans göstergeleri (KPI) anlık olarak raporlanmaktadır.

* **Gelişmiş Veri Görselleştirme:** Ham veriler, Recharts entegrasyonu ile anlamlı metriklere dönüştürülerek; kullanıcı bazlı iş yükü dağılımı, bilet yaşam döngüsü grafikleri ve günlük aktivite yoğunluğu üzerinden trend analizi yapılabilmesine olanak sağlar.

* **Canlı Güvenlik & Tehdit İzleme:** Admin Dashboard üzerinden gerçekleşen başarılı/başarısız giriş denemeleri ve yetkisiz erişim teşebbüsleri eş zamanlı olarak takip edilerek sistem güvenliği proaktif olarak yönetilmektedir.

* **Operasyonel Veri Çıktısı:** İzlenen tüm loglar ve bilet geçmişi, tek tıkla raporlanabilir. İndirme işlemi JWT yetkilendirme kontrolüyle korunarak veri güvenliği en üst seviyede tutulmaktadır.


