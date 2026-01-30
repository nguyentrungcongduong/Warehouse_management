WMS API DOCUMENTATION - TỔNG QUAN
1. RESPONSE STRUCTURE CHUẨN
1.1 Success Response
json{
  "success": true,
  "message": "success",
  "data": { ... },
  "timestamp": "2026-01-27T10:30:00Z"
}
1.2 Paged Response
json{
  "success": true,
  "message": "success",
  "data": { 
    "content": {
        
    }
    "page": 0,
    "size": 10,
    "totalElements": 6,
    "totalPages": 1,
    "last": true
   },
  "timestamp": "2026-01-27T10:30:00Z"
}
1.2 Error Response
json{
  "success": false,
  "error": {
    "type": "problem/unauthorized",
    "title": "Unauthorized",
    "status": 401,
    "detail": "Token không hợp lệ (hết hạn, không đúng định dạng, hoặc không truyền JWT ở header).",
    "message": "error.invalid_token",
    "params": "Authentication"
    },
    "timestamp": "2026-01-30T09:34:03.602842700Z"
}
```

---

---

## 2. ERROR CODES

| Code | HTTP Status | Mô tả |
|------|-------------|-------|
| `VALIDATION_ERROR` | 400 | Dữ liệu đầu vào không hợp lệ |
| `INSUFFICIENT_STOCK` | 400 | Không đủ hàng |
| `BATCH_EXPIRED` | 400 | Lô hàng đã hết hạn |
| `UNAUTHORIZED` | 401 | Chưa đăng nhập |
| `TOKEN_EXPIRED` | 401 | Token hết hạn |
| `FORBIDDEN` | 403 | Không có quyền |
| `NOT_FOUND` | 404 | Không tìm thấy |
| `DUPLICATE_ENTRY` | 409 | Dữ liệu trùng |
| `INTERNAL_SERVER_ERROR` | 500 | Lỗi server |

---

## 3. API ENDPOINTS BY MODULE

### 3.1 Module: AUTHENTICATION
```
POST   /api/auth/login
POST   /api/auth/refresh
POST   /api/auth/logout
```

### 3.2 Module: INVENTORY (RFID/Barcode)
```
# Stock Count Sessions
POST   /api/inventory/stock-count/sessions
GET    /api/inventory/stock-count/sessions
GET    /api/inventory/stock-count/sessions/{id}
POST   /api/inventory/stock-count/sessions/{id}/start
POST   /api/inventory/stock-count/sessions/{id}/complete
DELETE /api/inventory/stock-count/sessions/{id}

# Scanning
WS     /ws/inventory/scan
GET    /api/inventory/scan-logs

# Discrepancies & Adjustments
GET    /api/inventory/discrepancies
POST   /api/inventory/adjustments
GET    /api/inventory/adjustments
GET    /api/inventory/adjustments/{id}
POST   /api/inventory/adjustments/{id}/approve
POST   /api/inventory/adjustments/{id}/reject
```

### 3.3 Module: FEFO
```
# Batch Management
POST   /api/fefo/batches
GET    /api/fefo/batches
GET    /api/fefo/batches/{id}
PUT    /api/fefo/batches/{id}
DELETE /api/fefo/batches/{id}
POST   /api/fefo/batches/{id}/locations

# FEFO Picking
POST   /api/fefo/picking/generate
GET    /api/fefo/picking/{id}
POST   /api/fefo/picking/{id}/confirm
DELETE /api/fefo/picking/{id}

# Expiry Alerts
GET    /api/fefo/expiry-alerts
GET    /api/fefo/expiry-alerts/{id}
POST   /api/fefo/expiry-alerts/{id}/acknowledge

# Reports
GET    /api/fefo/reports/expiring-soon
GET    /api/fefo/reports/batch-movement
```

### 3.4 Module: CROSS-DOCKING
```
# Inbound Shipments
POST   /api/crossdock/inbound
GET    /api/crossdock/inbound
GET    /api/crossdock/inbound/{id}
PUT    /api/crossdock/inbound/{id}
POST   /api/crossdock/inbound/{id}/arrive
POST   /api/crossdock/inbound/{id}/receive
DELETE /api/crossdock/inbound/{id}

# Outbound Orders
POST   /api/crossdock/outbound
GET    /api/crossdock/outbound
GET    /api/crossdock/outbound/{id}
PUT    /api/crossdock/outbound/{id}
POST   /api/crossdock/outbound/{id}/confirm
DELETE /api/crossdock/outbound/{id}

# Cross-dock Matching
POST   /api/crossdock/matching/auto
POST   /api/crossdock/matching/manual
GET    /api/crossdock/mappings
GET    /api/crossdock/mappings/{id}
POST   /api/crossdock/mappings/{id}/move-to-staging
POST   /api/crossdock/mappings/{id}/ready-to-ship
POST   /api/crossdock/mappings/{id}/shipped

