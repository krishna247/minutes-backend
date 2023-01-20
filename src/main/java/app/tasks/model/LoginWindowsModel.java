package app.tasks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "login_windows")
@Entity
@NoArgsConstructor
@Setter
@Getter
@AllArgsConstructor
public class LoginWindowsModel {

    @Id
    String state;
//    String sessionToken;
    String userId;
    long event_timestamp;

}
