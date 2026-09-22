package in.gov.dilrmp.services.adminService;
import in.gov.dilrmp.models.admin.FlashMessage;
import in.gov.dilrmp.repositories.admin.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private AdminRepository adminRepository;

    public void save(FlashMessage messageForm) {
        try {
            messageForm.setLocalDate();

            // Check if the message is for All State/UTs (i.e., state == null)
            if (messageForm.getState() == null) {
                Optional<FlashMessage> existing = adminRepository.findMessageForAllStates();
                if (existing.isPresent()) {
                    FlashMessage existingMessage = existing.get();
                    existingMessage.setMessage(messageForm.getMessage());
                    existingMessage.setMessageType(messageForm.getMessageType());
                    existingMessage.setStartDate(messageForm.getStartDate());
                    existingMessage.setEndDate(messageForm.getEndDate());
                    existingMessage.setLocalDate();
                    adminRepository.save(existingMessage);
                    return;
                }
            }

            // If not for All State/UTs or no existing All-State message, save normally
            adminRepository.save(messageForm);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error occurred while saving the message.");
        }
    }


    public FlashMessage findByStateId(long stateID) {
        return adminRepository.findByStateId(stateID);
    }

    public FlashMessage findByStateNull() {
        return adminRepository.findByStateNull();
    }

    public List<FlashMessage> findAllFlashMessage() {
        return adminRepository.findAll();
    }
    public Optional<FlashMessage> findById(Long flashMessageID) {
        return adminRepository.findById(flashMessageID);
    }

    public void delete(FlashMessage flashMessage){
        adminRepository.delete(flashMessage);
    }
}