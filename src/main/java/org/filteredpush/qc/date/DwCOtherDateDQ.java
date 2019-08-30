package org.filteredpush.qc.date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Consulted;
import org.datakurator.ffdq.annotations.Mechanism;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.Specification;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.model.ResultState;
import org.filteredpush.qc.date.EventResult.EventQCResultState;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * FFDQ tests for Date related concepts in Darwin Core outside of the Event class.
 * 
 * Provides Core TG2 tests: 
 *   TG2-VALIDATION_DATEIDENTIFIED_OUTOFRANGE
 *   TG2-VALIDATION_DATEIDENTIFIED_NOTSTANDARD
 *   
 * Also provides: 
 * 	 TG2-AMENDMENT_DATEIDENTIFIED_STANDARDIZED
 *   Date Modified Format Correction (supplemental)  
 *   ModifiedDateValid (supplemental)
 * 
 * @author mole
 *
 */
@Mechanism(
		value = "urn:uuid:bf5b7706-d0a6-4c65-9644-c750e7188ee0",
		label = "Kurator: Date Validator - DwCOtherDateDQ")
public class DwCOtherDateDQ {
	
	private static final Log logger = LogFactory.getLog(DwCOtherDateDQ.class);

	 /**
     * #69 Validation SingleRecord Conformance: dateidentified notstandard
     *
     * Provides: VALIDATION_DATEIDENTIFIED_NOTSTANDARD
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @return DQResponse the ComplianceValue
     */
    @Provides("66269bdd-9271-4e76-b25c-7ab81eebe1d8")
    public static DQResponse<ComplianceValue> validationDateidentifiedNotstandard(@ActedUpon("dwc:dateIdentified") String dateIdentified) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:dateIdentified 
        // is either not present or is EMPTY; COMPLIANT if the value 
        // of the field dwc:dateIdentified is a valid ISO 8601-1:2019 
        //date; otherwise NOT_COMPLIANT 

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
     * #76 Validation SingleRecord Likelihood: dateidentified outofrange
     *
     * Provides: VALIDATION_DATEIDENTIFIED_OUTOFRANGE
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @param eventDate the provided dwc:eventDate against which to evaluate the dwc:dateIdentified
     * @return DQResponse the ComplianceValue
     */
    @Provides("dc8aae4b-134f-4d75-8a71-c4186239178e")
    public static DQResponse<ComplianceValue> validationDateidentifiedOutofrange(
    		@ActedUpon("dwc:dateIdentified") String dateIdentified,
    		@Consulted("dwc:eventDate") String eventDate
    		) {
    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

    	// Parameters. This test is defined as parameterized.
    	// Default values: earliest date = 1753-01-01, latest date = current day
    	String earliestDate = "1753-01-01";
    	DateTime latestDate = new DateTime();
    	String latest = Integer.toString(latestDate.getYear()) + "-" + Integer.toString(latestDate.getMonthOfYear()) + "-" + Integer.toString(latestDate.getDayOfMonth());
    	Interval withinInterval = DateUtils.extractInterval(earliestDate + "/" + latest);

    	// Specification
    	// INTERNAL_PREREQUISITES_NOT_MET if the field dwc:dateIdentified is 
    	// EMPTY or is not a valid ISO 8601-1:2019 date, or if the field 
    	// dwc:eventDate is not EMPTY and is not a valid ISO 8601-1:2019 date; 
    	// COMPLIANT if the value of the field dwc:dateIdentified is not prior to 
    	// dwc:eventDate, and is within the Parameter range; 
    	// otherwise NOT_COMPLIANT

    	// TODO: In test specification need be explicit about ranges (typical case, eventDate is date, dateIdentified is year, in same year.
    	//    Current implementation assumes overlap is compliant, test specification not yet clear.

    	if (DateUtils.isEmpty(dateIdentified)) {
    		result.addComment("No value provided for dwc:dateIdentified.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else if (!DateUtils.eventDateValid(dateIdentified)) {
    		result.addComment("Value provided for dwc:dateIdentified ["+dateIdentified+"] is not a valid date.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else {
    		Interval identifiedInterval = DateUtils.extractInterval(dateIdentified);
    		if (!withinInterval.contains(identifiedInterval)) {
    			result.setValue(ComplianceValue.NOT_COMPLIANT);
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    			result.addComment("Provided value for dateIdentified [" + dateIdentified + "] starts extends beyond the limits ["+earliestDate +"]-["+latestDate.toString("yyyy-MM-dd") +"].");
    		} else if (DateUtils.isEmpty(eventDate)) {
    			result.addComment("No value provided for dwc:eventDate, unable to compare with dwc:dateIdentified.");
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		} else if (!DateUtils.eventDateValid(eventDate)) { 
    			result.addComment("Provided value for dwc:eventDate ["+eventDate+"] is not a valid date, unable to compare with dwc:dateIdentified.");
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		} else { 
    			Interval eventInterval = DateUtils.extractInterval(eventDate);
    			if (eventInterval==null) { 
    				result.addComment("Unable to extract date from provided value for dwc:eventDate ["+eventDate+"], unable to compare with dwc:dateIdentified.");
    				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			} else if (eventInterval.contains(identifiedInterval)) {
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dateIdentified [" + dateIdentified + "] falls within the eventDate ["+eventDate+"].");
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} else if (identifiedInterval.getStart().isAfter(eventInterval.getEnd())) {
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dateIdentified [" + dateIdentified + "] is after the end of the eventDate ["+eventDate+"].");;
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} else if (identifiedInterval.getStart().equals(eventInterval.getStart())) {
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dateIdentified [" + dateIdentified + "] starts at the same time as the eventDate ["+eventDate+"].");;
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} else if (identifiedInterval.getStart().isBefore(eventInterval.getStart())) { 
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for dateIdentified [" + dateIdentified + "] starts before the eventDate starts.");
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
	 * Given a dateIdentified, check to see if it is empty or contains a valid date value.  If it contains
	 * a value that is not a valid (ISO formatted) date, propose a properly formatted dateModified as an amendment.
	 * 
	 * TG2-AMENDMENT_DATEIDENTIFIED_STANDARDIZED
	 * 
	 * @param dateIdentified to check
	 * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
	 *    resultState is CHANGED if a new value is proposed.
	 * 
	 */
	//@Provides(value = "AMENDMENT_DATEIDENTIFIED_STANDARDIZED")
	@Provides(value = "urn:uuid:39bb2280-1215-447b-9221-fd13bc990641")
    @Amendment( label = "AMENDMENT_DATEIDENTIFIED_STANDARDIZED", description="The value of dwc:dateIdentified was amended to conform with the ISO 8601:2004(E) date format")
	@Specification(value = "Check dwc:dateIdentified to see if it is empty or contains a valid date value. If it contains a " +
	        "value that is not a valid date, propose a properly formatted dateIdentified as an amendment.")
	public static DQResponse<AmendmentValue> correctIdentifiedDateFormat(@ActedUpon(value = "dwc:dateIdentified") String dateIdentified) {
		DQResponse<AmendmentValue> result = new DQResponse<>();

		if (DateUtils.eventDateValid(dateIdentified)) {
			result.setResultState(ResultState.NO_CHANGE);
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

					result.setValue(new AmendmentValue(correctedValues));

					if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
						result.setResultState(ResultState.AMBIGUOUS);
						result.addComment(extractResponse.getComment());
					} else {
						if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
							result.addComment("Interpretation of dwc:dateIdentified [" + dateIdentified + "] is suspect.");
							result.addComment(extractResponse.getComment());
						}
						result.setResultState(ResultState.CHANGED);
					}
				} else {
					result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
					result.addComment("Unable to extract a date from " + dateIdentified);
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
	 *    resultState is CHANGED if a new value is proposed.
	 */
	@Provides(value = "urn:uuid:367bf43f-9cb6-45b2-b45f-b8152f1d334a")
	@Amendment(label = "Date Modified Format Correction", description = "Try to propose a correction for a date modified")
	@Specification(value = "Check dcterms:modified to see if it is empty or contains a valid date value. If it contains a " +
	        "value that is not a valid date, propose a properly formatted dcterms:modified as an amendment.")
	public static DQResponse<AmendmentValue> correctModifiedDateFormat(@ActedUpon(value = "dcterms:modified") String modified) {
		DQResponse<AmendmentValue> result = new DQResponse<>();

		if (DateUtils.eventDateValid(modified)) {
			result.setResultState(ResultState.NO_CHANGE);
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
						result.setResultState(ResultState.CHANGED);
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
