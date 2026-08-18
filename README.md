# 📚 Library Management System

A robust, feature-rich Command Line Interface (CLI) Library Management Application built using **Java**, **JDBC**, and **MySQL**, designed with **HikariCP connection pooling** for high-performance database interactions.

---

## 🌟 Key Features

- **📖 Catalog Management:** Add new books to the library inventory and search existing books by title or author keywords.
- **👤 Member Registration:** Register library members with unique email validation.
- **🔄 Issue & Return Workflow:** Process book borrowings and returns with automated stock management and transaction logging.
- **💰 Overdue Fine Handling:** Check outstanding balances and record fine payments.
- **📋 Audit Logging:** Administrative oversight through system-level event tracking.
- **⚡ High Performance Database Access:** Efficient connection pooling powered by **HikariCP**.

---

## 🛠️ Tech Stack

- **Language:** Java 17+
- **Database:** MySQL 8.0+
- **Database Access:** JDBC & HikariCP
- **Build Tool:** Apache Maven

---

## 📋 Database Schema Setup

Before running the application, set up your MySQL database with the following structure:

```sql
CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- Books Table
CREATE TABLE books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    available_copies INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Members Table
CREATE TABLE members (
    member_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions Table
CREATE TABLE transactions (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id INT,
    book_id INT,
    issue_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    return_date TIMESTAMP NULL,
    fine_amount DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (member_id) REFERENCES members(member_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

-- Audit Logs Table
CREATE TABLE audit_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    action_description VARCHAR(500) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
