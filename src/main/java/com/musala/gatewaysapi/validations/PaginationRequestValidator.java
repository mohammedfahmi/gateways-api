package com.musala.gatewaysapi.validations;

import com.musala.gatewaysapi.models.GatewayPaginationRequest;
import com.musala.gatewaysapi.services.GatewayService;
import lombok.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.text.MessageFormat;

import static com.musala.gatewaysapi.validations.ValidationErrorMessages.*;

@Data
@Builder
@RequiredArgsConstructor
@Component
public class PaginationRequestValidator implements Validator {
    private final GatewayService gatewayService;
    @Override
    public boolean supports(Class<?> clazz) {
        return GatewayPaginationRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        int gatewaysCount = gatewayService.getAllGatewaysCount();
        GatewayPaginationRequest request = (GatewayPaginationRequest) target;
        if(isGatewayCountLessThanPageFirstItem(gatewaysCount, request) && isGatewaysCountZeroAndPageIsNot(gatewaysCount, request.getPage()))
            errors.rejectValue("page", MessageFormat.format(REQUESTED_PAGE_NOT_AVAILABLE, request.getPage()), REQUESTED_DEFAULT_PAGE_NOT_AVAILABLE);
    }

    private boolean isGatewayCountLessThanPageFirstItem(int gatewaysCount, GatewayPaginationRequest request) {
        return request.getSize() * request.getPage() + 1 > gatewaysCount;
    }

    private boolean isGatewaysCountZeroAndPageIsNot(int gatewaysCount, int page) {
        return gatewaysCount == 0 && page > 0;
    }
}
