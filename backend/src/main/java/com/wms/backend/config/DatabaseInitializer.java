package com.wms.backend.config;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.wms.backend.auth.entity.Permission;
import com.wms.backend.auth.entity.Role;
import com.wms.backend.auth.entity.User;
import com.wms.backend.auth.repository.PermissionRepository;
import com.wms.backend.auth.repository.RoleRepository;
import com.wms.backend.auth.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

        private final PermissionRepository permissionRepository;
        private final RoleRepository roleRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                log.info(">>> START INITIALIZING DATABASE");

                // 1. Init Permissions
                if (permissionRepository.count() == 0) {
                        initPermissions();
                }

                // 2. Init Roles
                if (roleRepository.count() == 0) {
                        initRoles();
                }

                // 3. Init Admin User
                if (userRepository.count() == 0) {
                        initAdminUser();
                }

                log.info(">>> END INITIALIZING DATABASE");
        }

        /**
         * INIT PERMISSIONS
         */
        private void initPermissions() {
                log.info("Initializing Permissions...");

                List<Permission> permissions = new ArrayList<>();

                // ==================== INVENTORY MODULE ====================
                // Stock Count
                permissions.add(
                                createPermission("STOCK_COUNT_CREATE", "Tạo phiên kiểm kê", "INVENTORY",
                                                "Tạo phiên stock count mới"));
                permissions.add(createPermission("STOCK_COUNT_READ", "Xem phiên kiểm kê", "INVENTORY",
                                "Xem danh sách và chi tiết stock count"));
                permissions.add(createPermission("STOCK_COUNT_START", "Bắt đầu kiểm kê", "INVENTORY",
                                "Bắt đầu phiên kiểm kê"));
                permissions.add(createPermission("STOCK_COUNT_COMPLETE", "Hoàn thành kiểm kê", "INVENTORY",
                                "Hoàn thành phiên kiểm kê"));
                permissions.add(createPermission("STOCK_COUNT_DELETE", "Xóa phiên kiểm kê", "INVENTORY",
                                "Xóa phiên kiểm kê"));

                // Scanning
                permissions
                                .add(createPermission("SCAN_BARCODE", "Quét barcode/RFID", "INVENTORY",
                                                "Quét mã sản phẩm và vị trí"));
                permissions.add(createPermission("SCAN_LOG_READ", "Xem lịch sử quét", "INVENTORY",
                                "Xem lịch sử quét mã"));

                // Inventory Adjustment
                permissions.add(
                                createPermission("ADJUSTMENT_CREATE", "Tạo điều chỉnh tồn kho", "INVENTORY",
                                                "Tạo phiếu điều chỉnh"));
                permissions.add(createPermission("ADJUSTMENT_READ", "Xem điều chỉnh", "INVENTORY",
                                "Xem danh sách điều chỉnh"));
                permissions.add(createPermission("ADJUSTMENT_APPROVE", "Phê duyệt điều chỉnh", "INVENTORY",
                                "Phê duyệt phiếu điều chỉnh"));
                permissions.add(
                                createPermission("ADJUSTMENT_REJECT", "Từ chối điều chỉnh", "INVENTORY",
                                                "Từ chối phiếu điều chỉnh"));

                // Discrepancy
                permissions.add(createPermission("DISCREPANCY_READ", "Xem chênh lệch", "INVENTORY",
                                "Xem danh sách chênh lệch tồn kho"));

                // ==================== FEFO MODULE ====================
                // Batch Management
                permissions.add(createPermission("BATCH_CREATE", "Tạo lô hàng", "FEFO", "Tạo batch mới"));
                permissions.add(createPermission("BATCH_READ", "Xem lô hàng", "FEFO",
                                "Xem danh sách và chi tiết batch"));
                permissions.add(createPermission("BATCH_UPDATE", "Cập nhật lô hàng", "FEFO",
                                "Cập nhật thông tin batch"));
                permissions.add(createPermission("BATCH_DELETE", "Xóa lô hàng", "FEFO", "Xóa batch"));
                permissions.add(
                                createPermission("BATCH_ASSIGN_LOCATION", "Gán vị trí lô hàng", "FEFO",
                                                "Assign batch vào location"));

                // FEFO Picking
                permissions.add(
                                createPermission("PICKING_GENERATE", "Tạo picking list", "FEFO",
                                                "Tạo danh sách picking theo FEFO"));
                permissions.add(createPermission("PICKING_READ", "Xem picking list", "FEFO", "Xem danh sách picking"));
                permissions.add(createPermission("PICKING_EXECUTE", "Thực hiện picking", "FEFO", "Thực hiện lấy hàng"));
                permissions.add(createPermission("PICKING_CONFIRM", "Xác nhận picking", "FEFO", "Xác nhận đã picking"));
                permissions.add(createPermission("PICKING_DELETE", "Xóa picking list", "FEFO", "Xóa picking list"));

                // Expiry Alerts
                permissions.add(createPermission("EXPIRY_ALERT_READ", "Xem cảnh báo HSD", "FEFO",
                                "Xem cảnh báo hết hạn"));
                permissions.add(createPermission("EXPIRY_ALERT_ACKNOWLEDGE", "Xác nhận cảnh báo HSD", "FEFO",
                                "Acknowledge expiry alert"));

                // Reports
                permissions.add(
                                createPermission("BATCH_REPORT_READ", "Xem báo cáo lô hàng", "FEFO",
                                                "Xem báo cáo batch và expiry"));

                // ==================== CROSS-DOCK MODULE ====================
                // Inbound Shipment
                permissions.add(createPermission("INBOUND_CREATE", "Tạo đơn nhập", "CROSSDOCK",
                                "Tạo inbound shipment"));
                permissions.add(createPermission("INBOUND_READ", "Xem đơn nhập", "CROSSDOCK", "Xem danh sách inbound"));
                permissions.add(createPermission("INBOUND_UPDATE", "Cập nhật đơn nhập", "CROSSDOCK",
                                "Cập nhật inbound"));
                permissions.add(createPermission("INBOUND_DELETE", "Xóa đơn nhập", "CROSSDOCK", "Xóa inbound"));
                permissions
                                .add(createPermission("INBOUND_ARRIVE", "Đánh dấu hàng đến", "CROSSDOCK",
                                                "Đánh dấu inbound arrived"));
                permissions.add(createPermission("INBOUND_RECEIVE", "Nhận hàng", "CROSSDOCK", "Receive inbound items"));

                // Outbound Order
                permissions.add(createPermission("OUTBOUND_CREATE", "Tạo đơn xuất", "CROSSDOCK", "Tạo outbound order"));
                permissions.add(createPermission("OUTBOUND_READ", "Xem đơn xuất", "CROSSDOCK",
                                "Xem danh sách outbound"));
                permissions.add(createPermission("OUTBOUND_UPDATE", "Cập nhật đơn xuất", "CROSSDOCK",
                                "Cập nhật outbound"));
                permissions.add(createPermission("OUTBOUND_DELETE", "Xóa đơn xuất", "CROSSDOCK", "Xóa outbound"));
                permissions
                                .add(createPermission("OUTBOUND_CONFIRM", "Xác nhận đơn xuất", "CROSSDOCK",
                                                "Confirm outbound order"));

                // Cross-dock Matching
                permissions.add(createPermission("CROSSDOCK_MATCH_AUTO", "Tự động ghép đơn", "CROSSDOCK",
                                "Auto-match inbound/outbound"));
                permissions.add(createPermission("CROSSDOCK_MATCH_MANUAL", "Ghép đơn thủ công", "CROSSDOCK",
                                "Manual match"));
                permissions
                                .add(createPermission("CROSSDOCK_MAPPING_READ", "Xem mapping", "CROSSDOCK",
                                                "Xem cross-dock mappings"));
                permissions
                                .add(createPermission("CROSSDOCK_MOVE_STAGING", "Chuyển staging", "CROSSDOCK",
                                                "Move to staging area"));
                permissions.add(createPermission("CROSSDOCK_READY_SHIP", "Sẵn sàng xuất", "CROSSDOCK",
                                "Mark ready to ship"));
                permissions.add(createPermission("CROSSDOCK_SHIPPED", "Xác nhận đã xuất", "CROSSDOCK",
                                "Confirm shipped"));

                // Reports
                permissions.add(createPermission("CROSSDOCK_REPORT_READ", "Xem báo cáo cross-dock", "CROSSDOCK",
                                "Xem báo cáo hiệu suất"));

                // ==================== PACKAGING MODULE ====================
                // Packaging Materials
                permissions.add(createPermission("MATERIAL_CREATE", "Tạo vật tư", "PACKAGING",
                                "Tạo packaging material"));
                permissions.add(createPermission("MATERIAL_READ", "Xem vật tư", "PACKAGING",
                                "Xem danh sách materials"));
                permissions.add(createPermission("MATERIAL_UPDATE", "Cập nhật vật tư", "PACKAGING",
                                "Cập nhật material"));
                permissions.add(createPermission("MATERIAL_DELETE", "Xóa vật tư", "PACKAGING", "Xóa material"));
                permissions.add(createPermission("MATERIAL_UPDATE_STOCK", "Cập nhật tồn kho vật tư", "PACKAGING",
                                "Update material stock"));
                permissions.add(createPermission("MATERIAL_LOW_STOCK_READ", "Xem vật tư sắp hết", "PACKAGING",
                                "Xem materials low stock"));

                // Packaging Templates
                permissions.add(createPermission("TEMPLATE_CREATE", "Tạo template", "PACKAGING",
                                "Tạo packaging template"));
                permissions.add(createPermission("TEMPLATE_READ", "Xem template", "PACKAGING",
                                "Xem packaging templates"));
                permissions.add(createPermission("TEMPLATE_UPDATE", "Cập nhật template", "PACKAGING",
                                "Cập nhật template"));
                permissions.add(createPermission("TEMPLATE_DELETE", "Xóa template", "PACKAGING", "Xóa template"));

                // Packaging Usage
                permissions.add(
                                createPermission("PACKAGING_CALCULATE", "Tính toán vật tư", "PACKAGING",
                                                "Calculate materials needed"));
                permissions.add(createPermission("PACKAGING_USE", "Sử dụng vật tư", "PACKAGING",
                                "Confirm packaging usage"));
                permissions.add(createPermission("PACKAGING_HISTORY_READ", "Xem lịch sử đóng gói", "PACKAGING",
                                "Xem packaging usage history"));

                // Reports
                permissions.add(createPermission("PACKAGING_REPORT_READ", "Xem báo cáo đóng gói", "PACKAGING",
                                "Xem báo cáo chi phí và tiêu thụ"));

                // ==================== WAREHOUSE CORE MODULE ====================
                // Warehouse Management
                permissions.add(createPermission("WAREHOUSE_CREATE", "Tạo kho", "WAREHOUSE", "Tạo warehouse"));
                permissions.add(createPermission("WAREHOUSE_READ", "Xem kho", "WAREHOUSE", "Xem danh sách warehouses"));
                permissions.add(createPermission("WAREHOUSE_UPDATE", "Cập nhật kho", "WAREHOUSE",
                                "Cập nhật warehouse"));
                permissions.add(createPermission("WAREHOUSE_DELETE", "Xóa kho", "WAREHOUSE", "Xóa warehouse"));

                // Zone Management
                permissions.add(createPermission("ZONE_CREATE", "Tạo zone", "WAREHOUSE", "Tạo warehouse zone"));
                permissions.add(createPermission("ZONE_READ", "Xem zone", "WAREHOUSE", "Xem zones"));
                permissions.add(createPermission("ZONE_UPDATE", "Cập nhật zone", "WAREHOUSE", "Cập nhật zone"));
                permissions.add(createPermission("ZONE_DELETE", "Xóa zone", "WAREHOUSE", "Xóa zone"));

                // Location Management
                permissions.add(createPermission("LOCATION_CREATE", "Tạo vị trí", "WAREHOUSE", "Tạo storage location"));
                permissions.add(createPermission("LOCATION_READ", "Xem vị trí", "WAREHOUSE", "Xem locations"));
                permissions.add(createPermission("LOCATION_UPDATE", "Cập nhật vị trí", "WAREHOUSE",
                                "Cập nhật location"));
                permissions.add(createPermission("LOCATION_DELETE", "Xóa vị trí", "WAREHOUSE", "Xóa location"));

                // Product Management
                permissions.add(createPermission("PRODUCT_CREATE", "Tạo sản phẩm", "WAREHOUSE", "Tạo product"));
                permissions.add(createPermission("PRODUCT_READ", "Xem sản phẩm", "WAREHOUSE", "Xem products"));
                permissions.add(createPermission("PRODUCT_UPDATE", "Cập nhật sản phẩm", "WAREHOUSE",
                                "Cập nhật product"));
                permissions.add(createPermission("PRODUCT_DELETE", "Xóa sản phẩm", "WAREHOUSE", "Xóa product"));

                // Category Management
                permissions.add(createPermission("CATEGORY_CREATE", "Tạo danh mục", "WAREHOUSE", "Tạo category"));
                permissions.add(createPermission("CATEGORY_READ", "Xem danh mục", "WAREHOUSE", "Xem categories"));
                permissions.add(createPermission("CATEGORY_UPDATE", "Cập nhật danh mục", "WAREHOUSE",
                                "Cập nhật category"));
                permissions.add(createPermission("CATEGORY_DELETE", "Xóa danh mục", "WAREHOUSE", "Xóa category"));

                // Supplier Management
                permissions.add(createPermission("SUPPLIER_CREATE", "Tạo NCC", "WAREHOUSE", "Tạo supplier"));
                permissions.add(createPermission("SUPPLIER_READ", "Xem NCC", "WAREHOUSE", "Xem suppliers"));
                permissions.add(createPermission("SUPPLIER_UPDATE", "Cập nhật NCC", "WAREHOUSE", "Cập nhật supplier"));
                permissions.add(createPermission("SUPPLIER_DELETE", "Xóa NCC", "WAREHOUSE", "Xóa supplier"));

                // Customer Management
                permissions.add(createPermission("CUSTOMER_CREATE", "Tạo khách hàng", "WAREHOUSE", "Tạo customer"));
                permissions.add(createPermission("CUSTOMER_READ", "Xem khách hàng", "WAREHOUSE", "Xem customers"));
                permissions.add(createPermission("CUSTOMER_UPDATE", "Cập nhật khách hàng", "WAREHOUSE",
                                "Cập nhật customer"));
                permissions.add(createPermission("CUSTOMER_DELETE", "Xóa khách hàng", "WAREHOUSE", "Xóa customer"));

                // ==================== SYSTEM MODULE ====================
                // User Management
                permissions.add(createPermission("USER_CREATE", "Tạo người dùng", "SYSTEM", "Tạo user"));
                permissions.add(createPermission("USER_READ", "Xem người dùng", "SYSTEM", "Xem users"));
                permissions.add(createPermission("USER_UPDATE", "Cập nhật người dùng", "SYSTEM", "Cập nhật user"));
                permissions.add(createPermission("USER_DELETE", "Xóa người dùng", "SYSTEM", "Xóa user"));

                // Role Management
                permissions.add(createPermission("ROLE_CREATE", "Tạo vai trò", "SYSTEM", "Tạo role"));
                permissions.add(createPermission("ROLE_READ", "Xem vai trò", "SYSTEM", "Xem roles"));
                permissions.add(createPermission("ROLE_UPDATE", "Cập nhật vai trò", "SYSTEM", "Cập nhật role"));
                permissions.add(createPermission("ROLE_DELETE", "Xóa vai trò", "SYSTEM", "Xóa role"));
                permissions.add(createPermission("ROLE_ASSIGN_PERMISSION", "Gán quyền cho vai trò", "SYSTEM",
                                "Assign permissions to role"));

                // Permission Management
                permissions.add(createPermission("PERMISSION_CREATE", "Tạo quyền", "SYSTEM", "Tạo permission new"));
                permissions.add(createPermission("PERMISSION_READ", "Xem quyền", "SYSTEM", "Xem permissions"));
                permissions.add(createPermission("PERMISSION_UPDATE", "Cập nhật quyền", "SYSTEM",
                                "Cập nhật permission"));
                permissions.add(createPermission("PERMISSION_DELETE", "Xóa quyền", "SYSTEM", "Xóa permission"));

                // Reports
                permissions.add(createPermission("REPORT_READ", "Xem báo cáo", "SYSTEM", "Xem tất cả báo cáo"));
                permissions.add(createPermission("REPORT_EXPORT", "Xuất báo cáo", "SYSTEM", "Export reports"));

                // Audit Logs
                permissions.add(createPermission("AUDIT_LOG_READ", "Xem lịch sử thao tác", "SYSTEM", "Xem audit logs"));

                permissionRepository.saveAll(permissions);
                log.info("Created {} permissions", permissions.size());
        }

        /**
         * Helper method to create permission
         */
        private Permission createPermission(String code, String name, String module, String description) {
                Permission permission = new Permission();
                permission.setCode(code);
                permission.setName(name);
                permission.setModule(module);
                permission.setDescription(description);
                permission.setCreatedAt(Instant.now());
                return permission;
        }

        /**
         * INIT ROLES
         */
        private void initRoles() {
                log.info("Initializing Roles...");

                List<Permission> allPermissions = permissionRepository.findAll();

                // ==================== ADMIN ROLE ====================
                Role adminRole = new Role();
                adminRole.setName("ADMIN");
                adminRole.setDescription("Toàn quyền hệ thống");
                adminRole.setActive(true);
                adminRole.setPermissions(new HashSet<>(allPermissions)); // All permissions
                adminRole.setCreatedAt(Instant.now());
                roleRepository.save(adminRole);
                log.info("Created role: ADMIN with {} permissions", allPermissions.size());

                // ==================== WAREHOUSE_MANAGER ROLE ====================
                Role managerRole = new Role();
                managerRole.setName("WAREHOUSE_MANAGER");
                managerRole.setDescription("Quản lý kho - Approve workflows");
                managerRole.setActive(true);
                managerRole.setPermissions(new HashSet<>(permissionRepository.findByCodeIn(List.of(
                                // Inventory
                                "STOCK_COUNT_CREATE", "STOCK_COUNT_READ", "STOCK_COUNT_START", "STOCK_COUNT_COMPLETE",
                                "SCAN_BARCODE", "SCAN_LOG_READ",
                                "ADJUSTMENT_CREATE", "ADJUSTMENT_READ", "ADJUSTMENT_APPROVE", "ADJUSTMENT_REJECT",
                                "DISCREPANCY_READ",

                                // FEFO
                                "BATCH_CREATE", "BATCH_READ", "BATCH_UPDATE", "BATCH_ASSIGN_LOCATION",
                                "PICKING_GENERATE", "PICKING_READ", "EXPIRY_ALERT_READ", "EXPIRY_ALERT_ACKNOWLEDGE",
                                "BATCH_REPORT_READ",

                                // Cross-dock
                                "INBOUND_CREATE", "INBOUND_READ", "INBOUND_UPDATE", "INBOUND_ARRIVE", "INBOUND_RECEIVE",
                                "OUTBOUND_CREATE", "OUTBOUND_READ", "OUTBOUND_UPDATE", "OUTBOUND_CONFIRM",
                                "CROSSDOCK_MATCH_AUTO", "CROSSDOCK_MATCH_MANUAL", "CROSSDOCK_MAPPING_READ",
                                "CROSSDOCK_MOVE_STAGING", "CROSSDOCK_READY_SHIP", "CROSSDOCK_SHIPPED",
                                "CROSSDOCK_REPORT_READ",

                                // Packaging
                                "MATERIAL_CREATE", "MATERIAL_READ", "MATERIAL_UPDATE", "MATERIAL_UPDATE_STOCK",
                                "MATERIAL_LOW_STOCK_READ",
                                "TEMPLATE_CREATE", "TEMPLATE_READ", "TEMPLATE_UPDATE",
                                "PACKAGING_CALCULATE", "PACKAGING_USE", "PACKAGING_HISTORY_READ",
                                "PACKAGING_REPORT_READ",

                                // Warehouse Core
                                "WAREHOUSE_READ", "ZONE_READ", "LOCATION_READ", "LOCATION_CREATE", "LOCATION_UPDATE",
                                "PRODUCT_READ", "PRODUCT_CREATE", "PRODUCT_UPDATE",
                                "CATEGORY_READ", "SUPPLIER_READ", "CUSTOMER_READ",

                                // Reports
                                "REPORT_READ", "REPORT_EXPORT"))));
                managerRole.setCreatedAt(Instant.now());
                roleRepository.save(managerRole);
                log.info("Created role: WAREHOUSE_MANAGER");

                // ==================== INVENTORY_CLERK ROLE ====================
                Role clerkRole = new Role();
                clerkRole.setName("INVENTORY_CLERK");
                clerkRole.setDescription("Nhân viên kiểm kê - Nhận/Xuất hàng");
                clerkRole.setActive(true);
                clerkRole.setPermissions(new HashSet<>(permissionRepository.findByCodeIn(List.of(
                                // Inventory
                                "STOCK_COUNT_CREATE", "STOCK_COUNT_READ", "STOCK_COUNT_START", "STOCK_COUNT_COMPLETE",
                                "SCAN_BARCODE", "SCAN_LOG_READ",
                                "ADJUSTMENT_CREATE", "ADJUSTMENT_READ", "DISCREPANCY_READ",

                                // FEFO
                                "BATCH_CREATE", "BATCH_READ", "BATCH_UPDATE", "BATCH_ASSIGN_LOCATION",
                                "PICKING_GENERATE", "PICKING_READ", "EXPIRY_ALERT_READ",

                                // Cross-dock
                                "INBOUND_CREATE", "INBOUND_READ", "INBOUND_UPDATE", "INBOUND_ARRIVE", "INBOUND_RECEIVE",
                                "OUTBOUND_CREATE", "OUTBOUND_READ", "OUTBOUND_UPDATE",
                                "CROSSDOCK_MAPPING_READ", "CROSSDOCK_MOVE_STAGING",

                                // Packaging
                                "MATERIAL_READ", "PACKAGING_CALCULATE", "PACKAGING_USE", "PACKAGING_HISTORY_READ",

                                // Warehouse Core
                                "PRODUCT_READ", "LOCATION_READ", "SUPPLIER_READ", "CUSTOMER_READ",

                                // Reports
                                "REPORT_READ"))));
                clerkRole.setCreatedAt(Instant.now());
                roleRepository.save(clerkRole);
                log.info("Created role: INVENTORY_CLERK");

                // ==================== PICKER ROLE ====================
                Role pickerRole = new Role();
                pickerRole.setName("PICKER");
                pickerRole.setDescription("Nhân viên lấy hàng");
                pickerRole.setActive(true);
                pickerRole.setPermissions(new HashSet<>(permissionRepository.findByCodeIn(List.of(
                                // Inventory
                                "SCAN_BARCODE",

                                // FEFO
                                "BATCH_READ", "PICKING_READ", "PICKING_EXECUTE", "PICKING_CONFIRM",

                                // Cross-dock
                                "CROSSDOCK_MAPPING_READ", "CROSSDOCK_MOVE_STAGING",

                                // Warehouse Core
                                "PRODUCT_READ", "LOCATION_READ",

                                // Reports
                                "REPORT_READ"))));
                pickerRole.setCreatedAt(Instant.now());
                roleRepository.save(pickerRole);
                log.info("Created role: PICKER");

                // ==================== PACKER ROLE ====================
                Role packerRole = new Role();
                packerRole.setName("PACKER");
                packerRole.setDescription("Nhân viên đóng gói");
                packerRole.setActive(true);
                packerRole.setPermissions(new HashSet<>(permissionRepository.findByCodeIn(List.of(
                                // Inventory
                                "SCAN_BARCODE",

                                // Packaging
                                "MATERIAL_READ", "TEMPLATE_READ", "PACKAGING_CALCULATE", "PACKAGING_USE",

                                // Warehouse Core
                                "PRODUCT_READ", "LOCATION_READ",

                                // Reports
                                "REPORT_READ"))));
                packerRole.setCreatedAt(Instant.now());
                roleRepository.save(packerRole);
                log.info("Created role: PACKER");

                // ==================== VIEWER ROLE ====================
                Role viewerRole = new Role();
                viewerRole.setName("VIEWER");
                viewerRole.setDescription("Chỉ xem - Không thao tác");
                viewerRole.setActive(true);
                viewerRole.setPermissions(new HashSet<>(permissionRepository.findByCodeIn(List.of(
                                // Inventory
                                "STOCK_COUNT_READ", "SCAN_LOG_READ", "ADJUSTMENT_READ", "DISCREPANCY_READ",

                                // FEFO
                                "BATCH_READ", "PICKING_READ", "EXPIRY_ALERT_READ", "BATCH_REPORT_READ",

                                // Cross-dock
                                "INBOUND_READ", "OUTBOUND_READ", "CROSSDOCK_MAPPING_READ", "CROSSDOCK_REPORT_READ",

                                // Packaging
                                "MATERIAL_READ", "TEMPLATE_READ", "PACKAGING_HISTORY_READ", "PACKAGING_REPORT_READ",

                                // Warehouse Core
                                "WAREHOUSE_READ", "ZONE_READ", "LOCATION_READ",
                                "PRODUCT_READ", "CATEGORY_READ", "SUPPLIER_READ", "CUSTOMER_READ",

                                // Reports
                                "REPORT_READ", "REPORT_EXPORT",

                                // System
                                "AUDIT_LOG_READ"))));
                viewerRole.setCreatedAt(Instant.now());
                roleRepository.save(viewerRole);
                log.info("Created role: VIEWER");

                log.info("Created total {} roles", 6);
        }

        /**
         * INIT ADMIN USER
         */
        private void initAdminUser() {
                log.info("Initializing Admin User...");

                User admin = new User();
                admin.setUsername("admin@wms.com");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@wms.com");
                admin.setFullName("System Administrator");
                admin.setPhone("0900000000");
                admin.setWarehouseId(null); // ADMIN không thuộc warehouse nào
                admin.setActive(true);
                admin.setCreatedAt(Instant.now());
                admin.setUpdatedAt(Instant.now());

                // Gán role ADMIN
                Role adminRole = roleRepository.findByName("ADMIN")
                                .orElseThrow(() -> new RuntimeException("Admin role not found"));

                admin.setRoles(Set.of(adminRole));

                userRepository.save(admin);

                log.info("Created admin user successfully");
                log.info("=".repeat(60));
                log.info("DEFAULT ADMIN CREDENTIALS:");
                log.info("Username: admin@wms.com");
                log.info("Password: admin123");
                log.info("Role: ADMIN");
                log.info("Permissions: {} permissions", adminRole.getPermissions().size());
                log.info("=".repeat(60));
        }
}