package com.example.app_learn_chinese_2025.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;

public class NoiDungBaiGiangFragment extends Fragment {
    private TextView tvTieuDe, tvCapDoHSK, tvLoaiBaiGiang, tvLuotXem, tvThoiLuong, tvMoTa;
    private WebView webViewNoiDung;

    private SessionManager sessionManager;
    private BaiGiangRepository baiGiangRepository;

    private long baiGiangId;
    private BaiGiang currentBaiGiang;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get baiGiangId from arguments
        if (getArguments() != null) {
            baiGiangId = getArguments().getLong("BAI_GIANG_ID", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_noi_dung_bai_giang, container, false);

        initViews(view);

        // Load data
        loadBaiGiang();

        return view;
    }

    private void initViews(View view) {
        tvTieuDe = view.findViewById(R.id.tvTieuDe);
        tvCapDoHSK = view.findViewById(R.id.tvCapDoHSK);
        tvLoaiBaiGiang = view.findViewById(R.id.tvLoaiBaiGiang);
        tvLuotXem = view.findViewById(R.id.tvLuotXem);
        tvThoiLuong = view.findViewById(R.id.tvThoiLuong);
        tvMoTa = view.findViewById(R.id.tvMoTa);
        webViewNoiDung = view.findViewById(R.id.webViewNoiDung);

        sessionManager = new SessionManager(requireContext());
        baiGiangRepository = new BaiGiangRepository(sessionManager);
    }

    private void loadBaiGiang() {
        if (baiGiangId <= 0) {
            Toast.makeText(requireContext(), "Không tìm thấy thông tin bài giảng", Toast.LENGTH_SHORT).show();
            return;
        }

        baiGiangRepository.getBaiGiangById(baiGiangId, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                currentBaiGiang = baiGiang;

                // Update UI
                updateUI();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        if (currentBaiGiang != null) {
            tvTieuDe.setText(currentBaiGiang.getTieuDe());

            // Set CapDoHSK
            if (currentBaiGiang.getCapDoHSK() != null) {
                tvCapDoHSK.setText("HSK " + currentBaiGiang.getCapDoHSK().getCapDo());
                tvCapDoHSK.setVisibility(View.VISIBLE);
            } else {
                tvCapDoHSK.setVisibility(View.GONE);
            }

            // Set LoaiBaiGiang
            if (currentBaiGiang.getLoaiBaiGiang() != null) {
                tvLoaiBaiGiang.setText(currentBaiGiang.getLoaiBaiGiang().getTenLoai());
                tvLoaiBaiGiang.setVisibility(View.VISIBLE);
            } else {
                tvLoaiBaiGiang.setVisibility(View.GONE);
            }

            // Set view count and duration
            tvLuotXem.setText(currentBaiGiang.getLuotXem() + " lượt xem");
            tvThoiLuong.setText(currentBaiGiang.getThoiLuong() + " phút");

            // Set description
            tvMoTa.setText(currentBaiGiang.getMoTa());

            // Load content into WebView
            String htmlContent = currentBaiGiang.getNoiDung();

            // Wrap content in HTML tags if needed
            if (!htmlContent.trim().startsWith("<html>")) {
                htmlContent = "<html><head>" +
                        "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                        "<style>" +
                        "body { font-family: 'Roboto', sans-serif; line-height: 1.6; padding: 16px; }" +
                        "img { max-width: 100%; height: auto; }" +
                        "table { border-collapse: collapse; width: 100%; }" +
                        "th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }" +
                        "</style>" +
                        "</head><body>" + htmlContent + "</body></html>";
            }

            webViewNoiDung.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        }
    }
}