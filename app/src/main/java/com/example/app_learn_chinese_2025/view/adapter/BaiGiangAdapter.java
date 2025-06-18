package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.util.Constants;

import java.util.List;

public class BaiGiangAdapter extends RecyclerView.Adapter<BaiGiangAdapter.ViewHolder> {

    private Context context;
    private List<BaiGiang> baiGiangList;
    private OnBaiGiangItemClickListener listener;
    private boolean isTeacher; // Để phân biệt quyền giáo viên và học sinh

    public interface OnBaiGiangItemClickListener {
        void onItemClick(BaiGiang baiGiang);
        void onEditClick(BaiGiang baiGiang);
        void onDeleteClick(BaiGiang baiGiang);
    }

    public BaiGiangAdapter(Context context, List<BaiGiang> baiGiangList, boolean isTeacher, OnBaiGiangItemClickListener listener) {
        this.context = context;
        this.baiGiangList = baiGiangList;
        this.isTeacher = isTeacher;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bai_giang, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BaiGiang baiGiang = baiGiangList.get(position);

        // DEBUG: Log thông tin bài giảng
        Log.d("ADAPTER_DEBUG", "=== Bài giảng " + position + " ===");
        Log.d("ADAPTER_DEBUG", "ID: " + baiGiang.getID());
        Log.d("ADAPTER_DEBUG", "Tiêu đề: " + baiGiang.getTieuDe());
        Log.d("ADAPTER_DEBUG", "Video URL: " + baiGiang.getVideoURL());
        Log.d("ADAPTER_DEBUG", "Image URL: " + baiGiang.getHinhAnh());

        // Hiển thị tiêu đề
        holder.tvTieuDe.setText(baiGiang.getTieuDe());

        // Hiển thị mô tả
        if (baiGiang.getMoTa() != null && !baiGiang.getMoTa().isEmpty()) {
            holder.tvMoTa.setText(baiGiang.getMoTa());
            holder.tvMoTa.setVisibility(View.VISIBLE);
        } else {
            holder.tvMoTa.setVisibility(View.GONE);
        }

        // Hiển thị cấp độ HSK
        if (baiGiang.getCapDoHSK() != null) {
            holder.tvCapDoHSK.setText(baiGiang.getCapDoHSK().getTenCapDo());
        } else {
            holder.tvCapDoHSK.setText("Chưa phân cấp");
        }

        // Hiển thị chủ đề
        if (baiGiang.getChuDe() != null) {
            holder.tvChuDe.setText(baiGiang.getChuDe().getTenChuDe());
        } else {
            holder.tvChuDe.setText("Chưa có chủ đề");
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
        holder.tvThoiLuong.setText(formatThoiLuong(baiGiang.getThoiLuong()));

        // Tải hình ảnh bằng Glide - SỬA LẠI
        if (baiGiang.getHinhAnh() != null && !baiGiang.getHinhAnh().isEmpty()) {
            String imageUrl = buildCorrectFileUrl(baiGiang.getHinhAnh());
            Log.d("ADAPTER_DEBUG", "Loading image: " + imageUrl);

            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Drawable> target, boolean isFirstResource) {
                            Log.e("ADAPTER_DEBUG", "Failed to load image: " + imageUrl);
                            if (e != null) {
                                Log.e("ADAPTER_DEBUG", "Glide error: " + e.getMessage());
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model,
                                                       Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            Log.d("ADAPTER_DEBUG", "Image loaded successfully: " + imageUrl);
                            return false;
                        }
                    })
                    .into(holder.ivBaiGiang);
        } else {
            Log.d("ADAPTER_DEBUG", "No image URL available");
            holder.ivBaiGiang.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Hiển thị icon video nếu có video
        if (baiGiang.getVideoURL() != null && !baiGiang.getVideoURL().isEmpty()) {
            Log.d("ADAPTER_DEBUG", "Bài giảng có video");
            // Hiển thị icon play hoặc indicator video
            if (holder.ivVideoIndicator != null) {
                holder.ivVideoIndicator.setVisibility(View.VISIBLE);
            }
        } else {
            Log.d("ADAPTER_DEBUG", "Bài giảng không có video");
            if (holder.ivVideoIndicator != null) {
                holder.ivVideoIndicator.setVisibility(View.GONE);
            }
        }

        // Hiển thị badge bài giảng gói (premium)
        if (baiGiang.isLaBaiGiangGoi()) {
            if (holder.tvPremiumBadge != null) {
                holder.tvPremiumBadge.setVisibility(View.VISIBLE);
                holder.tvPremiumBadge.setText("PREMIUM");
            }
        } else {
            if (holder.tvPremiumBadge != null) {
                holder.tvPremiumBadge.setVisibility(View.GONE);
            }
        }

        // Hiển thị trạng thái bài giảng
        if (baiGiang.isTrangThai()) {
            holder.itemView.setAlpha(1.0f);
            if (holder.tvStatus != null) {
                holder.tvStatus.setVisibility(View.GONE);
            }
        } else {
            holder.itemView.setAlpha(0.5f);
            if (holder.tvStatus != null) {
                holder.tvStatus.setVisibility(View.VISIBLE);
                holder.tvStatus.setText("Không hoạt động");
            }
        }

        // QUAN TRỌNG: Hiển thị/ẩn các nút sửa/xóa dựa trên vai trò người dùng
        if (isTeacher) {
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        // QUAN TRỌNG: Xử lý sự kiện khi click vào item - học sinh có thể xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            Log.d("ADAPTER_DEBUG", "Clicked on bài giảng: " + baiGiang.getTieuDe());
            if (listener != null) {
                listener.onItemClick(baiGiang);
            }
        });

