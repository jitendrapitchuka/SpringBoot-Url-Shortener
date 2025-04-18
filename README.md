# SpringBoot URL Shortener

This is a URL shortener application built with **Spring Boot**, **Flyway**, and **Thymeleaf**. The project demonstrates how to create a simple URL shortening service, using Flyway for database migration and Thymeleaf for dynamic front-end views.

---

## 🚀 Technologies Used

- **Spring Boot**: Java framework for building web applications.
- **Flyway**: Database migration tool to handle version control of your schema.
- **Thymeleaf**: Modern server-side Java template engine for rendering dynamic HTML.

---

## New things that learned from this project :

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

```
### 📌 **Note:** In Spring Data JPA, pagination is **zero-based**.  

> This means the first page starts at `page = 0`, not `page = 1`.
> 
> ✅ Example: `PageRequest.of(0, 10)` returns the **first page** with 10 items.
> 
> Refer in the project for more info.
> 
### ✅ @Modifying


This annotation tells Spring Data JPA:  
👉 “This query modifies data (e.g., `DELETE` or `UPDATE`), not just `SELECT`.”

It’s required because by default, Spring Data assumes repository methods are **read-only** unless told otherwise.

#### 🔧 Example usage:

```java
    @Modifying
    void deleteByIdInAndCreatedById(List<Long> ids, Long userId);
```


# 🛠 JDBC Access in Spring: JdbcTemplate vs JdbcClient

Spring provides multiple options for accessing relational databases using JDBC. Below is a comparison of the traditional `JdbcTemplate` and the modern `JdbcClient` introduced in **Spring Framework 6+**, along with usage examples for named parameters.

---

## 📌 1. JdbcTemplate (Classic)

### ✅ Pros:
- Stable and widely used.
- Available in all versions of Spring.

### ❌ Cons:
- ❌ No support for named parameters.
- ❌ More verbose and requires boilerplate.

### 🔧 Example (Positional Parameters):

Positional parameters use `?` placeholders. You must pass values in the correct order.

```java
@Autowired
private JdbcTemplate jdbcTemplate;

public void saveUser(String email, String password) {
    String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
    jdbcTemplate.update(sql, email, password);
}

@Autowired
private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

public void saveUser(String email, String password) {
        String sql = "INSERT INTO users (email, password) VALUES (:email, :password)";

        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        namedParameterJdbcTemplate.update(sql, params);
        }
```
## 🚀 Using `JdbcClient` in Spring Framework 6+

`JdbcClient` is a modern, fluent alternative to `JdbcTemplate`, introduced in **Spring Framework 6+**.  
It supports:

- ✅ Named parameters (built-in)
- ✅ Fluent, chainable API
- ✅ Lambda support for mapping
- ✅ Generated key handling
- ✅ Cleaner syntax and less boilerplate

---

### ✅ Setup
In **`JdbcClient`**, the **named parameters** in the SQL query (e.g., `:email`, `:password`) must exactly match the parameter names used in the `.param()` method calls.

```java
   public void save(User user) {
        String sql = """
                INSERT INTO users (email, password, name, role, created_at)
                VALUES (:email, :password, :name, :role, :createdAt)
                RETURNING id
                """;
        var keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql(sql)
                .param("email", user.getEmail())
                .param("password", user.getPassword())
                .param("name", user.getName())
                .param("role", user.getRole().name())
                .param("createdAt", Timestamp.from(user.getCreatedAt()))
                .update(keyHolder);
        Long userId = keyHolder.getKeyAs(Long.class);
        log.info("User saved with id: {}", userId);
    }
```


