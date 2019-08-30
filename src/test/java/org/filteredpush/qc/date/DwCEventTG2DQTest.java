/** DwCEventTG2DQTest.java
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
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.model.ResultState;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DwCEventTG2DQTest {
	private static final Log logger = LogFactory.getLog(DwCEventTG2DQTest.class);
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#measureEventdatePrecisioninseconds(java.lang.String)}.
	 */
	@Test
	public void testMeasureEventdatePrecisioninseconds() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationDayNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationDayNotstandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationDayOutofrange(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationDayOutofrange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEnddayofyearOutofrange(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEnddayofyearOutofrange() {
		
		String year = null;
		String endDayOfYear = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
	
		year = "2000";
		endDayOfYear = "1";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "2000";
		endDayOfYear = "365";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "2000";
		endDayOfYear = "3000";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		year = "2000";
		endDayOfYear = "";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "";
		endDayOfYear = "10";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "";
		endDayOfYear = "365";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "";
		endDayOfYear = "366";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "";
		endDayOfYear = "380";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEventEmpty(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEventEmpty() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEventInconsistent(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEventInconsistent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEventdateEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateEmpty() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEventdateNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateNotstandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEventdateOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateOutofrange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationMonthNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationMonthNotstandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationStartdayofyearOutofrange(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationStartdayofyearOutofrange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationYearEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationYearEmpty() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:year is 
        // not present; COMPLIANT if the value of the field dwc:year 
        //is not EMPTY; otherwise NOT_COMPLIANT 
		
		String year = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year="";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year=" ";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year="\n";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year="NULL";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		year="1880";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				

	    year = "-1";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				
				
		year = "text";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationYearOutofRange(java.lang.String)}.
	 */
	@Test
	public void testValidationYearOutofRange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentDateidentifiedStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentDateidentifiedStandardized() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentDayStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentDayStandardized() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is not present 
        // or is EMPTY; AMENDED if the value of dwc:day was unambiguously 
        // interpreted to be an integer between 1 and 31 inclusive; 
        // otherwise NOT_CHANGED 
		String proposed;
		
		String day = "";  
		DQResponse<AmendmentValue> result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = null;  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = " ";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = "\n";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = "1";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "30";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "0";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "33";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "?3";  // could represent uncertainty or a placeholder for an illegible character  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		logger.debug(result.getComment());
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "3?";  // could represent uncertainty or a placeholder for an illegible character
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		logger.debug(result.getComment());
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "??";  // common no-values marker
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "**";  // common no-values marker
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "1st";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("1", proposed);
		
		day = "3rd";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("3", proposed);
		
		day = "1 ";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("1", proposed);
		
		day = " 1 ";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("1", proposed);
		
		day = " 1\n";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("1", proposed);
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentEventFromEventdate(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventFromEventdate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentEventdateFromVerbatim(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromVerbatim() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentEventdateFromYearmonthday(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromYearmonthday() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentEventdateFromYearstartdayofyearenddayofyear(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromYearstartdayofyearenddayofyear() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentEventdateStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateStandardized() {
		
        // Specification
        // INTERNAL_PREREQUESITES_NOT_MET if the field dwc:eventDate 
        // is EMPTY; AMENDED if the field dwc:eventDate was changed 
        // to unambiguously conform with an ISO 8601-1:2019 date; otherwise 
        // NOT_CHANGED 
		String proposed;
		
		String eventDate = "";  
		DQResponse<AmendmentValue> result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = null;  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "\n";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "1937-08-23/1037-09-09";  // range with end before start
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		eventDate = "1937-08-23";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		eventDate = "1937-08-23/1938-01-04";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		eventDate = "string";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		eventDate = "1937-8-23";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
		assertEquals("1937-08-23", proposed);
		
		eventDate = "1937-Feb-23";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
		assertEquals("1937-02-23", proposed);
		
		eventDate = "Feb 23, 1937";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
		assertEquals("1937-02-23", proposed);

		eventDate = "2-23-1937";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
		assertEquals("1937-02-23", proposed);
		
		eventDate = "April 1937";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
		assertEquals("1937-04", proposed);

    	eventDate = "11-VII-1885";
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
    	assertEquals("1885-07-11", proposed);
		
		eventDate = "2-3-1937"; // ambiguous m-d or d-m 
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		eventDate = "April 137"; // suspect 
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentMonthStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentMonthStandardized() {
		fail("Not yet implemented");
	}
}
