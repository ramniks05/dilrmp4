package in.gov.dilrmp.api.prayas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Single REST API for PRAYAS Portal with only the five document indicators.
 */
@RestController
@RequestMapping("/api/prayas")
@CrossOrigin
public class PrayasPortalApiController {

    @Autowired
    private PrayasPortalApiService prayasPortalApiService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> allStates() {
        return ResponseEntity.ok(prayasPortalApiService.allStates());
    }

    @GetMapping("/states")
    public ResponseEntity<Map<String, Object>> states() {
        return ResponseEntity.ok(prayasPortalApiService.allStates());
    }
}
