package com.example.app_learn_chinese_2025.model.data;

public class ClaudeResponse {
    private String object;
    private String vocabulary;
    private String pinyin;
    private String vietnamese;
    private String example;
    private boolean success;
    private String error;

    // Constructors, getters, setters
    public ClaudeResponse() {}

    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }

    public String getVocabulary() { return vocabulary; }
    public void setVocabulary(String vocabulary) { this.vocabulary = vocabulary; }

    public String getPinyin() { return pinyin; }
    public void setPinyin(String pinyin) { this.pinyin = pinyin; }

    public String getVietnamese() { return vietnamese; }
    public void setVietnamese(String vietnamese) { this.vietnamese = vietnamese; }

    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
