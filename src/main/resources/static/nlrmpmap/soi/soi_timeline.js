




document.addEventListener('DOMContentLoaded', function () {
    const stateSelect = document.getElementById('stateSelect');
    const timelineBtn = document.getElementById('timelineButton');
    const timelineDiv = document.getElementById('timelineDiv');

    stateSelect.addEventListener('change', function () {
        const selected = stateSelect.value;

        // ✅ Hide and clear the timeline div whenever state changes
        timelineDiv.innerHTML = '';
        timelineDiv.style.display = 'none';

        // Show or hide the timeline button depending on selected state
        timelineBtn.style.display = (selected && selected !== 'all') ? 'inline-block' : 'none';
    });

    timelineBtn.addEventListener('click', function () {
        const state = stateSelect.value;
        if (!state || state === 'all') return;

        fetch(`/chart/flying-status-timelines?state=${encodeURIComponent(state)}`)
            .then(response => response.json())
            .then(data => {
                if (data.status === 'error') {
                    timelineDiv.innerHTML = `<div class="alert alert-danger">${data.message}</div>`;
                    timelineDiv.style.display = 'block';
                    return;
                }

                const rowsHTML = data.data.map((row, i) => {
                    const tech = row.technology?.toLowerCase().trim() || '';
                    let technologySpecificTDs = '';

                    if (tech === 'tech 1') {
                        technologySpecificTDs = `
                            ${renderDateTd(row.qaQc2DCompletion, row.qaQc2DTentative)}
                            ${renderDateTd(row.feature2DCompletion, row.feature2DTentative)}
                            ${renderDateTd(row.stereo2DCompletion, row.stereo2DTentative)}
                            ${renderDateTd(row.qaQcStereo2DCompletion, row.qaQcStereo2DTentative)}
                        `;
                    } else if (tech === 'tech 2' || tech === 'tech 3') {
                        technologySpecificTDs = `
                            ${renderDateTd(row.threeDFeatExtCompletion, row.threeDFeatExtTentative)}
                            ${renderDateTd(row.threeDFeatExtQaQcCompletion, row.threeDFeatExtQaQcTentative)}
                            ${renderDateTd(row.threeDMilestoneCompletion, row.threeDMilestoneTentative)}
                            ${renderDateTd(row.threeDMilestoneQaQcCompletion, row.threeDMilestoneQaQcTentative)}
                        `;
                    } else {
                        technologySpecificTDs = `<td colspan="4" style="background: #f2f2f2;"></td>`;
                    }

                    return `
                        <tr>
                            <td class="sticky sticky-1">${i + 1}</td>
                            <td class="sticky sticky-2">${row.ulbName ?? '-'}</td>
                            <td class="sticky sticky-3">${row.package ?? '-'}</td>
                            <td class="sticky sticky-4">${row.technology ?? '-'}</td>
                            <td>${row.contractor ?? '-'}</td>
                            <td>${row.commencement ?? '-'}</td>
                            <td>${row.bufferArea ?? '-'}</td>
                            ${renderDateTd(row.completionDate, row.tentativeDate)}
                            ${renderDateTd(row.oriCompletionDate, row.oriTentativeDate)}
                            ${renderDateTd(row.oriQaqcCompletionDate, row.oriQaqcTentativeDate)}
                            ${renderDateTd(row.milestone2Completion, row.milestone2Tentative)}
                            ${renderDateTd(row.qaQcMilestone2Completion, row.qaQcMilestone2Tentative)}
                            ${technologySpecificTDs}
                        </tr>
                    `;
                }).join('');

                timelineDiv.innerHTML = `
                    <div class="table-container" style="max-height: calc(15 * 60px); overflow-y: auto;">
                        <table id="timelineTable" class="custom-grid" border="1" style="width: 100%; border-collapse: collapse; table-layout: auto;">
                            <thead style="position: sticky; top: 0; background-color: #fff; z-index: 1;">
                                <tr>
                                    <th colspan="20" style="background-color: #1797ff;">
                                        <h4 class="text-center my-2 text-white">Timeline for ${data.stateName}</h4>
                                    </th>
                                </tr>
                                <tr style="background-color: #ffff;">
                                    <td colspan="20">
                                        <strong>Total ULBs:</strong> ${data.ulbCount} &nbsp;&nbsp;&nbsp;
                                        <strong>Total Area:</strong> ${data.totalArea.toFixed(2)} sq.km
                                        &nbsp;&nbsp;&nbsp;
                                        <span style="display: inline-block; width: 30px; height: 12px; background: linear-gradient(45deg, rgb(105, 206, 198), rgb(143, 219, 213)); border: 1px solid #155724; margin-left: 40px;"></span>
                                        <span style="color: #155724;">Completion Date</span>
                                        <span style="display: inline-block; width: 30px; height: 12px; background: linear-gradient(45deg, #FFB64D, #ffcb80); border: 1px solid #856404; margin-left: 20px;"></span>
                                        <span style="color: #856404;">Tentative Date </span>
                                        <span style="display: inline-block; width: 30px; height: 12px; background: linear-gradient(45deg, rgb(255, 83, 112), rgb(255, 134, 154)); border: 1px solid #856404; margin-left: 20px;"></span>
                                        <span style="color: #BF360C;">Not Entered Yet </span>
                                    </td>
                                </tr>
                                <tr>
                                    <th class="sticky sticky-1">S.No.</th>
                                    <th class="sticky sticky-2">ULB/Town</th>
                                    <th class="sticky sticky-3">Package</th>
                                    <th class="sticky sticky-4">Technology</th>
                                    <th>3rd Party Agency</th>
                                    <th>Contract Signing Date</th>
                                    <th>Buffer Area for Data Acquisition (sq.km)</th>
                                    <th>Date of Aerial Data Acquisition (Completion / Tentative)</th>
                                    <th>Submission Date of ORI (Completion / Tentative)</th>
                                    <th>QA/QC Date of ORI (Completion / Tentative)</th>
                                    <th>Submission Date of Milestone 2 (Completion / Tentative)</th>
                                    <th>QA/QC Date of Milestone 2 (Completion / Tentative)</th>
                                    <th>Submission Date of Feature Extraction Milestone 3A (Completion / Tentative)</th>
                                    <th>QA/QC Date of Feature Extraction Milestone 3A (Completion / Tentative)</th>
                                    <th>Submission Date of Feature Extraction Milestone 3B (Completion / Tentative)</th>
                                    <th>QA/QC Date of Feature Extraction Milestone 3B (Completion / Tentative)</th>
                                </tr>
                            </thead>
                            <tbody>${rowsHTML}</tbody>
                        </table>
                    </div>
                `;

                // ✅ Make sure the timeline becomes visible after loading
                timelineDiv.style.display = 'block';
            })
            .catch(err => {
                console.error(err);
                timelineDiv.innerHTML = `<div class="alert alert-danger">Something went wrong.</div>`;
                timelineDiv.style.display = 'block';
            });
    });

    // ✅ Helper for date cells
    function renderDateTd(completion, tentative) {
        const isValid = val => val != null && val.toString().trim() !== '';
        if (isValid(completion)) {
            return `<td style="background: linear-gradient(45deg, rgb(105, 206, 198), rgb(143, 219, 213)); color:#0a0a06;">${completion}</td>`;
        } else if (isValid(tentative)) {
            return `<td style="background: linear-gradient(45deg, #FFB64D, #ffcb80); color:#0a0a06;">${tentative}</td>`;
        } else {
            return `<td style="background: linear-gradient(45deg, rgb(255, 83, 112), rgb(255, 134, 154)); color:#0a0a06;"></td>`;
        }
    }
});






