package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.BatchUpdateFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFollowersResponse;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.FollowService;
import edu.byu.cs.tweeter.util.JsonSerializer;

public class PostUpdateFeedMessages implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        FollowService service = new FollowService(new DynamoDAOFactory());

        String updateFeedQueueURL = "https://sqs.us-west-1.amazonaws.com/140218667860/UpdateFeedQueue";

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println("Message Body: " + msg.getBody());

            // deserialize the PostStatusRequest
            PostStatusRequest postStatusRequest = JsonSerializer.deserialize(msg.getBody(), PostStatusRequest.class);

            // build the GetFollowersRequest
            AuthToken authToken = postStatusRequest.getAuthToken();
            String followeeAlias = postStatusRequest.getStatus().getUser().getAlias();
            int limit = 25;
            String lastFollowerAlias = null;

            while (true) {
                // get a page of followers
                GetFollowersRequest getFollowersRequest = new GetFollowersRequest(authToken, followeeAlias, limit, lastFollowerAlias);
                GetFollowersResponse getFollowersResponse = service.getFollowers(getFollowersRequest);

                if (!getFollowersResponse.isSuccess()) {
                    throw new RuntimeException("[Server Error] Failed to get followers for updating feeds after posting a status.");
                }

                BatchUpdateFeedRequest batchUpdateFeedRequest = new BatchUpdateFeedRequest(authToken, postStatusRequest.getStatus(), getFollowersResponse.getFollowers());

                SendMessageRequest send_msg_request = new SendMessageRequest()
                        .withQueueUrl(updateFeedQueueURL)
                        .withMessageBody(JsonSerializer.serialize(batchUpdateFeedRequest));

                AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
                SendMessageResult send_msg_result = sqs.sendMessage(send_msg_request);
                System.out.println("Message ID: " + send_msg_result.getMessageId());

                if (!getFollowersResponse.getHasMorePages()) {
                    break;
                }

                lastFollowerAlias = getFollowersResponse.getFollowers().get(limit - 1).getAlias();
            }

        }

        return null;
    }

}
