/**
 * 
 */
package org.filteredpush.qc.date;

/**
 * @author mole
 *
 */
public class EventQCResult {
	
	public enum EventQCResultState { 
	    NOT_RUN, 
	    AMBIGUOUS, INTERNAL_PREREQISITES_NOT_MET, 
	    DATE, RANGE;
	}
	
	private EventQCResultState resultState;
	private String result;
	private StringBuffer resultComment;
	
	public EventQCResult() { 
		setResultState(EventQCResultState.NOT_RUN);
		setResult(null);
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
	public EventQCResultState getResultState() {
		return resultState;
	}

	/**
	 * @param resultState the resultState to set
	 */
	public void setResultState(EventQCResultState resultState) {
		this.resultState = resultState;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

}