/*======Show Component by Table =====================================================*/
$(document).ready(function () {
    // Current state variable
    let currentState = 'all';
    let currentTable = 'allSummaryTable'; // Track currently visible table

    // Hide all tables initially
    $('table[id$="SummaryTable"]').hide();

    // Initialize with first table visible
    $('#allSummaryTable').show();
    $('.table-toggle[data-table="allSummaryTable"]').addClass('active');

    // State dropdown change handler
    $('#stateSelect').change(function () {
        currentState = $(this).val();

        // Clear all table bodies and show loading message
        $('table[id$="SummaryTable"] tbody').empty().append(
            '<tr><td colspan="61" class="text-center">Loading data...</td></tr>'
        );

        // Reset to "All" table
        $('table[id$="SummaryTable"]').hide();
        $('#allSummaryTable').show();
        currentTable = 'allSummaryTable';

        // Update active button
        $('.table-toggle').removeClass('active');
        $('.table-toggle[data-table="allSummaryTable"]').addClass('active');

        // Reload data for all tables (but only show the All table initially)
        loadTableData('allComp', 'allSummaryTable');
        loadTableData('aerial', 'aerialSummaryTable');
        loadTableData('ori', 'oriSummaryTable');
        loadTableData('dsm', 'dsmSummaryTable');
        loadTableData('dtm', 'dtmSummaryTable');
        loadTableData('mesh', '3DMeshSummaryTable');
        loadTableData('2dfeat', '2DFeatureSummaryTable');
        loadTableData('3dfeat', '3DFeatureSummaryTable');
    });

    // Handle table button clicks
    $('.table-toggle').click(function () {
        const tableId = $(this).data('table');
        const dataType = $(this).data('type');
        currentTable = tableId;

        // Update UI
        $('table[id$="SummaryTable"]').hide();
        $('#' + tableId).show();
        $('.table-toggle').removeClass('active');
        $(this).addClass('active');

        // Reload data if table is empty or if it's the All table
        if ($('#' + tableId + ' tbody').children().length <= 1) { // <=1 to account for possible loading message
            loadTableData(dataType, tableId);
        }
    });

function loadTableData(dataType, tableId) {
    $.ajax({
        url: '/chart/state-wise-soi-summary',
        type: 'GET',
        data: { state: currentState },
        success: function(response) {
            if (!response) {
                showError(tableId, "Invalid response from server");
                return;
            }

            // ✅ Display state details
            const stateName = response.stateName || 'All States';
            $('#stateNameSpan').text(stateName);

            const totalUlb = response.totalUlb;
            $('#totalUlb').text(totalUlb);

            const totalBufferArea = response.totalBufferArea;
            const formattedBufferArea = Number(totalBufferArea).toLocaleString("en-IN", {
                minimumFractionDigits: 2,
                maximumFractionDigits: 2
            });
            $('#totalBufferArea').text(formattedBufferArea + ' sq.km');

            // ✅ Select correct dataset
            let tableData;
            switch (dataType) {
                case 'allComp': tableData = response.allComp || []; break;
                case 'aerial':  tableData = response.aerial || []; break;
                case 'ori':     tableData = response.ori || []; break;
                case 'dsm':     tableData = response.dsm || []; break;
                case 'dtm':     tableData = response.dtm || []; break;
                case 'mesh':    tableData = response.mesh || []; break;
                case '2dfeat':  tableData = response.twoDfeat || []; break;
                case '3dfeat':  tableData = response.threeDfeat || []; break;
                default:
                    showError(tableId, "Unknown data type: " + dataType);
                    return;
            }

            const tbody = $('#' + tableId + ' tbody');
            tbody.empty();

            if (tableData.length === 0) {
                tbody.append('<tr><td colspan="61" class="text-center">No data available</td></tr>');
                return;
            }

            // ✅ Build table rows
            displayTableData(tbody, tableData);

            // ✅ Fix: get all rows from this table only
            const allRows = document.querySelectorAll(`#${tableId} tbody tr`);

            allRows.forEach(row => {
                const cells = row.querySelectorAll('td');
                cells.forEach((cell, colIndex) => {
                    cell.addEventListener('mouseenter', () => {
                        row.classList.add('highlight-row');
                        allRows.forEach(r => {
                            const colCell = r.children[colIndex];
                            if (colCell) colCell.classList.add('highlight-col');
                        });
                        cell.classList.add('hover-cell');
                    });

                    cell.addEventListener('mouseleave', () => {
                        row.classList.remove('highlight-row');
                        allRows.forEach(r => {
                            const colCell = r.children[colIndex];
                            if (colCell) colCell.classList.remove('highlight-col');
                        });
                        cell.classList.remove('hover-cell');
                    });
                });
            });
        },
        error: function(xhr, status, error) {
            showError(tableId, "Error loading data: " + error);
        }
    });
}

function displayTableData(tbody, data) {
    data.forEach((item, index) => {
        let row = `<tr><td>${index + 1}</td>`;
        item.forEach((col, i) => {
            if (i === 4 || i === 7 || i === 10 || i === 13 || i === 15 || i === 15 || i === 18 || i === 21 || i === 24 || i === 27 || i === 30 || i === 33 || i === 36 ||
            i === 39 || i === 42 || i === 45 ||  i === 48 || i === 51 || i === 54 ||i === 57 ||i === 60  ) {
                row += `<td>${getStatusLabel1(col)}</td>`;
            } else {
                row += `<td>${col}</td>`;
            }
        });
        row += '</tr>';
        tbody.append(row);
    });
}




    // Error handling
    function showError(tableId, message) {
        const tbody = $('#' + tableId + ' tbody');
        tbody.empty();
        tbody.append(`<tr><td colspan="61" class="text-center error">${message}</td></tr>`);
        console.error(message);
    }

    // Load initial data
    loadTableData('allComp', 'allSummaryTable');
});

