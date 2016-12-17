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
import org.filteredpush.qc.date.EventDQAmedment.EventQCAmendmentState;
import org.filteredpush.qc.date.EventDQValidation.EventDQValidationResult;
import org.filteredpush.qc.date.EventDQValidation.EventDQValidationState;


/**
 * Darwin Core Event eventDate related Data Quality Measures, Validations, and Enhancements. 
 * 
 *  Provides support for the following draft TDWG DQIG TG2 validations and amendments.  
 *  
 *  DAY_MONTH_TRANSPOSED 
 *  DAY_MONTH_YEAR_FILLED_IN
 *  EVENTDATE_FILLED_IN_FROM_VERBATIM  
 *  START_ENDDAYOFYEAR_FILLED_IN
 *  
 *  EVENT_DATE_DURATION_SECONDS  
 *  DAY_IS_FIRST_OF_CENTURY
 *  DAY_IS_FIRST_OF_YEAR
 *  
 *  DAY_IN_RANGE  
 *  MONTH_IN_RANGE 
 *  DAY_POSSIBLE_FOR_MONTH_YEAR  
 *  EVENTDATE_CONSISTENT_WITH_DAY_MONTH_YEAR  
 *  EVENTDATE_IN_PAST
 *  EVENTDATE_PRECISON_MONTH_OR_BETTER 
 *  EVENTDATE_PRECISON_YEAR_OR_BETTER 
 *  STARTDATE_CONSISTENT_WITH_ENDDATE
 *  YEAR_PROVIDED
 *  EVENTDATE_CONSISTENT_WITH_ATOMIC_PARTS 
 * 
 * @author mole
 *
 */
public class DwCEventDQ {
	
