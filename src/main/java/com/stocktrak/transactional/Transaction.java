package com.stocktrak.transactional;

/**
 * Created by Douglas on 2/14/2015.
 */
public class Transaction {
    private int quantity;
    private String symbol;
    private Type type;
    public enum Type {
        BUY, SELL
    }
    public Transaction(int quantity, String symbol, Type type) {
        this.quantity = quantity;
        this.symbol = symbol;
        this.type = type;
    }

    public int getQuantity() {

        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
