package com.wms.backend.shared.exception;

public class MultipartException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public MultipartException(String message) {
        super(ErrorConstants.MULTIPART_EXCEPTION_TYPE, message, "HomestayImage", "multipartfileerror");
    }
}
