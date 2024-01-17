# Retrospective for complete OpenAPI specification (week 2-3)




| Task                                 | Description                                                                                     | Assigned to    | Estimated effort (in days) | Actual effort | Finished    | Notes                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| ------------------------------------ | ----------------------------------------------------------------------------------------------- | -------------- | -------------------------- | ------------- | ----------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Create initial openAPI specification | Generate proper and working endpoints to project’s repository (i. e., make a proper .yaml file) | The whole team | 6 days                     | 6 days        | Yes         | We have initially created an interactive project where we made a sketch of how the whole entities and classes should be related in the project. Then, a list of possible endpoints that would be needed were created based on the requirements that were established a week before. The whole team was involved in long discussions. Then, an interactive project was created where the whole endpoints were implemented in .yaml format. |
| Fix openAPI specification            | Improve on the initial version to better reflect the models we were going to use                | The whole team | 1 day                      | 1 day         | Yes         | Based on the feedback/gained knowledge from many sources (TAs, lectures, mattermost etc.) we have made many changes throughout the week. There are also issues assigned and created for these tasks.                                                                                                                                                                                                                                      |
| Setup authorization boilerplate code | Encode the role of a user into the JWT token and use it for authorization                       | Jesse Klijnsma | 1 day                      | 1 day         | Yes, but no | In the early stages, we assumed using the JWT tokens was mandatory, so I implemented a proof of concept. Due to confusion after the demo lecture, we were under the impression that the authorization wasn’t even needed and we decided not to implement it, although afterwards we could have gone through with the plan to do proper authorization.                                                                                     |

# Main problems encountered
## Problem 1
### Description:
We misunderstood the extent of our user story. We originally designed a larger and more general scenario. This included parts that were handled by the other team. Until we met up with the other team we didn’t realize our mistake



### Reaction:
Once we knew what was the extent of our service, we changed our plan and our openAPI spec to include only our relevant parts.

## Problem 2
### Description:
We have misinterpreted some of the technical requirements for the openAPI specifcation document. For example, we did not provide example inputs for our entities, nor did we write some proper descriptions for each endpoint.

### Reaction:
We have added all of the missed content to the .yaml file and fixed it.

## Adjustments for the next Sprint plan

For the next Sprint plan we realize we have to be more clear with our definitions and requirements. So we will work on trying to have a more structured and clear requirements list. This is even more important considering that we will start doing more individual work.
