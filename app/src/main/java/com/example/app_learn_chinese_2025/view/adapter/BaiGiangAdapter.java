package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_learn_chinese_2025.util.Constants;
import com.squareup.picasso.Picasso;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;

import java.util.ArrayList;
import java.util.List;

public class BaiGiangAdapter extends RecyclerView.Adapter<BaiGiangAdapter.BaiGiangViewHolder> {
    private static final String TAG = "BaiGiangAdapter";

    private final Context context;
    private List<BaiGiang> baiGiangList;
    private final boolean isTeacherMode;
    private OnBaiGiangItemClickListener itemClickListener; // For students
    private OnBaiGiangActionListener actionListener; // For teachers

    // Interface for student mode (click to view details)
    public interface OnBaiGiangItemClickListener {
        void onItemClick(BaiGiang baiGiang);

        void onEditClick(BaiGiang baiGiang);

        void onDeleteClick(BaiGiang baiGiang);

        void onPlayVideo(BaiGiang baiGiang);

        void onPlayAudio(BaiGiang baiGiang);
    }

    // Interface for teacher mode (click, edit, delete)
    public interface OnBaiGiangActionListener {
        void onItemClick(BaiGiang baiGiang);

        void onEditBaiGiang(BaiGiang baiGiang);

        void onDeleteBaiGiang(BaiGiang baiGiang);

        void onPlayVideo(BaiGiang baiGiang);

        void onPlayAudio(BaiGiang baiGiang);
    }

    // Constructor for teacher mode
    public BaiGiangAdapter(Context context, List<BaiGiang> baiGiangList, boolean isTeacherMode, OnBaiGiangActionListener actionListener) {
        this.context = context;
        this.baiGiangList = baiGiangList != null ? baiGiangList : new ArrayList<>();
        this.isTeacherMode = isTeacherMode;
        this.actionListener = actionListener;
        Log.d(TAG, "🏗️ BaiGiangAdapter created for TEACHER mode with " + this.baiGiangList.size() + " items");
    }

    // Constructor for student mode
    public BaiGiangAdapter(Context context, List<BaiGiang> baiGiangList, OnBaiGiangItemClickListener itemClickListener) {
        this.context = context;
        this.baiGiangList = baiGiangList != null ? baiGiangList : new ArrayList<>();
        this.isTeacherMode = false;
        this.itemClickListener = itemClickListener;
        Log.d(TAG, "🏗️ BaiGiangAdapter created for STUDENT mode with " + this.baiGiangList.size() + " items");
    }

