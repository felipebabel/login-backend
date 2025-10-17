# Login Backend

# Secure User, Account, and Data Management Platform

This project is a full-featured back-end for a secure web platform focused on user authentication, account management, and role-based access control (RBAC). It demonstrates a clean and maintainable **Hexagonal Architecture** (Ports & Adapters) with modern security practices.

The platform uses **JWT and Refresh Tokens** for authentication, **Bcrypt** for password encryption, and provides scheduled maintenance tasks such as log cleanup, account inactivation, and token cleanup.

## 🧩 Architecture Overview

The back-end is designed using **Hexagonal Architecture**, ensuring that business logic is isolated from frameworks and technical implementations.

Key components:

### 🔐 Authentication & Authorization

* JWT + Refresh Token (valid for 24h)
* Password encryption using Bcrypt
* Role-Based Access Control (RBAC): `ADMIN`, `ANALYST`, `USER`

### 🗂 Admin Dashboard Features

* Manage accounts (Active, Inactive, Pending, Blocked, Active Sessions)
* View and filter activity logs
* Modify global settings: password expiration, JWT expiration
* Analytical charts: total accounts, monthly account growth, login success/failure ratio

### 📊 Analyst Dashboard Features

* Read-only access to dashboard
* Shortcut to user profile

### 👤 User Dashboard Features

* Personal profile management
* Password change
* View own activity log
* Delete account

### 🛠 Scheduled Tasks

* Log cleanup (older than 180 days)
* Inactivate accounts without access for 30 days
* Delete expired password reset tokens

---

## Key Features

* ✅ JWT Authentication with Refresh Tokens
* ✅ Password Encryption using Bcrypt
* ✅ Role-Based Access Control (RBAC)
* ✅ Hexagonal Architecture (Clean Architecture)
* ✅ Scheduler for system maintenance
* ✅ Swagger Documentation for API endpoints
* ✅ Multi-language support: English, Portuguese, Spanish, German
* ✅ Email sending via Brevo for account activation and password recovery
* ✅ Docker-ready deployment

---

## 💻 Tech Stack

* Java 17 (Spring Boot)
* Maven
* PostgreSQL
* Docker & Docker Compose
* Swagger for API documentation
* Brevo (Sendinblue) for email services
* Spring Scheduler for cron jobs

---

## 🚀 Getting Started

### Prerequisites

* Java 17+
* Maven
* Docker & Docker Compose
* PostgreSQL

### Clone the repo

```bash
git clone git@github.com:felipebabel/login-backend.git
cd login-backend
```

### Configure environment

Before running the application, make sure to update the values in application.yml for your database, JWT, and email configuration. Alternatively, you can update the settings directly in the docker-compose.yml file if you prefer running via Docker.

---

## 📋 Testing the API

Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

You can test all endpoints including:

* `/api/v1/auth/login` → Login with username and password
* `/api/v1/auth/register` → Create a new account
* `/api/v1/auth/validate-code` → Validate activation or password recovery codes
* `/api/v1/users` → Admin/Analyst endpoints for account management
* `/api/v1/logs` → View system logs (Admin only)

---

## ❤️ Health Check

```bash
curl --location 'http://localhost:8080/actuator/health'
```

---

## 🔗 Front-End Repository

The front-end of this platform can be found here: [Login](https://felipebabel.github.io/login/)

---