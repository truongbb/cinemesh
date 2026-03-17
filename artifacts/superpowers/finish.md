# Finish

### Verification
- Commands run: 
  - `mvn clean compile` in `common/`
  - `mvn clean compile` in `infrastructure/cinemesh-gateway/`
  - `mvn clean install -DskipTests` in `common/`
  - `mvn clean compile` in `services/movie-service/`
- Results: All projects built successfully with no errors.

### Summary of changes
- Modified `CorrelationIdGlobalFilter.java` in `cinemesh-gateway` to append `X-Request-Id` to the `ServerHttpResponse` so the frontend receives the gateway-generated ID.
- Consolidated Feign interceptors in the `common` library:
  - Removed `requestInterceptor` bean from `FeignClientConfig.java` to prevent multiple conflicting interceptors.
  - Merged Authorization and Request ID extraction logic into a single resilient interceptor inside `FeignCorrelationInterceptor.java`.
  - Added robust fallback for the `requestId`: `MDC` -> `HttpServletRequest` (header) -> `HttpServletResponse` (header).

### Follow-ups
- Run the system locally and hit an endpoint with `curl -v` or Postman to verify `X-Request-Id` appears in the response.
- Check service logs to confirm the same ID is printed across all services in the chain.

### How to validate manually
1. Start your `discovery-server`, `cinemesh-gateway`, `auth-service`, and `movie-service`.
2. Make a request through the gateway (e.g., login or fetch a movie).
3. Observe the response headers in your HTTP client (you should see `X-Request-Id: <uuid>`).
4. Look at the console logs for the Gateway, Service A, and Service B. You should see the exact same UUID mapped to `requestId` in the MDC log output.
