package bitmsg;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import org.apache.commons.codec.binary.Base64;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.json.JSONArray;
import org.json.JSONObject;

public class BitMsgComms 
{
	private XmlRpcClient getBitMsg() throws MalformedURLException
	{
		XmlRpcClientConfigImpl cc = new XmlRpcClientConfigImpl();
		cc.setServerURL(new URL("http://localhost:8442/"));
		cc.setBasicUserName("mirror_messaging");
		cc.setBasicPassword("123");
		
		XmlRpcClient bitmsg = new XmlRpcClient();
		bitmsg.setConfig(cc);
		return bitmsg;
	}

	public int add (int firstInteger, int secondInteger) throws MalformedURLException, XmlRpcException
	{
		Vector<Integer> params = new Vector<Integer>();
		params.addElement (firstInteger);
		params.addElement (secondInteger);
		
		int result = (Integer) getBitMsg().execute ("add", params);
		
		return result;
	}

	public String helloWorld(String firstString, String secondString) throws MalformedURLException, XmlRpcException 
	{
		Vector<String> params = new Vector<String>();
		params.addElement(firstString);
		params.addElement(secondString);
		
		String result = (String) getBitMsg().execute("helloWorld", params);
		
		return result;
	}
	
	public ArrayList<Address> listAddresses() throws MalformedURLException, XmlRpcException, UnsupportedEncodingException 
	{
		Vector<String> params = new Vector<String>();
		
		String result = (String) getBitMsg().execute("listAddresses", params);
				
		JSONObject jsonResult = new JSONObject(result);
		JSONArray jsonAddresses = jsonResult.getJSONArray("addresses");
		
		ArrayList<Address> addresses = new ArrayList<Address>();
		
		int index = 0;
		while (index<jsonAddresses.length())
		{
			JSONObject jsonAddress = jsonAddresses.getJSONObject(index);
			
			String label = jsonAddress.getString("label");
			String addressString = jsonAddress.getString("address");
			int stream = jsonAddress.getInt("stream");
			boolean enabled = jsonAddress.getBoolean("enabled");
			
			Address address = new Address(label, addressString, stream, enabled);
			
			addresses.add(address);
			
			index++;
		}
		
		return addresses;
	}

	public ArrayList<Message> getAllInboxMessages() throws MalformedURLException, XmlRpcException, UnsupportedEncodingException 
	{
		Vector<String> params = new Vector<String>();
		
		String result = (String) getBitMsg().execute("getAllInboxMessages", params);
				
		JSONObject jsonResult = new JSONObject(result);
		JSONArray jsonMsgs = jsonResult.getJSONArray("inboxMessages");
		
		ArrayList<Message> msgs = new ArrayList<Message>();
		
		int index = 0;
		while (index<jsonMsgs.length())
		{
			JSONObject jsonMsg = jsonMsgs.getJSONObject(index);
			
			int encodingType = jsonMsg.getInt("encodingType");
			String toAddress = jsonMsg.getString("toAddress");
			String msgid = jsonMsg.getString("msgid");
			int receivedTime = jsonMsg.getInt("receivedTime");
			String fromAddress = jsonMsg.getString("fromAddress");
			
			String subject = jsonMsg.getString("subject").trim();
			subject = new String(Base64.decodeBase64(subject), "UTF-8");
			
			String message = jsonMsg.getString("message").trim();
			message = new String(Base64.decodeBase64(message), "UTF-8");
			
			Message msg = new Message(encodingType, toAddress, msgid, receivedTime, fromAddress, subject, message, null, null);
			
			msgs.add(msg);
			
			index++;
		}
		
		Collections.reverse(msgs);
		
		return msgs;
	}
	
	public ArrayList<Message> getAllSentMessages() throws MalformedURLException, XmlRpcException, UnsupportedEncodingException 
	{
		Vector<String> params = new Vector<String>();
		
		String result = (String) getBitMsg().execute("getAllSentMessages", params);
		
		JSONObject jsonResult = new JSONObject(result);
		JSONArray jsonMsgs = jsonResult.getJSONArray("sentMessages");
		
		ArrayList<Message> msgs = new ArrayList<Message>();
		
		int index = 0;
		while (index<jsonMsgs.length())
		{
			JSONObject jsonMsg = jsonMsgs.getJSONObject(index);
						
			int encodingType = jsonMsg.getInt("encodingType");
			String toAddress = jsonMsg.getString("toAddress");
			String msgid = jsonMsg.getString("msgid");
			int lastActionTime = jsonMsg.getInt("lastActionTime");
			String fromAddress = jsonMsg.getString("fromAddress");
			
			String subject = jsonMsg.getString("subject").trim();
			subject = new String(Base64.decodeBase64(subject), "UTF-8");
			
			String message = jsonMsg.getString("message").trim();
			message = new String(Base64.decodeBase64(message), "UTF-8");
			
			String status = jsonMsg.getString("status");
			
			String ackData = jsonMsg.getString("ackData");
						
			Message msg = new Message(encodingType, toAddress, msgid, lastActionTime, fromAddress, subject, message, status, ackData);
			
			msgs.add(msg);
			
			index++;
		}
		
		Collections.reverse(msgs);
		
		return msgs;
	}

