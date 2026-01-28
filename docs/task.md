PHÂN CHIA TASK CHO 2 NGƯỜI (FE + BE)
CHIẾN LƯỢC PHÂN CHIA
Nguyên tắc:

BE phải hoàn thành trước → FE mới integrate được
Chia theo Module → Mỗi người làm end-to-end 1 module
Ưu tiên Module đơn giản trước → RFID/Inventory → FEFO → Cross-dock → Packaging
Tổng thời gian: 12-16 tuần


PHASE BREAKDOWN
🔧 PHASE 0: SETUP (Tuần 1-2) - CẢ 2 CÙNG LÀM
Backend Dev:

 Setup Spring Boot project structure
 Setup PostgreSQL database
 Tạo database schema (Flyway migration)
 Setup JWT Authentication
 Setup Global Exception Handler (RFC 7807)
 Tạo base classes (BaseEntity, BaseRepository, etc.)
 Setup Swagger/OpenAPI documentation
 Seed data (categories, warehouses, users, products mẫu)

Frontend Dev:

 Setup React project (Vite/CRA)
 Setup folder structure
 Setup Routing (React Router)
 Setup State Management (Redux Toolkit / Zustand)
 Setup Axios interceptors (JWT handling)
 Setup UI Library (Ant Design / Material-UI)
 Tạo base components (Layout, Sidebar, Header)
 Tạo Login page + Auth flow

Deliverable: Project skeleton hoàn chỉnh, Login working

📦 PHASE 1: MODULE WAREHOUSE CORE (Tuần 3-4)

Làm module này trước vì các module khác phụ thuộc vào nó

Backend Dev (Tuần 3-4):

 Products Management

 Entity: Product, Category
 Repository: ProductRepository, CategoryRepository
 Service: ProductService
 Controller: ProductController
 APIs: CRUD products, categories


 Warehouse & Locations

 Entity: Warehouse, Zone, StorageLocation
 Repository: WarehouseRepository, LocationRepository
 Service: WarehouseService, LocationService
 Controller: WarehouseController, LocationController
 APIs: CRUD warehouses, zones, locations


 Suppliers & Customers

 Entity: Supplier, Customer
 APIs: CRUD suppliers, customers



APIs cần có:
POST   /api/warehouse/products
GET    /api/warehouse/products
GET    /api/warehouse/products/{id}
PUT    /api/warehouse/products/{id}
DELETE /api/warehouse/products/{id}

POST   /api/warehouse/locations
GET    /api/warehouse/locations
...tương tự cho warehouses, suppliers, customers
Frontend Dev (Tuần 3-4):

 Products Management UI

 Product List page (table, pagination, search)
 Product Create/Edit form
 Product Detail page
 Category management


 Warehouse & Locations UI

 Warehouse list page
 Location management (tree view)
 Location assignment UI


 Master Data UI

 Supplier management
 Customer management



Deliverable: Master data hoàn chỉnh (Products, Warehouses, Locations)

📋 PHASE 2: MODULE INVENTORY (RFID/Barcode) (Tuần 5-7)
Backend Dev (Tuần 5-6):

 Stock Count Sessions

 Aggregate: StockCountSession
 Entity: StockCountDetail
 Value Objects: SessionNumber, CountType, VarianceInfo
 Domain Service: VarianceCalculator
 Repository: StockCountRepository
 Application Service: StockCountService
 Controller: StockCountController
 APIs: Create session, start, complete


 Scanning

 Entity: ScanLog
 WebSocket handler: ScanWebSocketHandler
 Service: ScanningService
 Real-time scan processing


 Inventory Adjustments

 Aggregate: InventoryAdjustment
 Service: InventoryAdjustmentService
 APIs: Create, approve adjustments



APIs cần có:
POST   /api/inventory/stock-count/sessions
GET    /api/inventory/stock-count/sessions
POST   /api/inventory/stock-count/sessions/{id}/start
WS     /ws/inventory/scan
POST   /api/inventory/adjustments
POST   /api/inventory/adjustments/{id}/approve
Frontend Dev (Tuần 5-7):

 Stock Count Management

 Stock count session list
 Create session form
 Session detail page
 Start/Complete session actions


 Scanning Interface

 Barcode scanner component (webcam integration)
 WebSocket integration
 Real-time scan feedback
 Scan history table


 Discrepancy Management

 Discrepancy list with filters
 Adjustment creation form
 Approval workflow UI


 Dashboard

 Inventory summary cards
 Recent scans widget
 Accuracy metrics



Deliverable: Stock count system hoàn chỉnh với barcode scanning

🏷️ PHASE 3: MODULE FEFO (Tuần 8-10)
Backend Dev (Tuần 8-9):

 Batch Management

 Aggregate: Batch
 Value Objects: BatchNumber, ExpiryDate, DaysToExpiry, AlertLevel
 Entity: BatchLocation
 Repository: BatchRepository
 Service: BatchManagementService
 APIs: CRUD batches, assign to locations


 FEFO Picking

 Aggregate: PickingList
 Domain Service: FefoAlgorithm (CORE LOGIC)
 Service: FefoPickingService
 APIs: Generate picking list, confirm picking


 Expiry Alerts

 Entity: ExpiryAlert
 Service: ExpiryAlertService
 Scheduler: Daily expiry check
 APIs: List alerts, acknowledge



