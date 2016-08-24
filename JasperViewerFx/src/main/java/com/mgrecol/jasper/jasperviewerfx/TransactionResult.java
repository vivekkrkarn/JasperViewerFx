package com.mgrecol.jasper.jasperviewerfx;

import java.util.Date;

/**
 * @author Michael Grecol
 * @project JasperViewerFx
 * @filename TransactionResult.java
 * @date Mar 23, 2015
 */
public class TransactionResult {

    private long resultNumber;
    private String resultDescription;
    private String result;
    private Date transactionTime;
    private int objectId;

    public long getResultNumber() {
        return resultNumber;
    }

    public void setResultNumber(long resultNumber) {
        this.resultNumber = resultNumber;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    @Override
    public String toString() {
        return "TransactionResult [resultNumber=" + resultNumber
                + ", resultDescription=" + resultDescription + ", result=" + result
                + ", transactionTime=" + transactionTime + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (resultNumber ^ (resultNumber >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (null == obj)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransactionResult other = (TransactionResult) obj;
        if (resultNumber != other.resultNumber) return false;
        return true;
    }
}
