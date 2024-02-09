# Welcome to my final java course project.
### Used technologies: Java (Spring Boot 3.15), Angular 16, mySQL)

### The main features are described in Readme file in GitHub repository. Here I will describe the installation.

<br><br>

## Sequence of actions to start the project:
#### Download the project using GitHub CLI = https://github.com/Igor-Shishkin/CRM-system.git
#### [Optional] Set up src/main/resources/application.properties
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

<br><br><br><br>

#### Start database container
In one terminal window run `cd server && docker-compose up`

#### Start server application
In another terminal window run `mvn clean install -U -DskipTests && mvn spring-boot:run`

#### Start the Angular application
In yet another terminal window run (if you haven't try terminator!) `npm install && ng serve --port 8081`
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