	public Message getInboxMessageById(String msg_id) throws MalformedURLException, XmlRpcException, UnsupportedEncodingException 
	{
		Vector<String> params = new Vector<String>();
		params.addElement(msg_id);
		
		String result = (String) getBitMsg().execute("getInboxMessageById", params);
				
		JSONObject jsonResult = new JSONObject(result);
		JSONArray jsonMsgs = jsonResult.getJSONArray("inboxMessage");
		
		JSONObject jsonMsg = jsonMsgs.getJSONObject(0);
		
		int encodingType = jsonMsg.getInt("encodingType");
		String toAddress = jsonMsg.getString("toAddress");
		String msgid = jsonMsg.getString("msgid");
		int receivedTime = jsonMsg.getInt("receivedTime");
		String fromAddress = jsonMsg.getString("fromAddress");
		
		String subject = jsonMsg.getString("subject").trim();
		subject = new String(Base64.decodeBase64(subject), "UTF-8");
		
		String message = jsonMsg.getString("message").trim();
		message = new String(Base64.decodeBase64(message), "UTF-8");
		
		Message msg = new Message(encodingType, toAddress, msgid, receivedTime, fromAddress, subject, message, null, null);
		
		return msg;
	}
	
	public Message getSentMessageById(String msg_id) throws MalformedURLException, XmlRpcException, UnsupportedEncodingException 
	{
		Vector<String> params = new Vector<String>();
		params.addElement(msg_id);
		
		String result = (String) getBitMsg().execute("getSentMessageById", params);
		
		JSONObject jsonResult = new JSONObject(result);
		JSONArray jsonMsgs = jsonResult.getJSONArray("sentMessage");
		
		JSONObject jsonMsg = jsonMsgs.getJSONObject(0);
		
		int encodingType = jsonMsg.getInt("encodingType");
		String toAddress = jsonMsg.getString("toAddress");
		String msgid = jsonMsg.getString("msgid");
		int receivedTime = jsonMsg.getInt("lastActionTime");
		String fromAddress = jsonMsg.getString("fromAddress");
		
		String subject = jsonMsg.getString("subject").trim();
		subject = new String(Base64.decodeBase64(subject), "UTF-8");
		
		String message = jsonMsg.getString("message").trim();
		message = new String(Base64.decodeBase64(message), "UTF-8");
		
		String status = jsonMsg.getString("status");
		
		String ackData = jsonMsg.getString("ackData");
		
		Message msg = new Message(encodingType, toAddress, msgid, receivedTime, fromAddress, subject, message, status, ackData);
		
		return msg;
	}

	public Message getSentMessageByAckData(String ackData) throws MalformedURLException, XmlRpcException, UnsupportedEncodingException 
	{
		Vector<String> params = new Vector<String>();
		params.addElement(ackData);
		
		String result = (String) getBitMsg().execute("getSentMessageByAckData", params);
		
		JSONObject jsonResult = new JSONObject(result);
		JSONArray jsonMsgs = jsonResult.getJSONArray("sentMessage");
		
		JSONObject jsonMsg = jsonMsgs.getJSONObject(0);
		
		int encodingType = jsonMsg.getInt("encodingType");
		String toAddress = jsonMsg.getString("toAddress");
		String msgid = jsonMsg.getString("msgid");
		int receivedTime = jsonMsg.getInt("lastActionTime");
		String fromAddress = jsonMsg.getString("fromAddress");
		
		String subject = jsonMsg.getString("subject").trim();
		subject = new String(Base64.decodeBase64(subject), "UTF-8");
		
		String message = jsonMsg.getString("message").trim();
		message = new String(Base64.decodeBase64(message), "UTF-8");
		
		String status = jsonMsg.getString("status");
		
		Message msg = new Message(encodingType, toAddress, msgid, receivedTime, fromAddress, subject, message, status, ackData);
		
		return msg;
	}

}
