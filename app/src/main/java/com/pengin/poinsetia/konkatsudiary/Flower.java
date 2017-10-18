package com.pengin.poinsetia.konkatsudiary;


import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Flower extends RealmObject {
    @PrimaryKey
    private int id;// PrimaryKey
    private String color;  // 色カラム
    private String flower;  // 花カラム
    private int position; // リスト位置

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getFlower() { return flower; }

    public void setFlower(String flower) { this.flower = flower; }

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public int getPosition() {
        return position;
    }

    public void setPosition(int pos) {
        this.position = pos;
    }
}
