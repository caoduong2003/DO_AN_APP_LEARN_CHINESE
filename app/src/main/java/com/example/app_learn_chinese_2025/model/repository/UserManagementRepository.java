package com.example.app_learn_chinese_2025.model.repository;

import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.remote.ApiService;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManagementRepository {
    private static final String TAG = "UserManagementRepository";
    private final SessionManager sessionManager;

    public interface OnUserCallback {
        void onSuccess(User user);
        void onError(String errorMessage);
    }

    public interface OnUserListCallback {
        void onSuccess(List<User> users);
        void onError(String errorMessage);
    }

    public interface OnStatusChangeCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public UserManagementRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private String getToken() {
        String token = sessionManager.getToken();
        return token != null ? "Bearer " + token : "";
    }

    public void getUsersByRole(int role, OnUserListCallback callback) {
        Call<List<ApiService.UserResponse>> call;

        Log.d(TAG, "getUsersByRole called with role: " + role);

        if (role == 1) {
            // Teachers
            Log.d(TAG, "Fetching teachers...");
            call = RetrofitClient.getInstance(sessionManager).getApiService().getAllTeachers(getToken());
        } else {
            // Students
            Log.d(TAG, "Fetching students...");
            call = RetrofitClient.getInstance(sessionManager).getApiService().getAllStudents(getToken());
        }

        call.enqueue(new Callback<List<ApiService.UserResponse>>() {
            @Override
            public void onResponse(Call<List<ApiService.UserResponse>> call, Response<List<ApiService.UserResponse>> response) {
                Log.d(TAG, "getUsersByRole URL: " + call.request().url());
                Log.d(TAG, "API Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    List<ApiService.UserResponse> responseList = response.body();
                    Log.d(TAG, "Response body size: " + responseList.size());
                    List<User> users = convertToUserList(responseList);
                    callback.onSuccess(users);
                } else {
                    String errorMessage = "Không thể tải danh sách người dùng: HTTP " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += " - " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                    }
                    Log.e(TAG, errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<List<ApiService.UserResponse>> call, Throwable t) {
                String errorMessage = "Lỗi kết nối: " + t.getMessage();
                Log.e(TAG, errorMessage, t);
                callback.onError(errorMessage);
            }
        });
    }

    public void getUserById(long id, OnUserCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().getUserById(getToken(), id)
                .enqueue(new Callback<ApiService.UserResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.UserResponse> call, Response<ApiService.UserResponse> response) {
                        Log.d(TAG, "getUserById URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            User user = convertToUser(response.body());
                            callback.onSuccess(user);
                        } else {
                            String errorMessage = "Không thể tải thông tin người dùng: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.UserResponse> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void createUser(User user, OnUserCallback callback) {
        ApiService.CreateUserRequest request = convertToCreateRequest(user);
        Log.d(TAG, "Creating user: " + new Gson().toJson(request));

        RetrofitClient.getInstance(sessionManager).getApiService().createUser(getToken(), request)
                .enqueue(new Callback<ApiService.UserResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.UserResponse> call, Response<ApiService.UserResponse> response) {
                        Log.d(TAG, "createUser URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            User createdUser = convertToUser(response.body());
                            callback.onSuccess(createdUser);
                        } else {
                            String errorMessage = "Tạo người dùng thất bại: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.UserResponse> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void updateUser(long id, User user, OnUserCallback callback) {
        ApiService.UpdateUserRequest request = convertToUpdateRequest(user);
        Log.d(TAG, "Updating user: " + new Gson().toJson(request));

        RetrofitClient.getInstance(sessionManager).getApiService().updateUser(getToken(), id, request)
                .enqueue(new Callback<ApiService.UserResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.UserResponse> call, Response<ApiService.UserResponse> response) {
                        Log.d(TAG, "updateUser URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            User updatedUser = convertToUser(response.body());
                            callback.onSuccess(updatedUser);
                        } else {
                            String errorMessage = "Cập nhật người dùng thất bại: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.UserResponse> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void deleteUser(long id, OnUserCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().deleteUser(getToken(), id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d(TAG, "deleteUser URL: " + call.request().url());
                        if (response.isSuccessful()) {
                            User deletedUser = new User();
                            deletedUser.setID(id);
                            callback.onSuccess(deletedUser);
                        } else {
                            String errorMessage = "Xóa người dùng thất bại: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void toggleUserStatus(long id, boolean newStatus, OnStatusChangeCallback callback) {
        RetrofitClient.getInstance(sessionManager).getApiService().changeUserStatus(getToken(), id, newStatus)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Log.d(TAG, "toggleUserStatus URL: " + call.request().url());
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            String errorMessage = "Cập nhật trạng thái thất bại: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    public void updateUserStatus(User user, boolean trangThai, OnUserCallback callback) {
        Log.d(TAG, "Updating user status for ID: " + user.getID() + ", trangThai: " + trangThai);
        user.setTrangThai(trangThai); // Cập nhật trạng thái trong đối tượng User
        ApiService.UpdateUserRequest request = convertToUpdateRequest(user);
        Log.d(TAG, "Update user status request: " + new Gson().toJson(request));

        RetrofitClient.getInstance(sessionManager).getApiService().updateUser(getToken(), user.getID(), request)
                .enqueue(new Callback<ApiService.UserResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.UserResponse> call, Response<ApiService.UserResponse> response) {
                        Log.d(TAG, "updateUserStatus URL: " + call.request().url());
                        if (response.isSuccessful() && response.body() != null) {
                            User updatedUser = convertToUser(response.body());
                            Log.d(TAG, "Updated user status for: " + updatedUser.getTenDangNhap() + ", trangThai: " + updatedUser.getTrangThai());
                            callback.onSuccess(updatedUser);
                        } else {
                            String errorMessage = "Cập nhật trạng thái người dùng thất bại: HTTP " + response.code();
                            try {
                                if (response.errorBody() != null) {
                                    errorMessage += " - " + response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                            }
                            Log.e(TAG, errorMessage);
                            callback.onError(errorMessage);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.UserResponse> call, Throwable t) {
                        String errorMessage = "Lỗi kết nối: " + t.getMessage();
                        Log.e(TAG, errorMessage, t);
                        callback.onError(errorMessage);
                    }
                });
    }

    private List<User> convertToUserList(List<ApiService.UserResponse> userResponses) {
        List<User> users = new ArrayList<>();
        for (ApiService.UserResponse userResponse : userResponses) {
            users.add(convertToUser(userResponse));
        }
        return users;
    }

    private User convertToUser(ApiService.UserResponse userResponse) {
        try {
            User user = new User();
            if (userResponse.getId() != null) {
                user.setID(userResponse.getId());
            }
            user.setTenDangNhap(userResponse.getTenDangNhap() != null ? userResponse.getTenDangNhap() : "");
            user.setEmail(userResponse.getEmail());
            user.setHoTen(userResponse.getHoTen() != null ? userResponse.getHoTen() : "");
            user.setSoDienThoai(userResponse.getSoDienThoai());
            user.setHinhDaiDien(userResponse.getHinhDaiDien());
            user.setVaiTro(userResponse.getVaiTro() != null ? userResponse.getVaiTro() : 2);
            user.setTrinhDoHSK(userResponse.getTrinhDoHSK() != null ? userResponse.getTrinhDoHSK() : 0);
            user.setTrangThai(userResponse.getTrangThai() != null ? userResponse.getTrangThai() : true);
            Log.d(TAG, "Converted user: " + user.getTenDangNhap() + ", Role: " + user.getVaiTro());
            return user;
        } catch (Exception e) {
            Log.e(TAG, "Error converting user: " + e.getMessage(), e);
            throw e;
        }
    }

    private ApiService.CreateUserRequest convertToCreateRequest(User user) {
        ApiService.CreateUserRequest request = new ApiService.CreateUserRequest();
        request.setTenDangNhap(user.getTenDangNhap());
        request.setEmail(user.getEmail());
        request.setMatKhau(user.getMatKhau());
        request.setHoTen(user.getHoTen());
        request.setSoDienThoai(user.getSoDienThoai());
        request.setVaiTro(user.getVaiTro());
        request.setTrinhDoHSK(user.getTrinhDoHSK());
        request.setHinhDaiDien(user.getHinhDaiDien());
        request.setTrangThai(user.getTrangThai());
        return request;
    }

    private ApiService.UpdateUserRequest convertToUpdateRequest(User user) {
        ApiService.UpdateUserRequest request = new ApiService.UpdateUserRequest();
        request.setEmail(user.getEmail());
        request.setHoTen(user.getHoTen());
        request.setSoDienThoai(user.getSoDienThoai());
        request.setVaiTro(user.getVaiTro());
        request.setTrinhDoHSK(user.getTrinhDoHSK());
        request.setHinhDaiDien(user.getHinhDaiDien());
        request.setTrangThai(user.getTrangThai());
        return request;
    }
}