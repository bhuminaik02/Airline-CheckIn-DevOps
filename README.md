# Airline Check-In Demonstration Portal

A fictional Spring Boot application demonstrating booking lookup, seat selection, check-in, and a demo boarding pass. This is a learning/demo project; it does not create real bookings or provide valid travel documents.

## Requirements

- Java 21
- The included Maven Wrapper (Maven is downloaded by the wrapper if needed)

## Run locally

From the project root on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8080/](http://localhost:8080/) in a browser. Stop the application with `Ctrl+C`.

## Run tests

```powershell
.\mvnw.cmd clean test
```

## Sample booking credentials

Use a booking reference and matching passenger surname:

| Booking reference | Surname | Sample status |
| --- | --- | --- |
| `DEMO1001` | `Morgan` | Booked |
| `DEMO1002` | `Lee` | Booked |
| `DEMO2001` | `Rivera` | Booked |
| `DEMO3001` | `Patel` | Cancelled |

The sample data is fictional and stored in an in-memory H2 database. Changes made while the app is running are not persistent and are reset when the application restarts.

## Demo safety notice

Any boarding pass shown by this application is for demonstration only and must display **DEMO — NOT VALID FOR TRAVEL**. It is not a ticket or travel document.
