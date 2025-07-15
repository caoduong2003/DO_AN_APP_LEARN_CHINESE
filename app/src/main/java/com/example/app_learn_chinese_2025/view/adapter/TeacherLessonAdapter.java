package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.remote.ApiService;
import com.example.app_learn_chinese_2025.util.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 🎯 Adapter cho danh sách bài giảng của giáo viên
 * Hiển thị với các chức năng: xem, sửa, xóa, thay đổi trạng thái, nhân bản
 */
public class TeacherLessonAdapter extends RecyclerView.Adapter<TeacherLessonAdapter.LessonViewHolder> {

    private Context context;
    private List<ApiService.TeacherBaiGiangResponse.SimpleResponse> lessonList;
    private OnLessonActionListener listener;
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    // ===== INTERFACE =====

    public interface OnLessonActionListener {
        void onLessonClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson);
        void onEditClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson);
        void onDeleteClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson);
        void onToggleStatusClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson);
        void onTogglePremiumClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson);
        void onDuplicateClick(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson);
    }

    // ===== CONSTRUCTOR =====

    public TeacherLessonAdapter(Context context,
                                List<ApiService.TeacherBaiGiangResponse.SimpleResponse> lessonList,
                                OnLessonActionListener listener) {
        this.context = context;
        this.lessonList = lessonList;
        this.listener = listener;
    }

    // ===== ADAPTER METHODS =====

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_teacher_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        ApiService.TeacherBaiGiangResponse.SimpleResponse lesson = lessonList.get(position);
        holder.bind(lesson);
    }

    @Override
    public int getItemCount() {
        return lessonList != null ? lessonList.size() : 0;
    }

    // ===== VIEW HOLDER =====

    class LessonViewHolder extends RecyclerView.ViewHolder {

        // UI Components
        private CardView cardView;
        private ImageView ivThumbnail, ivStatusIcon, ivPremiumIcon;
        private TextView tvTitle, tvDescription, tvStats, tvDateTime, tvCategory;
        private ImageButton btnEdit, btnDelete, btnToggleStatus, btnTogglePremium, btnDuplicate;
        private View statusIndicator;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        private void initViews() {
            cardView = itemView.findViewById(R.id.cardView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            ivStatusIcon = itemView.findViewById(R.id.ivStatusIcon);
            ivPremiumIcon = itemView.findViewById(R.id.ivPremiumIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStats = itemView.findViewById(R.id.tvStats);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            btnToggleStatus = itemView.findViewById(R.id.btnToggleStatus);
            btnTogglePremium = itemView.findViewById(R.id.btnTogglePremium);
            btnDuplicate = itemView.findViewById(R.id.btnDuplicate);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }

        public void bind(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson) {
            // Basic info
            tvTitle.setText(lesson.getTieuDe());
            tvDescription.setText(lesson.getMoTa());

            // Stats
            String statsText = String.format("👁 %d | ⏱ %d phút | 📅 %s",
                    lesson.getLuotXem() != null ? lesson.getLuotXem() : 0,
                    lesson.getThoiLuong() != null ? lesson.getThoiLuong() : 0,
                    lesson.getMaBaiGiang() != null ? lesson.getMaBaiGiang() : "N/A");
            tvStats.setText(statsText);

            // Date time
            if (lesson.getNgayCapNhat() != null) {
                try {
                    Date date = inputFormat.parse(lesson.getNgayCapNhat());
                    tvDateTime.setText("Cập nhật: " + outputFormat.format(date));
                } catch (ParseException e) {
                    tvDateTime.setText("Cập nhật: " + lesson.getNgayCapNhat());
                }
            } else {
                tvDateTime.setText("Chưa cập nhật");
            }

            // Category info
            StringBuilder categoryText = new StringBuilder();
            if (lesson.getCapDoHSK() != null) {
                categoryText.append("HSK ").append(lesson.getCapDoHSK().getCapDo());
            }
            if (lesson.getChuDe() != null) {
                if (categoryText.length() > 0) categoryText.append(" • ");
                categoryText.append(lesson.getChuDe().getTen());
            }
            if (lesson.getLoaiBaiGiang() != null) {
                if (categoryText.length() > 0) categoryText.append(" • ");
                categoryText.append(lesson.getLoaiBaiGiang().getTen());
            }
            tvCategory.setText(categoryText.toString());

            // Thumbnail
            if (lesson.getHinhAnh() != null && !lesson.getHinhAnh().isEmpty()) {
                String imageUrl = Constants.getBaseUrl() + Constants.API_VIEW_IMAGE + lesson.getHinhAnh();
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_lesson)
                        .error(R.drawable.placeholder_lesson)
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.placeholder_lesson);
            }

            // Status indicators
            updateStatusIndicators(lesson);

            // Button icons and colors
            updateButtonStates(lesson);

            // Click listeners
            setupClickListeners(lesson);
        }

        private void updateStatusIndicators(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson) {
            // Status indicator color
            if (lesson.getTrangThai() != null && lesson.getTrangThai()) {
                statusIndicator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
                ivStatusIcon.setImageResource(R.drawable.ic_public);
                ivStatusIcon.setColorFilter(context.getResources().getColor(android.R.color.holo_green_dark));
            } else {
                statusIndicator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
                ivStatusIcon.setImageResource(R.drawable.ic_private);
                ivStatusIcon.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark));
            }

            // Premium indicator
            if (lesson.getLaBaiGiangGoi() != null && lesson.getLaBaiGiangGoi()) {
                ivPremiumIcon.setVisibility(View.VISIBLE);
                ivPremiumIcon.setImageResource(R.drawable.ic_premium);
                ivPremiumIcon.setColorFilter(context.getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                ivPremiumIcon.setVisibility(View.GONE);
            }
        }

        private void updateButtonStates(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson) {
            // Toggle status button
            if (lesson.getTrangThai() != null && lesson.getTrangThai()) {
                btnToggleStatus.setImageResource(R.drawable.ic_hide);
                btnToggleStatus.setContentDescription("Ẩn bài giảng");
            } else {
                btnToggleStatus.setImageResource(R.drawable.ic_show);
                btnToggleStatus.setContentDescription("Công khai bài giảng");
            }

            // Toggle premium button
            if (lesson.getLaBaiGiangGoi() != null && lesson.getLaBaiGiangGoi()) {
                btnTogglePremium.setImageResource(R.drawable.ic_free);
                btnTogglePremium.setContentDescription("Chuyển thành miễn phí");
            } else {
                btnTogglePremium.setImageResource(R.drawable.ic_premium);
                btnTogglePremium.setContentDescription("Chuyển thành premium");
            }
        }

        private void setupClickListeners(ApiService.TeacherBaiGiangResponse.SimpleResponse lesson) {
            // Card click - view detail
            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLessonClick(lesson);
                }
            });

            // Edit button
            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(lesson);
                }
            });

            // Delete button
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(lesson);
                }
            });

            // Toggle status button
            btnToggleStatus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onToggleStatusClick(lesson);
                }
            });

            // Toggle premium button
            btnTogglePremium.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTogglePremiumClick(lesson);
                }
            });

            // Duplicate button
            btnDuplicate.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDuplicateClick(lesson);
                }
            });
        }
    }

    // ===== PUBLIC METHODS =====

    public void updateData(List<ApiService.TeacherBaiGiangResponse.SimpleResponse> newLessonList) {
        this.lessonList = newLessonList;
        notifyDataSetChanged();
    }

    public void addData(List<ApiService.TeacherBaiGiangResponse.SimpleResponse> newLessons) {
        int startPosition = lessonList.size();
        lessonList.addAll(newLessons);
        notifyItemRangeInserted(startPosition, newLessons.size());
    }

    public void removeItem(int position) {
        if (position >= 0 && position < lessonList.size()) {
            lessonList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public ApiService.TeacherBaiGiangResponse.SimpleResponse getItem(int position) {
        return position >= 0 && position < lessonList.size() ? lessonList.get(position) : null;
    }
}