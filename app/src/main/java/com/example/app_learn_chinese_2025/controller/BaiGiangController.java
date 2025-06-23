package com.example.app_learn_chinese_2025.controller;

import android.content.Context;
import android.util.Log;

import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;

import java.util.List;

public class BaiGiangController {
    private static final String TAG = "BaiGiangController";

    private final BaiGiangRepository repository;
    private final SessionManager sessionManager;
    private final OnBaiGiangListener listener;

    public interface OnBaiGiangListener {
        void onBaiGiangListReceived(List<BaiGiang> baiGiangList);
        void onBaiGiangDetailReceived(BaiGiang baiGiang);
        void onBaiGiangCreated(BaiGiang baiGiang);
        void onBaiGiangUpdated(BaiGiang baiGiang);
        void onBaiGiangDeleted();
        void onError(String message);
    }

    public BaiGiangController(Context context, OnBaiGiangListener listener) {
        this.sessionManager = new SessionManager(context);
        this.repository = new BaiGiangRepository(context, sessionManager);
        this.listener = listener;
    }

    public void getBaiGiangList(Long giangVienId, Integer loaiBaiGiangId, Integer capDoHSK_ID, Integer chuDeId, Boolean published) {
        Log.d(TAG, "Getting BaiGiang list...");
        repository.getAllBaiGiang(giangVienId, loaiBaiGiangId, capDoHSK_ID, chuDeId, published, new BaiGiangRepository.OnBaiGiangListCallback() {
            @Override
            public void onSuccess(List<BaiGiang> baiGiangList) {
                Log.d(TAG, "Received " + baiGiangList.size() + " items");
                if (listener != null) {
                    listener.onBaiGiangListReceived(baiGiangList);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    public void getBaiGiangDetail(long id) {
        Log.d(TAG, "Getting BaiGiang detail for ID: " + id);
        repository.getBaiGiangById(id, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                Log.d(TAG, "Received BaiGiang: " + baiGiang.getTieuDe());
                if (listener != null) {
                    listener.onBaiGiangDetailReceived(baiGiang);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    public void getBaiGiangById(long id) {
        getBaiGiangDetail(id);
    }

    public void createBaiGiang(BaiGiang baiGiang) {
        if (sessionManager.getUserRole() != Constants.ROLE_TEACHER) {
            if (listener != null) {
                listener.onError("Chỉ giảng viên có quyền tạo bài giảng");
            }
            return;
        }
        Log.d(TAG, "Creating BaiGiang: " + baiGiang.getTieuDe());
        repository.createBaiGiang(baiGiang, sessionManager, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang createdBaiGiang) {
                Log.d(TAG, "Created BaiGiang: " + createdBaiGiang.getTieuDe());
                if (listener != null) {
                    listener.onBaiGiangCreated(createdBaiGiang);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    public void updateBaiGiang(long id, BaiGiang baiGiang) {
        if (sessionManager.getUserRole() != Constants.ROLE_TEACHER) {
            if (listener != null) {
                listener.onError("Chỉ giảng viên có quyền cập nhật bài giảng");
            }
            return;
        }
        Log.d(TAG, "Updating BaiGiang ID: " + id);
        repository.updateBaiGiang(id, baiGiang, sessionManager, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang updatedBaiGiang) {
                Log.d(TAG, "Updated BaiGiang: " + updatedBaiGiang.getTieuDe());
                if (listener != null) {
                    listener.onBaiGiangUpdated(updatedBaiGiang);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    public void deleteBaiGiang(long id) {
        int userRole = sessionManager.getUserRole();
        if (userRole != Constants.ROLE_ADMIN && userRole != Constants.ROLE_TEACHER) {
            if (listener != null) {
                listener.onError("Không có quyền xóa bài giảng");
            }
            return;
        }
        Log.d(TAG, "Deleting BaiGiang ID: " + id);
        repository.deleteBaiGiang(id, sessionManager, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                Log.d(TAG, "Deleted BaiGiang ID: " + id);
                if (listener != null) {
                    listener.onBaiGiangDeleted();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error: " + errorMessage);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }
}