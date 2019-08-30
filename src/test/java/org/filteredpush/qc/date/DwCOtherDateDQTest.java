/** DwCOtherDateDQTest.java
 * 
 * Copyright 2019 President and Fellows of Harvard College
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

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.model.ResultState;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DwCOtherDateDQTest {
	private static final Log logger = LogFactory.getLog(DwCOtherDateDQTest.class);


	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCOtherDateDQ#validationDateidentifiedNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationDateidentifiedNotstandard() {
		
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:dateIdentified 
        // is either not present or is EMPTY; COMPLIANT if the value 
        // of the field dwc:dateIdentified is a valid ISO 8601-1:2019 
        //date; otherwise NOT_COMPLIANT 		
		
		String dateIdentified = null;
		
		DQResponse<ComplianceValue> result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		dateIdentified = "";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		dateIdentified = " ";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		dateIdentified = "\n";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		dateIdentified = "13ab/02/r3:06";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		dateIdentified = "string";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		dateIdentified = "1700";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "1100";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		dateIdentified = "1902-01";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "1100-01";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "1902-01-03";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "1100-01-22";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "2012-12-31";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "2004-02-29";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "2005-02-29";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "2004-366";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "2005-366";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "1832/1900";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
	
		dateIdentified = "1832-12-15/1833-06-12";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
	
		dateIdentified = "2012/12/31";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "2012-12-32";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "2012-02-30";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "2012-Feb-02";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "2012-1-3";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		System.out.println(result.getComment());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "1937-08-23/1037-09-09";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		System.out.println(result.getComment());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCOtherDateDQ#validationDateidentifiedOutofrange(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationDateidentifiedOutofrange() {
    	// INTERNAL_PREREQUISITES_NOT_MET if the field dwc:dateIdentified is 
    	// EMPTY or is not a valid ISO 8601-1:2019 date, or if the field 
    	// dwc:eventDate is not EMPTY and is not a valid ISO 8601-1:2019 date; 
    	// COMPLIANT if the value of the field dwc:dateIdentified is not prior to 
    	// dwc:eventDate, and is within the Parameter range; 
    	// otherwise NOT_COMPLIANT		
		
		fail("Not yet implemented");
	}
}