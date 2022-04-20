package com.backendspringboot.jwt;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
//this class will handler the http response to the user
public class HttpResponse {
    private int httpStatusCode; //200, 201, 400, 500
    private HttpStatus httpStatus; //the status of the response
    private String reason; //the reason of the error
    private String message; //the message displayed to the user

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy hh:mm:ss", timezone = "Africa/Tunis") //format the shape of the date
    private Date timeStamp;

    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
        this.timeStamp =  new Date();
    }
}
