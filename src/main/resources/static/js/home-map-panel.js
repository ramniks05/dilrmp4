/* DILRMP-MIS 4.0 home page: State/UT details panel beside the map (click, search, legend, districts, shareable link). */
(function () {
    'use strict';

    var BANDS = [
        { label: '0 - 30%', colour: '#FF0000' },
        { label: '30 - 65%', colour: '#FF9A00' },
        { label: '65 - 80%', colour: '#FFDB00' },
        { label: '80 - 100%', colour: '#059212' }
    ];
    var COMPONENT_LABELS = {
        clr: 'Computerization of Land Records', MapDigitization: 'Digitization of Maps / FMBs / Tippans', sro: 'Computerization of Registration Process (SROs)',
        mrr: 'Modern Record Room (Tehsil)', surveyReservey: 'Survey / Re-survey', rccms: 'Computerization of Revenue Courts',
        linkedwithaadhaar: 'Consent-based Aadhaar integration with RoR', sroModernization: 'Modernization of SROs',
        legacyRegistered: 'Scanning of Legacy Registered Documents', legacyRevenue: 'Scanning of Legacy Revenue Records'
    };
    var MIS_MAP_TITLES = {
        sroModernization: 'Percentage of SROs modernized',
        legacyRegistered: 'Percentage of legacy registered document pages digitized',
        legacyRevenue: 'Percentage of legacy revenue record pages digitized'
    };
    var pendingMisMap = null;

    function isStateLevelOnly(comp) {
        var list = (mapData && mapData.stateLevelOnly) || ['sro'];
        return list.indexOf(comp) >= 0;
    }

    function stateLevelNote() {
        return '<p class="hp-note hp-note--box">' + esc(COMPONENT_LABELS[component]) + ' is reported at State/UT level only, so districts are shown as outlines.</p>';
    }

    window.hpLoadGenericMap = function (id) {
        if (!mapData) { pendingMisMap = id; return; }
        pendingMisMap = null;
        if (typeof window.hpGenericStateMap === 'function') {
            window.hpGenericStateMap(MIS_MAP_TITLES[id] || COMPONENT_LABELS[id], mapData.percents[id] || {});
        }
    };

    var mapData = null;
    var component = 'clr';
    var current = null;
    var currentDistrict = null;
    var view = 'national';
    var iconsUrl = '';

    function el(id) { return document.getElementById(id); }

    function esc(text) {
        return String(text == null ? '' : text).replace(/[&<>"']/g, function (ch) {
            return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[ch];
        });
    }

    function titleCase(name) {
        return String(name || '').toLowerCase().replace(/(^|[\s(&-])([a-z])/g, function (m, p, c) { return p + c.toUpperCase(); })
            .replace(/\bAnd\b/g, 'and');
    }

    function indian(v) { return Math.round(Number(v) || 0).toLocaleString('en-IN'); }

    function icon(name) {
        return '<svg aria-hidden="true"><use href="' + iconsUrl + '#' + name + '"/></svg>';
    }

    function band(p) {
        if (p == null) { return -1; }
        if (p <= 30) { return 0; }
        if (p <= 65) { return 1; }
        if (p <= 80) { return 2; }
        return 3;
    }

    function pctText(p) { return p == null ? 'Not reported' : Number(p).toFixed(1) + '%'; }

    function stateName(id) {
        var s = (mapData && mapData.states || []).filter(function (x) { return String(x.id) === String(id); })[0];
        return s ? s.name : '';
    }

    function show(which) {
        view = which;
        el('hp-details-national').hidden = which !== 'national';
        el('hp-details-dynamic').hidden = which === 'national';
        el('hp-map-back').hidden = !window.hpSelectedStateId;
    }

    function updateUrl() {
        if (!window.history || !window.history.replaceState) { return; }
        var params = new URLSearchParams(window.location.search);
        if (component && component !== 'clr') { params.set('component', component); } else { params.delete('component'); }
        if (current) { params.set('state', current.id); } else { params.delete('state'); }
        if (current && currentDistrict) { params.set('district', currentDistrict.lgd); } else { params.delete('district'); }
        var qs = params.toString();
        window.history.replaceState(null, '', window.location.pathname + (qs ? '?' + qs : '') + window.location.hash);
    }

    function backToIndia() {
        current = null;
        currentDistrict = null;
        window.hpSelectedStateId = null;
        el('hp-state-search').value = '';
        if (typeof window.hpResetMapExtent === 'function') { window.hpResetMapExtent(); }
        show('national');
        updateUrl();
    }

    function headHtml(kicker, title, backLabel, backAction) {
        return '<div class="hp-dyn__head"><span class="hp-kicker">' + esc(kicker) + '</span><h2 id="hp-dyn-title">' + esc(title) + '</h2>'
            + '<div class="hp-dyn__tools"><button type="button" class="hp-btn hp-btn--outline hp-btn--sm" data-dyn-back="' + backAction + '">'
            + icon('i-back') + esc(backLabel) + '</button>'
            + '</div></div>';
    }

    function renderState(d) {
        var rows = d.components.map(function (c) {
            var s = c.state == null ? null : Math.min(100, c.state);
            var i = c.india == null ? null : Math.min(100, c.india);
            var b = band(s);
            return '<li class="' + (c.key === component ? 'is-current' : '') + (c.onMap ? ' is-map' : '') + '"'
                + (c.onMap ? ' data-dyn-component="' + c.key + '" tabindex="0" role="button" title="Show on map"' : '') + '>'
                + '<div class="hp-dyn__row"><span>' + esc(c.label) + '</span><b>' + pctText(c.state) + '</b></div>'
                + '<span class="hp-vs"><i style="width:' + (s || 0).toFixed(1) + '%;background:' + (b >= 0 ? BANDS[b].colour : '#d9d9d9') + '"></i>'
                + (i == null ? '' : '<em style="left:' + i.toFixed(1) + '%" title="All India ' + i.toFixed(1) + '%"></em>') + '</span>'
                + '<small>All India ' + pctText(c.india) + '</small></li>';
        }).join('');

        var groups = {};
        var order = [];
        d.services.forEach(function (s) {
            if (!groups[s.group]) { groups[s.group] = []; order.push(s.group); }
            groups[s.group].push(s);
        });
        var yesCount = d.services.filter(function (s) { return s.yes; }).length;
        var services = order.map(function (g) {
            return '<div class="hp-dyn__svc"><b>' + esc(g) + '</b><div>' + groups[g].map(function (s) {
                return '<span class="hp-svc-chip ' + (s.yes ? 'is-yes' : 'is-no') + '">' + (s.yes ? '&#10003; ' : '') + esc(s.label) + '</span>';
            }).join('') + '</div></div>';
        }).join('');

        el('hp-details-dynamic').innerHTML = headHtml('State/UT details', titleCase(d.name), 'All India', 'india')
            + '<div class="hp-dyn__facts"><span><b>' + indian(d.districts) + '</b>Districts</span><span><b>' + indian(d.tehsils) + '</b>Tehsils</span>'
            + '<span><b>' + indian(d.villages) + '</b>Villages</span></div>'
            + '<p class="hp-note">District MIS submitted by ' + d.districtsReported + ' of ' + d.districtsTotal + ' districts'
            + (d.lastUpdate ? ' &middot; last updated ' + esc(d.lastUpdate) : '') + '</p>'
            + '<h3 class="hp-mini-title">Component progress <small>(bar = State/UT, mark = All India)</small></h3>'
            + '<ul class="hp-dyn__list">' + rows + '</ul>'
            + (isStateLevelOnly(component) ? stateLevelNote()
                : '<button type="button" class="hp-btn hp-btn--saffron hp-btn--sm hp-dyn__districts" data-dyn-districts>'
                    + 'Districts: ' + esc(COMPONENT_LABELS[component]) + icon('i-arrow') + '</button>')
            + '<h3 class="hp-mini-title">Citizen services online <small>(' + yesCount + ' of ' + d.services.length + ')</small></h3>' + services;
        show('state');
    }

    var districtCache = {};

    function loadDistricts(stateId) {
        var key = stateId + '|' + component;
        if (districtCache[key]) { return $.Deferred().resolve(districtCache[key]).promise(); }
        return $.ajax({ url: '/site/home-preview/state/' + encodeURIComponent(stateId) + '/districts', data: { component: component }, method: 'GET' })
            .then(function (list) { districtCache[key] = list; return list; });
    }

    function focusDistrictOnMap(lgd) {
        if (typeof window.hpHighlightDistrict === 'function') { window.hpHighlightDistrict(lgd); }
        if (typeof window.hpZoomToDistrict === 'function') { window.hpZoomToDistrict(lgd); }
    }

    function drawDistricts(stateId) {
        if (typeof window.hpShowDistricts !== 'function') { return; }
        var restoreHighlight = function () {
            if (currentDistrict) { focusDistrictOnMap(currentDistrict.lgd); }
        };
        if (isStateLevelOnly(component)) {
            window.hpShowDistricts(stateId, {}, COMPONENT_LABELS[component], restoreHighlight);
            return;
        }
        loadDistricts(stateId).then(function (list) {
            if (String(window.hpSelectedStateId) !== String(stateId)) { return; }
            var byLgd = {};
            list.forEach(function (d) { if (d.lgd != null) { byLgd[d.lgd] = d.percent; } });
            window.hpShowDistricts(stateId, byLgd, COMPONENT_LABELS[component], restoreHighlight);
        });
    }

    function renderDistrict(d) {
        var rows = d.components.map(function (c) {
            var v = c.district == null ? null : Math.min(100, c.district);
            var s = c.state == null ? null : Math.min(100, c.state);
            var b = band(v);
            return '<li class="' + (c.key === component ? 'is-current' : '') + (c.onMap ? ' is-map' : '') + '"'
                + (c.onMap ? ' data-dyn-component="' + c.key + '" tabindex="0" role="button" title="Colour the map by this component"' : '') + '>'
                + '<div class="hp-dyn__row"><span>' + esc(c.label) + '</span><b>' + (c.stateLevelOnly ? 'State/UT level only' : pctText(c.district)) + '</b></div>'
                + '<span class="hp-vs"><i style="width:' + (v || 0).toFixed(1) + '%;background:' + (b >= 0 ? BANDS[b].colour : '#d9d9d9') + '"></i>'
                + (s == null ? '' : '<em style="left:' + s.toFixed(1) + '%" title="' + esc(titleCase(d.stateName)) + ' ' + s.toFixed(1) + '%"></em>') + '</span>'
                + '<small>' + esc(titleCase(d.stateName)) + ' ' + pctText(c.state) + ' &middot; All India ' + pctText(c.india) + '</small></li>';
        }).join('');
        el('hp-details-dynamic').innerHTML = '<div class="hp-dyn__head"><span class="hp-kicker">District details</span>'
            + '<h2 id="hp-dyn-title">' + esc(titleCase(d.name)) + '</h2><p class="hp-note">' + esc(titleCase(d.stateName)) + '</p>'
            + '<div class="hp-dyn__tools">'
            + '<button type="button" class="hp-btn hp-btn--outline hp-btn--sm" data-dyn-back="state">' + icon('i-back') + esc(titleCase(d.stateName)) + '</button>'
            + '<button type="button" class="hp-btn hp-btn--outline hp-btn--sm" data-dyn-back="india">All India</button>'
            + '</div></div>'
            + '<div class="hp-dyn__facts hp-dyn__facts--2"><span><b>' + indian(d.tehsils) + '</b>Tehsils</span><span><b>' + indian(d.villages) + '</b>Villages</span></div>'
            + '<p class="hp-note">' + (d.reported ? 'District MIS submitted' + (d.lastUpdate ? ' &middot; last updated ' + esc(d.lastUpdate) : '')
                : 'District MIS not yet submitted') + '</p>'
            + '<h3 class="hp-mini-title">Component progress <small>(bar = district, mark = State/UT)</small></h3>'
            + '<ul class="hp-dyn__list">' + rows + '</ul>';
        show('district');
    }

    function openDistrict(lgd) {
        if (!lgd || !current) { return; }
        el('hp-details-dynamic').innerHTML = '<div class="hp-dyn__loading" data-skeleton>&nbsp;</div>';
        show('district');
        focusDistrictOnMap(lgd);
        $.ajax({ url: '/site/home-preview/district/' + encodeURIComponent(lgd), method: 'GET' })
            .done(function (d) {
                currentDistrict = d;
                renderDistrict(d);
                updateUrl();
            })
            .fail(function () {
                currentDistrict = null;
                el('hp-details-dynamic').innerHTML = headHtml('District details', 'District', titleCase(current.name), 'state')
                    + '<p class="hp-note">District details could not be loaded.</p>';
            });
    }

    function leaveDistrict() {
        currentDistrict = null;
        if (typeof window.hpHighlightDistrict === 'function') { window.hpHighlightDistrict(null); }
        if (typeof window.hpZoomToState === 'function') { window.hpZoomToState(current.id); }
        renderState(current);
        updateUrl();
    }

    window.hpMapDistrictClick = function (lgd) { openDistrict(lgd); };

    window.hpOnStateLayerReady = function (stateId) { drawDistricts(stateId); };

    function openState(id, districtLgd) {
        if (!id) { return; }
        window.hpSelectedStateId = String(id);
        currentDistrict = null;
        el('hp-details-dynamic').innerHTML = '<div class="hp-dyn__loading" data-skeleton>&nbsp;</div>';
        show('state');
        $.ajax({ url: '/site/home-preview/state/' + encodeURIComponent(id), method: 'GET' })
            .done(function (d) {
                current = d;
                el('hp-state-search').value = titleCase(d.name);
                renderState(d);
                updateUrl();
                if (typeof window.hpZoomToState === 'function') { window.hpZoomToState(d.id); }
                drawDistricts(d.id);
                if (districtLgd) { openDistrict(districtLgd); }
            })
            .fail(function () {
                el('hp-details-dynamic').innerHTML = headHtml('State/UT details', stateName(id) || 'State/UT', 'All India', 'india')
                    + '<p class="hp-note">Details could not be loaded. Please try again.</p>';
            });
    }

    function openDistricts() {
        if (!current) { return; }
        var d = current;
        if (isStateLevelOnly(component)) {
            el('hp-details-dynamic').innerHTML = headHtml(COMPONENT_LABELS[component], titleCase(d.name) + ' districts', titleCase(d.name), 'state')
                + stateLevelNote();
            show('districts');
            return;
        }
        el('hp-details-dynamic').innerHTML = headHtml(COMPONENT_LABELS[component], titleCase(d.name) + ' districts', titleCase(d.name), 'state')
            + '<div class="hp-dyn__loading" data-skeleton>&nbsp;</div>';
        show('districts');
        loadDistricts(d.id)
            .done(function (list) {
                var html = list.length ? list.map(function (x) {
                    var b = band(x.percent);
                    return '<li><button type="button"' + (x.lgd != null ? ' data-dyn-district="' + esc(x.lgd) + '"' : ' disabled') + '>'
                        + '<span class="hp-band-dot" style="background:' + (b >= 0 ? BANDS[b].colour : '#d9d9d9') + '"></span>'
                        + '<span>' + esc(titleCase(x.name)) + '</span><b>' + pctText(x.percent) + '</b></button></li>';
                }).join('') : '<li><span>No district data reported yet.</span></li>';
                el('hp-details-dynamic').innerHTML = headHtml(COMPONENT_LABELS[component], titleCase(d.name) + ' districts', titleCase(d.name), 'state')
                    + '<p class="hp-note">' + list.length + ' districts, highest first. Select a district, or click it on the map.</p>'
                    + '<ul class="hp-dyn__plain hp-dyn__plain--links">' + html + '</ul>';
            })
            .fail(function () {
                el('hp-details-dynamic').innerHTML = headHtml(COMPONENT_LABELS[component], titleCase(d.name) + ' districts', titleCase(d.name), 'state')
                    + '<p class="hp-note">District details could not be loaded.</p>';
            });
    }

    function openBand(index) {
        if (!mapData) { return; }
        var values = mapData.percents[component] || {};
        var list = mapData.states.filter(function (s) { return band(values[s.id]) === index; })
            .sort(function (a, b) { return (values[b.id] || 0) - (values[a.id] || 0); });
        var html = list.length ? list.map(function (s) {
            return '<li><button type="button" data-dyn-state="' + s.id + '"><span class="hp-band-dot" style="background:' + BANDS[index].colour + '"></span>'
                + '<span>' + esc(titleCase(s.name)) + '</span><b>' + pctText(values[s.id]) + '</b></button></li>';
        }).join('') : '<li><span>No State/UT in this range.</span></li>';
        current = null;
        currentDistrict = null;
        window.hpSelectedStateId = null;
        if (typeof window.hpResetMapExtent === 'function') { window.hpResetMapExtent(); }
        updateUrl();
        el('hp-details-dynamic').innerHTML = headHtml(COMPONENT_LABELS[component], BANDS[index].label + ' range', 'All India', 'india')
            + '<p class="hp-note">' + list.length + ' States/UTs. Select one to see its details.</p>'
            + '<ul class="hp-dyn__plain hp-dyn__plain--links">' + html + '</ul>';
        show('band');
        document.querySelectorAll('[data-hp-band]').forEach(function (btn) {
            btn.setAttribute('aria-pressed', String(Number(btn.getAttribute('data-hp-band')) === index));
        });
    }

    function renderBandSummary() {
        var host = el('hp-band-summary');
        if (!host || !mapData) { return; }
        var values = mapData.percents[component] || {};
        var counts = [0, 0, 0, 0];
        var reported = 0;
        mapData.states.forEach(function (s) {
            var b = band(values[s.id]);
            if (b >= 0) { counts[b]++; reported++; }
        });
        var max = Math.max.apply(null, counts.concat([1]));
        host.innerHTML = BANDS.slice().reverse().map(function (b, i) {
            var index = BANDS.length - 1 - i;
            return '<li><button type="button" data-summary-band="' + index + '">'
                + '<span class="hp-band-dot" style="background:' + b.colour + '"></span><span>' + b.label + '</span>'
                + '<span class="hp-bands__bar"><i style="width:' + (counts[index] / max * 100).toFixed(1) + '%;background:' + b.colour + '"></i></span>'
                + '<b>' + counts[index] + '</b></button></li>';
        }).join('') + (reported < mapData.states.length
            ? '<li class="hp-note">' + (mapData.states.length - reported) + ' States/UTs not reported</li>' : '');
    }

    function initSearch() {
        var input = el('hp-state-search');
        function pick() {
            var value = input.value.trim().toLowerCase();
            if (!value || !mapData) { return; }
            var match = mapData.states.filter(function (s) { return s.name.toLowerCase() === value || titleCase(s.name).toLowerCase() === value; })[0]
                || mapData.states.filter(function (s) { return s.name.toLowerCase().indexOf(value) === 0; })[0];
            if (match) { openState(match.id); }
        }
        input.addEventListener('change', pick);
        input.addEventListener('keydown', function (e) { if (e.key === 'Enter') { e.preventDefault(); pick(); } });
    }

    function initPanelClicks() {
        el('hp-details-dynamic').addEventListener('click', function (e) {
            var back = e.target.closest('[data-dyn-back]');
            if (back) {
                if (back.getAttribute('data-dyn-back') === 'state' && current) { leaveDistrict(); } else { backToIndia(); }
                return;
            }
            if (e.target.closest('[data-dyn-districts]')) { openDistricts(); return; }
            var dist = e.target.closest('[data-dyn-district]');
            if (dist) { openDistrict(dist.getAttribute('data-dyn-district')); return; }
            var st = e.target.closest('[data-dyn-state]');
            if (st) { openState(st.getAttribute('data-dyn-state')); return; }
            var comp = e.target.closest('[data-dyn-component]');
            if (comp) {
                var chip = document.querySelector('[data-hp-select="' + comp.getAttribute('data-dyn-component') + '"]');
                if (chip) { chip.click(); }
            }
        });
        el('hp-details-dynamic').addEventListener('keydown', function (e) {
            var comp = e.target.closest('[data-dyn-component]');
            if (comp && (e.key === 'Enter' || e.key === ' ')) { e.preventDefault(); comp.click(); }
        });
        document.querySelectorAll('[data-hp-band]').forEach(function (btn) {
            btn.addEventListener('click', function () { openBand(Number(btn.getAttribute('data-hp-band'))); });
        });
        el('hp-map-back').addEventListener('click', backToIndia);
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && window.hpSelectedStateId && !e.target.closest('input, select, textarea')) {
                backToIndia();
            }
        });
        el('hp-band-summary').addEventListener('click', function (e) {
            var btn = e.target.closest('[data-summary-band]');
            if (btn) { openBand(Number(btn.getAttribute('data-summary-band'))); }
        });
    }

    document.addEventListener('hp:component', function (e) {
        component = e.detail;
        updateUrl();
        renderBandSummary();
        if (view === 'district' && currentDistrict) { renderDistrict(currentDistrict); }
        else if (view === 'state' && current) { renderState(current); }
        else if (view === 'districts') { openDistricts(); }
        else if (view === 'band') {
            var pressed = document.querySelector('[data-hp-band][aria-pressed="true"]');
            if (pressed) { openBand(Number(pressed.getAttribute('data-hp-band'))); }
        }
    });

    document.addEventListener('hp:insights', function (e) {
        mapData = e.detail && e.detail.map;
        if (!mapData) { return; }
        el('hp-state-list').innerHTML = mapData.states.map(function (s) {
            return '<option value="' + esc(titleCase(s.name)) + '"></option>';
        }).join('');
        renderBandSummary();
        if (pendingMisMap) { window.hpLoadGenericMap(pendingMisMap); }
        var params = new URLSearchParams(window.location.search);
        var wanted = params.get('component');
        if (wanted && COMPONENT_LABELS[wanted] && wanted !== 'clr') {
            var chip = document.querySelector('[data-hp-select="' + wanted + '"]');
            if (chip) { chip.click(); }
        }
        var state = params.get('state');
        var district = params.get('district');
        if (state && /^\d+$/.test(state)) { openState(state, district && /^\d+$/.test(district) ? district : null); }
    });

    function initZoom() {
        document.querySelectorAll('[data-hp-zoom]').forEach(function (btn) {
            btn.addEventListener('click', function () {
                var action = btn.getAttribute('data-hp-zoom');
                if (action === 'fit') {
                    if (window.hpSelectedStateId) { backToIndia(); }
                    else if (typeof window.hpResetMapExtent === 'function') { window.hpResetMapExtent(); }
                } else if (typeof window.hpZoomBy === 'function') {
                    window.hpZoomBy(action === 'in' ? 0.5 : 2);
                }
            });
        });
    }

    window.hpMapStateClick = function (id) { openState(id); };
    window.hpMapBackToIndia = function () { backToIndia(); };

    $(function () {
        var use = document.querySelector('use[href]');
        iconsUrl = use ? use.getAttribute('href').split('#')[0] : '';
        initSearch();
        initPanelClicks();
        initZoom();
    });
})();
