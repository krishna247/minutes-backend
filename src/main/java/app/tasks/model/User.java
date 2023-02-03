package app.tasks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
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
    @Size(min = 5, message = "Username must be at least 5 characters")
    String username;
}
