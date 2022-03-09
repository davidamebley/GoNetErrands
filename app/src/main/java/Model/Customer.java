package Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Akwasi on ${9/27/2018}.
 */

public class Customer {
    private int customerId;
    private String customerCode, customerFistName, customerLastName, customerGender,
            customerPhoto, customerDOB,
            customerEmail, customerPhone, customerAddress, customerGhPostAddress,
            customerDeliveryLocation, customerAgentCode;

    public Customer(int customerId, String customerCode, String customerFistName,
                    String customerLastName, String customerGender, String customerPhoto,
                    String customerDOB, String customerEmail, String customerPhone,
                    String customerAddress, String customerGhPostAddress,
                    String customerDeliveryLocation, String customerAgentCode) {
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerFistName = customerFistName;
        this.customerLastName = customerLastName;
        this.customerGender = customerGender;
        this.customerPhoto = customerPhoto;
        this.customerDOB = customerDOB;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.customerGhPostAddress = customerGhPostAddress;
        this.customerDeliveryLocation = customerDeliveryLocation;
        this.customerAgentCode = customerAgentCode;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public String getCustomerFistName() {
        return customerFistName;
    }

    public void setCustomerFistName(String customerFistName) {
        this.customerFistName = customerFistName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getCustomerGender() {
        return customerGender;
    }

    public void setCustomerGender(String customerGender) {
        this.customerGender = customerGender;
    }

    public String getCustomerPhoto() {
        return customerPhoto;
    }

    public void setCustomerPhoto(String customerPhoto) {
        this.customerPhoto = customerPhoto;
    }

    public String getCustomerDOB() {
        return customerDOB;
    }

    public void setCustomerDOB(String customerDOB) {
        this.customerDOB = customerDOB;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerGhPostAddress() {
        return customerGhPostAddress;
    }

    public void setCustomerGhPostAddress(String customerGhPostAddress) {
        this.customerGhPostAddress = customerGhPostAddress;
    }

    public String getCustomerDeliveryLocation() {
        return customerDeliveryLocation;
    }

    public void setCustomerDeliveryLocation(String customerDeliveryLocation) {
        this.customerDeliveryLocation = customerDeliveryLocation;
    }

    public String getCustomerAgentCode() {
        return customerAgentCode;
    }

}

