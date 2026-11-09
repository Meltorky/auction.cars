package auction.shared.HttpRequests;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.util.List;

// APPLYING THE STATIC FACTORY METHOD

@Getter
@JsonPropertyOrder({"succeeded", "message", "data", "errors"})
public class GlobalResponse<T> {

    private final boolean succeeded;
    private final T data;
    private final String message;
    private final List<ErrorMessage> errors;

    private GlobalResponse(
            boolean succeeded,
            T data,
            List<ErrorMessage> errors,
            String message) {
        this.succeeded = succeeded;
        this.data = data;
        this.errors = errors;
        this.message = message;
    }

    // Success response with an optional Data
    public static GlobalResponse<Void> success(String message) {
        return new GlobalResponse<Void>(true, null, null, message);
    }

    // Success response with Data
    public static <T> GlobalResponse<T> success(T data, String message) {
        return new GlobalResponse<T>(true, data, null, message);
    }

    // Error response
    public static GlobalResponse<Void> error(List<ErrorMessage> errors) {
        return new GlobalResponse<Void>(false, null, errors, null);
    }


    public record ErrorMessage(String error) {
    }
}
