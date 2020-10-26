/**
 * This package provides different extension points and mechanism for the
 * Slingshot simulator.
 * 
 * <p>
 * <strong>Behavioral</strong>. The behavior extension point is in the
 * behavioral package. The behavior of this system is defined to be the handler
 * of events. This includes dispatching events and to execute certain methods on
 * certain events, as well as publishing new events. Also, these methods are
 * intercepted by the interceptor. The behavior is most likely the simulation
 * part.
 * </p>
 * 
 * <p>
 * <strong>Model</strong>. TODO: The model extension point. This extension point
 * will give possibilities to define further models that are needed for the
 * simulation. Right now, only the usage model and allocation model are
 * supported (See {@link SimulationModel}).
 * </p>
 * 
 */
package org.palladiosimulator.analyzer.slingshot.simulation.extensions;