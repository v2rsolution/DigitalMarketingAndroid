package com.wscubetech.seovideotutorials.model;

import java.util.ArrayList;

/**
 * Created by wscube on 20/6/16.
 */
public class TrainingPointsModel {

    String Heading;
    ArrayList<String> arrayOfPoints=new ArrayList<>();

    public String getHeading() {
        return Heading;
    }

    public void setHeading(String heading) {
        Heading = heading;
    }

    public ArrayList<String> getArrayOfPoints() {
        return arrayOfPoints;
    }

    public void setArrayOfPoints(ArrayList<String> arrayOfPoints) {
        this.arrayOfPoints = arrayOfPoints;
    }
}
