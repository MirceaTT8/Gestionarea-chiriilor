Aplicație web pentru gestionare chiriilor, dezvoltată ca lucrare de licență

Baza de Date
Dezvoltare și demo: H2 in-memory database
Producție: PostgreSQL
ORM: Spring Data JPA cu Hibernate

Backend
Java 21 + Spring Boot 3.4.3
Spring Security - autentificare JWT
Spring Data JPA - persistența datelor
H2 Database (dezvoltare și demo) / PostgreSQL (producție)
Maven - build tool
Frontend
Vue.js 3 - framework reactive
Vite - build tool modern
TailwindCSS - styling responsive

🚀 Instalare și Rulare
Cerințe de sistem
Java 21+
PostgreSQL 13+
Node.js 18+
Maven 3.6+

# Clonează repository-ul
git clone [<url-repo>](https://github.com/MirceaTT8/Gestionarea-chiriilor/)
cd Lease-Management

# Backend (Spring Boot)
cd backend
mvn spring-boot:run

# Frontend (Vue.js) - terminal nou
cd frontend  
npm install
npm run dev
