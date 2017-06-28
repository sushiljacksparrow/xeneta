package models;

public class Port {

	private String code;
    private String name;
    private String slug;
	
    public Port() {
        
    }
    
	public Port(String code, String name, String slug) {
	    super();
		this.setCode(code);
		this.setName(name);
		this.setSlug(slug);
	}

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
