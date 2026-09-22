function loadDistricts(stateId) {
        if (!stateId) return;

        // Fetch districts based on state ID
        fetch(`/ulb/districts/${stateId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Failed to fetch districts");
                }
                return response.json();
            })
            .then(districts => {
                const districtDropdown = document.getElementById("districtId");
                districtDropdown.innerHTML = ""; // Clear existing options
                districtDropdown.disabled = false;

                // Add a default option
                const defaultOption = document.createElement("option");
                defaultOption.value = "";
                defaultOption.textContent = "Select District";
                defaultOption.disabled = true;
                defaultOption.selected = true;
                districtDropdown.appendChild(defaultOption);
                 setDefaultFormValues();

                // Populate the dropdown with districts
                districts.forEach(district => {
                    const option = document.createElement("option");
                    option.value = district.districtId;
                    option.textContent = district.districtName;
                    districtDropdown.appendChild(option);
                });

                // Clear and disable ULB dropdown
                const ulbDropdown = document.getElementById("ulbId");
                ulbDropdown.innerHTML = "";
                ulbDropdown.disabled = true;
            })
            .catch(error => {
                 setDefaultFormValues();


            });
    }

 function loadUlbNames(districtId) {
     if (!districtId) return;

     const ulbDropdown = document.getElementById("ulbId");
     ulbDropdown.innerHTML = ""; // Clear existing options
     ulbDropdown.disabled = true; // Initially disable before fetch

     // Reset dependent form fields
     setDefaultFormValues();

     fetch(`/ulb/ulbName/${districtId}`)
         .then(response => {
             if (!response.ok) throw new Error("Failed to fetch ULB names");
             return response.json();
         })
         .then(ulbs => {
             ulbDropdown.disabled = false;

             // Add default option
             const defaultOption = document.createElement("option");
             defaultOption.value = "";
             defaultOption.textContent = "Select ULB";
             defaultOption.disabled = true;
             defaultOption.selected = true;
             ulbDropdown.appendChild(defaultOption);

             // Add ULB options
             ulbs.forEach(ulb => {
                 const option = document.createElement("option");
                 option.value = ulb.id;
                 option.textContent = ulb.ulbName;
                 ulbDropdown.appendChild(option);
             });
         })
         .catch(error => {
             console.error("Error loading ULBs:", error);
             // Optionally show error message
         });
 }


    function loadUlbDetails(ulbId) {
        if (!ulbId) {
            setDefaultFormValues();
            return;
        }

        fetch(`/ulb/ulb-details/${ulbId}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error("Failed to fetch ULB details");
                }
                return response.json();
            })
            .then(data => {
                function setValueById(id, value) {
                    const el = document.getElementById(id);
                    if (el) {
                        el.value = value;
                    } else {
                        console.warn(`Element with ID '${id}' not found.`);
                    }
                }

                setValueById("ulbMasterId", data.id || '');
                setValueById("officerName", data.officerName || '');
                setValueById("gdEmail", data.gdEmail || '');
                setValueById("zoneName", data.zoneName || '');
                setValueById("contractor", data.contractor || '');
                setValueById("technology", data.technology || '');
                setValueById("gdwing", data.gdwing || '');
                setValueById("soipackage", data.soipackage || '');
                setValueById("sanctionedArea", data.sanctionedArea || 0.00);
                setValueById("bufferAreaDataAcquisition", data.bufferAreaDataAcquisition || 0.00);

                if (data.updateOnDate) {
                    const dateObj = new Date(data.updateOnDate);
                    const date = dateObj.toISOString().slice(0, 10); // yyyy-MM-dd
                    const hours = String(dateObj.getHours()).padStart(2, '0');
                    const minutes = String(dateObj.getMinutes()).padStart(2, '0');
                    const time = `${hours}:${minutes}`; // HH:mm
                    setValueById("updateOnDate", `${date} ${time}`);
                } else {
                    setValueById("updateOnDate", '');
                }

                setValueById("dateOfCommecement", data.dateOfCommecement || '');
                setValueById("nakshaUploadedWebportal", data.nakshaUploadedWebportal || '');
                
                // New Excel Sheet Fields
                setValueById("oriDataUploaded", data.oriDataUploaded || '');
                setValueById("twoDFeDataUploaded", data.twoDFeDataUploaded || '');
                setValueById("surveyUnitUploadedByGd", data.surveyUnitUploadedByGd || '');

                // Cumulative Completed
                setValueById("tech1Cumulative", data.tech1Cumulative || '');
                setValueById("tech2Cumulative", data.tech2Cumulative || '');
                setValueById("tech3Cumulative", data.tech3Cumulative || '');
                setValueById("tech3LidarSensor", data.tech3LidarSensor || '');

                // Percentage Completed
                setValueById("tech1Percentage", data.tech1Percentage || '');
                setValueById("tech2Percentage", data.tech2Percentage || '');
                setValueById("tech3Percentage", data.tech3Percentage || '');
                setValueById("tech3LidarSensorPercentage", data.tech3LidarSensorPercentage || '');

                 // Remark and Status
                 setValueById("techStatus", data.techStatus || '');
                 setValueById("tech2ObliqueStatus", data.tech2ObliqueStatus || '');
                 setValueById("tech3ObliqueStatus", data.tech3ObliqueStatus || '');
                 setValueById("tech3LidarSensorStatus", data.tech3LidarSensorStatus || '');
                 setValueById("techTentativeDate", data.tentativeDate || '');
                 setValueById("techCompletionDate", data.completionDate || '');
                 setValueById("aerialAcquisirionRemark", data.aerialAcquisirionRemark || '');


                // Grid Completion
                setValueById("totalGrids", data.totalGrids || '');
                setValueById("gridsCompletedLastWeek", data.gridsCompletedLastWeek || '');
                setValueById("totalGridsCompleted", data.totalGridsCompleted || '');

               // Separate Data Processing (ORI)
               setValueById("separateOriDataProcessingLastWeek", data.separateOriDataProcessingLastWeek || '');
               setValueById("separateOriDataProcessingCumulative", data.separateOriDataProcessingCumulative || '');
               setValueById("separateOriDataProcessingPercentage", data.separateOriDataProcessingPercentage || '');
               setValueById("oriCumulativestatus", data.oriCumulativestatus || '');
               setValueById("oriCompletionDate", data.oriCompletionDate || '');
               setValueById("oriTentativeDate", data.oriTentativeDate || '');

               // Separate QA/QC Data Processing (ORI)
               setValueById("separateOriQaqcDataProcessingLastWeek", data.separateOriQaqcDataProcessingLastWeek || '');
               setValueById("separateOriQaqcDataProcessingCumulative", data.separateOriQaqcDataProcessingCumulative || '');
               setValueById("separateOriQaqcDataProcessingPercentage", data.separateOriQaqcDataProcessingPercentage || '');
               setValueById("oriCumulativeQaQcStatus", data.oriCumulativeQaQcStatus || '');
               setValueById("oriqaQcCompletionDate", data.oriqaQcCompletionDate || '');
               setValueById("oriqaQcTentativeDate", data.oriqaQcTentativeDate || '');

                // Data Processing (DSM)
                setValueById("dsmDataProcessingLastWeek", data.dsmDataProcessingLastWeek || '');
                setValueById("dsmDataProcessingCumulative", data.dsmDataProcessingCumulative || '');
                setValueById("dsmDataProcessingPercentage", data.dsmDataProcessingPercentage || '');
                setValueById("dsmSubmissionStatus", data.dsmSubmissionStatus || '');

                // QA/QC - Data Processing (DSM)
                setValueById("dsmDataProcessingQaqcLastWeek", data.dsmDataProcessingQaqcLastWeek || '');
                setValueById("dsmDataProcessingQaqcCumulative", data.dsmDataProcessingQaqcCumulative || '');
                setValueById("dsmDataProcessingQaqcPercentage", data.dsmDataProcessingQaqcPercentage || '');
                setValueById("dsmSubmissionQaQcStatus", data.dsmSubmissionQaQcStatus || '');

                // Data Processing (DTM)
                setValueById("dtmDataProcessingLastWeek", data.dtmDataProcessingLastWeek || '');
                setValueById("dtmDataProcessingCumulative", data.dtmDataProcessingCumulative || '');
                setValueById("dtmDataProcessingPercentage", data.dtmDataProcessingPercentage || '');
                setValueById("dtmSubmissionStatus", data.dtmSubmissionStatus || '');

                // QA/QC - Data Processing (DTM)
                setValueById("dtmDataProcessingQaqcLastWeek", data.dtmDataProcessingQaqcLastWeek || '');
                setValueById("dtmDataProcessingQaqcCumulative", data.dtmDataProcessingQaqcCumulative || '');
                setValueById("dtmDataProcessingQaqcPercentage", data.dtmDataProcessingQaqcPercentage || '');
                setValueById("dtmSubmissionQaQcStatus", data.dtmSubmissionQaQcStatus || '');

                // Data Processing (3D Mesh Model)
                setValueById("meshDataProcessingLastWeek", data.meshDataProcessingLastWeek || '');
                setValueById("meshDataProcessingCumulative", data.meshDataProcessingCumulative || '');
                setValueById("meshDataProcessingPercentage", data.meshDataProcessingPercentage || '');
                setValueById("threedMeshModelStatus", data.threedMeshModelStatus || '');

                // QA/QC - Data Processing (3D Mesh Model)
                setValueById("meshDataProcessingQaqcLastWeek", data.meshDataProcessingQaqcLastWeek || '');
                setValueById("meshDataProcessingQaqcCumulative", data.meshDataProcessingQaqcCumulative || '');
                setValueById("meshDataProcessingQaqcPercentage", data.meshDataProcessingQaqcPercentage || '');
                setValueById("threedMeshModelQaQcStatus", data.threedMeshModelQaQcStatus || '');


                 // 2D Feature Extraction (Vendor Submission & GD QA/QC Review)
                 setValueById("featureExtractionOriCompletedLastWeek", data.featureExtractionOriCompletedLastWeek || '');
                 setValueById("featureExtractionOriCumulativeCompleted", data.featureExtractionOriCumulativeCompleted || '');
                 setValueById("featureExtractionOriPercentageCompleted", data.featureExtractionOriPercentageCompleted || '');
                 setValueById("twoDfeatureextractionStatus", data.twoDfeatureextractionStatus || '');


                  // Feature Extraction QA/QC
                  setValueById("featureExtractionOriQaqcCompletedLastWeek", data.featureExtractionOriQaqcCompletedLastWeek || '');
                  setValueById("featureExtractionOriQaqcCumulativeCompleted", data.featureExtractionOriQaqcCumulativeCompleted || '');
                  setValueById("featureExtractionOriQaqcPercentageCompleted", data.featureExtractionOriQaqcPercentageCompleted || '');
                  setValueById("twoDfeatureextractionQaQcStatus", data.twoDfeatureextractionQaQcStatus || '');


                 //2D Feature Extraction Stereomode
                  setValueById("twoDFeatextrStereomodeLastWeek", data.twoDFeatextrStereomodeLastWeek || '');
                  setValueById("twoDFeatextrStereomodeCumulative", data.twoDFeatextrStereomodeCumulative || '');
                  setValueById("twoDFeatextrStereomodePercentage", data.twoDFeatextrStereomodePercentage || '');
                  setValueById("twoDFeatextrStereomodeStatus", data.twoDFeatextrStereomodeStatus || '');

                 // 2D Feature Extraction QA/QC Stereomode
                 setValueById("twoDFeatextrStereomodeLastWeekQaQc", data.twoDFeatextrStereomodeLastWeekQaQc || '');
                 setValueById("twoDFeatextrStereomodeCumulativeQaQc", data.twoDFeatextrStereomodeCumulativeQaQc || '');
                 setValueById("twoDFeatextrStereomodePercentageQaQc", data.twoDFeatextrStereomodePercentageQaQc || '');
                 setValueById("twoDFeatextrStereomodeQaQcStatus", data.twoDFeatextrStereomodeQaQcStatus || '');

                  setValueById("featureExtraction2DCompletionDate", data.featureExtraction2DCompletionDate || '');
                  setValueById("featureExtraction2DTentativeDate", data.featureExtraction2DTentativeDate || '');
                  setValueById("qaQc2DCompletionDate", data.qaQc2DCompletionDate || '');
                  setValueById("qaQc2DTentativeDate", data.qaQc2DTentativeDate || '');

                  setValueById("stereo2DCompletionDate", data.stereo2DCompletionDate || '');
                  setValueById("stereo2DTentativeDate", data.stereo2DTentativeDate || '');
                  setValueById("qaQcStereo2DCompletionDate", data.qaQcStereo2DCompletionDate || '');
                  setValueById("qaQcStereo2DTentativeDate", data.qaQcStereo2DTentativeDate || '');






                 //3D Feature Extraction
                 setValueById("threeDFeatextrLastWeek", data.threeDFeatextrLastWeek || '');
                 setValueById("threeDFeatextrCumulative", data.threeDFeatextrCumulative || '');
                 setValueById("threeDFeatextrPercentage", data.threeDFeatextrPercentage || '');
                  setValueById("threeDFeatextrStatus", data.threeDFeatextrStatus || '');

                 // 3D Feature Extraction QA/QC
                 setValueById("threeDFeatextrLastWeekQaQc", data.threeDFeatextrLastWeekQaQc || '');
                 setValueById("threeDFeatextrCumulativeQaQc", data.threeDFeatextrCumulativeQaQc || '');
                 setValueById("threeDDFeatextrPercentageQaQc", data.threeDDFeatextrPercentageQaQc || '');
                  setValueById("threeDFeatextrQaQcStatus", data.threeDFeatextrQaQcStatus || '');



                  //3D Feature Extraction Milestone 3B
                  setValueById("threeDMilestoneLastWeek", data.threeDMilestoneLastWeek || '');
                  setValueById("threeDmilestoneCumulative", data.threeDmilestoneCumulative || '');
                  setValueById("threeDmilestonePercentage", data.threeDmilestonePercentage || '');
                  setValueById("threeDmilestoneStatus", data.threeDmilestoneStatus || '');

                  // 3D Feature Extraction QA/QC Milestone 3B
                  setValueById("threeDmilestoneLastWeekQaQc", data.threeDmilestoneLastWeekQaQc || '');
                  setValueById("threeDmilestoneCumulativeQaQc", data.threeDmilestoneCumulativeQaQc || '');
                  setValueById("threeDmilestonePercentageQaQc", data.threeDmilestonePercentageQaQc || '');
                  setValueById("threeDmilestoneQaQcStatus", data.threeDmilestoneQaQcStatus || '');



                   setValueById("threeDFeatExtCompletionDate", data.threeDFeatExtCompletionDate || '');
                   setValueById("threeDFeatExtTentativeDate", data.threeDFeatExtTentativeDate || '');
                   setValueById("threeDFeatExtQaQcCompletionDate", data.threeDFeatExtQaQcCompletionDate || '');
                   setValueById("threeDFeatExtQaQcTentativeDate", data.threeDFeatExtQaQcTentativeDate || '');


                   setValueById("threeDMilestoneCompletionDate", data.threeDMilestoneCompletionDate || '');
                   setValueById("threeDMilestoneTentativeDate", data.threeDMilestoneTentativeDate || '');
                   setValueById("threeDMilestoneQaQcCompletionDate", data.threeDMilestoneQaQcCompletionDate || '');
                   setValueById("threeDMilestoneQaQcTentativeDate", data.threeDMilestoneQaQcTentativeDate || '');



                    //Milestone 2 Completion Date for Submission and QA/QC (Date in DD-MM-YY)
                    setValueById("milestone2TentativeDate", data.milestone2TentativeDate || '');
                    setValueById("milestone2CompletionDate", data.milestone2CompletionDate || '');

                    setValueById("qaQcmilestone2TentativeDate", data.qaQcmilestone2TentativeDate || '');
                    setValueById("qaQcmilestone2CompletionDate", data.qaQcmilestone2CompletionDate || '');



                // Existing Mappings
                setValueById("totalGC", data.totalGC || '');
                setValueById("oriReceivedBack", data.oriReceivedBack || '');
                setValueById("oriSubmittedToState", data.oriSubmittedToState || '');
                setValueById("remark", data.remark || '');

            })
            .catch(error => {
                console.error("Failed to load ULB details:", error);
                setDefaultFormValues();
            });
    }

    function setDefaultFormValues() {
        function setValueById(id, value) {
            const el = document.getElementById(id);
            if (el) {
                el.value = value;
            } else {
                console.warn(`Element with ID '${id}' not found.`);
            }
        }

        setValueById("ulbMasterId", "");
        setValueById("officerName", "");
        setValueById("gdEmail", "");
        setValueById("zoneName", "");
        setValueById("contractor", "");
        setValueById("technology", "");
        setValueById("gdwing", "");
        setValueById("soipackage", "");
        setValueById("sanctionedArea", 0.00);
        setValueById("bufferAreaDataAcquisition", 0.00);
        setValueById("dateOfCommecement", "");
        setValueById("nakshaUploadedWebportal", "");
        setValueById("updateOnDate", "");
        
        // New Excel Sheet Fields
        setValueById("oriDataUploaded", "");
        setValueById("twoDFeDataUploaded", "");
        setValueById("surveyUnitUploadedByGd", "");

       // Cumulative Completed
           setValueById("tech1Cumulative", "");
           setValueById("tech2Cumulative", "");
           setValueById("tech3Cumulative", "");
           setValueById("tech3LidarSensor", "");

           // Percentage Completed
           setValueById("tech1Percentage", "");
           setValueById("tech2Percentage", "");
           setValueById("tech3Percentage", "");
           setValueById("tech3LidarSensorPercentage", "");

           // Remarks & Status
           setValueById("techStatus", "");
           setValueById("tech2ObliqueStatus", "");
           setValueById("tech3ObliqueStatus", "");
           setValueById("tech3LidarSensorStatus", "");
           setValueById("aerialAcquisirionRemark", "");

           // Grid Completion
           setValueById("totalGrids", "");
           setValueById("gridsCompletedLastWeek", "");
           setValueById("totalGridsCompleted", "");

           // Separate Data Processing (ORI)
           setValueById("separateOriDataProcessingLastWeek", "");
           setValueById("separateOriDataProcessingCumulative", "");
           setValueById("separateOriDataProcessingPercentage", "");
           setValueById("oriCumulativestatus", "");
           setValueById("oriCompletionDate", "");
           setValueById("oriTentativeDate", "");

           // Separate QA/QC Data Processing (ORI)
           setValueById("separateOriQaqcDataProcessingLastWeek", "");
           setValueById("separateOriQaqcDataProcessingCumulative", "");
           setValueById("separateOriQaqcDataProcessingPercentage", "");
           setValueById("oriCumulativeQaQcStatus", "");
           setValueById("oriqaQcCompletionDate", "");
           setValueById("oriqaQcTentativeDate", "");


           // Data Processing (DSM)
           setValueById("dsmDataProcessingLastWeek", "");
           setValueById("dsmDataProcessingCumulative", "");
           setValueById("dsmDataProcessingPercentage", "");
           setValueById("dsmSubmissionStatus", "");

           // QA/QC - Data Processing (DSM)
           setValueById("dsmDataProcessingQaqcLastWeek", "");
           setValueById("dsmDataProcessingQaqcCumulative", "");
           setValueById("dsmDataProcessingQaqcPercentage", "");
           setValueById("dsmSubmissionQaQcStatus", "");

           // Data Processing (DTM)
           setValueById("dtmDataProcessingLastWeek", "");
           setValueById("dtmDataProcessingCumulative", "");
           setValueById("dtmDataProcessingPercentage", "");
           setValueById("dtmSubmissionStatus", "");

           // QA/QC - Data Processing (DTM)
           setValueById("dtmDataProcessingQaqcLastWeek", "");
           setValueById("dtmDataProcessingQaqcCumulative", "");
           setValueById("dtmDataProcessingQaqcPercentage", "");
           setValueById("dtmSubmissionQaQcStatus", "");

           // Data Processing (3D Mesh Model)
           setValueById("meshDataProcessingLastWeek", "");
           setValueById("meshDataProcessingCumulative", "");
           setValueById("meshDataProcessingPercentage", "");
           setValueById("threedMeshModelStatus", "");

           // QA/QC - Data Processing (3D Mesh Model)
           setValueById("meshDataProcessingQaqcLastWeek", "");
           setValueById("meshDataProcessingQaqcCumulative", "");
           setValueById("meshDataProcessingQaqcPercentage", "");
           setValueById("threedMeshModelQaQcStatus", "");

           // 2D Feature Extraction (Vendor Submission & GD QA/QC Review)
           setValueById("featureExtractionOriCompletedLastWeek", "");
           setValueById("featureExtractionOriCumulativeCompleted", "");
           setValueById("featureExtractionOriPercentageCompleted", "");
           setValueById("twoDfeatureextractionStatus", "");

           // Feature Extraction QA/QC
           setValueById("featureExtractionOriQaqcCompletedLastWeek", "");
           setValueById("featureExtractionOriQaqcCumulativeCompleted", "");
           setValueById("featureExtractionOriQaqcPercentageCompleted", "");
           setValueById("twoDfeatureextractionQaQcStatus", "");

           // 2D Feature Extraction Stereomode
           setValueById("twoDFeatextrStereomodeLastWeek", "");
           setValueById("twoDFeatextrStereomodeCumulative", "");
           setValueById("twoDFeatextrStereomodePercentage", "");
           setValueById("twoDFeatextrStereomodeStatus", "");

           // 2D Feature Extraction QA/QC Stereomode
           setValueById("twoDFeatextrStereomodeLastWeekQaQc", "");
           setValueById("twoDFeatextrStereomodeCumulativeQaQc", "");
           setValueById("twoDFeatextrStereomodePercentageQaQc", "");
           setValueById("twoDFeatextrStereomodeQaQcStatus", "");



           // 3D Feature Extraction
           setValueById("threeDFeatextrLastWeek", "");
           setValueById("threeDFeatextrCumulative", "");
           setValueById("threeDFeatextrPercentage", "");
           setValueById("threeDFeatextrStatus", "");

           // 3D Feature Extraction QA/QC
           setValueById("threeDFeatextrLastWeekQaQc", "");
           setValueById("threeDFeatextrCumulativeQaQc", "");
           setValueById("threeDDFeatextrPercentageQaQc", "");
           setValueById("threeDFeatextrQaQcStatus", "");

           // 3D Feature Extraction Milestone 3B
            setValueById("threeDMilestoneLastWeek", "");
            setValueById("threeDmilestoneCumulative", "");
            setValueById("threeDmilestonePercentage", "");
            setValueById("threeDmilestoneStatus", "");

            // 3D Feature Extraction QA/QC
            setValueById("threeDmilestoneLastWeekQaQc", "");
            setValueById("threeDmilestoneCumulativeQaQc", "");
            setValueById("threeDmilestonePercentageQaQc", "");
            setValueById("threeDmilestoneQaQcStatus", "");


            setValueById("milestone2TentativeDate", "");
            setValueById("milestone2CompletionDate", "");
            setValueById("qaQcmilestone2TentativeDate", "");
            setValueById("qaQcmilestone2CompletionDate", "");




        // Existing Mappings
        setValueById("totalGC", 0);
        setValueById("oriReceivedBack", "No");
        setValueById("oriSubmittedToState", "No");
        setValueById("remark", " ");
    }

         function validateDoubleInput(input) {
                const doubleRegex = /^-?\d*(\.\d{0,})?$/;
                const value = input.value;
                if (!doubleRegex.test(value)) {
                    input.value = value.slice(0, -1);
                }

            }


    /*===================3D Mesh Model form: Editing access to be restricted to ULBs under Tech 2 and Tech 3 only.
                         2D Feature Extraction (on ORI & Stereo Mode): Editing access to be restricted to ULBs under Tech 1 only.============================*/


    document.addEventListener("DOMContentLoaded", function () {
        const technologySelect = document.getElementById("technology");
        const ulbSelect = document.getElementById("ulbId");
        const districtSelect = document.getElementById("districtId");

        function applyReadonlyStyle(el, makeReadonly) {
            if (!el) return;
            const bg = "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAQAAAAECAYAAACp8Z5+AAAAIklEQVQIW2NkQAKrVq36zwjjgzhhYWGMYAEYB8RmROaABADeOQ8CXl/xfgAAAABJRU5ErkJggg==)";
            if (el.tagName === 'SELECT') {
                el.disabled = makeReadonly;
                el.style.backgroundImage = makeReadonly ? bg : "";
                el.style.backgroundRepeat = makeReadonly ? "repeat" : "";
            } else {
                el.readOnly = makeReadonly;
                el.style.backgroundImage = makeReadonly ? bg : "";
                el.style.backgroundRepeat = makeReadonly ? "repeat" : "";
            }
        }

        function updateFieldVisibility() {
            const selectedTech = technologySelect.value;

            // Mesh-specific fields
            const meshFields = [
                "meshDataProcessingLastWeek",
                "meshDataProcessingCumulative",
                "meshDataProcessingPercentage",
                "threedMeshModelStatus",
                "meshDataProcessingQaqcLastWeek",
                "meshDataProcessingQaqcCumulative",
                "meshDataProcessingQaqcPercentage",
                "threedMeshModelQaQcStatus"
            ];

            const tech1Fields = [
                "tech1Cumulative", "tech1Percentage", "techStatus",

                // Additional Tech 1 fields
                "twoDFeatextrStereomodeStatus",
                "twoDFeatextrStereomodePercentageQaQc",
                "twoDFeatextrStereomodeCumulativeQaQc",
                "twoDFeatextrStereomodeLastWeekQaQc",
                "twoDfeatureextractionQaQcStatus",
                "twoDFeatextrStereomodePercentage",
                "twoDFeatextrStereomodeCumulative",
                "twoDFeatextrStereomodeLastWeek",
                "twoDFeatextrStereomodeQaQcStatus",
                "featureExtractionOriQaqcPercentageCompleted",
                "featureExtractionOriQaqcCumulativeCompleted",
                "featureExtractionOriQaqcCompletedLastWeek",
                "twoDfeatureextractionStatus",
                "featureExtractionOriPercentageCompleted",
                "featureExtractionOriCumulativeCompleted",
                "featureExtractionOriCompletedLastWeek",

                 "featureExtraction2DCompletionDate",
                 "featureExtraction2DTentativeDate",

                 "featureExtraction2DCompletionDate",
                 "featureExtraction2DTentativeDate",

                 "qaQc2DCompletionDateDiv",
                 "qaQc2DTentativeDate",

                 "stereo2DCompletionDate",
                 "stereo2DTentativeDate",

                 "qaQcStereo2DCompletionDate",
                 "qaQcStereo2DTentativeDate",
            ];

            const tech2Fields = [
                "tech2Cumulative", "tech2Percentage", "tech2ObliqueStatus",
                ...meshFields
            ];

            const tech3Fields = [
                "tech3Cumulative", "tech3Percentage", "tech3ObliqueStatus",
                "tech3LidarSensor", "tech3LidarSensorPercentage", "tech3LidarSensorStatus",
                ...meshFields
            ];

            const allFields = [...tech1Fields, ...tech2Fields, ...tech3Fields];

            // Disable all fields first
            allFields.forEach(id => applyReadonlyStyle(document.getElementById(id), true));

            // Enable relevant fields only
            let enableFields = [];
            if (selectedTech === "Tech 1") enableFields = tech1Fields;
            else if (selectedTech === "Tech 2") enableFields = tech2Fields;
            else if (selectedTech === "Tech 3") enableFields = tech3Fields;

            enableFields.forEach(id => applyReadonlyStyle(document.getElementById(id), false));
        }

        // ULB change — disable everything, then re-evaluate
        ulbSelect.addEventListener("change", function () {
            const allFieldIds = [
                // All fields from all techs
                "tech1Cumulative", "tech1Percentage", "techStatus",
                "tech2Cumulative", "tech2Percentage", "tech2ObliqueStatus",
                "tech3Cumulative", "tech3Percentage", "tech3ObliqueStatus",
                "tech3LidarSensor", "tech3LidarSensorPercentage", "tech3LidarSensorStatus",

                // Tech 1 extra
                "twoDFeatextrStereomodeStatus",
                "twoDFeatextrStereomodePercentageQaQc",
                "twoDFeatextrStereomodeCumulativeQaQc",
                "twoDFeatextrStereomodeLastWeekQaQc",
                "twoDfeatureextractionQaQcStatus",
                "twoDFeatextrStereomodePercentage",
                "twoDFeatextrStereomodeCumulative",
                "twoDFeatextrStereomodeLastWeek",
                "twoDFeatextrStereomodeQaQcStatus",
                "featureExtractionOriQaqcPercentageCompleted",
                "featureExtractionOriQaqcCumulativeCompleted",
                "featureExtractionOriQaqcCompletedLastWeek",
                "twoDfeatureextractionStatus",
                "featureExtractionOriPercentageCompleted",
                "featureExtractionOriCumulativeCompleted",
                "featureExtractionOriCompletedLastWeek",

                "featureExtraction2DCompletionDate",
                "featureExtraction2DTentativeDate",

                "featureExtraction2DCompletionDate",
                "featureExtraction2DTentativeDate",

                "qaQc2DCompletionDateDiv",
                "qaQc2DTentativeDate",

                "stereo2DCompletionDate",
                "stereo2DTentativeDate",

                "qaQcStereo2DCompletionDate",
                "qaQcStereo2DTentativeDate",

                // Mesh fields for Tech 2/3
                "meshDataProcessingLastWeek",
                "meshDataProcessingCumulative",
                "meshDataProcessingPercentage",
                "threedMeshModelStatus",
                "meshDataProcessingQaqcLastWeek",
                "meshDataProcessingQaqcCumulative",
                "meshDataProcessingQaqcPercentage",
                "threedMeshModelQaQcStatus"
            ];
            allFieldIds.forEach(id => applyReadonlyStyle(document.getElementById(id), true));

            setTimeout(updateFieldVisibility, 300); // Optional delay
        });

        // District change → lock all fields
        districtSelect.addEventListener("change", function () {
            ulbSelect.dispatchEvent(new Event("change")); // reuse ULB logic
        });

        technologySelect.addEventListener("change", updateFieldVisibility);
        updateFieldVisibility(); // Initial run
    });





