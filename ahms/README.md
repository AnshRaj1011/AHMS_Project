# 🏥 Advanced Hospital Management System (AHMS)

A full-stack hospital management system built with **Java Spring Boot**, **Python microservices**, and **HTML/CSS/JS** frontend.

---

## 📦 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17 + Spring Boot 3.2 |
| Security | JWT + Spring Security |
| Database | MySQL 8 |
| Real-time Chat | WebSocket (STOMP) |
| Python Services | FastAPI (QR + Chatbot) |
| Frontend | HTML5 + CSS3 + Vanilla JS |

---

## 🗂️ Project Structure

```
ahms/
├── src/main/java/com/ahms/
│   ├── config/          # Security, WebSocket, WebClient configs
│   ├── controller/      # REST API controllers (12 modules)
│   ├── entity/          # JPA entities
│   ├── exception/       # Global exception handler
│   ├── repository/      # Spring Data JPA repos
│   ├── security/        # JWT filter, UserDetailsService
│   └── service/         # Business logic services
├── src/main/resources/
│   └── application.properties
├── python/
│   ├── qr_service/      # QR code generator (port 5001)
│   └── chatbot/         # AI chatbot service (port 5002)
├── frontend/
│   ├── pages/           # login.html, dashboard.html, admin.html
│   ├── css/style.css
│   └── js/              # api.js, dashboard.js, admin.js
└── pom.xml
```

---

## ✅ Modules

1. **User Registration & Auth** — JWT-based login, role-based access (Patient/Doctor/Admin/Emergency Staff)
2. **Patient Profile & QR** — Auto registration ID + QR code generation
3. **Doctor Management** — Profiles, specialization, availability
4. **Appointment Booking** — Conflict detection, virtual/in-person, doctor notes
5. **Bed Management** — Real-time ward/ICU bed availability, admit/discharge
6. **ICU Emergency Quota** — Emergency request → admin approve/reject → auto bed assignment
7. **Lab Test Booking** — MRI, CT, Blood Test, X-Ray etc. with price catalog
8. **Ambulance Dispatch** — Emergency requests → staff dispatch with ETA tracking
9. **Live Chat & Bot** — WebSocket doctor chat + rule-based chatbot (Python fallback)
10. **Admin Dashboard** — Full stats, manage all modules
11. **QR Code Service** — Python FastAPI service generates patient QR codes
12. **Hospital Map** — Floor-wise department guide in chatbot

---

## 🚀 Setup Instructions

### 1. MySQL
```sql
CREATE DATABASE ahms_db;
```
Update `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 2. Run Spring Boot Backend
```bash
cd ahms
mvn spring-boot:run
# API runs at http://localhost:8080/api
```

### 3. Run Python QR Service
```bash
cd python
pip install -r requirements.txt
uvicorn qr_service.main:app --port 5001
```

### 4. Run Python Chatbot Service
```bash
uvicorn chatbot.main:app --port 5002
```

### 5. Open Frontend
Open `frontend/pages/login.html` in your browser (use VS Code Live Server for best results).

---

## 🔐 Default API Endpoints

| Method | Endpoint | Access |
|---|---|---|
| POST | /api/auth/register | Public |
| POST | /api/auth/login | Public |
| GET | /api/patients | Admin |
| POST | /api/appointments | Patient/Admin |
| GET | /api/beds/available | All authenticated |
| POST | /api/icu/request | Patient |
| POST | /api/ambulance/public/request | Public |
| POST | /api/chat/bot | Public |
| GET | /api/admin/dashboard | Admin only |

---

## 👥 Team Avengers
- **Ansh Raj** (Team Lead) — 2418286
- Aditya Chamoli — 2418163
- Ayush Yadav — 2419439
- Sumit Bhandari — 2419032

**Graphic Era Hill University, Dehradun**
