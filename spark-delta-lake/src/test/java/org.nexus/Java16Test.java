package org.nexus;

import org.junit.jupiter.api.Test;

public class Java16Test {
    @Test
    public void hello() {
        String message = "This test runs with Java 16 and above only";
//        var message = """
//            hello, java 16
//                """;
        System.out.println(message);
    }
}