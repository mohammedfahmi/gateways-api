package com.musala.gatewaysapi.models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Setter
@Getter
@Component
public class BindingError {
    private String field;
    private String rejectedValue;
    private Boolean bindingFailure;
    private String objectName;
    private List<String> codes;
    private String defaultMessage;

    @Override
    public String toString(){
        return MessageFormat.format(
                "failed to bind value {0} to field {1}",
                 this.rejectedValue, this.field);
    }
}
