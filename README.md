# Fiches de Voeux

## A tool to streamline the collection and management of faculty teaching preferences for university course scheduling.

Fiches de Voeux is a web-based application designed to facilitate the process of gathering **teaching preferences** (which courses faculty wish to teach) and assist department heads in the **course assignment** process. Built for university departments, it aims to replace manual or semi-manual processes with an efficient, centralized system based on the needs identified at USTHB's Faculty of Informatics.

## How to Use Fiches de Voeux

The application is primarily accessed through a web interface, with different functionalities depending on user roles:

### For Teachers

1.  **Login:** Access the application using your provided university credentials.
2.  **Submit Preferences:** Navigate to the "Fiche de Voeux" form for the upcoming academic year/semester.
3.  **Select Modules:** Choose your preferred modules based on level, specialty, and teaching type (Cours, TD, TP), likely ranking your choices.
4.  **Indicate Availability:** Specify preferences for supplementary hours or PFE supervision.
5.  **Submit:** Finalize and submit your preferences before the deadline.

### For Administrators (Head of Department / Staff)

1.  **Login:** Access the application using administrative credentials.
2.  **Manage Data:** Access sections to manage core data like the teacher database, course catalogs (modules, levels, specialties), and academic terms.
3.  **View Submissions:** Review submitted "Fiches de Voeux" from all teachers for a given semester. Data might be viewable online or exported.
4.  **Assignment Support:** Utilize tools to view teacher preferences for specific modules to aid in creating the teaching schedule ("organigramme"). (Functionality may vary based on implementation).
5.  **Manage Users:** Add or update teacher information.

## How to Install Fiches de Voeux

### On Windows

> W.I.P (Deployment likely involves running a JAR file or deploying to a web server)

### On Linux

> W.I.P (Deployment likely involves running a JAR file, using Docker, or deploying to a web server)

### From Source

You can clone this repository using the git CLI.
After that, assuming a **Spring Boot with Maven** setup (adjust if using Gradle or Go), you can use commands like:

| Command         | Arguments              | Description                                                           |
| --------------- | ---------------------- | --------------------------------------------------------------------- |
| **`mvn install`** | none                   | Installs dependencies and builds the project (`.jar` in `/target`)      |
| **`mvn spring-boot:run`** | none                   | Runs the application directly using Maven                             |
| **`mvn test`** | none                   | Runs the tests within the project                                     |
| **`mvn clean`** | none                   | Cleans the `/target` directory created by Maven                       |


## Known Issues

> None currently known.

## More Information about Fiches de Voeux

This application aims to implement the key objectives outlined in the project proposal, including:

* Digitized course catalogs
* Teacher database management
* Electronic submission of teaching preferences
* Data extraction to support schedule creation
* Potential tools to assist in assignment negotiation
