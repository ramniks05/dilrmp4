package in.gov.dilrmp.services.administrativeBoundry;

/**
 * A validation failure while creating a district. Thrown before or instead of
 * a commit so the caller can show the message and the transaction rolls back.
 */
public class AddDistrictException extends RuntimeException {

    public AddDistrictException(String message) {
        super(message);
    }
}
