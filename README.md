# taskolotl-backend

## API Docs

- http://localhost:8080/taskolotl/swagger-ui/index.html
- http://localhost:8080/taskolotl/v3/api-docs

## Generate a Bearer Token

- http://localhost:8080/taskolotl/login/oauth2/code/google

## Code Analysis

Useful for troubleshooting, improving, documenting, and testing with coding assistants

```bash
# Base directory of your project
BASE_DIR="src/main/java/com/saunderscox/taskolotl"
ENTITY="User"

# Concatenate all the files and copy to clipboard
cat "$BASE_DIR/entity/${ENTITY}.java" \
    "$BASE_DIR/dto/${ENTITY}UpdateRequestDto.java" \
    "$BASE_DIR/dto/${ENTITY}CreateRequestDto.java" \
    "$BASE_DIR/dto/${ENTITY}ResponseDto.java" \
    "$BASE_DIR/dto/${ENTITY}UpdateRequestDto.java" \
    "$BASE_DIR/mapper/${ENTITY}Mapper.java" | clip

echo "Contents of ${ENTITY} files copied to clipboard!"
```

```bash
# Base directory of your project
BASE_DIR="src/main/java/com/saunderscox/taskolotl"

# Concatenate all security files and copy to clipboard
cat \
"$BASE_DIR/config/security/SecurityConfig.java" \
"$BASE_DIR/config/security/OAuth2SuccessHandler.java" \
"$BASE_DIR/service/AuthService.java" \
"$BASE_DIR/controller/AuthController.java" \
| clip

echo "Contents of security files copied to clipboard!"
```

```bash
BASE_DIR="src/main/java/com/saunderscox/taskolotl"

# Find and concatenate all .java files recursively and copy to clipboard
find "$BASE_DIR" -name "*.java" -type f -exec cat {} \; | clip

echo "Contents of all .java files under $BASE_DIR copied to clipboard!"
```

## Versioning

- fix: → Patch release (1.0.0 → 1.0.1)
- feat: → Minor release (1.0.0 → 1.1.0)
- Any commit with BREAKING CHANGE: → Major release (1.0.0 → 2.0.0)
- See https://github.com/semantic-release/semantic-release?tab=readme-ov-file#how-does-it-work

## Setup

### Disable Mockito Warning

Add the following VM arg to handle the Mockito agent warning:

`-XX:+EnableDynamicAgentLoading`

# Code Guidelines

1. Controllers → What your API exposes to the world
2. DTOs → How data crosses the API boundary
3. Services → Core business logic
4. Mappers → Data transformation between layers
5. Entities → Domain model and persistence
6. Repositories → Data access patterns
7. Exceptions → Error handling strategy
8. Security → Protection mechanisms

# /controller

## Controller Setup

- [ ] Use `@RestController` annotation
- [ ] Define base path with `@RequestMapping("/api/resource-name")`
- [ ] Use constructor injection with `@RequiredArgsConstructor`
- [ ] Add OpenAPI documentation with `@Tag(name = "Resource", description = "...")`
- [ ] Enable validation with `@Validated`

## API Documentation

- [ ] Document each endpoint with `@Operation(summary = "...")`
- [ ] Group related operations with `@Tag(name = "Resource - Category")`
- [ ] Document pagination with `@PageableAsQueryParam`

## Request Handling

- [ ] Validate request bodies with `@Valid @RequestBody`
- [ ] Use `@PathVariable` for resource identifiers
- [ ] Validate query parameters with appropriate constraints
- [ ] Support pagination with `Pageable` parameter

## Response Handling

- [ ] Return `ResponseEntity<T>` from all methods
- [ ] Use `ResponseEntity.ok()` for successful GET requests
- [ ] Use `ResponseEntity.status(HttpStatus.CREATED)` for POST requests
- [ ] Use `ResponseEntity.noContent()` for DELETE requests
- [ ] Use separate DTOs for requests and responses

## Security

- [ ] Secure endpoints with `@PreAuthorize` annotations
- [ ] Use service methods for complex authorization rules

## Endpoint Patterns

- [ ] Implement standard CRUD operations:
    - [ ] GET `/api/resources` - List all (paginated)
    - [ ] GET `/api/resources/{id}` - Get single resource
    - [ ] POST `/api/resources` - Create resource
    - [ ] PUT `/api/resources/{id}` - Update resource
    - [ ] DELETE `/api/resources/{id}` - Delete resource
