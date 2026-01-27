Ý tưởng tổng thể (Big Picture)

Mục tiêu:
Xây dựng một hệ thống quản lý kho tích hợp, hỗ trợ:

Xuất hàng đúng hạn sử dụng (FEFO)

Xử lý hàng Cross-docking không cần lưu kho

Kiểm kê kho theo thời gian thực

Quản lý chi phí vật tư đóng gói chính xác theo đơn hàng

➡️ Thay vì 4–5 hệ thống rời rạc, ta có 1 core platform + nhiều module nghiệp vụ.

3️⃣ Cách gộp các đề tài thành 1 hệ thống duy nhất
🧠 Core System: Warehouse Management Core

Đây là “xương sống”:

Quản lý SKU, lô hàng (Batch/Lot)

Hạn sử dụng

Vị trí kho

Tồn kho logic (System Inventory)

📦 Module 1: Quản lý kho theo FEFO

Gộp đề tài số 1

Chức năng chính:

Lưu hạn sử dụng theo Batch/Lot

Khi tạo đơn xuất → hệ thống tự động đề xuất lô gần hết hạn nhất

Cảnh báo hàng sắp hết hạn / quá hạn

Giá trị học thuật:

Business rule engine

Thuật toán chọn lô hàng

Rất hợp cho dược phẩm & thực phẩm

🚚 Module 2: Cross-docking Management

Chức năng chính:

Nhận hàng → gán trực tiếp cho đơn xuất

Không ghi nhận vào tồn kho dài hạn

Theo dõi thời gian hàng nằm tại dock

Liên kết với FEFO:

Nếu hàng Cross-docking có hạn dùng → vẫn áp dụng FEFO khi phân bổ

📡 Module 3: Kiểm kê kho thời gian thực (RFID / Barcode)


Chức năng chính:

Quét mã khi:

Nhập kho

Xuất kho

Kiểm kê định kỳ

So sánh:

System Inventory

Physical Count

Ghi nhận chênh lệch & lịch sử kiểm kê

Điểm mạnh kỹ thuật:

Real-time update

Event-driven (scan → update stock)

Có thể demo rất dễ

📦📦 Module 4: Quản lý vật tư tiêu hao đóng gói

Chức năng chính:

Theo dõi:

Thùng carton

Băng keo

Xốp nổ…

Mỗi đơn xuất → tự động trừ vật tư

Tính chi phí đóng gói theo đơn hàng

Liên kết hay:

Xuất kho FEFO → phát sinh đóng gói

Cross-docking → vẫn có chi phí vật tư

4️⃣ Kiến trúc hệ thống đề xuất (dễ thuyết trình)
[ Web / Mobile App ]
        |
[ API Gateway ]
        |
------------------------------------------------
| Inventory | FEFO Engine | Cross-docking |
| Packaging | RFID/Barcode | Reporting    |
------------------------------------------------
        |
     [ Database ]

Tech stack gợi ý (tuỳ trình độ):

Backend: Java Spring Boot 

DB: PostgreSQL

Frontend: React

Scan: Barcode Scanner (mock cũng được)

RFID: mô phỏng bằng API/event

5️⃣ Nếu viết báo cáo / khóa luận thì chia chương thế nào?

Giới thiệu & bài toán thực tế

Tổng quan hệ thống WMS

Thiết kế kiến trúc hệ thống

Module FEFO & thuật toán phân bổ

Cross-docking workflow

Kiểm kê kho thời gian thực

Quản lý chi phí vật tư đóng gói

Demo – đánh giá – hướng phát triển

👉 Nghe là thấy “đề tài lớn nhưng có kiểm soát” liền.