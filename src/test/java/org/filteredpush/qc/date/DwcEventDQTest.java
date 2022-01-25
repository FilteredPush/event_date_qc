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
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.CompletenessValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.NumericalValue;
import org.datakurator.ffdq.model.ResultState;
import org.joda.time.LocalDateTime;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DwcEventDQTest {
	private static final Log logger = LogFactory.getLog(DwcEventDQTest.class);

	@Test
	public void testMeasureDuration() { 
		DQResponse<NumericalValue> measure = DwCEventDQ.measureDurationSeconds("1880-05-08");
		Long seconds = (60l*60l*24l)-1l; 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureDurationSeconds("1880-05");
		seconds = (60l*60l*24l*31)-1l; 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureDurationSeconds("");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, measure.getResultState());
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#isDayInRange(java.lang.String)}.
	 */
	@Test
	public void testIsDayInRange() {
		DQResponse<ComplianceValue> result = null;
		for (int i = 1; i<=31; i++) { 
			result = DwCEventDQ.isDayInRange(Integer.toString(i));
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		result = DwCEventDQ.isDayInRange(" 1 ");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		result = DwCEventDQ.isDayInRange("01");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());

		int i=0;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());

		i=32;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());

		i=-1;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());

		i=Integer.MAX_VALUE;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());

		i=Integer.MIN_VALUE;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());

		result = DwCEventDQ.isDayInRange(null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCEventDQ.isDayInRange("");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
	
		result = DwCEventDQ.isDayInRange(" ");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCEventDQ.isDayInRange("A");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCEventDQ.isDayInRange("1.5");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCEventDQ.isDayInRange("**");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCEventDQ.isDayInRange("1st");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		result = DwCEventDQ.isDayInRange("2nd");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#isMonthInRange(java.lang.String)}.
	 */
	@Test
	public void testIsMonthInRange() {
		DQResponse<ComplianceValue> result = null;
		for (int i = 1; i<=12; i++) { 
			result = DwCEventDQ.isMonthInRange(Integer.toString(i));
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		result = DwCEventDQ.isMonthInRange(" 1 ");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		result = DwCEventDQ.isMonthInRange("01");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());

		int i=0;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());

		i=13;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());

		i=-1;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());

		i=Integer.MAX_VALUE;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());

		i=Integer.MIN_VALUE;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	

		result = DwCEventDQ.isMonthInRange(null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange("");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange(" ");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange("A");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange("1.5");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange("**");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		result = DwCEventDQ.isMonthInRange("Jan");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isMonthInRange("January");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
	}

	@Test
	public void testDurationInYear() {
		DQResponse<ComplianceValue> result = null;
		for (int i = 1981; i<=1983; i++) {
			// not leap years
			result = DwCEventDQ.isEventDateJulianYearOrLess(Integer.toString(i));
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			result = DwCEventDQ.isEventDateYearOrLess(Integer.toString(i));
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		}
		// Leap year
		result = DwCEventDQ.isEventDateJulianYearOrLess("1980");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		result = DwCEventDQ.isEventDateYearOrLess("1980");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
	}
	
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#isDayPossibleForMonthYear(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testIsDayPossibleForMonthYear() {
		DQResponse<ComplianceValue> result = null;
		for (int year = 1900; year<=1903; year++) { 
			for (int month = 1; month<=12; month++) { 
				for (int day= 1; day<=27; day++) { 
					result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
					logger.debug(year + " " + month + " " + day);
					assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
					assertEquals(ComplianceValue.COMPLIANT, result.getValue());
				}
			}
		}

		// Not a leap year
		int year = 2001;
		int month = 2;
		int day = 28;
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = 2001;
		month = 2;
		day = 29;  
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		year = 2001;
		month = 2;
		day = 30;  
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		// Leap year
		year = 2000;
		month = 2;
		day = 29;  
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = 2000;
		month = 2;
		day = 30;
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		year = 2000;
		month = 2;
		day = 31;
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());

	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#dayMonthTransposition(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDayMonthTransposition() {
		String month = "30";
		String day = "11";
		DQResponse<AmendmentValue> result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(ResultState.TRANSPOSED, result.getResultState());
		assertEquals("11",result.getValue().getObject().get("dwc:month"));
		assertEquals("30",result.getValue().getObject().get("dwc:day"));

		result = DwCEventDQ.dayMonthTransposition(day,month);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());

		day = "5"; 
		month = "11";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());   

		day = "15"; 
		month = "8";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());  

		day = "15"; 
		month = "15";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	

		day = "15"; 
		month = "34";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());      	

		day = "-1"; 
		month = "15";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());      	

		day = "-1"; 
		month = "5";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());      	

		result = DwCEventDQ.dayMonthTransposition(day,"");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
	}
	
	@Test
	public void testExtractDateFromVerbatim() { 
		String eventDate = "";
		String verbatimEventDate = "Jan 1884";
		DQResponse<AmendmentValue> result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1884-01",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());

		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1884-03-01",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());		
		
		eventDate = "1884";
		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		assertEquals(0,result.getValue().getObject().size());			
		eventDate = "1884-03-01";
		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		assertEquals(0,result.getValue().getObject().size());		
		
		eventDate = null;
		verbatimEventDate = "";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(0,result.getValue().getObject().size());			
		
		eventDate = null;
		verbatimEventDate = "5-8-1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.AMBIGUOUS, result.getResultState());
		assertEquals("1884-05-08/1884-08-05",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());		
		
		eventDate = null;
		verbatimEventDate = "2001/Feb/29";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(0,result.getValue().getObject().size());			
		
	}
	
	@Test
	public void testCorrectEventDateFormat() { 
		String eventDate = "Jan 1884";
		DQResponse<AmendmentValue> result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1884-01",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());	
		
		eventDate = "1 Jan 1884";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1884-01-01",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());		
		
		/* Case that inspired adding this method, typical use of / instead of - in dates. 
		 */
		eventDate = "1884/01/02";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1884-01-02",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());		
		
		eventDate = "1884-01-02";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());		
	
		eventDate = "";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		
		result = DwCEventDQ.correctEventDateFormat(null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "2305345ifo342fd,cofaga";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "02/03/1884";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(ResultState.AMBIGUOUS, result.getResultState());	
		assertEquals("1884-02-03/1884-03-02",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());				
		
	}
	
	@Test
	public void testEventDateFromYearStartEndDay() { 
		String eventDate = "";
		String year = "1980";
		String startDay = "5";
		String endDay = "6";
		DQResponse<AmendmentValue> result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-01-05/1980-01-06", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "1980"; // eventDate contains a value
		year = "1980";
		startDay = "5";
		endDay = "6";
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.NOT_RUN,result.getResultState()); 
		
		eventDate = "";
		year = "1980";
		startDay = "day5";  // not a number
		endDay = "6";
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = "";
		year = "1980";  // a leap year
		startDay = "1";
		endDay = "366";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-01-01/1980-12-31", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDay = "1";
		endDay = "365";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1981-01-01/1981-12-31", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDay = "45";
		endDay = "280";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1981-02-14/1981-10-07", result.getValue().getObject().get("dwc:eventDate"));		
		
		eventDate = "";
		year = "1980";  // a leap year
		startDay = "45";  // spans leap day
		endDay = "280";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-02-14/1980-10-06", result.getValue().getObject().get("dwc:eventDate"));			
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDay = "1";
		endDay = "366";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());		
		
		eventDate = "";
		year = "1980";
		startDay = "900";  // out of range
		endDay = "920";
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = "";
		year = "1980";
		startDay = "200";  // start and end reversed or in different years.
		endDay = "10";
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());	
		
		// Example given in TG2 definition: 
		//dwc:year=1999, dwc:startDayOfYear=123, dwc:endDayOfYear=125 
		//therefore dwc:eventDate=1999-05-03/1999-05-05
		eventDate = "";
		year = "1999"; 
		startDay = "123"; 
		endDay = "125";  
		result = DwCEventDQ.eventDateFromYearStartEndDay(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1999-05-03/1999-05-05", result.getValue().getObject().get("dwc:eventDate"));	
		
	}
	
	
	@Test
	public void testEventDateFromMonthDay() { 
		String eventDate = "";
		String year = "1980";
		String month = "5";
		String day = "6";
		DQResponse<AmendmentValue> result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-05-06", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "1980"; // eventDate contains a value
		year = "1980";
		month = "5";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.NOT_RUN,result.getResultState()); 
		
		eventDate = ""; // 
		year = "1980";
		month = "5";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-05-06", result.getValue().getObject().get("dwc:eventDate")); 
		
		eventDate = ""; 
		year = "1980";
		month = "12";
		day = "03";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-12-03", result.getValue().getObject().get("dwc:eventDate")); 
		
		eventDate = ""; 
		year = "1980";
		month = "12";
		day = "";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-12", result.getValue().getObject().get("dwc:eventDate")); 	
		
		eventDate = ""; 
		year = "1980";
		month = "";
		day = "";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980", result.getValue().getObject().get("dwc:eventDate")); 			
		
		eventDate = ""; 
		year = "1640";
		month = "2";
		day = "05";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1640-02-05", result.getValue().getObject().get("dwc:eventDate")); 			
		
		eventDate = ""; 
		year = "1931";
		month = "2";
		day = "31";   // no such day
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = ""; 
		year = "1931";
		month = "2";
		day = "29";   // no such day
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = ""; 
		year = "1932";
		month = "2";
		day = "29";   // leap day
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1932-02-29", result.getValue().getObject().get("dwc:eventDate")); 			
		
		eventDate = ""; 
		year = "1980";
		month = "text";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());

		eventDate = ""; 
		year = "";
		month = "5";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());	
		
		eventDate = ""; 
		year = "1980";
		month = "12";
		day = "text";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = ""; 
		year = "1980";
		month = "IV";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = ""; 
		year = "";  // no value provided for year
		month = "1";
		day = "6";
		result = DwCEventDQ.eventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());			
		
	}
	
	@Test
	public void testIsStartEndDayPossibleForYear() {
		DQResponse<ComplianceValue> result = null;
		for (int year = 1900; year<=1903; year++) { 
			for (int day= 1; day<=365; day++) { 
				logger.debug(day + "-" + year);
				result = DwCEventDQ.isEndDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
				assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
				assertEquals(ComplianceValue.COMPLIANT, result.getValue());
				
				result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
				assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
				assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			}
		}

		// A leap year
		int year = 1932;
		int day = 366;
		result = DwCEventDQ.isEndDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		// not a leap year
		year = 2001;
		day = 366;  // out of range
		result = DwCEventDQ.isEndDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
		year = 2001;
		day = 0;  // out of range
		result = DwCEventDQ.isEndDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		year = 2001;
		day = 367;  // out of range
		result = DwCEventDQ.isEndDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
		// test with no year
		day = 366;  
		result = DwCEventDQ.isEndDayOfYearInRangeForYear(Integer.toString(day),"");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.startDayOfYearInRangeForYear(Integer.toString(day),"");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
	}
	
	@Test 
	public void testMonthStandardized() { 
		String month = "12";
		DQResponse<AmendmentValue> result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		
		month = "0";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		month = "1414";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());		
		
		month = "";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		month = "randomtext";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		month = "Jan";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));
		
		month = "Jan.";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));		
		
		month = "January";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));	
		
		month = "JAn.";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));		
		
		month = "Feb.";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "Février";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "Fevrier";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "fevrier";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "Febbraio";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "Febrero";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "Februar";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "二";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "i";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));			
		
		month = "I";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));

		month = "II";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));
		
		month = "III";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("3", result.getValue().getObject().get("dwc:month"));
		
		month = "IV";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("4", result.getValue().getObject().get("dwc:month"));

		month = "V";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("5", result.getValue().getObject().get("dwc:month"));
		
		month = "VI";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("6", result.getValue().getObject().get("dwc:month"));		
		
		month = "VII";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("7", result.getValue().getObject().get("dwc:month"));

		month = "VIII";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("8", result.getValue().getObject().get("dwc:month"));
		
		month = "IX";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("9", result.getValue().getObject().get("dwc:month"));		
		
		month = "X";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("10", result.getValue().getObject().get("dwc:month"));

		month = "XI";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("11", result.getValue().getObject().get("dwc:month"));
		
		month = "XII";
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("12", result.getValue().getObject().get("dwc:month"));	
		
		month = "XIII";  // no month 13
		result = DwCEventDQ.standardizeMonth(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
	}
	
	@Test 
	public void testDayStandardized() {
		String day = "1";
		DQResponse<AmendmentValue> result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		for (int i=2; i<32; i++) { 
		   result = DwCEventDQ.standardizeDay(Integer.toString(i));
		   assertEquals(ResultState.NO_CHANGE, result.getResultState());
		}
		
		day = "0";
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		day = "32";
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());		
		
		day = "";
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		day = "Feb.";
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = "1st";
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:day"));		
		
		day = "2nd.";
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:day"));
		
		day = "31st";
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("31", result.getValue().getObject().get("dwc:day"));		
		
		day = "32nd";
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		day = "one";
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		day = "o1";  // typo for 01
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		day = "O1";  // typo for 01
		result = DwCEventDQ.standardizeDay(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
	}
	
	@Test
	public void testEventEmpty() {
		DQResponse<ComplianceValue> result = null;
		String eventDate = null;
		String verbatimEventDate = null;
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		String eventTime = null;
		
		result = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		eventDate = "";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		eventDate = "";
		verbatimEventDate = " ";
		year = " ";
		month = "NULL";
		day = "\t";
		startDayOfYear = "\n";
		endDayOfYear = "  ";
		eventTime = "                        ";
		
		result = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		
		eventDate = "text";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
	
		// Removing, eventTime removed from the test.
//		eventDate = "";
//		verbatimEventDate = "";
//		year = "";
//		month = "";
//		day = "";
//		startDayOfYear = "";
//		endDayOfYear = "";
//		eventTime = "text";
//		
//		result = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
//		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
//		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		eventDate = "1880-02-03";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		
		// removing, eventTime removed from the method.
//		eventDate = "";
//		verbatimEventDate = "";
//		year = "";
//		month = "";
//		day = "";
//		startDayOfYear = "";
//		endDayOfYear = "";
//		eventTime = "14:50";
//		
//		result = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
//		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
//		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		eventDate = "";
		verbatimEventDate = "";
		year = "";
		month = "2";
		day = "4";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		eventDate = "";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "1";
		endDayOfYear = "365";
		eventTime = "";
		
		result = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		eventDate = "";
		verbatimEventDate = "Summer";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
	}
	
	@Test
	public void testMeasureEventEmpty() {
		DQResponse<CompletenessValue> result = null;
		String eventDate = null;
		String verbatimEventDate = null;
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		String eventTime = null;
		
		result = DwCEventDQ.measureEventCompleteness(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(CompletenessValue.NOT_COMPLETE, result.getValue());	
		
		eventDate = "";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.measureEventCompleteness(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(CompletenessValue.NOT_COMPLETE, result.getValue());	
		
		eventDate = "";
		verbatimEventDate = " ";
		year = " ";
		month = "NULL";
		day = "\t";
		startDayOfYear = "\n";
		endDayOfYear = "  ";
		eventTime = "                        ";
		
		result = DwCEventDQ.measureEventCompleteness(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(CompletenessValue.COMPLETE, result.getValue());	
		
		eventDate = "text";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.measureEventCompleteness(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(CompletenessValue.COMPLETE, result.getValue());	
		
		// Removing, eventTime removed from the method.
//		eventDate = "";
//		verbatimEventDate = "";
//		year = "";
//		month = "";
//		day = "";
//		startDayOfYear = "";
//		endDayOfYear = "";
//		eventTime = "text";
//		
//		result = DwCEventDQ.measureEventCompleteness(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
//		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
//		assertEquals(CompletenessValue.COMPLETE, result.getValue());	
		
		eventDate = "1880-02-03";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.measureEventCompleteness(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(CompletenessValue.COMPLETE, result.getValue());	
		
		// Removing, eventTime removed from the method.
//		eventDate = "";
//		verbatimEventDate = "";
//		year = "";
//		month = "";
//		day = "";
//		startDayOfYear = "";
//		endDayOfYear = "";
//		eventTime = "14:50";
//		
//		result = DwCEventDQ.measureEventCompleteness(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
//		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
//		assertEquals(CompletenessValue.COMPLETE, result.getValue());	
		
		eventDate = "";
		verbatimEventDate = "";
		year = "";
		month = "2";
		day = "4";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.measureEventCompleteness(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(CompletenessValue.COMPLETE, result.getValue());	
		
		eventDate = "";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "1";
		endDayOfYear = "365";
		eventTime = "";
		
		result = DwCEventDQ.measureEventCompleteness(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(CompletenessValue.COMPLETE, result.getValue());	
		
		eventDate = "";
		verbatimEventDate = "Summer";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.measureEventCompleteness(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(CompletenessValue.COMPLETE, result.getValue());	
	}	
	
	@Test
	public void testEventDateConsistentWithAtomic() {
		String eventDate = null;
		String verbatimEventDate = null;
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		String eventTime = null;
		
		DQResponse<ComplianceValue> result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		eventDate = "";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
			
		eventDate = "1980-02-14";
		verbatimEventDate = "";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		eventDate = "1980-02-14";
		verbatimEventDate = "February 14, 1980";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		eventDate = "1980-02-14";
		verbatimEventDate = "February 14, 1980";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = Integer.toString(31 + 14);
		eventTime = " 12:00";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		eventDate = "";
		verbatimEventDate = "February 14, 1980";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = Integer.toString(31 + 14);
		eventTime = " 12:00";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		eventDate = "1980-02-14";
		verbatimEventDate = "February 15, 1980";
		year = "1980";
		month = "2";
		day = "13";
		startDayOfYear = Integer.toString(31 + 12);
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
		eventDate = "";
		verbatimEventDate = "February 15, 1980";
		year = "1980";
		month = "2";
		day = "13";
		startDayOfYear = Integer.toString(31 + 12);
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		eventDate = "1980-02-14";
		verbatimEventDate = "February 14, 1980";
		year = "1980";
		month = "2";
		day = "15";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
		eventDate = "1980-02-14";
		verbatimEventDate = "February 14, 1980";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 15);
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		eventDate = "1980-02-14";
		verbatimEventDate = "February 14, 1980";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = Integer.toString(31 + 25);    // date range not in eventDate
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
		eventDate = "1980-02-14/1980-02-25";
		verbatimEventDate = "February 14-25, 1980";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = Integer.toString(31 + 25);    // date range in eventDate
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		logger.debug(result.getComment());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		eventDate = "1980-02-14/1981-01-12";
		verbatimEventDate = "February 14, 1980 to January 1, 1981";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = Integer.toString(12);    // date range in eventDate, but in previous year
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		logger.debug(result.getComment());
 		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		
		eventDate = "1980-02";
		verbatimEventDate = "February 1980";
		year = "1980";
		month = "2";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";    
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		logger.debug(result.getComment());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		eventDate = "1980-02";
		verbatimEventDate = "February 1980";
		year = "1980";
		month = "2";
		day = "";
		startDayOfYear = "32";
		assertTrue(DateUtils.includesLeapDay(eventDate));
		endDayOfYear = Integer.toString(31 + 29);    
		eventTime = "";
		
		result = DwCEventDQ.isEventDateConsistentWithAtomic(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear, eventTime);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		logger.debug(result.getComment());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				
		
	}
	
	@Test
	public void testEventDateEmpty() {
		String eventDate = null;
		DQResponse<ComplianceValue> result = DwCEventDQ.validationEventdateEmpty(eventDate);
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		eventDate = "";
		result = DwCEventDQ.validationEventdateEmpty(eventDate);
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		eventDate = "\n";
		result = DwCEventDQ.validationEventdateEmpty(eventDate);
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		eventDate = " ";
		result = DwCEventDQ.validationEventdateEmpty(eventDate);
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		eventDate = "NULL";
		result = DwCEventDQ.validationEventdateEmpty(eventDate);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		eventDate = "1840-01-05";
		result = DwCEventDQ.validationEventdateEmpty(eventDate);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		eventDate = "-1";
		result = DwCEventDQ.validationEventdateEmpty(eventDate);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		eventDate = "text";
		result = DwCEventDQ.validationEventdateEmpty(eventDate);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
	}
	
	@Test
	public void testYearEmpty() {
		String year = null;
		DQResponse<ComplianceValue> result = DwCEventDQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year="";
		result = DwCEventDQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year="NULL";
		result = DwCEventDQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		year="1880";
		result = DwCEventDQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				

	    year = "-1";
		result = DwCEventDQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				
				
		year = "text";
		result = DwCEventDQ.validationYearEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				
	}
	
	@Test
	public void testFillInEventFromEventDate() {
	
		String eventDate = null;
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		DQResponse<AmendmentValue> result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "";
		year = "1918";
		month = "1";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "1918/1/15";
		year = "";
		month = "";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "1918-33-15";
		year = "";
		month = "";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "1918-01-15";
		year = "1918";
		month = "1";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());  
		
		eventDate = "1918-01-15";
		year = "1";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "1";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());  		
		
		eventDate = "1918/1/15";  // incorrect format
		year = "1918";
		month = "1";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
			
		eventDate = "1918-01-15/1900-01-15";  // end before start
		year = "1918";
		month = "1";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getResultState().getLabel());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "1884-01-15";
		year = "";
		month = "1";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getResultState().getLabel());
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884",result.getValue().getObject().get("dwc:year"));
		assertEquals(1,result.getValue().getObject().size());	
		
		eventDate = "1884-01-15";
		year = "";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "1";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884",result.getValue().getObject().get("dwc:year"));
		assertEquals(1,result.getValue().getObject().size());			
		
		eventDate = "1884-01-15";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884",result.getValue().getObject().get("dwc:year"));
		assertEquals("1",result.getValue().getObject().get("dwc:month"));
		assertEquals("15",result.getValue().getObject().get("dwc:day"));
		assertEquals(3,result.getValue().getObject().size());		
		
		eventDate = "1884-01-15";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884",result.getValue().getObject().get("dwc:year"));
		assertEquals("1",result.getValue().getObject().get("dwc:month"));
		assertEquals("15",result.getValue().getObject().get("dwc:day"));
		assertEquals("15",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("15",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(5,result.getValue().getObject().size());	
		
		eventDate = "1884-01-15";
		year = "";
		month = "1";
		day = "15";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884",result.getValue().getObject().get("dwc:year"));
		assertEquals("15",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("15",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(3,result.getValue().getObject().size());		
		
		eventDate = "1884-01-15";
		year = "NULL";
		month = "1";
		day = "15";
		startDayOfYear = "NULL";
		endDayOfYear = "NULL";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());

		eventDate = "1884-01-15";
		year = " ";
		month = "1";
		day = "15";
		startDayOfYear = "\n";
		endDayOfYear = "   ";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884",result.getValue().getObject().get("dwc:year"));
		assertEquals("15",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("15",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(3,result.getValue().getObject().size());			
		
		eventDate = "1884-01-15/1884-02-03";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884",result.getValue().getObject().get("dwc:year"));
		assertEquals("1",result.getValue().getObject().get("dwc:month"));
		assertEquals("15",result.getValue().getObject().get("dwc:day"));
		assertEquals("15",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("34",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(5,result.getValue().getObject().size());
		
		eventDate = "1884-01-15/1884-02-3";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		eventDate = "1955-12-31/1956-01-03";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1955",result.getValue().getObject().get("dwc:year"));
		assertEquals("12",result.getValue().getObject().get("dwc:month"));
		assertEquals("31",result.getValue().getObject().get("dwc:day"));
		assertEquals("365",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("3",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(5,result.getValue().getObject().size());	
		
		eventDate = "1955-02";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1955",result.getValue().getObject().get("dwc:year"));
		assertEquals("2",result.getValue().getObject().get("dwc:month"));
		assertEquals("1",result.getValue().getObject().get("dwc:day"));
		assertEquals("32",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals(Integer.toString(31+28),result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(5,result.getValue().getObject().size());			
		
		eventDate = "1955";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getResultState().getLabel());
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1955",result.getValue().getObject().get("dwc:year"));
		assertEquals("1",result.getValue().getObject().get("dwc:month"));
		assertEquals("1",result.getValue().getObject().get("dwc:day"));
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(5,result.getValue().getObject().size());			
		
		eventDate = "1955-002";  // three digits, day of year.
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1955",result.getValue().getObject().get("dwc:year"));
		assertEquals("1",result.getValue().getObject().get("dwc:month"));
		assertEquals("2",result.getValue().getObject().get("dwc:day"));
		assertEquals("2",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("2",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(5,result.getValue().getObject().size());			
		
    	// test leap day handling
    	
		eventDate = "1980";
		year = "1980";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("366",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());	
		
		eventDate = "1981";
		year = "1981";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());	
		
		eventDate = "1982";
		year = "1982";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());			
		
		eventDate = "1983";
		year = "1983";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());
		
		eventDate = "1984";
		year = "1984";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("366",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());	
		
		eventDate = "1600";
		year = "year";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("366",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());	
		
		eventDate = "1700";
		year = "year";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());			
		
		eventDate = "1800";
		year = "year";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());			
		
		eventDate = "1900";
		year = "year";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());	
		
		eventDate = "2000";
		year = "year";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.fillInEventFromEventDate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("366",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());			
		
	}
	
	@Test
	public void testYearInRange() {
		String year = null;
		
		DQResponse<ComplianceValue> result = DwCEventDQ.isYearInRange(year, null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		result = DwCEventDQ.isYearInRange(year, 1900);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "1700";
		result = DwCEventDQ.isYearInRange(year, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "1699";
		result = DwCEventDQ.isYearInRange(year, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
		Integer upperBound = LocalDateTime.now().getYear();
		for (int i=1701; i<=upperBound; i++) { 
			result = DwCEventDQ.isYearInRange(Integer.toString(i), null);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		
		year = Integer.toString(upperBound + 1);
		result = DwCEventDQ.isYearInRange(year, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year = "1899";
		result = DwCEventDQ.isYearInRange(year, 1900);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year = "1900";
		result = DwCEventDQ.isYearInRange(year, 1900);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		
		year = "1901";
		result = DwCEventDQ.isYearInRange(year, 1900);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		year = Integer.toString(LocalDateTime.now().getYear());
		result = DwCEventDQ.isYearInRange(year, 1900);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		year = Integer.toString(LocalDateTime.now().getYear() + 1);
		result = DwCEventDQ.isYearInRange(year, 1900);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());				
		
	}
	
	@Test
	public void testEventDateWhollyInRange() {
		String eventDate = null;
		
		DQResponse<ComplianceValue> result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1900", null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "13ab/02/r3:06";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1900", null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		
		eventDate = "1700";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	

		eventDate = "1700-01";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		eventDate = "1700-01-01";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		eventDate = "1880-06-15";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		
		eventDate = "1954/1955";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		eventDate = "1599";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		eventDate = "1699";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		eventDate = "1699-01-01";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		
		eventDate = "1599";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, "1900-01-01");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "0001-01-02";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "0001-01-01", null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "-2000";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
	
		eventDate = "1599-12";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			

		eventDate = "1599-12-31";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		eventDate = "1599-365";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
	
		eventDate = "1800-01-01";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1800", null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				
			
		eventDate = "1900-01-01";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1800", "2000");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		eventDate = "1799-12-31";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, "1800", "2000");
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		eventDate = "1599-12-31/1600-01-01";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		eventDate = "1599-12-31/1600-12-31";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
		eventDate = "1599-12-31/1800-12-31";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		eventDate = "1599-01-01/1599-12-31";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
    	Integer upperBound = LocalDateTime.now().getYear();
		eventDate = "1699-12-31/" + Integer.toString(upperBound + 1).trim();
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		// beginning of next year.
		eventDate = Integer.toString(upperBound + 1).trim() + "-01-01";
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
		// next year.
		eventDate = Integer.toString(upperBound + 1).trim();
		result = DwCEventDQ.validationEventdateOutofrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
	}	
	
	
}
