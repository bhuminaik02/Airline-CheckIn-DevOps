# Airline Check-In Demonstration Portal

A fictional Spring Boot application demonstrating booking lookup, seat selection, check-in, and a demo boarding pass. This is a learning/demo project; it does not create real bookings or provide valid travel documents.

The API also supports booking administration: create bookings, list and search bookings, view a booking by reference, and update passenger/flight details while a booking remains in `BOOKED` status. New bookings require a valid, currently unassigned seat and are created in `BOOKED` status. These endpoints are part of this demonstration and do not include authentication or role-based access control.

## Requirements

- Java 25 (LTS)
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

## Booking management API

- `POST /api/bookings` creates a booking. The JSON body requires `bookingReference`, `firstName`, `surname`, `flightNumber`, `origin`, `destination`, `departureTime` (ISO local date-time), and `seatNumber` (rows 1–39, seats A–F).
- `GET /api/bookings` lists bookings.
- `GET /api/bookings/search` searches with optional `reference`, `surname`, and `flightNumber` query parameters. Supplied fields are matched case-insensitively by substring; omitted fields do not filter.
- `GET /api/bookings/{reference}/detail` retrieves a booking by reference. The existing `GET /api/bookings/{reference}?surname=...` customer lookup remains available.
- `PUT /api/bookings/{reference}` updates passenger and flight details. Seat and status are not updateable through this operation, and cancelled or checked-in bookings cannot be updated.

## Demo safety notice

Any boarding pass shown by this application is for demonstration only and must display **DEMO — NOT VALID FOR TRAVEL**. It is not a ticket or travel document.
