package com.yuldashov.scheduler.dto.mail;

public record EmailMessage (
    String address,
    EmailType type,
    String body ) {}

