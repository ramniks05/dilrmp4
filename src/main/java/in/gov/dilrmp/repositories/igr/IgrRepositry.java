package in.gov.dilrmp.repositories.igr;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


import in.gov.dilrmp.models.igr.Igr;


public interface IgrRepositry extends CrudRepository<Igr, Long> {
    @Query("SELECT i FROM Igr i WHERE i.user.id = :userId")
    public Igr findIgrByUserId(@Param("userId") Long userId);
}
