# access-control

*many-to-many relationships between roles and features*

Each actors.Role object knows which features it can access.

Each actors.Feature object knows which Roles can access it.

Create a Redis set for each role: 

- sRole:{role-id}
 
    - contains feature-keys

Create a Redis set for each feature:
 
- sFeature:{feature-id} 

    - contains role-keys

Need methods to:
 
- add a new role

- add a new feature

- assign a feature to a role

- check if a given role can access a given feature

- get all features for a given role

- get all roles for a given feature