- [ ] Implement search with GET `/api/resources/search`
- [ ] Group related resources with nested paths
- [ ] Use POST for actions that change state

# /dto

## DTO Class Structure

- [ ] Create a base response DTO (`BaseResponse`) for common fields
- [ ] Extend base response for all response DTOs
- [ ] Separate request DTOs by purpose (create vs update)
- [ ] Use appropriate Lombok annotations to reduce boilerplate (@Data, @Builder)
- [ ] Apply `@JsonInclude(JsonInclude.Include.NON_NULL)` to omit null fields in responses
- [ ] Use `@EqualsAndHashCode(callSuper = true)` when extending base classes

## Request DTO Design

- [ ] Create separate DTOs for creation and updates
- [ ] Use strict validation for creation requests
- [ ] Use lenient validation for update requests (allow partial updates)
- [ ] Include only fields that should be modifiable by clients
- [ ] Use IDs rather than embedded objects for relationships
- [ ] Consider using record types for immutable DTOs (Java 16+)

## Response DTO Design

- [ ] Include all fields needed by the client
- [ ] Extend base response classes for common fields (id, timestamps)
- [ ] Use IDs rather than embedded objects for relationships
- [ ] Consider pagination wrappers for collection responses
- [ ] Include only necessary data to minimize payload size
- [ ] Use consistent naming patterns across all response DTOs

## Validation

- [ ] Apply appropriate validation annotations (@NotNull, @NotBlank, @Size)
- [ ] Include descriptive validation messages
- [ ] Match validation constraints to database column constraints
- [ ] Use custom validators for complex business rules
- [ ] Consider using validation groups for different contexts
- [ ] Validate collections appropriately (@NotEmpty where required)

## Type Safety

- [ ] Use specific types rather than generic Object
- [ ] Use enums for fields with a fixed set of values
- [ ] Consider using custom serializers/deserializers for complex types
- [ ] Use appropriate collection types (Set for unique values, List for ordered)
- [ ] Initialize collections to empty collections, not null
- [ ] Use UUID for entity identifiers consistently

## Documentation

- [ ] Add class-level JavaDoc explaining the DTO's purpose
- [ ] Document non-obvious fields or validation rules
- [ ] Consider using OpenAPI annotations for API documentation
- [ ] Include examples in documentation where helpful
- [ ] Document any special serialization behavior

# /service

## Service Setup

- [ ] Use `@Service` annotation on the class
- [ ] Use constructor injection with `@RequiredArgsConstructor` (Lombok)
- [ ] Add `@Transactional` for data consistency across database operations
- [ ] Use `@Slf4j` for logging capabilities
- [ ] Define constant strings for common error messages (e.g., `RESOURCE_NOT_FOUND = "Resource not found with id: "`)
- [ ] Inject all required repositories, mappers, and other services as final fields

## CRUD Operations

- [ ] Implement standard CRUD methods:
    - [ ] `getAll(Pageable)` - Return `Page<ResponseDTO>` with pagination
    - [ ] `getById(UUID)` - Use `repository.findById().orElseThrow()` pattern
    - [ ] `create(CreateRequest)` - Convert DTO to entity, set relationships, save, return response DTO
    - [ ] `update(UUID, UpdateRequest)` - Find entity, update non-null fields, update relationships, save
    - [ ] `delete(UUID)` - Check existence with `repository.existsById()` before deleting

## Data Mapping

- [ ] Use dedicated mapper classes (e.g., with MapStruct)
- [ ] Map paginated results with `.map(mapper::toResponseDto)` chaining
- [ ] Create entity from DTO with `mapper.toEntity(dto)`
- [ ] Convert entity to response DTO with `mapper.toResponseDto(entity)`

## Error Handling

- [ ] Throw `ResourceNotFoundException` with descriptive messages
- [ ] Include resource type and ID in error messages
- [ ] Validate collections of IDs against found entities
- [ ] Create helper methods for validation (e.g., `validateAllUsersFound()`)
- [ ] Use appropriate exception types for different error scenarios

## Relationship Management

