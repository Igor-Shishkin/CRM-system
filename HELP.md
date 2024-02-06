# Welcome to my final java course project.
### Used technologies: Java (Spring Boot 3.15), Angular 16, mySQL)

### The main features are described in Readme file in GitHub repository. Here I will describe the installation.

<br><br>

## Sequence of actions to start the project:
#### Upload the project using GitHub CLI = https://github.com/Igor-Shishkin/CRM-system.git
#### Set up src/main/resources/application.properties
- enter your username and password for the SQL database:<br>
  spring.datasource.username=**YOUR_SQL_USER_NAME**<br>
  spring.datasource.password=**YOUR_SQL_PASSWORD**
- if necessary, change the database name<br>
- you can set the address and password for your mailbox to send letters:<br>
  spring.mail.username=**YOUR_MAILBOX_USERNAME**<br>
  spring.mail.password=**YOUR_MAILBOX_PASSWORD**<br>
  *password - you need to set a special code in your mail settings to log in from applications*
- if you want, you can change the initial data for the administrator (the user is automatically added to the database to add new ones)<br>
  app.crm.admin.name=admin<br>
  app.crm.admin.password=000000

#### To get acquainted with the project's capabilities, I recommend that you run the scripts in the file *src/main/resources/sql_schema.sql*
- Basic data will be loaded from it, thanks to which you will see how the system operates. If you don't do this, you will still have to manually create the database and tables.

<br><br><br><br>

### You can launch the program in several ways, I will tell you how to do it using the IntelliJ

### Open project in IntelliJ
#### Run the Spring Boot application with IntelliJ (usual running - Shift+F10)
#### Download the necessary npm and Angular packages:
- Open terminal (in IntelliJ)
- enter the following commands into the terminal
    - **cd .\angular-app\\**
    - **npm install -g @angular/cli@16**
    - **npm install bootstrap@4.6.2**
    - **npm install --force**
    - **ng add @angular/material**

### Run the Angular application entering the following commands into the terminal
- __ng serve --port 8081__
### If everything is installed successfully, you can open the browser and launch the application by entering in the address bar:
- **http://localhost:8081/**
#### You can log in as administrator (default) with:
- login: admin
- password: 000000

##### If you want to test the application and have loaded data from src/main/resources/sql_schema.sql file into the database, you can also log in as the following users:
- with USER_ROLE:
    - login: user
    - password: 000000
- with USER_ROLE and ADMIN_ROLE:
    - login: user-admin
    - password: 000000

### You can also use Swagger Tools by clicking on the following link in your browser:
- http://localhost:8080/swagger-ui/index.html#/

#### I wish you pleasant and productive use of the program!








