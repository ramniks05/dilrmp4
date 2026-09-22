// Function to enable or disable the 'contectDetails' field based on 'nodalOfficerAppointed'
function toggleContactDetails() {
  const nodalOfficerAppointed = document.getElementById('nodalOfficerAppointed').value;

  const nodalOfficerNameField = document.getElementById('nodalOfficerName');
  const phoneNumberField = document.getElementById('phonenumber');
  const emailField = document.getElementById('email');

  console.log('toggleContactDetails called. Value:', nodalOfficerAppointed);

  if (nodalOfficerAppointed === 'Yes') {
    // Enable and require fields
    nodalOfficerNameField.disabled = false;
    nodalOfficerNameField.setAttribute("required", "true");

    phoneNumberField.disabled = false;
    phoneNumberField.setAttribute("required", "true");

    emailField.disabled = false;
    emailField.setAttribute("required", "true");

    console.log('Fields enabled');
  } else {
    // Disable and clear values
    nodalOfficerNameField.disabled = true;
    nodalOfficerNameField.value = '';
    nodalOfficerNameField.removeAttribute("required");

    phoneNumberField.disabled = true;
    phoneNumberField.value = '';
    phoneNumberField.removeAttribute("required");

    emailField.disabled = true;
    emailField.value = '';
    emailField.removeAttribute("required");

    console.log('Fields disabled and cleared');
  }
}


// Initialize state on page load
document.addEventListener('DOMContentLoaded', () => {
    toggleContactDetails();
});

// Function to fetch data and dynamically update the form
function fetchNakshaMISData(ulbId) {
    if (!ulbId) return; // Exit if no ULB is selected

    console.log('Fetching NakshaMISData for ULB ID:', ulbId);

    fetch(`/ulb/getNakshaMISData/${ulbId}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch NakshaMISData');
            }
            return response.json();
        })
        .then(data => {
            console.log('Data fetched successfully:', data);

            // Populate form fields dynamically
            document.getElementById('nakshaID').value = data.id || '';
            document.getElementById('nodalOfficerAppointed').value = data.nodalOfficerAppointed || '';
            document.getElementById('nodalOfficerName').value = data.nodalOfficerName || '';
            document.getElementById('phonenumber').value = data.phonenumber || '';
            document.getElementById('email').value = data.email || '';
            document.getElementById('totalSPMUPositionsSanctioned').value = data.totalSPMUPositionsSanctioned || '';
            document.getElementById('spmuRecruitmentCompleted').value = data.spmuRecruitmentCompleted || '';
            document.getElementById('totalProfessionalsRecruited').value = data.totalProfessionalsRecruited || '';
            document.getElementById('teamsFormedForsanctioned').value = data.teamsFormedForsanctioned || '';
            document.getElementById('teamsFormedForFieldSurvey').value = data.teamsFormedForFieldSurvey || '';
            document.getElementById('roversProcuredForFieldSurvey').value = data.roversProcuredForFieldSurvey || '';
            document.getElementById('roversSanctioned').value = data.roversSanctioned || '';
            document.getElementById('legalFrameworkUrbanSurveyStatus').value = data.legalFrameworkUrbanSurveyStatus || '';
            document.getElementById('legalFrameworkAmendmentStatus').value = data.legalFrameworkAmendmentStatus || '';
            document.getElementById('propertyTaxDataObtained').value = data.propertyTaxDataObtained || '';
            document.getElementById('propertyTaxDataDigitized').value = data.propertyTaxDataDigitized || '';
            document.getElementById('slcMeetingConducted').value = data.slcMeetingConducted || '';
            document.getElementById('slcMeetingDate').value = data.slcMeetingDate || '';
            document.getElementById('ulbsGroundTruthingCompleted').value = data.ulbsGroundTruthingCompleted || '';


            // Ensure the 'toggleContactDetails' function is called after populating fields
            toggleContactDetails();
        })
        .catch(error => console.error('Error fetching NakshaMISData:', error));
}




function onDistrictChange(districtId) {
    console.log("Selected District ID:", districtId);
     resetFormExceptDistrictAndUlb();

    // Fetch ULB data based on the selected district ID
    fetch(`/ulb/getUlbsByDistrict/${districtId}`)
        .then(response => response.json())
        .then(data => {
            const ulbSelect = document.getElementById("ulbSelect");

            // Clear existing options
            ulbSelect.innerHTML = '<option value="" disabled selected>Select Nodal Department</option>';

            // Populate the ULB dropdown
            data.forEach(ulb => {
                const option = document.createElement("option");
                option.value = ulb.id; // Assuming ULB ID is returned as `id`
                option.textContent = ulb.name; // Assuming ULB name is returned as `name`
                ulbSelect.appendChild(option);
            });
        })
        .catch(error => {
            console.error("Error fetching ULB data:", error);
        });
}

   function resetForm() {
        // Get the form element and reset it
        document.getElementById("ulbMisDataForm").reset(); // Make sure your form has the ID "myForm"
    }








function resetFormExceptDistrictAndUlb() {
    // Get the form element
    var form = document.getElementById("ulbMisDataForm");

    // Loop through all form elements and reset them, except for districtSelect and ulbSelect
    for (var i = 0; i < form.elements.length; i++) {
        var element = form.elements[i];

        // Check if the element is not one of the two select fields we want to skip
        if (element.id !== 'districtSelect' && element.id !== 'ulbSelect') {
            if (element.type === 'select-one' || element.type === 'select-multiple') {
                element.selectedIndex = 0; // Reset select elements
            } else if (element.type === 'text' || element.type === 'textarea' || element.type === 'password' || element.type === 'email' ||element.type === 'number'
                         || element.type==='hidden') {
                element.value = ''; // Reset text fields
            } else if (element.type === 'checkbox' || element.type === 'radio') {
                element.checked = false; // Reset checkboxes and radio buttons
            }
        }
    }
}
















