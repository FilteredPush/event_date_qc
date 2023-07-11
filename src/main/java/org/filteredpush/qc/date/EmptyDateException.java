/**
 * EmptyDateException.java
 */
package org.filteredpush.qc.date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mole
 *
 */
public class EmptyDateException extends Exception {

	private static final long serialVersionUID = 6416935624079111027L;
	private static final Log logger = LogFactory.getLog(EmptyDateException.class);
	
	/**
	 * Default constructor specifying message of empty date.
	 */
	public EmptyDateException() {
		super("Empty Date");
	}
	/**
	 * @param message describing the exception.
	 * @param cause of the exception (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 * @param enableSuppression true if suppression is enabled
	 * @param writableStackTrace  true the stack trace should be writable
	 */
	public EmptyDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	/**
	 * @param message describing the exception.
	 * @param cause of the exception (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public EmptyDateException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param message describing the exception
	 */
	public EmptyDateException(String message) {
		super(message);
	}
	/**
	 * @param cause of the exception (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
	 */
	public EmptyDateException(Throwable cause) {
		super(cause);
	}
	
	
}
