package edu.byu.cs.tweeter.server.dao.dynamo;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.server.dao.ImageDAO;

public class S3ImageDAO implements ImageDAO {

    private final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.US_WEST_1).build();
    private final String BUCKET_NAME = "cs340-tweeter-images";

    @Override
    public String uploadImage(String image, String username) {
        byte[] bI = Base64.getDecoder().decode(image);
        InputStream fis = new ByteArrayInputStream(bI);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bI.length);
        metadata.setContentType("image/png");
        metadata.setCacheControl("public, max-age=31536000");

        s3.putObject(BUCKET_NAME, username, fis, metadata);
        s3.setObjectAcl(BUCKET_NAME, username, CannedAccessControlList.PublicRead);

        return s3.getUrl(BUCKET_NAME, username).toString();
    }

}
