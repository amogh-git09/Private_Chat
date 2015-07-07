package androidapps.amogh.privatechatapp;

class Message{
	private String message;
	private String senderId;

	public Message(String senderId, String message){
		this.senderId = senderId;
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public String getSenderId(){
		return senderId;
	}
}