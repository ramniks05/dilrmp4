

//=============== Field teams (Sanctioned by DoLR) and Field team members trained till date comparison ================================
function fetchGroupedULBs(gdId) {
    $.ajax({
        url: '/chart/grouped-ulbs',
        method: 'GET',
        data: { gdId: gdId },
        success: function (data) {
            let modalBody = '';
            data.forEach(item => {
                modalBody += `
                    <strong>GD/Wing:</strong> ${item.gdName}<br/>
                    <strong>State:</strong> ${item.stateName}<br/>
                    <strong>ULBs:</strong> ${item.ulbNames}<hr/>
                `;
            });

            $('#ulbModalBody').html(modalBody);
            $('#ulbInfoModal').modal('show');
        },
        error: function () {
            alert('Failed to fetch detailed ULB info.');
        }
    });
}


$(document).ready(function () {
  let allCoEData = [];



  // Load chart data
  $.ajax({
    url: '/chart/coe-state-wise-summary',
    method: 'GET',
    beforeSend: () => $("#wait").show(),
    success: function (response) {
      $("#wait").hide();
      allCoEData = response;
      populateCoEDropdown(allCoEData);
    },
    error: () => {
      $("#wait").hide();
      $('#coeDonutChartWrapper').html('<p style="color:red;">Failed to load chart data.</p>');
    }
  });
$(document).ready(function () {
  let allCoEData = [];

  // Load chart data
  $.ajax({
    url: '/chart/coe-state-wise-summary',
    method: 'GET',
    beforeSend: () => $("#wait").show(),
    success: function (response) {
      $("#wait").hide();
      allCoEData = response;
      populateCoEDropdown(allCoEData); // also renders default
    },
    error: () => {
      $("#wait").hide();
      $('#coeDonutChartWrapper').html('<p style="color:red;">Failed to load chart data.</p>');
    }
  });

  // ✅ Populate CoE dropdown and set default chart
 function populateCoEDropdown(data) {
   const coeSelect = $('#coeSelect');
   coeSelect.empty(); // clear old options

   // Extract unique names and sort them alphabetically
   const uniqueNames = [...new Set(data.map(item => item.nameOfCoE))].sort();

   // Add default option
   coeSelect.append('<option value="">--- Select CoE ---</option>');

   // Append sorted unique names
   uniqueNames.forEach((name, index) => {
     const selected = index === 0 ? 'selected' : '';
     coeSelect.append(`<option value="${name}" ${selected}>${name}</option>`);
   });

   // Set chart type default to "bar"
   $('#chartTypeSelect').val('bar');

   // Trigger chart rendering for the first item
   if (uniqueNames.length > 0) {
     renderFilteredCharts(uniqueNames[0], 'bar');
   }
 }





$(document).on('change', '#coeSelect', function () {
  const selectedCoE = $(this).val();
  const currentHeader = $('.card-header').text().trim(); // current chart title

  if (selectedCoE && selectedCoE !== currentHeader) {
    renderFilteredCharts(selectedCoE);
  }
});

});



 // Populate dropdown
 function populateCoEDropdown(data) {
   const coeSelect = $('#coeSelect');
   const uniqueNames = [...new Set(data.map(item => item.nameOfCoE))];

   uniqueNames.forEach((name, index) => {
     const selected = index === 0 ? 'selected' : '';
     coeSelect.append(`<option value="${name}" ${selected}>${name}</option>`);
   });

   // Set chart type default to "bar"
   $('#chartTypeSelect').val('bar');

   // Trigger default chart rendering
   renderFilteredCharts(uniqueNames[0], 'bar');
 }


  // Handle dropdown change
  $('#chartTypeSelect, #coeSelect').on('change', function () {
    const selectedCoE = $('#coeSelect').val();
    const selectedType = $('#chartTypeSelect').val();
    if (selectedCoE && selectedType) {
      renderFilteredCharts(selectedCoE, selectedType);
    }
  });

  // Render chart
function renderFilteredCharts(selectedCoE) {
  const wrapper = $('#coeDonutChartWrapper');
  wrapper.empty();

  const filteredData = allCoEData.filter(item => item.nameOfCoE === selectedCoE);

  // 🔁 Get all unique CoE names for the dropdown
  const uniqueCoENames = [...new Set(allCoEData.map(item => item.nameOfCoE))].sort();

  filteredData.forEach((coeItem, index) => {
    const pieChartId = 'pie-chart-' + index;
    const barChartId = 'bar-chart-' + index;

    // ✅ Generate CoE dropdown HTML with options
    let coeDropdownHtml = `
      <div class="form-group row">
        <label class="col-form-label">Select CoE Name:</label>
        <div class="col-sm-6">
          <select class="w-200 form-control" id="coeSelect" style="height: 45px; padding: 8px 12px;">
            <option value="">--- Select CoE ---</option>`;

    uniqueCoENames.forEach(name => {
      const selectedAttr = name === selectedCoE ? 'selected' : '';
      coeDropdownHtml += `<option value="${name}" ${selectedAttr}>${name}</option>`;
    });

    // ✅ Now close </select> and add <hr> AFTER the loop


    coeDropdownHtml += `
          </select>
        </div>
      </div>

      <hr style="border-top: 2px solid #1797ff;">
      <br>
    `;

    // ✅ Append everything into the wrapper
    wrapper.append(`
      <div class="col-md-12 mb-4">
        <div class="card shadow">
          <h5>
            <div class="card-header text-white text-center" style="background: linear-gradient(45deg, #4099ff, #73b4ff);">
              ${coeItem.nameOfCoE}
            </div>
          </h4>
          <div class="card-body">
            ${coeDropdownHtml}
            <div class="row" >
              <div class="col-md-12">
                <h5 class="text-center font-weight-bold mb-2">Master Trainers Trained at CoEs</h5>
                 <br>
                <div id="${pieChartId}" style="width:100%; height:300px;"></div>
              </div>

            </div>
            <br>
          </div>
        </div>
      </div>
    `);


    const chartData = coeItem.data.map(item => ({
      name: item.state,
      y: item.fieldTeamsSanctioned,
      fieldTeamsSanctioned: item.fieldTeamsSanctioned,
      fieldTeamsTrained: item.fieldTeamsTrained,
      masterTrainers: item.masterTrainers
    }));

    render3DDonutChart(pieChartId, chartData);
    renderBarChart(barChartId, chartData);
  });
}






function render3DDonutChart(containerId, chartData) {
  const pieData = chartData.map(d => ({
    name: d.name,              // State name
    y: d.masterTrainers        // Slice size = masterTrainers
  }));

  Highcharts.chart(containerId, {
    colors: ['#4099ff', '#FF5370', '#2ed8b6', '#FFB64D', '#FE8A7D', '#69CEC6'],
    chart: {
      type: 'pie'
    },

    title: { text: '' },

    tooltip: {
      useHTML: true,
      formatter: function () {
        return `
          <b>${this.point.name}</b><br>
          Master Trainers: <b>${this.point.y}</b>
        `;
      }
    },

    plotOptions: {
      pie: {
        allowPointSelect: true,
        cursor: 'pointer',
        dataLabels: {
          enabled: true,
          format: '{point.name}: {point.y}',
          style: { fontSize: '12px' }
        }
      }
    },

    series: [{
      name: 'Master Trainers',
      colorByPoint: true,
      data: pieData
    }],

    responsive: {
      rules: [{
        condition: { maxWidth: 768 },
        chartOptions: {
          chart: { height: 300 },
          tooltip: { useHTML: false }
        }
      }]
    }
  });
}




function renderBarChart(containerId, chartData) {
  const container = document.getElementById(containerId);
  const canvas = document.createElement('canvas');
  container.innerHTML = ""; // clear old chart if re-rendering
  container.appendChild(canvas);
  const ctx = canvas.getContext('2d');

  // Define gradient color pairs
  const gradientPairs = [
    ['#4099ff', '#73b4ff'],  // Blue shades
    ['#FF5370', '#ff869a'],  // Red shades
    ['#2ed8b6', '#59e0c5'],  // Teal shades
    ['#FFB64D', '#ffcb80'],  // Orange shades
    ['#FE8A7D', '#feb8b0'],  // Coral shades
    ['#69CEC6', '#8fdbd5']   // Aqua shades
  ];

  // Helper to create gradient
  function createGradient(ctx, index) {
    const gradient = ctx.createLinearGradient(0, 0, 0, 300); // vertical gradient
    const [startColor, endColor] = gradientPairs[index % gradientPairs.length];
    gradient.addColorStop(0, startColor);
    gradient.addColorStop(1, endColor);
    return gradient;
  }

  // Create gradient arrays for each dataset
  const gradients1 = chartData.map((_, i) => createGradient(ctx, i));
  const gradients2 = chartData.map((_, i) => createGradient(ctx, (i + 3))); // shift colors for 2nd dataset

  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: chartData.map(d => d.name),
      datasets: [
        {
          label: 'Field Survey Teams Sanctioned ',
          data: chartData.map(d => d.fieldTeamsSanctioned),
          backgroundColor: gradients1
        },
        {
          label: 'Field Survey Officers Trained till Date',
          data: chartData.map(d => d.fieldTeamsTrained),
          backgroundColor: gradients2
        }
      ]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      aspectRatio: 2,
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            font: {
              weight: 'bold',
              size: 12
            }
          }
        },
        tooltip: {
          mode: 'index',
          intersect: false
        }
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'States',
            font: { weight: 'bold' }
          },
          ticks: {
            font: { weight: 'bold' }
          },
          stacked: false
        },
        y: {
          beginAtZero: true,
          title: {
            display: true,
            text: 'Total Count',
            font: { weight: 'bold' }
          },
          ticks: {
            font: { weight: 'bold' }
          }
        }
      }
    },
    plugins: [{
      id: 'bar-value-label',
      afterDatasetsDraw(chart) {
        const { ctx } = chart;
        chart.data.datasets.forEach((dataset, i) => {
          const meta = chart.getDatasetMeta(i);
          meta.data.forEach((bar, index) => {
            const value = dataset.data[index];
            if (value !== 0 && value != null) {
              ctx.save();
              ctx.fillStyle = 'black'; // values visible on gradients
              ctx.font = 'bold 12px Arial';
              ctx.textAlign = 'center';
              ctx.textBaseline = 'bottom';
              ctx.fillText(value, bar.x, bar.y - 2); // show above bar
              ctx.restore();
            }
          });
        });
      }
    }]
  });
}



});

