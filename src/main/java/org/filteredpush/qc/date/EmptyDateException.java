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
	 * 
	 */
	public EmptyDateException() {
		super("Empty Date");
	}
	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public EmptyDateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
	/**
	 * @param message
	 * @param cause
	 */
	public EmptyDateException(String message, Throwable cause) {
		super(message, cause);
	}
	/**
	 * @param message
	 */
	public EmptyDateException(String message) {
		super(message);
	}
	/**
	 * @param cause
	 */
	public EmptyDateException(Throwable cause) {
		super(cause);
	}
	
	
}
