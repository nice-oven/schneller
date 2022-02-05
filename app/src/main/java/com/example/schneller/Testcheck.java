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
    private String test_id_pei;
    private double cq_25_30;
    private double cq_lt_25;
    private double cq_gt_30;
    private double total_sensitivity;

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

    public double getCq_25_30() {
        return cq_25_30;
    }

    public void setCq_25_30(double cq_25_30) {
        this.cq_25_30 = cq_25_30;
    }

    public double getCq_lt_25() {
        return cq_lt_25;
    }

    public void setCq_lt_25(double cq_lt_25) {
        this.cq_lt_25 = cq_lt_25;
    }

    public double getCq_gt_30() {
        return cq_gt_30;
    }

    public void setCq_gt_30(double cq_gt_30) {
        this.cq_gt_30 = cq_gt_30;
    }

    public double getTotal_sensitivity() {
        return total_sensitivity;
    }

    public void setTotal_sensitivity(double total_sensitivity) {
        this.total_sensitivity = total_sensitivity;
    }

    public String getTest_id_pei() {
        return test_id_pei;
    }

    public void setTest_id_pei(String test_id_pei) {
        this.test_id_pei = test_id_pei;
    }

}
