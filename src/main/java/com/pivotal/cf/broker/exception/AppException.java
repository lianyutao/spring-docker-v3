package com.pivotal.cf.broker.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;

public class AppException extends RuntimeException {
    private static final long serialVersionUID = 5439915454935047935L;

    public static final String copyright = "";

    private String errorCode;
    private Log log = LogFactory.getLog(this.getClass());

    private Collection<AppException> exceptions = new ArrayList<AppException>();
    private String messageKey = null;
    private Object[] messageArgs = null;

    // @Autowired
    // private MessageSource messageSource;

    public AppException(String message) {
        super(message);
    }

     public AppException(String errorCode, String message) {
         super(message);
         this.errorCode = errorCode;
     }

    public String getErrorCode() {
        return errorCode;
    }

    protected Throwable rootCause = null;

    public AppException(Throwable rootCause) {
        this.rootCause = rootCause;
    }

    public AppException(Throwable rootCause, String messageKey, Object[] messageArgs) {
        this.rootCause = rootCause;
        this.messageArgs = messageArgs;
        this.messageKey = messageKey;
    }

    @SuppressWarnings("rawtypes")
    public Collection getCollection() {
        return exceptions;
    }

    public void addException(AppException ex) {
        exceptions.add(ex);
    }

    public void setMessageKey(String key) {
        this.messageKey = key;
    }

    public String getMessageKey() {

        return messageKey;
    }

    public String getMessage(MessageSource messageSource, String defaultMessage, Locale locale) {

        String message;

        if (messageKey == null || messageKey.length() == 0) {
            message = super.getMessage();
        } else {
            message = messageSource.getMessage(messageKey, messageArgs, defaultMessage, locale);
        }

        return message;
    }

    public void setMessageArgs(Object[] args) {
        this.messageArgs = args;
    }

    public Object[] getMessageArgs() {
        return messageArgs;
    }

    public void setRootCause(Throwable anException) {
        rootCause = anException;
    }

    public Throwable getRootCause() {
        return rootCause;
    }

    public void printStackTrace() {
        log.info(System.err);
        printStackTrace(System.err);
    }

    public void printStackTrace(PrintStream outStream) {
        printStackTrace(new PrintWriter(outStream));
    }

    public void printStackTrace(PrintWriter writer) {
        super.printStackTrace(writer);
        if (getRootCause() != null) {
            getRootCause().printStackTrace(writer);
        }
        writer.flush();
    }
}
