package com.wscubetech.seovideotutorials.model;

/**
 * Created by wscube on 23/6/16.
 */
public class PlacedStudentModel {

    String Name="", Image="";

    public PlacedStudentModel(String name, String image) {
        Name = name;
        Image = image;
    }


    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
