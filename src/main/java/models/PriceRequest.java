package models;

public class PriceRequest {

    private String fromDate;
    private String toDate;
    private String origin;
    private String destination;
    private String currency;
    private int value;

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromdate) {
        this.fromDate = fromdate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String todate) {
        this.toDate = todate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

}
