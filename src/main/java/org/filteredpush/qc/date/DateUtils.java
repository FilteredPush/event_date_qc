/** DateUtils.java
 * 
 * Copyright 2015 President and Fellows of Harvard College
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.EnumDQResultState;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Instant;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Utility functions for working with DarwinCore date concepts.
 *  
 *  Includes:
 *  
 *  dayMonthTransposition(String month, String day) 
 *  extractDateFromVerbatim(String verbatimEventDate)
 *  measureDurationSeconds(String eventDate)
 *  isDayInRange(String day) 
 *  isMonthInRange(String month) 
 *  isDayPossibleForMonthYear(String year, String month, String day) 
 *  isConsistent(String eventDate, String year, String month, String day) 
 *  specificToMonthScale(String eventDate)
 *  specificToYearScale(String eventDate)
 *  isConsistent(String eventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day)
 *  
 *  extractZuluTime(String eventDate) 
 *  createEventDateFromParts(String verbatimEventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day)
 *  
 *  containsTime(String eventDate)
 *  eventDateValid(String eventDate)
 *  
 *  Also for helping map non-Darwin Core concepts on to Darwin Core:
 *  createEventDateFromStartEnd(String startDate, String endDate)
 * 
 * @author mole
 *
 */
public class DateUtils {

	private static final Log logger = LogFactory.getLog(DateUtils.class);
	
