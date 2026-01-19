package com.group4.ecommerceplatform.exceptions.handlers;
import com.group4.ecommerceplatform.exceptions.NotFoundException;
import com.group4.ecommerceplatform.responses.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalWebExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public String handleValidationException(NotFoundException ex, Model model){
        model.addAttribute("error", ex.getMessage());
        return "admin/pages/errors/404";
    }
}
