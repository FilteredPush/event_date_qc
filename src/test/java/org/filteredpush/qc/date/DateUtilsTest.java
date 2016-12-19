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

import org.apache.commons.logging.Log;
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
    public void extractDateFromVerbatimTest() { 
    	Map<String,String> result = DateUtils.extractDateFromVerbatim("May 9th 1880");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1880-05-09", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim(null);
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim(" ");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim("[ ]");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim("[]");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim("..");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim("zzzzzz");
    	assertEquals(0, result.size());
    	result = DateUtils.extractDateFromVerbatim("****");
    	assertEquals(0, result.size());
    	
    	result = DateUtils.extractDateFromVerbatim("18/35");
    	assertEquals(0, result.size());
    	
    	result = DateUtils.extractDateFromVerbatim("Jul. 9th 1880");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1880-07-09", result.get("result"));   
    	
    	result = DateUtils.extractDateFromVerbatim("Sept. 5 1901");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1901-09-05", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("[Sept. 5 1901]");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1901-09-05", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("May 13. 1883");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1883-05-13", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("May 9th, 1915");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1915-05-09", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Oct. 13th, 1991");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1991-10-13", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Oct 13th, 1991");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1991-10-13", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("October 13th, 1991");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1991-10-13", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("October 14th 1902");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1902-10-14", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("October 15 1916");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1916-10-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Oct. 15-1916");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1916-10-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("May 16-1910");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1910-05-16", result.get("result"));    	
    	
    	result = DateUtils.extractDateFromVerbatim("May 20th. 1902");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1902-05-20", result.get("result"));      	
    	
    	result = DateUtils.extractDateFromVerbatim("11-VI-1886");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1886-06-11", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("6.I.1928");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1928-01-06", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("11-VII-1885");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1885-07-11", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("7. VII. 1878");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1878-07-07", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("7. VIII. 1878");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1878-08-07", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("7. X. 1877");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1877-10-07", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("7,V,1941");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1941-05-07", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("8.14.1893");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1893-08-14", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("31 April 1902");
    	assertEquals(0, result.size());
    	
    	result = DateUtils.extractDateFromVerbatim("July, 14, 1879");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1879-07-14", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("21 Sept.,1902");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1902-09-21", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("21 Sept.,1902.");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1902-09-21", result.get("result"));     	
    	
    	result = DateUtils.extractDateFromVerbatim("June 38, 1939");
    	assertEquals(0, result.size());
    	
    	result = DateUtils.extractDateFromVerbatim("May, 1 1962");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1962-05-01", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Sept. 1,1962");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1962-09-01", result.get("result"));    	
    	
    	result = DateUtils.extractDateFromVerbatim("11/5 1898");
    	assertEquals("ambiguous", result.get("resultState"));
    	assertEquals("1898-05-11/1898-11-05", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("May, 18. 1898");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1898-05-18", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("III/20/1958");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1958-03-20", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("May 20. 1898");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1898-05-20", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("22 Sept, 1904");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1904-09-22", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("1943 June 10");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1943-06-10", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("June 17.1883");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1883-06-17", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("janvier 17 1883");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1883-01-17", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("janv. 17 1883");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1883-01-17", result.get("result"));    	
    	
    	result = DateUtils.extractDateFromVerbatim("1933, July 16");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1933-07-16", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("2010年10月18日");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("2010-10-18", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("1910-01-01/1911-12-31");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1910-01-01/1911-12-31", result.get("result"));      	

    	result = DateUtils.extractDateFromVerbatim("April 1871");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1871-04", result.get("result"));  
    	
    	result = DateUtils.extractDateToDayFromVerbatim("April 1871", DateUtils.YEAR_BEFORE_SUSPECT);
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1871-04-01/1871-04-30", result.get("result"));      	
    	
    	result = DateUtils.extractDateFromVerbatim("1981-03");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1981-03", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("June, 1871");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1871-06", result.get("result"));  
    	
    	result = DateUtils.extractDateFromVerbatim("July 16-26, 1945");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1945-07-16/1945-07-26", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("July 16 to 26, 1945");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1945-07-16/1945-07-26", result.get("result"));     	
    	
    	result = DateUtils.extractDateFromVerbatim("July 16-26 1945");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1945-07-16/1945-07-26", result.get("result")); 
    	
    	result = DateUtils.extractDateFromVerbatim("August 1-10, 2006");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("2006-08-01/2006-08-10", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("giugno 1-10, 2006");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("2006-06-01/2006-06-10", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("[Nov. 8]");
    	assertEquals(0, result.size());
    	
    	result = DateUtils.extractDateFromVerbatim("[March 8]");
    	assertEquals(0, result.size());
    	
    	result = DateUtils.extractDateFromVerbatim("Sept.-1873");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1873-09", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("July 5 - 1889");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1889-07-05", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Aug. 7 -1892");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1892-08-07", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("12/1911");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1911-12", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Sept. 5 1901", 1950);
    	assertEquals("suspect", result.get("resultState"));
    	assertEquals("1901-09-05", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Apr/1963");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1963-04", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("May/1963");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1963-05", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("1875-1899");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1875/1899", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("17.III-1933");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1933-03-17", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("17-III 1933");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1933-03-17", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("15 Giugno 1917");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1917-06-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("15 Juin 1917");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1917-06-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("15 giugno 1917");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1917-06-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("15 juin 1917");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1917-06-15", result.get("result"));    	
    	
    	result = DateUtils.extractDateFromVerbatim("15 Août 1917");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1917-08-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("15 août 1917");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1917-08-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("15 Aout 1917");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1917-08-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("15 aout 1917");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1917-08-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("15 agosto 1917");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1917-08-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("15 Agosto 1917");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1917-08-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("22.4.1927");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1927-04-22", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("4.4.1927");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1927-04-04", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("5.4.1927");
    	assertEquals("ambiguous", result.get("resultState"));
    	assertEquals("1927-04-05/1927-05-04", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("05-05-1927/05-05-1927");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1927-05-05", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("31 x 1996");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1996-10-31", result.get("result"));    	
    	
    	result = DateUtils.extractDateFromVerbatim("IX-6 1962");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1962-09-06", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("2006.III.30");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("2006-03-30", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("4/1959");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1959-04", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("IX.10-1920");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1920-09-10", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("1913-14");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1913/1914", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("1913-04");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1913-04", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("1905-03");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1905-03", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("1903-04");
    	assertEquals("suspect", result.get("resultState"));
    	assertEquals("1903-04", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("1928-29");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1928/1929", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("8 Setiembre 1920");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1920-09-08", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("15.June 1956");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1956-06-15", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("30/Sep/1950");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1950-09-30", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("31/Sep/1950");  // only 30 days
    	assertEquals(0, result.size());
    	
    	result = DateUtils.extractDateFromVerbatim("31iii2005");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("2005-03-31", result.get("result"));

    	result = DateUtils.extractDateFromVerbatim("31March2005");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("2005-03-31", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("31. Mar.2005");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("2005-03-31", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("Jan-Feb/1962");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1962-01/1962-02", result.get("result"));
    	
    	result = DateUtils.extractDateToDayFromVerbatim("Jan-Feb/1962", DateUtils.YEAR_BEFORE_SUSPECT);
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1962-01-01/1962-02-28", result.get("result"));
    	
    	result = DateUtils.extractDateToDayFromVerbatim("Jan-Feb/1964", DateUtils.YEAR_BEFORE_SUSPECT); 
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1964-01-01/1964-02-29", result.get("result"));  // leap year
    	
    	result = DateUtils.extractDateFromVerbatim("1908 June 10th");
    	assertEquals("date", result.get("resultState"));
    	assertEquals("1908-06-10", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("1982 -1983");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1982/1983", result.get("result"));
    	
    	result = DateUtils.extractDateFromVerbatim("1934 - 1936");
    	assertEquals("range", result.get("resultState"));
    	assertEquals("1934/1936", result.get("result"));
    	
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

    	result = DateUtils.extractDateFromVerbatimER("April 1871");
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1871-04", result.getResult());  
    	
    	result = DateUtils.extractDateToDayFromVerbatimER("April 1871", DateUtils.YEAR_BEFORE_SUSPECT);
    	assertEquals(EventResult.EventQCResultState.RANGE, result.getResultState());
    	assertEquals("1871-04-01/1871-04-30", result.getResult());      	
    	
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
    	
    	/*
    	 May and June 1899
    	 [29 Apr - 24 May 1847]
		 July 17 and 18, 1914
    	 Sept.-Oct. 1943
    	 Jan- Feb/1963
    	 4-11.viii.1934
		 4-12 Mar 2006
		 8-15 to 20, 1884
		 August 29 - September 2, 2006
		 
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
    
}
