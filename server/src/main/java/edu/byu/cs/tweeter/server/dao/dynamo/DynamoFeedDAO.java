package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowsDAO;
import edu.byu.cs.tweeter.server.dao.bean.FeedBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoFeedDAO extends DynamoStatusDAO implements FeedDAO {

    private static final String TableName = "feed";

    private static final DynamoDbTable<FeedBean> feedTable = enhancedClient.table(TableName, TableSchema.fromBean(FeedBean.class));

    private final FollowsDAO followsDAO = new DynamoFollowsDAO();

    @Override
    public Pair<List<Status>, Boolean> getPagedFeed(String targetUserAlias, int pageSize, Status lastStatus) {
        List<FeedBean> feedBeans = new ArrayList<>();
        List<Status> feedStatuses = new ArrayList<>();
        boolean hasMorePages = false;

        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(false);

        if (lastStatus != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("receiver_alias", AttributeValue.builder().s(targetUserAlias).build());
            startKey.put("timestamp", AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        feedTable.query(request).items().stream().limit(pageSize+1).forEach(s -> feedBeans.add(s));

        // Check if there's more pages
        if (feedBeans.size() == pageSize + 1) {
            hasMorePages = true;
            feedBeans.remove(feedBeans.size() - 1);
        }

        for (FeedBean feedBean : feedBeans) {
            User receiver = new User(feedBean.getFirst_name(), feedBean.getLast_name(), feedBean.getReceiver_alias(), feedBean.getImage());

            Status status = new Status();

            status.setPost(feedBean.getPost());
            status.setUser(receiver);
            status.setDatetime(feedBean.getFormatted_date_time());
            status.setUrls(parseURLs(feedBean.getPost()));
            status.setMentions(parseMentions(feedBean.getPost()));
            status.setTimestamp(feedBean.getTimestamp());

            feedStatuses.add(status);
        }

        return new Pair<>(feedStatuses, hasMorePages);
    }

    @Override
    public void addStatus(Status status) {
        User sender = status.getUser();
        List<User> receivers = followsDAO.getAllFollowers(sender.getAlias());

        for (User receiver : receivers) {
            FeedBean feedBean = new FeedBean();

            feedBean.setReceiver_alias(receiver.getAlias());
            feedBean.setTimestamp(status.getTimestamp());
            feedBean.setFormatted_date_time(status.getDatetime());
            feedBean.setPost(status.getPost());
            feedBean.setFirst_name(sender.getFirstName());
            feedBean.setLast_name(sender.getLastName());
            feedBean.setImage(sender.getImageUrl());

            feedTable.putItem(feedBean);
        }
    }

}
