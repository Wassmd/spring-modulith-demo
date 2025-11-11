# Keycloak Setup Guide

## Overview
This project uses Keycloak for OAuth2/OIDC authentication. Keycloak is configured in Docker Compose and runs on port 8180.

## Starting Keycloak

Keycloak will start automatically with Docker Compose:
```bash
docker compose up -d
```

## Accessing Keycloak Admin Console

1. **URL**: http://localhost:8180
2. **Admin Username**: `admin`
3. **Admin Password**: `admin`

## Initial Realm Setup

### 1. Create a Realm
1. Log in to Keycloak Admin Console
2. Click on the dropdown in the top-left (currently "master")
3. Click "Create Realm"
4. Name: `spring-modulith`
5. Click "Create"

### 2. Create a Client
1. In the `spring-modulith` realm, go to "Clients"
2. Click "Create client"
3. Configure:
   - **Client ID**: `spring-modulith-client`
   - **Client Type**: OpenID Connect
   - Click "Next"
4. Capability config:
   - Enable "Client authentication" (for confidential client)
   - Enable "Authorization"
   - Enable "Standard flow"
   - Enable "Direct access grants"
   - Click "Next"
5. Login settings:
   - **Valid redirect URIs**: `http://localhost:8080/*`
   - **Web origins**: `http://localhost:8080`
   - Click "Save"
6. Go to "Credentials" tab and copy the **Client Secret**

### 3. Create Roles
1. Go to "Realm roles" in the left menu
2. Click "Create role"
3. Create the following roles:
   - `user` - Basic user role
   - `admin` - Administrator role
   - `customer` - Customer role
   - `order_manager` - Order management role

### 4. Create a Test User
1. Go to "Users" in the left menu
2. Click "Create new user"
3. Configure:
   - **Username**: `testuser`
   - **Email**: `testuser@example.com`
   - **First name**: `Test`
   - **Last name**: `User`
   - **Email verified**: ON
   - Click "Create"
4. Go to "Credentials" tab:
   - Click "Set password"
   - **Password**: `password`
   - **Temporary**: OFF
   - Click "Save"
5. Go to "Role mapping" tab:
   - Click "Assign role"
   - Select `user` and other desired roles
   - Click "Assign"

## Getting an Access Token

### Using curl (Password Grant)
```bash
curl -X POST http://localhost:8180/realms/spring-modulith/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=spring-modulith-client" \
  -d "client_secret=YOUR_CLIENT_SECRET" \
  -d "username=testuser" \
  -d "password=password" \
  -d "grant_type=password"
```

### Using Postman
1. Create a new request
2. Go to "Authorization" tab
3. Type: `OAuth 2.0`
4. Configuration:
   - **Grant Type**: Password Credentials
   - **Access Token URL**: `http://localhost:8180/realms/spring-modulith/protocol/openid-connect/token`
   - **Client ID**: `spring-modulith-client`
   - **Client Secret**: YOUR_CLIENT_SECRET
   - **Username**: `testuser`
   - **Password**: `password`
   - **Scope**: `openid profile email`
5. Click "Get New Access Token"
6. Click "Use Token"

## Testing Protected Endpoints

### Using curl with Access Token
```bash
# Get access token and store it
TOKEN=$(curl -X POST http://localhost:8180/realms/spring-modulith/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "client_id=spring-modulith-client" \
  -d "client_secret=YOUR_CLIENT_SECRET" \
  -d "username=testuser" \
  -d "password=password" \
  -d "grant_type=password" | jq -r '.access_token')

# Use the token to access protected endpoints
curl -X GET http://localhost:8080/orders \
  -H "Authorization: Bearer $TOKEN"

# Create an order
curl -X POST http://localhost:8080/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "lineItems": [
      {"product": 1, "quantity": 2},
      {"product": 2, "quantity": 1}
    ]
  }'
```

## Application Configuration

The Spring Boot application is configured in `application.yml`:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/spring-modulith
          jwk-set-uri: http://localhost:8180/realms/spring-modulith/protocol/openid-connect/certs
```

## Public Endpoints

The following endpoints are accessible without authentication:
- `/actuator/**` - Actuator endpoints
- `/apidoc/**` - Swagger UI
- `/v3/api-docs/**` - OpenAPI documentation
- `/swagger-ui/**` - Swagger UI resources

All other endpoints require a valid JWT token.

## Security Features

### JWT Token Validation
- Tokens are validated against Keycloak's public key
- Token expiration is checked
- Issuer validation ensures tokens come from the correct realm

### Role Mapping
The application extracts roles from:
1. **OAuth2 Scopes** - Standard scope-based authorities
2. **Realm Roles** - Keycloak realm-level roles (prefixed with `ROLE_`)
3. **Client Roles** - Application-specific roles (prefixed with `ROLE_`)

### Authorization
You can use Spring Security's `@PreAuthorize` annotation to secure methods:

```java
@PreAuthorize("hasRole('admin')")
public void deleteOrder(int orderId) {
    // Only users with 'admin' role can execute this
}

@PreAuthorize("hasAnyRole('user', 'customer')")
public Order getOrder(int orderId) {
    // Users with 'user' or 'customer' role can execute this
}
```

## Troubleshooting

### Keycloak not starting
- Check if port 8180 is already in use
- Verify PostgreSQL is running (Keycloak uses it for persistence)
- Check Docker logs: `docker compose logs keycloak`

### 401 Unauthorized
- Verify the token is valid and not expired
- Check the token is included in the `Authorization` header as `Bearer <token>`
- Verify the realm name matches in both Keycloak and application.yml

### 403 Forbidden
- User is authenticated but doesn't have the required role
- Check role mappings in Keycloak
- Verify role extraction in `SecurityConfig.java`

## Additional Resources

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/)
- [JWT.io](https://jwt.io/) - Decode and inspect JWT tokens

