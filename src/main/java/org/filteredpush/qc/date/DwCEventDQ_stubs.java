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
     * Is the value of dwc:eventDate a valid ISO date?
     *
     * Provides: VALIDATION_EVENTDATE_STANDARD
     * Version: 2023-03-29
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_EVENTDATE_STANDARD", description="Is the value of dwc:eventDate a valid ISO date?")
    @Provides("4f2bf8fd-fc5c-493f-a44c-e7b16153c803")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/4f2bf8fd-fc5c-493f-a44c-e7b16153c803/2023-03-29")
    public DQResponse<ComplianceValue> validationEventdateStandard(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY; 
        // COMPLIANT if the value of dwc:eventDate is a valid ISO 8601-1 
        // date; otherwise NOT_COMPLIANT 

        return result;
    }

    /**
     * Are the values in dwc:eventDate consistent with the values in dwc:year, dwc:month, dwc:day, dwc:startDayOfYear and dwc:endDayOfYear?
     *
     * Provides: VALIDATION_EVENT_CONSISTENT
     * Version: 2023-01-28
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_EVENT_CONSISTENT", description="Are the values in dwc:eventDate consistent with the values in dwc:year, dwc:month, dwc:day, dwc:startDayOfYear and dwc:endDayOfYear?")
    @Provides("5618f083-d55a-4ac2-92b5-b9fb227b832f")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/5618f083-d55a-4ac2-92b5-b9fb227b832f/2023-01-28")
    public DQResponse<ComplianceValue> validationEventConsistent(@ActedUpon("dwc:startDayOfYear") String startDayOfYear, @ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:year") String year, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day, @ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY, 
        // or all of dwc:year, dwc:month, dwc:day, dwc:startDayOfYear 
        // and dwc:endDayOfYear are EMPTY; COMPLIANT if all of the 
        // following conditions are met 1) the provided value of year 
        // matches the start year of the range represented by dwc:eventDate 
        // or dwc:year is empty, and 2) the provided value in dwc:month 
        // matches the start month of the range represented by dwc:eventDate 
        // or dwc:month is empty, and 3) the provided value in dwc:day 
        // matches the start day of the range represented by dwc:eventDate 
        // or dwc:day is empty, and 4) the provided value in dwc:startDayOfYear 
        // matches the start day of the year of the range represented 
        // by dwc:eventDate or dwc:startDayOfYear is empty, and 5) 
        // the provided value in dwc:endDayOfYear matches the end day 
        // of the year the range represented by dwc:eventDate or dwc:endDayOfYear 
        // is empty; otherwise NOT_COMPLIANT. 

        return result;
    }

    /**
     * Is there a value in dwc:dataGeneralizations?
     *
     * Provides: ISSUE_DATAGENERALIZATIONS_NOTEMPTY
     * Version: 2022-11-08
     *
     * @param dataGeneralizations the provided dwc:dataGeneralizations to evaluate
     * @return DQResponse the response of type IssueValue to return
     */
    @Issue(label="ISSUE_DATAGENERALIZATIONS_NOTEMPTY", description="Is there a value in dwc:dataGeneralizations?")
    @Provides("13d5a10e-188e-40fd-a22c-dbaa87b91df2")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/13d5a10e-188e-40fd-a22c-dbaa87b91df2/2022-11-08")
    public DQResponse<IssueValue> issueDatageneralizationsNotempty(@ActedUpon("dwc:dataGeneralizations") String dataGeneralizations) {
        DQResponse<IssueValue> result = new DQResponse<IssueValue>();

        //TODO:  Implement specification
        // POTENTIAL_ISSUE if dwc:dataGeneralizations is not EMPTY; 
        // otherwise NOT_ISSUE 

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
     * Is there a value in any of the terms dwc:eventDate, dwc:year, dwc:month, dwc:day, dwc:startDayOfYear, dwc:endDayOfYear, dwc:verbatimEventDate?
     *
     * Provides: VALIDATION_EVENT_TEMPORAL_NOTEMPTY
     * Version: 2022-11-09
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param verbatimEventDate the provided dwc:verbatimEventDate to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_EVENT_TEMPORAL_NOTEMPTY", description="Is there a value in any of the terms dwc:eventDate, dwc:year, dwc:month, dwc:day, dwc:startDayOfYear, dwc:endDayOfYear, dwc:verbatimEventDate?")
    @Provides("41267642-60ff-4116-90eb-499fee2cd83f")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/41267642-60ff-4116-90eb-499fee2cd83f/2022-11-09")
    public DQResponse<ComplianceValue> validationEventTemporalNotempty(@ActedUpon("dwc:startDayOfYear") String startDayOfYear, @ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:year") String year, @ActedUpon("dwc:verbatimEventDate") String verbatimEventDate, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day, @ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // COMPLIANT if any of dwc:eventDate, dwc:year, dwc:month, 
        // dwc:day, dwc:startDayOfYear, dwc:endDayOfYear, dwc:verbatimEventDate 
        // are NOT EMPTY; otherwise NOT_COMPLIANT. 

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
     * Is the value of dwc:day interpretable as a valid integer between 1 and 28 inclusive or 29, 30 or 31 given the relative month and year?
     *
     * Provides: VALIDATION_DAY_INRANGE
     * Version: 2023-03-29
     *
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_DAY_INRANGE", description="Is the value of dwc:day interpretable as a valid integer between 1 and 28 inclusive or 29, 30 or 31 given the relative month and year?")
    @Provides("8d787cb5-73e2-4c39-9cd1-67c7361dc02e")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/8d787cb5-73e2-4c39-9cd1-67c7361dc02e/2023-03-29")
    public DQResponse<ComplianceValue> validationDayInrange(@ActedUpon("dwc:year") String year, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if (a) dwc:day is EMPTY, 
        // or (b) dwc:day is not interpretable as an integer, or (c) 
        // dwc:day is interpretable as an integer between 29 and 31 
        // inclusive and dwc:month is not interpretable as an integer 
        // between 1 and 12, or (d) dwc:month is interpretable as the 
        // integer 2 and dwc:day is interpretable as the integer 29 
        // and dwc:year is not interpretable as a valid ISO 8601-1 
        // year; COMPLIANT if (a) the value of dwc:day is interpretable 
        // as an integer between 1 and 28 inclusive, or (b) dwc:day 
        // is interpretable as an integer between 29 and 30 and dwc:month 
        // is interpretable as an integer in the set (4,6,9,11), or 
        // (c) dwc:day is interpretable as an integer between 29 and 
        // 31 and dwc:month is interpretable as an integer in the set 
        // (1,3,5,7,8,10,12), or (d) dwc:day is interpretable as the 
        // integer 29 and dwc:month is interpretable as the integer 
        // 2 and dwc:year is interpretable as is a valid leap year 
        // (evenly divisible by 400 or (evenly divisible by 4 but not 
        // evenly divisible by 100)); otherwise NOT_COMPLIANT." 

        return result;
    }

    /**
     * Is the value of dwc:month interpretable as an integer between 1 and 12 inclusive?
     *
     * Provides: VALIDATION_MONTH_STANDARD
     * Version: 2023-03-01
     *
     * @param month the provided dwc:month to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Validation(label="VALIDATION_MONTH_STANDARD", description="Is the value of dwc:month interpretable as an integer between 1 and 12 inclusive?")
    @Provides("01c6dafa-0886-4b7e-9881-2c3018c98bdc")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/01c6dafa-0886-4b7e-9881-2c3018c98bdc/2023-03-01")
    public DQResponse<ComplianceValue> validationMonthStandard(@ActedUpon("dwc:month") String month) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:month is EMPTY; COMPLIANT 
        // if the value of dwc:month is interpretable as an integer 
        // between 1 and 12 inclusive; otherwise NOT_COMPLIANT 

        return result;
    }

    /**
     * Propose amendment to the value of dwc:day as a integer between 1 and 31 inclusive.
     *
     * Provides: AMENDMENT_DAY_STANDARDIZED
     * Version: 2022-11-13
     *
     * @param day the provided dwc:day to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_DAY_STANDARDIZED", description="Propose amendment to the value of dwc:day as a integer between 1 and 31 inclusive.")
    @Provides("b129fa4d-b25b-43f7-9645-5ed4d44b357b")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/b129fa4d-b25b-43f7-9645-5ed4d44b357b/2022-11-13")
    public DQResponse<AmendmentValue> amendmentDayStandardized(@ActedUpon("dwc:day") String day) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; AMENDED 
        // the value of dwc:day if the value was unambiguously interpreted 
        // as an integer between 1 and 31 inclusive; otherwise NOT_AMENDED 
        // 

        return result;
    }

    /**
     * Propose an amendment to the value of dwc:month as an integer between 1 and 12 inclusive.
     *
     * Provides: AMENDMENT_MONTH_STANDARDIZED
     * Version: 2022-11-10
     *
     * @param month the provided dwc:month to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Amendment(label="AMENDMENT_MONTH_STANDARDIZED", description="Propose an amendment to the value of dwc:month as an integer between 1 and 12 inclusive.")
    @Provides("2e371d57-1eb3-4fe3-8a61-dff43ced50cf")
    @ProvidesVersion("https://rs.tdwg.org/bdq/terms/2e371d57-1eb3-4fe3-8a61-dff43ced50cf/2022-11-10")
    public DQResponse<AmendmentValue> amendmentMonthStandardized(@ActedUpon("dwc:month") String month) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:month is EMPTY; AMENDED 
        // the value of dwc:month if it was able to be unambiguously 
        // interpreted as an integer between 1 and 12 inclusive; otherwise 
        // NOT_AMENDED 

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
