package org.filteredpush.qc.date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Mechanism;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.Specification;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.api.EnumDQAmendmentResultState;
import org.datakurator.ffdq.api.EnumDQResultState;
import org.datakurator.ffdq.api.EnumDQValidationResult;
import org.filteredpush.qc.date.EventResult.EventQCResultState;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

/**
 * FFDQ tests for Date related concepts in Darwin Core outside of the Event class.
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
	@Amendment(label = "Date Identified Standardized", description = "Try to propose a correction for a date identified")
	@Specification(value = "Check dwc:dateIdentified to see if it is empty or contains a valid date value. If it contains a " +
	        "value that is not a valid date, propose a properly formatted dateIdentified as an amendment.")
	public static EventDQAmendment correctIdentifiedDateFormat(@ActedUpon(value = "dwc:dateIdentified") String dateIdentified) {
	    EventDQAmendment result = new EventDQAmendment();
	    if (DateUtils.eventDateValid(dateIdentified)) {
	        result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
	        result.addComment("dwc:dateIdentified contains a correctly formatted date, not changing.");
	    } else {
	        if (DateUtils.isEmpty(dateIdentified)) {
	            result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
	                result.addResult("dwc:dateIdentified", extractResponse.getResult());
	                if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
	                    result.setResultState(EnumDQAmendmentResultState.AMBIGUOUS);
	                    result.addComment(extractResponse.getComment());
	                } else {
	                    if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
	                        result.addComment("Interpretation of dwc:dateIdentified [" + dateIdentified + "] is suspect.");
	                        result.addComment(extractResponse.getComment());
	                    }
	                    result.setResultState(EnumDQAmendmentResultState.CHANGED);
	                }
	            } else {
	                result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
	public static EventDQAmendment correctModifiedDateFormat(@ActedUpon(value = "dcterms:modified") String modified) {
	    EventDQAmendment result = new EventDQAmendment();
	    if (DateUtils.eventDateValid(modified)) {
	        result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
	        result.addComment("dcterms:modified contains a correctly formatted date, not changing.");
	    } else {
	        if (DateUtils.isEmpty(modified)) {
	            result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
	                result.addResult("dcterms:modified", extractResponse.getResult());
	                if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
	                    result.setResultState(EnumDQAmendmentResultState.AMBIGUOUS);
	                    result.addComment(extractResponse.getComment());
	                } else {
	                    if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
	                        result.addComment("Interpretation of dcterms:modified [" + modified + "] is suspect.");
	                        result.addComment(extractResponse.getComment());
	                    }
	                    result.setResultState(EnumDQAmendmentResultState.CHANGED);
	                }
	            } else {
	                result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
	public static EventDQValidation isModifiedDateValid(@ActedUpon(value = "dcterms:modified") String modified) {
		EventDQValidation result = new EventDQValidation();
		if (DateUtils.isEmpty(modified)) {
			result.addComment("No value provided for dcterms:modified.");
			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
		} else { 
			try { 
		        if (DateUtils.eventDateValid(modified) && DateUtils.specificToDay(modified)) {
					result.setResult(EnumDQValidationResult.COMPLIANT);
					result.addComment("Provided value for dcterms:modified '" + modified + "' is formated as an ISO date that can be parsed to an explicit date/time ");
				} else { 
		            if (!DateUtils.eventDateValid(modified)) { 
					    result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
					    result.addComment("Provided value for dcterms:modified '" + modified + "' is not a validly formatted ISO date .");
	                } else { 
					    result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
					    result.addComment("Provided value for dcterms:modified '" + modified + "' is a validly formatted ISO date, but has a duration of more than one day, modified is expected to be an explicit date/time.");
	                }
				}
				result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
				result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment(e.getMessage());
			}
		}
		return result;
	}
	
    //TG2-VALIDATION_DATEIDENTIFIED_OUTOFRANGE
	@Provides(value="urn:uuid:dc8aae4b-134f-4d75-8a71-c4186239178e")
    public static EventDQValidation isDateIdentifiedInRange(@ActedUpon(value = "dwc:dateIdentified") String dateIdentified, Integer lowerBound, Boolean useLowerBound) {
    	EventDQValidation result = new EventDQValidation();
    	// TODO: Implementation may be too tightly bound to year, may need to extract first/last day for finer granularity test
    	// TODO: Implementation is the same as isEventDateInRange, this may or may not need to be changed.
    	if (lowerBound==null) { 
    		lowerBound = 1700;
    	}
    	if (useLowerBound==null) { 
    		useLowerBound = true;
    	}
    	Integer upperBound = LocalDateTime.now().getYear();
    	if (DateUtils.isEmpty(dateIdentified)) {
    		result.addComment("No value provided for dwc:dateIdentified.");
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		if (! DateUtils.eventDateValid(dateIdentified)) { 
    			result.addComment("Value provided for dwc:dateIdentified ["+dateIdentified+"] not recognized as a valid date.");
    			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		} else { 
    			int startYear = 0;
    			Interval interval = DateUtils.extractInterval(dateIdentified);
    			if (DateUtils.isRange(dateIdentified)) {
    				int endYear = interval.getEnd().getYear();
    				startYear = interval.getStart().getYear();
    				if (useLowerBound) { 
    					if (endYear<lowerBound|| startYear>upperBound) { 
    						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is not a range spanning part of the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is a range spanning at least part of " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					}    				
    				} else { 
    					if (startYear>upperBound) { 
    						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is not a range spanning part of the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is a range spanning at least part of " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					}       					
    				}
    			} else {
    				startYear = interval.getStart().getYear();
    				if (useLowerBound) { 
    					if (startYear<lowerBound || startYear>upperBound) { 
    						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' does not have a year in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' does not have a year in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} 
    				} else { 
    					if (startYear>upperBound) { 
    						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is not after  " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is after " + upperBound.toString() + " (current year).");
    					}     					
    				}
    			}
    			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		}
    	}
    	return result;
    }		
	
    //TG2-VALIDATION_DATEIDENTIFIED_NOTSTANDARD
    @Provides(value="urn:uuid:66269bdd-9271-4e76-b25c-7ab81eebe1d8")
	public static EventDQValidation isDateIdentifiedValid(@ActedUpon(value = "dwc:dateIdentified") String dateIdentified) {
		EventDQValidation result = new EventDQValidation();
		if (DateUtils.isEmpty(dateIdentified)) {
			result.addComment("No value provided for dwc:dateIdentified.");
			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
		} else { 
			try { 
		        if (DateUtils.eventDateValid(dateIdentified)) {
					result.setResult(EnumDQValidationResult.COMPLIANT);
					result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is formated as a valid ISO date. ");
				} else { 
					result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
					result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is not a validly formatted ISO date .");
				}
				result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
				result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment(e.getMessage());
			}
		}
		return result;
	}
	
    //TG2-VALIDATION_DATEIDENTIFIED_PREEVENTDATE	
    @Provides(value="urn:uuid:391ca46d-3842-4a18-970c-0434cbc17f07")
	public static EventDQValidation isDateIdentifiedPreEventDate(@ActedUpon(value = "dwc:dateIdentified") String dateIdentified,  @ActedUpon(value="dwc:eventDate")String eventDate) {
		EventDQValidation result = new EventDQValidation();
		if (DateUtils.isEmpty(dateIdentified) || DateUtils.isEmpty(eventDate)) {
			result.addComment("No value provided for one or both of dwc:dateIdentified or dwc:eventDate.");
			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
		} else { 
			try { 
		        if (DateUtils.eventDateValid(dateIdentified)) {
			        if (DateUtils.eventDateValid(eventDate)) {
			        	// Interval.isBefore() tests if one interval is entirely before another interval.
			        	if (!DateUtils.extractInterval(dateIdentified).isBefore(DateUtils.extractInterval(eventDate))) { 
			        		result.setResult(EnumDQValidationResult.COMPLIANT);
			        		result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is not entirely before dwc:eventDate provided ["+eventDate+"] . ");
			        	} else {
			        		result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
			        		result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is entirely before dwc:eventDate provided ["+eventDate+"]");
			        	}
					} else { 
						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is not a validly formatted ISO date .");
					}
				} else { 
					result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
					result.addComment("Provided value for dwc:dateIdentified '" + dateIdentified + "' is not a validly formatted ISO date .");
				}
				result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
				result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment(e.getMessage());
			}
		}
		return result;
	}
}