$(document).ready(function () {
  $.ajax({
    url: '/chart/unique-coe-names',
    method: 'GET',
    beforeSend: function () {
      $('#wait').show(); // Optional loader
    },
    success: function (data) {
      $('#wait').hide();
      const coeSelect = $('#coeSelect');
      coeSelect.empty();
      coeSelect.append('<option value="">--- Select CoE ---</option>');

      data.forEach(coeName => {
        coeSelect.append(`<option value="${coeName}">${coeName}</option>`);
      });
    },
    error: function () {
      $('#wait').hide();
      alert('Failed to load CoE names.');
    }
  });
});

//====================================================================================================================================

$(document).ready(function () {
  // Load UI container
  // Load UI container
 $('#capacityContainer').html(`
   <div class="card shadow-sm mb-3">
     <div class="card-body">

       <!-- Header inside card -->
       <div class="label label-primary mb-3">
         <h5 class="m-t-20 text-center">Capacity Building</h5>
       </div>

       <!-- Dropdown inside card -->
       <div class="form-group row">
         <br>
         <label class="col-sm-2 col-form-label">Select State/UT:</label>
         <div class="col-sm-6">
           <select class="form-control w-100" id="stateSelect" style="height: 45px; padding: 8px 12px;">
             <option value="">--- All States/UTs ---</option>
           </select>
         </div>
       </div>

        <br>
 <hr style="border-top: 2px solid #1797ff;">
       <!-- Cards Row for clickable metrics -->
       <div class="row mt-4" id="cardsRow"></div>
        <br>
     </div>
   </div>

   <!-- Modal Wrapper (outside card) -->
   <div id="modalWrapper"></div>
 `);



  // Load State Dropdown
  fetch('/chart/state-names')
    .then(res => res.json())
    .then(data => {
      const select = $('#stateSelect');
      data.forEach(state => {
        select.append(`<option value="${state.id}">${state.name}</option>`);
      });

      // Default report load
      loadCapacityBuildingReport("");
    });

  // Change event to reload report
  $(document).on('change', '#stateSelect', function () {
    loadCapacityBuildingReport(this.value);
  });

  // Inject modal once
  $('#modalWrapper').html(getModalHtml());

  // Inject cards dynamically
  $('#cardsRow').html(getAllCardsHtml());

  // Attach card click handlers
  attachCardClickEvents();
});

