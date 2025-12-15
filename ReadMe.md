# Hata Takip Sistemi

Nesne Yönelimli Programlama dersi için geliştirilmiş web tabanlı hata takip ve yönetim sistemi.

## Özellikler

- Hata kaydı oluşturma, güncelleme ve silme
- Kullanıcı rolleri (Yönetici, Geliştirici, Test Uzmanı, Raporlayıcı)
- Hata durumu takibi (Açık, Devam Ediyor, Çözüldü, Kapatıldı)
- Hata önceliklendirme sistemi

## KUllanılan Teknolojiler

- **Backend:** Java 21, Spring Boot 4.0
- **Veritabanı:** PostgreSQL
- **ORM:** Hibernate/JPA
- **Güvenlik:** Spring Security
- **Build Tool:** Maven

## OOP Prensipleri

Projede kullanılan nesne yönelimli programlama konseptleri:

- **Encapsulation** (Kapsülleme)
- **Inheritance** (Kalıtım)
- **Polymorphism** (Çok biçimlilik)
- **Abstraction** (Soyutlama)

## 📦 Kurulum
```bash
# Repository'yi klonla
git clone https://github.com/Ynssr/bugtracker.git

# Proje dizinine git
cd bugtracker

# PostgreSQL veritabanını oluştur
createdb bugtracker_db

# application.properties dosyasını düzenle
# Veritabanı bilgilerini güncelle

# Uygulamayı çalıştır
mvn spring-boot:run
```
