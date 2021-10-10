package com.musala.gatewaysapi.validations;

import com.musala.gatewaysapi.models.GatewayPageRequest;
import com.musala.gatewaysapi.services.GatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.MessageFormat;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Constraint;

import static com.musala.gatewaysapi.validations.ValidationErrorMessages.REQUESTED_PAGE_DOES_NOT_EXIST;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Constraint(validatedBy = PageIsValidImpl.class)
public @interface PageIsValid {
}

@Component
@RequiredArgsConstructor
class PageIsValidImpl
        implements ConstraintValidator<PageIsValid, GatewayPageRequest> {


    private final GatewayService gatewayService;
    private final ValidationUtil validationUtil = new ValidationUtil();

    @Override
    public void initialize(final PageIsValid page) {
        //  Nothing to initialize: Just validating the page number against the DB
    }

    @Override
    public boolean isValid(final GatewayPageRequest gatewayPageRequest, final ConstraintValidatorContext constraintValidatorContext) {
        if( gatewayPageRequest.getPage().equals(null) || gatewayPageRequest.getSize().equals(null))
            return false;
        Integer gatewaysCount = gatewayService.getAllGatewaysCount();
        if( (gatewayPageRequest.getSize() * gatewayPageRequest.getPage() +1) >  gatewaysCount ) {
            validationUtil.customViolationTemplateGeneration(
                    MessageFormat.format(REQUESTED_PAGE_DOES_NOT_EXIST, gatewayPageRequest.getPage()),
                    constraintValidatorContext);
            return false;
        }
        return true;
    }
}