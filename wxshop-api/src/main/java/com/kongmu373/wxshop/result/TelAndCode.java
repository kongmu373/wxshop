package com.kongmu373.wxshop.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.springframework.lang.Nullable;

import java.io.Serializable;

@AutoValue
@JsonSerialize(as = TelAndCode.class)
@JsonDeserialize(builder = AutoValue_TelAndCode.Builder.class)
public abstract class TelAndCode implements Serializable {
    @Nullable
    @JsonProperty("tel")
    public abstract String getTel();

    @Nullable
    @JsonProperty("code")
    public abstract String getCode();

    public static TelAndCode create(String newTel, String newCode) {
        return builder()
                       .setTel(newTel)
                       .setCode(newCode)
                       .build();
    }

    public static Builder builder() {
        return new AutoValue_TelAndCode.Builder();
    }


    @AutoValue.Builder
    public abstract static class Builder {
        @JsonProperty("tel")
        public abstract Builder setTel(String newTel);

        @JsonProperty("code")
        public abstract Builder setCode(String newCode);

        public abstract TelAndCode build();
    }
}
