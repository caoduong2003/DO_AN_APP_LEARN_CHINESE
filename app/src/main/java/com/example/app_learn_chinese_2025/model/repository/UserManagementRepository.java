package com.example.app_learn_chinese_2025.model.repository;

import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.gson.Gson;

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

    // Get all users by role
    public void getUsersByRole(int role, OnUserListCallback callback) {
        RetrofitClient.getInstance().getApiService().getUsersByRole(getToken(), role)
                .enqueue(new Callback<List<User>>() {
                    @Override
                    public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                                Log.e(TAG, "Get users failed: HTTP " + response.code() + ", Error: " + errorBody);
                                callback.onError("Không thể tải danh sách người dùng");
                            } catch (Exception e) {
                                callback.onError("Không thể tải danh sách người dùng");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<User>> call, Throwable t) {
                        Log.e(TAG, "Get users network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Get user by ID
    public void getUserById(long id, OnUserCallback callback) {
        RetrofitClient.getInstance().getApiService().getUserById(getToken(), id)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
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
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Get user network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Create new user
    public void createUser(User user, OnUserCallback callback) {
        Log.d(TAG, "Creating user: " + new Gson().toJson(user));

        RetrofitClient.getInstance().getApiService().createUser(getToken(), user)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d(TAG, "Create user response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Create user success: " + new Gson().toJson(response.body()));
                            callback.onSuccess(response.body());
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
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Create user network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Update user
    public void updateUser(long id, User user, OnUserCallback callback) {
        Log.d(TAG, "Updating user: " + new Gson().toJson(user));

        RetrofitClient.getInstance().getApiService().updateUser(getToken(), id, user)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d(TAG, "Update user response code: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Update user success: " + new Gson().toJson(response.body()));
                            callback.onSuccess(response.body());
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
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Update user network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Delete user
    public void deleteUser(long id, OnUserCallback callback) {
        RetrofitClient.getInstance().getApiService().deleteUser(getToken(), id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
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
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(TAG, "Delete user network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    // Toggle user status (active/inactive)
    public void toggleUserStatus(long id, OnUserCallback callback) {
        RetrofitClient.getInstance().getApiService().toggleUserStatus(getToken(), id)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            callback.onSuccess(response.body());
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
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, "Toggle status network error: " + t.getMessage(), t);
                        callback.onError("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }
}