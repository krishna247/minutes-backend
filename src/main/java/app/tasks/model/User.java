package app.tasks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "user_details")
@Entity
@NoArgsConstructor
@Setter
@Getter
@ToString
public class User {

    @Id
    String id;

    String name;
    @Column(name="photo_url")
    String photoUrl;
}
