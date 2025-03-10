package com.deportes.api.records;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponseBody<T>(String message, T data) {
}