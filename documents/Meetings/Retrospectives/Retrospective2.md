# Retrospective for modifying the openAPI specifications and building code structure (week 3-4)

| Task                              | Description                                                                                       | Assigned to    | Estimated effort | Actual effort | Finished | Notes                                                                                                                                                                                                                                                                                    |
| --------------------------------- | ------------------------------------------------------------------------------------------------- | -------------- | ---------------- | ------------- | -------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Modify the OpenAPI specifications | Modify or add endpoints to project’s repository (update the yaml file) based on provided feedback | The whole team | 6 days           | 6 days        | Yes      | After receiving feedback from the TA and the other 2 teams we have reviewed it and made modifications.We have communicated with other 2 microservice teams and also reviewed their work. Also, we have introduced changes based on what we figured out to be wrong ourselves.|
| Create code structure             | Implement the basic structure of our code, based on what we discussed.                            | The whole team | 1 day            | 1 day         | Yes      | We decided on a general approach and template of the coding part. We also implemented in on the main repository so everyone could start working on their part.                                                                                                                           |
| Create initial system state       | Added a default always-present admin account                                                      | Jesse Klijnsma | 1 day            | 1 day         | Yes      | The initial admin was necessary to allow for certain admin-only operations to be performed                                                                                                                                                                                               |

# Main problems encountered

## Problem 1
### Description:
During the third week, once we have started creating the general code structure, we have found out that we had actually missed to add a couple of things to the OpenAPI specifications: for example, we have not thought to build requestModels as a hierarchy, all inheriting from userRequestModel. This later turned out to be very useful for all the development.
### Reaction:
We have fixed the things that have been missed out by simply adding proper implementation to the .yaml file.

## Problem 2
### Description:
Some endpoints that were implemented in the previous weeks had inconsistent content. For example, a few ones had different return status codes for the same type of requests (201 and 200 for UPDATE requests, etc.) and there were also some inconsistencies in the paths we were using to retrieve “all” users of a certain type (we were using /customer/.. to get information for a specific customer, but /customers to get all customers). Moreover, we didn’t appropriately signal that a certain user was unauthorized to complete some operation.

### Reaction:
We have successfully tackled the problems, by renaming paths to use the /user/all pattern, while also changing the return status codes to the appropriate values. When it comes to authorization we debated on whether to use 401 or 403 as an appropriate status code to return when a user is not authorised to perform a certain request. Despite the naming, we went for 403 (Forbidden), instead of 401 (Unauthorized)m since the description of the latter implied that it is more related to authentication, whereas we did not need that. Thus we went for the first, which is more related to having the server refuse to fulfill a request.
## Problem 3
### Description: 
The endpoints we implemented before the deadline were well defined, but not complete. We are missing multiple endpoints to satisfy all requirements of our microservice.

### Reaction: 
In response to this problem we met up with all of the team members, discussed which endpoints we are missing and modified the OpenAPI to meet all of the requirements. In addition to that we discussed with the other microservice teams and added the endpoints necessary to make integration of our microservices possible.

## Adjustments for the next Sprint Plan

For the next sprint, we should start assigning individual tasks, as opposed to working together. In addition to that, one of our team members cannot join the next sprint meeting in person, so we will have to take that into account and make sure he can still participate in the meeting virtually. (e.g. someone uses Discord/ Whatsapp to communicate with him while we are in the meeting).
