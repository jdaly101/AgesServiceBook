package com.agesinitiatives.servicebook.entities;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by John on 1/5/2018.
 */

public class AgesService {
    public Date date;
    public String serviceType;
    public String serviceUrl;

    public AgesService(Date date, String serviceType, String serviceUrl) {
        this.date = date;
        this.serviceType = serviceType;
        this.serviceUrl = serviceUrl;
    }

    public String getTitle() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd - ");
        return sdf.format(date) + serviceType;
    }

    public String toString() {
        return this.serviceType;
    }
}
