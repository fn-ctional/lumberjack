# Lumberjack

### A device library and logging system using RFID technology.

This system is being developed as part of a project with The University of Bristol Department of Computer Science, for a second year project. The team is comprised of 6 students with varying areas of expertise and interest.

To run the project, navigate to the root of the project directory <code>lumberjack_spring</code> and run
Spring Boot <code>mvn spring-boot:run</code>.

Navigate to <a href="http://localhost:8080/">localhost:8080</a> to enter the
web front-end.

It is neccessary to provide configuration files in resources/config/ detailing the following:

<code>database.properties:
-ip
-port
-database
-username
-password</code>

<code>email.properties:
-host
-port
-username
-password</code>

Additionally, testdatabase.properties is required to run unit tests, with the same fields as database.properties. 