function getModalHtml() {
  return `
    <div class="modal fade" id="oriModal" tabindex="-1" role="dialog" aria-labelledby="oriModalLabel" aria-hidden="true">
      <div class="modal-dialog modal-xl modal-dialog-centered" role="document" style="max-width: 90%; margin: auto;">
        <div class="modal-content">
          <div class="modal-header bg-info text-white">
            <h5 class="modal-title" id="oriModalLabel">Modal Title</h5>
            <a href="javascript:void(0);" class="btn btn-danger btn-round" data-bs-dismiss="modal" aria-label="Close">Close</a>
          </div>

          <div class="modal-body card">

            <!-- 🔍 Search by State -->
            <div class="mb-3">
              <input type="text" id="stateSearch" class="form-control" placeholder="Search by State name...">
            </div>

            <div class="table-responsive dataTables_wrapper dt-bootstrap4" style="max-height: calc(13 * 50px); overflow-y : auto; overflow-x: auto; border: 1px solid #ccc;">
              <table class="custom-grid" border="1" style="width: 100%; border-collapse: collapse; table-layout: auto;">
                <thead style="position: sticky; top: 0; background: linear-gradient(45deg, #4099ff, #73b4ff);">
                  <tr>
                    <th colspan="6" style="background-color: #1797ff;">
                      <h4 class="m-t-20" id="oriModalHeaderText" style="text-align: center; margin: 10px 0;">Header Title</h4>
                    </th>
                  </tr>
                  <tr id="oriModalTableHeader"></tr>
                </thead>
                <tbody id="ori-table-body"></tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>`;
}