    @NonNull
    @Override
    public BaiGiangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "🏗️ onCreateViewHolder called");
        View view = LayoutInflater.from(context).inflate(R.layout.item_bai_giang, parent, false);
        return new BaiGiangViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaiGiangViewHolder holder, int position) {
        Log.d(TAG, "📄 onBindViewHolder - position: " + position + " / " + baiGiangList.size());

        if (position >= baiGiangList.size()) {
            Log.e(TAG, "❌ Position " + position + " >= list size " + baiGiangList.size());
            return;
        }

        BaiGiang baiGiang = baiGiangList.get(position);
        Log.d(TAG, "📖 Binding lesson: " + baiGiang.getTieuDe());

        // ✅ SET BASIC INFO
        String title = baiGiang.getTieuDe() != null ? baiGiang.getTieuDe() : "Không có tiêu đề";
        String description = baiGiang.getMoTa() != null ? baiGiang.getMoTa() : "Không có mô tả";

        holder.tvTitle.setText(title);
        holder.tvDescription.setText(description);

        // ✅ SET HSK LEVEL
        if (holder.tvHSKLevel != null && baiGiang.getCapDoHSK() != null) {
            holder.tvHSKLevel.setText(baiGiang.getCapDoHSK().getTenCapDo());
            holder.tvHSKLevel.setVisibility(View.VISIBLE);
        } else if (holder.tvHSKLevel != null) {
            holder.tvHSKLevel.setVisibility(View.GONE);
        }

        // ✅ SET DURATION
        if (holder.tvDuration != null) {
            holder.tvDuration.setText(baiGiang.getThoiLuong() + " phút");
            holder.tvDuration.setVisibility(View.VISIBLE);
        }

        // ✅ SET VIEWS COUNT
        if (holder.tvViews != null) {
            holder.tvViews.setText(baiGiang.getLuotXem() + " lượt xem");
            holder.tvViews.setVisibility(View.VISIBLE);
        }

        // ✅ SET LESSON TYPE
        if (holder.tvLessonType != null && baiGiang.getLoaiBaiGiang() != null) {
            holder.tvLessonType.setText(baiGiang.getLoaiBaiGiang().getTenLoai());
            holder.tvLessonType.setVisibility(View.VISIBLE);
        } else if (holder.tvLessonType != null) {
            holder.tvLessonType.setVisibility(View.GONE);
        }

        // ✅ SET TOPIC
        if (holder.tvTopic != null && baiGiang.getChuDe() != null) {
            holder.tvTopic.setText(baiGiang.getChuDe().getTenChuDe());
            holder.tvTopic.setVisibility(View.VISIBLE);
        } else if (holder.tvTopic != null) {
            holder.tvTopic.setVisibility(View.GONE);
        }

        // ✅ SET PREMIUM BADGE
        if (holder.tvPremium != null) {
            if (baiGiang.isLaBaiGiangGoi()) {
                holder.tvPremium.setVisibility(View.VISIBLE);
                holder.tvPremium.setText("PREMIUM");
            } else {
                holder.tvPremium.setVisibility(View.GONE);
            }
        }

        // ✅ LOAD THUMBNAIL IMAGE with Picasso
        if (holder.ivThumbnail != null && baiGiang.getHinhAnh() != null && !baiGiang.getHinhAnh().isEmpty()) {
            String imageUrl = Constants.getBaseUrl() + baiGiang.getHinhAnh();
            Log.d(TAG, "🖼️ Loading image: " + imageUrl);

            Picasso.get()
                    .load(imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .fit()
                    .centerCrop()
                    .into(holder.ivThumbnail);
        } else if (holder.ivThumbnail != null) {
            holder.ivThumbnail.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // ✅ SET VIDEO/AUDIO INDICATORS
        boolean hasVideo = baiGiang.getVideoURL() != null && !baiGiang.getVideoURL().isEmpty();

        // Show video icon if has video
        if (holder.ivVideoIcon != null) {
            if (hasVideo) {
                holder.ivVideoIcon.setVisibility(View.VISIBLE);
                Log.d(TAG, "📹 Video available: " + baiGiang.getVideoURL());
            } else {
                holder.ivVideoIcon.setVisibility(View.GONE);
            }
        }


        // ✅ SETUP MEDIA BUTTONS
        if (holder.btnPlayVideo != null) {
            if (hasVideo) {
                holder.btnPlayVideo.setVisibility(View.VISIBLE);
                holder.btnPlayVideo.setOnClickListener(v -> {
                    Log.d(TAG, "📹 Play video clicked: " + baiGiang.getVideoURL());
                    if (isTeacherMode && actionListener != null) {
                        actionListener.onPlayVideo(baiGiang);
                    } else if (!isTeacherMode && itemClickListener != null) {
                        itemClickListener.onPlayVideo(baiGiang);
                    }
                });
            } else {
                holder.btnPlayVideo.setVisibility(View.GONE);
            }
        }


        // ✅ HANDLE TEACHER/STUDENT MODE
        if (isTeacherMode) {
            Log.d(TAG, "👨‍🏫 Teacher mode - showing edit/delete buttons");
            if (holder.btnEdit != null) {
                holder.btnEdit.setVisibility(View.VISIBLE);
                holder.btnEdit.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onEditBaiGiang(baiGiang);
                    }
                });
            }
            if (holder.btnDelete != null) {
                holder.btnDelete.setVisibility(View.VISIBLE);
                holder.btnDelete.setOnClickListener(v -> {
                    if (actionListener != null) {
                        actionListener.onDeleteBaiGiang(baiGiang);
                    }
                });
            }
            if (holder.layoutActionButtons != null) {
                holder.layoutActionButtons.setVisibility(View.VISIBLE);
            }
        } else {
            Log.d(TAG, "👨‍🎓 Student mode - hiding edit/delete buttons");
            if (holder.btnEdit != null) holder.btnEdit.setVisibility(View.GONE);
            if (holder.btnDelete != null) holder.btnDelete.setVisibility(View.GONE);
            if (holder.layoutActionButtons != null) {
                holder.layoutActionButtons.setVisibility(View.GONE);
            }
        }

        // ✅ HANDLE ITEM CLICK
        holder.itemView.setOnClickListener(v -> {
            Log.d(TAG, "🖱️ Item clicked: " + baiGiang.getTieuDe());
            if (isTeacherMode && actionListener != null) {
                Log.d(TAG, "👨‍🏫 Teacher click");
                actionListener.onItemClick(baiGiang);
            } else if (!isTeacherMode && itemClickListener != null) {
                Log.d(TAG, "👨‍🎓 Student click");
                itemClickListener.onItemClick(baiGiang);
            } else {
                Log.w(TAG, "⚠️ No click listener set!");
            }
        });

        Log.d(TAG, "✅ onBindViewHolder completed for position " + position);


    }

    @Override
    public int getItemCount() {
        int count = baiGiangList.size();
        Log.d(TAG, "📊 getItemCount: " + count);
        return count;
    }

