package com.jkTech.document.managementApp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "document_contents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentContent {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Document document;

    @Column(columnDefinition = "TEXT")
    private String content;

//    @Column(name = "search_vector", columnDefinition = "TEXT")
//    @Lob
//    private String searchVector;
}
