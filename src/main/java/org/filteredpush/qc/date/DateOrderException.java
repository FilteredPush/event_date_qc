/**
 * DateOrderException.java
 */
package org.filteredpush.qc.date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class DateOrderException extends Exception {

	private static final long serialVersionUID = 1814986175371605298L;

	private static final Log logger = LogFactory.getLog(DateOrderException.class);

	/**
	 *  Exception for describing that begin date comes after end date.
	 */
	public DateOrderException() {
		super("begin/end dates in wrong order");
	}

	/**
	 * @param message the message for the exception
	 */
	public DateOrderException(String message) {
		super(message);
	}

	/**
	 * @param cause the cause of the exception (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public DateOrderException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message the message for the exception
	 * @param cause the cause of the exception (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public DateOrderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message  the message for the exception
	 * @param cause he cause of the exception, (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 * @param enableSuppression whether or not suppression is enabled or disabled
	 * @param writableStackTrace whether or not the stack trace should be writable
	 */
	public DateOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
