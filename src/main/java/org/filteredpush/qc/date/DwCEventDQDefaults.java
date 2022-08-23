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
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.ComplianceValue;

/**
 * @author mole
 *
 */
public class DwCEventDQDefaults extends DwCEventDQ {

	private static final Log logger = LogFactory.getLog(DwCEventDQDefaults.class);

    /**
     * #36 Validation SingleRecord Conformance: eventdate outofrange
     * 
     * Given an eventDate check to see if that event date falls entirely outside a range from a
     * specified lower bound (1600-01-01 by default) and a specified upper bound (the end of the 
     * current year by default)   
     *
     * Provides: VALIDATION_EVENTDATE_OUTOFRANGE
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("3cff4dc4-72e9-4abe-9bf3-8a30f1618432")
    public static DQResponse<ComplianceValue> validationEventdateOutofrange(@ActedUpon("dwc:eventDate") String eventDate) {
        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
        // or if the value of dwc:eventDate is not a valid ISO 8601-1:2019 
        // date; COMPLIANT if the range of dwc:eventDate is entirely 
        // within the parameter range, otherwise NOT_COMPLIANT 

        // Parameters. This test is defined as parameterized.
        // Default values: bdq:earliestValidDate="1600"; bdq:latestValidDate=current year
    	
    	String currentYear = String.format("%04d",Calendar.getInstance().get(Calendar.YEAR)) + "-12-31";
    	return DwCEventDQ.validationEventdateOutofrange(eventDate,"1600-01-01",currentYear);
    }
    
	/**
	 * Given a year, evaluate whether that year falls in the range between lower bound value of 1600 
	 * and the current year as the upper bound. This implementation uses the year from the local date/time 
	 * as the upper bound, and will give different answers for values of year within 1 of the current year when run 
	 * in different time zones within one day of a year boundary, thus this test is not suitable for fine grained 
	 * evaluations of time.
	 * 
     * #84 Validation SingleRecord Conformance: year outofrange
     *
     * Provides: VALIDATION_YEAR_OUTOFRANGE
     * 
     * @param year the provided dwc:year to evaluate
     * @return DQResponse the response of type ComplianceValue to return
	 */
    @Provides("ad0c8855-de69-4843-a80c-a5387d20fbc8")
    public static DQResponse<ComplianceValue> validationYearOutofrange(@ActedUpon("dwc:year") String year) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        // Specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:year is not present, 
        // or is EMPTY or cannot be interpreted as an integer; COMPLIANT 
        // if the value of dwc:year is within the Parameter range; 
        // otherwise NOT_COMPLIANT 

        // This test is defined as parameterized.
        // bdq:earliestDate="1600"; bdq:latestDate=current year
        
    	Integer upperBound = LocalDateTime.now().getYear();

        return validationYearOutofrange(year, 1600, upperBound);
    }

}
