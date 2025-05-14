# 📘 Document Management System – Spring Boot REST API

A secure, role-based REST API built with **Spring Boot**, **Spring Security (JWT)**, **JPA/Hibernate**, and **MySQL**. This project enables user authentication, document upload/management, keyword-based search, and admin-level file control operations.

---

## 🚀 Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Security (JWT)**
- **Spring Data JPA (Hibernate)**
- **MySQL**
- **Maven**
- **Postman** (for API testing)

---

## 🧰 Features

- ✅ JWT-based user registration and login
- 🔐 Role-based access control (User/Admin)
- 📄 Document upload, download, delete
- 🔍 Document search by keywords
- 🧠 Q&A-based search from document context
- 🗂️ Admin-only file system operations
- 📬 Postman-tested endpoints
- ⚙️ Auto-database schema generation (Hibernate)

---

## 📁 Project Structure

\`\`\`
src/
├── config/          # Security configuration (JWT, CORS)
├── controller/      # REST API endpoints
├── dto/             # DTOs for API requests/responses
├── entity/          # JPA entities (database models)
├── repository/      # Spring Data JPA repositories
├── service/         # Service layer (business logic)
├── util/            # Utility classes
└── main/            # Spring Boot entry point
\`\`\`

---

## 🧠 Database Setup (MySQL)

1. Create a database/schema in MySQL:
   \`\`\`sql
   CREATE DATABASE your_db;
   \`\`\`

2. Update your credentials in \`src/main/resources/application.properties\`:
   \`\`\`properties
   spring.datasource.url=jdbc:mysql://localhost:3306/your_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   \`\`\`

> Tables will be created automatically on application startup.

---

## 🔐 Authentication Flow

### 📝 Signup
\`\`\`
POST /api/v1/auth/signup
\`\`\`
Registers a new user.

### 🔑 Login
\`\`\`
POST /api/v1/auth/login
\`\`\`
Returns:
- Access Token
- Refresh Token
- User Info
- Expiry Time

### 🔐 Access Secured APIs

Use header:
\`\`\`
Authorization: Bearer <access_token>
\`\`\`

### 🔁 Refresh Token
\`\`\`
POST /api/v1/auth/refresh
\`\`\`

### 🚪 Logout
\`\`\`
POST /api/v1/auth/logout
\`\`\`

---

## 📄 API Endpoints Overview

### 🔐 Authentication
| Method | Endpoint               | Description         |
|--------|------------------------|---------------------|
| POST   | \`/api/v1/auth/signup\`  | Register user       |
| POST   | \`/api/v1/auth/login\`   | Login user          |
| POST   | \`/api/v1/auth/refresh\` | Refresh JWT token   |
| POST   | \`/api/v1/auth/logout\`  | Logout user         |

### 📁 Document Management
| Method | Endpoint                         | Description           |
|--------|----------------------------------|-----------------------|
| POST   | \`/api/v1/documents\`              | Upload document       |
| GET    | \`/api/v1/documents/{id}\`         | Get document by ID    |
| GET    | \`/api/v1/documents\`              | List all documents    |
| GET    | \`/api/v1/documents/search\`       | Search by keyword     |
| DELETE | \`/api/v1/documents/{id}\`         | Delete document       |

### 👥 User Management
| Method | Endpoint               | Description           |
|--------|------------------------|-----------------------|
| GET    | \`/api/v1/users/{id}\`   | Get user by ID        |
| PUT    | \`/api/v1/users/{id}\`   | Update user           |
| DELETE | \`/api/v1/users/{id}\`   | Delete user           |

### 🗂️ File Storage (Admin Only)
| Method | Endpoint                      | Description            |
|--------|-------------------------------|------------------------|
| POST   | \`/api/v1/storage/upload\`      | Upload system file     |
| GET    | \`/api/v1/storage/files\`       | List system files      |
| DELETE | \`/api/v1/storage/files\`       | Delete all system files|

---

## 🧾 Database Tables

- \`user\`
- \`roles\`
- \`user_roles\`
- \`documents\`
- \`document_content\`
- \`document_keywords\`
- \`document_type\`

---

## 📦 Repository Includes

- ✅ Full Source Code (\`/src\`)
- 📬 Postman Collection (\`/postman\`)
- 🧩 SQL Dump File (\`/database.sql\`)
- 📄 Docs & Design Overview (\`/docs\`)
- 📘 \`README.md\` (this file)