document.addEventListener('DOMContentLoaded', function () {
    function getById(id) {
        return document.getElementById(id);
    }

    function setupStatusSync(qaqcId, vendorId) {
        const qaqcSelect = getById(qaqcId);
        const vendorSelect = getById(vendorId);

        if (!qaqcSelect || !vendorSelect) return;

        // Function to update dropdown freeze state
        function updateDropdownFreezeState() {
            const qaqcValue = parseInt(qaqcSelect.value);
            // Values: 6 = ACCEPTED, 8 = ACCEPTED_AFTER_CORRECTION
            if (qaqcValue === 6 || qaqcValue === 8) {
                // Freeze the vendor dropdown using pointer-events instead of disabled
                vendorSelect.style.pointerEvents = 'none';
                vendorSelect.style.backgroundColor = '#e9ecef';
                vendorSelect.style.opacity = '0.6';
                vendorSelect.style.cursor = 'not-allowed';
                vendorSelect.title = 'This field is frozen because QA/QC status is Accepted or Accepted after Correction';
                vendorSelect.setAttribute('data-frozen', 'true');
            } else {
                // Unfreeze the vendor dropdown
                vendorSelect.style.pointerEvents = '';
                vendorSelect.style.backgroundColor = '';
                vendorSelect.style.opacity = '';
                vendorSelect.style.cursor = '';
                vendorSelect.title = '';
                vendorSelect.removeAttribute('data-frozen');
            }
        }

        qaqcSelect.addEventListener("change", function () {
            const value = parseInt(this.value);
            const map = {
                6: 3, // ACCEPTED → COMPLETED
                8: 5, // ACCEPTED_AFTER_CORRECTION → Completed post Correction
                7: 4  // RETURNED → UNDER_CORRECTION
            };

            if (map[value] !== undefined) {
                vendorSelect.value = map[value].toString();
            }
            
            // Update freeze state after value change
            updateDropdownFreezeState();
        });
        
        // Vendor → QAQC sync (new conditions)
        vendorSelect.addEventListener("change", function () {
            const value = parseInt(this.value);
            if (value === 1 || value === 2) {
                qaqcSelect.value = "1";
            }
        });
        

        // Check initial state on page load with delay to ensure form data is loaded
        setTimeout(function() {
            updateDropdownFreezeState();
        }, 100);
        
        // Also check when the form data is loaded (additional safety)
        setTimeout(function() {
            updateDropdownFreezeState();
        }, 500);
    }

    // Call function for each pair
    setupStatusSync("oriCumulativeQaQcStatus", "oriCumulativestatus");
    setupStatusSync("dsmSubmissionQaQcStatus", "dsmSubmissionStatus");
    setupStatusSync("dtmSubmissionQaQcStatus", "dtmSubmissionStatus");
    setupStatusSync("threedMeshModelQaQcStatus", "threedMeshModelStatus");
    setupStatusSync("twoDfeatureextractionQaQcStatus", "twoDfeatureextractionStatus");
    setupStatusSync("twoDFeatextrStereomodeQaQcStatus", "twoDFeatextrStereomodeStatus");
    setupStatusSync("threeDFeatextrQaQcStatus", "threeDFeatextrStatus");
    setupStatusSync("threeDmilestoneQaQcStatus", "threeDmilestoneStatus");

});

