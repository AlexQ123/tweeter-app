package edu.byu.cs.tweeter.server.dao.dynamo;

import java.sql.Timestamp;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.bean.StoryBean;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class DynamoStoryDAO extends DynamoDAO implements StoryDAO {

    private static final String TableName = "story";

    private static final DynamoDbTable<StoryBean> storyTable = enhancedClient.table(TableName, TableSchema.fromBean(StoryBean.class));

    @Override
    public Pair<List<Status>, Boolean> getPagedStory(String targetUserAlias, int pageSize, Status lastStatus) {
        return null;
    }

    @Override
    public void addStatus(Status status) {
        StoryBean storyBean = new StoryBean();
        storyBean.setSender_alias(status.getUser().getAlias());
        storyBean.setTimestamp(Timestamp.valueOf(status.getDate()).getTime());
        storyBean.setPost(status.getPost());
        storyBean.setFirst_name(status.getUser().getFirstName());
        storyBean.setLast_name(status.getUser().getLastName());
        storyBean.setImage(status.getUser().getImageUrl());

        storyTable.putItem(storyBean);
    }

}
