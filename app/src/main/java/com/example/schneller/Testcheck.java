package com.example.schneller;

import java.io.Serializable;

public class Testcheck implements Serializable {
    private String test_id;
    private String name;
    private String manufacturer;
    private Boolean peiTested;
    private String link;
    private double specificity;
    private double sensitivity;

    public String getTest_id() {
        return test_id;
    }

    public void setTest_id(String test_id) {
        this.test_id = test_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Boolean getPeiTested() {
        return peiTested;
    }

    public void setPeiTested(Boolean peiTested) {
        this.peiTested = peiTested;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public double getSpecificity() {
        return specificity;
    }

    public void setSpecificity(double specificity) {
        this.specificity = specificity;
    }

    public double getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(double sensitivity) {
        this.sensitivity = sensitivity;
    }
}
