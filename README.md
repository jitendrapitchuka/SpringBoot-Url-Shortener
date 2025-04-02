# SpringBoot URL Shortener

This is a URL shortener application built with **Spring Boot**, **Flyway**, and **Thymeleaf**. The project demonstrates how to create a simple URL shortening service, using Flyway for database migration and Thymeleaf for dynamic front-end views.

---

## 🚀 Technologies Used

- **Spring Boot**: Java framework for building web applications.
- **Flyway**: Database migration tool to handle version control of your schema.
- **Thymeleaf**: Modern server-side Java template engine for rendering dynamic HTML.

---

### 🛠️ Useful configuration / Annotation

The application is configured using `application.properties` and `@ConfigurationProperties` in Spring Boot.
``` java
@ConfigurationProperties(prefix = "app")
@Validated
public record ApplicationProperties(
@NotBlank
@DefaultValue ("http://localhost:8080") String baseUrl,
@DefaultValue("30")@Min(1) @Max(365) int defaultExpiryInDays,
@DefaultValue("true") boolean validateOriginalUrl
) {
}
```
### 📝  (`application.properties`)

```properties
# Base URL of the application (default: http://localhost:8080)
app.base-url=http://localhost:8080

# Default expiration time for short URLs (in days) (default: 30, range: 1-365)
app.default-expiry-in-days=30

# Whether to validate the original URL before shortening (default: true)
app.validate-original-url=true
```

### 📌 Solving the N+1 Problem with  `spring.jpa.open-in-view=false`

In **Spring Boot** applications using **JPA** (Java Persistence API), **lazy loading** of entities can sometimes cause the **N+1 problem**. This happens when a parent entity is fetched, and each of its related child entities is lazily loaded in separate queries, leading to excessive database queries.

####  What is the N+1 Problem?

The **N+1 problem** occurs when:

1. One query is issued to load the parent entity (e.g., a list of posts).
2. Multiple queries (N queries) are issued to fetch the associated child entities (e.g., comments for each post).

This results in **1 + N** queries, which is inefficient and can severely degrade performance, especially with large datasets.

#### How `spring.jpa.open-in-view=false` Helps

By default, **Spring Boot** keeps the Hibernate session open during the entire view rendering process (via `spring.jpa.open-in-view=true`), which allows **lazy loading** of related entities after the transaction is committed. However, this can lead to unwanted queries and the N+1 problem.

When you set `spring.jpa.open-in-view=false`, the Hibernate session is closed after the transaction ends, preventing lazy loading of associations outside of the transaction context. This forces **eager loading** of related entities within the transaction, which helps eliminate the N+1 problem and improves application performance.

#### Example Configuration:

```properties
# Disable open-in-view to prevent lazy loading and solve the N+1 problem
spring.jpa.open-in-view=false
