package com.example.app_learn_chinese_2025.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.remote.RetrofitClient;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.provider.OpenableColumns;
import androidx.annotation.Nullable;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditBaiGiangActivity extends AppCompatActivity {
    private TextView tvTitle;
    private TextInputEditText etMaBaiGiang, etTieuDe, etMoTa, etNoiDung, etThoiLuong;
    private Spinner spinnerLoaiBaiGiang, spinnerCapDoHSK, spinnerChuDe;
    private Button btnSave, btnCancel, btnManageTuVung;

    private SessionManager sessionManager;
    private BaiGiangRepository baiGiangRepository;
    private ProgressDialog progressDialog;

    private List<LoaiBaiGiang> loaiBaiGiangList;
    private List<CapDoHSK> capDoHSKList;
    private List<ChuDe> chuDeList;

    private long baiGiangId = -1; // -1 for adding new, otherwise for editing
    private BaiGiang currentBaiGiang; // Current bai giang being edited

    private int selectedLoaiBaiGiangPosition = 0;
    private int selectedCapDoHSKPosition = 0;
    private int selectedChuDePosition = 0;

    private static final int REQUEST_VIDEO_PICK = 1001;
    private static final int REQUEST_IMAGE_PICK = 1002;

    private Button btnSelectVideo, btnUploadVideo, btnSelectImage, btnUploadImage;
    private TextView tvSelectedVideo;
    private ImageView ivThumbnail;
    private Uri selectedVideoUri, selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bai_giang);

        // Get baiGiangId from intent extras (if available)
        if (getIntent().hasExtra("BAI_GIANG_ID")) {
            baiGiangId = getIntent().getLongExtra("BAI_GIANG_ID", -1);
        }

        initViews();
        setupListeners();

        // Load data
        loadDataForSpinners();

        if (baiGiangId != -1) {
            // Edit mode
            tvTitle.setText("Chỉnh sửa bài giảng");
            loadBaiGiang(baiGiangId);
            btnManageTuVung.setEnabled(true);
        } else {
            // Add mode
            tvTitle.setText("Thêm bài giảng mới");
            btnManageTuVung.setEnabled(false);
        }
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        etMaBaiGiang = findViewById(R.id.etMaBaiGiang);
        etTieuDe = findViewById(R.id.etTieuDe);
        etMoTa = findViewById(R.id.etMoTa);
        etNoiDung = findViewById(R.id.etNoiDung);
        etThoiLuong = findViewById(R.id.etThoiLuong);

        spinnerLoaiBaiGiang = findViewById(R.id.spinnerLoaiBaiGiang);
        spinnerCapDoHSK = findViewById(R.id.spinnerCapDoHSK);
        spinnerChuDe = findViewById(R.id.spinnerChuDe);

        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);
        btnManageTuVung = findViewById(R.id.btnManageTuVung);

        sessionManager = new SessionManager(this);
        baiGiangRepository = new BaiGiangRepository(sessionManager);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);

        loaiBaiGiangList = new ArrayList<>();
        capDoHSKList = new ArrayList<>();
        chuDeList = new ArrayList<>();

        btnSelectVideo = findViewById(R.id.btnSelectVideo);
        btnUploadVideo = findViewById(R.id.btnUploadVideo);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        tvSelectedVideo = findViewById(R.id.tvSelectedVideo);
        ivThumbnail = findViewById(R.id.ivThumbnail);
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveBaiGiang());
        btnCancel.setOnClickListener(v -> finish());

        btnSelectVideo.setOnClickListener(v -> selectVideo());
        btnUploadVideo.setOnClickListener(v -> uploadVideo());
        btnSelectImage.setOnClickListener(v -> selectImage());
        btnUploadImage.setOnClickListener(v -> uploadImage());


        btnManageTuVung.setOnClickListener(v -> {
            if (baiGiangId != -1) {
                Intent intent = new Intent(EditBaiGiangActivity.this, ManageTuVungActivity.class);
                intent.putExtra("BAI_GIANG_ID", baiGiangId);
                startActivity(intent);
            }
        });

        // Spinner listeners
        spinnerLoaiBaiGiang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLoaiBaiGiangPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerCapDoHSK.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCapDoHSKPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        spinnerChuDe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedChuDePosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadDataForSpinners() {
        progressDialog.show();

        // Load Loai Bai Giang
        baiGiangRepository.getAllLoaiBaiGiang(new BaiGiangRepository.OnLoaiBaiGiangListCallback() {
            @Override
            public void onSuccess(List<LoaiBaiGiang> loaiBaiGiangs) {
                loaiBaiGiangList = loaiBaiGiangs;
                setupLoaiBaiGiangSpinner();

                // Load Cap Do HSK after Loai Bai Giang is loaded
                loadCapDoHSK();
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(EditBaiGiangActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCapDoHSK() {
        baiGiangRepository.getAllCapDoHSK(new BaiGiangRepository.OnCapDoHSKListCallback() {
            @Override
            public void onSuccess(List<CapDoHSK> capDoHSKs) {
                capDoHSKList = capDoHSKs;
                setupCapDoHSKSpinner();

                // Load Chu De after Cap Do HSK is loaded
                loadChuDe();
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(EditBaiGiangActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadChuDe() {
        baiGiangRepository.getAllChuDe(new BaiGiangRepository.OnChuDeListCallback() {
            @Override
            public void onSuccess(List<ChuDe> chuDes) {
                chuDeList = chuDes;
                setupChuDeSpinner();

                progressDialog.dismiss();
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(EditBaiGiangActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLoaiBaiGiangSpinner() {
        List<String> loaiBaiGiangNames = new ArrayList<>();
        for (LoaiBaiGiang loaiBaiGiang : loaiBaiGiangList) {
            loaiBaiGiangNames.add(loaiBaiGiang.getTenLoai());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, loaiBaiGiangNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoaiBaiGiang.setAdapter(adapter);
    }

    private void setupCapDoHSKSpinner() {
        List<String> capDoHSKNames = new ArrayList<>();
        for (CapDoHSK capDoHSK : capDoHSKList) {
            capDoHSKNames.add("HSK " + capDoHSK.getCapDo() + " - " + capDoHSK.getTenCapDo());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, capDoHSKNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCapDoHSK.setAdapter(adapter);
    }

    private void setupChuDeSpinner() {
        List<String> chuDeNames = new ArrayList<>();
        for (ChuDe chuDe : chuDeList) {
            chuDeNames.add(chuDe.getTenChuDe());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, chuDeNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerChuDe.setAdapter(adapter);
    }

    private void loadBaiGiang(long id) {
        progressDialog.show();

        baiGiangRepository.getBaiGiangById(id, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                currentBaiGiang = baiGiang;

                // Set values to UI elements
                etMaBaiGiang.setText(baiGiang.getMaBaiGiang());
                etTieuDe.setText(baiGiang.getTieuDe());
                etMoTa.setText(baiGiang.getMoTa());
                etNoiDung.setText(baiGiang.getNoiDung());
                etThoiLuong.setText(String.valueOf(baiGiang.getThoiLuong()));

                // Set spinner selections
                if (baiGiang.getLoaiBaiGiang() != null) {
                    for (int i = 0; i < loaiBaiGiangList.size(); i++) {
                        if (loaiBaiGiangList.get(i).getID() == baiGiang.getLoaiBaiGiang().getID()) {
                            spinnerLoaiBaiGiang.setSelection(i);
                            selectedLoaiBaiGiangPosition = i;
                            break;
                        }
                    }
                }

                if (baiGiang.getCapDoHSK() != null) {
                    for (int i = 0; i < capDoHSKList.size(); i++) {
                        if (capDoHSKList.get(i).getID() == baiGiang.getCapDoHSK().getID()) {
                            spinnerCapDoHSK.setSelection(i);
                            selectedCapDoHSKPosition = i;
                            break;
                        }
                    }
                }

                if (baiGiang.getChuDe() != null) {
                    for (int i = 0; i < chuDeList.size(); i++) {
                        if (chuDeList.get(i).getID() == baiGiang.getChuDe().getID()) {
                            spinnerChuDe.setSelection(i);
                            selectedChuDePosition = i;
                            break;
                        }
                    }
                }

                progressDialog.dismiss();
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(EditBaiGiangActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish(); // Close activity if error occurs
            }
        });
    }

    private void saveBaiGiang() {
        // Validate input
        String maBaiGiang = etMaBaiGiang.getText().toString().trim();
        String tieuDe = etTieuDe.getText().toString().trim();
        String moTa = etMoTa.getText().toString().trim();
        String noiDung = etNoiDung.getText().toString().trim();
        String thoiLuongStr = etThoiLuong.getText().toString().trim();

        if (TextUtils.isEmpty(maBaiGiang)) {
            etMaBaiGiang.setError("Vui lòng nhập mã bài giảng");
            return;
        }

        if (TextUtils.isEmpty(tieuDe)) {
            etTieuDe.setError("Vui lòng nhập tiêu đề");
            return;
        }

        if (TextUtils.isEmpty(thoiLuongStr)) {
            etThoiLuong.setError("Vui lòng nhập thời lượng");
            return;
        }

        int thoiLuong;
        try {
            thoiLuong = Integer.parseInt(thoiLuongStr);
        } catch (NumberFormatException e) {
            etThoiLuong.setError("Thời lượng phải là số");
            return;
        }

        // Create or update BaiGiang object
        BaiGiang baiGiang;
        if (baiGiangId != -1 && currentBaiGiang != null) {
            // Edit mode
            baiGiang = currentBaiGiang;
        } else {
            // Add mode
            baiGiang = new BaiGiang();
            baiGiang.setNgayTao(new Date());
            baiGiang.setLuotXem(0);
            baiGiang.setTrangThai(true);
            baiGiang.setLaBaiGiangGoi(false);

            // Set GiangVienID
            User user = sessionManager.getUserDetails();
            if (user != null) {
                baiGiang.setGiangVienID(user.getID());
            } else {
                Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Update fields
        baiGiang.setMaBaiGiang(maBaiGiang);
        baiGiang.setTieuDe(tieuDe);
        baiGiang.setMoTa(moTa);
        baiGiang.setNoiDung(noiDung);
        baiGiang.setThoiLuong(thoiLuong);
        baiGiang.setNgayCapNhat(new Date());

        // Set selected items from spinners
        if (!loaiBaiGiangList.isEmpty()) {
            baiGiang.setLoaiBaiGiang(loaiBaiGiangList.get(selectedLoaiBaiGiangPosition));
        }

        if (!capDoHSKList.isEmpty()) {
            baiGiang.setCapDoHSK(capDoHSKList.get(selectedCapDoHSKPosition));
        }

        if (!chuDeList.isEmpty()) {
            baiGiang.setChuDe(chuDeList.get(selectedChuDePosition));
        }

        progressDialog.show();

        if (baiGiangId != -1) {
            // Update existing
            baiGiangRepository.updateBaiGiang(baiGiangId, baiGiang, new BaiGiangRepository.OnBaiGiangCallback() {
                @Override
                public void onSuccess(BaiGiang updatedBaiGiang) {
                    progressDialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    progressDialog.dismiss();
                    Toast.makeText(EditBaiGiangActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create new
            baiGiangRepository.createBaiGiang(baiGiang, new BaiGiangRepository.OnBaiGiangCallback() {
                @Override
                public void onSuccess(BaiGiang newBaiGiang) {
                    progressDialog.dismiss();
                    baiGiangId = newBaiGiang.getID(); // Update ID for newly created
                    btnManageTuVung.setEnabled(true); // Enable manage vocab button
                    setResult(RESULT_OK);

                    // Ask if user wants to add vocabulary
                    askToAddVocabulary(newBaiGiang);
                }

                @Override
                public void onError(String errorMessage) {
                    progressDialog.dismiss();
                    Toast.makeText(EditBaiGiangActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void askToAddVocabulary(BaiGiang baiGiang) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Thêm từ vựng")
                .setMessage("Bài giảng đã được lưu. Bạn có muốn thêm từ vựng cho bài giảng này không?")
                .setPositiveButton("Có", (dialog, which) -> {
                    Intent intent = new Intent(EditBaiGiangActivity.this, ManageTuVungActivity.class);
                    intent.putExtra("BAI_GIANG_ID", baiGiang.getID());
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Không", (dialog, which) -> finish())
                .show();
    }

    private void selectVideo() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Chọn video"), REQUEST_VIDEO_PICK);
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_VIDEO_PICK) {
                selectedVideoUri = data.getData();
                if (selectedVideoUri != null) {
                    String fileName = getFileName(selectedVideoUri);
                    tvSelectedVideo.setText("Đã chọn: " + fileName);
                    btnUploadVideo.setEnabled(true);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    // Show preview
                    Glide.with(this)
                            .load(selectedImageUri)
                            .into(ivThumbnail);
                    btnUploadImage.setEnabled(true);
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (nameIndex >= 0) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void uploadVideo() {
        if (selectedVideoUri == null) {
            Toast.makeText(this, "Vui lòng chọn video trước", Toast.LENGTH_SHORT).show();
            return;
        }

        if (baiGiangId == -1) {
            Toast.makeText(this, "Vui lòng lưu bài giảng trước khi upload video", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Đang upload video...");
        progressDialog.show();

        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedVideoUri);
            String fileName = getFileName(selectedVideoUri);

            // Create multipart request
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("video/*"),
                    getBytes(inputStream)
            );

            MultipartBody.Part videoPart = MultipartBody.Part.createFormData("video", fileName, requestFile);

            RetrofitClient.getInstance().getApiService().uploadVideoForLesson(baiGiangId, videoPart)
                    .enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                Map<String, Object> result = response.body();
                                Boolean success = (Boolean) result.get("success");
                                if (success != null && success) {
                                    String videoUrl = (String) result.get("videoUrl");
                                    Toast.makeText(EditBaiGiangActivity.this, "Upload video thành công", Toast.LENGTH_SHORT).show();

                                    // Update current bai giang
                                    if (currentBaiGiang != null) {
                                        currentBaiGiang.setVideoURL(videoUrl);
                                    }

                                    // Reset UI
                                    selectedVideoUri = null;
                                    tvSelectedVideo.setText("Video đã được upload");
                                    btnUploadVideo.setEnabled(false);
                                } else {
                                    String error = (String) result.get("error");
                                    Toast.makeText(EditBaiGiangActivity.this, "Upload thất bại: " + error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(EditBaiGiangActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(EditBaiGiangActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Lỗi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn hình ảnh trước", Toast.LENGTH_SHORT).show();
            return;
        }

        if (baiGiangId == -1) {
            Toast.makeText(this, "Vui lòng lưu bài giảng trước khi upload hình ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Đang upload hình ảnh...");
        progressDialog.show();

        try {
            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
            String fileName = getFileName(selectedImageUri);

            // Create multipart request
            RequestBody requestFile = RequestBody.create(
                    MediaType.parse("image/*"),
                    getBytes(inputStream)
            );

            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", fileName, requestFile);

            RetrofitClient.getInstance().getApiService().uploadThumbnailForLesson(baiGiangId, imagePart)
                    .enqueue(new Callback<Map<String, Object>>() {
                        @Override
                        public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                            progressDialog.dismiss();
                            if (response.isSuccessful() && response.body() != null) {
                                Map<String, Object> result = response.body();
                                Boolean success = (Boolean) result.get("success");
                                if (success != null && success) {
                                    String imageUrl = (String) result.get("imageUrl");
                                    Toast.makeText(EditBaiGiangActivity.this, "Upload hình ảnh thành công", Toast.LENGTH_SHORT).show();

                                    // Update current bai giang
                                    if (currentBaiGiang != null) {
                                        currentBaiGiang.setHinhAnh(imageUrl);
                                    }

                                    // Reset UI
                                    selectedImageUri = null;
                                    btnUploadImage.setEnabled(false);
                                } else {
                                    String error = (String) result.get("error");
                                    Toast.makeText(EditBaiGiangActivity.this, "Upload thất bại: " + error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(EditBaiGiangActivity.this, "Upload thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(EditBaiGiangActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            progressDialog.dismiss();
            Toast.makeText(this, "Lỗi đọc file: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        return byteBuffer.toByteArray();
    }
}