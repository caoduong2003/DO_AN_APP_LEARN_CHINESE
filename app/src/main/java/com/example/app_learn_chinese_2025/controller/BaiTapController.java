package com.example.app_learn_chinese_2025.controller;

import android.util.Log;
import com.example.app_learn_chinese_2025.model.data.BaiTap;
import com.example.app_learn_chinese_2025.model.data.KetQuaBaiTap;
import com.example.app_learn_chinese_2025.model.request.LamBaiTapRequest;
import com.example.app_learn_chinese_2025.model.response.ApiResponse;
import com.example.app_learn_chinese_2025.model.remote.ApiService;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.List;

public class BaiTapController {
    private static final String TAG = "BAI_TAP_CONTROLLER";

    private ApiService apiService;
    private SessionManager sessionManager;
    private BaiTapControllerListener listener;

    public interface BaiTapControllerListener {
        void onBaiTapListLoaded(List<BaiTap> baiTapList);
        void onBaiTapDetailLoaded(BaiTap baiTap);
        void onBaiTapSubmitted(KetQuaBaiTap ketQua);
        void onKetQuaListLoaded(List<KetQuaBaiTap> ketQuaList);
        void onPingSuccess(String message);
        void onError(String error);
    }

    public BaiTapController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.apiService = RetrofitClient.getInstance(sessionManager).getApiService();
    }

    public void setListener(BaiTapControllerListener listener) {
        this.listener = listener;
    }

    /**
     * Test API connection
     */
    public void pingBaiTap() {
        Log.d(TAG, "Pinging bai tap API");

        Call<ApiResponse<String>> call = apiService.pingBaiTap();
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccessful()) {
                        Log.d(TAG, "Ping successful: " + apiResponse.getData());
                        if (listener != null) {
                            listener.onPingSuccess(apiResponse.getMessage());
                        }
                    } else {
                        Log.e(TAG, "Ping API error: " + apiResponse.getMessage());
                        if (listener != null) {
                            listener.onError(apiResponse.getErrorMessage());
                        }
                    }
                } else {
                    Log.e(TAG, "Ping HTTP error: " + response.code());
                    if (listener != null) {
                        listener.onError("Lỗi kết nối API: " + response.code());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Log.e(TAG, "Ping network error", t);
                if (listener != null) {
                    listener.onError("Lỗi mạng: " + t.getMessage());
                }
            }
        });
    }

    /**
     * Lấy danh sách bài tập
     */
    public void getBaiTapList(Integer capDoHSKId, Integer chuDeId) {
        Log.d(TAG, "Getting bai tap list - CapDoHSK: " + capDoHSKId + ", ChuDe: " + chuDeId);

        Call<ApiResponse<List<BaiTap>>> call = apiService.getBaiTapList(capDoHSKId, chuDeId);
        call.enqueue(new Callback<ApiResponse<List<BaiTap>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BaiTap>>> call, Response<ApiResponse<List<BaiTap>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<BaiTap>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.hasData()) {
                        Log.d(TAG, "Bai tap list loaded successfully: " + apiResponse.getData().size() + " items");
                        if (listener != null) {
                            listener.onBaiTapListLoaded(apiResponse.getData());
                        }
                    } else {
                        Log.e(TAG, "Bai tap list API error: " + apiResponse.getMessage());
                        if (listener != null) {
                            listener.onError(apiResponse.getErrorMessage());
                        }
                    }
                } else {
                    Log.e(TAG, "Bai tap list HTTP error: " + response.code());
                    if (listener != null) {
                        listener.onError("Không thể tải danh sách bài tập");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BaiTap>>> call, Throwable t) {
                Log.e(TAG, "Network error loading bai tap list", t);
                if (listener != null) {
                    listener.onError("Lỗi kết nối mạng");
                }
            }
        });
    }

    /**
     * Lấy chi tiết bài tập
     */
    public void getBaiTapDetail(Long baiTapId) {
        Log.d(TAG, "Getting bai tap detail for ID: " + baiTapId);

        Call<ApiResponse<BaiTap>> call = apiService.getBaiTapDetail(baiTapId);
        call.enqueue(new Callback<ApiResponse<BaiTap>>() {
            @Override
            public void onResponse(Call<ApiResponse<BaiTap>> call, Response<ApiResponse<BaiTap>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<BaiTap> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.hasData()) {
                        Log.d(TAG, "Bai tap detail loaded successfully");
                        if (listener != null) {
                            listener.onBaiTapDetailLoaded(apiResponse.getData());
                        }
                    } else {
                        Log.e(TAG, "Bai tap detail API error: " + apiResponse.getMessage());
                        if (listener != null) {
                            listener.onError(apiResponse.getErrorMessage());
                        }
                    }
                } else {
                    Log.e(TAG, "Bai tap detail HTTP error: " + response.code());
                    if (listener != null) {
                        listener.onError("Không thể tải chi tiết bài tập");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BaiTap>> call, Throwable t) {
                Log.e(TAG, "Network error loading bai tap detail", t);
                if (listener != null) {
                    listener.onError("Lỗi kết nối mạng");
                }
            }
        });
    }

    /**
     * Nộp bài tập
     */
    public void submitBaiTap(LamBaiTapRequest request) {
        Log.d(TAG, "Submitting bai tap: " + request.getBaiTapId());

        Call<ApiResponse<KetQuaBaiTap>> call = apiService.submitBaiTap(request);
        call.enqueue(new Callback<ApiResponse<KetQuaBaiTap>>() {
            @Override
            public void onResponse(Call<ApiResponse<KetQuaBaiTap>> call, Response<ApiResponse<KetQuaBaiTap>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<KetQuaBaiTap> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.hasData()) {
                        Log.d(TAG, "Bai tap submitted successfully");
                        if (listener != null) {
                            listener.onBaiTapSubmitted(apiResponse.getData());
                        }
                    } else {
                        Log.e(TAG, "Submit API error: " + apiResponse.getMessage());
                        if (listener != null) {
                            listener.onError(apiResponse.getErrorMessage());
                        }
                    }
                } else {
                    Log.e(TAG, "Submit HTTP error: " + response.code());
                    if (listener != null) {
                        listener.onError("Không thể nộp bài tập");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<KetQuaBaiTap>> call, Throwable t) {
                Log.e(TAG, "Network error submitting bai tap", t);
                if (listener != null) {
                    listener.onError("Lỗi kết nối mạng");
                }
            }
        });
    }

    /**
     * Lấy danh sách kết quả bài tập
     */
    public void getKetQuaList() {
        Log.d(TAG, "Getting ket qua list");

        Call<ApiResponse<List<KetQuaBaiTap>>> call = apiService.getKetQuaBaiTap();
        call.enqueue(new Callback<ApiResponse<List<KetQuaBaiTap>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<KetQuaBaiTap>>> call, Response<ApiResponse<List<KetQuaBaiTap>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<KetQuaBaiTap>> apiResponse = response.body();
                    if (apiResponse.isSuccessful() && apiResponse.hasData()) {
                        Log.d(TAG, "Ket qua list loaded successfully: " + apiResponse.getData().size() + " items");
                        if (listener != null) {
                            listener.onKetQuaListLoaded(apiResponse.getData());
                        }
                    } else {
                        Log.e(TAG, "Ket qua list API error: " + apiResponse.getMessage());
                        if (listener != null) {
                            listener.onError(apiResponse.getErrorMessage());
                        }
                    }
                } else {
                    Log.e(TAG, "Ket qua list HTTP error: " + response.code());
                    if (listener != null) {
                        listener.onError("Không thể tải danh sách kết quả");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<KetQuaBaiTap>>> call, Throwable t) {
                Log.e(TAG, "Network error loading ket qua list", t);
                if (listener != null) {
                    listener.onError("Lỗi kết nối mạng");
                }
            }
        });
    }
}