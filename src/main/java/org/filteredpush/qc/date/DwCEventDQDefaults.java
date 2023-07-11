/**
 * DwCEventDQDefaults.java
 * 
 * Copyright 2022 President and Fellows of Harvard College
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

import java.time.LocalDateTime;
import java.util.Calendar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Mechanism;
import org.datakurator.ffdq.annotations.Parameter;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.ProvidesVersion;
import org.datakurator.ffdq.annotations.Specification;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;

/**
 * Implementation of DwCEventDQ methods that take parameters without the parameters, providing the default 
 * values for the parameters.   
 * 
 * @author mole
 *
 */
@Mechanism(
		value = "urn:uuid:a5fdf476-2e84-4004-bdc1-fc606a5ca2c8",
		label = "Kurator: Date Validator - DwCEventDQ:v3.0.3-SNAPSHOT")
public class DwCEventDQDefaults extends DwCEventDQ {

	private static final Log logger = LogFactory.getLog(DwCEventDQDefaults.class);

    /**
     * Is the value of dwc:eventDate entirely with the Parameter Range?
     * 
     * #36 Validation SingleRecord Conformance: eventdate outofrange
     * 
     * Given an eventDate check to see if that event date falls entirely outside a range from a
     * specified lower bound (1582-11-15 by default) and a specified upper bound (the end of the 
     * current year by default)   
     *
     * Provides: VALIDATION_EVENTDATE_INRANGE
     * Version: 2023-06-12
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_EVENTDATE_INRANGE", description="Is the value of dwc:eventDate entirely with the Parameter Range?")
    @Provides("3cff4dc4-72e9-4abe-9bf3-8a30f1618432")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/3cff4dc4-72e9-4abe-9bf3-8a30f1618432/2023-06-12")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY or if the value of dwc:eventDate is not a valid ISO 8601-1 date; COMPLIANT if the range of dwc:eventDate is entirely within the range bdq:earliestValidDate to bdq:latestValidDate, inclusive, otherwise NOT_COMPLIANT bdq:earliestValidDate default ='1582-11-15',bdq:latestValidDate default = current year")
    public static DQResponse<ComplianceValue> validationEventdateInrange(@ActedUpon("dwc:eventDate") String eventDate) {
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
        // or if the value of dwc:eventDate is not a valid ISO 8601-1 
        // date; COMPLIANT if the range of dwc:eventDate is entirely 
        // within the range bdq:earliestValidDate to bdq:latestValidDate, 
        // inclusive, otherwise NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:earliestValidDate="1582-11-15"; bdq:latestValidDate=current year    	
    	
    	String currentYear = String.format("%04d",Calendar.getInstance().get(Calendar.YEAR)) + "-12-31";
    	return DwCEventDQ.validationEventdateInrange(eventDate,"1582-11-15",currentYear);
    }
    
	/**
	 * Given a year, evaluate whether that year falls in the range between the default lower bound value of 1582 
	 * and the default current year as the upper bound. This implementation uses the year from the local date/time 
	 * as the upper bound, and will give different answers for values of year within 1 of the current year when run 
	 * in different time zones within one day of a year boundary, thus this test is not suitable for fine grained 
	 * evaluations of time.  
	 * 
     * #84 Validation SingleRecord Conformance: year outofrange
     *
     * Provides: VALIDATION_YEAR_INRANGE
     * Version: 2023-06-28
     * 
     * @param year the provided dwc:year to evaluate
     * @return DQResponse the response of type ComplianceValue to return
	 */
    @Validation(label="VALIDATION_YEAR_INRANGE", description="Is the value of dwc:year within the Parameter range?")
    @Provides("ad0c8855-de69-4843-a80c-a5387d20fbc8")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/ad0c8855-de69-4843-a80c-a5387d20fbc8/2023-06-28")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if dwc:year is not present, or is EMPTY or cannot be interpreted as an integer; COMPLIANT if the value of dwc:year is within the range bdq:earliestValidDate to bdq:latestValidDate inclusive; otherwise NOT_COMPLIANT bdq:earliestValidDate='1582',bdq:latestValidDate=current year")
    public static DQResponse<ComplianceValue> validationYearInrange(@ActedUpon("dwc:year") String year) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();
        // This test is defined as parameterized.
        // Default values: 
        // bdq:earliestDate="1582"; bdq:latestDate=current year
        
    	Integer upperBound = LocalDateTime.now().getYear();

        return validationYearInrange(year, 1582, upperBound);
    }
    
}
