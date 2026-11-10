# Online Car Auction Platform

A backend REST API for an online car auction platform, built with **Java and Spring Boot**. The platform allows car owners to submit their vehicles for review, administrators to manage auction requests and create auctions, and users to participate in live auctions through a competitive bidding system.

The project focuses on building a maintainable backend with clear business rules, secure-by-design foundations, and reliable handling of concurrent bidding operations.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Implemented Features](#implemented-features)
- [Auction Workflow](#auction-workflow)
- [Bidding System](#bidding-system)
- [Technology Stack](#technology-stack)
- [Architecture and Project Structure](#architecture-and-project-structure)
- [Database Design](#database-design)
- [API Endpoints](#api-endpoints)
- [Important Technical Decisions](#important-technical-decisions)
- [Current Limitations](#current-limitations)
- [Future Work](#future-work)
- [Getting Started](#getting-started)

---

## Project Overview

The platform provides a structured process for managing vehicle auctions, from vehicle submission to auction completion.

It supports three main workflows:

1. **Vehicle Management:** Car owners can register their vehicles and submit them for administrative review.
2. **Auction Management:** Administrators review auction requests, approve or reject them, and create scheduled auctions.
3. **Bidding:** Users can participate in live auctions by placing bids that follow predefined bidding rules.

The backend is designed as a RESTful API, keeping business logic separate from HTTP request handling and allowing a frontend application to be integrated independently.

## Implemented Features

### 1. Vehicle Management

Car owners can manage their vehicles through the following operations:

- Register a new vehicle.
- Update vehicle information.
- Retrieve their own vehicles.
- Submit a vehicle for administrative review.
- Delete a vehicle when deletion rules allow it.

**Vehicle review workflow:**

- `DRAFT`
- `SUBMITTED`
- `UNDER_REVIEW`
- `CHANGES_REQUESTED`
- `APPROVED`
- `REJECTED`

Business rules include:

- Only the vehicle owner can manage their vehicle.
- Only approved vehicles can be submitted for auction.
- Vehicles cannot be edited while submitted or under review.
- Editing an eligible vehicle resets its review status to `DRAFT`.
- Vehicles cannot be deleted after auction request history has been created.
- Vehicles cannot be edited or deleted when an active scheduled or live auction exists.

### 2. Auction Request Management

Vehicle owners can request an auction for an approved vehicle.

Supported auction types:

- `RESERVE`: The owner proposes a minimum acceptable selling price.
- `NO_RESERVE`: The auction has no reserve price.

The auction request workflow supports:

- Submitting an auction request.
- Retrieving the owner's auction requests.
- Withdrawing an eligible request.
- Updating a request when an administrator requests changes.
- Resubmitting the updated request.

**Auction request statuses:**

- `SUBMITTED`
- `UNDER_REVIEW`
- `CHANGES_REQUESTED`
- `APPROVED`
- `REJECTED`
- `WITHDRAWN`
- `AUCTION_CREATED`

Business rules include:

- Only the vehicle owner can submit an auction request.
- Only approved vehicles are eligible.
- A vehicle cannot have multiple active auction requests.
- Reserve auctions require a positive reserve price.
- No-reserve auctions must not contain a reserve price.
- Owners cannot change the vehicle associated with an existing request.
- Previous requests are retained as history.

### 3. Administrative Auction Request Management

Administrators can manage submitted auction requests through a dedicated workflow.

Available operations:

- Retrieve pending requests.
- Start reviewing a request.
- Request changes from the vehicle owner.
- Reject a request.
- Approve a request and determine the proposed reserve price.

The approved reserve price is stored separately from the owner's original requested price.

This allows administrators to negotiate or adjust the proposed reserve price without losing the owner's original submission.

### 4. Auction Management

Once an auction request is approved, an administrator can create an auction.

Implemented functionality:

- Create an auction from an approved auction request.
- Define the starting price.
- Define the auction start and end times.
- Configure the minimum bid increment.
- Start an eligible scheduled auction.
- End a live auction.
- Cancel a scheduled auction.

**Auction statuses:**

- `SCHEDULED`
- `LIVE`
- `ENDED`
- `CANCELLED`

**Auction results:**

- `NOT_DECIDED`
- `SOLD`
- `UNSOLD`
- `PENDING_NEGOTIATION`

Auction rules include:

- An auction can only be created from an approved request.
- Each auction request can produce only one auction.
- Reserve prices are determined by the approved request, not directly supplied by the auction creation request.
- Bidding is allowed only while the auction is live.
- Auctions can only be cancelled before they start.
- When an auction ends, the highest bidder is recorded as the winner.
- If no bids were submitted, the auction is marked as `UNSOLD`.

Auction start and end operations are currently triggered through API endpoints. Automatic scheduling is planned for a future milestone.

### 5. Bidding System

Users can participate in live auctions by submitting bids.

Implemented functionality:

- Place a bid on a live auction.
- Retrieve an auction's bid history.
- Retrieve the current highest bid.

Bidding rules include:

- Bids can only be placed on live auctions.
- Users cannot bid on their own vehicles.
- The first bid must meet or exceed the auction's starting price.
- Every subsequent bid must exceed the highest bid by at least the configured minimum bid increment.
- Bids are associated with the authenticated user's identity through the current-user request attribute placeholder.
- Bid timestamps are generated by the server.

### 6. Concurrent Bidding Protection

Handling simultaneous bids is an important part of an auction platform.

The bidding service uses:

- `@Transactional` to execute the bidding operation within a database transaction.
- `@Lock(LockModeType.PESSIMISTIC_WRITE)` on the auction lookup to acquire a database-level write lock.
- The same auction locking mechanism when ending an auction.

This ensures that simultaneous bidding operations on the same auction are processed sequentially, preventing multiple users from validating their bids against an outdated highest bid.

It also ensures that ending an auction and placing a bid cannot independently proceed against the same unlocked auction state.

---

## Auction Workflow

The overall business process follows this sequence:

```text
Car Owner
    |
    v
Register Vehicle
    |
    v
Submit Vehicle for Review
    |
    v
Administrator Reviews Vehicle
    |
    +----> Changes Requested / Rejected
    |
    v
Vehicle Approved
    |
    v
Owner Submits Auction Request
    |
    v
Administrator Reviews Request
    |
    +----> Changes Requested / Rejected
    |
    v
Auction Request Approved
    |
    v
Administrator Creates Auction
    |
    v
Scheduled Auction
    |
    v
Live Auction
    |
    v
Users Place Bids
    |
    v
Auction Ends
    |
    +----> Highest Bidder Wins
    |
    +----> No Bids = Unsold
```

---

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 25 | Main programming language |
| Spring Boot 4.1.1 | Backend application framework |
| Spring Web | REST API development |
| Spring Data JPA | Database access and repository abstraction |
| Hibernate | ORM and entity persistence |
| PostgreSQL | Relational database |
| Jakarta Bean Validation | Request and entity validation |
| Lombok | Reducing boilerplate code |
| Maven | Dependency and project management |
| IntelliJ IDEA | Development environment |

---

## Architecture and Project Structure

The project follows a feature-oriented package structure, keeping related functionality together.

```text
auction
|
+-- cars
|   +-- controllers
|   +-- dtos
|   +-- entities
|   +-- enums
|   +-- repositories
|   +-- services
|
+-- auctionRequests
|   +-- controllers
|   +-- dtos
|   +-- entities
|   +-- enums
|   +-- repositories
|   +-- services
|
+-- auctions
|   +-- controllers
|   +-- dtos
|   +-- entities
|   +-- enums
|   +-- repositories
|   +-- services
|
+-- bids
|   +-- controllers
|   +-- dtos
|   +-- entities
|   +-- repositories
|   +-- services
|
+-- users
|
+-- shared
    +-- HttpRequests
```

The application separates responsibilities into clear layers:

- **Controllers:** Handle HTTP requests, path variables, request bodies, and responses.
- **DTOs:** Define the data accepted and returned by the API.
- **Services:** Contain business rules and coordinate application operations.
- **Repositories:** Handle database access using Spring Data JPA.
- **Entities:** Represent the application's persistent domain data.
- **Enums:** Define controlled values for statuses, types, and results.
- **Shared:** Contains common functionality such as custom exceptions and HTTP error handling.

---

## Database Design

The main domain entities are:

| Entity | Responsibility |
|---|---|
| `User` | Represents platform users and vehicle owners. |
| `Car` | Stores vehicle information and its review status. |
| `AuctionRequest` | Stores an owner's request to auction a vehicle. |
| `Auction` | Represents the actual scheduled or live auction. |
| `Bid` | Stores bids submitted by users. |

### Important Relationships

- A user can own multiple cars.
- A car can have multiple auction requests over time.
- An auction request can create one auction.
- An auction can contain multiple bids.
- Each bid belongs to one bidder and one auction.
- An auction can have a winner after completion.

### Database Design Considerations

- UUIDs are used for entity identifiers.
- `BigDecimal` is used for monetary values to avoid floating-point precision issues.
- `Instant` is used for timestamps.
- Relationships use lazy fetching where appropriate.
- Entity relationships enforce the required database associations.
- Database indexes support common auction and bidding queries.

---

## API Endpoints

The following endpoints represent the implemented API surface.

### Vehicle Management

Base URL: `/api/cars`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/cars` | Create a vehicle |
| PUT | `/api/cars/{carId}` | Update a vehicle |
| GET | `/api/cars/my` | Retrieve the current user's vehicles |
| PATCH | `/api/cars/{carId}/submit` | Submit a vehicle for review |
| DELETE | `/api/cars/{carId}` | Delete an eligible vehicle |

### Owner Auction Requests

Base URL: `/api/auction-requests`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auction-requests` | Submit an auction request |
| GET | `/api/auction-requests/my` | Retrieve the current user's requests |
| PATCH | `/api/auction-requests/{requestId}/withdraw` | Withdraw a request |
| PUT | `/api/auction-requests/{requestId}/resubmit` | Update and resubmit a request |

### Administrative Auction Requests

Base URL: `/api/admin/auction-requests`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/admin/auction-requests/pending` | Retrieve pending requests |
| PATCH | `/api/admin/auction-requests/{requestId}/start-review` | Start reviewing a request |
| PATCH | `/api/admin/auction-requests/{requestId}/request-changes` | Request changes |
| PATCH | `/api/admin/auction-requests/{requestId}/reject` | Reject a request |
| PATCH | `/api/admin/auction-requests/{requestId}/approve` | Approve a request |

### Auction Management

Base URL: `/api/admin/auctions`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/admin/auctions/{requestId}` | Create an auction |
| PATCH | `/api/admin/auctions/{auctionId}/start` | Start an auction |
| PATCH | `/api/admin/auctions/{auctionId}/end` | End an auction |
| PATCH | `/api/admin/auctions/{auctionId}/cancel` | Cancel a scheduled auction |

### Bidding

Base URL: `/api/auctions/{auctionId}/bids`

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auctions/{auctionId}/bids` | Place a bid |
| GET | `/api/auctions/{auctionId}/bids` | Retrieve auction bid history |
| GET | `/api/auctions/{auctionId}/bids/highest` | Retrieve the highest bid |

---

## Important Technical Decisions

### DTO-Based API Design

Entities are not exposed directly through the API. DTOs define the request and response contracts, keeping internal database structures separate from external API consumers.

### Business Logic in Services

Controllers remain lightweight and delegate business operations to service classes. This keeps business rules centralized and easier to maintain.

### Spring Data JPA

Database operations are handled through Spring Data JPA repositories and derived query methods, avoiding unnecessary handwritten SQL.

### Transaction Management

`@Transactional` is used to maintain consistency when an operation updates multiple related entities.

For example, creating an auction also updates its associated auction request status.

### Pessimistic Locking

Pessimistic locking protects critical auction operations where concurrent requests could otherwise cause inconsistent results, particularly during bidding and auction completion.

### Centralized Exception Handling

The application uses a custom runtime exception, `CustomResponseException`, together with centralized exception handling to provide consistent API error responses.

### Server-Side Validation

Jakarta Bean Validation is used to validate request data, including required fields, positive monetary values, and valid date constraints.

Business rules are additionally enforced in the service layer.

---

## Current Limitations

The following features are intentionally not part of the completed implementation yet:

- JWT authentication and full Spring Security integration.
- Role-based endpoint authorization.
- Real-time bidding updates.
- Redis caching.
- Frontend application.
- Automatic auction scheduling.
- Production deployment configuration.

The current implementation uses a temporary `currentUser` request attribute as an authentication placeholder. This must be replaced with Spring Security authentication before production use.

---

## Future Work

The project will continue through the following milestones.

### Milestone 3 — Authentication and Authorization

- Implement user registration and login.
- Secure password storage using password hashing.
- Implement JWT generation and validation.
- Configure Spring Security with stateless authentication.
- Introduce role-based access control for administrators and regular users.
- Replace temporary request attributes with authenticated principals.
- Secure administrative and user-specific endpoints.

### Milestone 4 — Real-Time Features

- Integrate WebSocket communication.
- Broadcast new bids to connected clients.
- Update the highest bid without refreshing the page.
- Provide real-time auction countdowns.
- Notify users when auctions start or end.
- Implement anti-sniping protection: when a bid is placed during the final 60 seconds, extend the auction so that at least 60 seconds remain.

### Milestone 5 — Redis Integration

- Cache active auction information.
- Cache frequently requested highest bids.
- Cache auctions ending soon.
- Improve read performance for public auction browsing.
- Keep PostgreSQL as the source of truth.

### Milestone 6 — Public Auction Browsing

- Build a public homepage accessible to guests and authenticated users.
- Display live and upcoming auctions.
- Add search and filtering.
- Provide auction detail pages.
- Display current highest bids and remaining time.
- Expose appropriate public bidding history.

### Milestone 7 — Frontend Integration

- Develop the frontend application.
- Integrate authentication flows.
- Build vehicle management screens.
- Build owner auction request screens.
- Build an administrative dashboard.
- Implement the live bidding interface.
- Integrate WebSocket updates.

### Milestone 8 — Automation and Background Jobs

- Automatically start scheduled auctions.
- Automatically end expired auctions.
- Handle auctions that expire while the application is offline.
- Introduce reliable background processing for auction lifecycle management.

### Milestone 9 — Finalization and Deployment

- Review application security.
- Improve validation and error handling.
- Review database performance and indexing.
- Configure Docker.
- Prepare production environment configuration.
- Deploy the backend.
- Complete API documentation using Swagger / OpenAPI.

---

## Getting Started

### Prerequisites

Make sure the following tools are installed:

- Java 25
- Maven
- PostgreSQL
- Git

### Installation

1. Clone the repository:

```bash
git clone <repository-url>
```

2. Navigate to the project directory:

```bash
cd <project-directory>
```

3. Create a PostgreSQL database for the application.

4. Configure your database connection in `application.properties` or `application.yml`:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
```

Use your own database connection values and keep credentials out of source control.

5. Run the application:

```bash
mvn spring-boot:run
```

The API will start on the configured Spring Boot server port (8080 by default).

---

## Project Status

**Current implementation:** Vehicle Management, Auction Request Management, Auction Lifecycle Management, and Bidding System.

**Next milestone:** Authentication and Authorization using Spring Security and JWT.

---

## Author

Developed as a backend engineering portfolio project to demonstrate practical experience with Java, Spring Boot, REST API development, relational database design, transaction management, and concurrency control.

