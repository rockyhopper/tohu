package org.tohu.util;

import java.io.Serializable;

public class DisplayFact implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String id;
	private String ruleName;
	private String keyPageElementId;
	
	public DisplayFact(String id) {
		this(id, null);
	}
	
	public DisplayFact(String id, String ruleName) {
		this(id, ruleName, null);
	}

	public DisplayFact(String id, String ruleName, String keyPageElementId) {
		super();
		this.id = id;
		this.ruleName = ruleName;
		this.keyPageElementId = keyPageElementId;
	}

	public String getId() {
		return id;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getKeyPageElementId() {
		return keyPageElementId;
	}

	public void setKeyPageElementId(String keyPageElementId) {
		this.keyPageElementId = keyPageElementId;
	}
	

}
