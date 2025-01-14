package ru.yandex.practicum.filmorate.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponse;

public class BadRequestResponse implements ErrorResponse {
    private final HttpStatusCode statusCode;
    private final ProblemDetail body;

    public BadRequestResponse(String title, String message) {
        this.statusCode = HttpStatusCode.valueOf(400); // BAD_REQUEST
        this.body = ProblemDetail.forStatusAndDetail(statusCode, message);
        this.body.setTitle(title);
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    @Override
    public HttpHeaders getHeaders() {
        return HttpHeaders.EMPTY;
    }

    @Override
    public ProblemDetail getBody() {
        return body;
    }
}