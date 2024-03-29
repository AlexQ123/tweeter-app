package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticateResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.UserService;

public class RegisterHandler implements RequestHandler<RegisterRequest, AuthenticateResponse> {
    @Override
    public AuthenticateResponse handleRequest(RegisterRequest registerRequest, Context context) {
        UserService userService = new UserService(new DynamoDAOFactory());
        return userService.register(registerRequest);
    }
}
