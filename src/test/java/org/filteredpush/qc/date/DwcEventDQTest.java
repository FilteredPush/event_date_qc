/** DwcEventDQTest.java
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

import static org.junit.Assert.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.EnumDQAmendmentResultState;
import org.datakurator.ffdq.api.EnumDQResultState;
import org.datakurator.ffdq.api.EnumDQValidationResult;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DwcEventDQTest {
	private static final Log logger = LogFactory.getLog(DwcEventDQTest.class);

	@Test
	public void testMeasureDuration() { 
		EventDQMeasurement<Long> measure = DwCEventDQ.measureDurationSeconds("1880-05-08");
		Long seconds = (60l*60l*24l)-1l; 
		assertEquals(seconds, measure.getValue());
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureDurationSeconds("1880-05");
		seconds = (60l*60l*24l*31)-1l; 
		assertEquals(seconds, measure.getValue());		
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureDurationSeconds("");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, measure.getResultState());
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#isDayInRange(java.lang.String)}.
	 */
	@Test
	public void testIsDayInRange() {
		EventDQValidation result = null;
		for (int i = 1; i<=31; i++) { 
			result = DwCEventDQ.isDayInRange(Integer.toString(i));
			assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		}
		result = DwCEventDQ.isDayInRange(" 1 ");
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		result = DwCEventDQ.isDayInRange("01");
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());

		int i=0;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=32;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());	 

		i=-1;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=Integer.MAX_VALUE;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=Integer.MIN_VALUE;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());	

		result = DwCEventDQ.isDayInRange(null);
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange("");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange(" ");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange("A");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange("1.5");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange("**");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		result = DwCEventDQ.isDayInRange("1st");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange("2nd");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#isMonthInRange(java.lang.String)}.
	 */
	@Test
	public void testIsMonthInRange() {
		EventDQValidation result = null;
		for (int i = 1; i<=12; i++) { 
			result = DwCEventDQ.isMonthInRange(Integer.toString(i));
			assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		}
		result = DwCEventDQ.isMonthInRange(" 1 ");
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		result = DwCEventDQ.isMonthInRange("01");
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());

		int i=0;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=13;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());	 

		i=-1;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=Integer.MAX_VALUE;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=Integer.MIN_VALUE;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());	

		result = DwCEventDQ.isMonthInRange(null);
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange("");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange(" ");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange("A");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange("1.5");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange("**");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		result = DwCEventDQ.isMonthInRange("Jan");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange("January");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
	}

	@Test
	public void testDurationInYear() {
		EventDQValidation result = null;
		for (int i = 1981; i<=1983; i++) {
			// not leap years
			result = DwCEventDQ.isEventDateJulianYearOrLess(Integer.toString(i));
			assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
			result = DwCEventDQ.isEventDateYearOrLess(Integer.toString(i));
			assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());			
		}
		// Leap year
		result = DwCEventDQ.isEventDateJulianYearOrLess("1980");
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());
		result = DwCEventDQ.isEventDateYearOrLess("1980");
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());			
	}
	
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#isDayPossibleForMonthYear(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testIsDayPossibleForMonthYear() {
		EventDQValidation result = null;
		for (int year = 1900; year<=1903; year++) { 
			for (int month = 1; month<=12; month++) { 
				for (int day= 1; day<=27; day++) { 
					result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
					logger.debug(year + " " + month + " " + day);
					assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
					assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
				}
			}
		}

		// Not a leap year
		int year = 2001;
		int month = 2;
		int day = 28;
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		
		year = 2001;
		month = 2;
		day = 29;  
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());
		
		year = 2001;
		month = 2;
		day = 30;  
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());
		
		// Leap year
		year = 2000;
		month = 2;
		day = 29;  
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		
		year = 2000;
		month = 2;
		day = 30;
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());
		
		year = 2000;
		month = 2;
		day = 31;
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#dayMonthTransposition(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDayMonthTransposition() {
		String month = "30";
		String day = "11";
		EventDQAmendment result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(EnumDQAmendmentResultState.TRANSPOSED, result.getResultState());
		assertEquals("11",result.getResult().get("dwc:month"));
		assertEquals("30",result.getResult().get("dwc:day"));

		result = DwCEventDQ.dayMonthTransposition(day,month);
		assertEquals(EnumDQAmendmentResultState.NO_CHANGE, result.getResultState());

		day = "5"; 
		month = "11";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(EnumDQAmendmentResultState.NO_CHANGE, result.getResultState());   

		day = "15"; 
		month = "8";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(EnumDQAmendmentResultState.NO_CHANGE, result.getResultState());  

		day = "15"; 
		month = "15";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	

		day = "15"; 
		month = "34";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());      	

		day = "-1"; 
		month = "15";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());      	

		day = "-1"; 
		month = "5";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());      	

		result = DwCEventDQ.dayMonthTransposition(day,"");
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
	}
	
	@Test
	public void testExtractDateFromVerbatim() { 
		String eventDate = "";
		String verbatimEventDate = "Jan 1884";
		EventDQAmendment result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1884-01",result.getResult().get("dwc:eventDate"));
		assertEquals(1,result.getResult().size());

		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1884-03-01",result.getResult().get("dwc:eventDate"));
		assertEquals(1,result.getResult().size());		
		
		eventDate = "1884";
		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(EnumDQAmendmentResultState.NO_CHANGE, result.getResultState());
		assertEquals(0,result.getResult().size());			
		eventDate = "1884-03-01";
		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(EnumDQAmendmentResultState.NO_CHANGE, result.getResultState());
		assertEquals(0,result.getResult().size());		
		
		eventDate = null;
		verbatimEventDate = "";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(0,result.getResult().size());			
		
		eventDate = null;
		verbatimEventDate = "5-8-1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(EnumDQAmendmentResultState.AMBIGUOUS, result.getResultState());
		assertEquals("1884-05-08/1884-08-05",result.getResult().get("dwc:eventDate"));
		assertEquals(1,result.getResult().size());		
		
		eventDate = null;
		verbatimEventDate = "2001/Feb/29";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(0,result.getResult().size());			
		
	}
	
	@Test
	public void testCorrectEventDateFormat() { 
		String eventDate = "Jan 1884";
		EventDQAmendment result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1884-01",result.getResult().get("dwc:eventDate"));
		assertEquals(1,result.getResult().size());	
		
		eventDate = "1 Jan 1884";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1884-01-01",result.getResult().get("dwc:eventDate"));
		assertEquals(1,result.getResult().size());		
		
		/* Case that inspired adding this method, typical use of / instead of - in dates. 
		 */
		eventDate = "1884/01/02";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1884-01-02",result.getResult().get("dwc:eventDate"));
		assertEquals(1,result.getResult().size());		
		
		eventDate = "1884-01-02";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(EnumDQAmendmentResultState.NO_CHANGE, result.getResultState());		
	
		eventDate = "";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		
		result = DwCEventDQ.correctEventDateFormat(null);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "2305345ifo342fd,cofaga";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "02/03/1884";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(EnumDQAmendmentResultState.AMBIGUOUS, result.getResultState());	
		assertEquals("1884-02-03/1884-03-02",result.getResult().get("dwc:eventDate"));
		assertEquals(1,result.getResult().size());				
		
	}
	
	@Test
	public void testEventDateFromYearStartEndDay() { 
		String eventDate = "";
		String year = "1980";
		String startDay = "5";
		String endDay = "6";
		EventDQAmendment result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-01-05/1980-01-06", result.getResult().get("dwc:eventDate"));
		
		eventDate = "1980"; // eventDate contains a value
		year = "1980";
		startDay = "5";
		endDay = "6";
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.NOT_RUN,result.getResultState()); 
		
		eventDate = "";
		year = "1980";
		startDay = "day5";  // not a number
		endDay = "6";
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = "";
		year = "1980";  // a leap year
		startDay = "1";
		endDay = "366";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-01-01/1980-12-31", result.getResult().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDay = "1";
		endDay = "365";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1981-01-01/1981-12-31", result.getResult().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDay = "45";
		endDay = "280";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1981-02-14/1981-10-07", result.getResult().get("dwc:eventDate"));		
		
		eventDate = "";
		year = "1980";  // a leap year
		startDay = "45";  // spans leap day
		endDay = "280";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-02-14/1980-10-06", result.getResult().get("dwc:eventDate"));			
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDay = "1";
		endDay = "366";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());		
		
		eventDate = "";
		year = "1980";
		startDay = "900";  // out of range
		endDay = "920";
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = "";
		year = "1980";
		startDay = "200";  // start and end reversed or in different years.
		endDay = "10";
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());	
		
		// Example given in TG2 definition: 
		//dwc:year=1999, dwc:startDayOfYear=123, dwc:endDayOfYear=125 
		//therefore dwc:eventDate=1999-05-03/1999-05-05
		eventDate = "";
		year = "1999"; 
		startDay = "123"; 
		endDay = "125";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1999-05-03/1999-05-05", result.getResult().get("dwc:eventDate"));	
		
	}
	
	
	@Test
	public void testEventDateFromMonthDay() { 
		String eventDate = "";
		String year = "1980";
		String month = "5";
		String day = "6";
		EventDQAmendment result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-05-06", result.getResult().get("dwc:eventDate"));
		
		eventDate = "1980"; // eventDate contains a value
		year = "1980";
		month = "5";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.NOT_RUN,result.getResultState()); 
		
		eventDate = ""; // 
		year = "1980";
		month = "5";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-05-06", result.getResult().get("dwc:eventDate")); 
		
		eventDate = ""; 
		year = "1980";
		month = "12";
		day = "03";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-12-03", result.getResult().get("dwc:eventDate")); 
		
		eventDate = ""; 
		year = "1980";
		month = "12";
		day = "";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-12", result.getResult().get("dwc:eventDate")); 	
		
		eventDate = ""; 
		year = "1980";
		month = "";
		day = "";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1980", result.getResult().get("dwc:eventDate")); 			
		
		eventDate = ""; 
		year = "1640";
		month = "2";
		day = "05";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1640-02-05", result.getResult().get("dwc:eventDate")); 			
		
		eventDate = ""; 
		year = "1931";
		month = "2";
		day = "31";   // no such day
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = ""; 
		year = "1931";
		month = "2";
		day = "29";   // no such day
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = ""; 
		year = "1932";
		month = "2";
		day = "29";   // leap day
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.FILLED_IN,result.getResultState());
		assertEquals("1932-02-29", result.getResult().get("dwc:eventDate")); 			
		
		eventDate = ""; 
		year = "1980";
		month = "text";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());

		eventDate = ""; 
		year = "";
		month = "5";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());	
		
		eventDate = ""; 
		year = "1980";
		month = "12";
		day = "text";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = ""; 
		year = "1980";
		month = "IV";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = ""; 
		year = "";  // no value provided for year
		month = "1";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());			
		
	}
	
	@Test
	public void testIsStartEndDayPossibleForYear() {
		EventDQValidation result = null;
		for (int year = 1900; year<=1903; year++) { 
			for (int day= 1; day<=365; day++) { 
				logger.debug(day + "-" + year);
				result = DwCEventDQ.endDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
				assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
				assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
				
				result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
				assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
				assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
			}
		}

		// A leap year
		int year = 1932;
		int day = 366;
		result = DwCEventDQ.endDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		
		// not a leap year
		year = 2001;
		day = 366;  // out of range
		result = DwCEventDQ.endDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());	
		result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());		
		
		year = 2001;
		day = 0;  // out of range
		result = DwCEventDQ.endDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());	
		result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());
		
		year = 2001;
		day = 367;  // out of range
		result = DwCEventDQ.endDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());	
		result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(EnumDQResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());		
		
		// test with no year
		day = 366;  
		result = DwCEventDQ.endDayOfYearInRangeForYear(Integer.toString(day),"");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day),"");
		assertEquals(EnumDQResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
	}
	
	@Test 
	public void testMonthStandardized() { 
		String month = "12";
		EventDQAmendment result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.NO_CHANGE, result.getResultState());
		
		month = "0";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.NO_CHANGE, result.getResultState());	
		
		month = "1414";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.NO_CHANGE, result.getResultState());		
		
		month = "";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		month = "randomtext";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		month = "Jan";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getResult().get("dwc:month"));
		
		month = "Jan.";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getResult().get("dwc:month"));		
		
		month = "January";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getResult().get("dwc:month"));	
		
		month = "JAn.";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getResult().get("dwc:month"));		
		
		month = "Feb.";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getResult().get("dwc:month"));		
		
		month = "Février";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getResult().get("dwc:month"));		
		
		month = "Fevrier";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getResult().get("dwc:month"));		
		
		month = "fevrier";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getResult().get("dwc:month"));		
		
		month = "Febbraio";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getResult().get("dwc:month"));		
		
		month = "Febrero";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getResult().get("dwc:month"));		
		
		month = "Februar";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getResult().get("dwc:month"));		
		
		month = "二";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getResult().get("dwc:month"));		
		
		month = "i";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getResult().get("dwc:month"));			
		
		month = "I";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getResult().get("dwc:month"));

		month = "II";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getResult().get("dwc:month"));
		
		month = "III";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("3", result.getResult().get("dwc:month"));
		
		month = "IV";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("4", result.getResult().get("dwc:month"));

		month = "V";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("5", result.getResult().get("dwc:month"));
		
		month = "VI";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("6", result.getResult().get("dwc:month"));		
		
		month = "VII";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("7", result.getResult().get("dwc:month"));

		month = "VIII";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("8", result.getResult().get("dwc:month"));
		
		month = "IX";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("9", result.getResult().get("dwc:month"));		
		
		month = "X";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("10", result.getResult().get("dwc:month"));

		month = "XI";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("11", result.getResult().get("dwc:month"));
		
		month = "XII";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.CHANGED, result.getResultState());
		assertEquals("12", result.getResult().get("dwc:month"));	
		
		month = "XIII";  // no month 13
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(EnumDQAmendmentResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
	}
	
}
