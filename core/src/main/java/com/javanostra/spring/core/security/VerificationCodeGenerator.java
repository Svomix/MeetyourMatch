package com.javanostra.spring.core.security;

import java.security.SecureRandom;

public class VerificationCodeGenerator {
    private static final SecureRandom random = new SecureRandom();
    public static String generateCode() {
        char[] code = new char[4];

        for (int i = 0; i < 4; i++) {
            code[i] = Character.forDigit(random.nextInt(10), 10);
        }

        return String.valueOf(code);
    }
}
