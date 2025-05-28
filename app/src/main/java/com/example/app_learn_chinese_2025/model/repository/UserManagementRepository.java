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
    private SessionManager sessionManager;

    public interface OnUserCallback {
        void onSuccess(User user);

        void onError(String errorMessage);
    }

    public interface OnUserListCallback {
        void onSuccess(List<User> users);

        void onError(String errorMessage);
    }

    public UserManagementRepository(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    private String getToken() {
        return "Bearer " + sessionManager.getToken();
    }

    public void getUsersByRole(int role, OnUserListCallback callback) {
        Call<List<ApiService.UserResponse>> call;

        Log.d(TAG, "getUsersByRole called with role: " + role);

        if (role == 1) {
            // Teachers
            Log.d(TAG, "Fetching teachers...");
            call = RetrofitClient.getInstance().getApiService().getAllTeachers(getToken());
        } else {
            // Students
            Log.d(TAG, "Fetching students...");
            call = RetrofitClient.getInstance().getApiService().getAllStudents(getToken());
        }

        call.enqueue(new Callback<List<ApiService.UserResponse>>() {
            @Override
            public void onResponse(Call<List<ApiService.UserResponse>> call, Response<List<ApiService.UserResponse>> response) {
                Log.d(TAG, "API Response code: " + response.code());
                Log.d(TAG, "API Response successful: " + response.isSuccessful());

                if (response.isSuccessful() && response.body() != null) {
                    List<ApiService.UserResponse> responseList = response.body();
                    Log.d(TAG, "Response body size: " + responseList.size());

                    List<User> users = convertToUserList(responseList);
                    Log.d(TAG, "Converted users size: " + users.size());

                    callback.onSuccess(users);
                } else {
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e(TAG, "Get users failed: HTTP " + response.code() + ", Error: " + errorBody);
                        callback.onError("Không thể tải danh sách người dùng: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                        callback.onError("Không thể tải danh sách người dùng");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ApiService.UserResponse>> call, Throwable t) {
                Log.e(TAG, "Get users network error: " + t.getMessage(), t);
                callback.onError("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    // Get user by ID
    public void getUserById(long id, OnUserCallback callback) {
        RetrofitClient.getInstance().getApiService().getUserById(getToken(), id)
                .enqueue(new Callback<ApiService.UserResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.UserResponse> call, Response<ApiService.UserResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            User user = convertToUser(response.body());
                            callback.onSuccess(user);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                Log.e(TAG, "Get user failed: HTTP " + response.code() + ", Error: " + errorBody);
                                callback.onError("Không thể tải thông tin người dùng");
                            } catch (Exception e) {
                                callback.onError("Không thể tải thông tin người dùng");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.UserResponse> call, Throwable t) {
                        Log.e(TAG, "Get user network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Create new user
    public void createUser(User user, OnUserCallback callback) {
        ApiService.CreateUserRequest request = convertToCreateRequest(user);
        Log.d(TAG, "Creating user: " + new Gson().toJson(request));

        RetrofitClient.getInstance().getApiService().createUser(getToken(), request)
                .enqueue(new Callback<ApiService.UserResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.UserResponse> call, Response<ApiService.UserResponse> response) {
                        Log.d(TAG, "Create user response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            User createdUser = convertToUser(response.body());
                            Log.d(TAG, "Create user success: " + new Gson().toJson(createdUser));
                            callback.onSuccess(createdUser);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                Log.e(TAG, "Create user failed: HTTP " + response.code() + ", Error: " + errorBody);
                                callback.onError("Tạo người dùng thất bại: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                                callback.onError("Tạo người dùng thất bại");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.UserResponse> call, Throwable t) {
                        Log.e(TAG, "Create user network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Update user
    public void updateUser(long id, User user, OnUserCallback callback) {
        ApiService.UpdateUserRequest request = convertToUpdateRequest(user);
        Log.d(TAG, "Updating user: " + new Gson().toJson(request));

        RetrofitClient.getInstance().getApiService().updateUser(getToken(), id, request)
                .enqueue(new Callback<ApiService.UserResponse>() {
                    @Override
                    public void onResponse(Call<ApiService.UserResponse> call, Response<ApiService.UserResponse> response) {
                        Log.d(TAG, "Update user response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            User updatedUser = convertToUser(response.body());
                            Log.d(TAG, "Update user success: " + new Gson().toJson(updatedUser));
                            callback.onSuccess(updatedUser);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                Log.e(TAG, "Update user failed: HTTP " + response.code() + ", Error: " + errorBody);
                                callback.onError("Cập nhật người dùng thất bại: " + errorBody);
                            } catch (Exception e) {
                                Log.e(TAG, "Error parsing error body: " + e.getMessage());
                                callback.onError("Cập nhật người dùng thất bại");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.UserResponse> call, Throwable t) {
                        Log.e(TAG, "Update user network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Delete user
    public void deleteUser(long id, OnUserCallback callback) {
        RetrofitClient.getInstance().getApiService().deleteUser(getToken(), id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            // Return a dummy user object to indicate success
                            User deletedUser = new User();
                            deletedUser.setID(id);
                            callback.onSuccess(deletedUser);
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                Log.e(TAG, "Delete user failed: HTTP " + response.code() + ", Error: " + errorBody);
                                callback.onError("Xóa người dùng thất bại: " + errorBody);
                            } catch (Exception e) {
                                callback.onError("Xóa người dùng thất bại");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.e(TAG, "Delete user network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Toggle user status (active/inactive)
    public void toggleUserStatus(long id, OnUserCallback callback) {
        // First get current user to toggle status
        getUserById(id, new OnUserCallback() {
            @Override
            public void onSuccess(User user) {
                boolean newStatus = !user.getTrangThai();

                RetrofitClient.getInstance().getApiService().changeUserStatus(getToken(), id, newStatus)
                        .enqueue(new Callback<ApiService.UserResponse>() {
                            @Override
                            public void onResponse(Call<ApiService.UserResponse> call, Response<ApiService.UserResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    User updatedUser = convertToUser(response.body());
                                    callback.onSuccess(updatedUser);
                                } else {
                                    try {
                                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                        Log.e(TAG, "Toggle status failed: HTTP " + response.code() + ", Error: " + errorBody);
                                        callback.onError("Cập nhật trạng thái thất bại: " + errorBody);
                                    } catch (Exception e) {
                                        callback.onError("Cập nhật trạng thái thất bại");
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiService.UserResponse> call, Throwable t) {
                                Log.e(TAG, "Toggle status network error: " + t.getMessage(), t);
                                callback.onError("Lỗi kết nối: " + t.getMessage());
                            }
                        });
            }

            @Override
            public void onError(String errorMessage) {
                callback.onError(errorMessage);
            }
        });
    }

    // Converter methods
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

            // Handle Long ID
            if (userResponse.getId() != null) {
                user.setID(userResponse.getId());
            }

            // Handle String fields
            user.setTenDangNhap(userResponse.getTenDangNhap() != null ? userResponse.getTenDangNhap() : "");
            user.setEmail(userResponse.getEmail());
            user.setHoTen(userResponse.getHoTen() != null ? userResponse.getHoTen() : "");
            user.setSoDienThoai(userResponse.getSoDienThoai());
            user.setHinhDaiDien(userResponse.getHinhDaiDien());

            // Handle Integer fields
            user.setVaiTro(userResponse.getVaiTro() != null ? userResponse.getVaiTro() : 2);
            user.setTrinhDoHSK(userResponse.getTrinhDoHSK() != null ? userResponse.getTrinhDoHSK() : 0);

            // Handle Boolean field
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