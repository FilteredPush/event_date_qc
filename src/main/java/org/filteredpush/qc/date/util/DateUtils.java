/** DateUtils.java
 * 
 * Copyright 2015-2017 President and Fellows of Harvard College
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
package org.filteredpush.qc.date.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.filteredpush.qc.date.EmptyDateException;
import org.filteredpush.qc.date.EventResult;
import org.filteredpush.qc.date.LocalDateInterval;
import org.filteredpush.qc.date.EventResult.EventQCResultState;
import org.filteredpush.qc.date.LocalDateTimeInterval;
import org.filteredpush.qc.date.TimeExtractionException;

import java.time.Instant;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;

/**
 * Utility functions for working with DarwinCore date concepts.
 *  
 *  Includes:
 *  
 *  dayMonthTransposition(String month, String day) 
 *  extractDateFromVerbatimER(String verbatimEventDate)
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
 *  Note: extractDate functions that return a Map&lt;String,String&gt;
 *  (for example extractDateFromVerbatim()) are deprecated and should be replaced
 *  by the versions that return an EventResult object, e.g. EventResult extractDateFromVerbatimER(). 
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
	 * Test to see whether an eventDate contains a string in an expected ISO format
	 * which is represents an actual date or date range.
	 * 
	 * @param eventDate string to test for expected format.
	 * @return true if eventDate is in an expected format for eventDate, otherwise false.
	 */
	public static boolean eventDateValid(String eventDate) {
		boolean result = false; 
		logger.debug(eventDate);
		if (!DateUtils.isEmpty(eventDate)) { 
			if (!eventDate.contains("/") && extractDate(eventDate)!=null) { 
				result = true;
				logger.debug(eventDate);    		
			} else { 
				LocalDateInterval interval = extractInterval(eventDate);
				if (interval!=null) { 
					logger.debug(interval.getStartDate());
					logger.debug(interval.getEndDate());
					if (interval.getStartDate().isBefore(interval.getEndDate()) 
							|| interval.getStartDate().isEqual(interval.getEndDate())) { 
						result = true;
					}
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
		
		logger.debug(verbatimEventDate);
		logger.debug(startDayOfYear);
		logger.debug(endDayOfYear);
		logger.debug(year);
		logger.debug(month);
		logger.debug(day);

		if (verbatimEventDate!=null && verbatimEventDate.trim().length()>0) { 
			EventResult verbatim = extractDateToDayFromVerbatimER(verbatimEventDate, DateUtils.YEAR_BEFORE_SUSPECT); 
			if (!verbatim.getResultState().equals(EventQCResultState.NOT_RUN)) { 
				if (verbatim.getResultState().equals(EventQCResultState.DATE)) { 
					result = verbatim.getResult();
				}
				if (verbatim.getResultState().equals(EventQCResultState.AMBIGUOUS)) { 
					result = verbatim.getResult();
				}		
				if (verbatim.getResultState().equals(EventQCResultState.RANGE)) { 
					result = verbatim.getResult();
				}					
				if (verbatim.getResultState().equals(EventQCResultState.SUSPECT)) { 
					result = verbatim.getResult();
				}					
			}
			logger.debug(result);
		}
		if (year!=null && year.matches("[0-9]{4}") && isEmpty(month) && isEmpty(day) && isEmpty(startDayOfYear)) { 
		    result = year;
			logger.debug(result);
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
			    EventResult verbatim = extractDateToDayFromVerbatimER(assembly.toString(), DateUtils.YEAR_BEFORE_SUSPECT) ;
			    logger.debug(verbatim.getResultState().toString());
			    logger.debug(verbatim.getResult());
				if (verbatim.getResultState().equals(EventQCResultState.DATE) || verbatim.getResultState().equals(EventQCResultState.RANGE)) { 
					result = verbatim.getResult();
				}
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
			logger.debug(result);
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
			    EventResult verbatim = extractDateToDayFromVerbatimER(assembly.toString(), DateUtils.YEAR_BEFORE_SUSPECT) ;
			    logger.debug(verbatim.getResultState().toString());
			    logger.debug(verbatim.getResult());
				if (verbatim.getResultState().equals(EventQCResultState.DATE)
						|| verbatim.getResultState().equals(EventQCResultState.RANGE)
						|| verbatim.getResultState().equals(EventQCResultState.SUSPECT)
				) { 
					result = verbatim.getResult();
				}
			} catch (Exception e) {
				logger.debug(e.getMessage());
			}
			logger.debug(result);
		}			
		if (!DateUtils.isEmpty(year) && 
			DateUtils.isEmpty(month) &&
			DateUtils.isEmpty(day) &&
			DateUtils.isEmpty(verbatimEventDate) &&
			!DateUtils.isEmpty(startDayOfYear) &&
			!DateUtils.isEmpty(endDayOfYear)
			)
		{
			try { 
				Integer yearInt = Integer.parseInt(year.trim());
				Integer startInt = Integer.parseInt(startDayOfYear.trim());
				Integer endInt = Integer.parseInt(endDayOfYear.trim());
				String toTest = String.format("%04d", yearInt).concat("-");
				toTest = toTest.concat(String.format("%03d", startInt));
				toTest = toTest.concat("/");
				toTest = toTest.concat(String.format("%04d", yearInt)).concat("-");
				toTest = toTest.concat(String.format("%03d", endInt));
				logger.debug(toTest);

				result = new LocalDateInterval(toTest).toString();
			} catch (NumberFormatException | DateTimeParseException | EmptyDateException e) {
				logger.debug(e.getMessage());
			}
			logger.debug(result);
		}
		if (year!=null && year.matches("[0-9]{4}") && month!=null && month.matches("[0-9]{1,2}") &&( day==null || day.trim().length()==0 )) {  
		    result = String.format("%04d",Integer.parseInt(year)) + "-" + String.format("%02d",Integer.parseInt(month));
			logger.debug(result);
		}
		if (year!=null && year.matches("[0-9]{4}") && month!=null && month.matches("[0-9]{1,2}") && day!=null && day.matches("[0-9]{1,2}")) {  
		    result = String.format("%04d",Integer.parseInt(year)) + "-" + 
                     String.format("%02d",Integer.parseInt(month)) + "-" + 
                     String.format("%02d",Integer.parseInt(day));
			logger.debug(result);
		}
		if (!DateUtils.isEmpty(result)) { 
			logger.debug(result);
			// confirm the result and make sure it is represented in consistent form.
			try {
				result = new LocalDateInterval(result).toString();
				logger.debug(result);
			} catch (DateTimeParseException | EmptyDateException e) {
				result = null;
				logger.error(e.getMessage());
			}
		}
		return result;
	}
	
	/**
	 * Attempt to extract a date or date range in standard format from a provided verbatim 
	 * date string.  Does not make an assumption about ambiguous mm/dd/yyyy dates, but returns
	 * the range of possible dates and a result state asserting that the result is ambiguous.  
	 * Assumes that any date prior to DateUtils.YEAR_BEFORE_SUSPECT is a suspect date. 
	 * 
	 * Provides: EVENTDATE_FILLED_IN_FROM_VERBATIM
	 * 
	 * Example use illustrated with unit test:
	 <pre> 
	    result = DateUtils.extractDateFromVerbatimER("April 1871");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1871-04", result.getResult());   // String suitable for dwc:eventDate.
    	logger.debug(result.getComment());    // Provenance behind result assertion.
     </pre>  
	 * 
	 * @param verbatimEventDate a string containing a verbatim event date.
	 * @return a result object with a resultState and the extracted value in result.
	 * 
	 * @see DateUtils#extractDateFromVerbatimER(String, int, Boolean) to
	 *    override default assumptions.  
	 * @see DateUtils#YEAR_BEFORE_SUSPECT for default point before which dates are considered suspect.
	 * 
	 */
	public static EventResult extractDateFromVerbatimER(String verbatimEventDate) {
		return extractDateFromVerbatimER(verbatimEventDate, DateUtils.YEAR_BEFORE_SUSPECT, null);
	}	
	
	/**
	 * Attempt to extract a date or date range in standard format from a provided verbatim 
	 * date string.  
	 * 
	 * @param verbatimEventDate a string containing a verbatim event date.
	 * @return a map with result and resultState as keys
	 * 
	 * @deprecated to be removed
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
				   LocalDateInterval parseDate = extractDateInterval(dateRange);
				   logger.debug(parseDate);
				   String resultDate =  parseDate.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + parseDate.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
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
	 * @deprecated to be removed, does not handle enough cases.
	 * @see DateUtils#extractDateToDayFromVerbatimER(String, int) replacement method
	 */
	public static Map<String,String> extractDateToDayFromVerbatim(String verbatimEventDate, int yearsBeforeSuspect) {
		Map<String,String> result =  extractDateFromVerbatim(verbatimEventDate, yearsBeforeSuspect);
		if (result.size()>0 && result.get("resultState").equals("range")) {
			String dateRange = result.get("result");
			try { 
				   LocalDateInterval parseDate = extractDateInterval(dateRange);
				   logger.debug(parseDate.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) );
				   logger.debug(parseDate.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) );
				   String resultDate =  parseDate.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + parseDate.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) ;
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
	 * 
	 * @deprecated to be removed
	 * @see DateUtils#extractDateFromVerbatimER(String, int, Boolean) replacement method.
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
		if (verbatimEventDate!=null && verbatimEventDate.startsWith("[") &&  
		    verbatimEventDate.endsWith("]")
		    ) { 
			verbatimEventDate = verbatimEventDate.substring(1);
			verbatimEventDate = verbatimEventDate.substring(0,verbatimEventDate.length()-1);
		}
		
		if (verbatimEventDate!=null && verbatimEventDate.matches(".*\\[[0-9]+\\].*")) { 
			verbatimEventDate = verbatimEventDate.replace("[", "").replace("]", "");
		}
		
		// Strip off leading and trailing quotation marks
		if (verbatimEventDate!=null && verbatimEventDate.startsWith("\"") &&  
		    verbatimEventDate!=null && verbatimEventDate.endsWith("\"")) { 
			verbatimEventDate = verbatimEventDate.substring(1,verbatimEventDate.length()-1);
		}
		
		// strip off leading and trailing whitespace
		if (verbatimEventDate!=null && (verbatimEventDate.startsWith(" ") || verbatimEventDate.endsWith(" "))) { 
			verbatimEventDate = verbatimEventDate.trim();
		}
		// strip off trailing period after number
		if (verbatimEventDate!=null && 
				verbatimEventDate.endsWith(".") && 
				verbatimEventDate.matches(".*[0-9]\\.$")) { 
			verbatimEventDate = verbatimEventDate.substring(0,verbatimEventDate.length()-1);
			logger.debug(verbatimEventDate);
		}		
		
		
		// Stop before doing work if provided verbatim string is null.
		if (isEmpty(verbatimEventDate)) { 
			return result;
		}
		

		
		if (verbatimEventDate.matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}/[0-9]{4}[-][0-9]{2}[-][0-9]{2}$")) {
			// if verbatim date is a ISO formatted range with identical first and last dates (/), use just one.
			// Example: 1982-12-11/1982-12-11  changed to 1982-12-11
			String[] bits = verbatimEventDate.split("/");
			if (bits.length==2 && bits[0].equals(bits[1])) { 
				verbatimEventDate = bits[0];
			}
		}
		if (verbatimEventDate.matches("^[0-9]{4}[/][0-9]{2}[/][0-9]{2}-[0-9]{4}[/][0-9]{2}[/][0-9]{2}$")) {
			// if verbatim date is a range with identical first and last dates (-), use just one.
			// Example: 1982/12/11-1982/12/11  changed to 1982/12/11
			String[] bits = verbatimEventDate.split("-");
			if (bits.length==2 && bits[0].equals(bits[1])) { 
				verbatimEventDate = bits[0];
			}
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
		if (verbatimEventDate.matches("^[0-9]{4}[-]([0-9]{1,2}|[A-Za-z]+)[-][0-9]{1,2}.*")) {
			// Both separators are the same.
			// Example 1982-02-05
			// Example 1982-Feb-05
			// Example 1982-02-05
			// Example 1982-02-05T05:03:06

			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ISO_DATE)
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ISO_DATE_TIME)
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ISO_DATE)
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'M'-'d"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'LLL'-'dd"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'LLLL'-'dd"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					LocalDate startDateBit = LocalDate.parse(verbatimEventDate, i.next());
					resultDate = startDateBit.format(DateTimeFormatter.ISO_LOCAL_DATE);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}
		if (verbatimEventDate.matches("^[0-9]{4}[/]([0-9]{1,2}|[A-Za-z]+)[/][0-9]{1,2}$")) {
			// Both separators are the same.
			// Example 1982/02/05
			// Example 1982/Feb/05
			// Example 1982/02/05T05:03:06
				
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'/'M'/'d"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuu'/'LLL'/'d"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					LocalDate startDateBit = LocalDate.parse(verbatimEventDate, i.next());
					resultDate = startDateBit.format(DateTimeFormatter.ISO_LOCAL_DATE);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
				
		}
		if (verbatimEventDate.matches("^[0-9]{4}[/]([0-9]{1,2}|[A-Za-z]+)[/][0-9]{1,2}T.*$")) {
			// Both separators are the same, date and time
			// Example 1982/02/05T05:03:06
			// replace('/','-') below to parse with yyyy-MM-dd...
			
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ISO_ZONED_DATE_TIME)
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					LocalDate startDateBit = LocalDate.parse(verbatimEventDate.replace("/", "-"), i.next());
					resultDate = startDateBit.format(DateTimeFormatter.ISO_LOCAL_DATE);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[0-9]{4}-([2-3][0-9]|[1][3-9])-([1-9]|[0][0-9]|[1][0-2])$")) 
		{
			// Example 1982-22-05
			// Fits pattern yyyy-dd-mm, translate as such
			try {
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'dd'-'MM"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT);
	    		LocalDate parseDate = LocalDate.parse(verbatimEventDate, formatter);
				resultDate = parseDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDate);
				logger.debug(result.getResult());
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}
		logger.debug(verbatimEventDate);
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[0-9]{4}[.,]([2-3][0-9]|[1][3-9])[.,]([0-9]|[0][0-9]|[1][0-2])$")) 
		{
			// Example 1982.22.05
			// Example 1982,22,5
			// Fits pattern yyyy-dd-mm, translate as such
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'.'dd'.'MM"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'.'dd'.'M"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			String commasReplaced =  verbatimEventDate.replace(",", ".");
			logger.debug(commasReplaced);

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					LocalDate startDateBit = LocalDate.parse(commasReplaced, i.next());
					resultDate = startDateBit.format(DateTimeFormatter.ISO_LOCAL_DATE);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
					matched = true;
					logger.debug(result.getResult());
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[0-9]{4}[.,][0-9]{1,2}[.,][0-9]{1,2}$")) 
		{
			// Example 1982.02.05
			// Example 1982,02,05
			// Cases where the 1 digit numbers are both smaller than 12 are treated as ambiguous
			// Cases where the 1 digit numbers are both smaller than 12 are treated as ambiguous
			// Cases where 1 digit numbers are zero padded and numbers are both smaller than 12 are treated as a date in the form yyyy-mm-dd
			String resultDateMD = null;
			String resultDateDM = null;
			LocalDate parseDate1 = null;
			LocalDate parseDate2 = null;
			logger.debug(verbatimEventDate);
			
			try {
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'.'M'.'d"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT);
				String commasReplaced =  verbatimEventDate.replace(",", ".");
	    		parseDate1 = LocalDate.parse(commasReplaced, formatter);
				resultDateMD = parseDate1.format(DateTimeFormatter.ISO_LOCAL_DATE);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
			try { 
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'.'d'.'M"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT);
				String commasReplaced =  verbatimEventDate.replace(",", ".");
	    		parseDate2 = LocalDate.parse(commasReplaced, formatter);
				resultDateDM = parseDate2.format(DateTimeFormatter.ISO_LOCAL_DATE);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
			if (resultDateMD!=null && resultDateDM==null) {
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDateMD);
				logger.debug(result.getResult());
			} else if (resultDateMD==null && resultDateDM!=null) { 
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDateDM);
				logger.debug(result.getResult());
			} else if (resultDateMD!=null && resultDateDM!=null) { 
				if (resultDateMD.equals(resultDateDM)) { 
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDateDM);
					logger.debug(result.getResult());
				} else { 
					if (verbatimEventDate.replace(",", ".").matches("^[0-9]{4}\\.[0-9]{2}\\.[0-9]{1,2}$")) {
						// treat a yyyy-mm-dd
						result.setResultState(EventResult.EventQCResultState.DATE);
				        result.setResult(resultDateMD);
				        logger.debug(result.getResult());
					} else { 
						result.setResultState(EventResult.EventQCResultState.AMBIGUOUS);
						if (parseDate1.isBefore(parseDate2)) { 
							result.setResult(resultDateMD + "/" + resultDateDM);
						} else { 
							result.setResult(resultDateDM + "/" + resultDateMD);
						}
				        logger.debug(result.getResult());
					}
				    logger.debug(result.getResult());
				}
			} 			
			
		}
				
		if (verbatimEventDate.matches("^[0-9]{1,2}[-/ ][0-9]{4}$")) { 
			// Example 02/1982
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'M'-'uuuu"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'M' 'uuuu"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					String dayAppended = "01-" + verbatimEventDate.replace("/", "-");
					LocalDate startDateBit = LocalDate.parse(dayAppended, i.next());
					resultDate = startDateBit.format(DateTimeFormatter.ofPattern("yyyy-MM"));
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					if (e.getMessage().contains("Unable")) { e.printStackTrace(); }
					logger.debug(e.getMessage());
				}
			}
		}		
		
	
		if (verbatimEventDate.matches("^[0-9]{4}年[0-9]{1,2}月[0-9]{1,2}[日号]$")) { 
			// Example: 1972年03月25日
			
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'年'M'月'd'日'"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'年'M'月'd'号'"))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					LocalDate startDateBit = LocalDate.parse(verbatimEventDate, i.next());
					resultDate = startDateBit.format(DateTimeFormatter.ISO_LOCAL_DATE);
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					if (e.getMessage().contains("Unable")) { e.printStackTrace(); }
					logger.debug(e.getMessage());
				}
			}
		}
		
		if (verbatimEventDate.matches("^[0-9]{4}[-][0-9]{3}/[0-9]{4}[-][0-9]{3}$")) { 
			// Example: 1982-145/1982-200
			try { 
				String[] bits = verbatimEventDate.split("/");
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ISO_ORDINAL_DATE)
						.toFormatter().withResolverStyle(ResolverStyle.STRICT);
				LocalDate parseStartDate = LocalDate.parse(bits[0],formatter);
				LocalDate parseEndDate = LocalDate.parse(bits[1],formatter);
				if (parseStartDate.equals(parseEndDate)) { 
					resultDate =  parseStartDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
					result.setResultState(EventResult.EventQCResultState.DATE);
				} else { 
					resultDate =  parseStartDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + parseEndDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
					result.setResultState(EventResult.EventQCResultState.RANGE);
				}
				logger.debug(resultDate);
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
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ISO_LOCAL_DATE)
						.toFormatter().withResolverStyle(ResolverStyle.STRICT);
				LocalDate parseDate = LocalDate.parse(verbatimEventDate + "-01-01" ,formatter);
				resultDate = parseDate.format(DateTimeFormatter.ofPattern("yyyy"));
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				yearsBeforeSuspect < 0 &&
				verbatimEventDate.matches("^-[0-9]{4}$")) { 
			// Example: -2000 
			try { 
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("Guuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT);
				LocalDate parseDate = LocalDate.parse(verbatimEventDate.replace("-", "BC") + "-01-01" ,formatter);
				resultDate = parseDate.format(DateTimeFormatter.ofPattern("yyyy G"));
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}
						
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
				verbatimEventDate.matches("^[12][0-9]{1}00[']{0,1}s$")) {
			// Example: 1900s 
			try { 
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ISO_LOCAL_DATE)
						.toFormatter().withResolverStyle(ResolverStyle.STRICT);
				String verbatimEventDateDelta = verbatimEventDate.replace("'", "");
				verbatimEventDateDelta = verbatimEventDateDelta.replace("s", "-01-01");
				LocalDate parseDate = LocalDate.parse(verbatimEventDateDelta,formatter);
				LocalDate endDate = parseDate.plusYears(100).minusDays(1);
				resultDate = parseDate.format(DateTimeFormatter.ofPattern("yyyy")) + "-01-01/" + endDate.format(DateTimeFormatter.ofPattern("yyyy")) + "-12-31";
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
				verbatimEventDate.matches("^[12][0-9]{2}0[']{0,1}s$")) {
			// Example: 1970s 
			try { 
				String verbatimEventDateDelta = verbatimEventDate.replace("'", "");
				verbatimEventDateDelta = verbatimEventDateDelta.replace("s", "-01-01");
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ISO_LOCAL_DATE)
						.toFormatter().withResolverStyle(ResolverStyle.STRICT);
				LocalDate parseDate = LocalDate.parse(verbatimEventDateDelta,formatter);
				LocalDate endDate = parseDate.plusYears(10).minusDays(1);
				resultDate = parseDate.format(DateTimeFormatter.ofPattern("yyyy")) + "-01-01/" + endDate.format(DateTimeFormatter.ofPattern("yyyy")) + "-12-31";
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}
		}		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[A-Za-z]{3,9}[.]{0,1}[ ]{0,1}[-/ ][0-9]{4}$")) { 
			// Example: Jan-1980
			// Example: Jan./1980
			// Example: January 1980
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLL'/'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLLL'/'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					String cleaned = verbatimEventDate.replace(".", "");
					cleaned = "01-" + cleaned;
					LocalDate parseDate = LocalDate.parse(cleaned, i.next());
					resultDate = parseDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}
		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[A-Za-z]{3,9}[.]{0,1}[,]{0,1}[ ]{0,1}[0-9]{4}$")) { 
			// Example: June, 1980
			// Example: Jan. 1980
			// Example: January 1980
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLL'/'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLLL'/'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					String cleaned = cleanMonth(verbatimEventDate);
					cleaned = cleaned.replace(".","-").replace(",", "-").replace(" ","-");
					cleaned = cleaned.replace("--", "-").replace("--", "-");
					cleaned = "01-" + cleaned;
					LocalDate parseDate = LocalDate.parse(cleaned, i.next());
					resultDate = parseDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}
				
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			// Example: 04/03/1994  (ambiguous)
			// Example: 04/20/1994
			// Example: 20/04/1994
			String resultDateMD = null;
			String resultDateDM = null;
			LocalDate parseDate1 = null;
			LocalDate parseDate2 = null;
			if (assumemmddyyyy==null || assumemmddyyyy) { 
				List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("MM'-'dd'-'uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("MM'-'dd',-'uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("MM'-'dd','uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("M'-'d'-'uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("M'-'d',-'uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("M'-'d','uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));

				Iterator<DateTimeFormatter> i = formatters.iterator();
				boolean matched = false;
				while (i.hasNext() && !matched) {
					try { 
						String verbatimEventDateCleaned = verbatimEventDate.replace("/", "-").replace(" ", "-").replace(".","-");;
						verbatimEventDateCleaned = verbatimEventDateCleaned.replace("--", "-");
						parseDate1 = LocalDate.parse(verbatimEventDateCleaned, i.next());
						resultDateMD = parseDate1.format(DateTimeFormatter.ISO_LOCAL_DATE);
						logger.debug(resultDateMD);
						matched = true;
					} catch (Exception e) { 
						logger.debug(e.getMessage());
					}
				}
			} 
			if (assumemmddyyyy==null || !assumemmddyyyy) { 
				List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dd'-'MM'-'uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dd'-'MM',-'uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dd'-'MM','uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'-'M'-'uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'-'M',-'uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'-'M','uuuu"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));

				Iterator<DateTimeFormatter> i = formatters.iterator();
				boolean matched = false;
				while (i.hasNext() && !matched) {
					try { 
						String verbatimEventDateCleaned = verbatimEventDate.replace("/", "-").replace(" ", "-").replace(".","-");;
						verbatimEventDateCleaned = verbatimEventDateCleaned.replace("--", "-");
						parseDate2 = LocalDate.parse(verbatimEventDateCleaned, i.next());
						resultDateDM = parseDate2.format(DateTimeFormatter.ISO_LOCAL_DATE);
						logger.debug(resultDateDM);
						matched = true;
					} catch (Exception e) { 
						logger.debug(e.getMessage());
					}
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
				    if (parseDate1.isBefore(parseDate2)) { 
				        result.setResult(resultDateMD + "/" + resultDateDM);
				    } else { 
				        result.setResult(resultDateDM + "/" + resultDateMD);
				    }
				}
			} 
		}
		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[A-Z][A-Za-z]+ [0-9]{1,2}[stndrh]{2} [A-Za-z]+.{0,1},{0,1} [0-9]{4}$")) { 
			// Example: Friday 29th Oct. 2021
			
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEE' 'd'st 'LLLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEE' 'd'nd 'LLLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEE' 'd'rd 'LLLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEE' 'd'th 'LLLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEE' 'd' 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEE' 'd'st 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEE' 'd'nd 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEE' 'd'rd 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEE' 'd'th 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEE' 'd' 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEEE' 'd'st 'LLLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEEE' 'd'nd 'LLLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEEE' 'd'rd 'LLLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEEE' 'd'th 'LLLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEEE' 'd' 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEEE' 'd'st 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEEE' 'd'nd 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEEE' 'd'rd 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEEE' 'd'th 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("EEEE' 'd' 'LLL' 'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			
			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			String cleaned = verbatimEventDate.replace(",","");
			cleaned = DateUtils.cleanMonth(cleaned).replace(".", "");
			logger.debug(cleaned);
			while (i.hasNext() && !matched) {
				try { 
					LocalDate parseDate = LocalDate.parse(cleaned, i.next());
					resultDate = parseDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}	
		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^([0-9]{1,2}|[A-Za-z]+)[-/. ]([0-9]{1,2}|[A-Za-z]+),{0,1}[-/. ][0-9]{4}$")) { 
			// Example: 03/Jan/1982
			// Example: Jan-03-1982
			
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("LLLL'-'dd'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("LLL'-'dd'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("d'-'LLLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("LLLL'-'d'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("d'-'LLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("LLL'-'d'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					String cleaned = verbatimEventDate.replace("/","-").replace(".", "-").replace(" ", "-").replace("--", "-").replace(",","");
					LocalDate parseDate = LocalDate.parse(cleaned, i.next());
					resultDate = parseDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}	
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^([0-9]{1,2}|[A-Za-z]+)[-/. ]{1,2}([0-9]{1,2}|[A-Za-z]+),{0,1}[-/., ]{1,2}[0-9]{4}$")) { 
			// Example: Jan 03, 1982
			// Example: 3 Jan, 1982
			
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("LLLL'-'dd'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("LLL'-'dd'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("d'-'LLLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("LLLL'-'d'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("d'-'LLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("LLL'-'d'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					String cleaned = cleanMonth(verbatimEventDate);
					cleaned = cleaned.replace("/","-").replace(".", "-").replace(" ", "-").replace("--", "-").replace(",","-");
					cleaned = cleaned.replace("--", "-").replace("--", "-");
					LocalDate parseDate = LocalDate.parse(cleaned, i.next());
					resultDate = parseDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}			
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[X*]{2}[-/. ]([0-9]{1,2}|[A-Za-z]+)[-/. ][0-9]{4}$")) { 
			// Example: XX-04-1982   (XX for day) (which can't be a roman numeral month)
			// Example: XX-Jan-1995
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.SMART));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'MM'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'M'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					logger.debug(verbatimEventDate);
					String cleaned = cleanMonth(verbatimEventDate.substring(3));  // prevent XX from being turned into OctoberOctober.
					logger.debug(cleaned);
					cleaned = verbatimEventDate.substring(0, 3) + cleaned;
					logger.debug(cleaned);
					cleaned = cleaned.replace("/","-").replace(".", "-").replace(" ", "-").replace("--", "-").replace(",","");
					cleaned = cleaned.replaceFirst("XX-", "01-");
					cleaned = cleaned.replace("**-", "01-");
					logger.debug(cleaned);
					LocalDate parseDate = LocalDate.parse(cleaned, i.next());
					resultDate = parseDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
		}		

		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[X*]{2,3}[-/. ][X*]{2,3}[-/. ][0-9]{4}$")) { 
			// Example: XX-XXX-1995
			// Example: **-**-1995
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'MM'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			String cleaned = verbatimEventDate.replace("/","-").replace(".", "-").replace(" ", "-").replace("--", "-").replace(",","");
			cleaned = cleaned.replace("XX-XX-", "01-01-");
			cleaned = cleaned.replace("XX-XXX-", "01-01-");
			cleaned = cleaned.replace("XXX-XX-", "01-01-");
			cleaned = cleaned.replace("**-**-", "01-01-");
			cleaned = cleaned.replace("**-***-", "01-01-");
			cleaned = cleaned.replace("***-**-", "01-01-");

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					LocalDate parseDate = LocalDate.parse(cleaned, i.next());
					resultDate = parseDate.format(DateTimeFormatter.ofPattern("yyyy"));
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}			 
		}		
		
		if (verbatimEventDate.matches("^[0-9]{4}[-][0-9]{3}$")) { 
			// Example: 1994-128  (three digits after year = day of year).
			if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
				try { 
					DateTimeFormatter formatter = new DateTimeFormatterBuilder()
							.append(DateTimeFormatter.ISO_ORDINAL_DATE)
							.toFormatter().withResolverStyle(ResolverStyle.STRICT);
					LocalDate parseDate = LocalDate.parse(verbatimEventDate,formatter);
					resultDate =  parseDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}			
			}	
		}
	
		// NOTE: Block order from here on matters.  
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			try { 
				// Example: 1983-15  (two digits after year may fall into subsequent blocks).
				// Example: 1933-Mar
				logger.debug(verbatimEventDate);
				
				List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'M'-'dd"))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLL'-'dd").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLLL'-'dd").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));

				Iterator<DateTimeFormatter> i = formatters.iterator();
				boolean matched = false;
				while (i.hasNext() && !matched) {
					try { 
						String cleaned = cleanMonth(verbatimEventDate);
						cleaned = cleaned.replace("/","-").replace(".", "-").replace(" ", "-").replace("--", "-").replace(",","");
						cleaned = cleaned.replaceFirst("-$", "");
						cleaned = cleaned + "-01";
						logger.debug(cleaned);
						LocalDate parseDate = LocalDate.parse(cleaned, i.next());
						resultDate = parseDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
						logger.debug(resultDate);
						if (verbatimEventDate.matches("^[0-9]{4}[-][0-9]{2}$")) { 
							String century = verbatimEventDate.substring(0,2);
							String startBit = verbatimEventDate.substring(0,4);
							String endBit = verbatimEventDate.substring(5, 7);
							// 1815-16  won't parse in thls block as 16 is too large to be a month, passes to next block
							// 1805-06  could be month or abbreviated year.  Suspect.
							// 1805-03  should to be month (3<=5, thus not likely to be year range).
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
						matched = true;
					} catch (Exception e) { 
						logger.debug(e.getMessage());
					}
				}	
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}
		
		if ( result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
				verbatimEventDate.matches("^[0-9]{4}[-][0-9]{2}$")) 
		{
			// Example: 1884-85   (two digits look like year later in century).
			try { 
				String century = verbatimEventDate.substring(0,2);
				String startBit = verbatimEventDate.substring(0,4);
				String endBit = verbatimEventDate.substring(5, 7);
				String assembly = startBit+"/"+century+endBit;
				logger.debug(assembly);
				LocalDateInterval parseDate = new LocalDateInterval(assembly);
				logger.debug(parseDate);
				resultDate =  parseDate.getStart().format(DateTimeFormatter.ofPattern("yyyy")) + "/" + parseDate.getEnd().format(DateTimeFormatter.ofPattern("yyyy"));
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
				DateTimeFormatter formatter = new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.BASIC_ISO_DATE)
						.toFormatter().withResolverStyle(ResolverStyle.STRICT);
				LocalDate parseDate = LocalDate.parse(verbatimEventDate,formatter);
				resultDate =  parseDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
				logger.debug(resultDate);
				result.setResultState(EventResult.EventQCResultState.DATE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}			
		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			// Multiple yyyy-mmm-ddd, mmm-dd-yyyy, dd-mmm-yyyy patterns.

			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();

			List <Locale> locales = new ArrayList<Locale>();
			locales.add(Locale.ENGLISH);
			locales.add(Locale.FRENCH);
			locales.add(Locale.GERMAN);
			locales.add(Locale.ITALIAN);
			locales.add(Locale.KOREAN);
			try { 
				locales.add(Locale.forLanguageTag("es"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}
			try { 
				locales.add(Locale.forLanguageTag("pt"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}
			try { 
				locales.add(Locale.forLanguageTag("cs"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}
			try { 
				locales.add(Locale.forLanguageTag("da"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}
			try { 
				locales.add(Locale.forLanguageTag("cy"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}
			try { 
				locales.add(Locale.forLanguageTag("lt"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}
			try { 
				locales.add(Locale.forLanguageTag("ru"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}	
			try { 
				locales.add(Locale.forLanguageTag("nl"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}
			try { 
				locales.add(Locale.forLanguageTag("no"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}
			try { 
				locales.add(Locale.forLanguageTag("sv"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}
			try { 
				locales.add(Locale.forLanguageTag("sw"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}
			try { 
				locales.add(Locale.forLanguageTag("yo"));
			} catch (NullPointerException e) { 
				logger.debug(e.getMessage());
			}

			Iterator<Locale> iloc = locales.iterator();
			while (iloc.hasNext()) { 
				Locale loc = iloc.next();

				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLL'-'d").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLLL'-'d").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLL'-'d'st'").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLLL'-'d'st'").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLL'-'d'nd'").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLLL'-'d'nd'").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLL'-'d'rd'").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLLL'-'d'rd'").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLL'-'d'th'").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLLL'-'d'th'").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));

				if (yearsBeforeSuspect < 1000) { 
					formatters.add(new DateTimeFormatterBuilder()
							.append(DateTimeFormatter.ofPattern("LLLL'-'d'-'uuu").withLocale(loc))
							.toFormatter().withResolverStyle(ResolverStyle.STRICT));
					formatters.add(new DateTimeFormatterBuilder()
							.append(DateTimeFormatter.ofPattern("LLL'-'d'-'uuu").withLocale(loc))
							.toFormatter().withResolverStyle(ResolverStyle.STRICT));
					formatters.add(new DateTimeFormatterBuilder()
							.append(DateTimeFormatter.ofPattern("LLL'.-'d'-'uuu").withLocale(loc))
							.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				}

				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'-'d'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL,'-'d'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'-'d'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'-'d'st-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'-'d'st-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'-'d'nd-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'-'d'nd-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'-'d'rd-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'-'d'rd-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'-'d'd-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'-'d'd-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'-'d'th-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'-'d'th-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'-'d'''-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'-'d'''-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'-'d'''''-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'-'d'''''-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));

				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'-'LLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'-'LLLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'st-'LLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'st-'LLLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'nd-'LLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'nd-'LLLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'rd-'LLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'rd-'LLLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'th-'LLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'th-'LLLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));

				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLLd").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLd'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dLLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuuLLLd").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLduuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dLLLuuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuu'-'LLLLd").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLLd'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dLLLL'-'uuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("uuuuLLLLd").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dLLLLuuuu").withLocale(loc))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			}

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			String cleaned = cleanMonth(verbatimEventDate);
			cleaned = cleaned.replace("/","-").replace(".", "-").replace(" ", "-").replace(":", "-").replace(";", "-").replace(",", "-");
			cleaned = cleaned.replaceAll("-+", "-").replace(",","").replaceFirst("-$","");
			if (yearsBeforeSuspect < 100) {
				// only interpret two digit years if yearsBeforeSuspect < 100, meaning some two digit years may
				// be valid years in the range 0001-0099
				if (cleaned.matches("^[A-Za-z]+-[0-9]{1,2}-[0-9]{1,2}$")) { 
					// could be MMM-dd-yy two digit year, insert 0 for parser
					cleaned = cleaned.substring(0, cleaned.length()-2) + "0" + cleaned.substring(cleaned.length()-2);
					logger.debug(cleaned);
				}
			}
			logger.debug(cleaned);
			while (i.hasNext() && !matched) {
				try { 
					DateTimeFormatter formatter = i.next();
					LocalDate parseDate = LocalDate.parse(cleaned,formatter);
					resultDate =  parseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					// logger.debug(e.getMessage());
				}
			}
		}		
		
		logger.debug(result.getResultState());
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
				verbatimEventDate.matches(".*[A-Za-z].+[0-9]{4}$") ) {
			// Example: jan.-1992
			// Example: January 1992
			logger.debug(verbatimEventDate);
			
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("dd'-'LLLL'-'uuuu").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			logger.debug(verbatimEventDate);
			String cleaned = cleanMonth(verbatimEventDate);
			cleaned = cleaned.replace("/","-").replace(".", "-").replace(",","-").replace(" ","-");
			cleaned = cleaned.replaceAll("-(-)+", "-");
			cleaned = "01-" + cleaned;
			logger.debug(cleaned);
			
			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					LocalDate parseDate = LocalDate.parse(cleaned, i.next());
					resultDate = parseDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.RANGE);
					result.setResult(resultDate);
					matched = true;
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
				String cleaned = verbatimEventDate.replace(" to ", "/");
				cleaned = cleaned.replace("-", "/").replace(" ", "").replace(" ", "");
				logger.debug(cleaned);
				LocalDateInterval parseDate = new LocalDateInterval(cleaned);
				logger.debug(parseDate.toString());
				resultDate =  parseDate.getStart().format(DateTimeFormatter.ofPattern("yyyy")) + "/" + parseDate.getEnd().format(DateTimeFormatter.ofPattern("yyyy"));
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}

		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[A-Za-z]+[.]{0,1}( et | & | and | to |[-][ ]{0,1}| [-] )[A-Za-z]+[.]{0,1}(, |[/ .])[0-9]{4}$")) { 
			logger.debug(verbatimEventDate);
			// Example: Jan to Feb 1882
			// Example: Jan-Feb/1882
			verbatimEventDate = verbatimEventDate.replace(", ", " ");
		    if ( verbatimEventDate.matches("^[A-Za-z]+[.]{0,1}[-][A-Za-z]+[.]{0,1}[.][0-9]{4}$"))
		    { 
		    	// transform case with multiple periods to slash before year.
			    verbatimEventDate = verbatimEventDate.substring(0,verbatimEventDate.length()-5) + "/" + verbatimEventDate.substring(verbatimEventDate.length()-4);
			   logger.debug(verbatimEventDate);
		    }
		    if ( verbatimEventDate.matches("^[A-Za-z]+[.]{0,1}[ ][-][ ]{1}[A-Za-z]+[.]{0,1}[/ .][0-9]{4}$"))
		    { 
		    	// remove space around dash.
			    verbatimEventDate = verbatimEventDate.replace(" - ", "-");
			   logger.debug(verbatimEventDate);
		    }
		    if ( verbatimEventDate.matches("^[A-Za-z]+[.]{0,1}[-][ ]{1}[A-Za-z]+[.]{0,1}[/ .][0-9]{4}$"))
		    { 
		    	// remove space trailing after dash.
			    verbatimEventDate = verbatimEventDate.replace("- ", "-");
			   logger.debug(verbatimEventDate);
		    }
		    boolean isDisjunctFlag = false;
		    if ( verbatimEventDate.matches("^[A-Za-z]+[.]{0,1}( and | et | & ){1}[A-Za-z]+[.]{0,1}[/ .][0-9]{4}$"))
		    { 
		    	// replace and with dash
			    verbatimEventDate = verbatimEventDate.replace(" and ", "-");
			    verbatimEventDate = verbatimEventDate.replace(" et ", "-");
			    verbatimEventDate = verbatimEventDate.replace(" & ", "-");
			   logger.debug(verbatimEventDate);
			   isDisjunctFlag = true;
		    }		
		    if ( verbatimEventDate.matches("^[A-Za-z]+[.]{0,1} to {1}[A-Za-z]+[.]{0,1}[/ .][0-9]{4}$"))
		    { 
		    	// replace to with dash
			    verbatimEventDate = verbatimEventDate.replace(" to ", "-");
			   logger.debug(verbatimEventDate);
		    }			    
			try { 
				List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dd'-'LLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dd'-'LLLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dd'-'LLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dd'-'LLLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				String[] bits = verbatimEventDate.replace(" ", "/").split("-");
				if (bits!=null && bits.length==2) { 
					String year = verbatimEventDate.substring(verbatimEventDate.length()-4,verbatimEventDate.length());
					String startBit = "01-" + cleanMonth(bits[0]+"/"+year).replace("./", "/");   // e.g.  Jan      ->  01-Jan/1882
					String endBit = "01-" + cleanMonth(bits[1]).replace("./", "/");              // e.g.  Feb/1882 ->  01-Feb/1882
					logger.debug(startBit);
					logger.debug(endBit);

					Iterator<DateTimeFormatter> i = formatters.iterator();
					boolean matched = false;
					while (i.hasNext() && !matched) {
						try { 
							DateTimeFormatter formatter = i.next();
							LocalDate parseStartDate = LocalDate.parse(startBit,formatter);
							Iterator<DateTimeFormatter> iend = formatters.iterator();
							boolean matchedend = false;
							while (iend.hasNext() && !matchedend) {
								try { 
									formatter = iend.next();
									LocalDate parseEndDate = LocalDate.parse(endBit,formatter);
									resultDate =  parseStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM")) + "/" + parseEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
									logger.debug(resultDate);
									result.setResultState(EventResult.EventQCResultState.RANGE);
									result.setResult(resultDate);
									if (isDisjunctFlag) {
										try {
											// check to see if the date represents two adjacent months, if so, range
											LocalDateInterval range = new LocalDateInterval(result.getResult());
											if (range.getStart().getMonthValue()+1==range.getEnd().getMonthValue() 
													&& range.getStart().getYear()==range.getEnd().getYear()) 
											{ 
												result.setResultState(EventResult.EventQCResultState.RANGE);
											}  else { 
												result.setResultState(EventResult.EventQCResultState.DISJUNCT_RANGE);
											}
										} catch (DateTimeParseException | EmptyDateException e) {
											logger.error(e.getMessage());
										}
									}
								} catch (Exception e) { 
									logger.debug(e.getMessage());
								}
							}
							matched = true;
						} catch (Exception e) { 
							logger.debug(e.getMessage());
						}
					}	
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
					String startBit = bits[0]+"-"+year;
					String endBit = bits[1];
					startBit = cleanMonth(startBit).replace("/", "-").replace(" ", "-").replace(".","-").replace("--","-").replace("--", "-");
					endBit = cleanMonth(endBit).replace("/", "-").replace(" ", "-").replace(".","-").replace("--","-").replace("--", "-");;
					logger.debug(startBit);
					logger.debug(endBit);
					
					List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
					formatters.add(new DateTimeFormatterBuilder()
							.append(DateTimeFormatter.ofPattern("dd'-'LLL'-'uuuu").withLocale(Locale.ENGLISH))
							.toFormatter().withResolverStyle(ResolverStyle.STRICT));
					formatters.add(new DateTimeFormatterBuilder()
							.append(DateTimeFormatter.ofPattern("dd'-'LLLL'-'uuuu").withLocale(Locale.ENGLISH))
							.toFormatter().withResolverStyle(ResolverStyle.STRICT));
					formatters.add(new DateTimeFormatterBuilder()
							.append(DateTimeFormatter.ofPattern("ddLLL'-'uuuu").withLocale(Locale.ENGLISH))
							.toFormatter().withResolverStyle(ResolverStyle.STRICT));
					formatters.add(new DateTimeFormatterBuilder()
							.append(DateTimeFormatter.ofPattern("ddLLLL'-'uuuu").withLocale(Locale.ENGLISH))
							.toFormatter().withResolverStyle(ResolverStyle.STRICT));

					Iterator<DateTimeFormatter> i = formatters.iterator();
					boolean matched = false;
					while (i.hasNext() && !matched) {
						try { 
							DateTimeFormatter formatter = i.next();
							LocalDate parseStartDate = LocalDate.parse(startBit,formatter);
							Iterator<DateTimeFormatter> iend = formatters.iterator();
							boolean matchedend = false;
							while (iend.hasNext() && !matchedend) {
								formatter = iend.next();
								try { 
									LocalDate parseEndDate = LocalDate.parse(endBit,formatter);
									resultDate =  parseStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
											"/" + parseEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
									logger.debug(resultDate);
									result.setResultState(EventResult.EventQCResultState.RANGE);
									result.setResult(resultDate);
									matchedend = true;
								} catch (Exception e) { 	
									logger.debug(e.getMessage());
								}
							}
							matched = true;
						} catch (Exception e) { 	
							logger.debug(e.getMessage());
						}
					}
				}
			} catch (Exception e) { 	
				logger.debug(e.getMessage());
			}
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[A-Za-z]+[.]{0,1}[ ]{0,1}[0-9]{1,2}( - |[-]| to | and | et | & )[A-Za-z]+[.]{0,1}[ ]{0,1}[0-9]{1,2}[/ .,][ ]{0,1}[0-9]{4}$")) 
		{ 
			logger.debug(verbatimEventDate);
			// Example: Aug. 5 - Sept. 8, 1943
			String[] bits = verbatimEventDate.replace(" to ","-").replace(" - ","-")
					.replace(" and ", "-").replace(" et ","-")
					.replace(", "," ").replace(" ", "/").split("-");
			if (bits!=null && bits.length==2) { 
				String year = verbatimEventDate.substring(verbatimEventDate.length()-4,verbatimEventDate.length());
				String startBit = bits[0]+"/"+year;
				logger.debug(cleanMonth(startBit));
				logger.debug(cleanMonth(bits[1]));

				List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'/'d'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'./'d'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'./'d'-'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLL'.'d'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'/'d'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'./'d'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("LLLL'.'d'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));

				Iterator<DateTimeFormatter> i = formatters.iterator();
				boolean matched = false;
				while (i.hasNext() && !matched) {
					try { 
						DateTimeFormatter formatter = i.next();
						LocalDate parseStartDate = LocalDate.parse(cleanMonth(startBit),formatter);
						LocalDate parseEndDate = LocalDate.parse(cleanMonth(bits[1]),formatter);
						resultDate =  parseStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
								"/" + parseEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						logger.debug(resultDate);
						if (verbatimEventDate.matches(".*( and | et | & ).*")) {
							result.setResultState(EventResult.EventQCResultState.DISJUNCT_RANGE);
						} else {
							result.setResultState(EventResult.EventQCResultState.RANGE);
						}
						result.setResult(resultDate);
					} catch (DateTimeParseException e) {
						logger.debug(e.getMessage());
					}
				}
			}
		}			
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[0-9]{1,2}(.){0,1}[ ]*[A-Za-z]+[.]{0,1}( - |[.]|[-]| to | and | et | & ){0,1}[0-9]{1,2}[.]{0,1}[ ]*[A-Za-z]+[.]{0,1}[/ .,][ ]*{0,1}[0-9]{4}$")) 
		{ 
			logger.debug(verbatimEventDate);
			// Example: 5 Aug. - 8 Sept,, 1943
			// Example: 9.I-10.XII.2000
			String[] bits = verbatimEventDate.replace(" to ","-").replace(" - ","-")
					.replace(" and ", "-").replace(" et ","-")
					.replace(", "," ").replace(" ", "/").split("-");
			if (bits!=null && bits.length==2) { 
				String year = verbatimEventDate.substring(verbatimEventDate.length()-4,verbatimEventDate.length());
				String startBit = bits[0]+"/"+year;
				logger.debug(cleanMonth(startBit));
				logger.debug(cleanMonth(bits[1]));

				List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'/'LLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'/'LLL'./-'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'/'LLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'/'LLLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'/'LLLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'/'LLLL'./-'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'.'LLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'.'LLL'./-'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'.'LLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'.'LLLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'.'LLLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'.'LLLL'./-'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));

				Iterator<DateTimeFormatter> i = formatters.iterator();
				boolean matched = false;
				while (i.hasNext() && !matched) {
					try { 
						DateTimeFormatter formatter = i.next();
						LocalDate parseStartDate = LocalDate.parse(cleanMonth(startBit),formatter);
						LocalDate parseEndDate = LocalDate.parse(cleanMonth(bits[1]),formatter);
						resultDate =  parseStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
								"/" + parseEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						logger.debug(resultDate);
						if (verbatimEventDate.matches(".*( and | et | & ).*")) {
							result.setResultState(EventResult.EventQCResultState.DISJUNCT_RANGE);
						} else {
							result.setResultState(EventResult.EventQCResultState.RANGE);
						}
						result.setResult(resultDate);
					} catch (DateTimeParseException e) {
						logger.debug(e.getMessage());
					}
				}
			}
		}			

		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) &&
				verbatimEventDate.matches("^[0-9]{1,2}([ ]{0,1}[-][ ]{0,1}| and | et | to | & )[0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[/ -.][0-9]{4}$")) 
		{ 
			// Example: 11 et 14 VII 1910
			// Example: 05-02 Jan./1992
			String toCheck = verbatimEventDate;
			toCheck = toCheck.replace(" - ", "-").replace(" et ", "-").replace(" and ", "-").replace(" to ", "-");
			// Note: "and" has different semantics than "to", may imply that a specimen record
			// represents two occurrences (e.g. flower on one date, fruit on another) rather than
			// a range, but dwc:eventDate representation for both forms on one event is a range.
			if (toCheck.matches("^[0-9]{1,2}[-][0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[-][0-9]{4}$")) { 
				// transform case with multiple dashes to slash before year.
				toCheck = toCheck.substring(0,toCheck.length()-5) + "/" + toCheck.substring(toCheck.length()-4);
				logger.debug(toCheck);
			}
			if (toCheck.matches("^[0-9]{1,2}[-][0-9]{1,2}[ /.]{0,1}[A-Za-z]+[.]{0,1}[.][0-9]{4}$")) { 
				// transform case with multiple periods to slash before year.
				toCheck = toCheck.substring(0,toCheck.length()-5) + "/" + toCheck.substring(toCheck.length()-4);
				logger.debug(toCheck);
			}

			logger.debug(toCheck);
			String[] datebits = toCheck.replace(" ", "/").split("-");
			if (datebits!=null && datebits.length==2) { 
				String year = toCheck.substring(toCheck.length()-4,toCheck.length());
				logger.debug(cleanMonth(datebits[1]));
				List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d' 'LLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'.'LLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'/'LLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dLLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d' 'LLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'.'LLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'/'LLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dLLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d' 'LLLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'.'LLLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'/'LLLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dLLLL'/'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d' 'LLLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'.'LLLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("d'/'LLLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));
				formatters.add(new DateTimeFormatterBuilder()
						.append(DateTimeFormatter.ofPattern("dLLLL'./'uuuu").withLocale(Locale.ENGLISH))
						.toFormatter().withResolverStyle(ResolverStyle.STRICT));

				Iterator<DateTimeFormatter> i = formatters.iterator();
				boolean matched = false;
				while (i.hasNext() && !matched) {
					try { 
						DateTimeFormatter formatter = i.next();
						logger.debug(formatter.toString());

						LocalDate parseEndDate = LocalDate.parse(cleanMonth(datebits[1]),formatter);
						String startMonthYear = parseEndDate.format(DateTimeFormatter.ofPattern("LLLL/yyyy").withLocale(Locale.ENGLISH));
						String startBit = datebits[0]+"/"+startMonthYear;
						logger.debug(startBit);
						Iterator<DateTimeFormatter> istart = formatters.iterator();
						boolean matchedstart = false;
						LocalDate parseStartDate = null;
						while (istart.hasNext() && !matchedstart) {
							formatter = istart.next();
							try { 
								parseStartDate = LocalDate.parse(startBit,formatter);
								matchedstart = true;
							} catch (DateTimeParseException ex) { 
								logger.debug(ex.getMessage());
							}
						}
						resultDate =  parseStartDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
								"/" + parseEndDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						logger.debug(resultDate);
						result.setResult(resultDate);
						result.setResultState(EventResult.EventQCResultState.RANGE);
						if (verbatimEventDate.matches(".*( and | et | & ).*")) {
							try { 
							LocalDateInterval range = new LocalDateInterval(result.getResult());
							if (range.getEnd().getDayOfYear()-1==range.getStart().getDayOfYear() 
									&& range.getStart().getMonthValue()==range.getEnd().getMonthValue() 
									&& range.getStart().getYear()==range.getEnd().getYear()) 
							{ 
								result.setResultState(EventResult.EventQCResultState.RANGE);
							}  else { 
								result.setResultState(EventResult.EventQCResultState.DISJUNCT_RANGE);
							}
							} catch (Exception e) { 
								logger.error(e.getMessage());
							}
						}
					} catch (DateTimeParseException|NullPointerException e) {
						logger.debug(e.getMessage());
					}
				}
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
				verbatimEventDate.matches("^[1-2][07-9] [0-9]{1,2}/([0-9]{1,2}|[IVXivx]{1,4}) [0-9]{2}$")) 
		{ 
			// See: Day/month between century and year, from "Date Format" thread
	    	//  on NHCOLL started by Paul Callomon
			// Example: 19 3/viii 25
			// 18 25/7 73
			// 18 12/7 96
			String cleaned = verbatimEventDate;
			cleaned = cleaned.substring(3,cleaned.length()-3) + "/" + cleaned.substring(0,2) + cleaned.substring(cleaned.length()-2);
			cleaned = cleanMonth(cleaned);
			logger.debug(cleaned);
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
				.append(DateTimeFormatter.ofPattern("d'/'M'/'uuuu").withLocale(Locale.ENGLISH))
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
				.append(DateTimeFormatter.ofPattern("d'/'LLLL'/'uuuu").withLocale(Locale.ENGLISH))
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				try { 
					LocalDate parseDate = LocalDate.parse(cleaned, i.next());
					resultDate = parseDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
					logger.debug(resultDate);
					result.setResultState(EventResult.EventQCResultState.DATE);
					result.setResult(resultDate);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}	
			
		}
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN) && 
				verbatimEventDate.matches("^[0-9]{4}-[0-9]{2}/[0-9]{4}-[0-9]{2}$")
			) {
			// Example: 1885-03/1886-04
			try { 
				LocalDateInterval parseDate = new LocalDateInterval(verbatimEventDate);
				logger.debug(parseDate);
				resultDate =  parseDate.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM"))
						+ "/" + parseDate.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM"));
				result.setResultState(EventResult.EventQCResultState.RANGE);
				result.setResult(resultDate);
			} catch (Exception e) { 
				logger.debug(e.getMessage());
			}			
		}	
		
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			// Example: 1995-03-05/1996-05-08
			// Now try to see if string matches a simple ISO date format
			// and contains at least 4 digits  (nn/nn is recognizable as a y/y format, but shouldn't match here)
			if (verbatimEventDate.matches(".*[0-9]{4}.*")) {
				logger.debug(verbatimEventDate);
				if (verbatimEventDate.matches("^[0-9]{1,2}/[0-9]{4}$")) { 
					// skip and parse later
				} else { 
					try { 
						// try parsing as a straight ISO date
						LocalDateInterval testInterval = new LocalDateInterval(verbatimEventDate);
						if (testInterval!=null) { 
							logger.debug(testInterval.toString());
							if (testInterval.isSingleDay()) { 
								resultDate = testInterval.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
								result.setResultState(EventResult.EventQCResultState.DATE);
								result.setResult(resultDate);
							} else if (testInterval.getStart()!=null && testInterval.getEnd()!=null) { 
								resultDate = testInterval.toString();
								result.setResultState(EventResult.EventQCResultState.RANGE);
								result.setResult(resultDate);
							}
							if (testInterval.getStart().getYear()< yearsBeforeSuspect) { 
								result.setResultState(EventResult.EventQCResultState.SUSPECT);
							}
						}
					} catch (EmptyDateException|DateTimeParseException e) { 
						logger.debug(e.getMessage());
					}
				} 
			}
		}	
		if (result.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
			// Example: Jan,15-18 1882
			// Example: Jan. 17 and 18 1882
			String cleaned = verbatimEventDate.trim();
			if (verbatimEventDate.matches("^[A-Za-z.]+[ ,]+[0-9]{1,2}( and | et | & )[0-9]{0,2}[ ,]+[0-9]{4}$")) { 
				// Note: "and" has different semantics than "to", may imply that a specimen record
				// represents two occurrences (e.g. flower on one date, fruit on another) rather than
				// a range, but dwc:eventDate representation for both forms on one event is a range.
				cleaned = cleaned.replace(" and ", " to ");
				cleaned = cleaned.replace(" et ", " to ");
				cleaned = cleaned.replace(" & ", " to ");
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
						EventResult resultBit0 = DateUtils.extractDateFromVerbatimER(bits[0]);
						if (resultBit0!=null && resultBit0.getResultState().equals(EventQCResultState.DATE)) {
							EventResult resultBit1 = DateUtils.extractDateFromVerbatimER(bits[1]);
							if (resultBit1!=null && resultBit1.getResultState().equals(EventQCResultState.DATE)) {
								result.setResult(resultBit0.getResult()+ "/" + resultBit1.getResult());
								result.setResultState(EventResult.EventQCResultState.RANGE);
								// check to see if this is a disjunct range of days
								if (verbatimEventDate.matches(".*( and | et | & ).*")) {
									try {
										// check to see if the date represents two adjacent days, if so, range
										LocalDateInterval range = new LocalDateInterval(result.getResult());
										if (range.getEnd().getDayOfYear()-1==range.getStart().getDayOfYear() 
												&& range.getStart().getMonthValue()==range.getEnd().getMonthValue() 
												&& range.getStart().getYear()==range.getEnd().getYear()) 
										{ 
											result.setResultState(EventResult.EventQCResultState.RANGE);
										}  else { 
											result.setResultState(EventResult.EventQCResultState.DISJUNCT_RANGE);
										}
									} catch (DateTimeParseException | EmptyDateException e) {
										logger.error(e.getMessage());
									}
								}
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
			LocalDateInterval testExtract = DateUtils.extractDateInterval(result.getResult());
			if(testExtract==null || testExtract.getStart().getYear()< yearsBeforeSuspect) {
				// parsed start year was before the yearsBeforeSuspect value. 
				result.setResultState(EventResult.EventQCResultState.SUSPECT);
				logger.debug(result.getResult());
				logger.debug(testExtract);
			} else { 
				// parsed start year was equal to or after the yearsBeforeSuspect value. 
				logger.debug(result.getResult());
			}
			// check that a 4 digit year is present, if years before suspect is > 999,
			// excepting the one yy dd/mm yy case where yyyy is split.
			if (!verbatimEventDate.matches(".*[0-9]{4}.*")
					&& !verbatimEventDate.matches("^[1-2][07-9] [0-9]{1,2}/([0-9]{1,2}|[IVXivx]{1,4}) [0-9]{2}$")
					&& yearsBeforeSuspect>999) 
			{ 
				// there should be a 4 digit year present, but none is, yet a result was found,
				// assume this is in error and return a NOT_RUN result.
				result = new EventResult();
				logger.debug(result.getResult());
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
    	LocalDateInterval test = extractInterval(eventDate);
    	if (test!=null && test.toDuration().getSeconds() > 86400l) { 
    		// simple ISO date representing more than one day
    		isRange = true;
    	} else if (test==null) { 
    		// wasn't a simple ISO date, try the verbatim date parser and see if it returns a range.
    		EventResult lookupResult = DateUtils.extractDateFromVerbatimER(eventDate);
    		if (lookupResult!=null && lookupResult.getResult()!=null && lookupResult.getResult().equals(EventResult.EventQCResultState.RANGE)) { 
    			isRange = true;
    		}
    	}
    	return isRange;
    }	
	
    
    
    /**
     * Given a string that may be a date or a date range, extract a interval of
     * dates from that date range (ignoring time (thus the duration for the 
     * interval will be from one day to another).
     * 
     * @see DateUtils#extractInterval(String) which now does the same thing.
     * 
     * @param eventDate a string containing a dwc:eventDate from which to extract an interval.
     * @return An interval from one LocalDate to another LocalDate, null if no interval can be extracted.
     */
    public static LocalDateInterval extractDateInterval(String eventDate) {
    	LocalDateInterval result = null;
		try {
			result = new LocalDateInterval(eventDate);
		} catch (DateTimeParseException | EmptyDateException e) {
			logger.debug(e.getMessage());
		}
    	return result;
    }
    
    /**
     * Given a string that may be a date or a date range, extract a interval of
     * dates from that date range, to the resolution of a day, but not finer.
     * 
     * @see DateUtils#extractDateInterval(String) which returns a pair of DateMidnights.
     * 
     * @param eventDate a string containing a dwc:eventDate from which to extract an interval.
     * @return an interval from the beginning of event date to the end of event date.
     */
    public static LocalDateInterval extractInterval(String eventDate) {
    	LocalDateInterval result = null;
    	
    	try {
			result = new LocalDateInterval(eventDate);
		} catch (DateTimeParseException | EmptyDateException e) {
			logger.debug(e.getMessage());
		}
    	return result;
    }  
    
    /**
     * Extract a single LocalDate date from an event date, if given a range, 
     * returns a local date for the start of the range.
     * 
     * @param eventDate an event date from which to try to extract a LocalDAte
     * @return a LocalDate or null if a date cannot be extracted
     */
    public static LocalDate extractDate(String eventDate) {
    	LocalDate result = null;
    	if (eventDate!=null && eventDate.matches("^[0-9]{1,4}$")) {
        	DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        			.append(DateTimeFormatter.ISO_LOCAL_DATE)
        			.toFormatter().withResolverStyle(ResolverStyle.STRICT);
        	try { 
        		result = LocalDate.parse(eventDate + "-01-01", formatter);
        	} catch (Exception e) { 
        		// not a date
        		logger.debug(e.getMessage());
        	}		
    	} else if (eventDate!=null && eventDate.matches("^[0-9]{4}-[0-9]{2}$")) {
            	DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            			.append(DateTimeFormatter.ISO_LOCAL_DATE)
            			.toFormatter().withResolverStyle(ResolverStyle.STRICT);
            	try { 
            		result = LocalDate.parse(eventDate + "-01", formatter);
            	} catch (Exception e) { 
            		// not a date
            		logger.debug(e.getMessage());
            	}		
    	} else if (eventDate!=null && eventDate.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}/[0-9]{4}-[0-9]{2}-[0-9]{2}$")) {
        	DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        			.append(DateTimeFormatter.ISO_LOCAL_DATE)
        			.toFormatter().withResolverStyle(ResolverStyle.STRICT);
        	try { 
        		String[] bits = eventDate.split("/");
        		result = LocalDate.parse(bits[0], formatter);
        	} catch (Exception e) { 
        		// not a date
        		logger.debug(e.getMessage());
        	}            	
    	} else { 
    		List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
    		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    			.append(DateTimeFormatter.ISO_LOCAL_DATE)
    			.toFormatter().withResolverStyle(ResolverStyle.STRICT);
    		formatters.add(formatter);
    		DateTimeFormatter formatter1 = new DateTimeFormatterBuilder()
    			.append(DateTimeFormatter.ISO_ORDINAL_DATE)
    			.toFormatter().withResolverStyle(ResolverStyle.STRICT);
    		formatters.add(formatter1);
    		DateTimeFormatter formatter2 = new DateTimeFormatterBuilder()
        			.append(DateTimeFormatter.ISO_DATE_TIME)
        			.toFormatter().withResolverStyle(ResolverStyle.STRICT);
        	formatters.add(formatter2);
    		DateTimeFormatter formatter3 = new DateTimeFormatterBuilder()
        			.append(DateTimeFormatter.ISO_DATE)
        			.toFormatter().withResolverStyle(ResolverStyle.STRICT);
        	formatters.add(formatter3);
    		
    		Iterator<DateTimeFormatter> i = formatters.iterator();
    		while (i.hasNext()) { 
    			try { 
    				DateTimeFormatter parser = i.next();
    				//logger.debug(parser.toString());
    				result = LocalDate.parse(eventDate, parser);
    			} catch (Exception e) { 
            		// not parsable with that formatter
            		logger.debug(e.getMessage());
            	}
    		}
    	} 
    	if (result!=null && eventDate.matches("^[0-9]{4}-[0-9]{1}$")) {
    		// ISO requires 2 digit, zero padded month and day, single digit not allowed.
    		result = null;
    	}
    	if (result!=null && eventDate.matches("^[0-9]{4}-[0-9]{1}-.+")) {
    		// ISO requires 2 digit, zero padded month and day, single digit not allowed.
    		result = null;
    	}
    	if (result!=null && eventDate.matches("^[0-9]{4}-[0-9]{1,2}-[0-9]{1}$")) {
    		// ISO requires 2 digit, zero padded month and day, single digit not allowed.
    		result = null;
    	}
    	if (result!=null && eventDate.matches("^[0-9]{4}-[0-9]{1,2}-[0-9]{1}/.+")) {
    		// ISO requires 2 digit, zero padded month and day, single digit not allowed.
    		result = null;
    	}
    	logger.debug(result);
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
    			logger.debug(startDayInt);
    			logger.debug(startDayOfYear);
    			if (DateUtils.extractDate(eventDate)!=null && DateUtils.extractDate(eventDate).getDayOfYear() == startDayInt) { 
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
    			LocalDateInterval eventDateInterval = DateUtils.extractInterval(eventDate);
    			logger.debug(eventDateInterval.toString());
    			logger.debug(endDayInt);
    			if (eventDateInterval!=null && eventDateInterval.getEndDate()!=null) { 
    				int endDayOfInterval = eventDateInterval.getEndDate().getDayOfYear(); 
    				logger.debug(endDayOfInterval);
    				if (eventDateInterval.getStartDate().getDayOfYear() == startDayInt && endDayOfInterval == endDayInt ) { 
    					result=true;
    				} else { 
    					result = false;
    				}
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

    	if (DateUtils.isEmpty(eventDate) && DateUtils.isEmpty(year) && DateUtils.isEmpty(month) && DateUtils.isEmpty(day)) {
    		// no values to compare
    		result = true;
    	} else if (isEmpty(eventDate)) {
    		// no event date to make comarison with
    		result = true;
    	} else { 
    		LocalDateInterval test = extractInterval(eventDate);
    		if (test!=null) {
    			LocalDate start = test.getStartDate();
    			logger.debug(start);
    			boolean anyFails = false;
    			Integer dayBit = null;
    			if (!isEmpty(day)) { 
    				try { 
    					dayBit = Integer.parseInt(day);
    				} catch (NumberFormatException e) { 
    					anyFails = true;
    				}
    			}
    			Integer monthBit = null;
    			if (!isEmpty(month)) { 
    				try { 
    					monthBit = Integer.parseInt(month);
    				} catch (NumberFormatException e) { 
    					anyFails = true;
    				}
    			}
    			Integer yearBit = null;
    			logger.debug(anyFails);
    			if (!isEmpty(year)) { 
    				try { 
    					yearBit = Integer.parseInt(year);
    				} catch (NumberFormatException e) { 
    					anyFails = true;
    				}
    			}
    			logger.debug(anyFails);
    			if (!DateUtils.isEmpty(year) && yearBit!=null) {
    				if (start.getYear()!=yearBit) { 
    					anyFails = true;
    				}
    			}
    			logger.debug(anyFails);
    			if (!DateUtils.isEmpty(month) && monthBit!=null) { 
    				if (start.getMonthValue()!=monthBit) { 
    					anyFails = true;
    				}
    			}
    			logger.debug(anyFails);
    			if (!DateUtils.isEmpty(day) && dayBit!=null) {
    				if (start.getDayOfMonth()!=dayBit) { 
    					anyFails = true;
    				}
    			}
    			logger.debug(anyFails);
    			if (anyFails) { 
    				result = false;
    			} else { 
    				result = true;
    			}
    		}
    	}
    	
        return result;
    }
    
    /**
     * Does a string contain a non-blank value.
     * 
     * @param aString to check
     * @return true if the string is null, is an empty string, 
     *     or contains only whitespace.
     */
    public static boolean isEmpty(String aString)  {
    	boolean result = true;
    	if (aString != null && aString.trim().length()>0) { 
    		// TG2, do not consider string representations of NULL as null, consider as data.
    		//if (!aString.trim().toUpperCase().equals("NULL")) { 
    		   result = false;
    		//}
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
			logger.debug(eventDate);
    		
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H[VV][x]").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m[VV][x]").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m':'s['.'S][VV][x]").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m':'s['.'S][VV][x]").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			//1905-04-08T01:02:03.004Z
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m':'s'.'SSS[VV][x]").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched) {
				DateTimeFormatter formatter = i.next();
				//logger.debug(formatter.toString());
				try { 
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
				} catch (Exception e) { 
					logger.debug(e.getMessage());
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
    	boolean hasResult = false;
    	
    	if (!isEmpty(eventDate)) { 
    		if (eventDate.endsWith("UTC")) { eventDate = eventDate.replace("UTC", "Z"); } 
			logger.debug(eventDate);
    		
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME.withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ISO_ZONED_DATE_TIME.withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'Hx").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'Hxx").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'Hxxx").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'HX").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'HXX").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'HXXX").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));			
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H[VV][xx]").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m[VV][xx]").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m':'s['.'S][VV][xx]").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m':'s['.'S][VV][xx]").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			//1905-04-08T01:02:03.004Z
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m':'s'.'SSSSSSSS[VV][xx]").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			//1905-04-08T08:32:16-05:00
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m':'sx").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m':'sxx").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
					.append(DateTimeFormatter.ofPattern("uuuu'-'MM'-'dd'T'H':'m':'sxxx").withLocale(Locale.ENGLISH))
					.toFormatter().withResolverStyle(ResolverStyle.STRICT));

			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			while (i.hasNext() && !matched && !hasResult) {
				DateTimeFormatter formatter = i.next();
				logger.debug(formatter.toString());
				try { 
		    		if (eventDate.matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
		    			try { 
		    				ZonedDateTime match = ZonedDateTime.parse(eventDate, formatter);
		    				logger.debug(match);
		    				try { 
		    					result = match.withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("HH':'mm':'ss'.'SSSVV"));
		    				} catch (Exception e) { 
		    					result = match.withZoneSameInstant(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_TIME);
		    					logger.debug(result);
		    				}
		    				hasResult = true;
		    			} catch (Exception e) { 
		    				// not parsed as date with a time
		    				logger.error(e.getMessage());
		    			}    		
		    		}
		    		if (isRange(eventDate) && eventDate.contains("/") && !hasResult) { 
		    			String[] bits = eventDate.split("/");
		    			if (bits!=null && bits.length>1) { 
		    				// does either start or end date contain a time?
		    				if (bits[0].matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
		    					try { 
		    						LocalDateTime match = LocalDateTime.parse(bits[0], formatter);
		    						result = match.format(DateTimeFormatter.ISO_TIME);
		    						hasResult = true;
		    						logger.debug(match);
		    					} catch (Exception e) { 
		    						// not a date with a time
		    						logger.error(e.getMessage());
		    					}     
		    				}
		    				if (bits[1].matches("^[0-9]{4}[-][0-9]{2}[-][0-9]{2}[Tt].+")) { 
		    					try { 
		    						LocalDateTime match = LocalDateTime.parse(bits[1], formatter);
		    						result = match.format(DateTimeFormatter.ISO_TIME);
		    						hasResult = true;
		    						logger.debug(match);
		    					} catch (Exception e) { 
		    						// not a date with a time
		    						logger.error(e.getMessage());
		    					}     	  
		    				}
		    			}
		    		}
				} catch (Exception e) { 
					logger.debug(e.getMessage());
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
    	    LocalDateInterval eventDateInterval = extractInterval(eventDate);
    	    Duration eventDateDuration = eventDateInterval.toDuration();
    	    logger.debug(eventDateDuration.toDays());
    	    if (eventDateDuration.toDays()<1l) { 
    	    	result = true;
    	    } else if (eventDateDuration.toDays()==1l && eventDateInterval.getStart().getDayOfYear()==eventDateInterval.getEnd().getDayOfYear()) {
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
    	    LocalDateInterval eventDateInterval = extractDateInterval(eventDate);
    	    Duration duration = eventDateInterval.toDuration();
    	    if (duration.toDays() <= 31l) { 
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
     * @return true if duration is 365 days or less, 366 if the duration includes
     *   a leap day.
     */    
    public static boolean specificToYearScale(String eventDate) { 
    	boolean result = false;
    	if (!isEmpty(eventDate)) { 
    	    LocalDateInterval eventDateInterval = extractDateInterval(eventDate);
    	    Duration duration = eventDateInterval.toDuration();
    	    int daysInYear = 365;
    	    if (includesLeapDay(eventDate)) { 
    	    	daysInYear = 366;
    	    }
    	    Duration minusYear = (duration.minus(Duration.ofDays(daysInYear)));
    	    logger.debug(minusYear.toDays());
    	    if (minusYear.isNegative() || minusYear.isZero()) { 
    	    	result = true;
    	    }
    	}
    	return result;
    }      
    
    /**
     * Test if an event date specifies a duration of 10 years or less.
     * 
     * @param eventDate to test.
     * @return true if duration is 3653 days or or less.
     */    
    public static boolean specificToDecadeScale(String eventDate) { 
    	boolean result = false;
    	if (!isEmpty(eventDate)) { 
    	    LocalDateInterval eventDateInterval = extractDateInterval(eventDate);
    	    Duration duration = eventDateInterval.toDuration();
    	    Duration minusDecade = (duration.minus(Duration.ofDays((365*10)+3)));
    	    logger.debug(minusDecade.toDays());
    	    if (minusDecade.isNegative() || minusDecade.isZero()) { 
    	    	result = true;
    	    }
    	}
    	return result;
    }        
    
    /**
     * Measure the duration of an event date in seconds, when a time is 
     * specified, ceiling to the nearest second, when a time is not 
     * specified, from the date midnight at the beginning of a date 
     * range to the end of the last second of the day at the end of the 
     * range.  This will return 86400 seconds for the duration of 
     * a day specified as 1980-01-01.  Excludes leap seconds.  Treats 
     * date ranges and dates of reduced precision in the same way, that is
     * 1832 and 1832-01-01/1832-12-31 both return the same duration in seconds.
     * Consumers should be cautioned that the resulting value is makes 
     * simplifying assumptions, and should be treated as an approximation, 
     * see: https://xkcd.com/2867/
     * 
     * Provides: EVENT_DATE_DURATION_SECONDS
     * 
     * Suggested by Alex Thompson in a TDWG data quality task group call.
     * 
     * @param eventDate to test.
     * @return the duration of eventDate in seconds.
     * @throws TimeExtractionException if unable to obtain duration in seconds from the provided 
     *   eventDate string.
     */    
    public static long measureDurationSeconds(String eventDate) throws TimeExtractionException { 
    	long result = 0l;
    	if (!isEmpty(eventDate)) { 
    		if (eventDate.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) { 
    			java.time.LocalDate localDate = java.time.LocalDate.parse(eventDate,java.time.format.DateTimeFormatter.ISO_LOCAL_DATE.withResolverStyle(ResolverStyle.STRICT));
    			result = 86400;
    		} else if (eventDate.matches("^[0-9]{4}-[0-9]{3}$")) { 
    			java.time.LocalDate localDate = java.time.LocalDate.parse(eventDate,java.time.format.DateTimeFormatter.ISO_ORDINAL_DATE.withResolverStyle(ResolverStyle.STRICT));
    			result = 86400;
    		} else if (eventDate.contains("T")) { 
    			logger.debug(eventDate);
    			LocalDateTimeInterval interval;
				try {
					interval = new LocalDateTimeInterval(eventDate);
					logger.debug(interval);
					result = interval.toDuration().getSeconds();
				} catch (DateTimeParseException | EmptyDateException e) {
					logger.debug(e.getMessage());
					throw new TimeExtractionException(e.getMessage());
				}
    		} else { 
    			LocalDateInterval interval;
				try {
					interval = new LocalDateInterval(eventDate);
					result = interval.toDuration().getSeconds();
				} catch (DateTimeParseException | EmptyDateException e) {
					logger.debug(e.getMessage());
					throw new TimeExtractionException(e.getMessage());
				}
    		} 
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
    		time.append(String.format("%02d",instant.get(ChronoField.CLOCK_HOUR_OF_DAY)));
    		time.append(":").append(String.format("%02d",instant.get(ChronoField.MINUTE_OF_HOUR)));
    		time.append(":").append(String.format("%02d",instant.get(ChronoField.SECOND_OF_MINUTE)));
    		time.append(".").append(String.format("%03d",instant.get(ChronoField.MILLI_OF_SECOND)));
    		// java.time.Instant doesn't include time zone, so treat as UTC
    		time.append("Z"); 
    		result = time.toString();
    	}
    	return result;
    }
    
    /**
     * Interpret non-numeric month values as numbers in the range 1-12.
     * 
     * @param value string representation of a month to interpret.
     * @return input value with text, roman numeral, and some other forms
     *   replaced with a string containing a number in the range 1-12.
     */
    public static String interpretAsIntegerMonth(String value) { 
    	String cleaned = value.trim().replace(".", "").replace(",", "");
    	if (cleaned.toUpperCase().matches("^I$")) { cleaned = "1"; } 
    	if (cleaned.toUpperCase().matches("^II$")) { cleaned = "2"; } 
    	if (cleaned.toUpperCase().matches("^III$")) { cleaned = "3"; } 
    	if (cleaned.toUpperCase().matches("^IV$")) { cleaned = "4"; } 
    	if (cleaned.toUpperCase().matches("^V$")) { cleaned = "5"; } 
    	if (cleaned.toUpperCase().matches("^VI$")) { cleaned = "6"; } 
    	if (cleaned.toUpperCase().matches("^VII$")) { cleaned = "7"; } 
    	if (cleaned.toUpperCase().matches("^VIII$")) { cleaned = "8"; } 
    	if (cleaned.toUpperCase().matches("^IX$")) { cleaned = "9"; } 
    	if (cleaned.toUpperCase().matches("^X$")) { cleaned = "10"; } 
    	if (cleaned.toUpperCase().matches("^XI$")) { cleaned = "11"; } 
    	if (cleaned.toUpperCase().matches("^XII$")) { cleaned = "12"; } 
    	
    	if (cleaned.toLowerCase().matches("^one$")) { cleaned = "1"; } 
    	if (cleaned.toLowerCase().matches("^two$")) { cleaned = "2"; } 
    	if (cleaned.toLowerCase().matches("^three$")) { cleaned = "3"; } 
    	if (cleaned.toLowerCase().matches("^four$")) { cleaned = "4"; } 
    	if (cleaned.toLowerCase().matches("^five$")) { cleaned = "5"; } 
    	if (cleaned.toLowerCase().matches("^six$")) { cleaned = "6"; } 
    	if (cleaned.toLowerCase().matches("^seven$")) { cleaned = "7"; } 
    	if (cleaned.toLowerCase().matches("^eight$")) { cleaned = "8"; } 
    	if (cleaned.toLowerCase().matches("^nine$")) { cleaned = "9"; } 
    	if (cleaned.toLowerCase().matches("^ten$")) { cleaned = "10"; } 
    	if (cleaned.toLowerCase().matches("^one$")) { cleaned = "11"; } 
    	if (cleaned.toLowerCase().matches("^one$")) { cleaned = "12"; } 

		cleaned = cleaned.replace("月", "");
		cleaned = cleaned.replace("五", "5");
		cleaned = cleaned.replace("六", "6");
		cleaned = cleaned.replace("七", "7");
		cleaned = cleaned.replace("八", "8");
		cleaned = cleaned.replace("九", "9");
		cleaned = cleaned.replace("十一", "11");
		cleaned = cleaned.replace("十二", "12");
		cleaned = cleaned.replace("十", "10");
		cleaned = cleaned.replace("一", "1");    		
		cleaned = cleaned.replace("二", "2");
		cleaned = cleaned.replace("三", "3");
		cleaned = cleaned.replace("四", "4");
		
		return cleaned;
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
    		// Some variant abbreviations
    		cleaned = cleaned.replace("Sept.", "Sep.");
    		cleaned = cleaned.replace("sEPT.", "Sep.");
    		cleaned = cleaned.replace("SEpt.", "Sep.");
    		cleaned = cleaned.replace("Sept ", "Sep. ");
    		cleaned = cleaned.replace("Sept-", "Sep-");
    		cleaned = cleaned.replace("Sept,", "Sep.,");
    		cleaned = cleaned.replace("Sept/", "Sep./");
    		cleaned = cleaned.replace("OCt.", "Oct.");
    		cleaned = cleaned.replace("OCt-", "Oct-");
    		cleaned = cleaned.replace("OCt ", "Oct ");
    		cleaned = cleaned.replace("JAn.", "Jan.");
    		cleaned = cleaned.replace("FEb.", "Feb.");
    		cleaned = cleaned.replace("MAr.", "Mar.");
    		cleaned = cleaned.replace("APr.", "APr.");
    		cleaned = cleaned.replace("JUn.", "Jun.");
    		cleaned = cleaned.replace("JUl.", "Jul.");
    		cleaned = cleaned.replace("AUg.", "Aug.");
    		cleaned = cleaned.replace("NOv.", "Nov.");
    		cleaned = cleaned.replace("DEc.", "Dec.");
    		cleaned = cleaned.replace("  ", " ").trim();
    		cleaned = cleaned.replace(" ,", ",");
    		cleaned = cleaned.replace(" - ", "-");
    		cleaned = cleaned.replace("- ", "-");
    		cleaned = cleaned.replace(" -", "-");
    		// Strip off a trailing period after a final year
    		if (cleaned.matches("^.*[0-9]{4}[.]$")) { 
    			cleaned = cleaned.replaceAll("[.]$", "");
    		}
    		cleaned = cleaned.replace("Jly. ", "July ");
    		cleaned = cleaned.replace("Jly ", "July ");
    		cleaned = cleaned.replace("Febr.", "February");
    		// Some misspellings
    		cleaned = cleaned.replace("Jully ", "July ");
    		cleaned = cleaned.replace("Septmber", "September");
    		cleaned = cleaned.replace("Febuary", "February");
    		cleaned = cleaned.replace("Janauary", "January");

    		// Java date time parsing as used here, is case sensitive for months.
    		// Put cases of alternative spellings, missing accents, and capitalization into
    		// a form that tava.time will parse.

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
    		
    		cleaned = cleaned.replace("DEcember", "December");
    		cleaned = cleaned.replace("NOvember", "November");
    		cleaned = cleaned.replace("OCtober", "October");
    		cleaned = cleaned.replace("SEptember", "September");
    		cleaned = cleaned.replace("AUgust", "August");
    		cleaned = cleaned.replace("JUly", "July");
    		cleaned = cleaned.replace("JUne", "June");
    		cleaned = cleaned.replace("MAy", "May");
    		cleaned = cleaned.replace("APril", "April");
    		cleaned = cleaned.replace("MArch", "March");
    		cleaned = cleaned.replace("FEbruary", "February");
    		cleaned = cleaned.replace("JAnuary", "January");	    		
    		
    		cleaned = cleaned.replace("DEC", "Dec");
    		cleaned = cleaned.replace("NOV", "Nov");
    		cleaned = cleaned.replace("OCT", "Oct");
    		cleaned = cleaned.replace("SEP", "Sep");
    		cleaned = cleaned.replace("SEP", "Sep");
    		cleaned = cleaned.replace("AUG", "Aug");
    		cleaned = cleaned.replace("JUL", "Jul");
    		cleaned = cleaned.replace("JUN", "Jun");
    		cleaned = cleaned.replace("APR", "Apr");
    		cleaned = cleaned.replace("MAR", "Mar");
    		cleaned = cleaned.replace("FEB", "Feb");
    		cleaned = cleaned.replace("JAN", "Jan");	 
    		
    		cleaned = cleaned.replace("dec ", "Dec ");
    		cleaned = cleaned.replace("nov ", "Nov ");
    		cleaned = cleaned.replace("oct ", "Oct ");
    		cleaned = cleaned.replace("sep ", "Sep ");
    		cleaned = cleaned.replace("aug ", "Aug ");
    		cleaned = cleaned.replace("jul ", "Jul ");
    		cleaned = cleaned.replace("jun ", "Jun ");
    		cleaned = cleaned.replace("apr ", "Apr ");
    		cleaned = cleaned.replace("mar ", "Mar ");
    		cleaned = cleaned.replace("feb ", "Feb ");
    		cleaned = cleaned.replace("jan ", "Jan ");
    		
    		cleaned = cleaned.replace("dECEMBER", "December");
    		cleaned = cleaned.replace("nOVEMBER", "November");
    		cleaned = cleaned.replace("oCTOBER", "October");
    		cleaned = cleaned.replace("sEPTEMBER", "September");
    		cleaned = cleaned.replace("aUGUST", "August");
    		cleaned = cleaned.replace("jULY", "July");
    		cleaned = cleaned.replace("jUNE", "June");
    		cleaned = cleaned.replace("mAY", "May");
    		cleaned = cleaned.replace("aPRIL", "April");
    		cleaned = cleaned.replace("mARCH", "March");
    		cleaned = cleaned.replace("fEBRUARY", "February");
    		cleaned = cleaned.replace("jANUARY", "January");    		
            // uncommon abbreviations
    		cleaned = cleaned.replace("Mrch", "March");
    		cleaned = cleaned.replace("Jnry", "January");
    		cleaned = cleaned.replace("Februar", "February");
    		
    		// handle all lower case english to prevent partial matches on 
    		// lower case to month conversions that follow
    		cleaned = cleaned.replace("january", "January");
    		cleaned = cleaned.replace("february", "February");
    		cleaned = cleaned.replace("march", "March");
    		cleaned = cleaned.replace("april", "April");
    		cleaned = cleaned.replace("may", "May");
    		cleaned = cleaned.replace("june", "June");
    		cleaned = cleaned.replace("july", "July");
    		cleaned = cleaned.replace("august", "August");
    		cleaned = cleaned.replace("september", "Sepember");
    		cleaned = cleaned.replace("october", "October");
    		cleaned = cleaned.replace("november", "November");
    		cleaned = cleaned.replace("december", "December");
    		
    		// Italian months are lower case, if capitalized, skip a step and go right to english.
    		cleaned = cleaned.replace("Dicembre", "December");
    		cleaned = cleaned.replace("dicembre", "December");
    		cleaned = cleaned.replace("Novembre", "November");
    		cleaned = cleaned.replace("novembre", "November");
    		cleaned = cleaned.replace("Ottobre", "October");
    		cleaned = cleaned.replace("ottobre", "October");
    		cleaned = cleaned.replace("Settembre", "September");
    		cleaned = cleaned.replace("settembre", "September");
    		cleaned = cleaned.replace("Agosto", "August");
    		cleaned = cleaned.replace("agosto", "August");
    		cleaned = cleaned.replace("Luglio", "July");
    		cleaned = cleaned.replace("luglio", "July");
    		cleaned = cleaned.replace("Giugno", "June");
    		cleaned = cleaned.replace("giugno", "June");
    		cleaned = cleaned.replace("Maggio", "May");
    		cleaned = cleaned.replace("maggio", "May");
    		cleaned = cleaned.replace("Aprile", "April");
    		cleaned = cleaned.replace("aprile", "April");
    		cleaned = cleaned.replace("Marzo", "March");
    		cleaned = cleaned.replace("marzo", "March");
    		cleaned = cleaned.replace("Febbraio", "February");
    		cleaned = cleaned.replace("febbraio", "February");
    		cleaned = cleaned.replace("Gennaio", "January");			
    		cleaned = cleaned.replace("gennaio", "January");			
    		// likewise french, also handle omitted accents
    		cleaned = cleaned.replace("Janvier", "January");
    		cleaned = cleaned.replace("janvier", "January");
    		cleaned = cleaned.replace("janv", "January");
    		cleaned = cleaned.replace("Février", "February");
    		cleaned = cleaned.replace("Fevrier", "February");
    		cleaned = cleaned.replace("fevrier", "February");
    		cleaned = cleaned.replace("Mars", "March");
    		cleaned = cleaned.replace("mars", "March");
    		cleaned = cleaned.replace("Avril", "April");
    		cleaned = cleaned.replace("avril", "April");
    		cleaned = cleaned.replace("Mai", "May");
    		cleaned = cleaned.replace("Juin", "June");
    		cleaned = cleaned.replace("juin", "June ");
    		cleaned = cleaned.replace("Juillet", "July");
    		cleaned = cleaned.replace("juillet", "July");
    		cleaned = cleaned.replace("Août", "August");
    		cleaned = cleaned.replace("août", "August");
    		cleaned = cleaned.replace("Aout", "August");
    		cleaned = cleaned.replace("aout", "August");
    		cleaned = cleaned.replace("Septembre", "September");
    		cleaned = cleaned.replace("septembre", "September");
    		cleaned = cleaned.replace("Octobre", "October");
    		cleaned = cleaned.replace("octobre", "October");
    		cleaned = cleaned.replace("Novembre", "November");
    		cleaned = cleaned.replace("novembre", "November");
    		cleaned = cleaned.replace("Décembre", "December");
    		cleaned = cleaned.replace("Decembre", "December");
    		cleaned = cleaned.replace("decembre", "December");
    		// likewise spanish
    		cleaned = cleaned.replace("Enero", "January");
    		cleaned = cleaned.replace("enero", "January");
    		cleaned = cleaned.replace("Febrero", "February");
    		cleaned = cleaned.replace("febrero", "February");
    		cleaned = cleaned.replace("Marzo", "March");
    		cleaned = cleaned.replace("marzo", "March");
    		cleaned = cleaned.replace("Abril", "April");
    		cleaned = cleaned.replace("abril", "April");
    		cleaned = cleaned.replace("Mayo", "May");
    		cleaned = cleaned.replace("mayo", "May");
    		cleaned = cleaned.replace("Junio", "June");
    		cleaned = cleaned.replace("junio", "June");  // order matters, must come before juni, da_DK below.
    		cleaned = cleaned.replace("Julio", "July");
    		cleaned = cleaned.replace("julio", "July");
    		cleaned = cleaned.replace("Agosto", "August");
    		cleaned = cleaned.replace("agosto", "August");
    		cleaned = cleaned.replace("Septiembre", "September");
    		cleaned = cleaned.replace("septiembre", "September");
    		cleaned = cleaned.replace("Setiembre", "September");  // alternative spelling
    		cleaned = cleaned.replace("setiembre", "September");
    		cleaned = cleaned.replace("Octubre", "October");
    		cleaned = cleaned.replace("octubre", "October");
    		cleaned = cleaned.replace("Noviembre", "November");
    		cleaned = cleaned.replace("noviembre", "November");
    		cleaned = cleaned.replace("Diciembre", "December");
    		cleaned = cleaned.replace("diciembre", "December");
    		// likewise Danish (excluding same as english)
    		cleaned = cleaned.replace("januar", "January");
    		cleaned = cleaned.replace("februar", "February");
    		cleaned = cleaned.replace("marts", "March");
    		cleaned = cleaned.replace("maj", "May");
    		cleaned = cleaned.replace("juni", "June");
    		cleaned = cleaned.replace("juli", "July");
    		cleaned = cleaned.replace("oktober", "October");
    		
    		// Welsh
    		cleaned = cleaned.replace("Ionawr", "January");
    		cleaned = cleaned.replace("Chwefror", "February");
    		cleaned = cleaned.replace("Mawrth", "March");
    		cleaned = cleaned.replace("Ebrill", "April");
    		cleaned = cleaned.replace("Mai", "May");
    		cleaned = cleaned.replace("mai ", "May ");
    		cleaned = cleaned.replace("mai-", "May-");
    		cleaned = cleaned.replace("mai,", "May,");
    		cleaned = cleaned.replace("mai.", "May.");
    		cleaned = cleaned.replace("Mehefin", "June");
    		cleaned = cleaned.replace("Gorffennaf", "July");
    		cleaned = cleaned.replace("Awst", "August");
    		cleaned = cleaned.replace("Medi", "September");
    		cleaned = cleaned.replace("Hydref", "October");
    		cleaned = cleaned.replace("Tachwedd", "November");
    		cleaned = cleaned.replace("Rhagfyr", "December");
    		
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
    		if (!cleaned.matches(".*XX.*")) {
    			// avoid translating XX or XXX to October.
    			cleaned = cleaned.replace("X", "October");
    		}
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
    		if (!cleaned.matches(".*\\.xx.*")) {
    			// avoid translating xx or xxx to October.
    			cleaned = cleaned.replace(".x", ".October");
    		}
    		cleaned = cleaned.replace(" vi", " June");
    		cleaned = cleaned.replace(" ix", " September");
    		cleaned = cleaned.replace(" i", " January");
    		cleaned = cleaned.replace(" v", " May");
    		cleaned = cleaned.replace(" x", " October");

    		cleaned = cleaned.replace("月", "");
    		cleaned = cleaned.replace("三", "March");
    		cleaned = cleaned.replace("四", "April");
    		cleaned = cleaned.replace("五", "May");
    		cleaned = cleaned.replace("六", "June");
    		cleaned = cleaned.replace("七", "July");
    		cleaned = cleaned.replace("八", "August");
    		cleaned = cleaned.replace("九", "September");
    		cleaned = cleaned.replace("十一", "November");
    		cleaned = cleaned.replace("十二", "December");
    		cleaned = cleaned.replace("十", "October");
    		cleaned = cleaned.replace("一", "January");    		
    		cleaned = cleaned.replace("二", "February");

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
	 * @return true if at least one leap day is present in the eventDate range, otherwise false.
	 */
	public static boolean includesLeapDay(String eventDate) {
		boolean result = false;
		if (!DateUtils.isEmpty(eventDate) && DateUtils.eventDateValid(eventDate)) { 
			if (countLeapDays(eventDate) > 0) {
				result = true;
			}
		}
		return result;
	}
	
	/**
	 * Count the number of leap days present in an event date 
	 * 
	 * @param eventDate to check for leap days
	 * @return number of leap days present in eventDate, 0 if no leap days are present or
	 *    if eventDate does not contain a date. 
	 */
	public static int countLeapDays(String eventDate) {
		int result = 0;
		if (!DateUtils.isEmpty(eventDate) && DateUtils.eventDateValid(eventDate)) {
			LocalDateInterval interval = extractInterval(eventDate);
			Instant startInstant = interval.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC);
			Instant endInstant = interval.getEndDate().atStartOfDay().toInstant(ZoneOffset.UTC).plusSeconds(86399);
			Integer sYear = interval.getStartDate().getYear();
			Integer eYear = interval.getEndDate().getYear();
			String startYear = Integer.toString(sYear).trim();
			String endYear = Integer.toString(eYear).trim();
			String leapDay = startYear + "-02-29";
			logger.debug(leapDay);
			if (DateUtils.eventDateValid(leapDay)) {
				Instant instantInLeapDay = extractInterval(leapDay).getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC).plusSeconds(10);
				if (startInstant.isBefore(instantInLeapDay) && endInstant.isAfter(instantInLeapDay)) { 
					result = 1;
				}
			}
			// Range spanning more than one year, check last year
			if (!endYear.equals(startYear)) { 
				leapDay = endYear + "-02-29";
				logger.debug(leapDay);
				if (DateUtils.eventDateValid(leapDay)) { 
					Instant instantInLeapDay = extractInterval(leapDay).getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC).plusSeconds(10);
					if (startInstant.isBefore(instantInLeapDay) && endInstant.isAfter(instantInLeapDay)) { 
						result++;
					}
				}				
			}
			// Ranges of more than two years, check intermediate years
			if (eYear > sYear + 1) { 
				for (int testYear = sYear+1; testYear<eYear; testYear++) { 
					leapDay = Integer.toString(testYear).trim() + "-02-29";
					logger.debug(leapDay);
					if (DateUtils.eventDateValid(leapDay)) { 
						Instant instantInLeapDay = extractInterval(leapDay).getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC).plusSeconds(10);
						if (startInstant.isBefore(instantInLeapDay) && endInstant.isAfter(instantInLeapDay)) { 
							result++;
						}
					}				
				}
			}
		}
		return result;
	}	
	
	/**
	 * Test to see if a verbatimEventDate appears to represent a discontinuous range (e.g. 
	 * 'Jan 5 and Feb 2 1882'.   When used on an Event that represents a collecting event, 
	 * signals that more than the occurrence should be split into more than one occurrence 
	 * (e.g. one occurrence on Jan 5, another on Feb 2).  Two adjacent days, e.g. Jan 5 and 6,
	 * are not considered discontinuous.
	 * 
	 * @param verbatimEventDate to test
	 * @return null if no date is found, true if the date appears to be a discontinuous range, 
	 *    false otherwise.
	 */
	public static Boolean verbatimIsDiscontinuous(String verbatimEventDate) { 
		Boolean result = null;
		if (!isEmpty(verbatimEventDate)) { 
			EventResult containedDate = DateUtils.extractDateFromVerbatimER(verbatimEventDate);
			if (containedDate.getResultState().equals(EventResult.EventQCResultState.DISJUNCT_RANGE)) {
				result = true;
			} else if (!containedDate.getResultState().equals(EventResult.EventQCResultState.NOT_RUN)) {
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * Compare two strings that should represent event dates (ingoring time, if not a date range)
	 * 
	 * @param eventDate to compare with second event date
	 * @param secondEventDate to compare with
	 * @return true if the two provided event dates represent the same interval.
	 */
	public static Boolean eventsAreSameInterval(String eventDate, String secondEventDate) {
		boolean result = false;
		try {
			LocalDateInterval interval = null;
			LocalDateInterval secondInterval = null;
            interval = DateUtils.extractDateInterval(eventDate);
            secondInterval = DateUtils.extractDateInterval(secondEventDate);
			logger.debug(interval.toString());
			logger.debug(secondInterval.toString());
            result = interval.equals(secondInterval);
		} catch (Exception e) { 
			logger.error(e.getMessage());
		}
        return result;
	}
	
	/**
	 * Test to see if a string matches the expectations for an ISO date or date range,
	 * without any time information, in one of the forms for a date or a date range expected
	 * by the TDWG BDQ TG2 tests (e.g. yyyy, yyyy-mm-dd, yyyy/yyyy, yyyy-mm-dd/yyyy-mm-dd).
	 * 
	 * @param aDateString a string which might be a date to be tested.
	 * @return true if the provided string is in one of the expected forms for an ISO date or date range,
	 *    otherwise false.
	 */
	public static Boolean stringIsISOFormattedDate(String aDateString) { 
		boolean result = false;
		
		try { 
			LocalDateInterval testInterval = new LocalDateInterval(aDateString);
			
    		result=true;
			
    		// exclude any successful parses where month or day is one digit
	    	if (aDateString.matches("^[0-9]{4}-[0-9]{1}-[0-9]{1,2}$")) {
	    		result = false;
	    	} else if (aDateString.matches("^[0-9]{4}-[0-9]{1,2}-[0-9]{1}$")) { 
	    		result = false;
	    	} else if (aDateString.matches("^[0-9]{4}-[0-9]{1}$")) {
	    		result = false;
	    	} else if (aDateString.matches("^.*/[0-9]{4}-[0-9]{1}$")) { 
	    		result = false;
	    	} else if (aDateString.matches("^.*/[0-9]{4}-[0-9]{1,2}-[0-9]{1}$")) { 
	    		result = false;
	    	} else if (aDateString.matches("^.*/[0-9]{4}-[0-9]{1}-[0-9]{1,2}$")) { 
	    		result = false;
	    	} else if (aDateString.matches("^-[0-9]{1}-[0-9]{0,2}$")) { 
	    		result = false;
	    	} else if (aDateString.matches("^-[0-9]{0,2}-[0-9]{1}$")) { 
	    		result = false;
	    	}
			
		} catch (Exception e) { 
			logger.debug(e.getMessage());
		}
		
		return result;
	}
	
    /**
     * Run from the command line, arguments -f to specify a file, -m to show matches. 
     * Converts dates in a specified input file from verbatim form to format expected by dwc:eventDate.
     * 
     * @param args -f filename to check a file containing a list of dates, one per line, 
     *        e.g. -f src/test/resources/example_dates.csv
     *    -m to show matched dates and their interpretations otherwise lists non-matched lines.  
     *    -a to show all lines, matched or not with their interpretations.
     */
	public static void main(String[] args) { 
		DateUtils.interpretDates(args);
	} 
	
	/**
	 * Invokes internals for the command line execution, 
	 * @param args arguments arguments -f to specify a file, -m to show matches. 
	 */
	public static void interpretDates(String[] args) {
		DateUtils.interpretDates(args,true);
	}

	/**
	 * Internals for the command line execution.  Attempts to interpret verbatim dates found in
	 * a file and writes output to the console.
	 * 
	 * @param args arguments -f to specify a file, resource example_dates.csv is used
	 *   if null, -m to show matches. 
	 * @param showSummaryLines true to show count of matches and unmatched lines at end of output
	 */
	public static void interpretDates(String[] args,Boolean showSummaryLines) {
		if (showSummaryLines == null) { showSummaryLines=true; } 
 		try {
			File datesFile = null;
			try { 
				URL datesURI = DateUtils.class.getResource("/example_dates.csv");
				datesFile = new File(datesURI.toURI());
			} catch (NullPointerException e){ 
				logger.debug(e.getMessage());
			} catch (URISyntaxException e) {
				logger.error(e.getMessage());
			}
			if (args!=null && args.length>0 && args[0]!=null && args[0].toLowerCase().equals("-f")) {
				if (args[1]!=null) { 
					datesFile = new File(args[1]);
				}
			}
			boolean standardIn = false;
			if (datesFile==null) { 
				if (args!=null && args.length>0 && args[0]!=null && args[0].toLowerCase().equals("-i")) {
					standardIn = true;
				} else if (args!=null && args.length>1 && args[0]!=null && args[0].toLowerCase().equals("-v")) {
					StringBuffer verbatim = new StringBuffer();  // support presence of absence of quotes
					for (int i=1; i<args.length; i++) { 
						verbatim.append(args[i]).append(" ");
					}
				    EventResult result = DateUtils.extractDateFromVerbatimER(verbatim.toString().trim());
				    String retval = result.getResult();
				    if (retval==null) { retval=""; }
					System.out.println(retval);
				    System.exit(0);;
				} else { 
					System.out.println("Check a file consisting of verbatim dates, one date per line.");
					System.out.println("Specify a file to check with: -f filename");
					System.out.println("Add no additional options to see only non-matched lines.");
					System.out.println("Show only matching lines with -m");
					System.out.println("Show both matching and non-matching lines with -a");
					System.out.println("Read one line from standard input with: -i");
					System.out.println("Interpret one date with: -v \"{date}\", e.g. -v \"Feb, 2012\", no other parameters. ");
					System.exit(1);
				}
			}
			boolean showMatches = false;
			boolean showAll = false;
			for (int i=0; i<args.length; i++) {
				if (args[i].equals("-m")) { showMatches = true; } 
				if (args[i].equals("-a")) { showAll = true; } 
			}
			BufferedReader reader = null;
			if (standardIn) { 
				reader = new BufferedReader(new InputStreamReader(System.in)); 
			} else { 
				reader =  new BufferedReader(new FileReader(datesFile));
			}
			String line = null;
			int unmatched = 0;
			int matched = 0;
			boolean done = false;
			while (!done && (line=reader.readLine())!=null) {
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
					if (standardIn) { 
						System.out.println(result.getResult());
					} else { 
						if (showMatches || showAll) { 
							System.out.println(line + "\t" + result.getResultState() + "\t" + result.getResult());
						}
					}
				}
				if (standardIn) { 
					done = true;  // only read one line of text in this mode.
				}
			}
			reader.close();
			if (!standardIn) { 
				if (showSummaryLines) { 
					System.out.println("Unmatched lines: " + unmatched);
 					System.out.println("Matched lines: " + matched);
				}
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
			System.out.println(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());;
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Return the Integer month number for a month represented as a Roman numeral, 
	 * if given a string that matches a Roman numeral in the range I to XII (case
	 * insensitive), return the corresponding Integer value for month of year.
	 * 
	 * @param month a string representing a month
	 * @return an integer in the range 1-12 if month is a roman numeral in the
	 *   range I-XII, otherwise null
	 */
	public static Integer romanMonthToInteger(String month) { 
		Integer result = null;
		if (month!=null && month.matches("^[XIVxiv]+$")) { 
			switch (month.toUpperCase()) { 
				case "I": result = Integer.valueOf(1); break;
				case "II": result = Integer.valueOf(2); break;
				case "III": result = Integer.valueOf(3); break;
				case "IV": result = Integer.valueOf(4); break;
				case "V": result = Integer.valueOf(5); break;
				case "VI": result = Integer.valueOf(6); break;
				case "VII": result = Integer.valueOf(7); break;
				case "VIII": result = Integer.valueOf(8); break;
				case "IX": result = Integer.valueOf(9); break;
				case "X": result = Integer.valueOf(10); break;
				case "XI": result = Integer.valueOf(11); break;
				case "XII": result = Integer.valueOf(12); break;
			}
		} 
		return result;
	}

	/**
	 * Get a map of english words for numbers in the range 1-31 to integers,
	 * for example one:1, first:1, two:2 
	 * @return a map of day_as_string, day_as_integer,
	 */
	public static Map<String, Integer> getDayStringMap(){ 
		Map<String, Integer> result = new HashMap<String,Integer>();
		result.put("one",1);
		result.put("first",1);
		result.put("two",2);
		result.put("second",2);
		result.put("three",3);
		result.put("third",3);
		result.put("four",4);
		result.put("fourth",4);
		result.put("five",5);
		result.put("fifth",5);
		result.put("six",6);
		result.put("sixth",6);
		result.put("seven",7);
		result.put("seventh",7);
		result.put("eight",8);
		result.put("eighth",8);
		result.put("nine",9);
		result.put("ninth",9);
		result.put("ten",10);
		result.put("tenth",10);
		result.put("eleven",11);
		result.put("eleventh",11);
		result.put("twelve",12);
		result.put("twelfth",12);
		result.put("thirteen",13);
		result.put("thirteenth",13);
		result.put("fourteen",14);
		result.put("fourteenth",14);
		result.put("fifteen",15);
		result.put("fifteenth",15);
		result.put("sixteen",16);
		result.put("sixteenth",16);
		result.put("seventeen",17);
		result.put("seventeenth",17);
		result.put("eightteen",18);
		result.put("eighteenth",18);
		result.put("nineteen",19);
		result.put("ninteenth",19);
		result.put("twenty",20);
		result.put("twenteth",21);
		result.put("twentyone",21);
		result.put("twentyfirst",21);
		result.put("twentytwo",22);
		result.put("twentysecond",22);
		result.put("twentythree",23);
		result.put("twentythird",23);
		result.put("twentyfour",24);
		result.put("twentyfourth",24);
		result.put("twentyfive",25);
		result.put("twentyfifth",25);
		result.put("twentysix",26);
		result.put("twentysixth",26);
		result.put("twentyseven",27);
		result.put("twentyseventh",27);
		result.put("twentyeight",28);
		result.put("twentyeighth",28);
		result.put("twentynine",29);
		result.put("twentyninth",29);
		result.put("thirty",30);
		result.put("thirtyeth",30);
		result.put("thirtyone",31);
		result.put("thirtyfirst",31);
		result.put("thirtytwo",32);
		result.put("thirtysecond",32);
		return result;
	}
	
	/**
	 * Test to see if a date in ISO date form has a precision of one day or better (distinct from 
	 * a range of more than one day, 1842 has a precision of one year, 1842-01-01/1842-12-31 has 
	 * a precision of one day, though it represents a range of one year).
	 * 
	 * @param eventDate to test
	 * @return true if precision is one day or better, false otherwise.
	 */
	public static boolean hasResolutionDayOrFiner(String eventDate) { 
		boolean result = false;
		if (!DateUtils.isEmpty(eventDate)) { 
			if (eventDate.contains("/")) { 
				String[] bits = eventDate.split("/");
				boolean anyFail = false;
				for (int i=0; i<bits.length; i++) { 
					logger.debug(bits[i]);
					if (!hasResolutionDayOrFiner(bits[i])) {
						if (!anyFail) { anyFail = true; }
					}
				}
				if (anyFail) { 
					result = false;
				} else { 
					result = true;
				}
			} else { 
				if (eventDate.contains("T")) { 
					// strip off any time
					eventDate = eventDate.replaceAll("T.*$", "").trim();
				}
				if (eventDate.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) { 
					result = true;
				} else if (eventDate.matches("^[0-9]{4}$")) {
					result = false;
				} else if (eventDate.matches("^[0-9]{4}-[0-9]{2}$")) {
					result = false;
				} else if (eventDate.matches("^[0-9]{4}-[0-9]{3}$")) {
					result = true;
				} else if (eventDate.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}T.*")) { 
					result = true;
				} else if (eventDate.matches("^[0-9]{4}-[0-9]{3}T.*")) {
					result = true;
				}
			}
		}
		return result;
	}
    
}