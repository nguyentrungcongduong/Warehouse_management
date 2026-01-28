-- ============================================
-- QUẢN LÝ SẢN PHẨM
-- ============================================
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    parent_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_parent_category FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    product_code VARCHAR(50) UNIQUE NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    category_id BIGINT,
    description TEXT,
    unit_of_measure VARCHAR(20),
    weight DECIMAL(10,3),
    length DECIMAL(10,2),
    width DECIMAL(10,2),
    height DECIMAL(10,2),
    volume DECIMAL(10,3),
    requires_expiry_tracking BOOLEAN DEFAULT FALSE,
    is_temperature_sensitive BOOLEAN DEFAULT FALSE,
    storage_temperature_min DECIMAL(5,2),
    storage_temperature_max DECIMAL(5,2),
    barcode VARCHAR(100) UNIQUE,
    rfid_tag VARCHAR(100) UNIQUE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- ============================================
-- QUẢN LÝ KHO & VỊ TRÍ
-- ============================================
CREATE TABLE warehouses (
    id BIGSERIAL PRIMARY KEY,
    warehouse_code VARCHAR(50) UNIQUE NOT NULL,
    warehouse_name VARCHAR(200) NOT NULL,
    address TEXT,
    city VARCHAR(100),
    country VARCHAR(100),
    postal_code VARCHAR(20),
    phone VARCHAR(20),
    manager_name VARCHAR(100),
    warehouse_type VARCHAR(30),
    total_capacity DECIMAL(12,2),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE warehouse_zones (
    id BIGSERIAL PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    zone_code VARCHAR(50) NOT NULL,
    zone_name VARCHAR(100),
    zone_type VARCHAR(30),
    temperature_controlled BOOLEAN DEFAULT FALSE,
    temperature_min DECIMAL(5,2),
    temperature_max DECIMAL(5,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_zone_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT uk_zone UNIQUE (warehouse_id, zone_code)
);

CREATE TABLE storage_locations (
    id BIGSERIAL PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    zone_id BIGINT NOT NULL,
    location_code VARCHAR(50) NOT NULL,
    location_type VARCHAR(30),
    aisle VARCHAR(10),
    rack VARCHAR(10),
    shelf VARCHAR(10),
    bin VARCHAR(10),
    max_capacity DECIMAL(10,2),
    current_capacity DECIMAL(10,2) DEFAULT 0,
    is_available BOOLEAN DEFAULT TRUE,
    barcode VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_loc_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_loc_zone FOREIGN KEY (zone_id) REFERENCES warehouse_zones(id),
    CONSTRAINT uk_location UNIQUE (warehouse_id, location_code)
);

-- ============================================
-- MODULE 1: FEFO - QUẢN LÝ LÔ HÀNG
-- ============================================
CREATE TABLE batches (
    id BIGSERIAL PRIMARY KEY,
    batch_number VARCHAR(50) NOT NULL,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    manufacture_date DATE,
    expiry_date DATE,
    received_date DATE NOT NULL,
    supplier_id BIGINT,
    initial_quantity DECIMAL(12,3) NOT NULL,
    current_quantity DECIMAL(12,3) NOT NULL,
    reserved_quantity DECIMAL(12,3) DEFAULT 0,
    available_quantity DECIMAL(12,3) GENERATED ALWAYS AS (current_quantity - reserved_quantity) STORED,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    quality_status VARCHAR(30),
    -- Postgres dùng phép trừ DATE thay vì DATEDIFF
    days_to_expiry INT GENERATED ALWAYS AS (expiry_date - CURRENT_DATE) STORED,
    expiry_alert_level VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_batch_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_batch_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT uk_batch UNIQUE (batch_number, product_id)
);

CREATE TABLE batch_locations (
    id BIGSERIAL PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    quantity DECIMAL(12,3) NOT NULL,
    assigned_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_bl_batch FOREIGN KEY (batch_id) REFERENCES batches(id),
    CONSTRAINT fk_bl_location FOREIGN KEY (location_id) REFERENCES storage_locations(id),
    CONSTRAINT uk_batch_location UNIQUE (batch_id, location_id)
);

CREATE TABLE expiry_alerts (
    id BIGSERIAL PRIMARY KEY,
    batch_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    alert_level VARCHAR(20),
    days_remaining INT,
    quantity_affected DECIMAL(12,3),
    alert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_acknowledged BOOLEAN DEFAULT FALSE,
    acknowledged_by VARCHAR(100),
    acknowledged_at TIMESTAMP,
    action_taken TEXT,
    CONSTRAINT fk_alert_batch FOREIGN KEY (batch_id) REFERENCES batches(id),
    CONSTRAINT fk_alert_product FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ============================================
-- MODULE 2: CROSS-DOCKING
-- ============================================
CREATE TABLE suppliers (
    id BIGSERIAL PRIMARY KEY,
    supplier_code VARCHAR(50) UNIQUE NOT NULL,
    supplier_name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    address TEXT,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    customer_code VARCHAR(50) UNIQUE NOT NULL,
    customer_name VARCHAR(200) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20),
    email VARCHAR(100),
    shipping_address TEXT,
    billing_address TEXT,
    customer_type VARCHAR(30),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE inbound_shipments (
    id BIGSERIAL PRIMARY KEY,
    shipment_number VARCHAR(50) UNIQUE NOT NULL,
    supplier_id BIGINT,
    warehouse_id BIGINT NOT NULL,
    carrier VARCHAR(100),
    tracking_number VARCHAR(100),
    vehicle_number VARCHAR(50),
    driver_name VARCHAR(100),
    driver_phone VARCHAR(20),
    expected_arrival_date TIMESTAMP,
    actual_arrival_date TIMESTAMP,
    is_cross_dock BOOLEAN DEFAULT FALSE,
    cross_dock_priority INT,
    status VARCHAR(30) DEFAULT 'PLANNED',
    total_items INT,
    received_items INT DEFAULT 0,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    CONSTRAINT fk_in_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id),
    CONSTRAINT fk_in_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

CREATE TABLE inbound_shipment_items (
    id BIGSERIAL PRIMARY KEY,
    inbound_shipment_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    batch_number VARCHAR(50),
    expected_quantity DECIMAL(12,3) NOT NULL,
    received_quantity DECIMAL(12,3) DEFAULT 0,
    rejected_quantity DECIMAL(12,3) DEFAULT 0,
    manufacture_date DATE,
    expiry_date DATE,
    status VARCHAR(30) DEFAULT 'PENDING',
    quality_check_status VARCHAR(30),
    cross_dock_assigned BOOLEAN DEFAULT FALSE,
    notes TEXT,
    CONSTRAINT fk_isi_shipment FOREIGN KEY (inbound_shipment_id) REFERENCES inbound_shipments(id),
    CONSTRAINT fk_isi_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE outbound_orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(50) UNIQUE NOT NULL,
    customer_id BIGINT,
    warehouse_id BIGINT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    required_date TIMESTAMP,
    promise_date TIMESTAMP,
    is_cross_dock_eligible BOOLEAN DEFAULT FALSE,
    cross_dock_matched BOOLEAN DEFAULT FALSE,
    status VARCHAR(30) DEFAULT 'PENDING',
    priority VARCHAR(20) DEFAULT 'NORMAL',
    shipping_address TEXT,
    shipping_method VARCHAR(50),
    carrier VARCHAR(100),
    tracking_number VARCHAR(100),
    total_items INT,
    picked_items INT DEFAULT 0,
    total_weight DECIMAL(10,3),
    total_volume DECIMAL(10,3),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    CONSTRAINT fk_out_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT fk_out_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

CREATE TABLE outbound_order_items (
    id BIGSERIAL PRIMARY KEY,
    outbound_order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    ordered_quantity DECIMAL(12,3) NOT NULL,
    picked_quantity DECIMAL(12,3) DEFAULT 0,
    packed_quantity DECIMAL(12,3) DEFAULT 0,
    status VARCHAR(30) DEFAULT 'PENDING',
    notes TEXT,
    CONSTRAINT fk_ooi_order FOREIGN KEY (outbound_order_id) REFERENCES outbound_orders(id),
    CONSTRAINT fk_ooi_product FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE cross_dock_mappings (
    id BIGSERIAL PRIMARY KEY,
    inbound_shipment_id BIGINT NOT NULL,
    inbound_item_id BIGINT NOT NULL,
    outbound_order_id BIGINT NOT NULL,
    outbound_item_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    matched_quantity DECIMAL(12,3) NOT NULL,
    matched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transfer_started_at TIMESTAMP,
    transfer_completed_at TIMESTAMP,
    staging_location_id BIGINT,
    status VARCHAR(30) DEFAULT 'MATCHED',
    match_type VARCHAR(30),
    matched_by VARCHAR(100),
    notes TEXT,
    CONSTRAINT fk_cdm_in_shipment FOREIGN KEY (inbound_shipment_id) REFERENCES inbound_shipments(id),
    CONSTRAINT fk_cdm_in_item FOREIGN KEY (inbound_item_id) REFERENCES inbound_shipment_items(id),
    CONSTRAINT fk_cdm_out_order FOREIGN KEY (outbound_order_id) REFERENCES outbound_orders(id),
    CONSTRAINT fk_cdm_out_item FOREIGN KEY (outbound_item_id) REFERENCES outbound_order_items(id),
    CONSTRAINT fk_cdm_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_cdm_staging FOREIGN KEY (staging_location_id) REFERENCES storage_locations(id)
);

-- ============================================
-- MODULE 3: RFID/BARCODE - KIỂM KÊ
-- ============================================
CREATE TABLE stock_count_sessions (
    id BIGSERIAL PRIMARY KEY,
    session_number VARCHAR(50) UNIQUE NOT NULL,
    warehouse_id BIGINT NOT NULL,
    zone_id BIGINT,
    count_type VARCHAR(30),
    scheduled_date DATE,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    assigned_to VARCHAR(100),
    approved_by VARCHAR(100),
    status VARCHAR(30) DEFAULT 'PLANNED',
    total_locations INT,
    counted_locations INT DEFAULT 0,
    total_items INT,
    counted_items INT DEFAULT 0,
    discrepancy_count INT DEFAULT 0,
    accuracy_rate DECIMAL(5,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scs_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_scs_zone FOREIGN KEY (zone_id) REFERENCES warehouse_zones(id)
);

CREATE TABLE stock_count_details (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT NOT NULL,
    location_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    batch_id BIGINT,
    system_quantity DECIMAL(12,3) NOT NULL,
    counted_quantity DECIMAL(12,3),
    variance_quantity DECIMAL(12,3) GENERATED ALWAYS AS (counted_quantity - system_quantity) STORED,
    variance_percentage DECIMAL(5,2),
    count_method VARCHAR(20),
    scanned_at TIMESTAMP,
    scanned_by VARCHAR(100),
    device_id VARCHAR(100),
    status VARCHAR(30) DEFAULT 'PENDING',
    is_discrepancy BOOLEAN GENERATED ALWAYS AS (ABS(counted_quantity - system_quantity) > 0.001) STORED,
    discrepancy_reason VARCHAR(200),
    adjustment_approved BOOLEAN DEFAULT FALSE,
    adjustment_approved_by VARCHAR(100),
    adjustment_approved_at TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_scd_session FOREIGN KEY (session_id) REFERENCES stock_count_sessions(id),
    CONSTRAINT fk_scd_location FOREIGN KEY (location_id) REFERENCES storage_locations(id),
    CONSTRAINT fk_scd_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_scd_batch FOREIGN KEY (batch_id) REFERENCES batches(id)
);

CREATE TABLE scan_logs (
    id BIGSERIAL PRIMARY KEY,
    session_id BIGINT,
    scan_type VARCHAR(20),
    scanned_code VARCHAR(100) NOT NULL,
    scan_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    scanned_by VARCHAR(100),
    device_id VARCHAR(100),
    location_id BIGINT,
    product_id BIGINT,
    batch_id BIGINT,
    quantity DECIMAL(12,3),
    scan_purpose VARCHAR(30),
    status VARCHAR(20) DEFAULT 'SUCCESS',
    error_message TEXT,
    CONSTRAINT fk_sl_session FOREIGN KEY (session_id) REFERENCES stock_count_sessions(id),
    CONSTRAINT fk_sl_location FOREIGN KEY (location_id) REFERENCES storage_locations(id),
    CONSTRAINT fk_sl_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_sl_batch FOREIGN KEY (batch_id) REFERENCES batches(id)
);

CREATE TABLE inventory_adjustments (
    id BIGSERIAL PRIMARY KEY,
    adjustment_number VARCHAR(50) UNIQUE NOT NULL,
    stock_count_detail_id BIGINT,
    warehouse_id BIGINT NOT NULL,
    location_id BIGINT,
    product_id BIGINT NOT NULL,
    batch_id BIGINT,
    adjustment_type VARCHAR(30),
    quantity_before DECIMAL(12,3) NOT NULL,
    adjustment_quantity DECIMAL(12,3) NOT NULL,
    quantity_after DECIMAL(12,3) NOT NULL,
    reason VARCHAR(200),
    supporting_documents TEXT,
    status VARCHAR(30) DEFAULT 'PENDING',
    created_by VARCHAR(100),
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_ia_scd FOREIGN KEY (stock_count_detail_id) REFERENCES stock_count_details(id),
    CONSTRAINT fk_ia_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_ia_location FOREIGN KEY (location_id) REFERENCES storage_locations(id),
    CONSTRAINT fk_ia_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_ia_batch FOREIGN KEY (batch_id) REFERENCES batches(id)
);

-- ============================================
-- MODULE 4: QUẢN LÝ VẬT TƯ ĐÓNG GÓI
-- ============================================
CREATE TABLE packaging_materials (
    id BIGSERIAL PRIMARY KEY,
    material_code VARCHAR(50) UNIQUE NOT NULL,
    material_name VARCHAR(200) NOT NULL,
    category VARCHAR(50),
    unit_of_measure VARCHAR(20),
    unit_cost DECIMAL(10,2) NOT NULL,
    supplier_id BIGINT,
    current_stock DECIMAL(12,3) NOT NULL DEFAULT 0,
    reserved_stock DECIMAL(12,3) DEFAULT 0,
    available_stock DECIMAL(12,3) GENERATED ALWAYS AS (current_stock - reserved_stock) STORED,
    min_stock_level DECIMAL(12,3),
    max_stock_level DECIMAL(12,3),
    reorder_point DECIMAL(12,3),
    reorder_quantity DECIMAL(12,3),
    barcode VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pm_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE packaging_templates (
    id BIGSERIAL PRIMARY KEY,
    template_name VARCHAR(100) NOT NULL,
    description TEXT,
    product_id BIGINT,
    category_id BIGINT,
    weight_from DECIMAL(10,3),
    weight_to DECIMAL(10,3),
    volume_from DECIMAL(10,3),
    volume_to DECIMAL(10,3),
    priority INT DEFAULT 0,
    is_default BOOLEAN DEFAULT FALSE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pt_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_pt_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE packaging_template_items (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity_per_unit DECIMAL(10,3) NOT NULL,
    is_optional BOOLEAN DEFAULT FALSE,
    notes TEXT,
    CONSTRAINT fk_pti_template FOREIGN KEY (template_id) REFERENCES packaging_templates(id),
    CONSTRAINT fk_pti_material FOREIGN KEY (material_id) REFERENCES packaging_materials(id),
    CONSTRAINT uk_template_material UNIQUE (template_id, material_id)
);

CREATE TABLE packaging_usage (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    template_id BIGINT,
    total_packaging_cost DECIMAL(10,2) DEFAULT 0,
    packaging_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    packed_by VARCHAR(100),
    notes TEXT,
    CONSTRAINT fk_pu_order FOREIGN KEY (order_id) REFERENCES outbound_orders(id),
    CONSTRAINT fk_pu_template FOREIGN KEY (template_id) REFERENCES packaging_templates(id)
);

CREATE TABLE packaging_usage_details (
    id BIGSERIAL PRIMARY KEY,
    packaging_usage_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    quantity_used DECIMAL(12,3) NOT NULL,
    unit_cost DECIMAL(10,2) NOT NULL,
    total_cost DECIMAL(10,2) GENERATED ALWAYS AS (quantity_used * unit_cost) STORED,
    CONSTRAINT fk_pud_usage FOREIGN KEY (packaging_usage_id) REFERENCES packaging_usage(id),
    CONSTRAINT fk_pud_material FOREIGN KEY (material_id) REFERENCES packaging_materials(id)
);

CREATE TABLE packaging_material_transactions (
    id BIGSERIAL PRIMARY KEY,
    material_id BIGINT NOT NULL,
    transaction_type VARCHAR(30),
    reference_type VARCHAR(30),
    reference_id BIGINT,
    quantity DECIMAL(12,3) NOT NULL,
    unit_cost DECIMAL(10,2),
    stock_before DECIMAL(12,3),
    stock_after DECIMAL(12,3),
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    notes TEXT,
    CONSTRAINT fk_pmt_material FOREIGN KEY (material_id) REFERENCES packaging_materials(id)
);

-- ============================================
-- INVENTORY MASTER (Tổng hợp tồn kho)
-- ============================================
CREATE TABLE inventory_summary (
    id BIGSERIAL PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    location_id BIGINT,
    batch_id BIGINT,
    on_hand_quantity DECIMAL(12,3) DEFAULT 0,
    available_quantity DECIMAL(12,3) DEFAULT 0,
    reserved_quantity DECIMAL(12,3) DEFAULT 0,
    in_transit_quantity DECIMAL(12,3) DEFAULT 0,
    average_cost DECIMAL(10,2),
    total_value DECIMAL(15,2),
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_counted_date DATE,
    CONSTRAINT fk_inv_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_inv_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_inv_location FOREIGN KEY (location_id) REFERENCES storage_locations(id),
    CONSTRAINT fk_inv_batch FOREIGN KEY (batch_id) REFERENCES batches(id),
    CONSTRAINT uk_inventory UNIQUE (warehouse_id, product_id, location_id, batch_id)
);

-- ============================================
-- USERS & PERMISSIONS
-- ============================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    full_name VARCHAR(200),
    phone VARCHAR(20),
    warehouse_id BIGINT,
    is_active BOOLEAN DEFAULT TRUE,
    refresh_token TEXT,
    fcm_token TEXT,
    refresh_token_expired_at TIMESTAMP,
    last_login TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id)
);

CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE permissions (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(100) UNIQUE NOT NULL,
    name VARCHAR(150) NOT NULL,
    module VARCHAR(50),
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE role_permissions (
    role_id BIGINT NOT NULL,
    permission_id BIGINT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

-- ============================================
-- AUDIT LOG (Lịch sử thao tác)
-- ============================================
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(50),
    entity_type VARCHAR(50),
    entity_id BIGINT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_user FOREIGN KEY (user_id) REFERENCES users(id)
);