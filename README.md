# 📖 BlogApp (Backend)

BlogApp, kullanıcıların blog yazıları paylaşabildiği, gönderilere yorum yapabildiği ve yönetici rolündeki kullanıcıların içerikleri yönetebildiği **RESTful API** uygulamasıdır.  
Bu proje **Spring Boot** kullanılarak geliştirilmiştir.  

---

## 🚀 Özellikler

### 👤 Kullanıcılar
- Kullanıcı kaydı (register) ve giriş (login)
- JWT tabanlı kimlik doğrulama
- Email doğrulama ve hesap aktivasyonu
- Şifre sıfırlama (Forgot Password) & Email reset linki
- Profil bilgilerini güncelleme
- Kullanıcıların kendi gönderilerini düzenleme / silme

### ✍️ Gönderiler (Posts)
- Post oluşturma, düzenleme, silme
- Post’lara resim yükleme (upload)
- Tag (etiket) sistemi
- Yorum sayacı

### 💬 Yorumlar (Comments)
- Kullanıcıların gönderilere yorum yapması
- Yorum düzenleme / silme
- Admin tarafından yorum yönetimi

### 🛡️ Admin Panel
- Kullanıcı yönetimi (roller: USER, ADMIN)
- Post ve yorum yönetimi (onaylama, silme)
- Admin sadece kendisine özel endpointlere erişebilir

### ⚙️ Diğer
- Global exception handling
- Validation (ör: şifre kuralları, email formatı)
- DTO & Entity ayrımı
- Image upload (dosyalar `uploads/` klasörüne kaydedilir)
- Database: PostgreSQL

---

## 🛠️ Kullanılan Teknolojiler

- **Java 17**
- **Spring Boot 3**
  - Spring Web
  - Spring Data JPA (Hibernate)
  - Spring Security (JWT)
  - Spring Validation
  - JavaMailSender (email işlemleri)
- **PostgreSQL**
- **Maven**


