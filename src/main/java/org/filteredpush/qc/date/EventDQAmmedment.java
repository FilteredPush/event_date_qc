/**
 * 
 */
package org.filteredpush.qc.date;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mole
 *
 */
public class EventDQAmmedment {
	
	public enum EventQCAmmendmentState { 
	    NOT_RUN, 
	    INTERNAL_PREREQISITES_NOT_MET, 
	    AMBIGUOUS, 
	    TRANSPOSED,
	    NO_CHANGE;
	}
	
	private EventQCAmmendmentState resultState;
	private Map<String,String> result;
	private StringBuffer resultComment;
	
	public EventDQAmmedment() { 
		setResultState(EventQCAmmendmentState.NOT_RUN);
		result = new HashMap<String, String>();
		resultComment = new StringBuffer();
	}
	
	public void addComment(String comment) { 
		if (resultComment.length()>0) {
			resultComment.append("|");
		}
		resultComment.append(comment);
	}

	public String getComment() { 
		return resultComment.toString();
	}
	
	/**
	 * @return the resultState
	 */
	public EventQCAmmendmentState getResultState() {
		return resultState;
	}

	/**
	 * @param resultState the resultState to set
	 */
	public void setResultState(EventQCAmmendmentState resultState) {
		this.resultState = resultState;
	}

	/**
	 * @return the result
	 */
	public Map<String,String> getResult() {
		return result;
	}

	/**
	 * Add a Darwin Core term and its value to the result.
	 * 
	 * @param key the darwin core term for which a value is being provided
	 * @param value the value provided for the key.
	 */
	public void addResult(String key, String value) {
		this.result.put(value, value);
	}

}
