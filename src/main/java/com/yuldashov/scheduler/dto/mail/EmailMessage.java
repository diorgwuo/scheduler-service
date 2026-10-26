package com.yuldashov.scheduler.dto.mail;

public record EmailMessage (
    String address,
    String title,
    String body ) {}