	/**
	 * Verbatim dates that parse to years prior to this year are considered suspect
	 * by default.
	 * 
	 */
	public static final int YEAR_BEFORE_SUSPECT = 1000;
	
	
	/**
	 * Test to see whether an eventDate contains a string in an expected ISO format.
	 * 
	 * @param eventDate string to test for expected format.
	 * @return true if eventDate is in an expected format for eventDate, otherwise false.
	 */
    public static boolean eventDateValid(String eventDate) {
    	boolean result = false; 
    	if (extractDate(eventDate)!=null) { 
    		result = true;
    	} else { 
    		Interval interval = extractInterval(eventDate);
    		if (interval!=null) { 
    			if (interval.getStart().isBefore(interval.getEnd())) { 
    			   result = true;
    			}
    		}
    	}
    	return result;
    }
	
	
	/**
	 * Attempt to construct an ISO formatted date as a string built from atomic parts of the date.
	 * 
	 * @param verbatimEventDate a string containing the verbatim event date 
	 * @param startDayOfYear a string containing the start day of a year, expected to parse to an integer.
	 * @param endDayOfYear a string containing the end day of a year, expected to parse to an integer.
	 * @param year a string containing the year, expected to parse to an integer.
	 * @param month a string containing the month, expected to parse to an integer.
	 * @param day a string containing the start day, expected to parse to an integer.
	 * 
	 * @return null, or a string in the form of an ISO date consistent with the input fields.
	 * 	 
	 */
	public static String createEventDateFromParts(String verbatimEventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day) {
		String result = null;

		if (verbatimEventDate!=null && verbatimEventDate.trim().length()>0) { 
			Map<String,String> verbatim = extractDateToDayFromVerbatim(verbatimEventDate, DateUtils.YEAR_BEFORE_SUSPECT); 
			if (verbatim.size()>0) { 
				if (verbatim.get("resultState")!=null && verbatim.get("resultState").equals("date")) { 
					result = verbatim.get("result");
				}
				if (verbatim.get("resultState")!=null && verbatim.get("resultState").equals("ambiguous")) { 
					result = verbatim.get("result");
				}		
				if (verbatim.get("resultState")!=null && verbatim.get("resultState").equals("range")) { 
					result = verbatim.get("result");
				}					
			}
		}
		if (year!=null && year.matches("[0-9]{4}") && isEmpty(month) && isEmpty(day) && isEmpty(startDayOfYear)) { 
		    result = year;
		}		
		if (year!=null && year.matches("[0-9]{4}") && 
				(month==null || month.trim().length()==0) && 
				( day==null || day.trim().length()==0 ) && 
				startDayOfYear !=null && startDayOfYear.trim().length() > 0
				) {
			try { 
				StringBuffer assembly = new StringBuffer();
				if (!isEmpty(endDayOfYear) && !startDayOfYear.trim().equals(endDayOfYear.trim())) {  
					assembly.append(year).append("-").append(String.format("%03d",Integer.parseInt(startDayOfYear))).append("/");
					assembly.append(year).append("-").append(String.format("%03d",Integer.parseInt(endDayOfYear)));
				} else { 
					assembly.append(year).append("-").append(String.format("%03d",Integer.parseInt(startDayOfYear)));
				}
			    Map<String,String> verbatim = extractDateToDayFromVerbatim(assembly.toString(), DateUtils.YEAR_BEFORE_SUSPECT) ;
			    logger.debug(verbatim.get("resultState"));
			    logger.debug(verbatim.get("result"));
				if (verbatim.get("resultState")!=null && (verbatim.get("resultState").equals("date") || verbatim.get("resultState").equals("range"))) { 
					result = verbatim.get("result");
				}
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}		
		if (    (verbatimEventDate!=null && verbatimEventDate.matches("^[0-9]{4}$")) &&
				(year==null || year.trim().length()==0) && 
				(month==null || month.trim().length()==0) && 
				( day==null || day.trim().length()==0 ) && 
				startDayOfYear !=null && startDayOfYear.trim().length() > 0
				) {
			try { 
				StringBuffer assembly = new StringBuffer();
				if (endDayOfYear !=null && endDayOfYear.trim().length() > 0 && !startDayOfYear.trim().equals(endDayOfYear.trim())) {  
					assembly.append(verbatimEventDate).append("-").append(String.format("%03d",Integer.parseInt(startDayOfYear))).append("/");
					assembly.append(verbatimEventDate).append("-").append(String.format("%03d",Integer.parseInt(endDayOfYear)));
				} else { 
					assembly.append(verbatimEventDate).append("-").append(String.format("%03d",Integer.parseInt(startDayOfYear)));
				}
			    Map<String,String> verbatim = extractDateToDayFromVerbatim(assembly.toString(), DateUtils.YEAR_BEFORE_SUSPECT) ;
			    logger.debug(verbatim.get("resultState"));
			    logger.debug(verbatim.get("result"));
				if (verbatim.get("resultState")!=null && (verbatim.get("resultState").equals("date") || verbatim.get("resultState").equals("range"))) { 
					result = verbatim.get("result");
				}
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
		}			
		if (year!=null && year.matches("[0-9]{4}") && month!=null && month.matches("[0-9]{1,2}") &&( day==null || day.trim().length()==0 )) {  
		    result = String.format("%o4d",Integer.parseInt(year)) + "-" + String.format("%02d",Integer.parseInt(month));
		}
		if (year!=null && year.matches("[0-9]{4}") && month!=null && month.matches("[0-9]{1,2}") && day!=null && day.matches("[0-9]{1,2}")) {  
		    result = String.format("%04d",Integer.parseInt(year)) + "-" + 
                     String.format("%02d",Integer.parseInt(month)) + "-" + 
                     String.format("%02d",Integer.parseInt(day));
		}
		return result;
	}
	
	/**
	 * Attempt to extract a date or date range in standard format from a provided verbatim 
	 * date string.  
	 * 
	 * Provides: EVENTDATE_FILLED_IN_FROM_VERBATIM 
	 * 
	 * @param verbatimEventDate a string containing a verbatim event date.
	 * @return a result object with a resultState and the extracted value in result.
	 * 
	 */
	public static EventResult extractDateFromVerbatimER(String verbatimEventDate) {
		return extractDateFromVerbatimER(verbatimEventDate, DateUtils.YEAR_BEFORE_SUSPECT, null);
	}	
	
	/**
	 * Attempt to extract a date or date range in standard format from a provided verbatim 
	 * date string.  
	 * 
	 * Provides: EVENTDATE_FILLED_IN_FROM_VERBATIM 
	 * 
	 * @param verbatimEventDate a string containing a verbatim event date.
	 * @return a map with result and resultState as keys
	 * 
	 * @deprecated
	 * @see #extractDateFromVerbatimER(String) replacement method.
	 */
	public static Map<String,String> extractDateFromVerbatim(String verbatimEventDate) {
		return extractDateFromVerbatim(verbatimEventDate, DateUtils.YEAR_BEFORE_SUSPECT);
	}
	
	/**
	 * Extract a date from a verbatim date, returning ranges specified to day.
	 * 
	 * @param verbatimEventDate a string containing a verbatim event date.
	 * @param yearsBeforeSuspect the value for a year before which parsed years should be considered suspect.
	 * @return an EventResult object containing the extracted date. 
	 */
	public static EventResult extractDateToDayFromVerbatimER(String verbatimEventDate, int yearsBeforeSuspect) {
		EventResult result =  extractDateFromVerbatimER(verbatimEventDate, yearsBeforeSuspect, null);
        logger.debug(result.getResultState());
        logger.debug(result.getResult());
		if (result!=null && result.getResultState().equals(EventResult.EventQCResultState.RANGE)) {
			String dateRange = result.getResult();
			try { 
				   Interval parseDate = extractDateInterval(dateRange);
				   logger.debug(parseDate);
				   String resultDate =  parseDate.getStart().toString("yyyy-MM-dd") + "/" + parseDate.getEnd().toString("yyyy-MM-dd");
				   result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
				result.setResultState(EventResult.EventQCResultState.INTERNAL_PREREQISITES_NOT_MET);
				result.addComment(e.getMessage());
			}
		}
		return result;
	}	
	
	/**
	 * Extract a date from a verbatim date, returning ranges specified to day.
	 * 
	 * @param verbatimEventDate a string containing a verbatim event date.
	 * @param yearsBeforeSuspect the value for a year before which parsed years should be considered suspect.
	 * @return  a map with result and resultState as keys
	 * 
	 * @deprecated
	 * @see #extractDateToDayFromVerbatimER(String, int) replacement method
	 */
	public static Map<String,String> extractDateToDayFromVerbatim(String verbatimEventDate, int yearsBeforeSuspect) {
		Map<String,String> result =  extractDateFromVerbatim(verbatimEventDate, yearsBeforeSuspect);
		if (result.size()>0 && result.get("resultState").equals("range")) {
			String dateRange = result.get("result");
			try { 
				   Interval parseDate = extractDateInterval(dateRange);
				   logger.debug(parseDate);
				   String resultDate =  parseDate.getStart().toString("yyyy-MM-dd") + "/" + parseDate.getEnd().toString("yyyy-MM-dd");
				   result.put("result",resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}
		return result;
	}
	
	/**
	 * Given a string that may represent a date or range of dates, or date time or range of date times,
	 * attempt to extract a standard date from that string.
	 * 
	 * @param verbatimEventDate a string containing a verbatim event date.
	 * @param yearsBeforeSuspect  Dates that parse to a year prior to this year are marked as suspect.
	 * 
	 * @return a map with keys resultState for the nature of the match and result for the resulting date. 
	 */
	public static Map<String,String> extractDateFromVerbatim(String verbatimEventDate, int yearsBeforeSuspect) {		
		Map result = new HashMap<String,String>();
	    EventResult eresult = extractDateFromVerbatimER(verbatimEventDate, yearsBeforeSuspect, null);
	    if (eresult!=null) { 
	    	if (!eresult.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) { 
	            result.put("resultState", eresult.getResultState().toString().toLowerCase());
	            result.put("result", eresult.getResult());
	    	}
	    }
	    return result;
	}
	
	/**
	 * Given a string that may represent a date or range of dates, or date time or range of date times,
	 * attempt to extract a standard date from that string.
	 * 
	 * @param verbatimEventDate a string containing a verbatim event date.
	 * @param yearsBeforeSuspect  Dates that parse to a year prior to this year are marked as suspect.
	 * @param assumemmddyyyy if true, assume that dates in the form nn-nn-nnnn are mm-dd-yyyy, if false, assume 
	 *       that these are dd-mm-yyyy, if null, such dates are tested for ambiguity.  
	 * 
	 * @return an EventResult with a resultState for the nature of the match and result for the resulting date. 
	 */
	public static EventResult extractDateFromVerbatimER(String verbatimEventDate, int yearsBeforeSuspect, Boolean assumemmddyyyy) {
		EventResult result = new EventResult();
		String resultDate = null;
		
		// Remove some common no data comments
		if (verbatimEventDate!=null && verbatimEventDate.contains("[no date]")) { 
			verbatimEventDate = verbatimEventDate.replace("[no date]", "");
		}
		if (verbatimEventDate!=null && verbatimEventDate.contains("[no year]")) { 
			verbatimEventDate = verbatimEventDate.replace("[no year]", "");
		}		
		
		// Strip off leading and trailing []
		if (verbatimEventDate!=null && verbatimEventDate.startsWith("[")) { 
			verbatimEventDate = verbatimEventDate.substring(1);
		}
		if (verbatimEventDate!=null && verbatimEventDate.endsWith("]")) { 
			verbatimEventDate = verbatimEventDate.substring(0,verbatimEventDate.length()-1);
		}
		
		// strip off leading and trailing whitespace
		if (verbatimEventDate!=null && (verbatimEventDate.startsWith(" ") || verbatimEventDate.endsWith(" "))) { 
			verbatimEventDate = verbatimEventDate.trim();
		}
		
		
		// Stop before doing work if provided verbatim string is null.
		if (isEmpty(verbatimEventDate)) { 
			return result;
		}
		
		if (verbatimEventDate.matches("^[0-9]{1,2}[-. ][0-9]{1,2}[-. ][0-9]{4}/[0-9]{1,2}[-. ][0-9]{1,2}[-. ][0-9]{4}$")) {
			// if verbatim date is a range with identical first and last dates (/), use just one.
			// Example: 12-11-1982/12-11-1982  changed to 12-11-1982
			String[] bits = verbatimEventDate.split("/");
			if (bits.length==2 && bits[0].equals(bits[1])) { 
				verbatimEventDate = bits[0];
			}
		}
		if (verbatimEventDate.matches("^[0-9]{1,2}[./ ][0-9]{1,2}[./ ][0-9]{4}[-][0-9]{1,2}[./ ][0-9]{1,2}[./ ][0-9]{4}$")) {
			// if verbatim date is a range with identical first and last dates (-), use just one.
			// Example: 12/11/1982-12/11/1982  changed to 12/11/1982
			String[] bits = verbatimEventDate.split("-");
			if (bits.length==2 && bits[0].equals(bits[1])) { 
				verbatimEventDate = bits[0];
			}
		}
		if (verbatimEventDate.matches("^[0-9]{4}[-/]([0-9]{1,2}|[A-Za-z]+)[-/][0-9]{1,2}.*")) {
			// Example 1982/02/05
			// Example 1982/Feb/05
			// Example 1982-02-05
			// Example 1982-02-05T05:03:06
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("yyyy/MM/dd").getParser(),
						DateTimeFormat.forPattern("yyyy/MMM/dd").getParser(),
						DateTimeFormat.forPattern("yyyy-MMM-dd").getParser(),
						ISODateTimeFormat.dateOptionalTimeParser().getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM-dd");
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}
		if (verbatimEventDate.matches("^[0-9]{1,2}[-/ ][0-9]{4}")) { 
			// Example 02/1982
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("MM-yyyy").getParser(),
						DateTimeFormat.forPattern("MM/yyyy").getParser(),
						DateTimeFormat.forPattern("MM yyyy").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM");
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}		
		if (verbatimEventDate.matches("^[0-9]{4}年[0-9]{1,2}月[0-9]{1,2}[日号]$")) { 
			// Example: 1972年03月25日
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("yyyy年MM月dd日").getParser(),
						DateTimeFormat.forPattern("yyyy年MM月dd号").getParser(),
						ISODateTimeFormat.dateOptionalTimeParser().getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter().withLocale(Locale.CHINESE);
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM-dd");
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}		
		if (verbatimEventDate.matches("^[0-9]{4}[-][0-9]{3}/[0-9]{4}[-][0-9]{3}$")) { 
			// Example: 1982-145
			try { 
				String[] bits = verbatimEventDate.split("/");
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("yyyy-D").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				LocalDate parseStartDate = LocalDate.parse(bits[0],formatter);
				LocalDate parseEndDate = LocalDate.parse(bits[1],formatter);
				resultDate =  parseStartDate.toString("yyyy-MM-dd") + "/" + parseEndDate.toString("yyyy-MM-dd");
				logger.debug(resultDate);
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
				verbatimEventDate.matches("^[0-9]{4}0000$")) { 
			// case 19800000
			verbatimEventDate = verbatimEventDate.substring(0, 4);
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
				verbatimEventDate.matches("^[0-9]{4}$")) { 
			// Example: 1962 
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("yyyy").getParser(),
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				resultDate = parseDate.toString("yyyy");
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
				verbatimEventDate.matches("^[12][0-9]{2}0s$")) {
			// Example: 1970s 
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("yyyy's").getParser(),
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
				DateMidnight endDate = parseDate.plusYears(10).minusDays(1);
				resultDate = parseDate.toString("yyyy") + "-01-01/" + endDate.toString("yyyy") + "-12-31";
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[A-Za-z]{3,9}[.]{0,1}[-/ ][0-9]{4}$")) { 
			// Example: Jan-1980
			// Example: Jan./1980
			// Example: January 1980
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("MMM-yyyy").getParser(),
						DateTimeFormat.forPattern("MMM/yyyy").getParser(),
						DateTimeFormat.forPattern("MMM yyyy").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				String cleaned = verbatimEventDate.replace(".", "");
				DateMidnight parseDate = LocalDate.parse(cleaned,formatter).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM");
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			// Example: 04/03/1994  (ambiguous)
			// Example: 04/20/1994
			// Example: 20/04/1994
			String resultDateMD = null;
			String resultDateDM = null;
			DateMidnight parseDate1 = null;
			DateMidnight parseDate2 = null;
			if (assumemmddyyyy==null || assumemmddyyyy) { 
				try { 
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("MM/dd/yyyy").getParser(),
							DateTimeFormat.forPattern("MM/dd yyyy").getParser(),
							DateTimeFormat.forPattern("MM dd yyyy").getParser(),
							DateTimeFormat.forPattern("MM-dd-yyyy").getParser(),
							DateTimeFormat.forPattern("MM.dd.yyyy").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					parseDate1 = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
					resultDateMD = parseDate1.toString("yyyy-MM-dd");
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			} 
			if (assumemmddyyyy==null || !assumemmddyyyy) { 
				try { 
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("dd/MM/yyyy").getParser(),
							DateTimeFormat.forPattern("dd/MM yyyy").getParser(),
							DateTimeFormat.forPattern("dd MM yyyy").getParser(),
							DateTimeFormat.forPattern("dd-MM-yyyy").getParser(),
							DateTimeFormat.forPattern("dd.MM.yyyy").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					parseDate2 = LocalDate.parse(verbatimEventDate,formatter).toDateMidnight();
					resultDateDM = parseDate2.toString("yyyy-MM-dd");
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}			
			}
			if (resultDateMD!=null && resultDateDM==null) {
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDateMD);
			} else if (resultDateMD==null && resultDateDM!=null) { 
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDateDM);
			} else if (resultDateMD!=null && resultDateDM!=null) { 
				if (resultDateMD.equals(resultDateDM)) { 
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDateDM);
				} else { 
					result.setResultState(EventResult.EventQCResultState.AMBIGUOUS);
				    Interval range = null;
				    if (parseDate1.isBefore(parseDate2)) { 
				        result.setResult(resultDateMD + "/" + resultDateDM);
				    } else { 
				        result.setResult(resultDateDM + "/" + resultDateMD);
				    }
				}
			} 
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^([0-9]{1,2}|[A-Za-z]+)[-/.]([0-9]{1,2}|[A-Za-z]+)[-/. ][0-9]{4}$")) { 
			// Example: 03/Jan/1982
			// Example: Jan-03-1982
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("MMM/dd/yyyy").getParser(),
						DateTimeFormat.forPattern("dd/MMM/yyyy").getParser(),
						DateTimeFormat.forPattern("MMM/dd yyyy").getParser(),
						DateTimeFormat.forPattern("dd/MMM yyyy").getParser(),
						DateTimeFormat.forPattern("MMM-dd-yyyy").getParser(),
						DateTimeFormat.forPattern("dd-MMM-yyyy").getParser(),
						DateTimeFormat.forPattern("MMM-dd yyyy").getParser(),
						DateTimeFormat.forPattern("dd-MMM yyyy").getParser(),
						DateTimeFormat.forPattern("MMM.dd.yyyy").getParser(),
						DateTimeFormat.forPattern("dd.MMM.yyyy").getParser(),
						DateTimeFormat.forPattern("MM.dd.yyyy").getParser(),
						DateTimeFormat.forPattern("dd.MM.yyyy").getParser()						
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter.withLocale(Locale.ENGLISH)).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM-dd");
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}	
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[X*]{2}[-/. ]([0-9]{1,2}|[A-Za-z]+)[-/. ][0-9]{4}$")) { 
			// Example: XX-04-1982   (XX for day)
			// Example: XX-Jan-1995
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("MMM/yyyy").getParser(),
						DateTimeFormat.forPattern("MMM yyyy").getParser(),
						DateTimeFormat.forPattern("MMM-yyyy").getParser(),
						DateTimeFormat.forPattern("MMM yyyy").getParser(),
						DateTimeFormat.forPattern("MMM.yyyy").getParser(),
						DateTimeFormat.forPattern("MM.yyyy").getParser()						
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate.substring(3),formatter.withLocale(Locale.ENGLISH)).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM");
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[X*]{2}[-/. ][X*]{2,3}[-/. ][0-9]{4}$")) { 
			// Example: XX-XXX-1995
			// Example: **-**-1995
			try { 
				DateTimeParser[] parsers = { 
						DateTimeFormat.forPattern("yyyy").getParser(),
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				String yearBit = verbatimEventDate.substring(verbatimEventDate.length()-4);
				DateMidnight parseDate = LocalDate.parse(yearBit,formatter.withLocale(Locale.ENGLISH)).toDateMidnight();
				resultDate = parseDate.toString("yyyy");
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}			
		if (verbatimEventDate.matches("^[0-9]{4}[-][0-9]{3}$")) { 
			// Example: 1994-128  (three digits after year = day of year).
			if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
				try { 
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("yyyy-D").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					LocalDate parseDate = LocalDate.parse(verbatimEventDate,formatter);
					resultDate =  parseDate.toString("yyyy-MM-dd");
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}			

			}	
		}
		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			try { 
				// Example: 1983-15  (two digits after year may fall into subsequent blocks).
				// Example: 1933-Mar
				DateTimeParser[] parsers = { 
					DateTimeFormat.forPattern("yyyy/M").getParser(),
					DateTimeFormat.forPattern("yyyy-M").getParser(),
					DateTimeFormat.forPattern("yyyy-MMM").getParser(),
					DateTimeFormat.forPattern("yyyy/MMM").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				LocalDate parseDate = LocalDate.parse(verbatimEventDate,formatter.withLocale(Locale.ENGLISH));
				resultDate =  parseDate.toString("yyyy-MM");
				// resultDate =  parseDate.dayOfMonth().withMinimumValue() + "/" + parseDate.dayOfMonth().withMaximumValue();
				logger.debug(resultDate);
				if (verbatimEventDate.matches("^[0-9]{4}[-][0-9]{2}$")) { 
				   String century = verbatimEventDate.substring(0,2);
				   String startBit = verbatimEventDate.substring(0,4);
				   String endBit = verbatimEventDate.substring(5, 7);
				   // 1815-16  won't parse here, passes to next block
				   // 1805-06  could be month or abbreviated year
				   // 1805-03  should to be month
				   if (Integer.parseInt(startBit)>=Integer.parseInt(century+endBit)) { 
					  result.setResultState(EventResult.EventQCResultState.RANGE);
				      result.setResult(resultDate);
				   } else { 
					  result.setResultState(EventResult.EventQCResultState.SUSPECT);
				      result.setResult(resultDate);
				   }
				} else {
				   result.setResultState(EventResult.EventQCResultState.RANGE);
				   result.setResult(resultDate);
				}
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[0-9]{4}[-][0-9]{2}$")) {
			// Example: 1884-85   (two digits look like year later in century).
			try { 
				String century = verbatimEventDate.substring(0,2);
				String startBit = verbatimEventDate.substring(0,4);
				String endBit = verbatimEventDate.substring(5, 7);
				String assembly = startBit+"/"+century+endBit;
				logger.debug(assembly);
				Interval parseDate = Interval.parse(assembly);
				logger.debug(parseDate);
				resultDate =  parseDate.getStart().toString("yyyy") + "/" + parseDate.getEnd().toString("yyyy");
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}					
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
				verbatimEventDate.matches("^[0-9]{4}[0-9]{2}[0-9]{2}$") && 
				!verbatimEventDate.endsWith("0000")) {
			// Example: 19950315
			try { 
				DateTimeParser[] parsers = { 
					DateTimeFormat.forPattern("yyyyMMdd").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				DateMidnight parseDate = LocalDate.parse(verbatimEventDate,formatter.withLocale(Locale.ENGLISH)).toDateMidnight();
				resultDate = parseDate.toString("yyyy-MM-dd");
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}			
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			// Example: 1845 
			try { 
				DateTimeParser[] parsers = { 
					DateTimeFormat.forPattern("yyyy").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				LocalDate parseDate = LocalDate.parse(verbatimEventDate,formatter);
				resultDate =  parseDate.dayOfYear().withMinimumValue() + "/" + parseDate.dayOfYear().withMaximumValue();
				logger.debug(resultDate);
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}	
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			// Multiple yyyy-mmm-ddd, mmm-dd-yyyy, dd-mmm-yyyy patterns.
			try { 
				DateTimeParser[] parsers = { 
					DateTimeFormat.forPattern("yyyy MMM dd").getParser(),
					DateTimeFormat.forPattern("yyyy MMM. dd").getParser(),
					DateTimeFormat.forPattern("yyyy, MMM dd").getParser(),
					DateTimeFormat.forPattern("yyyy, MMM. dd").getParser(),
					DateTimeFormat.forPattern("yyyy.MMM.dd").getParser(),
					
					DateTimeFormat.forPattern("yyyy MMM dd'st'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM. dd'st'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM dd'nd'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM. dd'nd'").getParser(),	
					DateTimeFormat.forPattern("yyyy MMM dd'rd'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM. dd'rd'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM dd'th'").getParser(),
					DateTimeFormat.forPattern("yyyy MMM. dd'th'").getParser(),
					
					DateTimeFormat.forPattern("MMM dd, yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'st', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'nd', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'rd', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'th', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd, yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'st', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'nd', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'rd', yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'th', yyyy").getParser(),
					
					DateTimeFormat.forPattern("MMM.dd,yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'st',yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'nd',yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'rd',yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'th',yyyy").getParser(),	
					
					DateTimeFormat.forPattern("MMM.dd.yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'st'.yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'nd'.yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'rd'.yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd'th'.yyyy").getParser(),					
					
					DateTimeFormat.forPattern("MMM-dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM-dd yyyy").getParser(),
					DateTimeFormat.forPattern("dd-MMM-yyyy").getParser(),
					DateTimeFormat.forPattern("dd.MMM.yyyy").getParser(),
					DateTimeFormat.forPattern("dd,MMM,yyyy").getParser(),
					DateTimeFormat.forPattern("dd.MMM.,yyyy").getParser(),
					DateTimeFormat.forPattern("dd. MMM.,yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM, dd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM, dd. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM, dd, yyyy").getParser(),					
					DateTimeFormat.forPattern("MMM, dd., yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd/yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd,yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd, yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd,yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd, yyyy").getParser(),
					DateTimeFormat.forPattern("dd. MMM. yyyy").getParser(),
					DateTimeFormat.forPattern("dd. MMM.yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM., yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM.,yyyy").getParser(),
					
					DateTimeFormat.forPattern("dd MMM, yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM,yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM.yyyy").getParser(),
					DateTimeFormat.forPattern("dd.MMM-yyyy").getParser(),
					DateTimeFormat.forPattern("dd.MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd. MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM-yyyy").getParser(),
					DateTimeFormat.forPattern("dd-MMM yyyy").getParser(),
					DateTimeFormat.forPattern("ddMMMyyyy").getParser(),
					
					DateTimeFormat.forPattern("MMM dd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'st' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'nd' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'rd' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'th' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'st' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'nd' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'rd' yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'th' yyyy").getParser(),	
					DateTimeFormat.forPattern("MMMdd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM.dd yyyy").getParser(),
					
					DateTimeFormat.forPattern("dd MMM, yyyy").getParser(),
					DateTimeFormat.forPattern("dd'st' MMM, yyyy").getParser(),
					DateTimeFormat.forPattern("dd'nd' MMM, yyyy").getParser(),
					DateTimeFormat.forPattern("dd'rd' MMM, yyyy").getParser(),
					DateTimeFormat.forPattern("dd'th MMM', yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM., yyyy").getParser(),
					DateTimeFormat.forPattern("dd'st' MMM., yyyy").getParser(),
					DateTimeFormat.forPattern("dd'nd' MMM., yyyy").getParser(),
					DateTimeFormat.forPattern("dd'rd' MMM., yyyy").getParser(),
					DateTimeFormat.forPattern("dd'th' MMM., yyyy").getParser(),
					
					DateTimeFormat.forPattern("dd MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd'st' MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd'nd' MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd'rd' MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd'th' MMM yyyy").getParser(),
					DateTimeFormat.forPattern("dd MMM. yyyy").getParser(),
					DateTimeFormat.forPattern("dd'st' MMM. yyyy").getParser(),
					DateTimeFormat.forPattern("dd'nd' MMM. yyyy").getParser(),
					DateTimeFormat.forPattern("dd'rd' MMM. yyyy").getParser(),
					DateTimeFormat.forPattern("dd'th' MMM. yyyy").getParser(),					
					
					DateTimeFormat.forPattern("dd/MMM/yyyy").getParser(),
					DateTimeFormat.forPattern("dd/MMM yyyy").getParser(),
					DateTimeFormat.forPattern("MMM/dd yyyy").getParser(),
					DateTimeFormat.forPattern("MMM/dd/yyyy").getParser(),
					
					DateTimeFormat.forPattern("MMM dd. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'st'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'nd'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'rd'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'th'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'st'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'nd'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'rd'. yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'th'. yyyy").getParser(),					
					DateTimeFormat.forPattern("MMM dd.yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd.yyyy").getParser(),

					DateTimeFormat.forPattern("MMM. dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'st'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'nd'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'rd'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM. dd'th'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'st'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'nd'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'rd'-yyyy").getParser(),
					DateTimeFormat.forPattern("MMM dd'th'-yyyy").getParser(),
					
					DateTimeFormat.forPattern("yyyy-MMM-dd").getParser()
				};
				DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
				String cleaned = cleanMonth(verbatimEventDate);
				
				try {
					// Specify English locale, or local default will be used
				    LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.ENGLISH));
				    resultDate =  parseDate.toString("yyyy-MM-dd");
				} catch (Exception e) {
					try {
						logger.debug(e.getMessage());
						LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.FRENCH));
						resultDate =  parseDate.toString("yyyy-MM-dd");
					} catch (Exception e1) { 
						try { 
							logger.debug(e1.getMessage());
							LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.ITALIAN));
							resultDate =  parseDate.toString("yyyy-MM-dd");
						} catch (Exception e2) {
							try { 
							logger.debug(e2.getMessage());
							LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.GERMAN));
							resultDate =  parseDate.toString("yyyy-MM-dd");
							} catch (Exception e3) { 
								try { 
								    logger.debug(e2.getMessage());
								    LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.forLanguageTag("es")));
								    resultDate =  parseDate.toString("yyyy-MM-dd");
								} catch (Exception e4) { 
									logger.debug(e2.getMessage());
									LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.forLanguageTag("pt")));
									resultDate =  parseDate.toString("yyyy-MM-dd");
								}
							}
						}
					}
				}	
				logger.debug(resultDate);
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}		
		logger.debug(result.getResultState());
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			// Example: jan.-1992
			// Example: January 1992
			if (verbatimEventDate.matches(".*[0-9]{4}.*")) { 
				try { 
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("MMM, yyyy").getParser(),
							DateTimeFormat.forPattern("MMM., yyyy").getParser(),
							DateTimeFormat.forPattern("MMM.,yyyy").getParser(),
							DateTimeFormat.forPattern("MMM.-yyyy").getParser(),
							DateTimeFormat.forPattern("MMM.yyyy").getParser(),
							DateTimeFormat.forPattern("MMM. yyyy").getParser(),
							DateTimeFormat.forPattern("MMM-yyyy").getParser(),
							DateTimeFormat.forPattern("MMM -yyyy").getParser(),
							DateTimeFormat.forPattern("MMM yyyy").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					String cleaned = cleanMonth(verbatimEventDate);
					// Strip off a trailing period after a final year
					if (cleaned.matches("^.*[0-9]{4}[.]$")) { 
						cleaned = cleaned.replaceAll("[.]$", "");
					}
					LocalDate parseDate = LocalDate.parse(cleaned,formatter.withLocale(Locale.ENGLISH));
					resultDate =  parseDate.toString("yyyy-MM");
					logger.debug(resultDate);
				    result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[0-9]{4}([- ]+| to |[/ ]+)[0-9]{4}$")) {
			// Example:  1882-1995
			// Example:  1882 to 1885
			// Example:  1882/1885
			try { 
				String cleaned = verbatimEventDate.replace(" ", "");
				cleaned = cleaned.replace("-", "/");
				if (cleaned.matches("^[0-9]{4}to[0-9]{4}$")) { 
					int len = verbatimEventDate.length();
					int lastYear = len - 4;
					cleaned = verbatimEventDate.substring(0,4) + "/" + verbatimEventDate.substring(lastYear, len);
				}
				logger.debug(cleaned);
				Interval parseDate = Interval.parse(cleaned);
				logger.debug(parseDate);
				resultDate =  parseDate.getStart().toString("yyyy") + "/" + parseDate.getEnd().toString("yyyy");
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}	
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[A-Za-z]+[.]{0,1}( and | to |[-][ ]{0,1})[A-Za-z]+[.]{0,1}(, |[/ .])[0-9]{4}$")) { 
			// Example: Jan to Feb 1882
			// Example: Jan-Feb/1882
			verbatimEventDate = verbatimEventDate.replace(", ", " ");
		    if ( verbatimEventDate.matches("^[A-Za-z]+[.]{0,1}[-][A-Za-z]+[.]{0,1}[.][0-9]{4}$"))
		    { 
		    	// transform case with multiple periods to slash before year.
			    verbatimEventDate = verbatimEventDate.substring(0,verbatimEventDate.length()-5) + "/" + verbatimEventDate.substring(verbatimEventDate.length()-4);
			   logger.debug(verbatimEventDate);
		    }
		    if ( verbatimEventDate.matches("^[A-Za-z]+[.]{0,1}[-][ ]{1}[A-Za-z]+[.]{0,1}[/ .][0-9]{4}$"))
		    { 
		    	// remove space trailing after dash.
			    verbatimEventDate = verbatimEventDate.replace("- ", "-");
			   logger.debug(verbatimEventDate);
		    }
		    if ( verbatimEventDate.matches("^[A-Za-z]+[.]{0,1} and {1}[A-Za-z]+[.]{0,1}[/ .][0-9]{4}$"))
		    { 
		    	// replace and with dash
			    verbatimEventDate = verbatimEventDate.replace(" and ", "-");
			   logger.debug(verbatimEventDate);
		    }		
		    if ( verbatimEventDate.matches("^[A-Za-z]+[.]{0,1} to {1}[A-Za-z]+[.]{0,1}[/ .][0-9]{4}$"))
		    { 
		    	// replace to with dash
			    verbatimEventDate = verbatimEventDate.replace(" to ", "-");
			   logger.debug(verbatimEventDate);
		    }			    
			try { 
				String[] bits = verbatimEventDate.replace(" ", "/").split("-");
				if (bits!=null && bits.length==2) { 
					String year = verbatimEventDate.substring(verbatimEventDate.length()-4,verbatimEventDate.length());
					String startBit = bits[0]+"/"+year;
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("MMM/yyyy").getParser(),
							DateTimeFormat.forPattern("MMM./yyyy").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					LocalDate parseStartDate = LocalDate.parse(cleanMonth(startBit),formatter.withLocale(Locale.ENGLISH));
					LocalDate parseEndDate = LocalDate.parse(cleanMonth(bits[1]),formatter.withLocale(Locale.ENGLISH));
					resultDate =  parseStartDate.toString("yyyy-MM") + "/" + parseEndDate.toString("yyyy-MM");
					logger.debug(resultDate);
				    result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
				}
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}( - |[-])[0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[/ -.][0-9]{4}$")) 
		{ 
			logger.debug(verbatimEventDate);
			// Example: 05/Jan/1882-03/Feb/1885
		    if (verbatimEventDate.matches("^[0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[-][0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[-][0-9]{4}$")) { 
		    	// transform case with multiple dashes to slash before year.
			    verbatimEventDate = verbatimEventDate.substring(0,verbatimEventDate.length()-5) + "/" + verbatimEventDate.substring(verbatimEventDate.length()-4);
			   logger.debug(verbatimEventDate);
		    }
		    if (verbatimEventDate.matches("^[0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[-][0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[.][0-9]{4}$")) { 
		    	// transform case with multiple periods to slash before year.
			    verbatimEventDate = verbatimEventDate.substring(0,verbatimEventDate.length()-5) + "/" + verbatimEventDate.substring(verbatimEventDate.length()-4);
			   logger.debug(verbatimEventDate);
		    }
			try { 
				String[] bits = verbatimEventDate.replace(" - ","-").replace(" ", "/").split("-");
				if (bits!=null && bits.length==2) { 
					String year = verbatimEventDate.substring(verbatimEventDate.length()-4,verbatimEventDate.length());
					String startBit = bits[0]+"/"+year;
					logger.debug(cleanMonth(startBit));
					logger.debug(cleanMonth(bits[1]));
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("dd MMM/yyyy").getParser(),
							DateTimeFormat.forPattern("dd.MMM/yyyy").getParser(),
							DateTimeFormat.forPattern("dd/MMM/yyyy").getParser(),
							DateTimeFormat.forPattern("ddMMM/yyyy").getParser(),
							DateTimeFormat.forPattern("dd MMM./yyyy").getParser(),
							DateTimeFormat.forPattern("dd.MMM./yyyy").getParser(),
							DateTimeFormat.forPattern("dd/MMM./yyyy").getParser(),
							DateTimeFormat.forPattern("ddMMM./yyyy").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					LocalDate parseStartDate = LocalDate.parse(cleanMonth(startBit),formatter.withLocale(Locale.ENGLISH));
					LocalDate parseEndDate = LocalDate.parse(cleanMonth(bits[1]),formatter.withLocale(Locale.ENGLISH));
					resultDate =  parseStartDate.toString("yyyy-MM-dd") + "/" + parseEndDate.toString("yyyy-MM-dd");
					logger.debug(resultDate);
				    result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
				}
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[A-Za-z]+[.]{0,1}[ ]{0,1}[0-9]{1,2}( - |[-]| to )[A-Za-z]+[.]{0,1}[ ]{0,1}[0-9]{1,2}[/ .,][ ]{0,1}[0-9]{4}$")) 
		{ 
			logger.debug(verbatimEventDate);
			// Example: Aug. 5 - Sept. 8, 1943
			try { 
				String[] bits = verbatimEventDate.replace(" to ","-").replace(" - ","-").replace(", "," ").replace(" ", "/").split("-");
				if (bits!=null && bits.length==2) { 
					String year = verbatimEventDate.substring(verbatimEventDate.length()-4,verbatimEventDate.length());
					String startBit = bits[0]+"/"+year;
					logger.debug(cleanMonth(startBit));
					logger.debug(cleanMonth(bits[1]));
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("MMM/dd/yyyy").getParser(),
							DateTimeFormat.forPattern("MMM./dd/yyyy").getParser(),
							DateTimeFormat.forPattern("MMM.dd/yyyy").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					LocalDate parseStartDate = LocalDate.parse(cleanMonth(startBit),formatter.withLocale(Locale.ENGLISH));
					LocalDate parseEndDate = LocalDate.parse(cleanMonth(bits[1]),formatter.withLocale(Locale.ENGLISH));
					resultDate =  parseStartDate.toString("yyyy-MM-dd") + "/" + parseEndDate.toString("yyyy-MM-dd");
					logger.debug(resultDate);
				    result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
				}
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}			
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[0-9]{1,2}[-][0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[/ -.][0-9]{4}$")) 
		{ 
			// Example 05-02 Jan./1992
		    if (verbatimEventDate.matches("^[0-9]{1,2}[-][0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[-][0-9]{4}$")) { 
		    	// transform case with multiple dashes to slash before year.
			    verbatimEventDate = verbatimEventDate.substring(0,verbatimEventDate.length()-5) + "/" + verbatimEventDate.substring(verbatimEventDate.length()-4);
			   logger.debug(verbatimEventDate);
		    }
		    if (verbatimEventDate.matches("^[0-9]{1,2}[-][0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[.][0-9]{4}$")) { 
		    	// transform case with multiple periods to slash before year.
			    verbatimEventDate = verbatimEventDate.substring(0,verbatimEventDate.length()-5) + "/" + verbatimEventDate.substring(verbatimEventDate.length()-4);
			   logger.debug(verbatimEventDate);
		    }
			try { 
				String[] bits = verbatimEventDate.replace(" ", "/").split("-");
				if (bits!=null && bits.length==2) { 
					String year = verbatimEventDate.substring(verbatimEventDate.length()-4,verbatimEventDate.length());
					logger.debug(cleanMonth(bits[1]));
					DateTimeParser[] parsers = { 
							DateTimeFormat.forPattern("dd MMM/yyyy").getParser(),
							DateTimeFormat.forPattern("dd.MMM/yyyy").getParser(),
							DateTimeFormat.forPattern("dd/MMM/yyyy").getParser(),
							DateTimeFormat.forPattern("ddMMM/yyyy").getParser(),
							DateTimeFormat.forPattern("dd MMM./yyyy").getParser(),
							DateTimeFormat.forPattern("dd.MMM./yyyy").getParser(),
							DateTimeFormat.forPattern("dd/MMM./yyyy").getParser(),
							DateTimeFormat.forPattern("ddMMM./yyyy").getParser()
					};
					DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
					LocalDate parseEndDate = LocalDate.parse(cleanMonth(bits[1]),formatter.withLocale(Locale.ENGLISH));
					String startMonthYear = parseEndDate.toString("MMM/yyyy");
					String startBit = bits[0]+"/"+startMonthYear;
					logger.debug(startBit);
					LocalDate parseStartDate = LocalDate.parse(startBit,formatter.withLocale(Locale.ENGLISH));
					resultDate =  parseStartDate.toString("yyyy-MM-dd") + "/" + parseEndDate.toString("yyyy-MM-dd");
					logger.debug(resultDate);
				    result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
				}
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[0-9]{2}[-. ]XXX[-. ][0-9]{4}$")) { 
			// Example: 05-XXX-1884
			try { 
				String start = verbatimEventDate.substring(verbatimEventDate.length()-4) + "-01-" + verbatimEventDate.substring(0,2);
				String end = verbatimEventDate.substring(verbatimEventDate.length()-4) + "-12-" + verbatimEventDate.substring(0,2);
				EventResult compositeResult = DateUtils.extractDateFromVerbatimER(start + "/" + end, yearsBeforeSuspect, assumemmddyyyy);
				logger.debug(compositeResult.getResultState());
				if (compositeResult.getResultState().equals(EventResult.EventQCResultState.RANGE)) { 
				   result.setResultState(EventResult.EventQCResultState.RANGE);
				   result.setResult(compositeResult.getResult());
				   logger.debug(result.getResult());
				}
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
				verbatimEventDate.matches("^[0-9]{4}-[0-9]{2}/[0-9]{4}-[0-9]{2}$")
			) {
			// Example: 1885-03/1886-04
			try { 
				Interval parseDate = Interval.parse(verbatimEventDate);
				logger.debug(parseDate);
				resultDate =  parseDate.getStart().toString("yyyy-MM") + "/" + parseDate.getEnd().toString("yyyy-MM");
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			// Example: 1995-03-05/1996-05-08
			try { 
				Interval parseDate = Interval.parse(verbatimEventDate);
				logger.debug(parseDate);
				resultDate =  parseDate.getStart().toString("yyyy-MM-dd") + "/" + parseDate.getEnd().toString("yyyy-MM-dd");
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}	
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			// Example: Jan,15-18 1882
			// Example: Jan. 17 and 18 1882
			String cleaned = verbatimEventDate.trim();
			if (verbatimEventDate.matches("^[A-Za-z.]+[ ,]+[0-9]{1,2} and [0-9]{0,2}[ ,]+[0-9]{4}$")) { 
				cleaned = cleaned.replace(" and ", " to ");
			}			
			if (verbatimEventDate.matches("^[A-Za-z.]+[ ,]+[0-9]{1,2}-[0-9]{0,2}[ ,]+[0-9]{4}$")) { 
				cleaned = cleaned.replace("-", " to ");
			}
			if (cleaned.contains(" to ")) { 
				String[] bits = cleaned.split(" to ");
				String yearRegex = ".*([0-9]{4}).*";
				Matcher yearMatcher = Pattern.compile(yearRegex).matcher(cleaned);
				String monthRegex = "([A-Za-z.]+).*";
				Matcher monthMatcher = Pattern.compile(monthRegex).matcher(cleaned);				
				if (yearMatcher.matches() && monthMatcher.matches()) {
				    String year = yearMatcher.group(1);
				    String month = monthMatcher.group(1);
				        if (bits.length==2) { 
				        	if (!bits[0].contains(year)) { 
				        		bits[0] = bits[0] + " " + year;
				        	}
				        	if (!bits[1].contains(year)) { 
				        		bits[1] = bits[1] + " " + year;
				        	}
				        	if (!bits[1].contains(month)) { 
				        		bits[1] = month + " " + bits[1];
				        	}				        	
				        	Map<String,String> resultBit0 = DateUtils.extractDateFromVerbatim(bits[0]);
				        	if (resultBit0.size()>0 && resultBit0.get("resultState").equals("date")) {
				        	    Map<String,String> resultBit1 = DateUtils.extractDateFromVerbatim(bits[1]);
				        	    if (resultBit1.size()>0 && resultBit1.get("resultState").equals("date")) {
				                    result.setResultState(EventResult.EventQCResultState.RANGE);
				    				result.setResult(resultBit0.get("result")+ "/" + resultBit1.get("result"));
				        	    }
				        	}
				        	logger.debug(bits[0]);
				        	logger.debug(bits[1]);
				        }
				}
			}
		}
		
		// Now test to see if result is sane.
		if (result!=null && !result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			Interval testExtract = DateUtils.extractDateInterval(result.getResult());
			if(testExtract==null || testExtract.getStart().getYear()< yearsBeforeSuspect) { 
				result.setResultState(EventResult.EventQCResultState.SUSPECT);
				logger.debug(result.getResult());
				logger.debug(testExtract);
			}
			if (!verbatimEventDate.matches(".*[0-9]{4}.*")) { 
				result = new EventResult();
			}
		}
		
		return result;
	}

    /**
     * Test to see if a string appears to represent a date range of more than one day.
     * 
     * @param eventDate to check
     * @return true if a date range, false otherwise.
     */
    public static boolean isRange(String eventDate) { 
    	boolean isRange = false;
    	if (eventDate!=null) { 
    		String[] dateBits = eventDate.split("/");
    		if (dateBits!=null && dateBits.length==2) { 
    			//probably a range.
    			DateTimeParser[] parsers = { 
    					DateTimeFormat.forPattern("yyyy-MM").getParser(),
    					DateTimeFormat.forPattern("yyyy").getParser(),
    					ISODateTimeFormat.dateOptionalTimeParser().getParser()
    			};
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    			try { 
    				// must be at least a 4 digit year.
    				if (dateBits[0].length()>3 && dateBits[1].length()>3) { 
    					DateMidnight startDate = LocalDate.parse(dateBits[0],formatter).toDateMidnight();
    					DateMidnight endDate = LocalDate.parse(dateBits[1],formatter).toDateMidnight();
    					// both start date and end date must parse as dates.
    					isRange = true;
    				}
    			} catch (Exception e) { 
    				// not a date range
    				e.printStackTrace();
    				logger.debug(e.getMessage());
    			}
    		} else if (dateBits!=null && dateBits.length==1) { 
    			logger.debug(dateBits[0]);
    			// Date bits does not contain a /
    			// Is eventDate in the form yyyy-mm-dd, if so, not a range  
    			DateTimeParser[] parsers = { 
    					DateTimeFormat.forPattern("yyyy-MM-dd").getParser(),
    			};
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    			try { 
    				DateMidnight date = DateMidnight.parse(eventDate,formatter);
    				isRange = false;
    			} catch (Exception e) { 
    				logger.debug(e.getMessage());
    				// not parsable with the yyyy-mm-dd parser.
    				DateTimeParser[] parsers2 = { 
        					DateTimeFormat.forPattern("yyyy-MM").getParser(),
        					DateTimeFormat.forPattern("yyyy").getParser(),
        			};
        			formatter = new DateTimeFormatterBuilder().append( null, parsers2 ).toFormatter();
        			try { 
        				// must be at least a 4 digit year.
        				if (dateBits[0].length()>3) { 
        					DateMidnight startDate = DateMidnight.parse(dateBits[0],formatter);
        					// date must parse as either year or year and month dates.
        					isRange = true;
        				}
        			} catch (Exception e1) { 
        				// not a date range
        			}    				
    				
    			}
    			
    		}
    	}
    	return isRange;
    }	
	
    
    /**
     * Given a string that may be a date or a date range, extract a interval of
     * dates from that date range (ignoring time (thus the duration for the 
     * interval will be from one date midnight to another).
     * 
     * @see #extractInterval(String) which is probably the method you want.
     * 
     * @param eventDate a string containing a dwc:eventDate from which to extract an interval.
     * @return An interval from one DateMidnight to another DateMidnight.
     */
    public static Interval extractDateInterval(String eventDate) {
    	Interval result = null;
    	DateTimeParser[] parsers = { 
    			DateTimeFormat.forPattern("yyyy-MM").getParser(),
    			DateTimeFormat.forPattern("yyyy").getParser(),
    			ISODateTimeFormat.dateOptionalTimeParser().getParser() 
    	};
    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    	if (eventDate!=null && eventDate.contains("/") && isRange(eventDate)) {
    		String[] dateBits = eventDate.split("/");
    		try { 
    			// must be at least a 4 digit year.
    			if (dateBits[0].length()>3 && dateBits[1].length()>3) { 
    				DateMidnight startDate = DateMidnight.parse(dateBits[0],formatter);
    				DateMidnight endDate = DateMidnight.parse(dateBits[1],formatter);
    				if (dateBits[1].length()==4) { 
    	                  result = new Interval(startDate,endDate.plusMonths(12).minusDays(1));
    	               } else if (dateBits[1].length()==7) { 
    	                  result = new Interval(startDate,endDate.plusMonths(1).minusDays(1));
    	               } else { 
    				      result = new Interval(startDate, endDate);
    	               }
    			}
    		} catch (Exception e) { 
    			// not a date range
               logger.error(e.getMessage());
    		}
    	} else {
    		try { 
               DateMidnight startDate = DateMidnight.parse(eventDate, formatter);
               logger.debug(eventDate);
               logger.debug(startDate);
               if (eventDate.length()==4) { 
                  result = new Interval(startDate,startDate.plusMonths(12).minusDays(1));
               } else if (eventDate.length()==7) { 
                  result = new Interval(startDate,startDate.plusMonths(1).minusDays(1));
               } else { 
                  result = new Interval(startDate,startDate.plusDays(1));
               }
    		} catch (Exception e) { 
    			// not a date
    			e.printStackTrace();
               logger.error(e.getMessage());
    		}
    	}
    	return result;
    }
    
    /**
     * Given a string that may be a date or a date range, extract a interval of
     * dates from that date range, up to the end milisecond of the last day.
     * 
     * @see #extractDateInterval(String) which returns a pair of DateMidnights.
     * 
     * @param eventDate a string containing a dwc:eventDate from which to extract an interval.
     * @return an interval from the beginning of event date to the end of event date.
     */
    public static Interval extractInterval(String eventDate) {
    	Interval result = null;
    	DateTimeParser[] parsers = { 
    			DateTimeFormat.forPattern("yyyy-MM").getParser(),
    			DateTimeFormat.forPattern("yyyy").getParser(),
    			ISODateTimeFormat.dateOptionalTimeParser().getParser() 
    	};
    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    	if (eventDate!=null && eventDate.contains("/") && isRange(eventDate)) {
    		String[] dateBits = eventDate.split("/");
    		try { 
    			// must be at least a 4 digit year.
    			if (dateBits[0].length()>3 && dateBits[1].length()>3) { 
    				DateMidnight startDate = DateMidnight.parse(dateBits[0],formatter);
    				DateTime endDate = DateTime.parse(dateBits[1],formatter);
    				logger.debug(startDate);
    				logger.debug(endDate);
    				if (dateBits[1].length()==4) { 
    					result = new Interval(startDate,endDate.plusMonths(12).minus(1l));
    				} else if (dateBits[1].length()==7) { 
    					result = new Interval(startDate,endDate.plusMonths(1).minus(1l));
    				} else { 
    					result = new Interval(startDate, endDate.plusDays(1).minus(1l));
    				}
    				logger.debug(result);
    			}
    		} catch (Exception e) { 
    			// not a date range
               logger.error(e.getMessage());
    		}
    	} else {
    		try { 
               DateMidnight startDate = DateMidnight.parse(eventDate, formatter);
               logger.debug(startDate);
               if (eventDate.length()==4) { 
                  DateTime endDate = startDate.toDateTime().plusMonths(12).minus(1l);
                  result = new Interval(startDate, endDate);
                  logger.debug(result);
               } else if (eventDate.length()==7) { 
                  DateTime endDate = startDate.toDateTime().plusMonths(1).minus(1l);
                  result = new Interval(startDate,endDate);
                  logger.debug(result);
               } else { 
                  DateTime endDate = startDate.toDateTime().plusDays(1).minus(1l);
                  result = new Interval(startDate,endDate);
                  logger.debug(result);
               }
    		} catch (Exception e) { 
    			// not a date
               logger.error(e.getMessage());
    		}
    	}
    	return result;
    }  
    
    /**
     * Extract a single joda date from an event date.
     * 
     * @param eventDate an event date from which to try to extract a DateMidnight
     * @return a DateMidnight or null if a date cannot be extracted
     */
    public static DateMidnight extractDate(String eventDate) {
    	DateMidnight result = null;
    	DateTimeParser[] parsers = { 
    			DateTimeFormat.forPattern("yyyy-MM").getParser(),
    			DateTimeFormat.forPattern("yyyy").getParser(),
    			ISODateTimeFormat.dateOptionalTimeParser().getParser(), 
    			ISODateTimeFormat.date().getParser() 
    	};
    	DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    	try { 
    		result = DateMidnight.parse(eventDate, formatter);
    		logger.debug(result);
    	} catch (Exception e) { 
    		// not a date
    		logger.error(e.getMessage());
    	}
    	return result;
    }      
    
    /**
     * Identify whether an event date is consistent with its atomic parts.  
     * 
     * @param eventDate  dwc:eventDate string to compare with atomic parts.
     * @param startDayOfYear  dwc:startDayOfYear for comparison with eventDate
     * @param endDayOfYear dwc:endDayOfYear for comparison with eventDate
     * @param year dwc:year for comparison with eventDate
     * @param month dwc:month for comparison with eventDate
     * @param day dwc:day for comparison with eventDate
     * 
     * @return true if consistent, or if eventDate is empty, or if all 
     *    atomic parts are empty, otherwise false.
     */
    public static boolean isConsistent(String eventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day) {
    	if (isEmpty(eventDate) || (isEmpty(startDayOfYear) && isEmpty(endDayOfYear) && isEmpty(year) && isEmpty(month) && isEmpty(day))) { 
    		return true;
    	}
		// TODO: Add support for eventTime
    	boolean result = false;
    	result = isConsistent(eventDate,year,month,day);
    	logger.debug(result);
    	if ((result || (!isEmpty(eventDate) && isEmpty(year) && isEmpty(month) && isEmpty(day))) && (!isEmpty(startDayOfYear) || !isEmpty(endDayOfYear))) {
    		if (endDayOfYear==null || endDayOfYear.trim().length()==0 || startDayOfYear.trim().equals(endDayOfYear.trim())) {
    			int startDayInt = -1;
    			try {
    				startDayInt = Integer.parseInt(startDayOfYear);
    			} catch (NumberFormatException e) {
    				logger.debug(e.getMessage());
    				logger.debug(startDayOfYear + " is not an integer."); 
    				result = false; 
    			} 
    			if (DateUtils.extractDate(eventDate).getDayOfYear() == startDayInt) { 
    				result=true;
    			} else { 
    				result = false;
    			}
    		} else {
       			int startDayInt = -1;
       			int endDayInt = -1;
    			try {
    				startDayInt = Integer.parseInt(startDayOfYear);
    				endDayInt = Integer.parseInt(endDayOfYear);
    			} catch (NumberFormatException e) {
    				logger.debug(e.getMessage());
    				result = false; 
    			} 
    			Interval eventDateInterval = DateUtils.extractDateInterval(eventDate);
    			logger.debug(eventDateInterval);
    			int endDayOfInterval = eventDateInterval.getEnd().getDayOfYear();  // midnight on the next day, so subtract 1 to get the same integer day.
    			if (eventDateInterval.getStart().getDayOfYear() == startDayInt && endDayOfInterval == endDayInt ) { 
    				result=true;
    			} else { 
    				result = false;
    			}
    		}
    	}
    	return result;
    }
    
    /**
     * Identify whether an event date and a year, month, and day are consistent, where consistent 
     * means that either the eventDate is a single day and the year-month-day represent the same day
     * or that eventDate is a date range that defines the same date range as year-month (where day is 
     * null) or the same date range as year (where day and month are null).  If all of eventDate, 
     * year, month, and day are null or empty, then returns true.  If eventDate specifies an interval
     * of more than one day and day is specified, then result is true if the day is the first day of the 
     * interval.  If eventDate is not null and year, month, and day are, then result is false (data is 
     * not consistent with no data).
     * 
     * Provides: EVENTDATE_CONSISTENT_WITH_DAY_MONTH_YEAR 
     * 
     * @param eventDate dwc:eventDate
     * @param year dwc:year
     * @param month dwc:month
     * @param day dwc:day
     * 
     * @return true if eventDate is consistent with year-month-day.
     */
    public static boolean isConsistent(String eventDate, String year, String month, String day) {
    	boolean result = false;
    	StringBuffer date = new StringBuffer();
    	if (!isEmpty(eventDate)) {
    		if (!isEmpty(year) && !isEmpty(month) && !isEmpty(day)) { 
    			date.append(year).append("-").append(month).append("-").append(day);
    			if (!isRange(eventDate)) { 
    				DateMidnight eventDateDate = extractDate(eventDate);
    				DateMidnight bitsDate = extractDate(date.toString());
    				if (eventDateDate!=null && bitsDate !=null) { 
    					if (eventDateDate.year().compareTo(bitsDate)==0 && eventDateDate.monthOfYear().compareTo(bitsDate)==0 && eventDateDate.dayOfMonth().compareTo(bitsDate)==0) {
    						result = true;   
    					}	   
    				}
    			} else {
    				Interval eventDateDate = extractDateInterval(eventDate);
    				DateMidnight bitsDate = extractDate(date.toString());
    				if (eventDateDate!=null && bitsDate !=null) { 
    					if (eventDateDate.getStart().year().compareTo(bitsDate)==0 && eventDateDate.getStart().monthOfYear().compareTo(bitsDate)==0 && eventDateDate.getStart().dayOfMonth().compareTo(bitsDate)==0) {
    						result = true;   
    					}	   
    				}    				
    				
    			}
    		}
    		if (!isEmpty(year) && !isEmpty(month) && isEmpty(day)) { 
    			date.append(year).append("-").append(month);
    			Interval eventDateInterval = extractDateInterval(eventDate);
    			Interval bitsInterval = extractDateInterval(date.toString());
    			if (eventDateInterval.equals(bitsInterval)) {
    				result = true;
    			}
    		}    	
    		if (!isEmpty(year) && isEmpty(month) && isEmpty(day)) { 
    			date.append(year);
    			Interval eventDateInterval = extractDateInterval(eventDate);
    			Interval bitsInterval = extractDateInterval(date.toString());
    			if (eventDateInterval.equals(bitsInterval)) {
    				result = true;
    			}
    		}    		
    	} else { 
    		if (isEmpty(year) && isEmpty(month) && isEmpty(day)) {
    			// eventDate, year, month, and day are all empty, treat as consistent.
    			result = true;
    		}
    	}
        return result;
    }
    
    /**
     * Does a string contain a non-blank value.
     * 
     * @param aString to check
     * @return true if the string is null, is an empty string, is equal to the value 'NULL'
     *     or contains only whitespace.
     */
    public static boolean isEmpty(String aString)  {
    	boolean result = true;
    	if (aString != null && aString.trim().length()>0) { 
    		if (!aString.trim().toUpperCase().equals("NULL")) { 
    		   result = false;
    		}
    	}
    	return result;
    }
   
    /**
     * Does eventDate match an ISO date that contains a time (including the instant of 
     * midnight (a time with all zero elements)). 
     * 
     * @param eventDate string to check for an ISO date with a time.
     * @return true if eventDate is an ISO date that includes a time, or if eventDate is an 
     * ISO date range either the start or end of which contains a time.  
     */
    public static boolean containsTime(String eventDate) {
    	boolean result = false;
    	if (!isEmpty(eventDate)) { 
    		if (eventDate.endsWith("UTC")) { eventDate = eventDate.replace("UTC", "Z"); } 
    		DateTimeParser[] parsers = { 
    				ISODateTimeFormat.dateHour().getParser(),
    				ISODateTimeFormat.dateTimeParser().getParser(),
    				ISODateTimeFormat.dateHourMinute().getParser(),
    				ISODateTimeFormat.dateHourMinuteSecond().getParser(),
    				ISODateTimeFormat.dateTime().getParser() 
    		};
    		DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    		if (eventDate.matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
    			try { 
    				LocalDate match = LocalDate.parse(eventDate, formatter);
    				result = true;
    				logger.debug(match);
    			} catch (Exception e) { 
    				// not a date with a time
    				logger.error(e.getMessage());
    			}    		
    		}
    		if (isRange(eventDate) && eventDate.contains("/") && !result) { 
    			String[] bits = eventDate.split("/");
    			if (bits!=null && bits.length>1) { 
    				// does either start or end date contain a time?
    				if (bits[0].matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
    					try { 
    						LocalDate match = LocalDate.parse(bits[0], formatter);
    						result = true;
    						logger.debug(match);
    					} catch (Exception e) { 
    						// not a date with a time
    						logger.error(e.getMessage());
    					}     
    				}
    				if (bits[1].matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
    					try { 
    						LocalDate match = LocalDate.parse(bits[1], formatter);
    						result = true;
    						logger.debug(match);
    					} catch (Exception e) { 
    						// not a date with a time
    						logger.error(e.getMessage());
    					}     	  
    				}
    			}
    		}
    	}
    	return result;
    }
    
    /**
     * Attempt to extract a time from an eventDate that could contain time information.
     * 
     * @param eventDate dwc:eventDate from which to try to extract a time (in UTC).
     * @return a string containing a time in UTC or null
     */
    public static String extractZuluTime(String eventDate) {
    	String result = null;
    	if (!isEmpty(eventDate)) { 
    		if (eventDate.endsWith("UTC")) { eventDate = eventDate.replace("UTC", "Z"); } 
    		DateTimeParser[] parsers = { 
    				ISODateTimeFormat.dateHour().getParser(),
    				ISODateTimeFormat.dateTimeParser().getParser(),
    				ISODateTimeFormat.dateHourMinute().getParser(),
    				ISODateTimeFormat.dateHourMinuteSecond().getParser(),
    				ISODateTimeFormat.dateTime().getParser() 
    		};
    		DateTimeFormatter formatter = new DateTimeFormatterBuilder().append( null, parsers ).toFormatter();
    		if (eventDate.matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
    			try { 
    	    		result = instantToStringTime(Instant.parse(eventDate, formatter));
    				logger.debug(result);
    			} catch (Exception e) { 
    				// not a date with a time
    				logger.error(e.getMessage());
    			}    		
    		}
    		if (isRange(eventDate) && eventDate.contains("/") && result!=null) { 
    			String[] bits = eventDate.split("/");
    			if (bits!=null && bits.length>1) { 
    				// does either start or end date contain a time?
    				if (bits[0].matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
    					try { 
    	    				result = instantToStringTime(Instant.parse(bits[0], formatter));
    						logger.debug(result);
    					} catch (Exception e) { 
    						// not a date with a time
    						logger.error(e.getMessage());
    					}     
    				}
    				if (bits[1].matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+") && result!=null) { 
    					try { 
    	    				result = instantToStringTime(Instant.parse(bits[1], formatter));
    	    				logger.debug(result);
    					} catch (Exception e) { 
    						// not a date with a time
    						logger.error(e.getMessage());
    					}     	  
    				}
    			}
    		}
    	}
    	return result;
    }    
    
    /**
     * Test if an event date specifies a duration of one day or less.
     * 
     * @param eventDate to test.
     * @return true if duration is one day or less.
     */
    public static boolean specificToDay(String eventDate) { 
    	boolean result = false;
    	if (!isEmpty(eventDate)) { 
    	    Interval eventDateInterval = extractInterval(eventDate);
    	    logger.debug(eventDateInterval);
    	    logger.debug(eventDateInterval.toDuration());
    	    if (eventDateInterval.toDuration().getStandardDays()<1l) { 
    	    	result = true;
    	    } else if (eventDateInterval.toDuration().getStandardDays()==1l && eventDateInterval.getStart().getDayOfYear()==eventDateInterval.getEnd().getDayOfYear()) {
    	    	result = true;
    	    }
    	}
    	return result;
    }

    /**
     * Test if an event date specifies a duration of 31 days or less.
     * 
     * Provides: EVENTDATE_PRECISON_MONTH_OR_BETTER
     * 
     * @param eventDate to test.
     * @return true if duration is 31 days or less.
     */
    public static boolean specificToMonthScale(String eventDate) { 
    	boolean result = false;
    	if (!isEmpty(eventDate)) { 
    	    Interval eventDateInterval = extractDateInterval(eventDate);
    	    if (eventDateInterval.toDuration().getStandardDays()<=31l) { 
    	    	result = true;
    	    }
    	}
    	return result;
    }    
    
    /**
     * Test if an event date specifies a duration of one year or less.
     * 
     * Provides: EVENTDATE_PRECISON_YEAR_OR_BETTER
     * 
     * @param eventDate to test.
     * @return true if duration is 365 days or less.
     */    
    public static boolean specificToYearScale(String eventDate) { 
    	boolean result = false;
    	if (!isEmpty(eventDate)) { 
    	    Interval eventDateInterval = extractDateInterval(eventDate);
    	    if (eventDateInterval.toDuration().getStandardDays()<=365l) { 
    	    	result = true;
    	    }
    	}
    	return result;
    }      
    
    /**
     * Test if an event date specifies a duration of 10 years or less.
     * 
     * @param eventDate to test.
     * @return true if duration is 10 years or or less.
     */    
    public static boolean specificToDecadeScale(String eventDate) { 
    	boolean result = false;
    	if (!isEmpty(eventDate)) { 
    	    Interval eventDateInterval = extractDateInterval(eventDate);
    	    if (eventDateInterval.toDuration().getStandardDays()<=3650l) { 
    	    	result = true;
    	    }
    	}
    	return result;
    }        
    
    /**
     * Measure the duration of an event date in seconds, when a time is 
     * specified, ceiling to the nearest second, when a time is not 
     * specified, from the date midnight at the beginning of a date 
     * range to the last second of the day at the end of the range.  This 
     * may return one second less than your expectation for the number 
     * of seconds in the interval (e.g. 86399 seconds for the duration of 
     * a day specified as 1980-01-01.
     * 
     * Provides: EVENT_DATE_DURATION_SECONDS
     * 
     * Suggested by Alex Thompson in a TDWG data quality task group call.
     * 
     * @param eventDate to test.
     * @return the duration of eventDate in seconds.
     */    
    public static long measureDurationSeconds(String eventDate) { 
    	long result = 0l;
    	if (!isEmpty(eventDate)) { 
    		Interval eventDateInterval = DateUtils.extractInterval(eventDate);
    		logger.debug(eventDateInterval.toDuration().getStandardDays());
    		logger.debug(eventDateInterval);
    		long mills = eventDateInterval.toDurationMillis();
    		result = (long)Math.ceil(mills/1000l);
    	}
    	return result;
    }    
    
    /**
     * Given an instant, return the time within one day that it represents as a string.
     * 
     * @param instant to obtain time from.
     * @return string in the form hh:mm:ss.sssZ or an empty string if instant is null.  
     */
    protected static String instantToStringTime(Instant instant) {
    	String result = "";
    	if (instant!=null) { 
    		StringBuffer time = new StringBuffer();
    		time.append(String.format("%02d",instant.get(DateTimeFieldType.hourOfDay())));
    		time.append(":").append(String.format("%02d",instant.get(DateTimeFieldType.minuteOfHour())));
    		time.append(":").append(String.format("%02d",instant.get(DateTimeFieldType.secondOfMinute())));
    		time.append(".").append(String.format("%03d",instant.get(DateTimeFieldType.millisOfSecond())));
    		String timeZone = instant.getZone().getID();
    		if (timeZone.equals("UTC")) { 
    		    time.append("Z"); 
    		} else { 
    			time.append(timeZone);
    		}
    		result = time.toString();
    	}
    	return result;
    }
    
    /**
     * Perform transformations to make more cases of textual months parsable by
     * joda. Transform roman numerals into months. For example, Sep. parses as 
     * month=09, but Sept. doesn't.  Likewise settembre parses as month=09, but
     * Settembre doesn't as joda expects months in Italian to be in lower case.
     * 
     * @param verbatimEventDate string containing a dwc:verbatimEventDate
     * @return String containing verbatimEventDate with transformations applied.
     */
    public static String cleanMonth(String verbatimEventDate) {
    	String cleaned = verbatimEventDate;
    	if (!isEmpty(verbatimEventDate)) { 
    		cleaned = cleaned.replace("Sept.", "Sep.");
    		cleaned = cleaned.replace("Sept ", "Sep. ");
    		cleaned = cleaned.replace("Sept,", "Sep.,");
    		cleaned = cleaned.replace("  ", " ").trim();
    		cleaned = cleaned.replace(" ,", ",");
    		cleaned = cleaned.replace(" - ", "-");
    		cleaned = cleaned.replace("- ", "-");
    		cleaned = cleaned.replace(" -", "-");
    		// Strip off a trailing period after a final year
    		if (cleaned.matches("^.*[0-9]{4}[.]$")) { 
    			cleaned = cleaned.replaceAll("[.]$", "");
    		}

    		// Joda date time parsing as used here, is case sensitive for months.
    		// Put cases of alternative spellings, missing accents, and capitalization into
    		// a form that Joda will parse.

    		cleaned = cleaned.replace("DECEMBER", "December");
    		cleaned = cleaned.replace("NOVEMBER", "November");
    		cleaned = cleaned.replace("OCTOBER", "October");
    		cleaned = cleaned.replace("SEPTEMBER", "September");
    		cleaned = cleaned.replace("AUGUST", "August");
    		cleaned = cleaned.replace("JULY", "July");
    		cleaned = cleaned.replace("JUNE", "June");
    		cleaned = cleaned.replace("MAY", "May");
    		cleaned = cleaned.replace("APRIL", "April");
    		cleaned = cleaned.replace("MARCH", "March");
    		cleaned = cleaned.replace("FEBRUARY", "February");
    		cleaned = cleaned.replace("JANUARY", "January");	    		
    		
    		// Italian months are lower case, if capitalized, skip a step and go right to english.
    		cleaned = cleaned.replace("Dicembre", "December");
    		cleaned = cleaned.replace("Novembre", "November");
    		cleaned = cleaned.replace("Ottobre", "October");
    		cleaned = cleaned.replace("Settembre", "September");
    		cleaned = cleaned.replace("Agosto", "August");
    		cleaned = cleaned.replace("Luglio", "July");
    		cleaned = cleaned.replace("Giugno", "June");
    		cleaned = cleaned.replace("Maggio", "May");
    		cleaned = cleaned.replace("Aprile", "April");
    		cleaned = cleaned.replace("Marzo", "March");
    		cleaned = cleaned.replace("Febbraio", "February");
    		cleaned = cleaned.replace("Gennaio", "January");			
    		// likewise french, also handle omitted accents
    		cleaned = cleaned.replace("Janvier", "January");
    		cleaned = cleaned.replace("Février", "February");
    		cleaned = cleaned.replace("Fevrier", "February");
    		cleaned = cleaned.replace("fevrier", "February");
    		cleaned = cleaned.replace("Mars", "March");
    		cleaned = cleaned.replace("Avril", "April");
    		cleaned = cleaned.replace("Mai", "May");
    		cleaned = cleaned.replace("Juin", "June");
    		cleaned = cleaned.replace("Juillet", "July");
    		cleaned = cleaned.replace("Août", "August");
    		cleaned = cleaned.replace("Aout", "August");
    		cleaned = cleaned.replace("aout", "August");
    		cleaned = cleaned.replace("Septembre", "September");
    		cleaned = cleaned.replace("Octobre", "October");
    		cleaned = cleaned.replace("Novembre", "November");
    		cleaned = cleaned.replace("Décembre", "December");
    		cleaned = cleaned.replace("Decembre", "December");
    		cleaned = cleaned.replace("decembre", "December");
    		// likewise spanish
    		cleaned = cleaned.replace("Enero", "January");
    		cleaned = cleaned.replace("Febrero", "February");
    		cleaned = cleaned.replace("Marzo", "March");
    		cleaned = cleaned.replace("Abril", "April");
    		cleaned = cleaned.replace("Mayo", "May");
    		cleaned = cleaned.replace("Junio", "June");
    		cleaned = cleaned.replace("Julio", "July");
    		cleaned = cleaned.replace("Agosto", "August");
    		cleaned = cleaned.replace("Septiembre", "September");
    		cleaned = cleaned.replace("Setiembre", "September");  // alternative spelling
    		cleaned = cleaned.replace("setiembre", "September");
    		cleaned = cleaned.replace("Octubre", "October");
    		cleaned = cleaned.replace("Noviembre", "November");
    		cleaned = cleaned.replace("Diciembre", "December");
    		
    		// Translate roman numerals to months, with care not to modify month names.
    		
    		cleaned = cleaned.replace(".i.", ".January.");
    		cleaned = cleaned.replace("/i/", "/January/");
    		cleaned = cleaned.replace(" i ", " January ");
    		cleaned = cleaned.replace(".ii.", ".February.");
    		cleaned = cleaned.replace("/ii/", "/February/");
    		cleaned = cleaned.replace(" ii ", " February ");	
    		cleaned = cleaned.replace(".v.", ".May.");
    		cleaned = cleaned.replace("/v/", "/May/");
    		cleaned = cleaned.replace(" v ", " May ");
    		cleaned = cleaned.replace(".iv.", ".April.");
    		cleaned = cleaned.replace("/iv/", "/April/");
    		cleaned = cleaned.replace(" iv ", " April ");	
    		cleaned = cleaned.replace(".vi.", ".June.");
    		cleaned = cleaned.replace("/vi/", "/June/");
    		cleaned = cleaned.replace(" vi ", " June ");	
    		cleaned = cleaned.replace(".x.", ".October.");
    		cleaned = cleaned.replace("/x/", "/October/");
    		cleaned = cleaned.replace(" x ", " October ");
    		cleaned = cleaned.replace(".ix.", ".September.");
    		cleaned = cleaned.replace("/ix/", "/September/");
    		cleaned = cleaned.replace(" ix ", " September ");	
    		cleaned = cleaned.replace("/xi/", "/November/");
    		cleaned = cleaned.replace(".xi.", ".November.");
    		cleaned = cleaned.replace(" xi ", " November ");		
    		cleaned = cleaned.replace(",i,", ".January.");
    		cleaned = cleaned.replace("-i-", " January ");
    		cleaned = cleaned.replace(",ii,", ".February.");
    		cleaned = cleaned.replace("-ii-", " February ");	
    		cleaned = cleaned.replace(",v,", ".May.");
    		cleaned = cleaned.replace("-v-", " May ");
    		cleaned = cleaned.replace(",iv,", ".April.");
    		cleaned = cleaned.replace("-iv-", " April ");	
    		cleaned = cleaned.replace(",vi,", ".June.");
    		cleaned = cleaned.replace("-vi-", " June ");	
    		cleaned = cleaned.replace(",x,", ".October.");
    		cleaned = cleaned.replace("-x-", " October ");
    		cleaned = cleaned.replace(",ix,", ".September.");
    		cleaned = cleaned.replace("-ix-", " September ");	
    		cleaned = cleaned.replace(",xi,", ".November.");
    		cleaned = cleaned.replace("-xi-", " November ");		
    		
    		// many cases below here are potentially problematic and should probably be 
    		// replaced by regular expressions instead of simple replace (order matters).
    		cleaned = cleaned.replace("XII", "December");
    		cleaned = cleaned.replace("xii", "December");
    		cleaned = cleaned.replace("XI", "November");
    		cleaned = cleaned.replace("xi", "November");
    		cleaned = cleaned.replace("IX", "September");
    		cleaned = cleaned.replace("X", "October");
    		cleaned = cleaned.replace("VIII", "August");
    		cleaned = cleaned.replace("viii", "August");
    		cleaned = cleaned.replace("VII", "July");
    		cleaned = cleaned.replace("vii", "July");
    		cleaned = cleaned.replace("VI", "June");
    		if (!cleaned.contains("janvier")) { 
    		    cleaned = cleaned.replace("vi", "June");
    		}
    		cleaned = cleaned.replace("IV", "April");
    		cleaned = cleaned.replace("iv", "April");
    		cleaned = cleaned.replace("V", "May");
    		cleaned = cleaned.replace("III", "March");
    		cleaned = cleaned.replace("iii", "March");
    		cleaned = cleaned.replace("II", "February");
    		cleaned = cleaned.replace("ii", "February");
    		cleaned = cleaned.replace("I", "January");
    		cleaned = cleaned.replace(".vi", ".June");
    		cleaned = cleaned.replace(".ix", ".September");
    		cleaned = cleaned.replace(".i", ".January");
    		cleaned = cleaned.replace(".v", ".May");
    		cleaned = cleaned.replace(".x", ".October");
    		cleaned = cleaned.replace(" vi", " June");
    		cleaned = cleaned.replace(" ix", " September");
    		cleaned = cleaned.replace(" i", " January");
    		cleaned = cleaned.replace(" v", " May");
    		cleaned = cleaned.replace(" x", " October");

    	}
    	return cleaned;
    }
    
    /**
     * Test to see if an integer is in the range of integers that can be days of the month.
     * 
     * @param day the value to test.
     * @return true if day is in the range 1 to 31 inclusive, false otherwise.
     */
    public static boolean isDayInRange(int day) { 
    	boolean result = false;
    	if (day>0 && day <32) { result = true; } 
    	return result;
    }
    

    
    /**
     * Test to see if an integer is in the range of integers that can be months of the year.
     * 
     * @param month the value to test
     * @return true if month is in the range 1 to 12 inclusive, false otherwise.
     */
    public static boolean isMonthInRange(int month) { 
    	boolean result = false;
    	if (month>0 && month <13) { result = true; } 
    	return result;
    }    
    
    /**
     * Given a start date and end date (which could be in verbatim forms) construct an 
     * event date representing the range from start of start date to end of end date.
     * If either startDate or endDate cannot be interpreted as a date, that bit will be
     * omitted from the result.  If no dates are provided or none can be recognized, 
     * returns null.
     * 
     * @param startDate to concatenate with end date. 
     * @param endDate to add after start date.
     * 
     * @return eventDate representing startDate to endDate, null if no dates were found.
     */
	public static String createEventDateFromStartEnd(String startDate, String endDate) {
		String result = null;
        boolean startIsRange = false; 
        String separator = "";
		
		if (!DateUtils.isEmpty(startDate)) {
			separator = "/";
			if (DateUtils.eventDateValid(startDate)) { 
			    result = startDate;
			    if (DateUtils.isRange(result)) { 
			    	startIsRange = true;
			    }
			} else { 
				EventResult startResult = DateUtils.extractDateFromVerbatimER(startDate);
				if (startResult.getResultState().equals(EventResult.EventQCResultState.DATE)) { 
					result = startResult.getResult();
				}
				if (startResult.getResultState().equals(EventResult.EventQCResultState.RANGE)) {
					result = startResult.getResult();
					startIsRange = true;
				}
			}
		} 
		
		boolean startDateFound = !DateUtils.isEmpty(result);
		
		logger.debug(result);
		
		if (!DateUtils.isEmpty(endDate)) { 
			if (startIsRange && result.contains("/")) {
				// we need just the start of the range
				result = result.substring(0, result.indexOf('/'));
			} 
			logger.debug(result);

			if (DateUtils.eventDateValid(endDate)) { 
				if (startDateFound) { 
					if (DateUtils.isRange(endDate) && endDate.contains("/")) { 
						endDate = endDate.substring(endDate.indexOf('/')+1);
					}
				   result = result + separator + endDate;
				} else { 
					result = endDate;
				}
				logger.debug(result);
			} else { 
				String endBit = "";
				EventResult endResult = DateUtils.extractDateFromVerbatimER(endDate);
				logger.debug(endResult.getResultState());
				if (endResult.getResultState().equals(EventResult.EventQCResultState.DATE)) { 
					endBit = endResult.getResult();
				}
				if (endResult.getResultState().equals(EventResult.EventQCResultState.RANGE)) {
					endBit = endResult.getResult();
					if (endBit.contains("/")) { 
						endBit = endBit.substring(endBit.indexOf('/')+1);
					}
				}
				if (!DateUtils.isEmpty(endBit)) { 
					if (startDateFound) { 
					    result = result + separator + endBit;
					} else { 
						result = endBit;
					}
				}
			}
		}
		
		if (!DateUtils.isEmpty(result) && !DateUtils.eventDateValid(result)) {
			logger.error(result);
			result = null;
		}
		
		return result;
	}
 
	/**
	 * Given an event date (which may represent a date range), check to see if the 
	 * eventDate contains a leap day.  Returns false if provided a null or invalid 
	 * eventDate value.
	 * 
	 * @param eventDate to check for a leap day
	 * @return true if a leap day is present in the eventDate range, otherwise false.
	 */
	public static boolean includesLeapDay(String eventDate) {
		boolean result = false;
		if (!DateUtils.isEmpty(eventDate) && DateUtils.eventDateValid(eventDate)) { 
			Interval interval = extractInterval(eventDate);
			String startYear = Integer.toString(interval.getStart().getYear()).trim();
			String endYear = Integer.toString(interval.getEnd().getYear()).trim();
			String leapDay = startYear + "-02-29";
			logger.debug(leapDay);
			if (DateUtils.eventDateValid(leapDay)) { 
				if (interval.contains(DateUtils.extractInterval(leapDay))) { 
					result = true;
				}
			}
			if (!endYear.equals(startYear)) { 
				leapDay = endYear + "-02-29";
				logger.debug(leapDay);
				if (DateUtils.eventDateValid(leapDay)) { 
					if (interval.contains(DateUtils.extractInterval(leapDay))) { 
						result = true;
					}
				}				
			}
			// TODO: Support ranges of more than one year.
		}
		return result;
	}
	
    /**
     * Run from the command line, arguments -f to specify a file, -m to show matches. 
     * Converts dates in a specified input file from verbatim form to format expected by dwc:eventDate.
     * 
     * @param args -f filename to check a file containing a list of dates, one per line.
     *    -m to show matched dates and their interpretations otherwise lists non-matched lines.  
     *    -a to show all lines, matched or not with their interpretations.
     */
    public static void main(String[] args) { 
        try {
        	File datesFile = null;
        	try { 
        	   URL datesURI = DateUtils.class.getResource("/example_dates.csv");
        	   datesFile = new File(datesURI.toURI());
        	} catch (NullPointerException e){ 
    			logger.error(e.getMessage());
    		} catch (URISyntaxException e) {
    			logger.error(e.getMessage());
        	}
        	if (args[0]!=null && args[0].toLowerCase().equals("-f")) {
        		if (args[1]!=null) { 
        			datesFile = new File(args[1]);
        		}
            }
        	boolean showMatches = false;
        	boolean showAll = false;
        	for (int i=0; i<args.length; i++) {
        		if (args[i].equals("-m")) { showMatches = true; } 
        		if (args[i].equals("-a")) { showAll = true; } 
        	}
			BufferedReader reader = new BufferedReader(new FileReader(datesFile));
			String line = null;
			int unmatched = 0;
			int matched = 0;
			while ((line=reader.readLine())!=null) {
				EventResult result = DateUtils.extractDateFromVerbatimER(line.trim());
				if (result==null || result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
					if (!showMatches && !showAll) {
					   System.out.println(line);
					}
					if (showAll) { 
					   System.out.println(line +  "\t" + result.getResultState());
					}
					unmatched++;
				} else { 
					matched++;
				   if (showMatches || showAll) { 
					   System.out.println(line + "\t" + result.getResultState() + "\t" + result.getResult());
				   }
				}
			}
			reader.close();
			System.out.println("Unmatched lines: " + unmatched);
			System.out.println("Matched lines: " + matched);
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());;
			System.out.println(e.getMessage());

		}
        
    }
    
    
    
}
