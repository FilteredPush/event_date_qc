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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.CompletenessValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.NumericalValue;
import org.datakurator.ffdq.model.ResultState;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DwcEventDQTest {
	private static final Log logger = LogFactory.getLog(DwcEventDQTest.class);

	@Test
	public void testMeasureDuration() { 
		DQResponse<NumericalValue> measure = DwCEventDQ.measureEventdateDurationinseconds("1880-05-08");
		Long seconds = (60l*60l*24l); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		assertNotNull(measure.getComment());  // the content of the comment is not standardized, but one must be present.
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1880-05");
		seconds = (60l*60l*24l*31); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1881");
		seconds = (60l*60l*24l*365); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1880"); // leap year
		seconds = (60l*60l*24l*366); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1880-02");  // Feb in leap year
		seconds = (60l*60l*24l*29); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());		
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1881-02");
		seconds = (60l*60l*24l*28); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());			
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1969");  
		seconds = (60l*60l*24l*365l); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1980");  
		seconds = (60l*60l*24l*366l); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1972");  // leap year with two leap seconds
		// if leap seconds were included would be 31622401, but guidance is to exclude leap seconds from this measure
		// seconds = (60l*60l*24l*366l)+1l; 
		seconds = (60l*60l*24l*366l); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1973-12-31"); // leap second
		// if leap seconds were included would be 31622401, but guidance is to exclude leap seconds from this measure
		seconds = (60l*60l*24l); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());		
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1881-02");
		seconds = (60l*60l*24l*28); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("2007-03-01/2008-02-29");
		seconds = (60l*60l*24l*366); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("2007-03-01/2008-05-11");
		seconds = (60l*60l*24l*(366+31+30+11)); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		LocalDateTime t1 = LocalDateTime.parse("2007-03-01T13:00:00");
		LocalDateTime t2 = LocalDateTime.parse("2008-05-11T15:30:00");
		Duration d1 = Duration.between(t1, t2);
		logger.debug(d1.getSeconds());  
		t1 = LocalDateTime.parse("2007-03-01T00:00:00");
		t2 = LocalDateTime.parse("2008-03-01T00:00:00");
		d1 = Duration.between(t1, t2);
		logger.debug(d1.getSeconds());  
		t1 = LocalDateTime.parse("2006-03-01T00:00:00");
		t2 = LocalDateTime.parse("2007-03-01T00:00:00");
		d1 = Duration.between(t1, t2);		
		logger.debug(d1.getSeconds());  
		t1 = LocalDateTime.parse("2006-03-01T13:00:00");
		t2 = LocalDateTime.parse("2007-05-11T15:30:00");
		d1 = Duration.between(t1, t2);
		logger.debug(d1.getSeconds()); 
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("2007-03-01T13:00:00Z/2008-05-11T15:30:00Z");
		seconds = (long) ((60l*60l*24l*(366l+31l+30l+10l)) + (60l*60l*2.5)); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("2007-03-01T13:00:00Z/2008-05-11T15:30:00Z");
		seconds = 37765800l; 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("2006-03-01T13:00:00Z/2007-05-11T15:30:00Z");
		seconds = (long) ((60l*60l*24l*(365l+31l+30l+10l)) + (60l*60l*2.5)); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("2006-03-08/10");
		seconds = (long) 60l*60l*24l * 3l; //  259200
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		assertEquals(seconds, measure.getObject());
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#validationDayStandard(java.lang.String)}.
	 */
	@Test
	public void testIsDayInRange() {
		DQResponse<ComplianceValue> result = null;
		for (int i = 1; i<=31; i++) { 
			result = DwCEventDQ.validationDayStandard(Integer.toString(i));
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		for (int i = 33; i<=40; i++) { 
			result = DwCEventDQ.validationDayStandard(Integer.toString(i));
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
			assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		}
		result = DwCEventDQ.validationDayStandard(" 1 ");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		result = DwCEventDQ.validationDayStandard("01");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());

		int i=0;
		result = DwCEventDQ.validationDayStandard(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

		i=32;
		result = DwCEventDQ.validationDayStandard(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

		i=-1;
		result = DwCEventDQ.validationDayStandard(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

		i=Integer.MAX_VALUE;
		result = DwCEventDQ.validationDayStandard(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

		i=Integer.MIN_VALUE;
		result = DwCEventDQ.validationDayStandard(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

		result = DwCEventDQ.validationDayStandard(null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCEventDQ.validationDayStandard("");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
	
		result = DwCEventDQ.validationDayStandard(" ");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertNull(result.getValue());
		
		result = DwCEventDQ.validationDayStandard("A");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCEventDQ.validationDayStandard("1.5");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCEventDQ.validationDayStandard("**");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCEventDQ.validationDayStandard("1st");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		result = DwCEventDQ.validationDayStandard("2nd");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#validationMonthStandard(java.lang.String)}.
	 */
	@Test
	public void testIsMonthInRange() {
		DQResponse<ComplianceValue> result = null;
		for (int i = 1; i<=12; i++) { 
			result = DwCEventDQ.validationMonthStandard(Integer.toString(i));
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		result = DwCEventDQ.validationMonthStandard(" 1 ");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		result = DwCEventDQ.validationMonthStandard("01");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());

		int i=0;
		result = DwCEventDQ.validationMonthStandard(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

		i=13;
		result = DwCEventDQ.validationMonthStandard(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

		i=-1;
		result = DwCEventDQ.validationMonthStandard(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

		i=Integer.MAX_VALUE;
		result = DwCEventDQ.validationMonthStandard(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

		i=Integer.MIN_VALUE;
		result = DwCEventDQ.validationMonthStandard(Integer.toString(i));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	

		result = DwCEventDQ.validationMonthStandard(null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.validationMonthStandard("");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.validationMonthStandard(" ");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		result = DwCEventDQ.validationMonthStandard("A");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		result = DwCEventDQ.validationMonthStandard("1.5");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		result = DwCEventDQ.validationMonthStandard("**");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		result = DwCEventDQ.validationMonthStandard("Jan");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		result = DwCEventDQ.validationMonthStandard("January");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
	}

	@Test
	public void testDurationInYear() {
		DQResponse<ComplianceValue> result = null;
		for (int i = 1981; i<=1983; i++) {
			// not leap years
			result = DwCEventDQ.isEventDateJulianYearOrLess(Integer.toString(i));
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			result = DwCEventDQ.isEventDateYearOrLess(Integer.toString(i));
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
			assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		}
		// Leap year
		result = DwCEventDQ.isEventDateJulianYearOrLess("1980");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		result = DwCEventDQ.isEventDateYearOrLess("1980");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
	}
	
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#validationDayInrange(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testIsDayPossibleForMonthYear() {
		DQResponse<ComplianceValue> result = null;
		for (int year = 1900; year<=1903; year++) { 
			for (int month = 1; month<=12; month++) { 
				for (int day= 1; day<=27; day++) { 
					result = DwCEventDQ.validationDayInrange(Integer.toString(year), Integer.toString(month), Integer.toString(day));
					logger.debug(year + " " + month + " " + day);
					assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
					assertEquals(ComplianceValue.COMPLIANT, result.getValue());
				}
			}
		}

		// Not a leap year
		int year = 2001;
		int month = 2;
		int day = 28;
		result = DwCEventDQ.validationDayInrange(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = 2001;
		month = 2;
		day = 29;  
		result = DwCEventDQ.validationDayInrange(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		year = 2001;
		month = 2;
		day = 30;  
		result = DwCEventDQ.validationDayInrange(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		// Leap year
		year = 2000;
		month = 2;
		day = 29;  
		result = DwCEventDQ.validationDayInrange(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = 2000;
		month = 2;
		day = 30;
		result = DwCEventDQ.validationDayInrange(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		year = 2000;
		month = 2;
		day = 31;
		result = DwCEventDQ.validationDayInrange(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());

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
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());

		day = "5"; 
		month = "11";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());   

		day = "15"; 
		month = "8";
		result = DwCEventDQ.dayMonthTransposition(month,day);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());  

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
	public void testAmendmentEventdateFromVerbatim() { 
		
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is not EMPTY 
        // or the value of dwc:verbatimEventDate is EMPTY or not unambiguously 
        // interpretable as an ISO 8601-1:2019 date; AMENDED if the 
        // value of dwc:eventDate was unambiguously interpreted from 
        // dwc:verbatimEventDate; otherwise NOT_AMENDED 
		
		String eventDate = "";
		String verbatimEventDate = "Jan 1884";
		DQResponse<AmendmentValue> result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884-01",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());

		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884-03-01",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());		
		
		eventDate = "1884";
		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertEquals(0,result.getValue().getObject().size());	
		
		eventDate = "1884-03-01";
		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertEquals(0,result.getValue().getObject().size());		
		
		eventDate = null;
		verbatimEventDate = "";
		result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(0,result.getValue().getObject().size());			
		
		eventDate = null;
		verbatimEventDate = "5-8-1884";
		result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertEquals(0, result.getValue().getObject().size());
		
		eventDate = null;
		verbatimEventDate = "2001/Feb/29";
		result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(0,result.getValue().getObject().size());			
		
	}
	
	@Test
	public void testCorrectEventDateFormat() { 
		String eventDate = "Jan 1884";
		DQResponse<AmendmentValue> result = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("1884-01",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());	
		
		eventDate = "1 Jan 1884";
		result = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("1884-01-01",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());		
		
		/* Case that inspired adding this method, typical use of / instead of - in dates. 
		 */
		eventDate = "1884/01/02";
		result = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("1884-01-02",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());		
		
		eventDate = "1884-01-02";
		result = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());		
	
		eventDate = "";
		result = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		
		result = DwCEventDQ.amendmentEventdateStandardized(null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "2305345ifo342fd,cofaga";
		result = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());	
		
		eventDate = "02/03/1884";
		result = DwCEventDQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());	
		//assertEquals("1884-02-03/1884-03-02",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(0,result.getValue().getObject().size());				
		
	}
	
	@Test
	public void testEventDateFromYearStartEndDay() { 
		String eventDate = "";
		String year = "1980";
		String startDay = "5";
		String endDay = "6";
		DQResponse<AmendmentValue> result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1980-01-05/1980-01-06", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "1980"; // eventDate contains a value
		year = "1980";
		startDay = "5";
		endDay = "6";
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState()); 
		
		eventDate = "";
		year = "1980";
		startDay = "day5";  // not a number
		endDay = "6";
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = "";
		year = "1980";  // a leap year
		startDay = "1";
		endDay = "366";  
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1980", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDay = "1";
		endDay = "365";  
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1981", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDay = "45";
		endDay = "280";  
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1981-02-14/1981-10-07", result.getValue().getObject().get("dwc:eventDate"));		
		
		eventDate = "";
		year = "1980";  // a leap year
		startDay = "45";  // spans leap day
		endDay = "280";  
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1980-02-14/1980-10-06", result.getValue().getObject().get("dwc:eventDate"));			
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDay = "1";
		endDay = "366";  
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.NOT_AMENDED.getLabel(),result.getResultState().getLabel());		
		
		eventDate = "";
		year = "1980";
		startDay = "900";  // out of range
		endDay = "920";
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(),result.getResultState().getLabel());
		
		eventDate = "";
		year = "1980";
		startDay = "200";  // start and end reversed or in different years.
		endDay = "10";
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.NOT_AMENDED.getLabel(),result.getResultState().getLabel());
		
		// Example given in TG2 definition: 
		//dwc:year=1999, dwc:startDayOfYear=123, dwc:endDayOfYear=125 
		//therefore dwc:eventDate=1999-05-03/1999-05-05
		eventDate = "";
		year = "1999"; 
		startDay = "123"; 
		endDay = "125";  
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDay, endDay);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1999-05-03/1999-05-05", result.getValue().getObject().get("dwc:eventDate"));	
		
	}
	
	
	@Test
	public void testEventDateFromMonthDay() { 
		String eventDate = "";
		String year = "1980";
		String month = "5";
		String day = "6";
		DQResponse<AmendmentValue> result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1980-05-06", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "1980"; // eventDate contains a value
		year = "1980";
		month = "5";
		day = "6";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState()); 
		
		eventDate = ""; // 
		year = "1980";
		month = "5";
		day = "6";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1980-05-06", result.getValue().getObject().get("dwc:eventDate")); 
		
		eventDate = ""; 
		year = "1980";
		month = "12";
		day = "03";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1980-12-03", result.getValue().getObject().get("dwc:eventDate")); 
		
		eventDate = ""; 
		year = "1980";
		month = "12";
		day = "";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1980-12", result.getValue().getObject().get("dwc:eventDate")); 	
		
		eventDate = ""; 
		year = "1980";
		month = "";
		day = "";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1980", result.getValue().getObject().get("dwc:eventDate")); 			
		
		eventDate = ""; 
		year = "1640";
		month = "2";
		day = "05";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1640-02-05", result.getValue().getObject().get("dwc:eventDate")); 			
		
		eventDate = ""; 
		year = "1582";
		month = "11";
		day = "15";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1582-11-15", result.getValue().getObject().get("dwc:eventDate")); 	
		
		eventDate = ""; 
		year = "1931";
		month = "2";
		day = "31";   // no such day
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.NOT_AMENDED.getLabel(),result.getResultState().getLabel());
		
		eventDate = ""; 
		year = "1931";
		month = "2";
		day = "29";   // no such day
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.NOT_AMENDED,result.getResultState());
		
		eventDate = ""; 
		year = "1932";
		month = "2";
		day = "29";   // leap day
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1932-02-29", result.getValue().getObject().get("dwc:eventDate")); 			
		
		eventDate = ""; 
		year = "1980";
		month = "text";
		day = "6";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		Map<String, String> retval = result.getValue().getObject();
		assertEquals(1, retval.size());
		assertEquals("1980", retval.get("dwc:eventDate"));

		eventDate = ""; 
		year = "";
		month = "5";
		day = "6";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());	
		
		eventDate = ""; 
		year = "1980";
		month = "12";
		day = "text";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		retval = result.getValue().getObject();
		assertEquals(1, retval.size());
		assertEquals("1980-12", retval.get("dwc:eventDate"));
		
		// Special case, support interpretation of roman numerais as month number.
		eventDate = ""; 
		year = "1980";
		month = "IV";
		day = "6";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(),result.getResultState().getLabel());
		assertEquals("1980-04-06", result.getValue().getObject().get("dwc:eventDate")); 			
		
		eventDate = ""; 
		year = "";  // no value provided for year
		month = "1";
		day = "6";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());			
		
		// Error cases in test validation data  585, 586
		eventDate = ""; 
		year = "2021"; 
		month = "10";
		day = "";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("2021-10", result.getValue().getObject().get("dwc:eventDate")); 
		
		eventDate = ""; 
		year = "2021"; 
		month = "10";
		day = "29";
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("2021-10-29", result.getValue().getObject().get("dwc:eventDate")); 			
		
		
	}
	
	@Test
	public void testIsStartEndDayPossibleForYear() {
		DQResponse<ComplianceValue> result = null;
		for (int year = 1900; year<=1903; year++) { 
			for (int day= 1; day<=365; day++) { 
				logger.debug(day + "-" + year);
				result = DwCEventDQ.validationEnddayofyearInrange(Integer.toString(day), Integer.toString(year));
				assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
				assertEquals(ComplianceValue.COMPLIANT, result.getValue());
				
				result = DwCEventDQ.validationStartdayofyearInrange(Integer.toString(day), Integer.toString(year));
				assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
				assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			}
		}

		// A leap year
		int year = 1932;
		int day = 366;
		result = DwCEventDQ.validationEnddayofyearInrange(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		result = DwCEventDQ.validationStartdayofyearInrange(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		// not a leap year
		year = 2001;
		day = 366;  // out of range
		result = DwCEventDQ.validationEnddayofyearInrange(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		result = DwCEventDQ.validationStartdayofyearInrange(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		year = 2001;
		day = 0;  // out of range
		result = DwCEventDQ.validationEnddayofyearInrange(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		result = DwCEventDQ.validationStartdayofyearInrange(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		year = 2001;
		day = 367;  // out of range
		result = DwCEventDQ.validationEnddayofyearInrange(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		result = DwCEventDQ.validationStartdayofyearInrange(Integer.toString(day), Integer.toString(year));
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		// test with no year
		day = 366;  
		result = DwCEventDQ.validationEnddayofyearInrange(Integer.toString(day),"");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		day = -1;
		result = DwCEventDQ.validationStartdayofyearInrange(Integer.toString(day),"");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		String eventDate = "1980-12-31";
		day = 366;  
		result = DwCEventDQ.validationEnddayofyearInrange(Integer.toString(day),eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1980-12-01";	// ending year is extracted from event date, not rest of date
		day = 366;  
		result = DwCEventDQ.validationEnddayofyearInrange(Integer.toString(day),eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());  
		
	}
	
	@Test 
	public void testMonthStandardized() { 
		String month = "12";
		DQResponse<AmendmentValue> result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		
		month = "0";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());	
		
		month = "1414";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());		
		
		month = "";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		month = "randomtext";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		
		month = "Jan";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "Jan.";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "January";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "JAn.";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "Feb.";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "Février";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "Fevrier";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "fevrier";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "Febbraio";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "Febrero";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "Februar";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		assertEquals(0, result.getValue().getObject().size());
		
		month = "二";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		logger.debug(result.getComment());
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "i";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));			
		
		month = "I";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));

		month = "II";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));
		
		month = "III";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("3", result.getValue().getObject().get("dwc:month"));
		
		month = "IV";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("4", result.getValue().getObject().get("dwc:month"));

		month = "V";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("5", result.getValue().getObject().get("dwc:month"));
		
		month = "VI";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("6", result.getValue().getObject().get("dwc:month"));		
		
		month = "VII";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("7", result.getValue().getObject().get("dwc:month"));

		month = "VIII";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("8", result.getValue().getObject().get("dwc:month"));
		
		month = "IX";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("9", result.getValue().getObject().get("dwc:month"));		
		
		month = "X";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("10", result.getValue().getObject().get("dwc:month"));

		month = "XI";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("11", result.getValue().getObject().get("dwc:month"));
		
		month = "XII";
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("12", result.getValue().getObject().get("dwc:month"));	
		
		month = "XIII";  // no month 13
		result = DwCEventDQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		
	}
	
	@Test 
	public void testDayStandardized() {
		String day = "1";
		DQResponse<AmendmentValue> result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		for (int i=2; i<32; i++) { 
		   result = DwCEventDQ.amendmentDayStandardized(Integer.toString(i));
		   assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		}
		
		day = "0";
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		day = "32";
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());		
		
		day = "";
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		day = "Feb.";
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());	
		
		day = "1st";
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:day"));		
		
		day = "2nd.";
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:day"));
		
		day = "31st";
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("31", result.getValue().getObject().get("dwc:day"));		
		
		day = "32nd";
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		
		day = "one";
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.AMENDED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:day"));		
		
		day = "o1";  // typo for 01
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
		day = "O1";  // typo for 01
		result = DwCEventDQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());
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
		
		result = DwCEventDQ.validationEventTemporalNotEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.validationEventTemporalNotEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "";
		verbatimEventDate = " ";
		year = " ";
		month = "NULL";
		day = "\t";
		startDayOfYear = "\n";
		endDayOfYear = "  ";
		eventTime = "                        ";
		
		result = DwCEventDQ.validationEventTemporalNotEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		eventDate = "text";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.validationEventTemporalNotEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
	
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
//		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
//		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1880-02-03";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.validationEventTemporalNotEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
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
//		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
//		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "";
		verbatimEventDate = "";
		year = "";
		month = "2";
		day = "4";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.validationEventTemporalNotEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "";
		verbatimEventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "1";
		endDayOfYear = "365";
		eventTime = "";
		
		result = DwCEventDQ.validationEventTemporalNotEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "";
		verbatimEventDate = "Summer";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		eventTime = "";
		
		result = DwCEventDQ.validationEventTemporalNotEmpty(eventDate, verbatimEventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
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
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
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
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
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
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
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
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
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
//		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
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
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
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
//		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
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
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
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
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
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
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(CompletenessValue.COMPLETE, result.getValue());	
	}	
	
	@Test
	public void testEventDateConsistentWithAtomic() {
		String eventDate = null;
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		
		DQResponse<ComplianceValue> result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
			
		eventDate = "1980-02-14";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = "";
		endDayOfYear = "";
		
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "1980-02-14";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = "";
		
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		/** 
		//Issue 67 confirmed expecations table.
		Here are some data that should be Compliant:
			eventDate 	startDayOfYear 	endDayOfYear 	year 	month 	day
			1981-01-03 	3 	3 	1981 	1 	3
			1981-01 			1981 	1 	
			1981 			1981 		
			1981-01/1981-12 			1981 		
			1981-01-01/1981-12-31 	1 	365 	1981 		
			1980/1981 					
			1980-01-10/1980-01-15 	10 	15 	1980 	1 	
			1981-12-30/1982-01-03 	364 	3 			
			*/
		
		eventDate = "1980-01-03";
		year = "1980";
		month = "1";
		day = "3";
		startDayOfYear = "3";
		endDayOfYear = "3";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		eventDate = "1980-01";
		year = "1980";
		month = "1";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
	
		eventDate = "1981";
		year = "1981";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1981-01/1981-12";
		year = "1981";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1981-01-01/1981-12-31";
		year = "1981";
		month = "";
		day = "";
		startDayOfYear = "1";
		endDayOfYear = "365";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1980/1981";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());
		
		eventDate = "1980-01-10/1980-01-15";
		year = "1980";
		month = "1";
		day = "";
		startDayOfYear = "10";
		endDayOfYear = "15";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1981-12-30/1982-01-03";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "364";
		endDayOfYear = "3";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		/*

			And some that should not (should return NOT_COMPLIANT), with non-compliant values in bold, and inconsistent values in italics.
			eventDate 	startDayOfYear 	endDayOfYear 	year 	month 	day
			1981-01 	1 	31 	1981 	1 	
			1981-01 			1981 	2 	
			1981 	1 	365 	1981 	1 	1
			1981-01/1981-12 			1981 		
			1981-01-01/1981-12-31 	1 	365 	1981 	1 	1
			1981-01-01/1981-12-31 	1 	366 	1981 		
			1980-01-10/1980-01-15 	10 	15 	1980 	1 	10
			1981-12-30/1982-01-03 	364 	3 	1981 		
			1981-12-30/1982-01-03 	3 	364 
*/		
		
		eventDate = "1981-01";
		year = "1981";
		month = "1";
		day = "";
		startDayOfYear = "1";
		endDayOfYear = "31";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
	
		eventDate = "1981-01";
		year = "1981";
		month = "2";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1981";
		year = "1981";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "365";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1981";
		year = "1981";
		month = "1";
		day = "";
		startDayOfYear = "1";
		endDayOfYear = "365";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1981";
		year = "1981";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1981";
		year = "1981";
		month = "";
		day = "";
		startDayOfYear = "1";
		endDayOfYear = "365";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1981";
		year = "1981";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "365";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1981-01/1981-12";
		year = "1981";
		month = "1";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1981-01-01/1981-12-31";
		year = "1981";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "365";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1981-01-01/1981-12-31";
		year = "1981";
		month = "";
		day = "";
		startDayOfYear = "1";
		endDayOfYear = "366";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1980/1981";
		year = "1980";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1980-01-10/1980-01-15";
		year = "1980";
		month = "1";
		day = "10";
		startDayOfYear = "10";
		endDayOfYear = "15";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1981-12-30/1982-01-03";
		year = "1981";
		month = "";
		day = "";
		startDayOfYear = "364";
		endDayOfYear = "3";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1981-12-30/1982-01-03";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "3";
		endDayOfYear = "364";
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		// Other tests
		eventDate = "1980-02-14";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = Integer.toString(31 + 14);
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = Integer.toString(31 + 14);
		
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());			
		
		eventDate = "1980-02-14";
		year = "1980";
		month = "2";
		day = "13";
		startDayOfYear = Integer.toString(31 + 12);
		endDayOfYear = "";
		
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		eventDate = "";
		year = "1980";
		month = "2";
		day = "13";
		startDayOfYear = Integer.toString(31 + 12);
		endDayOfYear = "";
		
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue());			
		
		eventDate = "1980-02-14";
		year = "1980";
		month = "2";
		day = "15";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = "";
		
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		eventDate = "1980-02-14";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 15);
		endDayOfYear = "";
		
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1980-02-14";
		year = "1980";
		month = "2";
		day = "14";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = Integer.toString(31 + 25);    // date range not in eventDate
		
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		eventDate = "1980-02-14/1980-02-25";
		year = "1980";
		month = "2";
		day = "";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = Integer.toString(31 + 25);    // date range in eventDate
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1980-02-14/1981-01-12";
		year = "";
		month = "";
		day = "";
		startDayOfYear = Integer.toString(31 + 14);
		endDayOfYear = Integer.toString(12);    // date range in eventDate, but in previous year
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		logger.debug(result.getComment());
 		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		eventDate = "1980-02";
		year = "1980";
		month = "2";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";    
		
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1980-02";
		year = "1980";
		month = "2";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		assertTrue(DateUtils.includesLeapDay(eventDate));
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1980-02-29";
		year = "1980";
		month = "2";
		day = "29";
		assertTrue(DateUtils.includesLeapDay(eventDate));
		startDayOfYear = Integer.toString(31 + 29);    
		endDayOfYear = Integer.toString(31 + 29);    
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1980-01-01/1980-12-31";
		year = "1980";
		month = "";
		day = "";
		assertTrue(DateUtils.includesLeapDay(eventDate));
		startDayOfYear = "1";    
		endDayOfYear = "366";    
		result = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());
		
	}
	
	@Test
	public void testEventDateEmpty() {
		String eventDate = null;
		DQResponse<ComplianceValue> result = DwCEventDQ.validationEventdateNotEmpty(eventDate);
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "";
		result = DwCEventDQ.validationEventdateNotEmpty(eventDate);
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "\n";
		result = DwCEventDQ.validationEventdateNotEmpty(eventDate);
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = " ";
		result = DwCEventDQ.validationEventdateNotEmpty(eventDate);
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "NULL";
		result = DwCEventDQ.validationEventdateNotEmpty(eventDate);
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1840-01-05";
		result = DwCEventDQ.validationEventdateNotEmpty(eventDate);
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "-1";
		result = DwCEventDQ.validationEventdateNotEmpty(eventDate);
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "text";
		result = DwCEventDQ.validationEventdateNotEmpty(eventDate);
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
	}
	
	@Test
	public void testYearEmpty() {
		String year = null;
		DQResponse<ComplianceValue> result = DwCEventDQ.validationYearNotEmpty(year);
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		year="";
		result = DwCEventDQ.validationYearNotEmpty(year);
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		year="NULL";
		result = DwCEventDQ.validationYearNotEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		year="1880";
		result = DwCEventDQ.validationYearNotEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());				

	    year = "-1";
		result = DwCEventDQ.validationYearNotEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());				
				
		year = "text";
		result = DwCEventDQ.validationYearNotEmpty(year);
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());				
	}
	
	@Test
	public void testFillInEventFromEventDate() {
	
		String eventDate = null;
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		DQResponse<AmendmentValue> result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "";
		year = "1918";
		month = "1";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "1918/1/15";
		year = "";
		month = "";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "1918-33-15";
		year = "";
		month = "";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		eventDate = "1918-01-15";
		year = "1918";
		month = "1";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());  
		
		eventDate = "1918-01-15";
		year = "1";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "1";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());  		
		
		eventDate = "1918/1/15";  // incorrect format
		year = "1918";
		month = "1";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
			
		eventDate = "1918-01-15/1900-01-15";  // end before start
		year = "1918";
		month = "1";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getResultState().getLabel());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());    	
		
		//Issue 67 confirmed expecations table.
		/*
		Here are some data that should be Compliant:
			eventDate 	startDayOfYear 	endDayOfYear 	year 	month 	day
			1981-01-03 	3 	3 	1981 	1 	3
			1981-01 			1981 	1 	
			1981 			1981 		
			1981-01/1981-12 			1981 		
			1981-01-01/1981-12-31 	1 	365 	1981 		
			1980/1981 					
			1980-01-10/1980-01-15 	10 	15 	1980 	1 	
			1981-12-30/1982-01-03 	364 	3 			
		*/
	
		eventDate = "1981-01-03"; 
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1981",result.getValue().getObject().get("dwc:year"));
		assertEquals("1",result.getValue().getObject().get("dwc:month"));
		assertEquals("3",result.getValue().getObject().get("dwc:day"));
		assertEquals("3",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("3",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(5,result.getValue().getObject().size());
		
		eventDate = "1981-01"; 
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1981",result.getValue().getObject().get("dwc:year"));
		assertEquals("1",result.getValue().getObject().get("dwc:month"));
		assertNull(result.getValue().getObject().get("dwc:day"));
		assertNull(result.getValue().getObject().get("dwc:startDayOfYear"));
		assertNull(result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());
		
		eventDate = "1981"; 
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1981",result.getValue().getObject().get("dwc:year"));
		assertNull(result.getValue().getObject().get("dwc:month"));
		assertNull(result.getValue().getObject().get("dwc:day"));
		assertNull(result.getValue().getObject().get("dwc:startDayOfYear"));
		assertNull(result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(1,result.getValue().getObject().size());
		
		eventDate = "1981-01/1981-12"; 
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1981",result.getValue().getObject().get("dwc:year"));
		assertNull(result.getValue().getObject().get("dwc:month"));
		assertNull(result.getValue().getObject().get("dwc:day"));
		assertNull(result.getValue().getObject().get("dwc:startDayOfYear"));
		assertNull(result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(1,result.getValue().getObject().size());
		
		eventDate = "1981-01-01/1981-12-31"; 
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1981",result.getValue().getObject().get("dwc:year"));
		assertNull(result.getValue().getObject().get("dwc:month"));
		assertNull(result.getValue().getObject().get("dwc:day"));
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(3,result.getValue().getObject().size());
		
		eventDate = "1980/1981"; 
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertEquals(0,result.getValue().getObject().size());
		
		eventDate = "1980-01-10/1980-01-15"; 
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1980",result.getValue().getObject().get("dwc:year"));
		assertEquals("1",result.getValue().getObject().get("dwc:month"));
		assertNull(result.getValue().getObject().get("dwc:day"));
		assertEquals("10",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("15",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(4,result.getValue().getObject().size());
		
		eventDate = "1981-12-30/1982-01-03"; 
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertNotNull(result.getComment());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertNull(result.getValue().getObject().get("dwc:year"));
		assertNull(result.getValue().getObject().get("dwc:month"));
		assertNull(result.getValue().getObject().get("dwc:day"));
		assertEquals("364",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("3",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());
		
		// Other Cases: 
		eventDate = "1884-01-15";
		year = "";
		month = "1";
		day = "15";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getResultState().getLabel());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1884",result.getValue().getObject().get("dwc:year"));
		assertEquals(1,result.getValue().getObject().size());	
		
		eventDate = "1884-01-15";
		year = "";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "1";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1884",result.getValue().getObject().get("dwc:year"));
		assertEquals(1,result.getValue().getObject().size());			
		
		eventDate = "1884-01-15";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "15";
		endDayOfYear = "15";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
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
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
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
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
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
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED, result.getResultState());

		eventDate = "1884-01-15";
		year = " ";
		month = "1";
		day = "15";
		startDayOfYear = "\n";
		endDayOfYear = "   ";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
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
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1884",result.getValue().getObject().get("dwc:year"));
		assertNull(result.getValue().getObject().get("dwc:month"));
		assertNull(result.getValue().getObject().get("dwc:day"));
		assertEquals("15",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("34",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(3,result.getValue().getObject().size());
		// note change in specifications 2023-06-23, ranges don't fill in day/month. 
		//assertEquals("1",result.getValue().getObject().get("dwc:month"));
		//assertEquals("15",result.getValue().getObject().get("dwc:day"));
		//assertEquals(5,result.getValue().getObject().size());
		
		eventDate = "1884-01-15/1884-02-3";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		// Note change in 2023-03-29 specification, event spanning a year boundary is internal prerequsities not met.
		// Note change in 2023-06-27 specification, event date spanning a year boundary fills in start/end day of year.
		eventDate = "1955-12-31/1956-01-03";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel()); 
		assertEquals("365", result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("3", result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2, result.getValue().getObject().size());
		
		eventDate = "1955-02";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1955",result.getValue().getObject().get("dwc:year"));
		assertEquals("2",result.getValue().getObject().get("dwc:month"));
		assertNull(result.getValue().getObject().get("dwc:day"));
		assertNull(result.getValue().getObject().get("dwc:startDayOfYear"));
		assertNull(result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());			
		
		eventDate = "1955";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getResultState().getLabel());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1955",result.getValue().getObject().get("dwc:year"));
		assertNull(result.getValue().getObject().get("dwc:month"));
		assertNull(result.getValue().getObject().get("dwc:day"));
		assertNull(result.getValue().getObject().get("dwc:startDayOfYear"));
		assertNull(result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(1,result.getValue().getObject().size());			
		
		eventDate = "1955-002";  // three digits, day of year.
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
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
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertEquals(0,result.getValue().getObject().size());	
		assertNotNull(result.getComment());
		
		eventDate = "1981";
		year = "1981";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertEquals(0,result.getValue().getObject().size());	
		
		eventDate = "1982";
		year = "1982";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertEquals(0,result.getValue().getObject().size());			
		
		eventDate = "1983";
		year = "1983";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		assertEquals(0,result.getValue().getObject().size());
		
		eventDate = "1984-01-01/1984-12-31";
		year = "1984";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("366",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());	
		
		eventDate = "1983-01-01/1983-12-31";
		year = "1983";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());			
		
		eventDate = "1600-12-31";
		year = "year";
		month = "12";
		day = "31";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("366",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("366",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());	
		
		eventDate = "1700-01-01/1700-12-31";
		year = "year";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());			
		
		eventDate = "1800-01-01/1800-12-31";
		year = "year";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());			
		
		eventDate = "1900-01-01/1900-12-31";
		year = "year";
		month = "1";
		day = "1";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("365",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(2,result.getValue().getObject().size());	
		
		eventDate = "2000-01-01/2000-12-31";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals("2000",result.getValue().getObject().get("dwc:year"));
		assertEquals("1",result.getValue().getObject().get("dwc:startDayOfYear"));
		assertEquals("366",result.getValue().getObject().get("dwc:endDayOfYear"));
		assertEquals(3,result.getValue().getObject().size());			
		
		
		
		
	}
	
	@Test
	public void testYearInRange() {
		String year = null;
		
		DQResponse<ComplianceValue> result = DwCEventDQ.validationYearInrange(year, null, null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		result = DwCEventDQ.validationYearInrange(year, 1900, null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "1700";
		result = DwCEventDQ.validationYearInrange(year, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "1699";
		result = DwCEventDQ.validationYearInrange(year, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		year = "1600";
		result = DwCEventDQ.validationYearInrange(year, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "1582";
		result = DwCEventDQ.validationYearInrange(year, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "1581";
		result = DwCEventDQ.validationYearInrange(year, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		year = "1499";
		result = DwCEventDQ.validationYearInrange(year, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		// as of https://rs.tdwg.org/bdq/terms/ad0c8855-de69-4843-a80c-a5387d20fbc8/2022-11-09
		// start year is 1500, so this is now compliant.
		year = "1599";
		result = DwCEventDQ.validationYearInrange(year, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		Integer upperBound = LocalDateTime.now().getYear();
		for (int i=1601; i<=upperBound; i++) { 
			result = DwCEventDQ.validationYearInrange(Integer.toString(i), 1600, upperBound);
			assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		
		year = Integer.toString(upperBound + 1);
		result = DwCEventDQ.validationYearInrange(year, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		year = "1899";
		result = DwCEventDQ.validationYearInrange(year, 1900, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		year = "1900";
		result = DwCEventDQ.validationYearInrange(year, 1900, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		year = "1901";
		result = DwCEventDQ.validationYearInrange(year, 1900, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		year = Integer.toString(LocalDateTime.now().getYear());
		result = DwCEventDQ.validationYearInrange(year, 1900, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		year = Integer.toString(LocalDateTime.now().getYear() + 1);
		result = DwCEventDQ.validationYearInrange(year, 1900, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());				
		
	}
	
	@Test
	public void testEventDateWhollyInRange() {
		String eventDate = null;
		
		DQResponse<ComplianceValue> result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		result = DwCEventDQ.validationEventdateInrange(eventDate, "1900", null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "13ab/02/r3:06";
		result = DwCEventDQ.validationEventdateInrange(eventDate, "1900", null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		
		eventDate = "1700";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	

		eventDate = "1700-01";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "1700-01-01";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "1880-06-15";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		eventDate = "1954/1955";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "1499";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		// as of https://rs.tdwg.org/bdq/terms/3cff4dc4-72e9-4abe-9bf3-8a30f1618432/2023-03-29
		// starting range begins at 1500 not 1600, so this is now compliant.
		eventDate = "1599";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		// as of https://rs.tdwg.org/bdq/terms/3cff4dc4-72e9-4abe-9bf3-8a30f1618432/2023-06-12
		// starting range begins at 1582-11-15 not 1500, test this bound.
		eventDate = "1582-11-15";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1582-11-14";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1582-01-01";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		// reduced precision but spanning boundary.
		eventDate = "1582";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1582-11-01/1582-11-30";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		eventDate = "1699";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1699-01-01";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		eventDate = "1499";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, "1900-01-01");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());
		
		// as of https://rs.tdwg.org/bdq/terms/3cff4dc4-72e9-4abe-9bf3-8a30f1618432/2023-03-29
		// start date is now 1500, so this is now compliant.
		eventDate = "1599";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, "1900-01-01");
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), result.getResultState().getLabel());		
		assertNull(result.getValue());
		
		eventDate = "0001";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "0001-01-02";
		result = DwCEventDQ.validationEventdateInrange(eventDate, "0001-01-01", null);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "-2000";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "-2000";
		result = DwCEventDQ.validationEventdateInrange(eventDate, "-4000", null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
	
		eventDate = "1499-12";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());			

		// as of https://rs.tdwg.org/bdq/terms/3cff4dc4-72e9-4abe-9bf3-8a30f1618432/2023-03-29
		// start date is now 1500, so this is now compliant.
		eventDate = "1599-12";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			

		eventDate = "1499-12-31";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		// as of https://rs.tdwg.org/bdq/terms/3cff4dc4-72e9-4abe-9bf3-8a30f1618432/2023-03-29
		// start date is now 1500, so this is now compliant.
		eventDate = "1599-12-31";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1499-365";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());			
	
		// as of https://rs.tdwg.org/bdq/terms/3cff4dc4-72e9-4abe-9bf3-8a30f1618432/2023-03-29
		// start date is now 1500, so this is now compliant.
		eventDate = "1599-365";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
	
		eventDate = "1800-01-01";
		result = DwCEventDQ.validationEventdateInrange(eventDate, "1800", null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());				
			
		eventDate = "1900-01-01";
		result = DwCEventDQ.validationEventdateInrange(eventDate, "1800", "2000");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "1799-12-31";
		result = DwCEventDQ.validationEventdateInrange(eventDate, "1800", "2000");
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "1499-12-31/1500-01-01";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		// as of https://rs.tdwg.org/bdq/terms/3cff4dc4-72e9-4abe-9bf3-8a30f1618432/2023-03-29
		// start date is now 1500, so this is now compliant.
		eventDate = "1599-12-31/1600-01-01";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1499-12-31/1500-12-31";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		// as of https://rs.tdwg.org/bdq/terms/3cff4dc4-72e9-4abe-9bf3-8a30f1618432/2023-03-29
		// start date is now 1500, so this is now compliant.
		eventDate = "1599-12-31/1600-12-31";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		eventDate = "1499-12-31/1800-12-31";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "1499-01-01/1599-12-31";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		eventDate = "1599-12-31/1800-12-31";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());			
		
		eventDate = "1599-01-01/1599-12-31";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), result.getValue().getLabel());	
		
    	Integer upperBound = LocalDateTime.now().getYear();
		eventDate = "1699-12-31/" + Integer.toString(upperBound + 1).trim();
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());	
		
		// beginning of next year.
		eventDate = Integer.toString(upperBound + 1).trim() + "-01-01";
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());		
		
		// next year.
		eventDate = Integer.toString(upperBound + 1).trim();
		result = DwCEventDQ.validationEventdateInrange(eventDate, null, null);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), result.getResultState().getLabel());		
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), result.getValue().getLabel());			
	}	
	
	@Test
	public void testInternalConsistency() {
	
		// Test for internal consistency in populating dwc:eventDate from other terms,
		// backfiling other event terms from dwc:eventDate, and assessing consistency of event terms.
		/*
		| eventDate 			| startDayOfYear | endDayOfYear | year | month | day | 
		| :-- 					| :-- 	| :-- 	| :-- 		| :-- 	| :-- |
		| 1981-01-03 			| 3 	| 3 	|  1981 	| 1 	| 3	|
		| 1981-01 				|  		| 		|  1981		| 1 	|  	|
		| 1981 					|  		|  		|  1981 	|  		|  	|
		| 1981-01/1981-12 		| 		|  		|  1981 	| 		| 	|
		| 1981-01-01/1981-12-31 | 1 	| 365 	|  1981 	|  		|  	|
		| 1980/1981 			|  		|  		|  			|  		|  	|
		| 1980-01-10/1980-01-15 | 10 	| 15 	|  1980 	| 1 	|  	|
		| 1981-12-30/1982-01-03 | 364 	| 3 	|  			|  		|  	|
	    */
		
		String eventDate = null;
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		DQResponse<AmendmentValue> result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());   
	
		eventDate = "1884-01-15";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		year = result.getValue().getObject().get("dwc:year");
		month = result.getValue().getObject().get("dwc:month");
		day = result.getValue().getObject().get("dwc:day");
		startDayOfYear = result.getValue().getObject().get("dwc:startDayOfYear");
		endDayOfYear = result.getValue().getObject().get("dwc:endDayOfYear");
		assertEquals("1884",year);
		assertEquals("1",month);
		assertEquals("15",day);
		assertEquals("15",startDayOfYear);
		assertEquals("15",endDayOfYear);
		assertEquals(5,result.getValue().getObject().size());	
		DQResponse<ComplianceValue> vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		
		eventDate = "";
		year = "1882";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventDateFromYearMonthDay(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		eventDate = result.getValue().getObject().get("dwc:eventDate");
		assertEquals(1,result.getValue().getObject().size());	
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.NOT_AMENDED.getLabel(), result.getResultState().getLabel());
		year = "";
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		year = result.getValue().getObject().get("dwc:year");
		assertEquals(1,result.getValue().getObject().size());	
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		
		eventDate = "";
		year = "1883";
		month = "";
		day = "";
		startDayOfYear = "1";
		endDayOfYear = "10";		
		result = DwCEventDQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, year, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		eventDate = result.getValue().getObject().get("dwc:eventDate");
		assertEquals(1,result.getValue().getObject().size());	
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		month = result.getValue().getObject().get("dwc:month");
		assertEquals(1,result.getValue().getObject().size());	
		year = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		year = result.getValue().getObject().get("dwc:year");
		startDayOfYear = result.getValue().getObject().get("dwc:startDayOfYear");
		endDayOfYear = result.getValue().getObject().get("dwc:endDayOfYear");
		assertEquals(3,result.getValue().getObject().size());	
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		
		eventDate = "1884-01";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		year = result.getValue().getObject().get("dwc:year");
		month = result.getValue().getObject().get("dwc:month");
		day = result.getValue().getObject().get("dwc:day");
		startDayOfYear = result.getValue().getObject().get("dwc:startDayOfYear");
		endDayOfYear = result.getValue().getObject().get("dwc:endDayOfYear");
		assertEquals("1884",year);
		assertEquals("1",month);
		assertEquals(2,result.getValue().getObject().size());	
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		// alternate incorrect formulation
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "31";		
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), vresult.getValue().getLabel());
		
		eventDate = "1981-12-30/1982-01-03";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		year = result.getValue().getObject().get("dwc:year");
		month = result.getValue().getObject().get("dwc:month");
		day = result.getValue().getObject().get("dwc:day");
		startDayOfYear = result.getValue().getObject().get("dwc:startDayOfYear");
		endDayOfYear = result.getValue().getObject().get("dwc:endDayOfYear");
		assertEquals("364",startDayOfYear);
		assertEquals("3",endDayOfYear);
		assertEquals(2,result.getValue().getObject().size());	
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		// alternate incorrect formulation
		day = "30";
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.NOT_COMPLIANT.getLabel(), vresult.getValue().getLabel());
		
		
		String verbatimEventDate = "1990";
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals(1,result.getValue().getObject().size());
		assertNotNull(result.getComment());
		eventDate = result.getValue().getObject().get("dwc:eventDate");
		logger.debug(eventDate);
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), vresult.getResultState().getLabel());
		assertNotNull(vresult.getComment());
		vresult = DwCEventDQ.validationEventdateStandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		assertNotNull(vresult.getComment());
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		year = result.getValue().getObject().get("dwc:year");
		assertEquals("1990",year);
		assertEquals(1,result.getValue().getObject().size());	
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		assertNotNull(vresult.getComment());
		
		verbatimEventDate = "August 10, 1991";
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals(1,result.getValue().getObject().size());
		assertNotNull(result.getComment());
		eventDate = result.getValue().getObject().get("dwc:eventDate");
		logger.debug(eventDate);
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), vresult.getResultState().getLabel());
		assertNotNull(vresult.getComment());
		vresult = DwCEventDQ.validationEventdateStandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		assertNotNull(vresult.getComment());
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		year = result.getValue().getObject().get("dwc:year");
		assertEquals("1991",year);
		month = result.getValue().getObject().get("dwc:month");
		assertEquals("8",month);
		day = result.getValue().getObject().get("dwc:day");
		assertEquals("10",day);
		startDayOfYear = result.getValue().getObject().get("dwc:startDayOfYear");
		assertEquals("222",startDayOfYear);
		endDayOfYear = result.getValue().getObject().get("dwc:endDayOfYear");
		assertEquals("222",endDayOfYear);
		assertEquals(5,result.getValue().getObject().size());	
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		assertNotNull(vresult.getComment());
		
		verbatimEventDate = "August 10, 1980";
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals(1,result.getValue().getObject().size());
		assertNotNull(result.getComment());
		eventDate = result.getValue().getObject().get("dwc:eventDate");
		logger.debug(eventDate);
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), vresult.getResultState().getLabel());
		assertNotNull(vresult.getComment());
		vresult = DwCEventDQ.validationEventdateStandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		assertNotNull(vresult.getComment());
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		year = result.getValue().getObject().get("dwc:year");
		assertEquals("1980",year);
		month = result.getValue().getObject().get("dwc:month");
		assertEquals("8",month);
		day = result.getValue().getObject().get("dwc:day");
		assertEquals("10",day);
		startDayOfYear = result.getValue().getObject().get("dwc:startDayOfYear");
		assertEquals("223",startDayOfYear);
		endDayOfYear = result.getValue().getObject().get("dwc:endDayOfYear");
		assertEquals("223",endDayOfYear);
		assertEquals(5,result.getValue().getObject().size());	
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		assertNotNull(vresult.getComment());
		
		verbatimEventDate = "July 1 to August 10, 1981";
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";		
		result = DwCEventDQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		assertEquals(1,result.getValue().getObject().size());
		assertNotNull(result.getComment());
		eventDate = result.getValue().getObject().get("dwc:eventDate");
		logger.debug(eventDate);
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET.getLabel(), vresult.getResultState().getLabel());
		assertNotNull(vresult.getComment());
		vresult = DwCEventDQ.validationEventdateStandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		assertNotNull(vresult.getComment());
		result = DwCEventDQ.amendmentEventFromEventdate(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.FILLED_IN.getLabel(), result.getResultState().getLabel());
		year = result.getValue().getObject().get("dwc:year");
		assertEquals("1981",year);
		assertNull(result.getValue().getObject().get("dwc:month"));
		assertNull(result.getValue().getObject().get("dwc:day"));
		assertEquals("",day);
		startDayOfYear = result.getValue().getObject().get("dwc:startDayOfYear");
		assertEquals("182",startDayOfYear);
		endDayOfYear = result.getValue().getObject().get("dwc:endDayOfYear");
		assertEquals("222",endDayOfYear);
		assertEquals(3,result.getValue().getObject().size());	
		vresult = DwCEventDQ.validationEventConsistent(eventDate, year, month, day, startDayOfYear, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT.getLabel(), vresult.getResultState().getLabel());
		assertEquals(ComplianceValue.COMPLIANT.getLabel(), vresult.getValue().getLabel());
		assertNotNull(vresult.getComment());
		
	} 
}
