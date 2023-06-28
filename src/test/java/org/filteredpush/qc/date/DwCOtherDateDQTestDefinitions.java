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
		DQResponse<ComplianceValue> result = DwCOtherDateDQ.validationDateidentifiedStandard(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		dateIdentified = "1883-06-22";
		result = DwCOtherDateDQ.validationDateidentifiedStandard(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());	
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		dateIdentified = "1883-13-34";
		result = DwCOtherDateDQ.validationDateidentifiedStandard(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());	
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
	}

	@Test
	public void testValidationDateidentifiedOutofrangeStringString() {        
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if any of these three conditions 
        // are met (1) dwc:dateIdentified is EMPTY, (2) dwc:dateIdentified 
        // is not a valid ISO 8601-1:2019 date, (3) dwc:eventDate is 
        // not EMPTY and is not a valid ISO 8601-1:2019 date; COMPLIANT 
        // if the value of dwc:dateIdentified is within the parameter 
        // ranges and either (1) dwc:eventDate is EMPTY or (2) if dwc:eventDate 
        // is a valid ISO 8601-1:2019 date and dwc:dateIdentified overlaps 
        // or follows the dwc:eventDate; otherwise NOT_COMPLIANT

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:earliestVaidDate="1753-01-01"; bdq:latestValidDate=current day
		
		String dateIdentified = "";  // dateIdentified empty
		String eventDate = "2022-01-04";
		DQResponse<ComplianceValue> result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		dateIdentified = "1882-34-34"; // dateIdentified not valid date
		eventDate = "2022-01-04";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		dateIdentified = "1882-03-04"; 
		eventDate = "2022-23-32"; // eventDate not empty and not valid
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		// Note change in error case for eventDate handling as of version 2023-03-29
		// Note change in error case for eventDate handling as of version 2023-06-20
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		
		dateIdentified = "2022-01-04";  // dateIdentified follows the event date
		eventDate = "2000-12-10"; 
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		dateIdentified = "2000-06-03";  // dateIdentified overlaps event date
		eventDate = "2000"; 
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());

		dateIdentified = "2000";  // dateIdentified overlaps event date
		eventDate = "2000-06-03"; 
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		dateIdentified = "1800-04-01";  // dateIdentified within default range with no event date
		eventDate = ""; 
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		dateIdentified = "1800-04-01";  // dateIdentified before eventDate
		eventDate = "2000-06-03"; 
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		dateIdentified = "1600-01-01";  // dateIdentified outside default range with no event date
		eventDate = ""; 
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
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
        // INTERNAL_PREREQUISITES_NOT_MET if (1) dwc:dateIdentified 
        // is EMPTY, or (2) dwc:dateIdentified contains an invalid 
        // value according to ISO 8601-1, or (3) bdq:includeEventDate=true 
        // and dwc:eventDate is not a valid ISO 8601-1 date; COMPLIANT 
        // if the value of dwc:dateIdentified is between bdq:earliestValidDate 
        // and bdq:latestValidDate inclusive and either (1) dwc:eventDate 
        // is EMPTY or bdq:includeEventDate=false, or (2) if dwc:eventDate 
        // is a valid ISO 8601-1 date and dwc:dateIdentified overlaps 
        // or is later than the dwc:eventDate; otherwise NOT_COMPLIANT 
        // bdq:sourceAuthority is "ISO 8601-1:2019" [https://www.iso.org/obp/ui/],bdq:earliestValidDate 
        // default="1753-01-01",bdq:latestValidDate default=[current 
        // day],bdq:includeEventDate default=true 
		
        // Parameters. This test is defined as parameterized.
        // Default values: bdq:earliestVaidDate="1753-01-01"; bdq:latestValidDate=current day
		
		String dateIdentified = "1950-05-06";  // dateIdentified within specified range
		String eventDate = "1950-01-12";
		String earlyestValidDate = "1949-01-01";
		String latestValidDate = "1962-01-01";
		String includeEventDate = "true";
		DQResponse<ComplianceValue> result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		dateIdentified = ""; 
		eventDate = "1950-01-12";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		
		dateIdentified = "12-1-15"; 
		eventDate = "1950-01-12";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		
		dateIdentified = "1950-05-06"; 
		eventDate = "12-1-15"; 
		includeEventDate = "true";
		earlyestValidDate = null;
		latestValidDate = null;
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		
		
        // COMPLIANT 
        // if the value of dwc:dateIdentified is between bdq:earliestValidDate 
        // and bdq:latestValidDate inclusive and either (1) dwc:eventDate 
        // is EMPTY or bdq:includeEventDate=false, or (2) if dwc:eventDate 
        // is a valid ISO 8601-1 date and dwc:dateIdentified overlaps 
        // or is later than the dwc:eventDate; otherwise NOT_COMPLIANT 
		
		dateIdentified = "1950-05-06"; 
		eventDate = ""; 
		earlyestValidDate = "1900-01-01";
		latestValidDate = "1999-12-31";
		includeEventDate = "true";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		dateIdentified = "1950-05-06"; 
		eventDate = "1990-01-01"; 
		earlyestValidDate = "1900-01-01";
		latestValidDate = "1999-12-31";
		includeEventDate = "false";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		dateIdentified = "1950-05-06"; 
		eventDate = "1945-01-10"; 
		earlyestValidDate = "1900-01-01";
		latestValidDate = "1999-12-31";
		includeEventDate = "true";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		dateIdentified = "1950-05-06"; 
		eventDate = "1945-01-10/1955-01-01"; 
		earlyestValidDate = "1900-01-01";
		latestValidDate = "1999-12-31";
		includeEventDate = "true";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		dateIdentified = "1950-05-06"; 
		eventDate = "1951-01-10/1955-01-01"; 
		earlyestValidDate = "1900-01-01";
		latestValidDate = "1999-12-31";
		includeEventDate = "true";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		dateIdentified = "1950-05-06"; 
		eventDate = "1955-01-01"; 
		earlyestValidDate = "1900-01-01";
		latestValidDate = "1999-12-31";
		includeEventDate = "true";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		dateIdentified = "1950-05-06"; 
		eventDate = ""; 
		includeEventDate = "false";
		earlyestValidDate = "1960-01-01";  // dateIdentified outside specified range
		latestValidDate = "2000-01-01";
		includeEventDate = "true";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		dateIdentified = "1950-01-01/1955-01-02";  // dateIdentified outside of specified range
		eventDate = ""; 
		includeEventDate = "false";
		earlyestValidDate = "1960-01-01";
		latestValidDate = "1962-01-01";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		dateIdentified = "1950-01-01/1965-01-02";  // dateIdentified overlaps specified range
		eventDate = ""; 
		includeEventDate = "false";
		earlyestValidDate = "1960-01-01";
		latestValidDate = "1962-01-01";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		dateIdentified = "1950-01-01/1961-01-02";  // dateIdentified overlaps event date
		eventDate = "1945-01-01/1955-01-01"; 
		includeEventDate = "true";
		earlyestValidDate = "1900-01-01";
		latestValidDate = "1999-12-31";
		result = DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, earlyestValidDate, latestValidDate, includeEventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
	}

	@Test
	public void testAmendmentDateidentifiedStandardized() {
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
        // EMPTY; AMENDED if the value of dwc:dateIdentified was altered 
        // to unambiguously conform with the ISO 8601-1:2019 date format; 
        // otherwise NOT_AMENDED 
		String dateIdentified = "1950-05-06";  // good value
		DQResponse<AmendmentValue> result = DwCOtherDateDQ.amendmentDateidentifiedStandardized(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		dateIdentified = "1950-5-6";  // non-conformant
		result = DwCOtherDateDQ.amendmentDateidentifiedStandardized(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
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
		
		dateIdentified = "1885-23-09";
		result = DwCOtherDateDQ.amendmentDateidentifiedStandardized(dateIdentified);
		logger.debug(result.getComment());
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		keys = result.getValue().getObject().keySet();
		assertEquals(1, keys.size());
		assertEquals("1885-09-23", result.getValue().getObject().get("dwc:dateIdentified"));
		
		
	}

}
