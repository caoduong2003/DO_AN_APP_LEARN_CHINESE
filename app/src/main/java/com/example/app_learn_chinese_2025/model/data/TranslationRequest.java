package com.example.app_learn_chinese_2025.model.data;

public class TranslationRequest {
    private String text;

    public TranslationRequest(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}