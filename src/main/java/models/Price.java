package models;

public class Price {
	private String origin, destination, date;
	private int price;
	
	public Price() {
	    
	}
	
	public Price(String origin, String destination, String date, int price) {
	    super();
		this.setOrigin(origin);
		this.setDestination(destination);
		this.setDate(date);
		this.setPrice(price);
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
	
}
