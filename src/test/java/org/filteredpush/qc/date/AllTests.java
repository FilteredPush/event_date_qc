package org.filteredpush.qc.date;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ 
	DateUtilsTest.class,
	DwcEventDQTest.class,
	DwCEventDQTestDefinitions.class,
	DwCOtherDateDQTest.class,
	DwCOtherDateDQTestDefinitions.class,
	LocalDateIntervalTest.class
	})
public class AllTests {

}
