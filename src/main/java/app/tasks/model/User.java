package app.tasks.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    String id;
    String name;
    String photoUrl;
    @Column(unique = true)
    @Size(min = 5, message = "Username must be at least 5 characters")
    String username;
}
