//------------ BACK END -----------------------------
com.wms
├── WmsApplication.java

├── config
│   ├── security
│   ├── swagger
│   ├── jpa
│   ├── websocket
│   └── async

├── shared
│   ├── exception
│   ├── response
│   ├── util
│   ├── constant
│   ├── base (BaseEntity, Auditable, BaseRepository…)
│   └── event

├── auth
│   ├── controller
│   ├── service
│   ├── entity (User, Role, Permission)
│   ├── repository
│   └── dto

├── masterdata                # CORE – dùng chung toàn hệ thống
│   ├── product               # products, categories
│   ├── warehouse             # warehouses, zones, locations
│   ├── partner               # suppliers, customers
│   └── inventory             # inventory_summary
│
│   └── (mỗi nhánh đều có controller/service/entity/repository/dto)

├── fefo                      # MODULE 1
│   ├── controller
│   ├── service
│   │   ├── BatchService
│   │   ├── FefoPickingService
│   │   └── ExpiryAlertService
│   ├── entity
│   │   ├── Batch
│   │   ├── BatchLocation
│   │   └── ExpiryAlert
│   ├── repository
│   ├── dto
│   └── scheduler             # job check hạn dùng

├── crossdock                 # MODULE 2
│   ├── controller
│   ├── service
│   │   ├── InboundService
│   │   ├── OutboundService
│   │   └── CrossDockMatchingService
│   ├── entity
│   │   ├── InboundShipment
│   │   ├── InboundShipmentItem
│   │   ├── OutboundOrder
│   │   ├── OutboundOrderItem
│   │   └── CrossDockMapping
│   ├── repository
│   ├── dto
│   └── event

├── stockcount                # MODULE 3 – RFID/BARCODE
│   ├── controller
│   ├── websocket             # realtime scan
│   ├── service
│   │   ├── StockCountSessionService
│   │   ├── ScanningService
│   │   └── InventoryAdjustmentService
│   ├── entity
│   │   ├── StockCountSession
│   │   ├── StockCountDetail
│   │   ├── ScanLog
│   │   └── InventoryAdjustment
│   ├── repository
│   ├── dto
│   └── device                # RFID, Barcode client

├── packaging                 # MODULE 4
│   ├── controller
│   ├── service
│   │   ├── PackagingMaterialService
│   │   ├── PackagingTemplateService
│   │   └── PackagingUsageService
│   ├── entity
│   │   ├── PackagingMaterial
│   │   ├── PackagingTemplate
│   │   ├── PackagingTemplateItem
│   │   ├── PackagingUsage
│   │   └── PackagingMaterialTransaction
│   ├── repository
│   ├── dto
│   └── scheduler

├── audit
│   ├── entity
│   ├── repository
│   └── service

└── integration
    ├── erp
    ├── rfid
    └── notification


//--------------- FRONT END ----------------------
src
├── app
│   ├── App.tsx
│   ├── router.tsx
│   └── store.ts

├── shared
│   ├── components (Table, Modal, Form, ScannerInput…)
│   ├── hooks
│   ├── utils
│   ├── constants
│   └── types

├── layout
│   ├── MainLayout.tsx
│   ├── Sidebar.tsx
│   ├── Header.tsx
│   └── ProtectedRoute.tsx

├── auth
│   ├── pages (Login, Profile, UserManagement)
│   ├── api
│   └── store

├── masterdata
│   ├── product
│   ├── warehouse
│   ├── partner
│   └── inventory

├── fefo
│   ├── pages
│   │   ├── BatchList
│   │   ├── BatchDetail
│   │   ├── FefoPicking
│   │   └── ExpiryAlertDashboard
│   ├── components
│   ├── api
│   └── store

├── crossdock
│   ├── pages
│   │   ├── Inbound
│   │   ├── Outbound
│   │   └── CrossDockBoard
│   ├── components
│   ├── api
│   └── store

├── stockcount
│   ├── pages
│   │   ├── SessionManagement
│   │   ├── LiveScan
│   │   └── AdjustmentApproval
│   ├── components
│   ├── api
│   ├── store
│   └── websocket

├── packaging
│   ├── pages
│   │   ├── MaterialManagement
│   │   ├── TemplateManagement
│   │   └── PackagingExecution
│   ├── components
│   ├── api
│   └── store

└── audit
    ├── pages
    └── api
