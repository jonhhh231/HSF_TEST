# 🛒 HSF_TEST - E-Commerce Platform

![Java](https://img.shields.io/badge/Java-17%2B-ed8b00?style=for-the-badge\&logo=openjdk\&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6db33f?style=for-the-badge\&logo=spring\&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005f0f.svg?style=for-the-badge\&logo=thymeleaf\&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5-563d7c?style=for-the-badge\&logo=bootstrap\&logoColor=white)
![Chart.js](https://img.shields.io/badge/Chart.js-F5788D?style=for-the-badge\&logo=chart.js\&logoColor=white)

---

# 📌 Project Overview

**HSF_TEST** là một hệ thống **E-Commerce Platform** được xây dựng bằng **Java Spring Boot (Spring MVC)** nhằm mô phỏng một nền tảng bán hàng trực tuyến hoàn chỉnh.

Dự án bao gồm:

* Hệ thống bán hàng online
* Dashboard thống kê
* Thanh toán online
* Chat realtime với Admin
* AI Chatbot hỗ trợ khách hàng

Hệ thống sử dụng kiến trúc **MVC (Model – View – Controller)** với **Server Side Rendering (Thymeleaf)** kết hợp **AJAX và WebSocket** để tăng trải nghiệm người dùng.

---

# 🧱 System Architecture

```
Client (Browser)
       │
       │ HTTP / WebSocket
       ▼
Spring Boot Application
       │
 ┌───────────────┐
 │ Controllers   │
 └───────────────┘
       │
       ▼
 ┌───────────────┐
 │ Services      │
 └───────────────┘
       │
       ▼
 ┌───────────────┐
 │ Repositories  │
 │ (Spring JPA)  │
 └───────────────┘
       │
       ▼
     Database
   (SQL Server)
```

---

# 🚀 Main Features

---

# 👤 Client Features

## 🏠 Product Browsing

Khách hàng có thể:

* Xem danh sách sản phẩm
* Xem chi tiết sản phẩm
* Lọc theo danh mục
* Xem review sản phẩm

---

## 🛒 Cart System

Chức năng giỏ hàng:

* Thêm sản phẩm
* Cập nhật số lượng
* Xóa sản phẩm
* Tính tổng tiền

Database sử dụng:

```
carts
cart_products
```

---

## 💳 Checkout System

Quy trình đặt hàng:

```
Cart
 ↓
Checkout
 ↓
Create Order
 ↓
Payment
```

Hỗ trợ thanh toán:

* **VNPAY**
* **COD (Cash On Delivery)**

---

## ⭐ Review System

Người dùng có thể review sản phẩm **sau khi đơn hàng đã giao thành công**.

Constraint database:

```sql
UNIQUE (user_id, order_id, product_id)
```

Điều này đảm bảo:

* mỗi user chỉ review
* 1 sản phẩm
* trong 1 đơn hàng

---

# 🤖 AI Chatbot

Website tích hợp **AI Chatbot hỗ trợ khách hàng**.

### Chức năng

* trả lời câu hỏi về sản phẩm
* hướng dẫn mua hàng
* hỗ trợ thông tin website

### Ví dụ

User hỏi:

```
Laptop gaming nào tốt?
```

AI trả lời:

```
Bạn có thể tham khảo ASUS ROG Strix G15 hoặc MSI Creator Z16.
```

---

# 💬 Realtime Chat với Admin

Hệ thống hỗ trợ **chat trực tiếp giữa khách hàng và Admin**.

## Công nghệ sử dụng

* Spring WebSocket
* STOMP
* SockJS
* AJAX

## Chat Flow

```
User gửi tin nhắn
        │
        ▼
 WebSocket Endpoint
        │
        ▼
Spring Controller
        │
        ▼
Admin nhận tin nhắn realtime
```

### Ưu điểm

* realtime
* không cần reload trang
* hỗ trợ nhiều user cùng lúc

---

# 💾 Chat Message Database

Tin nhắn được lưu lại để:

* lưu lịch sử chat
* phục vụ support khách hàng

### Table: `messages`

```sql
CREATE TABLE messages (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    sender_id INT,
    receiver_id INT,
    type INT NOT NULL DEFAULT 0,
    content NVARCHAR(MAX),
    created_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_message_sender
        FOREIGN KEY (sender_id) REFERENCES users(id)
);
```

### Message Type

| Type | Meaning      |
| ---- | ------------ |
| 0    | User → Admin |
| 1    | Admin → User |
| 2    | AI Chatbot   |

---

# 🛠 Admin Features

---

# 📊 Admin Dashboard

Dashboard hiển thị thống kê bằng **Chart.js**

### 📈 Revenue Chart

Biểu đồ doanh thu:

* theo ngày
* theo tháng
* theo năm

### 📊 Top Selling Products

Hiển thị:

```
Top 5 sản phẩm bán chạy
```

### AJAX Filter

Admin có thể lọc:

```
Month
Year
```

mà **không cần reload trang**.

---

# 📦 Product Management

Admin có thể:

* Thêm sản phẩm
* Chỉnh sửa sản phẩm
* Upload hình ảnh
* Quản lý tồn kho

---

# 🗂 Category Management

CRUD danh mục:

```
Create
Read
Update
Delete
```

Delete sử dụng **AJAX**.

---

# 📦 Order Management

Admin có thể:

* xem đơn hàng
* xem chi tiết đơn
* cập nhật trạng thái

### Shipping Status

```
PENDING
PROCESSING
SHIPPING
DELIVERED
```

---

# 🛠 Tech Stack

## Backend

* Java 17
* Spring Boot 3
* Spring MVC
* Spring Data JPA
* Spring Security
* Spring WebSocket

## Frontend

* HTML5
* CSS3
* Bootstrap 5
* Thymeleaf
* JavaScript
* AJAX

## Visualization

* Chart.js

## Database

* SQL Server
* MySQL (configurable)

## Build Tool

* Maven

## Utilities

* Lombok

---

# 🗄 Database Schema

Main tables:

```
users
categories
products
carts
cart_products
orders
order_details
transaction_histories
reviews
messages
```

---

# ⚙ Installation Guide

## 1️⃣ Requirements

* JDK 17+
* Maven
* SQL Server hoặc MySQL
* IntelliJ / Eclipse

---

## 2️⃣ Clone Project

```bash
git clone https://github.com/jonhhh231/HSF_TEST.git
cd HSF_TEST
```

---

## 3️⃣ Setup Database

Chạy file SQL:

```
database/schema.sql
```

Script sẽ:

* tạo database
* tạo tables
* seed dữ liệu
* tạo 100 đơn hàng giả lập

---

## 4️⃣ Configure Database

Trong file:

```
application.properties
```

Ví dụ:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=Assignment_1
spring.datasource.username=sa
spring.datasource.password=123456

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
```

---

## 5️⃣ Run Project

```bash
mvn spring-boot:run
```

hoặc chạy class:

```
HsfTestApplication.java
```

---

# 🌐 Access Application

### Client

```
http://localhost:8080
```

### Admin

```
http://localhost:8080/admin
```

---

# 🔑 Default Accounts

## Admin

```
email: admin@gmail.com
password: admin123
```

## User

```
email: a@gmail.com
password: 123456
```

---

# 📊 Sample Data

Database script sẽ tạo:

* 6 products
* 4 categories
* 100 orders
* random transactions
* sample reviews

---

# 🔮 Future Improvements

Planned features:

* Product search
* Pagination
* AI product recommendation
* Notification system
* Cloud image storage (S3 / Cloudinary)
* Microservice architecture
* Mobile API

---

# 📸 Screenshots (Optional)

Bạn có thể thêm:

```
/screenshots/home.png
/screenshots/product.png
/screenshots/admin-dashboard.png
/screenshots/chat-system.png
```

Ví dụ:

```
![Home Page](screenshots/home.png)
```

---

# 👨‍💻 Author

Developed by:

**Joss Ly**

GitHub

```
https://github.com/jonhhh231
```

---

⭐ If you find this project helpful, please give it a **star on GitHub**.
