package com.video.newqu.bean;

/**
 * TinyHung@Outlook.com
 * 2018/4/4
 */

public class SubscribeInfo {
    private String touser;
    private String template_id;
    private String url;
    private String scene;
    private String title;
    private Data data;

    public static class Builder{
        private String touser;
        private String template_id;
        private String url;
        private String scene;
        private String title;
        private Data data;

        public Builder setTouser(String touser) {
            this.touser = touser;
            return this;
        }
        public Builder setTemplate_id(String template_id) {
            this.template_id = template_id;
            return this;
        }
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }
        public Builder setScene(String scene) {
            this.scene = scene;
            return this;
        }
        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }
        public Builder setData(Data data) {
            this.data = data;
            return this;
        }
        public SubscribeInfo create(){
            return new SubscribeInfo(this);
        }
    }


    private SubscribeInfo(Builder builder) {
        this.touser = builder.touser;
        this.template_id = builder.template_id;
        this.url = builder.url;
        this.scene = builder.scene;
        this.title = builder.title;
        this.data = builder.data;
    }

    public String getTouser() {
        return touser;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public String getUrl() {
        return url;
    }

    public String getScene() {
        return scene;
    }

    public String getTitle() {
        return title;
    }

    public Data getData() {
        return data;
    }
}

class Data {
    private Content content;

    public static class Builder {
        private Content content;

        public Builder setContent(Content content) {
            this.content = content;
            return this;
        }
        public Data create(){
            return new Data(this);
        }
    }

    private Data(Builder builder) {
        this.content = builder.content;
    }

    public Content getContent() {
        return content;
    }


}

class Content {
    private String value;
    private String color;

    public static class  Builder{
        private String value;
        private String color;

        public Builder setValue(String value) {
            this.value = value;
            return this;
        }
        public Builder setColor(String color) {
            this.color = color;
            return this;
        }

        public Content create(){
            return new Content(this);
        }

    }

    private Content(Builder builder) {
        this.value = builder.value;
        this.color = builder.color;
    }

    public String getValue() {
        return value;
    }

    public String getColor() {
        return color;
    }
}
