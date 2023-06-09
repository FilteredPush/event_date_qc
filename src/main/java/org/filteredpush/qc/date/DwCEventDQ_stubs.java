/* NOTE: requires the ffdq-api dependecy in the maven pom.xml */

package org.filteredpush.qc.date;

import org.datakurator.ffdq.annotations.*;
import org.datakurator.ffdq.api.DQResponse;
import org.datakurator.ffdq.model.ResultState;
import org.datakurator.ffdq.api.result.*;

@Mechanism(value="a5fdf476-2e84-4004-bdc1-fc606a5ca2c8",label="Kurator: Date Validator - DwCEventDQ:v2.1.0")
public class DwCEventDQ_stubs {


    /**
     * Propose amendment to values in any of dwc:year, dwc:month, dwc:day, dwc:startDayOfYear or dwc:endDayOfYear from a the content of dwc:eventDate.
     *
     * Provides: AMENDMENT_EVENT_FROM_EVENTDATE
     * Version: 2023-03-29
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_EVENT_FROM_EVENTDATE", description="Propose amendment to values in any of dwc:year, dwc:month, dwc:day, dwc:startDayOfYear or dwc:endDayOfYear from a the content of dwc:eventDate.")
    @Provides("710fe118-17e1-440f-b428-88ba3f547d6d")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/710fe118-17e1-440f-b428-88ba3f547d6d/2023-03-29")
    public DQResponse<AmendmentValue> amendmentEventFromEventdate(@ActedUpon("dwc:startDayOfYear") String startDayOfYear, @ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:year") String year, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day, @ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
        // or contains an invalid value according to ISO 8601-1 or 
        // dwc:eventDate is not wholly within one year; FILLED_IN one 
        // or more EMPTY terms dwc:year, dwc:month, dwc:day, dwc:startDayOfYear, 
        // dwc:endDayOfYear if any were unambiguously interpreted from 
        // values in dwc:eventDate, and dwc:eventDate is wholly within 
        // one year; otherwise NOT_AMENDED 

        return result;
    }

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

    /**
     * Propose amendment to the value of dwc:eventDate from the content of dwc:verbatimEventDate.
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_VERBATIM
     * Version: 2023-03-29
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param verbatimEventDate the provided dwc:verbatimEventDate to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_EVENTDATE_FROM_VERBATIM", description="Propose amendment to the value of dwc:eventDate from the content of dwc:verbatimEventDate.")
    @Provides("6d0a0c10-5e4a-4759-b448-88932f399812")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/6d0a0c10-5e4a-4759-b448-88932f399812/2023-03-29")
    public DQResponse<AmendmentValue> amendmentEventdateFromVerbatim(@ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:verbatimEventDate") String verbatimEventDate) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is not EMPTY 
        // or the value of dwc:verbatimEventDate is EMPTY or not unambiguously 
        // interpretable as an ISO 8601-1 date; FILLED_IN the value 
        // of dwc:eventDate if an unambiguous ISO 8601-1 date was interpreted 
        // from dwc:verbatimEventDate; otherwise NOT_AMENDED 

        return result;
    }

    /**
     * Propose amendment to the value of dwc:eventDate from values in dwc:year, dwc:month and dwc:day.
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY
     * Version: 2023-03-29
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY", description="Propose amendment to the value of dwc:eventDate from values in dwc:year, dwc:month and dwc:day.")
    @Provides("3892f432-ddd0-4a0a-b713-f2e2ecbd879d")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/3892f432-ddd0-4a0a-b713-f2e2ecbd879d/2023-03-29")
    public DQResponse<AmendmentValue> amendmentEventdateFromYearmonthday(@ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:year") String year, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL _PREREQUISITES_NOT_MET if dwc:eventDate is not 
        // EMPTY or dwc:year is EMPTY or is not interpretable as a 
        // valid ISO 8601-1 year; FILLED_IN the value of dwc:eventDate 
        // if an ISO 8601-1 date was interpreted from the values in 
        // dwc:year, dwc:month and dwc:day; otherwise NOT_AMENDED 

        return result;
    }

    /**
     * Propose amendment to the value of dwc:eventDate from values in dwc:year, dwc:startDayOfYear and dwc:endDayOfYear.
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR
     * Version: 2023-03-01
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR", description="Propose amendment to the value of dwc:eventDate from values in dwc:year, dwc:startDayOfYear and dwc:endDayOfYear.")
    @Provides("eb0a44fa-241c-4d64-98df-ad4aa837307b")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/eb0a44fa-241c-4d64-98df-ad4aa837307b/2023-03-01")
    public DQResponse<AmendmentValue> amendmentEventdateFromYearstartdayofyearenddayofyear(@ActedUpon("dwc:startDayOfYear") String startDayOfYear, @ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:year") String year, @ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate was not 
        // EMPTY or any of dwc:year, dwc:startDayOfYear, or dwc:endDayOfYear 
        // were EMPTY or any of the values in dwc:year, dwc:startDayOfYear, 
        // or dwc:endDayOfYear were not independently interpretable; 
        // FILLED_IN the value of dwc:eventDate from values in dwc:year, 
        // dwc:startDayOfYear and dwc:endDayOfYear if the value of 
        // dwc:startDayOfYear is less than the value of dwc:endDayOfYear; 
        // otherwise NOT_AMENDED 

        return result;
    }

}