	private static final Log logger = LogFactory.getLog(DwCEventDQ.class);

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
    public static EventDQValidation isDayInRange(String day) { 
    	EventDQValidation result = new EventDQValidation();
    	if (DateUtils.isEmpty(day)) {
    		result.addComment("No value provided for day.");
    		result.setResultState(EventDQValidationState.INTERNAL_PREREQISITES_NOT_MET);
    	} else { 
    		try { 
    			int numericDay = Integer.parseInt(day.trim());
    			if (DateUtils.isDayInRange(numericDay)) { 
    				result.setResult(EventDQValidationResult.COMPLIANT);
    				result.addComment("Provided value for day '" + day + "' is an integer in the range 1 to 31.");
    			} else { 
    				result.setResult(EventDQValidationResult.NOT_COMPLIANT);
    				result.addComment("Provided value for day '" + day + "' is not an integer in the range 1 to 31.");
    			}
    			result.setResultState(EventDQValidationState.COMPLETED);
    		} catch (NumberFormatException e) { 
    			logger.debug(e.getMessage());
    			result.setResultState(EventDQValidationState.INTERNAL_PREREQISITES_NOT_MET);
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
    public static EventDQValidation isMonthInRange(String month) { 
    	EventDQValidation result = new EventDQValidation();
    	if (DateUtils.isEmpty(month)) {
    		result.addComment("No value provided for month.");
    		result.setResultState(EventDQValidationState.INTERNAL_PREREQISITES_NOT_MET);
    	} else { 
    		try { 
    			int numericMonth = Integer.parseInt(month.trim());
    			if (DateUtils.isMonthInRange(numericMonth)) { 
    				result.setResult(EventDQValidationResult.COMPLIANT);
    				result.addComment("Provided value for month '" + month + "' is an integer in the range 1 to 12.");
    			} else { 
    				result.setResult(EventDQValidationResult.NOT_COMPLIANT);
    				result.addComment("Provided value for month '" + month + "' is not an integer in the range 1 to 12.");
    			}
    			result.setResultState(EventDQValidationState.COMPLETED);
    		} catch (NumberFormatException e) { 
    			logger.debug(e.getMessage());
    			result.setResultState(EventDQValidationState.INTERNAL_PREREQISITES_NOT_MET);
    			result.addComment(e.getMessage());
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
     * @return an EventDQValidation object describing whether day exists in year-month-day.
     */
    public static EventDQValidation isDayPossibleForMonthYear(String year, String month, String day) { 
    	EventDQValidation result = new EventDQValidation();
    	
    	EventDQValidation monthResult =  isMonthInRange(month);
    	EventDQValidation dayResult =  isDayInRange(day);
    	
    	if (monthResult.getResultState().equals(EventDQValidationState.COMPLETED)) {
    		if (monthResult.getResult().equals(EventDQValidationResult.COMPLIANT)) { 
    	        if (dayResult.getResultState().equals(EventDQValidationState.COMPLETED)) { 
    	        	if (dayResult.getResult().equals(EventDQValidationResult.COMPLIANT)) {
    	        		try { 
    	        		    Integer numericYear = Integer.parseInt(year);
    	        		    String date = String.format("%04d", numericYear) + "-" + month.trim() + "-" + day.trim();

    	        	    	if (DateUtils.eventDateValid(date)) { 
    	        	    		result.setResult(EventDQValidationResult.COMPLIANT);
    	        	    		result.addComment("Provided value for year-month-day " + date + " parses to a valid day.");;
    	        	    	} else { 
    	        	    		result.setResult(EventDQValidationResult.NOT_COMPLIANT);
    	        	    		result.addComment("Provided value for year-month-day " + date + " does not parse to a valid day.");;
    	        	    	}
    	        		    result.setResultState(EventDQValidationState.COMPLETED);
    	        		} catch (NumberFormatException e) { 
    	        			result.setResultState(EventDQValidationState.INTERNAL_PREREQISITES_NOT_MET);
    	        		    result.addComment("Unable to parse integer from provided value for year " + year + " " + e.getMessage());;
    	        		}
    	        	} else { 
    	        		result.setResultState(EventDQValidationState.INTERNAL_PREREQISITES_NOT_MET);
    	        		result.addComment("Provided value for day " + day + " is outside the range 1-31.");;
    	        	}
    	        } else { 
    	        	result.setResultState(dayResult.getResultState());
    	        	result.addComment(dayResult.getComment());
    	        }
    		} else { 
    			result.setResultState(EventDQValidationState.INTERNAL_PREREQISITES_NOT_MET);
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
    public static final EventDQAmedment dayMonthTransposition(String month, String day) { 
    	EventDQAmedment result = new EventDQAmedment();
    	if (DateUtils.isEmpty(day) || DateUtils.isEmpty(month)) { 
    		result.setResultState(EventQCAmendmentState.INTERNAL_PREREQISITES_NOT_MET);
    		result.addComment("Either month or day was not provided.");
    	} else { 
        	EventDQValidation monthResult =  isMonthInRange(month);
        	EventDQValidation dayResult =  isDayInRange(day);
        	if (monthResult.getResultState().equals(EventDQValidationState.COMPLETED)) {
        		if (monthResult.getResult().equals(EventDQValidationResult.NOT_COMPLIANT)) { 
        			// month is integer, but out of range
        	        if (dayResult.getResultState().equals(EventDQValidationState.COMPLETED)) { 
        	        	// day is also integer
        	        	int dayNumeric = Integer.parseInt(day);
        	        	int monthNumeric = Integer.parseInt(month);
        	        	if (DateUtils.isDayInRange(monthNumeric) && DateUtils.isMonthInRange(dayNumeric)) { 
        	        		// day is in range for months, and month is in range for days, so transpose.
        	        	    result.addResult("dwc:month", day);
        	        	    result.addResult("dwc:day", month);
        	        	    result.setResultState(EventQCAmendmentState.TRANSPOSED);
        	        	} else { 
        	        	    result.setResultState(EventQCAmendmentState.INTERNAL_PREREQISITES_NOT_MET);
        	        	}
        	        } else { 
    		            result.setResultState(EventQCAmendmentState.INTERNAL_PREREQISITES_NOT_MET);
    		            result.addComment("dwc:day " + dayResult.getResultState() + ". " + dayResult.getComment());
        	        }
        		} else { 
        	        if (dayResult.getResultState().equals(EventDQValidationState.COMPLETED) &&
        	            dayResult.getResult().equals(EventDQValidationResult.COMPLIANT)) { 
        			    // month is in range for months, so don't try to change.
        	            result.setResultState(EventQCAmendmentState.NO_CHANGE);
        	        } else { 
    		            result.setResultState(EventQCAmendmentState.INTERNAL_PREREQISITES_NOT_MET);
        	        }
        		}
        	} else {
    		   result.setResultState(EventQCAmendmentState.INTERNAL_PREREQISITES_NOT_MET);
    		   result.addComment("dwc:month " + monthResult.getResultState() + ". " + monthResult.getComment());
        	}
    	}
    	return result;
    }    
    
}