# Reports
GET    /api/crossdock/reports/performance
GET    /api/crossdock/reports/efficiency
```

### 3.5 Module: PACKAGING
```
# Packaging Materials
POST   /api/packaging/materials
GET    /api/packaging/materials
GET    /api/packaging/materials/{id}
PUT    /api/packaging/materials/{id}
DELETE /api/packaging/materials/{id}
PUT    /api/packaging/materials/{id}/stock
GET    /api/packaging/materials/low-stock

# Packaging Templates
POST   /api/packaging/templates
GET    /api/packaging/templates
GET    /api/packaging/templates/{id}
PUT    /api/packaging/templates/{id}
DELETE /api/packaging/templates/{id}

# Packaging Calculation & Usage
POST   /api/packaging/calculate
POST   /api/packaging/usage
GET    /api/packaging/usage/history

# Reports
GET    /api/packaging/reports/costs
GET    /api/packaging/reports/consumption
```

### 3.6 Module: WAREHOUSE CORE
```
# Warehouses
POST   /api/warehouse/warehouses
GET    /api/warehouse/warehouses
GET    /api/warehouse/warehouses/{id}
PUT    /api/warehouse/warehouses/{id}

# Zones
POST   /api/warehouse/zones
GET    /api/warehouse/zones
GET    /api/warehouse/zones/{id}
PUT    /api/warehouse/zones/{id}

# Locations
POST   /api/warehouse/locations
GET    /api/warehouse/locations
GET    /api/warehouse/locations/{id}
PUT    /api/warehouse/locations/{id}

# Products
POST   /api/warehouse/products
GET    /api/warehouse/products
GET    /api/warehouse/products/{id}
PUT    /api/warehouse/products/{id}
DELETE /api/warehouse/products/{id}

# Categories
POST   /api/warehouse/categories
GET    /api/warehouse/categories
GET    /api/warehouse/categories/{id}
PUT    /api/warehouse/categories/{id}

# Suppliers
POST   /api/warehouse/suppliers
GET    /api/warehouse/suppliers
GET    /api/warehouse/suppliers/{id}

# Customers
POST   /api/warehouse/customers
GET    /api/warehouse/customers
GET    /api/warehouse/customers/{id}
```

---

## 4. REQUEST FLOW (Luồng xử lý 1 request)

### 4.1 Luồng tổng quan
```
Client Request
    ↓
Security Filter (JWT Authentication)
    ↓
Controller (Presentation Layer)
    ├─ Validate Request DTO (@Valid)
    ├─ Handle HTTP (Request/Response)
    └─ Delegate to Application Service
        ↓
Application Service (Application Layer)
    ├─ Orchestration (điều phối)
    ├─ Transaction Management (@Transactional)
    ├─ Convert DTO → Domain
    ├─ Call Domain Service/Repository
    ├─ Publish Domain Events
    └─ Convert Domain → DTO Response
        ↓
Domain Service (Domain Layer)
    ├─ Core Business Logic
    ├─ Complex algorithms (FEFO, Matching)
    └─ Coordinate multiple Entities
        ↓
Domain Model (Aggregate Root/Entity)
    ├─ Business Rules
    ├─ Validate Invariants
    ├─ State Changes
    └─ Raise Domain Events
        ↓
Repository Interface (Domain Layer)
    ↓
Repository Implementation (Infrastructure Layer)
    ├─ Convert Domain ↔ JPA Entity
    ├─ Call JPA Repository
    └─ Return Domain Object
        ↓
JPA Repository (Spring Data)
    ↓
Database (PostgreSQL)
    ↓
Response travels back up
    ↓
Client receives JSON Response
```

### 4.2 Ví dụ cụ thể: POST /api/fefo/picking/generate
```
1. CONTROLLER (PickingController)
   ├─ @PostMapping("/generate")
   ├─ Validate PickingListRequest (@Valid)
   ├─ Check authentication (JWT)
   └─ Call: fefoPickingService.generatePickingList(request)

2. APPLICATION SERVICE (FefoPickingService)
   ├─ @Transactional - Bắt đầu transaction
   ├─ Get data: batchRepository.findActiveBatchesByProductId(productId)
   ├─ Convert DTO → Domain: new Quantity(request.getQuantity())
   ├─ Call Domain Service: fefoAlgorithm.generatePickingList(batches, quantity)
   ├─ Save changes: batches.forEach(batchRepository::save)
   ├─ Publish events: batch.getDomainEvents().forEach(eventPublisher::publish)
   └─ Convert Domain → DTO: mapper.toResponse(pickingList)

