# event_date_qc
Data Quality library for dwc:eventDate and other dwc:Event terms.

Tools for working with dates in forms found in biodiversity data.

DOI: 10.5281/zenodo.166329 

[![DOI](https://zenodo.org/badge/70093157.svg)](https://zenodo.org/badge/latestdoi/70093157)

This library provides two classes each with a set of static methods which provide functions to support data quality control of date data in the context of biodiversity.  DateUtils provides a set of primitive methods for working with event date data, and DwCEventQC provides a wrapper set of annotated methods for making assertions about event date data in terms of the Fittness for Use Framework.  

# Include using maven

Available in Maven Central.

    <dependency>
        <groupId>org.filteredpush</groupId>
        <artifactId>event_date_qc</artifactId>
        <version>2.0.0</version>
    </dependency>

# Building

    mvn package

Library jar will be produced in /target/event_date_qc-{version}.jar

An executable jar will be produced in /event_date_qc-{version}-{gitcommit}-executable.jar.  This jar is not installed in the local maven repository or deployed to maven central with maven install or maven deploy.


# Quick Start: Find verbatim dates that can't be parsed.

Checkout and run mvn package, then execute jar file, e.g. with: 

    java -jar event_date_qc-2.1.0-SNAPSHOT-97344b4-executable.jar -f src/test/resources/example_dates.csv

Dates from the corpus of example dates that aren't yet recognized will be printed.  To show only matched dates add -m as an option, to show both matched and un-matched dates, use -a as an option.

Example output from 

    java -jar event_date_qc-2.1.0-SNAPSHOT-97344b4-executable.jar -f src/test/resources/example_dates_for_readme.csv -a -e verbatimDates

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

This library provides the class org.filteredpush.qc.date.DwCEventDQ

DwCEventDQ provides a set of static methods for working with dwc:Event terms wrapping the static methods in DateUtils and returning objects that implement the API provided by ffdq-api for reporting data quality assertions in terms of the Fit4U Framework. 

As of 2023-06-09, the current set of dwc:Event class related tests in the core test suite under consideration by the TDWG BDQIG TG2 tests and assertions task group have implementations in DwCEventDQ, and other date/time related tests have implementations in DwCOtherDateDQ.  These methods are annotated with @Provides, @Validation/Amendment/Measure, and @ProvidesVersion annotations in the form:  

    @Provides("f51e15a6-a67d-4729-9c28-3766299d2985")
    @Validation(label="VALIDATION_EVENTDATE_NOTEMPTY", description="Is there a value in dwc:eventDate")
	@ProvidesVersion("https://rs.tdwg.org/bdq/terms/f51e15a6-a67d-4729-9c28-3766299d2985/2022-11-08")
    public static  DQResponse<ComplianceValue> validationEventdateNotEmpty(@ActedUpon(value = "dwc:eventDate") String eventDate) {
       ...
    } 

These tests are subject to change as the TDWG BDQIG TG2 specifications change.   The current test specifications can be found [in a csv file](https://github.com/tdwg/bdq/blob/master/tg2/core/TG2_tests.csv) with rationalle management in [issues in the tdwg/bdq issue tracker](https://github.com/tdwg/bdq/labels/TIME).  The TDWG BDQ TG2 is preparing a draft standard for biodiversity data quality including these tests.

These test implementations can be validated against the TDWG BDQ TG2 [test validation data] (https://github.com/tdwg/bdq/blob/master/tg2/core/TG2_test_validation_data.csv) using [bdqtestrunner](https://github.com/FilteredPush/bdqtestrunner).  As of 2023-06-10, 353 test cases run, 10 are pending the rename of a test getting carried into the validation data, and of the 353 test case run, 7 are failing, with the reasons under discussion in the task group.

    $ java -jar bdqtestrunner-0.0.1-SNAPSHOT-dc382e1-executable.jar -c DwCOtherDateDQ,DwCEventDQDefaults,DwCMetadataDQ -g 26,69,76,84,130,131,33,36,49,147,126,66,140,76,128,127,88,72,125,67,93,86,132,52,61 > output.log

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

# Developer deployment: 

To deploy a snapshot to the snapshotRepository: 

    mvn clean deploy

To deploy a new release to maven central, set the version in pom.xml to a non-snapshot version, then deploy with the release profile (which adds package signing and deployment to release staging:

    mvn clean deploy -P release

After this, you will need to login to the sonatype oss repository hosting nexus instance (https://oss.sonatype.org/index.html#welcome), find the staged release in the staging repositories, and perform the release.  It should be possible (haven't verified this yet) to perform the release from the command line instead by running: 

    mvn nexus-staging:release -P release

