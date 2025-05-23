package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.view.activity.BaiGiangDetailActivity;
import com.example.app_learn_chinese_2025.view.activity.EditBaiGiangActivity;

import java.util.List;

public class BaiGiangAdapter extends RecyclerView.Adapter<BaiGiangAdapter.BaiGiangViewHolder> {
    private List<BaiGiang> baiGiangList;
    private Context context;
    private boolean isTeacher; // Để xác định có hiển thị nút sửa/xóa hay không
    private OnBaiGiangActionListener listener;

    public interface OnBaiGiangActionListener {
        void onEditClick(BaiGiang baiGiang);
        void onDeleteClick(BaiGiang baiGiang);
        void onItemClick(BaiGiang baiGiang);
    }

    public BaiGiangAdapter(Context context, List<BaiGiang> baiGiangList, boolean isTeacher, OnBaiGiangActionListener listener) {
        this.context = context;
        this.baiGiangList = baiGiangList;
        this.isTeacher = isTeacher;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaiGiangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bai_giang, parent, false);
        return new BaiGiangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaiGiangViewHolder holder, int position) {
        BaiGiang baiGiang = baiGiangList.get(position);

        holder.tvTieuDe.setText(baiGiang.getTieuDe());

        // Hiển thị cấp độ HSK
        if (baiGiang.getCapDoHSK() != null) {
            holder.tvCapDoHSK.setText("HSK " + baiGiang.getCapDoHSK().getCapDo());
        } else {
            holder.tvCapDoHSK.setText("HSK ?");
        }

        // Hiển thị loại bài giảng
        if (baiGiang.getLoaiBaiGiang() != null) {
            holder.tvLoaiBaiGiang.setText(baiGiang.getLoaiBaiGiang().getTenLoai());
        } else {
            holder.tvLoaiBaiGiang.setText("Chưa phân loại");
        }

        // Hiển thị lượt xem
        holder.tvLuotXem.setText(baiGiang.getLuotXem() + " lượt xem");

        // Hiển thị thời lượng
        holder.tvThoiLuong.setText(baiGiang.getThoiLuong() + " phút");

        // Tải hình ảnh bằng Glide
        if (baiGiang.getHinhAnh() != null && !baiGiang.getHinhAnh().isEmpty()) {
            String imageUrl = Constants.BASE_URL + baiGiang.getHinhAnh();
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.ivBaiGiang);
        } else {
            holder.ivBaiGiang.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Hiển thị/ẩn các nút sửa/xóa dựa trên vai trò người dùng
        if (isTeacher) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        // Xử lý sự kiện khi click vào item
        holder.itemView.setOnClickListener(v -> {
            listener.onItemClick(baiGiang);
        });

        // Xử lý sự kiện khi click vào nút sửa
        holder.btnEdit.setOnClickListener(v -> {
            listener.onEditClick(baiGiang);
        });

        // Xử lý sự kiện khi click vào nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            listener.onDeleteClick(baiGiang);
        });
    }

    @Override
    public int getItemCount() {
        return baiGiangList != null ? baiGiangList.size() : 0;
    }

    public void updateData(List<BaiGiang> newList) {
        this.baiGiangList = newList;
        notifyDataSetChanged();
    }

    public static class BaiGiangViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBaiGiang;
        TextView tvTieuDe, tvCapDoHSK, tvLoaiBaiGiang, tvLuotXem, tvThoiLuong;
        ImageButton btnEdit, btnDelete;

        public BaiGiangViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBaiGiang = itemView.findViewById(R.id.ivBaiGiang);
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvCapDoHSK = itemView.findViewById(R.id.tvCapDoHSK);
            tvLoaiBaiGiang = itemView.findViewById(R.id.tvLoaiBaiGiang);
            tvLuotXem = itemView.findViewById(R.id.tvLuotXem);
            tvThoiLuong = itemView.findViewById(R.id.tvThoiLuong);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}