        // Xử lý sự kiện khi click vào nút sửa (chỉ cho giáo viên)
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null && isTeacher) {
                listener.onEditClick(baiGiang);
            }
        });

        // Xử lý sự kiện khi click vào nút xóa (chỉ cho giáo viên)
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null && isTeacher) {
                listener.onDeleteClick(baiGiang);
            }
        });

        // Click vào thumbnail để xem chi tiết (cả học sinh và giáo viên)
        holder.ivBaiGiang.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(baiGiang);
            }
        });
    }

    @Override
    public int getItemCount() {
        return baiGiangList != null ? baiGiangList.size() : 0;
    }

    // Method để cập nhật data
    public void updateData(List<BaiGiang> newBaiGiangList) {
        this.baiGiangList = newBaiGiangList;
        notifyDataSetChanged();
    }

    // Method để thêm bài giảng mới
    public void addBaiGiang(BaiGiang baiGiang) {
        if (baiGiangList != null) {
            baiGiangList.add(0, baiGiang); // Thêm vào đầu list
            notifyItemInserted(0);
        }
    }

    // Method để xóa bài giảng
    public void removeBaiGiang(int position) {
        if (baiGiangList != null && position >= 0 && position < baiGiangList.size()) {
            baiGiangList.remove(position);
            notifyItemRemoved(position);
        }
    }

    // Method để cập nhật một bài giảng
    public void updateBaiGiang(int position, BaiGiang updatedBaiGiang) {
        if (baiGiangList != null && position >= 0 && position < baiGiangList.size()) {
            baiGiangList.set(position, updatedBaiGiang);
            notifyItemChanged(position);
        }
    }

    // Method helper để xây dựng URL file đúng
    private String buildCorrectFileUrl(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return null;
        }

        // Nếu đã là URL đầy đủ
        if (fileUrl.startsWith("http://") || fileUrl.startsWith("https://")) {
            return fileUrl;
        }

        // Lấy tên file từ đường dẫn
        String fileName = extractFileName(fileUrl);

        // Xây dựng URL đúng theo cách server serve file
        return Constants.BASE_URL + "api/files/" + fileName;
    }

    private String extractFileName(String filePath) {
        if (filePath.startsWith("/uploads/")) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        } else if (filePath.startsWith("uploads/")) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        } else if (filePath.contains("/")) {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        } else {
            return filePath;
        }
    }

    private String formatThoiLuong(int phut) {
        if (phut < 60) {
            return phut + " phút";
        } else {
            int gio = phut / 60;
            int phutConLai = phut % 60;
            if (phutConLai == 0) {
                return gio + " giờ";
            } else {
                return gio + " giờ " + phutConLai + " phút";
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTieuDe, tvMoTa, tvCapDoHSK, tvChuDe, tvLoaiBaiGiang, tvLuotXem, tvThoiLuong;
        TextView tvPremiumBadge, tvStatus;
        ImageView ivBaiGiang, ivVideoIndicator;
        Button btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Khởi tạo các view - đảm bảo ID match với layout
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            tvCapDoHSK = itemView.findViewById(R.id.tvCapDoHSK);
            tvChuDe = itemView.findViewById(R.id.tvChuDe);
            tvLoaiBaiGiang = itemView.findViewById(R.id.tvLoaiBaiGiang);
            tvLuotXem = itemView.findViewById(R.id.tvLuotXem);
            tvThoiLuong = itemView.findViewById(R.id.tvThoiLuong);

            ivBaiGiang = itemView.findViewById(R.id.ivBaiGiang);

            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            // Các view optional - có thể null nếu không có trong layout
            tvPremiumBadge = itemView.findViewById(R.id.tvPremiumBadge);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            ivVideoIndicator = itemView.findViewById(R.id.ivVideoIndicator);

            // Debug log để kiểm tra view initialization
            Log.d("ADAPTER_INIT", "ViewHolder initialized");
            Log.d("ADAPTER_INIT", "tvTieuDe: " + (tvTieuDe != null ? "OK" : "NULL"));
            Log.d("ADAPTER_INIT", "ivBaiGiang: " + (ivBaiGiang != null ? "OK" : "NULL"));
            Log.d("ADAPTER_INIT", "btnEdit: " + (btnEdit != null ? "OK" : "NULL"));
            Log.d("ADAPTER_INIT", "btnDelete: " + (btnDelete != null ? "OK" : "NULL"));
        }
    }

    // Method để filter danh sách bài giảng
    public void filterByLoaiBaiGiang(int loaiBaiGiangId) {
        // Implementation for filtering
        notifyDataSetChanged();
    }

    public void filterByCapDoHSK(int capDoHSKId) {
        // Implementation for filtering
        notifyDataSetChanged();
    }

    public void filterByChuDe(int chuDeId) {
        // Implementation for filtering
        notifyDataSetChanged();
    }

    // Method để search
    public void search(String keyword) {
        // Implementation for search functionality
        notifyDataSetChanged();
    }

    // Method để clear search/filter
    public void clearFilter() {
        notifyDataSetChanged();
    }

    // Method để set teacher mode
    public void setTeacherMode(boolean isTeacher) {
        this.isTeacher = isTeacher;
        notifyDataSetChanged();
    }

    // Method để get item by position
    public BaiGiang getItem(int position) {
        if (baiGiangList != null && position >= 0 && position < baiGiangList.size()) {
            return baiGiangList.get(position);
        }
        return null;
    }

    // Method để get position of item
    public int getPositionOfItem(BaiGiang baiGiang) {
        if (baiGiangList != null && baiGiang != null) {
            for (int i = 0; i < baiGiangList.size(); i++) {
                if (baiGiangList.get(i).getID() == baiGiang.getID()) {
                    return i;
                }
            }
        }
        return -1;
    }

    // Method để kiểm tra list có empty không
    public boolean isEmpty() {
        return baiGiangList == null || baiGiangList.isEmpty();
    }

    // Method để get total count
    public int getTotalCount() {
        return baiGiangList != null ? baiGiangList.size() : 0;
    }
}