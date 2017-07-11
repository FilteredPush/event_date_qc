/** DwCEventDQ.java
 * 
 * Copyright 2016 President and Fellows of Harvard College
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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.EnumDQResultState;
import org.datakurator.ffdq.api.EnumDQValidationResult;
import org.datakurator.ffdq.api.EnumDQAmendmentResultState;
import org.joda.time.Interval;


/**
 * Darwin Core Event eventDate related Data Quality Measures, Validations, and Enhancements. 
 * 
 *  Provides support for the following draft TDWG DQIG TG2 validations and amendments.  
 *  
 *  DAY_MONTH_TRANSPOSED  dayMonthTransposition(@ActedUpon(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) 
 *  DAY_MONTH_YEAR_FILLED_IN
 *  EVENTDATE_FILLED_IN_FROM_VERBATIM extractDateFromVerbatim(@ActedUpon(value = "dwc:eventDate") String eventDate, @Consulted(value = "dwc:verbatimEventDate") String verbatimEventDate)
 *  START_ENDDAYOFYEAR_FILLED_IN
 *
 *  EVENT_DATE_DURATION_SECONDS  measureDurationSeconds(@ActedUpon(value = "dwc:eventDate") String eventDate)
 *  DAY_IS_FIRST_OF_CENTURY
 *  DAY_IS_FIRST_OF_YEAR
 *  
 *  DAY_IN_RANGE  isDayInRange(@ActedUpon(value = "dwc:day") String day)   
 *  MONTH_IN_RANGE  isMonthInRange(@ActedUpon(value = "dwc:month") String month) 
 *  DAY_POSSIBLE_FOR_MONTH_YEAR  isDayPossibleForMonthYear(@Consulted(value="dwc:year") String year, @Consulted(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) 
 *  EVENTDATE_CONSISTENT_WITH_DAY_MONTH_YEAR  
 *  EVENTDATE_IN_PAST
 *  EVENTDATE_PRECISON_MONTH_OR_BETTER 
 *  EVENTDATE_PRECISON_YEAR_OR_BETTER isEventDateYearOrLess(@ActedUpon(value="dwc:eventDate") String eventDate) 
 *                               also isEventDateJulianYearOrLess(@ActedUpon(value="dwc:eventDate") String eventDate) 
 *  STARTDATE_CONSISTENT_WITH_ENDDATE
 *  YEAR_PROVIDED
 *  EVENTDATE_CONSISTENT_WITH_ATOMIC_PARTS isConsistent(String eventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day)
 *  
 *  Also provides (intended to prepare upstream data for Darwin Core: 
 *  UPSTREAM_EVENTDATE_FILLED_IN_FROM_START_END  extractDateFromStartEnd(@ActedUpon(value = "dwc:eventDate") String eventDate, @Consulted(value = "startDate") String startDate, @Consulted(value="endDate") String endDate) 
 * 
 * @author mole
 *
 */
@Mechanism("Kurator: Date Validator - DwCEventDQ")
public class DwCEventDQ {
	
	private static final Log logger = LogFactory.getLog(DwCEventDQ.class);
	
