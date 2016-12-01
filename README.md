# event_date_qc
Data Quality library for dwc:eventDate and other dwc:Event terms.

Tools for working with dates in forms found in biodiversity data.

DOI: 10.5281/zenodo.166329 

[![DOI](https://zenodo.org/badge/70093157.svg)](https://zenodo.org/badge/latestdoi/70093157)

This library provides the class org.filteredpush.qc.date.DateUtils

DateUtils provides a set of static methods for working with dwc:Event terms, including the following (see DateUtilsTest for examples):

**measureDurationSeconds(String eventDate)** Measure the duration of an event date in seconds, when a time is specified, ceiling to the nearest second, when a time is not specified, from the date midnight at the beginning of a date range to the last second of the day at the end of the range.

**isConsistent(String eventDate, String year, String month, String day)** Identify whether an event date and a year, month, and day are consistent, where consistent means that either the eventDate is a single day and the year-month-day represent the same day or that eventDate is a date range that defines the same date range as year-month (where day is null) or the same date range as year (where day and month are null).

**isConsistent(String eventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day)** Identify whether an event date is consistent with its atomic parts.

**isRange(String eventDate)** Test to see if a string appears to represent a date range of more than one day.

**createEventDateFromParts(String verbatimEventDate, String startDayOfYear, String endDayOfYear, String year, String month, String day** Attempt to construct an ISO formatted date as a string built from atomic parts of the date.

**extractDateFromVerbatim(String verbatimEventDate)** Attempt to extract a date or date range in standard format from a provided verbatim date string.  Returns a 'Map<String,String>' with keys resultState and result where resultState may be date, range, suspect, or ambiguous and result contains a string value in a format suitable for dwc:eventDate.  

Verbatim event dates in a variety of forms typical of natural science collections data can be interpreted into standard dwc:eventDate values, including dates with times, single dates, dates with roman numeral months, dates with months spelled out or abbreviated in several languages,  and date ranges.  Multiple orders of year month and day are supported, including day/month/year, month/day/year, and year/month/day, with identification of ambiguity and translation of ambiguous forms into ranges.  About 80-90% of verbatim date values found in Museum of Comparative Zoology and Harvard University Herbaria data are interpreted.  Dates with two digit years are not yet handled.  Examples of verbatim date formats that can be interpreted include: 

* May 9th 1880 -> 1880-05-09
* Jul. 9th 1880 -> 1880-07-09
* 11-VII-1885 -> 1885-07-11
* 31iii2005 -> 2005-03-31
* janv. 17 1883 -> 1883-01-17
* 2010年10月18日 -> 2010-10-18
* April 1871 -> 1871-04
* July 16-26, 1945 -> 1945-07-16/1945-07-26
* Jan-Feb/1964 -> 1964-01-01/1964-02-29
* 5.4.1927 -> 1927-04-05/1927-05-04 (ambiguous)

# Include using maven

Available in Maven Central.

    <dependency>
        <groupId>org.filteredpush</groupId>
        <artifactId>event_date_qc</artifactId>
        <version>1.0.1</version>
    </dependency>

# Building

    mvn package

# Developer deployment: 

To deploy a snapshot to the snapshotRepository: 

    mvn clean deploy

To deploy a new release to maven central, set the version in pom.xml to a non-snapshot version, then deploy with the release profile (which adds package signing and deployment to release staging:

    mvn clean deploy -P release

After this, you will need to login to the sonatype oss repository hosting nexus instance, find the staged release in the staging repositories, and perform the release.  It should be possible (haven't verified this yet) to perform the release from the command line instead by running: 

    mvn nexus-staging:release -P release

