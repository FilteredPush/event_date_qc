/**
 * 
 */
package org.filteredpush.qc.date;

import static org.junit.Assert.*;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.NumericalValue;
import org.datakurator.ffdq.model.ResultState;
import org.junit.Test;

/**
 * Minimal tests of conformance of DwCEventDQ to the test specifications.
 * This class tests the methods implementing TG2 CORE tests, with one test for each
 * clause in the specification of the test.  These tests are non-exhaustive, and only
 * demonstrate minimal compliance with the specifications (they demonstrate that the
 * implementation is capable of producing each result described in the specification 
 * in the expected structure).
 * 
 * @author mole
 *
 */
public class DwCEventDQTestDefinitions {
	
	private static final Log logger = LogFactory.getLog(DwCEventDQTestDefinitions.class);

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEventdateEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateEmpty() {
		// COMPLIANT if dwc:eventDate is not EMPTY; otherwise NOT_COMPLIANT 

		String eventDate = "1800";
		DQResponse<ComplianceValue> result = DwCEventDQ.validationEventdateEmpty(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "foo";
		result = DwCEventDQ.validationEventdateEmpty(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "";
		result = DwCEventDQ.validationEventdateEmpty(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());


	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEventdateOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateOutofrange() {
		// INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
		// or if the value of dwc:eventDate is not a valid ISO 8601-1:2019 
		// date; COMPLIANT if the range of dwc:eventDate is entirely 
		// within the parameter range, otherwise NOT_COMPLIANT 
		
		// Parameters. This test is defined as parameterized.
		// Default values: bdq:earliestValidDate="1600"; bdq:latestValidDate=current year
		
		
		String eventDate = "";
		DQResponse<ComplianceValue> result = DwCEventDQDefaults.validationEventdateOutofrange(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());

		eventDate = "3/4/5";
		result = DwCEventDQDefaults.validationEventdateOutofrange(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		eventDate = "1880";
		result = DwCEventDQDefaults.validationEventdateOutofrange(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1600/2000";
		result = DwCEventDQDefaults.validationEventdateOutofrange(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1599";
		result = DwCEventDQDefaults.validationEventdateOutofrange(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1599/1900";
		result = DwCEventDQDefaults.validationEventdateOutofrange(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEventdateOutofrange(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateOutofrangeParameterized() {
		// INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
		// or if the value of dwc:eventDate is not a valid ISO 8601-1:2019 
		// date; COMPLIANT if the range of dwc:eventDate is entirely 
		// within the parameter range, otherwise NOT_COMPLIANT 
		
		// Parameters. This test is defined as parameterized.
		// Default values: bdq:earliestValidDate="1600"; bdq:latestValidDate=current year
		
		String eventDate = "";
		DQResponse<ComplianceValue> result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1800-01-01", "1899-12-31");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());

		eventDate = "3/4/5";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1800-01-01", "1899-12-31");
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		eventDate = "1880";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1800-01-01", "1899-12-31");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
	
		eventDate = "1880-03-05/1889-06-03";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1800-01-01", "1899-12-31");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1600/2000";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1800-01-01", "1899-12-31");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1599";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1800-01-01", "1899-12-31");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1799/1832";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1800-01-01", "1899-12-31");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

	}
	
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationYearEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationYearEmpty() {
		// COMPLIANT if dwc:year is not EMPTY; otherwise NOT_COMPLIANT
		
		String year = "1800";
		DQResponse<ComplianceValue> result = DwCEventDQ.validationYearEmpty(year);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		year = "foo";
		result = DwCEventDQ.validationYearEmpty(year);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		year = "";
		result = DwCEventDQ.validationYearEmpty(year);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentEventFromEventdate(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventFromEventdate() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
        // or does not contain a valid ISO 8601-1:2019 date; AMENDED 
        // if one or more EMPTY terms of the dwc:Event class (dwc:year, 
        // dwc:month, dwc:day, dwc:startDayOfYear, dwc:endDayOfYear) 
        // have been filled in from a valid unambiguously interpretable 
        // value in dwc:eventDate and eventDate is wholly within one 
        // year; otherwise NOT_AMENDED 
		
		String eventDate = "";
		String year = "1832";
		String month = "";
		String day = "";
		String startDayOfYear = "";
		String endDayOfYear = "";
		DQResponse<AmendmentValue> response = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
	
		eventDate = "Foo";
		response = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		
		eventDate = "1961-01-28/1961-01-29";
		year = "";
		response = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(5, response.getValue().getObject().size());
		assertEquals("1961", response.getValue().getObject().get("dwc:year"));
		assertEquals("1", response.getValue().getObject().get("dwc:month"));
		assertEquals("28", response.getValue().getObject().get("dwc:day"));
		assertEquals("28", response.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("29", response.getValue().getObject().get("dwc:endDayOfYear"));
		logger.debug(response.getComment());
		
		eventDate = "1961-01-28/1961-01-29";
		year = "1961";
		response = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(4, response.getValue().getObject().size());
		assertEquals("1", response.getValue().getObject().get("dwc:month"));
		assertEquals("28", response.getValue().getObject().get("dwc:day"));
		assertEquals("28", response.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("29", response.getValue().getObject().get("dwc:endDayOfYear"));
		logger.debug(response.getComment());
		
		eventDate = "1961-01-28/1962-45-34";
		year = "1961";
		response = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		
		eventDate = "1961-01-28/1961-01-29";
		year = "1961";
		month = "01";
		day = "28";
		startDayOfYear = "28";
		endDayOfYear = "29";
		response = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		
		eventDate = "1961-01-28/1962-01-29";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		response = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentEventdateStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateStandardized() {
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY; 
        // AMENDED if the value of dwc:eventDate was changed to unambiguously 
        // conform with an ISO 8601-1:2019 date; otherwise NOT_AMENDED 
		
		String eventDate = "";
		DQResponse<AmendmentValue> response = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		
		eventDate = "X";
		response = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		
		eventDate = "1900";
		response = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		
		eventDate = "04/03/1900";
		response = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		
		eventDate = "1892/04/20";
		response = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(1, response.getValue().getObject().size());
		assertEquals("1892-04-20", response.getValue().getObject().get("dwc:eventDate"));
		logger.debug(response.getComment());
		
		eventDate = "2021-28-10";
		response = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(1, response.getValue().getObject().size());
		assertEquals("2021-10-28", response.getValue().getObject().get("dwc:eventDate"));
		logger.debug(response.getComment());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEventdateNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateNotstandard() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY; 
        // COMPLIANT if the value of dwc:eventDate is a valid ISO 8601-1:2019 
        // date; otherwise NOT_COMPLIANT 
		
		String eventDate = "";
		DQResponse<ComplianceValue> result = DwCEventDQ.validationEventdateNotstandard(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		eventDate = "1979-06-15";
		result = DwCEventDQ.validationEventdateNotstandard(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1979/06/15";
		result = DwCEventDQ.validationEventdateNotstandard(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEventConsistent(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEventInconsistent() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY, 
        // or all of dwc:year, dwc:month, dwc:day, dwc:startDayOfYear 
        // and dwc:endDayOfYear are EMPTY; COMPLIANT if all of the 
        // following conditions are met 1) the provided value of year 
        // matches the start year of the range represented by eventDate 
        // or year is empty, and 2) the provided value in month matches 
        // the start month of the range represented by eventDate or 
        // month is empty, and 3) the provided value in day matches 
        // the start day of the range represented by eventDate or day 
        // is empty, and 4) the provided value in startDayOfYear matches 
        // the start day of the year of the range represented by eventDate 
        // or startDayOfYear is empty, and 5) the provided value in 
        // endDayOfYear matches the end day of the year the range represented 
        // by eventDate or endDayOfYear is empty; otherwise NOT_COMPLIANT. 
        //
		
		String eventDate = "";
		String year = "";
		String month = "";
		String day = "";
		String startDayOfYear = "";
		String endDayOfYear = "";
		DQResponse<ComplianceValue> result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		eventDate = "1980";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		eventDate = "";
		year = "1980";
		month = "1";
		day = "2";
		startDayOfYear = "2";
		endDayOfYear = "2";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		eventDate = "1980-01-02/1980-01-03";
		year = "1980";
		month = "1";
		day = "2";
		startDayOfYear = "2";
		endDayOfYear = "3";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());

		eventDate="1949-09-15T12:34";
		year = "1949";
		month = "9";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate="1949-09-15T12:34";
		year = "1949";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		eventDate="1949-09-15T12:34";
		year = "1949";
		month = "8";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1980-01-02/1980-01-03";
		year = "1980";
		month = "1";
		day = "2";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1980-01-02/1980-01-03";
		year = "1980";
		month = "1";
		day = "3";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationYearOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationYearOutofrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:year is not present, 
        // or is EMPTY or cannot be interpreted as an integer; COMPLIANT 
        // if the value of dwc:year is within the Parameter range; 
        //otherwise NOT_COMPLIANT 

        // This test is defined as parameterized.
        // bdq:earliestDate="1600"; bdq:latestDate=current year
		
		String year = "";
		DQResponse<ComplianceValue> result = DwCEventDQ.validationYearOutofrange(year, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		year = "foo";
		result = DwCEventDQ.validationYearOutofrange(year, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		year = "1880";
		result = DwCEventDQ.validationYearOutofrange(year, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		year = "1580";
		result = DwCEventDQ.validationYearOutofrange(year, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		year = "2000";
		result = DwCEventDQ.validationYearOutofrange(year, 1900, 1999);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentEventdateFromVerbatim(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromVerbatim() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is not EMPTY 
        // or the value of dwc:verbatimEventDate is EMPTY or not unambiguously 
        // interpretable as an ISO 8601-1:2019 date; AMENDED if the 
        // value of dwc:eventDate was unambiguously interpreted from 
        // dwc:verbatimEventDate; otherwise NOT_AMENDED 
		
		String eventDate = "1900";
		String verbatimEventDate = "";
		DQResponse<AmendmentValue> response = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		eventDate = "";
		verbatimEventDate = "";
		response = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		eventDate = "";
		verbatimEventDate = "foo";
		response = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		eventDate = "";
		verbatimEventDate = "1932/11/23";
		response = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(1, response.getValue().getObject().size());
		assertEquals("{dwc:eventDate=1932-11-23}", response.getValue().getObject().toString());
		logger.debug(response.getComment());
		eventDate = "";
		verbatimEventDate = "2/3 1932";
		response = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		
		eventDate = "";
		verbatimEventDate = "1932.10.6";
		response = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(1, response.getValue().getObject().size());
		assertEquals("1932-10-06", response.getValue().getObject().get("dwc:eventDate"));
		logger.debug(response.getComment());
		
		eventDate = "";
		verbatimEventDate = "Friday 29th Oct. 2021";
		response = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel()); 
		assertEquals(1, response.getValue().getObject().size());
		assertEquals("2021-10-29", response.getValue().getObject().get("dwc:eventDate"));
		logger.debug(response.getComment());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEventTemporalEmpty(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEventTemporalEmpty() {
		
        // Specification
        // COMPLIANT if any of dwc:eventDate, dwc:year, dwc:month, 
        // dwc:day, dwc:startDayOfYear, dwc:endDayOfYear, dwc:verbatimEventDate 
        // are NOT EMPTY; otherwise NOT_COMPLIANT. 
		
		DQResponse<ComplianceValue> result = null;
		String eventDate = null;
		String verbatimEventDate = null;
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		String eventTime = null;
		
		result = DwCEventDQ.validationEventTemporalEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
	
		year = "1852";
		result = DwCEventDQ.validationEventTemporalEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentEventdateFromYearmonthday(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromYearmonthday() {
		
        // Specification
        // INTERNAL _PREREQUISITES_NOT_MET if dwc:eventDate is not 
        // EMPTY or dwc:year is EMPTY or is uninterpretable as a valid 
        // year; AMENDED if the value of dwc:eventDate was unambiguously 
        // interpreted from the values in dwc:year, dwc:month and dwc:day; 
        // otherwise NOT_AMENDED 
		
		DQResponse<AmendmentValue> result = null;
		String eventDate = null;
		String year = null;
		String month = null;
		String day = null;
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		
		eventDate="1880";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		
		eventDate="Foo";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		
		eventDate="";
		year="Foo";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		
		eventDate="";
		year="1880";
		month="12";
		day="15";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		assertNotNull(result.getValue().getObject());
		Map<String,String> resultValues = result.getValue().getObject();
		assertTrue(resultValues.containsKey("dwc:eventDate"));
		assertEquals("1880-12-15",resultValues.get("dwc:eventDate"));
		assertEquals(1,resultValues.size());
		
		eventDate="";
		year="1880";
		month="XII";
		day="15";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		assertNotNull(result.getValue().getObject());
		resultValues = result.getValue().getObject();
		assertTrue(resultValues.containsKey("dwc:eventDate"));
		assertEquals("1880-12-15",resultValues.get("dwc:eventDate"));
		assertEquals(1,resultValues.size());
		
		eventDate="";
		year="1880";
		month="32";
		day="15";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		assertNotNull(result.getValue().getObject());
		resultValues = result.getValue().getObject();
		assertTrue(resultValues.containsKey("dwc:eventDate"));
		assertEquals("1880",resultValues.get("dwc:eventDate"));
		assertEquals(1,resultValues.size());
		
		eventDate="";
		year="1880";
		month="Feb";
		day="15";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		assertNotNull(result.getValue().getObject());
		resultValues = result.getValue().getObject();
		assertTrue(resultValues.containsKey("dwc:eventDate"));
		assertEquals("1880",resultValues.get("dwc:eventDate"));
		assertEquals(1,resultValues.size());
		
		eventDate="";
		year="1880";
		month="";
		day="150";  // out of range
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		
		// Notes in issue: "If dwc:year and dwc:day are present, but dwc:month is not supplied, 
		// then just the year should be given as the proposed amendment."
		eventDate="";
		year="1880";
		month=""; // empty
		day="15";  
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		assertNotNull(result.getValue().getObject());
		resultValues = result.getValue().getObject();
		assertTrue(resultValues.containsKey("dwc:eventDate"));
		assertEquals("1880",resultValues.get("dwc:eventDate"));
		assertEquals(1,resultValues.size());
		
		eventDate="";
		year="1880";
		month="10"; 
		day="x";  // uninterpretable
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.AMENDED.getLabel(), result.getResultState().getLabel());
		assertNotNull(result.getValue().getObject());
		resultValues = result.getValue().getObject();
		assertTrue(resultValues.containsKey("dwc:eventDate"));
		assertEquals("1880-10",resultValues.get("dwc:eventDate"));
		assertEquals(1,resultValues.size());
		
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationDayOutofrange(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationDayOutofrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if (a) dwc:day is EMPTY, 
        // or (b) dwc:day is not interpretable as an integer, or (c) 
        // dwc:day is interpretable as an integer between 29 and 31 
        // inclusive and dwc:month is not interpretable as an integer 
        // between 1 and 12, or (d) dwc:month is interpretable as the 
        // integer 2 and dwc:day is interpretable as the integer 29 
        // and dwc:year is not interpretable as a valid ISO 8601 year; 
        // COMPLIANT if (a) the value of dwc:day is interpretable as 
        // an integer between 1 and 28 inclusive, or (b) dwc:day is 
        // interpretable as an integer between 29 and 30 and dwc:month 
        // is interpretable as an integer in the set (4,6,9,11), or 
        // (c) dwc:day is interpretable as an integer between 29 and 
        // 31 and dwc:month is interpretable as an integer in the set 
        // (1,3,5,7,8,10,12), or (d) dwc:day is interpretable as the 
        // integer 29 and dwc:month is interpretable as the integer 
        // 2 and dwc:year is interpretable as is a valid leap year 
        // (evenly divisible by 400 or (evenly divisible by 4 but not 
        // evenly divisible by 100)); otherwise NOT_COMPLIANT.
		
		DQResponse<ComplianceValue> response = null;
		String year = null;
		String month = null;
		String day = null;
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertNull(response.getValue());
		logger.debug(response.getComment());
		
		year = null;
		month = null;
		day = "34";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		year = null;
		month = null;
		day = "32";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		year = null;
		month = null;
		day = "-1";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		for (int d=1; d<29; d++) { 
			day = Integer.toString(d);
			response = DwCEventDQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
			assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
			logger.debug(response.getComment());
		}
		
		year = null;
		month = null;
		day = "29";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertNull(response.getValue());
		
		year = null;
		month = "2";
		day = "29";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertNull(response.getValue());
		
		year = null;
		month = "1";
		day = "29";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		year = "1981";
		month = "2";
		day = "29";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		year = "1980";
		month = "2";
		day = "29";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		year = null;
		month = "4";
		day = "30";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		year = null;
		month = "5";
		day = "30";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		year = null;
		month = "4";
		day = "31";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		year = null;
		month = "5";
		day = "30";
		response = DwCEventDQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationMonthNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationMonthNotstandard() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:month is EMPTY; COMPLIANT 
        // if the value of dwc:month is interpretable as an integer 
        // between 1 and 12 inclusive; otherwise NOT_COMPLIANT 
		
		String month = null;
		DQResponse<ComplianceValue> response = DwCEventDQ.validationMonthNotstandard(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertNull(response.getValue());
		logger.debug(response.getComment());
		
		for (int m=1; m<13; m++) { 
			month = Integer.toString(m);
			response = DwCEventDQ.validationMonthNotstandard(month);
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
			assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
			logger.debug(response.getComment());
			
			month = String.format("%02d", m);
			response = DwCEventDQ.validationMonthNotstandard(month);
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
			assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
			logger.debug(response.getComment());
		}
		
		month = "foo";
		response = DwCEventDQ.validationMonthNotstandard(month);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		month = "x";
		response = DwCEventDQ.validationMonthNotstandard(month);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		month = "IV";
		response = DwCEventDQ.validationMonthNotstandard(month);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentDayStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentDayStandardized() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; AMENDED 
        // if the value of dwc:day was unambiguously interpreted as 
        // an integer between 1 and 31 inclusive; otherwise NOT_AMENDED 
        //
		
		String day = null;
		DQResponse<AmendmentValue> response = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertEquals(0,response.getValue().getObject().size());
		logger.debug(response.getComment());
		
		day = " ";
		response = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertEquals(0,response.getValue().getObject().size());
		logger.debug(response.getComment());
		Map<String,String> value;
		
		for (int d=1; d<31; d++) { 
			day = Integer.toString(d);
			response = DwCEventDQ.amendmentDayStandardized(day);
			assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel());
			assertEquals(0,response.getValue().getObject().size());
			logger.debug(response.getComment());
			
			if (d<10) { 
				day = String.format("%02d", d);
				response = DwCEventDQ.amendmentDayStandardized(day);
				assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
				assertEquals(1, response.getValue().getObject().size());
				value = response.getValue().getObject();
				assertTrue(value.containsKey("dwc:day"));
				assertEquals(Integer.valueOf(d), Integer.valueOf(value.get("dwc:day")));
				logger.debug(response.getComment());
			} else {
				day = String.format("%03d", d);
				response = DwCEventDQ.amendmentDayStandardized(day);
				assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
				assertEquals(1, response.getValue().getObject().size());
				value = response.getValue().getObject();
				assertTrue(value.containsKey("dwc:day"));
				assertEquals(Integer.valueOf(d), Integer.valueOf(value.get("dwc:day")));
				logger.debug(response.getComment());
			}
			
			day = Integer.toString(d);
			response = DwCEventDQ.amendmentDayStandardized(" " + day + " ");
			assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
			assertEquals(1, response.getValue().getObject().size());
			value = response.getValue().getObject();
			assertTrue(value.containsKey("dwc:day"));
			assertEquals(Integer.valueOf(d), Integer.valueOf(value.get("dwc:day")));
			logger.debug(response.getComment());
			
		}
		day = "1st";
		response = DwCEventDQ.amendmentDayStandardized(" " + day + " ");
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(1, response.getValue().getObject().size());
		value = response.getValue().getObject();
		assertTrue(value.containsKey("dwc:day"));
		assertEquals(Integer.valueOf(1), Integer.valueOf(value.get("dwc:day")));
		logger.debug(response.getComment());

		day = "X";
		response = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(0,response.getValue().getObject().size());
		
		day = "3rd Wednesday";
		response = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(0,response.getValue().getObject().size());

	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentMonthStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentMonthStandardized() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:month is EMPTY; AMENDED 
        // if the value of dwc:month was able to be interpreted as 
        // a integer between 1 and 12 inclusive; otherwise NOT_AMENDED 
        //

		String month = null;
		DQResponse<AmendmentValue> response = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertEquals(0,response.getValue().getObject().size());
		logger.debug(response.getComment());
		
		month = " ";
		response = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertEquals(0,response.getValue().getObject().size());
		logger.debug(response.getComment());
		Map<String,String> value;
		
		for (int m=1; m<13; m++) { 
			month = Integer.toString(m);
			response = DwCEventDQ.amendmentMonthStandardized(month);
			assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel());
			assertEquals(0,response.getValue().getObject().size());
			logger.debug(response.getComment());
			
			if (m<10) { 
				month = String.format("%02d", m);
				response = DwCEventDQ.amendmentMonthStandardized(month);
				assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
				assertEquals(1, response.getValue().getObject().size());
				value = response.getValue().getObject();
				assertTrue(value.containsKey("dwc:month"));
				assertEquals(Integer.valueOf(m), Integer.valueOf(value.get("dwc:month")));
				logger.debug(response.getComment());
			} else {
				month = String.format("%03d", m);
				response = DwCEventDQ.amendmentMonthStandardized(month);
				assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
				assertEquals(1, response.getValue().getObject().size());
				value = response.getValue().getObject();
				assertTrue(value.containsKey("dwc:month"));
				assertEquals(Integer.valueOf(m), Integer.valueOf(value.get("dwc:month")));
				logger.debug(response.getComment());
			}
			
			month = String.format("%02d", m);
			response = DwCEventDQ.amendmentMonthStandardized(" " + month + " ");
			logger.debug(response.getComment());
			assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
			assertEquals(1, response.getValue().getObject().size());
			value = response.getValue().getObject();
			assertTrue(value.containsKey("dwc:month"));
			assertEquals(Integer.valueOf(m), Integer.valueOf(value.get("dwc:month")));
		}	
		
		month = "34";
		response = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(0,response.getValue().getObject().size());
		logger.debug(response.getComment());
		
		month = "Jan";
		response = DwCEventDQ.amendmentMonthStandardized(month);
		logger.debug(response.getComment());
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(0, response.getValue().getObject().size());
		
		month = "IV";
		response = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(1, response.getValue().getObject().size());
		value = response.getValue().getObject();
		assertTrue(value.containsKey("dwc:month"));
		assertEquals(Integer.valueOf(4), Integer.valueOf(value.get("dwc:month")));
		logger.debug(response.getComment());
		
		month = "x";
		response = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(1, response.getValue().getObject().size());
		value = response.getValue().getObject();
		assertTrue(value.containsKey("dwc:month"));
		assertEquals(Integer.valueOf(10), Integer.valueOf(value.get("dwc:month")));
		logger.debug(response.getComment());
		
		month = "October";
		response = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(0, response.getValue().getObject().size());
		logger.debug(response.getComment());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationStartdayofyearOutofrange(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationStartdayofyearOutofrange() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:startDayOfYear is 
        // EMPTY or if the value of dwc:startDayOfYear is equal to 
        // 366 and (dwc:eventDate is EMPTY or the value of dwc:eventDate 
        // can not be interpreted to find single year or a start year 
        // in a range); COMPLIANT if the value of dwc:startDayOfYear 
        // is an integer between 1 and 365, inclusive, or if the value 
        // of dwc:startDayOfYear is 366 and the start year interpreted 
        // from dwc:eventDate is a leap year; otherwise NOT_COMPLIANT 
        //
    	
		
		String startDayOfYear = null;
		String eventDate = null;
		DQResponse<ComplianceValue> response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertNull(response.getValue());
		logger.debug(response.getComment());
		
		startDayOfYear = "366";
		eventDate = null;
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertNull(response.getValue());
		logger.debug(response.getComment());
		
		startDayOfYear = "366";
		eventDate = "Foo";
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertNull(response.getValue());
		logger.debug(response.getComment());
		
		startDayOfYear = "365";
		eventDate = "Foo";
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		startDayOfYear = "365";
		eventDate = "";
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		startDayOfYear = "365";
		eventDate = "1980";
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		startDayOfYear = "365";
		eventDate = "1981";
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		startDayOfYear = "366";
		eventDate = "1980";
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		startDayOfYear = "366";
		eventDate = "1981";
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		startDayOfYear = "366";
		eventDate = "1979-01-01/1980-01-10";  // only start year is examined, not other parts of date for this test.
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		startDayOfYear = "366";
		eventDate = "1980-12-31/1981-12-31";
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		startDayOfYear = "foo";
		eventDate = "1980-12-31/1981-12-31";
		response = DwCEventDQ.validationStartdayofyearOutofrange(startDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEnddayofyearOutofrange(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEnddayofyearOutofrange() {
		
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:endDayOfYear is EMPTY 
        // or if the value of dwc:endDayOfYear is equal to 366 and 
        // (dwc:eventDate is EMPTY or the value of dwc:eventDate cannot 
        // be interpreted to find a single year or an end year in a 
        // range); COMPLIANT if the value of dwc:endDayOfYear is an 
        // integer between 1 and 365 inclusive, or if the value of 
        // dwc:endDayOfYear is 366 and the end year interpreted from 
        //dwc:eventDate is a leap year; otherwise NOT_COMPLIANT 
    	
		String endDayOfYear = null;
		String eventDate = null;
		DQResponse<ComplianceValue> response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertNull(response.getValue());
		logger.debug(response.getComment());
		
		endDayOfYear = "366";
		eventDate = null;
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertNull(response.getValue());
		logger.debug(response.getComment());
		
		endDayOfYear = "366";
		eventDate = "Foo";
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		assertNull(response.getValue());
		logger.debug(response.getComment());
		
		endDayOfYear = "365";
		eventDate = "Foo";
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		endDayOfYear = "365";
		eventDate = "";
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		endDayOfYear = "365";
		eventDate = "1980";
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		endDayOfYear = "365";
		eventDate = "1981";
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		endDayOfYear = "366";
		eventDate = "1980";
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		endDayOfYear = "366";
		eventDate = "1981";
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		endDayOfYear = "366";
		eventDate = "1979-01-01/1980-01-10";  // only end year is examined, not other parts of date for this test.
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		endDayOfYear = "366";
		eventDate = "1980-12-31/1981-12-31";
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
		endDayOfYear = "foo";
		eventDate = "1980-12-31/1981-12-31";
		response = DwCEventDQ.validationEnddayofyearOutofrange(endDayOfYear, eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), response.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), response.getValue().getLabel());
		logger.debug(response.getComment());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentEventdateFromYearstartdayofyearenddayofyear(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromYearstartdayofyearenddayofyear() {
		
        // Specification
    	// INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate was not 
    	// EMPTY or any of dwc:year, dwc:startDayOfYear, or dwc:endDayOfYear were EMPTY 
    	// or any of the values in dwc:year, dwc:startDayOfYear, or dwc:endDayOfYear 
    	// were not independently interpretable; AMENDED if dwc:eventDate was FILLED_IN 
    	// from the values in dwc:year, dwc:startDayOfYear and dwc:endDayOfYear; 
    	// if the value of dwc:endDayOfYear is less than the value of dwc:startDayOfYear, 
    	// or otherwise NOT_AMENDED
		
		String eventDate = "1890";
		String year = "";
		String startDayOfYear = "";
		String endDayOfYear = "";
		DQResponse<AmendmentValue> response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		
		eventDate = null;
		year = "";
		startDayOfYear = "";
		endDayOfYear = "";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		
		eventDate = null;
		year = "1890";
		startDayOfYear = "";
		endDayOfYear = "";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		
		eventDate = null;
		year = "";
		startDayOfYear = "5";
		endDayOfYear = "180";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		
		eventDate = null;
		year = "foo";
		startDayOfYear = "5";
		endDayOfYear = "180";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
			
		eventDate = null;
		year = "1890";
		startDayOfYear = "X";
		endDayOfYear = "180";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
			
		eventDate = null;
		year = "1890";
		startDayOfYear = "5";
		endDayOfYear = "Foo";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		
		eventDate = null;
		year = "32";
		startDayOfYear = "5";
		endDayOfYear = "180";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(1,response.getValue().getObject().size());
		assertEquals("dwc:eventDate",response.getValue().getObject().keySet().iterator().next());
		assertEquals("0032-01-05/0032-06-28",response.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1932";
		startDayOfYear = "5";
		endDayOfYear = "180";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(1,response.getValue().getObject().size());
		assertEquals("dwc:eventDate",response.getValue().getObject().keySet().iterator().next());
		assertEquals("1932-01-05/1932-06-28",response.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1980";
		startDayOfYear = "1";
		endDayOfYear = "366";  // leap year
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(1,response.getValue().getObject().size());
		assertEquals("dwc:eventDate",response.getValue().getObject().keySet().iterator().next());
		assertEquals("1980",response.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1980";
		startDayOfYear = "366";
		endDayOfYear = "366";  // leap year
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(1,response.getValue().getObject().size());
		assertEquals("dwc:eventDate",response.getValue().getObject().keySet().iterator().next());
		assertEquals("1980-12-31",response.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1932";
		startDayOfYear = "5";
		endDayOfYear = "";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
			
		eventDate = "";
		year = "1932";
		startDayOfYear = "";
		endDayOfYear = "5";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		
		eventDate = "";
		year = "1932";
		startDayOfYear = "180";
		endDayOfYear = "5";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel());
		
		eventDate = "";
		year = "1981";
		startDayOfYear = "1";
		endDayOfYear = "366"; // not a leap year
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), response.getResultState().getLabel());	
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#measureEventdatePrecisioninseconds(java.lang.String)}.
	 */
	@Test
	public void testMeasureEventdatePrecisioninseconds() {
		
        // Specification (updated as of 2022 Feb 21)
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY or does not 
        // contain a valid ISO 8601-1:2019 date; otherwise RUN_HAS_RESULT with the 
        // result value being the length of the period expressed in the dwc:eventDate 
        // in seconds
		
		DQResponse<NumericalValue> measure = DwCEventDQ.measureEventdatePrecisioninseconds("1880-05-08");
		Long seconds = (60l*60l*24l); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdatePrecisioninseconds("");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdatePrecisioninseconds("Foo");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), measure.getResultState().getLabel());
		
		measure = DwCEventDQ.measureEventdatePrecisioninseconds("1880-15-35");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), measure.getResultState().getLabel());
		
		measure = DwCEventDQ.measureEventdatePrecisioninseconds("1970");
		seconds = (60l*60l*24l*365); // not leap year, leap seconds ignored.
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdatePrecisioninseconds("1980");
		seconds = (60l*60l*24l*366); // leap year 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdatePrecisioninseconds("1981");
		seconds = (60l*60l*24l*365); // not leap year 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationDayNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationDayNotstandard() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; COMPLIANT
        // if the value of the field dwc:day is an integer between
        // 1 and 31 inclusive; otherwise NOT_COMPLIANT.
		
		String day = "1";
		DQResponse<ComplianceValue> result = DwCEventDQ.validationDayNotstandard(day);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		day = "33";
		result = DwCEventDQ.validationDayNotstandard(day);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		day = "";
		result = DwCEventDQ.validationDayNotstandard(day);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
	}

}
