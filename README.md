# deanoffice-backend
university DEANOFFICE project for improving student's software development knowledge: backend part

* Requirements: `Java 8` and `postgres`

**Stack:**
- Spring Boot 1.5.7.RELEASE
- Flyway

In order to work with IDEA Lombok plugin should be installed.

**Authorization:**

To have information about user who make request in a controller use next annotation @CurrentUser with ApplicationUser class as a parameter.
Example:
```
    @GetMapping("username")
    public ResponseEntity getCurrentUserName(@CurrentUser ApplicationUser applicationUser) {
     String userName = applicationUser.getUserName();
     return ResponseEntity.ok(userName);
    }
```
