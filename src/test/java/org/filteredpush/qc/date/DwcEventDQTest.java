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
}