// Load report data and inject HTML
function loadCapacityBuildingReport(stateId) {
    const wait = document.getElementById("wait");
    wait.style.display = "block";

    const url = stateId
        ? `/chart/capacity-building-report?stateId=${encodeURIComponent(stateId)}`
        : `/chart/capacity-building-report`;

    fetch(url)
        .then(response => response.json())
        .then(data => {
            wait.style.display = "none";

            if (!data) {
               /* document.getElementById('nigstMaster').innerHTML = "<strong>0</strong>";
                document.getElementById('coe').innerHTML = "<strong>0</strong>";*/
                document.getElementById('masterTrainers').innerHTML = "<strong>0</strong>";
                document.getElementById('fieldTeams').innerHTML = "<strong>0</strong>";
                document.getElementById('trainedTillDate').innerHTML = "<strong>0</strong>";
                document.getElementById('trainedFieldTeams').innerHTML = "<strong>0</strong>";
                document.getElementById('percentageTrainedTillDate').innerHTML = "<strong>0</strong>";
                return;
            }

            const report = data;

            /*document.getElementById('nigstMaster').innerHTML = `<i class="ti-shopping-cart f-left"></i><span><strong>${report.grandTotalNigstMasterTrainers || 0}</strong></span>`;
            document.getElementById('coe').innerHTML = `<i class="ti-shopping-cart f-left"></i><span><strong>${report.grandTotalCoeMasterTrainers || 0}</strong></span>`;*/
            document.getElementById('masterTrainers').innerHTML = `<i class="ti-shopping-cart f-left"></i><span><strong>${report.grandTotalTotalMasterTrainers || 0}</strong></span>`;
            document.getElementById('fieldTeams').innerHTML = `<i class="ti-shopping-cart f-left"></i><span><strong>${report.grandTotalFieldTeamsSanctioned || 0}</strong></span>`;
            document.getElementById('trainedTillDate').innerHTML = `<i class="ti-shopping-cart f-left"></i><span><strong>${report.grandTotalMembersToBeTrained || 0}</strong></span>`;
            document.getElementById('trainedFieldTeams').innerHTML = `<i class="ti-shopping-cart f-left"></i><span><strong>${report.grandTotalMembersTrained || 0}</strong></span>`;
           document.getElementById('percentageTrainedTillDate').innerHTML = `
               <i class="ti-shopping-cart f-left"></i>
               <span><strong>${(report.percentageTrained || 0).toFixed(2)} % </strong></span>
           `;



        })
        .catch(error => {
            wait.style.display = "none";
            console.error("Error fetching report:", error);
        });
}

document.addEventListener('DOMContentLoaded', () => {
    fetch('/chart/state-names')
        .then(response => response.json())
        .then(data => {
            const select = document.getElementById('stateSelect');
            data.forEach(state => {
                const option = document.createElement('option');
                option.value = state.id;
                option.textContent = state.name;
                select.appendChild(option);
            });

            // Load default report
            loadCapacityBuildingReport("");
        })
        .catch(error => console.error("Error loading states:", error));

    // Add change event listener
    document.getElementById('stateSelect').addEventListener('change', function () {
        loadCapacityBuildingReport(this.value);
    });
});
function fetchModalTableData(apiUrl, modalId, title, subHeader, columnHeaders, dataKeys, tableBodyId = 'ori-table-body') {
    const stateId = $('#stateSelect').val();
    $.ajax({
        url: `${apiUrl}?stateId=${encodeURIComponent(stateId)}`,
        method: 'GET',
        success: function (data) {
            // Set modal title and header
            $(`#${modalId} #oriModalLabel`).text(title);
            $(`#${modalId} #oriModalHeaderText`).text(subHeader);

            // Set table headers
            let headerRow = '';
            columnHeaders.forEach((col, index) => {
                headerRow += index === 0
                    ? `<th>${col}</th>`
                    : `<th class="sortable" data-column="${index}">${col}</th>`;
            });
            $('#oriModalTableHeader').html(headerRow);

            enableSortableHeaders();

            // Populate table body
            const tbody = $(`#${tableBodyId}`);
            tbody.empty();

            data.forEach((item, index) => {
                let row = `<tr><td>${index + 1}</td>`;
                dataKeys.forEach(key => {
                    row += `<td>${item[key] != null ? item[key] : ''}</td>`;
                });
                row += '</tr>';
                tbody.append(row);
            });

            // Row and column highlighting
            const rows = document.querySelectorAll(`#${tableBodyId} tr`);
            rows.forEach(row => {
                const cells = row.querySelectorAll('td');
                cells.forEach((cell, colIndex) => {
                    cell.addEventListener('mouseenter', () => {
                        row.classList.add('highlight-row');
                        rows.forEach(r => {
                            const colCell = r.children[colIndex];
                            if (colCell) colCell.classList.add('highlight-col');
                        });
                        cell.classList.add('hover-cell');
                    });
                    cell.addEventListener('mouseleave', () => {
                        row.classList.remove('highlight-row');
                        rows.forEach(r => {
                            const colCell = r.children[colIndex];
                            if (colCell) colCell.classList.remove('highlight-col');
                        });
                        cell.classList.remove('hover-cell');
                    });
                });
            });
        },
        error: function () {
            $(`#${tableBodyId}`).html('<tr><td colspan="6">Error loading data</td></tr>');
        }
    });
}


