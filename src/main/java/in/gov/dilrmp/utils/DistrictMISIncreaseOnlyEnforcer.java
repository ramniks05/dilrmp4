package in.gov.dilrmp.utils;

import in.gov.dilrmp.models.dataEntryModel.DistrictMISDataEntry;

import java.util.function.Consumer;

/**
 * Enforces increase-only updates for restricted District MIS question fields (Q4–Q7, Q11–Q17, Q20, Q22–Q42).
 */
public final class DistrictMISIncreaseOnlyEnforcer {

    private DistrictMISIncreaseOnlyEnforcer() {
    }

    public static int enforce(DistrictMISDataEntry incoming, DistrictMISDataEntry existing, boolean allowDecrease) {
        if (allowDecrease || existing == null) {
            return 0;
        }
        int reverted = 0;
        reverted += enforceInteger(existing.getVillagesComputerizationCompleted(), incoming.getVillagesComputerizationCompleted(),
                incoming::setVillagesComputerizationCompleted);
        reverted += enforceInteger(existing.getTotalRoR(), incoming.getTotalRoR(), incoming::setTotalRoR);
        reverted += enforceInteger(existing.getRorComputerized(), incoming.getRorComputerized(), incoming::setRorComputerized);
        reverted += enforceInteger(existing.getTotalLandOwners(), incoming.getTotalLandOwners(), incoming::setTotalLandOwners);
        reverted += enforceInteger(existing.getTotalCadastralMaps(), incoming.getTotalCadastralMaps(), incoming::setTotalCadastralMaps);
        reverted += enforceInteger(existing.getDigitizedCadastralMaps(), incoming.getDigitizedCadastralMaps(), incoming::setDigitizedCadastralMaps);
        reverted += enforceInteger(existing.getTotalFMBs(), incoming.getTotalFMBs(), incoming::setTotalFMBs);
        reverted += enforceInteger(existing.getDigitizedFMBs(), incoming.getDigitizedFMBs(), incoming::setDigitizedFMBs);
        reverted += enforceInteger(existing.getTotalTippans(), incoming.getTotalTippans(), incoming::setTotalTippans);
        reverted += enforceInteger(existing.getDigitizedTippans(), incoming.getDigitizedTippans(), incoming::setDigitizedTippans);
        reverted += enforceInteger(existing.getVillagesLinkedWithRoR(), incoming.getVillagesLinkedWithRoR(), incoming::setVillagesLinkedWithRoR);
        reverted += enforceInteger(existing.getVillagesGeoreferenced(), incoming.getVillagesGeoreferenced(), incoming::setVillagesGeoreferenced);
        reverted += enforceInteger(existing.getTotalLandParcels(), incoming.getTotalLandParcels(), incoming::setTotalLandParcels);
        reverted += enforceInteger(existing.getGeoreferencedLandParcels(), incoming.getGeoreferencedLandParcels(), incoming::setGeoreferencedLandParcels);
        reverted += enforceInteger(existing.getVillagesWithULIPN(), incoming.getVillagesWithULIPN(), incoming::setVillagesWithULIPN);
        reverted += enforceInteger(existing.getLandParcelsWithULIPN(), incoming.getLandParcelsWithULIPN(), incoming::setLandParcelsWithULIPN);
        reverted += enforceInteger(existing.getMrrSanctioned(), incoming.getMrrSanctioned(), incoming::setMrrSanctioned);
        reverted += enforceInteger(existing.getMrrCompleted(), incoming.getMrrCompleted(), incoming::setMrrCompleted);
        reverted += enforceDouble(existing.getTotalRuralRevenueArea(), incoming.getTotalRuralRevenueArea(), incoming::setTotalRuralRevenueArea);
        reverted += enforceDouble(existing.getAreaSanctionedForSurvey(), incoming.getAreaSanctionedForSurvey(), incoming::setAreaSanctionedForSurvey);
        reverted += enforceInteger(existing.getVillagesDroneFlyingCompleted(), incoming.getVillagesDroneFlyingCompleted(), incoming::setVillagesDroneFlyingCompleted);
        reverted += enforceDouble(existing.getAreaDroneFlyingCompleted(), incoming.getAreaDroneFlyingCompleted(), incoming::setAreaDroneFlyingCompleted);
        reverted += enforceInteger(existing.getVillagesMap1Generated(), incoming.getVillagesMap1Generated(), incoming::setVillagesMap1Generated);
        reverted += enforceInteger(existing.getVillagesDraftMapPublished(), incoming.getVillagesDraftMapPublished(), incoming::setVillagesDraftMapPublished);
        reverted += enforceInteger(existing.getVillagesFinalPromulgationDone(), incoming.getVillagesFinalPromulgationDone(), incoming::setVillagesFinalPromulgationDone);
        reverted += enforceInteger(existing.getVillagesSurveySanctionNotStarted(), incoming.getVillagesSurveySanctionNotStarted(), incoming::setVillagesSurveySanctionNotStarted);
        reverted += enforceDouble(existing.getAreaSurveySanctionNotStarted(), incoming.getAreaSurveySanctionNotStarted(), incoming::setAreaSurveySanctionNotStarted);
        reverted += enforceInteger(existing.getTotalRevenueCourts(), incoming.getTotalRevenueCourts(), incoming::setTotalRevenueCourts);
        reverted += enforceInteger(existing.getRevenueCourtsComputerized(), incoming.getRevenueCourtsComputerized(), incoming::setRevenueCourtsComputerized);
        reverted += enforceInteger(existing.getVillagesWithRoRLinkedAadhaar(), incoming.getVillagesWithRoRLinkedAadhaar(), incoming::setVillagesWithRoRLinkedAadhaar);
        reverted += enforceInteger(existing.getVillagesWith100PercentRoRLinkedAadhaar(), incoming.getVillagesWith100PercentRoRLinkedAadhaar(), incoming::setVillagesWith100PercentRoRLinkedAadhaar);
        reverted += enforceInteger(existing.getRorLinkedWithAadhaar(), incoming.getRorLinkedWithAadhaar(), incoming::setRorLinkedWithAadhaar);
        reverted += enforceInteger(existing.getRorLinkedWithMobileNumber(), incoming.getRorLinkedWithMobileNumber(), incoming::setRorLinkedWithMobileNumber);
        reverted += enforceInteger(existing.getRorLinkedWithAddress(), incoming.getRorLinkedWithAddress(), incoming::setRorLinkedWithAddress);
        reverted += enforceInteger(existing.getLandOwnersAadhar(), incoming.getLandOwnersAadhar(), incoming::setLandOwnersAadhar);
        reverted += enforceInteger(existing.getLandOwnersLinkedWithMobile(), incoming.getLandOwnersLinkedWithMobile(), incoming::setLandOwnersLinkedWithMobile);
        reverted += enforceInteger(existing.getLandOwnersLinkedWithAddress(), incoming.getLandOwnersLinkedWithAddress(), incoming::setLandOwnersLinkedWithAddress);
        reverted += enforceInteger(existing.getLegacyTotalPages(), incoming.getLegacyTotalPages(), incoming::setLegacyTotalPages);
        reverted += enforceInteger(existing.getLegacyDigitisedStateFundsPages(), incoming.getLegacyDigitisedStateFundsPages(), incoming::setLegacyDigitisedStateFundsPages);
        reverted += enforceInteger(existing.getLegacyDigitisedDilrmpFundsPages(), incoming.getLegacyDigitisedDilrmpFundsPages(), incoming::setLegacyDigitisedDilrmpFundsPages);
        reverted += enforceInteger(existing.getLegacyTotalDigitisedPages(), incoming.getLegacyTotalDigitisedPages(), incoming::setLegacyTotalDigitisedPages);
        reverted += enforceInteger(existing.getRevenueLegacyTotalPages(), incoming.getRevenueLegacyTotalPages(), incoming::setRevenueLegacyTotalPages);
        reverted += enforceInteger(existing.getRevenueLegacyDigitisedStateFundsPages(), incoming.getRevenueLegacyDigitisedStateFundsPages(), incoming::setRevenueLegacyDigitisedStateFundsPages);
        reverted += enforceInteger(existing.getRevenueLegacyDigitisedDilrmpFundsPages(), incoming.getRevenueLegacyDigitisedDilrmpFundsPages(), incoming::setRevenueLegacyDigitisedDilrmpFundsPages);
        // Year is free to change (not increase-only); DoLR sanctioned is on the generic DoLR form
        return reverted;
    }

    private static int enforceInteger(Integer existingValue, Integer incomingValue, Consumer<Integer> setter) {
        int existing = existingValue == null ? 0 : existingValue;
        int incoming = incomingValue == null ? 0 : incomingValue;
        if (incoming < existing) {
            setter.accept(existingValue == null ? 0 : existingValue);
            return 1;
        }
        return 0;
    }

    private static int enforceDouble(Double existingValue, Double incomingValue, Consumer<Double> setter) {
        double existing = existingValue == null ? 0.0 : existingValue;
        double incoming = incomingValue == null ? 0.0 : incomingValue;
        if (incoming < existing) {
            setter.accept(existingValue == null ? 0.0 : existingValue);
            return 1;
        }
        return 0;
    }
}
