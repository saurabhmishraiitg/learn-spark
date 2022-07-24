package org.nexus;

import org.junit.jupiter.api.Test;

public class Java16Test {
    @Test
    public void hello() {
        var message = """
            hello, java 16
                """;
        System.out.println(message);
    }
}