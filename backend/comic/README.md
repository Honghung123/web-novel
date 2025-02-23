# 📖 Novel Plugin System - Backend

## Overview
This is the **backend** of the **Novel Plugin System**, a Spring Boot application that provides APIs for managing novels, crawling data, handling authentication, and file conversion.

### ✨ Key Features:
- **Dynamic Plugin System**: Load and update plugins at runtime using **Reflection API**.
- **Web Scraping**: Crawl novel data from various sources using **Jsoup**.
- **File Conversion**: Convert HTML content into **PDF, AZW3, MP3** files.
- **Firebase Integration**:
  - **User Authentication** with **Firebase Authentication**.
  - **Push Notifications** via **Firebase Cloud Messaging (FCM)**.
  - **Cloud Storage** for storing downloaded files.
- **Swagger API Documentation** for easy API testing.

---

## 🛠 Technologies Used

### **Backend Stack**
- **Java 17+** - Programming language.
- **Spring Boot** - Backend framework.
- **PostgreSQL** - Database.
- **Swagger & SpringDoc** - API documentation.
- **Firebase Admin SDK** - Authentication, push notifications, and storage.

### **Dependencies**
| Dependency                          | Description |
|--------------------------------------|-------------|
| `firebase-admin`                     | Firebase integration |
| `spring-boot-starter-web`            | Spring Boot for REST API |
| `spring-boot-starter-validation`     | Input validation |
| `springdoc-openapi-starter-webmvc-ui`| Swagger API documentation |
| `jsoup`                              | Web scraping |
| `gson`                               | JSON parsing |
| `okhttp`                             | HTTP client for API communication |
| `httpclient`                         | Additional HTTP utilities |

---

## 📁 Project Structure

```
backend/
│── src/main/java/com/novel/
│   ├── controllers/    # REST API Controllers
│   ├── services/       # Business logic
│   ├── repositories/   # Database access layer
│   ├── models/         # Entity models
│   ├── utils/          # Helper functions
│── src/main/resources/
│   ├── application.yml # Configuration file
│── pom.xml
│── README.md
```

---

## 🚀 Getting Started

### **1️⃣ Prerequisites**
- **Java 17+**
- **Maven**
- **PostgreSQL Database**

### **2️⃣ Setup Database**
Create a PostgreSQL database and update `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/novel_db
    username: your_db_username
    password: your_db_password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

### **3️⃣ Installation**
Clone the repository and build the project:

```sh
git clone https://github.com/your-repo/novel-plugin-system.git
cd backend
mvn clean install
```

### **4️⃣ Running the Application**
```sh
mvn spring-boot:run
```
The backend will be available at:  
👉 `http://localhost:8080`

### **5️⃣ API Documentation**
Swagger API documentation can be accessed at:  
👉 `http://localhost:8080/swagger-ui.html`

---

## 🔥 Firebase Configuration
To enable Firebase features, create a **Firebase Service Account** and add the credentials file (`serviceAccountKey.json`) under `src/main/resources/`.

Then, update `application.yml`:
```yaml
firebase:
  service-account-file: classpath:serviceAccountKey.json
```

---

## 🤝 Contribution Guide
1. **Fork** the repository.
2. **Create a new branch**: `git checkout -b feature-branch`
3. **Commit your changes**: `git commit -m "Add new feature"`
4. **Push to the branch**: `git push origin feature-branch`
5. **Create a Pull Request!**

---

## 📜 License
This project is licensed under the **MIT License**.

---

## 👨‍💻 Contributors
- **Your Name** - *Backend Developer*
- **Other Team Members** - *Developers, Database Engineers, QA Engineers* 