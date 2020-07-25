package com.code4ro.legalconsultation.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Slf4j
public class I18nService {

    private final MessageSource messageSource;

    @Autowired
    public I18nService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String translate(final String i18nKey, Object... i18nArguments) {
        if (i18nKey == null) {
            return null;
        }
        final Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(i18nKey, i18nArguments, locale);
        } catch (final NoSuchMessageException exception) {
            log.error(exception.getLocalizedMessage(), exception);
            return i18nKey;
        }
    }
}
