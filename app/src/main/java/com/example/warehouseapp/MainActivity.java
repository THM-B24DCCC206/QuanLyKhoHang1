package com.example.warehouseapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Màn hình chính hiển thị thống kê kho và danh sách sản phẩm từ SQLite.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";

    private DatabaseHelper dbHelper;
    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStockCount;
    private TextView tvEmpty;
    private TextView tvTapHint;
    private RecyclerView rvProducts;

    private final ArrayList<Product> productList = new ArrayList<>();
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);
        tvEmpty = findViewById(R.id.tvEmpty);
        tvTapHint = findViewById(R.id.tvTapHint);
        rvProducts = findViewById(R.id.rvProducts);
        Button btnAddProduct = findViewById(R.id.btnAddProduct);

        adapter = new ProductAdapter(productList, product -> {
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, product.getId());
            startActivity(intent);
        });
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        rvProducts.setAdapter(adapter);

        btnAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại danh sách và thống kê mỗi khi quay lại từ màn hình thêm hoặc chi tiết
        loadProductsAndStats();
    }

    private void loadProductsAndStats() {
        productList.clear();
        productList.addAll(dbHelper.getAllProducts());
        adapter.notifyDataSetChanged();

        boolean isEmpty = productList.isEmpty();
        tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        rvProducts.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvTapHint.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        tvTotalProducts.setText(String.valueOf(productList.size()));
        tvTotalValue.setText(CurrencyUtils.formatVnd(dbHelper.getTotalValue()));
        tvLowStockCount.setText(String.valueOf(dbHelper.countLowStock()));
    }
}
