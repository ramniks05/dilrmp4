$(function () {
    // === Constants ===
    const metricColors = {
      totalSPMUPositionsSanctioned: 'rgba(255, 206, 86, 0.7)',
      totalProfessionalsRecruited: 'rgba(75, 192, 192, 0.7)',
      teamsFormedForsanctioned: 'rgba(153, 102, 255, 0.7)',
      teamsFormedForFieldSurvey: 'rgba(255, 159, 64, 0.7)',
      roversSanctioned: 'rgba(255, 99, 132, 0.7)',
      roversProcured: 'rgba(54, 162, 235, 0.7)'
    };

    const metricLabels = {
      totalSPMUPositionsSanctioned: 'SPMU Positions Sanctioned',
      totalProfessionalsRecruited: 'Professionals Recruited For SPMU',
      teamsFormedForsanctioned: 'Teams Formed For Sanctioned',
      teamsFormedForFieldSurvey: 'Teams Formed For Field Survey',
      roversSanctioned: 'Rovers Sanctioned',
      roversProcured: 'Rovers Procured'
    };

    // === Utility Function ===
    const normalize = val => val != null ? val : 0;

    // === Load Summary Data ===
    $.ajax({
      url: "/ulb/state-rovers-summary",
      method: "GET",
      beforeSend: function () {
        $("#wait").show();
      },
      success: function (data) {
        $("#wait").hide();

        if (data.length > 0) {
          const summary = data[0]; // ✅ access first object

          $('#totalSPMUPositionsSanctioned').text(normalize(summary.totalSPMUPositionsSanctioned));
          $('#totalProfessionalsRecruited').text(normalize(summary.totalProfessionalsRecruited));
          $('#teamsFormedForsanctioned').text(normalize(summary.teamsFormedForsanctioned));
          $('#teamsFormedForFieldSurvey').text(normalize(summary.teamsFormedForFieldSurvey));
          $('#roversSanctioned').text(normalize(summary.roversSanctioned));
          $('#nodalDepartmentName').text(normalize(summary.nodalDepartmentName));



          // prepare chart with `summary`
          const labels = [
            metricLabels.totalSPMUPositionsSanctioned,
            metricLabels.totalProfessionalsRecruited,
            metricLabels.teamsFormedForsanctioned,
            metricLabels.teamsFormedForFieldSurvey,
            metricLabels.roversSanctioned,
            metricLabels.roversProcured
          ];

          const values = [
            normalize(summary.totalSPMUPositionsSanctioned),
            normalize(summary.totalProfessionalsRecruited),
            normalize(summary.teamsFormedForsanctioned),
            normalize(summary.teamsFormedForFieldSurvey),
            normalize(summary.roversSanctioned),
            normalize(summary.roversProcured)
          ];

          renderCombinedChart('barChart', labels, values, values, Object.values(metricColors), Object.values(metricColors));
          renderDonutChart('donutChart1', labels.slice(0, 2), values.slice(0, 2), Object.values(metricColors).slice(0, 2), Object.values(metricColors).slice(0, 2));
          renderDonutChart('donutChart2', labels.slice(2, 4), values.slice(2, 4), Object.values(metricColors).slice(2, 4), Object.values(metricColors).slice(2, 4));
          renderDonutChart('donutChart3', labels.slice(4, 6), values.slice(4, 6), Object.values(metricColors).slice(4, 6), Object.values(metricColors).slice(4, 6));
        } else {
          $('#roversSummary').html("<p class='text-danger'>No summary data available.</p>");
        }
      },

      error: function () {
        $("#wait").hide();
        $('#roversSummary').html("<p class='text-danger'>Failed to load summary data.</p>");
      }
    });

    // === Chart Rendering Functions ===
    function renderCombinedChart(containerId, labels, barData, lineData, bgColor, borderColor) {
      new Chart(document.getElementById(containerId), {
        type: 'bar',
        data: {
          labels: labels,
          datasets: [
            {
              type: 'bar',
              label: 'Bar',
              data: barData,
              backgroundColor: bgColor,
              borderColor: borderColor,
              borderWidth: 1
            },
            {
              type: 'line',
              label: 'Line',
              data: lineData,
              borderColor: 'black',
              backgroundColor: 'transparent',
              tension: 0.4,
              pointBackgroundColor: 'black'
            }
          ]
        },
        options: {
          responsive: true,
          scales: {
            y: {
              beginAtZero: true
            }
          }
        }
      });
    }

    function renderDonutChart(containerId, labels, data, bgColor, borderColor) {
      new Chart(document.getElementById(containerId), {
        type: 'doughnut',
        data: {
          labels: labels,
          datasets: [{
            label: 'Rovers Summary',
            data: data,
            backgroundColor: bgColor,
            borderColor: borderColor,
            borderWidth: 1
          }]
        },
        options: {
          responsive: true,
          cutout: '50%'
        }
      });
    }
  });