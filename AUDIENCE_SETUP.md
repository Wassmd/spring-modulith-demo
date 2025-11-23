# Keycloak Audience Setup Guide

This guide explains how to configure Keycloak to include the required audience (`spring-modulith-demo-api`) in JWT tokens.

## Why Audience Validation?

Audience validation ensures that JWT tokens are intended for your specific service. This prevents tokens meant for other services from being accepted by your API.

## Setup Steps

### 1. Create Client Scope for Audience

1. Open Keycloak Admin Console: http://localhost:8180
2. Select your realm (e.g., `master`)
3. Navigate to **Client Scopes** → **Create client scope**
4. Configure:
   - **Name**: `spring-modulith-demo-api-audience`
   - **Type**: `Default`
   - **Protocol**: `openid-connect`
   - Click **Save**

### 2. Add Audience Mapper

1. In the newly created client scope, go to **Mappers** tab
2. Click **Add mapper** → **By configuration**
3. Select **Audience**
4. Configure:
   - **Name**: `spring-modulith-demo-api-audience-mapper`
   - **Mapper Type**: `Audience`
   - **Included Custom Audience**: `spring-modulith-demo-api`
   - **Add to access token**: **ON**
   - **Add to ID token**: **OFF** (optional)
5. Click **Save**

### 3. Assign Client Scope to Clients

For each client that should access your API:

#### For `spring-modulith-demo` client:
1. Go to **Clients** → **spring-modulith-demo**
2. Go to **Client scopes** tab
3. Click **Add client scope**
4. Select `spring-modulith-demo-api-audience`
5. Choose **Default** (so it's always included)
6. Click **Add**

#### For `spring-modulith-demo-admin` client:
1. Go to **Clients** → **spring-modulith-demo-admin**
2. Go to **Client scopes** tab
3. Click **Add client scope**
4. Select `spring-modulith-demo-api-audience`
5. Choose **Default**
6. Click **Add**

### 4. Verify the Token

After configuration, request a new token and decode it (use jwt.io):

```bash
curl -X POST 'http://localhost:8180/realms/master/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=client_credentials' \
  -d 'client_id=spring-modulith-demo' \
  -d 'client_secret=iYbaAuVlBrb7xUAvZ2S4hFFBmDcYKM3f'
```

The decoded JWT should contain:

```json
{
  "aud": ["spring-modulith-demo-api", "account"],
  ...
}
```

## Testing

### Valid Token (with correct audience):
```bash
# Get token with correct audience
TOKEN=$(curl -s -X POST 'http://localhost:8180/realms/master/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=client_credentials' \
  -d 'client_id=spring-modulith-demo' \
  -d 'client_secret=iYbaAuVlBrb7xUAvZ2S4hFFBmDcYKM3f' | jq -r '.access_token')

# Use token to access API
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/customers
```

✅ Should work if audience is configured correctly

### Invalid Token (without correct audience):
If you try to use a token from a different service or client without the correct audience:

```bash
curl -H "Authorization: Bearer $INVALID_TOKEN" http://localhost:8080/customers
```

❌ Will return: `401 Unauthorized` with error message about missing audience

## Troubleshooting

### Token rejected with "invalid_token" error
- Verify the client scope is added to your client as **Default** (not Optional)
- Check that the audience mapper has **Add to access token** enabled
- Request a new token after making changes
- Decode the token at jwt.io to verify the `aud` claim contains `spring-modulith-demo-api`

### Application still accepts tokens without audience
- Restart your Spring Boot application after configuration changes
- Verify `jwtDecoder()` bean is properly configured in `SecurityConfig.java`
- Check application logs for any JWT validation errors

