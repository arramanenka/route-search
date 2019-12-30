package com.romanenko.routefinder.web.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ErrorResponse {
    private final String errorMessage;
    private final long timestamp;
}
