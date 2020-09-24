# Use a DI Library

* Status: proposed
* Date: 22.09.2020

## Context and Problem Statement

The problem is that objects need to be shared between the bundles. Also, if more Models are needed for the simulation part (see [SimulationModel](../../bundles/org.palladiosimulator.analyzer.slingshot/src/org/palaldiosimulator/analyzer/slingshot/simulation/api/SimulationModel.java)) the API might break.

## Decision Drivers

* Should be a trusted library
* Uses annotations

## Considered Options

* [Google Dagger](https://github.com/google/dagger)
* [Google Guice](https://github.com/google/guice)

## Pros and Cons of the Options

### Google Dagger

* Good, because it processes the annotations at compile-time.
* Bad, because sometimes certain information are only available at runtime. While it is also possible here, it is more complicated and more @Modules have to be defined (how to have a global access here?)
* Bad, because at compile-time, the defined interfaces are implemented and it uses the naming convention by prepending the @Components names with `Dagger`.

### Google Guice

* Good, because it uses runtime injection and eases the process of finding the right module (bundles are loaded at runtime).
* Bad, because runtime injection is slow.
* Good, because it is more low-level and has the possibility.