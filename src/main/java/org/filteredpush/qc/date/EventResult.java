/**EventResult.java
 * 
 * Copyright 2016 President and Fellows of Harvard College
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.filteredpush.qc.date;

/**
 * Class to carry structured results (result plus metadata) from method calls in org.filteredpush.qc.date.
 * 
 * @author mole
 *
 */
public class EventResult {
	
	public enum EventQCResultState { 
	    NOT_RUN, 
	    AMBIGUOUS, 
	    SUSPECT,
	    DATE, 
	    RANGE,
	    INTERNAL_PREREQISITES_NOT_MET;
	}
	
	private EventQCResultState resultState;
	private String result;
	private StringBuffer resultComment;
	
	public EventResult() { 
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
