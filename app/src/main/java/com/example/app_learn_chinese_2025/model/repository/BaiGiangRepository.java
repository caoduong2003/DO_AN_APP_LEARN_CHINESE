package com.example.app_learn_chinese_2025.model.repository;

import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaiGiangRepository {
    private static final String TAG = "BaiGiangRepository";
    private SessionManager sessionManager;

    public interface OnBaiGiangCallback {
        void onSuccess(BaiGiang baiGiang);
        void onError(String errorMessage);
    }

    public interface OnBaiGiangListCallback {
        void onSuccess(List<BaiGiang> baiGiangList);
        void onError(String errorMessage);
    }

    public interface OnCapDoHSKListCallback {
        void onSuccess(List<CapDoHSK> capDoHSKList);
        void onError(String errorMessage);
    }

    public interface OnChuDeListCallback {
        void onSuccess(List<ChuDe> chuDeList);
        void onError(String errorMessage);
    }

    public interface OnLoaiBaiGiangListCallback {
        void onSuccess(List<LoaiBaiGiang> loaiBaiGiangList);
        void onError(String errorMessage);
    }

    public BaiGiangRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private String getToken() {
        return "Bearer " + sessionManager.getToken();
    }

    // Methods for CapDoHSK
    public void getAllCapDoHSK(OnCapDoHSKListCallback callback) {
        RetrofitClient.getInstance().getApiService().getAllCapDoHSK()
                .enqueue(new Callback<List<CapDoHSK>>() {
                    @Override
                    public void onResponse(Call<List<CapDoHSK>> call, Response<List<CapDoHSK>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tải danh sách cấp độ HSK");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CapDoHSK>> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Methods for ChuDe
    public void getAllChuDe(OnChuDeListCallback callback) {
        RetrofitClient.getInstance().getApiService().getAllChuDe()
                .enqueue(new Callback<List<ChuDe>>() {
                    @Override
                    public void onResponse(Call<List<ChuDe>> call, Response<List<ChuDe>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tải danh sách chủ đề");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ChuDe>> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Methods for LoaiBaiGiang
    public void getAllLoaiBaiGiang(OnLoaiBaiGiangListCallback callback) {
        RetrofitClient.getInstance().getApiService().getAllLoaiBaiGiang()
                .enqueue(new Callback<List<LoaiBaiGiang>>() {
                    @Override
                    public void onResponse(Call<List<LoaiBaiGiang>> call, Response<List<LoaiBaiGiang>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tải danh sách loại bài giảng");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<LoaiBaiGiang>> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Methods for BaiGiang - FIXED VERSION
    public void getAllBaiGiang(OnBaiGiangListCallback callback) {
        // Gọi method với 5 tham số, truyền null cho tất cả filter
        getAllBaiGiang(null, null, null, null, null, callback);
    }

    public void getBaiGiangByGiangVien(Long giangVienId, OnBaiGiangListCallback callback) {
        // Gọi method với 5 tham số
        getAllBaiGiang(giangVienId, null, null, null, null, callback);
    }

    public void getBaiGiangByLoai(Integer loaiBaiGiangId, OnBaiGiangListCallback callback) {
        // Gọi method với 5 tham số
        getAllBaiGiang(null, loaiBaiGiangId, null, null, null, callback);
    }

    public void getBaiGiangByCapDoHSK(Integer capDoHSK_ID, OnBaiGiangListCallback callback) {
        // Gọi method với 5 tham số
        getAllBaiGiang(null, null, capDoHSK_ID, null, null, callback);
    }

    public void getBaiGiangByChuDe(Integer chuDeId, OnBaiGiangListCallback callback) {
        // Gọi method với 5 tham số
        getAllBaiGiang(null, null, null, chuDeId, null, callback);
    }

    // Method chính với đủ 5 tham số
    public void getAllBaiGiang(Long giangVienId, Integer loaiBaiGiangId, Integer capDoHSK_ID, Integer chuDeId, Boolean published, OnBaiGiangListCallback callback) {
        RetrofitClient.getInstance().getApiService().getAllBaiGiang(giangVienId, loaiBaiGiangId, capDoHSK_ID, chuDeId, published)
                .enqueue(new Callback<List<BaiGiang>>() {
                    @Override
                    public void onResponse(Call<List<BaiGiang>> call, Response<List<BaiGiang>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tải danh sách bài giảng");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<BaiGiang>> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void getBaiGiangById(long id, OnBaiGiangCallback callback) {
        RetrofitClient.getInstance().getApiService().getBaiGiangById(id)
                .enqueue(new Callback<BaiGiang>() {
                    @Override
                    public void onResponse(Call<BaiGiang> call, Response<BaiGiang> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tải thông tin bài giảng");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaiGiang> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void createBaiGiang(BaiGiang baiGiang, OnBaiGiangCallback callback) {
        RetrofitClient.getInstance().getApiService().createBaiGiang(getToken(), baiGiang)
                .enqueue(new Callback<BaiGiang>() {
                    @Override
                    public void onResponse(Call<BaiGiang> call, Response<BaiGiang> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tạo bài giảng mới");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaiGiang> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void updateBaiGiang(long id, BaiGiang baiGiang, OnBaiGiangCallback callback) {
        RetrofitClient.getInstance().getApiService().updateBaiGiang(getToken(), id, baiGiang)
                .enqueue(new Callback<BaiGiang>() {
                    @Override
                    public void onResponse(Call<BaiGiang> call, Response<BaiGiang> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể cập nhật bài giảng");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaiGiang> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void deleteBaiGiang(long id, Callback<Void> callback) {
        RetrofitClient.getInstance().getApiService().deleteBaiGiang(getToken(), id)
                .enqueue(callback);
    }

    public void searchBaiGiang(String keyword, OnBaiGiangListCallback callback) {
        RetrofitClient.getInstance().getApiService().searchBaiGiang(keyword)
                .enqueue(new Callback<List<BaiGiang>>() {
                    @Override
                    public void onResponse(Call<List<BaiGiang>> call, Response<List<BaiGiang>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            callback.onError("Không thể tìm kiếm bài giảng");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<BaiGiang>> call, Throwable t) {
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }
}