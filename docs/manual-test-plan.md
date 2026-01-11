# TaskTrack API - Manual Test Plan

## Overview

Manual testing of the User Management REST API endpoints.

**Base URL:** `http://localhost:8080/api/users`  
**Authentication:** Basic Auth (admin/admin)

---

## Test Environment Setup

### Prerequisites

- [ ] PostgreSQL running on `localhost:5432`
- [ ] Database `tasktrackdb` exists
- [ ] Application running: `mvn spring-boot:run`
- [ ] Tool ready: Postman, Insomnia, or cURL

### Authentication Header (for all requests)

```
Authorization: Basic YWRtaW46YWRtaW4=
```

(Base64 of `admin:admin`)

---

## Test Cases

### TC-01: Create User - Happy Path

| Field        | Value           |
| ------------ | --------------- |
| **Endpoint** | POST /api/users |
| **Expected** | 201 Created     |

**Request Body:**

```json
{
  "userName": "john.doe",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "role": "DEVELOPER"
}
```

**cURL:**

```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4=" \
  -d '{"userName":"john.doe","email":"john.doe@example.com","fullName":"John Doe","role":"DEVELOPER"}'
```

**Expected Response:** 201 Created with UserDTO containing id, timestamps, active=true

---

### TC-02: Create User - Duplicate Username

| Field            | Value                          |
| ---------------- | ------------------------------ |
| **Endpoint**     | POST /api/users                |
| **Precondition** | User "john.doe" already exists |
| **Expected**     | 409 Conflict                   |

**Request Body:**

```json
{
  "userName": "john.doe",
  "email": "different@example.com",
  "fullName": "Different User",
  "role": "TESTER"
}
```

**Expected Response:**

```json
{
  "timestamp": "...",
  "status": 409,
  "error": "Conflict",
  "message": "Username already exists: john.doe",
  "path": "/api/users"
}
```

---

### TC-03: Create User - Duplicate Email

| Field            | Value                                       |
| ---------------- | ------------------------------------------- |
| **Endpoint**     | POST /api/users                             |
| **Precondition** | Email "john.doe@example.com" already exists |
| **Expected**     | 409 Conflict                                |

**Request:** Use different username but same email as TC-01

---

### TC-04: Create User - Validation Errors

| Field        | Value           |
| ------------ | --------------- |
| **Endpoint** | POST /api/users |
| **Expected** | 400 Bad Request |

**Request Body (missing required fields):**

```json
{
  "userName": "",
  "email": "invalid-email",
  "fullName": "",
  "role": null
}
```

**Expected Response:**

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for one or more fields",
  "details": {
    "userName": "Username is required",
    "email": "Email must be a valid email address",
    "fullName": "Full name is required",
    "role": "Role is required"
  }
}
```

---

### TC-05: Get All Users - Happy Path

| Field        | Value          |
| ------------ | -------------- |
| **Endpoint** | GET /api/users |
| **Expected** | 200 OK         |

**cURL:**

```bash
curl -X GET http://localhost:8080/api/users \
  -H "Authorization: Basic YWRtaW46YWRtaW4="
```

**Expected Response:** Array of UserDTO objects

---

### TC-06: Get User by ID - Happy Path

| Field        | Value               |
| ------------ | ------------------- |
| **Endpoint** | GET /api/users/{id} |
| **Expected** | 200 OK              |

**cURL:**

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Basic YWRtaW46YWRtaW4="
```

---

### TC-07: Get User by ID - Not Found

| Field        | Value               |
| ------------ | ------------------- |
| **Endpoint** | GET /api/users/9999 |
| **Expected** | 404 Not Found       |

**Expected Response:**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 9999",
  "path": "/api/users/9999"
}
```

---

### TC-08: Update User - Happy Path

| Field            | Value                 |
| ---------------- | --------------------- |
| **Endpoint**     | PUT /api/users/{id}   |
| **Precondition** | User with id=1 exists |
| **Expected**     | 200 OK                |

**Request Body:**

```json
{
  "userName": "john.updated",
  "email": "john.updated@example.com",
  "fullName": "John Updated",
  "role": "PROJECT_MANAGER",
  "active": true
}
```

**cURL:**

```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4=" \
  -d '{"userName":"john.updated","email":"john.updated@example.com","fullName":"John Updated","role":"PROJECT_MANAGER","active":true}'
