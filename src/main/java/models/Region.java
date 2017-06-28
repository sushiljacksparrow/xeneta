package models;

public class Region {
	
	private String slug;
    private String name;
    private String parent;
	
    public Region() {
        
    }
    
	public Region(String slug, String name, String parent) {
	    super();
		this.slug = slug;
		this.name = name;
		this.parent = parent;
	}

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public String getParent() {
        return parent;
    }
}
