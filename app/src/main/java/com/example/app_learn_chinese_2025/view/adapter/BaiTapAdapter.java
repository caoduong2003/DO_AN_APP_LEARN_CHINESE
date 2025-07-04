package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiTap;

import java.util.List;

public class BaiTapAdapter extends RecyclerView.Adapter<BaiTapAdapter.BaiTapViewHolder> {

    private Context context;
    private List<BaiTap> baiTapList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(BaiTap baiTap);
    }

    public BaiTapAdapter(Context context, List<BaiTap> baiTapList) {
        this.context = context;
        this.baiTapList = baiTapList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaiTapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bai_tap, parent, false);
        return new BaiTapViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaiTapViewHolder holder, int position) {
        BaiTap baiTap = baiTapList.get(position);
        holder.bind(baiTap);
    }

    @Override
    public int getItemCount() {
        return baiTapList != null ? baiTapList.size() : 0;
    }

    public void updateData(List<BaiTap> newBaiTapList) {
        this.baiTapList.clear();
        this.baiTapList.addAll(newBaiTapList);
        notifyDataSetChanged();
    }

    public class BaiTapViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView tvTieuDe, tvMoTa, tvCapDoHSK, tvChuDe;
        private TextView tvSoCauHoi, tvThoiGianLam, tvDiemToiDa;
        private TextView tvSoLanLam, tvDiemCaoNhat, tvStatus;

        public BaiTapViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView);
            tvTieuDe = itemView.findViewById(R.id.tvTieuDe);
            tvMoTa = itemView.findViewById(R.id.tvMoTa);
            tvCapDoHSK = itemView.findViewById(R.id.tvCapDoHSK);
            tvChuDe = itemView.findViewById(R.id.tvChuDe);
            tvSoCauHoi = itemView.findViewById(R.id.tvSoCauHoi);
            tvThoiGianLam = itemView.findViewById(R.id.tvThoiGianLam);
            tvDiemToiDa = itemView.findViewById(R.id.tvDiemToiDa);
            tvSoLanLam = itemView.findViewById(R.id.tvSoLanLam);
            tvDiemCaoNhat = itemView.findViewById(R.id.tvDiemCaoNhat);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            cardView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(baiTapList.get(getAdapterPosition()));
                }
            });
        }

        public void bind(BaiTap baiTap) {
            // Tiêu đề bài tập
            tvTieuDe.setText(baiTap.getTieuDe() != null ? baiTap.getTieuDe() : "Bài tập");

            // Mô tả
            if (baiTap.getMoTa() != null && !baiTap.getMoTa().isEmpty()) {
                tvMoTa.setText(baiTap.getMoTa());
                tvMoTa.setVisibility(View.VISIBLE);
            } else {
                tvMoTa.setText("Không có mô tả");
                tvMoTa.setVisibility(View.VISIBLE);
            }

            // Cấp độ HSK
            if (baiTap.getCapDoHSKTen() != null && !baiTap.getCapDoHSKTen().isEmpty()) {
                tvCapDoHSK.setText(baiTap.getCapDoHSKTen());
                tvCapDoHSK.setVisibility(View.VISIBLE);
            } else {
                tvCapDoHSK.setText("HSK");
                tvCapDoHSK.setVisibility(View.VISIBLE);
            }

            // Chủ đề
            if (baiTap.getChuDeTen() != null && !baiTap.getChuDeTen().isEmpty()) {
                tvChuDe.setText(baiTap.getChuDeTen());
                tvChuDe.setVisibility(View.VISIBLE);
            } else {
                tvChuDe.setText("Chủ đề");
                tvChuDe.setVisibility(View.VISIBLE);
            }

            // Thông tin bài tập - sử dụng utility methods đã tạo
            tvSoCauHoi.setText(baiTap.getFormattedQuestions());
            tvThoiGianLam.setText(baiTap.getFormattedTime());
            tvDiemToiDa.setText(baiTap.getFormattedScore());

            // Trạng thái làm bài
            if (baiTap.hasBeenTaken()) {
                tvSoLanLam.setText("Đã làm " + baiTap.getSoLanLam() + " lần");
                tvSoLanLam.setVisibility(View.VISIBLE);

                if (baiTap.hasDiemCaoNhat()) {
                    tvDiemCaoNhat.setText("Điểm cao nhất: " + String.format("%.1f", baiTap.getDiemCaoNhat()));
                    tvDiemCaoNhat.setVisibility(View.VISIBLE);

                    // Đặt màu theo điểm số
                    if (baiTap.getDiemToiDa() != null && baiTap.getDiemToiDa() > 0) {
                        float percentage = (baiTap.getDiemCaoNhat() / baiTap.getDiemToiDa()) * 100;
                        setScoreColor(tvDiemCaoNhat, percentage);
                    }
                } else {
                    tvDiemCaoNhat.setVisibility(View.GONE);
                }

                tvStatus.setText("Làm lại");
                tvStatus.setBackgroundColor(context.getResources().getColor(R.color.warning_color));
            } else {
                tvSoLanLam.setVisibility(View.GONE);
                tvDiemCaoNhat.setVisibility(View.GONE);
                tvStatus.setText("Chưa làm");
                tvStatus.setBackgroundColor(context.getResources().getColor(R.color.info));
            }
        }

        private void setScoreColor(TextView textView, float percentage) {
            int colorRes;
            if (percentage >= 90) {
                colorRes = R.color.success;
            } else if (percentage >= 80) {
                colorRes = R.color.info;
            } else if (percentage >= 70) {
                colorRes = R.color.warning;
            } else if (percentage >= 60) {
                colorRes = R.color.warning_color;
            } else {
                colorRes = R.color.error;
            }
            textView.setTextColor(context.getResources().getColor(colorRes));
        }
    }
}