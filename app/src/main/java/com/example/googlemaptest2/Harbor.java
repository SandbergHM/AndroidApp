package com.example.googlemaptest2;

public class Harbor {
    private String name;
    private double lat;
    private double lang;

    public Harbor(String name, Double lat, Double lang){
        name = this.name;
        lat = this.lat;
        lang = this.lang;
    }

    public String getName(){

        return name;
    }

    public double getLat(){
        return lat;
    }

    public double getLang(){

        return lang;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setLat(double lat){
        this.lat = lat;
    }

    public void setLang(double lang){
        this.lang = lang;
    }

}
