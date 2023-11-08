# Tennis Lessons Project

### Description

This is simple academic project for web application backend. It is API that
allow user register in service as an instructor/student role and order
tennis lessons

Instructions below are step-by-step guide to configure 
and run web application backend

Technology and practises stack:
* Docker in order to containerize database
* Spring security to provide registration/login mechanism and secure resource server
* H2 database for tests scope
* RESTful Web Service
* Three-layer architecture
* compliance with SOLID rules
* Unit and integrating testing
* Swagger

## Set up database

Ensure that you have Docker installed and it is running on your computer.

### Set up Docker container

Let's open your terminal and run command:

```shell
docker run --name tl-db -e MYSQL_ROOT_PASSWORD=root -d -p 3308:3306 mysql
```
This command will set up your database server on port 3308 in your local machine

This command will download the newest version of mysql database image, so 
if you need a specific version, specify it!

### Configure Docker container and create database

Next step is open your container using Docker Desktop or get inside
the container in CLI. If you achieved bash run command:

```shell
mysql -u root -p
```

You will see "Enter password:" prompt. <b>Type: root</b>

The next step is create database. If you see MYSQL monitor
and characteristic prompt: mysql> let's type:

```mysql
CREATE DATABASE tldb;
```

That's it! If you see success confirmation Query OK, you have finished process
of setting up tools needed.

You can verify connection with database by build-in Database tool.
In password text field type: root (it is invisible). It is likely that 
you will need to install MySQL database drivers. Push Test Connection
button, if you will see success confirmation you can run application





