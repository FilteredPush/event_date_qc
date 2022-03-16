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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.CompletenessValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.NumericalValue;
import org.datakurator.ffdq.model.ResultState;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Darwin Core Event eventDate related Data Quality Measures, Validations, and Enhancements. 
 *
 * Provides support for the following TDWG DQIG TG2 measures: 
 *
 * #140 MEASURE_EVENTDATE_PRECISIONINSECONDS 56b6c695-adf1-418e-95d2-da04cad7be53
 * 
 * Provides support for the following TDWG DQIG TG2 validations
 * 
 * #88 VALIDATION_EVENT_TEMPORAL_EMPTY 41267642-60ff-4116-90eb-499fee2cd83f
 * VALIDATION_EVENTDATE_EMPTY f51e15a6-a67d-4729-9c28-3766299d2985
 * #36 VALIDATION_EVENTDATE_OUTOFRANGE 3cff4dc4-72e9-4abe-9bf3-8a30f1618432
 * #49 VALIDATION_YEAR_EMPTY c09ecbf9-34e3-4f3e-b74a-8796af15e59f
 * VALIDATION_DAY_NOTSTANDARD 47ff73ba-0028-4f79-9ce1-ee7008d66498
 * VALIDATION_EVENTDATE_OUTOFRANGE 3cff4dc4-72e9-4abe-9bf3-8a30f1618432
 * VALIDATION_MONTH_NOTSTANDARD 01c6dafa-0886-4b7e-9881-2c3018c98bdc
 * VALIDATION_STARTDAYOFYEAR_OUTOFRANGE 85803c7e-2a5a-42e1-b8d3-299a44cafc46
 * VALIDATION_ENDDAYOFYEAR_OUTOFRANGE 9a39d88c-7eee-46df-b32a-c109f9f81fb8
 * #84 VALIDATION_YEAR_OUTOFRANGE ad0c8855-de69-4843-a80c-a5387d20fbc8
 * #125 VALIDATION_DAY_OUTOFRANGE 8d787cb5-73e2-4c39-9cd1-67c7361dc02e
 * #66 VALIDATION_EVENTDATE_NOTSTANDARD 4f2bf8fd-fc5c-493f-a44c-e7b16153c803
 * #67 VALIDATION_EVENT_INCONSISTENT 5618f083-d55a-4ac2-92b5-b9fb227b832f
 * 
 * Provides support for the following TDWG DQIG TG2 amendments 
 * 
 * AMENDMENT_DAY_STANDARDIZED b129fa4d-b25b-43f7-9645-5ed4d44b357b
 * AMENDMENT_MONTH_STANDARDIZED 2e371d57-1eb3-4fe3-8a61-dff43ced50cf
 * AMENDMENT_EVENTDATE_FROM_VERBATIM 6d0a0c10-5e4a-4759-b448-88932f399812
 * AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR eb0a44fa-241c-4d64-98df-ad4aa837307b
 * AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY 3892f432-ddd0-4a0a-b713-f2e2ecbd879d
 * #61 AMENDMENT_EVENTDATE_STANDARDIZED  	718dfc3c-cb52-4fca-b8e2-0e722f375da7
 * #52 AMENDMENT_EVENT_FROM_EVENTDATE 710fe118-17e1-440f-b428-88ba3f547d6d
 * 
 * Also Provides support for the following supplemental, under discussion or other tests: 
 *   DAY_IN_RANGE.  isDayInRange(@ActedUpon(value = "dwc:day") String day)   
 *      Is not the same as TG2-VALIDATION_DAY_OUTOFRANGE which includes year and month 
 *   DAY_MONTH_TRANSPOSED  dayMonthTransposition(@ActedUpon(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) 
 *   EVENTDATE_PRECISON_MONTH_OR_BETTER  TODO: not yet implemented.
 *   EVENTDATE_PRECISON_YEAR_OR_BETTER isEventDateYearOrLess(@ActedUpon(value="dwc:eventDate") String eventDate) 
 *                                also isEventDateJulianYearOrLess(@ActedUpon(value="dwc:eventDate") String eventDate) 
 * MEASURE_EVENT_NOTEMPTY 9dc97514-3b88-4afc-931d-5fc386be21ee (other)
 * 	An equivalent measure for TG2-VALIDATION_EVENT_EMPTY
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
 * @author mole
 *
 */
@Mechanism(
		value = "urn:uuid:a5fdf476-2e84-4004-bdc1-fc606a5ca2c8",
		label = "Kurator: Date Validator - DwCEventDQ:v2.1.0")
public class DwCEventDQ {
	
/**
 * Current definitions 2022Jan04	

	@Provides(value="urn:uuid:f51e15a6-a67d-4729-9c28-3766299d2985")
	@Validation( label = "VALIDATION_EVENTDATE_EMPTY", description="#33 Validation SingleRecord Completeness: eventdate empty")
	@Specification(value="COMPLIANT if dwc:eventDate is not EMPTY; otherwise NOT_COMPLIANT")
	--
	@Provides(value="urn:uuid:41267642-60ff-4116-90eb-499fee2cd83f")
	@Validation( label = "VALIDATION_EVENT_TEMPORAL_EMPTY", description="#88 Validation SingleRecord Completeness: event temporal empty")
	@Specification(value="COMPLIANT if at least one term needed to determine the event date exists and is not EMPTY; otherwise NOT_COMPLIANT")
	--
	@Provides(value="urn:uuid:4f2bf8fd-fc5c-493f-a44c-e7b16153c803")
	@Validation( label = "VALIDATION_EVENTDATE_NOTSTANDARD", description="#66 Validation SingleRecord Conformance: eventdate notstandard")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY; COMPLIANT if the value of dwc:eventDate is a valid ISO 8601-1:2019 date; otherwise NOT_COMPLIANT")
	--
	@Provides(value="urn:uuid:3cff4dc4-72e9-4abe-9bf3-8a30f1618432")
	@Validation( label = "VALIDATION_EVENTDATE_OUTOFRANGE", description="#36 Validation SingleRecord Conformance: eventdate outofrange")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY or if the value of dwc:eventDate is not a valid ISO 8601-1:2019 date; COMPLIANT if the range of dwc:eventDate is entirely within the parameter range, otherwise NOT_COMPLIANT")
	--
	@Provides(value="urn:uuid:5618f083-d55a-4ac2-92b5-b9fb227b832f")
	@Validation( label = "VALIDATION_EVENT_INCONSISTENT", description="#67 Validation SingleRecord Consistency: eventdate inconsistent")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY, or all of dwc:year, dwc:month, dwc:day, dwc:startDayOfYear and dwc:endDayOfYear are EMPTY; COMPLIANT if all of the following conditions are met 1) the provided value of year matches the start year of the range represented by eventDate or year is empty, and 2) the provided value in month matches the start month of the range represented by eventDate or month is empty, and 3) the provided value in day matches the start day of the range represented by eventDate or day is empty, and 4) the provided value in startDayOfYear matches the start day of the year of the range represented by eventDate or startDayOfYear is empty, and 5) the provided value in endDayOfYear matches the end day of the year the range represented by eventDate or endDayOfYear is empty; otherwise NOT_COMPLIANT.")
	--
	@Provides(value="urn:uuid:c09ecbf9-34e3-4f3e-b74a-8796af15e59f")
	@Validation( label = "VALIDATION_YEAR_EMPTY", description="#49 Validation SingleRecord Completeness: year empty")
	@Specification(value="COMPLIANT if dwc:year is not EMPTY; otherwise NOT_COMPLIANT")
	--
	@Provides(value="urn:uuid:ad0c8855-de69-4843-a80c-a5387d20fbc8")
	@Validation( label = "VALIDATION_YEAR_OUTOFRANGE", description="#84 Validation SingleRecord Conformance: year outofrange")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:year is not present, or is EMPTY or cannot be interpreted as an integer; COMPLIANT if the value of dwc:year is within the Parameter range; otherwise NOT_COMPLIANT")
	--
	@Provides(value="urn:uuid:01c6dafa-0886-4b7e-9881-2c3018c98bdc")
	@Validation( label = "VALIDATION_MONTH_NOTSTANDARD", description="#126 Validation SingleRecord Conformance: month notstandard")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:month is EMPTY; COMPLIANT if the value of dwc:month is an integer between 1 and 12 inclusive; otherwise NOT_COMPLIANT")
	--
	@Provides(value="urn:uuid:8d787cb5-73e2-4c39-9cd1-67c7361dc02e")
	@Validation( label = "VALIDATION_DAY_OUTOFRANGE", description="#125 Validation SingleRecord Conformance: day outofrange")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if (a) dwc:day is EMPTY (b) is not an integer, or (c) dwc:day is an integer between 29 and 31 inclusive and dwc:month is not an integer between 1 and 12, or (d) dwc:month is the integer 2 and dwc:day is the integer 29 and dwc:year is not a valid ISO 8601 year; COMPLIANT (a) if the value of the field dwc:day is an integer between 1 and 28 inclusive, or (b) dwc:day is an integer between 29 and 30 and dwc:month is an integer in the set (4,6,9,11), or (c) dwc:day is an integer between 29 and 31 and dwc:month is an integer in the set (1,3,5,7,8,10,12), or (d) dwc:day is the integer 29 and dwc:month is the integer 2 and dwc:year is a valid leap year (evenly divisible by 400 or (evenly divisible by 4 but not evenly divisible by 100)); otherwise NOT_COMPLIANT.")
	--
	@Provides(value="urn:uuid:47ff73ba-0028-4f79-9ce1-ee7008d66498")
	@Validation( label = "VALIDATION_DAY_NOTSTANDARD", description="#147 Validation SingleRecord Conformance: day notstandard")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; COMPLIANT if the value of the field dwc:day is an integer between 1 and 31 inclusive; otherwise NOT_COMPLIANT.")
	--
	@Provides(value="urn:uuid:85803c7e-2a5a-42e1-b8d3-299a44cafc46")
	@Validation( label = "VALIDATION_STARTDAYOFYEAR_OUTOFRANGE", description="#130 Validation SingleRecord Conformance: startdayofyear outofrange")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:startDayOfYear is EMPTY or if the value of dwc:startDayOfYear is equal to 366 and (dwc:eventDate is EMPTY or the value of dwc:eventDate can not be interpreted to find single year or a start year in a range); COMPLIANT if the value of dwc:startDayOfYear is an integer between 1 and 365, inclusive, or if the value of dwc:startDayOfYear is 366 and the start year interpreted from dwc:eventDate is a leap year; otherwise NOT_COMPLIANT")
	--
	@Provides(value="urn:uuid:9a39d88c-7eee-46df-b32a-c109f9f81fb8")
	@Validation( label = "VALIDATION_ENDDAYOFYEAR_OUTOFRANGE", description="#131 Validation SingleRecord Conformance: enddayofyear outofrange")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:endDayOfYear is EMPTY or if the value of dwc:endDayOfYear is equal to 366 and (dwc:eventDate is EMPTY or the value of dwc:eventDate can not be interpreted to find single year or an end year in a range); COMPLIANT if the value of dwc:endDayOfYear is an integer between 1 and 365 inclusive, or if the value of dwc:endDayOfYear is 366 and the end year interpreted from dwc:eventDate is a leap year; otherwise NOT_COMPLIANT")
	--
	@Provides(value="urn:uuid:3892f432-ddd0-4a0a-b713-f2e2ecbd879d")
	@Amendment( label = "AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY", description="#93 Amendment SingleRecord Completeness: eventdate from yearmonthday")
	@Specification(value="INTERNAL _PREREQUISITES_NOT_MET if dwc:eventDate is not EMPTY or dwc:year is EMPTY or is uninterpretable as a valid year; AMENDED if the value of dwc:eventDate was interpreted from the values in dwc:year, dwc:month and dwc:day; otherwise NOT_AMENDED")
	--
	@Provides(value="urn:uuid:6d0a0c10-5e4a-4759-b448-88932f399812")
	@Amendment( label = "AMENDMENT_EVENTDATE_FROM_VERBATIM", description="#86 Amendment SingleRecord Completeness: eventdate from verbatim")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is not EMPTY or the value of dwc:verbatimEventDate is EMPTY or not unambiguously interpretable as an ISO 8601-1:2019 date; AMENDED if the value of dwc:eventDate was unambiguously interpreted from dwc:verbatimEventDate; otherwise NOT_AMENDED")
	--
	@Provides(value="urn:uuid:b129fa4d-b25b-43f7-9645-5ed4d44b357b")
	@Amendment( label = "AMENDMENT_DAY_STANDARDIZED", description="#127 Amendment SingleRecord Conformance: day standardized")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; AMENDED if the value of dwc:day was unambiguously interpreted to be an integer between 1 and 31 inclusive; otherwise NOT_AMENDED")
	--
	@Provides(value="urn:uuid:2e371d57-1eb3-4fe3-8a61-dff43ced50cf")
	@Amendment( label = "AMENDMENT_MONTH_STANDARDIZED", description="#128 Amendment SingleRecord Conformance: month standardized")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:month is EMPTY; AMENDED if the value of dwc:month was able to be interpreted as a integer between 1 and 12 inclusive; otherwise NOT_AMENDED")
	--
	@Provides(value="urn:uuid:eb0a44fa-241c-4d64-98df-ad4aa837307b")
	@Amendment( label = "AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR", description="#132 Amendment SingleRecord Completeness: eventdate from yearstartdayofyearenddayofyear")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate was not EMPTY or dwc:year was EMPTY or both dwc:startDayOfYear and dwc:endDayOfYear were EMPTY or the values were not interpretable; AMENDED if the value of dwc:eventDate was FILLED_IN from the values in dwc:year, dwc:startDayOfYear and dwc:endDayOfYear; otherwise NOT_AMENDED")
	--
	@Provides(value="urn:uuid:39bb2280-1215-447b-9221-fd13bc990641")
	@Amendment( label = "AMENDMENT_DATEIDENTIFIED_STANDARDIZED", description="#26 Amendment SingleRecord Conformance: dateidentified standardized")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is EMPTY; AMENDED if the value of dwc:dateIdentified was altered to unambiguously conform with the ISO 8601-1:2019 date format; otherwise NOT_AMENDED")
	--
	@Provides(value="urn:uuid:710fe118-17e1-440f-b428-88ba3f547d6d")
	@Amendment( label = "AMENDMENT_EVENT_FROM_EVENTDATE", description="#52 Amendment SingleRecord Completeness: event from eventdate")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY or does not contain a valid ISO 8601-1:2019 date; AMENDED if one or more EMPTY terms of the dwc:Event class (dwc:year, dwc:month, dwc:day, dwc:startDayOfYear, dwc:endDayOfYear) have been filled in from a valid unambiguously interpretable value in dwc:eventDate and eventDate is wholly within one year; otherwise NOT_AMENDED")
	--
	@Provides(value="urn:uuid:718dfc3c-cb52-4fca-b8e2-0e722f375da7")
	@Amendment( label = "AMENDMENT_EVENTDATE_STANDARDIZED", description="#61 Amendment SingleRecord Conformance: eventdate standardized")
	@Specification(value="INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY; AMENDED if the value of dwc:eventDate was changed to unambiguously conform with an ISO 8601-1:2019 date; otherwise NOT_AMENDED")
*/
	
	private static final Log logger = LogFactory.getLog(DwCEventDQ.class);
	
    /**
     * #140 Measure SingleRecord Resolution: eventdate precisioninseconds
	 * Measure the duration of an event date in seconds.
     *
     * Provides: MEASURE_EVENTDATE_PRECISIONINSECONDS
     *
     * @param eventDate the provided dwc:eventDate to measure duration in seconds
     * @return DQResponse the response of type NumericalValue  to return
	 *   which if state is RUN_HAS_RESULT has a value of type Long.
     */
    @Provides("56b6c695-adf1-418e-95d2-da04cad7be53")
    public static DQResponse<NumericalValue> measureEventdatePrecisioninseconds(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<NumericalValue> result = new DQResponse<NumericalValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY or does not
        // contain a valid ISO 8601-1:2019 date; otherwise RUN_HAS_RESULT with the
        // result value being the length of the period expressed in the dwc:eventDate
        // in seconds 
        
        // In notes, exclude leap seconds.  
        // The joda and java Time libraries exclude leap seconds.
        
    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for eventDate.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		try { 
    			long seconds = DateUtils.measureDurationSeconds(eventDate);
    			result.setValue(new NumericalValue(seconds));
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
	 * Measure the completeness of an event date.   Non-Core.
	 *
	 * Provides: EVENT_DATE_COMPLETENESS
	 *
	 * @param eventDate to check if empty
	 * @return EventDQMeasurement object, which if state is COMPLETE has a value of type Long.
	 */
	@Provides(value = "urn:uuid:0a59e03f-ebb5-4df3-a802-2e444de525b5")
	@Measure(dimension = Dimension.COMPLETENESS, label = "EVENT_DATE_COMPLETENESS", description = "Measure the completeness of an event date.")
	@Specification(value = "For values of dwc:eventDate, check is not empty.")

	public static DQResponse<CompletenessValue> measureEventCompleteness(@ActedUpon(value = "dwc:eventDate") String eventDate) {
		DQResponse<CompletenessValue> result = new DQResponse<>();

		if (DateUtils.isEmpty(eventDate)) {
			result.addComment("No value provided for eventDate.");
			result.setValue(CompletenessValue.NOT_COMPLETE);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for eventDate.");
			result.setValue(CompletenessValue.COMPLETE);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}

		return result;
	}

	/**
	 * Test to see whether or not a dwc:eventDate contains any value.
	 * 
	 * #33 Validation SingleRecord Completeness: eventdate empty
	 *
	 * Provides: VALIDATION_EVENTDATE_EMPTY
	 *
	 * @param eventDate the provided dwc:eventDate to evaluate for emptyness
	 * @return DQResponse the response of type ComplianceValue  to return
	 */
	@Provides("f51e15a6-a67d-4729-9c28-3766299d2985")
	public static DQResponse<ComplianceValue> validationEventdateEmpty(@ActedUpon("dwc:eventDate") String eventDate) {
		DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

		// Specification
		// COMPLIANT if dwc:eventDate is not EMPTY; otherwise NOT_COMPLIANT 

		if (DateUtils.isEmpty(eventDate)) {
			result.addComment("No value provided for eventDate.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for eventDate.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
		return result;
	}

	
	/**
	 * Test to see whether or not a dwc:year contains any value.
	 * 
     * #49 Validation SingleRecord Completeness: year empty
     * 
     * Provides VALIDATION_YEAR_EMPTY
	 * 
     * @param year the provided dwc:year to evaluate for the presence of some value
     * @return DQVResponse of type ComplianceValue describing whether any value is present dwc:year.
	 */
	@Provides(value="c09ecbf9-34e3-4f3e-b74a-8796af15e59f")
	public static DQResponse<ComplianceValue> validationYearEmpty(@ActedUpon(value = "dwc:year") String year) {
		DQResponse<ComplianceValue> result = new DQResponse<>();

        // Specification
        // COMPLIANT if dwc:year is not EMPTY; otherwise NOT_COMPLIANT 
        //
		
		if (DateUtils.isEmpty(year)) {
			result.addComment("No value provided for dwc:year.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else { 
			result.addComment("Some value provided for dwc:year.");
			result.setValue(ComplianceValue.COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
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
     *    resultState is AMENDED if a new value is proposed.
     */

	@Provides("urn:uuid:da63f836-1fc6-4e96-a612-fa76678cfd6a")

	@Validation(
			label = "EVENT_DATE_AND_VERBATIM_CONSISTENT",
			description = "Test to see if the eventDate and verbatimEventDate are consistent.")

	@Specification("If a dwc:eventDate is not empty and the verbatimEventDate is not empty " +
			       "compare the value of dwc:eventDate with that of dwc:verbatimEventDate, " +
			       "and assert Compliant if the two represent the same date or date range.")

    public static DQResponse<ComplianceValue> isEventDateConsistentWithVerbatim(
    		@ActedUpon(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:verbatimEventDate") String verbatimEventDate) {

		DQResponse<ComplianceValue> result = new DQResponse<>();

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
					result.setValue(ComplianceValue.COMPLIANT);
					result.setResultState(ResultState.RUN_HAS_RESULT);
					result.addComment("Provided value for eventDate '" + eventDate + "' represents the same range as verbatimEventDate '"+verbatimEventDate+"'.");
				} else {
					result.setValue(ComplianceValue.NOT_COMPLIANT);
					result.setResultState(ResultState.RUN_HAS_RESULT);
					result.addComment("Provided value for eventDate '" + eventDate + "' does not represent the same range as verbatimEventDate '"+verbatimEventDate+"'.");
				}
			} else {
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment("Unable to extract a date from verbatimEventDate: " + verbatimEventDate);
			}
		} else {
			if (DateUtils.isEmpty(eventDate)) {
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment("eventDate does not contains a value.");
			}
			if (DateUtils.isEmpty(verbatimEventDate)) {
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment("verbatimEventDate does not contains a value.");
			}
		}

		return result;
	}

    /**
     * Deprecated, use: amendmentEventdateFromVerbatim
     */
	@Deprecated 
    public static DQResponse<AmendmentValue>  extractDateFromVerbatim(
    		@ActedUpon(value = "dwc:eventDate") String eventDate, 
    		@Consulted(value = "dwc:verbatimEventDate") String verbatimEventDate) {
		return amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
    }
    /**
     * #86 Amendment SingleRecord Completeness: eventdate from verbatim
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_VERBATIM
     * 
     * If a dwc:eventDate is empty and the verbatimEventDate is not empty, try to populate the 
     * eventDate from the verbatim value.
     *
     * @param eventDate the provided dwc:eventDate to try to populate if empty
     * @param verbatimEventDate the provided dwc:verbatimEventDate from which to try to parse an eventDate
     * @return DQResponse the response of type AmendmentValue to return, with a value containing
     *  a key for dwc:eventDate and a resultState is AMENDED if a new value is proposed.
     */
    @Provides("6d0a0c10-5e4a-4759-b448-88932f399812")
    public static DQResponse<AmendmentValue> amendmentEventdateFromVerbatim(
    		@ActedUpon("dwc:eventDate") String eventDate, 
    		@Consulted("dwc:verbatimEventDate") String verbatimEventDate) {
    	DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is not EMPTY 
        // or the value of dwc:verbatimEventDate is EMPTY or not unambiguously 
        // interpretable as an ISO 8601-1:2019 date; AMENDED if the 
        // value of dwc:eventDate was unambiguously interpreted from 
        // dwc:verbatimEventDate; otherwise NOT_AMENDED 
		
		if (DateUtils.isEmpty(eventDate)) {
			if (!DateUtils.isEmpty(verbatimEventDate)) {
				EventResult extractResponse = DateUtils.extractDateFromVerbatimER(verbatimEventDate);
				if (!extractResponse.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
						(extractResponse.getResultState().equals(EventResult.EventQCResultState.RANGE) ||
								extractResponse.getResultState().equals(EventResult.EventQCResultState.DATE)) )
				{
					Map<String, String> extractedValues = new HashMap<>();
					extractedValues.put("dwc:eventDate", extractResponse.getResult());
					result.setValue(new AmendmentValue(extractedValues));
					result.setResultState(ResultState.AMENDED);
				} else if (!extractResponse.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
						( extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS) ||
								extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)))
				{
					result.setResultState(ResultState.NOT_AMENDED);
					if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
				        //TODO:  Implement specification
				        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is not EMPTY 
				        // or the value of dwc:verbatimEventDate is EMPTY or not unambiguously 
				        // interpretable as an ISO 8601-1:2019 date; AMENDED if the 
				        // value of dwc:eventDate was unambiguously interpreted from 
				        //dwc:verbatimEventDate; otherwise NOT_AMENDED 				result.addComment("Supplied verbatimEventDate [" + verbatimEventDate + "] is ambiguous.");
						result.addComment(extractResponse.getComment());
					} else {
						if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
							result.addComment("Interpretation of verbatimEventDate [" + verbatimEventDate + "] is suspect.");
							result.addComment(extractResponse.getComment());
						}
					}
				} else {
					result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
					result.addComment("Unable to extract a date from " + verbatimEventDate);
				}
			} else {
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment("verbatimEventDate does not contains a value.");
			}
		} else {
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
			result.addComment("eventDate contains a value, not changing.");
		}

		if (result.getValue() == null) {
			result.setValue(new AmendmentValue(new HashMap<String, String>()));
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
     *    resultState is AMENDED if a new value is proposed.
     */
    //Provides urn:uuid:6d0a0c10-5e4a-4759-b448-88932f399812 and eb0a44fa-241c-4d64-98df-ad4aa837307b and 3892f432-ddd0-4a0a-b713-f2e2ecbd879d
    @Provides(value = "urn:uuid:016c6ee6-c528-4435-87ce-1a9dec9c7ae2")
	@Amendment(label = "EVENT_DATE_FROM_PARTS", description = "Try to populate the event date from the verbatim and other atomic parts (day, month, year, etc).")
	@Specification(value = "If a dwc:eventDate is empty and the verbatimEventDate is not empty fill in dwc:eventDate " +
			"based on value from dwc:verbatimEventDate, dwc:year dwc:month, dwc:day, dwc:start/endDayOfYear.")
    public static DQResponse<AmendmentValue> extractDateFromParts(@ActedUpon(value = "dwc:eventDate") String eventDate,
    		 @Consulted(value = "dwc:verbatimEventDate") String verbatimEventDate,
    		 @Consulted(value = "dwc:startDayOfYear") String startDayOfYear,
    		 @Consulted(value = "dwc:endDayOfYear") String endDayOfYear,
    		 @Consulted(value = "dwc:year") String year,
    		 @Consulted(value = "dwc:month") String month,
    		 @Consulted(value = "dwc:day") String day
    		 ) {
		DQResponse<AmendmentValue> result = new DQResponse<>();

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
					Map<String, String> extractedValues = new HashMap<>();
					extractedValues.put("dwc:eventDate", extractResponse.getResult());

					result.setValue(new AmendmentValue(extractedValues));

					if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
						result.setResultState(ResultState.AMBIGUOUS);
						result.addComment(extractResponse.getComment());
					} else {
						if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
							result.addComment("Interpretation of verbatimEventDate [" + verbatimEventDate + "] is suspect.");
							result.addComment(extractResponse.getComment());
						}
						result.setResultState(ResultState.AMENDED);
					}
				} else {
					result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
					result.addComment("Unable to extract a date from " + verbatimEventDate);
				}
			} else {
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment("verbatimEventDate does not contains a value.");
			}
		} else {
			result.setResultState(ResultState.NOT_AMENDED);
			result.addComment("eventDate contains a value, not changing.");
		}
		return result;
    }

    //@Provides(value = "UPSTREAM_EVENTDATE_FILLED_IN_FROM_START_END")
	@Provides(value = "urn:uuid:e4ddf9bc-cd10-46cc-b307-d6c7233a240a")
	@Amendment(label = "UPSTREAM_EVENTDATE_FILLED_IN_FROM_START_END", description = "Try to populate the event date from non-Darwin Core start date and end date terms.")
	@Specification(value = "If a dwc:eventDate is empty and an event date can be inferred from start date and end date, fill in dwc:eventDate " +
			"based on the values in the start and end dates.  Will not propose a change if dwc:eventDate contains a value.")
    public static DQResponse<AmendmentValue> extractDateFromStartEnd(@ActedUpon(value = "dwc:eventDate") String eventDate, @Consulted(value = "dwc:startDate") String startDate, @Consulted(value="dwc:endDate") String endDate) {
		DQResponse<AmendmentValue> result = new DQResponse<>();

		if (DateUtils.isEmpty(eventDate)) {
			String response = DateUtils.createEventDateFromStartEnd(startDate, endDate);
			if (DateUtils.isEmpty(response)) {
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment("Unable to extract a date from " + startDate + " and " + endDate);
			} else {
				Map<String, String> extractedValues = new HashMap<>();
				extractedValues.put("dwc:eventDate", response);

				result.setValue(new AmendmentValue(extractedValues));
				result.setResultState(ResultState.AMENDED);
			}
		} else {
			result.setResultState(ResultState.NOT_AMENDED);
			result.addComment("eventDate contains a value, not changing.");
		}
		return result;
    }

    /**
     *
     * 
     */
	@Deprecated
	public static DQResponse<ComplianceValue> isEventDateValid(@ActedUpon(value = "dwc:eventDate") String eventdate) {
        return validationEventdateNotstandard(eventdate);
    }
    /**
     * Test to see whether a provided dwc:eventDate is a validly formated ISO date.
     * 
     * #66 Validation SingleRecord Conformance: eventdate notstandard
     *
     * Provides: VALIDATION_EVENTDATE_NOTSTANDARD
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("4f2bf8fd-fc5c-493f-a44c-e7b16153c803")
    public static DQResponse<ComplianceValue> validationEventdateNotstandard(
    		@ActedUpon(value = "dwc:eventDate") String eventdate) {
		DQResponse<ComplianceValue> result = new DQResponse<>();
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY; 
        // COMPLIANT if the value of dwc:eventDate is a valid ISO 8601-1:2019 
        // date; otherwise NOT_COMPLIANT 

    	if (DateUtils.isEmpty(eventdate)) {
    		result.addComment("No value provided for dwc:eventDate.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else {
    		try {
    	        if (DateUtils.eventDateValid(eventdate)) {
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dwc:eventDate '" + eventdate + "' is formated as an ISO date. ");
    			} else {
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for dwc:eventDate '" + eventdate + "' is not a validly formatted ISO date .");
    			}
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    		} catch (Exception e) {
    			result.setValue(ComplianceValue.NOT_COMPLIANT);
    			result.addComment("Provided value for dwc:eventDate '" + eventdate + "' is unable to be interpreted as an ISO date .");
    			logger.debug(e.getMessage());
    			result.addComment(e.getMessage());
    		}
    	}
    	return result;
    }

    @Deprecated
    public DQResponse<AmendmentValue> correctEventDateFormat(@ActedUpon("dwc:eventDate") String eventDate) {
        return amendmentEventdateStandardized(eventDate);
    }
    
    /**
     * Given an event date, check to see if it is empty or contains a valid date value.  If it contains
     * a value that is not a valid date, propose a properly formatted eventDate as an amendment.
     *
     * #61 Amendment SingleRecord Conformance: eventdate standardized
     *
     * Provides: AMENDMENT_EVENTDATE_STANDARDIZED
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     *    with a value containing a key for dwc:eventDate and a
     *    resultState is AMENDED if a new value is proposed.
     */
    @Provides("718dfc3c-cb52-4fca-b8e2-0e722f375da7")
    public static DQResponse<AmendmentValue> amendmentEventdateStandardized(
    		@ActedUpon(value = "dwc:eventDate") String eventDate) {
		DQResponse<AmendmentValue> result = new DQResponse<>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY; 
        // AMENDED if the value of dwc:eventDate was changed to unambiguously 
        // conform with an ISO 8601-1:2019 date; otherwise NOT_AMENDED 
        
		if (DateUtils.eventDateValid(eventDate)) {
			result.setResultState(ResultState.NOT_AMENDED);
			result.addComment("eventDate contains a correctly formatted date, not changing.");
		} else {
			if (DateUtils.isEmpty(eventDate)) {
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment("eventDate does not contains a value.");
			} else {
				EventResult extractResponse = DateUtils.extractDateFromVerbatimER(eventDate);
				logger.debug(extractResponse.getResultState().toString());
				if (extractResponse.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) { 
					result.setResultState(ResultState.NOT_AMENDED);
					result.addComment("Unable to extract a date from eventDate [" + eventDate + "]");
				} else if ( extractResponse.getResultState().equals(EventResult.EventQCResultState.RANGE) ||
							extractResponse.getResultState().equals(EventResult.EventQCResultState.DATE) ||
							extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS) ||
							extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)
						)
				{
					if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
						result.setResultState(ResultState.NOT_AMENDED);
						result.addComment("Interpretation of eventDate [" + eventDate + "] is ambiguous, not changing.");
						result.addComment(extractResponse.getComment());
					} else if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
						result.addComment("Interpretation of eventDate [" + eventDate + "] is suspect, not changing.");
						result.addComment(extractResponse.getComment());
					} else {
						result.setResultState(ResultState.AMENDED);
						Map<String, String> correctedValues = new HashMap<>();
						correctedValues.put("dwc:eventDate", extractResponse.getResult());
						result.setValue(new AmendmentValue(correctedValues));
						result.addComment("Provided eventDate [" + eventDate + "] interpreted as ["+extractResponse.getResult() +"].");
					}
				} else {
					result.setResultState(ResultState.NOT_AMENDED);
					result.addComment("Unable to extract a date from " + eventDate);
				}
			}
		}
		if (result.getValue() == null) {
			result.setValue(new AmendmentValue(new HashMap<String, String>()));
		}
		return result;
    }

    /**
     * 
     * Test to see whether a provided day is an integer in the range of values that can be
     * a day of a month.
     * 
     * #147 Validation SingleRecord Conformance: day notstandard
     *
     * Provides: VALIDATION_DAY_NOTSTANDARD
     *
     * @param day the provided dwc:day to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     *    COMPLIANT if day is an integer in the range 1 to 31 inclusive, otherwise NOT_COMPLIANT
     * see DwCEventDQ.validationDayOutofrange(String year, String month, String day) 
	 *  providing VALIDATION_DAY_OUTOFRANGE
     */
    @Provides("47ff73ba-0028-4f79-9ce1-ee7008d66498")
    public static DQResponse<ComplianceValue> validationDayNotstandard(@ActedUpon("dwc:day") String day) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; COMPLIANT
        // if the value of the field dwc:day is an integer between
        // 1 and 31 inclusive; otherwise NOT_COMPLIANT.

		if (DateUtils.isEmpty(day)) {
			result.addComment("No value provided for day.");
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
		} else {
			try {
				int numericDay = Integer.parseInt(day.trim());
				if (DateUtils.isDayInRange(numericDay)) {
					result.setValue(ComplianceValue.COMPLIANT);
					result.addComment("Provided value for day '" + day + "' is an integer in the range 1 to 31.");
				} else {
					result.setValue(ComplianceValue.NOT_COMPLIANT);
					result.addComment("Provided value for day '" + day + "' is not an integer in the range 1 to 31.");
				}
				result.setResultState(ResultState.RUN_HAS_RESULT);
			} catch (NumberFormatException e) {
				logger.debug(e.getMessage());
				result.setValue(ComplianceValue.NOT_COMPLIANT);
				result.setResultState(ResultState.RUN_HAS_RESULT);
				result.addComment(e.getMessage());
			}
		}
		return result;
    }

 
	@Deprecated
    public static DQResponse<ComplianceValue> isMonthInRange(@ActedUpon(value="dwc:month") String month) {
		return validationMonthNotstandard(month);
	}
    /**
     * Test to see whether a provided month is in the range of integer values that form months of the year.
     *
     * #126 Validation SingleRecord Conformance: month notstandard
     *
     * Provides: VALIDATION_MONTH_NOTSTANDARD
     *
     * @param month the provided dwc:month string to evaluate
     * @return COMPLIANT if month is an integer in the range 1 to 12 inclusive, NOT_COMPLIANT if month is
     *     an integer outside this range, INTERNAL_PREREQUSISITES_NOT_MET if month is empty or an integer
     *     cannot be parsed from month.
     */
    @Provides("01c6dafa-0886-4b7e-9881-2c3018c98bdc")
    public static DQResponse<ComplianceValue> validationMonthNotstandard(@ActedUpon("dwc:month") String month) {
    	DQResponse<ComplianceValue> result = new DQResponse<>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:month is EMPTY; COMPLIANT 
        // if the value of dwc:month is interpretable as an integer 
        // between 1 and 12 inclusive; otherwise NOT_COMPLIANT 
    	
    	if (DateUtils.isEmpty(month)) {
    		result.addComment("No value provided for month.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else {
    		try {
    			int numericMonth = Integer.parseInt(month.trim());
    			if (DateUtils.isMonthInRange(numericMonth)) {
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for month '" + month + "' is an integer in the range 1 to 12.");
    			} else {
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for month '" + month + "' is not an integer in the range 1 to 12.");
    			}
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    		} catch (NumberFormatException e) {
    			logger.debug(e.getMessage());
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    			result.setValue(ComplianceValue.NOT_COMPLIANT);
    			result.addComment(e.getMessage());
    		}
    	}
    	return result;
    }

    //@Provides(value = "EVENTDATE_PRECISON_JULIAN_YEAR_OR_BETTER")
	@Provides(value = "urn:uuid:fd00e6be-45e4-4ced-9f3d-5cde30b21b69")
	@Validation(label = "EVENT_DATE_PRECISON_JULIAN_YEAR_OR_BETTER", description = "Test to see whether a provided event date " +
			"has a duration less than or equal to a standard astronomical Julian year.")
	@Specification(value = "Compliant if event date has a duration equal to or less than a = 31557600 seconds, otherwise not compliant. " +
			"Internal prerequisites not met if eventDate is empty or not valid.")
    public static DQResponse<ComplianceValue> isEventDateJulianYearOrLess(@ActedUpon(value="dwc:eventDate") String eventDate) {
    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for eventDate.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else {
    		if (DateUtils.eventDateValid(eventDate)) {
    			logger.debug(eventDate);
    			try {
    				logger.debug(DateUtils.measureDurationSeconds(eventDate));
    				if (DateUtils.measureDurationSeconds(eventDate)<= 31557600) {
    					result.setValue(ComplianceValue.COMPLIANT);
    					result.addComment("Provided value for eventDate '" + eventDate + "' has a duration less than or equal to one Julian year of 365.25 days.");
    				}  else {
    					result.setValue(ComplianceValue.NOT_COMPLIANT);
    					result.addComment("Provided value for eventDate '" + eventDate + "' has a duration more than one Julian year of 365.25 days.");
    				}
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} catch (TimeExtractionException e) {
    				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("Unabel to extract duration from provided dwc:eventDate value." + e.getMessage());
    			}
    		} else {
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("provided dwc:eventDate not recognized as a valid date value.");
    		}
    	}
    	return result;
    }


    @Provides(value = "urn:uuid:31d463b4-2a1c-4b90-b6c7-73459d1bad6d")
	@Validation(label = "EVENT_DATE_YEAR_INCOMPLETE", description = "Test to see whether a provided event date " +
			"has a duration less than or equal to a calendar year.")
	@Specification(value = "Compliant if event date has a duration equal to or less than 365 days if a standard year, 366 days if a leap year. " +
			"Internal prerequisites not met if eventDate is empty or not valid.")
    public static DQResponse<ComplianceValue> isEventDateYearOrLess(@ActedUpon(value="dwc:eventDate") String eventDate) {
    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for eventDate.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else {
    		try { 
    		if (DateUtils.includesLeapDay(eventDate)) {
    			if (DateUtils.eventDateValid(eventDate)) {
    				if (DateUtils.measureDurationSeconds(eventDate)<= 31622400) {
    					result.setValue(ComplianceValue.COMPLIANT);
    					result.addComment("Provided value for eventDate '" + eventDate + "' contains a leap day and has a duration less than or equal to one calendar year of 366 days.");
    				}  else {
    					result.setValue(ComplianceValue.NOT_COMPLIANT);
    					result.addComment("Provided value for eventDate '" + eventDate + "' contains a leap day has a duration more than one calendar year of 366 days.");
    				}
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} else {
    				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("provided dwc:eventDate not recognized as a valid date value.");
    			}
    		} else {
    			if (DateUtils.eventDateValid(eventDate)) {
    				if (DateUtils.measureDurationSeconds(eventDate)<= 31536000) {
    					result.setValue(ComplianceValue.COMPLIANT);
    					result.addComment("Provided value for eventDate '" + eventDate + "' does not contain a leap day and has a duration less than or equal to one calendar year of 365 days.");
    				}  else {
    					result.setValue(ComplianceValue.NOT_COMPLIANT);
    					result.addComment("Provided value for eventDate '" + eventDate + "' does not contain a leap day has a duration more than one calendar year of 365 days.");
    				}
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    			} else {
    				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("provided dwc:eventDate not recognized as a valid date value.");
    			}
    		}
    		} catch (TimeExtractionException e) { 
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("Unable to extract duration from provided dwc:eventDate value." + e.getMessage());
    		}
    	}
    	return result;
    }


        //TODO:  Implement specification
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
        //evenly divisible by 100)); otherwise NOT_COMPLIANT." 

    @Deprecated
    public static DQResponse<ComplianceValue> isDayPossibleForMonthYear(@Consulted(value="dwc:year") String year, @Consulted(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) {
        return validationDayOutofrange(year, month, day);
    }
    
    /**
     * Check if a value for day is consistent with a provided month and year.
     *
     * #125 Validation SingleRecord Conformance: day outofrange
     *
     * Provides: VALIDATION_DAY_OUTOFRANGE
     * (previous draft names include DAY_POSSIBLE_FOR_MONTH_YEAR and RECORDED_DATE_MISMATCH)
     *
     * @param year the provided dwc:year for the month and day
     * @param month the provided dwc:month for the day
     * @param day the provided dwc:day to evaluate 
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("8d787cb5-73e2-4c39-9cd1-67c7361dc02e")
    public static DQResponse<ComplianceValue> validationDayOutofrange(@ActedUpon("dwc:year") String year, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day) {
    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

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
    	
    	if (DateUtils.isEmpty(day)) { 
    		//IPNM (a) dwc:day is EMPTY, 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	    result.addComment("Provided value for dwc:day [" + day + "] is EMPTY.");;
    	} else { 
    		try { 
    			Integer dayInteger = Integer.parseInt(day.trim());
    			if (dayInteger>=1 && dayInteger<=28) {
    				// COMP (a) the value of dwc:day is interpretable as an integer between 1 and 28 inclusive, 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dwc:day [" + day + "] is in the range 1-28 inclusive.");;
    			} else if (dayInteger < 1) {
    				// otherwise
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for dwc:day [" + day + "] is less than 1.");;
    			} else if (dayInteger > 31) {
    				// otherwise 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for dwc:day [" + day + "] is greater than 31.");;
    			} else { 
    				try {
    					if (DateUtils.isEmpty(month)) { 
    						throw new NumberFormatException();
    					}
    					Integer monthInteger = Integer.parseInt(month.trim());
    					if (dayInteger>=29 && dayInteger<=30 && 
    							(monthInteger==4 || monthInteger==6 || monthInteger==9 || monthInteger==11))
    					{ 
    						// COMP (b) dwc:day is interpretable as an integer between 29 and 30 and dwc:month 
    						// is interpretable as an integer in the set (4,6,9,11),
    						result.setResultState(ResultState.RUN_HAS_RESULT);
    						result.setValue(ComplianceValue.COMPLIANT);
    						result.addComment("Provided value for dwc:day [" + day + "] is in the range 29-30 and month is in the set (4,6,9,11).");;
    					} else if (dayInteger>=29 && dayInteger<=31 && 
    							(monthInteger==1 || monthInteger==3 || monthInteger==5 || monthInteger==7 || monthInteger==8 || monthInteger==10 || monthInteger==12 )) 
    					{ 
    						///COMP (c) dwc:day is interpretable as an integer between 29 and 
    				        // 31 and dwc:month is interpretable as an integer in the set 
    				        // (1,3,5,7,8,10,12),
    						result.setResultState(ResultState.RUN_HAS_RESULT);
    						result.setValue(ComplianceValue.COMPLIANT);
    						result.addComment("Provided value for dwc:day [" + day + "] is in the range 29-31 and month is in the set (1,3,5,7,8,10,12).");;
    					} else if (dayInteger==29 && monthInteger==2) { 
    						// leap day, check if in leap year
    						try {     					
    							if (DateUtils.isEmpty(year)) { 
    								throw new NumberFormatException();
    							}
    							Integer yearInteger = Integer.parseInt(year.trim());
    							if ( ((yearInteger%400)==0) || ((yearInteger % 4)==0 && (yearInteger % 100)!=0)) { 
    								// COMP (d) dwc:day is interpretable as the 
    								// integer 29 and dwc:month is interpretable as the integer 
    								// 2 and dwc:year is interpretable as is a valid leap year 
    								// (evenly divisible by 400 or (evenly divisible by 4 but not 
    								// evenly divisible by 100)); 
    								result.setResultState(ResultState.RUN_HAS_RESULT);
    								result.setValue(ComplianceValue.COMPLIANT);
    								result.addComment("Provided value for dwc:day [" + day + "] is 29 and dwc:month ["+month+"] is 2 making it a leap day, and dwc:year ["+year+"] is a leap year.");;
    							} else { 
    								// otherwise NOT_COMPLIANT.
    								result.setResultState(ResultState.RUN_HAS_RESULT);
    								result.setValue(ComplianceValue.NOT_COMPLIANT);
    								result.addComment("Provided value for dwc:day [" + day + "] is 29 and dwc:month ["+month+"] is 2 making it a leap day, but dwc:year ["+year+"] is not a leap year.");;
    							}
    	    				} catch (NumberFormatException e) { 
    	    					// IPNM (d) dwc:month is interpretable as the 
    	    					// integer 2 and dwc:day is interpretable as the integer 29 
    	    					// and dwc:year is not interpretable as a valid ISO 8601 year; 
    	    					logger.debug(e.getMessage());
    	    					result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	    					result.addComment("Provided value for dwc:day [" + day + "] is 29 and dwc:month ["+month+"] is 2 making it a leap day, but dwc:year ["+year+"] could not be interpreted as an integer.");;
    	    				}
    					} else {
    						// otherwise
    						result.setResultState(ResultState.RUN_HAS_RESULT);
    						result.setValue(ComplianceValue.NOT_COMPLIANT);
    	    				result.addComment("Provided values for dwc:day [" + day + "] dwc:month ["+month+"] and dwc:year ["+year+"] didn't match the values for a valid day.");;
    					}
    				} catch (NumberFormatException e) { 
    					// IPNM  (c) dwc:day is interpretable as an integer between 29 and 31 
    					// inclusive and dwc:month is not interpretable as an integer 
    					// between 1 and 12, or  
    					logger.debug(e.getMessage());
    					result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    					result.addComment("Provided value for dwc:day [" + day + "] is between 29 and 31, but dwc:month ["+month+"] could not be interpreted as an integer.");;
    				}
    			}
    		} catch (NumberFormatException e) { 
    			// IPNM (b) dwc:day is not interpretable as an integer, 
    			logger.debug(e.getMessage());
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("Provided value for dwc:day [" + day + "] could not be interpreted as an integer.");;
    		}
    		
    	}
    	
    	/*
    	 * Original construct and validate a date based implementation.
    	DQResponse<ComplianceValue> monthResult =  validationMonthNotstandard(month);
    	DQResponse<ComplianceValue> dayResult =  validationDayNotstandard(day);
    	if (monthResult.getResultState().equals(ResultState.RUN_HAS_RESULT)) {
    		if (monthResult.getValue().equals(ComplianceValue.COMPLIANT)) {
    	        if (dayResult.getResultState().equals(ResultState.RUN_HAS_RESULT)) {
    	        	if (dayResult.getValue().equals(ComplianceValue.COMPLIANT)) {
    	        		try {
    	        			if (!DateUtils.isEmpty(month)) { month = StringUtils.leftPad(month,2,"0"); } 
    	        			if (!DateUtils.isEmpty(day)) { day = StringUtils.leftPad(day,2,"0"); } 
    	        		    Integer numericYear = Integer.parseInt(year);
    	        		    String date = String.format("%04d", numericYear) + "-" + month.trim() + "-" + day.trim();

    	        	    	if (DateUtils.eventDateValid(date)) {
    	        	    		result.setValue(ComplianceValue.COMPLIANT);
    	        	    		result.addComment("Provided value for year-month-day " + date + " parses to a valid day.");;
    	        	    	} else {
    	        	    		result.setValue(ComplianceValue.NOT_COMPLIANT);
    	        	    		result.addComment("Provided value for year-month-day " + date + " does not parse to a valid day.");;
    	        	    	}
    	        		    result.setResultState(ResultState.RUN_HAS_RESULT);
    	        		} catch (NumberFormatException e) {
    	        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	        		    result.addComment("Unable to parse integer from provided value for year " + year + " " + e.getMessage());;
    	        		}
    	        	} else {
    	        		result.setResultState(ResultState.RUN_HAS_RESULT);
    	        	    result.setValue(ComplianceValue.NOT_COMPLIANT);
    	        		result.addComment("Provided value for day " + day + " is outside the range 1-31.");;
    	        	}
    	        } else {
    	        	result.setResultState(dayResult.getResultState());
    	        	result.addComment(dayResult.getComment());
    	        }
    		} else {
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("Provided value for month " + month + " is outside the range 1-12.");;
    		}
    	} else {
    		result.setResultState(monthResult.getResultState());
    		result.addComment(monthResult.getComment());
    	}
    	*/

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
	@Amendment(label = "DAY_MONTH_TRANSPOSED", description = "Check of month is out of range for months, but day is " +
			"in range for months, and propose a transposition of the two if this is the case.")
	@Specification("If dwc:month and dwc:day are provided, propose a transposition if day is in range for months, and " +
			"month is in range for days")
    public static final DQResponse<AmendmentValue> dayMonthTransposition(@ActedUpon(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) {
		DQResponse<AmendmentValue> result = new DQResponse<>();

		if (DateUtils.isEmpty(day) || DateUtils.isEmpty(month)) {
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
			result.addComment("Either month or day was not provided.");
		} else {
			DQResponse<ComplianceValue> monthResult =  validationMonthNotstandard(month);
			DQResponse<ComplianceValue> dayResult =  validationDayNotstandard(day);

			if (monthResult.getResultState().equals(ResultState.RUN_HAS_RESULT)) {
				if (monthResult.getValue().equals(ComplianceValue.NOT_COMPLIANT)) {
					// month is integer, but out of range
					if (dayResult.getResultState().equals(ResultState.RUN_HAS_RESULT)) {
						// day is also integer
						int dayNumeric = Integer.parseInt(day);
						int monthNumeric = Integer.parseInt(month);
						if (DateUtils.isDayInRange(monthNumeric) && DateUtils.isMonthInRange(dayNumeric)) {
							// day is in range for months, and month is in range for days, so transpose.
							Map<String, String> transposedValues = new HashMap<>();
							transposedValues.put("dwc:month", day);
							transposedValues.put("dwc:day", month);

							result.setValue(new AmendmentValue(transposedValues));
							result.setResultState(ResultState.TRANSPOSED);
						} else {
							result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
						}
					} else {
						result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
						result.addComment("dwc:day " + dayResult.getResultState() + ". " + dayResult.getComment());
					}
				} else {
					if (dayResult.getResultState().equals(ResultState.RUN_HAS_RESULT) &&
							dayResult.getValue().equals(ComplianceValue.COMPLIANT)) {
						// month is in range for months, so don't try to change.
						result.setResultState(ResultState.NOT_AMENDED);
					} else {
						result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
					}
				}
			} else {
				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				result.addComment("dwc:month " + monthResult.getResultState() + ". " + monthResult.getComment());
			}
		}
		return result;
    }

    /**
     * #130 Validation SingleRecord Conformance: startdayofyear outofrange
     * 
     * Given an eventDate and an start day of a date range in days of the year, test whether or not
     * the value for startDayOfYear is in range for the days in the end year of the eventDate
     * (day is 1-365, or 366  in leap year).
     *
     * Provides: VALIDATION_STARTDAYOFYEAR_OUTOFRANGE
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("85803c7e-2a5a-42e1-b8d3-299a44cafc46")
    public static final DQResponse<ComplianceValue> validationStartdayofyearOutofrange(
    		@ActedUpon(value="dwc:startDayOfYear") String startDay, 
    		@Consulted(value="dwc:eventDate")String eventDate) {
    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
    	
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
    	
    	String year = ""; 
    	
    	boolean eventDateParseFailure = false;
    	if (!DateUtils.isEmpty(eventDate)) { 
    		try {
    			Integer startYearInt = new LocalDateInterval(eventDate).getStartDate().getYear();
    			year = Integer.toString(startYearInt);
    		} catch (DateTimeParseException | EmptyDateException e1) {
    			logger.debug(e1.getMessage());
    			eventDateParseFailure = true;
    		}
    	}
    	
    	logger.debug(startDay);
    	logger.debug(eventDate);
    	logger.debug(year);
    	
    	if (DateUtils.isEmpty(startDay)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("startDayOfYear was not provided.");
    	} else {
    	    try {
    	       Integer numericStartDay = Integer.parseInt(startDay);
    	       if (numericStartDay>0 && numericStartDay<366) {
    	    	   result.setValue(ComplianceValue.COMPLIANT);
    	    	   result.setResultState(ResultState.RUN_HAS_RESULT);
    		       result.addComment("startDayOfYear [" + startDay + "] is in range for days of the year.");
    	       } else if (numericStartDay==366) {
    	           if (DateUtils.isEmpty(year)) {
    		            result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		            if (eventDateParseFailure) {
    		            	result.addComment("unable to extract year from provided eventDate and day is 366, could be valid in a leap year.");
    		            } else {
    		            	result.addComment("year was not provided and day is 366, could be valid in a leap year.");
    		            }
    	           } else {
    	        	   String potentialDay = DateUtils.createEventDateFromParts("", startDay, "", year, "", "");
    	        	   if (DateUtils.isEmpty(potentialDay)) {
    		               result.setValue(ComplianceValue.NOT_COMPLIANT);
    	    	           result.setResultState(ResultState.RUN_HAS_RESULT);
    		               result.addComment("startDayOfYear [" + startDay + "] is out of range for year ["+ year +"].");
    	        	   } else if (DateUtils.eventDateValid(potentialDay)) {
    	    	           result.setValue(ComplianceValue.COMPLIANT);
    	    	           result.setResultState(ResultState.RUN_HAS_RESULT);
    		               result.addComment("startDayOfYear [" + startDay + "] is in range for days of the year ["+year+"].");
    	        	   } else {
    		               result.setValue(ComplianceValue.NOT_COMPLIANT);
    	    	           result.setResultState(ResultState.RUN_HAS_RESULT);
    		               result.addComment("startDayOfYear [" + startDay + "] is out of range for year ["+ year +"].");
    	        	   }
    	           }
    	       } else {
    		       result.setValue(ComplianceValue.NOT_COMPLIANT);
    	    	   result.setResultState(ResultState.RUN_HAS_RESULT);
    		       result.addComment("startDayOfYear [" + startDay + "] is out of range for days in the year.");
    	       }
    	    } catch (NumberFormatException e) {
    		   result.setResultState(ResultState.RUN_HAS_RESULT);
    		   result.setValue(ComplianceValue.NOT_COMPLIANT);
    		   result.addComment("startDayOfYear [" + startDay + "] is not a number.");
    	    }
    	}
    	return result;
    }

    
    /**
     * Given an eventDate and an end day of a date range in days of the year, test whether or not
     * the value for endDayOfYear is in range for the days in the end year of the eventDate 
     * enddayofyear in the range (1-365, or 366 in leap year).
     *
     * #131 Validation SingleRecord Conformance: enddayofyear outofrange
     * 
     * Provides: VALIDATION_ENDDAYOFYEAR_OUTOFRANGE
     *
     * @param endDay the provided dwc:endDayOfYear to evaluate for range 1-365 or 1-366 
     * @param eventDate to check to see if the year or the end year of the range has 
     *   a day 366, in range in in a leap year.
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("9a39d88c-7eee-46df-b32a-c109f9f81fb8")
    public static final DQResponse<ComplianceValue> validationEnddayofyearOutofrange(
    		@ActedUpon(value="dwc:endDayOfYear") String endDay, 
    		@Consulted(value="dwc:eventDate")String eventDate) {
    	
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:endDayOfYear is EMPTY 
        // or if the value of dwc:endDayOfYear is equal to 366 and 
        // (dwc:eventDate is EMPTY or the value of dwc:eventDate cannot 
        // be interpreted to find a single year or an end year in a 
        // range); COMPLIANT if the value of dwc:endDayOfYear is an 
        // integer between 1 and 365 inclusive, or if the value of 
        // dwc:endDayOfYear is 366 and the end year interpreted from 
        // dwc:eventDate is a leap year; otherwise NOT_COMPLIANT 
    	
    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
    	
    	String year = ""; 
    	
    	boolean eventDateParseFailure = false;
    	if (!DateUtils.isEmpty(eventDate)) { 
    		try {
    			Integer endYearInt = new LocalDateInterval(eventDate).getEndDate().getYear();
    			year = Integer.toString(endYearInt);
    		} catch (DateTimeParseException | EmptyDateException e1) {
    			logger.debug(e1.getMessage());
    			eventDateParseFailure = true;
    		}
    	}
    	
    	logger.debug(endDay);
    	logger.debug(eventDate);
    	logger.debug(year);
    	
    	if (DateUtils.isEmpty(endDay)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("endDayOfYear was not provided.");
    	} else {
    	    try {
    	       Integer numericEndDay = Integer.parseInt(endDay);
    	       if (numericEndDay>0 && numericEndDay<366) {
    	    	   result.setValue(ComplianceValue.COMPLIANT);
    	    	   result.setResultState(ResultState.RUN_HAS_RESULT);
    		       result.addComment("endDayOfYear [" + endDay + "] is in range for days of the year.");
    	       } else if (numericEndDay==366) {
    	           if (DateUtils.isEmpty(year)) {
    		            result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		            if (eventDateParseFailure) {
    		            	result.addComment("unable to extract year from provided eventDate and day is 366, could be valid in a leap year.");
    		            } else {
    		            	result.addComment("year was not provided and day is 366, could be valid in a leap year.");
    		            }
    	           } else {
    	        	   String potentialDay = DateUtils.createEventDateFromParts("", endDay, "", year, "", "");
    	        	   if (DateUtils.isEmpty(potentialDay)) {
    		               result.setValue(ComplianceValue.NOT_COMPLIANT);
    	    	           result.setResultState(ResultState.RUN_HAS_RESULT);
    		               result.addComment("endDayOfYear [" + endDay + "] is out of range for year ["+ year +"].");
    	        	   } else if (DateUtils.eventDateValid(potentialDay)) {
    	    	           result.setValue(ComplianceValue.COMPLIANT);
    	    	           result.setResultState(ResultState.RUN_HAS_RESULT);
    		               result.addComment("endDayOfYear [" + endDay + "] is in range for days of the year ["+year+"].");
    	        	   } else {
    		               result.setValue(ComplianceValue.NOT_COMPLIANT);
    	    	           result.setResultState(ResultState.RUN_HAS_RESULT);
    		               result.addComment("endDayOfYear [" + endDay + "] is out of range for year ["+ year +"].");
    	        	   }
    	           }
    	       } else {
    		       result.setValue(ComplianceValue.NOT_COMPLIANT);
    	    	   result.setResultState(ResultState.RUN_HAS_RESULT);
    		       result.addComment("endDayOfYear [" + endDay + "] is out of range for days in the year.");
    	       }
    	    } catch (NumberFormatException e) {
    		   result.setResultState(ResultState.RUN_HAS_RESULT);
    		   result.setValue(ComplianceValue.NOT_COMPLIANT);
    		   result.addComment("endDayOfYear [" + endDay + "] is not a number.");
    	    }
    	}
    	return result;
    }

    @Deprecated
    public static final DQResponse<AmendmentValue> eventDateFromYearStartEndDay(@ActedUpon(value="dwc:eventDate") String eventDate, @Consulted(value="dwc:year") String year, @Consulted(value="dwc:startDayOfYear") String startDay, @Consulted(value="dwc:endDayOfYear") String endDay ) {
    	return amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate,year,startDay,endDay);
    }

    /**
     * Given a year and a start and end day, propose a value to fill in an eventDate if it is not empty and if
     * both year and start day are not empty.
     *
     * #132 Amendment SingleRecord Completeness: eventdate from yearstartdayofyearenddayofyear
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR
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
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides(value="eb0a44fa-241c-4d64-98df-ad4aa837307b")
    public static final DQResponse<AmendmentValue> amendmentEventdateFromYearstartdayofyearenddayofyear(
    		@ActedUpon(value="dwc:eventDate") String eventDate, 
    		@Consulted(value="dwc:year") String year, 
    		@Consulted(value="dwc:startDayOfYear") String startDay, 
    		@Consulted(value="dwc:endDayOfYear") String endDay ) {
    	
    	DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();
    	
    	// Specification (Updated 2022 Mar 10)
    	// INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate was not 
    	// EMPTY or any of dwc:year, dwc:startDayOfYear, or dwc:endDayOfYear were EMPTY 
    	// or any of the values in dwc:year, dwc:startDayOfYear, or dwc:endDayOfYear 
    	// were not independently interpretable; AMENDED if dwc:eventDate was FILLED_IN 
    	// from the values in dwc:year, dwc:startDayOfYear and dwc:endDayOfYear; 
    	// if the value of dwc:endDayOfYear is less than the value of dwc:startDayOfYear, 
    	// or otherwise NOT_AMENDED
    	
    	if (DateUtils.isEmpty(year) || DateUtils.isEmpty(startDay) || DateUtils.isEmpty(endDay)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("One of year, startDayOfYear, or endDayOfYear was not provided.");
    	} else if (!DateUtils.isEmpty(eventDate)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("A value exists in dwc:eventDate, ammendment not attempted.");
    	} else {
    	    try {
     	       Integer numericYear = Integer.parseInt(year);
     	       Integer numericStartDay = Integer.parseInt(startDay);
     	       Integer numericEndDay = Integer.parseInt(endDay);
     	       logger.debug(numericStartDay);
     	       if (numericStartDay < 1 || numericStartDay > 366 || numericEndDay < 1 || numericEndDay > 366) { 
     	    	   // out of range for possible days of year, report and fail.
     	    	   if (numericStartDay < 1 || numericStartDay > 366) { 
     	    		   result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
     	    		   result.addComment("startDayOfYear [" + startDay + "], is out of range for possible days of the year.");
     	    	   }
     	    	   if (numericEndDay < 1 || numericEndDay > 366) { 
     	    		   result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
     	    		   result.addComment("endDayOfYear [" + endDay + "], is out of range for possible days of the year.");
     	    	   }
     	       } else { 
     	    	   // see if a valid date range can be constructed.
     	    	   String resultDateString = DateUtils.createEventDateFromParts("", startDay, endDay, year, "", "");
     	    	   logger.debug(resultDateString);

     	    	   if (DateUtils.isEmpty(resultDateString)) {
     	    		   if (numericEndDay < numericStartDay) { 
     	    			   result.setResultState(ResultState.NOT_AMENDED);
     	    			   result.addComment("Not attempting to amend, startDayOfYear [" + startDay + "] is greater than endDayOfYear ["+ endDay +"].");
     	    		   } else { 
     	    			   result.setResultState(ResultState.NOT_AMENDED);
     	    			   result.addComment("Unable to construct a valid ISO date from startDayOfYear [" + startDay + "], year ["+year+"], and endDayOfYear ["+ endDay +"].");
     	    		   }
     	    	   } else {
     	    		   result.setResultState(ResultState.AMENDED);
     	    		   Map<String, String> values = new HashMap<>();
     	    		   values.put("dwc:eventDate", resultDateString);
     	    		   result.setValue(new AmendmentValue(values));
     	    		   result.addComment("FILLED IN EMPTY dwc:eventDate from startDayOfYear [" + startDay + "], year ["+year+"], and endDayOfYear ["+ endDay +"].");
     	    	   }
     	       } 
    	    } catch (NumberFormatException e) {
     		   result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
     		   result.addComment("One of startDayOfYear [" + startDay + "], year ["+year+"], or endDayOfYear ["+ endDay +"] is not a number.");
     	    }
    	}
    	// make sure that an empty value map is returned instead of null.
    	if (result.getValue() == null) {
    		result.setValue(new AmendmentValue(new HashMap<String, String>()));
		}
    	
    	return result;
    }

    @Deprecated
    public static final DQResponse<AmendmentValue> eventDateFromYearMonthDay(
    		@ActedUpon(value="dwc:eventDate") String eventDate, 
    		@Consulted(value="dwc:year") String year, 
    		@Consulted(value="dwc:month") String month, 
    		@Consulted(value="dwc:day") String day ) 
    {
    	return amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
    }
    /**
     * Given values for year, month, and day propose a value to fill in an empty eventDate.
     *
     * #93 Amendment SingleRecord Completeness: eventdate from yearmonthday
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY
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
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("3892f432-ddd0-4a0a-b713-f2e2ecbd879d")
    public static final DQResponse<AmendmentValue> amendmentEventDateFromYearMonthDay(
    		@ActedUpon(value="dwc:eventDate") String eventDate, 
    		@Consulted(value="dwc:year") String year, 
    		@Consulted(value="dwc:month") String month, 
    		@Consulted(value="dwc:day") String day ) 
    {
        // Specification
        // INTERNAL _PREREQUISITES_NOT_MET if dwc:eventDate is not 
        // EMPTY or dwc:year is EMPTY or is uninterpretable as a valid 
        // year; AMENDED if the value of dwc:eventDate was unambiguously 
        // interpreted from the values in dwc:year, dwc:month and dwc:day; 
        // otherwise NOT_AMENDED 
    	
    	
    	DQResponse<AmendmentValue> result = new DQResponse<>();
    	if (DateUtils.isEmpty(year)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("No value for dwc:year was provided.");
    	} else if (!DateUtils.isEmpty(eventDate)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("A value exists in dwc:eventDate, ammendment not attempted.");
    	} else {
    	    try {
     	       Integer.parseInt(year);
     	       try {
     	    	   boolean hasMonth = false;
     	    	   if (!DateUtils.isEmpty(month)) {
     	    		   if (month.matches("^[XIVxiv]+$")) { 
     	    			   // Roman numeral month values are interpretable as numbers.
     	    			   logger.debug(month);
     	    			   if (DateUtils.romanMonthToInteger(month)==null) { 
     	    				   Integer.parseInt(month);
     	    				   hasMonth=true;
     	    			   } else { 
     	    				   String numericmonth = DateUtils.romanMonthToInteger(month).toString();
     	    				   result.addComment("Converting month ["+month+"] to ["+ numericmonth +"] .");
     	    				   month = DateUtils.romanMonthToInteger(month).toString();
     	    				   if (month!=null && month.length()>0) { 
     	    					   hasMonth=true;
     	    				   }
     	    			   }
     	    			   logger.debug(month);
     	    		   } else { 
     	    			   Integer.parseInt(month);
     	    			   hasMonth=true;
     	    		   }
     	    	   }
     	    	   if (!DateUtils.isEmpty(day)) {
     	    		   Integer numericDay = Integer.parseInt(day);
     	    		   if (!DateUtils.isDayInRange(numericDay)) {
     	    			   throw new NumberFormatException("The provided value for Day is out of range for a day");
     	    		   }
     	    	   }
     	    	   // try, may raise exception
     	    	   String resultDateString = DateUtils.createEventDateFromParts("", "", "", year, month, day);
     	    	   if (!hasMonth) { 
     	    		   // From Notes:  If dwc:year and dwc:day are present, 
     	    		   // but dwc:month is not supplied, then just the year should be given as the proposed amendment.
     	    		   resultDateString = DateUtils.createEventDateFromParts("", "", "", year, "", "");
     	    	   }
     	    	   if (DateUtils.isEmpty(resultDateString)) {
     	    		   result.setResultState(ResultState.NOT_AMENDED);
     	    		   result.addComment("Unable to construct an unabmiguous ISO date from year ["+year+"], month ["+ month +"] and day ["+ day +"].");
     	    	   } else if (!DateUtils.eventDateValid(resultDateString)) {
     	    		   result.setResultState(ResultState.NOT_AMENDED);
     	    		   result.addComment("Failed to construct an unambiguous ISO date from year ["+year+"], month ["+ month +"] and day ["+ day +"].");
     	    	   } else {
     	    		   result.setResultState(ResultState.AMENDED);
     	    		   Map<String, String> values = new HashMap<>();
     	    		   values.put("dwc:eventDate", resultDateString);
     	    		   result.setValue(new AmendmentValue(values));
     	    	   }
     	       } catch (NumberFormatException e) {
     	    	   result.setResultState(ResultState.NOT_AMENDED);
     	    	   result.addComment("One of month ["+month+"], or day ["+ day +"] is not interpretable as a number.");
     	       }

     	    } catch (NumberFormatException e) {
     		   result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
     		   result.addComment("Value provided for year [" + year + "] is not a number.");
     	    }
    	}
    	// make sure that an empty value map is returned instead of null.
    	if (result.getValue() == null) {
    		result.setValue(new AmendmentValue(new HashMap<String, String>()));
		}
    	
    	return result;
    }
    
    @Deprecated
    public static final DQResponse<AmendmentValue> standardizeMonth(@ActedUpon(value="dwc:month") String month) {
    	return amendmentMonthStandardized(month);
    }
    /**
     * Given a value of dwc:month, check to see if that month is an integer, if not, attempt to
     * propose a suitable integer for the month of the year from the value provided.
     *
     * #128 Amendment SingleRecord Conformance: month standardized
     *
     * Provides: AMENDMENT_MONTH_STANDARDIZED
     *
     * @param month the provided dwc:month to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("2e371d57-1eb3-4fe3-8a61-dff43ced50cf")
    public static final DQResponse<AmendmentValue> amendmentMonthStandardized(@ActedUpon(value="dwc:month") String month) {
    	DQResponse<AmendmentValue> result = new DQResponse<>();
    	
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:month is EMPTY; AMENDED 
        // if the value of dwc:month was able to be interpreted as 
        // a integer between 1 and 12 inclusive; otherwise NOT_AMENDED 
        //
    	
    	if (DateUtils.isEmpty(month)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("No value for dwc:month was provided.");
    	} else {

    		try {
    			Integer monthNumeric = Integer.parseInt(month);
    			result.addComment("A value for dwc:month parsable as an integer was provided.");
    			if (monthNumeric >= 1 && monthNumeric <=12) { 
    				result.addComment("Provided value for dwc:month was in the range 1-12.");
    				if (Integer.toString(monthNumeric).equals(month)) { 
    					result.setResultState(ResultState.NOT_AMENDED);
    					result.addComment("No change needed.");
    				} else {
    					result.setResultState(ResultState.AMENDED);
						Map<String, String> values = new HashMap<>();
						values.put("dwc:month", monthNumeric.toString());
						result.setValue(new AmendmentValue(values));
    					result.addComment("Interpreted provided value for dwc:month ["+month+"] as ["+monthNumeric.toString()+"].");
    				}
    			} else {
    				result.addComment("Provided value for dwc:month was outside the range 1-12.");
    				result.setResultState(ResultState.NOT_AMENDED);
    			}
    		} catch (NumberFormatException e) {
    			// Convert roman numerals, some problematic forms of abbreviations,
    			// non-english capitalization variants, absence of exepected accented characters,
    			// etc to date library (e.g. Joda) recognized month names.
    			String monthConverted = DateUtils.cleanMonth(month);
    			// Strip any trailing period off of month name.
    			String monthTrim = monthConverted.replaceFirst("\\.$", "").trim();
    			if (DateUtils.isEmpty(monthTrim)) {
    				result.setResultState(ResultState.NOT_AMENDED);
    				result.addComment("Unable to parse a meaningfull value for month from dwc:month ["+month+"].");
    			} else {
    				// Add the month string into the first day of that month in 1800, and see if
    				// a verbatim date can be parsed from that string.
    				StringBuilder testDate = new StringBuilder().append("1800-").append(monthTrim).append("-01");
    				EventResult convertedDateResult = DateUtils.extractDateFromVerbatimER(testDate.toString());
    				if (convertedDateResult.getResultState().equals(EventResult.EventQCResultState.DATE)) {
    					String convertedDate = convertedDateResult.getResult();
    					// Date could be parsed, extract the month.
    					Integer monthNumeric = DateUtils.extractDate(convertedDate).getMonthValue();
    					result.setResultState(ResultState.AMENDED);

						Map<String, String> values = new HashMap<>();
						values.put("dwc:month", monthNumeric.toString());

						result.setValue(new AmendmentValue(values));
    					result.addComment("Interpreted provided value for dwc:month ["+month+"] as ["+monthNumeric.toString()+"].");
    				} else {
    					result.setResultState(ResultState.NOT_AMENDED);
    					result.addComment("Unable to parse a meaningfull value for month from dwc:month ["+month+"].");
    				}
    			}
    		}

    	}
    	// make sure that an empty value map is returned instead of null.
    	if (result.getValue() == null) {
    		result.setValue(new AmendmentValue(new HashMap<String, String>()));
		}
    	return result;
    }

    @Deprecated
    public static final DQResponse<AmendmentValue> standardizeDay(@ActedUpon(value="dwc:day") String day) {
    	return amendmentDayStandardized(day);
    }
    /**
     * Given a dwc:day, if the day is not empty and not an integer, attempt to interpret the value
     * as a day of the month.   
     *
     * #127 Amendment SingleRecord Conformance: day standardized
     *
     * Provides: AMENDMENT_DAY_STANDARDIZED
     *
     * @param day the provided dwc:day to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("b129fa4d-b25b-43f7-9645-5ed4d44b357b")
    public static final DQResponse<AmendmentValue> amendmentDayStandardized(@ActedUpon(value="dwc:day") String day) {
    	DQResponse<AmendmentValue> result = new DQResponse<>();

    	// Specification
    	// INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; AMENDED 
    	// if the value of dwc:day was unambiguously interpreted as 
    	// an integer between 1 and 31 inclusive; otherwise NOT_AMENDED 
    	//

    	if (DateUtils.isEmpty(day)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("No value for dwc:day was provided.");
    	} else {
    		Map<String,Integer> daysTextMap = DateUtils.getDayStringMap();
    		String key = day.trim().toLowerCase().replace(" ", "");
    		if (daysTextMap.containsKey(key)) { 
    			result.setResultState(ResultState.AMENDED);
    			Map<String, String> values = new HashMap<>();
    			String dayInterpreted = Integer.toString(daysTextMap.get(key)).trim();
    			values.put("dwc:day", dayInterpreted) ;
    			result.setValue(new AmendmentValue(values));
    			result.addComment("Interpreted provided value for dwc:day ["+day+"] as ["+dayInterpreted+"].");
    		} else { 

    			try {
    				Integer dayNumeric = Integer.parseInt(day);
    				result.addComment("A value for dwc:day parsable as an integer was provided.");
    				String dayTrimmed = day.replaceAll("[^0-9]", "");
    				String dayCleaned = dayNumeric.toString();
    				logger.debug(day);
    				logger.debug(dayTrimmed);
    				logger.debug(dayCleaned);
    				if (dayTrimmed.equals(day) && dayCleaned.equals(day) ) { 
    					if (dayNumeric>0 && dayNumeric<32) {
    						result.setResultState(ResultState.NOT_AMENDED);
    						result.addComment("Provided value for dwc:day ["+day+"] is an integer in the range 1 to 32.");
    					} else {
    						result.setResultState(ResultState.NOT_AMENDED);
    						result.addComment("Unable to parse a meaningfull value for day of month from dwc:day ["+day+"].");
    					}
    				} else { 				
    					result.addComment("Extra non-numeric characters in dwc:day [" + day + "] were removed to form ["+ dayCleaned +"].");
    					if (dayNumeric>0 && dayNumeric<32) {
    						result.setResultState(ResultState.AMENDED);
    						Map<String, String> values = new HashMap<>();
    						values.put("dwc:day", dayCleaned);
    						result.setValue(new AmendmentValue(values));
    						result.addComment("Interpreted provided value for dwc:day ["+day+"] as ["+dayNumeric.toString()+"].");
    					} else {
    						result.setResultState(ResultState.NOT_AMENDED);
    						result.addComment("Unable to parse a meaningfull value for day of month from dwc:day ["+day+"].");
    					}
    				}
    			} catch (NumberFormatException e) {


    				// Strip off any trailing non-numeric characters.
    				String dayTrimmed = day.replaceAll("[^0-9]+$", "");
    				logger.debug(day);
    				logger.debug(dayTrimmed);
    				boolean failed = false;
    				if (!dayTrimmed.equals(day)) { 
    					// rule out ambiguous patterns
    					logger.debug(dayTrimmed);
    					if (day.matches("[0-9]{1,2}[stndrh]{2}\\.{0,1} [A-Za-z]+")) { 
    						logger.debug(day);
    						result.setResultState(ResultState.NOT_AMENDED);
    						result.addComment("A value for dwc:day matches the pattern 'dd(st|nd|rd|th) DayOfWeek' and is ambiguous.");
    						failed = true;
    					}
    				}
    				logger.debug(failed);
    				if (!failed) { 
    					// Try again
    					try {
    						Integer dayNumeric = Integer.parseInt(dayTrimmed.trim());
    						logger.debug(dayNumeric);
    						if (dayNumeric>0 && dayNumeric<32) {
    							result.setResultState(ResultState.AMENDED);

    							Map<String, String> values = new HashMap<>();
    							values.put("dwc:day", dayNumeric.toString());
    							result.setValue(new AmendmentValue(values));
    							result.addComment("Interpreted provided value for dwc:day ["+day+"] as ["+dayNumeric.toString()+"].");
    						} else {
    							result.setResultState(ResultState.NOT_AMENDED);
    							result.addComment("Unable to parse a meaningfull value for day of month from dwc:day ["+day+"].");

    						}
    					} catch (NumberFormatException ex) {
    						logger.debug(ex.getMessage(), ex);
    						result.setResultState(ResultState.NOT_AMENDED);
    						result.addComment("Unable to interpret value provided for dwc:day ["+ day + "] as a day of the month.");
    						logger.debug(result.getComment());
    					}
    				}
    			}
    		}
    	}

    	// make sure that an empty value map is returned instead of null.
    	if (result.getValue() == null) {
    		result.setValue(new AmendmentValue(new HashMap<String, String>()));
    	}

    	return result;
    }

    /**
     * Given a set of Event terms related to date and time, determine if they are consistent or
     * inconsistent.
     *
     * #67 Validation SingleRecord Consistency: eventdate inconsistent
     *
     * Provides: VALIDATION_EVENT_INCONSISTENT
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse the response of type ComplianceValue  to return describing 
     *     whether the event terms represent one temporal interval or
     */
    @Provides("5618f083-d55a-4ac2-92b5-b9fb227b832f")
	public static DQResponse<ComplianceValue> validationEventInconsistent(
    		@ActedUpon(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:year") String year,
			@ActedUpon(value = "dwc:month") String month,
			@ActedUpon(value = "dwc:day") String day,
			@ActedUpon(value = "dwc:startDayOfYear") String startDayOfYear,
			@ActedUpon(value = "dwc:endDayOfYear") String endDayOfYear
		)  
    {
		DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
		
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
		
		boolean inconsistencyFound = false;
		boolean interpretationProblem = false;

		// Compare eventDate and eventTime
		if (DateUtils.isEmpty(eventDate)) {
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
			result.addComment("Provided value for eventDate is empty.  Unable to evaluate consistency.");
		} else if (DateUtils.isEmpty(year) && DateUtils.isEmpty(month) && DateUtils.isEmpty(day)  && DateUtils.isEmpty(startDayOfYear) && DateUtils.isEmpty(endDayOfYear)) {
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
			result.addComment("Provided values for year, month, day, startDayOfYear and endDayOfYear are empty.  Unable to evaluate consistency.");
		} else { 
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
			if (!DateUtils.isEmpty(year)) { year = StringUtils.leftPad(year,4,"0"); } 
			if (!DateUtils.isEmpty(month)) { month = StringUtils.leftPad(month,2,"0"); } 
			if (!DateUtils.isEmpty(day)) { day = StringUtils.leftPad(day,2,"0"); } 
			StringBuilder tempDate = new StringBuilder().append(year)
					.append("-").append(month).append("-").append(day);
			if (!DateUtils.isConsistent(tempDate.toString(), startDayOfYear, "", year, month, day)) {
				inconsistencyFound = true;
				result.addComment("Provided value for year month and day'" + tempDate + "' appear to represent a date inconsistent with startDayOfYear [" + startDayOfYear + "] .");
			}
		}

		if (DateUtils.isEmpty(eventDate) &&
				DateUtils.isEmpty(year) &&
				DateUtils.isEmpty(month) &&
				DateUtils.isEmpty(day) &&
				DateUtils.isEmpty(startDayOfYear) &&
				DateUtils.isEmpty(endDayOfYear) )
		{
			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
			result.addComment("All provided event terms are empty, can't assess consistency.");
			logger.debug("All terms empty.");
		} else {
			if (inconsistencyFound) {
				// inconsistency trumps interpretation problem, return result as NOT COMPLIANT
				result.setResultState(ResultState.RUN_HAS_RESULT);
				result.setValue(ComplianceValue.NOT_COMPLIANT);
			} else {
				if (interpretationProblem) {
					result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
				} else {
					result.setResultState(ResultState.RUN_HAS_RESULT);
					result.setValue(ComplianceValue.COMPLIANT);
				}
			}
		}
		}

		return result;
	}

    @Deprecated
    public static DQResponse<ComplianceValue> isEventEmpty(
    		@ActedUpon(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:verbatimEventDate") String verbatimEventDate,
			@ActedUpon(value = "dwc:year") String year,
			@ActedUpon(value = "dwc:month") String month,
			@ActedUpon(value = "dwc:day") String day,
			@ActedUpon(value = "dwc:startDayOfYear") String startDayOfYear,
			@ActedUpon(value = "dwc:endDayOfYear") String endDayOfYear )
    {
        return validationEventTemporalEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);   
    }
    /**
     * Examine each of the date/time related terms in the Event class, and test to see if at least
     * one of them contains some value.  This may or may not be a meaningful value, and this may or
     * may not be interpretable to a date or date range.
     *
     * #88 Validation SingleRecord Completeness: event temporal empty
     *
     * Provides: VALIDATION_EVENT_TEMPORAL_EMPTY
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
    @Provides(value="41267642-60ff-4116-90eb-499fee2cd83f")
    public static DQResponse<ComplianceValue> validationEventTemporalEmpty(
    		@ActedUpon(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:verbatimEventDate") String verbatimEventDate,
			@ActedUpon(value = "dwc:year") String year,
			@ActedUpon(value = "dwc:month") String month,
			@ActedUpon(value = "dwc:day") String day,
			@ActedUpon(value = "dwc:startDayOfYear") String startDayOfYear,
			@ActedUpon(value = "dwc:endDayOfYear") String endDayOfYear )
			// @ActedUpon(value = "dwc:eventTime") String eventTime )   // Removed per discussion in tdwg/bdq issue 88
    {

        // Specification
        // COMPLIANT if any of dwc:eventDate, dwc:year, dwc:month, 
        // dwc:day, dwc:startDayOfYear, dwc:endDayOfYear, dwc:verbatimEventDate 
        // are NOT EMPTY; otherwise NOT_COMPLIANT. 
    	
		DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

		if (DateUtils.isEmpty(eventDate) &&
			DateUtils.isEmpty(year) &&
			DateUtils.isEmpty(month) &&
			DateUtils.isEmpty(day) &&
			DateUtils.isEmpty(startDayOfYear) &&
			DateUtils.isEmpty(endDayOfYear) &&
			DateUtils.isEmpty(verbatimEventDate)
				) {
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.addComment("No value is present in any of the Event temporal terms");
		} else {
			result.setValue(ComplianceValue.COMPLIANT);
			result.addComment("Some value is present in at least one of the Event temporal terms");
		}
		result.setResultState(ResultState.RUN_HAS_RESULT);

		return result;
	}

    /**
     * Examine each of the date/time related terms in the Event class, and test to see if at least
     * one of them contains some value.  This may or may not be a meaningful value, and this may or
     * may not be interpretable to a date or date range.
     *
	 * MEASURE_EVENT_NOTEMPTY 
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
	@Provides(value = "urn:uuid:9dc97514-3b88-4afc-931d-5fc386be21ee") // locally generated
	@Measure(dimension = Dimension.COMPLETENESS, label = "Event Completeness", description = "Measure the completeness of the temporal terms in an Event.")
	@Specification(value = "For values of dwc:eventDate, year, month, day, startDayOfYear, endDayOfYear, verbatimEventDate, eventTime, check is not empty.")
	public static DQResponse<CompletenessValue> measureEventCompleteness(
    		@ActedUpon(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:verbatimEventDate") String verbatimEventDate,
			@ActedUpon(value = "dwc:year") String year,
			@ActedUpon(value = "dwc:month") String month,
			@ActedUpon(value = "dwc:day") String day,
			@ActedUpon(value = "dwc:startDayOfYear") String startDayOfYear,
			@ActedUpon(value = "dwc:endDayOfYear") String endDayOfYear )
		{
		DQResponse<CompletenessValue> result = new DQResponse<>();
		DQResponse<ComplianceValue> validation = DwCEventDQ.validationEventTemporalEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		if (validation.getResultState().equals(ResultState.RUN_HAS_RESULT)) {
			if (validation.getValue().equals(ComplianceValue.COMPLIANT)) {
				result.setValue(CompletenessValue.COMPLETE);
			} else {
				result.addComment("No value provided for eventDate.");
				result.setValue(CompletenessValue.NOT_COMPLETE);
			}
			result.setResultState(ResultState.RUN_HAS_RESULT);
		}
		return result;
	}

	@Deprecated
	public static DQResponse<AmendmentValue> fillInEventFromEventDate(
    		@Consulted(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:year") String year,
			@ActedUpon(value = "dwc:month") String month,
			@ActedUpon(value = "dwc:day") String day,
			@ActedUpon(value = "dwc:startDayOfYear") String startDayOfYear,
			@ActedUpon(value = "dwc:endDayOfYear") String endDayOfYear
			)
	{
		return amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
	}

	/**
	 * Given a set of event terms, examine the content of eventDate, and if it is correctly formatted and
	 * can be interpreted, fill in any empty of the following terms (year, month, day, startDayOfYear, endDayOfYear)
	 * with appropriate values.
	 *
     * #52 Amendment SingleRecord Completeness: event from eventdate
     *
     * Provides: AMENDMENT_EVENT_FROM_EVENTDATE
	 *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate for emptyness and fill in 
     * @param month the provided dwc:month to evaluate for emptyness and fill in
     * @param day the provided dwc:day to evaluate for emptyness and fill in
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate for emptyness and fill in
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate for emptyness and fill in 
     * @return DQResponse the response of type AmendmentValue to return 
	 */
    @Provides("710fe118-17e1-440f-b428-88ba3f547d6d")
	public static DQResponse<AmendmentValue> amendmentEventFromEventdate(
    		@Consulted(value = "dwc:eventDate") String eventDate,
			@ActedUpon(value = "dwc:year") String year,
			@ActedUpon(value = "dwc:month") String month,
			@ActedUpon(value = "dwc:day") String day,
			@ActedUpon(value = "dwc:startDayOfYear") String startDayOfYear,
			@ActedUpon(value = "dwc:endDayOfYear") String endDayOfYear
			)
		{
    	DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();
    	
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
        // or does not contain a valid ISO 8601-1:2019 date; AMENDED 
        // if one or more EMPTY terms of the dwc:Event class (dwc:year, 
        // dwc:month, dwc:day, dwc:startDayOfYear, dwc:endDayOfYear) 
        // have been filled in from a valid unambiguously interpretable 
        // value in dwc:eventDate and eventDate is wholly within one 
        // year; otherwise NOT_AMENDED 
    	
    	if (DateUtils.isEmpty(eventDate)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("No value for dwc:eventDate was provided, no data to fill in from.");
    	} else {
    		if (!DateUtils.eventDateValid(eventDate)) {
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("Provided value for dwc:eventDate ["+eventDate+"] could not be interpreted.");
    		} else {
    			boolean isRange = false;
    			if (DateUtils.isRange(eventDate)) {
    				isRange = true;
    			}
    			LocalDateInterval interval = DateUtils.extractInterval(eventDate);
    			if (interval==null) {
    				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("Provided value for dwc:eventDate ["+ eventDate +"] appears to be correctly formatted, but could not be interpreted as a valid date.");

    			} else if (isRange && interval.getStart().getYear() != interval.getEnd().getYear() ) {
    				result.setResultState(ResultState.NOT_AMENDED);
    				result.addComment("Provided value for dwc:eventDate ["+ eventDate +"] represents a range of more than one year, not amending.");
    			} else {
    				Map<String, String> values = new HashMap<>();

    				if (DateUtils.isEmpty(day)) {
    					String newDay = Integer.toString(interval.getStartDate().getDayOfMonth());
						values.put("dwc:day", newDay );
    					result.setResultState(ResultState.AMENDED);
    					if (isRange) {
    						result.addComment("Added day ["+ newDay+"] from start day of range ["+eventDate+"].");
    					} else {
    						result.addComment("Added day ["+ newDay+"] from eventDate ["+eventDate+"].");
    					}
    				}
    				if (DateUtils.isEmpty(month)) {
    					String newMonth = Integer.toString(interval.getStartDate().getMonthValue());
    					values.put("dwc:month", newMonth );
    					result.setResultState(ResultState.AMENDED);
    					if (isRange) {
    						result.addComment("Added month ["+ newMonth +"] from start month of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added month ["+ newMonth +"] from eventDate ["+eventDate+"].");
    					}
    				}
    				if (DateUtils.isEmpty(month)) {
    					String newMonth = Integer.toString(interval.getStartDate().getMonthValue());
    					values.put("dwc:month", newMonth );
    					result.setResultState(ResultState.AMENDED);
    					if (isRange) {
    						result.addComment("Added month ["+ newMonth +"] from start month of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added month ["+ newMonth +"] from eventDate ["+eventDate+"].");
    					}
    				}
    				if (DateUtils.isEmpty(year)) {
    					String newYear = Integer.toString(interval.getStartDate().getYear());
    					values.put("dwc:year", newYear );
    					result.setResultState(ResultState.AMENDED);
    					if (isRange) {
    						result.addComment("Added year ["+ newYear +"] from start month of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added year ["+ newYear +"] from eventDate ["+eventDate+"].");
    					}
    				}

    				if (DateUtils.isEmpty(startDayOfYear)) {
    					String newDay = Integer.toString(interval.getStartDate().getDayOfYear());
    					values.put("dwc:startDayOfYear", newDay );
    					result.setResultState(ResultState.AMENDED);
    					if (isRange) {
    						result.addComment("Added startDayOfYear ["+ newDay +"] from start day of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added startDayOfYear ["+ newDay +"] from eventDate ["+eventDate+"].");
    					}
    				}

    				if (DateUtils.isEmpty(endDayOfYear)) {
    					String newDay = Integer.toString(interval.getEndDate().getDayOfYear());
    					values.put("dwc:endDayOfYear", newDay );
    					result.setResultState(ResultState.AMENDED);
    					if (isRange) {
    						result.addComment("Added endDayOfYear ["+ newDay +"] from end day of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added endDayOfYear ["+ newDay +"] from eventDate ["+eventDate+"].");
    					}
    				}

    				result.setValue(new AmendmentValue(values));
    				// Time could also be populated, but it isn't in scope for this issue. 
    				// Here is a minimal implementation,
    				// which illustrates some issues in implementation (using zulu time or not, dealing with time in ranges...)
    				//if (DateUtils.isEmpty(eventTime)) {
    				//	if (DateUtils.containsTime(eventDate)) {
    				//		String newTime = DateUtils.extractZuluTime(eventDate);
    				//		result.addResult("dwc:endDayOfYear", newTime );
    				//      result.setResultState(ResultState.FILLED_IN);
    				//	    result.addComment("Added eventTime ["+ newTime +"] from eventDate ["+eventDate+"].");
    				//	}
    				//}
    				if (!result.getResultState().equals(ResultState.AMENDED)) {
    					result.setResultState(ResultState.NOT_AMENDED);
    					result.addComment("No changes proposed, all candidate fields to fill in contain values.");
    				}
    			} // end interval extraction
    		} // end format validity check
    	}
    	
    	// make sure that an empty value map is returned instead of null.
    	if (result.getValue() == null) {
    		result.setValue(new AmendmentValue(new HashMap<String, String>()));
    	}
    	
    	return result;
	}
    
    @Provides("ad0c8855-de69-4843-a80c-a5387d20fbc8")
    public DQResponse<ComplianceValue> validationYearOutofrange(@ActedUpon("dwc:year") String year) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

    	Integer upperBound = LocalDateTime.now().getYear();

        return validationYearOutofrange(year, 1600, upperBound);
    }
	
	/**
	 * Given a year, evaluate whether that year falls in the range between provided upper and lower bounds inclusive.
	 * If null is provided for lowerBound, then the value 1600 will be used as the lower bound.  If null is porovided
	 * for the upper bound, the current year will be used.  This implementation uses the year from the local date/time 
	 * as the upper bound, and will give different answers for values of year within 1 of the current year when run 
	 * in different time zones within one day of a year boundary, thus this test is not suitable for fine grained 
	 * evaluations of time.
	 * 
     * #84 Validation SingleRecord Conformance: year outofrange
     *
     * Provides: VALIDATION_YEAR_OUTOFRANGE
     * 
     * Parameters: bdq:earliestDate="1600"; bdq:latestDate=current year
     * 
     * @param year the provided dwc:year to evaluate
	 * @param bdq:earliestDate integer for lower bound of range of in range years, if null 1600 will be used.
	 * @param bdq:latestDate integer for upper bound of range of in range years, if null current year will be used.
     * @return DQResponse the response of type ComplianceValue to return
	 */
    @Provides("ad0c8855-de69-4843-a80c-a5387d20fbc8")
    public static DQResponse<ComplianceValue> validationYearOutofrange(
    		@ActedUpon(value = "dwc:year") String year,  
    		@Parameter(name = "bdq:earliestDate") Integer lowerBound,
    		@Parameter(name = "bdq:latestDate") Integer upperBound) 
    {
    	DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
    	
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:year is not present, 
        // or is EMPTY or cannot be interpreted as an integer; COMPLIANT 
        // if the value of dwc:year is within the Parameter range; 
        // otherwise NOT_COMPLIANT 

        // This test is defined as parameterized.
        // bdq:earliestDate="1600"; bdq:latestDate=current year
    	
    	if (lowerBound==null) { 
    		lowerBound = 1600;
    	}
    	if (upperBound==null) { 
    		upperBound = LocalDateTime.now().getYear();
    	}
    	if (DateUtils.isEmpty(year)) {
    		result.addComment("No value provided for dwc:year.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		try { 
    			int numericYear = Integer.parseInt(year.trim());
    			if (numericYear<lowerBound || numericYear>upperBound) { 
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for dwc:year '" + year + "' is not an integer in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    			} else { 
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dwc:year '" + year + "' is an integer in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    			}
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    		} catch (NumberFormatException e) { 
    			logger.debug(e.getMessage());
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("Unable to parse dwc:year as an integer:" + e.getMessage());
    		}
    	}
    	return result;
    }		
	
    /**
     * #36 Validation SingleRecord Conformance: eventdate outofrange
     * 
     * Given an eventDate check to see if that event date falls entirely outside a range from a
     * specified lower bound (1600-01-01 by default) and a specified upper bound (the end of the 
     * current year by default)   
     *
     * Provides: VALIDATION_EVENTDATE_OUTOFRANGE
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("3cff4dc4-72e9-4abe-9bf3-8a30f1618432")
    public static DQResponse<ComplianceValue> validationEventdateOutofrange(@ActedUpon("dwc:eventDate") String eventDate) {
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
        // or if the value of dwc:eventDate is not a valid ISO 8601-1:2019 
        // date; COMPLIANT if the range of dwc:eventDate is entirely 
        // within the parameter range, otherwise NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:earliestValidDate="1600"; bdq:latestValidDate=current year
    	
    	String currentYear = String.format("%04d",Calendar.getInstance().get(Calendar.YEAR)) + "-12-31";
    	return DwCEventDQ.validationEventdateOutofrange(eventDate,"1600-01-01",currentYear);
    }
    	
        
    /**
     * #36 Validation SingleRecord Conformance: eventdate outofrange
     * 
     * Given an eventDate check to see if that event date falls entirely outside a range from a
     * specified lower bound (1600-01-01 by default) and a specified upper bound (the end of the 
     * current year by default)   
     *
     * Provides: VALIDATION_EVENTDATE_OUTOFRANGE
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param earlyestValidDate the earlyest date for which eventDate can be valid
     * @param latestValidDate the  latest date for which eventDate can be valid
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("3cff4dc4-72e9-4abe-9bf3-8a30f1618432")
    public static DQResponse<ComplianceValue> validationEventdateOutofrange(@ActedUpon("dwc:eventDate") String eventDate, @Parameter(name="bdq:earliestValidDate") String earlyestValidDate, @Parameter(name="bdq:latestValidDate") String latestValidDate ) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
 
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
        // or if the value of dwc:eventDate is not a valid ISO 8601-1:2019 
        // date; COMPLIANT if the range of dwc:eventDate is entirely 
        // within the parameter range, otherwise NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:earliestValidDate="1600"; bdq:latestValidDate=current year
        
        if (DateUtils.isEmpty(latestValidDate)) {
        	latestValidDate = String.format("%04d",Calendar.getInstance().get(Calendar.YEAR)) + "-12-31";
        }
        if (DateUtils.isEmpty(earlyestValidDate)) { 
        	earlyestValidDate = "1600-01-01";
        }
        
    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for dwc:eventDate.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		logger.debug(earlyestValidDate);
    		logger.debug(latestValidDate);
    		logger.debug(eventDate);
    		if (! DateUtils.eventDateValid(eventDate)) { 
    			result.addComment("Value provided for dwc:eventDate ["+eventDate+"] not recognized as a valid date.");
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			logger.debug(result.getComment());
    		} else { 
    			LocalDateInterval interval = DateUtils.extractInterval(eventDate);
    			LocalDateInterval bounds = DateUtils.extractInterval(earlyestValidDate + "/" + latestValidDate );
    			logger.debug(bounds);
    			logger.debug(interval);
    			if (bounds.contains(interval)) { 
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dwc:eventDate '" + eventDate + "' falls entirely within the range " + earlyestValidDate + " to " + latestValidDate + ".");
    			} else { 
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				if (bounds.overlaps(interval)) { 
    					result.addComment("Provided value for dwc:eventDate '" + eventDate + "' extends outside the range " + earlyestValidDate + " to " + latestValidDate + ".");
    				} else { 
    					result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is outside the range " + earlyestValidDate + " to " + latestValidDate + ".");
    				}
    			} 
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    		}
    	}
    	return result;
    }	    

}
