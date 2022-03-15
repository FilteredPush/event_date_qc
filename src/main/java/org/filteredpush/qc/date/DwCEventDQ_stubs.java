/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */

// TODO: Generated stub methods to incorporate into DwCEventDQ.
// This file is expected to be deleted when all methods are implemented.

package org.filteredpush.qc.date;

import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.ResultState;
import org.datakurator.ffdq.api.result.*;

@Mechanism(value="a5fdf476-2e84-4004-bdc1-fc606a5ca2c8",label="Kurator: Date Validator - DwCEventDQ:v2.1.0")
public class DwCEventDQ_stubs {

    /**
     * #52 Amendment SingleRecord Completeness: event from eventdate
     *
     * Provides: AMENDMENT_EVENT_FROM_EVENTDATE
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("710fe118-17e1-440f-b428-88ba3f547d6d")
    public DQResponse<AmendmentValue> amendmentEventFromEventdate(@ActedUpon("dwc:startDayOfYear") String startDayOfYear, @ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:year") String year, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day, @ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
        // or does not contain a valid ISO 8601-1:2019 date; AMENDED 
        // if one or more EMPTY terms of the dwc:Event class (dwc:year, 
        // dwc:month, dwc:day, dwc:startDayOfYear, dwc:endDayOfYear) 
        // have been filled in from a valid unambiguously interpretable 
        // value in dwc:eventDate and eventDate is wholly within one 
        //year; otherwise NOT_AMENDED 

        return result;
    }


    /**
     * #67 Validation SingleRecord Consistency: eventdate inconsistent
     *
     * Provides: VALIDATION_EVENT_INCONSISTENT
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("5618f083-d55a-4ac2-92b5-b9fb227b832f")
    public DQResponse<ComplianceValue> validationEventInconsistent(@ActedUpon("dwc:startDayOfYear") String startDayOfYear, @ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:year") String year, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day, @ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY, 
        // or all of dwc:year, dwc:month, dwc:day, dwc:startDayOfYear 
        // and dwc:endDayOfYear are EMPTY; COMPLIANT if all of the 
        // following conditions are met 1) the provided value of year 
        // matches the start year of the range represented by eventDate 
        // or year is empty, and 2) the provided value in month matches 
        // the start month of the range represented by eventDate or 
        // month is empty, and 3) the provided value in day matches 
        // the start day of the range represented by eventDate or day 
        // is empty, and 4) the provided value in startDayOfYear matches 
        // the start day of the year of the range represented by eventDate 
        // or startDayOfYear is empty, and 5) the provided value in 
        // endDayOfYear matches the end day of the year the range represented 
        // by eventDate or endDayOfYear is empty; otherwise NOT_COMPLIANT. 
        //

        return result;
    }


}