document.addEventListener('DOMContentLoaded', function () {
    const qaqcSelect = document.getElementById("oriCumulativeQaQcStatus");
    const completionDiv = document.getElementById("oriCompletionDateDiv");
    const tentativeDiv = document.getElementById("oriTentativeDateDiv");

    function toggleOriQaQcDateDivs(value) {
        const intVal = parseInt(value);
        if ([6, 8].includes(intVal)) {
            completionDiv.style.display = '';
            tentativeDiv.style.display = 'none';
        } else if (intVal === 7) {
            completionDiv.style.display = 'none';
            tentativeDiv.style.display = '';
        }
        // else: do nothing (retain current visibility)
    }

    if (qaqcSelect) {
        qaqcSelect.addEventListener("change", function () {
            toggleOriQaQcDateDivs(this.value);
        });

        // Initial run (optional)
        toggleOriQaQcDateDivs(qaqcSelect.value);
    }
});

//21. 2D Feature Extraction (For Tech 1 Submission By Vendor & QA/ QC By GD )

document.addEventListener('DOMContentLoaded', function () {
    // First 2D QAQC set
    const twoDQaqcSelect1 = document.getElementById("twoDfeatureextractionQaQcStatus");
    const twoDCompletionDiv1 = document.getElementById("featureExtraction2DCompletionDateDiv");
    const twoDTentativeDiv1 = document.getElementById("featureExtraction2DTentativeDateDiv");

    // Second 2D Stereo QAQC set
    const twoDQaqcSelect2 = document.getElementById("twoDFeatextrStereomodeQaQcStatus");
    const twoDCompletionDiv2 = document.getElementById("stereo2DCompletionDateDiv");
    const twoDTentativeDiv2 = document.getElementById("stereo2DTentativeDateDiv");

    // Generic toggle function
    function toggleQaQcDateDivs(value, completionDiv, tentativeDiv) {
        const intVal = parseInt(value);
        if ([6, 8].includes(intVal)) {
            completionDiv.style.display = '';
            tentativeDiv.style.display = 'none';
        } else if (intVal === 7) {
            completionDiv.style.display = 'none';
            tentativeDiv.style.display = '';
        }
        // else: do nothing
    }

    // Set up first select
    if (twoDQaqcSelect1) {
        twoDQaqcSelect1.addEventListener("change", function () {
            toggleQaQcDateDivs(this.value, twoDCompletionDiv1, twoDTentativeDiv1);
        });
        toggleQaQcDateDivs(twoDQaqcSelect1.value, twoDCompletionDiv1, twoDTentativeDiv1);
    }

    // Set up second select
    if (twoDQaqcSelect2) {
        twoDQaqcSelect2.addEventListener("change", function () {
            toggleQaQcDateDivs(this.value, twoDCompletionDiv2, twoDTentativeDiv2);
        });
        toggleQaQcDateDivs(twoDQaqcSelect2.value, twoDCompletionDiv2, twoDTentativeDiv2);
    }
});

