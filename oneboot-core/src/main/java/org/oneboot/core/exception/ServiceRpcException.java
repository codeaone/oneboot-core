package org.oneboot.core.exception;

public class ServiceRpcException extends ObootException {

	private static final long serialVersionUID = 4916143339407452797L;

	public ServiceRpcException(IErrorCode errorCode) {
		super(errorCode);
	}

}
