# Gestionarea Chiriilor

Aplicație web pentru gestionare chiriilor, dezvoltată ca lucrare de licență.

## Repository
**GitHub:** https://github.com/MirceaTT8/Gestionarea-chiriilor.git

## Descriere Tehnică

**Baza de Date:**
- Dezvoltare și demo: H2 in-memory database
- Producție: PostgreSQL
- ORM: Spring Data JPA cu Hibernate

**Backend:**
- Java 21 + Spring Boot 3.4.3
- Spring Security - autentificare JWT
- Spring Data JPA - persistența datelor
- H2 Database (dezvoltare și demo) / PostgreSQL (producție)
- Maven - build tool

**Frontend:**
- Vue.js 3 - framework reactive
- Vite - build tool modern
- TailwindCSS - styling responsive

## Cerințe de Sistem

🚀 **Instalare și Rulare:**
- Java 21+
- PostgreSQL 13+
- Node.js 18+
- Maven 3.6+

## Clonează Repository-ul

```bash
git clone https://github.com/MirceaTT8/Gestionarea-chiriilor.git
cd Gestionarea-chiriilor
```

## Backend (Spring Boot)

```bash
cd backend
mvn spring-boot:run
```

Serverul va porni pe: `http://localhost:8080`

## Frontend (Vue.js) - terminal nou

```bash
cd frontend
npm install
npm run dev
```

Aplicația va porni pe: `http://localhost:3000`

## Accesare Aplicație

Navigați la: **http://localhost:3000**
