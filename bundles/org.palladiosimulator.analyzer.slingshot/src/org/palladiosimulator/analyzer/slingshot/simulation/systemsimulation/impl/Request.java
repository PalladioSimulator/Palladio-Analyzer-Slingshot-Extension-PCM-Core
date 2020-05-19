package org.palladiosimulator.analyzer.slingshot.simulation.systemsimulation.impl;

import org.palladiosimulator.analyzer.slingshot.simulation.usagesimulation.impl.entities.User;
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

	public void setUser(User user) {
		this.user = user;
	}

	public ProvidedRole getProvidedRole() {
		return providedRole;
	}

	public void setProvidedRole(ProvidedRole providedRole) {
		this.providedRole = providedRole;
	}

	public Signature getSignature() {
		return signature;
	}

	public void setSignature(Signature signature) {
		this.signature = signature;
	}

	public Request() {
		
	}
	
	public Request(User user, ProvidedRole providedRole, Signature signature) {
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
