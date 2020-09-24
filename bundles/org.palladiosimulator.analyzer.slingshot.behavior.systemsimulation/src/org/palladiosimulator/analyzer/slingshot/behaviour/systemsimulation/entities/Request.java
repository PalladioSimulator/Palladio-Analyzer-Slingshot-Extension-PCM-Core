package org.palladiosimulator.analyzer.slingshot.behaviour.systemsimulation.entities;

import org.palladiosimulator.analyzer.slingshot.behavior.usagemodel.entities.User;
import org.palladiosimulator.pcm.repository.ProvidedRole;
import org.palladiosimulator.pcm.repository.Signature;

/**
 * 
 * A Request is an immutable data structure holding the owner of the request and the target of the request
 * 
 * @author Floriment Klinaku
 *
 */
public class Request {
	
	private String id; 
	
	// owner of the request
	private User user;
	
	// target of the request
	private ProvidedRole providedRole;
	private Signature signature;
	
	public User getUser() {
		return user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public ProvidedRole getProvidedRole() {
		return providedRole;
	}

	public void setProvidedRole(final ProvidedRole providedRole) {
		this.providedRole = providedRole;
	}

	public Signature getSignature() {
		return signature;
	}

	public void setSignature(final Signature signature) {
		this.signature = signature;
	}

	public Request() {
		
	}
	
	public Request(final User user, final ProvidedRole providedRole, final Signature signature) {
		super();
		this.user = user;
		this.providedRole = providedRole;
		this.signature = signature;
	}

	public String getId() {
		return id;
	}
	// handler of the request
	
	
	// context of the request e.g. variables
		
	// request children for external calls maybe;
	
}