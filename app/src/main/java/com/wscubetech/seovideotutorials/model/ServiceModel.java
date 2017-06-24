package com.wscubetech.seovideotutorials.model;

import java.io.Serializable;

/**
 * Created by wscubetech on 3/6/16.
 */
public class ServiceModel implements Serializable {
    String serviceId = "", serviceName = "", serviceDescription = "";
    int serviceImage = 0, serviceMainImage = 0, serviceDrawableBg = 0;

    public int getServiceMainImage() {
        return serviceMainImage;
    }

    public void setServiceMainImage(int serviceMainImage) {
        this.serviceMainImage = serviceMainImage;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(int serviceImage) {
        this.serviceImage = serviceImage;
    }

    public int getServiceDrawableBg() {
        return serviceDrawableBg;
    }

    public void setServiceDrawableBg(int serviceDrawableBg) {
        this.serviceDrawableBg = serviceDrawableBg;
    }
}
