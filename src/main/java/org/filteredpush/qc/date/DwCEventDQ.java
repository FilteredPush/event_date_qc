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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.EnumDQMeasurementResult;
import org.datakurator.ffdq.api.EnumDQResultState;
import org.datakurator.ffdq.api.EnumDQValidationResult;
import org.datakurator.ffdq.api.EnumDQAmendmentResultState;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;


/**
 * Darwin Core Event eventDate related Data Quality Measures, Validations, and Enhancements. 
 * 
 *  Provides support for the following draft TDWG DQIG TG2 validations and amendments.  
 *  
 * TG2-VALIDATION_EVENTDATE_EMPTY  f51e15a6-a67d-4729-9c28-3766299d2985
 * TG2-VALIDATION_YEAR_EMPTY  c09ecbf9-34e3-4f3e-b74a-8796af15e59f
 * TG2-AMENDMENT_EVENTDATE_FROM_VERBATIM  
 * ?? TG2-VALIDATION_EVENTDATE_NOTSTANDARD 4f2bf8fd-fc5c-493f-a44c-e7b16153c803
 * TG2-AMENDMENT_EVENTDATE_STANDARDIZED  	718dfc3c-cb52-4fca-b8e2-0e722f375da7
 * TG2-VALIDATION_MONTH_OUTOFRANGE isMonthInRange(@ActedUpon(value = "dwc:month") String month) 
 * TG2-VALIDATION_DAY_OUTOFRANGE isDayPossibleForMonthYear(@Consulted(value="dwc:year") String year, @Consulted(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) 
 * TG2-VALIDATION_STARTDAYOFYEAR_OUTOFRANGE
 * TG2-VALIDATION_ENDDAYOFYEAR_OUTOFRANGE 
 * TG2-AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR
 * TG2-AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY 
 * TG2-AMENDMENT_MONTH_STANDARDIZED 
 * TG2-AMENDMENT_DAY_STANDARDIZED 
 * TG2-VALIDATION_EVENT_INCONSISTENT isConsistent(String eventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day 
 * TG2-VALIDATION_EVENT_EMPTY
 * An equivalent measure for TG2-VALIDATION_EVENT_EMPTY
 * TG2-AMENDMENT_EVENT_FROM_EVENTDATE  	710fe118-17e1-440f-b428-88ba3f547d6d
 * TG2-VALIDATION_YEAR_OUTOFRANGE 
 * TG2-VALIDATION_EVENTDATE_OUTOFRANGE  
 * 
 * TG2-MEASURE_EVENT_RANGEINSECONDS  measureDurationSeconds(@ActedUpon(value = "dwc:eventDate") String eventDate)
 *    
 * Also Provides support for the following supplemental, under discussion or other tests: 
 *   DAY_IN_RANGE.  isDayInRange(@ActedUpon(value = "dwc:day") String day)   
 *      Is not the same as TG2-VALIDATION_DAY_OUTOFRANGE which includes year and month 
 *   DAY_MONTH_TRANSPOSED  dayMonthTransposition(@ActedUpon(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) 
 *   EVENTDATE_PRECISON_MONTH_OR_BETTER  TODO: not yet implemented.
 *   EVENTDATE_PRECISON_YEAR_OR_BETTER isEventDateYearOrLess(@ActedUpon(value="dwc:eventDate") String eventDate) 
 *                                also isEventDateJulianYearOrLess(@ActedUpon(value="dwc:eventDate") String eventDate) 
 *  
 *  Also provides (intended to prepare upstream data for Darwin Core: 
 *     UPSTREAM_EVENTDATE_FILLED_IN_FROM_START_END  extractDateFromStartEnd(@ActedUpon(value = "dwc:eventDate") String eventDate, @Consulted(value = "startDate") String startDate, @Consulted(value="endDate") String endDate) 
 *  
 *  Supplemental, not yet(?) implemented
 *  	DAY_IS_FIRST_OF_CENTURY
 *  	DAY_IS_FIRST_OF_YEAR
 *      EVENTDATE_CONSISTENT_WITH_DAY_MONTH_YEAR  
 *      STARTDATE_CONSISTENT_WITH_ENDDATE
 *      YEAR_PROVIDED
 * 
 *  Not implemented: 
 *  TG2-AMENDMENT_YEAR_STANDARDIZED  Unclear how to implement this one.
 *    Provides(value="urn:uuid:baf2a90b-af45-4f1a-839f-47126743a48a")
 *    Amendment( label = "AMENDMENT_YEAR_STANDARDIZED", description="The value of dwc:year was interpreted to be a number between a designated minimum value and the current year, inclusive")
 *
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
    @Provides(value="urn:uuid:b0753f69-08c1-45f5-a5ca-48d24e76d813")
    @Measure(dimension = Dimension.PRECISION, label = "MEASURE_EVENT_RANGEINSECONDS", description="Report on the length of the period expressed in the dwc:Event")
    @Specification(value="Report on the length of the period expressed in the dwc:Event The field dwc:eventDate contains a valid ISO 8601:2004(E) date.")

	
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
	public static EventDQMeasurement<EnumDQMeasurementResult> measureEventCompleteness(@ActedUpon(value = "dwc:eventDate") String eventDate) {
		EventDQMeasurement<EnumDQMeasurementResult> result = new EventDQMeasurement<EnumDQMeasurementResult>();
		if (DateUtils.isEmpty(eventDate)) {
			result.addComment("No value provided for eventDate.");
			result.setValue(EnumDQMeasurementResult.NOT_COMPLETE);
			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for eventDate.");
			result.setValue(EnumDQMeasurementResult.COMPLETE);
			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
		}

		return result;
	}
	
	/**
	 * Test to see whether or not a dwc:eventDate contains any value.
	 * 
	 * validation corresponding to TG2-VALIDATION_EVENTDATE_EMPTY  f51e15a6-a67d-4729-9c28-3766299d2985
	 * 
	 * @param eventDate to assess for emptyness
     * @return an object implementing DQValidationResponse describing whether any value is present dwc:eventDate.
	 */
    @Provides(value="urn:uuid:f51e15a6-a67d-4729-9c28-3766299d2985")
    @Validation( label = "VALIDATION_EVENTDATE_EMPTY", description="The field dwc:eventDate is not EMPTY")
    @Specification(value="The field dwc:eventDate is not EMPTY The field dwc:eventDate exists in the record.")
	public static EventDQValidation isEventDateEmpty(@ActedUpon(value = "dwc:eventDate") String eventDate) {
		EventDQValidation result = new EventDQValidation();
		if (DateUtils.isEmpty(eventDate)) {
			result.addComment("No value provided for eventDate.");
			result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for eventDate.");
			result.setResult(EnumDQValidationResult.COMPLIANT);
			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
		}
		return result;
	}
	
	
	/**
	 * Test to see whether or not a dwc:year contains any value.
	 * 
	 * validation corresponding to  TG2-VALIDATION_YEAR_EMPTY  c09ecbf9-34e3-4f3e-b74a-8796af15e59f
	 * 
	 * @param year to check for emptyness
     * @return an object implementing DQValidationResponse describing whether any value is present dwc:year.
	 */
	@Provides(value="urn:uuid:c09ecbf9-34e3-4f3e-b74a-8796af15e59f")
    @Validation( label = "VALIDATION_YEAR_EMPTY", description="The field dwc:year is not EMPTY")
    @Specification(value="The field dwc:year is not EMPTY The field dwc:year exists in the record.")
	public static EventDQValidation isYearEmpty(@ActedUpon(value = "dwc:year") String year) {
		EventDQValidation result = new EventDQValidation();
		if (DateUtils.isEmpty(year)) {
			result.addComment("No value provided for dwc:year.");
			result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:year.");
			result.setResult(EnumDQValidationResult.COMPLIANT);
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

    public static EventDQValidation isEventDateConsistentWithVerbatim(
    		@ActedUpon(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:verbatimEventDate") String verbatimEventDate) {

		EventDQValidation result = new EventDQValidation();

		// TODO: Actor logic here...

		//result.setResult(EnumDQValidationResult.COMPLIANT);
		//result.addComment("Provided value for eventDate '" + eventDate + "' represents the " +
		//		"same range as verbatimEventDate '" + verbatimEventDate + "'.");

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
    @Provides(value="urn:uuid:6d0a0c10-5e4a-4759-b448-88932f399812")
    @Amendment( label = "AMENDMENT_EVENTDATE_FROM_VERBATIM", description="The value of dwc:eventDate was interpreted from dwc:verbatimEventDate")
    @Specification(value="The value of dwc:eventDate was interpreted from dwc:verbatimEventDate The field dwc:eventDate is EMPTY and the field dwc:verbatimEventDate is not EMPTY and is interpretable as an ISO 8601:2004(E) date")
	//@Specification(value = "If a dwc:eventDate is empty and the verbatimEventDate is not empty fill in dwc:eventDate " +
	//		"based on value from dwc:verbatimEventDate")
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
     * Test to see whether a provided dwc:eventDate is a validly formated ISO date.
     * 
     * Provides: EventDateValid
     * TODO: needs clarification in the standard test suite, this may or may not be:  
     * ??  TG2-VALIDATION_EVENTDATE_NOTSTANDARD 4f2bf8fd-fc5c-493f-a44c-e7b16153c803
     * 
     * @param eventdate  a string to test
     * @return COMPLIANT if modified is a validly formated ISO date/time with a duration of less than one day, NOT_COMPLIANT if
     *     not an ISO date/time or a range of days, INTERNAL_PREREQUSISITES_NOT_MET if modified is empty.
     */
    //@Provides(value = "urn:uuid:f413594a-df57-41ea-a187-b8c6c6379b45")  // VALIDATION_EVENT_DATE_EXISTS
    @Provides(value="urn:uuid:4f2bf8fd-fc5c-493f-a44c-e7b16153c803")
    @Validation( label = "VALIDATION_EVENTDATE_NOTSTANDARD", description="The value of dwc:eventDate is a correctly formatted ISO 8601:2004(E) date")
    @Specification(value="The value of dwc:eventDate is a correctly formatted ISO 8601:2004(E) date The field dwc:eventDate is not EMPTY.")
	//@Validation(label = "Event date correctly formatted and exists", description = "Test to see whether a provided dwc:eventDate " +
	//		"is a validly formated ISO date or date/time for an existing date.")
	//@Specification(value = "Compliant if dwc:eventDate can to parsed as an actual ISO date, otherwise not compliant. " +
	//		"Internal prerequisites not met if dwc:eventDate is empty.")
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
     * TODO: Need to confirm GUID, EVENTDATE_FORMAT_CORRECTION and TG2-AMENDMENT_EVENTDATE_STANDARDIZED may be
     * the same thing with different guids.
     * 
     * TG2-AMENDMENT_EVENTDATE_STANDARDIZED  	718dfc3c-cb52-4fca-b8e2-0e722f375da7
     * 
     * @param eventDate to check
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     */
    //@Provides(value = "EVENTDATE_FORMAT_CORRECTION")
	//@Provides(value = "urn:uuid:134c7b4f-1261-41ec-acb5-69cd4bc8556f")
    @Provides(value="urn:uuid:718dfc3c-cb52-4fca-b8e2-0e722f375da7")
    @Amendment( label = "AMENDMENT_EVENTDATE_STANDARDIZED", description="The field dwc:eventDate was altered to conform with ISO 8601:2004(E)")
    @Specification(value="The field dwc:eventDate was altered to conform with ISO 8601:2004(E) The field dwc:eventDate is not EMPTY.")
	// @Specification(value = "Check dwc:eventDate to see if it is empty or contains a valid date value. If it contains a " +
	//		"value that is not a valid date, propose a properly formatted eventDate as an amendment.")
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
     * Test to see whether a provided day is an integer in the range of values that can be 
     * a day of a month.
     * 
     * Provides: DAY_IN_RANGE.  Is not the same as TG2-VALIDATION_DAY_OUTOFRANGE which includes year and month
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
     * TG2-VALIDATION_MONTH_OUTOFRANGE
     * 
     * @param month  a string to test
     * @return COMPLIANT if month is an integer in the range 1 to 12 inclusive, NOT_COMPLIANT if month is 
     *     an integer outside this range, INTERNAL_PREREQUSISITES_NOT_MET if month is empty or an integer
     *     cannot be parsed from month. 
     */
    // @Provides(value = "MONTH_IN_RANGE")
    // Corresponds to TG2-VALIDATION_MONTH_OUTOFRANGE
    //@Provides(value = "urn:uuid:01c6dafa-0886-4b7e-9881-2c3018c98bdc")  // MONTH_INVALID/MONTH_IN_RANGE
	//@Validation(label = "Month In Range", description = "Test to see whether a provided month is in the range of " +
	//		"integer values that form months of the year.")
	//@Specification(value = "Compliant if month is an integer in the range 1 to 12 inclusive, otherwise not compliant. " +
	//		"Internal prerequisites not met if month is empty or an integer cannot be parsed from month.")
    @Provides(value="urn:uuid:01c6dafa-0886-4b7e-9881-2c3018c98bdc")
    @Validation( label = "VALIDATION_MONTH_OUTOFRANGE", description="The value of dwc:month is between 1 and 12, inclusive")
    @Specification(value="The value of dwc:month is between 1 and 12, inclusive The value of dwc:month is a number.")
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

    
    // TODO: Confirm GUID, this may be 9f05540b-3783-4e0f-8e52-8d2a1f93d9d7 or  5618f083-d55a-4ac2-92b5-b9fb227b832f
    /**
     * Check if a value for day is consistent with a provided month and year. 
     * 
     * Provides: DAY_POSSIBLE_FOR_MONTH_YEAR  (RECORDED_DATE_MISMATCH)
     * 
     * TG2-VALIDATION_DAY_OUTOFRANGE
     * 
     * @param year for month and day
     * @param month for day
     * @param day to check 
     * @return an DQValidationResponse object describing whether day exists in year-month-day.
     */
    @Provides(value="urn:uuid:5618f083-d55a-4ac2-92b5-b9fb227b832f")
    @Validation( label = "VALIDATION_DAY_OUTOFRANGE", description="The value of dwc:day is a valid day given the month and year.")
    @Specification(value="The value of dwc:day is a valid day given the month and year. The value of dwc:day is a integer. If present, dwc:month and dwc:year must be integers.")
    //@Specification(value="The provided values for year, month, day and start and end days of year (dwc:year, dwc:month, dwc:day, dwc:startDayOfYear and dwc:endDayofYear) are within the range of the supplied dwc:eventDate The dwc:eventDate is not EMPTY and at least one of dwc:year, dwc:month, dwc:day, dwc:startDayOfYear and dwc:endDayOfYear is not EMPTY")
	//@Specification("Check that the value of dwc:eventDate is consistent with the values for dwc:month and dwc:year. " +
	//		"Requires valid values for month and year.")
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
     * Provides: dwc
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
    @Validation( label = "VALIDATION_STARTDAYOFYEAR_OUTOFRANGE", description="The value of dwc:startDayOfYear is a valid day given the year.")
    @Specification(value="The value of dwc:startDayOfYear is a valid day given the year. The value of dwc:startDayOfYear is a number. If present dwc:year must be an integer. This test should be run after the test TG2-AMENDMENT_EVENT_FROM_EVENTDATE (#52)")
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
     * @param endDay to evaluate for range
     * @param year to examine for day 366, in range in in a leap year.
     * @return an DQValidationResponse object describing whether the date year-endDayOfYear exists.
     */
    @Provides(value="urn:uuid:9a39d88c-7eee-46df-b32a-c109f9f81fb8")
    @Validation( label = "VALIDATION_ENDDAYOFYEAR_OUTOFRANGE", description="The value of dwc:endDayOfYear is a valid day given the year.")
    @Specification(value="The value of dwc:endDayOfYear is a valid day given the year. The value of dwc:endDayOfYear is a number. If present dwc:year must be an integer. This test should be run after the test TG2-AMENDMENT_EVENT_FROM_EVENTDATE (#52)")
    public static final EventDQValidation isEndDayOfYearInRangeForYear(@ActedUpon(value="dwc:endDayOfYear") String endDay, @Consulted(value="dwc:year")String year) { 
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
    @Amendment( label = "AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR", description="The value of dwc:eventDate was interpreted from the values in dwc:year, dwc:startDayOfYear and dwc:endDayOfYear")
    @Specification(value="The value of dwc:eventDate was interpreted from the values in dwc:year, dwc:startDayOfYear and dwc:endDayOfYear The field dwc:eventDate is EMPTY and at least dwc:year and one of dwc:startDayOfYear or dwc:endDayOfYear must not be EMPTY and must be interpretable.")
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
    @Provides(value="urn:uuid:3892f432-ddd0-4a0a-b713-f2e2ecbd879d")
    @Amendment( label = "AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY", description="The value of dwc:eventDate was interpreted from the values in dwc:year, dwc:month and dwc:day")
    @Specification(value="The value of dwc:eventDate was interpreted from the values in dwc:year, dwc:month and dwc:day The field dwc:eventDate is EMPTY and at least dwc:year (from among dwc:year, dwc:month, and dwc:day) must not be EMPTY and must be interpretable as a year.")
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
    @Amendment( label = "AMENDMENT_MONTH_STANDARDIZED", description="The value of dwc:month was interpreted to be a number between 1 and 12, inclusive")
    @Specification(value="The value of dwc:month was interpreted to be a number between 1 and 12, inclusive The field dwc:month is not EMPTY.")
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
     * This implementation is subject to change.
     * 
     * TG2-AMENDMENT_DAY_STANDARDIZED 
     * 
     * @param day to evaluate
     * @return an EventDQAmmendment which may contain a proposed amendment for key dwc:day.
     */
    @Provides(value="urn:uuid:b129fa4d-b25b-43f7-9645-5ed4d44b357b")
    @Amendment( label = "AMENDMENT_DAY_STANDARDIZED", description="The value of dwc:day was interpreted to be a number between 1 and 31, inclusive")
    @Specification(value="The value of dwc:day was interpreted to be a number between 1 and 31, inclusive The field dwc:day is not EMPTY.")
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
    
    
    /**
     * Given a set of Event terms related to date and time, determine if they are consistent or 
     * inconsistent.
     * 
     * TG2-VALIDATION_EVENT_INCONSISTENT 
     *  
     * @param eventDate to examine
     * @param verbatimEventDate  to examine
     * @param year to examine
     * @param month to examine
     * @param day to examine
     * @param startDayOfYear to examine
     * @param endDayOfYear to examine
     * @param eventTime to examine (may get removed from test definition)
     * @return an DQValidationResponse object describing whether the event terms represent one temporal interval or 
     *   whether they are inconsistent with each other.
     */
    @Provides(value="5618f083-d55a-4ac2-92b5-b9fb227b832f")
    public static EventDQValidation isEventDateConsistentWithAtomic(
    		@ActedUpon(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:verbatimEventDate") String verbatimEventDate, 
			@ActedUpon(value = "dwc:year") String year, 
			@ActedUpon(value = "dwc:month") String month, 
			@ActedUpon(value = "dwc:day") String day, 
			@ActedUpon(value = "dwc:startDayOfYear") String startDayOfYear, 
			@ActedUpon(value = "dwc:endDayOfYear") String endDayOfYear, 
			@ActedUpon(value = "dwc:eventTime") String eventTime )   // TODO: Not in definition of test, needs to be added there.
    {

		EventDQValidation result = new EventDQValidation();
		boolean inconsistencyFound = false;
		boolean interpretationProblem = false;

		// Compare eventDate and eventTime
		if (!DateUtils.isEmpty(eventDate) && !DateUtils.isEmpty(eventTime)) { 
			if (DateUtils.containsTime(eventDate)) { 
				String time1 = DateUtils.extractZuluTime(eventDate);
				String time2 = DateUtils.extractZuluTime("1900-01-01 "+ eventTime);
				if (time1!=null && time2!=null) { 
					if (!time1.equals(time2)) {
						inconsistencyFound = true;
						result.addComment("Time part of the provided value for eventDate '" + eventDate + "' appears to represents a different time than " +
								" eventTime '" + eventTime + "'.");
					}
				} else { 
					interpretationProblem = true;
					result.addComment("Unable to interpret the time part of either the provided value for eventDate '" + eventDate + "' or " +
								" eventTime '" + eventTime + "'.");
				}
			}
		}
		// compare eventDate and year, month, day
		if (!DateUtils.isEmpty(eventDate) && !DateUtils.isEmpty(year)) { 
			if (!DateUtils.isEmpty(month)) { 
				if (!DateUtils.isEmpty(day)) { 
					if (!DateUtils.isConsistent(eventDate, year, month, day)) { 
						inconsistencyFound = true;
						result.addComment("Provided value for eventDate '" + eventDate + "' appears to represent a date inconsistent with year-month-day " + year + "-" + month +"-" + day + ".");
					}

				} else { 
					if (!DateUtils.isConsistent(eventDate, year, month, "1")) { 
						inconsistencyFound = true;
						result.addComment("Provided value for eventDate '" + eventDate + "' appears to represent a date inconsistent with year-month " + year + "-" + month +" .");
					}
				}
			} else { 
				if (!DateUtils.isEmpty(day)) {
					interpretationProblem = true;
					result.addComment("Provided value for eventDate '" + eventDate + "' can't be tested for consistency with " + year + "- -" + day + " (no month provided).");
				} else { 
					if (!DateUtils.isConsistent(eventDate, year, "1", "1")) { 
						inconsistencyFound = true;
						result.addComment("Provided value for eventDate '" + eventDate + "' appears to represent a date inconsistent with year " + year + " .");
					}
				}				
			}
		}
		
		
		// compare eventDate and start/end day of year
		if (!DateUtils.isEmpty(eventDate) && !DateUtils.isEmpty(startDayOfYear)) { 
			if (!DateUtils.isConsistent(eventDate, startDayOfYear, endDayOfYear, "", "", "")) { 
				inconsistencyFound = true;
				result.addComment("Provided value for eventDate '" + eventDate + "' appears to represent a date inconsistent with startDayOfYear [" + startDayOfYear + "] or endDayOfYear [" + endDayOfYear +"].");
			}
			
		}
		
		// compare eventDate and month day (if year is empty)
		if (!DateUtils.isEmpty(eventDate) && DateUtils.isEmpty(year) && (!DateUtils.isEmpty(month) || !DateUtils.isEmpty(day))) { 
			if (!DateUtils.isConsistent(eventDate, "", month, day)) { 
				inconsistencyFound = true;
				result.addComment("Provided value for eventDate '" + eventDate + "' appears to represent a date inconsistent with  the month ["+ month +" or day [" + day + "], no year provided.");
			}
		}

		// compare year month day with start day of year
		if (!DateUtils.isEmpty(year) && !DateUtils.isEmpty(month) && !DateUtils.isEmpty(day) && !DateUtils.isEmpty(startDayOfYear)) {
			StringBuilder tempDate = new StringBuilder().append(year)
					.append("-").append(month).append("-").append(day);
			if (!DateUtils.isConsistent(tempDate.toString(), startDayOfYear, "", year, month, day)) { 
				inconsistencyFound = true;
				result.addComment("Provided value for year month and day'" + tempDate + "' appear to represent a date inconsistent with startDayOfYear [" + startDayOfYear + "] .");
			}
		}
		
		// compare eventDate with verbatimEventDate
		if (!DateUtils.isEmpty(eventDate) && !DateUtils.isEmpty(verbatimEventDate)) {
			String testDate = DateUtils.createEventDateFromParts(verbatimEventDate, "", "", "", "", "");
			if (!DateUtils.isEmpty(testDate)) { 
				if (!DateUtils.eventsAreSameInterval(eventDate, testDate)) { 
					inconsistencyFound = true;
					result.addComment("Provided value for eventDate '" + eventDate + "' appears to represent a different interval of time from the verbatimEventDate [" + verbatimEventDate +"].");
				}
			}
		}
		
		if (DateUtils.isEmpty(eventDate) && 
				DateUtils.isEmpty(year) &&
				DateUtils.isEmpty(month) &&
				DateUtils.isEmpty(day) &&
				DateUtils.isEmpty(startDayOfYear) &&
				DateUtils.isEmpty(endDayOfYear) &&
				DateUtils.isEmpty(eventTime) &&
				DateUtils.isEmpty(verbatimEventDate) ) 
		{	
			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
			result.addComment("All provided event terms are empty, can't assess consistency.");
			logger.debug("All terms empty.");
		} else { 
			if (inconsistencyFound) { 
				// inconsistency trumps interpretation problem, return result as NOT COMPLIANT
				result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
				result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
			} else { 
				if (interpretationProblem) { 
					result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
				} else { 
					result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
					result.setResult(EnumDQValidationResult.COMPLIANT);
				}
			}
		}

		return result;
	}  
    
    /**
     * Examine each of the date/time related terms in the Event class, and test to see if at least 
     * one of them contains some value.  This may or may not be a meaningful value, and this may or
     * may not be interpretable to a date or date range.
     * 
     * TG2-VALIDATION_EVENT_EMPTY
     * 
     * Does not include eventTime (not considered core) in the evaluation of emptyness.
     * 
     * @param eventDate to examine
     * @param verbatimEventDate to examine
     * @param year to examine
     * @param month to examine
     * @param day to examine
     * @param startDayOfYear to examine
     * @param endDayOfYear to examine
     * @return an DQValidationResponse object describing whether any value is present in any of the temporal terms of the event.
     */
    @Provides(value="urn:uuid:41267642-60ff-4116-90eb-499fee2cd83f")
    @Validation( label = "VALIDATION_EVENT_EMPTY", description="At least one field needed to determine the event date exists and is not EMPTY.")
    @Specification(value="At least one field needed to determine the event date exists and is not EMPTY. None. It is not necessary for the record to have any fields in the Event class to run this test.")
    public static EventDQValidation isEventEmpty(
    		@ActedUpon(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:verbatimEventDate") String verbatimEventDate, 
			@ActedUpon(value = "dwc:year") String year, 
			@ActedUpon(value = "dwc:month") String month, 
			@ActedUpon(value = "dwc:day") String day, 
			@ActedUpon(value = "dwc:startDayOfYear") String startDayOfYear, 
			@ActedUpon(value = "dwc:endDayOfYear") String endDayOfYear )
			// @ActedUpon(value = "dwc:eventTime") String eventTime )   // Removed per discussion in tdwg/bdq issue 88
    {
    	
		EventDQValidation result = new EventDQValidation();
		
		if (DateUtils.isEmpty(eventDate) && 
			DateUtils.isEmpty(year) &&
			DateUtils.isEmpty(month) &&
			DateUtils.isEmpty(day) &&
			DateUtils.isEmpty(startDayOfYear) &&
			DateUtils.isEmpty(endDayOfYear) &&
			DateUtils.isEmpty(verbatimEventDate)
				) { 
			result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
			result.addComment("No value is present in any of the Event temporal terms");
		} else { 
			result.setResult(EnumDQValidationResult.COMPLIANT);
			result.addComment("Some value is present in at least one of the Event temporal terms");
		}
		result.setResultState(EnumDQResultState.RUN_HAS_RESULT);

		return result;
	}     
    
    /**
     * Examine each of the date/time related terms in the Event class, and test to see if at least 
     * one of them contains some value.  This may or may not be a meaningful value, and this may or
     * may not be interpretable to a date or date range.
     * 
     * Equivalent measure for TG2-VALIDATION_EVENT_EMPTY
     * 
     * @param eventDate to examine
     * @param verbatimEventDate to examine
     * @param year to examine
     * @param month to examine
     * @param day to examine
     * @param startDayOfYear to examine
     * @param endDayOfYear to examine
     * @return an EventDQMeasurement object describing the completeness of the temporal terms of the event.
     */    
	@Provides(value = "9dc97514-3b88-4afc-931d-5fc386be21ee") // locally generated
	@Measure(dimension = Dimension.COMPLETENESS, label = "Event Completeness", description = "Measure the completeness of the temporal terms in an Event.")
	@Specification(value = "For values of dwc:eventDate, year, month, day, startDayOfYear, endDayOfYear, verbatimEventDate, eventTime, check is not empty.")
	public static EventDQMeasurement<EnumDQMeasurementResult> measureEventCompleteness(
    		@ActedUpon(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:verbatimEventDate") String verbatimEventDate, 
			@ActedUpon(value = "dwc:year") String year, 
			@ActedUpon(value = "dwc:month") String month, 
			@ActedUpon(value = "dwc:day") String day, 
			@ActedUpon(value = "dwc:startDayOfYear") String startDayOfYear, 
			@ActedUpon(value = "dwc:endDayOfYear") String endDayOfYear )
		{
		EventDQMeasurement<EnumDQMeasurementResult> result = new EventDQMeasurement<EnumDQMeasurementResult>();
		EventDQValidation validation = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		if (validation.getResultState().equals(EnumDQResultState.RUN_HAS_RESULT)) {
			if (validation.getResult().equals(EnumDQValidationResult.COMPLIANT)) { 
				result.setValue(EnumDQMeasurementResult.COMPLETE);
			} else { 
				result.addComment("No value provided for eventDate.");
				result.setValue(EnumDQMeasurementResult.NOT_COMPLETE);
			}
			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
		}
		return result;
	}    

 
	
	/**
	 * Given a set of event terms, examine the content of eventDate, and if it is correctly formatted and 
	 * can be interpreted, fill in any empty of the following terms (year, month, day, startDayOfYear, endDayOfYear)
	 * with appropriate values.
	 * 
     *TG2-AMENDMENT_EVENT_FROM_EVENTDATE  	710fe118-17e1-440f-b428-88ba3f547d6d
	 * 
	 * @param eventDate to examine
	 * @param year to check for emptyness
	 * @param month to check for emptyness
	 * @param day to check for emptyness
	 * @param startDayOfYear to check for emptyness
	 * @param endDayOfYear to check for emptyness
     * @return an EventDQAmmendment which may contain a proposed amendment for key dwc:day.
	 */
	@Provides(value="urn:uuid:710fe118-17e1-440f-b428-88ba3f547d6d")
    @Amendment( label = "AMENDMENT_EVENT_FROM_EVENTDATE", description="One or more empty component terms of the dwc:Event class (dwc:year, dwc:month, dwc:day, dwc:startDayOfYear, dwc:endDayOfYear) have been filled in from a valid value in the term dwc:eventDate.")
    @Specification(value="One or more empty component terms of the dwc:Event class (dwc:year, dwc:month, dwc:day, dwc:startDayOfYear, dwc:endDayOfYear) have been filled in from a valid value in the term dwc:eventDate. The field dwc:eventDate is not EMPTY and contains a valid ISO 8601:2004(E).  Run this amendment after any other amendment which may affect dwc:eventDate.")
	public static EventDQAmendment fillInEventFromEventDate(
    		@Consulted(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:year") String year, 
			@ActedUpon(value = "dwc:month") String month, 
			@ActedUpon(value = "dwc:day") String day, 
			@ActedUpon(value = "dwc:startDayOfYear") String startDayOfYear, 
			@ActedUpon(value = "dwc:endDayOfYear") String endDayOfYear 
			) 
		{
    	EventDQAmendment result = new EventDQAmendment();
    	if (DateUtils.isEmpty(eventDate)) { 
    		result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("No value for dwc:eventDate was provided, no data to fill in from.");
    	} else {
    		if (!DateUtils.eventDateValid(eventDate)) {
    			result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("Provided value for dwc:eventDate ["+eventDate+"] could not be interpreted.");
    		} else { 
    			boolean isRange = false;
    			if (DateUtils.isRange(eventDate)) { 
    				isRange = true;
    			}
    			Interval interval = DateUtils.extractInterval(eventDate);
    			if (interval==null) { 
    				result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("Provided value for dwc:eventDate ["+ eventDate +"] appears to be correctly formatted, but could not be interpreted as a valid date.");

    			} else { 
    				if (DateUtils.isEmpty(day)) { 
    					String newDay = Integer.toString(interval.getStart().getDayOfMonth());
    					result.addResult("dwc:day", newDay );
    					result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
    					if (isRange) { 
    						result.addComment("Added day ["+ newDay+"] from start day of range ["+eventDate+"].");
    					} else {
    						result.addComment("Added day ["+ newDay+"] from eventDate ["+eventDate+"].");
    					}
    				}
    				if (DateUtils.isEmpty(month)) { 
    					String newMonth = Integer.toString(interval.getStart().getMonthOfYear());
    					result.addResult("dwc:month", newMonth );
    					result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
    					if (isRange) { 
    						result.addComment("Added month ["+ newMonth +"] from start month of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added month ["+ newMonth +"] from eventDate ["+eventDate+"].");
    					}
    				}    		
    				if (DateUtils.isEmpty(month)) { 
    					String newMonth = Integer.toString(interval.getStart().getMonthOfYear());
    					result.addResult("dwc:month", newMonth );
    					result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
    					if (isRange) { 
    						result.addComment("Added month ["+ newMonth +"] from start month of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added month ["+ newMonth +"] from eventDate ["+eventDate+"].");
    					}
    				}     
    				if (DateUtils.isEmpty(year)) { 
    					String newYear = Integer.toString(interval.getStart().getYear());
    					result.addResult("dwc:year", newYear );
    					result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
    					if (isRange) { 
    						result.addComment("Added year ["+ newYear +"] from start month of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added year ["+ newYear +"] from eventDate ["+eventDate+"].");
    					}
    				}        		

    				if (DateUtils.isEmpty(startDayOfYear)) { 
    					String newDay = Integer.toString(interval.getStart().getDayOfYear());
    					result.addResult("dwc:startDayOfYear", newDay );
    					result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
    					if (isRange) { 
    						result.addComment("Added startDayOfYear ["+ newDay +"] from start day of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added startDayOfYear ["+ newDay +"] from eventDate ["+eventDate+"].");
    					}
    				} 

    				if (DateUtils.isEmpty(endDayOfYear)) { 
    					String newDay = Integer.toString(interval.getEnd().getDayOfYear());
    					result.addResult("dwc:endDayOfYear", newDay );
    					result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
    					if (isRange) { 
    						result.addComment("Added endDayOfYear ["+ newDay +"] from end day of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added endDayOfYear ["+ newDay +"] from eventDate ["+eventDate+"].");
    					}
    				}  

    				// Time could also be populated, but we probably don't want to.  Here is a minimal implementation,
    				// which illustrates some issues in implementation (using zulu time or not, dealing with time in ranges...)
    				//if (DateUtils.isEmpty(eventTime)) { 
    				//	if (DateUtils.containsTime(eventDate)) { 
    				//		String newTime = DateUtils.extractZuluTime(eventDate);
    				//		result.addResult("dwc:endDayOfYear", newTime );
    				//      result.setResultState(EnumDQAmendmentResultState.FILLED_IN);
    				//	    result.addComment("Added eventTime ["+ newTime +"] from eventDate ["+eventDate+"].");
    				//	}
    				//}
    				if (!result.getResultState().equals(EnumDQAmendmentResultState.FILLED_IN)) {
    					result.setResultState(EnumDQAmendmentResultState.NO_CHANGE);
    					result.addComment("No changes proposed, all candidate fields to fill in contain values.");
    				}
    			} // end interval extraction
    		} // end format validity check
    	}
    	return result;
	}
    
	
	/**
	 * Given a year, evaluate whether that year falls in the range between a provided lower bound and the current
	 * year, inclusive of the lower bound and the current year.  If null is provided for lowerBound, then the value
	 * 1700 will be used as the lower bound.  This implementation uses the year from the local date/time as the upper 
	 * bound, and will give different answers for values of year within 1 of the current year when run in different 
	 * time zones within one day of a year boundary (i.e. this test is not suitable for fine grained evaluations of 
	 * time).
	 * 
     * TG2-VALIDATION_YEAR_OUTOFRANGE 
     * 
	 * @param year to evaluate
	 * @param lowerBound integer for lower bound of range of in range years, if null 1700 will be used.
     * @return an DQValidationResponse object describing whether the provided value is in range.
	 */
    @Provides(value = "urn:uuid:ad0c8855-de69-4843-a80c-a5387d20fbc8")   // GUID for DAY_INVALID/DAY_IN_RANGE
    @Validation( label = "VALIDATION_YEAR_OUTOFRANGE", description="The value of dwc:year is between a designated minimum value and the current year, inclusive")
    @Specification(value="The value of dwc:year is between a designated minimum value and the current year, inclusive The value of dwc:year is a number.")
    public static EventDQValidation isYearInRange(@ActedUpon(value = "dwc:year") String year, Integer lowerBound) {
    	EventDQValidation result = new EventDQValidation();
    	if (lowerBound==null) { 
    		lowerBound = 1700;
    	}
    	Integer upperBound = LocalDateTime.now().getYear();
    	if (DateUtils.isEmpty(year)) {
    		result.addComment("No value provided for dwc:year.");
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		try { 
    			int numericYear = Integer.parseInt(year.trim());
    			if (numericYear<lowerBound || numericYear>upperBound) { 
    				result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    				result.addComment("Provided value for dwc:year '" + year + "' is not an integer in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    			} else { 
    				result.setResult(EnumDQValidationResult.COMPLIANT);
    				result.addComment("Provided value for dwc:year '" + year + "' is an integer in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    			}
    			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		} catch (NumberFormatException e) { 
    			logger.debug(e.getMessage());
    			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("Unable to parse dwc:year as an integer:" + e.getMessage());
    		}
    	}
    	return result;
    }		
	
    /**
     * Given an eventDate check to see if that event date falls entirely outside a range from a
     * specified lower bound (1700-01-01 by default) and the present.  
     * 
     * TODO: This may or may not be consistent with the standard test, current implementation here fails
     * only if the eventDate has no overlap with the lowerBound to the present, not if the eventDate has 
     * any overlap outside the lowerBound to the present.
     * TG2-VALIDATION_EVENTDATE_OUTOFRANGE ?  probably not
     * 
     * 
     * @param eventDate to check
     * @param lowerBound integer representing the year to use as the lower boundary, if null, then uses 1700
     * @param useLowerBound if false, no lower limit, otherwise uses supplied lower bound.
     * @return an DQValidationResponse object describing whether the provided value is in range.
     */
    @Provides(value="urn:uuid:bc8d1ffb-d074-4b4b-919c-f0c8fbe1a618")  // new guid, probably not the test as specified 
    @Validation( label = "VALIDATION_EVENTDATE_WHOLLYOUTOFRANGE", description="The range of dwc:eventDate does not fall entirely into the future and optionally does not fall entirely before a date designated when the test is run")
    @Specification(value="The range of dwc:eventDate is not entirely the future and optionally does not entirely fall before a date designated when the test is run The field dwc:eventDate is not EMPTY.")
    public static EventDQValidation isEventDateInRange(@ActedUpon(value = "dwc:eventDate") String eventDate, Integer lowerBound, Boolean useLowerBound) {
    	EventDQValidation result = new EventDQValidation();
    	// TODO: Implementation may be too tightly bound to year, may need to extract first/last day for finer granularity test
    	if (lowerBound==null) { 
    		lowerBound = 1700;
    	}
    	if (useLowerBound==null) { 
    		useLowerBound = true;
    	}
    	Integer upperBound = LocalDateTime.now().getYear();
    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for dwc:eventDate.");
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		if (! DateUtils.eventDateValid(eventDate)) { 
    			result.addComment("Value provided for dwc:eventDate ["+eventDate+"] not recognized as a valid date.");
    			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		} else { 
    			int startYear = 0;
    			Interval interval = DateUtils.extractInterval(eventDate);
    			if (DateUtils.isRange(eventDate)) {
    				int endYear = interval.getEnd().getYear();
    				startYear = interval.getStart().getYear();
    				if (useLowerBound) { 
    					if (endYear<lowerBound|| startYear>upperBound) { 
    						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is not a range spanning part of the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is a range spanning at least part of " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					}    				
    				} else { 
    					if (startYear>upperBound) { 
    						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is not a range spanning part of the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is a range spanning at least part of " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					}       					
    				}
    			} else {
    				startYear = interval.getStart().getYear();
    				if (useLowerBound) { 
    					if (startYear<lowerBound || startYear>upperBound) { 
    						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' does not have a year in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' does not have a year in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} 
    				} else { 
    					if (startYear>upperBound) { 
    						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is not after  " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is after " + upperBound.toString() + " (current year).");
    					}     					
    				}
    			}
    			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		}
    	}
    	return result;
    }	    

    /**
     * Given an eventDate check to see if that event date crosses outside a range from a
     * specified lower bound (1700-01-01 by default) and the present.  
     * 
     * TODO: This may or may not be consistent with the standard test
     * TG2-VALIDATION_EVENTDATE_OUTOFRANGE 
     * 
     * 
     * @param eventDate to check
     * @param lowerBound integer representing the year to use as the lower boundary, if null, then uses 1700
     * @param useLowerBound if false, no lower limit, otherwise uses supplied lower bound.
     * @return an DQValidationResponse object describing whether the provided value is in range.
     */
    @Provides(value="urn:uuid:3cff4dc4-72e9-4abe-9bf3-8a30f1618432")
    @Validation( label = "VALIDATION_EVENTDATE_EXTENDSOUTOFRANGE", description="The range of dwc:eventDate does not extend into the future and optionally does not extend before a date designated when the test is run")
    @Specification(value="The range of dwc:eventDate does not extend into the future and optionally does not extend before a date designated when the test is run The field dwc:eventDate is not EMPTY.")
    public static EventDQValidation isEventDateAtAllInRange(@ActedUpon(value = "dwc:eventDate") String eventDate, Integer lowerBound, Boolean useLowerBound) {
    	EventDQValidation result = new EventDQValidation();
    	// TODO: Implementation may be too tightly bound to year, may need to extract first/last day for finer granularity test
    	if (lowerBound==null) { 
    		lowerBound = 1700;
    	}
    	if (useLowerBound==null) { 
    		useLowerBound = true;
    	}
    	Integer upperBound = LocalDateTime.now().getYear();
    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for dwc:eventDate.");
    		result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		if (! DateUtils.eventDateValid(eventDate)) { 
    			result.addComment("Value provided for dwc:eventDate ["+eventDate+"] not recognized as a valid date.");
    			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		} else { 
    			int startYear = 0;
    			Interval interval = DateUtils.extractInterval(eventDate);
    			if (DateUtils.isRange(eventDate)) {
    				int endYear = interval.getEnd().getYear();
    				startYear = interval.getStart().getYear();
    				if (endYear > upperBound ) { 
    					result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    					result.addComment("Provided value for dwc:eventDate '" + eventDate + "' extends outside the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    				} else { 
    					if (useLowerBound) { 
        					if (startYear<lowerBound) {
            					result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
            					result.addComment("Provided value for dwc:eventDate '" + eventDate + "' extends outside the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
        					} else { 
        						result.setResult(EnumDQValidationResult.COMPLIANT);
        						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is a range spanning at least part of " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");    						
        					}
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is a range spanning at least part of " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");    						
    					}
    				} 
    			} else {
    				startYear = interval.getStart().getYear();
    				if (useLowerBound) { 
    					if (startYear<lowerBound || startYear>upperBound) { 
    						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' does not have a year in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' does not have a year in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} 
    				} else { 
    					if (startYear>upperBound) { 
    						result.setResult(EnumDQValidationResult.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is not after  " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setResult(EnumDQValidationResult.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is after " + upperBound.toString() + " (current year).");
    					}     					
    				}
    			}
    			result.setResultState(EnumDQResultState.RUN_HAS_RESULT);
    		}
    	}
    	return result;
    }	




}
