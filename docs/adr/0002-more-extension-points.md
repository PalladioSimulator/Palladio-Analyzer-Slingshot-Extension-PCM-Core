# More Extension Points

* Status: proposed

## Context and Problem Statement

At the moment, it is only possible to extend the behavior of the system. However, it should also be possible to extend the following points:

* Modules (see [Use A DI Library](0001-use-a-dependency-injection-library.md)).
* ~~Interceptors~~ (As discussed in the first code review, this was rejected).
* ~~Behavior~~ (already implemented)
* Workflow (to arrange the software components and composing them)
* Simulation Engine (to exchange the simulation engine (i.e. instead of using SSJ))
* *ExtensionPoints* (To define a extension point in the system. This might be used only for the other extension points defined above).
