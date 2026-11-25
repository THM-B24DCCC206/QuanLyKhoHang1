package com.example.warehouseapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Lớp quản lý cơ sở dữ liệu SQLite (warehouse.db) gồm bảng users và products.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "warehouse.db";
    public static final int DATABASE_VERSION = 1;
    public static final int LOW_STOCK_THRESHOLD = 5;

    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";

    private static final String TABLE_PRODUCTS = "products";
    private static final String COL_PROD_ID = "id";
    private static final String COL_PROD_NAME = "name";
    private static final String COL_PROD_PRICE = "price";
    private static final String COL_PROD_QUANTITY = "quantity";
    private static final String COL_PROD_CATEGORY = "category";
    private static final String COL_PROD_ACTIVE = "active";
    private static final String COL_PROD_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COL_PASSWORD + " TEXT NOT NULL)";

        String createProductsTable = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COL_PROD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PROD_NAME + " TEXT NOT NULL, "
                + COL_PROD_PRICE + " INTEGER NOT NULL, "
                + COL_PROD_QUANTITY + " INTEGER NOT NULL, "
                + COL_PROD_CATEGORY + " TEXT NOT NULL, "
                + COL_PROD_ACTIVE + " INTEGER NOT NULL, "
                + COL_PROD_DESCRIPTION + " TEXT)";

        db.execSQL(createUsersTable);
        db.execSQL(createProductsTable);

        // Chèn tài khoản mẫu và 3 sản phẩm mẫu khi khởi tạo database lần đầu
        insertUserInternal(db, "admin", "123456");
        insertProductInternal(db, new Product("Chuột không dây", 150000, 20, "Điện tử", true, "Chuột không dây kết nối 2.4GHz nhỏ gọn"));
        insertProductInternal(db, new Product("Bàn phím cơ", 450000, 3, "Điện tử", true, "Bàn phím cơ switch xanh, có đèn nền"));
        insertProductInternal(db, new Product("Sổ tay A5", 25000, 40, "Văn phòng phẩm", true, "Sổ tay bìa cứng A5, 200 trang"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    /**
     * Kiểm tra thông tin đăng nhập bằng câu truy vấn có tham số trên bảng users.
     */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_USERS,
                    new String[]{COL_USER_ID},
                    COL_USERNAME + " = ? AND " + COL_PASSWORD + " = ?",
                    new String[]{username, password},
                    null, null, null
            );
            return cursor.moveToFirst();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Thêm tài khoản mới vào bảng users.
     */
    public long insertUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();
        return insertUserInternal(db, username, password);
    }

    private long insertUserInternal(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    /**
     * Thêm sản phẩm mới vào bảng products và trả về id vừa tạo.
     */
    public long insertProduct(Product p) {
        SQLiteDatabase db = getWritableDatabase();
        return insertProductInternal(db, p);
    }

    private long insertProductInternal(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COL_PROD_NAME, p.getName());
        values.put(COL_PROD_PRICE, p.getPrice());
        values.put(COL_PROD_QUANTITY, p.getQuantity());
        values.put(COL_PROD_CATEGORY, p.getCategory());
        values.put(COL_PROD_ACTIVE, p.isActive() ? 1 : 0);
        values.put(COL_PROD_DESCRIPTION, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    /**
     * Lấy toàn bộ danh sách sản phẩm trong kho sắp xếp theo thứ tự tăng dần của id.
     */
    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_PRODUCTS,
                    null,
                    null, null, null, null,
                    COL_PROD_ID + " ASC"
            );
            while (cursor.moveToNext()) {
                list.add(cursorToProduct(cursor));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * Truy vấn thông tin một sản phẩm theo id.
     */
    public Product getProductById(int id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(
                    TABLE_PRODUCTS,
                    null,
                    COL_PROD_ID + " = ?",
                    new String[]{String.valueOf(id)},
                    null, null, null
            );
            if (cursor.moveToFirst()) {
                return cursorToProduct(cursor);
            }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Cập nhật số lượng tồn kho của sản phẩm theo id.
     */
    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROD_QUANTITY, quantity);
        return db.update(
                TABLE_PRODUCTS,
                values,
                COL_PROD_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    /**
     * Xóa sản phẩm khỏi cơ sở dữ liệu theo id.
     */
    public int deleteProduct(int id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(
                TABLE_PRODUCTS,
                COL_PROD_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    /**
     * Tính tổng giá trị hàng hóa trong kho: SUM(price * quantity).
     */
    public long getTotalValue() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT COALESCE(SUM(" + COL_PROD_PRICE + " * " + COL_PROD_QUANTITY + "), 0) FROM " + TABLE_PRODUCTS,
                    null
            );
            if (cursor.moveToFirst()) {
                return cursor.getLong(0);
            }
            return 0L;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Đếm số lượng sản phẩm sắp hết hàng (quantity < 5).
     */
    public int countLowStock() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COL_PROD_QUANTITY + " < ?",
                    new String[]{String.valueOf(LOW_STOCK_THRESHOLD)}
            );
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Product cursorToProduct(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_NAME));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_PRICE));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_QUANTITY));
        String category = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_CATEGORY));
        boolean active = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PROD_ACTIVE)) == 1;
        String description = cursor.getString(cursor.getColumnIndexOrThrow(COL_PROD_DESCRIPTION));
        return new Product(id, name, price, quantity, category, active, description);
    }
}
