/**EventDQValidation.java
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

import org.datakurator.ffdq.api.DQValidation;
import org.datakurator.ffdq.api.DQValidationResult;
import org.datakurator.ffdq.api.DQValidationState;

/**
 * Specific implementation of return values for an F4UF validation result for org.filteredpush.qc.date
 * intended to implement an validation interface from an F4UF api package at some future point.
 * 
 * @author mole
 *
 */
public class EventDQValidation implements DQValidation {
	
	private DQValidationState resultState;
	private DQValidationResult result;
	private StringBuffer resultComment;
	
	public EventDQValidation() { 
		setResultState(DQValidationState.NOT_RUN);
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
	public DQValidationState getResultState() {
		return resultState;
	}

	/**
	 * @param resultState the resultState to set
	 */
	public void setResultState(DQValidationState resultState) {
		this.resultState = resultState;
	}


	/**
	 * @return the result
	 */
	public DQValidationResult getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(DQValidationResult result) {
		this.result = result;
	}

}
