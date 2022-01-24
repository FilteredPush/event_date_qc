/** DwCOtherDateDQTest.java
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.model.ResultState;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class DwCOtherDateDQTest {
	private static final Log logger = LogFactory.getLog(DwCOtherDateDQTest.class);


	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCOtherDateDQ#validationDateidentifiedNotstandard(java.lang.String)}.
	 */
	@Test
	public void testValidationDateidentifiedNotstandard() {
		
        // INTERNAL_PREREQUISITES_NOT_MET if the field dwc:dateIdentified 
        // is either not present or is EMPTY; COMPLIANT if the value 
        // of the field dwc:dateIdentified is a valid ISO 8601-1:2019 
        //date; otherwise NOT_COMPLIANT 		
		
		String dateIdentified = null;
		
		DQResponse<ComplianceValue> result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		dateIdentified = "";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		dateIdentified = " ";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		dateIdentified = "\n";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		
		dateIdentified = "13ab/02/r3:06";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		dateIdentified = "string";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());	
		
		dateIdentified = "1700";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "1100";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());	
		
		dateIdentified = "1902-01";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "1100-01";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "1902-01-03";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "1100-01-22";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "2012-12-31";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "2004-02-29";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "2005-02-29";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "2004-366";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
		
		dateIdentified = "2005-366";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "1832/1900";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
	
		dateIdentified = "1832-12-15/1833-06-12";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());			
	
		dateIdentified = "2012/12/31";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "2012-12-32";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "2012-02-30";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "2012-Feb-02";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());		
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "2012-1-3";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		System.out.println(result.getComment());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
		dateIdentified = "1937-08-23/1037-09-09";
		result = DwCOtherDateDQ.validationDateidentifiedNotstandard(dateIdentified);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		System.out.println(result.getComment());
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());			
		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.DwCOtherDateDQ#validationDateidentifiedOutofrange(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testValidationDateidentifiedOutofrange() {
    	// INTERNAL_PREREQUISITES_NOT_MET if the field dwc:dateIdentified is 
    	// EMPTY or is not a valid ISO 8601-1:2019 date, or if the field 
    	// dwc:eventDate is not EMPTY and is not a valid ISO 8601-1:2019 date; 
    	// COMPLIANT if the value of the field dwc:dateIdentified is not prior to 
    	// dwc:eventDate, and is within the Parameter range; 
    	// otherwise NOT_COMPLIANT		
		
		// TODO: Change specification to handle empty dwc:eventDate, dateIdentified is not prior to non-empty eventDate.
		
		String eventDate = null;
		String dateIdentified = null;
		DQResponse<ComplianceValue> result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified, eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "";
		eventDate = "";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = " ";
		eventDate = "";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "string";
		eventDate = "";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "string";
		eventDate = "string";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "";
		eventDate = "string";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "1-3-1700";
		eventDate = "";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "1700-01-03";
		eventDate = "1-3-1700";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		logger.debug(result.getComment());
		logger.debug(result.getResultState().getLabel());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "1700-1-3";
		eventDate = "1700-01-03";
		logger.debug(DateUtils.eventDateValid(dateIdentified));
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		logger.debug(result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "1700-01-03";
		eventDate = "1700-1-3";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		logger.debug(result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "Jan 1, 1835";
		dateIdentified = "";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		eventDate = "1835-01-01";
		eventDate = "Jan 1, 1835";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "\n";
		eventDate = "";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "\t";
		eventDate = "";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "\n";
		eventDate = "1872";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "\t";
		eventDate = "1872";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());
		assertEquals(null, result.getValue());
		
		dateIdentified = "1752-12-31";
		eventDate = "1752-01-01";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		dateIdentified = "1753-01-01";
		eventDate = "1753-01-01";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		logger.debug(result.getResultState().getLabel());
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1785-03-15";
		eventDate = "1753";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1785-03-15";
		eventDate = "1785-03-14";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1785-03-15";
		eventDate = "1685-03-14";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());		
		
		dateIdentified = "1753-01-01";
		eventDate = "1753-01-01";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1812-05-22";
		eventDate = "1812-01-22";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1900-01-01";
		eventDate = "1899-12-31";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1935-12-31";
		eventDate = "1935";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1935-12-31";
		eventDate = "1935-11";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1935-12-31";
		eventDate = "1935-12-01";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1935-12-31";
		eventDate = "1935-12-31";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "2000-08-05";
		eventDate = "2000-08-04";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "2000-08-05";
		eventDate = "2000-08-05";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "2000-08-05";
		eventDate = "2000-08-06";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		dateIdentified = "\n";
		eventDate = "2000-08-06";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "\t";
		eventDate = "2000-08-06";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "1752-12-31";
		eventDate = "1752-12-31";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		dateIdentified = "1753-01";
		eventDate = "1753-01";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1753-01";
		eventDate = "1753-02";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		dateIdentified = "1753";
		eventDate = "1753";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1785-03-15";
		eventDate = "1785";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1785-03-15";
		eventDate = "1786";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		dateIdentified = "1753-01-01";
		eventDate = "1753";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1753-01-01";
		eventDate = "1753-02";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
		dateIdentified = "1812-05-22";
		eventDate = "1753";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1900-01-01";
		eventDate = "1500";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1935-12-31";
		eventDate = "1752-01-01";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "2000-08-05";
		eventDate = "2001";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		dateIdentified = "2004-02-29";
		eventDate = "2004-01";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified =  LocalDateTime.now().toString("yyyy-MM-dd"); 
        eventDate= LocalDateTime.now().toString("yyyy-MM-dd");
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		logger.debug(result.getComment());
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1753-01-01/"+ LocalDateTime.now().toString("yyyy-MM-dd");
		eventDate = dateIdentified;
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1753-01-01/"+ LocalDateTime.now().toString("yyyy-MM-dd");
		eventDate = "1753";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.COMPLIANT, result.getValue());
		
		dateIdentified = "1752-12-31/"+ LocalDateTime.now().toString("yyyy-MM-dd");
		eventDate = dateIdentified;
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		dateIdentified = "1753-08-16/2004-02-29";
		eventDate = "1763";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		logger.debug(result.getComment());
		logger.debug(result.getResultState().getLabel());
		// TODO: This case needs clarification in the specification
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		dateIdentified = "1503-08-16/1602-02-29";
		eventDate = "1500";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.INTERNAL_PREREQUISITES_NOT_MET, result.getResultState());	
		assertEquals(null, result.getValue());
		
		dateIdentified = "1503-08-16/1602-02-28";
		eventDate = "1503-08-16";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());
		
		dateIdentified = LocalDateTime.now().toString("yyyy-MM-dd") + "/" + LocalDateTime.now().plusDays(1).toString("yyyy-MM-dd");;
		eventDate = "1753";
		result = DwCOtherDateDQ.validationDateidentifiedOutofrange(dateIdentified,eventDate);
		assertEquals(ResultState.RUN_HAS_RESULT, result.getResultState());	
		assertEquals(ComplianceValue.NOT_COMPLIANT, result.getValue());		
		
	}
}