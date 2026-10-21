# CampusFind — Smart Campus Lost & Found Portal

CampusFind is a web-based Lost & Found platform designed to help students report lost items and find belongings within a college campus.

## Features

- User registration and login
- JWT-based authentication
- Secure password hashing with BCrypt
- Report lost or found items
- View, search, and filter items
- Manage personal reports
- Role-based access for users and admins
- Admin-only endpoints

## Tech Stack

**Frontend:** HTML, CSS, JavaScript

**Backend:** Java, Spring Boot, Spring Security, Spring Data JPA, Hibernate

**Database:** MySQL

**Authentication:** JWT, BCrypt

**Build Tool:** Maven

## Project Structure

- `backend/CampusFind/` — Spring Boot backend
- `frontend/` — Frontend pages and assets

## Setup

1. Install Java 21, Maven, and MySQL.
2. Create the required MySQL database.
3. Configure database credentials and the JWT signing secret using environment variables or local configuration.
4. Run the Spring Boot backend.
5. Open the frontend using a local development server.
6. Register an account and log in to use the application.

## Security Note

Never commit real database passwords, JWT signing secrets, access tokens, or other private credentials to GitHub.

## Author

Piyush Kumar Pandit
