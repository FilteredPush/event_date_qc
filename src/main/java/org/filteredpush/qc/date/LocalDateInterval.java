/**
 * LocalDateInterval.java
 */
package org.filteredpush.qc.date;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class LocalDateInterval {

	private static final Log logger = LogFactory.getLog(LocalDateInterval.class);
	
	private LocalDate startDate;
	private LocalDate endDate;
	
	/**
	 * An internal date structure used in parsing string dates within 
	 * LocalDateInterval, carries a start and end date.
	 *
	 */
	protected class DatePair { 
		
		private LocalDate startOfPair;
		private LocalDate endOfPair;
		
		public DatePair(LocalDate startOfPair, LocalDate endOfPair) {
			this.startOfPair = startOfPair;
			this.endOfPair = endOfPair;
		}
		public LocalDate getStartOfPair() {
			return startOfPair;
		}
		public void setStartOfPair(LocalDate startOfPair) {
			this.startOfPair = startOfPair;
		}
		public LocalDate getEndOfPair() {
			return endOfPair;
		}
		public void setEndOfPair(LocalDate endOfPair) {
			this.endOfPair = endOfPair;
		}
		
		
	}
	
	/**
	 * Construct a LocalDateInterval based on given start and end dates.
	 * 
	 * @param startDate the beginning date of the interval
	 * @param endDate the end date of the interval
	 */
	public LocalDateInterval(LocalDate startDate, LocalDate endDate) throws EmptyDateException,DateOrderException {
		if (startDate==null || endDate==null) { 
			throw new EmptyDateException("provided startDate and/or endDate was null");
		}
		if (startDate.isAfter(endDate)) { 
			throw new DateOrderException("Provided startDate ["+startDate+"] is after provided endDate ["+endDate+"]");
		}
		this.startDate = startDate;
		this.endDate = endDate;
	}
	
	/**
	 * Construct a LocalDateInterval consisting of a single day.
	 * 
	 * @param singleDay the day which is to be the startDate and endDate of the 
	 * LocalDateInterval
	 */
	public LocalDateInterval(LocalDate singleDay) throws EmptyDateException {
		if (singleDay==null) { 
			throw new EmptyDateException("provided date was null");
		}
		this.startDate = singleDay;
		this.endDate = singleDay;
	}
	
	/**
	 * Construct a LocalDateInterval from an ISO date.  
	 * 
	 * @param dateString a string representing an ISO date or date range in any 
	 *   of several ISO date forms, is not a general date parser, can parse
	 *   yyyy-mm-dd, yyyy-mm-dd/yyyy-mm-dd, yyyy-mm, yyyy, yyyy/yyyy, yyyy-ddd
	 *   and similar patterns.
	 *   
	 * @throws EmptyDateException if given an empty value for dateString
	 * @throws DateTimeParseException if unable to parse the provided string.
	 */
	public LocalDateInterval(String dateString) throws EmptyDateException, DateTimeParseException { 
		if (DateUtils.isEmpty(dateString)) { 
			throw new EmptyDateException("Provided dateString value is empty");
		}
    	if (dateString.contains("/")) {
    		String[] bits = dateString.split("/");
    		if (bits.length!=2) {
    			throw new DateTimeParseException("provided dateString contains a / but isn't parsable into exactly two parts", dateString, dateString.indexOf("/"));
    		}
    		String startBit = bits[0];
    		if (startBit.matches("^-[0-9]{3,4}")) {
    			startBit = startBit.replace("-", "").concat(" BCE");
    		}
    		String endBit = bits[1];
    		if (endBit.matches("^-[0-9]{3,4}")) {
    			endBit = endBit.replace("-", "").concat(" BCE");
    		}
    		DatePair startResult = parseDateBit(startBit);
    		this.startDate = startResult.startOfPair;
    		DatePair endResult = parseDateBit(endBit);
    		this.endDate = endResult.endOfPair;
    		if (endDate.isBefore(startDate)) { 
    			throw new DateTimeParseException("provided dateString has a start date later than the end date.", dateString, dateString.indexOf("/"));
    		}
    	} else { 
    		if (dateString.matches("^-[0-9]{3,4}")) { 
    			DatePair result = parseDateBit(dateString.replace("-", "").concat(" BCE"));
    			this.startDate = result.getStartOfPair();
    			this.endDate = result.getEndOfPair();
    		} else { 
    			DatePair result = parseDateBit(dateString);
    			this.startDate = result.getStartOfPair();
    			this.endDate = result.getEndOfPair();
    		}
    	}
	}

	/**
	 * Extract a pair of days, representing the start and end of the specified dateBit
	 * ignoring time, so long as the time part it is correctly formatted.
	 * 
	 * @param dateBit a string representing a date or range of dates, without a / to parse
	 * @return a date pair containing the start and end days of the range in dateBit, values are the
	 * same if dateBit represents a single day or less.  
	 */
	protected DatePair parseDateBit(String dateBit)  throws EmptyDateException, DateTimeParseException { 
		DatePair result = null;
		if (DateUtils.isEmpty(dateBit)) { 
			throw new EmptyDateException("Provided dateString value is empty");
		}
		if (dateBit.matches("^[0-9]{1,3}$")) { 
			// Java.time ISO date parsers, even in strict mode, will parse 1, 2, and 3 digit years, but these are 
			// invalid ISO dates, as the ISO standard requires the year to consist of at least 4 digits.
			throw new DateTimeParseException("unable to parse provided dateString, year does not consist of 4 digits", dateBit, 0);
		}
		if (dateBit.contains("T")) { 
			logger.debug(dateBit);
			// but check first that the time is correctly formatted
			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
			formatters.add(new DateTimeFormatterBuilder()
				.append(DateTimeFormatter.ISO_DATE_TIME)
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
				.append(DateTimeFormatter.ISO_ZONED_DATE_TIME)
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
    			.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
    			.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			// The java.time parsers don't treat minutes and seconds as optional, add more parsers
			formatters.add(new DateTimeFormatterBuilder()
    			.append(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'kk"))
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
    			.append(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'kk[VV][zz][X][xx][OOOO]"))
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
    			.append(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'kk':'mm[VV][zz][X][xx][OOOO"))
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			// Because we will be ignoring the time part, other than checking for valid format
			// try both kk (two digit clock hour of day 1-24 and HH hour of day 0-23
			// to interpret time, we'd need to know which of these to use.
			formatters.add(new DateTimeFormatterBuilder()
    			.append(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH"))
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
    			.append(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH[VV][zz][X][xx][OOOO]"))
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			formatters.add(new DateTimeFormatterBuilder()
    			.append(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH':'mm[VV][zz][X][xx][OOOO"))
				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
			Iterator<DateTimeFormatter> i = formatters.iterator();
			boolean matched = false;
			String testMe = "";
			// Handle valid cases of fractional lowest element present that java.time can't parse
			if (dateBit.matches("^.*T[0-9]{2}[.,][0-9]+$")) { 
				// Time represented a fractional hours will fail to parse, Java.time 
				//  doesn't have a formatter for this valid ISO date/time form.
				testMe = dateBit.replaceFirst("[.,][0-9]+$","");
			} else if (dateBit.matches("^.*T[0-9]{2}:[0-9]{2}[.,][0-9]+$")) { 
				// Time represented a fractional minutes will fail to parse, Java.time 
				//  doesn't have a formatter for this valid ISO date/time form.
				testMe = dateBit.replaceFirst("[.,][0-9]+$","");
			} else { 
				testMe = dateBit;
			}
			logger.debug(testMe);
			while (i.hasNext() && !matched) {
				try { 
					LocalDate startDateBit = LocalDate.parse(testMe, i.next());
					result = new DatePair(startDateBit, startDateBit);
					matched = true;
				} catch (Exception e) { 
					logger.debug(e.getMessage());
				}
			}
			if (!matched) { 
				throw new DateTimeParseException("unable to parse provided dateString, error parsing time part", dateBit, 0);
			} 
			// extract just the date part to pass on for testing
			dateBit = dateBit.substring(0,dateBit.indexOf("T"));
		}
		logger.debug(dateBit);
    	if (dateBit.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) { 
    		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ISO_LOCAL_DATE)
    				.toFormatter().withResolverStyle(ResolverStyle.STRICT);
    		LocalDate startDateBit = LocalDate.parse(dateBit, formatter);
    		result = new DatePair(startDateBit, startDateBit);
    	} else if (dateBit.matches("^[0-9]{4}-[0-9]{3}$")) { 
    		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ISO_ORDINAL_DATE)
    				.toFormatter().withResolverStyle(ResolverStyle.STRICT);
    		LocalDate startDateBit = LocalDate.parse(dateBit, formatter);
    		result = new DatePair(startDateBit, startDateBit);
    	} else { 
    		if (dateBit.matches("^[0-9]{4}-[0-9]{2}$")) {
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ISO_LOCAL_DATE)
    				.toFormatter().withResolverStyle(ResolverStyle.STRICT);
    			LocalDate startDateBit = LocalDate.parse(dateBit+"-01", formatter);
    			result = new DatePair(startDateBit,startDateBit.with(TemporalAdjusters.lastDayOfMonth()));
    		} else if (dateBit.matches("^[0-9]{1,4}$")) {
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ISO_LOCAL_DATE)
    				.toFormatter().withResolverStyle(ResolverStyle.STRICT);
    			if (dateBit.matches("^[0-9]{1,3}$")) {
    				dateBit = String.format("%04d", Integer.parseInt(dateBit));
    			}
    			LocalDate startDateBit = LocalDate.parse(dateBit+"-01-01", formatter);
    			result = new DatePair(startDateBit,startDateBit.with(TemporalAdjusters.lastDayOfYear()));
    		} else if (dateBit.matches("^[0-9]{1,4} BCE$")) {
    			logger.debug(dateBit);
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ofPattern("yyyy-MM-ddGGGGG"))
    				.toFormatter().withResolverStyle(ResolverStyle.STRICT);
    			dateBit = String.format("%04d", Integer.parseInt(dateBit.substring(0,4)));
    			LocalDate startDateBit = LocalDate.parse(dateBit+"-01-01B", formatter);
    			result = new DatePair(startDateBit.minusYears(1),startDateBit.minusYears(1).with(TemporalAdjusters.lastDayOfYear()));
    		} else { 
    			List<DateTimeFormatter> formatters = new ArrayList<DateTimeFormatter>();
    			
    			formatters.add(new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ISO_DATE)
    				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
    			formatters.add(new DateTimeFormatterBuilder()
        			.append(DateTimeFormatter.BASIC_ISO_DATE)
    				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
    			formatters.add(new DateTimeFormatterBuilder()
        			.append(DateTimeFormatter.ISO_DATE)
    				.toFormatter().withResolverStyle(ResolverStyle.STRICT));
    			
    			Iterator<DateTimeFormatter> i = formatters.iterator();
    			boolean matched = false;
    			while (i.hasNext() && !matched) {
    				try { 
    					LocalDate startDateBit = LocalDate.parse(dateBit, i.next());
    					result = new DatePair(startDateBit, startDateBit);
    					matched = true;
    				} catch (Exception e) { 
    					logger.debug(e.getMessage());
    				}
    			}
    			
    		}
    	}
    	if (result==null) { 
    		throw new DateTimeParseException("unable to parse provided dateString contains", dateBit, 0);
    	}
    	return result;
	} 
	/**
	 * @return the startDate
	 */
	public LocalDate getStartDate() {
		return startDate;
	}
	/**
	 * Same as getStartDate().
	 * 
	 * @return the startDate
	 */
	public LocalDate getStart() { 
		return startDate;
	}
	
	/**
	 * @param startDate the startDate to set
	 */
	private void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public LocalDate getEndDate() {
		return endDate;
	}
	/**
	 * Same as getEndDate()
	 * 
	 * @return the endDate
	 */
	public LocalDate getEnd() {
		return endDate;
	}	

	/**
	 * @param endDate the endDate to set
	 */
	private void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Test to see if this LocalDateInterval instance has a start day on the same
	 * date as the end day.
	 * 
	 * @return true if startDate=endDate and both are not null, otherwise false.
	 */
	public boolean isSingleDay() { 
		boolean result = false;
		if (startDate!=null && endDate!=null && startDate.isEqual(endDate)) { 
			result = true;
		}
		return result;
	}
	
	@Override
	public String toString() { 
		String result = null;
		if (isSingleDay()) { 
			result = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
		} else if (startDate!=null && endDate!=null) {
			logger.debug(startDate.toString());
			logger.debug(endDate.toString());
			if (startDate.getYear()==endDate.getYear() && startDate.getMonthValue()==1 && endDate.getMonthValue()==12 && startDate.getDayOfMonth()==1 && endDate.getDayOfMonth()==31) { 
				result = startDate.format(DateTimeFormatter.ofPattern("yyyy"));
			} else if (startDate.getYear()!=endDate.getYear() && startDate.getMonthValue()==1 && endDate.getMonthValue()==12 && startDate.getDayOfMonth()==1 && endDate.getDayOfMonth()==31) { 
				result = startDate.format(DateTimeFormatter.ofPattern("yyyy")) + "/" + endDate.format(DateTimeFormatter.ofPattern("yyyy"));
			} else if (startDate.getYear()==endDate.getYear() && startDate.getMonthValue()==endDate.getMonthValue() && startDate.getDayOfMonth()==1 && endDate.getDayOfMonth() ==endDate.lengthOfMonth()) { 
				result = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
			} else { 
				result = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
			} 
		} else { 
			if (startDate==null) { 
				result = "[null]";
			} else { 
				result = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
			}
			if (endDate==null) { 
				result = result+"/[null]";
			} else { 
				result = result + "/" + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
			}
 		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj!=null) { 
			if (obj.getClass().equals(this.getClass())) {
				if (this.startDate==null && ((LocalDateInterval)obj).getStartDate()==null) { 
					if (this.endDate==null && ((LocalDateInterval)obj).getEndDate()==null) { 
						result = true;
					} else { 
						if (this.endDate.equals(((LocalDateInterval)obj).getEndDate())) { 
							result = true;
						}
					}
				} else { 
					if (this.startDate.equals(((LocalDateInterval)obj).getStartDate())) { 
						if (this.endDate==null && ((LocalDateInterval)obj).getEndDate()==null) { 
							result = true;
						} else { 
							if (this.endDate.equals(((LocalDateInterval)obj).getEndDate())) { 
								result = true;
							}
						}
					}
				}
			}
		}
		return result;
	}

	/** 
	 * Test to see if the specified interval is wholly contained within the 
	 * current LocalDateInterval instance.
	 * 
	 * @param interval to compare 
	 * @return true if interval is entirely contained within this, otherwise false,
	 *   will return false if interval or any of the start/end dates are null;
	 */
	public boolean contains(LocalDateInterval interval) {
		boolean result = false;
		if (interval!=null && this.getStartDate()!=null && this.getEndDate()!=null && interval.getEndDate()!=null && interval.getStartDate()!=null)  {
			if (interval.getStartDate().isAfter(this.getStartDate()) || interval.getStartDate().isEqual(this.getStartDate())) { 
				if (interval.getEndDate().isBefore(this.getEndDate()) || interval.getEndDate().isEqual(this.getEndDate())) { 
				   result = true;
				}
			}
		}
		return result;
	}

	/**
	 * Test to see if the specified interval overlaps with the current LocalDateInterval instance
	 * that is if there exists some non-empty portion of interval that lies outside of 
	 * this and some non-empty portion of interval that lies within this.  
	 * 
	 * @param interval to compare
	 * @return true if the interval overlaps with this, otherwise false, will 
	 *    return false if interval or any of the start/end dates are null.
	 */
	public boolean overlaps(LocalDateInterval interval) {
		boolean result = false;
		if (interval!=null) { 
			if (interval.equals(this)) { 
				result = true;
			} else if (interval.contains(this)) {
				result = true;
			} else if (this.contains(interval)) {
				result = true;
			} else if (interval!=null && this.getStartDate()!=null && this.getEndDate()!=null && interval.getEndDate()!=null && interval.getStartDate()!=null)  {
				if (interval.getStart().isBefore(this.getStart()) && interval.getEnd().isAfter(this.getStart())) { 
					result = true;
				} else if (interval.getStart().isBefore(this.getEnd()) && interval.getEnd().isAfter(this.getEnd())) { 
					result = true;
				} else if (interval.getStart().equals(this.getStart()) && interval.getEnd().isAfter(this.getStart())) { 
					result = true;
				} else if (interval.getStart().isBefore(this.getEnd()) && interval.getEnd().equals(this.getEnd())) { 
					result = true;
				} else if (interval.getStart().isBefore(this.getStart()) && interval.getEnd().equals(this.getStart())) { 
					result = true;
				} else if (interval.getStart().equals(this.getEnd()) && interval.getEnd().isAfter(this.getEnd())) { 
					result = true;
				} 
			}
		}
		return result;
	}

	public Duration toDuration() {
		Duration result = null;
		
		result = Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay().plusDays(1));
		
		return result;
	}
	
}

	