3. DOMAIN SERVICE (FefoAlgorithm)
   ├─ Sort batches by expiry date (FEFO logic)
   ├─ Loop through batches
   ├─ Call: batch.reserve(quantity) for each batch
   ├─ Create PickingList aggregate
   └─ Return PickingList

4. DOMAIN MODEL (Batch)
   ├─ reserve(Quantity quantity):
   │   ├─ Validate: if (!availableQuantity >= quantity) throw Exception
   │   ├─ Validate: if (status != ACTIVE) throw Exception
   │   ├─ Change state: reservedQuantity += quantity
   │   └─ Raise event: BatchReservedEvent
   └─ Return updated Batch

5. REPOSITORY (BatchRepositoryImpl)
   ├─ Convert Domain → JPA Entity: mapper.toEntity(batch)
   ├─ Save: jpaRepository.save(entity)
   ├─ Convert JPA Entity → Domain: mapper.toDomain(saved)
   └─ Return Domain Batch

6. JPA REPOSITORY
   ├─ Execute: UPDATE batches SET reserved_quantity = ...
   └─ Return saved entity

7. RESPONSE
   └─ Return JSON:
      {
        "success": true,
        "data": {
          "pickingListId": 1,
          "items": [...]
        }
      }
```

### 4.3 Error Flow
```
Domain Layer throws Exception
    ↓
Application Service catches (hoặc không catch)
    ↓
Controller catches (hoặc không catch)
    ↓
Global Exception Handler (@RestControllerAdvice)
    ├─ @ExceptionHandler(InsufficientStockException.class)
    ├─ @ExceptionHandler(NotFoundException.class)
    ├─ @ExceptionHandler(ValidationException.class)
    └─ @ExceptionHandler(Exception.class)
        ↓
Return Error Response:
{
  "success": false,
  "error": {
    "code": "INSUFFICIENT_STOCK",
    "message": "Không đủ hàng",
    "details": [...]
  }
}
```

---

## 5. LAYER RESPONSIBILITIES

### Controller Layer
- ✅ Validate input DTO
- ✅ HTTP handling (status codes, headers)
- ✅ Delegate to Application Service
- ❌ KHÔNG chứa business logic

### Application Service Layer
- ✅ Orchestration (điều phối workflow)
- ✅ Transaction management
- ✅ DTO ↔ Domain conversion
- ✅ Publish domain events
- ❌ KHÔNG chứa business logic phức tạp

### Domain Service Layer
- ✅ Core business logic
- ✅ Complex algorithms
- ✅ Coordinate multiple entities
- ❌ KHÔNG access database directly

### Domain Model Layer
- ✅ Business rules
- ✅ Validate invariants
- ✅ State changes
- ✅ Raise domain events
- ❌ KHÔNG biết về persistence

### Repository Layer
- ✅ Implement Repository interface
- ✅ Convert Domain ↔ JPA Entity
- ✅ Database operations
- ❌ KHÔNG chứa business logic

---

## 6. COMMON PATTERNS

### 6.1 Pagination
```
Query params: ?page=0&size=20&sort=field,asc
```

### 6.2 Filtering
```
?status=ACTIVE&warehouseId=1&fromDate=2026-01-01
```

### 6.3 Search
```
?search=keyword
```

### 6.4 Date Format
```
ISO 8601: 2026-01-27T10:30:00Z
Date only: 2026-01-27
```

### 6.5 Authentication
```
Header: Authorization: Bearer <JWT_TOKEN>

7. VALIDATION STRATEGY
Level 1: DTO Validation (Controller)
java@NotNull
@Size(min = 5, max = 20)
@Pattern(regexp = "^[A-Z0-9]+$")
Level 2: Domain Validation (Value Objects)
javapublic BatchNumber(String value) {
    if (!value.matches("...")) throw Exception;
}
Level 3: Business Rules (Domain Model)
javapublic void reserve(Quantity qty) {
    if (availableQty < qty) throw Exception;
}
```

---

## 8. STATUS TRANSITIONS

### Batch Status
```
ACTIVE → QUARANTINE → ACTIVE
ACTIVE → EXPIRED
ACTIVE → DEPLETED
```

### Stock Count Status
```
PLANNED → IN_PROGRESS → COMPLETED → APPROVED
                      → CANCELLED
```

### Inbound Status
```
PLANNED → IN_TRANSIT → ARRIVED → RECEIVING → COMPLETED
                                           → CANCELLED
```

### Outbound Status
```
PENDING → CONFIRMED → PICKING → PICKED → PACKING → PACKED → SHIPPED → DELIVERED
                                                                     → CANCELLED
```

### Cross-dock Mapping Status
```
MATCHED → RECEIVING → STAGING → READY_TO_SHIP → SHIPPED → COMPLETED