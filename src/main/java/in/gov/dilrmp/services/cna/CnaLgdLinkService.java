package in.gov.dilrmp.services.cna;

import in.gov.dilrmp.models.cna.CnaLgdLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class CnaLgdLinkService {

    /** PFMS Unique Code of CNA to the State/UT LGD code. */
    private static final Map<String, Integer> LINKS = new LinkedHashMap<>();

    static {
        LINKS.put("ANSA00000513", 35);
        LINKS.put("ARPA00002135", 12);
        LINKS.put("ALMIS", 18);
        LINKS.put("BIHARHS", 10);
        LINKS.put("CHCH00002765", 4);
        LINKS.put("TLHY00006921", 36);
        LINKS.put("CTRP00008551", 22);
        LINKS.put("GJGA00001814", 24);
        LINKS.put("DDDA00000261", 38);
        LINKS.put("MLWK00004216", 17);
        LINKS.put("NLDI00002206", 13);
        LINKS.put("ANDH123", 28);
        LINKS.put("GANG00001073", 30);
        LINKS.put("HRPK00001347", 6);
        LINKS.put("HPSH00003988", 2);
        LINKS.put("Jammukashmir", 1);
        LINKS.put("JHAR123", 20);
        LINKS.put("KLTV00002059", 32);
        LINKS.put("LALE00000450", 37);
        LINKS.put("LDLD00000259", 31);
        LINKS.put("MP-SAMITI", 23);
        LINKS.put("Mahabhoomi", 27);
        LINKS.put("NISTN", 33);
        LINKS.put("KARNLRMPPMU", 29);
        LINKS.put("23378/98", 21);
        LINKS.put("MNWI00002571", 14);
        LINKS.put("mizoram", 15);
        LINKS.put("UPLU00011253", 9);
        LINKS.put("westbengal", 19);
        LINKS.put("PYPO00001077", 34);
        LINKS.put("PUN1", 3);
        LINKS.put("RAJAS123", 8);
        LINKS.put("SKES00000493", 11);
        LINKS.put("DLIN00000259", 7);
        LINKS.put("tripurapmu", 16);
        LINKS.put("UKDILRMP", 5);
    }

    @Autowired
    private CnaLgdLinkRepository repository;

    @Transactional
    public void seedLinks() {
        for (Map.Entry<String, Integer> entry : LINKS.entrySet()) {
            if (!repository.existsByCnaCodeIgnoreCase(entry.getKey())) {
                CnaLgdLink link = new CnaLgdLink();
                link.setCnaCode(entry.getKey());
                link.setLgdCode(entry.getValue());
                repository.save(link);
            }
        }
    }

    public Map<String, CnaLgdLink> allByCode() {
        Map<String, CnaLgdLink> byCode = new HashMap<>();
        for (CnaLgdLink link : repository.findAll()) {
            byCode.put(normalize(link.getCnaCode()), link);
        }
        return byCode;
    }

    public static String normalize(String cnaCode) {
        return cnaCode == null ? "" : cnaCode.trim().toUpperCase(Locale.ROOT);
    }
}
