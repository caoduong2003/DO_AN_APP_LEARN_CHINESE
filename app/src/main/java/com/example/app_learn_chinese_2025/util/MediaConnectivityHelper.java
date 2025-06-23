package com.example.app_learn_chinese_2025.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 🎯 Helper class để test connectivity với Media API endpoints
 */
public class MediaConnectivityHelper {

    private static final String TAG = "MEDIA_CONNECTIVITY";

    public interface ConnectivityCallback {
        void onConnectivityResult(boolean isConnected, String message);
    }

    /**
     * Test connectivity đến server debug endpoint
     */
    public static void testServerConnectivity(Context context, ConnectivityCallback callback) {
        new Thread(() -> {
            try {
                String testUrl = Constants.getDebugTestUrl();
                Log.d(TAG, "Testing server connectivity: " + testUrl);

                URL url = new URL(testUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                String responseMessage = connection.getResponseMessage();

                if (context != null) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (responseCode == 200) {
                            Log.d(TAG, "✅ Server connectivity OK");
                            if (callback != null) {
                                callback.onConnectivityResult(true, "Server kết nối thành công");
                            }
                        } else {
                            Log.e(TAG, "❌ Server connectivity failed: " + responseCode + " " + responseMessage);
                            if (callback != null) {
                                callback.onConnectivityResult(false, "Server lỗi: " + responseCode);
                            }
                        }
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "❌ Server connectivity test failed: " + e.getMessage());
                if (context != null) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (callback != null) {
                            callback.onConnectivityResult(false, "Không thể kết nối server: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Test image API endpoint
     */
    public static void testImageAPI(Context context, String imageName, ConnectivityCallback callback) {
        new Thread(() -> {
            try {
                String imageUrl = Constants.getCorrectImageUrl(imageName);
                Log.d(TAG, "Testing image API: " + imageUrl);

                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                String contentType = connection.getContentType();
                long contentLength = connection.getContentLengthLong();

                if (context != null) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (responseCode == 200) {
                            Log.d(TAG, "✅ Image API OK - " + imageName);
                            Log.d(TAG, "Content-Type: " + contentType + ", Size: " + contentLength);
                            if (callback != null) {
                                callback.onConnectivityResult(true, "Image API hoạt động: " + imageName);
                            }
                        } else {
                            Log.e(TAG, "❌ Image API failed: " + responseCode + " for " + imageName);
                            if (callback != null) {
                                callback.onConnectivityResult(false, "Image không tồn tại: " + imageName);
                            }
                        }
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "❌ Image API test failed: " + e.getMessage());
                if (context != null) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (callback != null) {
                            callback.onConnectivityResult(false, "Lỗi image API: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Test video API endpoint
     */
    public static void testVideoAPI(Context context, String videoName, ConnectivityCallback callback) {
        new Thread(() -> {
            try {
                String videoUrl = Constants.getCorrectVideoUrl(videoName);
                Log.d(TAG, "Testing video API: " + videoUrl);

                URL url = new URL(videoUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                String contentType = connection.getContentType();
                long contentLength = connection.getContentLengthLong();

                if (context != null) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (responseCode == 200) {
                            Log.d(TAG, "✅ Video API OK - " + videoName);
                            Log.d(TAG, "Content-Type: " + contentType + ", Size: " + (contentLength / 1024 / 1024) + " MB");
                            if (callback != null) {
                                callback.onConnectivityResult(true, "Video API hoạt động: " + videoName +
                                        " (" + (contentLength / 1024 / 1024) + " MB)");
                            }
                        } else {
                            Log.e(TAG, "❌ Video API failed: " + responseCode + " for " + videoName);
                            if (callback != null) {
                                callback.onConnectivityResult(false, "Video không tồn tại: " + videoName);
                            }
                        }
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "❌ Video API test failed: " + e.getMessage());
                if (context != null) {
                    ((android.app.Activity) context).runOnUiThread(() -> {
                        if (callback != null) {
                            callback.onConnectivityResult(false, "Lỗi video API: " + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * Test tất cả endpoints cùng lúc
     */
    public static void runFullConnectivityTest(Context context) {
        Log.d(TAG, "🎯 Running full connectivity test...");

        // Test 1: Server connectivity
        testServerConnectivity(context, (isConnected, message) -> {
            Log.d(TAG, "Server test: " + message);
            Toast.makeText(context, "Server: " + message, Toast.LENGTH_SHORT).show();
        });

        // Test 2: Sample image
        testImageAPI(context, "sample.png", (isConnected, message) -> {
            Log.d(TAG, "Image test: " + message);
            Toast.makeText(context, "Image: " + message, Toast.LENGTH_SHORT).show();
        });

        // Test 3: Sample video
        testVideoAPI(context, "sample.mp4", (isConnected, message) -> {
            Log.d(TAG, "Video test: " + message);
            Toast.makeText(context, "Video: " + message, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Debug: Log tất cả URLs được tạo
     */
    public static void logAllAPIEndpoints() {
        Log.d(TAG, "🎯 === ALL MEDIA API ENDPOINTS ===");
        Log.d(TAG, "Base URL: " + Constants.BASE_URL);
        Log.d(TAG, "Upload Image: " + Constants.getImageUploadUrl());
        Log.d(TAG, "Upload Video: " + Constants.getVideoUploadUrl());
        Log.d(TAG, "Debug Test: " + Constants.getDebugTestUrl());
        Log.d(TAG, "Debug List: " + Constants.getDebugListUrl());

        // Sample URLs
        Log.d(TAG, "Sample Image URL: " + Constants.getCorrectImageUrl("sample.png"));
        Log.d(TAG, "Sample Video URL: " + Constants.getCorrectVideoUrl("sample.mp4"));

        // Test với different input formats
        Log.d(TAG, "--- Testing different input formats ---");
        String[] testInputs = {
                "sample.png",
                "/uploads/images/sample.png",
                "uploads/images/sample.png",
                "http://example.com/sample.png"
        };

        for (String input : testInputs) {
            Log.d(TAG, "Input: " + input + " -> Image URL: " + Constants.getCorrectImageUrl(input));
        }

        String[] videoInputs = {
                "sample.mp4",
                "/uploads/videos/sample.mp4",
                "uploads/videos/sample.mp4",
                "http://example.com/sample.mp4"
        };

        for (String input : videoInputs) {
            Log.d(TAG, "Input: " + input + " -> Video URL: " + Constants.getCorrectVideoUrl(input));
        }
    }
}