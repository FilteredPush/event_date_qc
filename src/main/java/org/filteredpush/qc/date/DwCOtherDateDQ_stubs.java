/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */

package org.filteredpush.qc.date;

import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.ResultState;
import org.datakurator.ffdq.api.result.*;

@Mechanism(value="bf5b7706-d0a6-4c65-9644-c750e7188ee0",label="Kurator: Date Validator - DwCOtherDateDQ:v2.1.0")
public class DwCOtherDateDQ_stubs {

    /**
     * #26 Amendment SingleRecord Conformance: dateidentified standardized
     *
     * Provides: AMENDMENT_DATEIDENTIFIED_STANDARDIZED
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("39bb2280-1215-447b-9221-fd13bc990641")
    public DQResponse<AmendmentValue> amendmentDateidentifiedStandardized(@ActedUpon("dwc:dateIdentified") String dateIdentified) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
        // EMPTY; AMENDED if the value of dwc:dateIdentified was altered 
        // to unambiguously conform with the ISO 8601-1:2019 date format; 
        //otherwise NOT_AMENDED 

        return result;
    }

    /**
     * #69 Validation SingleRecord Conformance: dateidentified notstandard
     *
     * Provides: VALIDATION_DATEIDENTIFIED_NOTSTANDARD
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("66269bdd-9271-4e76-b25c-7ab81eebe1d8")
    public DQResponse<ComplianceValue> validationDateidentifiedNotstandard(@ActedUpon("dwc:dateIdentified") String dateIdentified) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
        // EMPTY; COMPLIANT if the value of dwc:dateIdentified is a 
        //valid ISO 8601-1:2019 date; otherwise NOT_COMPLIANT 

        return result;
    }

    /**
     * #76 Validation SingleRecord Likelihood: dateidentified outofrange
     *
     * Provides: VALIDATION_DATEIDENTIFIED_OUTOFRANGE
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("dc8aae4b-134f-4d75-8a71-c4186239178e")
    public DQResponse<ComplianceValue> validationDateidentifiedOutofrange(@ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:dateIdentified") String dateIdentified) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if any of these three conditions 
        // are met (1) dwc:dateIdentified is EMPTY, (2) dwc:dateIdentified 
        // is not a valid ISO 8601-1:2019 date, (3) dwc:eventDate is 
        // not EMPTY and is not a valid ISO 8601-1:2019 date; COMPLIANT 
        // if the value of dwc:dateIdentified is within the parameter 
        // ranges and either (1) dwc:eventDate is EMPTY or (2) if dwc:eventDate 
        // is a valid ISO 8601-1:2019 date and dwc:dateIdentified overlaps 
        //or follows the dwc:eventDate; otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // Default values: bdq:earliestVaidDate="1753-01-01"; bdq:latestValidDate=current day

        return result;
    }

}
