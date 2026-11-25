# Ứng dụng Quản lý kho hàng (Android - Java & SQLite)

Ứng dụng Android hỗ trợ quản lý sản phẩm trong kho hàng (tên, giá bán, số lượng tồn, phân loại, trạng thái kinh doanh, mô tả) và cảnh báo sản phẩm sắp hết hàng (`quantity < 5`), lưu trữ toàn bộ dữ liệu bằng **SQLite** thông qua `SQLiteOpenHelper`.

## Thông tin chung
- **Ngôn ngữ & Giao diện:** Java, XML (Material Components)
- **Cơ sở dữ liệu:** SQLite (`warehouse.db`, version `1`)
- **Tài khoản đăng nhập mẫu:** `admin` / `123456`
- **Dữ liệu mẫu khởi tạo sẵn:** `Chuột không dây` (SL: 20), `Bàn phím cơ` (SL: 3), `Sổ tay A5` (SL: 40)

---

## Cấu trúc thư mục và vai trò từng file

### 1. Cấu hình Project & Gradle
- `settings.gradle`: Khai báo tên project (`QuanLyKhoHang`), kho repository (`google`, `mavenCentral`) và module `:app`.
- `build.gradle`: Cấu hình plugin `com.android.application` ở cấp root.
- `gradle.properties`: Bật AndroidX (`android.useAndroidX=true`), cấu hình bộ nhớ JVM và `nonTransitiveRClass`.
- `gradlew`, `gradlew.bat`, `gradle/`: Bộ Gradle Wrapper (Gradle `9.5.0`) để build project đồng nhất trên mọi máy.
- `app/build.gradle`: Cấu hình `namespace` (`com.example.warehouseapp`), `compileSdk` (`37`), `minSdk` (`24`), `targetSdk` (`36`), Java 17 và các thư viện (`appcompat`, `material`, `constraintlayout`, `recyclerview`, `cardview`, `junit`).
- `app/proguard-rules.pro`: Tập tin cấu hình quy tắc ProGuard/R8 khi đóng gói release.
- `.gitignore`, `app/.gitignore`: Loại trừ thư mục build, cache Gradle, cấu hình IDE và file cá nhân (`local.properties`, `*.docx`) khỏi Git.

### 2. Manifest
- `app/src/main/AndroidManifest.xml`: Khai báo 4 Activity (`LoginActivity`, `MainActivity`, `AddProductActivity`, `ProductDetailActivity`), thiết lập `LoginActivity` làm màn hình khởi chạy (`MAIN` / `LAUNCHER`) và gắn theme `Theme.QuanLyKhoHang`.

### 3. Mã nguồn Java (`app/src/main/java/com/example/warehouseapp/`)
- `Product.java`: Lớp model biểu diễn đối tượng sản phẩm với các thuộc tính `id`, `name`, `price`, `quantity`, `category`, `active`, `description` cùng các constructor và getter/setter.
- `DatabaseHelper.java`: Lớp kế thừa `SQLiteOpenHelper` quản lý `warehouse.db` (version 1):
  - `onCreate()`: Tạo bảng `users`, bảng `products`, chèn tài khoản mẫu `admin`/`123456` và 3 sản phẩm mẫu.
  - `checkLogin(username, password)`: Kiểm tra đăng nhập bằng truy vấn tham số bảng `users`.
  - `insertUser(username, password)`: Thêm tài khoản vào bảng `users`.
  - `insertProduct(Product p)`: Thêm sản phẩm mới vào bảng `products`, trả về `id`.
  - `getAllProducts()`: Lấy danh sách toàn bộ sản phẩm từ SQLite.
  - `getProductById(int id)`: Truy vấn một sản phẩm theo `id`.
  - `updateQuantity(int id, int quantity)`: Cập nhật số lượng tồn kho theo `id`.
  - `deleteProduct(int id)`: Xóa sản phẩm theo `id`.
  - `getTotalValue()`: Tính tổng giá trị kho (`SUM(price * quantity)`).
  - `countLowStock()`: Đếm số sản phẩm có `quantity < 5`.
