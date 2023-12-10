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
    private Long id;

    //TODO: change path to byte[]
    private String path;
    private String note;

    @Column(name = "is_user_photo")
    private boolean isUserPhoto;

    @ManyToOne
    @JoinColumn(name = "order_id")
    @JsonBackReference
    private Order order;
}
