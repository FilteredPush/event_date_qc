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
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Consulted;
import org.datakurator.ffdq.annotations.Enhancement;
import org.datakurator.ffdq.annotations.PostEnhancement;
import org.datakurator.ffdq.annotations.PreEnhancement;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.api.EnumDQResultState;
import org.datakurator.ffdq.api.EnumDQValidationResult;
import org.datakurator.ffdq.api.EnumDQAmendmentResultState;


/**
 * Darwin Core Event eventDate related Data Quality Measures, Validations, and Enhancements. 
 * 
 *  Provides support for the following draft TDWG DQIG TG2 validations and amendments.  
 *  
 *  DAY_MONTH_TRANSPOSED  dayMonthTransposition(@ActedUpon(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) 
 *  DAY_MONTH_YEAR_FILLED_IN
 *  EVENTDATE_FILLED_IN_FROM_VERBATIM  
 *  START_ENDDAYOFYEAR_FILLED_IN
 *  
 *  EVENT_DATE_DURATION_SECONDS  
 *  DAY_IS_FIRST_OF_CENTURY
 *  DAY_IS_FIRST_OF_YEAR
 *  
 *  DAY_IN_RANGE  isDayInRange(@ActedUpon(value = "dwc:day") String day)   
 *  MONTH_IN_RANGE  isMonthInRange(@ActedUpon(value = "dwc:month") String month) 
 *  DAY_POSSIBLE_FOR_MONTH_YEAR  isDayPossibleForMonthYear(@Consulted(value="dwc:year") String year, @Consulted(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) 
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
    @Provides(value = "DAY_IN_RANGE")
    @PreEnhancement
    @PostEnhancement
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
    			result.setResultState(EnumDQResultState.COMPLETED);
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
    @Provides(value = "MONTH_IN_RANGE")
    @PreEnhancement
    @PostEnhancement
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
    			result.setResultState(EnumDQResultState.COMPLETED);
    		} catch (NumberFormatException e) { 
    			logger.debug(e.getMessage());
    			result.setResultState(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET);
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
     * @return an DQValidationResponse object describing whether day exists in year-month-day.
     */
    @Provides(value = "DAY_POSSIBLE_FOR_MONTH_YEAR")
    @PreEnhancement
    @PostEnhancement
    public static EventDQValidation isDayPossibleForMonthYear(@Consulted(value="dwc:year") String year, @Consulted(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) { 
    	EventDQValidation result = new EventDQValidation();
    	
    	EventDQValidation monthResult =  isMonthInRange(month);
    	EventDQValidation dayResult =  isDayInRange(day);
    	
    	if (monthResult.getResultState().equals(EnumDQResultState.COMPLETED)) {
    		if (monthResult.getResult().equals(EnumDQValidationResult.COMPLIANT)) { 
    	        if (dayResult.getResultState().equals(EnumDQResultState.COMPLETED)) { 
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
    	        		    result.setResultState(EnumDQResultState.COMPLETED);
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
    @Enhancement
    public static final EventDQAmedment dayMonthTransposition(@ActedUpon(value="dwc:month") String month, @ActedUpon(value="dwc:day") String day) { 
    	EventDQAmedment result = new EventDQAmedment();
    	if (DateUtils.isEmpty(day) || DateUtils.isEmpty(month)) { 
    		result.setResultState(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET);
    		result.addComment("Either month or day was not provided.");
    	} else { 
        	EventDQValidation monthResult =  isMonthInRange(month);
        	EventDQValidation dayResult =  isDayInRange(day);
        	if (monthResult.getResultState().equals(EnumDQResultState.COMPLETED)) {
        		if (monthResult.getResult().equals(EnumDQValidationResult.NOT_COMPLIANT)) { 
        			// month is integer, but out of range
        	        if (dayResult.getResultState().equals(EnumDQResultState.COMPLETED)) { 
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
        	        if (dayResult.getResultState().equals(EnumDQResultState.COMPLETED) &&
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