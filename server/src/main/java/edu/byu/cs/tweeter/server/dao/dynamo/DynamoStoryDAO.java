package edu.byu.cs.tweeter.server.dao.dynamo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.bean.StoryBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class DynamoStoryDAO extends DynamoStatusDAO implements StoryDAO {

    private static final String TableName = "story";

    private static final DynamoDbTable<StoryBean> storyTable = enhancedClient.table(TableName, TableSchema.fromBean(StoryBean.class));

    @Override
    public Pair<List<Status>, Boolean> getPagedStory(String targetUserAlias, int pageSize, Status lastStatus) {
        List<StoryBean> storyBeans = new ArrayList<>();
        List<Status> storyStatuses = new ArrayList<>();
        boolean hasMorePages = false;

        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key)).scanIndexForward(false);

        if (lastStatus != null) {
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("sender_alias", AttributeValue.builder().s(targetUserAlias).build());
            startKey.put("timestamp", AttributeValue.builder().n(String.valueOf(lastStatus.getTimestamp())).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        storyTable.query(request).items().stream().limit(pageSize+1).forEach(s -> storyBeans.add(s));

        // Check if there's more pages
        if (storyBeans.size() == pageSize + 1) {
            hasMorePages = true;
            storyBeans.remove(storyBeans.size() - 1);
        }

        // Convert StoryBeans to StoryStatuses
        for (StoryBean storyBean : storyBeans) {
            User sender = new User(storyBean.getFirst_name(), storyBean.getLast_name(), storyBean.getSender_alias(), storyBean.getImage());

            Status status = new Status();

            status.setPost(storyBean.getPost());
            status.setUser(sender);
            status.setDatetime(storyBean.getFormatted_date_time());
            status.setUrls(parseURLs(storyBean.getPost()));
            status.setMentions(parseMentions(storyBean.getPost()));
            status.setTimestamp(storyBean.getTimestamp());

            storyStatuses.add(status);
        }

        return new Pair<>(storyStatuses, hasMorePages);
    }

    @Override
    public void addStatus(Status status) {
        StoryBean storyBean = new StoryBean();

        storyBean.setSender_alias(status.getUser().getAlias());
        storyBean.setTimestamp(status.getTimestamp());
        storyBean.setFormatted_date_time(status.getDatetime());
        storyBean.setPost(status.getPost());
        storyBean.setFirst_name(status.getUser().getFirstName());
        storyBean.setLast_name(status.getUser().getLastName());
        storyBean.setImage(status.getUser().getImageUrl());

        storyTable.putItem(storyBean);
    }

}
