/**TestDwCOtherDateDQDefinitions.java
 * 
 * Copyright 2022 President and Fellows of Harvard College
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

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.model.ResultState;
import org.junit.Test;

/** 
 * Minimal tests of conformance of DwCOtherDateDQ to the test specifications.
 * This class tests the methods implementing TG2 CORE tests, with one test for each
 * clause in the specification of the test.  These tests are non-exhaustive, and only
 * demonstrate minimal compliance with the specifications.
 * 
 * @author mole
 *
 */
public class DwCOtherDateDQTestDefinitions {
	
	private static final Log logger = LogFactory.getLog(DwCOtherDateDQTestDefinitions.class);

	@Test
	public void testValidationDateidentifiedNotstandard() {
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
        // EMPTY; COMPLIANT if the value of dwc:dateIdentified is a 
        // valid ISO 8601-1:2019 date; otherwise NOT_COMPLIANT 
		
		String dateIdentified = "";
		DQResponse<ComplianceValue> result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		dateIdentified = "1883-06-22";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());	
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		dateIdentified = "1883-13-34";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());	
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
	}

	@Test
	public void testValidationDateidentifiedOutofrangeStringString() {        
		// Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
        // EMPTY or is not a valid ISO 8601-1:2019 date, or if dwc:eventDate 
        // is not EMPTY and is not a valid ISO 8601-1:2019 date; COMPLIANT 
        // if the value of dwc:dateIdentified overlaps or follows the 
        // dwc:eventDate, and is within the Parameter range; otherwise 
        // NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:earliestVaidDate="1753-01-01"; bdq:latestValidDate=current day
		
		String dateIdentified = "";  // dateIdentified empty
		String eventDate = "2022-01-04";
		DQResponse<ComplianceValue> result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		dateIdentified = "1882-34-34"; // dateIdentified not valid date
		eventDate = "2022-01-04";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		dateIdentified = "1882-03-04"; 
		eventDate = "2022-23-32"; // eventDate not empty and not valid
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		dateIdentified = "2022-01-04";  // dateIdentified follows the event date
		eventDate = "2000-12-10"; 
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		dateIdentified = "2000-06-03";  // dateIdentified overlaps event date
		eventDate = "2000"; 
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());

		dateIdentified = "2000";  // dateIdentified overlaps event date
		eventDate = "2000-06-03"; 
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		dateIdentified = "1800-04-01";  // dateIdentified within default range with no event date
		eventDate = ""; 
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		dateIdentified = "1800-04-01";  // dateIdentified before eventDate
		eventDate = "2000-06-03"; 
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		dateIdentified = "1600-01-01";  // dateIdentified outside default range with no event date
		eventDate = ""; 
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
	}

	@Test
	/** Minimal test of dateIdentified within or outside of earlyest/latest valid range.
	 * 
	 */
	public void testValidationDateidentifiedOutofrangeStringStringStringString() {
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if any of these three conditions are met 
        // (1) dwc:dateIdentified is EMPTY, (2) dwc:dateIdentified is not a valid 
        // ISO 8601-1:2019 date, (3) dwc:eventDate is not EMPTY and is not a valid
        // ISO 8601-1:2019 date; COMPLIANT if the value of dwc:dateIdentified is 
        // within the parameter ranges and either (1) dwcEventDate is EMPTY or 
        // (2) if dwc:eventDate is a valid ISO 8601-1:2019 date and dwc:dateIdentified 
        // overlaps or follows the dwc:eventDate; otherwise NOT_COMPLIANT
		
        // Parameters. This test is defined as parameterized.
        // Default values: bdq:earliestVaidDate="1753-01-01"; bdq:latestValidDate=current day
		
		String dateIdentified = "1950-05-06";  // dateIdentified within specified range
		String eventDate = "1950-01-12";
		String earlyestValidDate = "1949-01-01";
		String latestValidDate = "1962-01-01";
		DQResponse<ComplianceValue> result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		earlyestValidDate = "1960-01-01";  // dateIdentified outside specified range
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		

		dateIdentified = "1950-01-01/1961-01-02";  // dateIdentified extends beyond specified range
		earlyestValidDate = "1960-01-01";
		latestValidDate = "1962-01-01";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
	}

	@Test
	public void testAmendmentDateidentifiedStandardized() {
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
        // EMPTY; AMENDED if the value of dwc:dateIdentified was altered 
        // to unambiguously conform with the ISO 8601-1:2019 date format; 
        // otherwise NOT_CHANGED 
		String dateIdentified = "1950-05-06";  // good value
		DQResponse<AmendmentValue> result = DwCOtherDateDQ.amendmentDateidentifiedStandardized(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.NO_CHANGE.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		dateIdentified = "1950-5-6";  // non-conformant
		result = DwCOtherDateDQ.amendmentDateidentifiedStandardized(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.CHANGED.getLabel(), result.getResultState().getLabel());
		Set<String> keys = result.getValue().getObject().keySet();
		Iterator<String> i = keys.iterator();
		while (i.hasNext()) { 
			logger.debug(i.next());
		}
		assertTrue(result.getValue().getObject().containsKey("dwc:dateIdentified"));
		assertEquals("1950-05-06",result.getValue().getObject().get("dwc:dateIdentified"));
		
		dateIdentified = "";  // empty
		result = DwCOtherDateDQ.amendmentDateidentifiedStandardized(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
	}

}
