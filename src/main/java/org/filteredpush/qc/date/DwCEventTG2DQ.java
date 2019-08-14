/** DwCEventTG2DQ.java
 * 
 * Copyright 2019 President and Fellows of Harvard College
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

import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Consulted;
import org.datakurator.ffdq.annotations.Mechanism;
import org.datakurator.ffdq.annotations.Parameter;
import org.datakurator.ffdq.annotations.Provides;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.ResultState;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.CompletenessValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.NumericalValue;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.LocalDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Implementations (as of August 2019) of all of the current CORE TIME tests in the 
 * TDWG/BDQ TG2 test suite.  
 * 
 * TODO comments indicate concerns related to implementing as specified in the issues in github.com/tdwg/bdq
 * 
 * This class is expected to go away, with the methods being merged into the DwCEventDQ and DwCOtherDateDQ classes,
 * conflicts currently exist between the implementations in those classes and the current specifications implemented
 * here.  Adding this file to allow for work in progress comparision of the test implementations.
 * 
 * @author mole
 *
 */
@Mechanism(label="Kurator: Date Validator - DwCEventDQ", value="b844059f-87cf-4c31-b4d7-9a52003eef84")
public class DwCEventTG2DQ {
	
	private static final Log logger = LogFactory.getLog(DwCEventDQ.class);

