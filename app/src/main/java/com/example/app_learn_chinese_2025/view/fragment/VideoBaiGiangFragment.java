package com.example.app_learn_chinese_2025.view.fragment;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.SessionManager;

public class VideoBaiGiangFragment extends Fragment {
    private VideoView videoView;
    private WebView webView;
    private ProgressBar progressBar;
    private ImageButton btnPlay, btnPlayPause, btnFullscreen;
    private SeekBar seekBar;
    private TextView tvDuration, tvError, tvVideoTitle, tvVideoDescription;

    private SessionManager sessionManager;
    private BaiGiangRepository baiGiangRepository;

    private long baiGiangId;
    private BaiGiang currentBaiGiang;
    private boolean isPlaying = false;
    private Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get baiGiangId from arguments
        if (getArguments() != null) {
            baiGiangId = getArguments().getLong("BAI_GIANG_ID", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_bai_giang, container, false);

        initViews(view);
        setupListeners();

        // Load data
        loadBaiGiang();

        return view;
    }

    private void initViews(View view) {
        videoView = view.findViewById(R.id.videoView);
        webView = view.findViewById(R.id.webView);
        progressBar = view.findViewById(R.id.progressBar);
        btnPlay = view.findViewById(R.id.btnPlay);
        btnPlayPause = view.findViewById(R.id.btnPlayPause);
        btnFullscreen = view.findViewById(R.id.btnFullscreen);
        seekBar = view.findViewById(R.id.seekBar);
        tvDuration = view.findViewById(R.id.tvDuration);
        tvError = view.findViewById(R.id.tvError);
        tvVideoTitle = view.findViewById(R.id.tvVideoTitle);
        tvVideoDescription = view.findViewById(R.id.tvVideoDescription);

        sessionManager = new SessionManager(requireContext());
        baiGiangRepository = new BaiGiangRepository(requireContext(), sessionManager);
    }

    private void setupListeners() {
        btnPlay.setOnClickListener(v -> playVideo());
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        btnFullscreen.setOnClickListener(v -> toggleFullscreen());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && videoView.isPlaying()) {
                    videoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void loadBaiGiang() {
        if (baiGiangId <= 0) {
            showError("Không tìm thấy thông tin bài giảng");
            return;
        }

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
                showError(errorMessage);
                showLoading(false);
            }
        });
    }

    private void updateUI() {
        if (currentBaiGiang != null) {
            tvVideoTitle.setText(currentBaiGiang.getTieuDe());
            tvVideoDescription.setText(currentBaiGiang.getMoTa());
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

        // Check if it's a direct video file or streaming URL
        if (isDirectVideoUrl(videoUrl)) {
            setupVideoView(videoUrl);
        } else {
            setupWebView(videoUrl);
        }
    }

    private boolean isDirectVideoUrl(String url) {
        // Check if URL points to a direct video file
        return url.endsWith(".mp4") || url.endsWith(".avi") ||
                url.endsWith(".mkv") || url.endsWith(".mov") ||
                url.contains("your-video-server.com"); // Replace with your server domain
    }

    private void setupVideoView(String videoUrl) {
        try {
            webView.setVisibility(View.GONE);
            videoView.setVisibility(View.VISIBLE);

            // Build full URL
            String fullUrl = videoUrl;
            if (videoUrl.startsWith("/api/media/video/")) {
                // It's a relative URL from our server
                fullUrl = Constants.getBaseUrl() + videoUrl.substring(1); // Remove leading slash
            } else if (!videoUrl.startsWith("http")) {
                // It's a filename only
                fullUrl = Constants.getBaseUrl() + "api/media/video/" + videoUrl;
            }

            Uri uri = Uri.parse(fullUrl);
            videoView.setVideoURI(uri);

            // Setup media controller
            MediaController mediaController = new MediaController(requireContext());
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    btnPlay.setVisibility(View.GONE);
                    setupVideoControls();
                }
            });

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    showError("Không thể phát video");
                    return true;
                }
            });

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                    btnPlay.setVisibility(View.VISIBLE);
                }
            });

        } catch (Exception e) {
            showError("Lỗi khi tải video: " + e.getMessage());
        }
    }

    private void setupWebView(String videoUrl) {
        try {
            videoView.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);

            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setBuiltInZoomControls(false);
            webSettings.setDisplayZoomControls(false);
            webSettings.setSupportZoom(false);
            webSettings.setDefaultTextEncodingName("utf-8");

            // Enable hardware acceleration for video
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setMediaPlaybackRequiresUserGesture(false);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    // Hide loading when page finishes
                }
            });

            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    // Update loading progress if needed
                }
            });

            // Create HTML content for video
            String htmlContent = createVideoHtml(videoUrl);
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

        } catch (Exception e) {
            showError("Lỗi khi tải video: " + e.getMessage());
        }
    }

    private String createVideoHtml(String videoUrl) {
        return "<html><head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { margin: 0; padding: 0; background: black; }" +
                "video { width: 100%; height: 100%; object-fit: contain; }" +
                "</style>" +
                "</head><body>" +
                "<video controls autoplay>" +
                "<source src='" + videoUrl + "' type='video/mp4'>" +
                "Your browser does not support the video tag." +
                "</video>" +
                "</body></html>";
    }

    private void setupVideoControls() {
        if (videoView.getDuration() > 0) {
            seekBar.setMax(videoView.getDuration());
            updateSeekBar();
        }
    }

    private void updateSeekBar() {
        if (videoView.isPlaying()) {
            seekBar.setProgress(videoView.getCurrentPosition());
            updateDurationText();
            handler.postDelayed(this::updateSeekBar, 1000);
        }
    }

    private void updateDurationText() {
        int currentPos = videoView.getCurrentPosition();
        int duration = videoView.getDuration();

        String current = formatTime(currentPos);
        String total = formatTime(duration);

        tvDuration.setText(current + " / " + total);
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds = seconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    private void playVideo() {
        if (videoView.getVisibility() == View.VISIBLE) {
            videoView.start();
            isPlaying = true;
            btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            btnPlay.setVisibility(View.GONE);
            updateSeekBar();
        }
    }

    private void togglePlayPause() {
        if (videoView.getVisibility() == View.VISIBLE) {
            if (videoView.isPlaying()) {
                videoView.pause();
                isPlaying = false;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            } else {
                videoView.start();
                isPlaying = true;
                btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
                updateSeekBar();
            }
        }
    }

    private void toggleFullscreen() {
        // Implement fullscreen toggle if needed
        Toast.makeText(requireContext(), "Chức năng toàn màn hình đang phát triển", Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.GONE);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
        if (webView != null) {
            webView.destroy();
        }
        handler.removeCallbacksAndMessages(null);
    }
}