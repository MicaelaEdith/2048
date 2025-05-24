package ar.com.game.services;

public class ServiceResponse {

    private boolean success;
    private String message;
    private Object data;   // Puede ser un User, error, etc.
    private Object extra;  // Opcional: para retornar un segundo objeto (como un Duel pendiente)

    // Constructor básico
    public ServiceResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Constructor con datos
    public ServiceResponse(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Constructor con datos y extra
    public ServiceResponse(boolean success, String message, Object data, Object extra) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.extra = extra;
    }

    // Getters y setters
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

    public Object getExtra() {
        return extra;
    }

    public void setExtra(Object extra) {
        this.extra = extra;
    }
}