// [22]. 3D Feature Extraction (Submission By Vendor & QA/QC By GD)

document.addEventListener('DOMContentLoaded', function () {
    // First 3D QAQC set: Feature Extraction
    const threeDQaqcSelect1 = document.getElementById("threeDFeatextrQaQcStatus");
    const threeDCompletionDiv1 = document.getElementById("threeDFeatExtCompletionDateDiv");
    const threeDTentativeDiv1 = document.getElementById("threeDFeatExtTentativeDateDiv");

    // Second 3D QAQC set: Milestone
    const threeDQaqcSelect2 = document.getElementById("threeDmilestoneQaQcStatus");
    const threeDCompletionDiv2 = document.getElementById("threeDMilestoneCompletionDateDiv");
    const threeDTentativeDiv2 = document.getElementById("threeDMilestoneTentativeDateDiv");

    // Generic toggle function for both sets
    function toggleQaQcDateDivs(value, completionDiv, tentativeDiv) {
        const intVal = parseInt(value);
        if ([6, 8].includes(intVal)) {
            completionDiv.style.display = '';
            tentativeDiv.style.display = 'none';
        } else if (intVal === 7) {
            completionDiv.style.display = 'none';
            tentativeDiv.style.display = '';
        }
        // else: do nothing
    }

    // Initialize and attach change listener for Feature Extraction 3D
    if (threeDQaqcSelect1) {
        threeDQaqcSelect1.addEventListener("change", function () {
            toggleQaQcDateDivs(this.value, threeDCompletionDiv1, threeDTentativeDiv1);
        });
        toggleQaQcDateDivs(threeDQaqcSelect1.value, threeDCompletionDiv1, threeDTentativeDiv1);
    }

    // Initialize and attach change listener for Milestone 3D
    if (threeDQaqcSelect2) {
        threeDQaqcSelect2.addEventListener("change", function () {
            toggleQaQcDateDivs(this.value, threeDCompletionDiv2, threeDTentativeDiv2);
        });
        toggleQaQcDateDivs(threeDQaqcSelect2.value, threeDCompletionDiv2, threeDTentativeDiv2);
    }
});




