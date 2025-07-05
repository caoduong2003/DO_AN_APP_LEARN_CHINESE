package com.example.app_learn_chinese_2025.model.repository;

import android.content.Context;
import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.model.remote.ApiService;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 🚀 Repository cho Guest Mode APIs
 */
public class GuestRepository {
    private static final String TAG = "GuestRepository";

    private ApiService apiService;
    private Context context;

    public GuestRepository(Context context) {
        this.context = context;
        this.apiService = RetrofitClient.getInstance(new SessionManager(context)).getApiService();
    }

    // Callback interfaces
    public interface OnStatsCallback {
        void onSuccess(Map<String, Object> stats);
        void onError(String errorMessage);
    }

    public interface OnBaiGiangListCallback {
        void onSuccess(List<BaiGiang> baiGiangList);
        void onError(String errorMessage);
    }

    public interface OnBaiGiangCallback {
        void onSuccess(BaiGiang baiGiang);
        void onError(String errorMessage);
    }

    public interface OnTuVungListCallback {
        void onSuccess(List<TuVung> tuVungList);
        void onError(String errorMessage);
    }

    public interface OnChuDeListCallback {
        void onSuccess(List<ChuDe> chuDeList);
        void onError(String errorMessage);
    }

    public interface OnCapDoHSKListCallback {
        void onSuccess(List<CapDoHSK> capDoHSKList);
        void onError(String errorMessage);
    }

    public interface OnLoaiBaiGiangListCallback {
        void onSuccess(List<LoaiBaiGiang> loaiBaiGiangList);
        void onError(String errorMessage);
    }

