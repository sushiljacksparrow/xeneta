package models;


public class PriceResponse {
    String day;
    int average_price;
    
    public PriceResponse(String day, int average_price) {
        this.day = day;
        this.average_price = average_price;
    }
    
    public String getDay() {
        return day;
    }
    
    public int getAveragePrice() {
        return this.average_price;
    }
}
