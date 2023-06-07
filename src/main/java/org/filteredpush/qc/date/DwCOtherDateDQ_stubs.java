/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */

package org.filteredpush.qc.date;

import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.ResultState;
import org.datakurator.ffdq.api.result.*;

@Mechanism(value="bf5b7706-d0a6-4c65-9644-c750e7188ee0",label="Kurator: Date Validator - DwCOtherDateDQ:v2.1.0")
public class DwCOtherDateDQ_stubs {

    /**
     * Propose amendment to the value of dwc:dateIdentified to a valid ISO date.
     *
     * Provides: AMENDMENT_DATEIDENTIFIED_STANDARDIZED
     * Version: 2023-03-29
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_DATEIDENTIFIED_STANDARDIZED", description="Propose amendment to the value of dwc:dateIdentified to a valid ISO date.")
    @Provides("39bb2280-1215-447b-9221-fd13bc990641")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/39bb2280-1215-447b-9221-fd13bc990641/2023-03-29")
    public DQResponse<AmendmentValue> amendmentDateidentifiedStandardized(@ActedUpon("dwc:dateIdentified") String dateIdentified) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
        // EMPTY; AMENDED the value of dwc:dateIdentified if it was 
        // unambiguous and formatted as a valid ISO 8601-1 date; otherwise 
        // NOT_AMENDED 

        return result;
    }

    /**
     * Is the value of dwc:dateIdentified a valid ISO date?
     *
     * Provides: VALIDATION_DATEIDENTIFIED_STANDARD
     * Version: 2023-03-29
     *
     * @param dateIdentified the provided dwc:dateIdentified to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DATEIDENTIFIED_STANDARD", description="Is the value of dwc:dateIdentified a valid ISO date?")
    @Provides("66269bdd-9271-4e76-b25c-7ab81eebe1d8")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/66269bdd-9271-4e76-b25c-7ab81eebe1d8/2023-03-29")
    public DQResponse<ComplianceValue> validationDateidentifiedStandard(@ActedUpon("dwc:dateIdentified") String dateIdentified) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:dateIdentified is 
        // EMPTY; COMPLIANT if the value of dwc:dateIdentified contains 
        // a valid ISO 8601-1 date; otherwise NOT_COMPLIANT 

        return result;
    }

}
