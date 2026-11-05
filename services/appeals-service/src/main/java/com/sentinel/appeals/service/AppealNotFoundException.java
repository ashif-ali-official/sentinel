package com.sentinel.appeals.service;
public class AppealNotFoundException extends RuntimeException { public AppealNotFoundException(String id){super("Appeal not found: "+id);} }
