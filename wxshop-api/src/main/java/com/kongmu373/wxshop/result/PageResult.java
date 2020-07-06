package com.kongmu373.wxshop.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.auto.value.AutoValue;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.List;

@AutoValue
@JsonSerialize(as = PageResult.class)
@JsonDeserialize(builder = AutoValue_PageResult.Builder.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PageResult<T> implements Serializable {

    @JsonProperty("pageNum")
    public abstract Integer pageNum();

    @JsonProperty("pageSize")
    public abstract Integer pageSize();

    @JsonProperty("totalPage")
    public abstract Integer totalPage();

    @JsonProperty("data")
    @Nullable
    public abstract List<T> data();

    public static <T> PageResult<T> create(Integer pageNum, Integer pageSize, Integer totalPage, List<T> data) {
        return PageResult.<T>builder()
                       .pageNum(pageNum)
                       .pageSize(pageSize)
                       .totalPage(totalPage)
                       .data(data)
                       .build();
    }

    public static <T> Builder<T> builder() {
        return new AutoValue_PageResult.Builder<>();
    }


    @AutoValue.Builder
    public abstract static class Builder<T> {
        @JsonProperty("pageNum")
        public abstract Builder<T> pageNum(Integer pageNum);

        @JsonProperty("pageSize")
        public abstract Builder<T> pageSize(Integer pageSize);

        @JsonProperty("totalPage")
        public abstract Builder<T> totalPage(Integer totalPage);

        @JsonProperty("data")
        public abstract Builder<T> data(List<T> data);

        public abstract PageResult<T> build();
    }
}