    /**
     * #140 Measure SingleRecord Resolution: eventdate precisioninseconds
     *
     * Provides: MEASURE_EVENTDATE_PRECISIONINSECONDS
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse<NumericalValue>
     */
    @Provides("56b6c695-adf1-418e-95d2-da04cad7be53")
    public DQResponse<NumericalValue> measureEventdatePrecisioninseconds(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<NumericalValue> result = new DQResponse<NumericalValue>();

        // Specification:
        // INTERNAL_PREREQUESITES_NOT_MET if the field dwc:eventDate 
        // is not present or is EMPTY or does not contain a valid ISO 
        // 8601-1:2019 date; REPORT on the length of the period expressed 
        // in the dwc:eventDate in seconds; otherwise NOT_REPORTED 
        //

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
     * #69 Validation SingleRecord Conformance: dateidentified notstandard
     *
     * Provides: VALIDATION_DATEIDENTIFIED_NOTSTANDARD
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("66269bdd-9271-4e76-b25c-7ab81eebe1d8")
    public DQResponse<ComplianceValue> validationDateidentifiedNotstandard(@ActedUpon("dwc:dateIdentified") String dateIdentified) {
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
     * @return DQResponse<ComplianceValue>
     */
    @Provides("dc8aae4b-134f-4d75-8a71-c4186239178e")
    public DQResponse<ComplianceValue> validationDateidentifiedOutofrange(
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
    	// either not present, is EMPTY or is not a valid ISO 8601-1:2019 
    	// date, or if the field dwc:eventDate is non-EMPTY and is not a 
    	// valid ISO 8601-1:2019 date; COMPLIANT if the value of the field 
    	// dwc:dateIdentified is not prior to the eventDate, does not extend 
    	// into the future, or optionally does not extend before a date designated
    	// when the test is run (e.g., prior to 1753-01-01); otherwise NOT_COMPLIANT

    	if (DateUtils.isEmpty(dateIdentified)) {
    		result.addComment("No value provided for dwc:dateIdentified.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		if (! DateUtils.eventDateValid(dateIdentified)) { 
    			result.addComment("Value provided for dwc:dateIdentified ["+dateIdentified+"] not recognized as a valid date.");
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		} else { 
    			Interval identifiedInterval = DateUtils.extractInterval(dateIdentified);
    			if (withinInterval.contains(identifiedInterval)) {
    				if (!DateUtils.isEmpty(eventDate)) {
    					if (! DateUtils.eventDateValid(eventDate)) { 
    						Interval eventInterval = DateUtils.extractInterval(eventDate);
    						if (identifiedInterval.contains(eventInterval)) { 
    	        	    		result.setValue(ComplianceValue.COMPLIANT);
    	        	    		result.addComment("Provided value for dateIdentified [" + dateIdentified + "] is within range and does not preceede the eventDate.");;
    						} else { 
    	        	    		result.setValue(ComplianceValue.NOT_COMPLIANT);
    	        	    		result.addComment("Provided value for dateIdentified [" + dateIdentified + "] is within range but is more recent than the eventDate ["+eventDate+"].");;
    						}
    						result.setResultState(ResultState.RUN_HAS_RESULT);
    					} else { 
    						result.addComment("Value provided for dwc:eventDate ["+eventDate+"] not recognized as a valid date, unable to compare with dwc:dateIdentified.");
    						result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    					}
    				} else { 
    					result.setResultState(ResultState.RUN_HAS_RESULT);
    	        	    result.setValue(ComplianceValue.COMPLIANT);
    	        	    result.addComment("Provided value for dateIdentified [" + dateIdentified + "] is within range (and there is no value of eventDate to compare with).");
    				}
    			} else { 
    				result.setResultState(ResultState.RUN_HAS_RESULT);
    	        	result.setValue(ComplianceValue.NOT_COMPLIANT);
    	        	result.addComment("Provided value for dateIdentified [" + dateIdentified + "] is or extends outside the allowed range of ["+ withinInterval.toString() +"].");
    				
    			}
    		}
    	}        
        
        return result;
    }

    /**
     * #147 Validation SingleRecord Conformance: day notstandard
     *
     * Provides: VALIDATION_DAY_NOTSTANDARD
     *
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("47ff73ba-0028-4f79-9ce1-ee7008d66498")
    public DQResponse<ComplianceValue> validationDayNotstandard(
    		@Consulted("dwc:year") String year, 
    		@Consulted("dwc:month") String month, 
    		@ActedUpon("dwc:day") String day) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // TODO: Problem with specification: refers to cast, rather than unabmigously interpreted as.
        // TODO: Appears to conflate test of standard form with test of in range.
        //       Non-standard form is potentially fixable with an amendment, out of range is not. 
        
        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if a) dwc:day is not present 
        // or is EMPTY, or b) dwc:day can not be cast as an integer, 
        // or c) dwc:day can be cast as an integer between 29 and 31 
        // inclusive and dwc:month can not be cast as an integer between 
        // 1 and 12, or d) dwc:month can be cast as the integer 2 and 
        // dwc:month can be cast as the integer 29 and dwc:year can 
        // not be cast as a valid ISO 8601 year; COMPLIANT e) if the 
        // value of the field dwc:day can be cast as an integer between 
        // 1 and 28 inclusive, or f) dwc:day can be cast as an integer 
        // between 29 and 30 and dwc:month can be cast as one of (4,6,9,11), 
        // or g) dwc:day can be cast as an integer between 29 and 31 
        // and dwc:month can be cast as one of (1,3,5,7,8,10,12), or 
        // h) dwc:day can be cast as the integer 29 and dwc:month can 
        // be cast as the integer 2 and dwc:year is a valid leap year 
        // (evenly divisible by 400 or (evenly divisible by 4 but not 
        //evenly divisible by 100)); otherwise NOT_COMPLIANT 

    	DQResponse<ComplianceValue> monthResult =  validationMonthNotstandard(month);
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
        
        return result;
    }
    
    // TODO: Check status of this issue, should be an open issue distinct from validation_day_standard
    private static DQResponse<ComplianceValue> isDayInRange(@ActedUpon(value = "dwc:day") String day) {
		DQResponse<ComplianceValue> result = new DQResponse<>();

		if (DateUtils.isEmpty(day)) {
			result.addComment("No value provided for day.");
			result.setValue(ComplianceValue.NOT_COMPLIANT);
			result.setResultState(ResultState.RUN_HAS_RESULT);
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
    
    /**
     * #131 Validation SingleRecord Conformance: enddayofyear outofrange
     *
     * Provides: VALIDATION_ENDDAYOFYEAR_OUTOFRANGE
     *
     * @param year the provided dwc:year to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("9a39d88c-7eee-46df-b32a-c109f9f81fb8")
    public DQResponse<ComplianceValue> validationEnddayofyearOutofrange(@ActedUpon("dwc:year") String year, @ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the fields dwc:year or 
        // dwc:endDayOfYear year are either not present or are EMPTY; 
        // COMPLIANT if the value of the field dwc:endDayOfYear is 
        // a valid day given the year; otherwise NOT_COMPLIANT 

    	if (DateUtils.isEmpty(endDayOfYear)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("endDayOfYear was not provided.");
    	} else {
    	    try {
    	       Integer numericEndDay = Integer.parseInt(endDayOfYear);
    	       if (numericEndDay>0 && numericEndDay<366) {
    	    	   result.setValue(ComplianceValue.COMPLIANT);
    	    	   result.setResultState(ResultState.RUN_HAS_RESULT);
    		       result.addComment("endDayOfYear [" + endDayOfYear + "] is in range for days of the year.");
    	       } else if (numericEndDay==366) {
    	           if (DateUtils.isEmpty(year)) {
    		            result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		            result.addComment("year was not provided and day is 366, could be valid in a leap year.");
    	           } else {
    	        	   String potentialDay = DateUtils.createEventDateFromParts("", endDayOfYear, "", year, "", "");
    	        	   if (DateUtils.isEmpty(potentialDay)) {
    		               result.setValue(ComplianceValue.NOT_COMPLIANT);
    	    	           result.setResultState(ResultState.RUN_HAS_RESULT);
    		               result.addComment("endDayOfYear [" + endDayOfYear + "] is out of range for year ["+ year +"].");
    	        	   } else if (DateUtils.eventDateValid(potentialDay)) {
    	    	           result.setValue(ComplianceValue.COMPLIANT);
    	    	           result.setResultState(ResultState.RUN_HAS_RESULT);
    		               result.addComment("endDayOfYear [" + endDayOfYear + "] is in range for days of the year ["+year+"].");
    	        	   } else {
    		               result.setValue(ComplianceValue.NOT_COMPLIANT);
    	    	           result.setResultState(ResultState.RUN_HAS_RESULT);
    		               result.addComment("endDayOfYear [" + endDayOfYear + "] is out of range for year ["+ year +"].");
    	        	   }
    	           }
    	       } else {
    		       result.setValue(ComplianceValue.NOT_COMPLIANT);
    	    	   result.setResultState(ResultState.RUN_HAS_RESULT);
    		       result.addComment("endDayOfYear [" + endDayOfYear + "] is out of range for days in the year.");
    	       }
    	    } catch (NumberFormatException e) {
    		   result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		   result.addComment("endDayOfYear [" + endDayOfYear + "] is not a number.");
    	    }
    	}        
        
        return result;
    }

    /**
     * #88 Validation SingleRecord Completeness: event empty
     *
     * Provides: VALIDATION_EVENT_EMPTY
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param verbatimEventDate the provided dwc:verbatimEventDate to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("41267642-60ff-4116-90eb-499fee2cd83f")
    public DQResponse<ComplianceValue> validationEventEmpty(@ActedUpon("dwc:startDayOfYear") String startDayOfYear, 
    		@ActedUpon("dwc:eventDate") String eventDate, 
    		@ActedUpon("dwc:year") String year, 
    		@ActedUpon("dwc:verbatimEventDate") String verbatimEventDate, 
    		@ActedUpon("dwc:month") String month, 
    		@ActedUpon("dwc:day") String day, 
    		@ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // COMPLIANT if at least one field needed to determine the 
        // event date exists and is not EMPTY; otherwise NOT_COMPLIANT 
        //

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
     * #67 Validation SingleRecord Consistency: eventdate inconsistent
     *
     * Provides: VALIDATION_EVENT_INCONSISTENT
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param eventTime the provided dwc:eventTime to evaluate
     * @param day the provided dwc:day to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("5618f083-d55a-4ac2-92b5-b9fb227b832f")
    public DQResponse<ComplianceValue> validationEventInconsistent(
    		@ActedUpon("dwc:startDayOfYear") String startDayOfYear, 
    		@ActedUpon("dwc:eventDate") String eventDate, 
    		@ActedUpon("dwc:year") String year, 
    		@ActedUpon("dwc:month") String month, 
    		@ActedUpon("dwc:day") String day, 
    		@ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Note: Does not validate against verbatim date, just atomized date fields.
        
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is either 
        // not present or is EMPTY, or all of dwc:year, dwc:month, 
        // dwc:day, dwc:startDayOfYear and dwc:endDayOfYear are not 
        // present or are EMPTY; COMPLIANT if the provided values for 
        // dwc:year, dwc:month, dwc:day, dwc:startDayOfYear and dwc:endDayofYear 
        // are within the range of the supplied dwc:eventDate; otherwise 
        // NOT_COMPLIANT 
        
        
		boolean inconsistencyFound = false;
		boolean interpretationProblem = false;

		// dwc:eventTime is explicitly excluded.
//		// Compare eventDate and eventTime
//		if (!DateUtils.isEmpty(eventDate) && !DateUtils.isEmpty(eventTime)) {
//			if (DateUtils.containsTime(eventDate)) {
//				String time1 = DateUtils.extractZuluTime(eventDate);
//				String time2 = DateUtils.extractZuluTime("1900-01-01 "+ eventTime);
//				if (time1!=null && time2!=null) {
//					if (!time1.equals(time2)) {
//						inconsistencyFound = true;
//						result.addComment("Time part of the provided value for eventDate '" + eventDate + "' appears to represents a different time than " +
//								" eventTime '" + eventTime + "'.");
//					}
//				} else {
//					interpretationProblem = true;
//					result.addComment("Unable to interpret the time part of either the provided value for eventDate '" + eventDate + "' or " +
//								" eventTime '" + eventTime + "'.");
//				}
//			}
//		}
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

        // Note: Does not validate against verbatim date, just atomized date fields.
		// compare eventDate with verbatimEventDate
//		if (!DateUtils.isEmpty(eventDate) && !DateUtils.isEmpty(verbatimEventDate)) {
//			String testDate = DateUtils.createEventDateFromParts(verbatimEventDate, "", "", "", "", "");
//			if (!DateUtils.isEmpty(testDate)) {
//				if (!DateUtils.eventsAreSameInterval(eventDate, testDate)) {
//					inconsistencyFound = true;
//					result.addComment("Provided value for eventDate '" + eventDate + "' appears to represent a different interval of time from the verbatimEventDate [" + verbatimEventDate +"].");
//				}
//			}
//		}

		if (DateUtils.isEmpty(eventDate) &&
				DateUtils.isEmpty(year) &&
				DateUtils.isEmpty(month) &&
				DateUtils.isEmpty(day) &&
				DateUtils.isEmpty(startDayOfYear) &&
				DateUtils.isEmpty(endDayOfYear)
			 )
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

        return result;
    }

    /**
     * #33 Validation SingleRecord Completeness: eventdate empty
     *
     * Provides: VALIDATION_EVENTDATE_EMPTY
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("f51e15a6-a67d-4729-9c28-3766299d2985")
    public DQResponse<ComplianceValue> validationEventdateEmpty(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:eventDate 
        // is not present; COMPLIANT if the value of the field dwc:eventDate 
        //is not EMPTY; otherwise NOT_COMPLIANT 
        
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
     * #66 Validation SingleRecord Conformance: eventdate notstandard
     *
     * Provides: VALIDATION_EVENTDATE_NOTSTANDARD
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("4f2bf8fd-fc5c-493f-a44c-e7b16153c803")
    public DQResponse<ComplianceValue> validationEventdateNotstandard(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:eventDate 
        // is either not present or is EMPTY; COMPLIANT if the value 
        // of dwc:eventDate is a valid ISO 8601-1:2019 date; otherwise 
        // NOT_COMPLIANT 

    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for dwc:eventDate.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else {
    		try {
    	        if (DateUtils.eventDateValid(eventDate)) {
    				result.setValue(ComplianceValue.COMPLIANT);
    				result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is formated as an ISO date. ");
    			} else {
    				result.setValue(ComplianceValue.NOT_COMPLIANT);
    				result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is not a validly formatted ISO date .");
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
     * #36 Validation SingleRecord Conformance: eventdate outofrange
     *
     * Provides: VALIDATION_EVENTDATE_OUTOFRANGE
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("3cff4dc4-72e9-4abe-9bf3-8a30f1618432")
    public DQResponse<ComplianceValue> validationEventdateOutofrange(
    		@ActedUpon("dwc:eventDate") String eventDate,
    		@Parameter(name="bdq:useEarliestDate") Boolean useLowerBound,
    		@Parameter(name="bdq:earliestDate") String earliestDate
    		) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO: Problem implementing with this specification.  "default designated date"
        //TODO: Problem impementing with this specification no failure condition for non-iso date.
        
        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if there is no default designated 
        // date or the field dwc:eventDate is either not present or 
        // is EMPTY; COMPLIANT if the range of dwc:eventDate does not 
        // extend into the future and optionally does not extend before 
        // a date designated when the test is run, otherwise NOT_COMPLIANT 
        //

        //TODO: Parameters. This test is defined as parameterized.
        // Default values: earliest year = 1600, latest year = current year
        
        //TODO: Problem with issue: Needs a useEarliest parameter, but not listed.

        
        // TODO: Implementation is to tightly tied to years, change to assessing intervals.
        Integer lowerBound = 1600;
        if (earliestDate==null) { 
        	earliestDate = "1600-01-01";
        	lowerBound = DateUtils.extractDateInterval(earliestDate).getStart().getYear();
        }
    	
    	if (useLowerBound==null) { 
    		useLowerBound = true;
    	}
    	Integer upperBound = LocalDateTime.now().getYear();
    	if (DateUtils.isEmpty(eventDate)) {
    		result.addComment("No value provided for dwc:eventDate.");
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    	} else { 
    		if (! DateUtils.eventDateValid(eventDate)) { 
    			result.addComment("Value provided for dwc:eventDate ["+eventDate+"] not recognized as a valid date.");
    			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		} else { 
    			int startYear = 0;
    			Interval interval = DateUtils.extractInterval(eventDate);
    			if (DateUtils.isRange(eventDate)) {
    				int endYear = interval.getEnd().getYear();
    				startYear = interval.getStart().getYear();
    				if (useLowerBound) { 
    					if (endYear<lowerBound|| startYear>upperBound) { 
    						result.setValue(ComplianceValue.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is not a range spanning part of the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setValue(ComplianceValue.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is a range spanning at least part of " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					}    				
    				} else { 
    					if (startYear>upperBound) { 
    						result.setValue(ComplianceValue.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is not a range spanning part of the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setValue(ComplianceValue.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is a range spanning at least part of " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					}       					
    				}
    			} else {
    				startYear = interval.getStart().getYear();
    				if (useLowerBound) { 
    					if (startYear<lowerBound || startYear>upperBound) { 
    						result.setValue(ComplianceValue.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' does not have a year in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setValue(ComplianceValue.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' does not have a year in the range " + lowerBound.toString() + " to " + upperBound.toString() + " (current year).");
    					} 
    				} else { 
    					if (startYear>upperBound) { 
    						result.setValue(ComplianceValue.NOT_COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is not after  " + upperBound.toString() + " (current year).");
    					} else { 
    						result.setValue(ComplianceValue.COMPLIANT);
    						result.addComment("Provided value for dwc:eventDate '" + eventDate + "' is after " + upperBound.toString() + " (current year).");
    					}     					
    				}
    			}
    			result.setResultState(ResultState.RUN_HAS_RESULT);
    		}
    	}        
        
        return result;
    }

    /**
     * #126 Validation SingleRecord Conformance: month notstandard
     *
     * Provides: VALIDATION_MONTH_NOTSTANDARD
     *
     * @param month the provided dwc:month to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("01c6dafa-0886-4b7e-9881-2c3018c98bdc")
    public DQResponse<ComplianceValue> validationMonthNotstandard(@ActedUpon("dwc:month") String month) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:month is 
        // either not present or is EMPTY; COMPLIANT if the value of 
        // the field dwc:month is an integer between 1 and 12 inclusive; 
        // otherwise NOT_COMPLIANT 

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
    			result.addComment("Provided value for month '" + month + "' could not be parsed as an integer.");
    			result.addComment(e.getMessage());
    		}
    	}        
        
        return result;
    }

    /**
     * #130 Validation SingleRecord Conformance: startdayofyear outofrange
     *
     * Provides: VALIDATION_STARTDAYOFYEAR_OUTOFRANGE
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param year the provided dwc:year to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("85803c7e-2a5a-42e1-b8d3-299a44cafc46")
    public DQResponse<ComplianceValue> validationStartdayofyearOutofrange(
    		@ActedUpon("dwc:startDayOfYear") String startDayOfYear, 
    		@Consulted("dwc:year") String year) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:startDayOfYear 
        // is either not present or is EMPTY, or if the value of 
        // dwc:startDayOfYear = 366 and dwc:year is either not present 
        // or is EMPTY; COMPLIANT if the value of the field 
        // dwc:startDayOfYear is a valid day given the year; 
        // otherwise NOT_COMPLIANT
        
        if (DateUtils.isEmpty(startDayOfYear)) {
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("one of startDayOfYear or year was not provided to compare.");
        } else {
        	try {
        		Integer numericStartDay = Integer.parseInt(startDayOfYear);
        		if (numericStartDay>0 && numericStartDay<366) {
        			result.setValue(ComplianceValue.COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.addComment("startDayOfYear [" + startDayOfYear + "] is in range for days of the year.");
        		} else if (numericStartDay==366) {
        			if (DateUtils.isEmpty(year)) {
        				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        				result.addComment("year was not provided and day is 366, could be valid in a leap year, otherwise invalid.");
        			} else {
        				String potentialDay = DateUtils.createEventDateFromParts("", startDayOfYear, "", year, "", "");
        				if (DateUtils.isEmpty(potentialDay)) {
        					// createEventDateFromParts will return null and we end up here if value of year can't be interpreted as a year.
        					result.setValue(ComplianceValue.NOT_COMPLIANT);
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.addComment("startDayOfYear [" + startDayOfYear + "] is out of range for year ["+ year +"].");
        				} else if (DateUtils.eventDateValid(potentialDay)) {
        					result.setValue(ComplianceValue.COMPLIANT);
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.addComment("startDayOfYear [" + startDayOfYear + "] is in range for days of the year ["+year+"].");
        				} else {
        					result.setValue(ComplianceValue.NOT_COMPLIANT);
        					result.setResultState(ResultState.RUN_HAS_RESULT);
        					result.addComment("startDayOfYear [" + startDayOfYear + "] is out of range for year ["+ year +"].");
        				}
        			}
        		} else {
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        			result.addComment("startDayOfYear [" + startDayOfYear + "] is out of range for days in the year.");
        		}
        	} catch (NumberFormatException e) {
        		// result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);  specification changes this to not compliant
        		result.setValue(ComplianceValue.NOT_COMPLIANT);
        		result.setResultState(ResultState.RUN_HAS_RESULT);
        		result.addComment("startDayOfYear [" + startDayOfYear + "] is not a number, comparison to year is meaningless.");
        	}
        }        

        return result;
    }

    /**
     * #49 Validation SingleRecord Completeness: year empty
     *
     * Provides: VALIDATION_YEAR_EMPTY
     *
     * @param year the provided dwc:year to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("c09ecbf9-34e3-4f3e-b74a-8796af15e59f")
    public DQResponse<ComplianceValue> validationYearEmpty(@ActedUpon("dwc:year") String year) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:year is 
        // not present; COMPLIANT if the value of the field dwc:year 
        //is not EMPTY; otherwise NOT_COMPLIANT 

        // Note: In this implementation it is not possible to test within this method whether or not
        // dwc:year is present in the data set.
        
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
     * #84 Validation SingleRecord Conformance: year outofrange
     *
     * Provides: VALIDATION_YEAR_OUTOFRANGE
     *
     * @param year the provided dwc:year to evaluate
     * @return DQResponse<ComplianceValue>
     */
    @Provides("ad0c8855-de69-4843-a80c-a5387d20fbc8")
    public DQResponse<ComplianceValue> validationYearOutofRange(@ActedUpon("dwc:year") String year) {
    	Integer earliestYear = Integer.valueOf(1600);
    	return validationYearOutofRange(year, earliestYear, true);
    }

	private DQResponse<ComplianceValue> validationYearOutofRange(@ActedUpon("dwc:year") String year, 
    		@Parameter(name="bdq:earliestDate") Integer earliestDate, 
    		@Parameter(name="bdq:useEarliestDate") boolean useEarliestDate) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
        
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:year is not present, 
        // or is EMPTY or cannot be interpreted as an integer; 
        // COMPLIANT if the value of dwc:year is within the Parameter 
        // range; otherwise NOT_COMPLIANT

        // Parameters. This test is defined as parameterized.
        // Default Values: bdq:earliestDate = 1600, bdq:latestDate = current year
        
        // Notes: The results of this test are time-dependent. Next year is not valid now.
        // Next year it will be. This test provides the option to designate lower and upper 
        // limits to the year. The upper limit, if not provided, should default to the year 
        // when the test is run. NB By convention, use 1600 as a lower limit for collecting 
        // dates of biological specimens.
        
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer earliestYear = Integer.valueOf(1600);
        if (earliestDate!=null) { 
            earliestYear =  earliestDate;
        }
        
        if (!DateUtils.isEmpty(year)) { 
        	try { 
        		Integer yeari = Integer.valueOf(year);
        		if (yeari < earliestYear && useEarliestDate) { 
        			result.addComment("Value provided for dwc:year ["+ year +"] is before the lower bound [" + yeari.toString() + "].");
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        		} else if (yeari > currentYear ) { 
        			result.addComment("Value provided for dwc:year ["+ year +"] is in the future.");
        			result.setValue(ComplianceValue.NOT_COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        		} else { 
        			result.addComment("Value provided for dwc:year ["+ year +"] is an integer between [" + yeari.toString() + "] and [" + currentYear.toString() + "].");
        			result.setValue(ComplianceValue.COMPLIANT);
        			result.setResultState(ResultState.RUN_HAS_RESULT);
        		}
        	} catch (NumberFormatException e) { 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("The value of dwc:year provided ["+year+"] could not be cast as an integer.");
        	}
        } else { 
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("A value for dwc:year was not provided.");
        }

        return result;
    }

    /**
     * #26 Amendment SingleRecord Conformance: dateidentified standardized
     *
     * Provides: AMENDMENT_DATEIDENTIFIED_STANDARDIZED
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @return DQResponse<AmendmentValue>
     */
    @Provides("39bb2280-1215-447b-9221-fd13bc990641")
    public DQResponse<AmendmentValue> amendmentDateidentifiedStandardized(@ActedUpon("dwc:dateIdentified") String dateIdentified) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:dateIdentified was 
        // not present or is EMPTY; AMENDED if the value of dwc:dateIdentified 
        // was altered to unambiguously conform with the ISO 8601-1:2019 date format; 
        // otherwise NOT_CHANGED 
        
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

					if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
						result.setResultState(ResultState.NO_CHANGE); 
						// result.setResultState(ResultState.AMBIGUOUS); // Excluded to conform with AMENDMENT_EVENTDATE_STANDARDIZED
						result.addComment("Potential interpretation of dwc:dateIdentified [" + dateIdentified + "] as ["+ extractResponse.getResult() +"], but such interpretation is ambiguous." );
						result.addComment(extractResponse.getComment());
					} else {
						if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
							result.setResultState(ResultState.NO_CHANGE); 
							result.addComment("Potential interpretation of dwc:dateIdentified [" + dateIdentified + "] as ["+ extractResponse.getResult() +"] is suspect.");
							result.addComment(extractResponse.getComment());
						} else { 
							result.addComment("Unabmiguous interpretation of dwc:dateIdentified [" + dateIdentified + "] as ["+ extractResponse.getResult() +"].");
							result.setResultState(ResultState.CHANGED);
							result.setValue(new AmendmentValue(correctedValues));
						} 
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
     * #127 Amendment SingleRecord Conformance: day standardized
     *
     * Provides: AMENDMENT_DAY_STANDARDIZED
     *
     * @param day the provided dwc:day to evaluate
     * @return DQResponse<AmendmentValue>
     */
    @Provides("b129fa4d-b25b-43f7-9645-5ed4d44b357b")
    public DQResponse<AmendmentValue> amendmentDayStandardized(@ActedUpon("dwc:day") String day) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is not present 
        // or is EMPTY; AMENDED if the value of dwc:day was unambiguously 
        // interpreted to be an integer between 1 and 31 inclusive; 
        // otherwise NOT_CHANGED 

      	if (DateUtils.isEmpty(day)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("No value for dwc:day was provided.");
    	} else {
    		try {
    			@SuppressWarnings("unused")
				Integer dayNumeric = Integer.parseInt(day);
    			result.setResultState(ResultState.NO_CHANGE);
    			result.addComment("A value for dwc:day parsable as an integer was provided.");
    		} catch (NumberFormatException e) {
    			// Strip off any trailing non-numeric characters.
    			String dayTrimmed = day.replaceAll("[^0-9]+$", "");
    			// Try again
    			try {
    				Integer dayNumeric = Integer.parseInt(dayTrimmed);
    				if (dayNumeric>0 && dayNumeric<32) {
    					result.setResultState(ResultState.CHANGED);

						Map<String, String> values = new HashMap<>();
						values.put("dwc:day", dayNumeric.toString());
						result.setValue(new AmendmentValue(values));
    					result.addComment("Interpreted provided value for dwc:day ["+day+"] as ["+dayNumeric.toString()+"].");
    				} else {
    					result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    					result.addComment("Unable to parse a meaningfull value for day of month from dwc:day ["+day+"].");

    				}
    			} catch (NumberFormatException ex) {
    				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("Unable to interpret value provided for dwc:day ["+ day + "] as an integer between 1 and 31.");
    			}
    		}

    	}

    	if (result.getValue() == null) {
    		result.setValue(new AmendmentValue(new HashMap<String, String>()));
		}        
        
        return result;
    }

