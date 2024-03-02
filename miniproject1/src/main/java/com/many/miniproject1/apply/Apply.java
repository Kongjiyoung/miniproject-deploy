package com.many.miniproject1.apply;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Table(name = "apply_tb")
@Data
@Entity // 테이블 생성하기 위해 필요한 어노테이션
public class Apply {
    @Id // PK 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment 전략
    private Integer id;
    private Integer resumeId;
    private Integer postId;
    private Integer postWriterId;
    private Integer resumeWriterId;
    private String isPass;
    private Timestamp created_at;
}
