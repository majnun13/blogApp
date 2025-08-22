package com.nurlan.handler;

import com.nurlan.exception.BaseException;
import com.nurlan.exception.DuplicateResourceException;
import com.nurlan.exception.ForbiddenException;
import com.nurlan.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.*;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BaseException.class})
    public ResponseEntity<ApiError<?>> handleBaseException(BaseException ex, WebRequest request){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ApiError<?>> handleMethoArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request){
        Map<String, List<String>> map = new HashMap<>();
        for(ObjectError objError : ex.getBindingResult().getAllErrors()){
            String fieldName = ((FieldError)objError).getField();
            if(map.containsKey(fieldName)){
                map.put(fieldName, addValue(map.get(fieldName), objError.getDefaultMessage()));
            }else{
                map.put(fieldName, addValue(new ArrayList<>(), objError.getDefaultMessage()));
            }
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(createApiError(HttpStatus.BAD_REQUEST, map, request));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError<?>> handleUnauthorized(UnauthorizedException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(createApiError(HttpStatus.UNAUTHORIZED, ex.getMessage(), request));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiError<?>> handleForbidden(ForbiddenException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) // 403
                .body(createApiError(HttpStatus.FORBIDDEN, ex.getMessage(), request));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError<?>> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409 daha doğru
                .body(createApiError(HttpStatus.CONFLICT, ex.getMessage(), request));
    }
    private List<String> addValue(List<String> list, String newValue){
        list.add(newValue);
        return list;
    }
    private String getHostName(){
        try {
            return Inet4Address.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "";
    }

    // EKLE: status alan overload
    public <E> ApiError<E> createApiError(HttpStatus status, E message, WebRequest request){
        ApiError<E> apiError = new ApiError<>();
        apiError.setStatus(status.value());

        ErrorDetail<E> exception = new ErrorDetail<>();
        exception.setPath(request.getDescription(false).substring(4));
        exception.setCreatedDate(new Date());
        exception.setMessage(message);
        exception.setHostName(getHostName());

        apiError.setException(exception);
        return apiError;
    }

    public <E> ApiError<E> createApiError(E message, WebRequest request){
        return createApiError(HttpStatus.INTERNAL_SERVER_ERROR, message, request);
    }

}