function getAllCardsHtml() {
  const cards = [

    { id: "masterTrainersClick", color: "bg-c-green", title: "Master Trainers Trained till Date ", countId: "masterTrainers" },
    { id: "fieldTeamsClick", color: "bg-c-yellow", title: "Field Survey Teams Sanctioned", countId: "fieldTeams" },
    { id: "trainedDataClick", color: "bg-c-orenge", title: "Field Survey Officers to be trained", countId: "trainedFieldTeams" },
    { id: "beTrainedDataClick", color: "bg-c-lite-green", title: "Field Survey Officers Trained till Date", countId: "trainedTillDate" },
    { id: "bePercentageTrainedClick", color: "bg-c-orenge", title: "Percentage of Field Survey Officers Trained till Date", countId: "percentageTrainedTillDate" },
  ];

  return cards.map(card => `
    <div class="col-lg-4 col-xl-2 col-sm-6">
      <a href="javascript:void(0);" id="${card.id}" data-bs-toggle="modal" data-bs-target="#oriModal">
        <div class="badge-box ${card.color} order-card shadow-card">
          <div><h5>${card.title}</h5></div>
          <div><h2 id="${card.countId}"><i class="ti-shopping-cart f-left"></i><span><strong>0</strong></span></h2></div>
        </div>
      </a>
    </div>`).join('');
}

function attachCardClickEvents() {
  $('#oriTitleClick').click(() => {
    fetchModalTableData('/chart/nigst-data', 'oriModal', '', 'Master Trainers Trained till Date ',
      ['S.No.', 'State/UT', 'Name of CoE', 'Master Trainers Trained till Date'],
      ['stateName', 'nameOfCoE', 'nigstMasterTrainers']);
  });

  $('#coeClick').click(() => {
    fetchModalTableData('/chart/coe-data', 'oriModal', '', 'CoE ULB Level Master Trainers Trained',
      ['S.No.', 'State/UT', 'Name of CoE', 'CoE ULB Level Master Trainers Trained'],
      ['stateName', 'nameOfCoE', 'coeMasterTrainers']);
  });

  $('#masterTrainersClick').click(() => {
    fetchModalTableData('/chart/master-trainer-data', 'oriModal', '', 'Master Trainers Trained till Date ',
      ['S.No.', 'State/UT', 'Name of CoE', 'Master Trainers Trained till Date '],
      ['stateName', 'nameOfCoE', 'totalMasterTrainers']);
  });

  $('#fieldTeamsClick').click(() => {
    fetchModalTableData('/chart/field-teams-data', 'oriModal', '', 'Field Survey Teams Sanctioned',
      ['S.No.', 'State/UT', 'Name of CoE', 'Field Survey Teams Sanctioned'],
      ['stateName', 'nameOfCoE', 'fieldTeamsSanctioned']);
  });

  $('#trainedDataClick').click(() => {
    fetchModalTableData('/chart/trained-data', 'oriModal', '', 'Field Survey Officers to be trained',
      ['S.No.', 'State/UT', 'Name of CoE', 'Officers to be trained'],
      ['stateName', 'nameOfCoE', 'membersTrained']);
  });

  $('#beTrainedDataClick').click(() => {
    fetchModalTableData('/chart/to-be-trained-data', 'oriModal', '', 'Field Survey Officers Trained till Date',
      ['S.No.', 'State/UT', 'Name of CoE', 'Officers Trained till Date'],
      ['stateName', 'nameOfCoE', 'membersToBeTrained']);
  });

  $('#bePercentageTrainedClick').click(() => {
      fetchModalTableData('/chart/to-be-percentage-trained-data', 'oriModal', '', 'Percentage of Field Survey Officers Trained till Dated',
        ['S.No.', 'State/UT', 'Name of CoE','Officers to be trained', 'Officers Trained till Date','Percentage(%)'],
        ['stateName', 'nameOfCoE','membersTrained', 'membersToBeTrained','percentageTrained']);
    });
}

