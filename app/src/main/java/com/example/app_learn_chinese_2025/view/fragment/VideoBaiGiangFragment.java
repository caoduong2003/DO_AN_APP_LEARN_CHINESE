package com.example.app_learn_chinese_2025.view.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

public class VideoBaiGiangFragment extends Fragment {
    private static final String TAG = "VideoBaiGiangFragment";

    // UI Components
    private StyledPlayerView playerView;
    private ProgressBar progressBar;
    private ImageButton btnPlay;
    private TextView tvError, tvVideoTitle, tvVideoDescription;

    // ExoPlayer
    private ExoPlayer player;

    // Data
    private SessionManager sessionManager;
    private BaiGiangRepository baiGiangRepository;
    private long baiGiangId;
    private BaiGiang currentBaiGiang;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            baiGiangId = getArguments().getLong("BAI_GIANG_ID", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_bai_giang, container, false);

        initViews(view);
        initializePlayer();
        loadBaiGiang();

        return view;
    }

    private void initViews(View view) {
        // Try to find StyledPlayerView first, fallback to regular PlayerView
        playerView = view.findViewById(R.id.playerView);
        if (playerView == null) {
            // Create StyledPlayerView programmatically if not in layout
            playerView = new StyledPlayerView(requireContext());
        }

        progressBar = view.findViewById(R.id.progressBar);
        btnPlay = view.findViewById(R.id.btnPlay);
        tvError = view.findViewById(R.id.tvError);
        tvVideoTitle = view.findViewById(R.id.tvVideoTitle);
        tvVideoDescription = view.findViewById(R.id.tvVideoDescription);

        sessionManager = new SessionManager(requireContext());
        baiGiangRepository = new BaiGiangRepository(requireContext(), sessionManager);
    }

    private void initializePlayer() {
        if (player == null) {
            // ✅ CRITICAL: Configure DefaultHttpDataSource to handle 206 responses
            DefaultHttpDataSource.Factory httpDataSourceFactory = new DefaultHttpDataSource.Factory()
                    .setAllowCrossProtocolRedirects(true)
                    .setConnectTimeoutMs(30000)
                    .setReadTimeoutMs(30000)
                    .setUserAgent("TiengTrungApp/1.0");  // Important for some servers

            DefaultDataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(
                    requireContext(), httpDataSourceFactory);

            DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(dataSourceFactory);

            player = new ExoPlayer.Builder(requireContext())
                    .setMediaSourceFactory(mediaSourceFactory)
                    .build();

            playerView.setPlayer(player);
            playerView.setUseController(true);
            playerView.setControllerAutoShow(true);

            // Setup player listeners
            player.addListener(new Player.Listener() {
                @Override
                public void onPlayerError(PlaybackException error) {
                    Log.e(TAG, "❌ ExoPlayer error: " + error.getErrorCodeName() + " - " + error.getMessage(), error);
                    showError("Không thể phát video: " + error.getErrorCodeName());
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    switch (playbackState) {
                        case Player.STATE_BUFFERING:
                            Log.d(TAG, "🔄 Video đang tải...");
                            progressBar.setVisibility(View.VISIBLE);
                            break;
                        case Player.STATE_READY:
                            Log.d(TAG, "✅ Video sẵn sàng phát");
                            progressBar.setVisibility(View.GONE);
                            playerView.setVisibility(View.VISIBLE);
                            hideError();
                            btnPlay.setVisibility(View.GONE);
                            break;
                        case Player.STATE_ENDED:
                            Log.d(TAG, "⏹ Video đã phát xong");
                            btnPlay.setVisibility(View.VISIBLE);
                            break;
                        case Player.STATE_IDLE:
                            Log.d(TAG, "⏸ Player ở trạng thái idle");
                            break;
                    }
                }

                @Override
                public void onIsPlayingChanged(boolean isPlaying) {
                    Log.d(TAG, "▶️ Playing state changed: " + isPlaying);
                }
            });
        }
    }

    private void loadBaiGiang() {
        showLoading(true);

        baiGiangRepository.getBaiGiangById(baiGiangId, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                currentBaiGiang = baiGiang;
                updateUI();
                setupVideo();
                showLoading(false);
            }

            @Override
            public void onError(String errorMessage) {
                showError("Lỗi tải bài giảng: " + errorMessage);
                showLoading(false);
            }
        });
    }

    private void updateUI() {
        if (currentBaiGiang != null) {
            if (tvVideoTitle != null) {
                tvVideoTitle.setText(currentBaiGiang.getTieuDe());
            }
            if (tvVideoDescription != null) {
                tvVideoDescription.setText(currentBaiGiang.getMoTa());
            }
        }
    }

    private void setupVideo() {
        if (currentBaiGiang == null ||
                currentBaiGiang.getVideoURL() == null ||
                currentBaiGiang.getVideoURL().isEmpty()) {
            showError("Bài giảng này chưa có video");
            return;
        }

        String videoUrl = currentBaiGiang.getVideoURL();
        String fullUrl = Constants.getCorrectVideoUrl(videoUrl);

        Log.d(TAG, "🎬 Setting up video with URL: " + fullUrl);

        // ✅ DEBUG: Test the URL first
        Constants.testVideoUrl(fullUrl, requireContext());

        try {
            // ✅ CRITICAL: Create MediaItem and prepare player
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(fullUrl));
            player.setMediaItem(mediaItem);
            player.prepare();

            // Setup play button
            btnPlay.setOnClickListener(v -> {
                Log.d(TAG, "▶️ Play button clicked");
                player.setPlayWhenReady(true);
                btnPlay.setVisibility(View.GONE);
            });

        } catch (Exception e) {
            Log.e(TAG, "❌ Error setting up video: " + e.getMessage(), e);
            showError("Lỗi khi tải video: " + e.getMessage());
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showError(String message) {
        Log.e(TAG, "❌ Error: " + message);
        if (tvError != null) {
            tvError.setText(message);
            tvError.setVisibility(View.VISIBLE);
        }
        if (playerView != null) {
            playerView.setVisibility(View.GONE);
        }
        if (btnPlay != null) {
            btnPlay.setVisibility(View.GONE);
        }
    }

    private void hideError() {
        if (tvError != null) {
            tvError.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (player != null) {
            // Don't auto-play on start
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null && player.getPlayWhenReady()) {
            player.play();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
}