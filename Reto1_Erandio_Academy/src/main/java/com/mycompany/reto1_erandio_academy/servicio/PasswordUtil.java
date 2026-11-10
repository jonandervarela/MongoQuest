
package com.mycompany.reto1_erandio_academy.servicio;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtil {

    public static String generarHash(String password) {

        try {

            MessageDigest md =
                    MessageDigest.getInstance("SHA-256");

            byte[] resultado =
                    md.digest(password.getBytes("UTF-8"));

            StringBuilder hexadecimal = new StringBuilder();

            for (byte b : resultado) {

                hexadecimal.append(
                        String.format("%02x", b)
                );
            }

            return hexadecimal.toString();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error al proteger la contraseña.", e);
        }
    }
}
