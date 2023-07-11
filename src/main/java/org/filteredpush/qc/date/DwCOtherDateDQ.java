package org.filteredpush.qc.date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Consulted;
import org.datakurator.ffdq.annotations.Mechanism;
import org.datakurator.ffdq.annotations.Parameter;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.ProvidesVersion;
import org.datakurator.ffdq.annotations.Specification;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.model.ResultState;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * FFDQ tests for Date related concepts in Darwin Core outside of the Event class.
 * 
 * Provides Core TG2 tests: 
 *   #76 VALIDATION_DATEIDENTIFIED_INRANGE dc8aae4b-134f-4d75-8a71-c4186239178e
 *   #69 VALIDATION_DATEIDENTIFIED_STANDARD 66269bdd-9271-4e76-b25c-7ab81eebe1d8
 *   #26 AMENDMENT_DATEIDENTIFIED_STANDARDIZED 39bb2280-1215-447b-9221-fd13bc990641
 *   
 * Also provides: 
 *   Date Modified Format Correction (supplemental)  
 *   ModifiedDateValid (supplemental)
 * 
 * @author mole
 *
 */
@Mechanism(
		value = "urn:uuid:bf5b7706-d0a6-4c65-9644-c750e7188ee0",
		label = "Kurator: Date Validator - DwCOtherDateDQ:v3.0.2")
public class DwCOtherDateDQ {
	
	private static final Log logger = LogFactory.getLog(DwCOtherDateDQ.class);
	
