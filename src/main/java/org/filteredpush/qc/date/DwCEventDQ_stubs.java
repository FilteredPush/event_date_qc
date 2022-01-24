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
     * #33 Validation SingleRecord Completeness: eventdate empty
     *
     * Provides: VALIDATION_EVENTDATE_EMPTY
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("f51e15a6-a67d-4729-9c28-3766299d2985")
    public DQResponse<ComplianceValue> validationEventdateEmpty(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // COMPLIANT if dwc:eventDate is not EMPTY; otherwise NOT_COMPLIANT 
        //

        return result;
    }

    /**
     * #36 Validation SingleRecord Conformance: eventdate outofrange
     *
     * Provides: VALIDATION_EVENTDATE_OUTOFRANGE
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("3cff4dc4-72e9-4abe-9bf3-8a30f1618432")
    public DQResponse<ComplianceValue> validationEventdateOutofrange(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
        // or if the value of dwc:eventDate is not a valid ISO 8601-1:2019 
        // date; COMPLIANT if the range of dwc:eventDate is entirely 
        //within the parameter range, otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // Default values: bdq:earliestValidDate="1600"; bdq:latestValidDate=current year

        return result;
    }

    /**
     * #49 Validation SingleRecord Completeness: year empty
     *
     * Provides: VALIDATION_YEAR_EMPTY
     *
     * @param year the provided dwc:year to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("c09ecbf9-34e3-4f3e-b74a-8796af15e59f")
    public DQResponse<ComplianceValue> validationYearEmpty(@ActedUpon("dwc:year") String year) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // COMPLIANT if dwc:year is not EMPTY; otherwise NOT_COMPLIANT 
        //

        return result;
    }

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
        //year; otherwise NOT_CHANGED 

        return result;
    }

    /**
     * #61 Amendment SingleRecord Conformance: eventdate standardized
     *
     * Provides: AMENDMENT_EVENTDATE_STANDARDIZED
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("718dfc3c-cb52-4fca-b8e2-0e722f375da7")
    public DQResponse<AmendmentValue> amendmentEventdateStandardized(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY; 
        // AMENDED if the value of dwc:eventDate was changed to unambiguously 
        // conform with an ISO 8601-1:2019 date; otherwise NOT_CHANGED 
        //

        return result;
    }

    /**
     * #66 Validation SingleRecord Conformance: eventdate notstandard
     *
     * Provides: VALIDATION_EVENTDATE_NOTSTANDARD
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("4f2bf8fd-fc5c-493f-a44c-e7b16153c803")
    public DQResponse<ComplianceValue> validationEventdateNotstandard(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY; 
        // COMPLIANT if the value of dwc:eventDate is a valid ISO 8601-1:2019 
        //date; otherwise NOT_COMPLIANT 

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

    /**
     * #84 Validation SingleRecord Conformance: year outofrange
     *
     * Provides: VALIDATION_YEAR_OUTOFRANGE
     *
     * @param year the provided dwc:year to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("ad0c8855-de69-4843-a80c-a5387d20fbc8")
    public DQResponse<ComplianceValue> validationYearOutofrange(@ActedUpon("dwc:year") String year) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:year is not present, 
        // or is EMPTY or cannot be interpreted as an integer; COMPLIANT 
        // if the value of dwc:year is within the Parameter range; 
        //otherwise NOT_COMPLIANT 

        //TODO: Parameters. This test is defined as parameterized.
        // bdq:earliestDate="1600"; bdq:latestDate=current year

        return result;
    }

    /**
     * #86 Amendment SingleRecord Completeness: eventdate from verbatim
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_VERBATIM
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param verbatimEventDate the provided dwc:verbatimEventDate to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("6d0a0c10-5e4a-4759-b448-88932f399812")
    public DQResponse<AmendmentValue> amendmentEventdateFromVerbatim(@ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:verbatimEventDate") String verbatimEventDate) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is not EMPTY 
        // or the value of dwc:verbatimEventDate is EMPTY or not unambiguously 
        // interpretable as an ISO 8601-1:2019 date; AMENDED if the 
        // value of dwc:eventDate was unambiguously interpreted from 
        //dwc:verbatimEventDate; otherwise NOT_CHANGED 

        return result;
    }

    /**
     * #88 Validation SingleRecord Completeness: event temporal empty
     *
     * Provides: VALIDATION_EVENT_TEMPORAL_EMPTY
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
    @Provides("41267642-60ff-4116-90eb-499fee2cd83f")
    public DQResponse<ComplianceValue> validationEventTemporalEmpty(@ActedUpon("dwc:startDayOfYear") String startDayOfYear, @ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:year") String year, @ActedUpon("dwc:verbatimEventDate") String verbatimEventDate, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day, @ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // COMPLIANT if at least one term needed to determine the event 
        //date exists and is not EMPTY; otherwise NOT_COMPLIANT 

        return result;
    }

    /**
     * #93 Amendment SingleRecord Completeness: eventdate from yearmonthday
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("3892f432-ddd0-4a0a-b713-f2e2ecbd879d")
    public DQResponse<AmendmentValue> amendmentEventdateFromYearmonthday(@ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:year") String year, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL _PREREQUISITES_NOT_MET if dwc:eventDate is not 
        // EMPTY or dwc:year is EMPTY or is uninterpretable as a valid 
        // year; AMENDED if the value of dwc:eventDate was interpreted 
        // from the values in dwc:year, dwc:month and dwc:day; otherwise 
        //NOT_CHANGED 

        return result;
    }

    /**
     * #125 Validation SingleRecord Conformance: day outofrange
     *
     * Provides: VALIDATION_DAY_OUTOFRANGE
     *
     * @param year the provided dwc:year to evaluate
     * @param month the provided dwc:month to evaluate
     * @param day the provided dwc:day to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("5618f083-d55a-4ac2-92b5-b9fb227b832f")
    public DQResponse<ComplianceValue> validationDayOutofrange(@ActedUpon("dwc:year") String year, @ActedUpon("dwc:month") String month, @ActedUpon("dwc:day") String day) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if (a) dwc:day is EMPTY (b) 
        // is not an integer, or (c) dwc:day is an integer between 
        // 29 and 31 inclusive and dwc:month is not an integer between 
        // 1 and 12, or (d) dwc:month is the integer 2 and dwc:day 
        // is the integer 29 and dwc:year is not a valid ISO 8601 year; 
        // COMPLIANT (a) if the value of the field dwc:day is an integer 
        // between 1 and 28 inclusive, or (b) dwc:day is an integer 
        // between 29 and 30 and dwc:month is an integer in the set 
        // (4,6,9,11), or (c) dwc:day is an integer between 29 and 
        // 31 and dwc:month is an integer in the set (1,3,5,7,8,10,12), 
        // or (d) dwc:day is the integer 29 and dwc:month is the integer 
        // 2 and dwc:year is a valid leap year (evenly divisible by 
        // 400 or (evenly divisible by 4 but not evenly divisible by 
        //100)); otherwise NOT_COMPLIANT. 

        return result;
    }

    /**
     * #126 Validation SingleRecord Conformance: month notstandard
     *
     * Provides: VALIDATION_MONTH_NOTSTANDARD
     *
     * @param month the provided dwc:month to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("01c6dafa-0886-4b7e-9881-2c3018c98bdc")
    public DQResponse<ComplianceValue> validationMonthNotstandard(@ActedUpon("dwc:month") String month) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:month is EMPTY; COMPLIANT 
        // if the value of dwc:month is an integer between 1 and 12 
        //inclusive; otherwise NOT_COMPLIANT 

        return result;
    }

    /**
     * #127 Amendment SingleRecord Conformance: day standardized
     *
     * Provides: AMENDMENT_DAY_STANDARDIZED
     *
     * @param day the provided dwc:day to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("b129fa4d-b25b-43f7-9645-5ed4d44b357b")
    public DQResponse<AmendmentValue> amendmentDayStandardized(@ActedUpon("dwc:day") String day) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; AMENDED 
        // if the value of dwc:day was unambiguously interpreted to 
        // be an integer between 1 and 31 inclusive; otherwise NOT_CHANGED 
        //

        return result;
    }

    /**
     * #128 Amendment SingleRecord Conformance: month standardized
     *
     * Provides: AMENDMENT_MONTH_STANDARDIZED
     *
     * @param month the provided dwc:month to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("2e371d57-1eb3-4fe3-8a61-dff43ced50cf")
    public DQResponse<AmendmentValue> amendmentMonthStandardized(@ActedUpon("dwc:month") String month) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:month is EMPTY; AMENDED 
        // if the value of dwc:month was able to be interpreted as 
        // a integer between 1 and 12 inclusive; otherwise NOT_CHANGED 
        //

        return result;
    }

    /**
     * #130 Validation SingleRecord Conformance: startdayofyear outofrange
     *
     * Provides: VALIDATION_STARTDAYOFYEAR_OUTOFRANGE
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("85803c7e-2a5a-42e1-b8d3-299a44cafc46")
    public DQResponse<ComplianceValue> validationStartdayofyearOutofrange(@ActedUpon("dwc:startDayOfYear") String startDayOfYear, @ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:startDayOfYear is 
        // EMPTY or if the value of dwc:startDayOfYear is equal to 
        // 366 and (dwc:eventDate is EMPTY or the value of dwc:eventDate 
        // can not be interpreted to find single year or a start year 
        // in a range); COMPLIANT if the value of dwc:startDayOfYear 
        // is an integer between 1 and 365, inclusive, or if the value 
        // of dwc:startDayOfYear is 366 and the start year interpreted 
        // from dwc:eventDate is a leap year; otherwise NOT_COMPLIANT 
        //

        return result;
    }

    /**
     * #131 Validation SingleRecord Conformance: enddayofyear outofrange
     *
     * Provides: VALIDATION_ENDDAYOFYEAR_OUTOFRANGE
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("9a39d88c-7eee-46df-b32a-c109f9f81fb8")
    public DQResponse<ComplianceValue> validationEnddayofyearOutofrange(@ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:endDayOfYear is EMPTY 
        // or if the value of dwc:endDayOfYear is equal to 366 and 
        // (dwc:eventDate is EMPTY or the value of dwc:eventDate can 
        // not be interpreted to find single year or an end year in 
        // a range); COMPLIANT if the value of dwc:endDayOfYear is 
        // an integer between 1 and 365 inclusive, or if the value 
        // of dwc:endDayOfYear is 366 and the end year interpreted 
        // from dwc:eventDate is a leap year; otherwise NOT_COMPLIANT 
        //

        return result;
    }

    /**
     * #132 Amendment SingleRecord Completeness: eventdate from yearstartdayofyearenddayofyear
     *
     * Provides: AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR
     *
     * @param startDayOfYear the provided dwc:startDayOfYear to evaluate
     * @param eventDate the provided dwc:eventDate to evaluate
     * @param year the provided dwc:year to evaluate
     * @param endDayOfYear the provided dwc:endDayOfYear to evaluate
     * @return DQResponse the response of type AmendmentValue to return
     */
    @Provides("eb0a44fa-241c-4d64-98df-ad4aa837307b")
    public DQResponse<AmendmentValue> amendmentEventdateFromYearstartdayofyearenddayofyear(@ActedUpon("dwc:startDayOfYear") String startDayOfYear, @ActedUpon("dwc:eventDate") String eventDate, @ActedUpon("dwc:year") String year, @ActedUpon("dwc:endDayOfYear") String endDayOfYear) {
        DQResponse<AmendmentValue> result = new DQResponse<AmendmentValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate was not 
        // EMPTY or dwc:year was EMPTY or both dwc:startDayOfYear and 
        // dwc:endDayOfYear were EMPTY or the values were not interpretable; 
        // AMENDED if the value of dwc:eventDate was FILLED_IN from 
        // the values in dwc:year, dwc:startDayOfYear and dwc:endDayOfYear; 
        //otherwise NOT_CHANGED 

        return result;
    }

    /**
     * #140 Measure SingleRecord Resolution: eventdate precisioninseconds
     *
     * Provides: MEASURE_EVENTDATE_PRECISIONINSECONDS
     *
     * @param eventDate the provided dwc:eventDate to evaluate
     * @return DQResponse the response of type NumericalValue  to return
     */
    @Provides("56b6c695-adf1-418e-95d2-da04cad7be53")
    public DQResponse<NumericalValue> measureEventdatePrecisioninseconds(@ActedUpon("dwc:eventDate") String eventDate) {
        DQResponse<NumericalValue> result = new DQResponse<NumericalValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:eventDate is EMPTY 
        // or does not contain a valid ISO 8601-1:2019 date; REPORT 
        // on the length of the period expressed in the dwc:eventDate 
        //in seconds; otherwise NOT_REPORTED 

        return result;
    }

    /**
     * #147 Validation SingleRecord Conformance: day notstandard
     *
     * Provides: VALIDATION_DAY_NOTSTANDARD
     *
     * @param day the provided dwc:day to evaluate
     * @return DQResponse the response of type ComplianceValue  to return
     */
    @Provides("47ff73ba-0028-4f79-9ce1-ee7008d66498")
    public DQResponse<ComplianceValue> validationDayNotstandard(@ActedUpon("dwc:day") String day) {
        DQResponse<ComplianceValue> result = new DQResponse<ComplianceValue>();

        //TODO:  Implement specification
        // INTERNAL_PREREQUISITES_NOT_MET if dwc:day is EMPTY; COMPLIANT 
        // if the value of the field dwc:day is an integer between 
        //1 and 31 inclusive; otherwise NOT_COMPLIANT. 

        return result;
    }

}
