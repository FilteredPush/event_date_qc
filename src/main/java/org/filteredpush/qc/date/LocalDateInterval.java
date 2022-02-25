/**
 * LocalDateInterval.java
 */
package org.filteredpush.qc.date;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;

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
	public LocalDateInterval(LocalDate singleDay) {
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
    		DatePair startResult = parseDateBit(bits[0]);
    		this.startDate = startResult.startOfPair;
    		DatePair endResult = parseDateBit(bits[1]);
    		this.endDate = endResult.endOfPair;
    	} else { 
    		DatePair result = parseDateBit(dateString);
    		this.startDate = result.getStartOfPair();
    		this.endDate = result.getEndOfPair();
    	}
	}

	protected DatePair parseDateBit(String dateBit)  throws EmptyDateException, DateTimeParseException { 
		DatePair result = null;
		if (DateUtils.isEmpty(dateBit)) { 
			throw new EmptyDateException("Provided dateString value is empty");
		} 
    	if (dateBit.matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$")) { 
    		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ISO_LOCAL_DATE)
    				.toFormatter();
    		LocalDate startDateBit = LocalDate.parse(dateBit, formatter);
    		result = new DatePair(startDateBit, startDateBit);
    	} else if (dateBit.matches("^[0-9]{4}-[0-9]{3}$")) { 
    		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ISO_ORDINAL_DATE)
    				.toFormatter();
    		LocalDate startDateBit = LocalDate.parse(dateBit, formatter);
    		result = new DatePair(startDateBit, startDateBit);
    	} else { 
    		if (dateBit.matches("^[0-9]{4}-[0-9]{2}$")) {
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ISO_LOCAL_DATE)
    				.toFormatter();
    			LocalDate startDateBit = LocalDate.parse(dateBit+"-01", formatter);
    			result = new DatePair(startDateBit,startDateBit.with(TemporalAdjusters.lastDayOfMonth()));
    		} else if (dateBit.matches("^[0-9]{4}$")) {
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ISO_LOCAL_DATE)
    				.toFormatter();
    			LocalDate startDateBit = LocalDate.parse(dateBit+"-01-01", formatter);
    			result = new DatePair(startDateBit,startDateBit.with(TemporalAdjusters.lastDayOfYear()));
    		} else { 
    			DateTimeFormatter formatter = new DateTimeFormatterBuilder()
    				.append(DateTimeFormatter.ISO_DATE_TIME)
    				.append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    				.append(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    				.append(DateTimeFormatter.BASIC_ISO_DATE)
    				.append(DateTimeFormatter.ISO_DATE)
    				.toFormatter();
    			LocalDate startDateBit = LocalDate.parse(dateBit, formatter);
    			result = new DatePair(startDateBit, startDateBit);
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
	 * @param startDate the startDate to set
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public LocalDate getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(LocalDate endDate) {
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
		}
		if (startDate!=null && endDate!=null) {
			result = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + endDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
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
	
}

	