# Document Management System

## Overview

The Document Management System is a Spring Boot-based web application that allows users to securely manage and search documents. Users can upload, download, update, and delete documents based on their roles. This application utilizes JWT-based authentication, MySQL for data storage, and provides a comprehensive RESTful API for interaction.

## Features

- Add a New Document: Upload documents with metadata such as title, description, and keywords
- Retrieve All Documents: Fetch a list of all accessible documents in the database
- Retrieve a Document by ID: Get detailed information about a specific document
- Update a Document: Modify the metadata of an existing document
- Delete a Document: Remove a document from the database
- Search Documents: Find documents by keywords or content
- Q&A-based Search: Ask questions and get answers based on document content
- Role-based Access Control: Different permissions for User and Admin roles
- JWT Authentication: Secure token-based authentication system

## Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Security (JWT)**
- **Spring Data JPA (Hibernate)**
- **MySQL**
- **Maven**
- **Postman** (for API testing)

## Project Structure

```
src/
├── config/          # Security configuration (JWT, CORS)
├── controller/      # REST API endpoints
├── dto/             # DTOs for API requests/responses
├── entity/          # JPA entities (database models)
├── repository/      # Spring Data JPA repositories
├── service/         # Service layer (business logic)
├── util/            # Utility classes
└── main/            # Spring Boot entry point
```

## Database Setup (MySQL)

1. Create a database/schema in MySQL:
   ```sql
   CREATE DATABASE document_management_system;
   ```

2. Update your credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/document_management_system
   spring.datasource.username=root
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

> Tables will be created automatically on application startup.

## Authentication Flow

### Signup
```
POST /api/v1/auth/signup
```
Registers a new user.

### Login
```
POST /api/v1/auth/login
```
Returns:
- Access Token
- Refresh Token
- User Info
- Expiry Time

### Access Secured APIs

Use header:
```
Authorization: Bearer <access_token>
```

### Refresh Token
```
POST /api/v1/auth/refresh
```

### Logout
```
POST /api/v1/auth/logout
```

## API Endpoints Overview

### Authentication
| Method | Endpoint               | Description         |
|--------|------------------------|---------------------|
| POST   | `/api/v1/auth/signup`  | Register user       |
| POST   | `/api/v1/auth/login`   | Login user          |
| POST   | `/api/v1/auth/refresh` | Refresh JWT token   |
| POST   | `/api/v1/auth/logout`  | Logout user         |

### Document Management
| Method | Endpoint                         | Description           |
|--------|----------------------------------|-----------------------|
| POST   | `/api/v1/documents`              | Upload document       |
| GET    | `/api/v1/documents/{id}`         | Get document by ID    |
| GET    | `/api/v1/documents`              | List all documents    |
| GET    | `/api/v1/documents/search`       | Search by keyword     |
| DELETE | `/api/v1/documents/{id}`         | Delete document       |

### User Management
| Method | Endpoint               | Description           |
|--------|------------------------|-----------------------|
| GET    | `/api/v1/users/{id}`   | Get user by ID        |
| PUT    | `/api/v1/users/{id}`   | Update user           |
| DELETE | `/api/v1/users/{id}`   | Delete user           |

### File Storage (Admin Only)
| Method | Endpoint                      | Description            |
|--------|-------------------------------|------------------------|
| POST   | `/api/v1/storage/upload`      | Upload system file     |
| GET    | `/api/v1/storage/files`       | List system files      |
| DELETE | `/api/v1/storage/files`       | Delete all system files|

## Database Tables

- `user`
- `roles`
- `user_roles`
- `documents`
- `document_content`
- `document_keywords`
- `document_type`

## Steps to Set Up the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/abhishek6377/ManagementApp.git
   cd ManagementApp
   ```

2. Set up the MySQL database:
   ```sql
   CREATE DATABASE document_management_system;
   ```

3. Configure application.properties with your database credentials:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/managementapp
   spring.datasource.username=root
   spring.datasource.password=root
   ```

4. Build and run the application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. The application will be available at `http://localhost:8080`

## Repository Includes

- ✅ Full Source Code (`/src`)
- 📬 Postman Collection (`/postman`)
- 🧩 SQL Dump File (`/database.sql`)
- 📄 Docs & Design Overview (`/docs`)
- 📘 `README.md` (this file)
