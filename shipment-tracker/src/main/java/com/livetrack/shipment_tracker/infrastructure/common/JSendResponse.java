package com.livetrack.shipment_tracker.infrastructure.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JSendResponse<T> {

    private final String status;
    private final T data;
    private final String message;
    private final String code;

    private JSendResponse(String status, T data, String message, String code) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public static <T> JSendResponse<T> success(T data) {
        return new JSendResponse<>("success", data, null, null);
    }

    public static <T> JSendResponse<T> fail(T data) {
        return new JSendResponse<>("fail", data, null, null);
    }

    public static JSendResponse<Void> error(String message, String code) {
        return new JSendResponse<>("error", null, message, code);
    }

}