- [ ] Create specific methods for each relationship type (e.g., `updateMembers()`, `updateRoles()`)
- [ ] Handle null collections with early returns
- [ ] Remove items not in the requested set with `collection.removeIf(item -> !ids.contains(item.getId()))`
- [ ] Add new items with stream operations and existence checks
- [ ] Use entity helper methods for bidirectional relationships (e.g., `entity.addMember()`)

## Logging

- [ ] Log method entry at INFO level for important operations
- [ ] Include operation details (e.g., `log.info("Creating board '{}' with {} owners", title, count)`)
- [ ] Log detailed information at DEBUG level for troubleshooting
- [ ] Include relevant IDs and page numbers in log messages

## Security & Authorization

- [ ] Implement methods to check if a user has access to a resource
- [ ] Create convenience methods for controllers (e.g., `currentUserHasAccess(UUID resourceId)`)
- [ ] Use authentication service to get current user context
- [ ] Implement resource-specific access rules

## Business Logic

- [ ] Implement specialized operations as separate methods
- [ ] Validate business constraints with clear error messages
- [ ] Use descriptive method names that reflect the business operation
- [ ] Encapsulate complex operations in transaction boundaries

# /mapper

## Mapper Configuration

- [ ] Use `@Mapper(componentModel = "spring")` to integrate with Spring's dependency injection
- [ ] Configure `nullValuePropertyMappingStrategy` to handle null values appropriately
- [ ] Set `unmappedTargetPolicy` to control behavior for unmapped fields

## Mapping Methods

- [ ] Create clear, purpose-specific mapping methods (e.g., `toResponseDto`, `toEntity`)
- [ ] Provide collection mapping methods (e.g., `toResponseDtoList`)
- [ ] Implement update methods with `@MappingTarget` for partial updates
- [ ] Use descriptive method names that reflect the transformation

## Field Mappings

- [ ] Use `@Mapping` annotations to configure field mappings
- [ ] Explicitly `ignore` fields that shouldn't be mapped automatically
- [ ] Use `expression` for complex mappings that require custom logic
- [ ] Consider source/target name differences with `source` and `target` attributes

## Custom Mapping Logic

- [ ] Implement `default` methods for complex mapping logic
- [ ] Create helper methods for repetitive mapping patterns
- [ ] Handle collections mapping with appropriate stream operations
- [ ] **Consider collection initialization patterns**:
    - If entity collections use `@Builder.Default private Set<T> items = new HashSet<>()`, null checks are unnecessary
    - If collections might be null (no default initialization), add null checks before streaming
    - Document your approach to ensure consistent handling across mappers

## Relationship Handling

- [ ] Ignore entity relationships in entity-to-DTO mappings
- [ ] Map IDs instead of full entity objects in DTOs
- [ ] Use custom methods to extract IDs from related entities
- [ ] Handle bidirectional relationships carefully

## Performance Considerations

- [ ] Use efficient collection operations
- [ ] Consider lazy-loading implications when mapping JPA entities
- [ ] Avoid unnecessary object creation
- [ ] Use appropriate collection types for your use case

## Documentation

- [ ] Document complex mapping methods
- [ ] Explain non-obvious mapping decisions

# /entity

## Base Entity Structure

- [x] Create a `@MappedSuperclass` base entity with common fields
- [x] Use UUID for entity identifiers
- [x] Implement optimistic locking with `@Version`
- [x] Include audit fields (createdAt, updatedAt)
- [x] Implement proper equals/hashCode based on entity ID
- [x] Use lifecycle callbacks for automatic timestamp management
- [x] Apply appropriate Lombok annotations to reduce boilerplate

## Entity Class Design

- [ ] Extend BaseEntity for all persistent entities
- [ ] Use `@Entity` and `@Table` annotations with appropriate naming
- [ ] Define indexes for frequently queried columns
- [ ] Specify unique constraints for business uniqueness rules
- [ ] Apply `@ToString` strategically (avoid lazy-loaded collections)
- [ ] Consider using `@Builder` for complex entity creation

## Field Definitions

- [ ] Define appropriate column constraints (nullable, length, etc.)
- [ ] Use validation annotations (@NotBlank, @Size, etc.) for bean validation
- [ ] Choose appropriate collection types (Set for unique collections, List for ordered)
- [ ] Initialize collections with empty collections using `@Builder.Default`
- [ ] Apply `@Enumerated(EnumType.STRING)` for enum fields
- [ ] Use `@Column(length=n)` to constrain string field sizes

