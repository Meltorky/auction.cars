package auction.auctions.shared.HttpRequests;

import lombok.Getter;

@Getter
public class CustomResponseException extends RuntimeException {

    private final int code;

    public CustomResponseException(int code, String message) {
        this.code = code;
        super(message);
    }


    public static CustomResponseException resourceNotFound(String message) {
        return new CustomResponseException(404, message);
    }

    public static CustomResponseException resourceMisMatch(String message) {
        return new CustomResponseException(407, message);
    }

    public static CustomResponseException resourceDuplicateData(String message) {
        return new CustomResponseException(400, message);
    }

    public static CustomResponseException serverDown(String message) {
        return new CustomResponseException(500, message);
    }

    public static CustomResponseException forbidden(String message) {
        return new CustomResponseException(401, message);
    }
}
