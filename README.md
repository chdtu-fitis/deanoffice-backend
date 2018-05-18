# deanoffice-backend
university DEANOFFICE project for improving student's software development knowledge: backend part

* Requirements: `Java 8` and `Postgres`

**Stack:**
- Spring Boot 1.5.7.RELEASE
- Flyway

In order to work with IDEA Lombok plugin should be installed.

**Run backend**
In console in backend project root folder run:
gradle :api:aasemble
After that the application file api.war is created in build folder. Change folder to build and run:
java -jar api.war

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

Also, the current application user could be accessed in controller using ApplicationUserService
Example:
```
    @Autowired
    ApplicationUserService userService;

    @GetMapping("username")
    public ResponseEntity getCurrentUserName(@CurrentUser ApplicationUser applicationUser) {
      ApplicationUser applicationUser = userService.getApplicationUser()
    }


```

**Swagger https://swagger.io/**

To test API with Swagger.
Follow next steps:**

- Open http://localhost:8080/swagger-ui.html#
- Click Authorize button
- Input JWT token into value field with next format: **Bearer jwt-token**

Where jwt-token is valid token. To get valid token you can use next command:
```
curl -d '{"username":"test", "password":"test"}' -H "Content-Type: application/json" -X POST http://localhost:8080/login
```
