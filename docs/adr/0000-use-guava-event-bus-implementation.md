# Guava library for event bus implementation

## Context and Problem Statement

Since the components in the new extensible and flexible simulator are not tightly coupled throuh inprocess communication we needed a library that enables this flexibility. Extensions that add behaviour to the simulation can simply react on particular event types and produce their own events. 

## Decision Drivers <!-- optional -->

* An implementation from a credible source
* Serves the purpose of having components loosely coupled 

