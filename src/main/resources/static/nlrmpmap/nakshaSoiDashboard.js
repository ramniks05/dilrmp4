
$(document).ready(function () {
  // 1. Donut Chart: Area by Technology (Sanctioned Area)
  $.ajax({
    url: "/chart/area-by-technology",
    method: "GET",
    success: function (data) {
      const labels = data.map(item => item.technology || 'Unknown');
      const values = data.map(item => item.totalSanctionedArea || 0);
      renderPieChartHighchart({
        containerId: "technologyAreaChart",
        title: "",
        seriesName: "Sanctioned Area (sq. km)",
        dataKey: "totalSanctionedArea",
        rawData: data
      });
    },
    error: function () {
      console.error("Failed to load sanctioned area chart");
    }
  });

  // 2. Donut Chart: Area by Technology (Buffer Area)
  $.ajax({
    url: "/chart/area-by-technology",
    method: "GET",
    success: function (data) {
      const labels = data.map(item => item.technology || 'Unknown');
      const values = data.map(item => item.totalBufferArea || 0);
      renderPieChartHighchart({
        containerId: "technologyBufferAreaChart",
        title: "",
        seriesName: "Buffer Area (sq. km)",
        dataKey: "totalBufferArea",
        rawData: data
      });

    },
    error: function () {
      console.error("Failed to load buffer area chart");
    }
  });

  // 3. Donut Chart: Technology-wise ULB Count
  $.ajax({
    url: '/chart/technology-wise-ulb-count',
    method: 'GET',
    success: function (data) {
      const labels = data.map(item => item.technology || 'Unknown');
      const values = data.map(item => item.ulbCount || 0);
      renderPieChartHighchart({
        containerId: "ulbPieChart",
        title: "",
        seriesName: "ULB Count",
        dataKey: "ulbCount",
        rawData: data
      });

    },
    error: function () {
      console.error("Failed to load ULB chart");
    }
  });

  // 🍩 Generic Donut Chart Renderer
function renderPieChartHighchart({ containerId, title, seriesName, dataKey, rawData }) {
  const gradientColors = [
    ['#4099ff', '#73b4ff'],
    ['#FF5370', '#ff869a'],
    ['#2ed8b6', '#59e0c5'],
    ['#FFB64D', '#ffcb80'],
    ['#FE8A7D', '#feb8b0'],
    ['#69CEC6', '#8fdbd5']
  ];

  const chartData = rawData.map((item, index) => {
    const [start, end] = gradientColors[index % gradientColors.length];
    return {
      name: item.technology || 'Unknown',
      y: parseFloat(item[dataKey]) || 0,
      color: {
        linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
        stops: [[0, start], [1, end]]
      }
    };
  });

  const isULBChart = containerId === 'ulbPieChart';

  Highcharts.chart(containerId, {
    chart: {
      type: 'pie',
      options3d: {
        enabled: true,
        alpha: 45
      },
      backgroundColor: null
    },
    title: {
      text: title
    },
    tooltip: {
      pointFormat: isULBChart
        ? `<b>{point.y:.0f}</b>`
        : `<b>{point.y:.2f} sq. km</b>`
    },
    accessibility: {
      point: {
        valueSuffix: isULBChart ? '' : ' sq. km'
      }
    },
    plotOptions: {
      pie: {
        innerSize: '60%',
        depth: 45,
        allowPointSelect: true,
        cursor: 'pointer',
        dataLabels: {
          enabled: true,
          format: isULBChart
            ? '{point.name}: {point.y:.0f}'
            : '{point.name}: {point.y:.2f} sq. km',
          style: {
            color: '#333',
            fontSize: '14px',
            fontWeight: 'bold'
          }
        },
        showInLegend: true
      }
    },
    series: [{
      name: seriesName,
      data: chartData
    }],
     responsive: {
        rules: [{
          condition: {
            maxWidth: 500
          },
          chartOptions: {
            chart: {
              height: 300
            },
            plotOptions: {
              pie: {
                dataLabels: {
                  style: {
                    fontSize: '12px'
                  }
                }
              }
            }
          }
        }]
      }
  });
}





});


function render3DPieChart({ containerId, title, seriesName, dataKey, rawData }) {
  const backgroundColors = [
    'rgba(255, 99, 132, 0.8)',  // red
    'rgba(255, 159, 64, 0.8)',  // orange
    'rgba(255, 205, 86, 0.8)',  // yellow
    'rgba(75, 192, 192, 0.8)',  // teal
    'rgba(54, 162, 235, 0.8)',  // blue
    'rgba(153, 102, 255, 0.8)', // purple
    'rgba(100, 149, 237, 0.8)'  // cornflower blue
  ];

  const borderColors = [
    'rgb(180, 30, 50)',
    'rgb(200, 90, 30)',
    'rgb(180, 140, 40)',
    'rgb(20, 110, 110)',
    'rgb(20, 90, 180)',
    'rgb(80, 40, 180)',
    'rgb(65, 105, 225)'
  ];

  const chartData = rawData.map((item, index) => ({
    name: item.technology || 'Unknown',
    y: parseFloat(item[dataKey]) || 0,
    color: backgroundColors[index % backgroundColors.length],
    borderColor: borderColors[index % borderColors.length],
    borderWidth: 2
  }));

  Highcharts.chart(containerId, {
    chart: {
      type: 'pie',
      options3d: {
        enabled: true,
        alpha: 45
      }
    },
    title: {
      text: title
    },
    tooltip: {
      pointFormat:
        `<b>{point.y:${containerId === 'ulbPieChart' ? '.0f' : '.2f'}}${containerId === 'ulbPieChart' ? '' : ' sq. km'}</b>`
    },
    accessibility: {
      point: {
        valueSuffix: containerId === 'ulbPieChart' ? '' : ' sq. km'
      }
    },
    plotOptions: {
      pie: {
        allowPointSelect: true,
        cursor: 'pointer',
        depth: 45,
        innerSize: '60%',
        dataLabels: {
          enabled: true,
          format: containerId === 'ulbPieChart'
            ? '{point.name}: {point.y:.0f}'
            : '{point.name}: {point.y:.2f} sq. km',
          style: {
            color: '#333',
            fontSize: '14px',
            fontWeight: 'bold'
          }
        }
      }
    },
    series: [{
      name: seriesName,
      data: chartData
    }]
  });
}




  // Fetch data from your Spring Boot controller





//============================================

$(document).ready(function () {
    // Call API to get technology-wise area summary
    $.ajax({
        url: '/chart/technology-wise-area-summary',
        method: 'GET',
        success: function (data) {
            const technologies = data.map(item => item.technology);
            const sanctionedAreas = data.map(item => item.totalSanctionedArea);
            const bufferAreas = data.map(item => item.totalBufferArea);

            renderComparisonBarChart('comparisonChart', technologies, sanctionedAreas, bufferAreas, 'Technology');
        },
        error: function () {
            alert('Failed to load technology-wise area data.');
        }
    });

    // Call API to get contractor-wise area summary
    $.ajax({
        url: '/chart/contractor-wise-area-summary',
        method: 'GET',
        success: function (data) {
            const contractors = data.map(item => item.contractor);
            const sanctionedAreas = data.map(item => item.totalSanctionedArea);
            const bufferAreas = data.map(item => item.totalBufferArea);

            renderComparisonBarChart('comparison1Chart', contractors, sanctionedAreas, bufferAreas, 'Vendors');
        },
        error: function () {
            alert('Failed to load contractor-wise area data.');
        }
    });
});

$(document).ready(function() {
    $.ajax({
        url: '/chart/state-wise-area-summary',
        method: 'GET',
        success: function(data) {
            // Map the received data to arrays for chart labels and datasets
            const stateNames = data.map(item => item.stateName);
            const sanctionedAreas = data.map(item => Number(item.totalSanctionedArea)); // ensure number
            const bufferAreas = data.map(item => Number(item.totalBufferArea)); // ensure number

            // Call your chart rendering function
            renderComparisonBarChart('comparison2Chart', stateNames, sanctionedAreas, bufferAreas, 'State');
        },
        error: function() {
            alert('Failed to load state-wise area data.');
        }
    });
});



// Function to render comparison stacked bar chart
function renderComparisonBarChart(canvasId, labels, sanctionedData, bufferData, xAxisTitle) {
    const ctx = document.getElementById(canvasId).getContext('2d');

    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Sanctioned Area',
                    data: sanctionedData,
                    backgroundColor: 'rgba(54, 162, 235, 0.8)',
                    borderColor: 'rgba(20, 90, 180)',
                    borderWidth: 1
                },
                {
                    label: 'Buffer Area',
                    data: bufferData,
                    backgroundColor: 'rgba(255, 99, 132, 0.8)',
                    borderColor: 'rgb(180, 30, 50)',
                    borderWidth: 1
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: false, // show values immediately
            plugins: {
                tooltip: {
                    mode: 'index',
                    intersect: false,
                    callbacks: {
                        label: function(context) {
                            return context.dataset.label + ': ' + context.parsed.y + ' sq. km';
                        }
                    }
                },
                legend: {
                    position: 'top',
                    labels: {
                        font: { weight: 'bold' }
                    }
                }
            },
            scales: {
                y: {
                    stacked: true,
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Area (sq. km)',
                        font: { weight: 'bold' }
                    }
                },
                x: {
                    stacked: true,
                    title: {
                        display: true,
                        text: xAxisTitle,
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
                   ctx.fillStyle = 'black'; // better contrast inside bar
                   ctx.font = 'bold 12px Arial';
                   ctx.textAlign = 'center';
                   ctx.textBaseline = 'middle';
                   ctx.fillText(value, bar.x, bar.y + bar.height / 2); // inside bar center
                   ctx.restore();
                 }
               });
             });
           }
         }]
    });
}




$(document).ready(function () {
    // Fetch data from the backend API
    $.ajax({
        url: '/chart/ulb-under-each-gd',
        method: 'GET',
        success: function(data) {
            const gdNames = data.map(item => item.gdName);
            const ulbCounts = data.map(item => item.ulbCount);

            renderULBBarChart('ulbCountChart', gdNames, ulbCounts);
        },
        error: function() {
            alert('Failed to load ULB count data.');
        }
    });
});

/**
 * Render bar chart for ULB counts by GD using Chart.js
 * @param {string} canvasId - The canvas element ID
 * @param {string[]} labels - Array of GD names (x-axis)
 * @param {number[]} data - Array of ULB counts (y-axis)
 */
function renderULBBarChart(canvasId, labels, data) {
    const ctx = document.getElementById(canvasId).getContext('2d');

    // Destroy existing chart instance if any
    if (window.ulbChart) {
        window.ulbChart.destroy();
    }

    window.ulbChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'ULB Count',
                data: data,
                backgroundColor: 'rgba(75, 192, 192, 0.7)',
                borderColor: 'rgb(153, 102, 255)',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: false, // ❌ disable animation (important for immediate value draw)
            plugins: {
                legend: {
                    display: true,
                    labels: {
                        font: {
                            weight: 'bold'   // ✅ Bold legend label
                        }
                    }
                },
                tooltip: { mode: 'index', intersect: false }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    title: {
                        display: true,
                        text: 'Number of ULBs',
                        font: { weight: 'bold' }  // ✅ Bold axis title
                    }
                },
                x: {
                    title: {
                        display: true,
                        text: 'GDs',
                        font: { weight: 'bold' }  // ✅ Bold axis title
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
                            ctx.fillStyle = 'black'; // ✅ Visible on colored bars
                            ctx.font = 'bold 12px Arial';
                            ctx.textAlign = 'center';
                            ctx.textBaseline = 'bottom';
                            ctx.fillText(value, bar.x, bar.y - 5); // show above bar
                            ctx.restore();
                        }
                    });
                });
            }
        }]
    });
}







//=============== Aerial Data Acquisition  ===========================================================



$(document).ready(function () {
    $.ajax({
        url: '/chart/cumulative-data',
        method: 'GET',
        success: function (data) {
            let tbody = $('#cumulative-table-body');
            tbody.empty();

            data.forEach(function (item, index) {
                const row = `
                    <tr>
                        <td>${index + 1}</td>
                        <td><a href="#" class="ulb-link btn btn-sm btn-outline-success" style="border-radius: 30px;" data-id="${item.id}">${item.ulbName}</a></td>
                        <td>${item.tech1Cumulative}</td>
                        <td>${item.tech2Cumulative}</td>
                        <td>${item.tech3Cumulative}</td>
                        <td>${item.tech3LidarSensor}</td>


                        <td>${getStatusLabel(item.getTech1Status)}</td>
                        <td>${getStatusLabel(item.getTech2Status)}</td>
                        <td>${getStatusLabel(item.getTech3CumulativeStatus)}</td>


                        <td>${item.tech1Percentage}</td>
                        <td>${item.tech2Percentage}</td>
                        <td>${item.tech3Percentage}</td>
                        <td>${item.tech3LidarPercentage}</td>

                    </tr>
                `;
                tbody.append(row);
            });
              enableSortableHeader();

             $('#cumulative-table-body').on('click', '.ulb-link', function (e) {
                            e.preventDefault();
                            const id = $(this).data('id');
                             // Call the endpoint to fetch ULB details
                            $.ajax({
                                url: '/chart/ulb-details/' + id,
                                method: 'GET',
                                success: function (details) {
                                    if (details && details.length > 0) {
                                           const data = details[0]; // Assume one record per ID
                                           const content = `
                                               <p><strong>ULB:</strong> ${data[0]}</p>
                                               <p><strong>State/UT:</strong> ${data[1]}</p>
                                               <p><strong>District:</strong> ${data[2]}</p>
                                               <p><strong>GD/Wing:</strong> ${data[3]}</p>
                                               <p><strong>3rd Party Agency:</strong> ${data[4]}</p>
                                               <p><strong>Technology:</strong> ${data[5]}</p>
                                               <p><strong>DoLR Sanctioned Area:</strong> ${data[6]} sq km</p>
                                               <p><strong>Buffer Area Data Acquisition:</strong> ${data[7]} sq km</p>
                                           `;

                                           $('#ulbDetailContent').html(content);
                                           $('#ulbDetailModal').modal('show'); // Show Bootstrap modal
                                       }
                                },
                                error: function () {
                                    alert('Failed to fetch ULB details');
                                }
                            });
                        });

            // Highlight logic after rows are added
            const rows = document.querySelectorAll('#cumulative-table-body tr');

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
        }, // ←✅ Comma was missing here

        error: function () {
            alert('Failed to fetch data');
        }
    });
});





