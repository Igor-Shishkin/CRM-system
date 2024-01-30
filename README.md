# Welcome to my final java course project.

### Used technologies: Java (Spring Boot 3.15), Angular 16, mySQL

#### Instructions on how to launch the application are in HELP.md file, here I describe the general capabilities

### Welcome to the CRM System Help Guide! This document aims to provide you with detailed information on how to use and manage the CRM system effectively.

## User Guide

### Navigating the Dashboard

- **Navigation Menu**: <br>Using the navigation menu, which is located at the top of the screen, you can 
go to the home page ('Home' button), where you can select the section you are interested in. 
Or immediately click the 'Actions' button and select a section from the drop-down list.<br>
On the right side of the navigation menu you can use the 'Profile' and "Log out" icons.
- **Notes panel**<br>
  On the left is your notes panel. Notes are sorted by creation time.
  By clicking on a note you can edit it - mark it as important or as done, change the text or set the deadline.
  By clicking on the filter button, you can turn on and off sorting filters - for a specific section, for a specific entity, only important or only not done. Filters can be combined.
  The new message button allows you to create a new message
- **Main workspace**
  Here you can perform basic actions for working with the CRM system. 
  From the HOME page you can select the branch you are interested in. 
  At the moment, only the CLIENTS, LEADS and ADMINISTRATION sections are active, the rest are still under development.

## Managing (USER_ROLE)

### Working with clients
The system provides all the tools for storing the necessary information, maintaining logs and promoting clients*.

   1. **Pages CLIENTS, LEADS, BLACKLIST:**<br>
 - you can view a list of clients in the appropriate status, go to individual client information 
   (restore a client if they are on the blacklist)<br><br>

2. **Page CLIENT'S WORKPLACE:**<br>
 - You can view and edit information about the client, or send him to the blacklist.
 - There will also be three small buttons on the bottom right, with which you can create a note about this client, 
   create a new order or email him. <br>
 - Also here you can see all the client’s orders with basic details. If the order is paid, the message PAID appears
   By clicking on the order you can go to the order page.
3. **Page ORDER'S WORKPLACE:**<br>
 - By creating a new order or logging into an existing one, you will be taken to the order page.
   Here you will have basic information about the client and all the necessary information to promote the order.
 - As you go through new stages of working with the client, you will see a progress bar showing how much you 
   have done.
 - On the right side you will see green buttons. With its help, you can make a purchase list of items or materials 
   for order, confirm the signing of the contract, confirm payment for the order.
 - You can also send an email to the client

_You cannot sign an agreement if the purchase sheet is incorrectly drawn up (correct data is missing) and without 
showing the calculations to the client. You also cannot confirm payment for the order without a signed contract._

_*A marketing lead is a person who shows interest in a brand's products or services, 
which makes the person a potential customer. If the order is paid, the client automatically receives the 
'CLIENT' status._

## Administrating (ADMIN_ROLE)

If you have administrator rights, then the administration branch is available to you.
Here you can view all users, add them and delete them. 
<br>Be very careful - deleting a user deletes all the information with which he worked.

### General
all users can use THEIR notes and view their profile data.<br><br><br>

#### I wish you pleasant and productive use of the program!

