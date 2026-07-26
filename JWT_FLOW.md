# JWT Flow — Quick Reference

---

## 1. Register (`POST /user`)

```
No token needed
       ↓
Just saves user to DB with hashed password
```

---

## 2. Login (`POST /login`)

```
LoginRequest (username + password)
       ↓
AuthenticationManager.authenticate()
       ↓
DaoAuthenticationProvider
       ↓  calls loadUserByUsername("john")
UserDetailsServiceImpl — loads user from DB
       ↓  returns UserPrincipal (wraps User with hash + role)
BCryptPasswordEncoder.matches("pass123", "$2a$10$hash...")
       ↓
  ┌─── match? ───┐
  YES             NO
   ↓               ↓
authenticated   401 Unauthorized
   ↓
JwtService.generateToken("john")
       ↓
Returns { "token": "eyJhbG..." }
```

---

## 3. Authenticated Request (`GET /my-courses`)

```
Request with header: Authorization: Bearer <token>
       ↓
JwtAuthenticationFilter — runs on EVERY request
       ↓
JwtService.extractUsername(token) → "john"
       ↓
UserDetailsServiceImpl.loadUserByUsername("john")
       ↓
JwtService.isTokenValid(token) — checks signature + expiry
       ↓
Sets SecurityContext with user + roles
       ↓
SecurityConfig checks role rules
  • /teacher/** → ROLE_TEACHER required
  • /my-courses → any authenticated user
       ↓
Controller handles the request
```

---

## Key Points

| Concept | What happens |
|---|---|
| **Token validation** | Happens on every request via `JwtAuthenticationFilter` |
| **Password check** | Only happens at `/login` via `DaoAuthenticationProvider` |
| **JWT stores** | Just the username (subject) + expiry time |
| **Role check** | Done by Spring Security from DB data, not from the token |
| **Token expiry** | 24 hours (configurable in `application.properties`) |
| **Frontend's job** | Store the token, send it as `Bearer <token>` with every request |

---

## Beans & Classes — Definition & Purpose

| Bean/Class | Type | Purpose |
|---|---|---|
| **`AuthenticationManager`** | Interface | Orchestrates authentication. `AuthController` calls it at login. Delegates to `AuthenticationProvider`. |
| **`DaoAuthenticationProvider`** | Bean | Authenticates against DB. Calls `UserDetailsService.loadUserByUsername()`, then uses `PasswordEncoder.matches()` to compare raw password vs stored hash. |
| **`BCryptPasswordEncoder`** | Bean | Hashes passwords with BCrypt. Used by `DaoAuthenticationProvider` (login) and `UserController` (registration). |
| **`UserDetailsService` / `UserDetailsServiceImpl`** | Bean | Loads user from DB by username. Returns a `UserDetails` object. Used by login (DAO provider) and token validation (JWT filter). |
| **`UserDetails` / `UserPrincipal`** | Class | Spring Security's user representation. Holds username, password hash, and authorities (`ROLE_STUDENT` / `ROLE_TEACHER`). |
| **`SecurityContextHolder`** | Class | Static container holding the `SecurityContext` (current authenticated user) for the request. Set by the filter, read by controllers and config. |
| **`UsernamePasswordAuthenticationToken`** | Class | Holds credentials before auth (raw username/password) or authenticated principal + authorities after auth. |
| **`JwtAuthenticationFilter`** | Bean (extends `OncePerRequestFilter`) | Runs once per request. Extracts JWT from `Authorization` header, validates it, sets the `SecurityContext`. |
| **`SecurityFilterChain`** | Bean | Defines URL rules: `permitAll`, `authenticated`, `hasRole(...)`. Registers the JWT filter in the chain. |
| **`JwtService`** | Bean | Utility for JWT operations: `generateToken()`, `extractUsername()`, `isTokenValid()`. |
| **`UserRepo`** | Bean (Spring Data JPA) | `JpaRepository<User, Integer>`. Gives `findByName()` for DB lookups. |
| **`LoginRequest` / `LoginResponse`** | DTOs | POJOs for login API. Request carries username + password. Response carries the JWT token. |
