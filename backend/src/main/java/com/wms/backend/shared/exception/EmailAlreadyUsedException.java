package com.wms.backend.shared.exception;

public class EmailAlreadyUsedException extends ConflictException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyUsedException() {
        super(ErrorConstants.EMAIL_ALREADY_USED_TYPE, "The email has already been used!", "userManagement",
                "emailexists");
    }

}
