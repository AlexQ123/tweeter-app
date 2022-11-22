package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import edu.byu.cs.tweeter.model.net.request.BatchUpdateFeedRequest;
import edu.byu.cs.tweeter.server.dao.dynamo.DynamoDAOFactory;
import edu.byu.cs.tweeter.server.service.StatusService;
import edu.byu.cs.tweeter.util.JsonSerializer;

public class UpdateFeeds implements RequestHandler<SQSEvent, Void> {

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        StatusService service = new StatusService(new DynamoDAOFactory());

        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            System.out.println(msg.getBody());

            BatchUpdateFeedRequest batchUpdateFeedRequest = JsonSerializer.deserialize(msg.getBody(), BatchUpdateFeedRequest.class);

            service.batchUpdateFeed(batchUpdateFeedRequest);
        }

        return null;
    }

}