/*================================================================*/
const completionStatusMap1 = {};

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

            completionStatusMap1[originalDesc] = `label1 ${cssClass}`;
        });
    });


function getStatusLabel1(statusText) {
    const labelClass = completionStatusMap1[statusText] || 'label1';
    return `<span class="${labelClass}">${statusText}</span>`;
}

/*=========Backup  Time line========================================================= */

$(document).ready(function () {
    $.ajax({
        url: '/chart/backup-timelines-dates',
        type: 'GET',
        success: function (response) {
            let $select = $('#backupDateSelect');
            $select.empty();
            $select.append('<option value="">-- Select Date --</option>');

            if (response && response.length > 0) {
                response.forEach(function (date) {
                    $select.append('<option value="' + date + '">' + date + '</option>');
                });
            } else {
                $select.append('<option disabled>No dates found</option>');
            }
        },
        error: function () {
            alert('Failed to load backup dates');
        }
    });
});


$(document).ready(function () {
    const $stateSelect = $('#stateSelect');
    const $timelineBtn = $('#timelineButton');
    const $oldTimelineBtn = $('#oldTimelineButton');
    const $backupDateContainer = $('#backupDateContainer');
    const $backupDateSelect = $('#backupDateSelect');
    const $timelineResult = $('#timelineBackupTable');

    // 1️⃣ Show buttons when state is selected
    $stateSelect.on('change', function () {
        const state = $(this).val();

        // Always reset table and date dropdown on state change
        $timelineResult.html('');

        $backupDateSelect.empty().append('<option value="">-- Select Date --</option>');
        $backupDateContainer.hide();

        if (state && state !== 'all') {
            $timelineBtn.show();
            $oldTimelineBtn.show();
        } else {
            $timelineBtn.hide();
            $oldTimelineBtn.hide();
        }
    });

    // 2️⃣ Old Timeline button click → show date dropdown
    $oldTimelineBtn.on('click', function () {
        $backupDateContainer.show();

        $.ajax({
            url: '/chart/backup-timelines-dates',
            type: 'GET',
            success: function (response) {
                $backupDateSelect.empty();
                $backupDateSelect.append('<option value="">-- Select Date --</option>');

                if (response && response.length > 0) {
                    response.forEach(function (date) {
                        $backupDateSelect.append('<option value="' + date + '">' + date + '</option>');
                    });
                } else {
                    $backupDateSelect.append('<option disabled>No dates found</option>');
                }
            },
            error: function () {
                alert('Failed to load backup dates');
            }
        });
    });

    // 3️⃣ When backup date is selected → call API with state + backupDate
    $backupDateSelect.on('change', function () {
        const state = $stateSelect.val();
        const backupDate = $(this).val();

        if (!state || state === 'all' || !backupDate) {
            $timelineResult.html('');
            return;
        }

        fetch(`/chart/flying-status-timelines-backup?state=${encodeURIComponent(state)}&backupDate=${encodeURIComponent(backupDate)}`)
            .then(response => response.json())
            .then(data => {
                if (data.backupStatus === 'error') {
                    $timelineResult.html(`<div class="alert alert-danger">${data.message}</div>`);
                    return;
                }

                const rowsHTML = data.backupDdata.map((row, i) => {
                    const tech = row.technology?.toLowerCase().trim() || '';

                    let technologySpecificTDs = '';
                    if (tech === 'tech 1') {
                        technologySpecificTDs = `
                            ${renderDateTd(row.qaQc2DCompletion, row.qaQc2DTentative)}
                            ${renderDateTd(row.feature2DCompletion, row.feature2DTentative)}
                            ${renderDateTd(row.stereo2DCompletion, row.stereo2DTentative)}
                            ${renderDateTd(row.qaQcStereo2DCompletion, row.qaQcStereo2DTentative)}
                        `;
                    } else if (tech === 'tech 2' || tech === 'tech 3') {
                        technologySpecificTDs = `
                            ${renderDateTd(row.threeDFeatExtCompletion, row.threeDFeatExtTentative)}
                            ${renderDateTd(row.threeDFeatExtQaQcCompletion, row.threeDFeatExtQaQcTentative)}
                            ${renderDateTd(row.threeDMilestoneCompletion, row.threeDMilestoneTentative)}
                            ${renderDateTd(row.threeDMilestoneQaQcCompletion, row.threeDMilestoneQaQcTentative)}
                        `;
                    } else {
                        technologySpecificTDs = `<td colspan="4" style="background: #f2f2f2;"></td>`;
                    }

                    return `
                        <tr>
                            <td class="sticky sticky-1" >${i + 1}</td>
                            <td class="sticky sticky-2">${row.ulbName ?? '-'}</td>
                            <td class="sticky sticky-3" >${row.package ?? '-'}</td>
                            <td class="sticky sticky-4">${row.technology ?? '-'}</td>
                            <td>${row.contractor ?? '-'}</td>
                            <td>${row.commencement ?? '-'}</td>
                            <td>${row.bufferArea ?? '-'}</td>
                            ${renderDateTd(row.completionDate, row.tentativeDate)}
                            ${renderDateTd(row.oriCompletionDate, row.oriTentativeDate)}
                            ${renderDateTd(row.oriQaqcCompletionDate, row.oriQaqcTentativeDate)}
                            ${renderDateTd(row.milestone2Completion, row.milestone2Tentative)}
                            ${renderDateTd(row.qaQcMilestone2Completion, row.qaQcMilestone2Tentative)}
                            ${technologySpecificTDs}
                        </tr>
                    `;
                }).join('');

                $timelineResult.html(`
                    <div class="modal-body">
                        <div class="table-responsive table-container" style="max-height: calc(15 * 50px); overflow-y: auto;">
                            <table id="timelineTable" class="custom-grid" border="1" style="width: 100%; border-collapse: collapse; table-layout: auto;">
                                <thead style="position: sticky; top: 0; background-color: #fff; z-index: 1;">
                                    <tr>
                                        <th colspan="20" style="background-color: #1797ff;">
                                            <h4 class="text-center my-2 text-white">Old Timeline for ${data.backupStateName}</h4>
                                        </th>
                                    </tr>
                                    <tr style="background-color: #ffff;">
                                        <td colspan="20">
                                            <strong>Total ULBs:</strong> ${data.backupUlbCount} &nbsp;&nbsp;&nbsp;
                                            <strong>Total Area:</strong> ${data.backupTotalArea.toFixed(2)} sq.km
                                            &nbsp;&nbsp;&nbsp;

                                            <span style="display: inline-block; width: 30px; height: 12px; background: linear-gradient(45deg, rgb(105, 206, 198), rgb(143, 219, 213)); border: 1px solid #155724; margin-left: 40px;"></span>
                                            <span style="color: #155724;">Completion Date</span>
                                            <span style="display: inline-block; width: 30px; height: 12px; background: linear-gradient(45deg, #FFB64D, #ffcb80); border: 1px solid #856404; margin-left: 20px;"></span>
                                            <span style="color: #856404;">Tentative Date</span>
                                            <span style="display: inline-block; width: 30px; height: 12px; background: linear-gradient(45deg, rgb(255, 83, 112), rgb(255, 134, 154)); border: 1px solid #856404; margin-left: 20px;"></span>
                                            <span style="color: #BF360C;">Not Entered Yet</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <th class="sticky sticky-1">S.No.</th>
                                        <th class="sticky sticky-2">ULB/Town</th>
                                        <th class="sticky sticky-3">Package</th>
                                        <th class="sticky sticky-4">Technology</th>
                                        <th>3rd Party Agency</th>
                                        <th>Contract Signing Date</th>
                                        <th>Buffer Area for Data Acquisition (sq.km)</th>
                                        <th>Date of Aerial Data Acquisition (Completion / Tentative)</th>
                                        <th>Submission Date of ORI (Completion / Tentative)</th>
                                        <th>QA/QC Date of ORI (Completion / Tentative)</th>
                                        <th>Submission Date of Milestone 2 (Completion / Tentative)</th>
                                        <th>QA/QC Date of Milestone 2 (Completion / Tentative)</th>
                                        <th>Submission Date of Feature Extraction Milestone 3A (Completion / Tentative)</th>
                                        <th>QA/QC Date of Feature Extraction Milestone 3A (Completion / Tentative)</th>
                                        <th>Submission Date of Feature Extraction Milestone 3B (Completion / Tentative)</th>
                                        <th>QA/QC Date of Feature Extraction Milestone 3B (Completion / Tentative)</th>
                                    </tr>
                                </thead>
                                <tbody>${rowsHTML}</tbody>
                            </table>
                        </div>
                    </div>
                `);

                // Initialize draggable functionality for backup timeline
                initializeDraggableModal();
            })
            .catch(err => {
                console.error(err);
                $timelineResult.html(`<div class="alert alert-danger">Something went wrong.</div>`);
            });
    });

    // ✅ Helper for date cells
     function renderDateTd(completion, tentative) {
           const isValid = val => val != null && val.toString().trim() !== '';

           if (isValid(completion)) {
               return `<td style="background: linear-gradient(45deg, rgb(105, 206, 198), rgb(143, 219, 213)); color:#0a0a06;">${completion}</td>`;
           } else if (isValid(tentative)) {
               return `<td style="background: linear-gradient(45deg, #FFB64D, #ffcb80); color:#0a0a06;">${tentative}</td>`;
           } else {
               return `<td style="background: linear-gradient(45deg, rgb(255, 83, 112), rgb(255, 134, 154)); color:#0a0a06;"></td>`;
           }
       }

});


