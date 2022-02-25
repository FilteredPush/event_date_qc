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
	 * 
	 */
	public DateOrderException() {
		super("begin/end dates in wrong order");
	}

	/**
	 * @param message
	 */
	public DateOrderException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public DateOrderException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DateOrderException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public DateOrderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
