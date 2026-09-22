package in.gov.dilrmp.services.physicalProgressServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import in.gov.dilrmp.constants.ReportLabels;

import java.util.HashMap;
import java.util.Map;

@Service
public class StateMAPService {


    private Logger logger = LoggerFactory.getLogger(StateMAPService.class);




    public Map<String, String> createLabels() {
        Map<String, String> labels = new HashMap<>();
        labels.put("serialNumber", ReportLabels.SERIAL_NUMBER);
        labels.put("stateUT", ReportLabels.STATE_UT);
        labels.put("totalState", ReportLabels.TOTAL_STATE_UT);
        labels.put("numberOfStateWhereAvailable", ReportLabels.NUMBER_OF_STATE_WHERE);
        labels.put("districtName", ReportLabels.DISTRICT_NAME);
        labels.put("totalDistricts", ReportLabels.TOTAL_DISTRICTS);
        labels.put("totalTehsil", ReportLabels.TOTAL_TEHSILS);
        labels.put("totalVillage", ReportLabels.TOTAL_VILLAGES);
        labels.put("numberOfVillages", ReportLabels.NUMBER_OF_VILLAGES);
        labels.put("noOfCadastralMapsFmBsTippans", ReportLabels.NO_OF_CADASTRAL_MAPS_FMBS_TIPPANS);
        labels.put("cadastralMapsFmBsTippans", ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS);
        labels.put("cadastralMaps", ReportLabels.CADASTRAL_MAPS);
        labels.put("fmbs", ReportLabels.FMBs);
        labels.put("tippans", ReportLabels.TIPPANS);
        labels.put("total", ReportLabels.TOTAL);
        labels.put("digitized", ReportLabels.DIGITIZED);
        labels.put("cadastralMapsFmBsTippansLinkedToRor", ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_LINKED_TO_ROR);
        labels.put("cadastralMapsFmBsTippansGeoReferenced", ReportLabels.CADASTRAL_MAPS_FMBS_TIPPANS_GEO_REFERENCED);
        labels.put("ulpinAssigned", ReportLabels.ULPIN_ASSIGNED);
        labels.put("geoReferencedCadastralMapsFmBsTippans", ReportLabels.GEO_REFERENCED_CADASTRAL_MAPS_FMBS_TIPPANS);
        labels.put("noOfLandParcels", ReportLabels.NO_OF_LAND_PARCELS);
        labels.put("geoReferenced", ReportLabels.GEO_REFERENCED);
        labels.put("ulpinAssignedGeoReferenced", ReportLabels.ULPIN_ASSIGNED_GEO_REFERENCED);
        labels.put("number", ReportLabels.NO);
        labels.put("percentage", ReportLabels.PERCENTAGE);
        labels.put("reportName", ReportLabels.MAP_DIGITIZATION_REPORT);
        labels.put("reportNameforMapOny", ReportLabels.MAP_DIGITIZATION_REPORT_MAPFMBTIPPAN);
        labels.put("grandTotal", ReportLabels.GRAND_TOTAL);
        labels.put("updatedShowingCurrentOwnership", ReportLabels.SHOWING_CURRENT_OWNERSHIP);
        labels.put("noOfDistrictWhereCadastalMAPfmbTippanpdatedShowingCurrentOwnership", ReportLabels.NUMBER_OF_DISTRICT_WHERE_CADMAP_FMB_TIPPAN_SHOWING_CURRENT_OWNERSHIP);
        labels.put("yearUpWhichTheyAreUpdated", ReportLabels.IF_NO_YEAR_UPTO_WHICH_ARE_UPDATED);
        labels.put("YesNo", ReportLabels.YES_NO);


        return labels;
    }



}
