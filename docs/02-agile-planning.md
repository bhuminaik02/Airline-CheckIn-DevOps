# Agile Planning and DevOps Workflow



**Project:** Automated Airline Check-In Demonstration Portal

**Student:** Bhumi Naik

**Roll Number:** 64

**Student ID:** 23102A0070



## 1. Agile Approach



The project will use an iterative Agile approach. Work will be divided into small tasks, implemented, tested and reviewed before moving to the next stage. Git and GitHub will manage source changes, while automated pipelines will help verify the application.



## 2. User Stories and Acceptance Criteria



### Story 1: Search for a booking

As a passenger, I want to enter my booking reference and surname so that I can retrieve my sample booking.



Acceptance criteria:

- A valid reference and surname retrieve the matching booking.

- Incorrect details produce an appropriate error.

- The application does not expose unrelated bookings.



### Story 2: Create and update a booking

As an authorized operator, I want to create and update sample booking records so that booking information can be maintained.



Acceptance criteria:

- Valid booking data is accepted.

- Invalid data is rejected with a useful message.

- Updates affect the intended booking only.



### Story 3: Complete check-in

As a passenger, I want to select an available seat and complete a simulated check-in.



Acceptance criteria:

- An eligible booking can be checked in.

- An occupied or invalid seat is rejected.

- A cancelled or already checked-in booking cannot be checked in again.



### Story 4: View status and summary

As an operator, I want to view booking status and a summary so that I can monitor the demonstration workflow.



Acceptance criteria:

- Booking status is displayed accurately.

- Summary information matches the available records.

- Invalid status changes are rejected.



### Story 5: Automate delivery

As a developer, I want automated builds and tests so that code changes are checked before deployment.



Acceptance criteria:

- Maven builds and tests run successfully.

- Failed quality checks prevent deployment.

- Build and test evidence is retained.



## 3. Fifteen-Task Plan



1\. Define the problem, stakeholders, scope and success criteria.

2\. Create user stories, backlog, Definition of Done and DevOps workflow.

3\. Document requirements, architecture, data model and technology setup.

4\. Initialize GitHub repository, README, .gitignore and repository policies.

5\. Implement the first core feature using a feature branch and pull request.

6\. Complete the MVP, collaboration exercises, conflict resolution and release tag.

7\. Install Jenkins and configure a continuous integration job.

8\. Create a Jenkinsfile and deploy to the selected server target.

9\. Write and execute Selenium tests locally.

10\. Integrate Selenium tests and reports into Jenkins.

11\. Build and test a Docker image and container.

12\. Automate Docker image publishing and deployment through Jenkins.

13\. Create an Ansible playbook or Puppet configuration.

14\. Demonstrate provisioning, idempotency, health checks and rollback.

15\. Complete end-to-end validation, documentation, presentation and viva.



## 4. Definition of Done



A task is considered done when:

- Its required functionality or document exists.

- Acceptance criteria are checked.

- Relevant tests pass.

- Changes are committed to Git.

- Required review or deployment evidence is captured.

- Documentation is updated.

- Incomplete requirements and limitations are recorded honestly.



## 5. Kanban Workflow



Tasks move through these columns:



- Backlog

- Ready

- In Progress

- Testing / Review

- Done



Only tasks that meet their acceptance criteria should be moved to Done.



## 6. DevOps Lifecycle



Plan â†’ Code â†’ Build â†’ Test â†’ Package â†’ Deploy â†’ Operate â†’ Monitor â†’ Feedback



Git and GitHub manage changes. Jenkins automates the pipeline, Selenium validates browser journeys, Docker packages the application, and Ansible or Puppet manages environment configuration.



## 7. Risk Management



- Build failure: inspect logs and fix the underlying cause.

- Test failure: prevent deployment until the test passes.

- Deployment failure: retain the previous stable version for recovery.

- Environment differences: document and automate configuration.

- Incomplete requirements: track them in the backlog and do not mark them complete prematurely.



## 8. Current Progress Note



The repository, Spring Boot application, Maven verification and GitHub Actions workflow have been demonstrated. Jenkins, Selenium, Docker and configuration-management deliverables must be verified separately before they can be marked complete.
