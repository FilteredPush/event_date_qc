/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */

package org.filteredpush.qc.date;

import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.ResultState;
import org.datakurator.ffdq.api.result.*;

@Mechanism(value="a5fdf476-2e84-4004-bdc1-fc606a5ca2c8",label="Kurator: Date Validator - DwCEventDQ:v2.1.0")
public class DwCEventDQ_stubs {


    /**
     * Propose amendment of the value of dwc:eventDate to a valid ISO date.
     *
     * Provides: AMENDMENT_EVENTDATE_STANDARDIZED
     * Version: 2023-03-29
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_EVENTDATE_STANDARDIZED", description="Propose amendment of the value of dwc:eventDate to a valid ISO date.")
    @Provides("718dfc3c-cb52-4fca-b8e2-0e722f375da7")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/718dfc3c-cb52-4fca-b8e2-0e722f375da7/2023-03-29")
    public DQResponse<AmendmentValue> amendmentEventdateStandardized(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY; 
        // AMENDED the value of dwc:eventDate if it was unambiguous 
        // and formatted as a valid ISO 8601-1 date; otherwise NOT_AMENDED 
        // 

        return result;
    }

}
