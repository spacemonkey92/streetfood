package com.spaceagelabs.streetbaba.UI.viewmodel;

/**
 * Created by Nitin on 27/8/15.
 */
public class HomeCardsModel {

    int inconId;
    String heading;
    String subheading;

    public HomeCardsModel(String heading,String subheading){
        this.heading=heading;
        this.subheading=subheading;
    }

    public int getInconId() {
        return inconId;
    }

    public void setInconId(int inconId) {
        this.inconId = inconId;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getSubheading() {
        return subheading;
    }

    public void setSubheading(String subheading) {
        this.subheading = subheading;
    }



}
