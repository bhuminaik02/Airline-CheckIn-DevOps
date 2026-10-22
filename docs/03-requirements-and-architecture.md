\# Requirements and System Architecture



\*\*Project:\*\* Automated Airline Check-In Demonstration Portal

\*\*Student:\*\* Bhumi Naik

\*\*Roll Number:\*\* 64

\*\*Student ID:\*\* 23102A0070



\## 1. Project Overview



The Automated Airline Check-In Demonstration Portal is a web application developed using Java and Spring Boot. It demonstrates how passengers can search for bookings, select seats, and complete a simulated airline check-in process. It also provides booking-management API operations for demonstration and testing.



This application is for educational purposes only. It does not connect to a real airline reservation system and does not issue valid travel documents.



\## 2. Functional Requirements



1\. \*\*Booking lookup:\*\* The system shall allow users to retrieve a booking using its reference and passenger details.

2\. \*\*Booking search:\*\* The system shall provide an API for searching or listing bookings.

3\. \*\*Booking creation:\*\* The system shall accept booking creation requests and validate supplied data.

4\. \*\*Booking updates:\*\* The system shall support updating permitted booking details.

5\. \*\*Booking cancellation:\*\* The system shall provide an operation to cancel a booking.

6\. \*\*Seat selection:\*\* The system shall allow a passenger to request an available seat.

7\. \*\*Seat validation:\*\* The system shall reject invalid or unavailable seat assignments.

8\. \*\*Check-in:\*\* The system shall support a simulated check-in operation and maintain the booking status.

9\. \*\*Error handling:\*\* The system shall return appropriate responses for invalid requests and unsuccessful operations.

10\. \*\*Data persistence:\*\* The application shall use its configured database layer to manage booking and seat-reservation data.

11\. \*\*Access control:\*\* The application shall apply its configured Spring Security rules to protected operations.

12\. \*\*Automated testing:\*\* The project shall include automated tests for important application behaviour.



\## 3. Non-Functional Requirements



\- \*\*Usability:\*\* The application should be straightforward to use and suitable for a classroom demonstration.

\- \*\*Reliability:\*\* Invalid booking details and unavailable seats should be handled consistently.

\- \*\*Maintainability:\*\* Code should be organised into application, booking, API, and security packages.

\- \*\*Security:\*\* Inputs should be validated, and access to protected operations should follow the configured security rules.

\- \*\*Testability:\*\* Important service and API behaviours should be verifiable through automated tests.

\- \*\*Portability:\*\* The application should be runnable in a Java development environment with the required dependencies.

\- \*\*Deployment readiness:\*\* The project should eventually support automated CI/CD, containerisation, and configuration management as required by the assignment.



\## 4. Technology Stack



| Component | Technology |

|---|---|

| Programming language | Java |

| Application framework | Spring Boot 4.1.1 |

| Web/API layer | Spring MVC |

| Persistence | Spring Data JPA |

| Database | H2, as configured for the current demo |

| Input validation | Spring Boot validation support |

| Security | Spring Security |

| Build tool | Maven Wrapper |

| Testing | Spring Boot Test and Spring Security Test |

| Version control | Git |

| Remote repository | GitHub |

| CI currently configured | GitHub Actions |



Jenkins, Selenium automation, Docker, and Ansible/Puppet must be documented as completed only after they have actually been configured and verified.



\## 5. High-Level Architecture



The application follows a layered design:



1\. \*\*Client layer:\*\* A browser or API client sends booking requests.

2\. \*\*Controller layer:\*\* `BookingController` exposes REST endpoints under `/api/bookings`.

3\. \*\*Validation and error handling:\*\* Request DTOs validate incoming data, while `ApiExceptionHandler` handles API errors.

4\. \*\*Service layer:\*\* `BookingService` contains booking-related business operations.

5\. \*\*Repository layer:\*\* Spring Data JPA repositories access stored booking and seat-reservation data.

6\. \*\*Database layer:\*\* H2 provides database storage according to the current application configuration.

7\. \*\*Security layer:\*\* `DemoSecurityConfiguration` defines the application's Spring Security configuration.



The exact responsibilities and access rules of each layer should be confirmed by reviewing the implementation and tests.



\## 6. REST API Endpoint Inventory



The following endpoint mappings were found in `BookingController.java`.



| Method | Endpoint | Purpose |

|---|---|---|

| GET | `/api/bookings` | List bookings |

| GET | `/api/bookings/search` | Search bookings |

| GET | `/api/bookings/{reference}/detail` | Retrieve booking details |

| POST | `/api/bookings` | Create a booking |

| PUT | `/api/bookings/{reference}` | Update a booking |

| POST | `/api/bookings/{reference}/cancel` | Cancel a booking |

| GET | `/api/bookings/{reference}` | Retrieve a booking using its reference and supplied details |

| PUT | `/api/bookings/{reference}/seat` | Assign a seat |

| POST | `/api/bookings/{reference}/check-in` | Perform simulated check-in |



These entries are based on controller annotations. Request parameters, authentication requirements, response formats, and successful behaviour must be verified against the implementation and tests.



\## 7. Main Data Components



\- \*\*Booking:\*\* Represents booking information and its associated status.

\- \*\*BookingStatus:\*\* Represents the available booking status values.

\- \*\*FlightSeatReservation:\*\* Represents seat-reservation information.

\- \*\*BookingRepository:\*\* Provides booking data access.

\- \*\*FlightSeatReservationRepository:\*\* Provides seat-reservation data access.

\- \*\*Request DTOs:\*\* `BookingCreateRequest`, `BookingUpdateRequest`, `CheckInRequest`, and `SeatAssignmentRequest` represent incoming API data.



\## 8. Testing and Acceptance Criteria



The project will be considered ready for its demonstration when the following checks pass:



\- A valid sample booking can be retrieved.

\- Invalid booking details produce an appropriate error response.

\- Booking creation and permitted updates behave correctly.

\- Cancellation changes the booking state as intended.

\- Invalid or occupied seat assignments are rejected.

\- A valid eligible booking can complete simulated check-in.

\- Cancelled or already checked-in bookings cannot complete an invalid repeat operation.

\- Security rules behave as intended for permitted and prohibited requests.

\- Automated tests pass using the Maven Wrapper.

\- The CI pipeline reports the build and test result.



Each criterion must be verified using tests or recorded demonstration evidence before being marked complete.



\## 9. Current Limitations



\- This is a demonstration system, not a production airline platform.

\- Sample bookings are for testing purposes.

\- H2 is configured for the current demonstration environment; persistence across restarts depends on the database configuration.

\- Real airline integration, real passenger data, and actual boarding-pass issuance are outside the project scope.

\- Deployment automation, browser-based continuous testing, container publishing, and configuration-management automation require separate implementation and verification.



\## 10. Conclusion



The application provides a foundation for demonstrating booking management and simulated check-in through a Spring Boot REST API. This architecture can be used to implement and evaluate the remaining DevOps workflow, including automated testing, CI/CD, containerisation, deployment, and final project documentation.
