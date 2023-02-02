package app.tasks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Table(name = "user_details")
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
@AllArgsConstructor
public class User {
    @Id
    String id;
    String name;
    String photoUrl;
    @Column(unique = true)
    String username;
}