//========== Grid Completion Data ====================================================
$(document).ready(function () {
    $.ajax({
        url: '/chart/grid-summary',
        method: 'GET',
        success: function (data) {
            let tbody = $('#grid-table-body');
            tbody.empty();

            data.forEach(function (item, index) {
                let row = `<tr>
                    <td>${index + 1}</td>
                    <td>
                        <a href="#" class="ulb-link btn btn-sm btn-outline-success" style="border-radius: 30px;" data-id="${item[0]}">
                            ${item[1]}
                        </a>
                    </td>
                    <td>${item[2]}</td>
                    <td>${item[3]}</td>
                    <td>${item[4]}</td>
                </tr>`;

                tbody.append(row);
            });
             enableSortableHeader();
             $('#grid-table-body').on('click', '.ulb-link', function (e) {
                                        e.preventDefault();
                                        const id = $(this).data('id');
                                         // Call the endpoint to fetch ULB details
                                        $.ajax({
                                            url: '/chart/ulb-details/' + id,
                                            method: 'GET',
                                            success: function (details) {
                                                if (details && details.length > 0) {
                                                       const data = details[0]; // Assume one record per ID
                                                       const content = `
                                                           <p><strong>ULB:</strong> ${data[0]}</p>
                                                           <p><strong>State/UT:</strong> ${data[1]}</p>
                                                           <p><strong>District:</strong> ${data[2]}</p>
                                                           <p><strong>GD/Wing:</strong> ${data[3]}</p>
                                                           <p><strong>3rd Party Agency:</strong> ${data[4]}</p>
                                                           <p><strong>Technology:</strong> ${data[5]}</p>
                                                           <p><strong>DoLR Sanctioned Area:</strong> ${data[6]} sq km</p>
                                                           <p><strong>Buffer Area Data Acquisition:</strong> ${data[7]} sq km</p>
                                                       `;

                                                       $('#ulbDetailContent').html(content);
                                                       $('#ulbDetailModal').modal('show'); // Show Bootstrap modal
                                                   }
                                            },
                                            error: function () {
                                                alert('Failed to fetch ULB details');
                                            }
                                        });
                                    });

            // Highlight logic after rows are added
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
            alert('Failed to fetch data');
        }
    });
});





//========================== State Soi summary==========================
$(document).ready(function () {
  $.ajax({
    url: '/chart/state-soi-summary',
    method: "GET",
    beforeSend: function () {
      $("#wait").css("display", "block");
    },
    success: function (data) {
      $("#wait").css("display", "none");

      // Prepare labels and datasets for stacked bar
      const stateLabels = [];
       const ulbs = [], vendors = [], technologyCount = [], sanctionedAreas = [], bufferAreas = [], tech1 = [], tech2 = [], tech3 = []/*,tech3Lidar = [];*/


      data.forEach(item => {
        stateLabels.push(item.state);
        ulbs.push(item.ulbs || 0);
        vendors.push(item.vendors || 0);
        technologyCount.push(item.technologyCount);
        sanctionedAreas.push(item.sanctionedArea || 0);
        bufferAreas.push(item.bufferArea || 0);
        tech1.push(item.tech1 || 0);
        tech2.push(item.tech2 || 0);
        tech3.push(item.tech3 || 0);
      /*  tech3Lidar.push(item.tech3Lidar || 0);*/

      });

      const datasets = [
        {
          label: 'ULBs',
          data: ulbs,
          backgroundColor: 'rgba(255, 206, 86, 0.7)'
        },
        {
          label: '3rd Party Agency',
          data: vendors,
          backgroundColor: 'rgba(75, 192, 192, 0.7)'
        },
        {
          label: 'Technology',
          data: technologyCount,
          backgroundColor: 'rgba(238, 130, 238, 0.7)'
         },
        {
          label: 'DoLR Sanctioned Area(in sq km)',
          data: sanctionedAreas,
          backgroundColor: 'rgba(153, 102, 255, 0.7)'
        },
        {
          label: 'Buffer Area Data Acquisition(in sq km)',
          data: bufferAreas,
          backgroundColor: 'rgba(255, 159, 64, 0.7)'
        },
        {
          label: 'Tech 1 Nadir (Area in sq.km.)',
          data: tech1,
          backgroundColor: 'rgba(255, 99, 132, 0.7)'
        },
        {
          label: 'Tech 2 Oblique (Area in sq.km.)',
          data: tech2,
          backgroundColor: 'rgba(54, 162, 235, 0.7)'
        },
        {
          label: 'Tech 3 Oblique +  Lidar Sensor (Area in sq.km.)',
          data: tech3,
          backgroundColor: 'rgba(100, 149, 237, 0.8)'
        }/*,
        {
          label: 'Tech 3 Lidar Sensor (Area in sq.km.)',
          data: tech3Lidar,
          backgroundColor: 'rgba(60, 179, 113, 0.8)'
        }*/
      ];

      renderStackedBarChart('stateSummaryChart', stateLabels, datasets);
    },
    error: function () {
      $("#wait").css("display", "none");
      $('#roversSummary').html("<p style='color: red;'>Failed to load summary data.</p>");
    }
  });

function renderStackedBarChart(containerId, labels, datasets) {
  const ctx = document.getElementById(containerId).getContext('2d');

  // Destroy previous chart instance (if re-rendering)
  if (window.stackedChart) {
    window.stackedChart.destroy();
  }

  window.stackedChart = new Chart(ctx, {
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
        title: {
          display: false
        }
      },
      scales: {
        x: {
          stacked: true
        },
        y: {
          stacked: true,
          beginAtZero: true
        }
      }
    },
    plugins: [{
      // ✅ Custom value labels without extra plugin
      afterDatasetsDraw: function(chart) {
        const ctx = chart.ctx;
        chart.data.datasets.forEach((dataset, i) => {
          const meta = chart.getDatasetMeta(i);
          meta.data.forEach((bar, index) => {
            const value = dataset.data[index];
            if (value !== 0 && value !== null && value !== undefined) {
              ctx.fillStyle = "black"; // Text color
              ctx.font = "bold 12px Arial"; // Bold text
              ctx.textAlign = "center";
              ctx.textBaseline = "middle";
              ctx.fillText(value.toFixed(2), bar.x, bar.y - 5); // ✅ show with 2 decimals
            }
          });
        });
      }
    }]
  });
}



});

/*=========== compare-gd-names =======================*/
function loadGdComparison() {
    fetch('/chart/compare-gd-names')
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('gd-table-body');
            const stateSelect = document.getElementById('gd-state-filter');

            if (!tbody || !stateSelect) {
                alert("GD table body or state filter dropdown not found.");
                return;
            }

            const presentList = data.presentInVender || [];
            const states = data.gdStates || [];
            const gdToStates = data.gdToStates || {};

            // ✅ Convert present list to Set for lookup
            const presentSet = new Set(presentList.map(gd => gd.split(" (")[0]));

            // ✅ Populate state dropdown
            stateSelect.innerHTML = '<option value="All">---All State/UT---</option>';
            states.forEach(state => {
                const opt = document.createElement('option');
                opt.value = state.trim();
                opt.textContent = state;
                stateSelect.appendChild(opt);
            });

            // ✅ Rendering rows
            function renderTable(selectedState) {
                tbody.innerHTML = '';

                // Sort GDs alphabetically
                const allGds = Array.from(presentSet).sort();

                let rowIndex = 1;
                allGds.forEach(gd => {
                    const gdStates = gdToStates[gd] || [];

                    // 🔥 Filter by selected state
                    if (selectedState !== "All" && !gdStates.includes(selectedState)) {
                        return;
                    }

                    const row = `
                        <tr>
                            <td>${rowIndex++}</td>
                            <td>${gd}</td>
                        </tr>
                    `;
                    tbody.insertAdjacentHTML('beforeend', row);
                });
            }

            // ✅ Initial render
            renderTable("All");

            // ✅ On state filter change
            stateSelect.addEventListener('change', function () {
                renderTable(this.value);
            });
        })
        .catch(error => {
            alert("Error loading GD comparison: " + error);
            console.error("GD Comparison error:", error);
        });
}







/*================== End compare-gd-names =======================================*/
/* =========== compare-ulb-names =======================*/
let ulbData = {}; // Global storage

// Load ULB Comparison Data
function loadUlbComparison() {
    fetch('/chart/compare-ulb-names')
        .then(response => response.json())
        .then(data => {
            ulbData = data; // store globally

            populateStateDropdown(data);
            renderTable("all"); // default: all states
        })
        .catch(error => {
            alert("Error loading ULB comparison: " + error);
            console.error("ULB Comparison Error:", error);
        });
}

// Populate dropdown dynamically
function populateStateDropdown(data) {
    const stateFilter = document.getElementById('stateFilter');
    stateFilter.innerHTML = ""; // reset dropdown

    // Add "All States/UTs" option
    const allOption = document.createElement("option");
    allOption.value = "all";
    allOption.textContent = "-- All States/UTs --";
    stateFilter.appendChild(allOption);

    const stateSet = new Map();

    // Collect unique states from all lists
    ["presentInNaksha", "notInNaksha", "notflying_zone"].forEach(key => {
        (data[key] || []).forEach(ulb => {
            stateSet.set(ulb.stateId, ulb.stateName);
        });
    });

    // Sort states ASC by name
    const sortedStates = [...stateSet.entries()].sort((a, b) =>
        a[1].localeCompare(b[1])
    );

    // Add options
    sortedStates.forEach(([id, name]) => {
        const option = document.createElement("option");
        option.value = id;
        option.textContent = name;
        stateFilter.appendChild(option);
    });

    // Handle change event
    stateFilter.addEventListener("change", function () {
        renderTable(this.value);
    });
}

// Render table based on selected state
function renderTable(selectedStateId) {
    const tbody = document.getElementById('ulb-table-body');
    tbody.innerHTML = '';

    const presentList = ulbData.presentInNaksha || [];
    const notPresentList = ulbData.notInNaksha || [];
    const notFlyingZoneList = ulbData.notflying_zone || [];

    // Filter by state
    const filterFn = ulb => selectedStateId === "all" || ulb.stateId == selectedStateId;

    const presentFiltered = presentList.filter(filterFn);
    const notPresentFiltered = notPresentList.filter(filterFn);
    const notFlyingZoneFiltered = notFlyingZoneList.filter(filterFn);

    const maxLength = Math.max(
        presentFiltered.length,
        notPresentFiltered.length,
        notFlyingZoneFiltered.length
    );

    for (let i = 0; i < maxLength; i++) {
        const presentUlb = presentFiltered[i]?.ulbName || '';
        const notPresentUlb = notPresentFiltered[i]?.ulbName || '';
        const notFlyingZoneUlb = notFlyingZoneFiltered[i]?.ulbName || '';

        const row = `
            <tr>
                <td>${i + 1}</td>
                <td>${presentUlb}</td>

            </tr>
        `;
        tbody.insertAdjacentHTML('beforeend', row);
    }

    // ✅ Highlight rows and columns on hover
    /*const rows = document.querySelectorAll('#ulb-table-body tr');
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
    });*/
}


/*======================= End compare-ulb-names =============================================*/

/*=======================  compare-Agency-names =============================================*/
function loadAgencyComparison() {
    fetch('/chart/compare-agency-name')
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('agency-table-body');
            const stateSelect = document.getElementById('state-filter');

            if (!tbody || !stateSelect) {
                alert("Table body or state filter dropdown not found.");
                return;
            }

            tbody.innerHTML = '';

            const presentList = data.presentInDatabase || [];
            const contractorDetails = data.contractorDetails || {};

            // ✅ Collect unique states
            const stateSet = new Set();
            Object.values(contractorDetails).forEach(details => {
                (details.states || []).forEach(s => stateSet.add(s));
            });

            // ✅ Populate dropdown
            stateSelect.innerHTML = `<option value="All">--- All States/UTs ---</option>`;
            [...stateSet].sort().forEach(state => {
                stateSelect.innerHTML += `<option value="${state}">${state}</option>`;
            });

            // ✅ Render Table Function
            function renderTable(selectedState) {
                tbody.innerHTML = '';
                let serialNo = 1;

                presentList.forEach(presentAgency => {
                    const details = contractorDetails[presentAgency] || { states: [], ulbsByState: {} };
                    const states = details.states || [];
                    const ulbsByState = details.ulbsByState || {};

                    // 🔥 Skip agencies not in selected state
                    if (selectedState !== "All" && !states.includes(selectedState)) {
                        return;
                    }

                    // ✅ Show each ULB as a separate row
                    let ulbs = [];
                    if (selectedState === "All") {
                        // all states → flatten all ulbs
                        ulbs = Object.values(ulbsByState).flat();
                    } else {
                        ulbs = ulbsByState[selectedState] || [];
                    }

                    if (ulbs.length > 0) {
                        ulbs.forEach(ulb => {
                            const row = `
                                <tr>
                                    <td>${serialNo}</td>
                                    <td>${presentAgency}</td>
                                    <td>${ulb}</td>
                                </tr>
                            `;
                            tbody.insertAdjacentHTML('beforeend', row);
                            serialNo++;
                        });
                    } else {
                        // Agency without ULB → show "-"
                        const row = `
                            <tr>
                                <td>${serialNo}</td>
                                <td>${presentAgency}</td>
                                <td>-</td>
                            </tr>
                        `;
                        tbody.insertAdjacentHTML('beforeend', row);
                        serialNo++;
                    }
                });

                if (tbody.innerHTML.trim() === '') {
                    tbody.innerHTML = `<tr><td colspan="3" class="text-center text-muted">No data available</td></tr>`;
                }
            }

            // ✅ Default render → all states
            renderTable("All");

            // ✅ On dropdown change
            stateSelect.addEventListener('change', (e) => {
                renderTable(e.target.value);
            });
        })
        .catch(error => {
            console.error("Agency Comparison Error:", error);
            const tbody = document.getElementById('agency-table-body');
            if (tbody) {
                tbody.innerHTML = `
                    <tr><td colspan="3" class="text-danger">Failed to load data</td></tr>
                `;
            }
        });
}







