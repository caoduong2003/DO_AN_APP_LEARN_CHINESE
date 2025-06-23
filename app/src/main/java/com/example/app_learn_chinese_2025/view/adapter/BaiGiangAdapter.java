package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;

import java.util.ArrayList;
import java.util.List;

public class BaiGiangAdapter extends RecyclerView.Adapter<BaiGiangAdapter.BaiGiangViewHolder> {
    private static final String TAG = "BaiGiangAdapter";

    private final Context context;
    private final List<BaiGiang> baiGiangList;
    private final boolean isTeacherMode;
    private OnBaiGiangItemClickListener itemClickListener; // For students
    private OnBaiGiangActionListener actionListener; // For teachers

    // Interface for student mode (click to view details)
    public interface OnBaiGiangItemClickListener {
        void onItemClick(BaiGiang baiGiang);

        void onEditClick(BaiGiang baiGiang);

        void onDeleteClick(BaiGiang baiGiang);
    }

    // Interface for teacher mode (click, edit, delete)
    public interface OnBaiGiangActionListener {
        void onItemClick(BaiGiang baiGiang);
        void onEditBaiGiang(BaiGiang baiGiang);
        void onDeleteBaiGiang(BaiGiang baiGiang);
    }

    public BaiGiangAdapter(Context context, List<BaiGiang> baiGiangList, boolean isTeacherMode, OnBaiGiangActionListener actionListener) {
        this.context = context;
        this.baiGiangList = baiGiangList != null ? baiGiangList : new ArrayList<>();
        this.isTeacherMode = isTeacherMode;
        this.actionListener = actionListener;
    }

    public BaiGiangAdapter(Context context, List<BaiGiang> baiGiangList, OnBaiGiangItemClickListener itemClickListener) {
        this.context = context;
        this.baiGiangList = baiGiangList != null ? baiGiangList : new ArrayList<>();
        this.isTeacherMode = false;
        this.itemClickListener = itemClickListener;
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

        holder.tvTitle.setText(baiGiang.getTieuDe() != null ? baiGiang.getTieuDe() : "Không có tiêu đề");
        holder.tvDescription.setText(baiGiang.getMoTa() != null ? baiGiang.getMoTa() : "Không có mô tả");

        // Hide teacher-specific buttons in student mode
        if (isTeacherMode) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnEdit.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditBaiGiang(baiGiang);
                }
            });
            holder.btnDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteBaiGiang(baiGiang);
                }
            });
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        // Handle item click for both modes
        holder.itemView.setOnClickListener(v -> {
            if (isTeacherMode && actionListener != null) {
                actionListener.onItemClick(baiGiang);
            } else if (!isTeacherMode && itemClickListener != null) {
                itemClickListener.onItemClick(baiGiang);
            }
        });
    }

    @Override
    public int getItemCount() {
        return baiGiangList.size();
    }

    public void updateData(List<BaiGiang> newBaiGiangList) {
        this.baiGiangList.clear();
        if (newBaiGiangList != null) {
            this.baiGiangList.addAll(newBaiGiangList);
        }
        notifyDataSetChanged();
    }

    static class BaiGiangViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        ImageView ivThumbnail;
        Button btnEdit, btnDelete;

        public BaiGiangViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}