$(document).ready(function () {

$('#capacityContainerTable').append(`
  <div class="card shadow-sm mb-4">
    <div class="card-body" style="border: 1px solid #ccc;">
      <table class="custom-grid" border="1" style="width: 100%; border-collapse: collapse; table-layout: fixed;">
        <thead>
          <tr>
            <th colspan="15" style="background-color: #1797ff;">
              <div style="display: flex; justify-content: space-between; align-items: center; padding: 10px;">
                <input type="text" id="searchCapacity" placeholder="Search by State/UT"
                       class="form-control" style="width: 250px; margin-right: 15px;">
                <h4 class="m-t-20" style="margin: 0; color: white;">Capacity Building</h4>
                <div>
                  <a href="/physcial/report/pdf/capacity-building">
                    <img src="/images/pdf.png" alt="PDF Icon" class="btn-img" width="40" height="40"
                         style="margin-right: 10px; box-shadow: rgba(0,0,0,0.25) 0px 0.0625em 0.0625em,
                                                      rgba(0,0,0,0.25) 0px 0.125em 0.5em,
                                                      rgba(255,255,255,0.1) 0px 0px 0px 1px inset;" />
                  </a>
                  <a href="/physcial/report/excel/capacity-building">
                    <img src="/images/excel.png" alt="Excel Icon" class="btn-img" width="40" height="40"
                         style="box-shadow: rgba(0,0,0,0.25) 0px 0.0625em 0.0625em,
                                        rgba(0,0,0,0.25) 0px 0.125em 0.5em,
                                        rgba(255,255,255,0.1) 0px 0px 0px 1px inset;">
                  </a>
                </div>
              </div>
            </th>
          </tr>
           <tr>
               <th colspan="11">Capacity Building</th>
               <th colspan="4">IEC Activity</th>

         </tr>
          <tr>
            <th>S.No.</th>
            <th>State/UT</th>
            <th>Name of CoE</th>
            <th>Master Trainers Trained till Date</th>
            <th>CoE ULB Level Master Trainers Trained</th>
            <th>Total Master Trainers Trained</th>
            <th>Field Survey Teams Sanctioned by DoLR</th>
            <th>Officers to be trained</th>
            <th>Field Survey Officers Trained Till Date</th>
            <th>Total No. of Field Teams Trained till date</th>
            <th>Completion Percentage of Field Training (%)</th>
            <th>IEC Material Status </th>
            <th>Media Uploaded</th>
            <th>IEC Activity Conducted</th>
            <th>IEC Activity Status</th>
          </tr>
        </thead>
      </table>

      <div style="max-height: 400px; overflow-y: auto;">
        <table class="custom-grid" border="1" style="width: 100%; border-collapse: collapse; table-layout: fixed;">
          <tbody id="grid-table-body"></tbody>
        </table>
        <br>
        <hr class="sub-title">
      </div>
    </div>
  </div>
`);
$(document).on("input", "#searchCapacity", function () {
    const query = $(this).val().toLowerCase();

    $("#grid-table-body tr").filter(function () {
        const stateName = $(this).find("td:nth-child(2)").text().toLowerCase(); // 2nd column = State/UT
        $(this).toggle(stateName.includes(query));
    });
});


$.ajax({
  url: '/chart/capacity-building/entries',
  method: 'GET',
  success: function (data) {
    let tbody = $('#grid-table-body');
    tbody.empty();

    data.forEach(function (item, index) {
      let row = `<tr>
          <td>${index + 1}</td>
          <td>${item.stateName || ''}</td>
          <td>${item.nameOfCoE || ''}</td>
          <td>${item.nigstMasterTrainers || 0}</td>
          <td>${item.coeMasterTrainers || 0}</td>
          <td>${item.totalMasterTrainers || 0}</td>
          <td>${item.fieldTeamsSanctioned || 0}</td>
          <td>${item.membersTrained || 0}</td>
          <td>${item.membersToBeTrained || 0}</td>
          <td>${item.fieldTeamsTrained != null ? Number(item.fieldTeamsTrained).toFixed(2) : '0.00'}</td>
          <td>${item.percentageTrained != null ? Number(item.percentageTrained).toFixed(2) : '0.00%'}</td>
          <td>${item.iecMaterialStatus || ''}</td>
          <td>${item.iecMediaType || ''}</td>
          <td>${item.iecMediaVariety || ''}</td>
          <td>${item.iecActivityStatus || ''}</td>
        </tr>`;
      tbody.append(row);
    });

    // Optional: Cell highlight effect
    const rows = document.querySelectorAll('#grid-table-body tr');
    rows.forEach(row => {
      const cells = row.querySelectorAll('td');
      cells.forEach((cell, colIndex) => {
        cell.addEventListener('mouseenter', () => {
          row.classList.add('highlight-row');
          rows.forEach(r => {
            const colCell = r.children[colIndex];
            if (colCell) colCell.classList.add('highlight-col');
          });
          cell.classList.add('hover-cell');
        });
        cell.addEventListener('mouseleave', () => {
          row.classList.remove('highlight-row');
          rows.forEach(r => {
            const colCell = r.children[colIndex];
            if (colCell) colCell.classList.remove('highlight-col');
          });
          cell.classList.remove('hover-cell');
        });
      });
    });
  },
  error: function () {

  }
});
});

// ============ Capacity Building of Data Processing: Field Teams (Sanctioned by DoLR) and Field Team Members Trained to Date ==============

