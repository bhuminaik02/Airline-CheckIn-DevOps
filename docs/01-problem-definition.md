# Automated Airline Check-In Demonstration Portal



**Student Name:** Bhumi Naik

**Roll Number:** 64

**Student ID:** 23102A0070



## 1. Problem Statement



Traditional airline check-in processes may involve queues, manual verification, and delays when passengers need to confirm their booking details or select seats. This project develops a demonstration portal that allows users to look up a sample booking, validate passenger details, select an available seat, and complete a simulated check-in.



The project also demonstrates how software development and operations practices can be used to build, test, package, deploy, and maintain an application.



## 2. Target Users



- Passengers using the demonstration check-in portal.

- Airline support staff who need to review booking and check-in status.

- Developers responsible for application changes.

- DevOps engineers responsible for testing, deployment, and reliability.



## 3. Stakeholders



- Student developer and project owner.

- Passengers represented by the demonstration workflow.

- Airline operations and support representatives.

- Faculty evaluator.

- Development and operations teams.



## 4. Existing Pain Points



- Manual check-in processes can require additional time.

- Incorrect booking details can delay passenger verification.

- Seat allocation must prevent selection of an already occupied seat.

- Repeated manual testing can make application changes harder to verify.

- Manual deployment can introduce configuration errors.



These points describe the general problem being demonstrated; the project does not connect to a real airline reservation system.



## 5. Project Objectives



1\. Provide a simple booking lookup interface.

2\. Validate booking references and passenger surnames.

3\. Support simulated seat selection and check-in.

4\. Reject invalid bookings and unavailable seats.

5\. Display a demonstration boarding pass.

6\. Automate application builds and tests.

7\. Demonstrate browser automation, containerization, deployment, and configuration management.



## 6. Constraints



- The portal is a demonstration application, not an operational airline system.

- Sample passenger and booking information is used.

- The current application uses an H2 in-memory database.

- The project depends on Java, Maven, Git, and other tools required by the assignment.

- Jenkins, Selenium, Docker, and Ansible or Puppet must be configured and tested where required.

- Production security, real payment processing, and integration with airline reservation systems are outside the current scope.



## 7. Measurable Success Criteria



- The application starts successfully on the configured local port.

- Maven verification completes without test failures.

- Invalid booking details are rejected.

- Unavailable seats cannot be selected.

- A valid sample booking can complete the simulated check-in flow.

- GitHub contains the source code and meaningful commit history.

- Jenkins executes the required build and test pipeline.

- Selenium tests verify critical user journeys.

- Docker and configuration-management tasks are demonstrated with recorded evidence.

- Final documentation and viva materials are prepared.



## 8. MVP Scope



The 15-task MVP and DevOps project will cover booking creation, viewing, updating and searching where implemented; check-in status transitions; seat validation; a summary dashboard; automated testing; CI/CD; containerization; configuration management; and reliability validation.



Features not implemented yet will be added and verified during the project rather than reported as completed.
