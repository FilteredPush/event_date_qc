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
		
		fail("Not yet implemented");
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
		fail("Not yet implemented");

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
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentEventdateStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateStandardized() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEventdateNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateNotstandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEventInconsistent(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEventInconsistent() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationYearOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationYearOutofrange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentEventdateFromVerbatim(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromVerbatim() {
		
        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is not EMPTY 
        // or the value of dwc:verbatimEventDate is EMPTY or not unambiguously 
        // interpretable as an ISO 8601-1:2019 date; AMENDED if the 
        // value of dwc:eventDate was unambiguously interpreted from 
        //dwc:verbatimEventDate; otherwise NOT_AMENDED 
		
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
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		
		eventDate="";
		year="1880";
		month="Feb";
		day="15";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		
		eventDate="";
		year="1880";
		month="";
		day="150";  // out of range
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		
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
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationDayOutofrange(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationDayOutofrange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationMonthNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationMonthNotstandard() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentDayStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentDayStandardized() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentMonthStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentMonthStandardized() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationStartdayofyearOutofrange(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationStartdayofyearOutofrange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEnddayofyearOutofrange(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEnddayofyearOutofrange() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentEventdateFromYearstartdayofyearenddayofyear(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromYearstartdayofyearenddayofyear() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate was not 
        // EMPTY or dwc:year was EMPTY or both dwc:startDayOfYear and 
        // dwc:endDayOfYear were EMPTY or the values were not interpretable; 
        // AMENDED if dwc:eventDate was FILLED_IN from the values in 
        // dwc:year, dwc:startDayOfYear and dwc:endDayOfYear; otherwise 
        // NOT_AMENDED 
		
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
		year = "";
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
		year = "32";
		startDayOfYear = "5";
		endDayOfYear = "180";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
		
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
		year = "1932";
		startDayOfYear = "5";
		endDayOfYear = "";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.AMENDED.getLabel(), response.getResultState().getLabel());
		assertEquals(1,response.getValue().getObject().size());
		assertEquals("dwc:eventDate",response.getValue().getObject().keySet().iterator().next());
		assertEquals("1932-01-05",response.getValue().getObject().get("dwc:eventDate"));
			
		// NOTE: This may change.  Not clearly specified in the issue.
		eventDate = "";
		year = "1932";
		startDayOfYear = "";
		endDayOfYear = "5";
		response = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), response.getResultState().getLabel());
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
		
		
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationDayNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationDayNotstandard() {
		
        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; COMPLIANT 
        // if the value of the field dwc:day is an integer between 
        //1 and 31 inclusive; otherwise NOT_COMPLIANT. 
		
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
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
		fail("Not yet implemented");
	}

}
