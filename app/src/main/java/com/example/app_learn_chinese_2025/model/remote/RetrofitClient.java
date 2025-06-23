package com.example.app_learn_chinese_2025.model.remote;

import com.example.app_learn_chinese_2025.util.Constants;
import com.example.app_learn_chinese_2025.util.DateDeserializer;
import com.example.app_learn_chinese_2025.util.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static RetrofitClient instanceWithToken;
    private static RetrofitClient instanceWithoutToken;
    private final Retrofit retrofit;
    private final ApiService apiService;

    private RetrofitClient(SessionManager sessionManager, boolean useToken) {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);

        if (useToken && sessionManager != null) {
            clientBuilder.addInterceptor(chain -> {
                Request original = chain.request();
                String token = sessionManager.getToken();
                if (token != null && !token.isEmpty()) {
                    Request.Builder builder = original.newBuilder()
                            .header("Authorization", "Bearer " + token);
                    Request request = builder.build();
                    return chain.proceed(request);
                }
                return chain.proceed(original);
            });
        }

        OkHttpClient client = clientBuilder.build();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateDeserializer())
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance(SessionManager sessionManager) {
        if (instanceWithToken == null) {
            instanceWithToken = new RetrofitClient(sessionManager, true);
        }
        return instanceWithToken;
    }

    public static synchronized RetrofitClient getInstanceWithoutToken() {
        if (instanceWithoutToken == null) {
            instanceWithoutToken = new RetrofitClient(null, false);
        }
        return instanceWithoutToken;
    }

    public ApiService getApiService() {
        return apiService;
    }
}