package com.fiap.hospital.appointment.dto;

public record LoginResponse(String token, String tokenType, String email, String role) {}
