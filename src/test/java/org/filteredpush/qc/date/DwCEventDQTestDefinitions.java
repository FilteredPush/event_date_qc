/**
 * 
 */
package org.filteredpush.qc.date;

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.ComplianceValue;
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
		
		eventDate = "";
		result = DwCEventDQ.validationEventdateEmpty(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());

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
		
		year = "";
		result = DwCEventDQ.validationYearEmpty(year);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
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
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationEventTemporalEmpty(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEventTemporalEmpty() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#amendmentEventdateFromYearmonthday(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromYearmonthday() {
		fail("Not yet implemented");
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
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#measureEventdatePrecisioninseconds(java.lang.String)}.
	 */
	@Test
	public void testMeasureEventdatePrecisioninseconds() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQs#validationDayNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationDayNotstandard() {
		fail("Not yet implemented");
	}

}
