package com.agesinitiatives.servicebook.entities;

import java.util.Date;
import java.util.List;

/**
 * Created by John on 1/5/2018.
 */

public class AgesDate {
    public Date date;
    public List<AgesService> services;

    public AgesDate(Date date, List<AgesService> services) {
        this.date = date;
        this.services = services;
    }

    public String toString() {
        return date.toString();
    }
}
