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
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.report.ResultState;
import org.datakurator.ffdq.model.report.result.AmendmentValue;
import org.datakurator.ffdq.model.report.result.CompletenessValue;
import org.datakurator.ffdq.model.report.result.ComplianceValue;
import org.datakurator.ffdq.model.report.result.NumericalValue;
import org.joda.time.Interval;


/**
 * Darwin Core Event eventDate related Data Quality Measures, Validations, and Enhancements. 
 * 
 *  Provides support for the following draft TDWG DQIG TG2 validations and amendments.  
 *  
 *  DAY_MONTH_TRANSPOSED  dayMonthTransposition(@ActedUpon(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) 
 *  DAY_MONTH_YEAR_FILLED_IN
 *  EVENTDATE_FILLED_IN_FROM_VERBATIM extractDateFromVerbatim(@DQParam("dwc:eventDate") String eventDate, @Consulted(value = "dwc:verbatimEventDate") String verbatimEventDate)
 *  START_ENDDAYOFYEAR_FILLED_IN
 *
 *  EVENT_DATE_DURATION_SECONDS  measureDurationSeconds(@DQParam("dwc:eventDate") String eventDate)
 *  DAY_IS_FIRST_OF_CENTURY
 *  DAY_IS_FIRST_OF_YEAR
 *  
 *  DAY_IN_RANGE  isDayInRange(@DQParam("dwc:day") String day)   
 *  MONTH_IN_RANGE  isMonthInRange(@DQParam("dwc:month") String month) 
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
 *  UPSTREAM_EVENTDATE_FILLED_IN_FROM_START_END  extractDateFromStartEnd(@DQParam("dwc:eventDate") String eventDate, @Consulted(value = "startDate") String startDate, @Consulted(value="endDate") String endDate) 
 * 
 * @author mole
 *
 */
@DQClass("b844059f-87cf-4c31-b4d7-9a52003eef84")
public class DwCEventDQ {
	
	private static final Log logger = LogFactory.getLog(DwCEventDQ.class);
	
