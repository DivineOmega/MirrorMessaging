package bitmsg;


public class Message 
{
	private int encodingType;
	private String toAddress;
	private String msgid;
	private int timestamp;
	private String fromAddress;
	private String subject;
	private String message;
	private String status;
	private String ackData;

	public Message(int encodingType, String toAddress, String msgid, int timestamp, String fromAddress, String subject, String message, String status, String ackData) 
	{
		this.encodingType = encodingType;
		this.toAddress = toAddress;
		this.msgid = msgid;
		this.timestamp = timestamp;
		this.fromAddress = fromAddress;
		this.subject = subject;
		this.message = message;
		this.status = status;
		this.ackData = ackData;
	}

	public String getFromAddress() 
	{
		return fromAddress;
	}

	public String getSubject() 
	{
		return subject;
	}

	public int getReceivedTime() 
	{
		return timestamp;
	}

	public String getToAddress() 
	{
		return toAddress;
	}

	public String getId() 
	{
		return msgid;
	}

	public String getMessage() 
	{
		return message;
	}

	public String getStatus() 
	{
		return status;
	}
	
	public String getHumanFriendlyStatus()
	{
		if (status.equals("msgqueued")) return "Sending soon...";
		if (status.equals("doingmsgpow")) return "Sending...";
		if (status.equals("msgsent")) return "Sent";
		if (status.equals("ackreceived")) return "Delivered";
		
		return status;
	}

	public String getAckData() 
	{
		return ackData;
	}
	
}