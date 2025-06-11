package com.example.app_learn_chinese_2025.api;

import com.example.app_learn_chinese_2025.model.data.BaiGiang;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface BaiGiangApiService {
    @GET("api/bai-giang")
    Call<List<BaiGiang>> getBaiGiangList(@Header("Authorization") String token);

    @GET("api/bai-giang/{id}")
    Call<BaiGiang> getBaiGiangDetail(@Path("id") Long id, @Header("Authorization") String token);
} 