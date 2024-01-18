package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.exceptions.*;
import nl.tudelft.sem.yumyumnow.users.factories.CustomerFactory;
import nl.tudelft.sem.yumyumnow.users.model.Feedback;
import nl.tudelft.sem.yumyumnow.users.model.FeedbackRequestModel;
import nl.tudelft.sem.yumyumnow.users.model.User;
import nl.tudelft.sem.yumyumnow.users.repositories.CustomerRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.FeedbackRepository;
import nl.tudelft.sem.yumyumnow.users.repositories.UserRepository;
import nl.tudelft.sem.yumyumnow.users.validation.objects.ValidatorConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {

    private final transient FeedbackRepository feedbackRepository;
    private final transient UserRepository userRepository;
    private final transient ValidatorConstructor validatorConstructor;
    @Autowired
    public FeedbackService(FeedbackRepository feedbackRepository, UserRepository userRepository){
        this.feedbackRepository = feedbackRepository;
        this.userRepository = userRepository;
        this.validatorConstructor = new ValidatorConstructor();

    }
    public Feedback createFeedback(FeedbackRequestModel feedbackRequestModel)
        throws InvalidFieldException, MissingBodyException, UserNotFoundException {
        validateFeedback(feedbackRequestModel);
        findUser(feedbackRequestModel.getUserId());

        Feedback feedback = new Feedback();
        feedback.setComment(feedbackRequestModel.getComment());
        feedback.setComplaint(feedbackRequestModel.getComplaint());
        feedback.setUserId(feedbackRequestModel.getUserId());
        feedback.setStars(feedbackRequestModel.getStars());

        return feedbackRepository.saveAndFlush(feedback);
    }
    public void deleteFeedback(Long feedbackId, Long userId)
        throws InvalidFieldException, MissingBodyException, UserNotFoundException,
        FeedbackNotFoundException, NotAuthorizedException {
        Feedback feedback = getFeedbackById(feedbackId);
        User user = findUser(userId);

        if (feedback.getUserId() == userId || user.getRole() == User.RoleEnum.ADMIN) {
            feedbackRepository.delete(feedback);
        } else throw new NotAuthorizedException();
    }
    public List<Feedback> getAllFeedbacks() {
        return feedbackRepository.findAll();
    }
    public Feedback getFeedbackById(Long id) throws FeedbackNotFoundException {
        Optional<Feedback> feedback = feedbackRepository.findById(id);
        if (feedback.isEmpty()) throw new FeedbackNotFoundException(id);
        else return feedback.get();
    }

    public void validateFeedback(FeedbackRequestModel feedbackRequestModel)
        throws InvalidFieldException, MissingBodyException {

        if (feedbackRequestModel == null) throw new MissingBodyException();
        if (feedbackRequestModel.getComment() == null || feedbackRequestModel.getComment().equals("")){
            throw new InvalidFieldException("comment", feedbackRequestModel.getComment());
        }
        if (feedbackRequestModel.getComplaint() == null){
            throw new InvalidFieldException("complaint", "null");
        }
        if (feedbackRequestModel.getStars() == null){
            throw new InvalidFieldException("stars", "null");
        }
        if (feedbackRequestModel.getStars() < 0 || feedbackRequestModel.getStars() > 5){
            throw new InvalidFieldException("stars", feedbackRequestModel.getStars().toString());
        }
    }
    public User findUser(Long id)
        throws InvalidFieldException, MissingBodyException, UserNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()){
            throw new UserNotFoundException(id);
        }
        return user.get();
    }
}