/*============Flying Completed in T-3 More Info===============================================*/
function loadFlyingCompletedT3n() {
    fetch('/chart/compare-tech3-percentage')
        .then(response => response.json())
        .then(data => {
            const tableElement = $('#flyingCompletedT3Table');
            if (!tableElement.length) return;

            const list1 = data.tech3_completed || [];
            const list2 = data.tech3_under_process || [];
            const list3 = data.tech3_not_started || [];
            // const list4 = data.notFlyingZone || [];  // ❌ removed

            let tableData = [];

            // helper to push rows with status
            function pushRows(list, status, bgColor) {
                list.forEach(item => {
                    const [state, ulb] = (item || '').split(' - ');
                    if (ulb && state) {
                        tableData.push({
                            state: state.trim(),
                            ulb: ulb.trim(),
                            status: status,
                            bgColor: bgColor
                        });
                    }
                });
            }

            // ✅ add rows
            pushRows(list1, "Completed", "#d1e7dd");
            pushRows(list2, "Under Process", "#fff3cd");
            pushRows(list3, "Not Started", "#feb8b0");
            // pushRows(list4, "ORIs Available- Not Flying", "#e2e3e5"); // ❌ removed

            // ✅ totals
            const totalCompleted = list1.length;
            const totalUnderProcess = list2.length;
            const totalNotStarted = list3.length;
            const totalAll = tableData.length;   // ✅ fixed

            // ✅ update summary counters
            document.getElementById("totalAllT3").innerText = `Total ULBs: ${totalAll}`;
            document.getElementById("totalCompletedT3").innerText = `Completed: ${totalCompleted}`;
            document.getElementById("totalUnderProcessT3").innerText = `Under Process: ${totalUnderProcess}`;
            document.getElementById("totalNotStartedT3").innerText = `Not Started: ${totalNotStarted}`;
            // document.getElementById("totalNotFlyingT3").innerText = `ORIs Available- Not Flying: ${totalNotFlying}`; // ❌ removed

            // destroy old table if reloaded
            if ($.fn.DataTable.isDataTable('#flyingCompletedT3Table')) {
                tableElement.DataTable().clear().destroy();
            }

            // init DataTable
            const dt = tableElement.DataTable({
                data: tableData,
                destroy: true,
                columns: [
                    {
                        data: null,
                        render: (data, type, row, meta) => meta.row + 1,
                        orderable: false,
                        searchable: false
                    },
                    { data: 'state' },
                    { data: 'ulb' },
                    {
                        data: 'status',
                        render: (data, type, row) => {
                            return `<span style="background-color:${row.bgColor}; padding:3px 6px; display:block; border:1px solid #000;">${data}</span>`;
                        }
                    }
                ],
                paging: false,
                searching: true,
                ordering: true,
                order: [[1, 'asc']], // sort by state by default
                info: false
            });

            // recalc S.No on sort/search
            dt.on('order.dt search.dt', function () {
                dt.column(0, { search: 'applied', order: 'applied' })
                    .nodes()
                    .each((cell, i) => {
                        cell.innerHTML = i + 1;
                    });
            }).draw();
        })
        .catch(error => {
            alert("Error loading Flying Completed T3 data: " + error);
            console.error("Flying Completed T3 Error:", error);
        });
}



function loadFlyingCompletedT3LidarSensor() {

    fetch('/chart/tech3-lidar-percentage')
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('flyingT3LidarBody');
            if (!tbody) {
                alert("T3 table body not found.");
                return;
            }

            const list1 = data.tech3_100 || [];
            const list2 = data.tech3_lessThan100 || [];
            const list3 = data.tech3_nullOrZero || [];
            const list4 = data.notFlyingZone || [];

             const maxLength = Math.max(list1.length, list2.length, list3.length, list4.length);

            tbody.innerHTML = '';

            for (let i = 0; i < maxLength; i++) {
                const row = `
                    <tr>
                        <td>${i + 1}</td>
                        <td>${list1[i] || ''}</td>
                        <td>${list2[i] || ''}</td>
                        <td>${list3[i] || ''}</td>
                        <td>${list4[i] || ''}</td>



                    </tr>
                `;
                tbody.insertAdjacentHTML('beforeend', row);
            }

            // Row & column hover effect
            const rows = document.querySelectorAll('#flyingT3LidarBody tr');
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

            // Show the modal
           /* const modal = new bootstrap.Modal(document.getElementById('flyingCompletedT3Modal'));
            modal.show();*/
        })
        .catch(error => {
            alert("Error loading Flying Completed T3 data: " + error);
            console.error("Flying Completed T3 Error:", error);
        });
}

/*================= Flying Completed in T-1 & T-2 =========================================*/
/*function loadFlyingCompletedT1() {
    fetch('/chart/compare-tech1-tech2-percentage')
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('flyingCompletedT1T2Body');
            if (!tbody) return;

            const list1 = data.tech1_completed || [];
            const list2 = data.tech1_under_process || [];
            const list3 = data.tech1_not_started || [];
            const list4 = data.notFlyingZone || [];

            const maxLength = Math.max(list1.length, list2.length, list3.length, list4.length);
            tbody.innerHTML = '';

            for (let i = 0; i < maxLength; i++) {
                // Split "ULB - State" from all 3 lists
                const [state1, ulb1] = (list1[i] || '').split(' - ');
                const [state2, ulb2] = (list2[i] || '').split(' - ');
                const [state3, ulb3] = (list3[i] || '').split(' - ');
                const [state4, ulb4] = (list4[i] || '').split(' - ');

                const row = `
                    <tr>
                        <td style=" border: 1px solid #000000;">${i + 1}</td>

                        <!-- Tech1 100% -->
                        <td style="background-color: #d1e7dd; border: 1px solid #000000;">${state1 || ''}</td>
                        <td style="background-color: #d1e7dd; border: 1px solid #000000;">${ulb1 || ''}</td>


                        <!-- Tech1 < 100% -->
                        <td style="background-color: #fff3cd; border: 1px solid #000000;">${state2 || ''}</td>
                        <td style="background-color: #fff3cd; border: 1px solid #000000;">${ulb2 || ''}</td>


                        <!-- Tech1 = 0% -->
                        <td style="background-color: #feb8b0; border: 1px solid #000000;">${state3 || ''}</td>
                        <td style="background-color: #feb8b0; border: 1px solid #000000;">${ulb3 || ''}</td>


                        <!-- Not in Flying Zone -->
                        <td style="background-color: #e2e3e5; border: 1px solid #000000;">${state4 || ''}</td>
                        <td style="background-color: #e2e3e5; border: 1px solid #000000;">${ulb4 || ''}</td>
                    </tr>
                `;
                tbody.insertAdjacentHTML('beforeend', row);
            }
            enableSortableHeaders();
            *//*enableFlyingSearch();*//*

            // Hover effect
            const rows = document.querySelectorAll('#flyingCompletedT1T2Body tr');
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
        })
        .catch(error => {
            alert("Error loading Flying Completed T1/T2 data: " + error);
            console.error("Flying Completed T1/T2 Error:", error);
        });
}*/
function loadFlyingCompletedT1T2T3() {
    fetch('/chart/compare-tech1-tech2-tech3-percentage')
        .then(response => response.json())
        .then(data => {
            const tableElement = $('#flyingCompletedTable');
            if (!tableElement.length) return;

            // lists
            const listTech1Completed = data.tech1_completed || [];
            const listTech1UnderProcess = data.tech1_under_process || [];
            const listTech1NotStarted = data.tech1_not_started || [];
            const listTech2Completed = data.tech2_completed || [];
            const listTech2UnderProcess = data.tech2_under_process || [];
            const listTech2NotStarted = data.tech2_not_started || [];
            const listTech3Completed = data.tech3_completed || [];
            const listTech3UnderProcess = data.tech3_under_process || [];
            const listTech3NotStarted = data.tech3_not_started || [];

            let tableData = [];

            // helper → push rows
            function pushRows(list, tech, status, bgColor) {
                list.forEach(item => {
                    const [state, ulb] = (item || '').split(' - ');
                    if (ulb && state) {
                        tableData.push({
                            state: state.trim(),
                            ulb: ulb.trim(),
                            technology: tech,
                            status: status,
                            bgColor: bgColor
                        });
                    }
                });
            }

            // fill data
            pushRows(listTech1Completed, "Tech-1", "Completed", "#d1e7dd");
            pushRows(listTech1UnderProcess, "Tech-1", "Under Process", "#fff3cd");
            pushRows(listTech1NotStarted, "Tech-1", "Not Started", "#feb8b0");

            pushRows(listTech2Completed, "Tech-2", "Completed", "#d1e7dd");
            pushRows(listTech2UnderProcess, "Tech-2", "Under Process", "#fff3cd");
            pushRows(listTech2NotStarted, "Tech-2", "Not Started", "#feb8b0");

            pushRows(listTech3Completed, "Tech-3", "Completed", "#d1e7dd");
            pushRows(listTech3UnderProcess, "Tech-3", "Under Process", "#fff3cd");
            pushRows(listTech3NotStarted, "Tech-3", "Not Started", "#feb8b0");

            // ✅ totals
            const totalCompleted = listTech1Completed.length + listTech2Completed.length + listTech3Completed.length;
            const totalUnderProcess = listTech1UnderProcess.length + listTech2UnderProcess.length + listTech3UnderProcess.length;
            const totalNotStarted = listTech1NotStarted.length + listTech2NotStarted.length + listTech3NotStarted.length;
            const totalAll = tableData.length;

            // ✅ update counters
            document.getElementById("all").innerText = `Total ULBs: ${totalAll}`;
            document.getElementById("completed").innerText = `Completed: ${totalCompleted}`;
            document.getElementById("underProcess").innerText = `Under Process: ${totalUnderProcess}`;
            document.getElementById("notStarted").innerText = `Not Started: ${totalNotStarted}`;

            // destroy old table
            if ($.fn.DataTable.isDataTable('#flyingCompletedTable')) {
                tableElement.DataTable().clear().destroy();
            }

            // init datatable
            const dt = tableElement.DataTable({
                data: tableData,
                destroy: true,
                columns: [
                    {
                        data: null,
                        render: (data, type, row, meta) => meta.row + 1,
                        orderable: false,
                        searchable: false
                    },
                    { data: 'state' },
                    { data: 'ulb' },
                    { data: 'technology' },   // 👈 added Technology column
                    {
                        data: 'status',
                        render: (data, type, row) => {
                            return `<span style="background-color:${row.bgColor}; padding:3px 6px; display:block; border:1px solid #000;">${data}</span>`;
                        }
                    }
                ],
                paging: false,
                searching: true,
                ordering: true,
                order: [[1, 'asc']], // sort by state
                info: false
            });

            // reindex S.No
            dt.on('order.dt search.dt', function () {
                dt.column(0, { search: 'applied', order: 'applied' })
                    .nodes()
                    .each((cell, i) => {
                        cell.innerHTML = i + 1;
                    });
            }).draw();
        })
        .catch(error => {
            alert("Error loading Flying Completed T1/T2/T3 data: " + error);
            console.error("Flying Completed Error:", error);
        });
}


function loadFlyingCompletedT1() {
    fetch('/chart/compare-tech1-tech2-percentage')
        .then(response => response.json())
        .then(data => {
            const tableElement = $('#flyingCompletedT1T2Table');
            if (!tableElement.length) return;

            const list1 = data.tech1_completed || [];
            const list2 = data.tech1_under_process || [];
            const list3 = data.tech1_not_started || [];
            // const list4 = data.notFlyingZone || [];  // ❌ removed

            let tableData = [];

            // helper to push rows with status
            function pushRows(list, status, bgColor) {
                list.forEach(item => {
                    const [state, ulb] = (item || '').split(' - ');
                    if (ulb && state) {
                        tableData.push({
                            state: state.trim(),
                            ulb: ulb.trim(),
                            status: status,
                            bgColor: bgColor
                        });
                    }
                });
            }

            // fill data from each list
            pushRows(list1, "Completed", "#d1e7dd");
            pushRows(list2, "Under Process", "#fff3cd");
            pushRows(list3, "Not Started", "#feb8b0");
            // pushRows(list4, "ORIs Available- Not Flying", "#e2e3e5"); // ❌ removed

            // ✅ count totals
            const totalCompleted = list1.length;
            const totalUnderProcess = list2.length;
            const totalNotStarted = list3.length;
            const totalAll = tableData.length;   // ✅ fixed

            // ✅ update summary divs
            document.getElementById("totalAll").innerText = `Total ULBs: ${totalAll}`;
            document.getElementById("totalCompleted").innerText = `Completed: ${totalCompleted}`;
            document.getElementById("totalUnderProcess").innerText = `Under Process: ${totalUnderProcess}`;
            document.getElementById("totalNotStarted").innerText = `Not Started: ${totalNotStarted}`;
            // document.getElementById("totalNotFlying").innerText = `ORIs Available- Not Flying: ${totalNotFlying}`; // ❌ removed

            // Destroy existing table if reloaded
            if ($.fn.DataTable.isDataTable('#flyingCompletedT1T2Table')) {
                tableElement.DataTable().clear().destroy();
            }

            // Init DataTable
            const dt = tableElement.DataTable({
                data: tableData,
                destroy: true,
                columns: [
                    {
                        data: null,
                        render: (data, type, row, meta) => meta.row + 1,
                        orderable: false,
                        searchable: false
                    },
                    { data: 'state' },
                    { data: 'ulb' },
                    {
                        data: 'status',
                        render: function (data, type, row) {
                            return `<span style="background-color:${row.bgColor}; padding:2px 6px; display:block; border:1px solid #000;">${data}</span>`;
                        }
                    }
                ],
                paging: false,
                searching: true,
                ordering: true,
                info: false,
                order: [[1, 'asc']]   // 👈 default sort by state asc
            });

            // Recalculate S.No on sort/search
            dt.on('order.dt search.dt', function () {
                dt.column(0, { search: 'applied', order: 'applied' })
                    .nodes()
                    .each((cell, i) => {
                        cell.innerHTML = i + 1;
                    });
            }).draw();
        })
        .catch(error => {
            alert("Error loading Flying Completed T1/T2 data: " + error);
            console.error("Flying Completed T1/T2 Error:", error);
        });
}






