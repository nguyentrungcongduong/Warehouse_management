# WMS Core - Backend & Entity Design (MVP)

Tài liệu này định nghĩa cấu trúc dữ liệu, quan hệ Entity và các nghiệp vụ cốt lõi của hệ thống WMS.

---

## 🏗️ 1. Entity List (Đóng băng MVP)

### 1.1. Core Entities
- **Product**: Định nghĩa hàng hóa.
  - `id (PK)`, `sku (unique)`, `name`, `unit` (box, piece...), `active (boolean)`.
- **Batch (LOT)**: Quản lý lô hàng. (FEFO chạy trên đây).
  - `id (PK)`, `product_id (FK)`, `batch_code`, `expiry_date`, `quantity`, `received_at`.
  - *Quy tắc*: Không cho phép sửa `expiry_date` sau khi tạo.
- **WarehouseLocation**: Vị trí lưu kho.
  - `id (PK)`, `code` (VD: A01-B02), `description`, `active`.
- **Inventory**: Bảng cân đối tồn kho theo vị trí.
  - `id (PK)`, `product_id (FK)`, `location_id (FK)`, `on_hand_qty` (vật lý), `available_qty` (có thể xuất).

### 1.2. Transaction & Audit
- **StockMovement**: Nhật ký kho (Không bao giờ sửa/xóa).
  - `id (PK)`, `product_id (FK)`, `batch_id (FK, nullable)`, `type` (IN/OUT), `quantity`, `reference_type` (RECEIPT/ISSUE/COUNT/CROSS_DOCK), `reference_id`, `created_at`.
- **Order**: Dùng cho Cross-docking.
  - `id (PK)`, `order_code`, `status` (OPEN/COMPLETED).

### 1.3. Inventory Count (Kiểm kê)
- **InventoryCountSession**:
  - `id (PK)`, `started_at`, `finished_at`, `status` (OPEN/CLOSED).
- **InventoryCountItem**:
  - `id (PK)`, `session_id (FK)`, `product_id (FK)`, `system_qty`, `physical_qty`, `variance`.

### 1.4. Operational Materials
- **PackagingMaterial**:
  - `id (PK)`, `name`, `unit`, `quantity`, `cost_per_unit`.

---

## 📊 2. ERD (Text-Based Relationship)

```text
Product (1) ────< Batch (N)
Product (1) ────< Inventory (N)
Product (1) ────< StockMovement (N)
Product (1) ────< InventoryCountItem (N)

Batch (1) ────< StockMovement (N)

InventoryCountSession (1) ────< InventoryCountItem (N)

WarehouseLocation (1) ────< Inventory (N)

Order (1) ────< StockMovement (N) [Cross-dock flow]
```

---

## 🔒 3. Các quy tắc bất biến (Business Rules)

### RULE 1: FEFO (First Expired, First Out)
- Lô hàng hết hạn (**Expired**) tuyệt đối không xuất.
- Sắp xếp **expiry_date ASC**.
- Hệ thống tự động phân bổ qua nhiều Batch nếu một Batch không đủ số lượng.

### RULE 2: Inventory Count Control
- Khi Session **OPEN**: Chỉ cho phép ghi nhận `physical_qty`. `Inventory` chưa thay đổi.
- Khi Session **CLOSE**: Hệ thống mới cập nhật `Inventory` + Tạo `StockMovement(ADJUST)` cho các dòng có sai lệch.

### RULE 3: Audit Integrity
- `StockMovement` chỉ có hành vi **APPEND**.
- Không có chức năng Update hoặc Delete cho các bản ghi StockMovement để đảm bảo tính minh bạch khi thanh tra kho.

---

## 🧭 4. Map Module -> API -> Service (BƯỚC TIẾP THEO)

| Module | API Endpoint | Service Responsibility | Key Flow |
|--------|--------------|------------------------|----------|
| **Issue (FEFO)** | `POST /issue/suggest` | `IssueService.calculateFEFO` | Controller -> DB (Fetch Batches) -> Sort & Allocate -> Result |
| **Receipt** | `POST /receipt` | `ReceiptService.receive` | Create Batch -> Create StockMovement -> Update Inventory |
| **Count** | `PATCH /count/{id}/close` | `CountService.finalize` | Compare Qty -> Create Movements -> Update Inventory -> Close Session |
| **Cross-dock** | `POST /cross-dock` | `CrossDockService.process` | Create Order -> Receipt(IN) -> Issue(OUT) -> Bypass Rack |
