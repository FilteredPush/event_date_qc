/**
 * 
 */
package org.filteredpush.qc.date;

/**
 * @author mole
 *
 */
public class EventDQValidation {
	
	public enum EventDQValidationState { 
	    NOT_RUN, 
	    AMBIGUOUS,
	    INTERNAL_PREREQISITES_NOT_MET, 
	    COMPLETED;
	}
	
	public enum EventDQValidationResult {
	   COMPLIANT, 
	   NOT_COMPLIANT;
	}
	
	private EventDQValidationState resultState;
	private EventDQValidationResult result;
	private StringBuffer resultComment;
	
	public EventDQValidation() { 
		setResultState(EventDQValidationState.NOT_RUN);
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
	public EventDQValidationState getResultState() {
		return resultState;
	}

	/**
	 * @param resultState the resultState to set
	 */
	public void setResultState(EventDQValidationState resultState) {
		this.resultState = resultState;
	}


	/**
	 * @return the result
	 */
	public EventDQValidationResult getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(EventDQValidationResult result) {
		this.result = result;
	}

}
