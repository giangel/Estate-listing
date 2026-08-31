`README.md`.

```markdown
# Estate Listing System

A full-featured real estate listing web application built with **Java (Jakarta Servlets + JSP)** and **PostgreSQL**. It supports multiple user roles - Admin, Agent, Landlord, and regular Users - and includes property listings, roommate matching, appointments, inquiries, reviews, wishlists, notifications, fraud reporting, and audit logging.

## Features

- **Authentication**: Register, login, logout, forgot/reset password (with hashed passwords via jBCrypt)
- **Admin**: Dashboard, manage properties, verify agents, approve properties, view audit logs, handle fraud reports, generate reports, manage users
- **Agent**: Dashboard, manage clients
- **Landlord**: Dashboard, create/update/delete properties, view property performance
- **User**: Dashboard, browse/search/compare/feature properties, book appointments, send inquiries, leave reviews & ratings, manage wishlist, view recently viewed listings, receive notifications
- **Roommate Matching**: Create a roommate profile and view matches
- **File uploads** for property images/documents

## Tech Stack

| Layer          | Technology                                  |
|----------------|----------------------------------------------|
| Language       | Java 17                                       |
| Web Layer      | Jakarta Servlets 6.0, JSP + JSTL 3.0          |
| Build Tool     | Maven (packaged as `.war`)                    |
| Database       | PostgreSQL (JDBC driver 42.7.4)               |
| Security       | jBCrypt (password hashing)                    |
| App Server     | Apache Tomcat 10.1.x                          |
| Containerization | Docker (multi-stage build included)         |

## Prerequisites

Before you start, install the following:

1. **JDK 17** - [Eclipse Temurin JDK 17](https://adoptium.net/)
2. **Eclipse IDE for Enterprise Java and Web Developers** (this bundle includes Maven and WTP - Web Tools Platform)
3. **Apache Tomcat 10.1.x** - [Download here](https://tomcat.apache.org/download-10.cgi)
4. **PostgreSQL 12+** - [Download here](https://www.postgresql.org/download/)
5. **Git**

## Installation & Setup (Eclipse)

### 1. Clone the repository
```bash
git clone https://github.com/giangel/Estate-listing.git
```

### 2. Import the project into Eclipse
1. Open Eclipse.
2. Go to **File → Import → Maven → Existing Maven Projects**.
3. Click **Next**, then **Browse** and select the cloned `Estate-listing` (or `estate`) folder.
4. Make sure the `pom.xml` is checked, then click **Finish**.
5. Eclipse will import the project and download the Maven dependencies (PostgreSQL driver, JSTL, jBCrypt, Servlet API).

### 3. Set up the database
1. Open **pgAdmin** or the `psql` CLI and create the database:
   ```sql
   CREATE DATABASE realestate_aop;
   ```
2. Create the tables your DAO classes expect (Properties, Users, Appointments, Inquiries, Reviews, Notifications, Roommate profiles, Audit logs, Fraud reports, etc.) - restore your own schema/dump if you have one exported from your working environment.
3. Note the default connection settings baked into `DBConnection.java` (used only if the matching environment variable isn't set):

   | Setting     | Default value                                      |
   |-------------|------------------------------------------------------|
   | `DB_URL`    | `jdbc:postgresql://localhost:5432/realestate_aop`     |
   | `DB_USER`   | `postgres`                                           |
   | `DB_PASSWORD` | `3693`                                              |

   You can override these with environment variables instead of editing the source:
   ```bash
   export DB_URL=jdbc:postgresql://localhost:5432/realestate_aop
   export DB_USER=postgres
   export DB_PASSWORD=your_password
   ```
   In Eclipse, you can also set these under your server's **Run Configuration → Environment** tab so Tomcat picks them up at launch.

### 4. Add a Tomcat server to Eclipse
1. Go to **Window -> Show View -> Servers**.
2. Right-click in the Servers panel -> **New -> Server**.
3. Select **Apache -> Tomcat v10.1 Server**, click **Next**.
4. Browse to your local Tomcat 10.1 installation directory, click **Finish**.

### 5. Deploy the project to the server
1. In the Servers view, right-click your Tomcat server → **Add and Remove...**
2. Move **estate** (the project) from *Available* to *Configured*, click **Finish**.
3. Right-click the server → **Start** (or **Debug**, if you want to debug).

### 6. Access the application
Once Tomcat starts successfully, open your browser and go to:
```
http://localhost:8080/estate/
```
(The context root is `estate`, as configured in the Eclipse WTP module settings.)

## Alternative: Run with Docker

A `Dockerfile` is included if you'd rather skip Eclipse/Tomcat setup:

```bash
docker build -t estate-listing .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:postgresql://<db-host>:5432/realestate_aop \
  -e DB_USER=postgres \
  -e DB_PASSWORD=your_password \
  estate-listing
```

The app will be available at `http://localhost:8080/` (deployed as `ROOT.war` in the container, so no context path is needed here).

> Note: You still need a reachable PostgreSQL instance - either running on your host machine or as a separate container - since the app image only bundles the web application, not the database.

## Email Notifications

Email sending is currently **simulated** - `EmailUtil.java` prints messages to the console instead of sending real emails. To enable real email delivery, configure your SMTP credentials (host, port, auth) in `EmailUtil.java` and uncomment the JavaMail sending logic.

## Project Structure

```
estate/
├── src/
│   ├── main/
│   │   ├── java/com/realestate/
│   │   │   ├── dao/        # Data access objects (Property, User, Review, etc.)
│   │   │   ├── model/      # Entity/model classes
│   │   │   ├── servlet/    # Servlets (admin, agent, landlord, user, auth, etc.)
│   │   │   └── util/       # DBConnection, EmailUtil, and other utilities
│   │   └── webapp/         # JSP pages, static assets, WEB-INF
├── pom.xml
├── Dockerfile
└── README.md
```

## License

This Project belongs to the department of computer science, Adeseun Ogundoyin Polytechnic, Eruwa, Oyo State, Nigeria.
```
