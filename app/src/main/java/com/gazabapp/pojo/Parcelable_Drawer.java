package com.gazabapp.pojo;

public class Parcelable_Drawer
{
    private String Name,ID,URL;

    public Parcelable_Drawer(String Name,String ID,String URL)
    {
        this.Name = Name;
        this.ID = ID;
        this.URL = URL;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
