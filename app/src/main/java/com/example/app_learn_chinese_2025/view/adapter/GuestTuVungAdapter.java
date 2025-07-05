package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.TuVung;

import java.util.List;

/**
 * 🚀 Adapter cho từ vựng trong Guest Mode
 */
public class GuestTuVungAdapter extends RecyclerView.Adapter<GuestTuVungAdapter.TuVungViewHolder> {
    private static final String TAG = "GuestTuVungAdapter";

    private Context context;
    private List<TuVung> tuVungList;
    private OnTuVungClickListener listener;

    public interface OnTuVungClickListener {
        void onPlayAudio(TuVung tuVung);
        void onTranslate(TuVung tuVung);
        void onViewMore(TuVung tuVung);
    }

    public GuestTuVungAdapter(Context context, List<TuVung> tuVungList, OnTuVungClickListener listener) {
        this.context = context;
        this.tuVungList = tuVungList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TuVungViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_guest_tuvung, parent, false);
        return new TuVungViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TuVungViewHolder holder, int position) {
        TuVung tuVung = tuVungList.get(position);

        // Sử dụng methods đã bổ sung trong model
        holder.tvChinese.setText(tuVung.getTiengTrungDisplay());
        holder.tvPinyin.setText(tuVung.getPhienAmDisplay());
        holder.tvVietnamese.setText(tuVung.getTiengVietDisplay());

        // Hiển thị index cho guest
        holder.tvIndex.setText(String.valueOf(position + 1));

        // Guest limitation indicator
        if (tuVung.isPremium(position)) {
            holder.tvLimitIndicator.setVisibility(View.VISIBLE);
            holder.tvLimitIndicator.setText("Premium");
        } else {
            holder.tvLimitIndicator.setVisibility(View.GONE);
        }

        // Enable/disable buttons based on content
        holder.btnPlayAudio.setEnabled(tuVung.canPlayAudio());
        holder.btnTranslate.setEnabled(tuVung.canTranslate());

        // Click listeners
        holder.btnPlayAudio.setOnClickListener(v -> {
            if (listener != null && tuVung.canPlayAudio()) {
                listener.onPlayAudio(tuVung);
            }
        });

        holder.btnTranslate.setOnClickListener(v -> {
            if (listener != null && tuVung.canTranslate()) {
                listener.onTranslate(tuVung);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewMore(tuVung);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tuVungList.size();
    }

    public static class TuVungViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex, tvChinese, tvPinyin, tvVietnamese, tvLimitIndicator;
        Button btnPlayAudio, btnTranslate;

        public TuVungViewHolder(@NonNull View itemView) {
            super(itemView);

            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvChinese = itemView.findViewById(R.id.tvChinese);
            tvPinyin = itemView.findViewById(R.id.tvPinyin);
            tvVietnamese = itemView.findViewById(R.id.tvVietnamese);
            tvLimitIndicator = itemView.findViewById(R.id.tvLimitIndicator);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);
            btnTranslate = itemView.findViewById(R.id.btnTranslate);
        }
    }
}