package com.example.app_learn_chinese_2025.controller;

import android.content.Context;
import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.model.repository.GuestRepository;
import com.example.app_learn_chinese_2025.util.GuestLimitationHelper;

import java.util.List;
import java.util.Map;

/**
 * 🚀 Controller cho Guest Mode functionality
 */
public class GuestController {
    private static final String TAG = "GuestController";

    private GuestRepository repository;
    private GuestLimitationHelper limitationHelper;
    private OnGuestDataListener listener;

    // Callback interfaces
    public interface OnGuestDataListener {
        void onStatsLoaded(Map<String, Object> stats);
        void onBaiGiangListLoaded(List<BaiGiang> baiGiangList);
        void onBaiGiangDetailLoaded(BaiGiang baiGiang);
        void onTuVungListLoaded(List<TuVung> tuVungList);
        void onChuDeListLoaded(List<ChuDe> chuDeList);
        void onCapDoHSKListLoaded(List<CapDoHSK> capDoHSKList);
        void onLoaiBaiGiangListLoaded(List<LoaiBaiGiang> loaiBaiGiangList);
        void onError(String message);
    }

    public GuestController(Context context, OnGuestDataListener listener) {
        this.repository = new GuestRepository(context);
        this.limitationHelper = new GuestLimitationHelper(context);
        this.listener = listener;
    }

    /**
     * 🎯 Lấy thống kê guest
     */
    public void getGuestStats() {
        Log.d(TAG, "Getting guest stats");

        repository.getGuestStats(new GuestRepository.OnStatsCallback() {
            @Override
            public void onSuccess(Map<String, Object> stats) {
                Log.d(TAG, "✅ Guest stats loaded successfully");
                if (listener != null) {
                    listener.onStatsLoaded(stats);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "❌ Error loading guest stats: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    /**
     * 🎯 Lấy danh sách bài giảng cho guest
     */
    public void getGuestBaiGiang(int limit, Integer capDoHSK_ID, Integer chuDeId) {
        Log.d(TAG, "Getting guest bai giang list");

        repository.getGuestBaiGiang(limit, capDoHSK_ID, chuDeId, new GuestRepository.OnBaiGiangListCallback() {
            @Override
            public void onSuccess(List<BaiGiang> baiGiangList) {
                Log.d(TAG, "✅ Guest bai giang list loaded: " + baiGiangList.size() + " items");
                if (listener != null) {
                    listener.onBaiGiangListLoaded(baiGiangList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "❌ Error loading guest bai giang list: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    /**
     * 🎯 Lấy chi tiết bài giảng cho guest
     */
    public void getGuestBaiGiangDetail(long id) {
        Log.d(TAG, "Getting guest bai giang detail: " + id);

        repository.getGuestBaiGiangDetail(id, new GuestRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                Log.d(TAG, "✅ Guest bai giang detail loaded: " + baiGiang.getTieuDe());
                if (listener != null) {
                    listener.onBaiGiangDetailLoaded(baiGiang);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "❌ Error loading guest bai giang detail: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    /**
     * 🎯 Lấy từ vựng cho guest với limitation
     */
    public void getGuestTuVung(long baiGiangId, int limit) {
        Log.d(TAG, "Getting guest tu vung for bai giang: " + baiGiangId);

        repository.getGuestTuVung(baiGiangId, limit, new GuestRepository.OnTuVungListCallback() {
            @Override
            public void onSuccess(List<TuVung> tuVungList) {
                Log.d(TAG, "✅ Guest tu vung loaded: " + tuVungList.size() + " items");

                // Record vocabulary access
                limitationHelper.recordVocabularyAccess(baiGiangId);

                if (listener != null) {
                    listener.onTuVungListLoaded(tuVungList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "❌ Error loading guest tu vung: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    /**
     * 🎯 Lấy danh sách chủ đề cho guest
     */
    public void getGuestChuDe() {
        Log.d(TAG, "Getting guest chu de list");

        repository.getGuestChuDe(new GuestRepository.OnChuDeListCallback() {
            @Override
            public void onSuccess(List<ChuDe> chuDeList) {
                Log.d(TAG, "✅ Guest chu de list loaded: " + chuDeList.size() + " items");
                if (listener != null) {
                    listener.onChuDeListLoaded(chuDeList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "❌ Error loading guest chu de list: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    /**
     * 🎯 Lấy danh sách cấp độ HSK cho guest
     */
    public void getGuestCapDoHSK() {
        Log.d(TAG, "Getting guest cap do HSK list");

        repository.getGuestCapDoHSK(new GuestRepository.OnCapDoHSKListCallback() {
            @Override
            public void onSuccess(List<CapDoHSK> capDoHSKList) {
                Log.d(TAG, "✅ Guest cap do HSK list loaded: " + capDoHSKList.size() + " items");
                if (listener != null) {
                    listener.onCapDoHSKListLoaded(capDoHSKList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "❌ Error loading guest cap do HSK list: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    /**
     * 🎯 Lấy danh sách loại bài giảng cho guest
     */
    public void getGuestLoaiBaiGiang() {
        Log.d(TAG, "Getting guest loai bai giang list");

        repository.getGuestLoaiBaiGiang(new GuestRepository.OnLoaiBaiGiangListCallback() {
            @Override
            public void onSuccess(List<LoaiBaiGiang> loaiBaiGiangList) {
                Log.d(TAG, "✅ Guest loai bai giang list loaded: " + loaiBaiGiangList.size() + " items");
                if (listener != null) {
                    listener.onLoaiBaiGiangListLoaded(loaiBaiGiangList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "❌ Error loading guest loai bai giang list: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    /**
     * 🎯 Lấy limitation helper
     */
    public GuestLimitationHelper getLimitationHelper() {
        return limitationHelper;
    }
}