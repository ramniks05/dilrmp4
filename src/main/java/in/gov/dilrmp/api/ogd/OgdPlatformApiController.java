package in.gov.dilrmp.api.ogd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Single REST API for OGD Platform (data.gov.in): all States/UTs with
 * fields from every component listed in the share document.
 */
@RestController
@RequestMapping("/api/ogd")
@CrossOrigin
public class OgdPlatformApiController {

    @Autowired
    private OgdPlatformApiService ogdPlatformApiService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> allStates() {
        return ResponseEntity.ok(ogdPlatformApiService.allStates());
    }

    @GetMapping("/states")
    public ResponseEntity<Map<String, Object>> states() {
        return ResponseEntity.ok(ogdPlatformApiService.allStates());
    }
}
