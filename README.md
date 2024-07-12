# event_date_qc
Data Quality library for dwc:eventDate and other dwc:Event terms.

Tools for working with dates in forms found in biodiversity data.

DOI: 10.5281/zenodo.166329 

[![DOI](https://zenodo.org/badge/70093157.svg)](https://zenodo.org/badge/latestdoi/70093157)

This library provides two classes each with a set of static methods which provide functions to support data quality control of date data in the context of biodiversity.  DateUtils provides a set of primitive methods for working with event date data, and DwCEventQC provides a wrapper set of annotated methods for making assertions about event date data in terms of the Fittness for Use Framework.  

This library provides implementations for the Time related tests in the TDWG BDQ TG2 test suite.

Version 3.0.4 is up to date with the CORE and SUPPLEMENTAL Time related tests as of 2024-07-10.

# Include using maven

Available in Maven Central.

    <dependency>
        <groupId>org.filteredpush</groupId>
        <artifactId>event_date_qc</artifactId>
        <version>3.0.4</version>
    </dependency>

# Building

    mvn package

Library jar will be produced in /target/event_date_qc-{version}.jar

An executable jar will be produced in /event_date_qc-{version}-{gitcommit}-executable.jar.  This jar is not installed in the local maven repository or deployed to maven central with maven install or maven deploy.


# Quick Start for local: Find verbatim dates that can't be parsed.

Checkout and run mvn package, then execute jar file, e.g. with: 

    java -jar event_date_qc-3.0.2-cc343a8-executable.jar -f src/test/resources/example_dates.csv

Dates from the corpus of example dates that aren't yet recognized will be printed.  To show only matched dates add -m as an option, to show both matched and un-matched dates, use -a as an option.

Example output from 

    java -jar event_date_qc-3.0.2-cc343a8-executable.jar -f src/test/resources/example_dates_for_readme.csv -a -e verbatimDates

consists of tab separated lines listing the original value, a result code, and the interpreted date, with the last two lines summarizing the results:

    June 9 1906	DATE	1906-06-09
    VIII/13/1938	DATE	1938-08-13
    VIII/1892	RANGE	1892-08
    VIII/1893	RANGE	1893-08
    VIII/28 1895	DATE	1895-08-28
    VIII/30 1896	DATE	1896-08-30
    winter	NOT_RUN
    [18 June 1863]	DATE	1863-06-18
    [1800's]	RANGE	1800-01-01/1899-12-31
    September 9. 1913	DATE	1913-09-09
    September, 1862	RANGE	1862-09
    September, 1882	RANGE	1882-09
    11/8 1899	AMBIGUOUS	1899-08-11/1899-11-08
    11/8, 1883	AMBIGUOUS	1883-08-11/1883-11-08
    12-1-1915	AMBIGUOUS	1915-01-12/1915-12-01
    12-11-1937	AMBIGUOUS	1937-11-12/1937-12-11
    1-2 fevrier 1963	RANGE	1963-02-01/1963-02-02
    11 et 14 VII 1910	RANGE	1910-07-11/1910-07-14

This command line functionality is primarily intended for development use to assess and expand the coverage of different forms of verbatim dates.  The primary intent of this project is as a library.

## DateUtils - primitives

This library provides the class org.filteredpush.qc.date.DateUtils

DateUtils provides a set of static methods for working with dwc:Event terms, including the following (see DateUtilsTest for examples):

**measureDurationSeconds(String eventDate)** Measure the duration of an event date in seconds, when a time is specified, ceiling to the nearest second, when a time is not specified, from the date midnight at the beginning of a date range to the last second of the day at the end of the range.

**isConsistent(String eventDate, String year, String month, String day)** Identify whether an event date and a year, month, and day are consistent, where consistent means that either the eventDate is a single day and the year-month-day represent the same day or that eventDate is a date range that defines the same date range as year-month (where day is null) or the same date range as year (where day and month are null).

**isConsistent(String eventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day)** Identify whether an event date is consistent with its atomic parts.

**isRange(String eventDate)** Test to see if a string appears to represent a date range of more than one day.

**createEventDateFromParts(String verbatimEventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day** Attempt to construct an ISO formatted date as a string built from atomic parts of the date.  

**EventResult extractDateToDayFromVerbatimER(String verbatimEventDate, int yearsBeforeSuspect)**  Attempt to extract a date or date range in standard format from a provided verbatim date string.  Returns an EventResult object with properies resultState with values including date, range, suspect, or ambiguous and result, which contains a string value in a format suitable for dwc:eventDate.  

Verbatim event dates in a variety of forms typical of natural science collections data can be interpreted into standard dwc:eventDate values, including dates with times, single dates, dates with roman numeral months, dates with months spelled out or abbreviated in several languages,  and date ranges.  Multiple orders of year month and day are supported, including day/month/year, month/day/year, and year/month/day, with identification of ambiguity and translation of ambiguous forms into ranges.  About 83% of all distinct verbatim date values (and 96% of distinct verbatim date values with 4 digit years) found in Museum of Comparative Zoology and Harvard University Herbaria data are interpreted.  All but three forms of verbatim dates with 4 digit years found by VertNet are interpreted.  Dates with two digit years are not yet handled.  Examples of verbatim date formats that can be interpreted include: 

* May 9th 1880 -> 1880-05-09
* Jul. 9th 1880 -> 1880-07-09
* 11-VII-1885 -> 1885-07-11
* 31iii2005 -> 2005-03-31
* janv. 17 1883 -> 1883-01-17
* 2010年10月18日 -> 2010-10-18
* April 1871 -> 1871-04
* July 16-26, 1945 -> 1945-07-16/1945-07-26
* Jan-Feb/1964 -> 1964-01-01/1964-02-29
* Sept.-Oct. 1982 -> 1982-09/1982-10
* 29 vi-13 vii-2001 -> 2001-06-29/2001-07-13
* 5.4.1927 -> 1927-04-05/1927-05-04 (ambiguous)

## DwCEventQC - implementations of standard tests in the fittnes for use framework

This library provides the classes org.filteredpush.qc.date.DwCEventDQ, DwCEventDQDefaults, and DwCOtherDateDQ.

DwCEventDQ provides a set of static methods for working with dwc:Event terms wrapping the static methods in DateUtils and returning objects that implement the API provided by ffdq-api for reporting data quality assertions in terms of the Fit4U Framework. 

As of 2023-07-04, the current set of dwc:Event class related test specifications in the core test suite by the TDWG BDQIG TG2 tests and assertions task group have implementations in DwCEventDQ, and other date/time related tests have implementations in DwCOtherDateDQ.  Methods for tests that can be parameterized can be invoked from methods in DwCEventDQDefaults that do not take a parameter, but use the default value for any parameter as provided for in the test specifications.   These methods are annotated with @Provides, @Validation/Amendment/Measure, @ProvidesVersion, and @Specification annotations in the form:   

    @Provides("f51e15a6-a67d-4729-9c28-3766299d2985")
    @Validation(label="VALIDATION_EVENTDATE_NOTEMPTY", description="Is there a value in dwc:eventDate")
	@ProvidesVersion("https://rs.tdwg.org/bdq/terms/f51e15a6-a67d-4729-9c28-3766299d2985/2022-11-08")
	@Specification("COMPLIANT if dwc:eventDate is not EMPTY; otherwise NOT_COMPLIANT ")
    public static  DQResponse<ComplianceValue> validationEventdateNotEmpty(@ActedUpon(value = "dwc:eventDate") String eventDate) {
       ...
    } 

These tests are subject to change if there are further changes to the TDWG BDQ TG2 specifications.   The current test specifications can be found [in a csv file](https://github.com/tdwg/bdq/blob/master/tg2/core/TG2_tests.csv) with rationalle management in [issues in the tdwg/bdq issue tracker](https://github.com/tdwg/bdq/labels/TIME).  The TDWG BDQ TG2 is preparing a draft standard for biodiversity data quality including these tests.  As of 2023-07-04, specifications for the the tests implemented here are expected to be stable.  

These test implementations can be validated against the TDWG BDQ TG2 [test validation data] (https://github.com/tdwg/bdq/blob/master/tg2/core/TG2_test_validation_data.csv) using [bdqtestrunner](https://github.com/FilteredPush/bdqtestrunner).  As of 2023-07-04, 358 test cases run, all pass.


The unit test below shows an example of a call on DwCEventDQ.measureEventdateDurationinseconds to perform a measurement of the duration of a dwc:eventDate in seconds, and invocations of getResultState(), getValue(), and getComment() on the returned implementation of the DQMeasurementResult interface.

	@Test
	public void testMeasureDuration() { 
		DQResponse<NumericalValue> measure = DwCEventDQ.measureEventdateDurationinseconds("1880-05-08");
		Long seconds = (60l*60l*24l); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		assertNotNull(measure.getComment());

		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1880-05");
		seconds = (60l*60l*24l*31); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1881");
		seconds = (60l*60l*24l*365); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1880"); // leap year
		seconds = (60l*60l*24l*366); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());
		
		measure = DwCEventDQ.measureEventdateDurationinseconds("1880-02");  // Feb in leap year
		seconds = (60l*60l*24l*29); 
		assertEquals(seconds, measure.getObject());
		assertEquals(ResultState.RUN_HAS_RESULT, measure.getResultState());		
		
		// ...
	}
    
The APIs for both the java annotations for the framework and the result objects for the framework are not expected to change.    

These implementations of the TDWG BDQ TG2 test specifications are passing against all related validation data with the 2023-12-15 [test validation data](https://github.com/tdwg/bdq/blob/8b7078bf748112cbd0e9c76c415d2b0085caf5f7/tg2/core/TG2_test_validation_data.csv) including [non-printing characters](https://github.com/tdwg/bdq/blob/8b7078bf748112cbd0e9c76c415d2b0085caf5f7/tg2/core/TG2_test_validation_data_nonprintingchars.csv)). 

    $ java -jar bdqtestrunner-0.0.1-SNAPSHOT-35f8cf2-executable.jar -c DwCOtherDateDQDefaults,DwCEventDQDefaults

    Validation Test Data From: https://raw.githubusercontent.com/tdwg/bdq/master/tg2/core/TG2_test_validation_data.csv
    2024-01-11T14:42:53.613743137
    Validating Test Implementations In:
    org.filteredpush.qc.date.DwCEventDQDefaults
    org.filteredpush.qc.date.DwCOtherDateDQDefaults
    Ran 23 tests against the validation data.
    718dfc3c-cb52-4fca-b8e2-0e722f375da7  9 P: 9 F: 0 AMENDMENT_EVENTDATE_STANDARDIZED #61
    dc8aae4b-134f-4d75-8a71-c4186239178e 25 P:25 F: 0 VALIDATION_DATEIDENTIFIED_INRANGE #76
    710fe118-17e1-440f-b428-88ba3f547d6d 10 P:10 F: 0 AMENDMENT_EVENT_FROM_EVENTDATE #52
    c09ecbf9-34e3-4f3e-b74a-8796af15e59f 12 P:12 F: 0 VALIDATION_YEAR_NOTEMPTY #49
    2e371d57-1eb3-4fe3-8a61-dff43ced50cf  7 P: 7 F: 0 AMENDMENT_MONTH_STANDARDIZED #128
    9a39d88c-7eee-46df-b32a-c109f9f81fb8 10 P:10 F: 0 VALIDATION_ENDDAYOFYEAR_INRANGE #131
    5618f083-d55a-4ac2-92b5-b9fb227b832f 33 P:33 F: 0 VALIDATION_EVENTDATE_CONSISTENT #67
    56b6c695-adf1-418e-95d2-da04cad7be53 10 P:10 F: 0 MEASURE_EVENTDATE_PRECISIONINSECONDS #140
    f51e15a6-a67d-4729-9c28-3766299d2985 26 P:26 F: 0 VALIDATION_EVENTDATE_NOTEMPTY #33
    3892f432-ddd0-4a0a-b713-f2e2ecbd879d 12 P:12 F: 0 AMENDMENT_EVENTDATE_FROM_YEARMONTHDAY #93
    47ff73ba-0028-4f79-9ce1-ee7008d66498 11 P:11 F: 0 VALIDATION_DAY_STANDARD #147
    b129fa4d-b25b-43f7-9645-5ed4d44b357b  8 P: 8 F: 0 AMENDMENT_DAY_STANDARDIZED #127
    ad0c8855-de69-4843-a80c-a5387d20fbc8 13 P:13 F: 0 VALIDATION_YEAR_INRANGE #84
    3cff4dc4-72e9-4abe-9bf3-8a30f1618432 24 P:24 F: 0 VALIDATION_EVENTDATE_INRANGE #36
    eb0a44fa-241c-4d64-98df-ad4aa837307b  9 P: 9 F: 0 AMENDMENT_EVENTDATE_FROM_YEARSTARTDAYOFYEARENDDAYOFYEAR #132
    66269bdd-9271-4e76-b25c-7ab81eebe1d8 17 P:17 F: 0 VALIDATION_DATEIDENTIFIED_STANDARD #69
    6d0a0c10-5e4a-4759-b448-88932f399812 22 P:22 F: 0 AMENDMENT_EVENTDATE_FROM_VERBATIM #86
    41267642-60ff-4116-90eb-499fee2cd83f 30 P:30 F: 0 VALIDATION_EVENT_TEMPORAL_NOTEMPTY #88
    39bb2280-1215-447b-9221-fd13bc990641 10 P:10 F: 0 AMENDMENT_DATEIDENTIFIED_STANDARDIZED  #26
    01c6dafa-0886-4b7e-9881-2c3018c98bdc 10 P:10 F: 0 VALIDATION_MONTH_STANDARD #126
    4f2bf8fd-fc5c-493f-a44c-e7b16153c803 18 P:18 F: 0 VALIDATION_EVENTDATE_STANDARD #66
    8d787cb5-73e2-4c39-9cd1-67c7361dc02e 23 P:23 F: 0 VALIDATION_DAY_INRANGE #125
    85803c7e-2a5a-42e1-b8d3-299a44cafc46 11 P:11 F: 0 VALIDATION_STARTDAYOFYEAR_INRANGE #130
    Test cases: 360
    Total cases with no implementation: 832
    Total dataID validation rows: 1192

# Developer deployment: 

To deploy a snapshot to the snapshotRepository: 

    mvn clean deploy

To deploy a new release to maven central, set the version in pom.xml to a non-snapshot version, update the @Mechanism metadata (in the classes, generation configuration, and rdf), then deploy with the release profile (which adds package signing and deployment to release staging), then confirm release.

1. Set version in pom.xml to a non-snapshot version

2. Update @Mechanism metadata in: 

	generation/ident_time_tests.ttl
	generation/time_tests.ttl
	generation/event_date_qc_DwCEventDQ_stubs_kurator_ffdq.config
	generation/event_date_qc_DwCOtherDQ_stubs_kurator_ffdq.config
	generation/event_date_qc_DwCEventDQ_kurator_ffdq.config
	generation/event_date_qc_DwCOtherDQ_kurator_ffdq.config
	src/main/java/org/filteredpush/qc/date/DwCOtherDateDQDefaults.java
	src/main/java/org/filteredpush/qc/date/DwCOtherDateDQ.java
	src/main/java/org/filteredpush/qc/date/DwCEventDQDefaults.java
	src/main/java/org/filteredpush/qc/date/DwCEventDQ.java

3. Deploy using the release profile.

    mvn clean deploy -P release

4. Confirm release by loging to the sonatype oss repository hosting nexus instance (https://oss.sonatype.org/index.html#welcome), and searching for the artifact, or checking the staging repositories if it is held up in staging.

