package app.tasks.repository;

import app.tasks.model.LoginWindowsModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginWindowsRepository extends JpaRepository<LoginWindowsModel, String> {
    public LoginWindowsModel findByState(String code);
}