/*=======================Flying Completed  ========================================================================*/

function loadFlyingCompletedT2() {
    fetch('/chart/compare-tech1-tech2-percentage')
        .then(response => response.json())
        .then(data => {
            const tableElement = $('#flyingCompletedT2Table');
            if (!tableElement.length) return;

            const listCompleted = data.tech2_completed || [];
            const listUnderProcess = data.tech2_under_process || [];
            const listNotStarted = data.tech2_not_started || [];
            // const listNotFlying = data.notFlyingZone || [];  // ❌ removed

            let tableData = [];

            // helper to push rows with status
            function pushRows(list, status, bgColor) {
                list.forEach(item => {
                    const [state, ulb] = (item || '').split(' - ');
                    if (ulb && state) {
                        tableData.push({
                            state: state.trim(),
                            ulb: ulb.trim(),
                            status: status,
                            bgColor: bgColor
                        });
                    }
                });
            }

            // fill data from each list
            pushRows(listCompleted, "Completed", "#d1e7dd");
            pushRows(listUnderProcess, "Under Process", "#fff3cd");
            pushRows(listNotStarted, "Not Started", "#feb8b0");
            // pushRows(listNotFlying, "ORIs Available- Not Flying", "#e2e3e5"); // ❌ removed

            // ✅ count totals
            const totalCompleted = listCompleted.length;
            const totalUnderProcess = listUnderProcess.length;
            const totalNotStarted = listNotStarted.length;
            const totalAll = tableData.length;   // ✅ fixed

            // ✅ update summary divs
            document.getElementById("totalAllT2").innerText = `Total ULBs: ${totalAll}`;
            document.getElementById("totalCompletedT2").innerText = `Completed: ${totalCompleted}`;
            document.getElementById("totalUnderProcessT2").innerText = `Under Process: ${totalUnderProcess}`;
            document.getElementById("totalNotStartedT2").innerText = `Not Started: ${totalNotStarted}`;
            // document.getElementById("totalNotFlyingT2").innerText = `ORIs Available- Not Flying: ${totalNotFlying}`; // ❌ removed

            // Destroy existing table if reloaded
            if ($.fn.DataTable.isDataTable('#flyingCompletedT2Table')) {
                tableElement.DataTable().clear().destroy();
            }

            // Init DataTable
            const dt = tableElement.DataTable({
                data: tableData,
                destroy: true,
                columns: [
                    {
                        data: null,
                        render: (data, type, row, meta) => meta.row + 1,
                        orderable: false,
                        searchable: false
                    },
                    { data: 'state' },
                    { data: 'ulb' },
                    {
                        data: 'status',
                        render: function (data, type, row) {
                            return `<span style="background-color:${row.bgColor}; padding:2px 6px; display:block; border:1px solid #000;">${data}</span>`;
                        }
                    }
                ],
                paging: false,
                searching: true,
                ordering: true,
                order: [[1, 'asc']], // ✅ sort by state asc by default
                info: false
            });

            // Recalculate S.No on sort/search
            dt.on('order.dt search.dt', function () {
                dt.column(0, { search: 'applied', order: 'applied' })
                    .nodes()
                    .each((cell, i) => {
                        cell.innerHTML = i + 1;
                    });
            }).draw();
        })
        .catch(error => {
            alert("Error loading Flying Completed T2 data: " + error);
            console.error("Flying Completed T2 Error:", error);
        });
}



/*=======================  End-Agency-names =============================================*/




document.addEventListener('DOMContentLoaded', function () {
  fetch('/chart/gd-wise-flaying-summary')
    .then(response => response.json())
    .then(data => {
      const categories = [];
      const ulbCounts = [];
      const tech1Counts = [];
      const tech2Counts = [];
      const tech3Counts = [];
     const tech3LidarCounts = [];

      // Iterate through the data
      data.forEach(row => {
        const gdName = row[0];
        const ulbCount = row[1];
        const tech1 = row[2];
        const tech2 = row[3];
        const tech3 = row[4];
       const tech3Lidar = row[5];

        categories.push(gdName);
        ulbCounts.push(ulbCount);
        tech1Counts.push(tech1 || 0);  // If tech1 is null, set it to 0
        tech2Counts.push(tech2 || 0);  // If tech2 is null, set it to 0
        tech3Counts.push(tech3 || 0);  // If tech3 is null, set it to 0
        tech3LidarCounts.push(tech3Lidar || 0);  // If tech3Lidar is null, set it to 0
      });

      // Highcharts configuration
      Highcharts.chart('gdSummaryChart', {
        chart: {
          type: 'column'
        },
        title: {
          text: ''
        },
        xAxis: {
          categories: categories,
          crosshair: true
        },
        yAxis: {
          min: 0,
          title: {
            text: 'No. of ULBs'
          }
        },
        tooltip: {
          shared: true,
          formatter: function () {
            const index = this.points[0].point.index;
            const gdName = this.points[0].key;
            return `<b>GD Name: ${gdName}</b><br/>
                    Total No. of ULBs: ${ulbCounts[index]}<br/>
                    Tech 1 Nadir : ${tech1Counts[index]}<br/>
                    Tech 2 Oblique : ${tech2Counts[index]}<br/>
                    Tech 3 Oblique + Lidar Sensor : ${tech3Counts[index]}`;
                    /*Tech 3 Lidar Sensor : ${tech3LidarCounts[index]}*/
          }
        },
        plotOptions: {
          column: {
            dataLabels: {
              enabled: true
            }
          }
        },
        series: [
          {
            name: 'Total No. of ULBs',
            data: ulbCounts,
            color: '#7cb5ec'
          },
          {
            name: 'Tech 1 Nadir',
            data: tech1Counts,
            color: '#90ed7d'
          },
          {
            name: 'Tech 2 Oblique',
            data: tech2Counts,
            color: '#f7a35c'
          },
          {
            name: 'Tech 3 Oblique + Lidar Sensor',
            data: tech3Counts,
            color: '#8085e9'
          }/*,
          {
            name: 'Tech 3 Lidar Sensor',
            data: tech3LidarCounts,
            color: '#f15c80'
          }*/
        ]
      });
    })
    .catch(err => console.error("Chart fetch error:", err));
});





/*======== processingSummaryChart ========*/
/*document.addEventListener('DOMContentLoaded', function () {
  fetch('/chart/processing-completed-summary')
    .then(response => response.json())
    .then(data => {
      const contractors = [];
      const ulbs = [];
      const oriData = [];
      const oriQaqcData = [];
      const demData = [];
      const demQaqcData = [];
      const dsmData = [];
      const dsmQaqcData = [];
      const dtmData = [];
      const dtmQaqcData = [];
      const meshData = [];
      const meshQaqcData = [];
      const feOriData = [];
      const feOriQaqcData = [];
      const tech1Tech2Data = [];

      data.forEach(item => {
        contractors.push(item.contractor);
        ulbs.push(item.total);
        tech1Tech2Data.push(item. max_tech1_or_tech2_100);
        oriData.push(item.ori);
        oriQaqcData.push(item.ori_qaqc);
        demData.push(item.dem);
        demQaqcData.push(item.dem_qaqc);
        dsmData.push(item.dsm);
        dsmQaqcData.push(item.dsm_qaqc);
        dtmData.push(item.dtm);
        dtmQaqcData.push(item.dtm_qaqc);
        meshData.push(item.mesh);
        meshQaqcData.push(item.mesh_qaqc);
        feOriData.push(item.fe_ori);
        feOriQaqcData.push(item.fe_ori_qaqc);


      });

      Highcharts.chart('vendorSummaryChart', {
        chart: {
          type: 'bar' // ⬅️ Horizontal bars
        },
        title: {
          text: 'Vendor-wise Data Processing Summary'
        },
        xAxis: {
          categories: contractors,
          title: {
            text: null
          }
        },
        yAxis: {
          min: 0,
          title: {
            text: 'Counts',
            align: 'high'
          },
          labels: {
            overflow: 'justify'
          }
        },
        tooltip: {
          shared: true
        },
        plotOptions: {
          bar: {
            dataLabels: {
              enabled: true
            },
            stacking: 'normal' // optional: remove if you want grouped bars
          }
        },
         series: [
                 { name: "Total No. of  ULBs", data: ulbs, color: '#000000' }, // ← ULBs shown in black
                 { name: "No. of ULB's where ORI Submitted for QA/QC Completed", data: oriData },
                 { name: "No. of ULB's where flying is Completed in Tech 1&2", data: tech1Tech2Data },
               *//*  { name: "No. of ULB's QA/QC of Completed", data: oriQaqcData },*//*
               *//*  { name: "No. of ULB's DEM Submitted for QA/QC Completed", data: demData },*//*
              *//*   { name: "No. of ULB's QA/QC of DEM Completed", data: demQaqcData },*//*
                *//* { name: "No. of ULB's DSM Submitted for QA/QC Completed", data: dsmData },*//*
                 { name: "No. of ULB's  QA/QC of DSM is Done", data: dsmQaqcData },
                *//* { name: "No. of ULB's DTM Submitted for QA/QC Completed", data: dtmData },*//*
                 { name: "No. of ULB's QA/QC of DTM is Done", data: dtmQaqcData },
                *//* { name: "No. of ULB's 3D Mesh Model Submitted for QA/QC Completed", data: meshData },*//*
                 { name: "No. of ULB's  QA/QC of 3D Mesh is Done", data: meshQaqcData },
                *//* { name: "No. of ULB's 2D Feature Extraction Submitted for QA/QC Completed", data: feOriData },*//*
                 { name: "No. of ULB's QA/QC of 2D Feature Extraction is Completed", data: feOriQaqcData }
               ]
      });




    })
    .catch(err => console.error("Failed to load chart data:", err));
});*/

document.addEventListener('DOMContentLoaded', function () {
  fetch('/chart/processing-completed-summary')
    .then(response => response.json())
    .then(data => {
      const contractors = [];
      const ulbs = [];
      const oriQaqcData = [];
      const tech1Tech2Data = [];
      const dsmQaqcData = [];
      const dtmQaqcData = [];
      const meshQaqcData = [];
      const feOriQaqcData = [];

      const twoDFeatextrStereomode = [];
      const twoDFeatextrStereomodeQaQc = [];
      const threeD_fe_qaqc = [];
      const threeDmilestoneQaQcStatus = [];

      data.forEach(item => {
        contractors.push(item.contractor);
        ulbs.push(item.total);
        tech1Tech2Data.push(item.total_tech_count);
        oriQaqcData.push(item.ori_qaqc);
        dsmQaqcData.push(item.dsm_qaqc);
        dtmQaqcData.push(item.dtm_qaqc);
        meshQaqcData.push(item.mesh_qaqc);
        feOriQaqcData.push(item.fe_ori_qaqc);

        twoDFeatextrStereomode.push(item.twoDFeatextrStereomode);
        twoDFeatextrStereomodeQaQc.push(item.twoDFeatextrStereomodeQaQc);
        threeD_fe_qaqc.push(item.threeD_fe_qaqc);
        threeDmilestoneQaQcStatus.push(item.threeDmilestoneQaQcStatus);
      });

      Highcharts.chart('vendorSummaryChart', {
        chart: {
          type: 'bar' // ✅ Horizontal stacked bars
        },
        title: {
          text: ''
        },
        xAxis: {
          categories: contractors,
          title: {
            text: '3rd Party Agency'
          }
        },
        yAxis: {
          min: 0,
          title: {
            text: 'Number of ULBs'
          },

        },

    tooltip: {
      shared: true,
      useHTML: true,
      formatter: function () {
        let contractorName = this.points[0].key; // Correct way to get contractor name
        let s = '<div style="font-size:12px; font-weight:bold;">' + contractorName + '</div>';
        this.points.forEach(point => {
          s += '<span style="color:' + point.color + '">\u25CF</span> ' +
               '<span style="font-size:12px; ">' + point.series.name + '</span>: ' +
               '<b>' + point.y + '</b><br/>';
        });
        return s;
      }
    },

        plotOptions: {
          bar: {
            stacking: 'normal', // ✅ Enables stacked bars
            dataLabels: {
              enabled: true
            }
          }
        },
        series: [
          { name: "Total No. of ULBs", data: ulbs, color: '#ffab91' },
          { name: "No. of ULB\'s where flying is completed across Tech 1, Tech 2, and Tech 3 (Oblique + Lidar Sensor) ", data: tech1Tech2Data, color: '#c5e1a5' },
          { name: "NO. of ULB\'s where ORI Submitted for QA/QC", data: oriQaqcData, color: '#64b5f6' },

          { name: "NO. of ULB\'s where QA/QC of DSM is done", data: dsmQaqcData, color: '#80cbc4' },
          { name: "NO. of ULB\'s where QA/QC of DTM is done", data: dtmQaqcData, color: '#9ad9e1' },
          { name: "NO. of ULB\'s where QA/QC of 3D Mesh Model is done", data: meshQaqcData, color: '#9467bd' },
          { name: "No. of ULB\'s where QA/QC of 2D Feature Extraction is Completed", data: feOriQaqcData, color: '#8c564b' },



           { name: "No. of ULB\'s where QA/QC of 3D Feature Extraction (Milestone 3A) is Completed", data: threeD_fe_qaqc, color: '#BEC15C' },
           { name: "No. of ULB\'s where  2D Feature Extraction in Stereo Mode – QA/QC (Milestone 3B for Tech 1) is Completed", data: twoDFeatextrStereomodeQaQc, color: '#F54927' },
           { name: "No. of ULB\'s where QA/QC of Final 3D Feature Extraction (Milestone 3B) is Completed", data: threeDmilestoneQaQcStatus, color: '#F5B027' }
        ]
      });
    })
    .catch(err => console.error("Failed to load chart data:", err));
});



