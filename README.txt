Pharmacy Inventory Management System (PIMS)
User Manual

Quick Start For Windows

Option 1: Build the Windows executable

Run:

`scripts\package_windows_exe.bat`

Then start:

`dist\PIMS-1.0.exe`

Option 2: Run directly without packaging

Compile:

`if not exist out mkdir out`

`for /r src %f in (*.java) do @echo %f >> sources.txt`

`javac -cp "lib\mysql-connector-j.jar" -d out @sources.txt`

Run:

`java -cp "out;lib\mysql-connector-j.jar" ui.Main`

Default login:
- Admin: `admin` / `admin123`
- Cashier: `cashier` / `cash123`


1. System Overview

The Pharmacy Inventory Management System (PIMS) is a Java desktop application developed for managing pharmacy operations. The system uses a Swing graphical user interface and connects to a MySQL database through JDBC.

The application supports two user roles:
- Admin
- Cashier

Main functions of the system:
- Secure login with role-based access
- Medicine management
- Supplier management
- User management
- Point of Sale (POS) for sales processing
- Business reports for sales, stock levels, and expiry tracking


2. How To Run The App

Before running the system, make sure the following are available:
- Java 17
- MySQL Connector/J jar file in the `lib` folder
- MySQL database running

If the private MySQL server included with the project is being used, start it with:

`./scripts/start_mysql.sh`

This project uses Option 2 for running the application.

Option 2: Run using the compiled classes and classpath command

Step 1: Compile the project

`mkdir -p out`

`find src -name "*.java" | xargs javac -cp "lib/mysql-connector-j.jar" -d out`

Step 2: Run the application

`java -cp "out:lib/mysql-connector-j.jar" ui.Main`

Note:
- The MySQL JDBC driver jar must be inside the `lib` folder.
- The command above assumes the driver file is named `mysql-connector-j.jar`.
- If your jar file uses a versioned name, replace `mysql-connector-j.jar` in the command with the exact filename.

Example:
- `lib/mysql-connector-j-9.3.0.jar`
- Run command: `java -cp "out:lib/mysql-connector-j-9.3.0.jar" ui.Main`


2.1 Packaging And Executable Setup

This project includes packaging scripts in the `scripts` folder.

Linux app image:
- `./scripts/package_app_image.sh`
- Output folder: `dist/PIMS`

Windows executable:
- `scripts\package_windows_exe.bat`
- Output file: `dist\PIMS-1.0.exe`

Important note:
- A real `.exe` file can only be generated on Windows using `jpackage`.
- From this Linux environment, the provided packaging script can create a native Linux app image, not a Windows `.exe`.
- The Windows batch script is already prepared for building the `.exe` on a Windows machine with Java 17 and `jpackage` installed.


3. Default Login Credentials

Administrator account:
- Username: `admin`
- Password: `admin123`

Cashier account:
- Username: `cashier`
- Password: `cash123`


4. How To Login

1. Run the application.
2. The Login screen will appear.
3. Enter your username and password.
4. Click the `Login` button.
5. If the credentials are correct:
- Admin users are redirected to the Admin Dashboard.
- Cashier users are redirected to the Cashier Dashboard.
6. If the credentials are incorrect, an error message will be displayed.


5. How To Manage Medicines

This function is available to Admin users.

1. Log in as an Admin.
2. Open the `Medicines` tab in the Admin Dashboard.
3. The table will display all medicines currently stored in the database.
4. To add a medicine:
- Click `Add`
- Enter the medicine details
- Click the save button
5. To edit a medicine:
- Select a medicine from the table
- Click `Edit`
- Update the details
- Click the save button
6. To delete a medicine:
- Select a medicine from the table
- Click `Delete`
- Confirm the action

The medicine table refreshes after add, edit, and delete operations.


6. How To Process A Sale

This function is available to Cashier users.

1. Log in as a Cashier.
2. The POS screen will open.
3. Use the search field to find a medicine.
4. Select a medicine from the medicines table.
5. Click `Check Stock` if you want to view the item price and available quantity.
6. Click `Add To Cart`.
7. Enter the quantity required.
8. The selected medicine will be added to the cart.
9. Repeat the process for additional items.
10. The total amount will update automatically.
11. Click `Checkout` to complete the sale.
12. After a successful checkout:
- the sale is saved in the database
- stock is reduced automatically
- a bill window is displayed
- the cart is cleared

The bill window also allows the user to:
- Print the bill
- Save the bill as a text file


7. How To View Reports

This function is available to Admin users.

1. Log in as an Admin.
2. Open the `Reports` tab in the Admin Dashboard.
3. Use the report selector to choose one of the following reports:
- Sales
- Item-wise
- Low stock
- Expiry
4. The table refreshes automatically when a report is selected.

Report descriptions:
- Sales report: displays total sales grouped by date
- Item-wise report: displays total quantity sold grouped by medicine
- Low stock report: shows medicines where quantity in stock is less than or equal to reorder level
- Expiry report: shows medicines expiring within the next 30 days


8. Database Notes

Database name:
- `pims_db`

Application database user:
- Username: `pims_user`
- Password: `pims123`

SQL setup file:
- `database.sql`


9. Project Entry Point

Main class:
- `src/ui/Main.java`
