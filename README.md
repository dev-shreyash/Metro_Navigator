# Smart City Metro Navigator API

## Overview

The Smart City Metro Navigator is a Spring Boot REST API backend designed to calculate the most efficient transit routes across a metropolitan train network. Moving away from standard Breadth-First Search (BFS) node traversal, this system implements Dijkstra's Algorithm to evaluate weighted edges (distance and travel time) to provide users with the truly shortest and fastest path.

Currently configured with Mumbai Metro data, the API handles station interchanges, dynamic fare calculations, and travel time estimation. It is designed to be consumed by a mobile-responsive frontend application.

## Features

* **Weighted Pathfinding:** Uses Dijkstra's Algorithm via a Priority Queue to find the optimal route based on physical distance and train travel time.
* **Dynamic Fare Calculation:** Computes ticket pricing based on total kilometers traveled and base fares.
* **Interchange Handling:** Seamlessly maps routes across multiple metro lines (e.g., Blue Line to Aqua Line).
* **Relational Database Mapping:** Built with Spring Data JPA to enforce referential integrity between stations and route connections.

## Tech Stack

* **Backend Framework:** Java, Spring Boot, Spring Web
* **Database:** MySQL, Spring Data JPA, Hibernate
* **Build Tool:** Maven
* **Utilities:** Lombok

## Prerequisites

* Java Development Kit (JDK) 17 or higher
* Maven
* MySQL Server (running locally on port 3306)

## Installation & Setup

**1. Clone the repository:**

```bash
git clone https://github.com/dev-shreyash/Metro_Navigator
cd metro_navigator

```

**2. Database Setup:**
Open your MySQL CLI or Workbench and create the target database:

```sql
CREATE DATABASE metro_db;

```

**3. Configure Application Properties:**
Ensure `src/main/resources/application.properties` contains your local database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/metro_db
spring.datasource.username=root
spring.datasource.password=<your_password>

```

**4. Run the Application:**

```bash
mvn spring-boot:run

```

*Note: On the first run, the `DataSeeder` configuration will automatically populate the database with the initial Mumbai Metro station nodes and edge weights.*

## API Reference

### Find Fastest Route

Calculates the best route between a source and destination station.

**Endpoint:** `GET /api/v1/metro/route`

**Query Parameters:**

* `source` (String): Starting station name.
* `destination` (String): Ending station name.

**Example Request:**
`GET http://localhost:8080/api/v1/metro/route?source=Versova&destination=Dadar`

**Example Response:**

```json
{
  "source": "Versova",
  "destination": "Dadar",
  "stops": 4,
  "totalDistanceKm": 12.2,
  "estimatedTimeMins": 30.0,
  "fareRs": 35,
  "routePath": [
    "Versova",
    "DN Nagar",
    "Andheri",
    "BKC",
    "Dadar"
  ]
}

```

## Next Steps / Future Scope

* **Frontend Integration:** Develop a mobile-responsive React web application to consume this API and provide a commuter-friendly user interface.
* **Geocoding:** Add latitude/longitude coordinates to the Station entities to support "Find Nearest Station" functionality using public mapping APIs.
* **Live Traffic Simulation:** Introduce a scheduled service to dynamically alter edge weights (travel time) based on simulated peak-hour traffic.

## Future Improvements

* **React Web Application:** Develop a mobile-responsive frontend using React.js and Tailwind CSS. This will allow commuters to access the route planner on their phones, select stations from a clean UI, and view their generated ticket and fare instantly.
* **Interactive Visual Map:** Integrate Mapbox GL JS or Leaflet.js into the React frontend to visually plot the calculated Dijkstra path on a map, rather than relying solely on text output.
* **Geolocation Integration:** Add latitude and longitude data to the Station entities in the MySQL database. This will enable a "Find Nearest Station" feature that utilizes the commuter's device GPS.
* **Live Traffic Simulation:** Implement Spring Boot `@Scheduled` tasks to dynamically adjust the edge weights (travel time) based on simulated peak-hour traffic or network delays, forcing the algorithm to reroute in real-time.

## Author

**Shreyash Bhosale**

---

