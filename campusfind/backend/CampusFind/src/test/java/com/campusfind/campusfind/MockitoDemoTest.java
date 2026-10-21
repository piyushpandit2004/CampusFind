
package com.campusfind.campusfind;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MockitoDemoTest {

    interface Calculator {
        int add(int a, int b);
    }

    @Test
    void mockTest() {
        Calculator calculator = mock(Calculator.class);

        when(calculator.add(10, 20)).thenReturn(30);

        int result = calculator.add(10, 20);

        assertEquals(30, result);
    }
}
