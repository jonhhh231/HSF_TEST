# E-Commerce Platform Project
Project coding by SpringMVC

# HSF_TEST - E-Commerce Platform 

![Java](https://img.shields.io/badge/Java-17%2B-ed8b00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6db33f?style=for-the-badge&logo=spring&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005f0f.svg?style=for-the-badge&logo=thymeleaf&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-5-563d7c?style=for-the-badge&logo=bootstrap&logoColor=white)
![Chart.js](https://img.shields.io/badge/Chart.js-F5788D?style=for-the-badge&logo=chart.js&logoColor=white)

##  Giới thiệu (Introduction)

**HSF_TEST** là một nền tảng thương mại điện tử (E-Commerce) được xây dựng dựa trên Java Spring Boot. Dự án cung cấp các chức năng toàn diện cho việc bán hàng, quản lý đơn hàng, và đặc biệt là hệ thống Dashboard quản trị trực quan với biểu đồ thống kê.

Dự án này được thiết kế theo mô hình MVC, sử dụng Rendering phía Server (Thymeleaf) kết hợp với AJAX để tối ưu trải nghiệm người dùng.

##  Chức năng chính (Features)

###  Phân hệ Người dùng (Client)
* **Trang chủ & Sản phẩm:** Xem danh sách sản phẩm, chi tiết sản phẩm.
* **Giỏ hàng:** Thêm, sửa, xóa sản phẩm trong giỏ.
* **Đặt hàng:** Quy trình Checkout đơn giản.
* **Authentication:** Đăng ký, Đăng nhập.

###  Phân hệ Quản trị (Admin)
* **Dashboard Thống kê (Nổi bật):**
    * Biểu đồ đường (Line Chart): Theo dõi dòng tiền/doanh thu theo ngày trong tháng.
    * Biểu đồ cột (Bar Chart): Top 5 sản phẩm bán chạy nhất.
    * Bộ lọc (Filter): Xem thống kê theo Tháng/Năm tùy chọn (AJAX).
* **Quản lý Danh mục (Categories):**
    * CRUD (Thêm, Xem, Sửa, Xóa).
    * Xóa không tải lại trang (AJAX Delete).
* **Quản lý Sản phẩm (Products):** Thêm mới, cập nhật thông tin, upload ảnh.
* **Phân quyền (RBAC):** Sử dụng Spring Security để phân quyền truy cập (Chỉ Admin mới vào được trang quản trị).

## 🛠 Công nghệ sử dụng (Tech Stack)

* **Backend:** Java 17, Spring Boot 3 (Web, Data JPA, Security, DevTools).
* **Frontend:** HTML5, CSS3, Bootstrap 5, Thymeleaf, JavaScript (ES6).
* **Visualization:** Chart.js (Vẽ biểu đồ).
* **Database:** MySQL / SQL Server (Tùy cấu hình).
* **Build Tool:** Maven.
* **Utilities:** Lombok.

##  Cài đặt & Chạy dự án (Installation)

### 1. Yêu cầu hệ thống
* JDK 17 trở lên.
* Maven.
* MySQL hoặc SQL Server.

### 2. Clone dự án
```bash
git clone [https://github.com/jonhhh231/HSF_TEST.git](https://github.com/jonhhh231/HSF_TEST.git)
cd HSF_TEST

### 3. DB Script

-- =========================
-- CREATE DATABASE
-- =========================
CREATE DATABASE Assignment_1;
GO

USE Assignment_1;
GO

-- =========================
-- USERS
-- =========================
CREATE TABLE users (
    id INT IDENTITY(1,1) PRIMARY KEY,
    full_name NVARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    role VARCHAR(50),
    is_active BIT DEFAULT 1
);
GO

-- =========================
-- CATEGORIES
-- =========================
CREATE TABLE categories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255)
);
GO

-- =========================
-- PRODUCTS
-- =========================
CREATE TABLE products (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(255),
    description NVARCHAR(1000),
    processor NVARCHAR(255),
    graphics NVARCHAR(255),
    memory NVARCHAR(255),
    storage NVARCHAR(255),
    display NVARCHAR(255),
    battery NVARCHAR(255),
    price DECIMAL(10,2),
    stock_quantity INT,
    category_id INT,
    url NVARCHAR(500),
    is_active BIT DEFAULT 1,
    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id) REFERENCES categories(id)
);
GO

-- =========================
-- REVIEWS
-- =========================
CREATE TABLE reviews (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT,
    product_id INT,
    rating INT,
    comment NVARCHAR(MAX),
    CONSTRAINT fk_review_user
        FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_review_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);
GO

-- =========================
-- CARTS
-- =========================
CREATE TABLE carts (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT,
    CONSTRAINT fk_cart_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);
GO

-- =========================
-- CART_PRODUCTS
-- =========================
CREATE TABLE cart_products (
    cart_id INT,
    product_id INT,
    quantity INT,
    PRIMARY KEY (cart_id, product_id),
    CONSTRAINT fk_cart_product_cart
        FOREIGN KEY (cart_id) REFERENCES carts(id),
    CONSTRAINT fk_cart_product_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);
GO

-- =========================
-- ORDERS
-- =========================
CREATE TABLE orders (
    id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT,
    order_code VARCHAR(100),
    address NVARCHAR(255),
    final_price DECIMAL(10,2),
    payment_method VARCHAR(100),
    payment_status VARCHAR(100),
    paid_at DATETIME,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);
GO

-- =========================
-- ORDER_DETAILS
-- =========================
CREATE TABLE order_details (
    order_id INT,
    product_id INT,
    quantity INT,
    price DECIMAL(10,2),
    PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_detail_order
        FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT fk_order_detail_product
        FOREIGN KEY (product_id) REFERENCES products(id)
);
GO

-- =========================
-- TRANSACTION_HISTORIES
-- =========================
CREATE TABLE transaction_histories (
    id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT,
    transaction_code VARCHAR(255),
    amount DECIMAL(10,2),
    status VARCHAR(100),
    paid_at DATETIME,
    CONSTRAINT fk_transaction_order
        FOREIGN KEY (order_id) REFERENCES orders(id)
);
GO

-- =========================
-- INSERT DATA
-- =========================

-- USERS
INSERT INTO users (full_name, email, password, role) VALUES
(N'Nguyễn Văn A', 'a@gmail.com', '123456', 'USER'),
(N'Trần Thị B', 'b@gmail.com', '123456', 'USER'),
(N'Admin System', 'admin@gmail.com', 'admin123', 'ADMIN');

-- CATEGORIES
INSERT INTO categories (name) VALUES
(N'Laptop Gaming'),
(N'Laptop Văn Phòng'),
(N'MacBook'),
(N'Laptop Đồ Họa');

-- PRODUCTS
INSERT INTO products 
(name, description, processor, graphics, memory, storage, display, battery, price, stock_quantity, category_id, url) 
VALUES
(
 N'ASUS ROG Strix G15',
 N'ASUS ROG Strix G15 là mẫu laptop gaming cao cấp, được thiết kế dành cho người dùng yêu cầu hiệu năng mạnh mẽ, khả năng xử lý đồ họa vượt trội và độ ổn định cao trong các tác vụ nặng như chơi game và làm việc chuyên sâu.',
 N'AMD Ryzen 7 6800H',
 N'NVIDIA GeForce RTX 3060',
 N'16GB DDR5',
 N'1TB SSD',
 N'15.6 inch Full HD 144Hz',
 N'90Wh',
 28990000, 10, 1,
 N'https://bizweb.dktcdn.net/100/512/769/products/asus-rog-strix-g15-3-7b0021cd-b6ad-4303-84f2-7a76a524d973.webp?v=1721449212623'
),

(
 N'Acer Nitro 5',
 N'Acer Nitro 5 là dòng laptop gaming phổ thông, cân bằng tốt giữa hiệu năng và giá thành, phù hợp cho sinh viên, game thủ và người dùng cần một thiết bị mạnh mẽ cho công việc hàng ngày.',
 N'Intel Core i5 12500H',
 N'NVIDIA GeForce RTX 3050',
 N'16GB DDR4',
 N'512GB SSD',
 N'15.6 inch Full HD 144Hz',
 N'57Wh',
 22990000, 15, 1,
 N'https://hungphatlaptop.com/wp-content/uploads/2023/11/Acer-Nitro-5-Tiger-Features-02.jpeg'
),

(
 N'Dell Inspiron 15',
 N'Dell Inspiron 15 là laptop văn phòng hướng đến sự ổn định, thiết kế thanh lịch và hiệu suất đáp ứng tốt các nhu cầu học tập, làm việc và giải trí cơ bản.',
 N'Intel Core i5 1135G7',
 N'Intel Iris Xe Graphics',
 N'8GB DDR4',
 N'512GB SSD',
 N'15.6 inch Full HD',
 N'41Wh',
 15990000, 20, 2,
 N'https://zshop.vn/images/thumbnails/1357/1000/detailed/134/notebook-inspiron-15-3530-nt-plastic-usbc-silver-gallery-4_p2jb-6k.jpg'
),

(
 N'MacBook Air M1',
 N'MacBook Air M1 mang đến hiệu năng vượt trội trong thiết kế mỏng nhẹ, thời lượng pin ấn tượng và khả năng tối ưu hóa mạnh mẽ cho các tác vụ lập trình, học tập và sáng tạo nội dung.',
 N'Apple M1',
 N'Apple GPU 7-core',
 N'8GB Unified Memory',
 N'256GB SSD',
 N'13.3 inch Retina',
 N'49.9Wh',
 20990000, 12, 3,
 N'https://cafefcdn.com/203337114487263232/2021/8/23/-1629706474576156268954.jpg'
),

(
 N'MacBook Pro M2',
 N'MacBook Pro M2 là lựa chọn lý tưởng cho người dùng chuyên nghiệp, với hiệu năng cao, màn hình chất lượng và khả năng xử lý ổn định trong thời gian dài.',
 N'Apple M2',
 N'Apple GPU 10-core',
 N'16GB Unified Memory',
 N'512GB SSD',
 N'14 inch Liquid Retina XDR',
 N'70Wh',
 35990000, 8, 3,
 N'https://laptop15.vn/wp-content/uploads/2023/08/MacBook-Pro-M1-14-inch-4.png'
),

(
 N'MSI Creator Z16',
 N'MSI Creator Z16 là dòng laptop cao cấp dành cho đồ họa và sáng tạo nội dung, sở hữu cấu hình mạnh mẽ, thiết kế sang trọng và màn hình đạt chuẩn màu sắc chuyên nghiệp.',
 N'Intel Core i7 12700H',
 N'NVIDIA GeForce RTX 3060',
 N'32GB DDR5',
 N'1TB SSD',
 N'16 inch QHD+',
 N'90Wh',
 42990000, 5, 4,
 N'https://cdn.tgdd.vn/Products/Images/44/274783/msi-creator-z16-a12uet-i7-036vn-1-750x500.jpg'
);
-- REVIEWS
INSERT INTO reviews (user_id, product_id, rating, comment) VALUES
(1, 1, 5, N'Laptop gaming quá mượt, chiến game max setting'),
(2, 4, 5, N'MacBook pin trâu, dùng code cả ngày không lo'),
(1, 3, 4, N'Máy văn phòng ổn, hơi nóng nhẹ');


USE Assignment_1;
GO

DECLARE @counter INT = 1;
DECLARE @total_orders INT = 100;
DECLARE @start_date DATETIME = '2025-10-01';
DECLARE @end_date DATETIME = GETDATE(); -- Hiện tại (2026)

WHILE @counter <= @total_orders
BEGIN
    -- 1. Lấy ngẫu nhiên 1 User ID
    DECLARE @user_id INT = (SELECT TOP 1 id FROM users ORDER BY NEWID());
    
    -- 2. Tạo ngày ngẫu nhiên từ 10/2025 đến nay
    DECLARE @random_days INT = ABS(CHECKSUM(NEWID())) % DATEDIFF(DAY, @start_date, @end_date);
    DECLARE @order_date DATETIME = DATEADD(DAY, @random_days, @start_date);
    
    -- 3. Tạo mã đơn hàng ngẫu nhiên
    DECLARE @order_code VARCHAR(100) = 'ORD' + CAST(ABS(CHECKSUM(NEWID())) % 1000000 AS VARCHAR);

    -- 4. Xác định Method và Status
    -- Random: 0 = VNPAY, 1 = COD
    DECLARE @method_rand INT = ABS(CHECKSUM(NEWID())) % 2;
    DECLARE @method VARCHAR(100) = CASE WHEN @method_rand = 0 THEN 'VNPAY' ELSE 'COD' END;
    
    -- Nếu VNPAY thì mặc định PAID, nếu COD thì 70% là PAID, 30% UNPAID
    DECLARE @status VARCHAR(100) = 
        CASE 
            WHEN @method = 'VNPAY' THEN 'PAID'
            WHEN @method = 'COD' AND (ABS(CHECKSUM(NEWID())) % 10) < 7 THEN 'PAID'
            ELSE 'UNPAID'
        END;

    -- 5. Chèn vào bảng Orders
    INSERT INTO orders (user_id, order_code, address, final_price, payment_method, payment_status, paid_at, created_at, updated_at)
    VALUES (
        @user_id, 
        @order_code, 
        N'Địa chỉ khách hàng tại số ' + CAST(ABS(CHECKSUM(NEWID())) % 500 AS NVARCHAR), 
        0, 
        @method,
        @status,
        CASE WHEN @status = 'PAID' THEN @order_date ELSE NULL END, -- Chỉ có ngày thanh toán nếu đã PAID
        @order_date,
        @order_date
    );

    DECLARE @current_order_id INT = SCOPE_IDENTITY();

    -- 6. Chèn ngẫu nhiên từ 1 đến 3 sản phẩm vào Order_Details
    INSERT INTO order_details (order_id, product_id, quantity, price)
    SELECT TOP (ABS(CHECKSUM(NEWID())) % 3 + 1)
        @current_order_id,
        id,
        (ABS(CHECKSUM(NEWID())) % 2 + 1),
        price
    FROM products
    ORDER BY NEWID();

    -- 7. Cập nhật lại tổng tiền cho Order
    UPDATE o
    SET o.final_price = d.total
    FROM orders o
    JOIN (SELECT order_id, SUM(price * quantity) as total FROM order_details GROUP BY order_id) d 
    ON o.id = d.order_id
    WHERE o.id = @current_order_id;

    -- 8. Tạo lịch sử giao dịch nếu trạng thái là PAID
    IF @status = 'PAID'
    BEGIN
        INSERT INTO transaction_histories (order_id, transaction_code, amount, status, paid_at)
        SELECT id, 'TRX' + order_code, final_price, 'SUCCESS', paid_at
        FROM orders WHERE id = @current_order_id;
    END

    SET @counter = @counter + 1;
END;
GO

-- Kiểm tra 10 đơn hàng mới nhất
SELECT TOP 10 id, order_code, payment_method, payment_status, final_price, created_at 
FROM orders 
ORDER BY id DESC;