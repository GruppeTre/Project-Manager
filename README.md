# Project Calculation Tool

## Getting Started
### Local Installation:
1. Fork the project via GitHub, then clone the newly forked repository.
2. In your IDE of choice create project from version control.

**IDE Setup:**
Setup your environment variables (the following are an example on IntelliJ), others may vary in syntax:
- DEV_DATABASE_URL=jdbc:mysql://localhost:3306/project_manager;
- DEV_USERNAME=username;
- DEV_PASSWORD=password;
- Set active profile=dev

**Database Setup:**
The Database use MySQL version 8.*
1. Download and install the community version of MySql Workbench
2. Setup a localhost database with port: 3306
3. Run the CREATE.sql script found in src/main/resources/SqlScripts
4. Run the INSERT.sql script found in src/main/resources/SqlScripts

You're now set to try out the project calculation tool, simply run the project and go to localhost:8080

### External Installation:
The following is how to get the project up on Azure, we cannot guarantee this process is similar for other providers, but it should be apparent of necessary changes that can be applied to your specific provider.
1. Fork the project via GitHub.
 
**Azure Database Setup**
1. Go through the process of setting up a database.
2. Ensure you get access to the full database url, it is important for the process below.
3. Ensure you securely store your admin username as you need it for the process below.
4. Ensure you securely store your admin password as you need it for the process below.
5. Ensure the IP-address from which you access the database has a firewall exception so you can access it under network.

**Azure Website Setup**
1. Go through the process of setting up a website, if you have access to a free tier - it will work.
2. Once setup ensure the following secrets are set:
Setup your secret environment variables. To ensure that it loads the correct environment variables, you need to ensure you include the SPRING_PROFILES_ACTIVE.
- PROD_DATABASE_URL: jdbc:mysql://YourFullDatabaseUrl/project_manager
- PROD_USERNAME: your database admin username
- PROD_PASSWORD your database admin password
- SPRING_PROFILES_ACTIVE: prod

**Database Setup:**
The Database use MySQL version 8.*
1. Download and install the community version of MySql Workbench
2. Connect to your azure database with port: 3306
3. Run the CREATE.sql script found in src/main/resources/SqlScripts
4. Run the INSERT.sql script found in src/main/resources/SqlScripts

You're now set to try out the project calculation tool, go to your website and check it out!

## Usage
The project is for exam purposes, in order to add data on your own team members, edit the insert script.
This will change post-exam so that the tool no "emulates" an external database for employees.
As the project expects a specific email string namely @alphasolutions, we recommend refactoring the isValidEmail method to not check for this - simply check for @ or omit entirely.
- If you choose do the above, please bear in mind to update the INSERT script to account for the employees you need to add yourself.

The application comes with the following three types of users:
- Admin: admin@alphasolutions.com
  - Password: admin
- Project Leader: maho@alphasolutions.com
  - Password: admin
- Team member: jepa@alphasolutions.com
  - Password: admin

As this application is intended for use in Organizations, we do not allow passwords on your own account to be changed by you.
We highly recommend that your first order of business is to log in to admin@alphasolutions and create a secondary admin account.
Once this admin has been created, login to the new admin and change the password of admin@alphasolutions.com through reset password. It's wonky if you're a small team, we know!

**Features:**
- Ability to add, edit and delete accounts as Admin
- Ability to add, edit, archive and delete projects as Admin
- Ability as Project Leader to view projects assigned to you.
- Ability to add, edit and delete subprojects to a project as Project Leader
- Ability to add, edit, archive and delete Tasks to a subproject as Project leader
- Ability to access basic statistical informations on your project as a Project Leader
- Ability as Team Member to view projects in which you have tasks assigned.

## Code of Conduct
We believe in an inclusive environment.
1. We expect you to use a respectful tone towards others.
2. Discussions are always welcome, but be mindful of the above.

Everyone needs to be able to express their opinions and ideas - big or small in a safe environment.
Failure to adhere to common sense and reason when engaging with others is cause for removal, feedback ignored etc.

## Contributing
To contribute to the project such as creating issues or pull requests please refer to [Contributing][contr]

## Maintaining
The following contributors maintain the project
- [Emil Gurresø][maint1]
- [Jacob Klitgaard][maint2]
- [Jens Gotfredsen][maint3]
- [Magnus Sørensen][maint4]

[maint1]: <https://github.com/404BrainNotFound1000>
[maint2]: <https://github.com/jacobklitgaard>
[maint3]: <https://github.com/WiiTee>
[maint4]: <https://github.com/MaVirgil>
[contr]: <https://github.com/GruppeTre/ProjectCalculationTool/blob/main/CONTRIBUTING.md>
