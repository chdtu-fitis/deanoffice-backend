package ua.edu.chdtu.deanoffice.webstarter.security;

public class SecurityConstants {
    public static final String SECRET = "eyJhbGciOiJIUzUxMiJ9ex786eyJzdWIiOiJmaXRpcyIsImV4cCI6MTry6UzOTI1NjI2M30EanZZoDJuEYmdNJATW9yUt1-BrytMggAirnN68u5EZxOqChAbhobjObb0e6Gm5JluEAsY40QuxYWrjBM2HkL3cgndGzUINjpaFA";
    public static final long EXPIRATION_TIME = 864_000_000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
}