$(document).ready(function () {
  $.ajax({
    url: '/chart/capacity-building-state-summary',
    method: 'GET',
    beforeSend: function () {
      $("#wait").show();
    },
    success: function (data) {
      $("#wait").hide();

      // 🔁 Dynamically inject HTML
      const wrapper = $('#coeDonutChartWrapper1');
      wrapper.empty().append(`
        <div class="card" >
          <div class="card-body" >
            <div class="label label-primary">
              <h5 class="m-t-20 text-center">
                Field Survey Officers To Be Trained vs Field Survey Officers Trained Till Date
              </h5>
            </div>
            <br>
            <hr style="border-top: 2px solid #1797ff;">
            <br>
            <div class="chart-container" style="position: relative; height: 600px; width: 100%;">
              <canvas id="capacityBuildingStateChart"></canvas>
            </div>
          </div>
        </div>
      `);

      const stateLabels = [];
      const teams = [], trained = [], toBeTrained = [];

      data.forEach(item => {
        stateLabels.push(item.state);
        teams.push(item.trained || 0);
        trained.push(item.trained || 0);
        toBeTrained.push(item.toBeTrained || 0);

      });

      const datasets = [
        {
          label: 'Field Survey Officer To Be Trained',
          data: teams,
          backgroundColor:  ['#69CEC6', '#8fdbd5'], // Aqua
        },
        {
          label: 'Field Survey Officers Trained Till Date',
          data: toBeTrained,
          backgroundColor: ['#FFB64D', '#ffcb80']
        }
      ];

      renderStackedBarChart('capacityBuildingStateChart', stateLabels, datasets);
    },
    error: function () {
      $("#wait").hide();
      $('#coeDonutChartWrapper').html("<p style='color: red;'>Failed to load data.</p>");
    }
  });
});
function renderStackedBarChart(canvasId, labels, datasets) {
  const ctx = document.getElementById(canvasId).getContext('2d');

  // ✅ Assign colors automatically if not given
  const colors = [
    'rgba(54, 162, 235, 0.7)',   // Blue
    'rgba(255, 99, 132, 0.7)',   // Red
    'rgba(255, 206, 86, 0.7)',   // Yellow
    'rgba(75, 192, 192, 0.7)',   // Teal
    'rgba(153, 102, 255, 0.7)',  // Purple
    'rgba(255, 159, 64, 0.7)'    // Orange
  ];

  datasets.forEach((ds, i) => {
    if (!ds.backgroundColor) {
      ds.backgroundColor = colors[i % colors.length];
    }
    ds.stack = 'Stack 0'; // ✅ keep stacking consistent
  });

  new Chart(ctx, {
    type: 'bar',
    data: {
      labels: labels,
      datasets: datasets
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        tooltip: {
          mode: 'index',
          intersect: false
        },
        legend: {
          position: 'bottom',
          labels: {
            boxWidth: 12,
            font: {
              weight: 'bold',   // ✅ Bold legend
              size: 12
            }
          }
        }
      },
      scales: {
        x: {
          stacked: true,
          title: {
            display: true,
            text: 'States',
            font: {
              weight: 'bold'
            }
          },
          ticks: {
            maxRotation: 45,
            minRotation: 30,
            autoSkip: true,
            font: {
              weight: 'bold'
            }
          }
        },
        y: {
          stacked: true,
          beginAtZero: true,
          title: {
            display: true,
            text: 'Total Count',
            font: {
              weight: 'bold'
            }
          },
          ticks: {
            font: {
              weight: 'bold'
            }
          }
        }
      }
    },
    plugins: [{
      id: 'bar-value-label',
      afterDatasetsDraw(chart) {
        const { ctx } = chart;
        chart.data.datasets.forEach((dataset, i) => {
          const meta = chart.getDatasetMeta(i);
          meta.data.forEach((bar, index) => {
            const value = dataset.data[index];
            if (value !== 0 && value != null) {
              ctx.save();
              ctx.fillStyle = 'black';
              ctx.font = 'bold 12px Arial'; // ✅ Bold values
              ctx.textAlign = 'center';
              ctx.textBaseline = 'middle';
              ctx.fillText(value, bar.x, bar.y + bar.height / 2);
              ctx.restore();
            }
          });
        });
      }
    }]
  });
}

//=============================================================================



  function updateTime() {
        const now = new Date();

        // Format: 05 July 2025, 06:45 PM
        const options = {
            day: '2-digit',
            month: 'long',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: true
        };

        const formattedTime = now.toLocaleString('en-US', options);
        document.getElementById('currentTime').textContent = formattedTime;
    }

    // Initial call
    updateTime();

    // Update every second
    setInterval(updateTime, 1000);