/*==================== ORI Submitted for QA/QC ===================================================================*/
const completionStatusMap = {};

fetch('/chart/completion-statuses')
    .then(response => response.json())
    .then(data => {
        data.forEach(status => {
            const originalDesc = status.description;
            const desc = originalDesc.toLowerCase();
            let cssClass = 'label-default';

            if (desc.includes('rejected') || desc.includes('returned')) {
                cssClass = 'label-danger'; // red
            } else if (desc.includes('completed')) {
                cssClass = 'label-success'; // green
            } else if (desc.includes('accepted')) {
                cssClass = 'label-success'; // green
            } else if (desc.includes('progress') || desc.includes('correction')) {
                cssClass = 'label-warning'; // yellow
            } else if (desc.includes('not started')) {
                cssClass = 'label-danger'; // red
            } else if (desc.includes('n/a')) {  // ✅ fixed here
                cssClass = 'label-info'; // blue
            } else {
                cssClass = 'label-default1';
            }

            completionStatusMap[originalDesc] = `label1 ${cssClass}`;
        });
    });


function getStatusLabel(statusText) {
    const labelClass = completionStatusMap[statusText] || 'label1';
    return `<span class="${labelClass}">${statusText}</span>`;
}


function fetchModalTableData(apiUrl, modalId, title, subHeader, columnHeaders, tableBodyId = 'ori-table-body', selectionType = 'gd') {
    const selectedValue = selectionType === 'gd'
        ? $('#gdNameSelect').val()
        : $('#stateSelect').val();

    $('#oriModalTableHeader').empty();
    $(`#${tableBodyId}`).empty();

    const queryParam = selectionType === 'gd'
        ? `gdName=${encodeURIComponent(selectedValue)}`
        : `gdStateName=${encodeURIComponent(selectedValue)}`;

    $.ajax({
        url: `${apiUrl}?${queryParam}`,
        method: 'GET',
        success: function (data) {
            $(`#${modalId} #oriModalLabel`).text(title);
            $(`#${modalId} #oriModalHeaderText`).text(subHeader);

           let headerRow = '';
           columnHeaders.forEach((col, index) => {
               // Only add sortable class if it's NOT the first column (S.No)
               headerRow += `<th${index > 0 ? ' class="sortable" data-column="' + index + '"' : ''}>${col}</th>`;
           });
            $('#oriModalTableHeader').html(headerRow);
            enableSortableHeaders();

            const tbody = $(`#${tableBodyId}`);
            if (!data || data.length === 0) {
                tbody.html(`<tr "><td colspan="${columnHeaders.length}" style="text-align: center; color: red;">Data Not Available</td></tr>`);
                return;
            }

            data.forEach((item, index) => {
                let row = `<tr ><td>${index + 1}</td>`;
                item.forEach((col, i) => {
                    row += `<td>${i === 6 ? getStatusLabel(col) : col}</td>`;
                });
                row += '</tr>';
                tbody.append(row);
            });


            // Highlighting logic unchanged...
        },
        error: function () {
            $(`#${tableBodyId}`).html(`<tr><td colspan="${columnHeaders.length}" style="text-align: center; color: red;">Error loading data</td></tr>`);
        }
    });
}



// Table search filter
$('#oriTableSearch').off('input').on('input', function () {
    const searchText = $(this).val().toLowerCase();
    $(`#${tableBodyId} tr`).each(function () {
        const rowText = $(this).text().toLowerCase();
        $(this).toggle(rowText.indexOf(searchText) !== -1);
    });
});




