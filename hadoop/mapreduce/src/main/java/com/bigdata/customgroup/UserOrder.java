package com.bigdata.customgroup;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class UserOrder implements WritableComparable<UserOrder> {
    private String userID;
    private String yearMonth;
    private String title;
    private double unitPrice;
    private int purchaseNum;
    private String productID;

    public UserOrder(String userID, String yearMonth, String title, double unitPrice, int purchaseNum, String productID) {
        this.userID = userID;
        this.yearMonth = yearMonth;
        this.title = title;
        this.unitPrice = unitPrice;
        this.purchaseNum = purchaseNum;
        this.productID = productID;
    }

    public UserOrder() {}

    @Override
    public int compareTo(UserOrder o) {
//        int ret = getUserID().compareTo(o.getUserID());
//        if (ret != 0) {
//            return ret;
//        }
//
//        ret = getYearMonth().compareTo(o.getYearMonth());
//        if (ret != 0) {
//            return ret;
//        }
//
//        Double totalL = getPurchaseNum() * getUnitPrice();
//        Double totalR = o.getPurchaseNum() * o.getUnitPrice();
//
//        return -totalL.compareTo(totalR);
        return 0;
    }

    @Override
    public String toString() {
        return String.format("%s\t%s\t%s\t%.2f\t%d\t%s",
                userID, yearMonth, title, unitPrice, purchaseNum, productID);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(userID);
        dataOutput.writeUTF(yearMonth);
        dataOutput.writeUTF(title);
        dataOutput.writeDouble(unitPrice);
        dataOutput.writeInt(purchaseNum);
        dataOutput.writeUTF(productID);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        setUserID(dataInput.readUTF());
        setYearMonth(dataInput.readUTF());
        setTitle(dataInput.readUTF());
        setUnitPrice(dataInput.readDouble());
        setPurchaseNum(dataInput.readInt());
        setProductID(dataInput.readUTF());
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getYearMonth() {
        return yearMonth;
    }

    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getPurchaseNum() {
        return purchaseNum;
    }

    public void setPurchaseNum(int purchaseNum) {
        this.purchaseNum = purchaseNum;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }
}
