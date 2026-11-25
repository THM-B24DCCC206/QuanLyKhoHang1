package com.example.warehouseapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.appbar.MaterialToolbar;

/**
 * Màn hình chi tiết sản phẩm, cho phép nhập hàng, bán hàng hoặc xóa khỏi kho.
 */
public class ProductDetailActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private int productId;
    private Product currentProduct;

    private TextView tvName;
    private TextView tvCategory;
    private TextView tvStatus;
    private TextView tvPrice;
    private TextView tvQuantity;
    private TextView tvLowStock;
    private TextView tvTotalValue;
    private TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        dbHelper = new DatabaseHelper(this);
        productId = getIntent().getIntExtra(MainActivity.EXTRA_PRODUCT_ID, -1);
        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName = findViewById(R.id.tvDetailName);
        tvCategory = findViewById(R.id.tvDetailCategory);
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvPrice = findViewById(R.id.tvDetailPrice);
        tvQuantity = findViewById(R.id.tvDetailQuantity);
        tvLowStock = findViewById(R.id.tvDetailLowStock);
        tvTotalValue = findViewById(R.id.tvDetailTotalValue);
        tvDescription = findViewById(R.id.tvDetailDescription);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        Button btnRestock = findViewById(R.id.btnRestock);
        Button btnSell = findViewById(R.id.btnSell);
        Button btnBack = findViewById(R.id.btnBack);
        Button btnDelete = findViewById(R.id.btnDelete);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());
        btnRestock.setOnClickListener(v -> updateStock(1));
        btnSell.setOnClickListener(v -> sellOne());
        btnDelete.setOnClickListener(v -> confirmDelete());

        loadProduct();
    }

    private void loadProduct() {
        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm không còn tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvName.setText(currentProduct.getName());
        tvCategory.setText(currentProduct.getCategory());
        tvStatus.setText(currentProduct.isActive() ? R.string.label_active : R.string.badge_inactive);
        tvPrice.setText(CurrencyUtils.formatVnd(currentProduct.getPrice()));

        int quantity = currentProduct.getQuantity();
        tvQuantity.setText(String.valueOf(quantity));
        boolean isLowStock = quantity < DatabaseHelper.LOW_STOCK_THRESHOLD;
        tvQuantity.setTextColor(ContextCompat.getColor(
                this,
                isLowStock ? R.color.danger_red : R.color.text_primary
        ));
        tvLowStock.setVisibility(isLowStock ? View.VISIBLE : View.GONE);

        long stockValue = (long) currentProduct.getPrice() * quantity;
        tvTotalValue.setText(CurrencyUtils.formatVnd(stockValue));

        String desc = currentProduct.getDescription();
        tvDescription.setText(TextUtils.isEmpty(desc) ? getString(R.string.no_description) : desc);
    }

    private void updateStock(int delta) {
        if (currentProduct == null) {
            return;
        }
        int newQty = currentProduct.getQuantity() + delta;
        dbHelper.updateQuantity(productId, newQty);
        loadProduct();
    }

    private void sellOne() {
        if (currentProduct == null) {
            return;
        }
        // Chặn giảm khi tồn kho đã về 0
        if (currentProduct.getQuantity() <= 0) {
            Toast.makeText(this, R.string.msg_out_of_stock, Toast.LENGTH_SHORT).show();
            return;
        }
        updateStock(-1);
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa sản phẩm này khỏi kho?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteProduct(productId);
                    Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }
}