	/**
	 * Measure the duration of an event date in seconds.
	 *
	 * Provides: EVENT_DATE_DURATION_SECONDS
	 * 
	 * @param eventDate to measure duration in seconds
	 * @return EventDQMeasurement object, which if state is COMPLETE has a value of type Long.
	 */
	@DQProvides("b0753f69-08c1-45f5-a5ca-48d24e76d813")
	public static DQResponse<NumericalValue> measureDurationSeconds(@DQParam("dwc:eventDate") final String eventDate) {
		DQResponse<NumericalValue> result = new DQResponse<>();

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
	 * Measure the completeness of an event date.
	 *
	 * Provides: EVENT_DATE_COMPLETENESS
	 *
	 * @param eventDate to check if empty
	 * @return EventDQMeasurement object, which if state is COMPLETE has a value of type Long.
	 */
	@DQProvides(value = "0a59e03f-ebb5-4df3-a802-2e444de525b5")

	public static DQResponse<CompletenessValue> measureCompleteness(@DQParam("dwc:eventDate") final String eventDate) {
		DQResponse<CompletenessValue> result = new DQResponse<>();

		if (!DateUtils.isEmpty(eventDate)) {
			result.addComment("Value provided for eventDate.");
			result.setValue(CompletenessValue.COMPLETE);
			result.setResultState(ResultState.RUN_HAS_RESULT);
		} else {
			result.addComment("No value provided for eventDate.");
			result.setValue(CompletenessValue.NOT_COMPLETE);
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
     *    resultState is CHANGED if a new value is proposed.
     */
	@DQProvides("da63f836-1fc6-4e96-a612-fa76678cfd6a")
    public static DQResponse<ComplianceValue> eventDateConsistentWithVerbatim(@DQParam(value = "dwc:eventDate") String eventDate, @DQParam(value = "dwc:verbatimEventDate") String verbatimEventDate) {
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
     * If a dwc:eventDate is empty and the verbatimEventDate is not empty, try to populate the 
     * eventDate from the verbatim value.
     *
	 * Provides: EVENTDATE_FILLED_IN_FROM_VERBATIM
	 *
     * @param eventDate to check for emptyness
     * @param verbatimEventDate to try to replace a non-empty event date.
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     */
	@DQProvides("6d0a0c10-5e4a-4759-b448-88932f399812")
    public static DQResponse<AmendmentValue> extractDateFromVerbatim(@DQParam("dwc:eventDate") String eventDate, @DQParam("dwc:verbatimEventDate") String verbatimEventDate) {
		DQResponse<AmendmentValue> result = new DQResponse<>();
		AmendmentValue extractedValues = new AmendmentValue();

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

					extractedValues.addResult("dwc:eventDate", extractResponse.getResult());

    		        if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
    		        	result.setResultState(ResultState.AMBIGUOUS);
    		        	result.setValue(extractedValues);
    		        	result.addComment(extractResponse.getComment());
    		        } else { 
    		        	if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
							result.setValue(extractedValues);
    		        		result.addComment("Interpretation of verbatimEventDate [" + verbatimEventDate + "] is suspect.");
    		        		result.addComment(extractResponse.getComment());
    		        	}
    		        	result.setResultState(ResultState.CHANGED);
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
    		result.setResultState(ResultState.NO_CHANGE);
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
    @DQProvides("8cdd4f44-e7ed-4484-a1b8-4e6407a491e2")
    public static DQResponse<AmendmentValue> extractDateFromParts(@DQParam("dwc:eventDate") String eventDate,
    		 @DQParam("dwc:verbatimEventDate") String verbatimEventDate,
    		 @DQParam("dwc:startDayOfYear") String startDayOfYear,
    		 @DQParam("dwc:endDayOfYear") String endDayOfYear,
    		 @DQParam("dwc:year") String year,
    		 @DQParam("dwc:month") String month,
    		 @DQParam("dwc:day") String day) {
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
					AmendmentValue extractedValues = new AmendmentValue();
					extractedValues.addResult("dwc:eventDate", extractResponse.getResult());
					result.setValue(extractedValues);

    		        if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
    		        	result.setResultState(ResultState.AMBIGUOUS);
    		        	result.addComment(extractResponse.getComment());
    		        } else { 
    		        	if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
    		        		result.addComment("Interpretation of verbatimEventDate [" + verbatimEventDate + "] is suspect.");
    		        		result.addComment(extractResponse.getComment());
    		        	}
    		        	result.setResultState(ResultState.CHANGED);
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
    		result.setResultState(ResultState.NO_CHANGE);
    		result.addComment("eventDate contains a value, not changing.");
    	}
    	return result;
    }

	/**
	 * 	Provides : UPSTREAM_EVENTDATE_FILLED_IN_FROM_START_END
	 */
	@DQProvides("e4ddf9bc-cd10-46cc-b307-d6c7233a240a")
    public static DQResponse<AmendmentValue> extractDateFromStartEnd(@DQParam("dwc:eventDate") String eventDate,
														   @DQParam("dwc:startDate") String startDate,
														   @DQParam("dwc:endDate") String endDate) {
		DQResponse<AmendmentValue> result = new DQResponse<>();

    	if (DateUtils.isEmpty(eventDate)) { 
    		String response = DateUtils.createEventDateFromStartEnd(startDate, endDate);
    		if (DateUtils.isEmpty(response)) { 
    		    result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		    result.addComment("Unable to extract a date from " + startDate + " and " + endDate);
    		} else {
    			AmendmentValue extractedValues = new AmendmentValue();
				extractedValues.addResult("dwc:eventDate", response);
				result.addComment("Extracted a date " + response + " from " + startDate + " and " + endDate);

				result.setValue(extractedValues);
    		    result.setResultState(ResultState.CHANGED);
    		}
    	} else { 
    		result.setResultState(ResultState.NO_CHANGE);
    		result.addComment("eventDate contains a value, not changing.");
    	}
    	return result;
    }    
    

    /**
     * Test to see whether a provided dcterms:modified is a validly formated ISO date.
     * 
     * Provides: MODIFIED_DATE_INVALID/MODIFIED_DATE_VALID
     * 
     * @param modified  a string to test
     * @return COMPLIANT if modified is a validly formated ISO date/time with a duration of less than one day, NOT_COMPLIANT if
     *     not an ISO date/time or a range of days, INTERNAL_PREREQUSISITES_NOT_MET if modified is empty.
     */
    @DQProvides("62a9c256-43e4-41ee-8938-d2d2e99479ef")
    public static DQResponse<ComplianceValue> isModifiedDateValid(@DQParam("dcterms:modified") String modified) {
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


    /**
     * Given an event date, check to see if it is empty or contains a valid date value.  If it contains
     * a value that is not a valid date, propose a properly formatted eventDate as an amendment.
     *
	 * Provides: EVENTDATE_FORMAT_CORRECTION
	 *
     * @param eventDate to check
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     */
	@DQProvides("134c7b4f-1261-41ec-acb5-69cd4bc8556f")
    public static DQResponse<AmendmentValue> correctEventDateFormat(@DQParam("dwc:eventDate") String eventDate) {
		DQResponse<AmendmentValue> result = new DQResponse<>();

    	if (DateUtils.eventDateValid(eventDate)) {
    		result.setResultState(ResultState.NO_CHANGE);
    		result.addComment("eventDate contains a correctly formatted date, not changing.");
    	} else {
    		if (DateUtils.isEmpty(eventDate)) {
    		    result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
    		    	AmendmentValue correctedValues = new AmendmentValue();
					correctedValues.addResult("dwc:eventDate", extractResponse.getResult());
					result.setValue(correctedValues);

    		        if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
    		        	result.setResultState(ResultState.AMBIGUOUS);
    		        	result.addComment(extractResponse.getComment());
    		        } else { 
    		        	if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
    		        		result.addComment("Interpretation of eventDate [" + eventDate + "] is suspect.");
    		        		result.addComment(extractResponse.getComment());
    		        	}
    		        	result.setResultState(ResultState.CHANGED);
    		        }
    		    } else { 
    		        result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
    @DQProvides("367bf43f-9cb6-45b2-b45f-b8152f1d334a")
    public static DQResponse<AmendmentValue> correctModifiedDateFormat(@DQParam("dcterms:modified") String modified) {
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
                	AmendmentValue correctedValues = new AmendmentValue();
                	correctedValues.addResult("dcterms:modified", extractResponse.getResult());
					result.setValue(correctedValues);

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
     * Given a dateIdentified, check to see if it is empty or contains a valid date value.  If it contains
     * a value that is not a valid (ISO formatted) date, propose a properly formatted dateModified as an amendment.
     * 
	 * Provides: DATEIDENTIFIED_FORMAT_AMENDED
	 * 
     * @param dateIdentified to check
     * @return an implementation of DQAmendmentResponse, with a value containing a key for dwc:eventDate and a
     *    resultState is CHANGED if a new value is proposed.
     */
	@DQProvides("39bb2280-1215-447b-9221-fd13bc990641")
    public static DQResponse<AmendmentValue> correctIdentifiedDateFormat(@DQParam("dwc:dateIdentified") String dateIdentified) {
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
                	AmendmentValue correctedValues = new AmendmentValue();
					correctedValues.addResult("dwc:dateIdentified", extractResponse.getResult());
					result.setValue(correctedValues);

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
    @DQProvides("48aa7d66-36d1-4662-a503-df170f11b03f")
    public static DQResponse<ComplianceValue> isDayInRange(@DQParam("dwc:day") String day) {
    	DQResponse<ComplianceValue> result = new DQResponse<>();

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
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
    @DQProvides("01c6dafa-0886-4b7e-9881-2c3018c98bdc")
    public static DQResponse<ComplianceValue> isMonthInRange(@DQParam("dwc:month") String month) {
		DQResponse<ComplianceValue> result = new DQResponse<>();

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
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment(e.getMessage());
    		}
    	}
    	return result;
    }

	/**
	 *	Provides: EVENTDATE_PRECISON_JULIAN_YEAR_OR_BETTER
	 */
	@DQProvides("fd00e6be-45e4-4ced-9f3d-5cde30b21b69")
    public static DQResponse<ComplianceValue> isEventDateJulianYearOrLess(@DQParam("dwc:eventDate") String eventDate) {
		DQResponse<ComplianceValue> result = new DQResponse<>();

    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for eventDate.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		if (DateUtils.eventDateValid(eventDate)) { 
    			logger.debug(eventDate);
    			logger.debug(DateUtils.measureDurationSeconds(eventDate));
    			if (DateUtils.measureDurationSeconds(eventDate)<= 31557600) { 
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for eventDate '" + eventDate + "' has a duration less than or equal to one Julian year of 365.25 days.");
    			}  else { 
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for eventDate '" + eventDate + "' has a duration more than one Julian year of 365.25 days.");
    			}
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    		} else { 
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    			result.addComment("provided dwc:eventDate not recognized as a valid date value.");
    		}
    	}
    	return result;
    }      
    
    
    @DQProvides("31d463b4-2a1c-4b90-b6c7-73459d1bad6d")
    public static DQResponse<ComplianceValue> isEventDateYearOrLess(@DQParam("dwc:eventDate") String eventDate) {
		DQResponse<ComplianceValue> result = new DQResponse<>();

    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for eventDate.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
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
    @DQProvides("5618f083-d55a-4ac2-92b5-b9fb227b832f")
    public static DQResponse<ComplianceValue> isDayPossibleForMonthYear(@DQParam("dwc:year") String year, @DQParam("dwc:month") String month, @DQParam("dwc:day") String day) {
		DQResponse<ComplianceValue> result = new DQResponse<>();

		DQResponse<ComplianceValue> monthResult =  isMonthInRange(month);
		DQResponse<ComplianceValue> dayResult =  isDayInRange(day);
    	
    	if (monthResult.getResultState().equals(ResultState.RUN_HAS_RESULT)) {
    		if (monthResult.getValue().equals(ComplianceValue.COMPLIANT)) {
    	        if (dayResult.getResultState().equals(ResultState.RUN_HAS_RESULT)) {
    	        	if (dayResult.getValue().equals(ComplianceValue.COMPLIANT)) {
    	        		try { 
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
    	        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
    @DQProvides("f98a54eb-59e7-44c7-b96f-200e6af1c895")
    public static final DQResponse<AmendmentValue> dayMonthTransposition(@DQParam("dwc:month") String month, @DQParam("dwc:day") String day) {
		DQResponse<AmendmentValue> result = new DQResponse<>();

    	if (DateUtils.isEmpty(day) || DateUtils.isEmpty(month)) { 
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("Either month or day was not provided.");
    	} else {
			DQResponse<ComplianceValue> monthResult =  isMonthInRange(month);
			DQResponse<ComplianceValue> dayResult =  isDayInRange(day);

			if (monthResult.getResultState().equals(ResultState.RUN_HAS_RESULT)) {
        		if (monthResult.getValue().equals(ComplianceValue.NOT_COMPLIANT)) {
        			// month is integer, but out of range
        	        if (dayResult.getResultState().equals(ResultState.RUN_HAS_RESULT)) {
        	        	// day is also integer
        	        	int dayNumeric = Integer.parseInt(day);
        	        	int monthNumeric = Integer.parseInt(month);
        	        	if (DateUtils.isDayInRange(monthNumeric) && DateUtils.isMonthInRange(dayNumeric)) { 
        	        		// day is in range for months, and month is in range for days, so transpose.
							AmendmentValue transposedValues = new AmendmentValue();
							transposedValues.addResult("dwc:month", day);
							transposedValues.addResult("dwc:day", month);

							result.setValue(transposedValues);
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
        	            result.setResultState(ResultState.NO_CHANGE);
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
    
}