```

---

### TC-09: Update User - Not Found

| Field        | Value               |
| ------------ | ------------------- |
| **Endpoint** | PUT /api/users/9999 |
| **Expected** | 404 Not Found       |

---

### TC-10: Delete User (Soft Delete) - Happy Path

| Field        | Value                  |
| ------------ | ---------------------- |
| **Endpoint** | DELETE /api/users/{id} |
| **Expected** | 200 OK                 |

**cURL:**

```bash
curl -X DELETE http://localhost:8080/api/users/1 \
  -H "Authorization: Basic YWRtaW46YWRtaW4="
```

**Expected:** User returned with `active: false`

---

### TC-11: Reactivate User - Happy Path

| Field            | Value                                |
| ---------------- | ------------------------------------ |
| **Endpoint**     | PUT /api/users/{id}/reactivate       |
| **Precondition** | User with id=1 is inactive (deleted) |
| **Expected**     | 200 OK                               |

**cURL:**

```bash
curl -X PUT http://localhost:8080/api/users/1/reactivate \
  -H "Authorization: Basic YWRtaW46YWRtaW4="
```

**Expected:** User returned with `active: true`

---

### TC-12: Get Active Users Only

| Field        | Value                 |
| ------------ | --------------------- |
| **Endpoint** | GET /api/users/active |
| **Expected** | 200 OK                |

**Expected:** Only users with `active: true`

---

### TC-13: Get Users by Role

| Field        | Value                         |
| ------------ | ----------------------------- |
| **Endpoint** | GET /api/users/role/DEVELOPER |
| **Expected** | 200 OK                        |

**Valid Roles:** ADMIN, PROJECT_MANAGER, DEVELOPER, TESTER

**cURL:**

```bash
curl -X GET http://localhost:8080/api/users/role/DEVELOPER \
  -H "Authorization: Basic YWRtaW46YWRtaW4="
```

---

### TC-14: Invalid Role Parameter

| Field        | Value                            |
| ------------ | -------------------------------- |
| **Endpoint** | GET /api/users/role/INVALID_ROLE |
| **Expected** | 400 Bad Request                  |

---

### TC-15: Unauthorized Access

| Field        | Value                     |
| ------------ | ------------------------- |
| **Endpoint** | Any endpoint without auth |
| **Expected** | 401 Unauthorized          |

**cURL:**

```bash
curl -X GET http://localhost:8080/api/users
```

---

## Test Execution Checklist

| TC#   | Test Case                        | Status | Notes |
| ----- | -------------------------------- | ------ | ----- |
| TC-01 | Create User - Happy Path         | ⬜     |       |
| TC-02 | Create User - Duplicate Username | ⬜     |       |
| TC-03 | Create User - Duplicate Email    | ⬜     |       |
| TC-04 | Create User - Validation Errors  | ⬜     |       |
| TC-05 | Get All Users                    | ⬜     |       |
| TC-06 | Get User by ID                   | ⬜     |       |
| TC-07 | Get User by ID - Not Found       | ⬜     |       |
| TC-08 | Update User - Happy Path         | ⬜     |       |
| TC-09 | Update User - Not Found          | ⬜     |       |
| TC-10 | Delete User (Soft Delete)        | ⬜     |       |
| TC-11 | Reactivate User                  | ⬜     |       |
| TC-12 | Get Active Users                 | ⬜     |       |
| TC-13 | Get Users by Role                | ⬜     |       |
| TC-14 | Invalid Role Parameter           | ⬜     |       |
| TC-15 | Unauthorized Access              | ⬜     |       |

---

## Test Data

### Sample Users for Testing

```json
[
  {
    "userName": "alice.dev",
    "email": "alice@example.com",
    "fullName": "Alice Developer",
    "role": "DEVELOPER"
  },
  {
    "userName": "bob.pm",
    "email": "bob@example.com",
    "fullName": "Bob Manager",
    "role": "PROJECT_MANAGER"
  },
  {
    "userName": "charlie.qa",
    "email": "charlie@example.com",
    "fullName": "Charlie Tester",
    "role": "TESTER"
  },
  {
    "userName": "diana.admin",
    "email": "diana@example.com",
    "fullName": "Diana Admin",
    "role": "ADMIN"
  }
]
```

---

## Bug Report Template

**Bug ID:** BUG-XXX  
**Test Case:** TC-XX  
**Severity:** Critical / High / Medium / Low  
**Status:** Open / In Progress / Resolved

**Steps to Reproduce:**

1. ...
2. ...

**Expected Result:**  
...

**Actual Result:**  
...

**Screenshots/Logs:**  
...
