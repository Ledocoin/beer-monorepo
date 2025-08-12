package org.example.sideservicefbeerproj.component;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.experimental.UtilityClass;

import java.util.Random;

@UtilityClass
public class IdGenerator {
    private static final Random random = new Random();

    private static final char[] alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ_abcdefghijklmnopqrstuvwxyz".toCharArray();

    private static final int size = 12;

    public static String generate() {
        return NanoIdUtils.randomNanoId(random, alphabet, size);
    }
}
