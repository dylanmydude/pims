Pharmacy Inventory Management System (PIMS)

Technology Stack
- Java 17
- Swing (GUI)
- JDBC (MySQL)
- MySQL Connector/J (external JDBC driver)

Project Modules
- Authentication
- Admin
- POS
- Reports

Roles
- Admin
- Cashier

Layered Architecture
- src/ui
- src/dao
- src/model
- src/service
- src/utils

Current State
- Project structure created
- Login flow added with Swing + JDBC
- Database schema and seed script added

Entry Point
- src/ui/Main.java

Project Layout
- src/
- lib/
- out/
- database.sql

JDBC Driver Setup
- Download MySQL Connector/J and place the jar file inside the lib folder.
- Example jar name: lib/mysql-connector-j.jar

Local Database Server
- A private MySQL server has been set up under vendor/mysql/.
- Start it with: ./scripts/start_mysql.sh
- Stop it with: ./scripts/stop_mysql.sh
- App database user: pims_user
- App database password: pims123

Compile
- mkdir -p out
- find src -name "*.java" | xargs javac -cp "lib/mysql-connector-j.jar" -d out

Run
- java -cp "out:lib/mysql-connector-j.jar" ui.Main

Database Setup
- The schema in database.sql has already been imported into the local MySQL instance.
- Current JDBC URL in DBConnection.java:
- jdbc:mysql://127.0.0.1:3306/pims_db?useSSL=false&serverTimezone=UTC

Seed Login Accounts
- Admin: admin / admin123
- Cashier: cashier / cash123