// Draggable Modal Functionality
// This function makes the timeline modal draggable by adding mouse and touch event listeners
// Users can click and drag anywhere on the modal to move the window anywhere on the screen
function initializeDraggableModal() {
    const modal = document.getElementById('timelineModal');
    const modalDialog = modal.querySelector('.modal-dialog');
    const modalContent = modal.querySelector('.modal-content');
    
    if (!modal || !modalDialog || !modalContent) {
        console.warn('Modal elements not found for dragging functionality');
        return;
    }
    
    let isDragging = false;
    let currentX;
    let currentY;
    let initialX;
    let initialY;
    let xOffset = 0;
    let yOffset = 0;
    let dragStartElement = null;
    
    // Add draggable class to modal
    modal.classList.add('modal-draggable');
    
    // Make the entire modal content draggable
    modalContent.addEventListener('mousedown', dragStart);
    document.addEventListener('mousemove', drag);
    document.addEventListener('mouseup', dragEnd);
    
    // Touch events for mobile support
    modalContent.addEventListener('touchstart', dragStart);
    document.addEventListener('touchmove', drag);
    document.addEventListener('touchend', dragEnd);
    
    function dragStart(e) {
        // Don't start dragging if clicking on interactive elements
        if (e.target.tagName === 'BUTTON' || 
            e.target.tagName === 'INPUT' || 
            e.target.tagName === 'SELECT' || 
            e.target.tagName === 'A' ||
            e.target.closest('button') ||
            e.target.closest('input') ||
            e.target.closest('select') ||
            e.target.closest('a') ||
            e.target.closest('.table-responsive') ||
            e.target.closest('table')) {
            return;
        }
        
        if (e.type === "touchstart") {
            initialX = e.touches[0].clientX - xOffset;
            initialY = e.touches[0].clientY - yOffset;
        } else {
            initialX = e.clientX - xOffset;
            initialY = e.clientY - yOffset;
        }
        
        isDragging = true;
        dragStartElement = e.target;
        modal.classList.add('modal-dragging');
        
        // Add visual feedback
        modalContent.style.cursor = 'grabbing';
    }
    
    function drag(e) {
        if (isDragging) {
            e.preventDefault();
            
            if (e.type === "touchmove") {
                currentX = e.touches[0].clientX - initialX;
                currentY = e.touches[0].clientY - initialY;
            } else {
                currentX = e.clientX - initialX;
                currentY = e.clientY - initialY;
            }
            
            xOffset = currentX;
            yOffset = currentY;
            
            // Apply transform to modal dialog
            modalDialog.style.transform = `translate(${currentX}px, ${currentY}px)`;
        }
    }
    
    function dragEnd(e) {
        if (isDragging) {
            initialX = currentX;
            initialY = currentY;
            isDragging = false;
            dragStartElement = null;
            modal.classList.remove('modal-dragging');
            
            // Remove visual feedback
            modalContent.style.cursor = '';
        }
    }
    
    // Reset position when modal is hidden
    modal.addEventListener('hidden.bs.modal', function() {
        modalDialog.style.transform = '';
        modalContent.style.cursor = '';
        xOffset = 0;
        yOffset = 0;
        currentX = 0;
        currentY = 0;
        initialX = 0;
        initialY = 0;
        isDragging = false;
        dragStartElement = null;
    });
}





