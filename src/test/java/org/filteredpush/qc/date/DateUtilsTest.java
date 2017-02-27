/** DateUtilsTest.java
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
 * 
 */
package org.filteredpush.qc.date;

import static org.junit.Assert.*;

import java.util.Map;

import org.filteredpush.qc.date.DateUtils;
import org.joda.time.DateMidnight;
import org.joda.time.Interval;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DateUtilsTest {

	@Test
	public void testIsEmpty() { 
		assertEquals(true,DateUtils.isEmpty(null));
		assertEquals(true,DateUtils.isEmpty(""));
		assertEquals(true,DateUtils.isEmpty(" "));
		assertEquals(true,DateUtils.isEmpty("   "));
		assertEquals(true,DateUtils.isEmpty("\t"));
		assertEquals(true,DateUtils.isEmpty("NULL"));
		assertEquals(true,DateUtils.isEmpty("null"));
		assertEquals(true,DateUtils.isEmpty(" null "));
		
		assertEquals(false,DateUtils.isEmpty("1"));
		assertEquals(false,DateUtils.isEmpty("A"));
		assertEquals(false,DateUtils.isEmpty(" 2"));
		assertEquals(false,DateUtils.isEmpty(" 5 "));
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DateUtils#createEventDateFromParts(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testCreateEventDateFromParts() {
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts(null, null, null, "1882", "03", "24"));
		assertEquals(null, DateUtils.createEventDateFromParts(null, null, null, null, null, null));
		assertEquals(null, DateUtils.createEventDateFromParts(null, null, null, null, null, "01"));
		assertEquals(null, DateUtils.createEventDateFromParts(null, null, null, null, "01", "01"));
		assertEquals(null, DateUtils.createEventDateFromParts(null, "01", null, null, "01", "01"));
		assertEquals(null, DateUtils.createEventDateFromParts(null, "01", "01", null, "01", "01"));
		
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("1882-03-24", null, null, "1882", "03", "24"));
		assertEquals("1882-03-04", DateUtils.createEventDateFromParts("1882-03-04", null, null, "1882", "3", "4"));
		assertEquals("1882-03-04", DateUtils.createEventDateFromParts(null, null, null, "1882", "3", "4"));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("1882-03-24", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("03/24/1882", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("24/03/1882", null, null, null, null, null));
		
		assertNull(DateUtils.createEventDateFromParts(null, null, null, "1882", "300", "40"));
		assertNull(DateUtils.createEventDateFromParts("1882-300-40", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("1882-03-24", null, null, "1882", "030", "240"));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("1882-030-240", null, null, "1882", "03", "24"));

		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("1882-Mar-24", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("Mar/24/1882", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("March 24, 1882", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("Mar. 24, 1882", null, null, null, null, null));
		assertEquals("1882-03-24", DateUtils.createEventDateFromParts("Mar 24, 1882", null, null, null, null, null));
		
		assertEquals("1882-04-04", DateUtils.createEventDateFromParts("04/04/1882", null, null, null, null, null));
		assertEquals("1882-02-03/1882-03-02", DateUtils.createEventDateFromParts("02/03/1882", null, null, null, null, null));
		
		assertEquals("1882-01-01/1882-01-31", DateUtils.createEventDateFromParts("1882-01", null, null, null, null, null));
		assertEquals("1882-04-01/1882-04-30", DateUtils.createEventDateFromParts("1882-04", null, null, null, null, null));
		assertEquals("1882-01-01/1882-01-31", DateUtils.createEventDateFromParts("1882-Jan", null, null, null, null, null));
		assertEquals("1882-01-01/1882-01-31", DateUtils.createEventDateFromParts("1882/Jan", null, null, null, null, null));
		assertEquals("1882-01-01/1882-12-31", DateUtils.createEventDateFromParts("1882", null, null, null, null, null));
		
		assertEquals("1890-02-01", DateUtils.createEventDateFromParts("1890-032", null, null, null, null, null));
		assertEquals("1890-02-01", DateUtils.createEventDateFromParts("1890-032", "32", "32", null, null, null));
		assertEquals("1890-02-01", DateUtils.createEventDateFromParts("1890-032", "32", null, null, null, null));
		
		assertEquals("1882-01-05", DateUtils.createEventDateFromParts(null, "5",  null, "1882", null, null));
		assertEquals("1882-01-05", DateUtils.createEventDateFromParts(null, "5", "5", "1882", null, null));
		assertEquals("1882-01-05", DateUtils.createEventDateFromParts(null, "005", null, "1882", null, null));
		assertEquals("1882-01-05", DateUtils.createEventDateFromParts("1882", "5", null, null, null, null));
		assertEquals("1882-01-05", DateUtils.createEventDateFromParts("1882", "005", null, null, null, null));

		assertEquals("1882-01-05/1882-01-06", DateUtils.createEventDateFromParts("1882", "005", "006", null, null, null));		
		assertEquals("1882-01-05/1882-01-06", DateUtils.createEventDateFromParts(null, "005", "006", "1882", null, null));		
		
		assertEquals(null, DateUtils.createEventDateFromParts("1880-02-32", null, null, null, null, null));
		assertEquals(null, DateUtils.createEventDateFromParts("Feb 31, 1880", null, null, null, null, null));
		
		//TODO: More tests of date creation
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DateUtils#isRange(java.lang.String)}.
	 */
    @Test
    public void isRangeTest() { 
        assertFalse(DateUtils.isRange("1880-01-02"));
        assertTrue(DateUtils.isRange("1880-01-01/1880-12-31"));
        assertFalse(DateUtils.isRange("1880/01"));
        assertFalse(DateUtils.isRange("1880/01/01"));
        assertTrue(DateUtils.isRange("1980-01-01/1880-12-31"));  // is range doesn't test start/end
        assertTrue(DateUtils.isRange("1880/1881"));
        assertTrue(DateUtils.isRange("1880-03"));
        assertTrue(DateUtils.isRange("1884-01-01T05:05Z/1884-12-05"));
    }

	/**
	 * Test method for {@link org.filteredpush.qc.date.DateUtils#extractInterval(java.lang.String)}.
	 */
	@Test
	public void testExtractDateInterval() {
    	Interval test = DateUtils.extractDateInterval("1880-01-01/1880-12-31");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());
    	
    	test = DateUtils.extractDateInterval("1880");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());
    	
    	test = DateUtils.extractDateInterval("1880-02");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(2, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfMonth());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(2, test.getEnd().getMonthOfYear());
    	assertEquals(29, test.getEnd().getDayOfMonth());
    	
    	test = DateUtils.extractDateInterval("1880-01-01T08:30Z/1880-12-31");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(0, test.getStart().getHourOfDay());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());    	
    	assertEquals(0, test.getEnd().getHourOfDay());
    	
       	test = DateUtils.extractDateInterval("1880-02/1880-04");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(2, test.getStart().getMonthOfYear());
    	assertEquals(32, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(4, test.getEnd().getMonthOfYear());
    	assertEquals(30, test.getEnd().getDayOfMonth());    	
    }	

	@Test
	public void testExtractInterval() {
    	Interval test = DateUtils.extractInterval("1880-01-01/1880-12-31");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());
    	
    	test = DateUtils.extractInterval("1880-01-01");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(1, test.getEnd().getMonthOfYear());
    	assertEquals(1, test.getEnd().getDayOfMonth());
    	assertEquals(86399, test.toDuration().getStandardSeconds());
    	
    	test = DateUtils.extractInterval("1880-01");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1880, test.getEnd().getYear());
    	assertEquals(1, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth()); 
    	assertEquals(86400*31-1, test.toDuration().getStandardSeconds());
    	
    	test = DateUtils.extractInterval("1881");
    	assertEquals(1881, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1881, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());   
    	assertEquals(86400*365-1, test.toDuration().getStandardSeconds());
    	
       	test = DateUtils.extractInterval("1881/1882");
    	assertEquals(1881, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1882, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());    	
    	assertEquals(86400*365*2-1, test.toDuration().getStandardSeconds());
    	
    	test = DateUtils.extractInterval("1880/1881");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1881, test.getEnd().getYear());
    	assertEquals(12, test.getEnd().getMonthOfYear());
    	assertEquals(31, test.getEnd().getDayOfMonth());    	
    	// 1880 is a leap year, interval is 366+365 days.
    	assertEquals(86400*365*2-1+86400, test.toDuration().getStandardSeconds());    	
    	
       	test = DateUtils.extractInterval("1880-01/1881-02");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(1, test.getStart().getDayOfYear());
    	assertEquals(1881, test.getEnd().getYear());
    	assertEquals(2, test.getEnd().getMonthOfYear());
    	assertEquals(28, test.getEnd().getDayOfMonth());  
    	
       	test = DateUtils.extractInterval("1880-01-05/1881-02-05");
    	assertEquals(1880, test.getStart().getYear());
    	assertEquals(1, test.getStart().getMonthOfYear());
    	assertEquals(5, test.getStart().getDayOfYear());
    	assertEquals(1881, test.getEnd().getYear());
    	assertEquals(2, test.getEnd().getMonthOfYear());
    	assertEquals(5, test.getEnd().getDayOfMonth());  
    	
       	test = DateUtils.extractInterval("1880-01-05/1880-02-05");
    	assertEquals(86400*(32)-1, test.toDuration().getStandardSeconds());
	}
    	
	/**
	 * Test method for {@link org.filteredpush.qc.date.DateUtils#extractDate(java.lang.String)}.
	 */
    @Test
    public void extractDateTest() { 
    	DateMidnight test = DateUtils.extractDate("1980-04-03");
    	assertEquals(1980, test.getYear());
    	assertEquals(4, test.getMonthOfYear());
    	assertEquals(3, test.getDayOfMonth());
    	test = DateUtils.extractDate("1980-04");
    	assertEquals(1980, test.getYear());
    	assertEquals(4, test.getMonthOfYear());
    	assertEquals(1, test.getDayOfMonth());
    	test = DateUtils.extractDate("1980");
    	assertEquals(1980, test.getYear());
    	assertEquals(1, test.getMonthOfYear());
    	assertEquals(1, test.getDayOfMonth());
    	assertEquals(null,DateUtils.extractDate(""));
    }
    
    @Test
    public void isConsistentTest() { 
    	assertEquals(true, DateUtils.isConsistent("", "", "", ""));
    	assertEquals(true, DateUtils.isConsistent(null, "", "", ""));
    	assertEquals(true, DateUtils.isConsistent(null, "", null, ""));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18", "1884", "03", "18"));
    	assertEquals(false, DateUtils.isConsistent("1884-03-18", "1884", "03", "17"));
    	assertEquals(false, DateUtils.isConsistent("1884-03-18", "1884", "03", ""));
    	assertEquals(false, DateUtils.isConsistent("1884-03-18", "1884", "03", null));
    	assertEquals(false, DateUtils.isConsistent("1884-03-18", "1884", null, "18"));
    	assertEquals(false, DateUtils.isConsistent("1884-03-18", null, "03", "18"));
    	assertEquals(false, DateUtils.isConsistent(null, "1884", "03", "18"));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01", "1884", "01", null));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01", "1884", null, null));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01", null, null, null));
    	assertEquals(false, DateUtils.isConsistent(null, "1884", "1", "1"));
    	assertEquals(true, DateUtils.isConsistent("1884-03-01", "1884", "03", "1"));
    	assertEquals(true, DateUtils.isConsistent("1884-03-01", "1884", "03", "01"));
    	assertEquals(true, DateUtils.isConsistent("1884-03", "1884", "03", "01"));
    	assertEquals(false, DateUtils.isConsistent("1884-03", "1884", "03", "02"));
    	assertEquals(true, DateUtils.isConsistent("1884-03", "1884", "03", ""));
    	assertEquals(true, DateUtils.isConsistent("1884-03", "1884", "03", null));
    	assertEquals(true, DateUtils.isConsistent("1884-01-01/1884-01-31", "1884", "01", null));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01/1884-01-05", "1884", "01", null));    	
    	assertEquals(true, DateUtils.isConsistent("1884-01-01/1884-01-31", "1884", "01", ""));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01/1884-01-05", "1884", "01", ""));    	
    	assertEquals(true, DateUtils.isConsistent("1884-01-01/1884-12-31", "1884", null, null));
    	assertEquals(false, DateUtils.isConsistent("1884-01-01/1884-12-05", "1884", null, null));    	
    	assertEquals(false, DateUtils.isConsistent("1884-01-01T05:05Z/1884-12-05", "1884", null, null));
    	
    	assertEquals(true, DateUtils.isConsistent("1884-03-18/1884-03-19", "1884", "03", "18"));
    }
    
    @Test
    public void isConsistentTest2() {
    	assertEquals(true, DateUtils.isConsistent("", "", "", "", "",""));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18", "", "", "1884", "03", "18"));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18", "078", "078", "1884", "03", "18"));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18/1884-03-19", "078", "079", "1884", "03", "18"));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18/1884-03-19", "078", "079", null, null, null));
    	assertEquals(true, DateUtils.isConsistent("1884-03-18/1884-03-19", null, null, null, null, null));
    }
    
    @Test
    public void containsTimeTest() { 
    	assertEquals(false, DateUtils.containsTime(""));
    	assertEquals(false, DateUtils.containsTime(null));
    	assertEquals(false, DateUtils.containsTime("1905-04-08"));
    	assertEquals(false, DateUtils.containsTime("1905-04-08/1905-06-18"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T08"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04Z"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04UTC"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04:06"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04:06Z"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04:06-05:00"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T04:06-04:30"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T01:02:03.004Z"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T08/1905-06-18T08"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08T21/1905-06-18"));
    	assertEquals(true, DateUtils.containsTime("1905-04-08/1905-06-18T15"));
    	
    	assertEquals(true, DateUtils.containsTime("1905-04-08T00:00:00.000Z"));
    }
    
    @Test
    public void extractTimeTest() { 
    	assertEquals(null, DateUtils.extractZuluTime(""));
    	assertEquals(null, DateUtils.extractZuluTime(null));
    	assertEquals(null, DateUtils.extractZuluTime("1905-04-08"));
    	assertEquals("08:00:00.000Z", DateUtils.extractZuluTime("1905-04-08T08Z"));
    	assertEquals("08:32:16.000Z", DateUtils.extractZuluTime("1905-04-08T08:32:16Z"));
    	assertEquals("13:32:16.000Z", DateUtils.extractZuluTime("1905-04-08T08:32:16-05:00"));
    	assertEquals(null, DateUtils.extractZuluTime("1251e3254w2v"));
    }
    
    @Test
    public void specificityTest() { 
    	assertEquals(false, DateUtils.specificToDay(""));
    	assertEquals(false, DateUtils.specificToMonthScale(""));
    	assertEquals(false, DateUtils.specificToYearScale(""));
    	assertEquals(false, DateUtils.specificToDay(null));
    	assertEquals(false, DateUtils.specificToMonthScale(null));
    	assertEquals(false, DateUtils.specificToYearScale(null));
    	
    	assertEquals(false, DateUtils.specificToDay("1805-11-03/1805-11-05"));
    	assertEquals(false, DateUtils.specificToMonthScale("1805"));
    	assertEquals(false, DateUtils.specificToYearScale("1805/1806"));
    	
    	assertEquals(true, DateUtils.specificToDay("1805-11-03"));
    	assertEquals(true, DateUtils.specificToMonthScale("1805-11-03"));
    	assertEquals(true, DateUtils.specificToYearScale("1805-11-03"));
    	
    	assertEquals(false, DateUtils.specificToDay("1805-11"));
    	assertEquals(true, DateUtils.specificToMonthScale("1805-11"));
    	assertEquals(true, DateUtils.specificToYearScale("1805-11"));
    	
    	assertEquals(false, DateUtils.specificToDay("1805"));
    	assertEquals(false, DateUtils.specificToMonthScale("1805"));
    	assertEquals(true, DateUtils.specificToYearScale("1805"));   
    	
    	assertEquals(false, DateUtils.specificToDay("1805-11-03/1805-11-04"));
    	assertEquals(true, DateUtils.specificToMonthScale("1805-11-03/1805-11-04"));
    	assertEquals(true, DateUtils.specificToYearScale("1805-11-03/1805-11-04"));    
    	
    	assertEquals(false, DateUtils.specificToDay("1805-09-03/1805-11-04"));
    	assertEquals(false, DateUtils.specificToMonthScale("1805-09-03/1805-11-04"));
    	assertEquals(true, DateUtils.specificToYearScale("1805-09-03/1805-11-04")); 
    	
    	assertEquals(true, DateUtils.specificToYearScale("1805-09-03/1806-01-04")); 
    	
    	assertEquals(true, DateUtils.specificToDecadeScale("1805-09-03"));    	
    	assertEquals(true, DateUtils.specificToDecadeScale("1805-09"));    	
    	assertEquals(true, DateUtils.specificToDecadeScale("1805"));    	
    	assertEquals(true, DateUtils.specificToDecadeScale("1805/1806"));    	
    	assertEquals(true, DateUtils.specificToDecadeScale("1805-09-03/1806-01-04")); 
    	
    	assertEquals(false, DateUtils.specificToDecadeScale("1805-09-03/1815-10-01"));    	
    	assertEquals(false, DateUtils.specificToDecadeScale("1805/1816"));    	
    }
    
    @Test
    public void measureDateDurationTest() { 
    	assertEquals(0,DateUtils.measureDurationSeconds(""));
    	assertEquals(86399,DateUtils.measureDurationSeconds("1980-02-02"));
    	assertEquals(86400*29-1,DateUtils.measureDurationSeconds("1980-02"));
    	assertEquals(86400*28-1,DateUtils.measureDurationSeconds("1982-02"));
    	assertEquals(86400*365-1,DateUtils.measureDurationSeconds("1981"));
    	assertEquals(86400*366-1,DateUtils.measureDurationSeconds("1980"));
    	assertEquals((86400*(31+20))-1,DateUtils.measureDurationSeconds("1880-03-01/1880-04-20"));
    }
    

    @Test 
    /**
     * Test recognition of dates being suspect by provided date before suspect.
     */
    public void testExtractDateFromVerbatimERSuspect() { 
    	
	     EventResult result = DateUtils.extractDateFromVerbatimER("Sept. 5 1901", 1950, null);
	     assertEquals(EventResult.EventQCResultState.SUSPECT, result.getResultState());
	     assertEquals("1901-09-05", result.getResult());
	     
	     result = DateUtils.extractDateFromVerbatimER("Sept. 5 1901", 1850, null);
	     assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
	     assertEquals("1901-09-05", result.getResult());
	     
	     result = DateUtils.extractDateFromVerbatimER("Sept. 5 101", 50, null);
	     assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
	     assertEquals("0101-09-05", result.getResult());
	     
	     result = DateUtils.extractDateFromVerbatimER("Sept. 5 0101", 50, null);
	     assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
	     assertEquals("0101-09-05", result.getResult());
	     
	     result = DateUtils.extractDateFromVerbatimER("Sept. 5 41", 50, null);
	     assertEquals(EventResult.EventQCResultState.SUSPECT, result.getResultState());
	     assertEquals("0041-09-05", result.getResult());
	     
    } 
    
    @Test
    /** 
     * Test extraction of verbatim date values from VertNet reference set of patterns: 
     * https://github.com/kurator-org/kurator-validation/blob/783df57f56a54fd52fd6e22d97e96cdd828099f2/packages/kurator_dwca/data/referencedata/DateReferenceSet.txt    	
     */
    public void extractDateFromVerbatimERTestVertNetPatterns() { 
        /*
        19  31 August 1941
        20  19010000
        21  19630308
        22  XX NOV 1947
        23  23 XXX 1947
        24  XX XXX 1974
        25  1980s
        26  19-21.vii.1990
        27  9.ii-10.iii.2000
        28  xi.1996
        29  12.i.2000
        30  15.x.1993
        31  193-                  *** Not supported (yet, needs discussion)
        32  27-28 xi[no year]
        33  29 vi-13 vii-2001
        34  [no date] 1988
        35  purchased Dec 1997    *** Not supported (yet, needs discussion).
        36  18 x 1984,em 5 xi 84  *** Not supported.
        37  21 vii
        38  4 May
          
        40  1930
        41  17 Oct., 1993
        42  Aug. 1987
        43  Jan 1 1986
        44  jan 19 1985
        45  Dec 2 1984
        46  Sept.-Oct. 1982
        47  2011-08/2012-06
        48  5/9/1985              *** Conclusion differs
        */
    	
    	EventResult result = DateUtils.extractDateFromVerbatimER("31 August 1941");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1941-08-31", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("19010000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1901", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("19630308");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1963-03-08", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("XX NOV 1947");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1947-11", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("23 XXX 1947");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1947-01-23/1947-12-23", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("XX XXX 1974");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1974", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("1980s");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1980-01-01/1989-12-31", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("19-21.vii.1990");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1990-07-19/1990-07-21", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("9.ii-10.iii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-02-09/2000-03-10", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("xi.1996");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1996-11", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("12.i.2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-01-12", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("17 Oct., 1993");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1993-10-17", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("15.x.1993");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1993-10-15", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("193-");
        // problematic case, could be 1930/1939, or day 193 without a year, or year 193 without a month.
    	//assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	//assertEquals("1930-01-01/1939-12-31", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("27-28 xi[no year]");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
        result = DateUtils.extractDateFromVerbatimER("29 vi-13 vii-2001");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2001-06-29/2001-07-13", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("[no date] 1988");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1988", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("purchased Dec 1997");
        // Semantics here differ if this is a collecting event date (1700/1997-12-31) or just a date (1997/12)
    	//assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	//assertEquals("1997-12", result.getResult());
    	
        // **** Has a date range, but not supported. ****
        result = DateUtils.extractDateFromVerbatimER("18 x 1984,em 5 xi 84");
        // Problematic, special? case, "em" and mix of 2 and 4 digit years.  
        // "em" looks like bad character decoding for an encoding of an em-dash. 
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
        result = DateUtils.extractDateFromVerbatimER("4 May");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
    	result = DateUtils.extractDateFromVerbatimER("1930");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1930", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("17 Oct., 1993");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1993-10-17", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Aug. 1987");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1987-08", result.getResult());
        
        result = DateUtils.extractDateFromVerbatimER("Jan 1 1986");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1986-01-01", result.getResult());
        
        result = DateUtils.extractDateFromVerbatimER("jan 19 1985");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1985-01-19", result.getResult());
        
        result = DateUtils.extractDateFromVerbatimER("Dec 2 1984");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1984-12-02", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept.-Oct. 1982");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1982-09/1982-10", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("2011-08/2012-06");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2011-08/2012-06", result.getResult());    	
    	
    	result = DateUtils.extractDateFromVerbatimER("5/9/1985");
    	assertEquals(EventResult.EventQCResultState.AMBIGUOUS, result.getResultState());
    	assertEquals("1985-05-09/1985-09-05", result.getResult());    	
    	
    }
    
    
    @Test
    public void extractDateFromVerbatimERTest() { 
    	
    	EventResult result = DateUtils.extractDateFromVerbatimER("May 9th 1880");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1880-05-09", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	result = DateUtils.extractDateFromVerbatimER(null);
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	result = DateUtils.extractDateFromVerbatimER(" ");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	result = DateUtils.extractDateFromVerbatimER("[ ]");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	result = DateUtils.extractDateFromVerbatimER("[]");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	result = DateUtils.extractDateFromVerbatimER("..");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	result = DateUtils.extractDateFromVerbatimER("zzzzzz");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	result = DateUtils.extractDateFromVerbatimER("****");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
    	result = DateUtils.extractDateFromVerbatimER("18/35");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
    	result = DateUtils.extractDateFromVerbatimER("Jul. 9th 1880");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1880-07-09", result.getResult());   
    	
    	result = DateUtils.extractDateFromVerbatimER("1880/07/09");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1880-07-09", result.getResult());    
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept. 5 1901");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1901-09-05", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("[Sept. 5 1901]");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1901-09-05", result.getResult());   	
    	
    	result = DateUtils.extractDateFromVerbatimER("May 13. 1883");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1883-05-13", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("May 9th, 1915");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1915-05-09", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Oct. 13th, 1991");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1991-10-13", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Oct 13th, 1991");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1991-10-13", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("October 13th, 1991");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1991-10-13", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("October 14th 1902");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1902-10-14", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("October 15 1916");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1916-10-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Oct. 15-1916");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1916-10-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("May 16-1910");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1910-05-16", result.getResult());    	
    	
    	result = DateUtils.extractDateFromVerbatimER("May 20th. 1902");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1902-05-20", result.getResult());      	
    	
    	result = DateUtils.extractDateFromVerbatimER("11-VI-1886");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1886-06-11", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("6.I.1928");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1928-01-06", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("11-VII-1885");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1885-07-11", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("7. VII. 1878");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1878-07-07", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("7. VIII. 1878");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1878-08-07", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("7. X. 1877");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1877-10-07", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("7,V,1941");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1941-05-07", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("8.14.1893");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1893-08-14", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("31 April 1902");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
    	result = DateUtils.extractDateFromVerbatimER("July, 14, 1879");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1879-07-14", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("21 Sept.,1902");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1902-09-21", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("21 Sept.,1902.");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1902-09-21", result.getResult());     	
    	
    	result = DateUtils.extractDateFromVerbatimER("June 38, 1939");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
    	result = DateUtils.extractDateFromVerbatimER("May, 1 1962");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1962-05-01", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept. 1,1962");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1962-09-01", result.getResult());    	
    	
    	result = DateUtils.extractDateFromVerbatimER("11/5 1898");
    	assertEquals(EventResult.EventQCResultState.AMBIGUOUS, result.getResultState());
    	assertEquals("1898-05-11/1898-11-05", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("11 5 1898");
    	assertEquals(EventResult.EventQCResultState.AMBIGUOUS, result.getResultState());
    	assertEquals("1898-05-11/1898-11-05", result.getResult());    	
    	
    	result = DateUtils.extractDateFromVerbatimER("May, 18. 1898");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1898-05-18", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("III/20/1958");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1958-03-20", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("May 20. 1898");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1898-05-20", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("22 Sept, 1904");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1904-09-22", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("1943 June 10");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1943-06-10", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("June 17.1883");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1883-06-17", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("janvier 17 1883");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1883-01-17", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("janv. 17 1883");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1883-01-17", result.getResult());    	
    	
    	result = DateUtils.extractDateFromVerbatimER("1933, July 16");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1933-07-16", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("2010年10月18日");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2010-10-18", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1910-01-01/1911-12-31");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1910-01-01/1911-12-31", result.getResult());      	

    	result = DateUtils.extractDateFromVerbatimER("**-Nov-1910");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1910-11", result.getResult());      	
    	
    	result = DateUtils.extractDateFromVerbatimER("**-***-1910");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1910", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("**-**-1910");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1910", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("April 1871");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1871-04", result.getResult());  
    	
    	result = DateUtils.extractDateToDayFromVerbatimER("April 1871", DateUtils.YEAR_BEFORE_SUSPECT);
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1871-04-01/1871-04-30", result.getResult());   
    	
    	result = DateUtils.extractDateToDayFromVerbatimER("April 1871", 1900);
    	assertEquals(EventResult.EventQCResultState.SUSPECT, result.getResultState());
    	assertEquals("1871-04", result.getResult());  

    	result = DateUtils.extractDateFromVerbatimER("1981-03");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1981-03", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("June, 1871");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1871-06", result.getResult());  
    	
    	result = DateUtils.extractDateFromVerbatimER("July 16-26, 1945");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1945-07-16/1945-07-26", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("July 16 to 26, 1945");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1945-07-16/1945-07-26", result.getResult());     	
    	
    	result = DateUtils.extractDateFromVerbatimER("July 16-26 1945");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1945-07-16/1945-07-26", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("August 1-10, 2006");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2006-08-01/2006-08-10", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("giugno 1-10, 2006");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2006-06-01/2006-06-10", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("[Nov. 8]");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
    	result = DateUtils.extractDateFromVerbatimER("[March 8]");
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept.-1873");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1873-09", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("July 5 - 1889");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1889-07-05", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Aug. 7 -1892");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1892-08-07", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("12/1911");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1911-12", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept. 5 1901", 1950, null);
    	assertEquals(EventResult.EventQCResultState.SUSPECT, result.getResultState());
    	assertEquals("1901-09-05", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("11 5 1901", 1800, true);
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1901-11-05", result.getResult());   
    	
    	result = DateUtils.extractDateFromVerbatimER("11 5 1901", 1800, false);
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1901-05-11", result.getResult());     	
    	
    	result = DateUtils.extractDateFromVerbatimER("Apr/1963");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1963-04", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("May/1963");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1963-05", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1875-1899");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1875/1899", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("17.III-1933");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1933-03-17", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("17-III 1933");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1933-03-17", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("15 Giugno 1917");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1917-06-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("15 Juin 1917");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1917-06-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("15 giugno 1917");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1917-06-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("15 juin 1917");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1917-06-15", result.getResult());    	
    	
    	result = DateUtils.extractDateFromVerbatimER("15 Août 1917");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1917-08-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("15 août 1917");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1917-08-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("15 Aout 1917");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1917-08-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("15 aout 1917");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1917-08-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("15 agosto 1917");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1917-08-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("15 Agosto 1917");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1917-08-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("22.4.1927");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1927-04-22", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("4.4.1927");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1927-04-04", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("5.4.1927");
    	assertEquals(EventResult.EventQCResultState.AMBIGUOUS, result.getResultState());
    	assertEquals("1927-04-05/1927-05-04", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("05-05-1927/05-05-1927");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1927-05-05", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("31 x 1996");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1996-10-31", result.getResult());    	
    	
    	result = DateUtils.extractDateFromVerbatimER("IX-6 1962");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1962-09-06", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("2006.III.30");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2006-03-30", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("4/1959");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1959-04", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("IX.10-1920");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1920-09-10", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1913-14");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1913/1914", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1913-04");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1913-04", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1905-03");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1905-03", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1903-04");
    	assertEquals(EventResult.EventQCResultState.SUSPECT, result.getResultState());
    	assertEquals("1903-04", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1928-29");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1928/1929", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("8 Setiembre 1920");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1920-09-08", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("15.June 1956");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1956-06-15", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("30/Sep/1950");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1950-09-30", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("31/Sep/1950");  // only 30 days
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
    	result = DateUtils.extractDateFromVerbatimER("31iii2005");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2005-03-31", result.getResult());

    	result = DateUtils.extractDateFromVerbatimER("31March2005");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2005-03-31", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("31. Mar.2005");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2005-03-31", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Jan-Feb/1962");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1962-01/1962-02", result.getResult());
    	
    	result = DateUtils.extractDateToDayFromVerbatimER("Jan-Feb/1962", DateUtils.YEAR_BEFORE_SUSPECT);
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1962-01-01/1962-02-28", result.getResult());
    	
    	result = DateUtils.extractDateToDayFromVerbatimER("Jan-Feb/1964", DateUtils.YEAR_BEFORE_SUSPECT); 
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1964-01-01/1964-02-29", result.getResult());  // leap year
    	
    	result = DateUtils.extractDateFromVerbatimER("1908 June 10th");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1908-06-10", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1982 -1983");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1982/1983", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1934 - 1936");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1934/1936", result.getResult());
    	
    	// Checking translation of all roman numberal months
        result = DateUtils.extractDateFromVerbatimER("9.i-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-01-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.ii-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-02-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.iii-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-03-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.iv-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-04-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.v-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-05-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.vi-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-06-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.vii-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-07-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.viii-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-08-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.ix-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-09-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.x-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-10-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.xi-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-11-09/2000-12-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("9.xii-10.xii.2000");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2000-12-09/2000-12-10", result.getResult());    	
    	
        result = DateUtils.extractDateFromVerbatimER("10-i-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-01-10", result.getResult()); 
        result = DateUtils.extractDateFromVerbatimER("10-ii-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-02-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-iii-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-03-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-iv-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-04-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("10-v-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-05-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-vi-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-06-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-vii-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-07-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("10-viii-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-08-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("10-ix-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-09-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-x-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-10-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-xi-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-11-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-xii-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-12-10", result.getResult());
    	
        result = DateUtils.extractDateFromVerbatimER("10-I-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-01-10", result.getResult()); 
        result = DateUtils.extractDateFromVerbatimER("10-II-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-02-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-III-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-03-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-IV-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-04-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("10-V-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-05-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-VI-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-06-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-VII-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-07-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("10-VIII-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-08-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("10-IX-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-09-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-X-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-10-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-XI-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-11-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-XII-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-12-10", result.getResult());    	
    	
        result = DateUtils.extractDateFromVerbatimER("10-Jan-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-01-10", result.getResult()); 
        result = DateUtils.extractDateFromVerbatimER("10-Feb-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-02-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-Mar-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-03-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-Apr-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-04-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("10-May-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-05-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-Jun-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-06-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-Jul-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-07-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("10-Aug-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-08-10", result.getResult());    	
        result = DateUtils.extractDateFromVerbatimER("10-Sep-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-09-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-Oct-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-10-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-Nov-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-11-10", result.getResult());
        result = DateUtils.extractDateFromVerbatimER("10-Dec-2000");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("2000-12-10", result.getResult());      	
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept.-Oct. 1943");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1943-09/1943-10", result.getResult());  
   			
    	result = DateUtils.extractDateFromVerbatimER("4-11.viii.1934");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1934-08-04/1934-08-11", result.getResult());  
    	
    	result = DateUtils.extractDateFromVerbatimER("4-12 Mar 2006");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2006-03-04/2006-03-12", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Jan-Feb/1963");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1963-01/1963-02", result.getResult());    
    	
    	result = DateUtils.extractDateFromVerbatimER("Jan- Feb/1963");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1963-01/1963-02", result.getResult());     	
    	
    	result = DateUtils.extractDateFromVerbatimER("July 17 and 18, 1914");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1914-07-17/1914-07-18", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("8-15 to 20, 1884");
    	//assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	//assertEquals("1884-08-15/1884-08-20", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("August 29 - September 2, 2006");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2006-08-29/2006-09-02", result.getResult());   
    	
    	result = DateUtils.extractDateFromVerbatimER("Aug. 29 - Sept. 2, 2006");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("2006-08-29/2006-09-02", result.getResult());      	
    	
    	result = DateUtils.extractDateFromVerbatimER("May and June 1899");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1899-05/1899-06", result.getResult());    	
    	
    	result = DateUtils.extractDateFromVerbatimER("May to July 1899");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1899-05/1899-07", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("[29 Apr - 24 May 1847]");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1847-04-29/1847-05-24", result.getResult());    	
 
    	result = DateUtils.extractDateFromVerbatimER("May-June, 1902");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1902-05/1902-06", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("14. Aug 1853");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1853-08-14", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("21 APRIL 1968");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1968-04-21", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("[1840's]");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1840-01-01/1849-12-31", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1800's");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1800-01-01/1899-12-31", result.getResult());    	
    	
    	result = DateUtils.extractDateFromVerbatimER("VII/1892");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1892-07", result.getResult()); 
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept. 31, 1911");  // Sept has 30 days.
    	assertEquals(EventResult.EventQCResultState.NOT_RUN, result.getResultState());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept. 23' 1915");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1915-09-23", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept. 23'' 1915");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1915-09-23", result.getResult());
    		
    	result = DateUtils.extractDateFromVerbatimER("Sept 20'' 1903");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1903-09-20", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Jly 6 1916");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1916-07-06", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Jly. 1, 1920");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1920-07-01", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("7, July, 1901");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1901-07-07", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("June 8'' 1899");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1899-06-08", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("VII-20, 1897");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1897-07-20", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept., 2, 1889");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1889-09-02", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("August 2/1915");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1915-08-02", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("26th May, 1895");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1895-05-26", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Jan. /1901");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1901-01", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("August27, 1915");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1915-08-27", result.getResult());
		
    	result = DateUtils.extractDateFromVerbatimER("Aug.11, 1909");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1909-08-11", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept-25-1918");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1918-09-25", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("13 Aug..1942");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1942-08-13", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("10. 1. 1928");
    	assertEquals(EventResult.EventQCResultState.AMBIGUOUS, result.getResultState());
    	assertEquals("1928-01-10/1928-10-01", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1880. Aug 23.");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1880-08-23", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1886. Sept.");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1886-09", result.getResult());
    
    	result = DateUtils.extractDateFromVerbatimER("1924: Dec 24");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1924-12-24", result.getResult());
    
    	result = DateUtils.extractDateFromVerbatimER("1880. Aug 22");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1880-08-22", result.getResult());
    
    	result = DateUtils.extractDateFromVerbatimER("1880. July");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1880-07", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("8/16-1920");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1920-08-16", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("8/19, 1911");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1911-08-19", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept-Oct 1965");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1965-09/1965-10", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("29/7,1918");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1918-07-29", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("9/7,1918");
    	assertEquals(EventResult.EventQCResultState.AMBIGUOUS, result.getResultState());
    	assertEquals("1918-07-09/1918-09-07", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept. - Oct. 1965");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1965-09/1965-10", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("\"May, 1881\"");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1881-05", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("04 NOV 1989");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1989-11-04", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("Sept 6., 1898");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1898-09-06", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("SEpt. 10. 1932");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1932-09-10", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("April 3d. 1903.");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1903-04-03", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("1893,6,24");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1893-06-24", result.getResult());
    	
    	// With - or / as separator, assume yyyy-mm-dd format
    	result = DateUtils.extractDateFromVerbatimER("1893-6-19.");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1893-06-19", result.getResult());
    	
    	// Cases of year[.,]00[.,]00 could be yyyy-mm-dd, but the separators are non-standard, so 
    	// we should test for and assert ambiguity.
    	result = DateUtils.extractDateFromVerbatimER("1893,6.8");
    	assertEquals(EventResult.EventQCResultState.AMBIGUOUS, result.getResultState());
    	assertEquals("1893-06-08/1893-08-06", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("[20] Aug. 1898");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1898-08-20", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("OCt. 13, 1909");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1909-10-13", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("JUne 19 1966");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1966-06-19", result.getResult());
    	
    	result = DateUtils.extractDateFromVerbatimER("8 Sep,. 1947");
    	assertEquals(EventResult.EventQCResultState.DATE, result.getResultState());
    	assertEquals("1947-09-08", result.getResult());
    	
    	/*
    	 Not yet supported cases: 
    	 
    	 8-15 to 20, 1884
    	 II-VIII-1913
    	 
    	 Some cases where interpretation is problematic:
    	 6-11, 1902  -> (1902-06-11/1902-11-06 or 1902-06/1902-11)
    	 
    	 Discontinuous range: 
    	 Sept. & Oct. 1892 & Oct. 1893
    	 September 14th and 19th, 1916
    	 Sept. 27, 1896&Oct. 27, 1895
    	 May 18, May 25, June 7, 1895
    	 
    	*/  
    	
//    	Locale[] locs = Locale.getAvailableLocales();
//    	for (int i = 0; i<locs.length; i++) {
//    		System.out.println(locs[i].getLanguage());
//    	}
    	
    }    
    
    @Test
    public void testIsMonthInRange() { 
    	for (int i=1; i<=12; i++) { 
		   assertEquals(true,DateUtils.isMonthInRange(i));
    	}
    	
		assertEquals(false,DateUtils.isMonthInRange(0));
		assertEquals(false,DateUtils.isMonthInRange(13));
		assertEquals(false,DateUtils.isMonthInRange(-1));
		assertEquals(false,DateUtils.isMonthInRange(Integer.MAX_VALUE));
		assertEquals(false,DateUtils.isMonthInRange(Integer.MIN_VALUE));
    }
    
    @Test
    public void testIsDayInRange() { 
    	for (int i=1; i<=31; i++) { 
		   assertEquals(true,DateUtils.isDayInRange(i));
    	}
    	
		assertEquals(false,DateUtils.isDayInRange(0));
		assertEquals(false,DateUtils.isDayInRange(32));
		assertEquals(false,DateUtils.isDayInRange(-1));
		assertEquals(false,DateUtils.isDayInRange(Integer.MAX_VALUE));
		assertEquals(false,DateUtils.isDayInRange(Integer.MIN_VALUE));
    }    
    
    @Test
    public void testEventDateValid() { 
		assertEquals(true,DateUtils.eventDateValid("1880"));
		assertEquals(true,DateUtils.eventDateValid("1880-12"));
		assertEquals(true,DateUtils.eventDateValid("1880-12-31"));
		assertEquals(true,DateUtils.eventDateValid("1880-12-31/1881"));  
		assertEquals(true,DateUtils.eventDateValid("1880-12-31/1880"));  // format is valid
		assertEquals(true,DateUtils.eventDateValid("1880-12-31/1881-01"));
		assertEquals(true,DateUtils.eventDateValid("1880-12-31/1881-01-04"));
		
		assertEquals(true,DateUtils.eventDateValid("1884-01-01T05:05Z/1884-12-05"));
		assertEquals(true,DateUtils.eventDateValid("1805-09-03/1805-11-04"));
		
		assertEquals(true,DateUtils.eventDateValid("5"));  // valid ISO year, not zero padded.
		
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08T04"));
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08T08"));
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08T04Z"));
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08T04:06"));
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08T04:06Z"));
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08T04:06-05:00"));
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08T04:06-04:30"));
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08T01:02:03.004Z"));
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08T08/1905-06-18T08"));
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08T21/1905-06-18"));
    	assertEquals(true, DateUtils.eventDateValid("1905-04-08/1905-06-18T15"));	
    	
    	assertEquals(false, DateUtils.eventDateValid(""));
    	assertEquals(false, DateUtils.eventDateValid(null));
    	assertEquals(false, DateUtils.eventDateValid(" "));
    	
		assertEquals(false,DateUtils.eventDateValid("1880-Jan"));
		assertEquals(false,DateUtils.eventDateValid("1880-Jan-31"));    	
		assertEquals(false,DateUtils.eventDateValid("1880/12/31-1880"));
		assertEquals(false,DateUtils.eventDateValid("1880-12-31/1879"));  
    	
		assertEquals(false,DateUtils.eventDateValid("01-1880"));
		assertEquals(false,DateUtils.eventDateValid("31-01-1880"));    	
		assertEquals(false,DateUtils.eventDateValid("1880-Jan"));
		assertEquals(false,DateUtils.eventDateValid("1880/01/31")); 
		assertEquals(false,DateUtils.eventDateValid("1880/05")); 
		
		assertEquals(false,DateUtils.eventDateValid("1880-18")); // Doesn't parse as a date.
		assertEquals(false,DateUtils.eventDateValid("1880-25-11")); // Doesn't parse as a date.
		assertEquals(false,DateUtils.eventDateValid("1880-03-32")); // Doesn't parse as a date.
		assertEquals(false,DateUtils.eventDateValid("1880-02-31")); // Doesn't parse as a date.
		
    	assertEquals(false, DateUtils.eventDateValid("1905-04-08T04UTC"));  
    }
    
    @Test 
    public void testCreateEventDateFromStartEnd() { 
    	
    	assertEquals(null,DateUtils.createEventDateFromStartEnd(null,null));
    	assertEquals(null,DateUtils.createEventDateFromStartEnd("zzzzz",null));
    	assertEquals(null,DateUtils.createEventDateFromStartEnd("zzzzz","ZZZZ"));
    	
    	assertEquals("1964-03-05",DateUtils.createEventDateFromStartEnd("zzzzz","1964-03-05"));
    	assertEquals("1964-03-05",DateUtils.createEventDateFromStartEnd("1964-03-05","zzzz"));
    	
    	assertEquals("1960",DateUtils.createEventDateFromStartEnd("1960",null));
    	assertEquals("1960-01",DateUtils.createEventDateFromStartEnd("1960-01",null));
    	assertEquals("1960-01-04",DateUtils.createEventDateFromStartEnd("1960-01-04",null));
    	
    	assertEquals("1960",DateUtils.createEventDateFromStartEnd(null,"1960"));
    	assertEquals("1960/1964",DateUtils.createEventDateFromStartEnd(null,"1960/1964"));
    	
    	assertEquals("1960/1965",DateUtils.createEventDateFromStartEnd("1960","1965"));
    	assertEquals("1960-01/1965-04",DateUtils.createEventDateFromStartEnd("1960-01","1965-04"));
    	assertEquals("1960-01-04/1961-02-03",DateUtils.createEventDateFromStartEnd("1960-01-04","1961-02-03"));    	
    	
    	assertEquals("1960/1965-02",DateUtils.createEventDateFromStartEnd("1960","1965-02"));
    	assertEquals("1960-01/1965-04-08",DateUtils.createEventDateFromStartEnd("1960-01","1965-04-08"));
    	assertEquals("1960-01-08/1965-04",DateUtils.createEventDateFromStartEnd("1960-01-08","1965-04"));
    	
    	assertEquals("1960-01-04/1961-02-03",DateUtils.createEventDateFromStartEnd("1960-01-04","1961-Feb-03"));    	
    	assertEquals("1960-01-04/1961-02-03",DateUtils.createEventDateFromStartEnd("1960-01-04","3 Feb,1961")); 
    	
    	assertEquals("1960-01-04/1961-02-03",DateUtils.createEventDateFromStartEnd("1960-01-04/1960-01-08","1961-Feb-03"));    	
    	assertEquals("1960-01-04/1961-02-04",DateUtils.createEventDateFromStartEnd("1960-01-04/1960-01-08","1961-02-03/1961-02-04"));    	
    	
    	assertEquals("1961-02-03T04:05",DateUtils.createEventDateFromStartEnd("1961-02-03T04:05",null));
    	assertEquals("1961-02-03T04:05/1961-02-03T08:15",DateUtils.createEventDateFromStartEnd("1961-02-03T04:05","1961-02-03T08:15"));
    }
 
    @Test
    public void testIncludesLeapDay() { 
    	assertEquals(false, DateUtils.includesLeapDay(null));
    	assertEquals(false, DateUtils.includesLeapDay(""));
    	assertEquals(false, DateUtils.includesLeapDay("ZZZZZZ"));
    	
    	assertEquals(true, DateUtils.includesLeapDay("1980"));
    	assertEquals(false, DateUtils.includesLeapDay("1981"));
    	assertEquals(false, DateUtils.includesLeapDay("1982"));
    	assertEquals(false, DateUtils.includesLeapDay("1983"));
    	assertEquals(true, DateUtils.includesLeapDay("1984"));
    	
    	assertEquals(true, DateUtils.includesLeapDay("1600"));
    	assertEquals(false, DateUtils.includesLeapDay("1700"));
    	assertEquals(false, DateUtils.includesLeapDay("1800"));
    	assertEquals(false, DateUtils.includesLeapDay("1900"));
    	assertEquals(true, DateUtils.includesLeapDay("2000"));
    	
    	assertEquals(true, DateUtils.includesLeapDay("1984-02-28/1984-03-01"));
    	assertEquals(false, DateUtils.includesLeapDay("1985-02-28/1985-03-01"));
    	
    	assertEquals(true, DateUtils.includesLeapDay("1984-01-01/1985-12-31"));
    	assertEquals(false, DateUtils.includesLeapDay("1985-03-01/1985-12-31"));
    	assertEquals(false, DateUtils.includesLeapDay("1985-01-01/1985-02-28"));
    }
    
}
