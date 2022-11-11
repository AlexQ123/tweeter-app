package edu.byu.cs.tweeter.server.dao.bean;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class StoryBean {

    private String sender_alias;
    private long timestamp;
    private String post;
    private String first_name;
    private String last_name;
    private String image;

    @DynamoDbPartitionKey
    public String getSender_alias() {
        return sender_alias;
    }

    public void setSender_alias(String sender_alias) {
        this.sender_alias = sender_alias;
    }

    @DynamoDbSortKey
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
