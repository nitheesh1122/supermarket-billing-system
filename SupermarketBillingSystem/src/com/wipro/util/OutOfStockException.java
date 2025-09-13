package com.wipro.supermarket.util;

public class OutOfStockException extends Exception {
    @Override
    public String toString() {
        return "Requested quantity is not available in stock!";
    }
}
