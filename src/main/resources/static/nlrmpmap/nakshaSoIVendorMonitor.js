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
                setDefaultDataTable();

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
                 setDefaultDataTable()


            });
    }

    function loadUlbNames(districtId) {
        if (!districtId) return;

        // Fetch ULB names based on district ID
        fetch(`/ulb/ulbName/${districtId}`)
            .then(response => {
                if (!response.ok) {
                   setDefaultDataTable();
                    throw new Error("Failed to fetch ULB names");
                }
                return response.json();
            })
            .then(ulbs => {
                const ulbDropdown = document.getElementById("ulbId");
                ulbDropdown.innerHTML = ""; // Clear existing options
                ulbDropdown.disabled = false;
                 setDefaultDataTable();

                // Add a default option
                const defaultOption = document.createElement("option");
                defaultOption.value = "";
                defaultOption.textContent = "Select ULB";
                defaultOption.disabled = true;
                defaultOption.selected = true;
                ulbDropdown.appendChild(defaultOption);

                // Populate the dropdown with ULBs
                ulbs.forEach(ulb => {
                    const option = document.createElement("option");
                    option.value = ulb.id;
                    option.textContent = ulb.ulbName;
                    ulbDropdown.appendChild(option);
                });
            })
            .catch(error => {
                setDefaultDataTable();

            });
    }

$(document).ready(function() {
    $('#ulbDetailsTable').DataTable({
        "language": {
            "emptyTable": "Data entry has not started yet." // ✅ Custom message for empty data
        }
    });
});


function fetchFilteredData() {
    let stateId = document.getElementById("stateId").value;
    let districtId = document.getElementById("districtId").value;
    let ulbMasterId = document.getElementById("ulbId").value;

    let queryParams = [];
    if (stateId) queryParams.push(`stateId=${stateId}`);
    if (districtId) queryParams.push(`districtId=${districtId}`);
    if (ulbMasterId) queryParams.push(`ulbMasterId=${ulbMasterId}`);

    let queryString = queryParams.length > 0 ? `?${queryParams.join("&")}` : "";

    $("#wait").css("display", "block"); // Show loader

    fetch(`/ulb/search${queryString}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server Error: ${response.status} ${response.statusText}`);
            }
            return response.text();
        })
        .then(text => {
            if (!text) {
                console.warn("Empty response received");
                return []; // ✅ Return empty array if response is empty
            }
            return JSON.parse(text);
        })
        .then(data => {
            var table = $('#ulbDetailsTable').DataTable();
            table.clear(); // ✅ Clear existing data

            $("#wait").css("display", "none"); // Hide loader

            if (data.length === 0) {
                console.warn("No data available, forcing empty table message");

                // ✅ Force redraw to trigger "emptyTable" message
                table.clear().draw();

                // ✅ If DataTables does not apply "emptyTable" message, manually insert row
                $('#ulbDetailsTable tbody').html('<tr><td colspan="15" class="text-center">Data entry has not started yet.</td></tr>');
            } else {
                $("#ulbDetailsTable tbody").empty(); // ✅ Ensure no previous empty message
                data.forEach((row, index) => {
                    table.row.add([
                        index + 1, // Serial Number (Starting from 1)
                        row.stateName || '',
                        row.district_Name || '',
                        row.ulbName || '',
                        row.sanctionedArea || 0,
                        row.groundControlCompleted || 'No',
                        row.droneFlyingArea || 0,
                        row.oriGenerated || 'No',
                        row.featureExtracted || 'No',
                        row.demDelivered || 'No',
                        row.threeDModelDelivered || 'No',
                        row.oriHandedOverToState || 'No',
                        row.oriReceivedBack || 'No',
                        row.oriSubmittedToState || 'No',
                        row.soiQAQC || 'No'
                    ]);
                });
                table.draw(); // ✅ Update table with data
            }
        })
        .catch(error => {
            console.error("Error fetching data:", error);
            $("#wait").css("display", "none"); // Hide loader on error
        });
}




function setDefaultDataTable() {
      var table = $('#ulbDetailsTable').DataTable();
      table.clear().draw(); // Clear table data
  }

  // Initialize DataTable on page load
  $(document).ready(function() {
      $('#ulbDetailsTable').DataTable();
  });


  function handleStateChange(stateId) {
      // Reset district and ULB dropdowns
      document.getElementById("districtId").innerHTML = '<option value="" disabled selected>Select District</option>';
      document.getElementById("districtId").disabled = false;

      document.getElementById("ulbId").innerHTML = '<option value="" disabled selected>Select ULB</option>';
      document.getElementById("ulbId").disabled = true;

      // Load new districts for selected state
      loadDistricts(stateId);

      // Fetch data for new state selection
      fetchFilteredData();
  }

  function handleDistrictChange(districtId) {
      // Reset ULB dropdown
      document.getElementById("ulbId").innerHTML = '<option value="" disabled selected>Select ULB</option>';
      document.getElementById("ulbId").disabled = false;

      // Load new ULBs for selected district
      loadUlbNames(districtId);

      // Fetch data for new district selection
      fetchFilteredData();
  }

  function handleUlbChange() {
      // Fetch data for selected ULB
      fetchFilteredData();
  }


