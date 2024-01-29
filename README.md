# Welcome to my final java course project.
### Used technologies: Java (Spring Boot 3.15), Angular 16, mySQL)

### The main features are described in the file. Here I will describe the installation.

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

### You can launch the program in several ways, I will tell you how to do it using the IntelliJ

### Open project in IntelliJ
 - Run it (usual running - Shift+F10) 
 - Open terminal (in IntelliJ)
 - Go to angular-app folde with __command cd .\angular-app\___
 - Download the necessary npm and Angular packages:
     - **npm install**
     - 
