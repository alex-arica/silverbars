package silverbars.adapter.api.rest.orders.dto;

public class ErrorDto {

    public String message;

    public ErrorDto(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorDto{" +
                "message='" + message + '\'' +
                '}';
    }
}