$(document).ready(function () {


     $('#ulbClickClick').on('click', function () {
                const selectedType = $('input[name="selectionType"]:checked').val();
                fetchModalTableData(
                    '/chart/ulb-feature',
                    'oriModal',
                    '',
                    'ULBs',
                    ['S.No.', 'State/UT', 'ULB/Town','GD', '3rd Party Agency', 'Technology','Package', 'Buffer Area (sq.km)'],
                    'ori-table-body',
                     selectedType
                );
            });

    $('#t1TitleClick').on('click', function () {
                         const selectedType = $('input[name="selectionType"]:checked').val();
                         fetchModalTableData(
                             '/chart/tech1-nadir',
                             'oriModal',
                             ' Aerial Data Acquisition ',
                             'Tech 1 Nadir',
                             ['S.No.', 'State/UT', 'ULB','Technology', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                             'ori-table-body',
                              selectedType
                         );
                     });

    $('#t2TitleClick').on('click', function () {
                         const selectedType = $('input[name="selectionType"]:checked').val();
                         fetchModalTableData(
                             '/chart/tech2-oblique',
                             'oriModal',
                            ' Aerial Data Acquisition ',
                             ' Tech 2 Oblique ',
                             ['S.No.', 'State/UT', 'ULB','Technology', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                             'ori-table-body',
                              selectedType
                         );
                     });

    $('#t3TitleClick').on('click', function () {
                         const selectedType = $('input[name="selectionType"]:checked').val();
                         fetchModalTableData(
                             '/chart/tech3-oblique',
                             'oriModal',
                             ' Aerial Data Acquisition',
                             ' Tech 3 Oblique + Lidar Sensor ',
                             ['S.No.', 'State/UT', 'ULB','Technology', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                             'ori-table-body',
                              selectedType
                         );
                     });

    $('#oriTitleClick').on('click', function () {
    const selectedType = $('input[name="selectionType"]:checked').val();
        fetchModalTableData(
            '/chart/ori-data',
            'oriModal',
            'ORI ',
            'Submission By Vendor',
            ['S.No.', 'State/UT','ULB','Technology', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
             'ori-table-body',
             selectedType
        );
    });

   $('#oriQaQcTitleClick').on('click', function () {
       const selectedType = $('input[name="selectionType"]:checked').val(); // 'gd' or 'state'

       fetchModalTableData(
           '/chart/ori-qaqc-data',
           'oriModal',
           ' ORI',
           ' QA/QC By GD',
           ['S.No.', 'State/UT', 'ULB', 'Technology','GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)', 'Status', 'Completed (%)'],
           'ori-table-body',
           selectedType // <-- pass 'gd' or 'state'
       );
   });



    $('#dsmTitleClick').on('click', function () {

     const selectedType = $('input[name="selectionType"]:checked').val();
                    fetchModalTableData(
                        '/chart/dsm-data',
                        'oriModal',
                        'DSM',
                        'Submission By Vendor',
                        ['S.No.',  'State/UT','ULB','Technology', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                        'ori-table-body',
                        selectedType
                    );
                });

    $('#dsmQaQcTitleClick').on('click', function () {
        const selectedType = $('input[name="selectionType"]:checked').val();

                    fetchModalTableData(
                        '/chart/dsm-qaqc-data',
                        'oriModal',
                        'DSM',
                        'QA/QC By GD',
                        ['S.No.', 'State/UT', 'ULB','Technology', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                        'ori-table-body',
                        selectedType
                    );
                });

    $('#dtmTitleClick').on('click', function () {
                        const selectedType = $('input[name="selectionType"]:checked').val();
                        fetchModalTableData(
                            '/chart/dtm-data',
                            'oriModal',
                            'DTM',
                            'Submission By Vendor',
                            ['S.No.',  'State/UT','ULB', 'Technology','GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                             'ori-table-body',
                              selectedType
                        );
                    });

    $('#dtmQaQcTitleClick').on('click', function () {
                        const selectedType = $('input[name="selectionType"]:checked').val();
                        fetchModalTableData(
                            '/chart/dtm-qaqc-data',
                            'oriModal',
                            'DTM',
                            'QA/QC By GD',
                            ['S.No.', 'State/UT', 'ULB','Technology', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                            'ori-table-body',
                             selectedType
                        );
                    });

     $('#meshQaQcTitleClick').on('click', function () {
                               const selectedType = $('input[name="selectionType"]:checked').val();
                               fetchModalTableData(
                                   '/chart/mesh-qaqc-data',
                                   'oriModal',
                                    '3D Mesh Model',
                                    'QA/QC By GD',

                                   ['S.No.',  'State/UT','Technology', 'ULB', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', ' Completed (%)'],
                                    'ori-table-body',
                                     selectedType
                               );
                           });

    $('#meshTitleClick').on('click', function () {
                            const selectedType = $('input[name="selectionType"]:checked').val();
                            fetchModalTableData(
                                '/chart/mesh-data',
                                'oriModal',
                                 '3D Mesh Model',
                                 'Submission By Vendor',

                                ['S.No.', 'State/UT', 'ULB','Technology', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)', 'Status','Completed (%)'],
                                 'ori-table-body',
                                  selectedType
                            );
                        });



    $('#2DFeatureTitleClick').on('click', function () {
            const selectedType = $('input[name="selectionType"]:checked').val();
            fetchModalTableData(
                '/chart/feori-data',
                'oriModal',
                '2D Feature Extraction (ORI)',
                'Milestone 3A for Tech 1 - Submission By Vendor',
                ['S.No.', 'State/UT', 'ULB','Technology',  'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                 'ori-table-body',
                 selectedType
            );
        });

    $('#2DFeatureExtTitleClick').on('click', function () {
            const selectedType = $('input[name="selectionType"]:checked').val();
            fetchModalTableData(
                '/chart/feori-qaqc-data',
                'oriModal',
                '2D Feature Extraction (ORI)',
                'Milestone 3A for Tech 1 – QA/QC By GD',
                ['S.No.', 'State/UT', 'ULB','Technology',  'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                 'ori-table-body',
                  selectedType
            );
        });
// New Add  component=============================================
        $('#2dFeatureStereoClick').on('click', function () {
                    const selectedType = $('input[name="selectionType"]:checked').val();
                    fetchModalTableData(
                        '/chart/2d-feature-stereo',
                        'oriModal',
                        '2D Feature Extraction in Stereo Mode',
                        'Milestone 3B for Tech 1 - Submission By Vendor',
                        ['S.No.', 'State/UT', 'ULB', 'Technology', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                        'ori-table-body',
                         selectedType
                    );
                });
        $('#2dFeatureExtStereoClick').on('click', function () {
                   const selectedType = $('input[name="selectionType"]:checked').val();
                   fetchModalTableData(
                       '/chart/2d-feature-stereo-ext',
                       'oriModal',
                       '2D Feature Extraction in Stereo Mode',
                       'Milestone 3B for Tech 1 – QA/QC By GD',
                       ['S.No.', 'State/UT', 'ULB','Technology',  'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.) ','Status', 'Completed (%)'],
                       'ori-table-body',
                        selectedType
                   );
               });

     $('#3dFeatureClick').on('click', function () {
                const selectedType = $('input[name="selectionType"]:checked').val();
                fetchModalTableData(
                    '/chart/3d-feature',
                    'oriModal',
                    '2D Feature Extraction For Tech 2 & 3',
                    'Milestone 3A  - Submission By Vendor',
                    ['S.No.', 'State/UT', 'ULB','Technology',  'GD', '3rd Party Agency', 'Cumulative Completed','Status', 'Completed (%)'],
                    'ori-table-body',
                     selectedType
                );
            });

     $('#3dFeatureExtClick').on('click', function () {
                     const selectedType = $('input[name="selectionType"]:checked').val();
                     fetchModalTableData(
                         '/chart/3d-feature-ext',
                         'oriModal',
                         '2D Feature Extraction For Tech 2 & 3 ',
                         'Milestone 3A – QA/QC By GD ',
                         ['S.No.', 'State/UT', 'ULB','Technology',  'GD', '3rd Party Agency', 'Cumulative Completed','Status', 'Completed (%)'],
                         'ori-table-body',
                          selectedType
                     );
                 });

     $('#3dFeatureExtMilestoneClick').on('click', function () {
                         const selectedType = $('input[name="selectionType"]:checked').val();
                         fetchModalTableData(
                             '/chart/3d-feature-ext-milestone',
                             'oriModal',
                             'Final 3D Feature Extraction',
                             'Milestone 3B - Submission By Vendor',
                             ['S.No.', 'State/UT', 'ULB','Technology',  'GD', '3rd Party Agency', 'Cumulative Completed','Status', 'Completed (%)'],
                             'ori-table-body',
                             selectedType
                         );
                     });
     $('#3dFeatureExtQaQcMilestoneClick').on('click', function () {
                         const selectedType = $('input[name="selectionType"]:checked').val();
                         fetchModalTableData(
                             '/chart/3d-feature-ext-quack-milestone',
                             'oriModal',
                             'Final 3D Feature Extraction',
                             'Milestone 3B – QA/QC By GD ',
                             ['S.No.',  'State/UT','ULB','Technology',  'GD', '3rd Party Agency', 'Cumulative Completed','Status', 'Completed (%)'],
                             'ori-table-body',
                              selectedType
                         );
                     });

                     $('#stateSelect').on('change', function () {
                       const selectedType = $('input[name="selectionType"]:checked').val();
                             fetchModalTableData(
                                 '/chart/ori-data',
                                 'oriModal',
                                 ' ',
                                 ' Data Processing (ORI)',
                                  ['S.No.', 'ULB','Technology', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                                  'ori1-table-body',
                                  selectedType
                             );
                         });

                        /*$('#oriQaQcTitleClick').on('click', function () {*/
                        $('#stateSelect').on('change', function () {
                            const selectedType = $('input[name="selectionType"]:checked').val(); // 'gd' or 'state'

                            fetchModalTableData(
                                '/chart/ori-qaqc-data',
                                'oriModal',
                                ' ',
                                ' Data Processing (ORI) – QA/QC',
                                ['S.No.',  'ULB', 'Technology', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)', 'Status', 'Completed (%)'],
                                'ori-qa-qc-table-body',
                                selectedType // <-- pass 'gd' or 'state'
                            );
                        });



                        /* $('#dsmTitleClick').on('click', function () {*/
                     $('#stateSelect').on('change', function () {
                          const selectedType = $('input[name="selectionType"]:checked').val();
                                         fetchModalTableData(
                                             '/chart/dsm-data',
                                             'oriModal',
                                             ' ',
                                             'Data Processing (DSM)',
                                             ['S.No.','ULB','Technology',  '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                                             'dsm-table-body',
                                             selectedType
                                         );
                                     });

                       /*  $('#dsmQaQcTitleClick').on('click', function () {*/
                       $('#stateSelect').on('change', function () {
                             const selectedType = $('input[name="selectionType"]:checked').val();

                                         fetchModalTableData(
                                             '/chart/dsm-qaqc-data',
                                             'oriModal',
                                             '',
                                             'Data Processing (DSM) – QA/QC',
                                             ['S.No.', 'State/UT', 'ULB','Technology', 'GD', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                                             'dsm-qa_qc-table-body',
                                             selectedType
                                         );
                                     });

                         /*$('#dtmTitleClick').on('click', function () {*/
                          $('#stateSelect').on('change', function () {
                                             const selectedType = $('input[name="selectionType"]:checked').val();
                                             fetchModalTableData(
                                                 '/chart/dtm-data',
                                                 'oriModal',
                                                 '',
                                                 'Data Processing (DTM)',
                                                 ['S.No.', 'ULB', 'Technology', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                                                  'dtm-table-body',
                                                   selectedType
                                             );
                                         });

                        /* $('#dtmQaQcTitleClick').on('click', function () {*/
                         $('#stateSelect').on('change', function () {
                                             const selectedType = $('input[name="selectionType"]:checked').val();
                                             fetchModalTableData(
                                                 '/chart/dtm-qaqc-data',
                                                 'oriModal',
                                                 '',
                                                 'Data Processing (DTM) – QA/QC',
                                                 ['S.No.', 'ULB','Technology','3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                                                 'dtm-qa-qc-table-body',
                                                  selectedType
                                             );
                                         });

                          /*$('#meshQaQcTitleClick').on('click', function () {*/
                          $('#stateSelect').on('change', function () {
                                                    const selectedType = $('input[name="selectionType"]:checked').val();
                                                    fetchModalTableData(
                                                        '/chart/mesh-qaqc-data',
                                                        'oriModal',
                                                        '',
                                                        'Data Processing (3D Mesh Model)',
                                                        ['S.No.',  'ULB','Technology', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', ' Completed (%)'],
                                                         '3d-mesh-table-body',
                                                          selectedType
                                                    );
                                                });

                         $('#stateSelect').on('change', function () {
                                                 const selectedType = $('input[name="selectionType"]:checked').val();
                                                 fetchModalTableData(
                                                     '/chart/mesh-data',
                                                     'oriModal',
                                                     '',
                                                     'Data Processing (3D Mesh Model) – QA/QC',
                                                     ['S.No.', 'ULB','Technology', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)', 'Status','Completed (%)'],
                                                      '3d-mesh-qa-qc-table-body',
                                                       selectedType
                                                 );
                                             });



                          $('#stateSelect').on('change', function () {
                                 const selectedType = $('input[name="selectionType"]:checked').val();
                                 fetchModalTableData(
                                     '/chart/feori-data',
                                     'oriModal',
                                     '',
                                     '2D Feature Extraction (ORI)',
                                     ['S.No.', 'ULB','Technology',  '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                                      'feori-data-table-body',
                                      selectedType
                                 );
                             });

                         /*$('#2DFeatureExtTitleClick').on('click', function () {*/
                           $('#stateSelect').on('change', function () {
                                 const selectedType = $('input[name="selectionType"]:checked').val();
                                 fetchModalTableData(
                                     '/chart/feori-qaqc-data',
                                     'oriModal',
                                     '',
                                     '2D Feature Extraction (ORI) – QA/QC',
                                     ['S.No.',  'ULB','Technology', '3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                                      'feori-qa-qc-data-table-body',
                                       selectedType
                                 );
                             });
                     // New Add  component=============================================
                            /* $('#2dFeatureStereoClick').on('click', function () {*/
                            $('#stateSelect').on('change', function () {
                                         const selectedType = $('input[name="selectionType"]:checked').val();
                                         fetchModalTableData(
                                             '/chart/2d-feature-stereo',
                                             'oriModal',
                                             '',
                                             '2D Feature Extraction in Stereo Mode',
                                             ['S.No.',  'ULB', 'Technology','3rd Party Agency', 'Cumulative Completed (Area in sq.km.)','Status', 'Completed (%)'],
                                             '2d-feature-stereo-table-body',
                                              selectedType
                                         );
                                     });
                            /* $('#2dFeatureExtStereoClick').on('click', function () {*/
                             $('#stateSelect').on('change', function () {
                                        const selectedType = $('input[name="selectionType"]:checked').val();
                                        fetchModalTableData(
                                            '/chart/2d-feature-stereo-ext',
                                            'oriModal',
                                            '',
                                            '2D Feature Extraction in Stereo Mode – QA/QC',
                                            ['S.No.',  'ULB','Technology',  '3rd Party Agency', 'Cumulative Completed (Area in sq.km.) ','Status', 'Completed (%)'],
                                            '2d-feature-stereo-qa-qc-table-body',
                                             selectedType
                                        );
                                    });

                         /* $('#3dFeatureClick').on('click', function () {*/
                         $('#stateSelect').on('change', function () {
                                     const selectedType = $('input[name="selectionType"]:checked').val();
                                     fetchModalTableData(
                                         '/chart/3d-feature',
                                         'oriModal',
                                         '',
                                         '2D Feature Extraction For Tech 2 & 3 (Milestone 3A)',
                                         ['S.No.', 'ULB','Technology', '3rd Party Agency', 'Cumulative Completed','Status', 'Completed (%)'],
                                         '3d-feature-table-body',
                                          selectedType
                                     );
                                 });

                           $('#stateSelect').on('change', function () {
                                          const selectedType = $('input[name="selectionType"]:checked').val();
                                          fetchModalTableData(
                                              '/chart/3d-feature-ext',
                                              'oriModal',
                                              '',
                                              ' QA/QC of 3D Feature Extraction (Milestone 3A)',
                                              ['S.No.',  'ULB','Technology',  '3rd Party Agency', 'Cumulative Completed','Status', 'Completed (%)'],
                                              '3d-feature-qa-qc-table-body',
                                               selectedType
                                          );
                                      });

                          /*$('#3dFeatureExtMilestoneClick').on('click', function () {*/
                            $('#stateSelect').on('change', function () {
                                              const selectedType = $('input[name="selectionType"]:checked').val();
                                              fetchModalTableData(
                                                  '/chart/3d-feature-ext-milestone',
                                                  'oriModal',
                                                  '',
                                                  'Final 3D Feature Extraction (Milestone 3B)',
                                                  ['S.No.', 'ULB','Technology', '3rd Party Agency', 'Cumulative Completed','Status', 'Completed (%)'],
                                                  '3d-feature-ext-milestone',
                                                  selectedType
                                              );
                                          });
                           $('#stateSelect').on('change', function () {
                                              const selectedType = $('input[name="selectionType"]:checked').val();
                                              fetchModalTableData(
                                                  '/chart/3d-feature-ext-quack-milestone',
                                                  'oriModal',
                                                  '',
                                                  'QA/QC of Final 3D Feature Extraction (Milestone 3B)',
                                                  ['S.No.','ULB','Technology',  '3rd Party Agency', 'Cumulative Completed','Status', 'Completed (%)'],
                                                  '3d-feature-ext-quack-milestone',
                                                   selectedType
                                              );
                                          });

});
 /*    =====================================================================================            */


function enableSortableHeaders() {
    $('#oriModalTableHeader').on('click', '.sortable', function () {
        const columnIndex = $(this).data('column');

        // Skip sorting for the first column (S.No / index)
        if (columnIndex === 0) return;

        const tableBody = $('#ori-table-body');
        const rows = tableBody.find('tr').toArray();

        rows.sort((a, b) => {
            const aText = $(a).children('td').eq(columnIndex).text();
            const bText = $(b).children('td').eq(columnIndex).text();
            return aText.localeCompare(bText, undefined, { numeric: true });
        });

        // Re-render rows but keep S.No intact
        rows.forEach((row, idx) => {
            $(row).children('td').eq(0).text(idx + 1); // Reset index column
            tableBody.append(row);
        });
    });
}



function enableSortableHeader() {
    const getCellValue = (tr, idx) => tr.children[idx]?.innerText.trim() || "";

    const comparer = (idx, asc) => (a, b) => {
        const v1 = getCellValue(asc ? a : b, idx);
        const v2 = getCellValue(asc ? b : a, idx);

        const f1 = parseFloat(v1.replace(/,/g, ""));
        const f2 = parseFloat(v2.replace(/,/g, ""));

        if (!isNaN(f1) && !isNaN(f2)) return f1 - f2;
        return v1.localeCompare(v2);
    };

    document.querySelectorAll(".sortable").forEach(th => {
        let asc = true;
        th.style.cursor = "pointer";

        th.addEventListener("click", () => {
            const table = th.closest("table");
            const tbody = table.querySelector("tbody");
            const index = parseInt(th.getAttribute("data-column")) - 1; // FIXED HERE
            const rows = Array.from(tbody.querySelectorAll("tr"));

            rows.sort(comparer(index, asc));
            asc = !asc;

            rows.forEach(row => tbody.appendChild(row));
        });
    });
}
/*
function enableFlyingSearch() {
    const input = document.getElementById('flyingSearchInput');
    const table = document.getElementById('flyingCompletedT1T2Body');

    input.addEventListener('input', function () {
        const filter = input.value.trim().toLowerCase();
        const rows = table.getElementsByTagName('tr');

        Array.from(rows).forEach(row => {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(filter) ? '' : 'none';
        });
    });
}*/


$(document).ready(function () {

    });


document.addEventListener("DOMContentLoaded", function () {
    let chartInstance = null;

    const labelMap = {
        ulb: "Flying Completed in Tech 1 Nadir",
        t1: "Flying Completed in Tech 1 Nadir",
        t2: "Flying Completed in Tech 2 Oblique",
        t3: "Flying Completed in Tech 3 Oblique + Lidar Sensor",
        ori: "ORI Submitted for QA/QC",
        ori_qaqc: "QA/QC of ORI",
        cumulative: "DEM Submitted for QA/QC",
        cumulative_qaqc: "QA/QC of DEM",
        dsm: "DSM Submitted for QA/QC",
        dsm_qaqc: "QA/QC of DSM",
        dtm: "DTM Submitted for QA/QC",
        dtm_qaqc: "QA/QC of DTM",
        mesh: "3D Mesh Model Submitted for QA/QC",
        mesh_qaqct: "QA/QC of 3D Mesh",
        feature_extraction_ori: "2D Feature Extraction Submitted for QA/QC",
        feature_extraction_ori_qaqc: "QA/QC of 2D Feature Extraction"
    };

    function loadSubmittedData(type, value) {
        $("#wait").css("display", "block");

        let url = "/chart/submitted-data-processing?";
        if (type === "gd") {
            url += "gdName=" + encodeURIComponent(value);
        } else {
            url += "gdStateName=" + encodeURIComponent(value);
        }

        fetch(url)
            .then(response => response.json())
            .then(data => {
                $("#wait").css("display", "none");

                if (Object.keys(data).length === 0) {
                    document.getElementById('resultBox').innerHTML = "<p>No data available.</p>";
                    return;
                }

                const labels = Object.keys(data).map(key => labelMap[key] || key);
                const values = Object.values(data);

                Object.entries(data).forEach(([key, value]) => {
                    const element = document.getElementById(key);
                    if (element) {
                        element.innerHTML = `<div class="label-value"><span class="gradient-text">${value}</span></div>`;
                    }
                });

                if (chartInstance !== null) {
                    chartInstance.destroy();
                }

                const ctx = document.getElementById('processingBarChart').getContext('2d');
                chartInstance = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: labels,
                        datasets: [{
                            label: 'Data Processing Counts',
                            data: values,
                            backgroundColor: [
                                '#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b',
                                '#858796', '#5a5c69', '#20c9a6', '#ff6384', '#36a2eb',
                                '#9966ff', '#ffcd56'
                            ],
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        scales: {
                            y: {
                                beginAtZero: true,
                                ticks: {
                                    precision: 0
                                }
                            }
                        },
                        plugins: {
                            legend: { display: false },
                            tooltip: {
                                callbacks: {
                                    label: function (context) {
                                        return `${context.dataset.label}: ${context.parsed.y}`;
                                    }
                                }
                            }
                        }
                    }
                });
            })
            .catch(error => {
                $("#wait").css("display", "none");
                console.error("Error fetching data:", error);
                document.getElementById('resultBox').innerHTML = "<p>Error loading data.</p>";
            });
    }

    // ✅ Load GD and State dropdowns
   fetch('/chart/gd-names')
           .then(response => response.json())
           .then(data => {
               const select = document.getElementById('gdNameSelect');
               data.forEach(gd => {
                   const option = document.createElement('option');
                   option.value = gd.gdName;
                   option.textContent = gd.gdName;
                   select.appendChild(option);
               });

               // ✅ Load default GD data (i.e. gdName = "all") after dropdown is ready

   			 loadSubmittedData("gd", $("#gdNameSelect").val());
           })
           .catch(error => {
               console.error("Error loading GD names:", error);
           });

    fetch("/chart/getStates/UTs")
        .then(res => res.json())
        .then(data => {
            const stateSelect = $("#stateSelect");
             data.forEach(state => {
                stateSelect.append(`<option value="${state.stateName}">${state.stateName}</option>`);
            });
        });

         fetch("/chart/getStates/UTs")
                .then(res => res.json())
                .then(data => {

                    const stateSelect = $("#getState");
                    data.forEach(state => {
                        stateSelect.append(`<option value="${state.stateName}">${state.stateName}</option>`);
                    });
                });

fetch("/chart/getStates/UTs")
                .then(res => res.json())
                .then(data => {

                    const stateSelect = $("#state");
                    data.forEach(state => {
                        stateSelect.append(`<option value="${state.stateName}">${state.stateName}</option>`);
                    });
                });
    // ✅ Radio change event
    $('input[name="selectionType"]').change(function () {
        const type = $(this).val();

        // Reset the dropdown to "all"
        if (type === "gd") {
            $("#gdNameSelect").val("all");
            loadSubmittedData("gd", "all");
            $("#gdDropdownGroup").show();
            $("#stateDropdownGroup").hide();
             $("#stateDropdownGroups").hide();
        } else {
            $("#stateSelect").val("all");
            loadSubmittedData("state", "all");
            $("#gdDropdownGroup").hide();
            $("#stateDropdownGroup").show();
             $("#stateDropdownGroups").show();
        }
    });

    // ✅ Dropdown change events
    $("#gdNameSelect").change(function () {
        loadSubmittedData("gd", $(this).val());
    });

    $("#stateSelect").change(function () {
        loadSubmittedData("state", $(this).val());
    });

});

// 🔹 Add click handler for ULB count links
$(document).on('click', '.ulb-count-link', function(e) {
    e.preventDefault();
    let ulbNames = $(this).data('ulb-names');
    let contractorName = $(this).data('contractor');
    
        if (ulbNames && ulbNames.trim() !== '') {
            // Parse ULB names with states and create table rows
            let ulbTableRows = ulbNames.split(', ').map((item, index) => {
                // Extract ULB name and state from format "ULB Name (State Name)"
                // Handle various formats and clean up the state name
                let match = item.match(/^(.+?)\s*\((.+?)\)\s*$/);
                let ulbName, stateName;
                
                if (match) {
                    ulbName = match[1].trim();
                    // Clean up state name - keep the full state name
                    let stateText = match[2].trim();
                    
                    // Handle cases like "Ward 20&6) (Jharkhand" -> extract "Jharkhand"
                    // Look for state names at the end after any extra characters
                    let stateMatch = stateText.match(/([A-Za-z\s]+?)(?:\s*\))?\s*$/);
                    if (stateMatch) {
                        stateName = stateMatch[1].trim();
                        // Keep the full state name - don't remove any characters
                        // Only remove trailing parentheses and extra spaces
                        stateName = stateName.replace(/\s*\)\s*$/, '').trim();
                    } else {
                        stateName = stateText.replace(/\s*\)\s*$/, '').trim();
                    }
                } else {
                    // If no parentheses format, try to extract from other patterns
                    ulbName = item.trim();
                    stateName = 'Unknown';
                }
                
                // Map generic 'UT' to specific Union Territory based on ULB context
                // so that the popup shows full state names like 'Chandigarh (UT)'
                (function () {
                    if (!stateName) return;
                    const upperState = stateName.toUpperCase();
                    if (upperState === 'UT' || upperState === 'UNION TERRITORY') {
                        const u = ulbName ? ulbName.toUpperCase() : '';
                        if (u.includes('CHANDIGARH')) {
                            stateName = 'Chandigarh (UT)';
                            return;
                        }
                        if (
                            u.includes('BISHNAH') || u.includes('AWANTIPORA') || u.includes('KATRA') ||
                            u.includes('PATTAN') || u.includes('SRINAGAR') || u.includes('JAMMU') ||
                            u.includes('ANANTNAG') || u.includes('BARAMULLA')
                        ) {
                            stateName = 'Jammu & Kashmir (UT)';
                            return;
                        }
                        if (u.includes('MURUNGAPAKKAM') || u.includes('PUDUCHERRY') || u.includes('PONDICHERRY')) {
                            stateName = 'Puducherry (UT)';
                            return;
                        }
                        if (u.includes('DELHI') || u.includes('NEW DELHI')) {
                            stateName = 'Delhi (UT)';
                            return;
                        }
                        if (u.includes('LEH') || u.includes('KARGIL')) {
                            stateName = 'Ladakh (UT)';
                            return;
                        }
                        if (u.includes('DADRA') || u.includes('NAGAR HAVELI') || u.includes('DAMAN') || u.includes('DIU')) {
                            stateName = 'Dadra and Nagar Haveli and Daman and Diu (UT)';
                            return;
                        }
                        if (u.includes('PORT BLAIR') || u.includes('ANDAMAN')) {
                            stateName = 'Andaman and Nicobar Islands (UT)';
                            return;
                        }
                    }
                })();
                
                return `
                    <tr>
                        <td>${index + 1}</td>
                        <td>${ulbName}</td>
                        <td>${stateName}</td>
                    </tr>
                `;
            }).join('');
            
            let modalContent = `
                <div class="modal-header bg-info text-white">
                    <h5 class="modal-title">ULB List for ${contractorName}</h5>
                    <button type="button" class="btn btn-danger btn-round" data-bs-dismiss="modal" aria-label="Close">Close</button>
                </div>
                <div class="modal-body">
                    <p><strong>Total ULBs:</strong> ${ulbNames.split(', ').length}</p>
                    <div class="table-responsive">
                        <table class="table table-striped table-bordered">
                            <thead>
                                <tr style="background-color: #17a2b8; color: white;">
                                    <th style="width: 10%; background-color: #17a2b8; color: white; border: 1px solid #dee2e6;">S.No.</th>
                                    <th style="width: 50%; background-color: #17a2b8; color: white; border: 1px solid #dee2e6;">ULB Name</th>
                                    <th style="width: 40%; background-color: #17a2b8; color: white; border: 1px solid #dee2e6;">State Name</th>
                                </tr>
                            </thead>
                            <tbody>
                                ${ulbTableRows}
                            </tbody>
                        </table>
                    </div>
                </div>
            `;
        
        $('#ulbListModalContent').html(modalContent);
        $('#ulbListModal').modal('show');
    }
});

/*=================== TimelineTable  =========================================*/

$(document).ready(function () {
    // Hide the div by default
    $("#dataShowStateWis").hide();

    // On state select change
    $("#stateSelect").on("change", function () {
        const selectedValue = $(this).val();

        if (selectedValue !== "all" && selectedValue !== "") {
            $("#dataShowStateWis").show();
        } else {
            $("#dataShowStateWis").hide();
        }
    });
});
document.addEventListener("DOMContentLoaded", function () {
    const stateSelect = document.getElementById("stateSelect");
    const dataDiv = document.getElementById("dataShowStateWis");

    // Hide the div by default
    dataDiv.style.display = "none";

    stateSelect.addEventListener("change", function () {
        if (this.value !== "all" && this.value !== "") {
            dataDiv.style.display = "block";
        } else {
            dataDiv.style.display = "none";
        }
    });
});

/*=======================================*/
/*$(document).ready(function () {
    const rowsPerPage = 10;
    let currentPage = 1;
    let tableData = []; // Will hold AJAX response
    let currentFilter = 'all'; // Track current filter state

 function renderTable() {
     let tbody = $("#summaryTable tbody");
     tbody.empty();

     const start = (currentPage - 1) * rowsPerPage;
     const end = start + rowsPerPage;
     const pageData = tableData.slice(start, end);

     if (pageData.length === 0) {
         tbody.append('<tr><td colspan="51" class="text-center">No data available</td></tr>');
         setStickyLeft();
         return;
     }

     // Get all column headers to map data-comp attributes
     const headers = $("#summaryTable thead th[data-comp]");

     pageData.forEach((rowData, rowIndex) => {
         let rowHtml = "<tr>";
         rowHtml += `<td>${start + rowIndex + 1}</td>`; // S.No.

         rowData.forEach((cell, cellIndex) => {
             // Get the corresponding header for this column
             const header = headers.eq(cellIndex);
             const dataComp = header.attr('data-comp') || '';

             rowHtml += `<td data-comp="${dataComp}">${cell || ''}</td>`;
         });

         rowHtml += "</tr>";
         tbody.append(rowHtml);
     });

     renderPagination();
     setStickyLeft();
     applyFilter(currentFilter); // Reapply current filter after render
 }

    function renderPagination() {
        const container = $("#paginationContainer");
        container.empty();
        const pageCount = Math.ceil(tableData.length / rowsPerPage);

        for (let i = 1; i <= pageCount; i++) {
            const btn = $(`<button>${i}</button>`);
            btn.css({ margin: '0 3px', padding: '5px 10px', cursor: 'pointer' });
            if (i === currentPage) btn.css('font-weight', 'bold');

            btn.on('click', function () {
                currentPage = i;
                renderTable();
            });

            container.append(btn);
        }
    }

   function loadStateWiseSummary(state) {
       $.ajax({
           url: '/chart/state-wise-soi-summary',
           type: 'GET',
           data: { state: state },
           success: function (response) {
               stateName = response.stateName;
               gdName = response.gdName;
               tableData = response.tableData || [];
               contractors = response.contractors || []; // ✅ new contractors list

               currentPage = 1;
               renderTable();
               initEventHandlers();

               $("#stateNameCell").text(stateName);
               $("#gdNameCell").text(gdName);

               // ✅ Example: log contractors to console
               console.log("Contractors:", contractors);

               // ✅ Example: display contractors in a dropdown
               let contractorDropdown = $("#contractorDropdown");
               contractorDropdown.empty();
               contractors.forEach(function (c) {
                   contractorDropdown.append(`<option value="${c}">${c}</option>`);
               });
           },
           error: function () {
               alert("Error loading table data");
           }
       });
   }


    function applyFilter(filter) {
        currentFilter = filter;

        // Hide all component columns (both th and td)
        $('th[data-comp], td[data-comp]').hide();

        if (filter === 'all') {
            // Show all columns
            $('th[data-comp], td[data-comp]').show();
        } else {
            // Show selected component columns
            $(`th[data-comp="${filter}"], td[data-comp="${filter}"]`).show();
        }

        // Always show common columns (no data-comp)
        $('th:not([data-comp]), td:not([data-comp])').show();

        setStickyLeft();
        setActiveButton($(`.comp-filter[data-comp="${filter}"]`));
    }

    function setActiveButton(button) {
        $('.comp-filter').removeClass('active');
        $(button).addClass('active');
    }

   $(document).ready(function() {
     // Function to set sticky column positions
     function setStickyColumns() {
       const firstColWidth = $("#summaryTable th[rowspan='5']:nth-of-type(1)").outerWidth();
       const secondColWidth = $("#summaryTable th[rowspan='5']:nth-of-type(2)").outerWidth();
       const thirdColWidth = $("#summaryTable th[rowspan='5']:nth-of-type(3)").outerWidth();

       // Adjust left positions for sticky columns
       $("#summaryTable td:nth-child(2)").css("left", firstColWidth + "px");
       $("#summaryTable td:nth-child(3)").css("left", (firstColWidth + secondColWidth) + "px");
       $("#summaryTable td:nth-child(4)").css("left", (firstColWidth + secondColWidth + thirdColWidth) + "px");
     }

     // Initial setup
     setStickyColumns();

     // Recalculate on window resize
     $(window).resize(function() {
       setStickyColumns();
     });
   });

    // Call on document ready, window resize, and after table updates
    $(document).ready(function() {
        setStickyLeft();

        // Recalculate when window resizes
        $(window).resize(function() {
            setStickyLeft();
        });

        // Recalculate after AJAX loads
        $(document).ajaxComplete(function() {
            setTimeout(setStickyLeft, 100); // Small delay to allow DOM update
        });
    });

    // Initialize event handlers
    function initEventHandlers() {
        // State dropdown change
        $("#stateSelect").on("change", function () {
            loadStateWiseSummary($(this).val());
        });

        // Filter buttons
        $('.comp-filter').on('click', function() {
            const filter = $(this).data('comp');
            applyFilter(filter);
            setActiveButton(this);
        });
    }

    // Initial setup
    initEventHandlers();
    loadStateWiseSummary($("#stateSelect").val());
    applyFilter(currentFilter);
});

$(window).resize(function () {
    setStickyLeft();
});*/

/*===========================================*/
$(document).ready(function () {
    // Load default chart
    loadStateSoiSummary("all");

    // Listen for state change
    $("#state").on("change", function() {
        let stateName = $(this).val();
        loadStateSoiSummary(stateName);
    });
});

function loadStateSoiSummary(stateName) {
    $.ajax({
        url: "/chart/states-soi-summary",
        type: "GET",
        data: { stateName: stateName },
        success: function (response) {
            if (response.status === "success") {
                let chartData = [];
                let data = response.data[0]; // backend sends a single map inside list

                for (const key in data) {
                    if (data.hasOwnProperty(key)) {
                        chartData.push({ name: key, y: parseFloat(data[key]) || 0 });
                    }
                }

                render3DPieChart("stateSoiPieChart", chartData, stateName);
            } else {
                alert(response.message || "No data found");
            }
        },
        error: function () {
            alert("Error fetching chart data");
        }
    });
}

// Gradient colors for slices
const gradientColors = [
    ['#4099ff', '#73b4ff'],  // Blue
    ['#FF5370', '#ff869a'],  // Red
    ['#2ed8b6', '#59e0c5'],  // Teal
    ['#FFB64D', '#ffcb80'],  // Orange
    ['#FE8A7D', '#feb8b0'],  // Coral
    ['#69CEC6', '#8fdbd5'],  // Aqua
    ['#6f42c1', '#a074e8'],  // Purple
    ['#ffa500', '#ffd580'],  // Amber
    ['#20b2aa', '#66cfcf'],  // Light Sea Green
    ['#dc143c', '#f08080']   // Crimson
];

function render3DPieChart(containerId, chartData, stateName) {
    Highcharts.chart(containerId, {
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 50,
                beta: 0
            }
        },
        title: {
            text: (stateName === "all" ? "All States/UTs" : stateName)
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.y}</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                depth: 75,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '<b>{point.name}</b>: {point.y}'
                }
            }
        },
        exporting: {
            enabled: true,        // enable exporting module
            buttons: {
                contextButton: {
                    menuItems: [
                        'viewFullscreen',  // Full screen option
                        'printChart',
                        'downloadPNG',
                        'downloadJPEG',
                        'downloadPDF',
                        'downloadSVG'
                    ]
                }
            }
        },
        series: [{
            name: 'Count',
            colorByPoint: true,
            data: chartData.map((item, index) => ({
                name: item.name,
                y: item.y,
                color: {
                    linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
                    stops: [
                        [0, gradientColors[index % gradientColors.length][0]],
                        [1, gradientColors[index % gradientColors.length][1]]
                    ]
                }
            }))
        }]
    });
}




/*==========23-08-2025 ===================================================*/


$(document).ready(function () {
    let table = $('#area-summary-table').DataTable({
        destroy: true,
        ajax: {
            url: '/chart/soi-state-area-summary',
            dataSrc: ''
        },
        columns: [
            {
                data: null,
                render: function (data, type, row, meta) {
                    return meta.row + 1; // initial S.No
                },
                orderable: false, // disable sorting on S.No
                searchable: false
            },
            { data: 'ulbName' },
            { data: 'stateName' },
            { data: 'gdName' },
            { data: 'contractor' },
            { data: 'technology' },
            {
                data: 'sanctionedArea',
                className: "dt-body-right",
                type: "num"
            },
            {
                data: 'bufferAreaDataAcquisition',
                className: "dt-body-right",
                type: "num"
            }
        ],
        createdRow: function (row, data) {
            if ((data.ulbName || '').toUpperCase() === "TOTAL") {
                $(row).css({
                    "font-weight": "bold",
                    "background-color": "#f0f0f0"
                });
            }
        },
        paging: false,
        searching: true,
        ordering: true,
        info: false,
        order: [[1, "asc"]],
        drawCallback: function (settings) {
            // ✅ Calculate totals when table is drawn
            let api = this.api();

            // total ULB count (excluding TOTAL row)
            let totalULB = api
                .column(1, { search: 'applied' })
                .data()
                .filter(name => name !== "TOTAL")
                .count();

            // sum of sanctionedArea
            let totalSanctioned = api
                .column(6, { search: 'applied' })
                .data()
                .reduce((a, b) => (Number(a) || 0) + (Number(b) || 0), 0);

            // sum of bufferAreaDataAcquisition
            let totalBuffer = api
                .column(7, { search: 'applied' })
                .data()
                .reduce((a, b) => (Number(a) || 0) + (Number(b) || 0), 0);

            // show in divs
            $('#totalUlbDiv').text("Total ULB: " + totalULB);
            $('#totalSanctionedDiv').text("Total Sanctioned Area: " + totalSanctioned.toLocaleString('en-IN') +" sq. km");
            $('#totalBufferDiv').text("Total Buffer Area Acquisition: " + totalBuffer.toLocaleString('en-IN')+" sq. km");
        }
    });

    // 🔑 Recalculate S.No on every sort or search
    table.on('order.dt search.dt', function () {
        table.column(0, { search: 'applied', order: 'applied' })
             .nodes()
             .each(function (cell, i) {
                 cell.innerHTML = i + 1;
             });
    }).draw();

    // 🔹 Populate State Dropdown once data is loaded
    table.on('xhr', function () {
        let json = table.ajax.json();
        let states = [...new Set(json.map(d => d.stateName))]; // unique states
        states.sort(); // ascending order

        let select = $('#stateFilters');
        select.empty();
        select.append('<option value="">All States/UTs</option>');
        states.forEach(state => {
            select.append(`<option value="${state}">${state}</option>`);
        });
    });

    // 🔹 Filter table by State selection
    $('#stateFilters').on('change', function () {
        let val = $.fn.dataTable.util.escapeRegex($(this).val());
        if (val) {
            // exact match filter on stateName column (col index 2)
            table.column(2).search('^' + val + '$', true, false).draw();
        } else {
            // show all
            table.column(2).search('').draw();
        }
    });
});





 /*======27-08-2025=====================*/

$(document).ready(function () {


  // 🔹 initialize DataTable
  var table = $('#stateDataTable').DataTable({
      destroy: true,
      searching: true,
      paging: false,
      info: false,
      ordering: true,
      order: [[1, 'asc']],   // ✅ Default sort by State Name ASC
      columnDefs: [
          { orderable: false, targets: 0 } // disable sorting on S.No column
      ]
  });


    // 🔹 Load dropdown + data initially (ALL states)
    loadStats(null);

    // 🔹 On state selection (⚡ fixed: use #state because your select id="state")
    $('#state').on('change', function () {
        let stateName = $(this).val();
        if (stateName === "all") {
            stateName = null; // backend handles null as all states
        }
        loadStats(stateName);
    });

    // 🔹 function to fetch & render data
    function loadStats(stateName) {
        $.ajax({
            url: '/chart/sio-state-vendor-status-summary',
            type: 'GET',
            data: stateName ? { stateName: stateName } : {},
           success: function (allStats) {
               // clear table
               table.clear();

               // 🔹 Normalize response: wrap object in array if single
               if (!Array.isArray(allStats)) {
                   allStats = [allStats];
               }

               // Populate dropdown once
             // Populate dropdown once
             if ($('#state option').length <= 1) {
                 $.each(allStats, function (i, stats) {
                     $('#state').append('<option value="' + stats["state_name"] + '">' + stats["state_name"] + '</option>');
                 });

                 // ✅ Sort the dropdown options (except "All States/UTs")
                 let $select = $('#state');
                 let $firstOption = $select.find('option:first'); // keep "All States/UTs"
                 let $options = $select.find('option:not(:first)').sort(function (a, b) {
                     return $(a).text().localeCompare($(b).text());
                 });
                 $select.empty().append($firstOption).append($options);
             }


               // Loop through contractors
               $.each(allStats, function (index, stats) {
                   let ulbCount = stats["ulb_name_count"] || 0;
                   let ulbNames = stats["ulb_names"] || "";
                   let contractorName = stats["contractor_name"] || "UNKNOWN";
                   
                   // Create clickable ULB count link
                   let ulbCountLink = ulbCount > 0 ? 
                       `<a href="#" class="ulb-count-link btn btn-sm btn-outline-primary" style="border-radius: 20px;" 
                         data-ulb-names="${ulbNames}" data-contractor="${contractorName}">${ulbCount}</a>` : 
                       ulbCount;
                   
                   table.row.add([
                       "",
                       contractorName,
                       stats["state_name"] || "UNKNOWN",
                       ulbCountLink, // Clickable ULB count

                       stats["technology_completed_count"] || 0,

                       stats["ori_cumulative_status_3_5_count"] || 0,

                       stats["dsm_submission_status_3_5_count"] || 0,

                       stats["dtm_submission_status_3_5_count"] || 0,
                       stats["threeD_Meshmodel_status_count"] || 0,
                       stats["threeD_Meshmodel_status_3_5_count"] || 0,
                       stats["twoD_featureextraction_status_count"] || 0,
                       stats["twoD_featureextraction_status_3_5_count"] || 0,
                       stats["twoD_featextr_stereomode_status_count"] || 0,
                       stats["twoD_featextr_stereomode_status_3_5_count"] || 0,

                       stats["threeD_featextr_status_3_5_count"] || 0,

                       stats["threeD_milestone_status_3_5_count"] || 0
                   ]);
               });

               // draw table
               table.draw(false);
           },

            error: function (xhr) {
                alert("Error fetching stats: " + xhr.responseText);
            }
        });
    }

    // 🔹 Fix S.No column (renumber after sort/search)
    table.on('order.dt search.dt', function () {
        table.column(0, { search: 'applied', order: 'applied' })
            .nodes()
            .each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
    }).draw();

});



$(document).ready(function () {


  // 🔹 initialize DataTable
  var table = $('#stateGdDataTable').DataTable({
      destroy: true,
      searching: true,
      paging: false,
      info: false,
      ordering: true,
      order: [[1, 'asc']],   // ✅ Default sort by State Name ASC
      columnDefs: [
          { orderable: false, targets: 0 } // disable sorting on S.No column
      ]
  });


    // 🔹 Load dropdown + data initially (ALL states)
    loadStats(null);

    // 🔹 On state selection (⚡ fixed: use #state because your select id="state")
    $('#stateGd').on('change', function () {
        let stateName = $(this).val();
        if (stateName === "all") {
            stateName = null; // backend handles null as all states
        }
        loadStats(stateName);
    });

    // 🔹 function to fetch & render data
    function loadStats(stateName) {
        $.ajax({
            url: '/chart/sio-state-gd-status-summary',
            type: 'GET',
            data: stateName ? { stateName: stateName } : {},
           success: function (allStats) {
               // clear table
               table.clear();

               // 🔹 Normalize response: wrap object in array if single
               if (!Array.isArray(allStats)) {
                   allStats = [allStats];
               }

               // Populate dropdown once
             // Populate dropdown once
             if ($('#stateGd option').length <= 1) {
                 $.each(allStats, function (i, stats) {
                     $('#stateGd').append('<option value="' + stats["state_name"] + '">' + stats["state_name"] + '</option>');
                 });

                 // ✅ Sort the dropdown options (except "All States/UTs")
                 let $select = $('#stateGd');
                 let $firstOption = $select.find('option:first'); // keep "All States/UTs"
                 let $options = $select.find('option:not(:first)').sort(function (a, b) {
                     return $(a).text().localeCompare($(b).text());
                 });
                 $select.empty().append($firstOption).append($options);
             }


               // Loop through states
               $.each(allStats, function (index, stats) {
                   table.row.add([
                       "",
                       stats["gd_name"] || "UNKNOWN",

                       stats["ulb_name_count"] || 0,

                       stats["technology_completed_count"] || 0,

                       stats["ori_cumulative_status_6_8_count"] || 0,

                       stats["dsm_submission_status_6_8_count"] || 0,

                       stats["dtm_submission_status_6_8_count"] || 0,
                       stats["threeD_Meshmodel_status_count"] || 0,
                       stats["threeD_Meshmodel_status_6_8_count"] || 0,
                       stats["twoD_featureextraction_status_count"] || 0,
                       stats["twoD_featureextraction_status_6_8_count"] || 0,
                       stats["twoD_featextr_stereomode_status_count"] || 0,
                       stats["twoD_featextr_stereomode_status_6_8_count"] || 0,
                       stats["threeD_featextr_status_6_8_count"] || 0,
                       stats["threeD_milestone_status_6_8_count"] || 0
                   ]);
               });

               // draw table
               table.draw(false);
           },

            error: function (xhr) {
                alert("Error fetching stats: " + xhr.responseText);
            }
        });
    }

    // 🔹 Fix S.No column (renumber after sort/search)
    table.on('order.dt search.dt', function () {
        table.column(0, { search: 'applied', order: 'applied' })
            .nodes()
            .each(function (cell, i) {
                cell.innerHTML = i + 1;
            });
    }).draw();

});


/*=======================================*/
/*=======================  Data Uploaded by GDs on NAKSHA Portal(Yes/No) with Count More Details =============================================*/
let modalData = []; // store the modal data globally
let dataTable = null; // to hold the DataTable instance

function fetchDataUploadedByGD() {
    fetch('/chart/data-uploaded-by-gd-naksha-portal')
        .then(response => {
            if (!response.ok) {
                throw new Error("Network response was not ok");
            }
            return response.json();
        })
        .then(data => {
            if (data.error) {
                alert("Error: " + data.error);
                return;
            }

            modalData = data.gdList || [];
            populateModalStates(data.states || []);
            renderModalTable(modalData);
        })
        .catch(error => {
            console.error("Fetch error:", error);
            alert("Failed to fetch data from server.");
        });
}

function populateModalStates(states) {
    const select = document.getElementById('modalStateSelect');
    select.innerHTML = '<option value="all">-- All State/UT --</option>'; // reset

    states.sort().forEach(state => {
        const option = document.createElement('option');
        option.value = state;
        option.textContent = state;
        select.appendChild(option);
    });
}

function renderModalTable(data) {
    const table = $('#modalDataTable');

    // Destroy existing DataTable if already initialized
    if (dataTable) {
        dataTable.destroy();
    }

    const tbody = table.find('tbody');
    tbody.empty(); // clear existing rows

    data.forEach((item, index) => {
        const row = `
            <tr>
                <td></td> <!-- Index will be filled dynamically -->
                <td>${item.state}</td>
                <td>${item.gd}</td>
                <td>${item.ulb}</td>
                <td>${item.uploaded}</td>
            </tr>
        `;
        tbody.append(row);
    });

    // Initialize DataTable
    dataTable = table.DataTable({
        paging: false,
        searching: false,
        info: false,
        ordering: true,
        columnDefs: [
            { orderable: false, targets: 0 } // Disable sorting on first column (#)
        ]
    });

    // Update the index column on each draw (sort, filter, etc.)
    dataTable.on('order.dt search.dt draw.dt', function () {
        dataTable.column(0, { search: 'applied', order: 'applied' }).nodes().each(function (cell, i) {
            cell.innerHTML = i + 1;
        });
    }).draw();
}

function filterModalData() {
    const selectedState = document.getElementById('modalStateSelect').value;
    if (selectedState === 'all') {
        renderModalTable(modalData);
    } else {
        const filtered = modalData.filter(item => item.state === selectedState);
        renderModalTable(filtered);
    }
}























