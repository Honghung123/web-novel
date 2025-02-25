# Novel Plugin System

## Overview

This project is a **plugin-based website** for reading novels, allowing users to **read, switch between multiple novel sources, and download files for offline reading**.

### Key Features:

-   **Dynamic Plugin System**: Load and update plugins at runtime using the **Reflection API**.
-   **Web Scraping**: Crawl novel data from multiple online sources.
-   **File Conversion**: Convert HTML content into formats like **PDF, AZW3, and MP3**.
-   **Firebase Integration**:
    -   **Push Notifications** via **Firebase Cloud Messaging (FCM)**.
    -   **User Authentication** using **Firebase Authentication**.
    -   **Cloud Storage** for storing downloaded files.

## Technologies Used

### Frontend (ReactJS)

-   **ReactJS**: Core frontend framework.
-   **TailwindCSS**: Styling framework for a modern UI.
-   **Material UI**: Component library for a clean and accessible design.
-   **Dependencies**:
    -   `@dnd-kit/core`: Drag and drop support.
    -   `@mui/material`: Material UI components.
    -   `firebase`: Firebase authentication, storage, and push notifications.
    -   `axios`: HTTP client for API requests.

### Backend (Spring Boot)

-   **Java & Spring Boot**: Core backend technologies.
-   **PostgreSQL**: Database for storing novel data.
-   **Swagger & SpringDoc**: API documentation.
-   **Firebase Admin SDK**: Push notifications, authentication, and storage.
-   **Dependencies**:
    -   `firebase-admin`: Firebase integration.
    -   `spring-boot-starter-validation`: Input validation.
    -   `spring-boot-starter-web`: REST API development.
    -   `jsoup`: Web scraping.
    -   `springdoc-openapi-starter-webmvc-ui`: OpenAPI documentation.
    -   `gson`: JSON parsing.
    -   `okhttp`: HTTP client for API communication.
    -   `httpclient`: Additional HTTP functionalities.

---

## Project Structure

```
novel-plugin-system/
│── frontend/         # ReactJS Frontend
│   ├── src/
│   ├── public/
│   ├── package.json
│   ├── tailwind.config.js
│   └── README.md
│
│── backend/          # Spring Boot Backend
│   ├── src/
│   │   ├── main/java/com/
│   │   ├── main/resources/
│   │   ├── test/
│   ├── pom.xml
│   └── README.md
│
│── README.md         # Project Documentation
│── .gitignore
```

---

## Setup Instructions

### 1️⃣ Backend (Spring Boot)

#### **Requirements**

-   Java 17+
-   Maven
-   PostgreSQL Database
-   Firebase config JSON file

#### **Installation**

```sh
cd backend
mvn clean install
```

### ** Config firebase admin SDK **

You will have to download a Firebase config JSON file from Firebase when creating your Firebase project. Then, rename to `firebase-adminsdk.json` and place it in the `backend/src/main/resources` directory.

#### **Run Application**

```sh
mvn spring-boot:run
```

#### **API Documentation**

Access Swagger UI at:  
`http://localhost:8080/swagger-ui/index.html`

---

### 2️⃣ Frontend (ReactJS)

#### **Requirements**

-   Node.js 18+
-   npm or yarn
-   Firebase config object

#### **Installation**

```sh
cd frontend
npm install
```

#### **Config firebase identity**

Please refer to the [Firebase documentation](https://firebase.google.com/docs/web/setup#config-object) for instructions on how to configure your Firebase project. Then replace the `firebaseConfig` object in the `public/firebase-messaging-sw.js` file with your own configuration.

```js
const firebaseConfig = {
    apiKey: "YOUR_API_KEY",
    authDomain: "YOUR_AUTH_DOMAIN",
    projectId: "YOUR_PROJECT_ID",
    storageBucket: "YOUR_STORAGE_BUCKET",
    messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
    appId: "YOUR_API_ID",
    measurementId: "YOUR_MEASUREMENT_ID",
};
```

#### **Config firebase authentication**

Currently, this project uses Firebase authentication with username/password, Google and Github provider. So, you will have to config with client id and secret key for [Google](https://console.developers.google.com/auth/clients) and [Github](https://github.com/settings/applications/new) provider at your Firebase console panel.

#### **Run Application**

```sh
npm start
```

#### **Build for Production**

```sh
npm run build
```

---

## Contribution Guide

1. **Fork** the repository.
2. **Create a new branch**: `git checkout -b feature-branch`
3. **Commit your changes**: `git commit -m "Add new feature"`
4. **Push to the branch**: `git push origin feature-branch`
5. **Create a Pull Request**
