package bitmsg;

public class Address 
{
	private String label;
	private String address;
	private int stream;
	private boolean enabled;
	
	public Address(String label, String address, int stream, boolean enabled)
	{
		this.label = label;
		this.address = address;
		this.stream = stream;
		this.enabled = enabled;
	}
	
	public String getLabel() 
	{
		return label;
	}

	public String getAddress() 
	{
		return address;
	}
}
