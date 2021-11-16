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

import static com.musala.gatewaysapi.validations.ValidationErrorMessages.DEFAULT_REQUESTED_PAGE_NOT_AVAILABLE;
import static com.musala.gatewaysapi.validations.ValidationErrorMessages.REQUESTED_PAGE_NOT_AVAILABLE;
import static com.musala.gatewaysapi.validations.ValidationUtil.customViolationTemplateGeneration;

@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Constraint(validatedBy = PageIsValidImpl.class)
public @interface PageIsValid {
    String message() default DEFAULT_REQUESTED_PAGE_NOT_AVAILABLE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

@Component
@RequiredArgsConstructor
class PageIsValidImpl
        implements ConstraintValidator<PageIsValid, GatewayPaginationRequest> {
    private final GatewayService gatewayService;
    @Override
    public void initialize(final PageIsValid page) {
        ConstraintValidator.super.initialize(page);
    }
    @Override
    public boolean isValid(final GatewayPaginationRequest request, final ConstraintValidatorContext constraintValidatorContext) {
        String pageNotValid = MessageFormat.format(REQUESTED_PAGE_NOT_AVAILABLE, request.getPage());
        int gatewaysCount = gatewayService.getAllGatewaysCount();
        if(isPageNotValid(gatewaysCount, request.getPage(), request.getSize())) {
            customViolationTemplateGeneration(pageNotValid, constraintValidatorContext);
            return false;
        }
        return true;
    }
    private boolean isPageNotValid(int gatewaysCount, int page, int size) {
        return isGatewaysCountZeroAndPageIsNot(gatewaysCount, page) || isGatewayCountLessThanPageFirstItem(gatewaysCount, page, size);
    }
    private boolean isGatewaysCountZeroAndPageIsNot(int gatewaysCount, int page) {
        return gatewaysCount == 0 && page > 0;
    }
    private boolean isGatewayCountLessThanPageFirstItem(int gatewaysCount, int page, int size) {
        return page > 0 ? (size * page + 1 > gatewaysCount) : (page != 0) ;
    }
}