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
import org.datakurator.ffdq.model.report.ResultState;
import org.datakurator.ffdq.model.report.result.AmendmentValue;
import org.datakurator.ffdq.model.report.result.ComplianceValue;
import org.datakurator.ffdq.model.report.result.NumericalValue;
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
		assertEquals(seconds, measure.getValue().longValue());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureDurationSeconds("1880-05");
		seconds = (60l*60l*24l*31)-1l; 
		assertEquals(seconds, measure.getValue().longValue());		
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
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange("");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange(" ");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange("A");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange("1.5");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange("**");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		result = DwCEventDQ.isDayInRange("1st");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		result = DwCEventDQ.isDayInRange("2nd");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
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
		assertEquals("11",result.getValue().get("dwc:month"));
		assertEquals("30",result.getValue().get("dwc:day"));

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
		assertEquals("1884-01",result.getValue().get("dwc:eventDate"));
		assertEquals(1,result.getValue().size());

		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1884-03-01",result.getValue().get("dwc:eventDate"));
		assertEquals(1,result.getValue().size());		
		
		eventDate = "1884";
		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		assertEquals(0,result.getValue().size());			
		eventDate = "1884-03-01";
		verbatimEventDate = "1 Mar 1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		assertEquals(0,result.getValue().size());		
		
		eventDate = null;
		verbatimEventDate = "";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(0,result.getValue().size());			
		
		eventDate = null;
		verbatimEventDate = "5-8-1884";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.AMBIGUOUS, result.getResultState());
		assertEquals("1884-05-08/1884-08-05",result.getValue().get("dwc:eventDate"));
		assertEquals(1,result.getValue().size());		
		
		eventDate = null;
		verbatimEventDate = "2001/Feb/29";
		result = DwCEventDQ.extractDateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(0,result.getValue().size());			
		
	}
	
	@Test
	public void testCorrectEventDateFormat() { 
		String eventDate = "Jan 1884";
		DQResponse<AmendmentValue> result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1884-01",result.getValue().get("dwc:eventDate"));
		assertEquals(1,result.getValue().size());	
		
		eventDate = "1 Jan 1884";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1884-01-01",result.getValue().get("dwc:eventDate"));
		assertEquals(1,result.getValue().size());		
		
		/* Case that inspired adding this method, typical use of / instead of - in dates. 
		 */
		eventDate = "1884/01/02";
		result = DwCEventDQ.correctEventDateFormat(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1884-01-02",result.getValue().get("dwc:eventDate"));
		assertEquals(1,result.getValue().size());		
		
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
		assertEquals("1884-02-03/1884-03-02",result.getValue().get("dwc:eventDate"));
		assertEquals(1,result.getValue().size());				
		
	}
}
