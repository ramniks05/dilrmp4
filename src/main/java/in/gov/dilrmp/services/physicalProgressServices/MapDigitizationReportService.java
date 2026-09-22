package in.gov.dilrmp.services.physicalProgressServices;

import in.gov.dilrmp.models.reportDTO.MapDigitizationReport.MapDigitizationReport;
import in.gov.dilrmp.models.reportDTO.clr.StateClrReportView;
import in.gov.dilrmp.models.reportDTO.mrr.MrrViewReport;
import in.gov.dilrmp.repositories.physicalProgressRepositories.MapDigitizationReportRepository;
import in.gov.dilrmp.utils.DateUtils;
import in.gov.dilrmp.utils.NumberFormatterUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MapDigitizationReportService {
    @Autowired
    MapDigitizationReportRepository reportRepository;
    private static final Logger logger = LoggerFactory.getLogger(MapDigitizationReportService.class);

    public List<MapDigitizationReport> getAllMapDigitizationReport(){

        List<MapDigitizationReport> mapDigitizationReports = reportRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"));
        mapDigitizationReports = mapDigitizationReports.stream()
                .filter(map -> !map.getLgdCode().equals(999))
                .collect(Collectors.toList());

        return mapDigitizationReports;
    }

    public List<MapDigitizationReport> getAllMapDigitizationReportFormate(){

        List<MapDigitizationReport> mapDigitizationReports = reportRepository.findAll(Sort.by(Sort.Direction.ASC, "stateName"));
        mapDigitizationReports = mapDigitizationReports.stream()
                .filter(map -> !map.getLgdCode().equals(999))
                .map(mapL -> {
                    mapL.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(mapL.getTotalDistrict()));
                    mapL.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(mapL.getTotalTehsils()));
                    mapL.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(mapL.getTotalVillages()));
                    mapL.setFormattingtotalCadastralMaps(NumberFormatterUtil.formatWithCommas(mapL.getTotalCadastralMaps()));
                    mapL.setFormattingdigitizedCadastralMaps(NumberFormatterUtil.formatWithCommas(mapL.getDigitizedCadastralMaps()));

                    //all pending columns:
                    mapL.setFormattingtotalFmbs(NumberFormatterUtil.formatWithCommas(mapL.getTotalFmbs()));
                    mapL.setFormattingdigitizedFmbs(NumberFormatterUtil.formatWithCommas(mapL.getDigitizedFmbs()));
                    mapL.setFormattingtotalTippans(NumberFormatterUtil.formatWithCommas(mapL.getTotalTippans()));
                    mapL.setFormattingdigitizedTippans(NumberFormatterUtil.formatWithCommas(mapL.getDigitizedTippans()));
                    mapL.setFormattingvillagesLinkedWithRor(NumberFormatterUtil.formatWithCommas(mapL.getVillagesLinkedWithRor()));
                    //mapL.setMapsUpdatedBasedOnMutation(NumberFormatterUtil.formatWithCommas(mapL.getMapsUpdatedBasedOnMutation()));
                    mapL.setFormattingvillagesGeoreferenced(NumberFormatterUtil.formatWithCommas(mapL.getVillagesGeoreferenced()));
                    mapL.setFormattinggeoreferencedMaps(NumberFormatterUtil.formatWithCommas(mapL.getGeoreferencedMaps()));
                    mapL.setFormattingtotalMapsFmbTippans(NumberFormatterUtil.formatWithCommas(mapL.getTotalMapsFmbTippans()));
                    mapL.setFormattingtotalDigitizedMapsFmbTippans(NumberFormatterUtil.formatWithCommas(mapL.getTotalDigitizedMapsFmbTippans()));
                    mapL.setFormattingtotalLandParcels(NumberFormatterUtil.formatWithCommas(mapL.getTotalLandParcels()));
                    mapL.setFormattinggeoreferencedLandParcels(NumberFormatterUtil.formatWithCommas(mapL.getGeoreferencedLandParcels()));
                    mapL.setFormattingvillagesWithUlipn(NumberFormatterUtil.formatWithCommas(mapL.getVillagesWithUlipn()));
                    mapL.setFormattinglandParcelsWithUlipn(NumberFormatterUtil.formatWithCommas(mapL.getLandParcelsWithUlipn()));

                    return mapL;
                })
                .collect(Collectors.toList());

        return mapDigitizationReports;
    }


    public List<MapDigitizationReport> getStateMapReportsGrandToatal() {
        List<MapDigitizationReport> mapStateDataByStateList=reportRepository.findAllByLgdCode(999);

        for (MapDigitizationReport report : mapStateDataByStateList) {
            Integer mapsUpdated = 0;
            if (report.getMapsUpdatedBasedOnMutation() != null) {
                try {
                    mapsUpdated = Integer.valueOf(report.getMapsUpdatedBasedOnMutation());
                } catch (NumberFormatException e) {
                    mapsUpdated = 0;
                }
            }

            Integer totalDistrict = report.getTotalDistrict() != null ? report.getTotalDistrict() : 0;
            if (totalDistrict > 0) {
                double percentageValue = (mapsUpdated * 100.0) / totalDistrict;
                String percentage = String.format("%.2f%%", percentageValue);
                report.setMapsUpdatedBasedOnMutationPercentage(percentage);
            } else {
                report.setMapsUpdatedBasedOnMutationPercentage("0.00%");
            }
        }

        return mapStateDataByStateList;
    }

    public List<MapDigitizationReport> getStateMapReportsGrandToatalFormated() {
        List<MapDigitizationReport> mapList=reportRepository.findAllByLgdCode(999);
        mapList = mapList.stream()
                .map(mapL -> {
                    mapL.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(mapL.getTotalDistrict()));
                    mapL.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(mapL.getTotalTehsils()));
                    mapL.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(mapL.getTotalVillages()));
                    mapL.setFormattingtotalCadastralMaps(NumberFormatterUtil.formatWithCommas(mapL.getTotalCadastralMaps()));
                    mapL.setFormattingdigitizedCadastralMaps(NumberFormatterUtil.formatWithCommas(mapL.getDigitizedCadastralMaps()));

                    //all pending columns:
                    mapL.setFormattingtotalFmbs(NumberFormatterUtil.formatWithCommas(mapL.getTotalFmbs()));
                    mapL.setFormattingdigitizedFmbs(NumberFormatterUtil.formatWithCommas(mapL.getDigitizedFmbs()));
                    mapL.setFormattingtotalTippans(NumberFormatterUtil.formatWithCommas(mapL.getTotalTippans()));
                    mapL.setFormattingdigitizedTippans(NumberFormatterUtil.formatWithCommas(mapL.getDigitizedTippans()));
                    mapL.setFormattingvillagesLinkedWithRor(NumberFormatterUtil.formatWithCommas(mapL.getVillagesLinkedWithRor()));
                    //mapL.setMapsUpdatedBasedOnMutation(NumberFormatterUtil.formatWithCommas(mapL.getMapsUpdatedBasedOnMutation()));
                    mapL.setFormattingvillagesGeoreferenced(NumberFormatterUtil.formatWithCommas(mapL.getVillagesGeoreferenced()));
                    mapL.setFormattinggeoreferencedMaps(NumberFormatterUtil.formatWithCommas(mapL.getGeoreferencedMaps()));
                    mapL.setFormattingtotalMapsFmbTippans(NumberFormatterUtil.formatWithCommas(mapL.getTotalMapsFmbTippans()));
                    mapL.setFormattingtotalDigitizedMapsFmbTippans(NumberFormatterUtil.formatWithCommas(mapL.getTotalDigitizedMapsFmbTippans()));
                    mapL.setFormattingtotalLandParcels(NumberFormatterUtil.formatWithCommas(mapL.getTotalLandParcels()));
                    mapL.setFormattinggeoreferencedLandParcels(NumberFormatterUtil.formatWithCommas(mapL.getGeoreferencedLandParcels()));
                    mapL.setFormattingvillagesWithUlipn(NumberFormatterUtil.formatWithCommas(mapL.getVillagesWithUlipn()));
                    mapL.setFormattinglandParcelsWithUlipn(NumberFormatterUtil.formatWithCommas(mapL.getLandParcelsWithUlipn()));

                    return mapL;
                })
                .collect(Collectors.toList());
        return mapList;
    }




    public List<MapDigitizationReport> getMapStateDataByStateId(Integer stateId) {
        List<MapDigitizationReport> mapStateDataByStateList = reportRepository.findAllByLgdCode(stateId);

        for (MapDigitizationReport report : mapStateDataByStateList) {
            Integer mapsUpdated = 0;
            if (report.getMapsUpdatedBasedOnMutation() != null) {
                try {
                    mapsUpdated = Integer.valueOf(report.getMapsUpdatedBasedOnMutation());
                } catch (NumberFormatException e) {
                    mapsUpdated = 0;
                }
            }

            Integer totalDistrict = report.getTotalDistrict() != null ? report.getTotalDistrict() : 0;
            if (totalDistrict > 0) {
                double percentageValue = (mapsUpdated * 100.0) / totalDistrict;
                String percentage = String.format("%.2f%%", percentageValue); // Formats to two decimal places and adds %
                report.setMapsUpdatedBasedOnMutationPercentage(percentage);
            } else {
                report.setMapsUpdatedBasedOnMutationPercentage("0.00%");
            }
        }

        return mapStateDataByStateList;
    }


    public List<MapDigitizationReport> filterAndSortStateClrReport(String parameter, String ascDesc) {
        List<MapDigitizationReport> mapDigitizationReports = reportRepository.findAll();
        Comparator<MapDigitizationReport> comparator = Comparator.comparing(MapDigitizationReport::getStateName);
        if ("digitizedMaps".equals(parameter)) {
            comparator = Comparator.comparing(MapDigitizationReport::getDigitizedCadastralMapsPercent);
        } else if ("cft_digitizedMaps".equals(parameter)) {
            comparator = Comparator.comparing(MapDigitizationReport::getDigitizedMapsFmbTippansPercent);
        }
        else if ("villageLinkRoR".equals(parameter)) {
            comparator = Comparator.comparing(MapDigitizationReport::getVillagesLinkedWithRorPercent);
        }
        //New Added
        else if ("GeoReferencedCadMapsFMBsTipp".equals(parameter)) {
            comparator = Comparator.comparing(MapDigitizationReport::getGeoreferencedMapsPercent);
        }
        else if ("GeoReferencedCadMapsFMBsTippVillages".equals(parameter)) {
            comparator = Comparator.comparing(MapDigitizationReport::getVillagesGeoreferencedPercent);
        }
        else if ("ULPINassignedVillage".equals(parameter)) {
            comparator = Comparator.comparing(MapDigitizationReport::getVillagesWithUlipnPercent);
        }

        else if ("GeoReferencedLandPar".equals(parameter)) {
            comparator = Comparator.comparing(MapDigitizationReport::getGeoreferencedLandParcelsPercent);
        }
        else if ("ULPINassignedLandPar".equals(parameter)) {
            comparator = Comparator.comparing(MapDigitizationReport::getLandParcelsWithUlipnPercent);
        }

        if ("DESC".equals(ascDesc)) {
            comparator = comparator.reversed();
        }
        return   mapDigitizationReports.stream()
                .filter(mapL -> !mapL.getLgdCode().equals(999))
                .map(mapL -> {
                    mapL.setFormattingTotalDistrict(NumberFormatterUtil.formatWithCommas(mapL.getTotalDistrict()));
                    mapL.setFormattingTotalTehsils(NumberFormatterUtil.formatWithCommas(mapL.getTotalTehsils()));
                    mapL.setFormattingTotalVillages(NumberFormatterUtil.formatWithCommas(mapL.getTotalVillages()));
                    mapL.setFormattingtotalCadastralMaps(NumberFormatterUtil.formatWithCommas(mapL.getTotalCadastralMaps()));
                    mapL.setFormattingdigitizedCadastralMaps(NumberFormatterUtil.formatWithCommas(mapL.getDigitizedCadastralMaps()));

                    //all pending columns:
                    mapL.setFormattingtotalFmbs(NumberFormatterUtil.formatWithCommas(mapL.getTotalFmbs()));
                    mapL.setFormattingdigitizedFmbs(NumberFormatterUtil.formatWithCommas(mapL.getDigitizedFmbs()));
                    mapL.setFormattingtotalTippans(NumberFormatterUtil.formatWithCommas(mapL.getTotalTippans()));
                    mapL.setFormattingdigitizedTippans(NumberFormatterUtil.formatWithCommas(mapL.getDigitizedTippans()));
                    mapL.setFormattingvillagesLinkedWithRor(NumberFormatterUtil.formatWithCommas(mapL.getVillagesLinkedWithRor()));
                    //mapL.setMapsUpdatedBasedOnMutation(NumberFormatterUtil.formatWithCommas(mapL.getMapsUpdatedBasedOnMutation()));
                    mapL.setFormattingvillagesGeoreferenced(NumberFormatterUtil.formatWithCommas(mapL.getVillagesGeoreferenced()));
                    mapL.setFormattinggeoreferencedMaps(NumberFormatterUtil.formatWithCommas(mapL.getGeoreferencedMaps()));
                    mapL.setFormattingtotalMapsFmbTippans(NumberFormatterUtil.formatWithCommas(mapL.getTotalMapsFmbTippans()));
                    mapL.setFormattingtotalDigitizedMapsFmbTippans(NumberFormatterUtil.formatWithCommas(mapL.getTotalDigitizedMapsFmbTippans()));
                    mapL.setFormattingtotalLandParcels(NumberFormatterUtil.formatWithCommas(mapL.getTotalLandParcels()));
                    mapL.setFormattinggeoreferencedLandParcels(NumberFormatterUtil.formatWithCommas(mapL.getGeoreferencedLandParcels()));
                    mapL.setFormattingvillagesWithUlipn(NumberFormatterUtil.formatWithCommas(mapL.getVillagesWithUlipn()));
                    mapL.setFormattinglandParcelsWithUlipn(NumberFormatterUtil.formatWithCommas(mapL.getLandParcelsWithUlipn()));
                    return mapL;
                })
                .sorted(comparator)
                .collect(Collectors.toList());
    }


}
