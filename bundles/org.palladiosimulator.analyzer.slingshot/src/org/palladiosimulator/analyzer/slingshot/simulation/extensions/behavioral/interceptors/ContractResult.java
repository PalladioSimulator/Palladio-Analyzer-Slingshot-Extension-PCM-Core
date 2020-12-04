package org.palladiosimulator.analyzer.slingshot.simulation.extensions.behavioral.interceptors;

/**
 * Specifies whether a contract has been implemented successfully or not. If it
 * is not successful, then {@link #isFailed()} will return true.
 * 
 * For a successful contract, use {@link #success()}, and for a failed contract,
 * use {@link #fail(String)} instead of the constructor.
 * 
 * @author Julijan Katic
 */
public final class ContractResult {

	/** The default message when the contract was successful. */
	public static final String DEFAULT_SUCCESS_MESSAGE = "Contract Check Successful";

	/** Specifies whether the contract has failed. */
	private final boolean failed;
	/** The message of the contract. */
	private final String message;

	private ContractResult(final boolean failed, final String message) {
		super();
		this.failed = failed;
		this.message = message;
	}

	/**
	 * Returns true iff the contract checking has failed.
	 */
	public boolean isFailed() {
		return failed;
	}

	/**
	 * The message of the result. If the contract result has been successful, then
	 * it will always be {@link #DEFAULT_SUCCESS_MESSAGE}. Otherwise, a provided
	 * custom message is returned.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Instantiates a new contract result and marks it as successful.
	 * {@link #isFailed()} will be false and the {@link #getMessage()} is going to
	 * be {@link #DEFAULT_SUCCESS_MESSAGE}.
	 * 
	 * @return a new instance specifying a successful ContractResult.
	 */
	public static ContractResult success() {
		return new ContractResult(false, DEFAULT_SUCCESS_MESSAGE);
	}

	/**
	 * Instantiates a new failed contract result.
	 * 
	 * @param message The custom message that is passed to the contract result.
	 * @return a new instance.
	 */
	public static ContractResult fail(final String message) {
		return new ContractResult(true, message);
	}
}
