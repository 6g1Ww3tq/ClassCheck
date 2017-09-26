package com.classcheck.gen;

import java.util.Map;

public class MessagesReplace extends Replace{

	private Map<String, String> messagesMap;

	public MessagesReplace(String base, Map<String, String> messagesMap) {
		super(base);
		this.messagesMap = messagesMap;
	}

	public void changeMessages(){
		String aftMessage = null;

		for(String befMessage : messagesMap.keySet()){
			aftMessage = messagesMap.get(befMessage);
			super.setBefore(befMessage);
			super.setAfter(aftMessage);
			super.replace();
		}
	}
}
