/** DwCEventTG2DQTest.java
 * 
 * Copyright 2019 President and Fellows of Harvard College
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.api.result.NumericalValue;
import org.datakurator.ffdq.model.ResultState;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DwCEventTG2DQTest {
	private static final Log logger = LogFactory.getLog(DwCEventTG2DQTest.class);
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#measureEventdatePrecisioninseconds(java.lang.String)}.
	 */
	@Test
	public void testMeasureEventdatePrecisioninseconds() {
		
        // Specification:
        // INTERNAL_PREREQUESITES_NOT_MET if the field dwc:eventDate 
        // is not present or is EMPTY or does not contain a valid ISO 
        // 8601-1:2019 date; REPORT on the length of the period expressed 
        // in the dwc:eventDate in seconds; otherwise NOT_REPORTED 
        //		
		
		DQResponse<NumericalValue> measure = DwCEventTG2DQ.measureEventdatePrecisioninseconds("1880-05-08");
		Long seconds = (60l*60l*24l)-1l; 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventTG2DQ.measureEventdatePrecisioninseconds("1880-256");
		seconds = (60l*60l*24l)-1l; 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventTG2DQ.measureEventdatePrecisioninseconds("1880-05");
		seconds = (60l*60l*24l*31l)-1l; 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventTG2DQ.measureEventdatePrecisioninseconds("1931-04-15/1931-04-16");
		seconds = (60l*60l*24l*2l)-1l; 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure =  DwCEventTG2DQ.measureEventdatePrecisioninseconds("");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, measure.getResultState());
		
		measure =  DwCEventTG2DQ.measureEventdatePrecisioninseconds(null);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, measure.getResultState());
		
		measure =  DwCEventTG2DQ.measureEventdatePrecisioninseconds("string");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, measure.getResultState());
		
		measure =  DwCEventTG2DQ.measureEventdatePrecisioninseconds("3-IV-1883");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, measure.getResultState());
		
		measure =  DwCEventTG2DQ.measureEventdatePrecisioninseconds("1845-1-4");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, measure.getResultState());
		
		measure =  DwCEventTG2DQ.measureEventdatePrecisioninseconds("1946-03-12/1956-1-2");
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, measure.getResultState());
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationDayNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationDayNotstandard() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; 
        // COMPLIANT if the value of the field dwc:day is an integer 
        // between 1 and 31 inclusive; otherwise NOT_COMPLIANT.		
		
		String day = "";
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationDayNotstandard(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
	
		day = " ";
		result = DwCEventTG2DQ.validationDayNotstandard(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		day = null;
		result = DwCEventTG2DQ.validationDayNotstandard(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		day = "\n";
		result = DwCEventTG2DQ.validationDayNotstandard(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		day = "\t";
		result = DwCEventTG2DQ.validationDayNotstandard(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		day = "string";
		result = DwCEventTG2DQ.validationDayNotstandard(day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		day = "1st";
		result = DwCEventTG2DQ.validationDayNotstandard(day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		for (int i=1; i<=31; i++) {
			day = Integer.toString(i);
			result = DwCEventTG2DQ.validationDayNotstandard(day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		
		for (int i=32; i<=367; i++) {
			day = Integer.toString(i);
			result = DwCEventTG2DQ.validationDayNotstandard(day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		}
		
		for (int i=-32; i<=0; i++) {
			day = Integer.toString(i);
			result = DwCEventTG2DQ.validationDayNotstandard(day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		}
		
		day = Integer.toString(Integer.MAX_VALUE);
		result = DwCEventTG2DQ.validationDayNotstandard(day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		day = Integer.toString(Integer.MIN_VALUE);
		result = DwCEventTG2DQ.validationDayNotstandard(day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationDayOutofrange(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationDayOutofrange() {

    	// Specification 
    	// INTERNAL_PREREQUISITES_NOT_MET if (a) dwc:day is EMPTY 
    	// (b) is not an integer, or (c) dwc:day is an integer between
    	// 29 and 31 inclusive and dwc:month is not an integer between 
    	// 1 and 12, or (d) dwc:month is not the integer 2 and
    	// dwc:day is the integer 29 and dwc:year is not a valid ISO 8601 
    	// year; COMPLIANT (a) if the value of the field dwc:day is an 
    	// integer between 1 and 28 inclusive, or (b) dwc:day is an 
    	// integer between 29 and 30 and dwc:month is an integer in 
    	// the set (4,6,9,11), or (c) dwc:day is an integer between 
    	// 29 and 31 and dwc:month is an integer in the set (1,3,5,7,8,10,12),
    	// or (d) dwc:day is the integer 29 and dwc:month is the integer 2 
    	// and dwc:year is a valid leap year (evenly divisible by 400 
    	// or (evenly divisible by 4 but not evenly divisible by 100)); 
    	// otherwise NOT_COMPLIANT.		
		
		String day = null;
		String month = null;
		String year = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
	
		day = "string";
		month = null;
		year = null;
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = "string";
		month = "1";
		year = "2000";
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = "29";
		month = "string";
		year = "2000";
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = "2000";
		month = null;
		year = null;
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		for (int i=1; i<29; i++) { 
			day = Integer.toString(i);
			month = null;
			year = null;
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		
		for (int i=1; i<29; i++) { 
			day = Integer.toString(i);
			month = "5";
			year = null;
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		for (int i=1; i<29; i++) { 
			day = Integer.toString(i);
			month = "50";
			year = "string";
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		
		day = "29";
		month = "2";
		year = "string";
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		logger.debug(result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		
		day = "29";
		month = "2";
		year = "";
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		
		day = "29";
		month = "2";
		year = "";
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());			
		
		ArrayList<String> list = new ArrayList<String>();
		list.add("4");
		list.add("6");
		list.add("9");
		list.add("11");
		for (String l : list) { 
			day = Integer.toString(29);
			month = l;
			year = null;
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			logger.debug(result.getResultState().getLabel());
			logger.debug(result.getComment());
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			day = Integer.toString(30);
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			day = Integer.toString(31);
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		}
		
		int[] months = { 1,3,5,7,8,10,12 } ;
		for (int m : months) { 
			day = Integer.toString(29);
			month = Integer.toString(m);
			year = null;
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			day = Integer.toString(30);
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			day = Integer.toString(31);
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			day = Integer.toString(32);
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		}
		
		day = "29";
		month = "2";
		year = "1983";
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		logger.debug(result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		day = "29";
		month = "2";
		year = "2000";
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		day = "29";
		month = "2";
		year = "1984";
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		boolean leap = false;
		for (int i=1758; i<2100; i++) { 
			if ((i % 4) !=0 ) { 
				leap = false; 
			} else if (( i % 100) != 0) { 
				leap = true; 
		    } else if ((i % 400) != 0) {
		    	leap = false;
		    } else {  
				leap = true; 
		    }
			day = "29";
			month = "2";
			year = Integer.toString(i);
			result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			if (leap) { 
				assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			} else { 
				assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
			}
		}
		
		day = "-1";
		month = null;
		year = null;
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		day = "0";
		month = null;
		year = null;
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		day = "33";
		month = null;
		year = null;
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		day = Integer.toString(Integer.MAX_VALUE);
		month = null;
		year = null;
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		day = Integer.toString(Integer.MIN_VALUE);
		month = null;
		year = null;
		result = DwCEventTG2DQ.validationDayOutofrange(year, month, day);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEnddayofyearOutofrange(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEnddayofyearOutofrange() {
		
        // INTERNAL_PREREQUISITES_NOT_MET if the fields dwc:year or 
        // dwc:endDayOfYear year are either not present or are EMPTY; 
        // COMPLIANT if the value of the field dwc:endDayOfYear is 
        // a valid day given the year; otherwise NOT_COMPLIANT 		
		
		String year = null;
		String endDayOfYear = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
	
		year = "2000";
		endDayOfYear = "1";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "2000";
		endDayOfYear = "365";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "2000";
		endDayOfYear = "3000";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		year = "2000";
		endDayOfYear = "";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "";
		endDayOfYear = "10";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "";
		endDayOfYear = "365";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "";
		endDayOfYear = "366";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "";
		endDayOfYear = "380";
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		year = "";
		endDayOfYear = Integer.toString(Integer.MAX_VALUE);
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		year = "";
		endDayOfYear = Integer.toString(Integer.MIN_VALUE);
		result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		for (int i=-366; i<1; i++) { 
			year = "";
			endDayOfYear = Integer.toString(i);
			result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
			year = "1981";
			result = DwCEventTG2DQ.validationEnddayofyearOutofrange(year, endDayOfYear);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		}
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEventEmpty(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEventEmpty() {
		
        // Specification
        // COMPLIANT if at least one field needed to determine the 
        // event date exists and is not EMPTY; otherwise NOT_COMPLIANT
		
		String eventDate = null;
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		String verbatimEventDate = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		verbatimEventDate = "";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1872";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		verbatimEventDate = "";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "";
		year = "1872";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		verbatimEventDate = "";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		verbatimEventDate = "1872";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "";
		year = "";
		month = "12";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		verbatimEventDate = "";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "";
		year = "";
		month = "";
		day = "10";
		startDayOfYear = "";
		endDayOfYear = "";
		verbatimEventDate = "";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "10";
		endDayOfYear = "";
		verbatimEventDate = "";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "15";
		verbatimEventDate = "";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
	
		
		eventDate = "1872-12-02";
		year = "1872";
		month = "12";
		day = "2";
		startDayOfYear = "336";
		endDayOfYear = "336";
		verbatimEventDate = "Dec, 2 1872";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "string";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		verbatimEventDate = "";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1872-12-02";
		year = "1900";
		month = "5";
		day = "23";
		startDayOfYear = "46";
		endDayOfYear = "22";
		verbatimEventDate = "Dec, 5 1872";
		result = DwCEventTG2DQ.validationEventEmpty(startDayOfYear, eventDate, year, verbatimEventDate, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEventInconsistent(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationEventInconsistent() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is either 
        // not present or is EMPTY, or all of dwc:year, dwc:month, 
        // dwc:day, dwc:startDayOfYear and dwc:endDayOfYear are not 
        // present or are EMPTY; COMPLIANT if the provided values for 
        // dwc:year, dwc:month, dwc:day, dwc:startDayOfYear and dwc:endDayofYear 
        // are within the range of the supplied dwc:eventDate; otherwise 
        // NOT_COMPLIANT 		
		
		String eventDate = null;
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1932";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "";
		year = "1932";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1932";
		year = "1781";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1932";
		year = "1932";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "366";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1932";
		year = "1932";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1932";
		year = "1933";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "366";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1932";
		year = "1932";
		month = "2";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "366";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1932";
		year = "1932";
		month = "1";
		day = "2";
		startDayOfYear = "1";
		endDayOfYear = "366";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1932";
		year = "1932";
		month = "1";
		day = "1";
		startDayOfYear = "2";
		endDayOfYear = "366";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1932";
		year = "1932";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "35";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1932";
		year = "1932";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "365";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1932-01-05/1932-02-03";
		year = "1932";
		month = "1";
		day = "5";
		startDayOfYear = "5";
		endDayOfYear = "34";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		
		eventDate = "1931-01-05/1932-02-03";
		year = "1931";
		month = "1";
		day = "5";
		startDayOfYear = "5";
		endDayOfYear = "34";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		eventDate = "1931-01-05/1932-02-03";
		year = "1931";
		month = "1";
		day = "5";
		startDayOfYear = "5";
		endDayOfYear = "3";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		eventDate = null;
		year = "1931";
		month = "1";
		day = "5";
		startDayOfYear = "8";
		endDayOfYear = "3";
		result = DwCEventTG2DQ.validationEventInconsistent(startDayOfYear, eventDate, year, month, day, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());			
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEventdateEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateEmpty() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:eventDate 
        // is not present; COMPLIANT if the value of the field dwc:eventDate 
        //is not EMPTY; otherwise NOT_COMPLIANT 		
		
		String eventDate = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "";
		result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = " ";
		result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "\t";
		result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "\n";
		result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1880";
		result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1880-01-04/1885-03-05";
		result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "string";
		result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = " 1880 ";
		result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1880";
		result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1880-1-1";
		result = DwCEventTG2DQ.validationEventdateEmpty(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEventdateNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateNotstandard() {
		
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:eventDate 
        // is either not present or is EMPTY; COMPLIANT if the value 
        // of dwc:eventDate is a valid ISO 8601-1:2019 date; otherwise 
        // NOT_COMPLIANT 	
		
		String eventDate = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		eventDate = "";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		eventDate = " ";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		eventDate = "\n";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		eventDate = "\t";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		eventDate = "1880-1-1";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "string";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1-5-1880";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1 Jan, 1880";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "01-15-1880";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		LocalDateTime date = LocalDateTime.parse("0986-01-01");
		LocalDateTime endDate = LocalDateTime.parse("1005-12-31");
		while (date.isBefore(endDate)) { 
			eventDate=date.format(DateTimeFormatter.ISO_LOCAL_DATE);
			result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			date = date.plusDays(1);
		}
		date = LocalDateTime.parse("1750-01-01");
		while (date.isBefore(LocalDateTime.now())) { 
			eventDate=date.format(DateTimeFormatter.ISO_LOCAL_DATE);
			result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
			date = date.plusDays(1);
		}
		
		eventDate = "1980-02-29";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1981-02-29";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1981-02-30";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1981-02-31";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1981";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1981-01";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1981/1982";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1981-01/1981-02";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1981-01-13/1981-02-03";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1981-02-03T04:23:01.001-05:00";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1981-01/1981-02-93";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1982-01-13/1981-02-03";
		result = DwCEventTG2DQ.validationEventdateNotstandard(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationEventdateOutofrange(java.lang.String)}.
	 */
	@Test
	public void testValidationEventdateOutofrange() {
		
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY or if 
        // the value of dwc:eventDate is not a valid ISO 8601-1:2019 date; 
        // COMPLIANT if the range of dwc:eventDate is within the parameter 
        // range, otherwise NOT_COMPLIANT
		
		String eventDate = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = " ";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "string";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1-3-1700";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1700-1-3";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "Jan 1, 1835";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "\n";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "\t";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1599-12-31";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1600-01-01";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1685-03-15";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1700-01-01";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1812-05-22";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1900-01-01";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1935-12-31";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "2000-08-05";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "2004-02-29";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
        eventDate= LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1600-01-01/"+ LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1599-12-31/"+ LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = "1703-08-16/2004-02-29";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		eventDate = "1503-08-16/1602-02-29";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1503-08-16/1602-02-28";
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		eventDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE) + "/" + LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
		result = DwCEventTG2DQ.validationEventdateOutofrange(eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationMonthNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationMonthNotstandard() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:month is 
        // either not present or is EMPTY; COMPLIANT if the value of 
        // the field dwc:month is an integer between 1 and 12 inclusive; 
        // otherwise NOT_COMPLIANT 		
		
		String month = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationMonthNotstandard(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		for (int i=1; i<13; i++) {
			month = Integer.toString(i);
			result = DwCEventTG2DQ.validationMonthNotstandard(month);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		}
		
		month = "0";
		result = DwCEventTG2DQ.validationMonthNotstandard(month);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		month = "13";
		result = DwCEventTG2DQ.validationMonthNotstandard(month);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		month = "string";
		result = DwCEventTG2DQ.validationMonthNotstandard(month);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		month = "";
		result = DwCEventTG2DQ.validationMonthNotstandard(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		month = "14";
		result = DwCEventTG2DQ.validationMonthNotstandard(month);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		month = "-1";
		result = DwCEventTG2DQ.validationMonthNotstandard(month);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		month = Integer.toString(Integer.MAX_VALUE);
		result = DwCEventTG2DQ.validationMonthNotstandard(month);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		month = Integer.toString(Integer.MIN_VALUE);
		result = DwCEventTG2DQ.validationMonthNotstandard(month);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationStartdayofyearOutofrange(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationStartdayofyearOutofrange() {
	
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:startDayOfYear 
        // is either not present or is EMPTY, or if the value of 
        // dwc:startDayOfYear = 366 and dwc:year is either not present 
        // or is EMPTY; COMPLIANT if the value of the field 
        // dwc:startDayOfYear is a valid day given the year; 		
		
		String year = null;
		String startDayOfYear = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
	
		year = "2000";
		startDayOfYear = "1";
		result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "2000";
		startDayOfYear = "365";
		result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "2000";
		startDayOfYear = "3000";
		result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		year = "2000";
		startDayOfYear = "";
		result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "";
		startDayOfYear = "10";
		result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "";
		startDayOfYear = "365";
		result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		year = "";
		startDayOfYear = "366";
		result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "";
		startDayOfYear = "380";
		result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		year = "";
		startDayOfYear = Integer.toString(Integer.MAX_VALUE);
		result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		year = "";
		startDayOfYear = Integer.toString(Integer.MIN_VALUE);
		result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		for (int i=-366; i<1; i++) { 
			year = "";
			startDayOfYear = Integer.toString(i);
			result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
			year = "1981";
			result = DwCEventTG2DQ.validationStartdayofyearOutofrange(startDayOfYear, year);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());
			assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		}
	
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationYearEmpty(java.lang.String)}.
	 */
	@Test
	public void testValidationYearEmpty() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:year is 
        // not present; COMPLIANT if the value of the field dwc:year 
        //is not EMPTY; otherwise NOT_COMPLIANT 
		
		String year = null;
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year="";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year=" ";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year="\n";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		year="NULL";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		year="1880";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				

	    year = "-1";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				
				
		year = "text";
		result = DwCEventTG2DQ.validationYearEmpty(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());				
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#validationYearOutofRange(java.lang.String)}.
	 */
	@Test
	public void testValidationYearOutofRange() {
		
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:year is not present, 
        // or is EMPTY or cannot be interpreted as an integer; 
        // COMPLIANT if the value of dwc:year is within the Parameter 
        // range; otherwise NOT_COMPLIANT		
		
		String year = "";
		DQResponse<ComplianceValue> result = DwCEventTG2DQ.validationYearOutofRange(year);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = null;
		result = DwCEventTG2DQ.validationYearOutofRange(year);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = " ";
		result = DwCEventTG2DQ.validationYearOutofRange(year);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "\t";
		result = DwCEventTG2DQ.validationYearOutofRange(year);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "\n";
		result = DwCEventTG2DQ.validationYearOutofRange(year);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "string";
		result = DwCEventTG2DQ.validationYearOutofRange(year);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		year = "1980";
		result = DwCEventTG2DQ.validationYearOutofRange(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		
		year = "1599";
		result = DwCEventTG2DQ.validationYearOutofRange(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
        Integer currentYear = Calendar.getInstance().get(Calendar.YEAR);
		year = Integer.toString(currentYear);
		result = DwCEventTG2DQ.validationYearOutofRange(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		year = Integer.toString(currentYear+1);
		result = DwCEventTG2DQ.validationYearOutofRange(year);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
		for (int i=1600; i<=currentYear; i++) {
			year = Integer.toString(i);
			result = DwCEventTG2DQ.validationYearOutofRange(year);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
			assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		}
		
		for (int i=1000; i<1599; i++) {
			year = Integer.toString(i);
			result = DwCEventTG2DQ.validationYearOutofRange(year);
			assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
			assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		}
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentDateidentifiedStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentDateidentifiedStandardized() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:dateIdentified was 
        // not present or is EMPTY; AMENDED if the value of dwc:dateIdentified 
        // was altered to unambiguously conform with the ISO 8601-1:2019 date format; 
        // otherwise NOT_CHANGED 		
		
		String proposed;
		
		String dateIdentified = "";  
		DQResponse<AmendmentValue> result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = null;  
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "\n";  
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "1937-08-23/1037-09-09";  // range with end before start
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		// TODO: Inconsistent with eventDate standardized.
		logger.debug(result.getComment());
		logger.debug(result.getResultState().getLabel());
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "1937-08-23";  
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "1937-08-23/1938-01-04";  
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "string";  
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "1937-8-23";  
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.CHANGED, result.getResultState());
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:dateIdentified");
		assertEquals("1937-08-23", proposed);
		assertEquals(1, result.getValue().getObject().size());
		
		dateIdentified = "1937-Feb-23";  
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:dateIdentified");
		assertEquals("1937-02-23", proposed);
		assertEquals(1, result.getValue().getObject().size());
		
		dateIdentified = "Feb 23, 1937";  
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:dateIdentified");
		assertEquals("1937-02-23", proposed);
		assertEquals(1, result.getValue().getObject().size());

		dateIdentified = "2-23-1937";  
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:dateIdentified");
		assertEquals("1937-02-23", proposed);
		assertEquals(1, result.getValue().getObject().size());
		
		dateIdentified = "April 1937";  
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:dateIdentified");
		assertEquals("1937-04", proposed);
		assertEquals(1, result.getValue().getObject().size());

    	dateIdentified = "11-VII-1885";
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:dateIdentified");
    	assertEquals("1885-07-11", proposed);
		assertEquals(1, result.getValue().getObject().size());
		
		dateIdentified = "2-3-1937"; // ambiguous m-d or d-m 
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		dateIdentified = "April 137"; // suspect 
		result = DwCEventTG2DQ.amendmentDateidentifiedStandardized(dateIdentified);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());			
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentDayStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentDayStandardized() {
		
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is not present 
        // or is EMPTY; AMENDED if the value of dwc:day was unambiguously 
        // interpreted to be an integer between 1 and 31 inclusive; 
        // otherwise NOT_CHANGED 
		String proposed;
		
		String day = "";  
		DQResponse<AmendmentValue> result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = null;  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = " ";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = "\n";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		day = "1";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "30";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "0";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "33";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "?3";  // could represent uncertainty or a placeholder for an illegible character  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		logger.debug(result.getComment());
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "3?";  // could represent uncertainty or a placeholder for an illegible character
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		logger.debug(result.getComment());
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "??";  // common no-values marker
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "**";  // common no-values marker
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		day = "1st";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("1", proposed);
		
		day = "3rd";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("3", proposed);
		
		day = "1 ";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("1", proposed);
		
		day = " 1 ";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("1", proposed);
		
		day = " 1\n";  
		result = DwCEventTG2DQ.amendmentDayStandardized(day);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("1", proposed);
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentEventFromEventdate(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventFromEventdate() {
		
        // INTERNAL_PREREQUESITES_NOT_MET if the field dwc:eventDate 
        // is EMPTY or does not contain a valid ISO 8601-1:2019 date; 
        // AMENDED if one or more EMPTY terms of the dwc:Event class 
        // (dwc:year, dwc:month, dwc:day, dwc:startDayOfYear, dwc:endDayOfYear) 
        // have been filled in from a valid unambiguously interpretable 
        // value in dwc:eventDate; otherwise NOT_CHANGED
		
		String proposed = "";
		
		String eventDate = "";
		String year = null;
		String month = null;
		String day = null;
		String startDayOfYear = null;
		String endDayOfYear = null;
		DQResponse<AmendmentValue> result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "string";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "string";
		year = "1880";
		month = null;
		day = null;
		startDayOfYear = null;
		endDayOfYear = null;
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "1835-1-12";
		year = "";
		month = null;
		day = null;
		startDayOfYear = null;
		endDayOfYear = null;
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "January 1, 1835";
		year = "";
		month = null;
		day = null;
		startDayOfYear = null;
		endDayOfYear = null;
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		eventDate = "1835-01-12";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.FILLED_IN, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:year");
		assertEquals("1835", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:month");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:startDayOfYear");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:endDayOfYear");
		assertEquals("12", proposed);
		assertEquals(5,result.getValue().getObject().size());
		
		eventDate = "1835-01-12";
		year = "1900";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.FILLED_IN, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:year");
		assertEquals(null, proposed);
		assertEquals("1900", year);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:month");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:startDayOfYear");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:endDayOfYear");
		assertEquals("12", proposed);	
		assertEquals(4,result.getValue().getObject().size());
		
		eventDate = "1835-01-12";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "1";
		endDayOfYear = "";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.FILLED_IN, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:year");
		assertEquals("1835", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:month");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:startDayOfYear");
		assertEquals(null, proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:endDayOfYear");
		assertEquals("12", proposed);			
		
		eventDate = "1835-01-12/1836-01-05";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.FILLED_IN, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:year");
		assertEquals("1835", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:month");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:startDayOfYear");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:endDayOfYear");
		assertEquals("5", proposed);			
		
		eventDate = "1835-01";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.FILLED_IN, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:year");
		assertEquals("1835", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:month");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:startDayOfYear");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:endDayOfYear");
		assertEquals("31", proposed);		
		
		eventDate = "1835";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.FILLED_IN, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:year");
		assertEquals("1835", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:month");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:startDayOfYear");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:endDayOfYear");
		assertEquals("365", proposed);		
	
		eventDate = "1835";
		year = "1835";
		month = "1";
		day = "1";
		startDayOfYear = "1";
		endDayOfYear = "365";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.NO_CHANGE, result.getResultState());		
		
		eventDate = "1835";
		year = "string";
		month = "string";
		day = "string";
		startDayOfYear = "string";
		endDayOfYear = "string";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.NO_CHANGE, result.getResultState());		
		
		eventDate = "1835-01-12/1836";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.FILLED_IN, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:year");
		assertEquals("1835", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:month");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:startDayOfYear");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:endDayOfYear");
		assertEquals("366", proposed);		
		
		eventDate = "1834-01-12/1835";
		year = "";
		month = "";
		day = "";
		startDayOfYear = "";
		endDayOfYear = "";
		result = DwCEventTG2DQ.amendmentEventFromEventdate(eventDate, startDayOfYear, year, month, day, endDayOfYear);;
		assertEquals(ResultState.FILLED_IN, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:year");
		assertEquals("1834", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:month");
		assertEquals("1", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:day");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:startDayOfYear");
		assertEquals("12", proposed);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:endDayOfYear");
		assertEquals("365", proposed);			
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentEventdateFromVerbatim(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromVerbatim() {

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:eventDate 
        // is not EMPTY or the field dwc:verbatimEventDate is EMPTY 
        // or not unambiguously interpretable as an ISO 8601-1:2019 
        // date; AMENDED if the value of dwc:eventDate was unambiguously 
        // interpreted from dwc:verbatimEventDate; otherwise NOT_CHANGED 
        //

		String verbatimEventDate = "";
		String eventDate = "";
		DQResponse<AmendmentValue> result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);;
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		verbatimEventDate = "";
		eventDate = "1980";
		result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);
		logger.debug(result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		verbatimEventDate = "1985-01";
		eventDate = "1980";
		result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);;
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		
		verbatimEventDate = "1985-01";
		eventDate = "string";
		result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);;
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		
		verbatimEventDate = "1985-01";
		eventDate = "string";
		result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate, verbatimEventDate);;
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		
		eventDate = "";
		verbatimEventDate = "Jan 1884";
		result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		logger.debug(result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884-01",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());

		verbatimEventDate = "1 Mar 1884";
		result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.FILLED_IN, result.getResultState());
		assertEquals("1884-03-01",result.getValue().getObject().get("dwc:eventDate"));
		assertEquals(1,result.getValue().getObject().size());		
		
		eventDate = "1884";
		verbatimEventDate = "1 Mar 1884";
		result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		assertEquals(0,result.getValue().getObject().size());	
		
		eventDate = "1884-03-01";
		verbatimEventDate = "1 Mar 1884";
		result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		assertEquals(0,result.getValue().getObject().size());		
		
		eventDate = null;
		verbatimEventDate = "";
		result =DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(0,result.getValue().getObject().size());			
		
		eventDate = null;
		verbatimEventDate = "5-8-1884";
		result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		logger.debug(result.getResultState().getLabel());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		//assertEquals("1884-05-08/1884-08-05",result.getValue().getObject().get("dwc:eventDate"));
		// assertEquals(1,result.getValue().getObject().size());		
		assertEquals(0,result.getValue().getObject().size());		
		
		eventDate = null;
		verbatimEventDate = "2001/Feb/29";
		result = DwCEventTG2DQ.amendmentEventdateFromVerbatim(eventDate,verbatimEventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(0,result.getValue().getObject().size());					
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentEventdateFromYearmonthday(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromYearmonthday() {
		
        // INTERNAL _PREREQUISITES_NOT_MET if the field dwc:eventDate 
        // is not EMPTY or dwc:year is EMPTY or is uninterpretable 
        // as a valid year; AMENDED if the value of dwc:eventDate was 
        // interpreted from the values in dwc:year, dwc:month and dwc:day; 
        // otherwise NOT_CHANGED 		
		
		String eventDate = "";
		String year = "";
		String month = "";
		String day = "";
		DQResponse<AmendmentValue> result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		eventDate = "1980";
		year = "";
		month = "";
		day = "";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		logger.debug(result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ResultState.NO_CHANGE, result.getResultState());		
		assertEquals(null, result.getValue());
		
		eventDate = "string";
		year = "1980";
		month = "";
		day = "";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());		
		assertEquals(null, result.getValue());
		
		eventDate = "";
		year = "1980";
		month = "";
		day = "";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN, result.getResultState());		
		assertEquals(1, result.getValue().getObject().size());
		assertEquals("1980", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1980";
		month = "5";
		day = "";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN, result.getResultState());		
		assertEquals(1, result.getValue().getObject().size());
		assertEquals("1980-05", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1980";
		month = "5";
		day = "1";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN, result.getResultState());		
		assertEquals(1, result.getValue().getObject().size());
		assertEquals("1980-05-01", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = " ";
		year = "1980";
		month = "5";
		day = "1";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN, result.getResultState());		
		assertEquals(1, result.getValue().getObject().size());
		assertEquals("1980-05-01", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = " ";
		year = "1555";
		month = "5";
		day = "1";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN, result.getResultState());		
		assertEquals(1, result.getValue().getObject().size());
		assertEquals("1555-05-01", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = " ";
		year = "1981";
		month = "2";
		day = "29";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		assertEquals(null, result.getValue());
		
		eventDate = "";
		year = "1980";
		month = "2";
		day = "29";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.FILLED_IN, result.getResultState());		
		assertEquals(1, result.getValue().getObject().size());
		assertEquals("1980-02-29", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1981";
		month = "29";
		day = "2";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		assertEquals(null, result.getValue());
		
		eventDate = "";
		year = "1980";
		month = "26";
		day = "36";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		assertEquals(null, result.getValue());
		
		eventDate = "";
		year = "1981";
		month = "12";
		day = "32";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		assertEquals(null, result.getValue());
		
		eventDate = "";
		year = "string";
		month = "5";
		day = "1";
		result = DwCEventTG2DQ.amendmentEventdateFromYearmonthday(eventDate, year, month, day);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());		
		assertEquals(null, result.getValue());
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentEventdateFromYearstartdayofyearenddayofyear(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateFromYearstartdayofyearenddayofyear() {
		
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:eventDate 
        // was not EMPTY or dwc:year was EMPTY or both dwc:startDayOfYear 
        // and dwc:endDayOfYear were EMPTY or not interpretable; AMENDED 
        // if the value of dwc:eventDate was FILLED_IN from the values 
        // in dwc:year, dwc:startDayOfYear and dwc:endDayOfYear; otherwise 
        // NOT_CHANGED 		
		
		String eventDate = "";
		String year = "1980";
		String startDayOfYear = "5";
		String endDayOfYear = "6";
		DQResponse<AmendmentValue> result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-01-05/1980-01-06", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "1980"; // eventDate contains a value
		year = "1980";
		startDayOfYear = "5";
		endDayOfYear = "6";
		result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		logger.debug(result.getComment());
		logger.debug(result.getResultState().getLabel());
		assertEquals(ResultState.NO_CHANGE,result.getResultState()); 
		
		eventDate = "";
		year = "1980";
		startDayOfYear = "day5";  // not a number
		endDayOfYear = "6";
		result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = "";
		year = "1980";  // a leap year
		startDayOfYear = "1";
		endDayOfYear = "366";  
		result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-01-01/1980-12-31", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDayOfYear = "1";
		endDayOfYear = "365";  
		result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1981-01-01/1981-12-31", result.getValue().getObject().get("dwc:eventDate"));
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDayOfYear = "45";
		endDayOfYear = "280";  
		result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1981-02-14/1981-10-07", result.getValue().getObject().get("dwc:eventDate"));		
		
		eventDate = "";
		year = "1980";  // a leap year
		startDayOfYear = "45";  // spans leap day
		endDayOfYear = "280";  
		result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1980-02-14/1980-10-06", result.getValue().getObject().get("dwc:eventDate"));			
		
		eventDate = "";
		year = "1981";  // not a leap year
		startDayOfYear = "1";
		endDayOfYear = "366";  
		result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());		
		
		eventDate = "";
		year = "1980";
		startDayOfYear = "900";  // out of range
		endDayOfYear = "920";
		result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());
		
		eventDate = "";
		year = "1980";
		startDayOfYear = "200";  // start and end reversed or in different years.
		endDayOfYear = "10";
		result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET,result.getResultState());	
		
		// Example given in TG2 definition: 
		//dwc:year=1999, dwc:startDayOfYear=123, dwc:endDayOfYear=125 
		//therefore dwc:eventDate=1999-05-03/1999-05-05
		eventDate = "";
		year = "1999"; 
		startDayOfYear = "123"; 
		endDayOfYear = "125";  
		result = DwCEventTG2DQ.amendmentEventdateFromYearstartdayofyearenddayofyear(eventDate, startDayOfYear, year, endDayOfYear);
		assertEquals(ResultState.FILLED_IN,result.getResultState());
		assertEquals("1999-05-03/1999-05-05", result.getValue().getObject().get("dwc:eventDate"));			
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentEventdateStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentEventdateStandardized() {
		
        // Specification
        // INTERNAL_PREREQUESITES_NOT_MET if the field dwc:eventDate 
        // is EMPTY; AMENDED if the field dwc:eventDate was changed 
        // to unambiguously conform with an ISO 8601-1:2019 date; otherwise 
        // NOT_CHANGED 
		String proposed;
		
		String eventDate = "";  
		DQResponse<AmendmentValue> result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = null;  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "\n";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1937-08-23/1037-09-09";  // range with end before start
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1937-08-23";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1937-08-23/1938-01-04";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "string";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1937-8-23";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
		assertEquals("1937-08-23", proposed);
		assertEquals(1, result.getValue().getObject().size());
		
		eventDate = "1937-Feb-23";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
		assertEquals("1937-02-23", proposed);
		assertEquals(1, result.getValue().getObject().size());
		
		eventDate = "Feb 23, 1937";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
		assertEquals("1937-02-23", proposed);
		assertEquals(1, result.getValue().getObject().size());

		eventDate = "2-23-1937";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
		assertEquals("1937-02-23", proposed);
		assertEquals(1, result.getValue().getObject().size());
		
		eventDate = "April 1937";  
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.CHANGED, result.getResultState());	
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
		assertEquals("1937-04", proposed);
		assertEquals(1, result.getValue().getObject().size());

    	eventDate = "11-VII-1885";
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		proposed = ((AmendmentValue)result.getValue()).getObject().get("dwc:eventDate");
    	assertEquals("1885-07-11", proposed);
		assertEquals(1, result.getValue().getObject().size());
		
		eventDate = "2-3-1937"; // ambiguous m-d or d-m 
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
		eventDate = "April 137"; // suspect 
		result = DwCEventTG2DQ.amendmentEventdateStandardized(eventDate);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventTG2DQ#amendmentMonthStandardized(java.lang.String)}.
	 */
	@Test
	public void testAmendmentMonthStandardized() {
		
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:month is not present 
        // or is EMPTY; AMENDED if the value of dwc:month was interpreted 
        // to be a integer between 1 and 12 inclusive; otherwise NOT_CHANGED 		
		
		String month = "12";
		DQResponse<AmendmentValue> result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		assertEquals(null, result.getValue());
		
		month = "";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		month = " ";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		month = "\n";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		month = "\t";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		month = "randomtext";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		logger.debug(result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		assertEquals(null, result.getValue());
		
		month = "0";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());	
		assertEquals(null, result.getValue());
		
		month = "1414";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());		
		assertEquals(null, result.getValue());
		
		month = "Jan";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));
		
		month = "Jan.";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));		
		
		month = "January";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));	
		
		month = "JAn.";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));		
		
		month = "Feb.";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "Février";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "Fevrier";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "fevrier";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "Febbraio";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "Febrero";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "Februar";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "二";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));		
		
		month = "i";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));			
		
		month = "I";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("1", result.getValue().getObject().get("dwc:month"));

		month = "II";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("2", result.getValue().getObject().get("dwc:month"));
		
		month = "III";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("3", result.getValue().getObject().get("dwc:month"));
		
		month = "IV";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("4", result.getValue().getObject().get("dwc:month"));

		month = "V";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("5", result.getValue().getObject().get("dwc:month"));
		
		month = "VI";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("6", result.getValue().getObject().get("dwc:month"));		
		
		month = "VII";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("7", result.getValue().getObject().get("dwc:month"));

		month = "VIII";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("8", result.getValue().getObject().get("dwc:month"));
		
		month = "IX";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("9", result.getValue().getObject().get("dwc:month"));		
		
		month = "X";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("10", result.getValue().getObject().get("dwc:month"));

		month = "XI";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("11", result.getValue().getObject().get("dwc:month"));
		
		month = "XII";
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.CHANGED, result.getResultState());
		assertEquals("12", result.getValue().getObject().get("dwc:month"));	
		
		month = "XIII";  // no month 13
		result = DwCEventTG2DQ.amendmentMonthStandardized(month);
		assertEquals(ResultState.NO_CHANGE, result.getResultState());
		assertEquals(null, result.getValue());
			
	}
}
