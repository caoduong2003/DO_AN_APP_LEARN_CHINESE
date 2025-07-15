package com.example.app_learn_chinese_2025.controller;

import android.content.Context;
import android.util.Log;

import com.example.app_learn_chinese_2025.model.remote.ApiService;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 🎯 Controller cho Teacher BaiGiang CRUD operations
 * Kết nối Android app với backend APIs
 */
public class TeacherBaiGiangController {
    private static final String TAG = "TeacherBaiGiangController";

    private Context context;
    private SessionManager sessionManager;
    private ApiService apiService;
    private OnTeacherBaiGiangListener listener;

    // ===== CONSTRUCTOR =====

    public TeacherBaiGiangController(Context context, OnTeacherBaiGiangListener listener) {
        this.context = context;
        this.listener = listener;
        this.sessionManager = new SessionManager(context);
        this.apiService = RetrofitClient.getInstance(sessionManager).getApiService();
    }

    // ===== INTERFACE FOR CALLBACKS =====

    public interface OnTeacherBaiGiangListener {
        void onBaiGiangListReceived(ApiService.TeacherBaiGiangResponse.PageResponse response);
        void onBaiGiangDetailReceived(ApiService.TeacherBaiGiangResponse.DetailResponse baiGiang);
        void onBaiGiangCreated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang);
        void onBaiGiangUpdated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang);
        void onBaiGiangDeleted();
        void onStatusToggled(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang);
        void onBaiGiangDuplicated(ApiService.TeacherBaiGiangResponse.SimpleResponse baiGiang);
        void onStatisticsReceived(ApiService.TeacherBaiGiangResponse.StatsResponse stats);
        void onSearchResultReceived(List<ApiService.TeacherBaiGiangResponse.SimpleResponse> results);
        void onError(String message);
    }

    // ===== HELPER METHODS =====

    private String getAuthToken() {
        String token = sessionManager.getToken();
        return token != null ? "Bearer " + token : "";
    }

    private void handleError(String operation, Throwable t) {
        String message = "Lỗi " + operation + ": " + (t.getMessage() != null ? t.getMessage() : "Không xác định");
        Log.e(TAG, message, t);
        if (listener != null) {
            listener.onError(message);
        }
    }

    private void handleApiError(String operation, Response<?> response) {
        String message = "Lỗi " + operation + ": HTTP " + response.code();
        try {
            if (response.errorBody() != null) {
                message += " - " + response.errorBody().string();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error parsing error body", e);
        }
        Log.e(TAG, message);
        if (listener != null) {
            listener.onError(message);
        }
    }

    // ===== GET OPERATIONS =====

    /**
     * Lấy danh sách bài giảng với phân trang và filter
     */
    public void getMyBaiGiangs(int page, int size, String sortBy, String sortDir,
                               String search, Integer capDoHSKId, Integer chuDeId,
                               Integer loaiBaiGiangId, Boolean trangThai) {
        Log.d(TAG, "🌐 Getting bai giang list - page: " + page + ", size: " + size);

        apiService.getTeacherBaiGiangs(getAuthToken(), page, size, sortBy, sortDir,
                        search, capDoHSKId, chuDeId, loaiBaiGiangId, trangThai)
                .enqueue(new Callback<ApiService.TeacherBaiGiangResponse.PageResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.TeacherBaiGiangResponse.PageResponse> call,
                                           Response<ApiService.TeacherBaiGiangResponse.PageResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "✅ Successfully got bai giang list with " +
                                    response.body().getContent().size() + " items");
                            if (listener != null) {
                                listener.onBaiGiangListReceived(response.body());
                            }
                        } else {
                            handleApiError("lấy danh sách bài giảng", response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.TeacherBaiGiangResponse.PageResponse> call, Throwable t) {
                        handleError("lấy danh sách bài giảng", t);
                    }
                });
    }

    /**
     * Lấy chi tiết bài giảng
     */
    public void getBaiGiangDetail(Long id) {
        Log.d(TAG, "🌐 Getting bai giang detail for ID: " + id);

        apiService.getTeacherBaiGiangDetail(getAuthToken(), id)
                .enqueue(new Callback<ApiService.TeacherBaiGiangResponse.DetailResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.TeacherBaiGiangResponse.DetailResponse> call,
                                           Response<ApiService.TeacherBaiGiangResponse.DetailResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "✅ Successfully got bai giang detail: " + response.body().getTieuDe());
                            if (listener != null) {
                                listener.onBaiGiangDetailReceived(response.body());
                            }
                        } else {
                            handleApiError("lấy chi tiết bài giảng", response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.TeacherBaiGiangResponse.DetailResponse> call, Throwable t) {
                        handleError("lấy chi tiết bài giảng", t);
                    }
                });
    }

    /**
     * Lấy thống kê bài giảng
     */
    public void getStatistics() {
        Log.d(TAG, "🌐 Getting bai giang statistics");

        apiService.getTeacherBaiGiangStatistics(getAuthToken())
                .enqueue(new Callback<ApiService.TeacherBaiGiangResponse.StatsResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.TeacherBaiGiangResponse.StatsResponse> call,
                                           Response<ApiService.TeacherBaiGiangResponse.StatsResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "✅ Successfully got statistics: " + response.body().getTongSoBaiGiang() + " total");
                            if (listener != null) {
                                listener.onStatisticsReceived(response.body());
                            }
                        } else {
                            handleApiError("lấy thống kê", response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.TeacherBaiGiangResponse.StatsResponse> call, Throwable t) {
                        handleError("lấy thống kê", t);
                    }
                });
    }

    /**
     * Tìm kiếm bài giảng
     */
    public void searchBaiGiangs(String keyword, int limit) {
        Log.d(TAG, "🔍 Searching bai giang with keyword: " + keyword);

        apiService.searchTeacherBaiGiangs(getAuthToken(), keyword, limit)
                .enqueue(new Callback<List<ApiService.TeacherBaiGiangResponse.SimpleResponse>>() {
                    @Override
                    public void onResponse(Call<List<ApiService.TeacherBaiGiangResponse.SimpleResponse>> call,
                                           Response<List<ApiService.TeacherBaiGiangResponse.SimpleResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "✅ Search found " + response.body().size() + " results");
                            if (listener != null) {
                                listener.onSearchResultReceived(response.body());
                            }
                        } else {
                            handleApiError("tìm kiếm", response);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ApiService.TeacherBaiGiangResponse.SimpleResponse>> call, Throwable t) {
                        handleError("tìm kiếm", t);
                    }
                });
    }

    // ===== CREATE OPERATIONS =====

    /**
     * Tạo bài giảng mới
     */
    public void createBaiGiang(ApiService.TeacherBaiGiangRequest.CreateRequest request) {
        Log.d(TAG, "🌐 Creating new bai giang: " + request.getTieuDe());

        apiService.createTeacherBaiGiang(getAuthToken(), request)
                .enqueue(new Callback<ApiService.TeacherBaiGiangResponse.SimpleResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.TeacherBaiGiangResponse.SimpleResponse> call,
                                           Response<ApiService.TeacherBaiGiangResponse.SimpleResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "✅ Successfully created bai giang: " + response.body().getTieuDe());
                            if (listener != null) {
                                listener.onBaiGiangCreated(response.body());
                            }
                        } else {
                            handleApiError("tạo bài giảng", response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.TeacherBaiGiangResponse.SimpleResponse> call, Throwable t) {
                        handleError("tạo bài giảng", t);
                    }
                });
    }

    /**
     * Nhân bản bài giảng
     */
    public void duplicateBaiGiang(Long id, String newTitle) {
        Log.d(TAG, "🌐 Duplicating bai giang ID: " + id);

        Map<String, String> options = new HashMap<>();
        if (newTitle != null && !newTitle.trim().isEmpty()) {
            options.put("newTitle", newTitle.trim());
        }

        apiService.duplicateTeacherBaiGiang(getAuthToken(), id, options)
                .enqueue(new Callback<ApiService.TeacherBaiGiangResponse.SimpleResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.TeacherBaiGiangResponse.SimpleResponse> call,
                                           Response<ApiService.TeacherBaiGiangResponse.SimpleResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "✅ Successfully duplicated bai giang: " + response.body().getTieuDe());
                            if (listener != null) {
                                listener.onBaiGiangDuplicated(response.body());
                            }
                        } else {
                            handleApiError("nhân bản bài giảng", response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.TeacherBaiGiangResponse.SimpleResponse> call, Throwable t) {
                        handleError("nhân bản bài giảng", t);
                    }
                });
    }

    // ===== UPDATE OPERATIONS =====

    /**
     * Cập nhật bài giảng
     */
    public void updateBaiGiang(Long id, ApiService.TeacherBaiGiangRequest.UpdateRequest request) {
        Log.d(TAG, "🌐 Updating bai giang ID: " + id);

        apiService.updateTeacherBaiGiang(getAuthToken(), id, request)
                .enqueue(new Callback<ApiService.TeacherBaiGiangResponse.SimpleResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.TeacherBaiGiangResponse.SimpleResponse> call,
                                           Response<ApiService.TeacherBaiGiangResponse.SimpleResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "✅ Successfully updated bai giang: " + response.body().getTieuDe());
                            if (listener != null) {
                                listener.onBaiGiangUpdated(response.body());
                            }
                        } else {
                            handleApiError("cập nhật bài giảng", response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.TeacherBaiGiangResponse.SimpleResponse> call, Throwable t) {
                        handleError("cập nhật bài giảng", t);
                    }
                });
    }

    /**
     * Thay đổi trạng thái công khai/ẩn
     */
    public void toggleStatus(Long id) {
        Log.d(TAG, "🌐 Toggling status for bai giang ID: " + id);

        apiService.toggleTeacherBaiGiangStatus(getAuthToken(), id)
                .enqueue(new Callback<ApiService.TeacherBaiGiangResponse.SimpleResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.TeacherBaiGiangResponse.SimpleResponse> call,
                                           Response<ApiService.TeacherBaiGiangResponse.SimpleResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "✅ Successfully toggled status: " + response.body().getTrangThai());
                            if (listener != null) {
                                listener.onStatusToggled(response.body());
                            }
                        } else {
                            handleApiError("thay đổi trạng thái", response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.TeacherBaiGiangResponse.SimpleResponse> call, Throwable t) {
                        handleError("thay đổi trạng thái", t);
                    }
                });
    }

    /**
     * Thay đổi trạng thái premium
     */
    public void togglePremium(Long id) {
        Log.d(TAG, "🌐 Toggling premium for bai giang ID: " + id);

        apiService.toggleTeacherBaiGiangPremium(getAuthToken(), id)
                .enqueue(new Callback<ApiService.TeacherBaiGiangResponse.SimpleResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.TeacherBaiGiangResponse.SimpleResponse> call,
                                           Response<ApiService.TeacherBaiGiangResponse.SimpleResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "✅ Successfully toggled premium: " + response.body().getLaBaiGiangGoi());
                            if (listener != null) {
                                listener.onStatusToggled(response.body()); // Reuse same callback
                            }
                        } else {
                            handleApiError("thay đổi trạng thái premium", response);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.TeacherBaiGiangResponse.SimpleResponse> call, Throwable t) {
                        handleError("thay đổi trạng thái premium", t);
                    }
                });
    }

    // ===== DELETE OPERATIONS =====

    /**
     * Xóa bài giảng (soft delete)
     */
    public void deleteBaiGiang(Long id) {
        Log.d(TAG, "🌐 Deleting bai giang ID: " + id);

        apiService.deleteTeacherBaiGiang(getAuthToken(), id)
                .enqueue(new Callback<Map<String, String>>() {
                    @Override
                    public void onResponse(Call<Map<String, String>> call,
                                           Response<Map<String, String>> response) {
                        if (response.isSuccessful()) {
                            Log.d(TAG, "✅ Successfully deleted bai giang ID: " + id);
                            if (listener != null) {
                                listener.onBaiGiangDeleted();
                            }
                        } else {
                            handleApiError("xóa bài giảng", response);
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, String>> call, Throwable t) {
                        handleError("xóa bài giảng", t);
                    }
                });
    }

    // ===== CONVENIENCE METHODS =====

    /**
     * Lấy danh sách bài giảng với tham số mặc định
     */
    public void getMyBaiGiangs() {
        getMyBaiGiangs(0, 10, "ngayTao", "desc", null, null, null, null, null);
    }

    /**
     * Lấy danh sách bài giảng với trang cụ thể
     */
    public void getMyBaiGiangs(int page) {
        getMyBaiGiangs(page, 10, "ngayTao", "desc", null, null, null, null, null);
    }

    /**
     * Tìm kiếm với limit mặc định
     */
    public void searchBaiGiangs(String keyword) {
        searchBaiGiangs(keyword, 10);
    }

    /**
     * Tạo request từ các tham số cơ bản
     */
    public ApiService.TeacherBaiGiangRequest.CreateRequest createRequest(
            String tieuDe, String moTa, String noiDung) {
        return new ApiService.TeacherBaiGiangRequest.CreateRequest(tieuDe, moTa, noiDung);
    }

    /**
     * Tạo update request từ create request
     */
    public ApiService.TeacherBaiGiangRequest.UpdateRequest createUpdateRequest(
            String tieuDe, String moTa, String noiDung) {
        ApiService.TeacherBaiGiangRequest.UpdateRequest request =
                new ApiService.TeacherBaiGiangRequest.UpdateRequest();
        request.setTieuDe(tieuDe);
        request.setMoTa(moTa);
        request.setNoiDung(noiDung);
        return request;
    }

}