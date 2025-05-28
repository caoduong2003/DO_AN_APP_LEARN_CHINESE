package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.util.Constants;

import java.util.List;

public class UserManagementAdapter extends RecyclerView.Adapter<UserManagementAdapter.UserViewHolder> {
    private List<User> userList;
    private Context context;
    private OnUserActionListener listener;

    public interface OnUserActionListener {
        void onEditUser(User user);

        void onDeleteUser(User user);

        void onToggleStatus(User user);
    }

    public UserManagementAdapter(Context context, List<User> userList, OnUserActionListener listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_management, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        // Set user info
        holder.tvHoTen.setText(user.getHoTen() != null ? user.getHoTen() : "Chưa có tên");
        holder.tvTenDangNhap.setText("@" + user.getTenDangNhap());

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            holder.tvEmail.setText(user.getEmail());
            holder.tvEmail.setVisibility(View.VISIBLE);
        } else {
            holder.tvEmail.setVisibility(View.GONE);
        }

        if (user.getSoDienThoai() != null && !user.getSoDienThoai().isEmpty()) {
            holder.tvSoDienThoai.setText(user.getSoDienThoai());
            holder.tvSoDienThoai.setVisibility(View.VISIBLE);
        } else {
            holder.tvSoDienThoai.setVisibility(View.GONE);
        }

        // Set role - Sử dụng logic tương tự backend
        // Set status
        boolean trangThai = user.getTrangThai();
        holder.switchStatus.setChecked(trangThai);
        holder.switchStatus.setText(trangThai ? "Hoạt động" : "Đã khóa");

        // Set listeners
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditUser(user);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteUser(user);
            }
        });

        holder.switchStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleStatus(user);
            }
            // Reset switch to current state (will be updated after API call)
            holder.switchStatus.setChecked(trangThai);
        });

        // Set background color based on status
        if (trangThai) {
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.itemView.setAlpha(0.6f);
        }

        // Set listeners
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditUser(user);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteUser(user);
            }
        });

        holder.switchStatus.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToggleStatus(user);
            }
            // Reset switch to current state (will be updated after API call)
            holder.switchStatus.setChecked(trangThai);
        });

        // Set background color based on status
        if (trangThai) {
            holder.itemView.setAlpha(1.0f);
        } else {
            holder.itemView.setAlpha(0.6f);
        }
    }

    // Thêm method helper để convert vai trò
    private String getVaiTroText(Integer vaiTro) {
        if (vaiTro == null) return "Không xác định";
        switch (vaiTro) {
            case Constants.ROLE_ADMIN:
                return "Quản trị viên";
            case Constants.ROLE_TEACHER:
                return "Giáo viên";
            case Constants.ROLE_STUDENT:
                return "Học viên";
            default:
                return "Không xác định";
        }
    }

    @Override
    public int getItemCount() {
        return userList != null ? userList.size() : 0;
    }

    public void updateData(List<User> newList) {
        this.userList = newList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvHoTen, tvTenDangNhap, tvEmail, tvSoDienThoai, tvVaiTro;
        Switch switchStatus;
        ImageButton btnEdit, btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHoTen = itemView.findViewById(R.id.tvHoTen);
            tvTenDangNhap = itemView.findViewById(R.id.tvTenDangNhap);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvSoDienThoai = itemView.findViewById(R.id.tvSoDienThoai);
            tvVaiTro = itemView.findViewById(R.id.tvVaiTro);
            switchStatus = itemView.findViewById(R.id.switchStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}