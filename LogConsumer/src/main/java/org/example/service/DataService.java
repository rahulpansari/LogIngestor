package org.example.service;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.codec.TypeCodecs;
import com.datastax.oss.driver.api.core.type.codec.registry.CodecRegistry;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.apache.kafka.common.protocol.types.Field;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.example.request.Metadata;
import org.example.request.NewTestRequest;
import org.example.request.SearchRequest;
import org.example.request.TestRequest;
import org.example.utils.DbConnection;

import java.security.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataService {
    private  static CqlSession session= DbConnection.getConnection();

    private static String keyspaceName = "test_logs";
    private  static String tableName = "logs";

    // Sample data
    String columnName = "column1";
    String columnValue = "value1";
    private static  final Gson gson=new Gson();
    public static void  insertData(TestRequest request)
    {
        String id=null;
        if(request.getMetadata()!=null) {
            MongoClient mongoClient = DbConnection.getMongoConnection();
            MongoDatabase database = mongoClient.getDatabase("logs");
            Document document = Document.parse(gson.toJson(request.getMetadata()));
            database.getCollection("mylogs").insertOne(document);
            id= String.valueOf(document.get("_id"));

        }

        String columnName = "level,\n" +
                "    message,\n" +
                "    resourceid,\n" +
                "    timestamp,\n" +
                "    traceId,\n" +
                "    spanId,\n" +
                "    metadata,\n" +
                "    commit";
        String columnValue = "'"+request.getLevel()+"','"+request.getMessage()+"','"+request.getResourceId()+"','"+request.getTimestamp()+"','"+request.getTraceId()+"','"+request.getSpanId()+"','"+id+"','"+request.getCommit()+"'";
        String dQuery="INSERT INTO " + keyspaceName + "." + tableName + " (" + columnName + ") VALUES ("+columnValue+");";
        System.out.println( dQuery);
       System.out.println( session.execute(dQuery));

    }

    public static List<NewTestRequest>  searchData(SearchRequest request)
    {

        String id=null;
        List<NewTestRequest> requestList=new ArrayList<>();
        if(request.getMetadata()!=null) {
            MongoClient mongoClient = DbConnection.getMongoConnection();
            MongoDatabase database = mongoClient.getDatabase("logs");
            Document document = Document.parse(gson.toJson(request.getMetadata()));
            database.getCollection("mylogs").insertOne(document);

        }
        String filter=null;
        if(request.getLevel()!=null)
            filter="level='"+request.getLevel()+"' ";
        if(request.getTimestamps()!=null&&filter!=null)
            filter=filter+" AND timestamp >= "+request.getTimestamps()[0]+(request.getTimestamps().length>1?(" AND timestamp<="+ request.getTimestamps()[1]):"");
        else if(request.getTimestamps()!=null)
            filter="timestamp >= "+request.getTimestamps()[0]+(request.getTimestamps().length>1?(" AND timestamp<="+ request.getTimestamps()[1]):"");
        if(request.getResourceId()!=null&&filter!=null)
            filter=filter+"AND resourceId = '"+request.getResourceId()+"' ";
        else if(request.getResourceId()!=null)
            filter="resourceId =  '"+request.getResourceId()+"' ";
        if(request.getTraceId()!=null&&filter!=null)
            filter=filter+"AND traceId = '"+request.getTraceId()+"' ";
        else if(request.getTraceId()!=null)
            filter=" traceId =  '"+request.getTraceId()+"' ";
        if(request.getSpanId()!=null&&filter!=null)
            filter=filter+"AND spanId = '"+request.getSpanId()+"' ";
        else if(request.getSpanId()!=null)
            filter=" spanId =  '"+request.getSpanId()+"' ";

        if(request.getCommit()!=null&&filter!=null)
            filter=filter+"AND commit = '"+request.getCommit()+"' ";
        else if(request.getSpanId()!=null)
            filter=" commit =  '"+request.getCommit()+"' ";

        if(request.getMessage()!=null&&filter!=null)
            filter=filter+"AND message like '%"+request.getMessage()+"%' ";
        else if(request.getMessage()!=null)
            filter=" message like  '%"+request.getMessage()+"%' ";


        if(filter!=null)
        {
            if(filter.contains("AND"))
                filter=filter+" ALLOW FILTERING ";
            String dQuery="SELECT * FROM  " + keyspaceName + "." + tableName + " WHERE "+filter;
            System.out.println( dQuery);
            ResultSet resultSet=session.execute(dQuery);
            for (Row row : resultSet) {
                NewTestRequest request1=new NewTestRequest();
                request1.setLevel(row.getString("level"));
                request1.setMessage(row.getString("message"));
                request1.setCommit(row.getString("commit"));
                request1.setTimestamp(row.get("timestamp", Instant.class).toString());
                request1.setSpanId(row.getString("spanId"));
                request1.setTraceId(row.getString("traceId"));
                request1.setResourceId(row.getString("resourceId"));
                request1.setMetaDataId(row.getString("metadata"));
                requestList.add(request1);
                // Access columns by name or index and process the data

                // Process other columns if needed
            }
            if(request.getMetadata()!=null)
            {

            }
            else {
                System.out.println(gson.toJson(requestList.stream().map(NewTestRequest::getMetaDataId).collect(Collectors.toList())));
                MongoClient mongoClient = DbConnection.getMongoConnection();
                MongoDatabase database = mongoClient.getDatabase("logs");
                FindIterable<Document>data=database.getCollection("mylogs").find(Filters.in("_id",requestList.stream().map(NewTestRequest::getMetaDataId).map(ObjectId::new).collect(Collectors.toList())));
                MongoCursor<Document>cursor=data.iterator();

                while(cursor.hasNext())
                {
                    System.out.println("hi");
                    Document document=cursor.next();
                    String ids=document.get("parentResourceId").toString();
                    String parentResourceId=String.valueOf(document.get("parentResourceId"));
                    System.out.println(parentResourceId);
                    requestList.stream().filter(ss->ss.getMetaDataId().equals(ids)).forEach(s->{s.setMetadata(new Metadata(parentResourceId));s.setMetaDataId(null);});
                }
            }

        }
        System.out.println(gson.toJson(requestList));
        return requestList;

    }
}
