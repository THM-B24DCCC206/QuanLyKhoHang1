package com.example.warehouseapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputLayout;

public class AddProductActivity extends AppCompatActivity {

    private TextInputLayout tilName;
    private TextInputLayout tilPrice;
    private TextInputLayout tilQuantity;
    private EditText edtName;
    private EditText edtPrice;
    private EditText edtQuantity;
    private Spinner spinnerCategory;
    private SwitchMaterial switchActive;
    private EditText edtDescription;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        dbHelper = new DatabaseHelper(this);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        tilName = findViewById(R.id.tilProductName);
        tilPrice = findViewById(R.id.tilPrice);
        tilQuantity = findViewById(R.id.tilQuantity);
        edtName = findViewById(R.id.edtProductName);
        edtPrice = findViewById(R.id.edtPrice);
        edtQuantity = findViewById(R.id.edtQuantity);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        switchActive = findViewById(R.id.switchActive);
        edtDescription = findViewById(R.id.edtDescription);
        Button btnSave = findViewById(R.id.btnSaveProduct);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.product_categories,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        toolbar.setNavigationOnClickListener(v -> finish());
        btnSave.setOnClickListener(v -> saveProduct());
    }

    private void saveProduct() {
        tilName.setError(null);
        tilPrice.setError(null);
        tilQuantity.setError(null);

        String name = edtName.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        String quantityStr = edtQuantity.getText().toString().trim();
        String category = spinnerCategory.getSelectedItem().toString();
        boolean active = switchActive.isChecked();
        String description = edtDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            tilName.setError("Tên sản phẩm không được để trống");
            edtName.requestFocus();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            tilPrice.setError("Giá bán phải là số hợp lệ");
            edtPrice.requestFocus();
            return;
        }
        if (price <= 0) {
            tilPrice.setError("Giá bán phải lớn hơn 0");
            edtPrice.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            tilQuantity.setError("Số lượng phải là số nguyên hợp lệ");
            edtQuantity.requestFocus();
            return;
        }
        if (quantity < 0) {
            tilQuantity.setError("Số lượng không được âm");
            edtQuantity.requestFocus();
            return;
        }

        Product product = new Product(name, price, quantity, category, active, description);
        long insertedId = dbHelper.insertProduct(product);
        if (insertedId > 0) {
            Toast.makeText(this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lưu sản phẩm thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}
