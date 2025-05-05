package ar.com.game.services;

public class ServiceResponse {

    private boolean success;
    private String message;
    private Object data; // Opcional: puede ser un User, un error, etc.

    public ServiceResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ServiceResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