APIs cần có:
POST   /api/fefo/batches
GET    /api/fefo/batches
POST   /api/fefo/batches/{id}/locations
POST   /api/fefo/picking/generate
POST   /api/fefo/picking/{id}/confirm
GET    /api/fefo/expiry-alerts
POST   /api/fefo/expiry-alerts/{id}/acknowledge
GET    /api/fefo/reports/expiring-soon
Frontend Dev (Tuần 8-10):

 Batch Management

 Batch list with expiry warnings
 Create batch form
 Batch detail with locations
 Assign to location UI


 FEFO Picking

 Generate picking list page
 Picking list detail (prioritized by expiry)
 Confirm picking UI
 Visual indicators for expiry levels


 Expiry Management

 Expiry alerts dashboard
 Alert list with filters
 Acknowledge alert modal
 Expiring soon report


 Analytics

 Expiry timeline chart
 Batch movement report



Deliverable: FEFO system với auto-picking theo expiry date

🚚 PHASE 4: MODULE CROSS-DOCKING (Tuần 11-13)
Backend Dev (Tuần 11-12):

 Inbound Shipments

 Aggregate: InboundShipment
 Entity: InboundItem
 Service: InboundService
 APIs: Create, arrive, receive


 Outbound Orders

 Aggregate: OutboundOrder
 Entity: OutboundItem
 Service: OutboundService
 APIs: Create, confirm orders


 Cross-dock Matching

 Aggregate: CrossDockMapping
 Domain Service: AutoMatchingService (CORE LOGIC)
 Service: CrossDockMatchingService
 APIs: Auto-match, manual match, staging, shipping



APIs cần có:
POST   /api/crossdock/inbound
POST   /api/crossdock/inbound/{id}/arrive
POST   /api/crossdock/inbound/{id}/receive
POST   /api/crossdock/outbound
POST   /api/crossdock/matching/auto
POST   /api/crossdock/matching/manual
GET    /api/crossdock/mappings
POST   /api/crossdock/mappings/{id}/move-to-staging
POST   /api/crossdock/mappings/{id}/shipped
GET    /api/crossdock/reports/performance
Frontend Dev (Tuần 11-13):

 Inbound Management

 Inbound shipment list
 Create inbound form
 Arrive/Receive workflow UI
 Receiving checklist


 Outbound Management

 Outbound order list
 Create order form
 Order detail with status flow


 Cross-dock Matching

 Auto-match trigger UI
 Manual match interface
 Mapping list with status
 Staging area visualization
 Ship confirmation


 Cross-dock Dashboard

 Pending matches
 In-staging items
 Performance metrics
 Timeline chart



Deliverable: Cross-docking system với auto-matching

📦 PHASE 5: MODULE PACKAGING (Tuần 14-15)
Backend Dev (Tuần 14):

 Packaging Materials

 Aggregate: PackagingMaterial
 Service: PackagingMaterialService
 APIs: CRUD materials, stock updates


 Packaging Templates

 Aggregate: PackagingTemplate
 Entity: TemplateItem
 Service: PackagingTemplateService
 APIs: CRUD templates


 Packaging Calculation

 Domain Service: TemplateMatchingService
 Domain Service: MaterialCalculator, CostCalculator
 Service: PackagingCalculationService
 APIs: Calculate, confirm usage



APIs cần có:
POST   /api/packaging/materials
GET    /api/packaging/materials
PUT    /api/packaging/materials/{id}/stock
GET    /api/packaging/materials/low-stock
POST   /api/packaging/templates
GET    /api/packaging/templates
POST   /api/packaging/calculate
POST   /api/packaging/usage
GET    /api/packaging/reports/costs
Frontend Dev (Tuần 14-15):

 Material Management

 Material list with stock levels
 Create/Edit material form
 Stock update UI
 Low stock alerts


 Template Management

 Template list
 Template builder UI
 Material selection for template


 Packaging Calculator

 Calculate materials for order
 Material usage confirmation
 Cost breakdown display


 Reports

 Packaging cost report
 Material consumption chart



Deliverable: Packaging system với auto-calculation

🎯 PHASE 6: INTEGRATION & TESTING (Tuần 16)
Backend Dev:

 Integration testing
 API documentation review
 Performance optimization
 Bug fixes

Frontend Dev:

 End-to-end testing
 UI/UX polish
 Responsive design fixes
 Bug fixes

Cả 2:

 UAT (User Acceptance Testing)
 Documentation
 Deployment preparation

Deliverable: Hệ thống hoàn chỉnh, tested, ready to demo

GANTT CHART OVERVIEW
Week  1-2:  [SETUP - Cả 2]
Week  3-4:  [Warehouse Core]  BE ████ → FE ████
Week  5-7:  [Inventory]       BE ██████ → FE ██████
Week  8-10: [FEFO]            BE ██████ → FE ██████
Week 11-13: [Cross-dock]      BE ██████ → FE ██████
Week 14-15: [Packaging]       BE ████ → FE ████
Week 16:    [Integration & Testing - Cả 2]

TASK TRACKING
Backend Developer Checklist:
□ Phase 0: Setup (2 weeks)
□ Phase 1: Warehouse Core APIs (2 weeks)
□ Phase 2: Inventory APIs (2 weeks)
□ Phase 3: FEFO APIs (2 weeks)
□ Phase 4: Cross-dock APIs (2 weeks)
□ Phase 5: Packaging APIs (1 week)
□ Phase 6: Integration (1 week)
Frontend Developer Checklist:
□ Phase 0: Setup (2 weeks)
□ Phase 1: Warehouse Core UI (2 weeks)
□ Phase 2: Inventory UI (3 weeks)
□ Phase 3: FEFO UI (3 weeks)
□ Phase 4: Cross-dock UI (3 weeks)
□ Phase 5: Packaging UI (2 weeks)
□ Phase 6: Testing & Polish (1 week)

DEPENDENCIES
Warehouse Core
    ↓
Inventory (cần Products, Locations)
    ↓
FEFO (cần Inventory data)
    ↓
Cross-dock (cần FEFO logic)
    ↓
Packaging (cần Outbound Orders)