- `CurrencyUtils.java`: Lớp tiện ích định dạng tiền tệ VNĐ có dấu chấm phân cách hàng nghìn (ví dụ: `150.000 đ`).
- `ProductAdapter.java`: Adapter cho `RecyclerView`, hiển thị từng thẻ sản phẩm (avatar chữ cái đầu, tên, giá bán, số lượng, badge loại sản phẩm, nhãn cảnh báo đỏ `Sắp hết hàng` khi `quantity < 5`, nhãn `Ngừng kinh doanh` nếu `active = false`) và bắt sự kiện nhấn vào từng dòng.
- `LoginActivity.java`: Màn hình đăng nhập, kiểm tra bỏ trống và xác thực tài khoản qua `DatabaseHelper.checkLogin()`. Khi hợp lệ sẽ mở `MainActivity` và gọi `finish()`.
- `MainActivity.java`: Màn hình danh sách sản phẩm và thống kê kho (tổng số sản phẩm, tổng giá trị kho, số sản phẩm sắp hết hàng); tự động tải lại dữ liệu từ SQLite trong `onResume()`, mở `AddProductActivity` hoặc truyền `product_id` sang `ProductDetailActivity`.
- `AddProductActivity.java`: Màn hình thêm sản phẩm mới, kiểm tra hợp lệ tên sản phẩm, giá bán (`> 0`), số lượng (`>= 0`), chọn loại qua `Spinner`, trạng thái kinh doanh qua `SwitchMaterial`, nhập mô tả nhiều dòng và lưu vào SQLite.
- `ProductDetailActivity.java`: Màn hình chi tiết sản phẩm, nhận `product_id` từ `Intent` và truy vấn lại từ SQLite; hiển thị thông tin chi tiết, giá trị tồn kho (`price * quantity`) và hỗ trợ các thao tác `Nhập thêm 1`, `Bán 1` (chặn giảm và báo `"Hết hàng"` khi số lượng bằng 0), `Quay lại`, `Xóa sản phẩm` (có hộp thoại xác nhận).

### 4. Giao diện XML & Tài nguyên (`app/src/main/res/`)
- **Layouts (`res/layout/`):**
  - `activity_login.xml`: Giao diện đăng nhập với logo tròn, `TextInputLayout` (icon + nút ẩn/hiện mật khẩu), vùng báo lỗi và nút `Đăng nhập`.
  - `activity_main.xml`: `MaterialToolbar`, thẻ thống kê gradient Indigo, `RecyclerView` danh sách sản phẩm, trạng thái rỗng và nút `Thêm sản phẩm`.
  - `item_product.xml`: Layout một dòng sản phẩm dạng `MaterialCardView`.
  - `activity_add_product.xml`: Form thêm sản phẩm với `MaterialToolbar`, `TextInputLayout`, `Spinner` loại sản phẩm, `SwitchMaterial` và nút `Lưu sản phẩm`.
  - `activity_product_detail.xml`: Giao diện chi tiết sản phẩm trong `MaterialCardView` cùng 4 nút thao tác (`Nhập thêm 1`, `Bán 1`, `Quay lại`, `Xóa sản phẩm`).
- **Values (`res/values/`):**
  - `colors.xml`: Bảng màu chủ đạo Xanh Indigo (`#1E3A8A`, `#2563EB`), cam hổ phách (`#D97706`), đỏ cảnh báo (`#DC2626`) và màu nền/chữ.
  - `strings.xml`: Toàn bộ chuỗi hiển thị tiếng Việt và mảng danh mục `product_categories`.
  - `themes.xml`: Theme `Theme.QuanLyKhoHang` kế thừa `Theme.MaterialComponents.Light.NoActionBar` cùng các style cho `TextInputLayout` và `MaterialButton`.
- **Drawables & Icons (`res/drawable/`, `res/mipmap-anydpi-v26/`):**
  - Bộ shape nền bo góc cho badge, thẻ thống kê, nút bấm (`bg_*.xml`), bộ vector icon (`ic_*.xml`) và cấu hình adaptive icon của ứng dụng (`ic_launcher.xml`, `ic_launcher_round.xml`).

### 5. Kiểm thử (`app/src/test/`)
- `app/src/test/java/com/example/warehouseapp/CurrencyUtilsTest.java`: Unit test kiểm tra hàm định dạng tiền tệ `CurrencyUtils.formatVnd()`.
