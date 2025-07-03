package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.util.Constants;

import java.io.IOException;
import java.util.List;

public class TuVungAdapter extends RecyclerView.Adapter<TuVungAdapter.TuVungViewHolder> {
    private List<TuVung> tuVungList;
    private Context context;
    private boolean isEditable;
    private OnTuVungActionListener listener;
    private MediaPlayer mediaPlayer;

    public interface OnTuVungActionListener {
        void onEditClick(TuVung tuVung);
        void onDeleteClick(TuVung tuVung);
    }

    public TuVungAdapter(Context context, List<TuVung> tuVungList, boolean isEditable, OnTuVungActionListener listener) {
        this.context = context;
        this.tuVungList = tuVungList;
        this.isEditable = isEditable;
        this.listener = listener;
        this.mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public TuVungViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tu_vung, parent, false);
        return new TuVungViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TuVungViewHolder holder, int position) {
        TuVung tuVung = tuVungList.get(position);

        holder.tvTiengTrung.setText(tuVung.getTiengTrung());
        holder.tvPhienAm.setText(tuVung.getPhienAm());
        holder.tvTiengViet.setText(tuVung.getTiengViet());

        // Loại từ
        if (tuVung.getLoaiTu() != null && !tuVung.getLoaiTu().isEmpty()) {
            holder.tvLoaiTu.setText(tuVung.getLoaiTu());
            holder.tvLoaiTu.setVisibility(View.VISIBLE);
        } else {
            holder.tvLoaiTu.setVisibility(View.GONE);
        }

        // Xử lý nút hiển thị ví dụ
        if (tuVung.getViDu() != null && !tuVung.getViDu().isEmpty()) {
            holder.btnShowExample.setVisibility(View.VISIBLE);
            holder.btnShowExample.setOnClickListener(v -> {
                showExampleDialog(tuVung);
            });
        } else {
            holder.btnShowExample.setVisibility(View.GONE);
        }

        // Xử lý nút phát âm thanh
        holder.btnPlayAudio.setOnClickListener(v -> {
            if (tuVung.getAudioURL() != null && !tuVung.getAudioURL().isEmpty()) {
                playAudio(Constants.getBaseUrl() + tuVung.getAudioURL());
            } else {
                Toast.makeText(context, "Không có file âm thanh", Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập listener cho chỉnh sửa (nếu có)
        if (isEditable) {
            holder.itemView.setOnLongClickListener(v -> {
                showEditOptions(tuVung);
                return true;
            });
        }
    }

    private void showEditOptions(TuVung tuVung) {
        String[] options = {"Sửa", "Xóa", "Hủy"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Tùy chọn");
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Sửa
                    if (listener != null) {
                        listener.onEditClick(tuVung);
                    }
                    break;
                case 1: // Xóa
                    if (listener != null) {
                        confirmDelete(tuVung);
                    }
                    break;
                case 2: // Hủy
                    dialog.dismiss();
                    break;
            }
        });
        builder.show();
    }

    private void confirmDelete(TuVung tuVung) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa từ vựng này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            if (listener != null) {
                listener.onDeleteClick(tuVung);
            }
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showExampleDialog(TuVung tuVung) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ví dụ");

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_example, null);
        TextView tvTiengTrung = dialogView.findViewById(R.id.tvTiengTrung);
        TextView tvPhienAm = dialogView.findViewById(R.id.tvPhienAm);
        TextView tvTiengViet = dialogView.findViewById(R.id.tvTiengViet);

        // Phân tách ví dụ (giả sử format là "tiếng Trung|phiên âm|tiếng Việt")
        String[] parts = tuVung.getViDu().split("\\|");
        if (parts.length >= 3) {
            tvTiengTrung.setText(parts[0]);
            tvPhienAm.setText(parts[1]);
            tvTiengViet.setText(parts[2]);
        } else {
            tvTiengTrung.setText(tuVung.getViDu());
            tvPhienAm.setVisibility(View.GONE);
            tvTiengViet.setVisibility(View.GONE);
        }

        builder.setView(dialogView);
        builder.setPositiveButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void playAudio(String audioUrl) {
        // Dừng âm thanh đang phát (nếu có)
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Không thể phát âm thanh", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return tuVungList != null ? tuVungList.size() : 0;
    }

    public void updateData(List<TuVung> newList) {
        this.tuVungList = newList;
        notifyDataSetChanged();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static class TuVungViewHolder extends RecyclerView.ViewHolder {
        TextView tvTiengTrung, tvPhienAm, tvTiengViet, tvLoaiTu;
        Button btnShowExample;
        ImageButton btnPlayAudio;

        public TuVungViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTiengTrung = itemView.findViewById(R.id.tvTiengTrung);
            tvPhienAm = itemView.findViewById(R.id.tvPhienAm);
            tvTiengViet = itemView.findViewById(R.id.tvTiengViet);
            tvLoaiTu = itemView.findViewById(R.id.tvLoaiTu);
            btnShowExample = itemView.findViewById(R.id.btnShowExample);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);
        }
    }
}