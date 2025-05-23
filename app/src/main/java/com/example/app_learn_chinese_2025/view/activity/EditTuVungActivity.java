package com.example.app_learn_chinese_2025.view.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.ArrayList;
import java.util.List;

public class EditTuVungActivity extends AppCompatActivity {
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

    private List<CapDoHSK> capDoHSKList;
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
        baiGiangRepository = new BaiGiangRepository(sessionManager);
        tuVungRepository = new TuVungRepository(sessionManager);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xử lý...");
        progressDialog.setCancelable(false);

        // Initialize loaiTuArray
        loaiTuArray = new String[]{
                "Danh từ", "Động từ", "Tính từ", "Đại từ", "Phó từ",
                "Giới từ", "Liên từ", "Thán từ", "Trợ từ", "Lượng từ"
        };

        capDoHSKList = new ArrayList<>();
    }

    private void setupListeners() {
        btnGeneratePinyin.setOnClickListener(v -> generatePinyin());
        btnGenerateAudio.setOnClickListener(v -> generateAudio());
        btnSave.setOnClickListener(v -> saveTuVung());
        btnCancel.setOnClickListener(v -> finish());

        // Spinner listener
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
        baiGiangRepository.getBaiGiangById(baiGiangId, new BaiGiangRepository.OnBaiGiangCallback() {
            @Override
            public void onSuccess(BaiGiang baiGiang) {
                currentBaiGiang = baiGiang;

                // Now load CapDoHSK info
                loadCapDoHSK();
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void loadCapDoHSK() {
        baiGiangRepository.getAllCapDoHSK(new BaiGiangRepository.OnCapDoHSKListCallback() {
            @Override
            public void onSuccess(List<CapDoHSK> capDoHSKs) {
                capDoHSKList = capDoHSKs;
            }

            @Override
            public void onError(String errorMessage) {
                Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTuVung(long id) {
        progressDialog.show();

        tuVungRepository.getTuVungById(id, new TuVungRepository.OnTuVungCallback() {
            @Override
            public void onSuccess(TuVung tuVung) {
                currentTuVung = tuVung;

                // Set values to UI elements
                etTiengTrung.setText(tuVung.getTiengTrung());
                etPhienAm.setText(tuVung.getPhienAm());
                etTiengViet.setText(tuVung.getTiengViet());
                etViDu.setText(tuVung.getViDu());

                // Set spinner selection
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

                // Store the audio URL in the currentTuVung object
                if (currentTuVung == null) {
                    currentTuVung = new TuVung();
                }

                currentTuVung.setAudioURL(result);
            }

            @Override
            public void onError(String errorMessage) {
                progressDialog.dismiss();
                Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTuVung() {
        // Validate input
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

        // Create or update TuVung object
        TuVung tuVung;
        if (tuVungId != -1 && currentTuVung != null) {
            // Edit mode
            tuVung = currentTuVung;
        } else {
            // Add mode
            tuVung = new TuVung();

            // Set bai giang
            if (currentBaiGiang != null) {
                tuVung.setBaiGiang(currentBaiGiang);
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin bài giảng", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Update fields
        tuVung.setTiengTrung(tiengTrung);
        tuVung.setPhienAm(phienAm);
        tuVung.setTiengViet(tiengViet);
        tuVung.setViDu(viDu);
        tuVung.setLoaiTu(loaiTuArray[selectedLoaiTuPosition]);

        // Set CapDoHSK (same as bai giang's level)
        if (currentBaiGiang != null && currentBaiGiang.getCapDoHSK() != null) {
            tuVung.setCapDoHSK(currentBaiGiang.getCapDoHSK());
        } else if (!capDoHSKList.isEmpty()) {
            tuVung.setCapDoHSK(capDoHSKList.get(0)); // Default to first one
        }

        progressDialog.show();

        if (tuVungId != -1) {
            // Update existing
            tuVungRepository.updateTuVung(tuVungId, tuVung, new TuVungRepository.OnTuVungCallback() {
                @Override
                public void onSuccess(TuVung updatedTuVung) {
                    progressDialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    progressDialog.dismiss();
                    Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Create new
            tuVungRepository.createTuVung(tuVung, new TuVungRepository.OnTuVungCallback() {
                @Override
                public void onSuccess(TuVung newTuVung) {
                    progressDialog.dismiss();
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onError(String errorMessage) {
                    progressDialog.dismiss();
                    Toast.makeText(EditTuVungActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}