$('#capacityContainer123').append(`
  <form action="/ulb/capacity-building" method="post" id="capacityBuildingForm" class="form">
    <input type="hidden" id="id" name="id">
    <input type="hidden" name="nameOfCoE">

    <div>
      <div class="row mb-3">
        <div class="col-md-9 col-sm-12 headingOne">
          <span style="color:#ab7b75;">State/UT:</span>
          <span class="col-md-1" id="stateNameSpan"></span>
        </div>
        <div class="col-md-3 col-sm-12">
          <h6 class="heading-section headingOne">
            <span style="color:#FA8072;">Last Update:</span>
            <span id="lastUpdateSpan" class="col-md-1">N/A</span>
          </h6>
        </div>
      </div>

      <div class="row mb-3">
        <div class="col-12 headingOne">
          <span style="color:#ab7b75;">Name of CoE:</span>
          <span class="col-md-1" id="nameOfCoESpan"></span>
        </div>
      </div>
    </div>

    <hr style="border-top: 2px solid #FA8072;"><br>

    <div class="row mb-3">
      <div class="col-md-6 col-sm-12">
        <label class="form-label input-group mb-3">[1] NIGST master trainers</label>
        <input type="text" class="form-control input" name="nigstMasterTrainers" readonly>
      </div>
      <div class="col-md-6 col-sm-12">
        <label class="form-label input-group mb-3">[2] CoE master trainers</label>
        <input type="text" class="form-control input" name="coeMasterTrainers" readonly>
      </div>
    </div>

    <div class="row mb-3">
      <div class="col-md-6 col-sm-12">
        <label class="form-label input-group mb-3">[3] Total master trainers</label>
        <input type="text" class="form-control input" name="totalMasterTrainers" readonly>
      </div>
      <div class="col-md-6 col-sm-12">
        <label class="form-label input-group mb-3">[4] No. of field teams (Sanctioned by DoLR)</label>
        <input type="text" class="form-control input" id="fieldTeamsSanctioned" name="fieldTeamsSanctioned" readonly>
      </div>
    </div>

    <div class="row mb-3">
      <div class="col-md-6 col-sm-12">
        <label class="form-label input-group mb-3">[5] No. of members need to be trained to form field team</label>
        <input type="text" class="form-control input" name="membersTrained" readonly>
      </div>
      <div class="col-md-6 col-sm-12">
        <label class="form-label input-group mb-3">[6] Total no. of field team members trained till date</label>
        <input type="text" class="form-control input" id="membersToBeTrained"
               name="membersToBeTrained" placeholder="Enter total trained"
               onkeyup="validateMembersToBeTrained(this)"
               oninput="this.value = this.value.replace(/[^0-9]/g, '')">
        <span id="membersToBeTrainedError" class="text-danger small"></span>
      </div>
    </div>

    <div class="row mb-3">
      <div class="col-md-6 col-sm-12">
        <label class="form-label input-group mb-3">[7] Total No. of Field Teams Trained</label>
        <input type="text" class="form-control input" id="fieldTeamsTrained" name="fieldTeamsTrained" readonly>
      </div>
      <div class="col-md-6 col-sm-12">
        <label class="form-label input-group mb-3">[8] Percentage of Field Team trained till date (%)</label>
        <input type="text" class="form-control input" id="percentageTrained" name="percentageTrained" readonly>
      </div>
    </div>

    <hr style="border-top: 2px solid #FA8072;"><br>

    <div class="row mt-4">
      <div class="col-12 d-flex justify-content-center">
        <button type="submit" class="px-5 py-2 rounded-pill shadow-sm">
          <div class="svg-wrapper-1">
            <div class="svg-wrapper">
              <svg viewBox="0 0 24 24" width="24" height="24">
                <path fill="none" d="M0 0h24v24H0z"></path>
                <path fill="currentColor"
                      d="M1.946 9.315c-.522-.174-.527-.455.01-.634l19.087-6.362c.529-.176.832.12.684.638l-5.454 19.086c-.15.529-.455.547-.679.045L12 14l6-8-8 6-8.054-2.685z"></path>
              </svg>
            </div>
          </div>
          <span>Submit</span>
        </button>
      </div>
    </div>
  </form>
`);



       function validateMembersToBeTrained(input) {
            const requiredMembers = parseInt(document.querySelector('[name="membersTrained"]').value) || 0;
            const trainedMembers = parseInt(input.value) || 0;

            const errorSpan = document.getElementById("membersToBeTrainedError");

            if (trainedMembers > requiredMembers) {
                errorSpan.textContent = "Value at Serial no [6] cannot be greater than the value at Serial no [5].";
                document.getElementById("membersToBeTrained").value = "0";
                document.getElementById("fieldTeamsTrained").value = "0.00";
                document.getElementById("percentageTrained").value = "0.00";
            } else {
                errorSpan.textContent = "";

                // Calculate field teams trained (assuming each team = 3 members)
                const fieldTeamsTrained = trainedMembers / 3;
                const roundedTeamsTrained = fieldTeamsTrained.toFixed(2);
                document.getElementById("fieldTeamsTrained").value = roundedTeamsTrained;

                // Get sanctioned teams (you need to make sure this input exists!)
                const fieldTeamsSanctioned = parseInt(document.getElementById("fieldTeamsSanctioned")?.value) || 0;

                let percentageTrained = 0;
                if (fieldTeamsSanctioned > 0) {
                    percentageTrained = (fieldTeamsTrained / fieldTeamsSanctioned) * 100;
                }
                document.getElementById("percentageTrained").value = percentageTrained.toFixed(2);
            }
        }