## Relationships

- [ ] Define fetch type explicitly (usually LAZY for collections)
- [ ] Configure cascade operations appropriately
- [ ] Set up proper join tables/columns for many-to-many relationships
- [ ] Use `mappedBy` to specify the owning side of bidirectional relationships
- [ ] Consider `@OrderBy` for collections that have a natural order
- [ ] Use `orphanRemoval = true` when child entities shouldn't exist without parent

## Encapsulation & Domain Logic

- [ ] Make fields private with appropriate getters (already handled by Lombok @Getter)
- [ ] Use @Setter selectively only for fields that should be mutable
- [ ] Implement helper methods for bidirectional relationship management (addX/removeX)
- [ ] Create domain-specific methods that express business operations
- [ ] Protect collection modifier methods when direct modification should be controlled
- [ ] Implement business logic validation within entity methods

## Performance Considerations

- [ ] Avoid eager fetching of large collections
- [ ] Consider using @BatchSize for collections that are frequently accessed
- [ ] Be cautious with cascade operations on large collections
- [ ] Use appropriate collection types (HashSet for unordered, LinkedHashSet for insertion order)
- [ ] Limit the use of @ToString on large collections to prevent performance issues
- [ ] Consider database-specific optimizations when necessary

# /repository

## Repository Structure

- [ ] Extend appropriate Spring Data interface (JpaRepository, CrudRepository, etc.)
- [ ] Specify correct entity type and ID type as generic parameters
- [ ] Apply `@Repository` annotation (optional but recommended for consistency)
- [ ] Keep repository interfaces focused on a single entity type
- [ ] Organize imports logically and remove unused ones

## Method Naming

- [ ] Follow Spring Data query method naming conventions consistently
- [ ] Use clear, descriptive method names that reflect business operations
- [ ] Include search criteria in method name (e.g., `findByTitleContainingIgnoreCase`)
- [ ] Specify return type explicitly (Page, List, Optional, etc.)
- [ ] Use appropriate modifiers for case-insensitive searches when needed

## Pagination & Sorting

- [ ] Accept Pageable parameter for methods returning collections
- [ ] Return Page<T> for paginated results
- [ ] Consider providing non-paginated alternatives when appropriate
- [ ] Use consistent parameter ordering (search criteria first, then pageable)

## Query Methods

- [ ] Leverage Spring Data's method name query generation when possible
- [ ] Use derived query methods for simple queries
- [ ] Consider `@Query` annotation for complex queries
- [ ] Use named parameters for complex queries with `@Param`
- [ ] Implement count methods for frequently needed counts

## Relationship Handling

- [ ] Create methods to find by related entity IDs (e.g., `findByOwnersId`)
- [ ] Use appropriate join types for relationship queries
- [ ] Consider fetch joins for performance optimization when needed
- [ ] Implement methods for common relationship queries

## Performance Considerations

- [ ] Avoid fetching unnecessary data
- [ ] Consider projection interfaces for specific use cases
- [ ] Use exists methods instead of find+isEmpty for existence checks
- [ ] Implement count methods instead of find+size for counting
- [ ] Consider using query hints for performance-critical operations

## Documentation

- [ ] Keep documentation minimal and focused on non-obvious behavior
- [ ] Let method names be self-documenting through clear naming
- [ ] Document complex queries or methods with non-standard behavior
- [ ] Focus documentation efforts on the entity model instead

# /exception

## Exception Class Design

- [ ] Create specific exception classes for common error scenarios (ResourceNotFoundException,
  DuplicateResourceException)
- [ ] Use `@StandardException` from Lombok to reduce boilerplate in exception classes
- [ ] Apply `@ResponseStatus` to map exceptions directly to HTTP status codes
- [ ] Extend appropriate base exception types (RuntimeException for unchecked exceptions)
- [ ] Keep exception classes focused and single-purpose

## Global Exception Handler

- [ ] Implement a `@RestControllerAdvice` class to centralize exception handling
- [ ] Extend `ResponseEntityExceptionHandler` to handle Spring's built-in exceptions
- [ ] Create specific handlers for common exceptions with `@ExceptionHandler`
- [ ] Implement a catch-all handler for unexpected exceptions
- [ ] Use appropriate logging levels based on exception severity

