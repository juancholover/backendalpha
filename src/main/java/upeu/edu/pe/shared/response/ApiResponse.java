package upeu.edu.pe.shared.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String error;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, null, LocalDateTime.now());
    }

    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> success(String message) {
        return (ApiResponse<T>) new ApiResponse<>(true, message, null, null, LocalDateTime.now());
    }

    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> error(String message, String errorDetails) {
        return (ApiResponse<T>) new ApiResponse<>(false, message, null, errorDetails, LocalDateTime.now());
    }

    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> error(String message) {
        return (ApiResponse<T>) new ApiResponse<>(false, message, null, null, LocalDateTime.now());
    }
}