    /**
     * Is the value of dwc:dateIdentified a valid ISO date?
     * 
     * #69 Validation SingleRecord Conformance: dateidentified notstandard
     *
     * Provides: VALIDATION_DATEIDENTIFIED_STANDARD
     * Version: 2023-03-27
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DATEIDENTIFIED_STANDARD", description="Is the value of dwc:dateIdentified a valid ISO date?")
    @Provides("66269bdd-9271-4e76-b25c-7ab81eebe1d8")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/66269bdd-9271-4e76-b25c-7ab81eebe1d8/2023-03-27")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is EMPTY; COMPLIANT if the value of dwc:dateIdentified contains a valid ISO 8601-1 date; otherwise NOT_COMPLIANT ")
    public static DQResponse<ComplianceValue> validationDateidentifiedStandard(@ActedUpon("dwc:dateIdentified") String dateIdentified) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
        // EMPTY; COMPLIANT if the value of dwc:dateIdentified contains 
        // a valid ISO 8601-1 date; otherwise NOT_COMPLIANT 
        
    	if (DateUtils.isEmpty(dateIdentified)) {
    		result.addComment("No value provided for dwc:dateIdentified.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else {
    		try {
    	        if (DateUtils.eventDateValid(dateIdentified)) {
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is formated as an ISO date. ");
    			} else {
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is not a validly formatted ISO date .");
    			}
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    		} catch (Exception e) {
    			logger.debug(e.getMessage());
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment(e.getMessage());
    		}
    	}        
        
        return result;
    }
    
    
    /**
     * Is the value of dwc:dateIdentified within Parameter ranges and either overlap or is later than dwc:eventDate?
     *
     * #76 Validation SingleRecord Likelihood: dateidentified outofrange
     * with default values: bdq:earliestVaidDate="1753-01-01"; bdq:latestValidDate=current day
     *
     * Provides: VALIDATION_DATEIDENTIFIED_INRANGE
     * Version: 2023-06-20
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate as preceding dateIdentified
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DATEIDENTIFIED_INRANGE", description="Is the value of dwc:dateIdentified within Parameter ranges and either overlap or is later than dwc:eventDate?")
    @Provides("dc8aae4b-134f-4d75-8a71-c4186239178e")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/dc8aae4b-134f-4d75-8a71-c4186239178e/2023-06-20")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if (1) dwc:dateIdentified is EMPTY, or (2) dwc:dateIdentified contains an invalid value according to ISO 8601-1, or (3) bdq:includeEventDate=true and dwc:eventDate is not a valid ISO 8601-1 date; COMPLIANT if the value of dwc:dateIdentified is between bdq:earliestValidDate and bdq:latestValidDate inclusive and either (1) dwc:eventDate is EMPTY or bdq:includeEventDate=false, or (2) if dwc:eventDate is a valid ISO 8601-1 date and dwc:dateIdentified overlaps or is later than the dwc:eventDate; otherwise NOT_COMPLIANT bdq:sourceAuthority is 'ISO 8601-1:2019' [https://www.iso.org/obp/ui/],bdq:earliestValidDate default='1753-01-01',bdq:latestValidDate default=[current day],bdq:includeEventDate default=true")
    public static DQResponse<ComplianceValue> validationDateidentifiedInrange(
		@ActedUpon("dwc:dateIdentified") String dateIdentified,
		@Consulted("dwc:eventDate") String eventDate
	) {
        // Parameters. This test is defined as parameterized.
        // Default values: 
        // bdq:earliestValidDate default="1753-01-01"; bdq:latestValidDate default=[current day]; bdq:includeEventDate default=true
		String currentDay = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		return DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, "1753-01-01", currentDay, "true");
	} 

    /**
     * Is the value of dwc:dateIdentified within Parameter ranges and either overlap or is later than dwc:eventDate?
     * 
     * #76 Validation SingleRecord Likelihood: dateidentified outofrange
     *
     * Provides: VALIDATION_DATEIDENTIFIED_INRANGE
     * Version: 2023-06-20
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate as preceding dateIdentified
     * @param earliestValidDate the earliest dateIdentified considered to be within range
	 * @param latestValidDate the most recent dateIdentified considered to be within range
	 * @param includeEventDate if equal to "true" then also assess the dateIdentified against
	 *   the eventDate.
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DATEIDENTIFIED_INRANGE", description="Is the value of dwc:dateIdentified within Parameter ranges and either overlap or is later than dwc:eventDate?")
    @Provides("dc8aae4b-134f-4d75-8a71-c4186239178e")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/dc8aae4b-134f-4d75-8a71-c4186239178e/2023-06-20")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if (1) dwc:dateIdentified is EMPTY, or (2) dwc:dateIdentified contains an invalid value according to ISO 8601-1, or (3) bdq:includeEventDate=true and dwc:eventDate is not a valid ISO 8601-1 date; COMPLIANT if the value of dwc:dateIdentified is between bdq:earliestValidDate and bdq:latestValidDate inclusive and either (1) dwc:eventDate is EMPTY or bdq:includeEventDate=false, or (2) if dwc:eventDate is a valid ISO 8601-1 date and dwc:dateIdentified overlaps or is later than the dwc:eventDate; otherwise NOT_COMPLIANT bdq:sourceAuthority is 'ISO 8601-1:2019' [https://www.iso.org/obp/ui/],bdq:earliestValidDate default='1753-01-01',bdq:latestValidDate default=[current day],bdq:includeEventDate default=true")
    public static DQResponse<ComplianceValue> validationDateidentifiedInrange(
		@ActedUpon("dwc:dateIdentified") String dateIdentified,
		@Consulted("dwc:eventDate") String eventDate, 
        @Parameter(name="bdq:earliestVaidDate") String earliestValidDate,
		@Parameter(name="bdq:latestValidDate") String latestValidDate,
		@Parameter(name="bdq:includeEventDate") String includeEventDate
	) {

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
        // bdq:earliestValidDate,bdq:latestValidDate,bdq:includeEventDate
        // Default values: 
        // bdq:earliestValidDate default="1753-01-01"; bdq:latestValidDate default=[current day]; bdq:includeEventDate default=true

    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
		if (DateUtils.isEmpty(earliestValidDate)) { 
			earliestValidDate = "1753-01-01";
		}
		if (DateUtils.isEmpty(earliestValidDate)) { 
			latestValidDate  = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		}
		if (DateUtils.isEmpty(includeEventDate)) { 
			includeEventDate = "true";
		}
		Boolean includeEventDateBoolean = false;
		if (includeEventDate.toLowerCase().equals("true")) { 
			includeEventDateBoolean = true;
		} else if (includeEventDate.toLowerCase().equals("false")) { 
			includeEventDateBoolean = false;
		} else { 
    		result.addComment("Uninterpretable value provided for bdq:includeEventDate, treating as true.");
			includeEventDateBoolean = true;
		}

    	String range = earliestValidDate + "/" + latestValidDate;
    	LocalDateInterval withinInterval = DateUtils.extractInterval(range);

    	logger.debug(dateIdentified);
    	logger.debug(eventDate);
    	
    	if (DateUtils.isEmpty(dateIdentified)) {
    		// INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
    		// EMPTY 
    		result.addComment("No value provided for dwc:dateIdentified.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else if (!DateUtils.eventDateValid(dateIdentified)) {
    		// INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
    		// ... or contains and invalid value according to ISO 8601-1; 
    		result.addComment("Value provided for dwc:dateIdentified ["+dateIdentified+"] is not a valid date.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else if (DateUtils.isEmpty(eventDate) || !includeEventDateBoolean) {
    		// COMPLIANT 
    		// if the value of dwc:dateIdentified is between bdq:earliestValidDate 
    		// and bdq:latestValidDate inclusive and either (1) dwc:eventDate 
    		// is EMPTY or bdq:includeEventDate=false, or (2) ... 
    		if (DateUtils.isEmpty(eventDate)) {
    			result.addComment("No valid value provided for dwc:eventDate to compare with dwc:dateIdentified.");
    		}
    		LocalDateInterval identifiedInterval = DateUtils.extractInterval(dateIdentified);
    		if (withinInterval.overlaps(identifiedInterval)) {
    			result.setValue(ComplianceValue.COMPLIANT);
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    			result.addComment("Provided value for dateIdentified [" + dateIdentified + "] overlaps the limits ["+earliestValidDate +"]-["+latestValidDate +"].");
    		} else { 
    			result.setValue(ComplianceValue.NOT_COMPLIANT);
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    			result.addComment("Provided value for dateIdentified [" + dateIdentified + "] is outside the limits ["+earliestValidDate +"]-["+latestValidDate +"].");
    		}
    	} else {
    		LocalDateInterval identifiedInterval = DateUtils.extractInterval(dateIdentified);
    		LocalDateInterval eventInterval = DateUtils.extractInterval(eventDate);
    		// COMPLIANT 
    		// if the value of dwc:dateIdentified is between bdq:earliestValidDate 
    		// and bdq:latestValidDate inclusive and either (1) ...
    		// (2) if dwc:eventDate 
    		// is a valid ISO 8601-1 date and dwc:dateIdentified overlaps 
    		// or is later than the dwc:eventDate; otherwise NOT_COMPLIANT 
    		if (identifiedInterval==null) { 
    			// INTERNAL_PREREQUISITES_NOT_MET if 
    			// (2) dwc:dateIdentified contains an invalid 
    			// value according to ISO 8601-1 
    			result.addComment("Value provided for dwc:dateIdentified ["+dateIdentified+"] is not a valid date.");
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		} else if (eventInterval==null) { 
    			// INTERNAL_PREREQUISITES_NOT_MET ...
    			// (3) bdq:includeEventDate=true 
    			// and dwc:eventDate is not a valid ISO 8601-1 date; 
    			logger.error("Error Constructing interval from dwc:eventDate ["+eventDate+"]");
    			result.addComment("Error constructing an interpretable earliest/latest date range to test against ["+range+"] .");
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		} else if (withinInterval==null) { 
    			logger.error("Error Constructing interval from earliest date to latest date");
    			result.addComment("Error constructing an interpretable earliest/latest date range to test against ["+range+"] .");
    			result.setResultState(ResultState.EXTERNAL_PREREQUISITES_NOT_MET);
    		} else if (!withinInterval.contains(identifiedInterval)) {
    			result.setValue(ComplianceValue.NOT_COMPLIANT);
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    			result.addComment("Provided value for dateIdentified [" + dateIdentified + "] starts extends beyond the limits ["+earliestValidDate +"]-["+latestValidDate +"].");
    		} else if (!DateUtils.eventDateValid(eventDate)) { 
    			result.addComment("Provided value for dwc:eventDate ["+eventDate+"] is not a valid date, unable to compare with dwc:dateIdentified.");
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    			result.setValue(ComplianceValue.NOT_COMPLIANT);
    			// Note: changed behavior in v2023-03-29, invalid eventDate is not compliant, not a prerequisites failure.
    			// result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		} else { 
    			if (eventInterval.contains(identifiedInterval)) {
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dateIdentified [" + dateIdentified + "] falls within the eventDate ["+eventDate+"].");
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} else if (identifiedInterval.getStartDate().isAfter(eventInterval.getEndDate())) {
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dateIdentified [" + dateIdentified + "] is after the end of the eventDate ["+eventDate+"].");;
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} else if (identifiedInterval.getStartDate().equals(eventInterval.getStartDate())) {
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dateIdentified [" + dateIdentified + "] starts at the same time as the eventDate ["+eventDate+"].");;
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} else if (identifiedInterval.overlaps(eventInterval)) { 
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dateIdentified [" + dateIdentified + "] overlaps the eventDate ["+eventDate+"].");;
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} else if (identifiedInterval.getEndDate().isBefore(eventInterval.getStartDate())) { 
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for dateIdentified [" + dateIdentified + "] ends before the eventDate starts.");
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} else { 
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for dateIdentified [" + dateIdentified + "] is within allowed range but is not more recent than the eventDate ["+eventDate+"].");
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			}
    		}
    	}        

    	return result;
    }
	
    /**
     * Propose amendment to the value of dwc:dateIdentified to a valid ISO date.
     * 
     * #26 Amendment SingleRecord Conformance: dateidentified standardized
     *
     * Provides: AMENDMENT_DATEIDENTIFIED_STANDARDIZED
     * Version: 2023-06-13
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_DATEIDENTIFIED_STANDARDIZED", description="Propose amendment to the value of dwc:dateIdentified to a valid ISO date.")
    @Provides("39bb2280-1215-447b-9221-fd13bc990641")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/39bb2280-1215-447b-9221-fd13bc990641/2023-06-13")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is EMPTY; AMENDED if the value of dwc:dateIdentified was not a properly formatted ISO 8601-1 date but was unambiguous and was altered to be a valid ISO 8601-1 date; otherwise NOT_AMENDED. ")
    public static DQResponse<AmendmentValue> amendmentDateidentifiedStandardized(@ActedUpon("dwc:dateIdentified") String dateIdentified) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is EMPTY; 
        // AMENDED if the value of dwc:dateIdentified was not a properly 
        // formatted ISO 8601-1 date but was unambiguous and was altered 
        // to be a valid ISO 8601-1 date; otherwise NOT_AMENDED.

		if (DateUtils.eventDateValid(dateIdentified)) {
			result.setResultState(ResultState.NOT_AMENDED);
			result.addComment("dwc:dateIdentified contains a correctly formatted date, not changing.");
		} else {
			if (DateUtils.isEmpty(dateIdentified)) {
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment("dwc:dateIdentified does not contains a value.");
			} else {
				EventResult extractResponse = DateUtils.extractDateFromVerbatimER(dateIdentified);
				if (!extractResponse.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
						(extractResponse.getResultState().equals(EventResult.EventQCResultState.RANGE) ||
								extractResponse.getResultState().equals(EventResult.EventQCResultState.DATE) ||
								extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS) ||
								extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)
						)
						)
				{
					Map<String, String> correctedValues = new HashMap<>();
					correctedValues.put("dwc:dateIdentified", extractResponse.getResult());

					if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
						result.setResultState(ResultState.NOT_AMENDED); 
						// result.setResultState(ResultState.AMBIGUOUS); // Excluded to conform with AMENDMENT_EVENTDATE_STANDARDIZED
						result.addComment("Potential interpretation of dwc:dateIdentified [" + dateIdentified + "] as ["+ extractResponse.getResult() +"], but such interpretation is ambiguous." );
						result.addComment(extractResponse.getComment());
					} else {
						if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
							result.setResultState(ResultState.NOT_AMENDED); 
							result.addComment("Potential interpretation of dwc:dateIdentified [" + dateIdentified + "] as ["+ extractResponse.getResult() +"] is suspect.");
							result.addComment(extractResponse.getComment());
						} else { 
							result.addComment("Unabmiguous interpretation of dwc:dateIdentified [" + dateIdentified + "] as ["+ extractResponse.getResult() +"].");
							result.setResultState(ResultState.AMENDED);
							result.setValue(new AmendmentValue(correctedValues));
						} 
					}
				} else {
					boolean matched = false;
					logger.debug(dateIdentified);
					if (dateIdentified.matches("^[0-9]{4}.[0-9]{2}.[0-9]{2}$")) { 
						try {
							// try to see if this is yyyy-dd-mm error for yyyy-mm-dd
							Integer secondBit = Integer.parseInt(dateIdentified.substring(5,7));
							Integer thirdBit = Integer.parseInt(dateIdentified.substring(8));
							if (secondBit>12 && thirdBit<12) {
								// try switching second and third parts of date.
								String toTest = dateIdentified.substring(0, 4).concat("-").concat(dateIdentified.substring(8)).concat("-").concat(dateIdentified.substring(5, 7));
								logger.debug(toTest);
								LocalDateInterval testingToTest = new LocalDateInterval(toTest);
								if (testingToTest!=null && testingToTest.isSingleDay()) { 
									Map<String, String> correctedValues = new HashMap<>();
									correctedValues.put("dwc:dateIdentified", testingToTest.toString());
									result.addComment("Unabmiguous interpretation of dwc:dateIdentified [" + dateIdentified + "] as ["+ extractResponse.getResult() +"].");
									result.setResultState(ResultState.AMENDED);
									result.setValue(new AmendmentValue(correctedValues));
									matched = true;
								}
							}
						} catch (Exception e) {
							logger.debug(e.getMessage());
						}
					}
					if (!matched) { 
						EventResult tryVerbatimResult = DateUtils.extractDateFromVerbatimER(dateIdentified);
						if (tryVerbatimResult.getResultState().equals(EventResult.EventQCResultState.DATE)) { 
							Map<String, String> correctedValues = new HashMap<>();
							correctedValues.put("dwc:dateIdentified", tryVerbatimResult.getResult());
							result.addComment("Unabmiguous interpretation of dwc:dateIdentified [" + dateIdentified + "] as ["+ extractResponse.getResult() +"].");
							result.setResultState(ResultState.AMENDED);
							result.setValue(new AmendmentValue(correctedValues));
						} else { 
							// per specification, internal prerequisites not met only if empty, failure result is NOT_AMENDED.
							result.setResultState(ResultState.NOT_AMENDED);
							result.addComment("Unable to extract a date from " + dateIdentified);
						} 
					} 
				}
			}
		}        

        return result;
    }
	
	/**
	 * Given a dateModified, check to see if it is empty or contains a valid date value.  If it contains
	 * a value that is not a valid date, propose a properly formatted dateModified as an amendment.
	 *
	 * @param modified dcterms:modified (date last modified) to check
	 * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
	 *    resultState is AMENDED if a new value is proposed.
	 */
	@Provides(value = "urn:uuid:367bf43f-9cb6-45b2-b45f-b8152f1d334a")
	@Amendment(label = "Date Modified Format Correction", description = "Try to propose a correction for a date modified")
	@Specification(value = "Check dcterms:modified to see if it is empty or contains a valid date value. If it contains a " +
	        "value that is not a valid date, propose a properly formatted dcterms:modified as an amendment.")
	public static DQResponse<AmendmentValue> correctModifiedDateFormat(@ActedUpon(value = "dcterms:modified") String modified) {
		DQResponse<AmendmentValue> result = new DQResponse<>();

		if (DateUtils.eventDateValid(modified)) {
			result.setResultState(ResultState.NOT_AMENDED);
			result.addComment("dcterms:modified contains a correctly formatted date, not changing.");
		} else {
			if (DateUtils.isEmpty(modified)) {
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment("dcterms:modified does not contains a value.");
			} else {
				EventResult extractResponse = DateUtils.extractDateFromVerbatimER(modified);
				if (!extractResponse.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
						(extractResponse.getResultState().equals(EventResult.EventQCResultState.RANGE) ||
								extractResponse.getResultState().equals(EventResult.EventQCResultState.DATE) ||
								extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS) ||
								extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)
						)
						)
				{
					Map<String, String> correctedValues = new HashMap<String, String>();
					correctedValues.put("dcterms:modified", extractResponse.getResult());

					result.setValue(new AmendmentValue(correctedValues));

					if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
						result.setResultState(ResultState.AMBIGUOUS);
						result.addComment(extractResponse.getComment());
					} else {
						if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
							result.addComment("Interpretation of dcterms:modified [" + modified + "] is suspect.");
							result.addComment(extractResponse.getComment());
						}
						result.setResultState(ResultState.AMENDED);
					}
				} else {
					result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
					result.addComment("Unable to extract a date from " + modified);
				}
			}
		}
		return result;
	}

	/**
	 * Test to see whether a provided dcterms:modified is a validly formated ISO date.
	 *
	 * Provides: ModifiedDateValid
	 *
	 * @param modified  a string to test
	 * @return COMPLIANT if modified is a validly formated ISO date/time with a duration of less than one day, NOT_COMPLIANT if
	 *     not an ISO date/time or a range of days, INTERNAL_PREREQUSISITES_NOT_MET if modified is empty.
	 */
	@Provides(value = "urn:uuid:62a9c256-43e4-41ee-8938-d2d2e99479ef")  // MODIFIED_DATE_INVALID/MODIFIED_DATE_VALID
	@Validation(label = "Modified date correctly formatted", description = "Test to see whether a provided dcterms:modified " +
			"is a validly formated ISO date/time.")
	@Specification(value = "Compliant if dcterms:modified can to parsed to an explicit date/time, otherwise not compliant. " +
			"Internal prerequisites not met if dcterms:modified is empty.")
	public static DQResponse<ComplianceValue> isModifiedDateValid(@ActedUpon(value = "dcterms:modified") String modified) {
		DQResponse<ComplianceValue> result = new DQResponse<>();

		if (DateUtils.isEmpty(modified)) {
			result.addComment("No value provided for dcterms:modified.");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
		} else {
			try {
				if (DateUtils.eventDateValid(modified) && DateUtils.specificToDay(modified)) {
					result.setValue(ComplianceValue.COMPLIANT);
					result.addComment("Provided value for dcterms:modified '" + modified + "' is formated as an ISO date tha can be parsed to an explicit date/time ");
				} else {
					if (!DateUtils.eventDateValid(modified)) {
						result.setValue(ComplianceValue.NOT_COMPLIANT);
						result.addComment("Provided value for dcterms:modified '" + modified + "' is not a validly formatted ISO date .");
					} else {
						result.setValue(ComplianceValue.NOT_COMPLIANT);
						result.addComment("Provided value for dcterms:modified '" + modified + "' is a validly formatted ISO date, but has a duration of more than one day, modified is expected to be an explicit date/time.");
					}
				}
				result.setResultState(ResultState.RUN_HAS_RESULT);
			} catch (Exception e) {
				logger.debug(e.getMessage());
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment(e.getMessage());
			}
		}
		return result;
	}


}
