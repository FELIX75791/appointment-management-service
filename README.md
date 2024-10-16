# Appointment-Management-Service

Welcome to our Appointment-Management-Service! This is a team Project for our team DLJL. Our members are Ziyue Jin, Ken Deng, Jiacheng Liu, Yang Li.

## Building and Running a Local Instance

In order to build and use our service, you must install the following dependencies:

1. **Maven 3.9.5**: Download Maven: (https://maven.apache.org/download.cgi) and follow the installation instructions. Be sure to set the `bin` path as described in Maven's README. You are recommended to add the path in the source file of bash or zsh.
   
2. **JDK 17**: This project is developed with JDK 17. You can download JDK 17: (https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) from Oracle's website and install it.

3. **MyBatis**: Our project uses MyBatis framework. If needed, refer to the official MyBatis Documentation: (https://mybatis.org/mybatis-3/).

4. **MySQL**: Install and set up MySQL. For local development, we recommend MySQL Workbench for easier management. Create a database locally and ensure the connection details are properly set up in the project’s configuration file : application.properties.

5. **Cloning the Project**: you can clone the repository from GitHub. Click the green "Code" button on the repository page, copy the HTTP/SSH link, and use it to clone the project locally. You can also use Github Desktop!

6. **Building the Project**: Once cloned, use Maven to build the project by running: <code>mvn clean install</code> 

7. **Running the Project**: Once built, you can run this project by <code>mvn spring-boot:run</code> 


## Running a Cloud Based Instance


## Running test
We provided unit tests under the directory src/test
You are welcome to test our end points after running an instance. We recommend using Postman or Bruno

