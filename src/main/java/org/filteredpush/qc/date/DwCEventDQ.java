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
 *  Not implemented: 
 *  TG2-AMENDMENT_YEAR_STANDARDIZED  Unclear what to do with this one.
 * 
 * @author mole
 *
 */
@Mechanism(
		value = "urn:uuid:b844059f-87cf-4c31-b4d7-9a52003eef84",
		label = "Kurator: Date Validator - DwCEventDQ")
public class DwCEventDQ {
	
	private static final Log logger = LogFactory.getLog(DwCEventDQ.class);
	
	/**
	 * Measure the duration of an event date in seconds.
	 * 
	 * @param eventDate to measure duration in seconds
	 * @return EventDQMeasurement object, which if state is COMPLETE has a value of type Long.
	 */
    //@Provides(value = "EVENT_DATE_DURATION_SECONDS")
	@Provides(value = "urn:uuid:b0753f69-08c1-45f5-a5ca-48d24e76d813")
	@Measure(dimension = Dimension.PRECISION, label = "Event Date Duration In Seconds", description = "Measure the duration of an event date in seconds.")
	@Specification(value = "For values of dwc:eventDate, calculate the duration in seconds.")
	public static EventDQMeasurement<Long> measureDurationSeconds(@ActedUpon(value = "dwc:eventDate") String eventDate) {
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

	//@Provides(value = "EVENT_DATE_COMPLETENESS")
	@Provides(value = "urn:uuid:0a59e03f-ebb5-4df3-a802-2e444de525b5")
	@Measure(dimension = Dimension.COMPLETENESS, label = "Event Date Completeness", description = "Measure the completeness of an event date.")
	@Specification(value = "For values of dwc:eventDate, check is not empty.")
	public static EventDQMeasurement<Long> measureCompleteness(@ActedUpon(value = "dwc:eventDate") String eventDate) {
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

	@Provides("urn:uuid:da63f836-1fc6-4e96-a612-fa76678cfd6a")

	@Validation(
			label = "Event Date and Verbatim Consistent",
			description = "Test to see if the eventDate and verbatimEventDate are consistent.")

	@Specification("If a dwc:eventDate is not empty and the verbatimEventDate is not empty " +
			       "compare the value of dwc:eventDate with that of dwc:verbatimEventDate, " +
			       "and assert Compliant if the two represent the same date or date range.")

    public static EventDQValidation eventDateConsistentWithVerbatim(
    		@ActedUpon(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:verbatimEventDate") String verbatimEventDate) {

		EventDQValidation result = new EventDQValidation();

		// Actor logic here...

		result.setResult(EnumDQValidationResult.COMPLIANT);
		result.addComment("Provided value for eventDate '" + eventDate + "' represents the " +
				"same range as verbatimEventDate '" + verbatimEventDate + "'.");

		return result;
	}

    /**
     * If a dwc:eventDate is empty and the verbatimEventDate is not empty, try to populate the 
     * eventDate from the verbatim value.
     * 
     * TG2-AMENDMENT_EVENTDATE_FROM_VERBATIM  
     * 
     * Run in order: extractDateFromVerbatim, then eventDateFromYearStartEndDay, then eventDateFromYearMonthDay
     * 
     * @see eventDateFromYearStartEndDay
     * @see eventDateFromYearMonthDay
     * 
     * @param eventDate to check for emptyness
     * @param verbatimEventDate to try to replace a non-empty event date.
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     *    
     */
    //@Provides(value = "EVENTDATE_FILLED_IN_FROM_VERBATIM")
	@Provides(value = "urn:uuid:6d0a0c10-5e4a-4759-b448-88932f399812")
	@Amendment(label = "Event Date From Verbatim", description = "Try to populate the event date from the verbatim value.")
	@Specification(value = "If a dwc:eventDate is empty and the verbatimEventDate is not empty fill in dwc:eventDate " +
			"based on value from dwc:verbatimEventDate")
    public static EventDQAmendment extractDateFromVerbatim(@ActedUpon(value = "dwc:eventDate") String eventDate, @Consulted(value = "dwc:verbatimEventDate") String verbatimEventDate) {
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
    //Provides urn:uuid:6d0a0c10-5e4a-4759-b448-88932f399812 and eb0a44fa-241c-4d64-98df-ad4aa837307b and 3892f432-ddd0-4a0a-b713-f2e2ecbd879d
    @Provides(value = "urn:uuid:016c6ee6-c528-4435-87ce-1a9dec9c7ae2")
	@Amendment(label = "Event Date From Parts", description = "Try to populate the event date from the verbatim and other atomic parts (day, month, year, etc).")
	@Specification(value = "If a dwc:eventDate is empty and the verbatimEventDate is not empty fill in dwc:eventDate " +
			"based on value from dwc:verbatimEventDate, dwc:year dwc:month, dwc:day, dwc:start/endDayOfYear.")
    public static EventDQAmendment extractDateFromParts(@ActedUpon(value = "dwc:eventDate") String eventDate,
    		 @Consulted(value = "dwc:verbatimEventDate") String verbatimEventDate,
    		 @Consulted(value = "dwc:startDayOfYear") String startDayOfYear,
    		 @Consulted(value = "dwc:endDayOfYear") String endDayOfYear,
    		 @Consulted(value = "dwc:year") String year,
    		 @Consulted(value = "dwc:month") String month,
    		 @Consulted(value = "dwc:day") String day
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

    //@Provides(value = "UPSTREAM_EVENTDATE_FILLED_IN_FROM_START_END")
	@Provides(value = "urn:uuid:e4ddf9bc-cd10-46cc-b307-d6c7233a240a")
	@Amendment(label = "Event Date From non-Darwin Core start/end", description = "Try to populate the event date from non-Darwin Core start date and end date terms.")
	@Specification(value = "If a dwc:eventDate is empty and an event date can be inferred from start date and end date, fill in dwc:eventDate " +
			"based on the values in the start and end dates.  Will not propose a change if dwc:eventDate contains a value.")
    public static EventDQAmendment extractDateFromStartEnd(@ActedUpon(value = "dwc:eventDate") String eventDate, @Consulted(value = "dwc:startDate") String startDate, @Consulted(value="dwc:endDate") String endDate) {
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
    
    
    
    /**
     * Test to see whether a provided dcterms:modified is a validly formated ISO date.
     * 
     * Provides: EventDateValid
     * ??  TG2-VALIDATION_EVENTDATE_NOTSTANDARD
     * 
     * @param eventdate  a string to test
     * @return COMPLIANT if modified is a validly formated ISO date/time with a duration of less than one day, NOT_COMPLIANT if
     *     not an ISO date/time or a range of days, INTERNAL_PREREQUSISITES_NOT_MET if modified is empty.
     */
    @Provides(value = "urn:uuid:f413594a-df57-41ea-a187-b8c6c6379b45")  // VALIDATION_EVENT_DATE_EXISTS
	@Validation(label = "Event date correctly formatted and exists", description = "Test to see whether a provided dwc:eventDate " +
			"is a validly formated ISO date or date/time for an existing date.")
	@Specification(value = "Compliant if dwc:eventDate can to parsed as an actual ISO date, otherwise not compliant. " +
			"Internal prerequisites not met if dwc:eventDate is empty.")
    public static EventDQValidation isEventDateValid(@ActedUpon(value = "dwc:eventDate") String eventdate) {
    	EventDQValidation result = new EventDQValidation();
    	if (DateUtils.isEmpty(eventdate)) {
    		result.addComment("No value provided for dwc:eventDate.");
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		try { 
    	        if (DateUtils.eventDateValid(eventdate)) {
    				result.setResult(EnumDQValidationResult.COMPLIANT);
    				result.addComment("Provided value for dwc:eventDate '" + eventdate + "' is formated as an ISO date. ");
    			} else { 
    				result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    				result.addComment("Provided value for dwc:eventDate '" + eventdate + "' is not a validly formatted ISO date .");
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
     * 
     * @param eventDate to check
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     */
    //@Provides(value = "EVENTDATE_FORMAT_CORRECTION")
	@Provides(value = "urn:uuid:134c7b4f-1261-41ec-acb5-69cd4bc8556f")
	@Amendment(label = "Event Date Format Correction", description = "Try to propose a correction for an event date")
	@Specification(value = "Check dwc:eventDate to see if it is empty or contains a valid date value. If it contains a " +
			"value that is not a valid date, propose a properly formatted eventDate as an amendment.")
    public static EventDQAmendment correctEventDateFormat(@ActedUpon(value = "dwc:eventDate") String eventDate) {
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
    public static EventDQValidation isDayInRange(@ActedUpon(value = "dwc:day") String day) {
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
    // Corresponds to TG2-VALIDATION_MONTH_OUTOFRANGE
    @Provides(value = "urn:uuid:01c6dafa-0886-4b7e-9881-2c3018c98bdc")  // MONTH_INVALID/MONTH_IN_RANGE
	@Validation(label = "Month In Range", description = "Test to see whether a provided month is in the range of " +
			"integer values that form months of the year.")
	@Specification(value = "Compliant if month is an integer in the range 1 to 12 inclusive, otherwise not compliant. " +
			"Internal prerequisites not met if month is empty or an integer cannot be parsed from month.")
    public static EventDQValidation isMonthInRange(@ActedUpon(value="dwc:month") String month) {
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
    
    //@Provides(value = "EVENTDATE_PRECISON_JULIAN_YEAR_OR_BETTER")
	@Provides(value = "urn:uuid:fd00e6be-45e4-4ced-9f3d-5cde30b21b69")
	@Validation(label = "EventDate precision Julian year or better. ", description = "Test to see whether a provided event date " +
			"has a duration less than or equal to a standard astronomical Julian year.")
	@Specification(value = "Compliant if event date has a duration equal to or less than a = 31557600 seconds, otherwise not compliant. " +
			"Internal prerequisites not met if eventDate is empty or not valid.")
    public static EventDQValidation isEventDateJulianYearOrLess(@ActedUpon(value="dwc:eventDate") String eventDate) {
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
    
    
    @Provides(value = "urn:uuid:31d463b4-2a1c-4b90-b6c7-73459d1bad6d")
	@Validation(label = "EventDate precision calendar year or better. ", description = "Test to see whether a provided event date " +
			"has a duration less than or equal to a calendar year.")
	@Specification(value = "Compliant if event date has a duration equal to or less than 365 days if a standard year, 366 days if a leap year. " +
			"Internal prerequisites not met if eventDate is empty or not valid.")
    public static EventDQValidation isEventDateYearOrLess(@ActedUpon(value="dwc:eventDate") String eventDate) {
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
     * Provides: DAY_POSSIBLE_FOR_MONTH_YEAR  (RECORDED_DATE_MISMATCH)
     * 
     * @param year for month and day
     * @param month for day
     * @param day to check 
     * @return an DQValidationResponse object describing whether day exists in year-month-day.
     */
    @Provides(value = "urn:uuid:5618f083-d55a-4ac2-92b5-b9fb227b832f")
	@Validation(label = "Day Consistent With Month/Year", description = "Check if a value for day is consistent with a " +
			"provided month and year.")
	@Specification("Check that the value of dwc:eventDate is consistent with the values for dwc:month and dwc:year. " +
			"Requires valid values for month and year.")
    public static EventDQValidation isDayPossibleForMonthYear(@Consulted(value="dwc:year") String year, @Consulted(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) {
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
    @Provides(value = "urn:uuid:f98a54eb-59e7-44c7-b96f-200e6af1c895")
	@Amendment(label = "Day Month Transposition", description = "Check of month is out of range for months, but day is " +
			"in range for months, and propose a transposition of the two if this is the case.")
	@Specification("If dwc:month and dwc:day are provided, propose a transposition if day is in range for months, and " +
			"month is in range for days")
    public static final EventDQAmendment dayMonthTransposition(@ActedUpon(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) {
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
    
    /**
     * Given a year and an start day of a date range in days of the year, test whether or not
     * the value for startDayOfYear is in range for the days in that year (1-365, or 366  in leap year).
     * 
     * TG2-VALIDATION_STARTDAYOFYEAR_OUTOFRANGE
     * 
     * @param startDay startDayOfYearto check
     * @param year to check for leap year 
     * @return an DQValidationResponse object describing whether the date year-startDayOfYear exists.
     */
    @Provides(value="urn:uuid:85803c7e-2a5a-42e1-b8d3-299a44cafc46")
    public static final EventDQValidation startDayOfYearInRangeForYear(@ActedUpon(value="dwc:startDayOfYear") String startDay, @Consulted(value="dwc:year")String year) { 
    	EventDQValidation result = new EventDQValidation();
    	if (DateUtils.isEmpty(startDay)) { 
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("startDayOfYear was not provided.");
    	} else { 
    	    try { 
    	       Integer numericStartDay = Integer.parseInt(startDay);
    	       if (numericStartDay>0 && numericStartDay<366) { 
    	    	   result.setResult(EnumDQValidationResult.COMPLIANT);
    	    	   result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		       result.addComment("startDayOfYear [" + startDay + "] is in range for days of the year.");
    	       } else if (numericStartDay==366) {
    	           if (DateUtils.isEmpty(year)) { 
    		            result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		            result.addComment("year was not provided and day is 366, could be valid in a leap year.");
    	           } else {
    	        	   String potentialDay = DateUtils.createEventDateFromParts("", startDay, "", year, "", "");
    	        	   if (DateUtils.isEmpty(potentialDay)) { 
    		               result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    	    	           result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		               result.addComment("startDayOfYear [" + startDay + "] is out of range for year ["+ year +"].");
    	        	   } else if (DateUtils.eventDateValid(potentialDay)) { 
    	    	           result.setResult(EnumDQValidationResult.COMPLIANT);
    	    	           result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		               result.addComment("startDayOfYear [" + startDay + "] is in range for days of the year ["+year+"].");
    	        	   } else { 
    		               result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    	    	           result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		               result.addComment("startDayOfYear [" + startDay + "] is out of range for year ["+ year +"].");
    	        	   }
    	           } 
    	       } else { 
    		       result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    	    	   result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		       result.addComment("startDayOfYear [" + startDay + "] is out of range for days in the year.");
    	       }    	       
    	    } catch (NumberFormatException e) { 
    		   result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		   result.addComment("startDayOfYear [" + startDay + "] is not a number.");
    	    }
    	} 
    	return result;
    }
    
    /**
     * Given a year and an end day of a date range in days of the year, test whether or not
     * the value for endDayOfYear is in range for the days in that year (1-365, or 366  in leap year).
     * 
     * TG2-VALIDATION_ENDDAYOFYEAR_OUTOFRANGE 
     * 
     * @param endDay
     * @param year
     * @return an DQValidationResponse object describing whether the date year-endDayOfYear exists.
     */
    @Provides(value="urn:uuid:9a39d88c-7eee-46df-b32a-c109f9f81fb8")
    public static final EventDQValidation endDayOfYearInRangeForYear(@ActedUpon(value="dwc:endDayOfYear") String endDay, @Consulted(value="dwc:year")String year) { 
    	EventDQValidation result = new EventDQValidation();
    	if (DateUtils.isEmpty(endDay)) { 
    		result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("endDayOfYear was not provided.");
    	} else { 
    	    try { 
    	       Integer numericEndDay = Integer.parseInt(endDay);
    	       if (numericEndDay>0 && numericEndDay<366) { 
    	    	   result.setResult(EnumDQValidationResult.COMPLIANT);
    	    	   result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		       result.addComment("endDayOfYear [" + endDay + "] is in range for days of the year.");
    	       } else if (numericEndDay==366) {
    	           if (DateUtils.isEmpty(year)) { 
    		            result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		            result.addComment("year was not provided and day is 366, could be valid in a leap year.");
    	           } else {
    	        	   String potentialDay = DateUtils.createEventDateFromParts("", endDay, "", year, "", "");
    	        	   if (DateUtils.isEmpty(potentialDay)) { 
    		               result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    	    	           result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		               result.addComment("endDayOfYear [" + endDay + "] is out of range for year ["+ year +"].");
    	        	   } else if (DateUtils.eventDateValid(potentialDay)) { 
    	    	           result.setResult(EnumDQValidationResult.COMPLIANT);
    	    	           result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		               result.addComment("endDayOfYear [" + endDay + "] is in range for days of the year ["+year+"].");
    	        	   } else { 
    		               result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    	    	           result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		               result.addComment("endDayOfYear [" + endDay + "] is out of range for year ["+ year +"].");
    	        	   }
    	           } 
    	       } else { 
    		       result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    	    	   result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		       result.addComment("endDayOfYear [" + endDay + "] is out of range for days in the year.");
    	       }
    	    } catch (NumberFormatException e) { 
    		   result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		   result.addComment("endDayOfYear [" + endDay + "] is not a number.");
    	    }
    	} 
    	return result;
    }
    
    
    /**
     * Given a year and a start and end day, propose a value to fill in an eventDate if it is not empty and if
     * both year and start day are not empty.
     * 
     * TG2-AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR
     * 
     * Run in order: extractDateFromVerbatim, then eventDateFromYearStartEndDay, then eventDateFromYearMonthDay
     * 
     * @see extractDateFromVerbatim
     * @see eventDateFromYearMonthDay
     * 
     * @param eventDate to propose a value for if not empty
     * @param year from which to construct an event date
     * @param startDay from which to construct an event date 
     * @param endDay from which to construct an event date
     * @return an EventDQAmmendment which may contain a proposed ammendment.
     */
    @Provides(value="urn:uuid:eb0a44fa-241c-4d64-98df-ad4aa837307b")
    public static final EventDQAmendment eventDateFromYearStartEndDay(@ActedUpon(value="dwc:eventDate") String eventDate, @Consulted(value="dwc:year") String year, @Consulted(value="dwc:startDayOfYear") String startDay, @Consulted(value="dwc:endDayOfYear") String endDay ) {
    	EventDQAmendment result = new EventDQAmendment();
    	if (DateUtils.isEmpty(year) || DateUtils.isEmpty(startDay)) { 
    		result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("Either year or startDayOfYear was not provided.");
    	} else if (!DateUtils.isEmpty(eventDate)) { 
    		result.setResultState(EnumDQAmendmentResultState.NOT_RUN);
    		result.addComment("A value exists in dwc:eventDate, ammendment not attempted.");
    	} else { 
    	    try { 
     	       Integer numericYear = Integer.parseInt(year);
     	       Integer numericStartDay = Integer.parseInt(startDay);
     	       if (!DateUtils.isEmpty(endDay)) { 
     	           Integer numericEndDay = Integer.parseInt(endDay);
     	       }
     	       
     	       String resultDateString = DateUtils.createEventDateFromParts("", startDay, endDay, year, "", "");
     	       
     	       if (DateUtils.isEmpty(resultDateString)) {
     	    	   result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
     		       result.addComment("Unable to construct a valid ISO date from startDayOfYear [" + startDay + "], year ["+year+"], and endDayOfYear ["+ endDay +"].");
     	       } else {
     	    	   result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
     	    	   result.addResult("dwc:eventDate", resultDateString);
     	       }
     	       
     	    } catch (NumberFormatException e) { 
     		   result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
     		   result.addComment("One of startDayOfYear [" + startDay + "], year ["+year+"], or endDayOfYear ["+ endDay +"] is not a number.");
     	    }
    	}
    	return result;
    }

    
    /**
     * Given values for year, month, and day propose a value to fill in an empty eventDate. 
     *  
     * TG2-AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY 
     * 
     * Run in order: extractDateFromVerbatim, then eventDateFromYearStartEndDay, then eventDateFromYearMonthDay
     * 
     * @see eventDateFromYearStartEndDay
     * @see extractDateFromVerbatim
     * 
     * @param eventDate to fill in if not empty
     * @param year from which to construct the event date
     * @param month from which to construct the event date
     * @param day from which to construct the event date
     * @return an EventDQAmmendment which may contain a proposed ammendment.
     */
    @Provides(value= "urn:uuid:3892f432-ddd0-4a0a-b713-f2e2ecbd879d") 
    public static final EventDQAmendment eventDateFromYearMonthDay(@ActedUpon(value="dwc:eventDate") String eventDate, @Consulted(value="dwc:year") String year, @Consulted(value="dwc:month") String month, @Consulted(value="dwc:day") String day ) {
    	EventDQAmendment result = new EventDQAmendment();
    	if (DateUtils.isEmpty(year)) { 
    		result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("No value for dwc:year was provided.");
    	} else if (!DateUtils.isEmpty(eventDate)) { 
    		result.setResultState(EnumDQAmendmentResultState.NOT_RUN);
    		result.addComment("A value exists in dwc:eventDate, ammendment not attempted.");
    	} else { 
    	    try { 
     	       Integer numericYear = Integer.parseInt(year);
    	       if (!DateUtils.isEmpty(month)) { 
     	           Integer numericmonth = Integer.parseInt(month);
    	       }
     	       if (!DateUtils.isEmpty(day)) { 
     	           Integer numericDay = Integer.parseInt(day);
     	       }
     	       
     	       String resultDateString = DateUtils.createEventDateFromParts("", "", "", year, month, day);
     	       
     	       
     	       if (DateUtils.isEmpty(resultDateString)) {
     	    	   result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
     		       result.addComment("Unable to construct a valid ISO date from year ["+year+"], month ["+ month +"] and day ["+ day +"].");
     	       } else if (!DateUtils.eventDateValid(resultDateString)) { 
     	    	   result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
     		       result.addComment("Failed to construct a valid ISO date from year ["+year+"], month ["+ month +"] and day ["+ day +"].");
     	       } else {
     	    	   result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
     	    	   result.addResult("dwc:eventDate", resultDateString);
     	       }
     	       
     	    } catch (NumberFormatException e) { 
     		   result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
     		   result.addComment("One of year [" + year + "], month ["+month+"], or day ["+ day +"] is not a number.");
     	    }
    	}
    	return result;
    }
  
    
    /**
     * Given a value of dwc:month, check to see if that month is an integer, if not, attempt to 
     * propose a suitable integer for the month of the year from the value provided.
     * 
     * TG2-AMENDMENT_MONTH_STANDARDIZED 
     * 
     * @param month the value of dwc:month to assess
     * @return an EventDQAmmendment which may contain a proposed ammendment.
     */
    @Provides(value="urn:uuid:2e371d57-1eb3-4fe3-8a61-dff43ced50cf")
    public static final EventDQAmendment standardizeMonth(@ActedUpon(value="dwc:month") String month) {
    	EventDQAmendment result = new EventDQAmendment();
    	if (DateUtils.isEmpty(month)) { 
    		result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("No value for dwc:month was provided.");
    	} else { 

    		try { 
    			Integer monthnumeric = Integer.parseInt(month);
    			result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
    			result.addComment("A value for dwc:month parsable as an integer was provided.");
    		} catch (NumberFormatException e) { 
    			// Convert roman numerals, some problematic forms of abbreviations, 
    			// non-english capitalization variants, absence of exepected accented characters,
    			// etc to date library (e.g. Joda) recognized month names.
    			String monthConverted = DateUtils.cleanMonth(month);
    			// Strip any trailing period off of month name.
    			String monthTrim = monthConverted.replaceFirst("\\.$", "").trim();
    			if (DateUtils.isEmpty(monthTrim)) { 
    				result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("Unable to parse a meaningfull value for month from dwc:month ["+month+"].");
    			} else { 
    				// Add the month string into the first day of that month in 1800, and see if 
    				// a verbatim date can be parsed from that string.
    				StringBuilder testDate = new StringBuilder().append("1800-").append(monthTrim).append("-01");
    				EventResult convertedDateResult = DateUtils.extractDateFromVerbatimER(testDate.toString());
    				if (convertedDateResult.getResultState().equals(EventResult.EventQCResultState.DATE)) { 
    					String convertedDate = convertedDateResult.getResult();
    					// Date could be parsed, extract the month.
    					Integer monthNumeric = DateUtils.extractDate(convertedDate).getMonthOfYear();
    					result.setResultState(EnumDQAmendmentResultState.CHANGED);
    					result.addResult("dwc:month", monthNumeric.toString());
    					result.addComment("Interpreted provided value for dwc:month ["+month+"] as ["+monthNumeric.toString()+"].");
    				} else { 
    					result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    					result.addComment("Unable to parse a meaningfull value for month from dwc:month ["+month+"].");
    				}
    			} 
    		}
    		
    	}
    	return result;
    }
    
    
    /**
     * Given a dwc:day, if the day is not empty and not an integer, attempt to interpret the value 
     * as a day of the month.   Note: Implementation here is only guided by the example in the 
     * test description, trailing non-numeric characters are removed.   
     * 
     * TG2-AMENDMENT_DAY_STANDARDIZED 
     * 
     * @param day to evaluate
     * @return an EventDQAmmendment which may contain a proposed amendment for key dwc:day.
     */
    @Provides(value="urn:uuid:b129fa4d-b25b-43f7-9645-5ed4d44b357b")
    public static final EventDQAmendment standardizeDay(@ActedUpon(value="dwc:day") String day) {
    	EventDQAmendment result = new EventDQAmendment();
    	if (DateUtils.isEmpty(day)) { 
    		result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("No value for dwc:day was provided.");
    	} else { 
    		try { 
    			Integer dayNumeric = Integer.parseInt(day);
    			result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
    			result.addComment("A value for dwc:day parsable as an integer was provided.");
    		} catch (NumberFormatException e) { 
    			// Strip off any trailing non-numeric characters.
    			String dayTrimmed = day.replaceAll("[^0-9]+$", "");
    			// Try again
    			try { 
    				Integer dayNumeric = Integer.parseInt(dayTrimmed);
    				if (dayNumeric>0 && dayNumeric<32) { 
    					result.setResultState(EnumDQAmendmentResultState.CHANGED);
    					result.addResult("dwc:day", dayNumeric.toString());
    					result.addComment("Interpreted provided value for dwc:day ["+day+"] as ["+dayNumeric.toString()+"].");
    				} else { 
    					result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    					result.addComment("Unable to parse a meaningfull value for day of month from dwc:day ["+day+"].");

    				}
    			} catch (NumberFormatException ex) { 
    				result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("Unable to interpret value provided for dwc:day ["+ day + "] as a day of the month.");
    			}
    		} 
    		
    	}
    	return result;
    }    
    

    //TG2-AMENDMENT_EVENTDATE_STANDARDIZED
    
    //TG2-AMENDMENT_EVENT_FROM_EVENTDATE 
    
    //TG2-VALIDATION_MONTH_OUTOFRANGE
    //TG2-VALIDATION_DAY_OUTOFRANGE
    
    //TG2-VALIDATION_EVENT_EMPTY
    //TG2-VALIDATION_YEAR_OUTOFRANGE 
    //TG2-VALIDATION_EVENT_INCONSISTENT
    //TG2-VALIDATION_EVENTDATE_NOTSTANDARD
    //TG2-VALIDATION_YEAR_EMPTY
    //TG2-VALIDATION_EVENTDATE_OUTOFRANGE
    //TG2-VALIDATION_EVENTDATE_EMPTY 
    
    //TG2-VALIDATION_DATEIDENTIFIED_OUTOFRANGE
    //TG2-VALIDATION_DATEIDENTIFIED_NOTSTANDARD
    
    //TG2-VALIDATION_DATEIDENTIFIED_PREEVENTDATE
    
}
