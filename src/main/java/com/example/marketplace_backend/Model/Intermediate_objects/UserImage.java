package com.example.marketplace_backend.Model.Intermediate_objects;

import com.example.marketplace_backend.Model.FileEntity;
import com.example.marketplace_backend.Model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_image")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "image_id")
    private FileEntity image;
}