    /**
     * #52 Amendment SingleRecord Completeness: event from eventdate
     *
     * Provides: AMENDMENT_EVENT_FROM_EVENTDATE
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse<AmendmentValue>
     */
    @Provides("710fe118-17e1-440f-b428-88ba3f547d6d")
    public DQResponse<AmendmentValue> amendmentEventFromEventdate(
    		@Consulted("dwc:eventDate") String eventDate, 
    		@ActedUpon("dwc:startDayOfYear") String startDayOfYear, 
    		@ActedUpon("dwc:year") String year, 
    		@ActedUpon("dwc:month") String month, 
    		@ActedUpon("dwc:day") String day, 
    		@ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUESITES_NOT_MET if the field dwc:eventDate 
        // is EMPTY or does not contain a valid ISO 8601-1:2019 date; 
        // AMENDED if one or more EMPTY terms of the dwc:Event class 
        // (dwc:year, dwc:month, dwc:day, dwc:startDayOfYear, dwc:endDayOfYear) 
        // have been filled in from a valid unambiguously interpretable 
        // value in dwc:eventDate; otherwise NOT_CHANGED
        // 

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
    			Interval interval = DateUtils.extractInterval(eventDate);
    			if (interval==null) {
    				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    				result.addComment("Provided value for dwc:eventDate ["+ eventDate +"] appears to be correctly formatted, but could not be interpreted as a valid date.");

    			} else {
    				Map<String, String> values = new HashMap<>();

    				if (DateUtils.isEmpty(day)) {
    					String newDay = Integer.toString(interval.getStart().getDayOfMonth());
						values.put("dwc:day", newDay );
    					result.setResultState(ResultState.FILLED_IN);
    					if (isRange) {
    						result.addComment("Added day ["+ newDay+"] from start day of range ["+eventDate+"].");
    					} else {
    						result.addComment("Added day ["+ newDay+"] from eventDate ["+eventDate+"].");
    					}
    				}
    				if (DateUtils.isEmpty(month)) {
    					String newMonth = Integer.toString(interval.getStart().getMonthOfYear());
    					values.put("dwc:month", newMonth );
    					result.setResultState(ResultState.FILLED_IN);
    					if (isRange) {
    						result.addComment("Added month ["+ newMonth +"] from start month of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added month ["+ newMonth +"] from eventDate ["+eventDate+"].");
    					}
    				}
    				if (DateUtils.isEmpty(month)) {
    					String newMonth = Integer.toString(interval.getStart().getMonthOfYear());
    					values.put("dwc:month", newMonth );
    					result.setResultState(ResultState.FILLED_IN);
    					if (isRange) {
    						result.addComment("Added month ["+ newMonth +"] from start month of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added month ["+ newMonth +"] from eventDate ["+eventDate+"].");
    					}
    				}
    				if (DateUtils.isEmpty(year)) {
    					String newYear = Integer.toString(interval.getStart().getYear());
    					values.put("dwc:year", newYear );
    					result.setResultState(ResultState.FILLED_IN);
    					if (isRange) {
    						result.addComment("Added year ["+ newYear +"] from start month of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added year ["+ newYear +"] from eventDate ["+eventDate+"].");
    					}
    				}

    				if (DateUtils.isEmpty(startDayOfYear)) {
    					String newDay = Integer.toString(interval.getStart().getDayOfYear());
    					values.put("dwc:startDayOfYear", newDay );
    					result.setResultState(ResultState.FILLED_IN);
    					if (isRange) {
    						result.addComment("Added startDayOfYear ["+ newDay +"] from start day of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added startDayOfYear ["+ newDay +"] from eventDate ["+eventDate+"].");
    					}
    				}

    				if (DateUtils.isEmpty(endDayOfYear)) {
    					String newDay = Integer.toString(interval.getEnd().getDayOfYear());
    					values.put("dwc:endDayOfYear", newDay );
    					result.setResultState(ResultState.FILLED_IN);
    					if (isRange) {
    						result.addComment("Added endDayOfYear ["+ newDay +"] from end day of eventDate ["+eventDate+"].");
    					} else {
    						result.addComment("Added endDayOfYear ["+ newDay +"] from eventDate ["+eventDate+"].");
    					}
    				}

    				result.setValue(new AmendmentValue(values));
    				// Time could also be populated, but specification does not include it.  Here is a minimal implementation,
    				// which illustrates some issues in implementation (using zulu time or not, dealing with time in ranges...)
    				//if (DateUtils.isEmpty(eventTime)) {
    				//	if (DateUtils.containsTime(eventDate)) {
    				//		String newTime = DateUtils.extractZuluTime(eventDate);
    				//		result.addResult("dwc:endDayOfYear", newTime );
    				//      result.setResultState(ResultState.FILLED_IN);
    				//	    result.addComment("Added eventTime ["+ newTime +"] from eventDate ["+eventDate+"].");
    				//	}
    				//}
    				if (!result.getResultState().equals(ResultState.FILLED_IN)) {
    					result.setResultState(ResultState.NO_CHANGE);
    					result.addComment("No changes proposed, all candidate fields to fill in contain values.");
    				}
    			} // end interval extraction
    		} // end format validity check
    	}        
        
        return result;
    }

    /**
     * #86 Amendment SingleRecord Completeness: eventdate from verbatim
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_VERBATIM
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param verbatimEventDate the provided dwc:verbatimEventDate to evaluate
     * @return DQResponse<AmendmentValue>
     */
    @Provides("6d0a0c10-5e4a-4759-b448-88932f399812")
    public DQResponse<AmendmentValue> amendmentEventdateFromVerbatim(@ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:verbatimEventDate") String verbatimEventDate) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:eventDate 
        // is not EMPTY or the field dwc:verbatimEventDate is EMPTY 
        // or not unambiguously interpretable as an ISO 8601-1:2019 
        // date; AMENDED if the value of dwc:eventDate was unambiguously 
        // interpreted from dwc:verbatimEventDate; otherwise NOT_CHANGED 
        //

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
					Map<String, String> extractedValues = new HashMap<>();
					extractedValues.put("dwc:eventDate", extractResponse.getResult());

					if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
						result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
						result.addComment("Able to propose a value for dwc:eventDate [" + extractResponse.getResult() + "] but the verbatimEventDate is ambiguous.");
						result.addComment(extractResponse.getComment());
					} else {
						result.addComment("Proposed eventDate value of [" + extractResponse.getResult() + "] from verbatimEventDate [" + verbatimEventDate + "] is suspect.");
						if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
							result.addComment("Interpretation of verbatimEventDate [" + verbatimEventDate + "] is suspect.");
							result.addComment(extractResponse.getComment());
						}
						result.setResultState(ResultState.CHANGED);
						result.setValue(new AmendmentValue(extractedValues));
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

		if (result.getValue() == null) {
			result.setValue(new AmendmentValue(new HashMap<String, String>()));
		}        
        
        return result;
    }

    /**
     * #93 Amendment SingleRecord Completeness: eventdate from yearmonthday
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @return DQResponse<AmendmentValue>
     */
    @Provides("3892f432-ddd0-4a0a-b713-f2e2ecbd879d")
    public DQResponse<AmendmentValue> amendmentEventdateFromYearmonthday(
    		@ActedUpon("dwc:eventDate") String eventDate, 
    		@Consulted("dwc:year") String year, 
    		@Consulted("dwc:month") String month, 
    		@Consulted("dwc:day") String day) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL _PREREQUISITES_NOT_MET if the field dwc:eventDate 
        // is not EMPTY or dwc:year is EMPTY or is uninterpretable 
        // as a valid year; AMENDED if the value of dwc:eventDate was 
        // interpreted from the values in dwc:year, dwc:month and dwc:day; 
        // otherwise NOT_CHANGED 

        if (DateUtils.isEmpty(year)) {
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("No value for dwc:year was provided.");
        } else if (!DateUtils.isEmpty(eventDate)) {
        	result.setResultState(ResultState.NOT_RUN);
        	result.addComment("A value exists in dwc:eventDate, ammendment not attempted.");
        } else {
        	try {
        		// Try to parse integer out of year, throw exception on failure
        		@SuppressWarnings("unused")
        		Integer numericYear = Integer.parseInt(year);
        		
        		// Try to parse integer out of month, leave out on failure (specification is for valid year only)
        		try { 
        			if (!DateUtils.isEmpty(month)) {
        				@SuppressWarnings("unused")
        				Integer numericmonth = Integer.parseInt(month);
        			}
        		} catch (NumberFormatException e) {
        			result.addComment("dwc:month [" + month + "] is not a number.");
        			month = "";
        		}
        		// Try to parse integer out of month, leave out on failure (specification is for valid year only)
        		try { 
        			if (!DateUtils.isEmpty(day)) {
        				@SuppressWarnings("unused")
        				Integer numericDay = Integer.parseInt(day);
        			}
        		} catch (NumberFormatException e) {
        			result.addComment("dwc:day [" + day + "] is not a number.");
        			day = "";
        		}

        		
        		String resultDateString = DateUtils.createEventDateFromParts("", "", "", year, month, day);


        		if (DateUtils.isEmpty(resultDateString)) {
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("Unable to construct a valid ISO date from year ["+year+"], month ["+ month +"] and day ["+ day +"].");
        		} else if (!DateUtils.eventDateValid(resultDateString)) {
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("Failed to construct a valid ISO date from year ["+year+"], month ["+ month +"] and day ["+ day +"].");
        		} else {
        			result.setResultState(ResultState.FILLED_IN);

        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:eventDate", resultDateString);

        			result.setValue(new AmendmentValue(values));
        		}

        	} catch (NumberFormatException e) {
        		// catches number format exception on parse of year.
        		// Exceptions for month and day parsing are not critical and are caught and handled above. 
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("dwc:year [" + year + " is not a number.");
        	}
        }        
        
        return result;
    }

    /**
     * #132 Amendment SingleRecord Completeness: eventdate from yearstartdayofyearenddayofyear
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse<AmendmentValue>
     */
    @Provides("eb0a44fa-241c-4d64-98df-ad4aa837307b")
    public DQResponse<AmendmentValue> amendmentEventdateFromYearstartdayofyearenddayofyear(
    		@ActedUpon("dwc:eventDate") String eventDate, 
    		@Consulted("dwc:startDayOfYear") String startDayOfYear, 
    		@Consulted("dwc:year") String year, 
    		@Consulted("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:eventDate 
        // was not EMPTY or dwc:year was EMPTY or both dwc:startDayOfYear 
        // and dwc:endDayOfYear were EMPTY or not interpretable; AMENDED 
        // if the value of dwc:eventDate was FILLED_IN from the values 
        // in dwc:year, dwc:startDayOfYear and dwc:endDayOfYear; otherwise 
        // NOT_CHANGED 
        
        if (DateUtils.isEmpty(year) || DateUtils.isEmpty(startDayOfYear)) {
        	result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        	result.addComment("Either year or startDayOfYear was not provided.");
        } else if (!DateUtils.isEmpty(eventDate)) {
        	result.setResultState(ResultState.NOT_RUN);
        	result.addComment("A value exists in dwc:eventDate, ammendment not attempted.");
        } else {
        	try {
        		// Attempt to parse an integer from each of the three Consulted terms
        		// exception thrown if any one of them can't be parsed.
        		@SuppressWarnings("unused")
        		Integer numericYear = Integer.parseInt(year);
        		@SuppressWarnings("unused")
        		Integer numericStartDay = Integer.parseInt(startDayOfYear);
        		if (!DateUtils.isEmpty(endDayOfYear)) {
        			@SuppressWarnings("unused")
        			Integer numericEndDay = Integer.parseInt(endDayOfYear);
        		}

        		String resultDateString = DateUtils.createEventDateFromParts("", startDayOfYear, endDayOfYear, year, "", "");

        		if (DateUtils.isEmpty(resultDateString)) {
        			result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        			result.addComment("Unable to construct a valid ISO date from startDayOfYear [" + startDayOfYear + "], year ["+year+"], and endDayOfYear ["+ endDayOfYear +"].");
        		} else {
        			result.setResultState(ResultState.FILLED_IN);
        			Map<String, String> values = new HashMap<>();
        			values.put("dwc:eventDate", resultDateString);
        			result.setValue(new AmendmentValue(values));
        		}

        	} catch (NumberFormatException e) {
        		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
        		result.addComment("One of startDayOfYear [" + startDayOfYear + "], year ["+year+"], or endDayOfYear ["+ endDayOfYear +"] is not a number.");
        	}
        }        

        return result;
    }

    /**
     * #61 Amendment SingleRecord Conformance: eventdate standardized
     *
     * Provides: AMENDMENT_EVENTDATE_STANDARDIZED
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse<AmendmentValue>
     */
    @Provides("718dfc3c-cb52-4fca-b8e2-0e722f375da7")
    public DQResponse<AmendmentValue> amendmentEventdateStandardized(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUESITES_NOT_MET if the field dwc:eventDate 
        // is EMPTY; AMENDED if the field dwc:eventDate was changed 
        // to unambiguously conform with an ISO 8601-1:2019 date; otherwise 
        // NOT_CHANGED 
        
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
					Map<String, String> correctedValues = new HashMap<>();
					correctedValues.put("dwc:eventDate", extractResponse.getResult());

					if (extractResponse.getResultState().equals(EventResult.EventQCResultState.AMBIGUOUS)) {
						result.addComment("Interpretation of eventDate [" + eventDate + "] as ["+ extractResponse.getResult() +"] possible, but is ambiguous.");
						result.setResultState(ResultState.NO_CHANGE); 
						// result.setResultState(ResultState.AMBIGUOUS);   // now excluded by the specification.
						result.addComment(extractResponse.getComment());
					} else {
						if (extractResponse.getResultState().equals(EventResult.EventQCResultState.SUSPECT)) {
							result.addComment("Interpretation of eventDate [" + eventDate + "] as ["+ extractResponse.getResult() +"] possible, but suspect.");
							result.addComment(extractResponse.getComment());
							result.setResultState(ResultState.NO_CHANGE); 
						} else { 
							result.addComment("Unabmiguous Interpretation of eventDate [" + eventDate + "] as [" + extractResponse.getResult() + "].");
							result.setResultState(ResultState.CHANGED);
							result.setValue(new AmendmentValue(correctedValues));
						}
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
     * #128 Amendment SingleRecord Conformance: month standardized
     *
     * Provides: AMENDMENT_MONTH_STANDARDIZED
     *
     * @param month the provided dwc:month to evaluate
     * @return DQResponse<AmendmentValue>
     */
    @Provides("2e371d57-1eb3-4fe3-8a61-dff43ced50cf")
    public DQResponse<AmendmentValue> amendmentMonthStandardized(@ActedUpon("dwc:month") String month) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:month is not present 
        // or is EMPTY; AMENDED if the value of dwc:month was interpreted 
        // to be a integer between 1 and 12 inclusive; otherwise NOT_CHANGED 
        //

    	if (DateUtils.isEmpty(month)) {
    		result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("No value for dwc:month was provided.");
    	} else {
    		try {
    			Integer monthnumeric = Integer.parseInt(month);
    			if (monthnumeric < 1 || monthnumeric > 12) { 
    				result.setResultState(ResultState.NO_CHANGE);
    				result.addComment("A value for dwc:month parsable as an integer was provided, but is outside the range 1-12");
    			} else { 
    				result.setResultState(ResultState.NO_CHANGE);
    				result.addComment("A value for dwc:month parsable as an integer was provided.");
    			}
    		} catch (NumberFormatException e) {
    			// Convert roman numerals, some problematic forms of abbreviations,
    			// non-english capitalization variants, absence of exepected accented characters,
    			// etc to date library (e.g. Joda) recognized month names.
    			String monthConverted = DateUtils.cleanMonth(month);
    			// Strip any trailing period off of month name.
    			String monthTrim = monthConverted.replaceFirst("\\.$", "").trim();
    			if (DateUtils.isEmpty(monthTrim)) {
    				result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
    					result.setResultState(ResultState.CHANGED);

						Map<String, String> values = new HashMap<>();
						values.put("dwc:month", monthNumeric.toString());

						result.setValue(new AmendmentValue(values));
    					result.addComment("Interpreted provided value for dwc:month ["+month+"] as ["+monthNumeric.toString()+"].");
    				} else {
    					result.setResultState(ResultState.INTERNAL_PREREQUISITES_NOT_MET);
    					result.addComment("Unable to parse a meaningfull value for month from dwc:month ["+month+"].");
    				}
    			}
    		}

    	}        
        
        return result;
    }

}
