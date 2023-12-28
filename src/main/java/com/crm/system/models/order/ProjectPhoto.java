package com.crm.system.models.order;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_photos")
@Getter
@Setter
public class ProjectPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "photo_id")
    private Long photoId;

    @Lob
    @Column(name = "photo_data", columnDefinition = "LONGBLOB")
    private byte[] photoData;

    private String note;

    @Column(name = "is_client_photo")
    private boolean isClientPhoto;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;
}
