package com.example.app_learn_chinese_2025.model.data;

public class ClaudeRequest {
    private String model;
    private int max_tokens;
    private Message[] messages;

    public static class Message {
        private String role;
        private Content[] content;

        public Message(String role, Content[] content) {
            this.role = role;
            this.content = content;
        }

        // Getters and setters
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public Content[] getContent() { return content; }
        public void setContent(Content[] content) { this.content = content; }
    }

    public static class Content {
        private String type;
        private String text;
        private ImageSource source;

        // For text content
        public Content(String type, String text) {
            this.type = type;
            this.text = text;
        }

        // For image content
        public Content(String type, ImageSource source) {
            this.type = type;
            this.source = source;
        }

        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        public ImageSource getSource() { return source; }
        public void setSource(ImageSource source) { this.source = source; }
    }

    public static class ImageSource {
        private String type;
        private String media_type;
        private String data;

        public ImageSource(String type, String media_type, String data) {
            this.type = type;
            this.media_type = media_type;
            this.data = data;
        }

        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getMedia_type() { return media_type; }
        public void setMedia_type(String media_type) { this.media_type = media_type; }
        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
    }

    // Constructors, getters, setters
    public ClaudeRequest() {}

    public ClaudeRequest(String model, int max_tokens, Message[] messages) {
        this.model = model;
        this.max_tokens = max_tokens;
        this.messages = messages;
    }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public int getMax_tokens() { return max_tokens; }
    public void setMax_tokens(int max_tokens) { this.max_tokens = max_tokens; }
    public Message[] getMessages() { return messages; }
    public void setMessages(Message[] messages) { this.messages = messages; }
}