package com.pivotal.cf.broker.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;

import com.pivotal.cf.broker.exception.AppException;

public class LogUtil {
    public static void exception(Logger logger, Throwable e) {
        StringWriter sw = new StringWriter();
        try {
            e.printStackTrace(new PrintWriter(sw));
            if (e instanceof AppException) {
                logger.warn(logger.getName() + " occured " + e.getClass().getSimpleName() + " " + ((AppException)e).getMessageKey());
            } else {
                logger.warn(logger.getName() + " occured " + e.getClass().getSimpleName() + " " + e.getMessage());
            }
            logger.warn(sw.toString());
        } finally {
            try {
                sw.close();
            } catch (IOException e1) {
                logger.error(e1);
            }
        }
    }

    public static void exception(MessageSource messageSource, Logger logger, Throwable e, Locale locale) {
        StringWriter sw = new StringWriter();
        try {
            e.printStackTrace(new PrintWriter(sw));
            if (e instanceof AppException) {
                logger.warn(logger.getName() + " occured " + e.getClass().getSimpleName() + " " + ((AppException)e).getMessage(messageSource, ((AppException) e).getMessageKey(), locale));
            } else {
                logger.warn(logger.getName() + " occured " + e.getClass().getSimpleName() + " " + e.getMessage());
            }
            logger.warn(sw.toString());
        } finally {
            try {
                sw.close();
            } catch (IOException e1) {
                logger.error(e1);
            }
        }
    }
}
