# access-control

*many-to-many relationships between roles and features*

Each Role object knows which features it can access.

Each Feature object knows which Roles can access it.

We create a Redis set for each role: 

- sRole:{role-id}
 
    - contains feature-keys

We create a Redis set for each feature:
 
- sFeature:{feature-id} 

    - contains role-keys

The Access Control System should include methods to:
 
- add a new role

- add a new feature

- assign a feature to a role

- check if a given role can access a given feature

- get all features for a given role

- get all roles for a given feature

control
---
AccessControlManager

- The controller class for this POC
- Delegates work to Feature and Role
- Handles creation of new Features and Roles

actors
---
Feature

- Able to determine which Roles are authorized.
- Able to grant authorization to a Role.
- Knows how to lookup its data from the DB.

Role

- Able to determine which Features it has access to.
- Able to add new Features to its access list.
- Knows how to lookup its data from the DB.