    /**
     * 🎯 Lấy thống kê guest
     */
    public void getGuestStats(OnStatsCallback callback) {
        Log.d(TAG, "Getting guest stats");

        Call<Map<String, Object>> call = apiService.getGuestStats();
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Guest stats loaded successfully");
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "❌ Failed to load guest stats: " + response.code());
                    callback.onError("Không thể tải thống kê");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Log.e(TAG, "❌ Network error loading guest stats", t);
                callback.onError("Lỗi kết nối mạng");
            }
        });
    }

    /**
     * 🎯 Lấy danh sách bài giảng cho guest
     */
    public void getGuestBaiGiang(int limit, Integer capDoHSK_ID, Integer chuDeId, OnBaiGiangListCallback callback) {
        Log.d(TAG, "Getting guest bai giang list with limit: " + limit);

        Call<List<BaiGiang>> call = apiService.getGuestBaiGiang(limit, capDoHSK_ID, chuDeId);
        call.enqueue(new Callback<List<BaiGiang>>() {
            @Override
            public void onResponse(Call<List<BaiGiang>> call, Response<List<BaiGiang>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Guest bai giang list loaded: " + response.body().size() + " items");
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "❌ Failed to load guest bai giang list: " + response.code());
                    callback.onError("Không thể tải danh sách bài giảng");
                }
            }

            @Override
            public void onFailure(Call<List<BaiGiang>> call, Throwable t) {
                Log.e(TAG, "❌ Network error loading guest bai giang list", t);
                callback.onError("Lỗi kết nối mạng");
            }
        });
    }

    /**
     * 🎯 Lấy chi tiết bài giảng cho guest
     */
    public void getGuestBaiGiangDetail(long id, OnBaiGiangCallback callback) {
        Log.d(TAG, "Getting guest bai giang detail: " + id);

        Call<BaiGiang> call = apiService.getGuestBaiGiangDetail(id);
        call.enqueue(new Callback<BaiGiang>() {
            @Override
            public void onResponse(Call<BaiGiang> call, Response<BaiGiang> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Guest bai giang detail loaded: " + response.body().getTieuDe());
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "❌ Failed to load guest bai giang detail: " + response.code());
                    callback.onError("Không thể tải chi tiết bài giảng");
                }
            }

            @Override
            public void onFailure(Call<BaiGiang> call, Throwable t) {
                Log.e(TAG, "❌ Network error loading guest bai giang detail", t);
                callback.onError("Lỗi kết nối mạng");
            }
        });
    }

    /**
     * 🎯 Lấy từ vựng cho guest
     */
    public void getGuestTuVung(long baiGiangId, int limit, OnTuVungListCallback callback) {
        Log.d(TAG, "Getting guest tu vung for bai giang: " + baiGiangId + ", limit: " + limit);

        Call<List<TuVung>> call = apiService.getGuestTuVung(baiGiangId, limit);
        call.enqueue(new Callback<List<TuVung>>() {
            @Override
            public void onResponse(Call<List<TuVung>> call, Response<List<TuVung>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Guest tu vung loaded: " + response.body().size() + " items");
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "❌ Failed to load guest tu vung: " + response.code());
                    callback.onError("Không thể tải từ vựng");
                }
            }

            @Override
            public void onFailure(Call<List<TuVung>> call, Throwable t) {
                Log.e(TAG, "❌ Network error loading guest tu vung", t);
                callback.onError("Lỗi kết nối mạng");
            }
        });
    }

    /**
     * 🎯 Lấy danh sách chủ đề cho guest
     */
    public void getGuestChuDe(OnChuDeListCallback callback) {
        Log.d(TAG, "Getting guest chu de list");

        Call<List<ChuDe>> call = apiService.getGuestChuDe();
        call.enqueue(new Callback<List<ChuDe>>() {
            @Override
            public void onResponse(Call<List<ChuDe>> call, Response<List<ChuDe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Guest chu de list loaded: " + response.body().size() + " items");
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "❌ Failed to load guest chu de list: " + response.code());
                    callback.onError("Không thể tải danh sách chủ đề");
                }
            }

            @Override
            public void onFailure(Call<List<ChuDe>> call, Throwable t) {
                Log.e(TAG, "❌ Network error loading guest chu de list", t);
                callback.onError("Lỗi kết nối mạng");
            }
        });
    }

    /**
     * 🎯 Lấy danh sách cấp độ HSK cho guest
     */
    public void getGuestCapDoHSK(OnCapDoHSKListCallback callback) {
        Log.d(TAG, "Getting guest cap do HSK list");

        Call<List<CapDoHSK>> call = apiService.getGuestCapDoHSK();
        call.enqueue(new Callback<List<CapDoHSK>>() {
            @Override
            public void onResponse(Call<List<CapDoHSK>> call, Response<List<CapDoHSK>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Guest cap do HSK list loaded: " + response.body().size() + " items");
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "❌ Failed to load guest cap do HSK list: " + response.code());
                    callback.onError("Không thể tải danh sách cấp độ HSK");
                }
            }

            @Override
            public void onFailure(Call<List<CapDoHSK>> call, Throwable t) {
                Log.e(TAG, "❌ Network error loading guest cap do HSK list", t);
                callback.onError("Lỗi kết nối mạng");
            }
        });
    }

    /**
     * 🎯 Lấy danh sách loại bài giảng cho guest
     */
    public void getGuestLoaiBaiGiang(OnLoaiBaiGiangListCallback callback) {
        Log.d(TAG, "Getting guest loai bai giang list");

        Call<List<LoaiBaiGiang>> call = apiService.getGuestLoaiBaiGiang();
        call.enqueue(new Callback<List<LoaiBaiGiang>>() {
            @Override
            public void onResponse(Call<List<LoaiBaiGiang>> call, Response<List<LoaiBaiGiang>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "✅ Guest loai bai giang list loaded: " + response.body().size() + " items");
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "❌ Failed to load guest loai bai giang list: " + response.code());
                    callback.onError("Không thể tải danh sách loại bài giảng");
                }
            }

            @Override
            public void onFailure(Call<List<LoaiBaiGiang>> call, Throwable t) {
                Log.e(TAG, "❌ Network error loading guest loai bai giang list", t);
                callback.onError("Lỗi kết nối mạng");
            }
        });
    }
}