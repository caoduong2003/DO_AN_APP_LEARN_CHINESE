package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
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
import com.example.app_learn_chinese_2025.model.data.VisualLearning;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class VisualLearningAdapter extends RecyclerView.Adapter<VisualLearningAdapter.ViewHolder> {
    private static final String TAG = "VisualLearningAdapter";

    private Context context;
    private List<VisualLearning> dataList;
    private OnItemClickListener listener;
    private SimpleDateFormat dateFormat;

    public interface OnItemClickListener {
        void onItemClick(VisualLearning item);
        void onFavoriteClick(VisualLearning item);
        void onDeleteClick(VisualLearning item);
    }

    public VisualLearningAdapter(Context context, OnItemClickListener listener) {
        this.context = context;
        this.dataList = new ArrayList<>();
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_visual_learning, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VisualLearning item = dataList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateData(List<VisualLearning> newData) {
        this.dataList.clear();
        if (newData != null) {
            this.dataList.addAll(newData);
        }
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivImage;
        private TextView tvObject;
        private TextView tvChinese;
        private TextView tvPinyin;
        private TextView tvVietnamese;
        private TextView tvExample;
        private TextView tvDate;
        private ImageButton btnFavorite;
        private ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = itemView.findViewById(R.id.ivImage);
            tvObject = itemView.findViewById(R.id.tvObject);
            tvChinese = itemView.findViewById(R.id.tvChinese);
            tvPinyin = itemView.findViewById(R.id.tvPinyin);
            tvVietnamese = itemView.findViewById(R.id.tvVietnamese);
            tvExample = itemView.findViewById(R.id.tvExample);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(VisualLearning item) {
            // Set text data
            tvObject.setText(item.getDetectedObject() != null ? item.getDetectedObject() : "Không xác định");
            tvChinese.setText(item.getChineseVocabulary() != null ? item.getChineseVocabulary() : "");
            tvPinyin.setText(item.getPinyin() != null ? item.getPinyin() : "");
            tvVietnamese.setText(item.getVietnameseMeaning() != null ? item.getVietnameseMeaning() : "");
            tvExample.setText(item.getExampleSentence() != null ? item.getExampleSentence() : "");
            tvDate.setText(dateFormat.format(new Date(item.getCreatedAt())));

            // Set favorite button
            btnFavorite.setImageResource(item.isFavorite() ?
                    R.drawable.ic_favorite_filled : R.drawable.ic_favorite_border);

            // Load image if exists
            if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                Glide.with(context)
                        .load(item.getImagePath())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .error(R.drawable.ic_image_error)
                        .centerCrop()
                        .into(ivImage);
            } else {
                ivImage.setImageResource(R.drawable.ic_image_placeholder);
            }

            // Set click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });

            btnFavorite.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFavoriteClick(item);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(item);
                }
            });
        }
    }
}