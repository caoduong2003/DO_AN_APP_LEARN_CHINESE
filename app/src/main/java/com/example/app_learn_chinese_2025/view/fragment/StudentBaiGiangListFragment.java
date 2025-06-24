// ✅ ENHANCED StudentBaiGiangListFragment.java - Hỗ trợ Video và Audio

package com.example.app_learn_chinese_2025.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.controller.BaiGiangController;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.view.activity.StudentDashboardActivity;
import com.example.app_learn_chinese_2025.view.adapter.BaiGiangAdapter;

import java.util.ArrayList;
import java.util.List;

public class StudentBaiGiangListFragment extends Fragment implements BaiGiangAdapter.OnBaiGiangItemClickListener, BaiGiangController.OnBaiGiangListener {
    private static final String TAG = "StudentBaiGiangListFragment";

    private RecyclerView rvBaiGiang;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefresh;
    private BaiGiangAdapter adapter;
    private BaiGiangController baiGiangController;
    private List<BaiGiang> baiGiangList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "🔄 onCreateView started");
        View view = inflater.inflate(R.layout.fragment_student_bai_giang_list, container, false);
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        loadBaiGiangs();
        Log.d(TAG, "✅ onCreateView completed");
        return view;
    }

    private void initViews(View view) {
        Log.d(TAG, "🔧 initViews started");
        rvBaiGiang = view.findViewById(R.id.rvBaiGiang);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmptyState = view.findViewById(R.id.tvEmptyState);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        baiGiangController = new BaiGiangController(requireContext(), this);
        baiGiangList = new ArrayList<>();

        // Hide loading components initially
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);

        Log.d(TAG, "✅ initViews completed");
    }

    private void setupRecyclerView() {
        Log.d(TAG, "🔧 setupRecyclerView started");
        adapter = new BaiGiangAdapter(requireContext(), baiGiangList, this);
        rvBaiGiang.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvBaiGiang.setAdapter(adapter);

        Log.d(TAG, "📋 Adapter created with " + baiGiangList.size() + " items");
        Log.d(TAG, "✅ setupRecyclerView completed");
    }

    private void setupSwipeRefresh() {
        Log.d(TAG, "🔧 setupSwipeRefresh started");
        if (swipeRefresh != null) {
            swipeRefresh.setOnRefreshListener(this::loadBaiGiangs);
            swipeRefresh.setColorSchemeResources(
                    android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light
            );
        }
        Log.d(TAG, "✅ setupSwipeRefresh completed");
    }

    private void loadBaiGiangs() {
        Log.d(TAG, "🌐 loadBaiGiangs started");

        if (!isNetworkAvailable()) {
            Toast.makeText(requireContext(), "Không có kết nối mạng", Toast.LENGTH_SHORT).show();
            swipeRefresh.setRefreshing(false);
            showEmptyState(true, "Không có kết nối mạng");
            return;
        }

        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(true);
        }
        showEmptyState(false, "");

        Log.d(TAG, "📡 Calling API: getBaiGiangList(null, null, null, null, true)");
        baiGiangController.getBaiGiangList(null, null, null, null, true); // Load published lessons
    }

    private boolean isNetworkAvailable() {
        try {
            android.net.ConnectivityManager cm = (android.net.ConnectivityManager) requireContext().getSystemService(android.content.Context.CONNECTIVITY_SERVICE);
            android.net.Network network = cm.getActiveNetwork();
            return network != null;
        } catch (Exception e) {
            Log.e(TAG, "Error checking network: " + e.getMessage());
            return false;
        }
    }

    private void showEmptyState(boolean show, String message) {
        Log.d(TAG, "📋 showEmptyState: " + show + ", message: " + message);

        if (tvEmptyState != null) {
            tvEmptyState.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show && message != null && !message.isEmpty()) {
                tvEmptyState.setText(message);
            }
        }

        if (rvBaiGiang != null) {
            rvBaiGiang.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // ✅ IMPLEMENT: OnBaiGiangItemClickListener methods với Video/Audio support

    @Override
    public void onItemClick(BaiGiang baiGiang) {
        Log.d(TAG, "🖱️ onItemClick: " + baiGiang.getTieuDe());
        if (getActivity() instanceof StudentDashboardActivity) {
            // Navigate đến detail screen
            ((StudentDashboardActivity) getActivity()).navigateToLessonDetail(baiGiang.getId());
        }
    }

    @Override
    public void onEditClick(BaiGiang baiGiang) {
        // Student không được phép edit
        Log.d(TAG, "Edit not available for students");
    }

    @Override
    public void onDeleteClick(BaiGiang baiGiang) {
        // Student không được phép delete
        Log.d(TAG, "Delete not available for students");
    }

    // ✅ THÊM: Xử lý Video playback
    @Override
    public void onPlayVideo(BaiGiang baiGiang) {
        Log.d(TAG, "📹 onPlayVideo: " + baiGiang.getTieuDe());

        if (baiGiang.getVideoURL() != null && !baiGiang.getVideoURL().isEmpty()) {
            String videoUrl = Constants.BASE_URL + baiGiang.getVideoURL();
            Log.d(TAG, "📹 Playing video: " + videoUrl);

            try {
                // Option 1: Sử dụng Intent để mở video player external
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(videoUrl), "video/*");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (intent.resolveActivity(requireContext().getPackageManager()) != null) {
                    startActivity(intent);
                    Log.d(TAG, "✅ Video player opened successfully");
                } else {
                    // Fallback: Mở trong browser
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
                    startActivity(browserIntent);
                    Log.d(TAG, "✅ Video opened in browser");
                }

                // TODO: Implement custom video player activity
                // navigateToVideoPlayer(baiGiang);

            } catch (Exception e) {
                Log.e(TAG, "❌ Error playing video: " + e.getMessage());
                Toast.makeText(requireContext(), "Không thể phát video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Video không có sẵn", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPlayAudio(BaiGiang baiGiang) {

    }


    // ✅ TODO: Custom Video Player (Tùy chọn)
    private void navigateToVideoPlayer(BaiGiang baiGiang) {
        // Có thể tạo VideoPlayerActivity riêng
        /*
        Intent intent = new Intent(requireContext(), VideoPlayerActivity.class);
        intent.putExtra("VIDEO_URL", baiGiang.getFullVideoURL());
        intent.putExtra("LESSON_TITLE", baiGiang.getTieuDe());
        intent.putExtra("LESSON_ID", baiGiang.getId());
        startActivity(intent);
        */
    }

    // ✅ TODO: Custom Audio Player (Tùy chọn)
    private void playAudioInApp(BaiGiang baiGiang) {
        // Có thể sử dụng MediaPlayer để phát audio trong app
        /*
        MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(baiGiang.getFullAudioURL());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "Error playing audio", e);
        }
        */
    }

    // ✅ IMPLEMENT: OnBaiGiangListener methods (từ BaiGiangController)
    @Override
    public void onBaiGiangListReceived(List<BaiGiang> baiGiangList) {
        Log.d(TAG, "📋 === API RESPONSE RECEIVED ===");
        Log.d(TAG, "📋 Total lessons received: " + (baiGiangList != null ? baiGiangList.size() : 0));

        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }

        this.baiGiangList.clear();
        if (baiGiangList != null && !baiGiangList.isEmpty()) {
            this.baiGiangList.addAll(baiGiangList);

            // Debug log media availability
            int videoCount = 0;
            int audioCount = 0;
            for (BaiGiang bg : baiGiangList) {
                if (bg.hasVideo()) videoCount++;
            }
            Log.d(TAG, "📊 Media availability: " + videoCount + " videos, " + audioCount + " audios");

            // Debug log first few lessons
            for (int i = 0; i < Math.min(3, baiGiangList.size()); i++) {
                BaiGiang bg = baiGiangList.get(i);
                Log.d(TAG, "📖 Lesson " + (i+1) + ": " + bg.getTieuDe() +
                        " (Video: " + bg.hasVideo() + ")");
            }
        }

        if (adapter != null) {
            Log.d(TAG, "🔄 Updating adapter with " + this.baiGiangList.size() + " items");
            adapter.updateData(this.baiGiangList);
        } else {
            Log.e(TAG, "❌ Adapter is NULL!");
        }

        if (baiGiangList == null || baiGiangList.isEmpty()) {
            Log.w(TAG, "📭 No lessons found - showing empty state");
            showEmptyState(true, "Chưa có bài giảng nào được xuất bản");
        } else {
            Log.d(TAG, "📚 Displaying " + baiGiangList.size() + " lessons with media support");
            showEmptyState(false, "");
        }

        Log.d(TAG, "✅ onBaiGiangListReceived completed");
    }

    @Override
    public void onBaiGiangDetailReceived(BaiGiang baiGiang) {
        // Not used in this fragment
    }

    @Override
    public void onBaiGiangCreated(BaiGiang baiGiang) {
        // Not used in this fragment
    }

    @Override
    public void onBaiGiangUpdated(BaiGiang baiGiang) {
        // Not used in this fragment
    }

    @Override
    public void onBaiGiangDeleted() {
        // Not used in this fragment
    }

    @Override
    public void onError(String message) {
        Log.e(TAG, "❌ API Error: " + message);

        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(false);
        }

        Toast.makeText(requireContext(), "Lỗi: " + message, Toast.LENGTH_LONG).show();
        showEmptyState(true, "Lỗi tải dữ liệu: " + message);
    }

    public void refreshData() {
        Log.d(TAG, "🔄 Manual refresh triggered");
        loadBaiGiangs();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "🔄 onResume - checking adapter state");
        if (adapter != null && baiGiangList != null) {
            Log.d(TAG, "📋 Adapter has " + adapter.getItemCount() + " items");
        }
    }
}