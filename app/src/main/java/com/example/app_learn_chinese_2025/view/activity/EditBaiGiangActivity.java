package com.example.app_learn_chinese_2025.view.activity;

import android.app.ProgressDialog;
import android.content.Intent;
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
import com.example.app_learn_chinese_2025.model.data.ChuDe;
import com.example.app_learn_chinese_2025.model.data.LoaiBaiGiang;
import com.example.app_learn_chinese_2025.model.data.User;
import com.example.app_learn_chinese_2025.model.repository.BaiGiangRepository;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> saveBaiGiang());
        btnCancel.setOnClickListener(v -> finish());

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
}