package com.yukidev.bandolier.ui;

/**
 * Created by IamKl on 3/19/2017.
 */

public class Bullet {

    // Bullet basics
    private String title;
    private String orderDate;
    private String date;
    private String action;
    private String result;
    private String impact;

    // Default constructor required by Firebase
    public Bullet (){

    }

    //Constructor
    public Bullet (String title, String date, String action, String result, String impact) {
        this.title = title;
        this.date = date;
        this.action = action;
        this.result = result;
        this.impact = impact;

    }

    //Getters
    public String getTitle() {return title;}
    public String getOrderDate() {return orderDate;}
    public String getDate() {return date;}
    public String getAction() {return action;}
    public String getResult() {return result;}
    public String getImpact() {return impact;}

    //Setters
    public void setTitle(String title){
        this.title = title;
    }
    public void setOrderDate(String orderDate){
        this.orderDate = orderDate;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setAction(String action){
        this.action = action;
    }
    public void setResult(String result){
        this.result = result;
    }
    public void setImpact(String impact){
        this.impact = impact;
    }
}
