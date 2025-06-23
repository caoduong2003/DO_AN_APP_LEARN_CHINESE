package com.example.app_learn_chinese_2025.view.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app_learn_chinese_2025.R;
import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import com.example.app_learn_chinese_2025.model.data.CapDoHSK;
import com.example.app_learn_chinese_2025.model.data.TuVung;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.model.repository.TuVungRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class EditTuVungActivity extends AppCompatActivity {
    private static final String TAG = "EditTuVungActivity";
    private TextView tvTitle;
    private TextInputEditText etTiengTrung, etPhienAm, etTiengViet, etViDu;
    private Spinner spinnerLoaiTu;
    private Button btnGeneratePinyin, btnGenerateAudio, btnSave, btnCancel;

    private SessionManager sessionManager;
    private BaiGiangRepository baiGiangRepository;
    private TuVungRepository tuVungRepository;
    private ProgressDialog progressDialog;

    private long baiGiangId;
    private long tuVungId = -1; // -1 for adding new, otherwise for editing
    private BaiGiang currentBaiGiang;
    private TuVung currentTuVung;

    private String[] loaiTuArray;
    private int selectedLoaiTuPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tu_vung);

        // Get IDs from intent extras
        if (getIntent().hasExtra("BAI_GIANG_ID")) {
            baiGiangId = getIntent().getLongExtra("BAI_GIANG_ID", -1);
        } else {
            Toast.makeText(this, "Không tìm thấy thông tin bài giảng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (baiGiangId == -1) {
            Toast.makeText(this, "ID bài giảng không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (getIntent().hasExtra("TU_VUNG_ID")) {
            tuVungId = getIntent().getLongExtra("TU_VUNG_ID", -1);
        }

        initViews();
        setupListeners();
        setupLoaiTuSpinner();

        // Load data
        loadBaiGiang();

        if (tuVungId != -1) {
            // Edit mode
            tvTitle.setText("Sửa từ vựng");
            loadTuVung(tuVungId);
        } else {
            // Add mode
            tvTitle.setText("Thêm từ vựng mới");
        }
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);
        etTiengTrung = findViewById(R.id.etTiengTrung);
        etPhienAm = findViewById(R.id.etPhienAm);
        etTiengViet = findViewById(R.id.etTiengViet);
        etViDu = findViewById(R.id.etViDu);

        spinnerLoaiTu = findViewById(R.id.spinnerLoaiTu);
        btnGeneratePinyin = findViewById(R.id.btnGeneratePinyin);
        btnGenerateAudio = findViewById(R.id.btnGenerateAudio);
        btnSave = findViewById(R.id.btnSave);
        btnCancel = findViewById(R.id.btnCancel);

        sessionManager = new SessionManager(this);
        baiGiangRepository = new BaiGiangRepository(this, sessionManager); // Sửa: truyền Context
        tuVungRepository = new TuVungRepository(sessionManager);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);

        // Initialize loaiTuArray
        loaiTuArray = new String[]{
                "Danh từ", "Động từ", "Tính từ", "Đại từ", "Phó từ",
                "Giới từ", "Liên từ", "Thán từ", "Trợ từ", "Lượng từ"
        };
    }

    private void setupListeners() {
        btnGeneratePinyin.setOnClickListener(v -> generatePinyin());
        btnGenerateAudio.setOnClickListener(v -> generateAudio());
        btnSave.setOnClickListener(v -> saveTuVung());
        btnCancel.setOnClickListener(v -> finish());

        spinnerLoaiTu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedLoaiTuPosition = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void setupLoaiTuSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, loaiTuArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLoaiTu.setAdapter(adapter);
    }

    private void loadBaiGiang() {
        progressDialog.show();
        baiGiangRepository.getBaiGiangById(baiGiangId, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                currentBaiGiang = baiGiang;
                // Xử lý capDoHSK null
                if (baiGiang.getCapDoHSK() == null) {
                    Log.w(TAG, "CapDoHSK is null for BaiGiang: " + baiGiang.getTieuDe());
                    baiGiang.setCapDoHSK(new CapDoHSK(1, "HSK 1")); // Giá trị mặc định
                }
                progressDialog.dismiss();
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Log.e(TAG, "Load bai giang error: " + errorMessage);
                Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadTuVung(long id) {
        progressDialog.show();
        tuVungRepository.getTuVungById(id, new TuVungRepository.OnTuVungCallback() {
            @Override
            public void onSuccess(TuVung tuVung) {
                currentTuVung = tuVung;
                etTiengTrung.setText(tuVung.getTiengTrung());
                etPhienAm.setText(tuVung.getPhienAm());
                etTiengViet.setText(tuVung.getTiengViet());
                etViDu.setText(tuVung.getViDu());

                String loaiTu = tuVung.getLoaiTu();
                for (int i = 0; i < loaiTuArray.length; i++) {
                    if (loaiTuArray[i].equals(loaiTu)) {
                        spinnerLoaiTu.setSelection(i);
                        selectedLoaiTuPosition = i;
                        break;
                    }
                }

                progressDialog.dismiss();
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Log.e(TAG, "Load tu vung error: " + errorMessage);
                Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void generatePinyin() {
        String chineseText = etTiengTrung.getText().toString().trim();
        if (TextUtils.isEmpty(chineseText)) {
            Toast.makeText(this, "Vui lòng nhập tiếng Trung trước", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        tuVungRepository.generatePinyin(chineseText, new TuVungRepository.OnStringCallback() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                etPhienAm.setText(result);
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Log.e(TAG, "Generate pinyin error: " + errorMessage);
                Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateAudio() {
        String chineseText = etTiengTrung.getText().toString().trim();
        if (TextUtils.isEmpty(chineseText)) {
            Toast.makeText(this, "Vui lòng nhập tiếng Trung trước", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        Toast.makeText(this, "Đang tạo file âm thanh...", Toast.LENGTH_SHORT).show();
        tuVungRepository.generateAudio(chineseText, new TuVungRepository.OnStringCallback() {
            @Override
            public void onSuccess(String result) {
                progressDialog.dismiss();
                Toast.makeText(EditTuVungActivity.this, "Đã tạo file âm thanh", Toast.LENGTH_SHORT).show();
                if (currentTuVung == null) {
                    currentTuVung = new TuVung();
                }
                currentTuVung.setAudioURL(result);
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Log.e(TAG, "Generate audio error: " + errorMessage);
                Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTuVung() {
        String tiengTrung = etTiengTrung.getText().toString().trim();
        String phienAm = etPhienAm.getText().toString().trim();
        String tiengViet = etTiengViet.getText().toString().trim();
        String viDu = etViDu.getText().toString().trim();

        if (TextUtils.isEmpty(tiengTrung)) {
            etTiengTrung.setError("Vui lòng nhập tiếng Trung");
            return;
        }

        if (TextUtils.isEmpty(tiengViet)) {
            etTiengViet.setError("Vui lòng nhập tiếng Việt");
            return;
        }

        if (currentBaiGiang == null) {
            Toast.makeText(this, "Không tìm thấy thông tin bài giảng", Toast.LENGTH_SHORT).show();
            return;
        }

        // Xử lý capDoHSK null
        if (currentBaiGiang.getCapDoHSK() == null) {
            Log.w(TAG, "CapDoHSK is null, setting default HSK 1 for BaiGiang: " + currentBaiGiang.getTieuDe());
            currentBaiGiang.setCapDoHSK(new CapDoHSK(1, "HSK 1")); // Giá trị mặc định
            Toast.makeText(this, "Cấp độ HSK chưa được đặt, dùng mặc định HSK 1", Toast.LENGTH_SHORT).show();
        }

        TuVung tuVung;
        if (tuVungId != -1 && currentTuVung != null) {
            tuVung = currentTuVung;
        } else {
            tuVung = new TuVung();
            tuVung.setBaiGiang(currentBaiGiang);
        }

        tuVung.setTiengTrung(tiengTrung);
        tuVung.setPhienAm(phienAm);
        tuVung.setTiengViet(tiengViet);
        tuVung.setViDu(viDu);
        tuVung.setLoaiTu(loaiTuArray[selectedLoaiTuPosition]);
        tuVung.setCapDoHSK(currentBaiGiang.getCapDoHSK());

        progressDialog.show();
        if (tuVungId != -1) {
            tuVungRepository.updateTuVung(tuVungId, tuVung, new TuVungRepository.OnTuVungCallback() {
                @Override
                public void onSuccess(TuVung updatedTuVung) {
                    progressDialog.dismiss();
                    Toast.makeText(EditTuVungActivity.this, "Cập nhật từ vựng thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    progressDialog.dismiss();
                    Log.e(TAG, "Update tu vung error: " + errorMessage);
                    Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tuVungRepository.createTuVung(tuVung, new TuVungRepository.OnTuVungCallback() {
                @Override
                public void onSuccess(TuVung newTuVung) {
                    progressDialog.dismiss();
                    Toast.makeText(EditTuVungActivity.this, "Thêm từ vựng thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    progressDialog.dismiss();
                    Log.e(TAG, "Create tu vung error: " + errorMessage);
                    Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}