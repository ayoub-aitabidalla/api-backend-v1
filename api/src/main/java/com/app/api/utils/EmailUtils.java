package com.app.api.utils;

public class EmailUtils {
    public static String getEmailMessage(String name, String host, String confirmationKey) {
        return "Hello " + name + ",\n\nYour new account has been created. Please click the link below to verify your account. \n\n" +
                getVerificationUrl(host, confirmationKey) + "\n\nThe support Team";
    }

    public static String getVerificationUrl(String host, String confirmationKey) {
        return host + "/api/v1/auth?token=" + confirmationKey;
    }
}
