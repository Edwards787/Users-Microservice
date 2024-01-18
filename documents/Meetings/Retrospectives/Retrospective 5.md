# Wrap up coding, implement end-to-end tests and write final report (week 7-8)

| Task                                                       | Description                                                                                                                                                                                                                                         | Assigned to      | Estimated effort | Actual effort | Finished | Notes                                                                                                                                                                                                                                                                                                                                                                                                |
| ---------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------- | ---------------- | ------------- | -------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Implement end-to-end system tests                          | Create end-to-end system tests to check the compatibility with other microservices                                                                                                                                                                  | Whole team       | 6h / person      | 6h / person   | Yes      | We have tested out compatibility with other microservices by using Postman. We have thought of as many possible scenarios and saved them as screenshots that were added in the final report.                                                                                                                                                                                                         |
| Refactor builder class                                     | Refactor builder classes once again to follow factory design pattern                                                                                                                                                                                | Saunaq, Justinas | 6h / person      | 6h / person   | Yes      | We have found out that there is a necessity to refactor our builder class once again to follow the Factory design pattern, as the Factory design pattern should not be used to update objects. Therefore, we have changed it and now it is hopefully following the design pattern properly.                                                                                                          |
| Implement last endpoints                                   | Implement last couple of endpoints that have been (somehow) missed-out                                                                                                                                                                              | Jesse            | 4h               | 4h            | Yes      | We have accidentally forgotten to implement a couple of endpoints and therefore they had to be implemented slightly off-schedule. However, we have successfully done that and finished the coding part.                                                                                                                                                                                              |
| Create fake implementation for email and location services | We have added fake implementation for email and location services so that all of the initially set-out requirements would be satisfied                                                                                                              | Jesse            | 6h               | 6h            | Yes      | We have created ‘default’ location and email service implementations, and allow for actual implementation to be added later. Also, we (hopefully correctly) implemented the Builder design pattern for email service. |
| Write final report | Write the final report that aims to demonstrate the functionality of the implemented system, the thought process, decision-making, and efforts made towards software quality, design, and integration aspects throughout the development process | Whole team       | 3h               | 2h            | Yes      | The final report must contain the final list of requirements implemented, Design pattern Implementation, Software Quality Report, Mutation Testing Report and Integration Report. We have distributed tasks and divided into sub teams that worked on different parts of the report. Later, we all went through the report together and made sure it was written properly. This took us a few days. |

# Main problems encountered
## Problem 1
### Description:
Refactoring the builder class to be a factory was an issue, because we were unsure as to whether we were applying the design pattern correctly. Initially it looked like the builder design pattern was a good fit for our use case, but, after some further discussion, we agreed that the factory pattern was better, but it still wasn’t 100% a perfect fit.

### Reaction:
We decided to ask a TA and several course staff about what changes we should make to properly implement the Factory design pattern, and they said that our specific implementation didn’t matter so long as the key aspects of the factory pattern were still held, so we made some small changes to better fit the factory pattern, but we think that our current implementation is good

## Problem 2
### Description:
While writing system tests, we have faced difficulties when trying to access other microservice. One of the microservice (namely, Delivery) had a couple methods implemented with bugs, therefore, it was not possible to access any of their endpoints, as an error was being thrown with any of the requests. However, after some time the team made a pretty big merge request and solved this problem. However, it did cost us a bit of stress and we lost some time.

### Reaction:
We addressed the other subteam with  a message notifying them about the problem. They have received a message and made changes, however, even after the initial changes and us specifically addressing the exact problem, they did not make it work. Therefore, as we did not want to waste more time, we temporarily changed some of their code in our branch so we could start testing.

## Problem 3
### Description:
When writing system tests, we have faced difficulties due to poor implementation from our colleagues in other microservices. Some of their design choices turned out to require way more effort to test than it should actually take if it was implemented smartly. For example, when creating an order, instead of specifying the ID of the dish that is being ordered,  a whole dish object (with quite a few attributes) has to be passed. Situations like that made testing more difficult and time-consuming.
### Reaction:
Even with all of these difficulties, we managed to properly test the system. It created, however, a lot of frustration from some of our team members that had to deal with these issues.

## Problem 4
### Description:
We have somehow forgotten to implement several (namely, four) endpoints. Even though for quite some time we thought we had finished implementing all of the endpoints, due to a misunderstanding, some of them were forgotten.

### Reaction:
After realizing this we quickly assigned them to a team member and it was swiftly taken care of. It is a good thing we realized the endpoints were missing before the deadline of our project. In addition, it was also an opportunity for the team member assigned to work on implementing endpoints, as he had previously completed other tasks.


## Adjustments for the next Sprint Plan

As this is the last sprint, there are no adjustments for next week.
