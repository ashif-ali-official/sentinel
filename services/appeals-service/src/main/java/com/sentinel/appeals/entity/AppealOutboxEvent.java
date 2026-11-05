package com.sentinel.appeals.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="appeal_outbox_events")
public class AppealOutboxEvent {
    @Id @Column(length=36) private String id;
    @Column(name="aggregate_id",nullable=false,length=36) private String aggregateId;
    @Column(name="event_type",nullable=false,length=150) private String eventType;
    @Column(nullable=false,length=200) private String topic;
    @Column(nullable=false,columnDefinition="json") private String payload;
    @Column(nullable=false,length=20) private String status;
    @Column(nullable=false) private int attempts;
    @Column(name="created_at",nullable=false) private Instant createdAt;
    @Column(name="next_attempt_at",nullable=false) private Instant nextAttemptAt;
    @Column(name="claimed_at") private Instant claimedAt;
    @Column(name="published_at") private Instant publishedAt;
    @Column(name="last_error",length=2000) private String lastError;
    protected AppealOutboxEvent(){}
    public AppealOutboxEvent(String id,String aggregate,String type,String topic,String payload){this.id=id;aggregateId=aggregate;eventType=type;this.topic=topic;this.payload=payload;status="PENDING";createdAt=Instant.now();nextAttemptAt=createdAt;}
    public String getId(){return id;} public String getAggregateId(){return aggregateId;} public String getTopic(){return topic;} public String getPayload(){return payload;}
    public void published(){status="PUBLISHED";publishedAt=Instant.now();claimedAt=null;lastError=null;}
    public void failed(String error,int max){attempts++;lastError=error==null?"Unknown publishing error":error.substring(0,Math.min(2000,error.length()));claimedAt=null;if(attempts>=max)status="FAILED";else{status="PENDING";nextAttemptAt=Instant.now().plusSeconds(Math.min(300L,1L<<Math.min(attempts,8)));}}
}
