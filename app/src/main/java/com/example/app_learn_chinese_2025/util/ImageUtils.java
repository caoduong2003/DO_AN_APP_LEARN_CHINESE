package com.example.app_learn_chinese_2025.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 🖼️ ImageUtils - Helper cho xử lý ảnh
 * Đơn giản và dễ hiểu
 */
public class ImageUtils {
    private static final String TAG = "ImageUtils";
    private static final int MAX_IMAGE_SIZE = 1024; // Max width/height
    private static final int COMPRESSION_QUALITY = 85; // JPEG quality

    /**
     * 📸 Encode ảnh thành base64 string
     */
    public static String encodeImageToBase64(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                Log.e(TAG, "❌ Image path is null or empty");
                return null;
            }

            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                Log.e(TAG, "❌ Image file does not exist: " + imagePath);
                return null;
            }

            // Đọc và resize ảnh
            Bitmap bitmap = loadAndResizeImage(imagePath);
            if (bitmap == null) {
                Log.e(TAG, "❌ Failed to load bitmap from: " + imagePath);
                return null;
            }

            // Convert bitmap to base64
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, baos);
            byte[] imageBytes = baos.toByteArray();

            String base64String = Base64.encodeToString(imageBytes, Base64.NO_WRAP);

            // Cleanup
            bitmap.recycle();
            baos.close();

            Log.d(TAG, "✅ Image encoded successfully. Size: " + base64String.length() + " characters");
            return base64String;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error encoding image to base64", e);
            return null;
        }
    }

    /**
     * 📏 Load và resize ảnh để tối ưu performance
     */
    private static Bitmap loadAndResizeImage(String imagePath) {
        try {
            // Đọc dimensions của ảnh trước
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);

            // Tính toán sample size để resize
            options.inSampleSize = calculateInSampleSize(options, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
            options.inJustDecodeBounds = false;

            // Load ảnh với sample size
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

            if (bitmap == null) {
                Log.e(TAG, "❌ Failed to decode bitmap from file");
                return null;
            }

            // Resize thêm nếu cần
            if (bitmap.getWidth() > MAX_IMAGE_SIZE || bitmap.getHeight() > MAX_IMAGE_SIZE) {
                bitmap = resizeBitmap(bitmap, MAX_IMAGE_SIZE);
            }

            Log.d(TAG, "✅ Image loaded and resized: " + bitmap.getWidth() + "x" + bitmap.getHeight());
            return bitmap;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error loading and resizing image", e);
            return null;
        }
    }

    /**
     * 🔢 Tính toán sample size cho resize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 📐 Resize bitmap giữ tỷ lệ
     */
    private static Bitmap resizeBitmap(Bitmap original, int maxSize) {
        int width = original.getWidth();
        int height = original.getHeight();

        float ratio = Math.min((float) maxSize / width, (float) maxSize / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        Bitmap resized = Bitmap.createScaledBitmap(original, newWidth, newHeight, true);

        // Cleanup original nếu khác với resized
        if (resized != original) {
            original.recycle();
        }

        return resized;
    }

    /**
     * 📂 Lấy kích thước file
     */
    public static long getFileSize(String filePath) {
        try {
            File file = new File(filePath);
            return file.exists() ? file.length() : 0;
        } catch (Exception e) {
            Log.e(TAG, "Error getting file size", e);
            return 0;
        }
    }

    /**
     * 🗑️ Xóa file ảnh
     */
    public static boolean deleteImageFile(String imagePath) {
        try {
            if (imagePath == null || imagePath.isEmpty()) {
                return false;
            }

            File file = new File(imagePath);
            boolean deleted = file.delete();

            if (deleted) {
                Log.d(TAG, "✅ Image file deleted: " + imagePath);
            } else {
                Log.w(TAG, "⚠️ Failed to delete image file: " + imagePath);
            }

            return deleted;
        } catch (Exception e) {
            Log.e(TAG, "❌ Error deleting image file", e);
            return false;
        }
    }
}