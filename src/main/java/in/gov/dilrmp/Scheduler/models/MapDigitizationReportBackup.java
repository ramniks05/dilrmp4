package in.gov.dilrmp.Scheduler.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@Table(name = "map_digitization_report_view_backup")
public class MapDigitizationReportBackup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate backupDate;
    private Integer lgdCode;
    private String stateName;
    private Integer totalDistrict;
    private Integer totalVillages;
    private Integer totalTehsils;
    private Integer totalCadastralMaps;
    private Integer digitizedCadastralMaps;
    private Integer totalFmbs;
    private Integer digitizedFmbs;
    private Integer totalTippans;
    private Integer digitizedTippans;
    private Integer villagesLinkedWithRor;
    private String mapsUpdatedBasedOnMutation;
    private Integer villagesGeoreferenced;
    private Integer georeferencedMaps;
    private Integer totalMapsFmbTippans;
    private Integer totalDigitizedMapsFmbTippans;
    private Integer totalLandParcels;
    private Integer georeferencedLandParcels;
    private Integer villagesWithUlipn;
    private Integer landParcelsWithUlipn;
    private Double digitizedCadastralMapsPercent;
    private Double digitizedFmbsPercent;
    private Double digitizedTippansPercent;
    private BigDecimal digitizedMapsFmbTippansPercent;
    private BigDecimal villagesLinkedWithRorPercent;
    private BigDecimal villagesGeoreferencedPercent;
    private Double georeferencedMapsPercent;
    private Double georeferencedLandParcelsPercent;
    private BigDecimal villagesWithUlipnPercent;
    private Double landParcelsWithUlipnPercent;
    private Date mapsUpdatePeriod;
}
