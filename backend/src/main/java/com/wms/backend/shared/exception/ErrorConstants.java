package com.wms.backend.shared.exception;

import java.net.URI;

public final class ErrorConstants {

    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";

    public static final URI DEFAULT_TYPE = URI.create("/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create("/constraint-violation");
    public static final URI INVALID_PASSWORD_TYPE = URI.create("/invalid-password");
    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create("/email-already-used");
    public static final URI LOGIN_ALREADY_USED_TYPE = URI.create("/login-already-used");
    public static final URI MULTIPART_EXCEPTION_TYPE = URI.create("/multipart-error");

    public static final URI ENTITY_NOT_FOUND_TYPE = URI.create("/entity-not-found");
    public static final URI UNAUTHORIZED = URI.create("/unauthorized");
    public static final URI FORBIDDEN = URI.create("/forbidden");
    public static final URI CONFLICT = URI.create("/conflict");
    public static final URI TOO_MANY_REQUESTS = URI.create("/too-many-requests");

    private ErrorConstants() {
    }
}
