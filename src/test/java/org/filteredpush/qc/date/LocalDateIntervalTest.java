/**
 * LocalDateIntervalTest.java
 */
package org.filteredpush.qc.date;

import static org.junit.Assert.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

/**
 * @author mole
 *
 */
public class LocalDateIntervalTest {

	private static final Log logger = LogFactory.getLog(LocalDateIntervalTest.class);

	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#LocalDateInterval(java.time.LocalDate, java.time.LocalDate)}.
	 */
	@Test
	public void testLocalDateIntervalLocalDateLocalDate() {
		LocalDate date1 = LocalDate.of(1950, 2, 4);
		LocalDate date2 = LocalDate.of(1950, 2, 6);
		LocalDateInterval instance;
		try {
			instance = new LocalDateInterval(date1, date2);
			assertEquals(date1.getYear(), instance.getStartDate().getYear());
			assertEquals(date1.getMonthValue(), instance.getStartDate().getMonthValue());
			assertEquals(date1.getDayOfYear(), instance.getStartDate().getDayOfYear());
			assertEquals(date2.getYear(), instance.getEndDate().getYear());
			assertEquals(date2.getMonthValue(), instance.getEndDate().getMonthValue());
			assertEquals(date2.getDayOfYear(), instance.getEndDate().getDayOfYear());
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
		try {
			instance = new LocalDateInterval(date2, date1);
			fail("DateOrderException should have been thrown");
		} catch (EmptyDateException | DateOrderException e) {
			assertEquals(e.getClass(), DateOrderException.class);
		}
		try {
			instance = new LocalDateInterval(date1, date1);
			assertEquals(date1.getYear(), instance.getStartDate().getYear());
			assertEquals(date1.getMonthValue(), instance.getStartDate().getMonthValue());
			assertEquals(date1.getDayOfYear(), instance.getStartDate().getDayOfYear());
			assertEquals(date1.getYear(), instance.getEndDate().getYear());
			assertEquals(date1.getMonthValue(), instance.getEndDate().getMonthValue());
			assertEquals(date1.getDayOfYear(), instance.getEndDate().getDayOfYear());
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
		try {
			instance = new LocalDateInterval(date1, null);
			fail("EmptyDateException should have been thrown");
		} catch (EmptyDateException | DateOrderException e) {
			assertEquals(e.getClass(), EmptyDateException.class);
		}
		try {
			instance = new LocalDateInterval(null, date2);
			fail("EmptyDateException should have been thrown");
		} catch (EmptyDateException | DateOrderException e) {
			assertEquals(e.getClass(), EmptyDateException.class);
		}
		try {
			instance = new LocalDateInterval(null, null);
			fail("EmptyDateException should have been thrown");
		} catch (EmptyDateException | DateOrderException e) {
			assertEquals(e.getClass(), EmptyDateException.class);
		}			
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#LocalDateInterval(java.time.LocalDate)}.
	 */
	@Test
	public void testLocalDateIntervalLocalDate() {
		LocalDate date1 = LocalDate.of(1950, 2, 4);
		LocalDateInterval instance;
		try {
			instance = new LocalDateInterval(date1);
			assertEquals(date1.getYear(), instance.getStartDate().getYear());
			assertEquals(date1.getMonthValue(), instance.getStartDate().getMonthValue());
			assertEquals(date1.getDayOfYear(), instance.getStartDate().getDayOfYear());
			assertEquals(date1.getYear(), instance.getEndDate().getYear());
			assertEquals(date1.getMonthValue(), instance.getEndDate().getMonthValue());
			assertEquals(date1.getDayOfYear(), instance.getEndDate().getDayOfYear());
		} catch (EmptyDateException e) {
			fail(e.getMessage());
		}
		try {
			instance = new LocalDateInterval(((LocalDate)null));
			fail("EmptyDateException should have been thrown");
		} catch (EmptyDateException e) {
			assertEquals(e.getClass(), EmptyDateException.class);
		}		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#LocalDateInterval(java.lang.String)}.
	 */
	@Test
	public void testLocalDateIntervalString() {
		String date = "1950-02-04";
		LocalDateInterval instance;
		try {
			instance = new LocalDateInterval(date);
			assertEquals(1950, instance.getStartDate().getYear());
			assertEquals(2, instance.getStartDate().getMonthValue());
			assertEquals(4, instance.getStartDate().getDayOfMonth());
			assertEquals(1950, instance.getEndDate().getYear());
			assertEquals(2, instance.getEndDate().getMonthValue());
			assertEquals(4, instance.getEndDate().getDayOfMonth());
		} catch (EmptyDateException e) {
			fail(e.getMessage());
		}
		date = "1949-12-31/1951-05-06";
		try {
			instance = new LocalDateInterval(date);
			assertEquals(1949, instance.getStartDate().getYear());
			assertEquals(12, instance.getStartDate().getMonthValue());
			assertEquals(31, instance.getStartDate().getDayOfMonth());
			assertEquals(1951, instance.getEndDate().getYear());
			assertEquals(5, instance.getEndDate().getMonthValue());
			assertEquals(6, instance.getEndDate().getDayOfMonth());
		} catch (EmptyDateException e) {
			fail(e.getMessage());
		}		
		try {
			instance = new LocalDateInterval(((String)null));
			fail("EmptyDateException should have been thrown");
		} catch (EmptyDateException e) {
			assertEquals(e.getClass(), EmptyDateException.class);
		}	
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#parseDateBit(java.lang.String)}.
	 */
	@Test
	public void testParseDateBit() {
		try {
			LocalDateInterval instance = new LocalDateInterval("1900-01-01");
			assertEquals("1900-01-01", instance.toString());
			instance = new LocalDateInterval("1900-01");
			assertEquals("1900-01", instance.toString());
			instance = new LocalDateInterval("1900");
			assertEquals("1900", instance.toString());
			instance = new LocalDateInterval("1900-003");
			assertEquals("1900-01-03", instance.toString());
		} catch (DateTimeParseException | EmptyDateException e) {
			fail(e.getMessage());
		}
		try {
			@SuppressWarnings("unused")
			LocalDateInterval instance = new LocalDateInterval("");
			fail("should have thrown exception");
		} catch (DateTimeParseException | EmptyDateException e) {
			assertEquals(EmptyDateException.class,e.getClass());
		}
		try {
			@SuppressWarnings("unused")
			LocalDateInterval instance = new LocalDateInterval(((String)null));
			fail("should have thrown exception");
		} catch (DateTimeParseException | EmptyDateException e) {
			assertEquals(EmptyDateException.class,e.getClass());
		}
		try {
			@SuppressWarnings("unused")
			LocalDateInterval instance = new LocalDateInterval("foo");
			fail("should have thrown exception");
		} catch (DateTimeParseException | EmptyDateException e) {
			assertEquals(DateTimeParseException.class,e.getClass());
		}
		try {
			@SuppressWarnings("unused")
			LocalDateInterval instance = new LocalDateInterval("1942-12-34");
			fail("should have thrown exception");
		} catch (DateTimeParseException | EmptyDateException e) {
			assertEquals(DateTimeParseException.class,e.getClass());
		}
		try {
			@SuppressWarnings("unused")
			LocalDateInterval instance = new LocalDateInterval("1942-15-01");
			fail("should have thrown exception");
		} catch (DateTimeParseException | EmptyDateException e) {
			assertEquals(DateTimeParseException.class,e.getClass());
		}	
		try {
			@SuppressWarnings("unused")
			LocalDateInterval instance = new LocalDateInterval("1942-5-1");
			fail("should have thrown exception");
		} catch (DateTimeParseException | EmptyDateException e) {
			assertEquals(DateTimeParseException.class,e.getClass());
		}	
		try {
			@SuppressWarnings("unused")
			LocalDateInterval instance = new LocalDateInterval("1942-5");
			fail("should have thrown exception");
		} catch (DateTimeParseException | EmptyDateException e) {
			assertEquals(DateTimeParseException.class,e.getClass());
		}
		try {
			@SuppressWarnings("unused")
			LocalDateInterval instance = new LocalDateInterval("/1900-01-05");
			fail("should have thrown exception");
		} catch (DateTimeParseException | EmptyDateException e) {
			assertEquals(EmptyDateException.class,e.getClass());
		}
		try {
			@SuppressWarnings("unused")
			LocalDateInterval instance = new LocalDateInterval("/1900-01-05");
			fail("should have thrown exception");
		} catch (DateTimeParseException | EmptyDateException e) {
			assertEquals(EmptyDateException.class,e.getClass());
		}		
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#getStartDate()}.
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#getStart()}.
	 */
	@Test
	public void testGetStartDate() {
		LocalDate date1 = LocalDate.of(1950, 2, 4);
		LocalDate date2 = LocalDate.of(1950, 2, 6);
		LocalDateInterval instance;
		try {
			instance = new LocalDateInterval(date1, date2);
			assertEquals(date1.getYear(), instance.getStartDate().getYear());
			assertEquals(date1.getMonthValue(), instance.getStartDate().getMonthValue());
			assertEquals(date1.getDayOfYear(), instance.getStartDate().getDayOfYear());
			assertEquals(date1.getYear(), instance.getStart().getYear());
			assertEquals(date1.getMonthValue(), instance.getStart().getMonthValue());
			assertEquals(date1.getDayOfYear(), instance.getStart().getDayOfYear());
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
	}


	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#getEndDate()}.
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#getEnd()}.
	 */
	@Test
	public void testGetEndDate() {
		LocalDate date1 = LocalDate.of(1950, 2, 4);
		LocalDate date2 = LocalDate.of(1950, 2, 6);
		LocalDateInterval instance;
		try {
			instance = new LocalDateInterval(date1, date2);
			assertEquals(date2.getYear(), instance.getEnd().getYear());
			assertEquals(date2.getMonthValue(), instance.getEnd().getMonthValue());
			assertEquals(date2.getDayOfYear(), instance.getEnd().getDayOfYear());
			assertEquals(date2.getYear(), instance.getEndDate().getYear());
			assertEquals(date2.getMonthValue(), instance.getEndDate().getMonthValue());
			assertEquals(date2.getDayOfYear(), instance.getEndDate().getDayOfYear());
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
		try {
			instance = new LocalDateInterval(date1, date1);
			assertEquals(date1.getYear(), instance.getEnd().getYear());
			assertEquals(date1.getMonthValue(), instance.getEnd().getMonthValue());
			assertEquals(date1.getDayOfYear(), instance.getEnd().getDayOfYear());
			assertEquals(date1.getYear(), instance.getEndDate().getYear());
			assertEquals(date1.getMonthValue(), instance.getEndDate().getMonthValue());
			assertEquals(date1.getDayOfYear(), instance.getEndDate().getDayOfYear());
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#isSingleDay()}.
	 */
	@Test
	public void testIsSingleDay() {
		LocalDate date1 = LocalDate.of(1950, 2, 4);
		LocalDate date2 = LocalDate.of(1950, 2, 6);
		LocalDateInterval instance;
		try {
			instance = new LocalDateInterval(date1, date2);
			assertFalse(instance.isSingleDay());
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
		try {
			instance = new LocalDateInterval(date1, date1);
			assertTrue(instance.isSingleDay());
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#toString()}.
	 */
	@Test
	public void testToString() {
		LocalDate date1 = LocalDate.of(1950, 2, 4);
		LocalDate date2 = LocalDate.of(1950, 2, 6);
		LocalDateInterval instance;
		try {
			instance = new LocalDateInterval(date1, date2);
			assertEquals("1950-02-04/1950-02-06", instance.toString());
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
		try {
			instance = new LocalDateInterval(date1);
			assertEquals("1950-02-04", instance.toString());
		} catch (EmptyDateException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#equals(java.lang.Object)}.
	 */
	@Test
	public void testEqualsObject() {

		LocalDate date1 = LocalDate.of(1950, 2, 4);
		LocalDate date2 = LocalDate.of(1950, 2, 6);
		LocalDate date3 = LocalDate.of(1850, 12, 2);
		try {
			LocalDateInterval x = new LocalDateInterval(date1, date2);
			LocalDateInterval y = new LocalDateInterval(date1);
			LocalDateInterval z = new LocalDateInterval(date1);
			LocalDateInterval zz = new LocalDateInterval(date1);
			LocalDateInterval a = new LocalDateInterval(date3);
			assertFalse(x.equals(y));
			assertFalse(x.equals(z));
			assertFalse(x.equals(zz));
			assertFalse(x.equals(a));
			// reflexive
			assertTrue(x.equals(x));
			// symmetric
			assertTrue(y.equals(z));
			assertTrue(z.equals(y));
			// transitive
			assertTrue(y.equals(z));
			assertTrue(z.equals(zz));
			assertTrue(zz.equals(y));
			// consistent
			for (int i=1;i<=10;i++) { 
				assertFalse(x.equals(y));
				assertTrue(x.equals(x));
				assertTrue(z.equals(y));
			} 
			// nulls false
			assertFalse(x.equals(null));
			assertFalse(y.equals(null));
			assertFalse(z.equals(null));
			assertFalse(zz.equals(null));
			assertFalse(a.equals(null));
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#contains(org.filteredpush.qc.date.LocalDateInterval)}.
	 */
	@Test
	public void testContains() {
		try {
			LocalDateInterval intervalday = new LocalDateInterval("1882-03-04");
			LocalDateInterval interval = new LocalDateInterval("1882-03-01/1882-03-06");
			LocalDateInterval overlappinginterval = new LocalDateInterval("1882-03-04/1882-03-10");
			LocalDateInterval laterinterval = new LocalDateInterval("1982-03-01/1982-03-05");
			
			assertTrue(interval.contains(intervalday));
			assertFalse(intervalday.contains(interval));
			assertFalse(intervalday.contains(laterinterval));
			assertFalse(laterinterval.contains(interval));
			assertTrue(overlappinginterval.contains(intervalday));
			assertFalse(overlappinginterval.contains(interval));
			assertFalse(interval.contains(overlappinginterval));
			assertFalse(overlappinginterval.contains(laterinterval));
			assertFalse(intervalday.contains(overlappinginterval));
			
			assertFalse(intervalday.contains(null));
			
		} catch (DateTimeParseException | EmptyDateException e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#overlaps(org.filteredpush.qc.date.LocalDateInterval)}.
	 */
	@Test
	public void testOverlaps() {
		
		try {
			LocalDateInterval intervalday = new LocalDateInterval("1882-03-04");
			LocalDateInterval interval = new LocalDateInterval("1882-03-01/1882-03-06");
			LocalDateInterval overlappinginterval = new LocalDateInterval("1882-03-04/1882-03-10");
			LocalDateInterval laterinterval = new LocalDateInterval("1982-03-01/1982-03-05");
			LocalDateInterval laterintervalsamestart = new LocalDateInterval("1982-03-01/1982-03-06");
			LocalDateInterval laterintervalsameend = new LocalDateInterval("1982-02-01/1982-03-05");
			
			assertTrue(interval.overlaps(intervalday));
			assertTrue(intervalday.overlaps(interval));
			assertFalse(intervalday.overlaps(laterinterval));
			assertFalse(laterinterval.overlaps(interval));
			assertTrue(laterinterval.overlaps(laterintervalsamestart));
			assertTrue(laterintervalsamestart.overlaps(laterinterval));
			assertTrue(laterinterval.overlaps(laterintervalsameend));
			assertTrue(laterintervalsameend.overlaps(laterinterval));
			assertTrue(intervalday.overlaps(overlappinginterval));
			assertTrue(overlappinginterval.overlaps(intervalday));
			assertTrue(overlappinginterval.overlaps(interval));
			assertTrue(interval.overlaps(overlappinginterval));
			assertFalse(overlappinginterval.overlaps(laterinterval));
			assertTrue(intervalday.overlaps(overlappinginterval));
			
			assertFalse(intervalday.overlaps(null));
			
		} catch (DateTimeParseException | EmptyDateException e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link org.filteredpush.qc.date.LocalDateInterval#toDuration()}.
	 */
	@Test
	public void testToDuration() {
		LocalDate date1 = LocalDate.of(1950, 2, 4);
		LocalDate date2 = LocalDate.of(1950, 2, 6);
		LocalDateInterval instance;
		try {
			instance = new LocalDateInterval(date1, date2);
			Duration duration = instance.toDuration();
			assertEquals(86400*3, duration.getSeconds());
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
		try {
			instance = new LocalDateInterval(date1, date1);
			Duration duration = instance.toDuration();
			assertEquals(86400, duration.getSeconds());
		} catch (EmptyDateException | DateOrderException e) {
			fail(e.getMessage());
		}
	}

}
