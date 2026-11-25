package com.example.warehouseapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    private final List<Product> products;
    private final OnProductClickListener listener;

    public ProductAdapter(List<Product> products, OnProductClickListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAvatar;
        private final TextView tvName;
        private final TextView tvQuantity;
        private final TextView tvPrice;
        private final TextView tvCategory;
        private final TextView tvLowStockBadge;
        private final TextView tvInactiveBadge;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatar = itemView.findViewById(R.id.tvItemAvatar);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvLowStockBadge = itemView.findViewById(R.id.tvLowStockBadge);
            tvInactiveBadge = itemView.findViewById(R.id.tvInactiveBadge);
        }

        void bind(Product product, OnProductClickListener listener) {
            String name = product.getName() != null ? product.getName().trim() : "";
            tvAvatar.setText(name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase(Locale.getDefault()));
            tvName.setText(name);
            tvQuantity.setText("SL: " + product.getQuantity());
            tvPrice.setText("Giá bán: " + CurrencyUtils.formatVnd(product.getPrice()));
            tvCategory.setText(product.getCategory());

            boolean isLowStock = product.getQuantity() < DatabaseHelper.LOW_STOCK_THRESHOLD;
            int qtyColor = ContextCompat.getColor(
                    itemView.getContext(),
                    isLowStock ? R.color.danger_red : R.color.text_primary
            );
            tvQuantity.setTextColor(qtyColor);
            tvLowStockBadge.setVisibility(isLowStock ? View.VISIBLE : View.GONE);
            tvInactiveBadge.setVisibility(product.isActive() ? View.GONE : View.VISIBLE);

            itemView.setOnClickListener(v -> listener.onProductClick(product));
        }
    }
}
