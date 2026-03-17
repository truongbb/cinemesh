# Review

### Blockers
- None.

### Majors
- None.

### Minors
- None.

### Nits
- The `MDC_KEY` and `REQUEST_ID_HEADER` definitions are correctly centralized in `CommonConstant`, but ensure all future services import and use `CommonConstant` rather than hardcoding "X-Request-Id".

### Overall summary
Changes have successfully consolidated the Feign Interceptors to prevent duplicated bindings and potential overrides, which was causing empty headers. The Spring Cloud Gateway is also updated to return the request ID to the client response, which solves the immediate traceability requirement. All modified projects successfully compile.
