package com.kongmu373.wxshop.result;

import com.google.auto.value.AutoValue;

import java.util.List;
import java.util.Map;

@AutoValue
public abstract class HttpResponse {

    public abstract int getCode();

    public abstract String getBody();


    public abstract Map<String, List<String>> getHeaders();

    public static HttpResponse create(int newCode, String newBody, Map<String, List<String>> newHeaders) {
        return builder()
                       .setCode(newCode)
                       .setBody(newBody)
                       .setHeaders(newHeaders)
                       .build();
    }

    public static Builder builder() {
        return new AutoValue_HttpResponse.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setCode(int newCode);

        public abstract Builder setBody(String newBody);

        public abstract Builder setHeaders(Map<String, List<String>> newHeaders);

        public abstract HttpResponse build();
    }
}
