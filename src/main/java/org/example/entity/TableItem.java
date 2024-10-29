package org.example.entity;

public class TableItem {
    String id;
    String amount;

    public TableItem() {
    }

    public TableItem(String id, String amount) {
        this.id = id;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