	/**
	 * Measure the duration of an event date in seconds.
	 * 
	 * @param eventDate to measure duration in seconds
	 * @return EventDQMeasurement object, which if state is COMPLETE has a value of type Long.
	 */
    @Provides(value = "EVENT_DATE_DURATION_SECONDS")
	@Measure(label = "Event Date Duration In Seconds", description = "Measure the duration of an event date in seconds.")
	@Specification(value = "For values of dwc:eventDate, calculate the duration in seconds.")
	public static EventDQMeasurement<Long> measureDurationSeconds(@ActedUpon(value = "eventDate") String eventDate) {
		EventDQMeasurement<Long> result = new EventDQMeasurement<Long>();
    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for eventDate.");
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		try { 
    			long seconds = DateUtils.measureDurationSeconds(eventDate);
    			result.setValue(new Long(seconds));
    			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		} catch (Exception e) { 
    			logger.debug(e.getMessage());
    			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment(e.getMessage());
    		}
    	}
    	return result;
	}

	@Provides(value = "EVENT_DATE_COMPLETENESS")
	@Measure(label = "Event Date Completeness", description = "Measure the completeness of an event date.")
	@Specification(value = "For values of dwc:eventDate, check is not empty.")
	public static EventDQMeasurement<Long> measureCompleteness(@ActedUpon(value = "eventDate") String eventDate) {
		EventDQMeasurement<Long> result = new EventDQMeasurement<Long>();
		if (!DateUtils.isEmpty(eventDate)) {
			result.addComment("No value provided for eventDate.");
			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
		}

		return result;
	}
	
    /**
     * If a dwc:eventDate is empty and the verbatimEventDate is not empty, try to populate the 
     * eventDate from the verbatim value.
     * 
     * @param eventDate to check for emptyness
     * @param verbatimEventDate to try to replace a non-empty event date.
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     */
    @Provides(value = "EVENTDATE_CONSISTENT_WITH_VERBATIM")
	@Validation(label = "Event Date and Verbatim Consistent", description = "Test to see if the eventDate and verbatimEventDate are consistent.")
	@Specification(value = "If a dwc:eventDate is not empty and the verbatimEventDate is not empty compare the value " +
			"of dwc:eventDate with that of dwc:verbatimEventDate, and assert Compliant if the two represent the same data or date range.")
    public static EventDQValidation eventDateConsistentWithVerbatim(@ActedUpon(value = "eventDate") String eventDate, @ActedUpon(value = "verbatimEventDate") String verbatimEventDate) {
    	EventDQValidation result = new EventDQValidation();
    	if (!DateUtils.isEmpty(eventDate) && !DateUtils.isEmpty(verbatimEventDate)) {
		    EventResult extractResponse = DateUtils.extractDateFromVerbatimER(verbatimEventDate);
		    if (!extractResponse.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
		    		(extractResponse.getResultState().equals(EventResult.EventQCResultState.RANGE) || 
		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.DATE) ||
		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS) ||
		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)
		    	     ) 
		    	) 
		    { 
		    	if (DateUtils.eventsAreSameInterval(eventDate, extractResponse.getResult())) { 
					result.setResult(EnumDQValidationResult.COMPLIANT);
    				result.addComment("Provided value for eventDate '" + eventDate + "' represents the same range as verbatimEventDate '"+verbatimEventDate+"'.");
		    	} else { 
					result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    				result.addComment("Provided value for eventDate '" + eventDate + "' does not represent the same range as verbatimEventDate '"+verbatimEventDate+"'.");
		    	}
		    } else { 
		        result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
		        result.addComment("Unable to extract a date from verbatimEventDate: " + verbatimEventDate);
		    }    		
    	} else {
    		if (DateUtils.isEmpty(eventDate)) { 
    		    result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		    result.addComment("eventDate does not contains a value.");
    		}   			
    		if (DateUtils.isEmpty(verbatimEventDate)) { 
    		    result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		    result.addComment("verbatimEventDate does not contains a value.");
    		}   			
    	}

    	return result;
    }	

    /**
     * If a dwc:eventDate is empty and the verbatimEventDate is not empty, try to populate the 
     * eventDate from the verbatim value.
     * 
     * @param eventDate to check for emptyness
     * @param verbatimEventDate to try to replace a non-empty event date.
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     */
    @Provides(value = "EVENTDATE_FILLED_IN_FROM_VERBATIM")
	@Amendment(label = "Event Date From Verbatim", description = "Try to populate the event date from the verbatim value.")
	@Specification(value = "If a dwc:eventDate is empty and the verbatimEventDate is not empty fill in dwc:eventDate " +
			"based on value from dwc:verbatimEventDate")
    public static EventDQAmendment extractDateFromVerbatim(@ActedUpon(value = "eventDate") String eventDate, @Consulted(value = "verbatimEventDate") String verbatimEventDate) {
    	EventDQAmendment result = new EventDQAmendment();
    	if (DateUtils.isEmpty(eventDate)) { 
    		if (!DateUtils.isEmpty(verbatimEventDate)) { 
    		    EventResult extractResponse = DateUtils.extractDateFromVerbatimER(verbatimEventDate);
    		    if (!extractResponse.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
    		    		(extractResponse.getResultState().equals(EventResult.EventQCResultState.RANGE) || 
    		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.DATE) ||
    		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS) ||
    		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)
    		    	     ) 
    		    	) 
    		    { 
    		        result.addResult("dwc:eventDate", extractResponse.getResult());
    		        if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
    		        	result.setResultState(EnumDQAmendmentResultState.AMBIGUOUS);
    		        	result.addComment(extractResponse.getComment());
    		        } else { 
    		        	if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
    		        		result.addComment("Interpretation of verbatimEventDate [" + verbatimEventDate + "] is suspect.");
    		        		result.addComment(extractResponse.getComment());
    		        	}
    		        	result.setResultState(EnumDQAmendmentResultState.CHANGED);
    		        }
    		    } else { 
    		        result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		        result.addComment("Unable to extract a date from " + verbatimEventDate);
    		    }
    		} else { 
    		    result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		    result.addComment("verbatimEventDate does not contains a value.");
    		}
    	} else { 
    		result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
    		result.addComment("eventDate contains a value, not changing.");
    	}
    	return result;
    }
    

    /**
     * If a dwc:eventDate is empty and the verbatimEventDate, day, month, year, startDayOfYear, etc are not empty, try to populate the 
     * eventDate from the verbatim and other atomic values.
     * 
     * @param eventDate to check for emptyness
     * @param verbatimEventDate to try to replace a non-empty event date.
     * @param startDayOfYear to try to replace a non-empty event date.
     * @param endDayOfYear to try to replace a non-empty event date.
     * @param year to try to replace a non-empty event date.
     * @param month to try to replace a non-empty event date.
     * @param day to try to replace a non-empty event date.
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     */
    @Provides(value = "urn:uuid:6d0a0c10-5e4a-4759-b448-88932f399812")
	@Amendment(label = "Event Date From Parts", description = "Try to populate the event date from the verbatim and other atomic parts (day, month, year, etc).")
	@Specification(value = "If a dwc:eventDate is empty and the verbatimEventDate is not empty fill in dwc:eventDate " +
			"based on value from dwc:verbatimEventDate, dwc:year dwc:month, dwc:day, dwc:start/endDayOfYear.")
    public static EventDQAmendment extractDateFromParts(@ActedUpon(value = "eventDate") String eventDate, 
    		 @Consulted(value = "verbatimEventDate") String verbatimEventDate,
    		 @Consulted(value = "startDayOfYear") String startDayOfYear, 
    		 @Consulted(value = "endDayOfYear") String endDayOfYear, 
    		 @Consulted(value = "year") String year, 
    		 @Consulted(value = "month") String month, 
    		 @Consulted(value = "day") String day
    		 ) {
    	EventDQAmendment result = new EventDQAmendment();
    	if (DateUtils.isEmpty(eventDate)) { 
    		if (!DateUtils.isEmpty(verbatimEventDate)) { 
    		    String createdDate = DateUtils.createEventDateFromParts(verbatimEventDate, startDayOfYear, endDayOfYear, year, month, day);
    		    EventResult extractResponse = DateUtils.extractDateFromVerbatimER(createdDate);
    		    if (!extractResponse.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
    		    		(extractResponse.getResultState().equals(EventResult.EventQCResultState.RANGE) || 
    		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.DATE) ||
    		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS) ||
    		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)
    		    	     ) 
    		    	) 
    		    { 
    		        result.addResult("dwc:eventDate", extractResponse.getResult());
    		        if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
    		        	result.setResultState(EnumDQAmendmentResultState.AMBIGUOUS);
    		        	result.addComment(extractResponse.getComment());
    		        } else { 
    		        	if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
    		        		result.addComment("Interpretation of verbatimEventDate [" + verbatimEventDate + "] is suspect.");
    		        		result.addComment(extractResponse.getComment());
    		        	}
    		        	result.setResultState(EnumDQAmendmentResultState.CHANGED);
    		        }
    		    } else { 
    		        result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		        result.addComment("Unable to extract a date from " + verbatimEventDate);
    		    }
    		} else { 
    		    result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		    result.addComment("verbatimEventDate does not contains a value.");
    		}
    	} else { 
    		result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
    		result.addComment("eventDate contains a value, not changing.");
    	}
    	return result;
    }

    @Provides(value = "UPSTREAM_EVENTDATE_FILLED_IN_FROM_START_END")
	@Amendment(label = "Event Date From non-Darwin Core start/end", description = "Try to populate the event date from non-Darwin Core start date and end date terms.")
	@Specification(value = "If a dwc:eventDate is empty and an event date can be inferred from start date and end date, fill in dwc:eventDate " +
			"based on the values in the start and end dates.  Will not propose a change if dwc:eventDate contains a value.")
    public static EventDQAmendment extractDateFromStartEnd(@ActedUpon(value = "eventDate") String eventDate, @Consulted(value = "startDate") String startDate, @Consulted(value="endDate") String endDate) {
    	EventDQAmendment result = new EventDQAmendment();
    	if (DateUtils.isEmpty(eventDate)) { 
    		String response = DateUtils.createEventDateFromStartEnd(startDate, endDate);
    		if (DateUtils.isEmpty(response)) { 
    		    result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		    result.addComment("Unable to extract a date from " + startDate + " and " + endDate);
    		} else { 
    		    result.addResult("dwc:eventDate", response);
    		    result.setResultState(EnumDQAmendmentResultState.CHANGED);
    		}
    	} else { 
    		result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
    		result.addComment("eventDate contains a value, not changing.");
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
    				result.addComment("Provided value for dcterms:modified '" + modified + "' is formated as an ISO date tha can be parsed to an explicit date/time ");
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


    /**
     * Given an event date, check to see if it is empty or contains a valid date value.  If it contains
     * a value that is not a valid date, propose a properly formatted eventDate as an amendment.
     * 
     * @param eventDate to check
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     */
    @Provides(value = "EVENTDATE_FORMAT_CORRECTION")
	@Amendment(label = "Event Date Format Correction", description = "Try to propose a correction for an event date")
	@Specification(value = "Check dwc:eventDate to see if it is empty or contains a valid date value. If it contains a " +
			"value that is not a valid date, propose a properly formatted eventDate as an amendment.")
    public static EventDQAmendment correctEventDateFormat(@ActedUpon(value = "eventDate") String eventDate) {
    	EventDQAmendment result = new EventDQAmendment();
    	if (DateUtils.eventDateValid(eventDate)) {
    		result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
    		result.addComment("eventDate contains a correctly formatted date, not changing.");
    	} else {
    		if (DateUtils.isEmpty(eventDate)) {
    		    result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		    result.addComment("eventDate does not contains a value.");
    		} else { 
    		    EventResult extractResponse = DateUtils.extractDateFromVerbatimER(eventDate);
    		    if (!extractResponse.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
    		    		(extractResponse.getResultState().equals(EventResult.EventQCResultState.RANGE) || 
    		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.DATE) ||
    		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS) ||
    		    	      extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)
    		    	     ) 
    		    	) 
    		    { 
    		        result.addResult("dwc:eventDate", extractResponse.getResult());
    		        if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
    		        	result.setResultState(EnumDQAmendmentResultState.AMBIGUOUS);
    		        	result.addComment(extractResponse.getComment());
    		        } else { 
    		        	if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
    		        		result.addComment("Interpretation of eventDate [" + eventDate + "] is suspect.");
    		        		result.addComment(extractResponse.getComment());
    		        	}
    		        	result.setResultState(EnumDQAmendmentResultState.CHANGED);
    		        }
    		    } else { 
    		        result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		        result.addComment("Unable to extract a date from " + eventDate);
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
     * Given a dateIdentified, check to see if it is empty or contains a valid date value.  If it contains
     * a value that is not a valid (ISO formatted) date, propose a properly formatted dateModified as an amendment.
     * 
     * @param dateIdentified to check
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     */
    @Provides(value = "DATEIDENTIFIED_FORMAT_AMENDED")
    @Amendment(label = "Date Identified Format Correction", description = "Try to propose a correction for a date identified")
    @Specification(value = "Check dwc:dateIdentified to see if it is empty or contains a valid date value. If it contains a " +
            "value that is not a valid date, propose a properly formatted dateIdentified as an amendment.")
    public static EventDQAmendment correctIdentifiedDateFormat(@ActedUpon(value = "dateIdentified") String dateIdentified) {
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
     * Test to see whether a provided day is an integer in the range of values that can be 
     * a day of a month.
     * 
     * Provides: DAY_IN_RANGE
     * 
     * @param day  a string to test
     * @return COMPLIANT if day is an integer in the range 1 to 31 inclusive, NOT_COMPLIANT if day is 
     *     an integer outside this range, INTERNAL_PREREQUSISITES_NOT_MET if day is empty or an integer
     *     cannot be parsed from day. 
     */
    //@Provides(value = "DAY_IN_RANGE")
    @Provides(value = "urn:uuid:48aa7d66-36d1-4662-a503-df170f11b03f")   // GUID for DAY_INVALID/DAY_IN_RANGE
	@Validation(label = "Day In Range", description = "Test to see whether a provided day is an integer in the range " +
			"of values that can be a day of a month.")
	@Specification(value = "Compliant if dwc:day is an integer in the range 1 to 31 inclusive, not compliant otherwise. " +
			"Internal prerequisites not met if day is empty or an integer cannot be parsed from day.")
    public static EventDQValidation isDayInRange(@ActedUpon(value = "day") String day) {
    	EventDQValidation result = new EventDQValidation();
    	if (DateUtils.isEmpty(day)) {
    		result.addComment("No value provided for day.");
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		try { 
    			int numericDay = Integer.parseInt(day.trim());
    			if (DateUtils.isDayInRange(numericDay)) { 
    				result.setResult(EnumDQValidationResult.COMPLIANT);
    				result.addComment("Provided value for day '" + day + "' is an integer in the range 1 to 31.");
    			} else { 
    				result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    				result.addComment("Provided value for day '" + day + "' is not an integer in the range 1 to 31.");
    			}
    			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		} catch (NumberFormatException e) { 
    			logger.debug(e.getMessage());
    			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment(e.getMessage());
    		}
    	}
    	return result;
    }	
    
    /**
     * Test to see whether a provided month is in the range of integer values that form months of the year.
     * 
     * Provides: MONTH_IN_RANGE
     * 
     * @param month  a string to test
     * @return COMPLIANT if month is an integer in the range 1 to 12 inclusive, NOT_COMPLIANT if month is 
     *     an integer outside this range, INTERNAL_PREREQUSISITES_NOT_MET if month is empty or an integer
     *     cannot be parsed from month. 
     */
    // @Provides(value = "MONTH_IN_RANGE")
    @Provides(value = "urn:uuid:01c6dafa-0886-4b7e-9881-2c3018c98bdc")  // MONTH_INVALID/MONTH_IN_RANGE
	@Validation(label = "Month In Range", description = "Test to see whether a provided month is in the range of " +
			"integer values that form months of the year.")
	@Specification(value = "Compliant if month is an integer in the range 1 to 12 inclusive, otherwise not compliant. " +
			"Internal prerequisites not met if month is empty or an integer cannot be parsed from month.")
    public static EventDQValidation isMonthInRange(@ActedUpon(value="month") String month) {
    	EventDQValidation result = new EventDQValidation();
    	if (DateUtils.isEmpty(month)) {
    		result.addComment("No value provided for month.");
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		try { 
    			int numericMonth = Integer.parseInt(month.trim());
    			if (DateUtils.isMonthInRange(numericMonth)) { 
    				result.setResult(EnumDQValidationResult.COMPLIANT);
    				result.addComment("Provided value for month '" + month + "' is an integer in the range 1 to 12.");
    			} else { 
    				result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    				result.addComment("Provided value for month '" + month + "' is not an integer in the range 1 to 12.");
    			}
    			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		} catch (NumberFormatException e) { 
    			logger.debug(e.getMessage());
    			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment(e.getMessage());
    		}
    	}
    	return result;
    }    
    
    @Provides(value = "EVENTDATE_PRECISON_JULIAN_YEAR_OR_BETTER")
	@Validation(label = "EventDate precision Julian year or better. ", description = "Test to see whether a provided event date " +
			"has a duration less than or equal to a standard astronomical Julian year.")
	@Specification(value = "Compliant if event date has a duration equal to or less than a = 31557600 seconds, otherwise not compliant. " +
			"Internal prerequisites not met if eventDate is empty or not valid.")
    public static EventDQValidation isEventDateJulianYearOrLess(@ActedUpon(value="eventDate") String eventDate) {
    	EventDQValidation result = new EventDQValidation();
    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for eventDate.");
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		if (DateUtils.eventDateValid(eventDate)) { 
    			logger.debug(eventDate);
    			logger.debug(DateUtils.measureDurationSeconds(eventDate));
    			if (DateUtils.measureDurationSeconds(eventDate)<= 31557600) { 
    				result.setResult(EnumDQValidationResult.COMPLIANT);
    				result.addComment("Provided value for eventDate '" + eventDate + "' has a duration less than or equal to one Julian year of 365.25 days.");
    			}  else { 
    				result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    				result.addComment("Provided value for eventDate '" + eventDate + "' has a duration more than one Julian year of 365.25 days.");
    			}
    			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		} else { 
    			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("provided dwc:eventDate not recognized as a valid date value.");
    		}
    	}
    	return result;
    }      
    
    
    @Provides(value = "EVENTDATE_PRECISON_YEAR_OR_BETTER")
	@Validation(label = "EventDate precision calendar year or better. ", description = "Test to see whether a provided event date " +
			"has a duration less than or equal to a calendar year.")
	@Specification(value = "Compliant if event date has a duration equal to or less than 365 days if a standard year, 366 days if a leap year. " +
			"Internal prerequisites not met if eventDate is empty or not valid.")
    public static EventDQValidation isEventDateYearOrLess(@ActedUpon(value="eventDate") String eventDate) {
    	EventDQValidation result = new EventDQValidation();
    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for eventDate.");
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		if (DateUtils.includesLeapDay(eventDate)) { 
    			if (DateUtils.eventDateValid(eventDate)) { 
    				if (DateUtils.measureDurationSeconds(eventDate)<= 31622400) { 
    					result.setResult(EnumDQValidationResult.COMPLIANT);
    					result.addComment("Provided value for eventDate '" + eventDate + "' contains a leap day and has a duration less than or equal to one calendar year of 366 days.");
    				}  else { 
    					result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    					result.addComment("Provided value for eventDate '" + eventDate + "' contains a leap day has a duration more than one calendar year of 366 days.");
    				}
    				result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    			} else { 
    				result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("provided dwc:eventDate not recognized as a valid date value.");
    			}
    		} else { 
    			if (DateUtils.eventDateValid(eventDate)) { 
    				if (DateUtils.measureDurationSeconds(eventDate)<= 31536000) { 
    					result.setResult(EnumDQValidationResult.COMPLIANT);
    					result.addComment("Provided value for eventDate '" + eventDate + "' does not contain a leap day and has a duration less than or equal to one calendar year of 365 days.");
    				}  else { 
    					result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    					result.addComment("Provided value for eventDate '" + eventDate + "' does not contain a leap day has a duration more than one calendar year of 365 days.");
    				}
    				result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    			} else { 
    				result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("provided dwc:eventDate not recognized as a valid date value.");
    			}
    		}
    	}
    	return result;
    }     

    /**
     * Check if a value for day is consistent with a provided month and year. 
     * 
     * Provides: DAY_POSSIBLE_FOR_MONTH_YEAR  
     * 
     * @param year for month and day
     * @param month for day
     * @param day to check 
     * @return an DQValidationResponse object describing whether day exists in year-month-day.
     */
    @Provides(value = "DAY_POSSIBLE_FOR_MONTH_YEAR")
	@Validation(label = "Day Consistent With Month/Year", description = "Check if a value for day is consistent with a " +
			"provided month and year.")
	@Specification("Check that the value of dwc:eventDate is consistent with the values for dwc:month and dwc:year. " +
			"Requires valid values for month and year.")
    public static EventDQValidation isDayPossibleForMonthYear(@Consulted(value="year") String year, @Consulted(value="month") String month, @ActedUpon(value="day") String day) {
    	EventDQValidation result = new EventDQValidation();
    	
    	EventDQValidation monthResult =  isMonthInRange(month);
    	EventDQValidation dayResult =  isDayInRange(day);
    	
    	if (monthResult.getResultState().equals(EnumDQResultState.RUN_HAS_RESULT)) {
    		if (monthResult.getResult().equals(EnumDQValidationResult.COMPLIANT)) { 
    	        if (dayResult.getResultState().equals(EnumDQResultState.RUN_HAS_RESULT)) { 
    	        	if (dayResult.getResult().equals(EnumDQValidationResult.COMPLIANT)) {
    	        		try { 
    	        		    Integer numericYear = Integer.parseInt(year);
    	        		    String date = String.format("%04d", numericYear) + "-" + month.trim() + "-" + day.trim();

    	        	    	if (DateUtils.eventDateValid(date)) { 
    	        	    		result.setResult(EnumDQValidationResult.COMPLIANT);
    	        	    		result.addComment("Provided value for year-month-day " + date + " parses to a valid day.");;
    	        	    	} else { 
    	        	    		result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    	        	    		result.addComment("Provided value for year-month-day " + date + " does not parse to a valid day.");;
    	        	    	}
    	        		    result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    	        		} catch (NumberFormatException e) { 
    	        			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	        		    result.addComment("Unable to parse integer from provided value for year " + year + " " + e.getMessage());;
    	        		}
    	        	} else { 
    	        		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	        		result.addComment("Provided value for day " + day + " is outside the range 1-31.");;
    	        	}
    	        } else { 
    	        	result.setResultState(dayResult.getResultState());
    	        	result.addComment(dayResult.getComment());
    	        }
    		} else { 
    			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("Provided value for month " + month + " is outside the range 1-12.");;
    		}
    	} else { 
    		result.setResultState(monthResult.getResultState());
    		result.addComment(monthResult.getComment());
    	}
    	
    	return result;
    }    
    
    /**
     * Check of month is out of range for months, but day is in range for months, and
     * propose a transposition of the two if this is the case.
     * 
     * Provides: DAY_MONTH_TRANSPOSED
     * 
     * @param month the value of dwc:month
     * @param day  the value of dwc:day
     * @return an EventDQAmmendment which may contain a proposed ammendment.
     */
    @Provides(value = "DAY_MONTH_TRANSPOSED")
	@Amendment(label = "Day Month Transposition", description = "Check of month is out of range for months, but day is " +
			"in range for months, and propose a transposition of the two if this is the case.")
	@Specification("If dwc:month and dwc:day are provided, propose a transposition if day is in range for months, and " +
			"month is in range for days")
    public static final EventDQAmendment dayMonthTransposition(@ActedUpon(value="month") String month, @ActedUpon(value="day") String day) {
    	EventDQAmendment result = new EventDQAmendment();
    	if (DateUtils.isEmpty(day) || DateUtils.isEmpty(month)) { 
    		result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("Either month or day was not provided.");
    	} else { 
        	EventDQValidation monthResult =  isMonthInRange(month);
        	EventDQValidation dayResult =  isDayInRange(day);
        	if (monthResult.getResultState().equals(EnumDQResultState.RUN_HAS_RESULT)) {
        		if (monthResult.getResult().equals(EnumDQValidationResult.NOT_COMPLIANT)) { 
        			// month is integer, but out of range
        	        if (dayResult.getResultState().equals(EnumDQResultState.RUN_HAS_RESULT)) { 
        	        	// day is also integer
        	        	int dayNumeric = Integer.parseInt(day);
        	        	int monthNumeric = Integer.parseInt(month);
        	        	if (DateUtils.isDayInRange(monthNumeric) && DateUtils.isMonthInRange(dayNumeric)) { 
        	        		// day is in range for months, and month is in range for days, so transpose.
        	        	    result.addResult("dwc:month", day);
        	        	    result.addResult("dwc:day", month);
        	        	    result.setResultState(EnumDQAmendmentResultState.TRANSPOSED);
        	        	} else { 
        	        	    result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	        	}
        	        } else { 
    		            result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		            result.addComment("dwc:day " + dayResult.getResultState() + ". " + dayResult.getComment());
        	        }
        		} else { 
        	        if (dayResult.getResultState().equals(EnumDQResultState.RUN_HAS_RESULT) &&
        	            dayResult.getResult().equals(EnumDQValidationResult.COMPLIANT)) { 
        			    // month is in range for months, so don't try to change.
        	            result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
        	        } else { 
    		            result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	        }
        		}
        	} else {
    		   result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		   result.addComment("dwc:month " + monthResult.getResultState() + ". " + monthResult.getComment());
        	}
    	}
    	return result;
    }    
    
}
