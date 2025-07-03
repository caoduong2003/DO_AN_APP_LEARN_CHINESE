package com.example.app_learn_chinese_2025.view.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.MauCau;
import com.example.app_learn_chinese_2025.util.Constants;

import java.io.IOException;
import java.util.List;

public class MauCauAdapter extends RecyclerView.Adapter<MauCauAdapter.MauCauViewHolder> {
    private List<MauCau> mauCauList;
    private Context context;
    private MediaPlayer mediaPlayer;

    public MauCauAdapter(Context context, List<MauCau> mauCauList) {
        this.context = context;
        this.mauCauList = mauCauList;
        this.mediaPlayer = new MediaPlayer();
    }

    @NonNull
    @Override
    public MauCauViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mau_cau, parent, false);
        return new MauCauViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MauCauViewHolder holder, int position) {
        MauCau mauCau = mauCauList.get(position);

        // Set values
        holder.tvTiengTrung.setText(mauCau.getTiengTrung());
        holder.tvPhienAm.setText(mauCau.getPhienAm());
        holder.tvTiengViet.setText(mauCau.getTiengViet());

        // Show/hide note
        if (mauCau.getGhiChu() != null && !mauCau.getGhiChu().isEmpty()) {
            holder.tvGhiChu.setText(mauCau.getGhiChu());
            holder.tvGhiChu.setVisibility(View.VISIBLE);
        } else {
            holder.tvGhiChu.setVisibility(View.GONE);
        }

        // Set audio button click listener
        holder.btnPlayAudio.setOnClickListener(v -> {
            if (mauCau.getAudioURL() != null && !mauCau.getAudioURL().isEmpty()) {
                playAudio(Constants.getBaseUrl() + mauCau.getAudioURL());
            } else {
                Toast.makeText(context, "Không có file âm thanh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void playAudio(String audioUrl) {
        // Stop any currently playing audio
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
        return mauCauList != null ? mauCauList.size() : 0;
    }

    public void updateData(List<MauCau> newList) {
        this.mauCauList = newList;
        notifyDataSetChanged();
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static class MauCauViewHolder extends RecyclerView.ViewHolder {
        TextView tvTiengTrung, tvPhienAm, tvTiengViet, tvGhiChu;
        ImageButton btnPlayAudio;

        public MauCauViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTiengTrung = itemView.findViewById(R.id.tvTiengTrung);
            tvPhienAm = itemView.findViewById(R.id.tvPhienAm);
            tvTiengViet = itemView.findViewById(R.id.tvTiengViet);
            tvGhiChu = itemView.findViewById(R.id.tvGhiChu);
            btnPlayAudio = itemView.findViewById(R.id.btnPlayAudio);
        }
    }
}