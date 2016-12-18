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

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCEventDQ#isDayInRange(java.lang.String)}.
	 */
	@Test
	public void testIsDayInRange() {
		EventDQValidation result = null;
		for (int i = 1; i<=31; i++) { 
			result = DwCEventDQ.isDayInRange(Integer.toString(i));
			assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
			assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		}
		result = DwCEventDQ.isDayInRange(" 1 ");
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		result = DwCEventDQ.isDayInRange("01");
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());

		int i=0;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=32;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());	 

		i=-1;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=Integer.MAX_VALUE;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=Integer.MIN_VALUE;
		result = DwCEventDQ.isDayInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
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
			assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
			assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		}
		result = DwCEventDQ.isMonthInRange(" 1 ");
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		result = DwCEventDQ.isMonthInRange("01");
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());

		int i=0;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=13;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());	 

		i=-1;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=Integer.MAX_VALUE;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());

		i=Integer.MIN_VALUE;
		result = DwCEventDQ.isMonthInRange(Integer.toString(i));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
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
					assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
					assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
				}
			}
		}

		// Not a leap year
		int year = 2001;
		int month = 2;
		int day = 28;
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		
		year = 2001;
		month = 2;
		day = 29;  
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());
		
		year = 2001;
		month = 2;
		day = 30;  
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());
		
		// Leap year
		year = 2000;
		month = 2;
		day = 29;  
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.COMPLIANT, result.getResult());
		
		year = 2000;
		month = 2;
		day = 30;
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
		assertEquals(EnumDQValidationResult.NOT_COMPLIANT, result.getResult());
		
		year = 2000;
		month = 2;
		day = 31;
		result = DwCEventDQ.isDayPossibleForMonthYear(Integer.toString(year), Integer.toString(month), Integer.toString(day));
		assertEquals(EnumDQResultState.COMPLETED, result.getResultState());
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
}
