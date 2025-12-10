package com.paymentchain.customer.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessRuleException extends Exception {

    private long id;
    private String code;
    private HttpStatus httpStatus;

    public BusinessRuleException(long id, String code, String message, HttpStatus httpStatus) {
        super(message);
        this.id = id;
        this.code = code;
        this.httpStatus = httpStatus;
    }


    public BusinessRuleException(String code, String message, HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public BusinessRuleException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
