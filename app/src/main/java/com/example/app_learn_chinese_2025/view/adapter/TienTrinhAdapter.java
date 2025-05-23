package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.TienTrinh;
import com.example.app_learn_chinese_2025.view.activity.BaiGiangDetailActivity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TienTrinhAdapter extends RecyclerView.Adapter<TienTrinhAdapter.TienTrinhViewHolder> {
    private List<TienTrinh> tienTrinhList;
    private Context context;
    private SimpleDateFormat dateFormat;

    public TienTrinhAdapter(Context context, List<TienTrinh> tienTrinhList) {
        this.context = context;
        this.tienTrinhList = tienTrinhList;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public TienTrinhViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tien_trinh, parent, false);
        return new TienTrinhViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TienTrinhViewHolder holder, int position) {
        TienTrinh tienTrinh = tienTrinhList.get(position);

        // Set values
        if (tienTrinh.getBaiGiang() != null) {
            holder.tvTenBaiGiang.setText(tienTrinh.getBaiGiang().getTieuDe());
        } else {
            holder.tvTenBaiGiang.setText("Bài giảng không xác định");
        }

        // Format date
        if (tienTrinh.getNgayCapNhat() != null) {
            holder.tvNgayCapNhat.setText("Cập nhật: " + dateFormat.format(tienTrinh.getNgayCapNhat()));
        } else {
            holder.tvNgayCapNhat.setText("Chưa cập nhật");
        }

        // Set progress
        holder.progressBar.setProgress(tienTrinh.getTienDo());
        holder.tvTienDo.setText(tienTrinh.getTienDo() + "%");

        // Set completion status
        holder.tvTrangThai.setText(tienTrinh.isDaHoanThanh() ? "Đã hoàn thành" : "Đang học");
        holder.tvTrangThai.setTextColor(context.getResources().getColor(
                tienTrinh.isDaHoanThanh() ? android.R.color.holo_green_dark : android.R.color.holo_blue_dark));

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (tienTrinh.getBaiGiang() != null) {
                Intent intent = new Intent(context, BaiGiangDetailActivity.class);
                intent.putExtra("BAI_GIANG_ID", tienTrinh.getBaiGiang().getID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tienTrinhList != null ? tienTrinhList.size() : 0;
    }

    public void updateData(List<TienTrinh> newList) {
        this.tienTrinhList = newList;
        notifyDataSetChanged();
    }

    public static class TienTrinhViewHolder extends RecyclerView.ViewHolder {
        TextView tvTenBaiGiang, tvNgayCapNhat, tvTienDo, tvTrangThai;
        ProgressBar progressBar;

        public TienTrinhViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenBaiGiang = itemView.findViewById(R.id.tvTenBaiGiang);
            tvNgayCapNhat = itemView.findViewById(R.id.tvNgayCapNhat);
            tvTienDo = itemView.findViewById(R.id.tvTienDo);
            tvTrangThai = itemView.findViewById(R.id.tvTrangThai);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}