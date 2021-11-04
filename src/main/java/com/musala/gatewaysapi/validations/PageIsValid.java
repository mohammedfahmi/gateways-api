package com.musala.gatewaysapi.validations;

import com.musala.gatewaysapi.models.GatewayPaginationRequest;
import com.musala.gatewaysapi.services.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.MessageFormat;
import javax.validation.*;

import static com.musala.gatewaysapi.validations.ValidationErrorMessages.REQUESTED_DEFAULT_PAGE_NOT_AVAILABLE;
import static com.musala.gatewaysapi.validations.ValidationErrorMessages.REQUESTED_PAGE_NOT_AVAILABLE;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = PageIsValidImpl.class)
public @interface PageIsValid {
    String message() default REQUESTED_DEFAULT_PAGE_NOT_AVAILABLE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
@RequiredArgsConstructor
class PageIsValidImpl
        implements ConstraintValidator<PageIsValid, GatewayPaginationRequest> {
    private final GatewayService gatewayService;
    private final ValidationUtil validationUtil = new ValidationUtil();
    @Override
    public void initialize(final PageIsValid page) {/*  Nothing to initialize: Just validating the page number against the DB */}
    @Override
    public boolean isValid(final GatewayPaginationRequest request, final ConstraintValidatorContext constraintValidatorContext) {
        int gatewaysCount = gatewayService.getAllGatewaysCount();
        if(isGatewayCountLessThanPageFirstItem(gatewaysCount, request) || isGatewaysCountZeroAndPageIsNot(gatewaysCount, request.getPage())) {
            validationUtil.customViolationTemplateGeneration(
                    MessageFormat.format(REQUESTED_PAGE_NOT_AVAILABLE, request.getPage()),constraintValidatorContext);
            return false;
        }
        return true;
    }
    private boolean isGatewayCountLessThanPageFirstItem(int gatewaysCount, GatewayPaginationRequest request) {
        return request.getSize() * request.getPage() + 1 > gatewaysCount;
    }
    private boolean isGatewaysCountZeroAndPageIsNot(int gatewaysCount, int page) {
        return gatewaysCount == 0 && page > 0;
    }
}