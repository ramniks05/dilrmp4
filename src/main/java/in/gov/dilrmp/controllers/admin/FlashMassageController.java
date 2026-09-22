package in.gov.dilrmp.controllers.admin;
import in.gov.dilrmp.models.admin.FlashMessage;
import in.gov.dilrmp.models.administrativeBoundry.State;

import in.gov.dilrmp.services.adminService.MessageService;
import in.gov.dilrmp.services.administrativeBoundry.StateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("dolr")
public class FlashMassageController {
    Logger logger = LoggerFactory.getLogger(FlashMassageController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private StateService stateService;

    @GetMapping("/flash-massage")
       public String getAlertMessageForm(Model model) {
        List<State> states = stateService.findAllOrderByStateName();
        if (states == null || states.isEmpty()) {
            model.addAttribute("errorMessage", "No states available to display.");
        }
        model.addAttribute("states", states);
        model.addAttribute("flashMessage", new FlashMessage());

        return "pages/admin/flashMassage :: target-flash-massage";
    }


    @PostMapping("/save-flash-message")
    public ResponseEntity<Map<String, String>> saveFlashMessage(@ModelAttribute FlashMessage flashMessage) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            if (flashMessage.getStringStartDate() != null && flashMessage.getStringEndDate() != null) {
                flashMessage.setStartDate(LocalDate.parse(flashMessage.getStringStartDate(), formatter));
                flashMessage.setEndDate(LocalDate.parse(flashMessage.getStringEndDate(), formatter));

                if (flashMessage.getStartDate().isAfter(flashMessage.getEndDate())) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                            "status", "error",
                            "message", "Start date of flash message can't be greater than end date of flash message."
                    ));
                }
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "status", "error",
                        "message", "Please select valid start and end dates for the flash message."
                ));
            }

            // Special handling for All States (id = 999)
            if (flashMessage.getState() == null || flashMessage.getState().getId() == 999L) {
                flashMessage.setState(null); // Represent 'All States' with null or handle explicitly
                flashMessage.setLocalDate();
                messageService.save(flashMessage);
                logger.info("Flash message saved successfully for All States.");
            } else {
                flashMessage.setLocalDate();
                messageService.save(flashMessage);
                logger.info("Flash message saved successfully for state: {}", flashMessage.getState().getName());
            }

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Flash message updated successfully!"
            ));
        } catch (Exception e) {
            logger.error("Error occurred while saving the flash message: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "status", "error",
                    "message", "An error occurred while saving the flash message. Please try again later."
            ));
        }
    }


    @PostMapping("/flash-message-data")
    public String getFlashMessageData(@RequestParam("state.id") long stateID, Model model) {
        try {
            FlashMessage flashMessage;
            if(stateID== 999){
                flashMessage = messageService.findByStateNull();
            }else{
                flashMessage = messageService.findByStateId(stateID);
            }

           if (flashMessage != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                if(flashMessage.getStartDate()!= null && flashMessage.getEndDate() !=null){
                    flashMessage.setStringStartDate(flashMessage.getStartDate().format(formatter));
                    flashMessage.setStringEndDate(flashMessage.getEndDate().format(formatter));
                }
                model.addAttribute("flashMessage", flashMessage);
            } else {
                Integer lgdCode = Math.toIntExact(stateID); // Convert long to integer
                State state = stateService.findStateByLgdCode(lgdCode)
                        .orElseThrow(() -> new IllegalArgumentException("State not found for LGD code: " + lgdCode));
                flashMessage = new FlashMessage();
                flashMessage.setState(state);
                model.addAttribute("flashMessage", flashMessage);
            }

            List<State> states = stateService.findAllOrderByStateName();
            model.addAttribute("states", states);

            logger.info("Flash message data loaded successfully for State ID: {}", stateID);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid state ID: {}. Error: {}", stateID, e.getMessage(), e);
            model.addAttribute("errorMessage", "Invalid state ID: " + stateID);
        } catch (Exception e) {
            logger.error("An error occurred while loading flash message data for State ID: {}", stateID, e);
            model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        }

        return "pages/admin/flashMassage :: target-flash-massage";
    }

    @GetMapping("/get-all-flash-massage")
    public ResponseEntity<Map<String, Object>> getAllFlashMassage() {
        Map<String, Object> response = new HashMap<>();
        HttpStatus status = HttpStatus.OK;

        try {
            // Fetch all flash messages
            List<FlashMessage> allFlashMessages = messageService.findAllFlashMessage();

            // Sort by state name, treating null as "All State/UTs"
            allFlashMessages.sort(Comparator.comparing(
                    flashMessage -> {
                        State state = flashMessage.getState();
                        return (state != null && state.getName() != null) ? state.getName() : "All State/UTs";
                    },
                    String.CASE_INSENSITIVE_ORDER
            ));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            List<Map<String, Object>> flashMessageList = new ArrayList<>();

            for (FlashMessage flashMessage : allFlashMessages) {
                if (flashMessage != null) {
                    if (flashMessage.getStartDate() != null && flashMessage.getEndDate() != null) {
                        flashMessage.setStringStartDate(flashMessage.getStartDate().format(formatter));
                        flashMessage.setStringEndDate(flashMessage.getEndDate().format(formatter));
                    } else {
                        flashMessage.setStringStartDate("");
                        flashMessage.setStringEndDate("");
                    }

                    Map<String, Object> flashMessageData = new HashMap<>();
                    flashMessageData.put("id", flashMessage.getId());
                    flashMessageData.put(
                            "stateName",
                            flashMessage.getState() != null ? flashMessage.getState().getName() : "All State/UTs"
                    );
                    flashMessageData.put("message", flashMessage.getMessage());
                    flashMessageData.put("startDate", flashMessage.getStringStartDate());
                    flashMessageData.put("endDate", flashMessage.getStringEndDate());

                    flashMessageList.add(flashMessageData);
                }
            }

            response.put("status", "success");
            response.put("flashMessages", flashMessageList);
        } catch (Exception e) {
            logger.error("Error occurred while fetching flash messages", e);
            response.put("status", "error");
            response.put("message", "Failed to fetch flash messages. Please try again.");
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(response, status);
    }




    @PostMapping("/delete-flash-message/{id}")
    public ResponseEntity<?> deleteFlashMessage(@PathVariable Long id) {
        try {
            // Find the flash message by ID and delete it
            Optional<FlashMessage> flashMessage = messageService.findById(id);
            if (flashMessage.get() != null) {
                messageService.delete(flashMessage.get()); // Assuming delete method exists in your service
                return ResponseEntity.ok(Map.of("status", "success", "message", "Flash message deleted successfully"));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("status", "error", "message", "Flash message not found"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Error occurred while deleting the flash message"));
        }
    }



}
