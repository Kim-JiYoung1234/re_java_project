package com.human.ofim.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "category") // DB에 만들어둔 테이블 이름
public class CategoryEntity {

    @Id
    @Column(name = "category_id") // DB의 PK 컬럼명
    private int categoryId;

    @Column(name = "category_name") // 화면에 보여줄 이름 컬럼명 (예: 일상, 운동)
    private String categoryName;
}