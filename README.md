# MediCore – Hospital Management System

## Overview

Advanced Hospital Management System (AHMS) is a full-stack healthcare management platform developed using Java Spring Boot. The system helps hospitals manage patients, doctors, appointments, beds, ICU requests, ambulance services, laboratory tests, authentication, and real-time communication through a centralized dashboard.

The project aims to automate hospital operations, improve efficiency, and provide a secure and user-friendly experience for administrators, doctors, and patients.

---

## Features

### Authentication & Security

* JWT-based Authentication
* Spring Security Integration
* Secure Login and Registration
* Role-Based Access Control

### Patient Management

* Add New Patients
* Update Patient Records
* View Patient Details
* Delete Patient Records

### Doctor Management

* Add Doctors
* Manage Doctor Information
* View Doctor Profiles

### Appointment Management

* Book Appointments
* Manage Appointment Status
* View Appointment History

### Bed Management

* Track Available Beds
* Allocate Beds to Patients
* Monitor Bed Occupancy

### ICU Management

* ICU Bed Requests
* ICU Availability Tracking

### Ambulance Management

* Ambulance Request System
* Emergency Service Tracking

### Laboratory Management

* Lab Test Requests
* Test Record Management
* Report Tracking

### Real-Time Communication

* WebSocket-Based Chat System
* Instant Messaging Support

### Additional Services

* QR Code Service Integration
* Python Chatbot Support

---

## Technology Stack

### Backend

* Java 17
* Spring Boot
* Spring Security
* Spring Data JPA
* WebSocket
* JWT Authentication
* Maven

### Database

* MySQL

### Frontend

* HTML
* CSS
* JavaScript

### Additional Services

* Python
* QR Code Service

---

## Project Structure

```text
AHMS_Project
│
├── frontend
│   ├── css
│   ├── js
│   └── pages
│
├── python
│   ├── chatbot
│   └── qr_service
│
├── src
│   └── main
│       ├── java
│       │   └── com.ahms
│       └── resources
│
└── pom.xml
```

---

## Database Configuration

Update the database configuration inside:

```properties
src/main/resources/application.properties
```

Example:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ahms
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Installation & Setup

### Clone Repository

```bash
git clone https://github.com/AnshRaj1011/AHMS_Project.git
```

### Navigate to Project

```bash
cd AHMS_Project
```

### Configure MySQL Database

Create a database:

```sql
CREATE DATABASE ahms;
```

### Run Backend

```bash
mvn spring-boot:run
```

### Access Application

```text
http://localhost:8080
```

---

## API Modules

* Authentication API
* Patient API
* Doctor API
* Appointment API
* Bed API
* ICU API
* Ambulance API
* Laboratory API
* Chat API
* Admin API

---

## Future Enhancements

* Email Notifications
* SMS Alerts
* Online Payments
* Telemedicine Integration
* AI-Based Diagnosis Assistance
* Cloud Deployment
* Mobile Application Support

---

## Learning Outcomes

This project demonstrates practical implementation of:

* Spring Boot Application Development
* REST API Design
* JWT Authentication
* Database Management using JPA
* WebSocket Communication
* Secure Backend Architecture
* Full Stack Development Concepts

---

## Author

**Ansh Raj**

GitHub: https://github.com/AnshRaj1011

---

## License

This project is developed for educational and academic purposes.
