package in.gov.dilrmp.configs;
import in.gov.dilrmp.repositories.user.UserRepositry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
@Component
public class ApplicationStartupRunner implements ApplicationRunner{

    @Autowired
    UserRepositry userRepositry;

    @Override
    public void run(ApplicationArguments args) throws Exception {
              userRepositry.updateUsersActive(false);

    }
}