//    public void updateData(List<BaiGiang> newBaiGiangList) {
//        Log.d(TAG, "🔄 updateData called");
//        Log.d(TAG, "📊 Current list size: " + this.baiGiangList.size());
//        Log.d(TAG, "📊 New list size: " + (newBaiGiangList != null ? newBaiGiangList.size() : 0));
//
//        this.baiGiangList.clear();
//        if (newBaiGiangList != null) {
//            this.baiGiangList.addAll(newBaiGiangList);
//            Log.d(TAG, "✅ Added " + newBaiGiangList.size() + " items to adapter");
//
//            // Debug log first few items
//            for (int i = 0; i < Math.min(3, newBaiGiangList.size()); i++) {
//                BaiGiang bg = newBaiGiangList.get(i);
//                Log.d(TAG, "📝 Item " + i + ": " + bg.getTieuDe());
//            }
//        }
//
//        Log.d(TAG, "🔄 Calling notifyDataSetChanged()");
//        notifyDataSetChanged();
//        Log.d(TAG, "✅ updateData completed - final size: " + this.baiGiangList.size());
//    }
public void updateData(List<BaiGiang> newBaiGiangList) {
    Log.d(TAG, "=== 🔄 updateData FIXED VERSION ===");
    Log.d(TAG, "📊 Current list size: " + this.baiGiangList.size());
    Log.d(TAG, "📊 New list size: " + (newBaiGiangList != null ? newBaiGiangList.size() : "NULL"));

    // ✅ CRITICAL FIX: Null check trước
    if (newBaiGiangList == null) {
        Log.w(TAG, "⚠️ New list is NULL - clearing adapter");
        this.baiGiangList.clear();
        notifyDataSetChanged();
        Log.d(TAG, "✅ updateData completed - final size: " + this.baiGiangList.size());
        return;
    }

    // ✅ CRITICAL FIX: Debug reference check
    if (this.baiGiangList == newBaiGiangList) {
        Log.w(TAG, "⚠️ REFERENCE CONFLICT: Same list reference detected!");
        // Tạo copy mới để tránh reference conflict
        List<BaiGiang> tempList = new ArrayList<>(newBaiGiangList);
        this.baiGiangList.clear();
        this.baiGiangList.addAll(tempList);
    } else {
        // ✅ SAFE: Different references
        Log.d(TAG, "✅ Safe: Different list references");
        this.baiGiangList.clear();
        this.baiGiangList.addAll(newBaiGiangList);
    }

    Log.d(TAG, "📊 After clear/addAll - List size: " + this.baiGiangList.size());

    // ✅ Debug: Log first few items để verify
    if (!this.baiGiangList.isEmpty()) {
        Log.d(TAG, "✅ Items added successfully:");
        for (int i = 0; i < Math.min(3, this.baiGiangList.size()); i++) {
            BaiGiang bg = this.baiGiangList.get(i);
            Log.d(TAG, "📝 Item " + i + ": " + (bg != null ? bg.getTieuDe() : "NULL"));
        }
    } else {
        Log.e(TAG, "❌ No items in list after addAll!");

        // ✅ FALLBACK: Try direct assignment
        Log.d(TAG, "🔄 Trying direct assignment fallback...");
        try {
            this.baiGiangList = new ArrayList<>(newBaiGiangList);
            Log.d(TAG, "✅ Fallback successful - size: " + this.baiGiangList.size());
        } catch (Exception e) {
            Log.e(TAG, "❌ Fallback failed: " + e.getMessage());
            this.baiGiangList = new ArrayList<>();
        }
    }

    Log.d(TAG, "🔄 Calling notifyDataSetChanged()");
    notifyDataSetChanged();
    Log.d(TAG, "✅ updateData completed - final size: " + this.baiGiangList.size());
    Log.d(TAG, "=== END updateData FIXED ===");
}

    // ✅ ALTERNATIVE: Completely new updateData method
    public void updateDataSafe(List<BaiGiang> newBaiGiangList) {
        Log.d(TAG, "🔄 updateDataSafe called - ALTERNATIVE METHOD");

        // Create completely new list
        List<BaiGiang> safeList = new ArrayList<>();

        if (newBaiGiangList != null) {
            // Add items one by one to ensure copy
            for (BaiGiang item : newBaiGiangList) {
                if (item != null) {
                    safeList.add(item);
                }
            }
        }

        Log.d(TAG, "📊 Safe list created with " + safeList.size() + " items");

        // Replace reference completely
        this.baiGiangList = safeList;

        Log.d(TAG, "📊 Adapter list updated to " + this.baiGiangList.size() + " items");

        notifyDataSetChanged();
        Log.d(TAG, "✅ updateDataSafe completed");
    }

    static class BaiGiangViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription;
        TextView tvHSKLevel, tvDuration, tvViews;
        TextView tvLessonType, tvTopic, tvPremium;
        ImageView ivThumbnail;
        // ✅ THÊM: Media icons và buttons
        ImageView ivVideoIcon, ivAudioIcon;
        Button btnPlayVideo, btnPlayAudio;
        Button btnEdit, btnDelete;
        View layoutActionButtons;

        public BaiGiangViewHolder(@NonNull View itemView) {
            super(itemView);

            // Basic info
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);

            // Lesson details
            tvHSKLevel = itemView.findViewById(R.id.tvHSKLevel);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvLessonType = itemView.findViewById(R.id.tvLessonType);
            tvTopic = itemView.findViewById(R.id.tvTopic);
            tvPremium = itemView.findViewById(R.id.tvPremium);

            // ✅ THÊM: Media components
            ivVideoIcon = itemView.findViewById(R.id.ivVideoIcon);
            btnPlayVideo = itemView.findViewById(R.id.btnPlayVideo);


            // Action buttons
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            layoutActionButtons = itemView.findViewById(R.id.layoutActionButtons);

            Log.d("BaiGiangViewHolder", "🏗️ ViewHolder created with media support");
        }
    }
}