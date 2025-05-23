/** DwCOtherDateDQDefaults.java
 * 
 * Copyright 2016-2025 President and Fellows of Harvard College
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.datakurator.ffdq.annotations.ActedUpon;
import org.datakurator.ffdq.annotations.Amendment;
import org.datakurator.ffdq.annotations.Consulted;
import org.datakurator.ffdq.annotations.Mechanism;
import org.datakurator.ffdq.annotations.Parameter;
import org.datakurator.ffdq.annotations.Provides;
import org.datakurator.ffdq.annotations.ProvidesVersion;
import org.datakurator.ffdq.annotations.Specification;
import org.datakurator.ffdq.annotations.Validation;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.api.result.AmendmentValue;
import org.datakurator.ffdq.api.result.ComplianceValue;
import org.datakurator.ffdq.model.ResultState;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * FFDQ tests for Date related concepts in Darwin Core outside of the Event class.
 * 
 * Provides Core TG2 tests with default parameter values: 
 *   #76 VALIDATION_DATEIDENTIFIED_INRANGE dc8aae4b-134f-4d75-8a71-c4186239178e
 *   
 * @author mole
 *
 */
@Mechanism(
		value = "urn:uuid:bf5b7706-d0a6-4c65-9644-c750e7188ee0",
		label = "Kurator: Date Validator - DwCOtherDateDQ:v3.1.1-SNAPSHOT")
public class DwCOtherDateDQDefaults extends DwCOtherDateDQ {
	
	private static final Log logger = LogFactory.getLog(DwCOtherDateDQ.class);
	
		
	/**
	 * Is the value of dwc:dateIdentified within Parameter ranges and either overlap or is later than dwc:eventDate?
	 *
	 * #76 Validation SingleRecord Likelihood: dateidentified outofrange
	 * with default values: bdq:earliestVaidDate="1753-01-01"; bdq:latestValidDate=current day
	 *
	 * Provides: VALIDATION_DATEIDENTIFIED_INRANGE
     * Version: 2024-09-16
	 *
	 * @param dateIdentified the provided dwc:dateIdentified to evaluate
	 * @param eventDate the provided dwc:eventDate to evaluate as preceding dateIdentified
	 * @return DQResponse the response of type ComplianceValue  to return
	 */
    @Validation(label="VALIDATION_DATEIDENTIFIED_INRANGE", description="Is the value of dwc:dateIdentified within Parameter ranges and either overlap or is later than dwc:eventDate?")
    @Provides("dc8aae4b-134f-4d75-8a71-c4186239178e")
    @ProvidesVersion("https://rs.tdwg.org/bdqtest/terms/dc8aae4b-134f-4d75-8a71-c4186239178e/2024-09-16")
    @Specification("INTERNAL_PREREQUISITES_NOT_MET if (1) dwc:dateIdentified is bdq:Empty, or (2) dwc:dateIdentified contains an invalid value according to ISO 8601, or (3) bdq:includeEventDate=true and dwc:eventDate is not a valid ISO 8601 date; COMPLIANT if the value of dwc:dateIdentified is between bdq:earliestValidDate and bdq:latestValidDate inclusive and either (1) dwc:eventDate is bdq:Empty or bdq:includeEventDate=false, or (2) if dwc:eventDate is a valid ISO 8601 date and dwc:dateIdentified overlaps or is later than the dwc:eventDate; otherwise NOT_COMPLIANT. ,bdq:earliestValidDate default = '1753-01-01',bdq:latestValidDate default = '{current day}',bdq:includeEventDate default = 'true'")
	public static DQResponse<ComplianceValue> validationDateidentifiedInrange(
		@ActedUpon("dwc:dateIdentified") String dateIdentified,
		@Consulted("dwc:eventDate") String eventDate
	) {
		// Parameters. This test is defined as parameterized.
		// Default values: 
		// bdq:earliestValidDate default="1753-01-01"; bdq:latestValidDate default=[current day]; bdq:includeEventDate default=true
		String currentDay = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
		return DwCOtherDateDQ.validationDateidentifiedInrange(dateIdentified, eventDate, "1753-01-01", currentDay, "true");
	} 


}