## Error Response Format

- [ ] Use Spring's `ProblemDetail` for standardized error responses
- [ ] Include timestamp in error responses
- [ ] Provide clear, user-friendly error messages
- [ ] Avoid exposing sensitive information or stack traces in responses
- [ ] Format validation errors to clearly indicate the problematic fields

## Database Exception Handling

- [ ] Handle constraint violations with specific, helpful messages
- [ ] Parse constraint names to provide context-specific error messages
- [ ] Distinguish between different constraint types (unique, foreign key, check)
- [ ] Map database errors to appropriate HTTP status codes
- [ ] Log detailed database errors for debugging while returning sanitized messages

## Validation Exception Handling

- [ ] Handle `MethodArgumentNotValidException` for bean validation failures
- [ ] Format validation errors to include both field names and error messages
- [ ] Return appropriate HTTP status codes for validation errors (400 Bad Request)
- [ ] Consider grouping related validation errors for better client experience
- [ ] Ensure validation error messages are user-friendly

## Logging Strategy

- [ ] Log client errors (4xx) at WARN level
- [ ] Log server errors (5xx) at ERROR level with full exception details
- [ ] Include relevant context in log messages
- [ ] Avoid logging sensitive information
- [ ] Use appropriate log formats for easier parsing and analysis

## Security Exception Handling

- [ ] Handle authentication and authorization exceptions appropriately
- [ ] Avoid revealing security-sensitive information in error messages
- [ ] Return consistent responses for security-related errors
- [ ] Consider rate limiting for authentication failure endpoints
- [ ] Log security exceptions with sufficient detail for audit purposes

# /config/security

## Configuration Structure

- [ ] Separate configuration concerns into focused classes (SecurityConfig, TokenProps, etc.)
- [ ] Use `@Configuration` and `@EnableWebSecurity` for security configuration classes
- [ ] Externalize security properties using `@ConfigurationProperties`
- [ ] Implement custom filters as separate components
- [ ] Use constructor injection with `@RequiredArgsConstructor` for dependencies

## JWT Implementation

- [ ] Store JWT secret in externalized configuration (not hardcoded)
- [ ] Configure appropriate token expiration times
- [ ] Implement both access and refresh token mechanisms
- [ ] Use secure algorithms (e.g., HMAC-SHA256) for token signing
- [ ] Create a dedicated bean for JWT decoder/encoder
- [ ] Validate token claims (issuer, expiration, etc.)

## Authentication Flow

- [ ] Implement OAuth2 login with appropriate success handlers
- [ ] Configure OAuth2 resource server for JWT validation
- [ ] Create custom authentication success handlers for post-login actions
- [ ] Implement user creation/retrieval during OAuth authentication
- [ ] Return appropriate tokens after successful authentication

## Authorization & Access Control

- [ ] Configure explicit authorization rules for all endpoints
- [ ] Use method-level security with `@PreAuthorize` for fine-grained control
- [ ] Implement proper role/authority management
- [ ] Secure sensitive endpoints with appropriate authentication requirements
- [ ] Allow public access only to necessary endpoints (auth, health, docs)

## Security Headers & Protection

- [ ] Configure CORS with appropriate origin restrictions
- [ ] Consider CSRF protection strategy (disabled for stateless APIs with proper token validation)
- [ ] Set appropriate Content-Security-Policy headers
- [ ] Configure frame options based on environment (development vs. production)
- [ ] Implement HTTP-only, secure cookies if using cookie-based authentication

## Error Handling & Logging

- [ ] Implement proper exception handling for authentication failures
- [ ] Use appropriate logging levels for security events
- [ ] Avoid exposing sensitive information in error responses
- [ ] Log authentication successes and failures with relevant details
- [ ] Implement rate limiting for authentication endpoints

## Environment-Specific Configuration

- [ ] Use profile-specific security settings (dev vs. prod)
- [ ] Apply stricter security in production environments
- [ ] Enable development-only features (H2 console, etc.) conditionally
- [ ] Use environment variables for sensitive configuration in production
- [ ] Document environment-specific security considerations

## Token Management

- [ ] Implement token refresh mechanism
- [ ] Consider token revocation strategy
- [ ] Store tokens securely on the client side
- [ ] Include only necessary claims in tokens
- [ ] Use appropriate token scopes for different operations
