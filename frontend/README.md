# 📖 Novel Plugin System - Frontend

## Overview

This is the **frontend** of the **Novel Plugin System**, a web application that allows users to **read novels, switch between sources, and download files for offline reading**.

### ✨ Key Features:

-   **Modern UI** built with **ReactJS**, **TailwindCSS**, and **Material UI**.
-   **Dynamic Plugin System** for switching between different novel sources.
-   **Firebase Integration**:
    -   **User Authentication** with Firebase.
    -   **Push Notifications** using Firebase Cloud Messaging (FCM).
    -   **Cloud Storage** for managing downloaded files.
-   **Drag and Drop Support** for managing novel lists.
-   **Responsive Design** for seamless experience across all devices.

---

## 🛠 Technologies Used

### **Frontend Stack**

-   **ReactJS** - Core frontend framework.
-   **TailwindCSS** - Utility-first CSS framework for styling.
-   **Material UI** - Component library for UI components.

### **Dependencies**

| Dependency      | Description                                         |
| --------------- | --------------------------------------------------- |
| `react`         | Core React library                                  |
| `@mui/material` | Material UI components                              |
| `@dnd-kit/core` | Drag-and-drop support                               |
| `firebase`      | Firebase authentication, notifications, and storage |
| `axios`         | HTTP client for API requests                        |

---

## 📁 Project Structure

```
frontend/
│── src/
│   ├── components/       # Reusable UI components
│   ├── pages/            # Page components (Home, Login, etc.)
│   ├── hooks/            # Custom React hooks
│   ├── utils/            # Helper functions
│   ├── services/         # API services and Firebase interactions
│── public/
│── package.json
│── tailwind.config.js
│── README.md
```

---

## 🚀 Getting Started

### **1️⃣ Prerequisites**

-   **Node.js 18+**
-   **npm** (or **yarn**)

### **2️⃣ Installation**

Clone the repository and install dependencies:

```sh
git clone https://github.com/your-repo/novel-plugin-system.git
cd frontend
npm install
```

### **3️⃣ Running the App**

Start the development server:

```sh
npm start
```

The application will be available at:  
👉 `http://localhost:3000`

### **4️⃣ Building for Production**

To create an optimized production build:

```sh
npm run build
```

The output will be in the `build/` folder.

---

## 🔥 Firebase Configuration

To enable Firebase features, create a `.env` file in the **frontend** directory and add:

```env
REACT_APP_FIREBASE_API_KEY=your_api_key
REACT_APP_FIREBASE_AUTH_DOMAIN=your_auth_domain
REACT_APP_FIREBASE_PROJECT_ID=your_project_id
REACT_APP_FIREBASE_STORAGE_BUCKET=your_storage_bucket
REACT_APP_FIREBASE_MESSAGING_SENDER_ID=your_messaging_sender_id
REACT_APP_FIREBASE_APP_ID=your_app_id
```

Ensure you replace `your_api_key` and other values with actual Firebase credentials.

---

## 🤝 Contribution Guide

1. **Fork** the repository.
2. **Create a new branch**: `git checkout -b feature-branch`
3. **Commit your changes**: `git commit -m "Add new feature"`
4. **Push to the branch**: `git push origin feature-branch`
5. **Create a Pull Request!**

---
