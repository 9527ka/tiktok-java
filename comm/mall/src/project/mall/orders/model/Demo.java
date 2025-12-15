package project.mall.orders.model;

import java.math.BigDecimal;

public class Demo {
    public static void main(String[] args) {
        int b1 = 0b01;
        int b2 = 0b10;

        System.out.println("--> b1:" + b1);
        System.out.println("--> b2:" + b2);
        System.out.println("--> b1 | b2:" + (b1 | b2));

        Double d = 0.000999;
        BigDecimal decimal = new BigDecimal("9.99E-4");

        System.out.println("---> d0:" + d + ", d:" + decimal.toString());
    }
}
